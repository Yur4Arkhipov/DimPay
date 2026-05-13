package com.example.dimpay.feature.home.ui.home

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dimpay.core.designsystem.R
import com.example.dimpay.core.designsystem.theme.bgMainColor
import com.example.dimpay.feature.home.model.BankCardUi
import com.example.dimpay.feature.home.ui.home.components.BankCardItem
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import androidx.core.graphics.createBitmap

@androidx.annotation.RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
@Composable
fun HomeScreen(
    onNavigateToAddCard: () -> Unit,
    onNavigateToQr: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    var selectedCard by remember { mutableStateOf<BankCardUi?>(null) }
    val paymentDialogState by viewModel.paymentDialogState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    LaunchedEffect(paymentDialogState.navigateToOfflineQr) {
        if (paymentDialogState.navigateToOfflineQr) {
            viewModel.closePaymentDialog()
            viewModel.resetOfflineNavigation()
            Log.d("VMHome", "selectedCard: ${paymentDialogState.card?.cardId}")
            paymentDialogState.card?.cardId?.let { onNavigateToQr(it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgMainColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 6.dp
                )
            ) {
                Text(
                    text = "Мои карты",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1A1A3A)
                )
            }

            if (cards.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "У вас пока нет карт",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color(0xFF1A1A3A)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Добавьте карту для оплаты",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 120.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(
                        items = cards,
                        key = { it.cardId }
                    ) { card ->
                        BankCardItem(
                            card = card,
                            onClick = {
                                viewModel.openPaymentDialog(card)
                            },
                            onLongClick = {
                                selectedCard = card
                            }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onNavigateToAddCard,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .fillMaxWidth()
                .height(64.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5B3FD8)
            ),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 10.dp
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null,
                tint = Color.White
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Добавить карту",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = Color.White
            )
        }
    }

    selectedCard?.let { card ->
        AlertDialog(
            onDismissRequest = {
                selectedCard = null
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCard(card.cardId)
                        selectedCard = null
                    }
                ) {
                    Text("Удалить")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedCard = null
                    }
                ) {
                    Text("Отмена")
                }
            },
            title = { Text("Удалить карту?") },
            text = { Text("Карта ${card.cardName} будет удалена") }
        )
    }

    paymentDialogState.card?.let { card ->
        AlertDialog(
            onDismissRequest = { viewModel.closePaymentDialog() },
            confirmButton = {
                when {
                    paymentDialogState.confirmation != null -> {
                        TextButton(
                            onClick = {
                                viewModel.confirmPayment()
                            }
                        ) {
                            Text("Подтвердить")
                        }
                    }
                    paymentDialogState.qrValue == null -> {
                        TextButton(
                            onClick = {
                                viewModel.onPayClick(context)
                            }
                        ) {
                            Text("Оплатить")
                        }
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (
                            paymentDialogState.qrValue != null ||
                            paymentDialogState.confirmation != null
                        ) {
                            viewModel.cancelPayment()
                        }
                        viewModel.closePaymentDialog()
                    }
                ) {
                    Text("Закрыть")
                }
            },
            title = {
                Text(
                    if (paymentDialogState.qrValue == null) {
                        "Подтверждение оплаты"
                    } else {
                        "QR код оплаты"
                    }
                )
            },
            text = {
                when {
                    paymentDialogState.isLoading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    paymentDialogState.qrValue != null -> {
                        val bitmap = remember(
                                paymentDialogState.qrValue
                            ) {
                                generateQrBitmap(paymentDialogState.qrValue ?: "")
                            }
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier.size(260.dp)
                        )
                    }
                    paymentDialogState.error != null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Ошибка оплаты",
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = paymentDialogState.error ?: ""
                            )
                        }
                    }
                    paymentDialogState.confirmation != null -> {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 8.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PaymentInfoRow(
                                    label = "Продавец",
                                    value = paymentDialogState.confirmation?.merchantName
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                PaymentInfoRow(
                                    label = "Сумма",
                                    value = "${paymentDialogState.confirmation?.amount} ₽"
                                )
                            }
                        }
                    }
                    else -> {
                        Text(text = "Вы хотите оплатить картой " + "\"${card.cardName}\"?")
                    }
                }
            }
        )
    }
}

@Composable
private fun PaymentInfoRow(
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

fun generateQrBitmap(
    text: String,
    size: Int = 512
): Bitmap {
    val bits = MultiFormatWriter().encode(
        text,
        BarcodeFormat.QR_CODE,
        size,
        size
    )

    return createBitmap(size, size, Bitmap.Config.RGB_565).apply {

        for (x in 0 until size) {
            for (y in 0 until size) {
                setPixel(x, y,
                    if (bits[x, y]) {
                        android.graphics.Color.BLACK
                    } else {
                        android.graphics.Color.WHITE
                    }
                )
            }
        }
    }
}