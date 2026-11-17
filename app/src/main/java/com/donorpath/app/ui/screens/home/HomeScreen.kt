@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.donorpath.app.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.donorpath.app.data.local.OrganizationEntity
import com.donorpath.app.data.local.UserProfileEntity
import com.donorpath.app.data.repository.DonorPathRepository
import com.donorpath.app.data.repository.DonorTier
import com.donorpath.app.di.SimpleViewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: DonorPathRepository) : ViewModel() {
    val organizations: StateFlow<List<OrganizationEntity>> = repository.organizations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val profile: StateFlow<UserProfileEntity?> = repository.profile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch { repository.ensureSeeded() }
    }
}

@Composable
fun HomeScreen(
    repository: DonorPathRepository,
    onOrganizationClick: (Long) -> Unit
) {
    val viewModel: HomeViewModel = viewModel(factory = SimpleViewModelFactory { HomeViewModel(repository) })
    val organizations by viewModel.organizations.collectAsState()
    val profile by viewModel.profile.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("DonorPath") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            profile?.let { ProfileSummaryCard(it) }

            Text(
                text = "Partner organizations",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(organizations, key = { it.id }) { org ->
                    OrganizationCard(org) { onOrganizationClick(org.id) }
                }
            }
        }
    }
}

@Composable
private fun ProfileSummaryCard(profile: UserProfileEntity) {
    val tier = DonorTier.forPoints(profile.totalPoints)
    val nextTier = DonorTier.next(profile.totalPoints)
    val progress = if (nextTier != null) {
        (profile.totalPoints - tier.minPoints).toFloat() / (nextTier.minPoints - tier.minPoints).toFloat()
    } else 1f

    Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${tier.label} Donor", style = MaterialTheme.typography.titleMedium)
                Text("${profile.totalPoints} pts", style = MaterialTheme.typography.titleMedium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (nextTier != null) {
                    "${nextTier.minPoints - profile.totalPoints} pts to ${nextTier.label}"
                } else {
                    "Highest tier reached"
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun OrganizationCard(organization: OrganizationEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(organization.name, style = MaterialTheme.typography.titleMedium)
                if (organization.verified) {
                    Icon(
                        imageVector = Icons.Filled.VerifiedUser,
                        contentDescription = "Verified",
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }
            Text(organization.category, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(organization.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
