@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.donorpath.app.ui.screens.orgdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donorpath.app.data.local.OrganizationEntity
import com.donorpath.app.data.repository.DonorPathRepository
import com.donorpath.app.di.SimpleViewModelFactory
import kotlinx.coroutines.launch

class OrganizationDetailViewModel(
    private val repository: DonorPathRepository,
    private val orgId: Long
) : ViewModel() {
    var organization by mutableStateOf<OrganizationEntity?>(null)
        private set

    init {
        viewModelScope.launch {
            organization = repository.getOrganization(orgId)
        }
    }
}

@Composable
fun OrganizationDetailScreen(
    repository: DonorPathRepository,
    orgId: Long,
    onDonateClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val viewModel: OrganizationDetailViewModel = viewModel(
        factory = SimpleViewModelFactory { OrganizationDetailViewModel(repository, orgId) }
    )
    val organization = viewModel.organization

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(organization?.name ?: "Organization") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (organization == null) {
            Column(
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.padding(32.dp))
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            AssistChip(onClick = {}, label = { Text(organization.category) })
            Spacer(modifier = Modifier.height(12.dp))
            Text(organization.description, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (organization.verified) "Verified partner" else "Pending verification",
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "DonorPath acts as a mediator: your donation is routed to this organization " +
                    "and you earn reward points for giving.",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onDonateClick(organization.id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Donate to ${organization.name}")
            }
        }
    }
}
