package com.example.drinkapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.drinkapp.model.*

class CoffeeViewModel : ViewModel() {

    // 1. Danh sách thực đơn
    val menuList = mutableStateListOf<DrinkItem>().apply {
        addAll(sampleMenuData)
    }

    // 2. Giỏ hàng & Danh sách ID món yêu thích
    val cartItems = mutableStateListOf<CartItem>()
    val favoriteDrinkIds = mutableStateListOf<String>()

    // 3. Lịch sử đơn hàng đã đặt (đồng bộ với Barista)
    val baristaOrders = mutableStateListOf<BaristaOrder>().apply {
        addAll(sampleBaristaOrders)
    }

    // 4. Danh sách mã khuyến mãi (Vouchers)
    val voucherList = mutableStateListOf<Voucher>().apply {
        addAll(sampleVouchers)
    }

    // 5. Quản lý màn hình & Vai trò đăng nhập
    var currentScreen by mutableStateOf("LOGIN") // "LOGIN", "MAIN_TABS", "DETAIL", "CART", "CHECKOUT", "BARISTA", "SEARCH"
    var userRole by mutableStateOf("") // "USER" hoặc "BARISTA"
    var selectedNavTab by mutableIntStateOf(0)
    var selectedDrink by mutableStateOf<DrinkItem?>(null)

    // 6. Quản lý trạng thái hoạt động của Quán
    var storeStatus by mutableStateOf("Đang mở cửa") // "Đang mở cửa", "Quán bận 15 phút", "Quán bận 30 phút", "Quán bận 1 tiếng", "Đã đóng cửa"

    fun updateStoreStatus(status: String) {
        storeStatus = status
    }

    fun login(role: String) {
        userRole = role
        if (role == "BARISTA") {
            currentScreen = "BARISTA"
        } else {
            selectedNavTab = 0
            currentScreen = "MAIN_TABS"
        }
    }

    fun logout() {
        userRole = ""
        currentScreen = "LOGIN"
    }

    fun openDrinkDetail(drink: DrinkItem) {
        selectedDrink = drink
        currentScreen = "DETAIL"
    }

    fun toggleFavorite(drinkId: String) {
        if (drinkId in favoriteDrinkIds) {
            favoriteDrinkIds.remove(drinkId)
        } else {
            favoriteDrinkIds.add(drinkId)
        }
    }

    fun addToCart(
        drink: DrinkItem,
        selectedSize: DrinkOption?,
        selectedToppings: List<DrinkOption>,
        quantity: Int,
        note: String
    ) {
        cartItems.add(
            CartItem(
                id = "cart_${System.currentTimeMillis()}",
                drink = drink,
                selectedSize = selectedSize,
                selectedToppings = selectedToppings,
                note = note,
                quantity = quantity
            )
        )
        currentScreen = "MAIN_TABS"
    }

    fun placeDeliveryOrder(
        customerName: String,
        phoneNumber: String,
        deliveryAddress: String,
        totalAmount: Int,
        appliedVoucherCode: String? = null
    ) {
        val orderDetails = cartItems.map { item ->
            OrderItemDetail(
                drinkName = item.drink.name,
                quantity = item.quantity,
                sizeAndToppings = item.optionsSummary.ifBlank { "Tiêu chuẩn" },
                note = item.note
            )
        }

        val newOrder = BaristaOrder(
            orderId = "#CF-${105 + baristaOrders.size}",
            customerName = customerName,
            customerPhone = phoneNumber,
            deliveryAddress = deliveryAddress,
            orderTime = "Vừa xong",
            status = OrderStatus.NEW,
            items = orderDetails,
            totalPrice = totalAmount
        )

        baristaOrders.add(0, newOrder)
        cartItems.clear()

        // Trừ 1 lượt sử dụng voucher nếu có
        appliedVoucherCode?.let { consumeVoucher(it) }
    }
    
    // Giả lập API gọi Shipper sang Backend / Lalamove
    fun callShipper(orderId: String) {
        val idx = baristaOrders.indexOfFirst { it.orderId == orderId }
        if (idx != -1) {
            val currentOrder = baristaOrders[idx]
            baristaOrders[idx] = currentOrder.copy(
                shippingStatus = ShippingStatus.SEARCHING
            )
            
            // Giả lập webhook API trả kết quả tìm thấy tài xế sau một khoảng thời gian (ở đây ta cập nhật luôn)
            baristaOrders[idx] = baristaOrders[idx].copy(
                shippingStatus = ShippingStatus.DELIVERING,
                driverName = "Tài xế AhaMove",
                driverPhone = "0900.xxx.xxx",
                driverPlate = "59-X3 888.88"
            )
        }
    }
    
    fun markOrderAsDelivered(orderId: String) {
        val idx = baristaOrders.indexOfFirst { it.orderId == orderId }
        if (idx != -1) {
            baristaOrders[idx] = baristaOrders[idx].copy(
                shippingStatus = ShippingStatus.DELIVERED
            )
        }
    }

    // Trừ lượt dùng voucher khi đặt đơn thành công
    fun consumeVoucher(code: String) {
        val idx = voucherList.indexOfFirst { it.code.equals(code, ignoreCase = true) }
        if (idx != -1) {
            val current = voucherList[idx]
            if (current.remainingUsage > 0) {
                voucherList[idx] = current.copy(remainingUsage = current.remainingUsage - 1)
            }
        }
    }

    // Đặt lại đơn hàng cũ chỉ với 1 bấm
    fun reorder(order: BaristaOrder) {
        order.items.forEach { item ->
            val matchedDrink = menuList.find { it.name == item.drinkName } ?: sampleMenuData.first()
            val cartItem = CartItem(
                id = "cart_${System.currentTimeMillis()}_${(100..999).random()}",
                drink = matchedDrink,
                selectedSize = matchedDrink.sizes.firstOrNull(),
                selectedToppings = emptyList(),
                note = item.note,
                quantity = item.quantity
            )
            cartItems.add(cartItem)
        }
        selectedNavTab = 2 // Chuyển sang Tab Giỏ hàng
        currentScreen = "MAIN_TABS"
    }

    // Khách hàng tự hủy đơn khi đơn vẫn ở trạng thái Mới (NEW)
    fun cancelCustomerOrder(orderId: String, reason: String) {
        val idx = baristaOrders.indexOfFirst { it.orderId == orderId }
        if (idx != -1) {
            baristaOrders[idx] = baristaOrders[idx].copy(
                status = OrderStatus.CANCELLED,
                cancelReason = "Khách hủy: $reason"
            )
        }
    }

    // Thêm sản phẩm mới vào kho món ăn/đồ uống
    fun addNewDrink(
        name: String,
        category: String,
        price: Int,
        description: String
    ) {
        val newId = "${menuList.size + 10}"
        val newDrink = DrinkItem(
            id = newId,
            category = if (category.isNotBlank()) category else "Cà phê",
            name = name.trim(),
            description = description.ifBlank { "Thức uống thơm ngon được chế biến từ nguyên liệu tươi mới." },
            basePrice = price,
            sizes = listOf(
                FoodOption("s1", "Size M (Vừa)", 0),
                FoodOption("s2", "Size L (Lớn)", 6000)
            ),
            toppings = listOf(
                FoodOption("t1", "Thêm 1 Shot Espresso", 10000)
            )
        )
        menuList.add(newDrink)
    }

    // Thêm mã khuyến mãi (Voucher) mới
    fun addNewVoucher(
        code: String,
        description: String,
        discountType: DiscountType,
        discountValue: Int,
        minSubTotal: Int,
        maxDiscount: Int,
        maxUsage: Int
    ) {
        val newVoucher = Voucher(
            code = code.trim().uppercase(),
            description = description.ifBlank { "Mã giảm giá do quán phát hành" },
            discountType = discountType,
            discountValue = discountValue,
            minSubTotal = minSubTotal,
            maxDiscount = maxDiscount,
            remainingUsage = maxUsage,
            expiryDate = "31/12/2026"
        )
        voucherList.add(0, newVoucher)
    }
}