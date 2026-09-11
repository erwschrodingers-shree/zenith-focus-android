package com.zenith.focus.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zenith.focus.data.model.ZenSession
import kotlinx.coroutines.flow.Flow

@Dao
interface ZenSessionDao {

    @Insert
    suspend fun insertSession(session: ZenSession): Long

    @Update
    suspend fun updateSession(session: ZenSession)

    @Query("SELECT * FROM zen_sessions ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSessions(limit: Int = 10): Flow<List<ZenSession>>

    @Query("SELECT * FROM zen_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): ZenSession?

    @Query("SELECT COUNT(*) FROM zen_sessions WHERE completed = 1")
    fun getCompletedSessionCount(): Flow<Int>

    @Query("SELECT SUM(durationMinutes) FROM zen_sessions WHERE completed = 1")
    fun getTotalFocusMinutes(): Flow<Int?>

    @Query("SELECT * FROM zen_sessions WHERE completed = 1 ORDER BY startTime DESC")
    fun getAllCompletedSessions(): Flow<List<ZenSession>>
}
