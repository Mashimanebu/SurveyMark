package com.survey.mark.domain.model

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.survey.mark.domain.repo.ConditionReportRepository
import com.survey.mark.domain.repo.ControlPointRepository
import com.survey.mark.domain.repo.NewMarkRepository
import com.survey.mark.domain.repo.OccupationLogRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val controlPointRepo: ControlPointRepository,
    private val conditionReportRepo: ConditionReportRepository,
    private val occupationLogRepo: OccupationLogRepository,
    private val newMarkRepo: NewMarkRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("SyncWorker starting")
        return try {
            controlPointRepo.syncFromServer()
            conditionReportRepo.syncPending()
            occupationLogRepo.syncPending()
            newMarkRepo.syncPending()

            Timber.d("SyncWorker completed successfully")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "SyncWorker failed")
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
    }

    companion object {
        private const val MAX_RETRIES = 3
        const val WORK_NAME_PERIODIC = "surveymark_periodic_sync"
        const val WORK_NAME_ONE_SHOT = "surveymark_oneshot_sync"

        fun periodicRequest(): PeriodicWorkRequest =
            PeriodicWorkRequestBuilder<SyncWorker>(6, TimeUnit.HOURS).setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES).build()

        fun oneShotRequest(): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SyncWorker>().setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10, TimeUnit.MINUTES).build()
    }
}