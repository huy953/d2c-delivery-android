package com.example.drinkapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.R
import com.example.drinkapp.model.BaristaOrder
import com.example.drinkapp.model.OrderStatus
import com.example.drinkapp.model.ShippingStatus

@Composable
fun ProfileScreen(
    orders: List<BaristaOrder>,
    cartItemCount: Int = 0,
    selectedNavIndex: Int = 3,
    onSelectNav: (Int) -> Unit = {},
    onReorder: (BaristaOrder) -> Unit = {},
    onCancelOrder: (order: BaristaOrder, reason: String) -> Unit = { _, _ -> },
    onLogout: () -> Unit = {}
) {
    // Lưu đánh giá 1-5 sao theo từng mã đơn hàng
    val orderRatings = remember { mutableStateMapOf<String, Int>() }

    // State cho Popup Khách hàng tự hủy đơn
    var orderToCancelByCustomer by remember { mutableStateOf<BaristaOrder?>(null) }
    var customerCancelReasonSelected by remember { mutableStateOf("Muốn đổi địa chỉ/SĐT giao hàng") }

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Tài khoản của tôi",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CoffeeDark
                )

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Đăng xuất",
                        tint = CoffeeMuted
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Thông tin người dùng
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = CardSurface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_coffee),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(CreamBackground)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Trần Gia Huy",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoffeeDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CreamBackground
                                ) {
                                    Text(
                                        text = "Thành viên Thân thiết",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CoffeePrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 14.dp),
                            color = CreamBackground
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = CoffeeMuted, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("0901 234 567", fontSize = 14.sp, color = CoffeeDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CoffeeMuted, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("19 Nguyễn Hữu Thọ, Quận 7, TP.HCM", fontSize = 14.sp, color = CoffeeDark)
                        }
                    }
                }
            }

            // Tiêu đề mục Đơn hàng của bạn
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null, tint = CoffeePrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đơn hàng của bạn (${orders.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = CoffeeDark
                    )
                }
            }

            // Danh sách đơn hàng
            items(orders, key = { it.orderId }) { order ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = CardSurface,
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Đơn ${order.orderId} • ${order.orderTime}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoffeeDark
                            )

                            val (statusBg, statusColor) = when (order.status) {
                                OrderStatus.NEW -> Color(0xFFFFF3E0) to Color(0xFFE65100)
                                OrderStatus.PREPARING -> Color(0xFFEFEBE9) to CoffeePrimary
                                OrderStatus.READY -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
                                OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
                            }
                            Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
                                Text(
                                    text = order.status.label,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = order.items.joinToString(", ") { "${it.quantity}x ${it.drinkName}" },
                            fontSize = 13.sp,
                            color = CoffeeMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tổng tiền: ${formatVnd(order.totalPrice)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeePrimary
                        )

                        if (!order.cancelReason.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Lý do hủy: ${order.cancelReason}",
                                fontSize = 12.sp,
                                color = Color(0xFFD32F2F)
                            )
                        }

                        // Hiển thị nút Hủy đơn hàng khi đơn còn ở trạng thái Mới (NEW)
                        if (order.status == OrderStatus.NEW) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        customerCancelReasonSelected = "Muốn đổi địa chỉ/SĐT giao hàng"
                                        orderToCancelByCustomer = order
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                    border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Text("Hủy đơn hàng", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Hiển thị thông tin tài xế giao hàng nếu quán đã gọi Shipper
                        if (order.driverName != null) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = CreamBackground
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CreamBackground)
                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = CoffeePrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Tài xế: ${order.driverName} (Lalamove)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CoffeeDark
                                    )
                                    Text(
                                        text = "SĐT: ${order.driverPhone} • Biển số: ${order.driverPlate}",
                                        fontSize = 11.sp,
                                        color = CoffeeMuted
                                    )
                                    if (order.shippingStatus == ShippingStatus.DELIVERED) {
                                        Text(
                                            text = "Đã giao thành công",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    } else {
                                        Text(
                                            text = "Tài xế đang giao tới bạn...",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE65100)
                                        )
                                    }
                                }
                            }
                        }

                        // Màn hình đơn giao thành công (DELIVERED): Hiện Đánh giá 1-5 sao & Nút Đặt lại đơn cũ
                        if (order.shippingStatus == ShippingStatus.DELIVERED) {
                            Spacer(modifier = Modifier.height(10.dp))

                            val currentRating = orderRatings[order.orderId] ?: 0

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (currentRating > 0) "Đã đánh giá $currentRating sao" else "Đánh giá dịch vụ:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CoffeeDark
                                    )
                                    Row {
                                        (1..5).forEach { star ->
                                            Icon(
                                                imageVector = if (star <= currentRating) Icons.Default.Star else Icons.Default.StarBorder,
                                                contentDescription = "Sao $star",
                                                tint = if (star <= currentRating) Color(0xFFFFB300) else CoffeeMuted,
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clickable { orderRatings[order.orderId] = star }
                                                    .padding(2.dp)
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = { onReorder(order) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Replay,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Đặt lại đơn này", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Popup Xác nhận Hủy đơn hàng dành cho Khách hàng
    if (orderToCancelByCustomer != null) {
        val targetOrder = orderToCancelByCustomer!!
        AlertDialog(
            onDismissRequest = { orderToCancelByCustomer = null },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Xác nhận hủy đơn hàng", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Đơn ${targetOrder.orderId}", fontSize = 13.sp, color = CoffeeMuted)
                    Text("Vui lòng chọn lý do bạn muốn hủy đơn:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)

                    val customerReasons = listOf(
                        "Muốn đổi địa chỉ/SĐT giao hàng",
                        "Muốn đổi/thêm món ăn khác",
                        "Thời gian chờ giao quá lâu",
                        "Lý do khác"
                    )

                    customerReasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { customerCancelReasonSelected = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (customerCancelReasonSelected == reason),
                                onClick = { customerCancelReasonSelected = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = CoffeePrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(reason, fontSize = 13.sp, color = CoffeeDark)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCancelOrder(targetOrder, customerCancelReasonSelected)
                        orderToCancelByCustomer = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Xác nhận hủy", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { orderToCancelByCustomer = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Quay lại", color = CoffeeMuted)
                }
            }
        )
    }
}
