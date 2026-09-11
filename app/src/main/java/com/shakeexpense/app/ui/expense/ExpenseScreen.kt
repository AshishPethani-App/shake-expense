package com.shakeexpense.app.ui.expense

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shakeexpense.app.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun ExpenseScreen(
    onDismiss: () -> Unit,
    viewModel: ExpenseViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val amountFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        amountFocus.requestFocus()
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            delay(1500)
            viewModel.reset()
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(28.dp)) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Add Expense", fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = TextPrimary, modifier = Modifier.weight(1f))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(state.isSaved) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()) {
                        Text("✓ Expense saved", color = Success,
                            fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (!state.isSaved) {
                    OutlinedTextField(
                        value = state.amount,
                        onValueChange = viewModel::onAmountChange,
                        label = { Text("Amount") },
                        prefix = { Text("₹ ", fontWeight = FontWeight.SemiBold) },
                        placeholder = { Text("0") },
                        isError = state.amountError != null,
                        supportingText = { state.amountError?.let { Text(it, color = Error) } },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(amountFocus),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text("For") },
                        placeholder = { Text("What was this for?") },
                        isError = state.descriptionError != null,
                        supportingText = { state.descriptionError?.let { Text(it, color = Error) } },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { viewModel.saveExpense() }),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(8.dp))
                    state.errorMessage?.let {
                        Text(it, color = Error, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = viewModel::saveExpense,
                        enabled = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = OnPrimary,
                                modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                        } else {
                            Text("SAVE EXPENSE", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
