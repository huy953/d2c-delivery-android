package com.example.drinkapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.R
import com.example.drinkapp.model.CartItem
import com.example.drinkapp.model.sampleCartItems

@Composable
fun CartScreen(
    cartItems: MutableList<CartItem> = remember { mutableStateListOf<CartItem>().apply { addAll(sampleCartItems) } },
    selectedNavIndex: Int = 2,
    onSelectNav: (Int) -> Unit = {},
    onCheckoutClick: (List<CartItem>, Int) -> Unit = { _, _ -> }
) {
    val totalAmount = cartItems.sumOf { it.totalPrice }
    val totalQuantity = cartItems.sumOf { it.quantity }

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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Giỏ hàng",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoffeeDark
                    )

                    if (cartItems.isNotEmpty()) {
                        IconButton(
                            onClick = { cartItems.clear() },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CardSurface)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Xóa tất cả",
                                tint = CoffeeMuted
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (cartItems.isNotEmpty()) {
                    Surface(
                        color = CreamBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Tổng cộng",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CoffeeMuted
                                )
                                Text(
                                    text = formatVnd(totalAmount),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CoffeeDark
                                )
                            }

                            Button(
                                onClick = { onCheckoutClick(cartItems.toList(), totalAmount) },
                                colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = "Đặt hàng",
                                    color = androidx.compose.ui.graphics.Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                FloatingBottomNavBar(
                    selectedIndex = selectedNavIndex,
                    onSelect = onSelectNav,
                    cartItemCount = totalQuantity
                )
            }
        }
    ) { innerPadding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Giỏ hàng của bạn đang trống",
                    fontSize = 16.sp,
                    color = CoffeeMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onIncrease = {
                            val idx = cartItems.indexOfFirst { it.id == item.id }
                            if (idx != -1) {
                                cartItems[idx] = item.copy(quantity = item.quantity + 1)
                            }
                        },
                        onDecrease = {
                            val idx = cartItems.indexOfFirst { it.id == item.id }
                            if (idx != -1) {
                                if (item.quantity > 1) {
                                    cartItems[idx] = item.copy(quantity = item.quantity - 1)
                                } else {
                                    cartItems.removeAt(idx)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CardSurface,
        shadowElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_coffee),
                contentDescription = item.drink.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape)
                    .background(CreamBackground)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.drink.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (item.optionsSummary.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.optionsSummary,
                        fontSize = 13.sp,
                        color = CoffeeMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatVnd(item.totalPrice),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CoffeePrimary
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CreamBackground)
                                .clickable { onDecrease() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Giảm",
                                tint = CoffeeDark,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Text(
                            text = "${item.quantity}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeDark
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(CreamBackground)
                                .clickable { onIncrease() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = CoffeePrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCartScreen() {
    CartScreen()
}
