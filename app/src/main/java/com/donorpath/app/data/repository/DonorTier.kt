package com.donorpath.app.data.repository

enum class DonorTier(val label: String, val minPoints: Int) {
    BRONZE("Bronze", 0),
    SILVER("Silver", 100),
    GOLD("Gold", 500),
    PLATINUM("Platinum", 1500);

    companion object {
        fun forPoints(points: Int): DonorTier =
            entries.lastOrNull { points >= it.minPoints } ?: BRONZE

        fun next(points: Int): DonorTier? {
            val current = forPoints(points)
            val ordinal = current.ordinal
            return entries.getOrNull(ordinal + 1)
        }
    }
}
