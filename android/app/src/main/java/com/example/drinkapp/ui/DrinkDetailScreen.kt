package com.example.drinkapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.R
import com.example.drinkapp.model.DrinkItem
import com.example.drinkapp.model.DrinkOption
import com.example.drinkapp.model.sampleMenuData

@Composable
fun DrinkDetailScreen(
    drink: DrinkItem,
    isFavorite: Boolean = false,
    onToggleFavorite: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onAddToCart: (
        drink: DrinkItem,
        selectedSize: DrinkOption?,
        selectedToppings: List<DrinkOption>,
        quantity: Int,
        note: String,
        totalPrice: Int
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    // Mặc định chọn Size đầu tiên
    var selectedSize by remember { mutableStateOf(drink.sizes.firstOrNull()) }
    // Lưu danh sách ID các topping đang được chọn
    val selectedToppingIds = remember { mutableStateListOf<String>() }
    var quantity by remember { mutableIntStateOf(1) }
    var note by remember { mutableStateOf("") }

    // Tự động tính lại giá mỗi khi đổi Size, Topping hoặc Số lượng
    val selectedToppings = drink.toppings.filter { it.id in selectedToppingIds }
    val unitPrice = drink.basePrice +
            (selectedSize?.extraPrice ?: 0) +
            selectedToppings.sumOf { it.extraPrice }
    val totalPrice = unitPrice * quantity

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(42.dp)
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
                    text = "Chi tiết món",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeDark
                )

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Yêu thích",
                        tint = if (isFavorite) Color(0xFFD32F2F) else CoffeeDark
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = CardSurface,
                shadowElevation = 10.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Cụm nút tăng giảm số lượng (- 1 +)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(CreamBackground)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Giảm",
                                tint = if (quantity > 1) CoffeeDark else CoffeeMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "$quantity",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeDark,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        IconButton(
                            onClick = { quantity++ },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tăng",
                                tint = CoffeePrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Nút Thêm vào giỏ hàng màu nâu chủ đạo
                    Button(
                        onClick = {
                            onAddToCart(
                                drink,
                                selectedSize,
                                selectedToppings,
                                quantity,
                                note.trim(),
                                totalPrice
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Thêm • ${formatVnd(totalPrice)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // 1. Ảnh minh họa món
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = CardSurface,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(20.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_coffee),
                            contentDescription = drink.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(165.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }

            // 2. Tên món, giá gốc và mô tả
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = drink.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoffeeDark,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatVnd(drink.basePrice),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = drink.description,
                        fontSize = 14.sp,
                        color = CoffeeMuted,
                        lineHeight = 20.sp
                    )
                }
            }

            // 3. Chọn kích cỡ (RadioButton)
            if (drink.sizes.isNotEmpty()) {
                item {
                    OptionSectionCard(title = "Chọn kích cỡ (Bắt buộc)") {
                        drink.sizes.forEach { size ->
                            val isSelected = selectedSize?.id == size.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { selectedSize = size }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedSize = size },
                                    colors = RadioButtonDefaults.colors(selectedColor = CoffeePrimary)
                                )
                                Text(
                                    text = size.name,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = CoffeeDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = if (size.extraPrice == 0) "Mặc định" else "+${formatVnd(size.extraPrice)}",
                                    fontSize = 14.sp,
                                    color = if (size.extraPrice == 0) CoffeeMuted else CoffeePrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 4. Chọn Topping (Checkbox)
            if (drink.toppings.isNotEmpty()) {
                item {
                    OptionSectionCard(title = "Thêm Topping (Tùy chọn)") {
                        drink.toppings.forEach { topping ->
                            val isChecked = topping.id in selectedToppingIds
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable {
                                        if (isChecked) selectedToppingIds.remove(topping.id)
                                        else selectedToppingIds.add(topping.id)
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedToppingIds.add(topping.id)
                                        else selectedToppingIds.remove(topping.id)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = CoffeePrimary)
                                )
                                Text(
                                    text = topping.name,
                                    fontSize = 15.sp,
                                    fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal,
                                    color = CoffeeDark,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "+${formatVnd(topping.extraPrice)}",
                                    fontSize = 14.sp,
                                    color = CoffeePrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // 5. Ghi chú cho Barista
            item {
                OptionSectionCard(title = "Ghi chú cho Barista") {
                    OutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = {
                            Text("Ví dụ: Ít đá, ít đường, để riêng đá...", fontSize = 14.sp, color = CoffeeMuted)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoffeePrimary,
                            unfocusedBorderColor = Color(0xFFE0D7CC),
                            cursorColor = CoffeePrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        maxLines = 2
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun OptionSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = CardSurface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CoffeeDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDrinkDetailScreen() {
    DrinkDetailScreen(drink = sampleMenuData.first())
}
