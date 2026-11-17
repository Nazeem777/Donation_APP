@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.donorpath.app.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donorpath.app.data.local.DonationEntity
import com.donorpath.app.data.repository.DonorPathRepository
import com.donorpath.app.di.SimpleViewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryViewModel(repository: DonorPathRepository) : ViewModel() {
    val donations: StateFlow<List<DonationEntity>> = repository.donations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

@Composable
fun HistoryScreen(repository: DonorPathRepository) {
    val viewModel: HistoryViewModel = viewModel(factory = SimpleViewModelFactory { HistoryViewModel(repository) })
    val donations by viewModel.donations.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Donation History") }) }
    ) { padding ->
        if (donations.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                Text(
                    "No donations yet. Pick an organization from Home to get started.",
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(donations, key = { it.id }) { donation ->
                DonationCard(donation)
            }
        }
    }
}

@Composable
private fun DonationCard(donation: DonationEntity) {
    val formatter = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(donation.organizationName, style = MaterialTheme.typography.titleSmall)
                Text("RM%.2f".format(donation.amount), style = MaterialTheme.typography.titleSmall)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(formatter.format(Date(donation.timestampMillis)), style = MaterialTheme.typography.bodySmall)
                Text("+${donation.pointsEarned} pts", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
