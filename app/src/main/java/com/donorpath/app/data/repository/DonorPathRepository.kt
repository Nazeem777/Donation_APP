package com.donorpath.app.data.repository

import com.donorpath.app.data.local.AppDatabase
import com.donorpath.app.data.local.DonationEntity
import com.donorpath.app.data.local.OrganizationEntity
import com.donorpath.app.data.local.RewardEntity
import com.donorpath.app.data.local.UserProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlin.math.roundToInt

/**
 * DonorPath sits between the donor and a catalog of partner charities: a donation made
 * here is recorded against the organization the donor picked, and the donor is credited
 * reward points in return. No real payment gateway is wired in yet, so a "donation" here
 * is simulated and recorded locally rather than transferring real funds.
 */
class DonorPathRepository(private val db: AppDatabase) {

    val organizations: Flow<List<OrganizationEntity>> = db.organizationDao().observeAll()
    val donations: Flow<List<DonationEntity>> = db.donationDao().observeAll()
    val rewards: Flow<List<RewardEntity>> = db.rewardDao().observeAll()
    val profile: Flow<UserProfileEntity?> = db.userProfileDao().observeProfile()

    suspend fun ensureSeeded() {
        if (db.userProfileDao().getProfile() == null) {
            db.userProfileDao().insert(UserProfileEntity(name = "Donor"))
        }
        if (db.organizationDao().count() == 0) {
            db.organizationDao().insertAll(SeedData.organizations())
        }
        if (db.rewardDao().count() == 0) {
            db.rewardDao().insertAll(SeedData.rewards())
        }
    }

    suspend fun getOrganization(id: Long): OrganizationEntity? = db.organizationDao().getById(id)

    /**
     * Routes a donation to [organization] and credits the donor with points.
     * 1 point is earned per whole currency unit donated.
     */
    suspend fun donate(organization: OrganizationEntity, amount: Double): Int {
        require(amount > 0) { "Donation amount must be greater than zero" }

        val pointsEarned = amount.roundToInt()
        val profile = db.userProfileDao().getProfile()
            ?: UserProfileEntity(name = "Donor")

        db.donationDao().insert(
            DonationEntity(
                organizationId = organization.id,
                organizationName = organization.name,
                amount = amount,
                pointsEarned = pointsEarned,
                timestampMillis = System.currentTimeMillis()
            )
        )

        db.userProfileDao().update(
            profile.copy(
                totalPoints = profile.totalPoints + pointsEarned,
                totalDonated = profile.totalDonated + amount
            )
        )

        return pointsEarned
    }

    /** Redeems [reward] if the donor has enough points, deducting the cost. Returns true on success. */
    suspend fun redeemReward(reward: RewardEntity): Boolean {
        val profile = db.userProfileDao().getProfile() ?: return false
        if (reward.isRedeemed || profile.totalPoints < reward.costPoints) return false

        db.userProfileDao().update(profile.copy(totalPoints = profile.totalPoints - reward.costPoints))
        db.rewardDao().update(
            reward.copy(isRedeemed = true, redeemedAtMillis = System.currentTimeMillis())
        )
        return true
    }

    suspend fun updateName(name: String) {
        val profile = db.userProfileDao().getProfile() ?: return
        db.userProfileDao().update(profile.copy(name = name))
    }
}

private object SeedData {
    fun organizations() = listOf(
        OrganizationEntity(
            name = "Global Relief Alliance",
            category = "Disaster Relief",
            description = "Demo partner for emergency response and disaster relief causes worldwide.",
            verified = true
        ),
        OrganizationEntity(
            name = "Children First Fund",
            category = "Child Welfare",
            description = "Demo partner supporting education and welfare programs for children in need.",
            verified = true
        ),
        OrganizationEntity(
            name = "GreenEarth Conservation",
            category = "Environment",
            description = "Demo partner focused on reforestation and wildlife conservation projects.",
            verified = true
        ),
        OrganizationEntity(
            name = "Community Food Bank Network",
            category = "Hunger Relief",
            description = "Demo partner distributing food and essentials to local communities.",
            verified = true
        ),
        OrganizationEntity(
            name = "HopeCare Medical Fund",
            category = "Healthcare",
            description = "Demo partner funding medical care and health outreach programs.",
            verified = false
        )
    )

    fun rewards() = listOf(
        RewardEntity(
            title = "Certificate of Appreciation",
            description = "A printable certificate thanking you for your generosity.",
            costPoints = 20
        ),
        RewardEntity(
            title = "RM5 Partner Voucher",
            description = "Redeemable voucher from a DonorPath partner brand.",
            costPoints = 50
        ),
        RewardEntity(
            title = "RM10 Partner Voucher",
            description = "Redeemable voucher from a DonorPath partner brand.",
            costPoints = 100
        ),
        RewardEntity(
            title = "Gold Donor Badge",
            description = "A special profile badge recognizing your Gold-tier giving.",
            costPoints = 150
        ),
        RewardEntity(
            title = "Platinum Donor Frame",
            description = "An exclusive profile frame for Platinum-tier donors.",
            costPoints = 300
        )
    )
}
