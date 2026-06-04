package com.survey.mark.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.survey.mark.auth.domain.AccountStatus
import com.survey.mark.auth.domain.AuthRepository
import com.survey.mark.auth.domain.AuthState
import com.survey.mark.auth.domain.SignUpRequest
import com.survey.mark.auth.domain.SurveyUser
import com.survey.mark.auth.domain.UserRole
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    override val authState: Flow<AuthState> = callbackFlow {
        trySend(AuthState.Loading)

        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val fbUser = firebaseAuth.currentUser

            if (fbUser == null) {
                trySend(AuthState.Unauthenticated)
                return@AuthStateListener
            }

            launch {
                try {
                    Timber.d("Auth state changed — fetching profile for ${fbUser.uid}")

                    val snapshot = firestore
                        .collection(USERS_COLLECTION)
                        .document(fbUser.uid)
                        .get()
                        .await()

                    if (!snapshot.exists()) {
                        Timber.w("No Firestore profile found for ${fbUser.uid} — treating as unauthenticated")
                        trySend(AuthState.Unauthenticated)
                        return@launch
                    }

                    val user = snapshot.toSurveyUser()

                    if (user == null) {
                        Timber.w("Could not parse Firestore profile for ${fbUser.uid}")
                        trySend(AuthState.Unauthenticated)
                        return@launch
                    }

                    val state = when (user.status) {
                        AccountStatus.ACTIVE -> AuthState.Authenticated(user)
                        AccountStatus.PENDING -> AuthState.PendingApproval(user)
                        AccountStatus.SUSPENDED -> AuthState.Suspended(user)
                        AccountStatus.REJECTED -> AuthState.Unauthenticated
                    }

                    Timber.d("Auth state resolved: $state")
                    trySend(state)

                } catch (e: Exception) {
                    Timber.e(e, "Error fetching Firestore profile")
                    trySend(AuthState.Error(e.message ?: "Failed to load profile"))
                }
            }
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override val currentUser: SurveyUser? get() = null

    override suspend fun signIn(email: String, password: String): Result<SurveyUser> =
        runCatching {
            Timber.d("Signing in: $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: error("Firebase Auth returned no UID")
            Timber.d("Firebase Auth OK for $uid — authState flow will fetch profile")

            SurveyUser(
                uid = uid,
                email = email,
                displayName = result.user?.displayName ?: "",
                region = "",
                licenceNo = "",
                role = UserRole.SURVEYOR,
                status = AccountStatus.PENDING
            )
        }

    override suspend fun signUp(request: SignUpRequest): Result<SurveyUser> =
        runCatching {
            Timber.d("Creating account: ${request.email}")
            val result = auth.createUserWithEmailAndPassword(
                request.email, request.password
            ).await()
            val uid = result.user?.uid ?: error("Firebase Auth returned no UID")

            val user = SurveyUser(
                uid = uid,
                email = request.email,
                displayName = request.displayName,
                licenceNo = request.licenceNo,
                role = UserRole.SURVEYOR,
                status = AccountStatus.PENDING,
                region = request.region,
                createdAt = System.currentTimeMillis()
            )

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .set(user.toMap())
                .await()

            Timber.d("Firestore profile created for $uid")
            user
        }

    override suspend fun signOut() {
        Timber.d("Signing out")
        auth.signOut()
    }

    override suspend fun getCurrentUser(): SurveyUser? {
        val uid = auth.currentUser?.uid ?: return null
        return runCatching {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
                .toSurveyUser()
        }.getOrNull()
    }


    override suspend fun getPendingUsers(): Result<List<SurveyUser>> =
        runCatching {
            firestore.collection(USERS_COLLECTION)
                .whereEqualTo("status", AccountStatus.PENDING.name)
                .get().await()
                .documents.mapNotNull { it.toSurveyUser() }
        }

    override suspend fun getAllUsers(): Result<List<SurveyUser>> =
        runCatching {
            firestore.collection(USERS_COLLECTION)
                .get().await()
                .documents.mapNotNull { it.toSurveyUser() }
        }

    override suspend fun approveUser(uid: String): Result<Unit> =
        runCatching {
            firestore.collection(USERS_COLLECTION).document(uid)
                .update("status", AccountStatus.ACTIVE.name).await()
            Timber.d("User approved: $uid")
        }

    override suspend fun rejectUser(uid: String, reason: String): Result<Unit> =
        runCatching {
            firestore.collection(USERS_COLLECTION).document(uid)
                .update(mapOf("status" to AccountStatus.REJECTED.name, "rejectionReason" to reason))
                .await()
        }

    override suspend fun suspendUser(uid: String, reason: String): Result<Unit> =
        runCatching {
            firestore.collection(USERS_COLLECTION).document(uid)
                .update(
                    mapOf(
                        "status" to AccountStatus.SUSPENDED.name,
                        "suspensionReason" to reason
                    )
                )
                .await()
        }

    override suspend fun updateUserRole(uid: String, role: UserRole): Result<Unit> =
        runCatching {
            firestore.collection(USERS_COLLECTION).document(uid)
                .update("role", role.name).await()
        }

    private fun com.google.firebase.firestore.DocumentSnapshot.toSurveyUser(): SurveyUser? =
        runCatching {
            SurveyUser(
                uid = id,
                email = getString("email") ?: return@runCatching null,
                displayName = getString("displayName") ?: "",
                licenceNo = getString("licenceNo") ?: "",
                role = UserRole.valueOf(
                    getString("role") ?: UserRole.SURVEYOR.name
                ),
                status = AccountStatus.valueOf(
                    getString("status") ?: AccountStatus.PENDING.name
                ),
                region = getString("region") ?: "",
                photoUrl = getString("photoUrl"),
                createdAt = getLong("createdAt") ?: System.currentTimeMillis()
            )
        }.getOrNull()

    private fun SurveyUser.toMap(): Map<String, Any?> = mapOf(
        "uid" to uid,
        "email" to email,
        "displayName" to displayName,
        "licenceNo" to licenceNo,
        "role" to role.name,
        "status" to status.name,
        "region" to region,
        "photoUrl" to photoUrl,
        "createdAt" to createdAt
    )
}