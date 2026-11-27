package com.example.drinkapp.model

enum class OrderStatus(val label: String) {
    NEW("Mới"),
    PREPARING("Đang pha"),
    READY("Hoàn tất"),
    CANCELLED("Đã hủy")
}

// Bổ sung Enum Tiến trình giao hàng cho tương lai kết nối API Lalamove/AhaMove
enum class ShippingStatus(val label: String) {
    PENDING("Chưa gọi Shipper"),
    SEARCHING("Đang tìm tài xế..."),
    DELIVERING("Tài xế đang giao"),
    DELIVERED("Giao thành công")
}

data class OrderItemDetail(
    val drinkName: String,
    val quantity: Int,
    val sizeAndToppings: String,
    val note: String = ""
)

data class BaristaOrder(
    val orderId: String,
    // Tách riêng các trường để Backend dễ dàng kết nối API Lalamove/AhaMove
    val customerName: String, // Tên người đặt/người nhận
    val customerPhone: String = "", // SĐT Khách hàng (bắt buộc với API Shipper)
    val deliveryAddress: String = "", // Địa chỉ giao (Dropoff)
    val orderTime: String,
    val status: OrderStatus,
    val items: List<OrderItemDetail>,
    val totalPrice: Int, // Tiền thu hộ COD
    val cancelReason: String? = null,
    
    val shippingStatus: ShippingStatus = ShippingStatus.PENDING,
    
    // Các thông tin tài xế do Webhook Lalamove/AhaMove trả về
    val driverName: String? = null,
    val driverPhone: String? = null,
    val driverPlate: String? = null
)

val sampleBaristaOrders = listOf(
    BaristaOrder(
        orderId = "#CF-104",
        customerName = "Minh Anh",
        customerPhone = "0901234567",
        deliveryAddress = "Bàn 04 (Tại quán)",
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
        customerName = "Hoàng Nam",
        customerPhone = "0912345678",
        deliveryAddress = "Mang đi",
        orderTime = "6 phút trước",
        status = OrderStatus.PREPARING,
        items = listOf(
            OrderItemDetail("Bạc Xỉu Đá", 2, "Size L • Thạch cà phê", "Ít đá")
        ),
        totalPrice = 96000
    ),
    BaristaOrder(
        orderId = "#CF-102",
        customerName = "Thu Hà",
        customerPhone = "0987654321",
        deliveryAddress = "789 Nguyễn Đình Chiểu, Q3",
        orderTime = "14 phút trước",
        status = OrderStatus.READY,
        shippingStatus = ShippingStatus.DELIVERED,
        driverName = "Nguyễn Văn Hùng",
        driverPhone = "0988776655",
        driverPlate = "59-X3 888.99",
        items = listOf(
            OrderItemDetail("Americano Lạnh", 1, "Size M")
        ),
        totalPrice = 39000
    )
)