package com.example.drinkapp.model

enum class DiscountType(val label: String) {
    PERCENT("Giảm theo %"),
    FIXED_AMOUNT("Giảm số tiền cố định")
}

data class Voucher(
    val code: String,                // Mã voucher (VD: "COFFEE10", "GIAM15K")
    val description: String,         // Mô tả (VD: "Giảm 10% tối đa 20k cho đơn từ 80k")
    val discountType: DiscountType,  // PERCENT hoặc FIXED_AMOUNT
    val discountValue: Int,          // 10 (%) hoặc 15000 (đ)
    val minSubTotal: Int = 0,        // Đơn tối thiểu (đ)
    val maxDiscount: Int = 0,        // Giảm tối đa cho PERCENT (đ)
    val remainingUsage: Int = 50,    // Số lượt sử dụng còn lại
    val expiryDate: String = "31/12/2026" // Hạn sử dụng
)

val sampleVouchers = listOf(
    Voucher(
        code = "COFFEE10",
        description = "Giảm 10% tối đa 20.000đ tiền món cho đơn từ 80.000đ",
        discountType = DiscountType.PERCENT,
        discountValue = 10,
        minSubTotal = 80000,
        maxDiscount = 20000,
        remainingUsage = 50,
        expiryDate = "31/12/2026"
    ),
    Voucher(
        code = "GIAM15K",
        description = "Giảm 15.000đ tiền món cho đơn từ 100.000đ",
        discountType = DiscountType.FIXED_AMOUNT,
        discountValue = 15000,
        minSubTotal = 100000,
        remainingUsage = 30,
        expiryDate = "31/12/2026"
    ),
    Voucher(
        code = "KHAO20K",
        description = "Giảm 20.000đ tiền món cho đơn từ 120.000đ",
        discountType = DiscountType.FIXED_AMOUNT,
        discountValue = 20000,
        minSubTotal = 120000,
        remainingUsage = 20,
        expiryDate = "31/12/2026"
    )
)
