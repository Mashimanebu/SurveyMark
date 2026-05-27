package com.survey.mark.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.survey.mark.data.dao.ConditionReportDao
import com.survey.mark.data.dao.ControlPointDao
import com.survey.mark.data.dao.NewMarkSubmissionDao
import com.survey.mark.data.dao.OccupationLogDao
import com.survey.mark.data.entity.ConditionReportEntity
import com.survey.mark.data.entity.ControlPointEntity
import com.survey.mark.data.entity.ControlPointTypeConverters
import com.survey.mark.data.entity.NewMarkSubmissionEntity
import com.survey.mark.data.entity.OccupationLogEntity
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber

@Database(
    entities = [
        ControlPointEntity::class,
        ConditionReportEntity::class,
        OccupationLogEntity::class,
        NewMarkSubmissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ControlPointTypeConverters::class)
abstract class SurveyMarkDatabase : RoomDatabase() {

    abstract fun controlPointDao(): ControlPointDao
    abstract fun conditionReportDao(): ConditionReportDao
    abstract fun occupationLogDao(): OccupationLogDao
    abstract fun newMarkSubmissionDao(): NewMarkSubmissionDao

    companion object {
        const val DATABASE_NAME = "surveymark_eswatini.db"

        fun seedCallback(scope: CoroutineScope) = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Timber.d("Database created — seed will run post-open")
            }
        }
    }
}