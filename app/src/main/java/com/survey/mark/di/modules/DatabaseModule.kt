package com.survey.mark.di.modules

import android.content.Context
import androidx.room.Room
import com.survey.mark.data.dao.ConditionReportDao
import com.survey.mark.data.dao.ControlPointDao
import com.survey.mark.data.dao.NewMarkSubmissionDao
import com.survey.mark.data.dao.OccupationLogDao
import com.survey.mark.data.database.SurveyMarkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SurveyMarkDatabase =
        Room.databaseBuilder(
            context,
            SurveyMarkDatabase::class.java,
            SurveyMarkDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideControlPointDao(db: SurveyMarkDatabase): ControlPointDao =
        db.controlPointDao()

    @Provides fun provideConditionReportDao(db: SurveyMarkDatabase): ConditionReportDao =
        db.conditionReportDao()

    @Provides fun provideOccupationLogDao(db: SurveyMarkDatabase): OccupationLogDao =
        db.occupationLogDao()

    @Provides fun provideNewMarkDao(db: SurveyMarkDatabase): NewMarkSubmissionDao =
        db.newMarkSubmissionDao()
}
