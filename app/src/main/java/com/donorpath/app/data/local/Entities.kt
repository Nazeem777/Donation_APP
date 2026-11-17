package com.donorpath.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "organizations")
data class OrganizationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val description: String,
    val verified: Boolean
)

@Entity(tableName = "donations")
data class DonationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val organizationId: Long,
    val organizationName: String,
    val amount: Double,
    val pointsEarned: Int,
    val timestampMillis: Long
)

@Entity(tableName = "rewards")
data class RewardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val costPoints: Int,
    val isRedeemed: Boolean = false,
    val redeemedAtMillis: Long? = null
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 0,
    val name: String,
    val totalPoints: Int = 0,
    val totalDonated: Double = 0.0
)
