package com.example.drinkapp.model

enum class OrderStatus(val label: String) {
    NEW("Mới"),
    PREPARING("Đang pha"),
    READY("Hoàn tất")
}

data class OrderItemDetail(
    val foodName: String,
    val quantity: Int,
    val sizeAndToppings: String,
    val note: String = ""
)

data class BaristaOrder(
    val orderId: String,
    val customerName: String,
    val orderTime: String,
    val status: OrderStatus,
    val items: List<OrderItemDetail>,
    val totalPrice: Int
)

val sampleBaristaOrders = listOf(
    BaristaOrder(
        orderId = "#CF-104",
        customerName = "Bàn 04 • Minh Anh",
        orderTime = "2 phút trước",
        status = OrderStatus.NEW,
        items = listOf(
            OrderItemDetail("Cappuccino Nóng", 1, "Size M • Thêm 1 Shot Espresso", "Ít bọt sữa"),
            OrderItemDetail("Bánh Sừng Bò (Croissant)", 1, "1 Bánh (Hâm nóng)")
        ),
        totalPrice = 93000
    ),
    BaristaOrder(
        orderId = "#CF-103",
        customerName = "Mang đi • Hoàng Nam",
        orderTime = "6 phút trước",
        status = OrderStatus.PREPARING,
        items = listOf(
            OrderItemDetail("Bạc Xỉu Đá", 2, "Size L • Thạch cà phê", "Ít đá")
        ),
        totalPrice = 96000
    ),
    BaristaOrder(
        orderId = "#CF-102",
        customerName = "Bàn 02 • Thu Hà",
        orderTime = "14 phút trước",
        status = OrderStatus.READY,
        items = listOf(
            OrderItemDetail("Americano Lạnh", 1, "Size M")
        ),
        totalPrice = 39000
    )
)