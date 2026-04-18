package com.example.invoicegenerator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.compose.viewmodel.koinViewModel
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import com.example.invoicegenerator.Res
import com.example.invoicegenerator.business_details
import com.example.invoicegenerator.business_name_required
import com.example.invoicegenerator.gstin_required
import com.example.invoicegenerator.business_address
import com.example.invoicegenerator.bussiness_name
import com.example.invoicegenerator.email_optional
import com.example.invoicegenerator.phone_optional
import com.example.invoicegenerator.default_gst_rate
import com.example.invoicegenerator.continue_button
import com.example.invoicegenerator.ic_email
import com.example.invoicegenerator.ic_gst
import com.example.invoicegenerator.ic_location
import com.example.invoicegenerator.ic_phone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSetupScreen(
    onSetupComplete: () -> Unit,
) {
    val viewModel: BusinessViewModel = koinViewModel()
    var name by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gstRate by remember { mutableStateOf("18") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(Res.string.business_details)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(Res.string.business_name_required)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        painter = painterResource(Res.drawable.bussiness_name),
                        contentDescription = null
                    )
                }
            )

            OutlinedTextField(
                value = gstin,
                onValueChange = { gstin = it },
                label = { Text(stringResource(Res.string.gstin_required)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 27AAACR1234A1Z5") },
                leadingIcon = {
                    Icon(
                        painterResource(Res.drawable.ic_gst),
                        contentDescription = null
                    )
                }

            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(stringResource(Res.string.business_address)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                leadingIcon = {
                    Icon(
                        painterResource(Res.drawable.ic_location),
                        contentDescription = null
                    )
                }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(Res.string.email_optional)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {  Icon(
                    painterResource(Res.drawable.ic_email),
                    contentDescription = null
                ) }
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(Res.string.phone_optional)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {  Icon(
                    painterResource(Res.drawable.ic_phone),
                    contentDescription = null
                ) }
            )

            OutlinedTextField(
                value = gstRate,
                onValueChange = { gstRate = it },
                label = { Text(stringResource(Res.string.default_gst_rate)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("18") }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank() && gstin.isNotBlank()) {
                        viewModel.saveBusinessProfile(
                            name = name,
                            gstin = gstin,
                            address = address,
                            email = email,
                            phone = phone,
                            defaultGstRate = gstRate.toDoubleOrNull() ?: 18.0
                        )
                        onSetupComplete()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = name.isNotBlank() && gstin.isNotBlank()
            ) {
                Text(stringResource(Res.string.continue_button))
            }
        }
    }
}
