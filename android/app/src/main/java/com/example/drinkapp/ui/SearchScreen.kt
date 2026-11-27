package com.example.drinkapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.model.DrinkItem

@Composable
fun SearchScreen(
    menuList: List<DrinkItem>,
    onBackClick: () -> Unit = {},
    onDrinkClick: (DrinkItem) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val quickKeywords = listOf("Cappuccino", "Bạc Xỉu", "Americano", "Latte", "Bánh")

    val searchResults = if (searchQuery.isBlank()) {
        menuList
    } else {
        menuList.filter {
            it.name.contains(searchQuery.trim(), ignoreCase = true) ||
                    it.description.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CardSurface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = CoffeeDark
                        )
                    }

                    Text(
                        text = "Tìm kiếm đồ uống",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoffeeDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Nhập tên món, hương vị...", color = CoffeeMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CoffeePrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Xóa", tint = CoffeeMuted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardSurface,
                        unfocusedContainerColor = CardSurface,
                        focusedBorderColor = CoffeePrimary,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = CoffeePrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickKeywords) { keyword ->
                        val isSelected = searchQuery.equals(keyword, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) CoffeePrimary else CardSurface,
                            shadowElevation = 1.dp,
                            modifier = Modifier.clickable { searchQuery = keyword }
                        ) {
                            Text(
                                text = keyword,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) Color.White else CoffeeDark,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            if (searchResults.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không tìm thấy món phù hợp với \"$searchQuery\"",
                            color = CoffeeMuted,
                            fontSize = 15.sp
                        )
                    }
                }
            } else {
                items(searchResults, key = { it.id }) { drink ->
                    DrinkItemCard(
                        drink = drink,
                        onClick = { onDrinkClick(drink) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}
