@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.donorpath.app.ui.screens.rewards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donorpath.app.data.local.RewardEntity
import com.donorpath.app.data.local.UserProfileEntity
import com.donorpath.app.data.repository.DonorPathRepository
import com.donorpath.app.data.repository.DonorTier
import com.donorpath.app.di.SimpleViewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RewardsViewModel(private val repository: DonorPathRepository) : ViewModel() {
    val rewards: StateFlow<List<RewardEntity>> = repository.rewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<UserProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun redeem(reward: RewardEntity) {
        viewModelScope.launch { repository.redeemReward(reward) }
    }
}

@Composable
fun RewardsScreen(repository: DonorPathRepository) {
    val viewModel: RewardsViewModel = viewModel(factory = SimpleViewModelFactory { RewardsViewModel(repository) })
    val rewards by viewModel.rewards.collectAsState()
    val profile by viewModel.profile.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Rewards") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            profile?.let {
                val tier = DonorTier.forPoints(it.totalPoints)
                Text(
                    text = "${it.totalPoints} points · ${tier.label} tier",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rewards, key = { it.id }) { reward ->
                    RewardCard(
                        reward = reward,
                        canAfford = (profile?.totalPoints ?: 0) >= reward.costPoints,
                        onRedeem = { viewModel.redeem(reward) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RewardCard(reward: RewardEntity, canAfford: Boolean, onRedeem: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(end = 8.dp)) {
                Text(reward.title, style = MaterialTheme.typography.titleSmall)
                Text(reward.description, style = MaterialTheme.typography.bodySmall)
                Text("${reward.costPoints} pts", style = MaterialTheme.typography.labelMedium)
            }
            Button(onClick = onRedeem, enabled = !reward.isRedeemed && canAfford) {
                Text(if (reward.isRedeemed) "Redeemed" else "Redeem")
            }
        }
    }
}
