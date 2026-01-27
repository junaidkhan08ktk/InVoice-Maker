package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    onPurchasePro: () -> Unit,
    onPurchaseLifetime: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.Start)) {
            Icon(Icons.Default.Close, contentDescription = null)
        }

        Text(
            text = "Run Your Business Like a Professional",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Create unlimited GST invoices, remove watermark, and stay compliant.",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Pro Plan
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            onClick = onPurchasePro
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Pro Monthly", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    SuggestionChip(onClick = {}, label = { Text("Most Popular") })
                }
                Text(text = "₹3,299 / year", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                FeatureRow("Unlimited Invoices")
                FeatureRow("Remove Watermark")
                FeatureRow("Custom Branding")
                FeatureRow("Cloud Backup")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onPurchasePro, modifier = Modifier.fillMaxWidth()) {
                    Text("Start Free Trial")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lifetime Plan
        OutlinedCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPurchaseLifetime
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Lifetime Access", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = "₹9,999 one-time", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "All Pro features forever. No recurring payments.")
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = onPurchaseLifetime, modifier = Modifier.fillMaxWidth()) {
                    Text("Unlock Lifetime")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Trust Badges
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            TrustBadge("Cancel anytime")
            TrustBadge("Offline & secure")
            TrustBadge("GST Compliant")
        }
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun TrustBadge(text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(20.dp))
        Text(text = text, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}
