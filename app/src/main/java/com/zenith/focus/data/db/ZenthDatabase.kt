package com.zenith.focus.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zenith.focus.data.model.ZenSession

@Database(entities = [ZenSession::class], version = 1, exportSchema = false)
abstract class ZenthDatabase : RoomDatabase() {

    abstract fun zenSessionDao(): ZenSessionDao

    companion object {
        @Volatile
        private var instance: ZenthDatabase? = null

        fun getInstance(context: Context): ZenthDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    ZenthDatabase::class.java,
                    "zenith_database"
                ).build().also { instance = it }
            }
    }
}
