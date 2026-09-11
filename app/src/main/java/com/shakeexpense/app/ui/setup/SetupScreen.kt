package com.shakeexpense.app.ui.setup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shakeexpense.app.ui.theme.*

@Composable
fun SetupScreen(onSetupComplete: () -> Unit) {
    var url by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Shake Expense", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Record expenses instantly by shaking your phone.",
            textAlign = TextAlign.Center, color = TextSecondary, fontSize = 15.sp)
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(value = url, onValueChange = { url = it },
            label = { Text("Apps Script URL (optional)") },
            placeholder = { Text("https://script.google.com/...") },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true)
        Spacer(Modifier.height(20.dp))
        Button(onClick = onSetupComplete, modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)) {
            Text("GET STARTED", fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onSetupComplete) { Text("Continue in Demo Mode", color = TextSecondary) }
    }
}
