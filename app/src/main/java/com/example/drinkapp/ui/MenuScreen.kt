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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.R
import com.example.drinkapp.model.FoodItem
import com.example.drinkapp.model.sampleMenuData

val CreamBackground = Color(0xFFFAF7F2)   // Nền kem ấm giống ảnh mẫu
val CoffeePrimary = Color(0xFF6F4E37)     // Nâu chủ đạo
val CoffeeDark = Color(0xFF2C1E16)        // Nâu đậm cho tiêu đề
val CoffeeMuted = Color(0xFF8D7B68)       // Nâu nhạt cho mô tả & tab chưa chọn
val CardSurface = Color(0xFFFFFFFF)       // Nền trắng cho thẻ sản phẩm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menuList: List<FoodItem> = sampleMenuData,
    cartItemCount: Int = 0,
    cartTotalPrice: Int = 0,
    onFoodClick: (FoodItem) -> Unit = {},
    onOpenCartClick: () -> Unit = {}
) {
    val categories = listOf("Tất cả", "Coffee", "Bánh ngọt")
    var selectedCategory by remember { mutableStateOf("Tất cả") }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    val filteredFoods = if (selectedCategory == "Tất cả") {
        menuList
    } else {
        menuList.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            // Header giống ảnh 1: Logo + Tên quán bên trái, Icon giỏ hàng bên phải
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
                        Text(
                            text = "Đang mở cửa • Giao hàng tự động",
                            fontSize = 12.sp,
                            color = CoffeePrimary
                        )
                    }
                }

                // Icon Giỏ hàng kèm số lượng món ở góc trên phải (giống ảnh 1)
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = CoffeePrimary,
                                contentColor = Color.White
                            ) {
                                Text("$cartItemCount")
                            }
                        }
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onOpenCartClick() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Giỏ hàng",
                        tint = CoffeeDark,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Thanh điều hướng nổi 4 icon dưới đáy (giống ảnh 1)
                FloatingBottomNavBar(
                    selectedIndex = selectedNavIndex,
                    onSelect = { selectedNavIndex = it }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // 2. Thanh Tab danh mục dạng chữ có gạch chân màu nâu (thay cho FilterChip)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = selectedCategory == category
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = category,
                                fontSize = 15.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) CoffeeDark else CoffeeMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(2.5.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) CoffeePrimary else Color.Transparent)
                            )
                        }
                    }
                }
            }

            // 3. Hàng món nổi bật cuộn ngang (Ảnh ở trên, tên căn giữa ở dưới - giống ảnh 1)
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredFoods.take(5), key = { "featured_${it.id}" }) { food ->
                        FeaturedFoodItem(
                            food = food,
                            onClick = { onFoodClick(food) }
                        )
                    }
                }
            }

            // 4. Tiêu đề khu vực danh sách món chính (tương đương "Coffee beans" ở ảnh 1)
            item {
                Text(
                    text = "Thức uống nổi bật",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeDark,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 10.dp)
                )
            }

            // 5. Danh sách thẻ sản phẩm đã đổi sang tông trắng - nâu bo góc 20.dp
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
        modifier = modifier.size(75.dp)
    )
}

// Item cuộn ngang phía trên (giống 3 ly Cappuccino, Latte, Americano ở ảnh 1)
@Composable
fun FeaturedFoodItem(
    food: FoodItem,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(96.dp)
            .clickable { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardSurface,
            shadowElevation = 2.dp,
            modifier = Modifier.size(84.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                Image(
                    painter = painterResource(R.drawable.logo_coffee),
                    contentDescription = food.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = food.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = CoffeeDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
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
            // Ảnh món bo góc bên trái thẻ
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

            // Nút + Chọn tông nâu chủ đạo
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
fun FloatingBottomNavBar(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val navItems = listOf(
        Icons.Default.Home to "Trang chủ",
        Icons.Default.Search to "Tìm kiếm",
        Icons.Default.FavoriteBorder to "Yêu thích",
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

fun formatVnd(amount: Int): String = "%,dđ".format(amount).replace(',', '.')

@Preview(showBackground = true)
@Composable
fun PreviewMenuScreen() {
    MenuScreen(cartItemCount = 2, cartTotalPrice = 95000)
}
