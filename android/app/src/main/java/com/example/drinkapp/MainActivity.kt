package com.example.drinkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drinkapp.ui.BaristaScreen
import com.example.drinkapp.ui.CartScreen
import com.example.drinkapp.ui.CheckoutScreen
import com.example.drinkapp.ui.DrinkDetailScreen
import com.example.drinkapp.ui.FavoriteScreen
import com.example.drinkapp.ui.LoginScreen
import com.example.drinkapp.ui.MenuScreen
import com.example.drinkapp.ui.ProfileScreen
import com.example.drinkapp.ui.SearchScreen
import com.example.drinkapp.ui.theme.FoodAppTheme
import com.example.drinkapp.viewmodel.CoffeeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodAppTheme {
                CoffeeAppNavigation()
            }
        }
    }
}

@Composable
fun CoffeeAppNavigation(vm: CoffeeViewModel = viewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (vm.currentScreen) {
            "LOGIN" -> {
                LoginScreen(
                    onLoginSuccess = { role ->
                        vm.login(role)
                    }
                )
            }

            "MAIN_TABS", "MENU" -> {
                when (vm.selectedNavTab) {
                    0 -> MenuScreen(
                        menuList = vm.menuList,
                        cartItemCount = vm.cartItems.sumOf { it.quantity },
                        storeStatus = vm.storeStatus,
                        onFoodClick = { drink -> vm.openDrinkDetail(drink) },
                        onSearchClick = { vm.currentScreen = "SEARCH" },
                        selectedNavIndex = vm.selectedNavTab,
                        onSelectNav = { vm.selectedNavTab = it }
                    )
                    1 -> FavoriteScreen(
                        favoriteDrinks = vm.menuList.filter { it.id in vm.favoriteDrinkIds },
                        cartItemCount = vm.cartItems.sumOf { it.quantity },
                        selectedNavIndex = vm.selectedNavTab,
                        onSelectNav = { vm.selectedNavTab = it },
                        onDrinkClick = { drink -> vm.openDrinkDetail(drink) },
                        onExploreMenuClick = { vm.selectedNavTab = 0 }
                    )
                    2 -> CartScreen(
                        cartItems = vm.cartItems,
                        selectedNavIndex = vm.selectedNavTab,
                        onSelectNav = { vm.selectedNavTab = it },
                        onCheckoutClick = { _, _ -> vm.currentScreen = "CHECKOUT" }
                    )
                    3 -> ProfileScreen(
                        orders = vm.baristaOrders,
                        cartItemCount = vm.cartItems.sumOf { it.quantity },
                        selectedNavIndex = vm.selectedNavTab,
                        onSelectNav = { vm.selectedNavTab = it },
                        onReorder = { order -> vm.reorder(order) },
                        onCancelOrder = { order, reason -> vm.cancelCustomerOrder(order.orderId, reason) },
                        onLogout = { vm.logout() }
                    )
                }
            }

            "SEARCH" -> {
                SearchScreen(
                    menuList = vm.menuList,
                    onBackClick = { vm.currentScreen = "MAIN_TABS" },
                    onDrinkClick = { drink -> vm.openDrinkDetail(drink) }
                )
            }

            "DETAIL" -> {
                vm.selectedDrink?.let { drink ->
                    DrinkDetailScreen(
                        drink = drink,
                        isFavorite = drink.id in vm.favoriteDrinkIds,
                        onToggleFavorite = { vm.toggleFavorite(drink.id) },
                        onBackClick = { vm.currentScreen = "MAIN_TABS" },
                        onAddToCart = { d, size, toppings, qty, note, _ ->
                            vm.addToCart(d, size, toppings, qty, note)
                        }
                    )
                }
            }

            "CHECKOUT" -> {
                CheckoutScreen(
                    cartItems = vm.cartItems,
                    vouchers = vm.voucherList,
                    onBackClick = { vm.currentScreen = "MAIN_TABS" },
                    onOrderConfirmed = { name, phone, address, _, total, appliedVoucherCode ->
                        vm.placeDeliveryOrder(name, phone, address, total, appliedVoucherCode)
                    }
                )
            }

            "BARISTA" -> {
                BaristaScreen(
                    orders = vm.baristaOrders,
                    menuList = vm.menuList,
                    vouchers = vm.voucherList,
                    storeStatus = vm.storeStatus,
                    onUpdateStoreStatus = { status -> vm.updateStoreStatus(status) },
                    onLogout = { vm.logout() },
                    onAddNewDrink = { name, cat, price, desc ->
                        vm.addNewDrink(name, cat, price, desc)
                    },
                    onAddNewVoucher = { code, desc, type, value, minTotal, maxDisc, usage ->
                        vm.addNewVoucher(code, desc, type, value, minTotal, maxDisc, usage)
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCoffeeAppNavigation() {
    FoodAppTheme {
        CoffeeAppNavigation()
    }
}
