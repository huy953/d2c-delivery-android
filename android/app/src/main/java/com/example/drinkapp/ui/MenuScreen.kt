package com.example.drinkapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.R
import com.example.drinkapp.model.DrinkItem
import com.example.drinkapp.model.FoodItem
import com.example.drinkapp.model.sampleMenuData

val CreamBackground = Color(0xFFFAF7F2)   // Nền kem ấm
val CoffeePrimary = Color(0xFF6F4E37)     // Nâu chủ đạo
val CoffeeDark = Color(0xFF2C1E16)        // Nâu đậm cho tiêu đề
val CoffeeMuted = Color(0xFF8D7B68)       // Nâu nhạt cho mô tả & tab chưa chọn
val CardSurface = Color(0xFFFFFFFF)       // Nền trắng cho thẻ sản phẩm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menuList: List<FoodItem> = sampleMenuData,
    cartItemCount: Int = 0,
    storeStatus: String = "Đang mở cửa",
    onFoodClick: (FoodItem) -> Unit = {},
    onSearchClick: () -> Unit = {},
    selectedNavIndex: Int = 0,
    onSelectNav: (Int) -> Unit = {}
) {
    val categories = listOf("Tất cả", "Cà phê", "Bánh ngọt")
    var selectedCategory by remember { mutableStateOf("Tất cả") }

    val filteredFoods = if (selectedCategory == "Tất cả") {
        menuList
    } else {
        menuList.filter {
            it.category.equals(selectedCategory, ignoreCase = true) ||
                    (selectedCategory == "Cà phê" && it.category.equals("Coffee", ignoreCase = true))
        }
    }

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            // Header: Logo + Tên quán bên trái, Icon Tìm kiếm bên phải
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ImageLogo(modifier = Modifier.clip(CircleShape))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Coffee Shop",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = CoffeeDark
                        )

                        val (statusSubtitle, statusColor) = when {
                            storeStatus == "Đang mở cửa" -> "Đang mở cửa • Giao hàng tự động" to CoffeePrimary
                            storeStatus == "Đã đóng cửa" -> "Quán đã đóng cửa" to Color(0xFFD32F2F)
                            else -> "Quán bận ($storeStatus)" to Color(0xFFE65100)
                        }

                        Text(
                            text = statusSubtitle,
                            fontSize = 12.sp,
                            color = statusColor
                        )
                    }
                }

                // Icon Tìm kiếm ở góc trên phải để mở SearchScreen
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Tìm kiếm",
                        tint = CoffeeDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        },
        bottomBar = {
            FloatingBottomNavBar(
                selectedIndex = selectedNavIndex,
                onSelect = onSelectNav,
                cartItemCount = cartItemCount
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // 1. Thanh Tab chọn danh mục
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = category,
                                fontSize = 16.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CoffeeDark else CoffeeMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) CoffeePrimary else Color.Transparent)
                            )
                        }
                    }
                }
            }

            // 2. Hiển thị thẳng toàn bộ món theo danh mục đã chọn
            items(filteredFoods, key = { it.id }) { food ->
                FoodItemCard(
                    food = food,
                    onClick = { onFoodClick(food) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun ImageLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_coffee),
        contentDescription = "Logo Coffee shop",
        contentScale = ContentScale.Crop,
        modifier = modifier.size(50.dp)
    )
}

@Composable
fun FoodItemCard(
    food: FoodItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.logo_coffee),
                contentDescription = food.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(CreamBackground)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = CoffeeDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = food.description,
                    fontSize = 12.sp,
                    color = CoffeeMuted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatVnd(food.basePrice),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CoffeePrimary
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.size(width = 34.dp, height = 36.dp)
            ) {
                Text("+", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun DrinkItemCard(
    drink: DrinkItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FoodItemCard(food = drink, onClick = onClick, modifier = modifier)
}

@Composable
fun FloatingBottomNavBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    cartItemCount: Int = 0
) {
    val navItems = listOf(
        Icons.Default.Home to "Trang chủ",
        Icons.Default.FavoriteBorder to "Yêu thích",
        Icons.Default.ShoppingCart to "Giỏ hàng",
        Icons.Default.Person to "Tài khoản"
    )

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CardSurface,
        shadowElevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .height(58.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, (icon, label) ->
                val isSelected = selectedIndex == index
                IconButton(onClick = { onSelect(index) }) {
                    if (index == 2 && cartItemCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = CoffeePrimary,
                                    contentColor = Color.White
                                ) {
                                    Text("$cartItemCount")
                                }
                            }
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) CoffeePrimary else CoffeeMuted,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (isSelected) CoffeePrimary else CoffeeMuted,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatVnd(amount: Int): String = "%,dđ".format(amount).replace(',', '.')

@Preview(showBackground = true)
@Composable
fun PreviewMenuScreen() {
    MenuScreen(cartItemCount = 2)
}
