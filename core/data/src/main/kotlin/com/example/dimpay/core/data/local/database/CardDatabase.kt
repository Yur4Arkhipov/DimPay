package com.example.dimpay.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.dimpay.core.data.local.dao.CardDao
import com.example.dimpay.core.data.local.entities.CardEntity

@Database(
    entities = [CardEntity::class],
    version = 1
)
abstract class CardDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao

//    companion object {
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                db.execSQL("ALTER TABLE meal ADD COLUMN imageUri TEXT")
//            }
//        }
//    }
}