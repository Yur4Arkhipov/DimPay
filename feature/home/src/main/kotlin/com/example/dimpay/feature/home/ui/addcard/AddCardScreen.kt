package com.example.dimpay.feature.home.ui.addcard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dimpay.core.designsystem.R
import com.example.dimpay.core.designsystem.extensions.CardNumberTransformation
import com.example.dimpay.core.designsystem.extensions.ExpireDateTransformation
import com.example.dimpay.feature.home.ui.addcard.component.CardPreview

@Composable
fun AddCardScreen(
    viewModel: AddCardViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF8F8FB),
                        Color.White
                    )
                )
            )
            .focusable()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { focusManager.clearFocus() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = null
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Добавление карты",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF1A1A3A)
                )
            }

            CardPreview(
                cardName = uiState.cardName,
                cardNumber = uiState.cardNumber,
                expireDate = uiState.expireDate,
                cardCvv = uiState.cvv,
                isCvvVisible = uiState.isCvvVisible
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Данные карты",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = uiState.cardName,
                onValueChange = viewModel::onCardNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Название карты")
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.cardNumber,
                onValueChange = viewModel::onCardNumberChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text("Номер карты")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                visualTransformation = CardNumberTransformation(),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.expireDate,
                    onValueChange = viewModel::onExpireDateChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("MM/YY")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    visualTransformation = ExpireDateTransformation(),
                    shape = RoundedCornerShape(20.dp)
                )

                OutlinedTextField(
                    value = uiState.cvv,
                    onValueChange = viewModel::onCvvChange,
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("CVV")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    visualTransformation = if (uiState.isCvvVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = viewModel::toggleCvvVisibility
                        ) {
                            Icon(
                                painter = if (uiState.isCvvVisible) {
                                    painterResource(R.drawable.ic_eye_close)
                                } else {
                                    painterResource(R.drawable.ic_eye_open)
                                },
                                contentDescription = null
                            )
                        }
                    },
                    shape = RoundedCornerShape(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5B3FD8)
                )
            ) {
                Text(
                    text = "Добавить карту",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}