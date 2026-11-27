package com.example.drinkapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.model.DrinkItem

@Composable
fun FavoriteScreen(
    favoriteDrinks: List<DrinkItem>,
    cartItemCount: Int = 0,
    selectedNavIndex: Int = 1,
    onSelectNav: (Int) -> Unit = {},
    onDrinkClick: (DrinkItem) -> Unit = {},
    onExploreMenuClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Món yêu thích",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CoffeeDark
                )
                Text(
                    text = "${favoriteDrinks.size} món đã lưu",
                    fontSize = 13.sp,
                    color = CoffeeMuted
                )
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
        if (favoriteDrinks.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = CoffeeMuted,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Bạn chưa lưu món yêu thích nào",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Hãy bấm biểu tượng trái tim trong trang chi tiết món để lưu lại nhé!",
                    fontSize = 13.sp,
                    color = CoffeeMuted
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onExploreMenuClick,
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Khám phá thực đơn", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteDrinks, key = { it.id }) { drink ->
                    DrinkItemCard(
                        drink = drink,
                        onClick = { onDrinkClick(drink) }
                    )
                }
            }
        }
    }
}
