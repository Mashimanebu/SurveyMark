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
            val firebaseUser = firebaseAuth.currentUser

            if (firebaseUser == null) {
                trySend(AuthState.Unauthenticated)
                return@AuthStateListener
            }

            firestore.collection(USERS_COLLECTION)
                .document(firebaseUser.uid)
                .get()
                .addOnSuccessListener { document ->

                    val user = document.toSurveyUser()

                    if (user == null) {
                        trySend(AuthState.Unauthenticated)
                        return@addOnSuccessListener
                    }

                    when (user.status) {
                        AccountStatus.ACTIVE ->
                            trySend(AuthState.Authenticated(user))

                        AccountStatus.PENDING ->
                            trySend(AuthState.PendingApproval(user))

                        AccountStatus.SUSPENDED ->
                            trySend(AuthState.Suspended(user))

                        AccountStatus.REJECTED ->
                            trySend(AuthState.Unauthenticated)
                    }
                }
                .addOnFailureListener { error ->
                    Timber.e(error)
                    trySend(
                        AuthState.Error(
                            error.message ?: "Failed to load user"
                        )
                    )
                }
        }

        auth.addAuthStateListener(listener)

        awaitClose {
            auth.removeAuthStateListener(listener)
        }
    }

    override val currentUser: SurveyUser?
        get() = null

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<SurveyUser> = runCatching {

        val result = auth
            .signInWithEmailAndPassword(email, password)
            .await()

        val uid = result.user?.uid
            ?: error("User not found")

        fetchUserProfile(uid)
            ?: error("Profile not found")
    }

    override suspend fun signUp(
        request: SignUpRequest
    ): Result<SurveyUser> = runCatching {

        val result = auth
            .createUserWithEmailAndPassword(
                request.email,
                request.password
            )
            .await()

        val uid = result.user?.uid
            ?: error("Failed to create account")

        val user = SurveyUser(
            uid = uid,
            email = request.email,
            displayName = request.displayName,
            licenceNo = request.licenceNo,
            role = UserRole.SURVEYOR,
            status = AccountStatus.PENDING,
            region = request.region
        )

        firestore.collection(USERS_COLLECTION)
            .document(uid)
            .set(user.toMap())
            .await()

        user
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun getCurrentUser(): SurveyUser? {
        val uid = auth.currentUser?.uid ?: return null
        return fetchUserProfile(uid)
    }

    override suspend fun getPendingUsers(): Result<List<SurveyUser>> =
        runCatching {

            firestore.collection(USERS_COLLECTION)
                .whereEqualTo(
                    "status",
                    AccountStatus.PENDING.name
                )
                .get()
                .await()
                .documents
                .mapNotNull { it.toSurveyUser() }
        }

    override suspend fun getAllUsers(): Result<List<SurveyUser>> =
        runCatching {

            firestore.collection(USERS_COLLECTION)
                .get()
                .await()
                .documents
                .mapNotNull { it.toSurveyUser() }
        }

    override suspend fun approveUser(
        uid: String
    ): Result<Unit> =
        runCatching {

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(
                    "status",
                    AccountStatus.ACTIVE.name
                )
                .await()
        }

    override suspend fun rejectUser(
        uid: String,
        reason: String
    ): Result<Unit> =
        runCatching {

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(
                    mapOf(
                        "status" to AccountStatus.REJECTED.name,
                        "rejectionReason" to reason
                    )
                )
                .await()
        }

    override suspend fun suspendUser(
        uid: String,
        reason: String
    ): Result<Unit> =
        runCatching {

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(
                    mapOf(
                        "status" to AccountStatus.SUSPENDED.name,
                        "suspensionReason" to reason
                    )
                )
                .await()
        }

    override suspend fun updateUserRole(
        uid: String,
        role: UserRole
    ): Result<Unit> =
        runCatching {

            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .update(
                    "role",
                    role.name
                )
                .await()
        }

    private suspend fun fetchUserProfile(
        uid: String
    ): SurveyUser? =
        runCatching {
            firestore.collection(USERS_COLLECTION)
                .document(uid)
                .get()
                .await()
                .toSurveyUser()
        }.getOrNull()

    private fun DocumentSnapshot.toSurveyUser(): SurveyUser? =
        runCatching {

            SurveyUser(
                uid = id,
                email = getString("email")
                    ?: return@runCatching null,
                displayName = getString("displayName") ?: "",
                licenceNo = getString("licenceNo") ?: "",
                role = runCatching {
                    UserRole.valueOf(
                        getString("role")
                            ?: UserRole.SURVEYOR.name
                    )
                }.getOrDefault(UserRole.SURVEYOR),
                status = runCatching {
                    AccountStatus.valueOf(
                        getString("status")
                            ?: AccountStatus.PENDING.name
                    )
                }.getOrDefault(AccountStatus.PENDING),
                region = getString("region") ?: "",
                photoUrl = getString("photoUrl"),
                createdAt = getLong("createdAt")
                    ?: System.currentTimeMillis()
            )
        }.getOrNull()

    private fun SurveyUser.toMap(): Map<String, Any?> =
        mapOf(
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