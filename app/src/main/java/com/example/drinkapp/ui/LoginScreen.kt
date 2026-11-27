package com.example.drinkapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.R

@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit = {}
) {
    // 0: Khách hàng (USER) | 1: Barista / Nhân viên (BARISTA)
    var selectedRoleIndex by remember { mutableIntStateOf(0) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamBackground),
        contentAlignment = Alignment.Center
    ) {
        // Khung ô vuông bự chứa toàn bộ giao diện đăng nhập
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = CardSurface,
            shadowElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // --- PHẦN TOP: Logo và Tên quán ---
                Image(
                    painter = painterResource(id = R.drawable.logo_coffee),
                    contentDescription = "Logo Coffee Shop",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(CreamBackground)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Coffee Shop",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CoffeeDark
                )

                Text(
                    text = "Đăng nhập để trải nghiệm dịch vụ",
                    fontSize = 13.sp,
                    color = CoffeeMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- PHẦN THÔNG TIN ĐĂNG NHẬP ---

                // Choose Role Selector (Khách hàng vs Barista)
                Text(
                    text = "Chọn vai trò đăng nhập:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CoffeeDark,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CreamBackground)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedRoleIndex == 0) CoffeePrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        TextButton(onClick = { selectedRoleIndex = 0 }) {
                            Text(
                                text = "Khách hàng",
                                color = if (selectedRoleIndex == 0) Color.White else CoffeeMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedRoleIndex == 1) CoffeePrimary else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        TextButton(onClick = { selectedRoleIndex = 1 }) {
                            Text(
                                text = "Barista",
                                color = if (selectedRoleIndex == 1) Color.White else CoffeeMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập Tên đăng nhập / Số điện thoại
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Tên đăng nhập / SĐT") },
                    placeholder = {
                        Text(if (selectedRoleIndex == 0) "VD: 0901234567" else "VD: barista_01")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (selectedRoleIndex == 0) Icons.Default.Person else Icons.Default.Badge,
                            contentDescription = null,
                            tint = CoffeeMuted
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoffeePrimary,
                        focusedLabelColor = CoffeePrimary,
                        cursorColor = CoffeePrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ô nhập Mật khẩu
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Mật khẩu") },
                    placeholder = { Text("Mật khẩu của bạn") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = CoffeeMuted
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CoffeePrimary,
                        focusedLabelColor = CoffeePrimary,
                        cursorColor = CoffeePrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFD32F2F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Nút Đăng nhập
                Button(
                    onClick = {
                        if (username.isBlank()) {
                            errorMessage = "Vui lòng nhập Tên đăng nhập / SĐT!"
                        } else {
                            errorMessage = ""
                            val role = if (selectedRoleIndex == 0) "USER" else "BARISTA"
                            onLoginSuccess(role)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Đăng nhập",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}
