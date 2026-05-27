package com.survey.mark.domain.model.sync

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import timber.log.Timber

class SyncScheduler(private val workManager: WorkManager) {

    fun schedulePeriodicSync() {
        workManager.enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            SyncWorker.periodicRequest()
        )
        Timber.d("Periodic sync scheduled every 6 hours")
    }

    fun triggerImmediateSync() {
        workManager.enqueueUniqueWork(
            SyncWorker.WORK_NAME_ONE_SHOT,
            ExistingWorkPolicy.REPLACE,
            SyncWorker.oneShotRequest()
        )
        Timber.d("Immediate sync triggered")
    }
}
