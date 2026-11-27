package com.example.drinkapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
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
import com.example.drinkapp.model.*

// Màu trạng thái bổ sung cho quầy pha chế
private val StatusPendingBg = Color(0xFFFFF3E0)
private val StatusPendingText = Color(0xFFE65100)
private val StatusDoneBg = Color(0xFFE8F5E9)
private val StatusDoneText = Color(0xFF2E7D32)

@Composable
fun BaristaScreen(
    orders: MutableList<BaristaOrder> = remember { mutableStateListOf<BaristaOrder>().apply { addAll(sampleBaristaOrders) } },
    menuList: List<FoodItem> = sampleMenuData,
    vouchers: List<Voucher> = sampleVouchers,
    storeStatus: String = "Đang mở cửa",
    onUpdateStoreStatus: (String) -> Unit = {},
    onLogout: () -> Unit = {},
    onAddNewDrink: (name: String, category: String, price: Int, description: String) -> Unit = { _, _, _, _ -> },
    onAddNewVoucher: (code: String, description: String, discountType: DiscountType, discountValue: Int, minSubTotal: Int, maxDiscount: Int, maxUsage: Int) -> Unit = { _, _, _, _, _, _, _ -> },
    onCallShipper: (String) -> Unit = {},
    onMarkAsDelivered: (String) -> Unit = {}
) {
    var selectedMainTab by remember { mutableIntStateOf(0) } // 0: Đơn hàng, 1: Kho món, 2: Khuyến mãi
    var selectedStatusFilter by remember { mutableStateOf("Tất cả") }

    // State cho Hộp thoại Chọn Trạng thái Quán (Đang mở cửa, Quán bận, Đóng cửa)
    var showStoreStatusDialog by remember { mutableStateOf(false) }
    var tempSelectedStatus by remember { mutableStateOf(storeStatus) }

    // State cho Popup xác nhận từ chối đơn hàng chống bấm nhầm
    var orderToCancel by remember { mutableStateOf<BaristaOrder?>(null) }
    var cancelReasonSelected by remember { mutableStateOf("Hết nguyên liệu") }

    // State cho Dialog Thêm sản phẩm mới vào kho
    var showAddProductDialog by remember { mutableStateOf(false) }
    var newProductName by remember { mutableStateOf("") }
    var newProductCategory by remember { mutableStateOf("Cà phê") }
    var newProductPrice by remember { mutableStateOf("") }
    var newProductDescription by remember { mutableStateOf("") }
    var dialogError by remember { mutableStateOf("") }

    // State cho Dialog Tạo Voucher mới
    var showAddVoucherDialog by remember { mutableStateOf(false) }
    var voucherCodeInput by remember { mutableStateOf("") }
    var voucherDescInput by remember { mutableStateOf("") }
    var voucherTypeSelected by remember { mutableStateOf(DiscountType.PERCENT) }
    var voucherValueInput by remember { mutableStateOf("") }
    var voucherMinTotalInput by remember { mutableStateOf("") }
    var voucherMaxDiscountInput by remember { mutableStateOf("") }
    var voucherUsageInput by remember { mutableStateOf("50") }
    var voucherError by remember { mutableStateOf("") }

    val stockMap = remember {
        mutableStateMapOf<String, Boolean>().apply {
            menuList.forEach { put(it.id, true) }
        }
    }

    val pendingCount = orders.count { it.status != OrderStatus.READY && it.status != OrderStatus.CANCELLED }

    Scaffold(
        containerColor = CreamBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cụm bên trái: Icon + Tiêu đề + Số đơn chờ (Thêm weight 1f để không chiếm dụng lề phải)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CoffeePrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Coffee,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Quầy Barista",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CoffeeDark
                        )
                        Text(
                            text = "$pendingCount đơn chờ xử lý",
                            fontSize = 12.sp,
                            color = CoffeeMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Cụm bên phải: Thẻ Trạng thái Quán + Nút Đăng xuất
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (statusBg, statusTextColor) = when {
                        storeStatus == "Đang mở cửa" -> StatusDoneBg to StatusDoneText
                        storeStatus == "Đã đóng cửa" -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
                        else -> StatusPendingBg to StatusPendingText
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusBg,
                        modifier = Modifier.clickable {
                            tempSelectedStatus = storeStatus
                            showStoreStatusDialog = true
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusTextColor)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = storeStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(CardSurface)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Đăng xuất",
                            tint = CoffeeMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Thanh chuyển đổi 3 tab: Đơn pha chế | Kho món | Khuyến mãi
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE6DC))
                    .padding(4.dp)
            ) {
                BaristaModeTab(
                    title = "Đơn pha chế ($pendingCount)",
                    icon = Icons.AutoMirrored.Filled.ReceiptLong,
                    selected = selectedMainTab == 0,
                    onClick = { selectedMainTab = 0 },
                    modifier = Modifier.weight(1f)
                )
                BaristaModeTab(
                    title = "Kho món (${menuList.size})",
                    icon = Icons.Default.Inventory2,
                    selected = selectedMainTab == 1,
                    onClick = { selectedMainTab = 1 },
                    modifier = Modifier.weight(1f)
                )
                BaristaModeTab(
                    title = "Vouchers (${vouchers.size})",
                    icon = Icons.Default.ConfirmationNumber,
                    selected = selectedMainTab == 2,
                    onClick = { selectedMainTab = 2 },
                    modifier = Modifier.weight(1f)
                )
            }

            when (selectedMainTab) {
                0 -> {
                    // TAB 0: QUẢN LÝ ĐƠN HÀNG
                    val statusFilters = listOf("Tất cả", "Mới", "Đang pha", "Hoàn tất", "Đã hủy")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(statusFilters) { filter ->
                            val isSelected = selectedStatusFilter == filter
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { selectedStatusFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 15.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) CoffeeDark else CoffeeMuted
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(26.dp)
                                        .height(3.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CoffeePrimary else Color.Transparent)
                                )
                            }
                        }
                    }

                    val filteredOrders = when (selectedStatusFilter) {
                        "Mới" -> orders.filter { it.status == OrderStatus.NEW }
                        "Đang pha" -> orders.filter { it.status == OrderStatus.PREPARING }
                        "Hoàn tất" -> orders.filter { it.status == OrderStatus.READY }
                        "Đã hủy" -> orders.filter { it.status == OrderStatus.CANCELLED }
                        else -> orders
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredOrders, key = { it.orderId }) { order ->
                            BaristaOrderCard(
                                order = order,
                                onAdvanceStatus = {
                                    val idx = orders.indexOfFirst { it.orderId == order.orderId }
                                    if (idx != -1) {
                                        val nextStatus = when (order.status) {
                                            OrderStatus.NEW -> OrderStatus.PREPARING
                                            OrderStatus.PREPARING -> OrderStatus.READY
                                            else -> order.status
                                        }
                                        orders[idx] = order.copy(status = nextStatus)
                                    }
                                },
                                onCancelOrder = {
                                    orderToCancel = order
                                },
                                onRestoreOrder = {
                                    val idx = orders.indexOfFirst { it.orderId == order.orderId }
                                    if (idx != -1) {
                                        orders[idx] = order.copy(status = OrderStatus.NEW, cancelReason = null)
                                    }
                                },
                                onCallShipper = { onCallShipper(order.orderId) },
                                onMarkAsDelivered = { onMarkAsDelivered(order.orderId) }
                            )
                        }
                    }
                }

                1 -> {
                    // TAB 1: KHO MÓN + NÚT THÊM MÓN MỚI
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Button(
                                onClick = {
                                    newProductName = ""
                                    newProductCategory = "Cà phê"
                                    newProductPrice = ""
                                    newProductDescription = ""
                                    dialogError = ""
                                    showAddProductDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Thêm sản phẩm mới vào kho", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        items(menuList, key = { it.id }) { food ->
                            val inStock = stockMap[food.id] ?: true
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CardSurface,
                                shadowElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = food.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (inStock) CoffeeDark else CoffeeMuted
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${formatVnd(food.basePrice)} • ${if (inStock) "Còn hàng" else "Tạm hết hàng"}",
                                            fontSize = 13.sp,
                                            color = if (inStock) StatusDoneText else StatusPendingText
                                        )
                                    }
                                    Switch(
                                        checked = inStock,
                                        onCheckedChange = { stockMap[food.id] = it },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = CoffeePrimary
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: QUẢN LÝ KHUYẾN MÃI (VOUCHERS)
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Button(
                                onClick = {
                                    voucherCodeInput = ""
                                    voucherDescInput = ""
                                    voucherTypeSelected = DiscountType.PERCENT
                                    voucherValueInput = ""
                                    voucherMinTotalInput = "80000"
                                    voucherMaxDiscountInput = "20000"
                                    voucherUsageInput = "50"
                                    voucherError = ""
                                    showAddVoucherDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tạo mã khuyến mãi mới", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }

                        items(vouchers, key = { it.code }) { v ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
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
                                            text = v.code,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 17.sp,
                                            color = CoffeePrimary
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (v.remainingUsage > 0) StatusDoneBg else Color(0xFFFFEBEE)
                                        ) {
                                            Text(
                                                text = if (v.remainingUsage > 0) "Còn ${v.remainingUsage} lượt" else "Đã hết lượt",
                                                color = if (v.remainingUsage > 0) StatusDoneText else Color(0xFFD32F2F),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(v.description, fontSize = 13.sp, color = CoffeeDark)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Điều kiện: Đơn tối thiểu ${formatVnd(v.minSubTotal)} • Hạn: ${v.expiryDate}",
                                        fontSize = 12.sp,
                                        color = CoffeeMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Popup Cài đặt Trạng thái Quán (Đang mở cửa, Quán bận 15p/30p/1h, Đóng cửa)
    if (showStoreStatusDialog) {
        AlertDialog(
            onDismissRequest = { showStoreStatusDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Cài đặt trạng thái quán", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Chọn trạng thái hoạt động hiện tại:", fontSize = 13.sp, color = CoffeeMuted)

                    val statusOptions = listOf(
                        "Đang mở cửa",
                        "Quán bận 15 phút",
                        "Quán bận 30 phút",
                        "Quán bận 1 tiếng",
                        "Đã đóng cửa"
                    )

                    statusOptions.forEach { statusOption ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { tempSelectedStatus = statusOption }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (tempSelectedStatus == statusOption),
                                onClick = { tempSelectedStatus = statusOption },
                                colors = RadioButtonDefaults.colors(selectedColor = CoffeePrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(statusOption, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = CoffeeDark)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateStoreStatus(tempSelectedStatus)
                        showStoreStatusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Lưu trạng thái", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showStoreStatusDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hủy", color = CoffeeMuted)
                }
            }
        )
    }

    // Popup Xác nhận Từ chối Đơn hàng (Chống lỡ tay bấm nhầm)
    if (orderToCancel != null) {
        val targetOrder = orderToCancel!!
        AlertDialog(
            onDismissRequest = { orderToCancel = null },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Xác nhận từ chối đơn hàng", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Đơn ${targetOrder.orderId} • ${targetOrder.customerName}", fontSize = 13.sp, color = CoffeeMuted)
                    Text("Lý do từ chối đơn hàng:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)

                    val reasons = listOf("Hết nguyên liệu", "Quán quá tải", "Khách yêu cầu hủy")
                    reasons.forEach { reason ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { cancelReasonSelected = reason }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = (cancelReasonSelected == reason),
                                onClick = { cancelReasonSelected = reason },
                                colors = RadioButtonDefaults.colors(selectedColor = CoffeePrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(reason, fontSize = 14.sp, color = CoffeeDark)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val idx = orders.indexOfFirst { it.orderId == targetOrder.orderId }
                        if (idx != -1) {
                            orders[idx] = targetOrder.copy(
                                status = OrderStatus.CANCELLED,
                                cancelReason = cancelReasonSelected
                            )
                        }
                        orderToCancel = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Xác nhận từ chối", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { orderToCancel = null },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hủy bỏ", color = CoffeeMuted)
                }
            }
        )
    }

    // Dialog Nhập thông tin Thêm sản phẩm mới
    if (showAddProductDialog) {
        AlertDialog(
            onDismissRequest = { showAddProductDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Thêm sản phẩm mới vào kho", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newProductName,
                        onValueChange = { newProductName = it },
                        label = { Text("Tên sản phẩm *") },
                        placeholder = { Text("VD: Trà Sữa Matcha") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Danh mục:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Cà phê", "Bánh ngọt").forEach { cat ->
                            val isSelected = newProductCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { newProductCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CoffeePrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = newProductPrice,
                        onValueChange = { newProductPrice = it },
                        label = { Text("Giá bán (đ) *") },
                        placeholder = { Text("VD: 39000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newProductDescription,
                        onValueChange = { newProductDescription = it },
                        label = { Text("Mô tả sản phẩm") },
                        placeholder = { Text("VD: Trà xanh matcha thơm béo...") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    if (dialogError.isNotEmpty()) {
                        Text(dialogError, color = Color(0xFFD32F2F), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val priceInt = newProductPrice.toIntOrNull()
                        if (newProductName.isBlank()) {
                            dialogError = "Vui lòng nhập Tên sản phẩm!"
                        } else if (priceInt == null || priceInt <= 0) {
                            dialogError = "Vui lòng nhập Giá bán hợp lệ!"
                        } else {
                            onAddNewDrink(newProductName.trim(), newProductCategory, priceInt, newProductDescription.trim())
                            showAddProductDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Thêm vào kho", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddProductDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hủy", color = CoffeeMuted)
                }
            }
        )
    }

    // Dialog Tạo Mã khuyến mãi (Voucher) mới
    if (showAddVoucherDialog) {
        AlertDialog(
            onDismissRequest = { showAddVoucherDialog = false },
            containerColor = CardSurface,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Tạo mã khuyến mãi mới", fontWeight = FontWeight.Bold, color = CoffeeDark, fontSize = 18.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = voucherCodeInput,
                        onValueChange = { voucherCodeInput = it.uppercase() },
                        label = { Text("Mã Voucher *") },
                        placeholder = { Text("VD: KM20K, CAFE10") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Loại giảm giá:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CoffeeDark)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = (voucherTypeSelected == DiscountType.PERCENT),
                            onClick = { voucherTypeSelected = DiscountType.PERCENT },
                            label = { Text("Giảm %", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CoffeePrimary, selectedLabelColor = Color.White)
                        )
                        FilterChip(
                            selected = (voucherTypeSelected == DiscountType.FIXED_AMOUNT),
                            onClick = { voucherTypeSelected = DiscountType.FIXED_AMOUNT },
                            label = { Text("Giảm tiền cố định", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = CoffeePrimary, selectedLabelColor = Color.White)
                        )
                    }

                    OutlinedTextField(
                        value = voucherValueInput,
                        onValueChange = { voucherValueInput = it },
                        label = { Text(if (voucherTypeSelected == DiscountType.PERCENT) "Số % giảm (VD: 10) *" else "Số tiền giảm (VD: 20000) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = voucherMinTotalInput,
                        onValueChange = { voucherMinTotalInput = it },
                        label = { Text("Đơn tối thiểu (đ)") },
                        placeholder = { Text("VD: 100000") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (voucherTypeSelected == DiscountType.PERCENT) {
                        OutlinedTextField(
                            value = voucherMaxDiscountInput,
                            onValueChange = { voucherMaxDiscountInput = it },
                            label = { Text("Giảm tối đa (đ)") },
                            placeholder = { Text("VD: 20000") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(
                        value = voucherUsageInput,
                        onValueChange = { voucherUsageInput = it },
                        label = { Text("Số lượt sử dụng") },
                        placeholder = { Text("VD: 50") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = voucherDescInput,
                        onValueChange = { voucherDescInput = it },
                        label = { Text("Mô tả hiển thị") },
                        placeholder = { Text("VD: Giảm 10% tối đa 20k cho đơn từ 80k") },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CoffeePrimary, cursorColor = CoffeePrimary),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    if (voucherError.isNotEmpty()) {
                        Text(voucherError, color = Color(0xFFD32F2F), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val valueInt = voucherValueInput.toIntOrNull()
                        val minTotalInt = voucherMinTotalInput.toIntOrNull() ?: 0
                        val maxDiscountInt = voucherMaxDiscountInput.toIntOrNull() ?: 0
                        val usageInt = voucherUsageInput.toIntOrNull() ?: 50

                        if (voucherCodeInput.isBlank()) {
                            voucherError = "Vui lòng nhập Mã Voucher!"
                        } else if (valueInt == null || valueInt <= 0) {
                            voucherError = "Vui lòng nhập Mức giảm hợp lệ!"
                        } else {
                            onAddNewVoucher(
                                voucherCodeInput.trim(),
                                voucherDescInput.trim(),
                                voucherTypeSelected,
                                valueInt,
                                minTotalInt,
                                maxDiscountInt,
                                usageInt
                            )
                            showAddVoucherDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Tạo Voucher", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAddVoucherDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Hủy", color = CoffeeMuted)
                }
            }
        )
    }
}

@Composable
private fun BaristaModeTab(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (selected) CoffeePrimary else Color.Transparent,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else CoffeeMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else CoffeeMuted
            )
        }
    }
}

@Composable
fun BaristaOrderCard(
    order: BaristaOrder,
    onAdvanceStatus: () -> Unit,
    onCancelOrder: () -> Unit,
    onRestoreOrder: () -> Unit = {},
    onCallShipper: () -> Unit = {},
    onMarkAsDelivered: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
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
                Column {
                    Text(
                        text = "${order.orderId} • ${order.customerName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CoffeeDark
                    )
                    Text(
                        text = "${order.orderTime} • ${formatVnd(order.totalPrice)}",
                        fontSize = 13.sp,
                        color = CoffeeMuted
                    )
                }

                val (badgeBg, badgeText) = when (order.status) {
                    OrderStatus.NEW -> StatusPendingBg to StatusPendingText
                    OrderStatus.PREPARING -> Color(0xFFEFEBE9) to CoffeePrimary
                    OrderStatus.READY -> StatusDoneBg to StatusDoneText
                    OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFD32F2F)
                }
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = order.status.label,
                        color = badgeText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = CreamBackground
            )

            order.items.forEach { item ->
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "${item.quantity}x ${item.drinkName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CoffeeDark
                    )
                    Text(
                        text = item.sizeAndToppings,
                        fontSize = 13.sp,
                        color = CoffeeMuted
                    )
                    if (item.note.isNotBlank()) {
                        Text(
                            text = "Ghi chú: ${item.note}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = StatusPendingText
                        )
                    }
                }
            }

            if (!order.cancelReason.isNullOrBlank()) {
                Text(
                    text = "Lý do từ chối: ${order.cancelReason}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFFD32F2F)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (order.status) {
                OrderStatus.NEW -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelOrder,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text("Từ chối", color = StatusPendingText, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = onAdvanceStatus,
                            colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(
                                text = "Nhận đơn & Pha chế",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                OrderStatus.PREPARING -> {
                    Button(
                        onClick = onAdvanceStatus,
                        colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text(
                            text = "Xác nhận đã pha xong",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                OrderStatus.READY -> {
                    // Luồng Gọi Shipper Lalamove/AhaMove
                    when (order.shippingStatus) {
                        ShippingStatus.PENDING -> {
                            Button(
                                onClick = onCallShipper,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100)),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeliveryDining,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Gọi Shipper Lalamove / AhaMove",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        ShippingStatus.SEARCHING -> {
                            OutlinedButton(
                                onClick = { },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                            ) {
                                Text(
                                    text = "Đang tìm tài xế qua API...",
                                    color = CoffeeMuted,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        ShippingStatus.DELIVERING -> {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CreamBackground)
                                        .padding(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = CoffeePrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Tài xế: ${order.driverName} (${order.driverPhone})",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CoffeeDark
                                        )
                                        Text(
                                            text = "Biển số: ${order.driverPlate}",
                                            fontSize = 12.sp,
                                            color = CoffeeMuted
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onMarkAsDelivered,
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusDoneText),
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Giả lập: Đã giao thành công",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        ShippingStatus.DELIVERED -> {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = StatusDoneText,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Đơn đã giao tới khách thành công",
                                    color = StatusDoneText,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
                OrderStatus.CANCELLED -> {
                    Button(
                        onClick = onRestoreOrder,
                        colors = ButtonDefaults.buttonColors(containerColor = CoffeePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Khôi phục đơn (Đưa về trạng thái Mới)",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBaristaScreen() {
    BaristaScreen()
}
