package com.survey.mark.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.survey.mark.data.dao.ControlPointDao
import com.survey.mark.data.entity.ControlPointEntity
import com.survey.mark.data.entity.ControlPointTypeConverters

@Database(
    entities  = [ControlPointEntity::class],
    version   = 1,
    exportSchema = false
)
@TypeConverters(ControlPointTypeConverters::class)
abstract class SurveyMarkDatabase : RoomDatabase() {
     abstract fun controlPointDao(): ControlPointDao

    companion object {
        const val DATABASE_NAME = "surveymark_eswatini.db"
    }
}