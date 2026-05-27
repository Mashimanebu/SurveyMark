package com.survey.mark.di.modules

import android.content.Context
import androidx.work.WorkManager
import com.survey.mark.domain.model.sync.SyncScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkModule {

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager =
        WorkManager.getInstance(context)

    @Provides
    @Singleton
    fun provideSyncScheduler(workManager: WorkManager): SyncScheduler =
        SyncScheduler(workManager)
}
