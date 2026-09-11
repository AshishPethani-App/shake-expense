package com.shakeexpense.app.ui.settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shakeexpense.app.ui.theme.*

@Composable
fun SettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var urlInput by remember(state.appsScriptUrl) { mutableStateOf(state.appsScriptUrl) }
    var tokenInput by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }, containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingSection(title = "Shake Detection") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable", Modifier.weight(1f), color = TextPrimary)
                    Switch(checked = state.shakeEnabled, onCheckedChange = { viewModel.setShakeEnabled(it) })
                }
                Spacer(Modifier.height(8.dp))
                Text("Sensitivity", fontWeight = FontWeight.Medium, color = TextPrimary)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { s ->
                        FilterChip(selected = state.sensitivity == s,
                            onClick = { viewModel.setSensitivity(s) },
                            label = { Text(s.lowercase().replaceFirstChar { it.uppercase() }) })
                    }
                }
            }
            SettingSection(title = "Google Sheet Connection") {
                OutlinedTextField(value = urlInput, onValueChange = { urlInput = it },
                    label = { Text("Apps Script Web App URL") },
                    placeholder = { Text("https://script.google.com/...") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = tokenInput, onValueChange = { tokenInput = it },
                    label = { Text("Secret Token (optional)") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
                Spacer(Modifier.height(8.dp))
                Button(onClick = {
                    viewModel.setAppsScriptUrl(urlInput)
                    if (tokenInput.isNotBlank()) viewModel.setSecretToken(tokenInput)
                }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
                    Text("Save Connection")
                }
            }
            SettingSection(title = "Sync") {
                Text("Last synced: ${state.lastSynced}", color = TextSecondary, fontSize = 13.sp)
            }
            SettingSection(title = "About") { Text("Shake Expense v1.0.0", color = TextSecondary) }
        }
    }
}

@Composable
fun SettingSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardBg)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}
