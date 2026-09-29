package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "followed_matches")
data class FollowedMatchEntity(
    @PrimaryKey
    val matchId: String,
    val title: String,
    val followedAt: Long = System.currentTimeMillis()
)

@Dao
interface FollowedMatchDao {
    @Query("SELECT * FROM followed_matches")
    fun getAllFollowed(): Flow<List<FollowedMatchEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM followed_matches WHERE matchId = :matchId)")
    fun isMatchFollowed(matchId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followMatch(match: FollowedMatchEntity)

    @Query("DELETE FROM followed_matches WHERE matchId = :matchId")
    suspend fun unfollowMatch(matchId: String)
}

@Database(entities = [FollowedMatchEntity::class], version = 1, exportSchema = false)
abstract class CrixerDatabase : RoomDatabase() {
    abstract fun followedMatchDao(): FollowedMatchDao
}
