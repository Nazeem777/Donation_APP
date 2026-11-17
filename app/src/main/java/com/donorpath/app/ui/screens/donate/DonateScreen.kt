@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.donorpath.app.ui.screens.donate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.donorpath.app.data.repository.DonorPathRepository
import com.donorpath.app.di.SimpleViewModelFactory
import kotlinx.coroutines.launch

private val suggestedAmounts = listOf(10.0, 25.0, 50.0, 100.0)

class DonateViewModel(
    private val repository: DonorPathRepository,
    private val orgId: Long
) : ViewModel() {
    var organizationName by mutableStateOf<String?>(null)
        private set
    var isProcessing by mutableStateOf(false)
        private set
    var pointsEarned by mutableStateOf<Int?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            organizationName = repository.getOrganization(orgId)?.name
        }
    }

    fun donate(amount: Double, onComplete: () -> Unit) {
        if (isProcessing) return
        errorMessage = null
        viewModelScope.launch {
            isProcessing = true
            try {
                val organization = repository.getOrganization(orgId)
                if (organization == null || amount <= 0) {
                    errorMessage = "Enter a valid donation amount"
                    return@launch
                }
                // Simulated payment processing: in production this step would
                // call a real payment gateway before DonorPath forwards the
                // donation to the partner organization.
                val points = repository.donate(organization, amount)
                pointsEarned = points
                onComplete()
            } catch (e: IllegalArgumentException) {
                errorMessage = e.message
            } finally {
                isProcessing = false
            }
        }
    }
}

@Composable
fun DonateScreen(
    repository: DonorPathRepository,
    orgId: Long,
    onDonationComplete: () -> Unit,
    onBack: () -> Unit
) {
    val viewModel: DonateViewModel = viewModel(
        factory = SimpleViewModelFactory { DonateViewModel(repository, orgId) }
    )
    var amountText by remember { mutableStateOf("25") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donate") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text(
                "Donating to ${viewModel.organizationName ?: "..."}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                suggestedAmounts.forEach { preset ->
                    FilterChip(
                        selected = amountText == preset.toInt().toString(),
                        onClick = { amountText = preset.toInt().toString() },
                        label = { Text("RM${preset.toInt()}") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Amount (RM)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "You'll earn ${amountText.toDoubleOrNull()?.toInt() ?: 0} reward points for this donation.",
                style = MaterialTheme.typography.bodySmall
            )

            viewModel.errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    viewModel.donate(amount, onDonationComplete)
                },
                enabled = !viewModel.isProcessing,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isProcessing) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp))
                } else {
                    Text("Confirm donation")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Payments are simulated in this build. In production, this step would " +
                    "hand off to a real payment gateway before DonorPath forwards the funds " +
                    "to the chosen organization.",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
