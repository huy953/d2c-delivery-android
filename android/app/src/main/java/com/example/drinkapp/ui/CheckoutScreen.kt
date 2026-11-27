package com.example.drinkapp.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drinkapp.model.CartItem
import com.example.drinkapp.model.DiscountType
import com.example.drinkapp.model.Voucher
import com.example.drinkapp.model.sampleCartItems
import com.example.drinkapp.model.sampleVouchers

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem> = sampleCartItems,
    vouchers: List<Voucher> = sampleVouchers,
    onBackClick: () -> Unit = {},
    onOrderConfirmed: (
        customerName: String,
        phoneNumber: String,
        deliveryAddress: String,
        paymentMethod: String,
        totalAmount: Int,
        appliedVoucherCode: String?
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    var customerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryNote by remember { mutableStateOf("") }

    // State cho mã giảm giá
    var promoCodeInput by remember { mutableStateOf("") }
    var appliedPromoCode by remember { mutableStateOf<String?>(null) }
    var showVoucherListDialog by remember { mutableStateOf(false) }

    // 0: Tiền mặt khi nhận hàng (COD) | 1: Chuyển khoản QR
    var paymentMethod by remember { mutableIntStateOf(0) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorText by remember { mutableStateOf(false) }

    // Tạm tính tiền món ăn
    val subTotal = cartItems.sumOf { it.totalPrice }

    // Báo giá phí ship tự động dựa theo khoảng cách địa chỉ nhập vào (5.000đ/km, tối thiểu 15.000đ)
    val (estimatedDistanceKm, estimatedPartner) = remember(deliveryAddress) {
        if (deliveryAddress.isBlank()) {
            Pair(2.4, "AhaMove")
        } else {
            val dist = (1.5 + (deliveryAddress.trim().length % 12) * 0.3).coerceIn(1.2, 8.5)
            val rounded = Math.round(dist * 10.0) / 10.0
            Pair(rounded, "AhaMove")
        }
    }

    val shippingFee = remember(estimatedDistanceKm) {
        val feePerKm = 5000
        val calculated = (estimatedDistanceKm * feePerKm).toInt()
        calculated.coerceAtLeast(15000)
    }

    // Kiểm tra và tính toán giảm giá từ danh sách Vouchers động
    val matchedVoucher = remember(vouchers, appliedPromoCode) {
        if (appliedPromoCode.isNullOrBlank()) null
        else vouchers.find { it.code.equals(appliedPromoCode, ignoreCase = true) }
    }

    val (discountAmount, promoMessage, isAppliedSuccess) = remember(subTotal, matchedVoucher, appliedPromoCode) {
        if (appliedPromoCode.isNullOrBlank()) {
            Triple(0, "", false)
        } else if (matchedVoucher == null) {
            Triple(0, "Mã $appliedPromoCode không tồn tại trên hệ thống!", false)
        } else if (matchedVoucher.remainingUsage <= 0) {
            Triple(0, "Mã ${matchedVoucher.code} đã hết lượt sử dụng!", false)
        } else if (subTotal < matchedVoucher.minSubTotal) {
            Triple(0, "Mã ${matchedVoucher.code} chỉ áp dụng cho đơn có tiền món từ ${formatVnd(matchedVoucher.minSubTotal)} trở lên!", false)
        } else {
            val calculated = when (matchedVoucher.discountType) {
                DiscountType.PERCENT -> {
                    val raw = (subTotal * (matchedVoucher.discountValue / 100.0)).toInt()
                    if (matchedVoucher.maxDiscount > 0) raw.coerceAtMost(matchedVoucher.maxDiscount) else raw
                }
                DiscountType.FIXED_AMOUNT -> matchedVoucher.discountValue
            }
            Triple(calculated, "Đã áp dụng mã ${matchedVoucher.code} (-${formatVnd(calculated)} tiền món)", true)
        }
    }

    // Công thức: Tổng cộng = (Tạm tính món - Giảm giá món) + Phí giao hàng
    val finalTotal = (subTotal - discountAmount).coerceAtLeast(0) + shippingFee

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CardSurface)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = CoffeeDark
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = "Xác nhận giao hàng",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CoffeeDark
                )
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
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Tổng thanh toán",
                            fontSize = 14.sp,
                            color = CoffeeMuted
                        )
                        Text(
                            text = formatVnd(finalTotal),
                            fontSize = 21.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoffeeDark
                        )
                    }

                    Button(
                        onClick = {
                            if (customerName.isBlank() || phoneNumber.isBlank() || deliveryAddress.isBlank()) {
                                showErrorText = true
                            } else {
                                showErrorText = false
                                val methodText = if (paymentMethod == 0) "Tiền mặt (COD)" else "Chuyển khoản QR"
                                onOrderConfirmed(
                                    customerName.trim(),
                                    phoneNumber.trim(),
                                    deliveryAddress.trim(),
                                    methodText,
                                    finalTotal,
                                    if (isAppliedSuccess) appliedPromoCode else null
                                )
                                showSuccessDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .width(185.dp)
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Đặt giao hàng",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Thông tin địa chỉ giao hàng & Báo giá phí ship tự động
            item {
                CheckoutSectionCard(title = "Thông tin nhận hàng") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CreamBackground)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeliveryDining,
                            contentDescription = null,
                            tint = CoffeePrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Khoảng cách ước tính: $estimatedDistanceKm km — Đối tác $estimatedPartner",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CoffeeDark
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Phí giao hàng: ${formatVnd(shippingFee)} (5.000đ/km, tối thiểu 15.000đ)",
                                fontSize = 12.sp,
                                color = CoffeeMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Tên người nhận *") },
                        placeholder = { Text("Ví dụ: Trần Gia Huy") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CoffeeMuted) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoffeePrimary,
                            focusedLabelColor = CoffeePrimary,
                            cursorColor = CoffeePrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Số điện thoại *") },
                        placeholder = { Text("Ví dụ: 0901234567") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = CoffeeMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoffeePrimary,
                            focusedLabelColor = CoffeePrimary,
                            cursorColor = CoffeePrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deliveryAddress,
                        onValueChange = { deliveryAddress = it },
                        label = { Text("Địa chỉ giao hàng *") },
                        placeholder = { Text("Số nhà, tên đường, phường/quận...") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = CoffeeMuted) },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoffeePrimary,
                            focusedLabelColor = CoffeePrimary,
                            cursorColor = CoffeePrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = deliveryNote,
                        onValueChange = { deliveryNote = it },
                        label = { Text("Ghi chú cho tài xế (Tùy chọn)") },
                        placeholder = { Text("Ví dụ: Gọi điện trước khi tới cổng...") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CoffeePrimary,
                            focusedLabelColor = CoffeePrimary,
                            cursorColor = CoffeePrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (showErrorText) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vui lòng nhập đầy đủ Tên, Số điện thoại và Địa chỉ giao hàng!",
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // 2. Tóm tắt danh sách món đặt
            item {
                CheckoutSectionCard(title = "Tóm tắt đơn hàng (${cartItems.sumOf { it.quantity }} món)") {
                    cartItems.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${item.quantity}x ${item.drink.name}",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CoffeeDark
                                )
                                if (item.optionsSummary.isNotBlank()) {
                                    Text(
                                        text = item.optionsSummary,
                                        fontSize = 12.sp,
                                        color = CoffeeMuted
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = formatVnd(item.totalPrice),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CoffeeDark
                            )
                        }
                        if (index < cartItems.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = CreamBackground
                            )
                        }
                    }
                }
            }

            // 3. Quản lý Mã giảm giá (Voucher)
            item {
                CheckoutSectionCard(title = "Mã giảm giá (Chỉ giảm tiền món)") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = promoCodeInput,
                            onValueChange = { promoCodeInput = it.uppercase() },
                            placeholder = { Text("VD: COFFEE10, GIAM15K", fontSize = 13.sp) },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CoffeePrimary,
                                cursorColor = CoffeePrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = {
                                appliedPromoCode = promoCodeInput.trim().uppercase()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(52.dp)
                        ) {
                            Text("Áp dụng", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nút mở danh sách các Voucher khả dụng
                    TextButton(
                        onClick = { showVoucherListDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ConfirmationNumber,
                            contentDescription = null,
                            tint = CoffeePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Xem ${vouchers.size} mã giảm giá hiện có",
                            color = CoffeePrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (promoMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = promoMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isAppliedSuccess) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                    }
                }
            }

            // 4. Phương thức thanh toán
            item {
                CheckoutSectionCard(title = "Phương thức thanh toán") {
                    PaymentOptionRow(
                        title = "Tiền mặt khi nhận hàng (COD)",
                        subtitle = "Thanh toán trực tiếp cho tài xế khi nhận đồ uống",
                        icon = Icons.Default.Payments,
                        selected = paymentMethod == 0,
                        onClick = { paymentMethod = 0 }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PaymentOptionRow(
                        title = "Chuyển khoản / Quét mã QR",
                        subtitle = "Thanh toán nhanh qua ứng dụng ngân hàng",
                        icon = Icons.Default.QrCode2,
                        selected = paymentMethod == 1,
                        onClick = { paymentMethod = 1 }
                    )
                }
            }

            // 5. Chi tiết thanh toán
            item {
                CheckoutSectionCard(title = "Chi tiết thanh toán") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Tạm tính tiền món", fontSize = 14.sp, color = CoffeeMuted)
                        Text(formatVnd(subTotal), fontSize = 14.sp, color = CoffeeDark)
                    }
                    if (discountAmount > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Giảm giá món ($appliedPromoCode)", fontSize = 14.sp, color = CoffeeMuted)
                            Text(
                                text = "-${formatVnd(discountAmount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Phí giao hàng ($estimatedDistanceKm km)", fontSize = 14.sp, color = CoffeeMuted)
                        Text(formatVnd(shippingFee), fontSize = 14.sp, color = CoffeeDark)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = CreamBackground
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Tổng cộng",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeDark
                        )
                        Text(
                            formatVnd(finalTotal),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CoffeePrimary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }

    // Popup xem danh sách Voucher khả dụng cho khách hàng chọn
    if (showVoucherListDialog) {
        AlertDialog(
            onDismissRequest = { showVoucherListDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Chọn mã giảm giá", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    vouchers.forEach { v ->
                        val isEligible = subTotal >= v.minSubTotal && v.remainingUsage > 0
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isEligible) CreamBackground else Color(0xFFF0F0F0),
                            border = BorderStroke(1.dp, if (isEligible) CoffeePrimary else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = isEligible) {
                                    promoCodeInput = v.code
                                    appliedPromoCode = v.code
                                    showVoucherListDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(v.code, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = if (isEligible) CoffeePrimary else CoffeeMuted)
                                    Text(v.description, fontSize = 12.sp, color = CoffeeMuted)
                                    Text("Còn ${v.remainingUsage} lượt • Hạn: ${v.expiryDate}", fontSize = 11.sp, color = CoffeeMuted)
                                }
                                Button(
                                    onClick = {
                                        promoCodeInput = v.code
                                        appliedPromoCode = v.code
                                        showVoucherListDialog = false
                                    },
                                    enabled = isEligible,
                                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Dùng", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showVoucherListDialog = false }) {
                    Text("Đóng", color = CoffeeMuted, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Hộp thoại xác nhận đặt đơn thành công
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Đặt giao hàng thành công!",
                    fontWeight = FontWeight.Bold,
                    color = CoffeeDark
                )
            },
            text = {
                Text(
                    text = "Quán đã nhận đơn và đang chuẩn bị pha chế. Tài xế AhaMove sẽ giao đến địa chỉ: $deliveryAddress ($estimatedDistanceKm km).",
                    color = CoffeeMuted,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBackClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quay về trang chủ", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun CheckoutSectionCard(
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
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

@Composable
private fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) CoffeePrimary else Color(0xFFE6DDD3)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .background(if (selected) CreamBackground else CardSurface)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CoffeePrimary,
            modifier = Modifier.size(26.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CoffeeDark
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = CoffeeMuted
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = CoffeePrimary)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCheckoutScreen() {
    CheckoutScreen()
}
