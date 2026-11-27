package com.example.drinkapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showRegisterSuccessDialog by remember { mutableStateOf(false) }

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

                Spacer(modifier = Modifier.height(24.dp))

                // Ô nhập Tên đăng nhập / Số điện thoại
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = { Text("Tên đăng nhập / SĐT") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
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
                    placeholder = { Text("Nhập mật khẩu của bạn") },
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

                // 1. Nút Đăng nhập (Màu nâu chữ trắng)
                Button(
                    onClick = {
                        if (username.isBlank()) {
                            errorMessage = "Vui lòng nhập Tên đăng nhập / SĐT!"
                        } else {
                            errorMessage = ""
                            val role = if (username.trim().lowercase().contains("barista")) "BARISTA" else "USER"
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

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Nút Đăng ký (Màu trắng chữ nâu)
                OutlinedButton(
                    onClick = {
                        if (username.isBlank()) {
                            errorMessage = "Vui lòng nhập Tên đăng nhập / SĐT để đăng ký!"
                        } else {
                            errorMessage = ""
                            showRegisterSuccessDialog = true
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                    border = BorderStroke(1.5.dp, CoffeePrimary),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = "Đăng ký",
                        color = CoffeePrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hướng dẫn tài khoản thử nghiệm ngay trên giao diện
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CreamBackground,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "💡 Gợi ý đăng nhập thử nghiệm:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Barista: Nhập tên chứa 'barista' (VD: barista)",
                            fontSize = 11.sp,
                            color = CoffeeMuted
                        )
                        Text(
                            text = "• Khách hàng: Nhập SĐT/Tên bất kỳ (VD: 0901234567)",
                            fontSize = 11.sp,
                            color = CoffeeMuted
                        )
                    }
                }
            }
        }
    }

    // Dialog thông báo Đăng ký thành công
    if (showRegisterSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterSuccessDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Đăng ký thành công!",
                    fontWeight = FontWeight.Bold,
                    color = CoffeeDark
                )
            },
            text = {
                Text(
                    text = "Tài khoản của bạn đã được khởi tạo thành công. Bạn có thể bấm Đăng nhập ngay bây giờ!",
                    color = CoffeeMuted,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRegisterSuccessDialog = false
                        val role = if (username.trim().lowercase().contains("barista")) "BARISTA" else "USER"
                        onLoginSuccess(role)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Đăng nhập ngay", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}
