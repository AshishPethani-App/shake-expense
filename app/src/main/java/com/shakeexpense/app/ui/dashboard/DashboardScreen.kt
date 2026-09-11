package com.shakeexpense.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shakeexpense.app.domain.model.Expense
import com.shakeexpense.app.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    onAddExpense: () -> Unit,
    onSettings: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shake Expense", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense,
                containerColor = Primary, contentColor = OnPrimary) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Primary)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Today's Expenses", color = OnPrimary.copy(alpha = 0.85f), fontSize = 14.sp)
                        Spacer(Modifier.height(4.dp))
                        Text("₹ \${fmt.format(state.todayTotal)}",
                            color = OnPrimary, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(2.dp))
                        Text("\${state.todayCount} expense\${if (state.todayCount != 1) "s" else ""}",
                            color = OnPrimary.copy(alpha = 0.75f), fontSize = 14.sp)
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CardBg)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Shake Detection", fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text(if (state.shakeEnabled) "Active - shake to add" else "Disabled",
                            fontSize = 12.sp, color = TextSecondary)
                    }
                    Switch(checked = state.shakeEnabled,
                        onCheckedChange = { viewModel.toggleShake(it) })
                }
            }
            if (state.recentExpenses.isNotEmpty()) {
                item { Text("Recent Expenses", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary) }
                items(state.recentExpenses) { expense -> ExpenseRow(expense) }
            } else {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No expenses yet", color = TextSecondary, fontSize = 16.sp)
                            Text("Shake your phone to add one!", color = TextSecondary, fontSize = 14.sp)
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ExpenseRow(expense: Expense) {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "IN"))
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardBg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(expense.description, fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 15.sp)
            Text(expense.createdAt.take(10), fontSize = 12.sp, color = TextSecondary)
        }
        Text("₹\${fmt.format(expense.amount)}", fontWeight = FontWeight.SemiBold, color = Primary, fontSize = 16.sp)
    }
}
