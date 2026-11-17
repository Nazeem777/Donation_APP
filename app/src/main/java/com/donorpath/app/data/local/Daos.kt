package com.donorpath.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OrganizationDao {
    @Query("SELECT * FROM organizations ORDER BY name ASC")
    fun observeAll(): Flow<List<OrganizationEntity>>

    @Query("SELECT * FROM organizations WHERE id = :id")
    suspend fun getById(id: Long): OrganizationEntity?

    @Query("SELECT COUNT(*) FROM organizations")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(organizations: List<OrganizationEntity>)
}

@Dao
interface DonationDao {
    @Query("SELECT * FROM donations ORDER BY timestampMillis DESC")
    fun observeAll(): Flow<List<DonationEntity>>

    @Insert
    suspend fun insert(donation: DonationEntity)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY costPoints ASC")
    fun observeAll(): Flow<List<RewardEntity>>

    @Query("SELECT COUNT(*) FROM rewards")
    suspend fun count(): Int

    @Insert
    suspend fun insertAll(rewards: List<RewardEntity>)

    @Update
    suspend fun update(reward: RewardEntity)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 0")
    suspend fun getProfile(): UserProfileEntity?

    @Insert
    suspend fun insert(profile: UserProfileEntity)

    @Update
    suspend fun update(profile: UserProfileEntity)
}
