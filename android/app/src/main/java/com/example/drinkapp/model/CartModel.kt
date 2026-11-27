package com.example.drinkapp.model

data class CartItem(
    val id: String,
    val drink: DrinkItem,
    val selectedSize: DrinkOption?,
    val selectedToppings: List<DrinkOption> = emptyList(),
    val note: String = "",
    val quantity: Int = 1
) {
    // Đơn giá của 1 ly (bao gồm giá gốc + size + các topping)
    val unitPrice: Int
        get() = drink.basePrice +
                (selectedSize?.extraPrice ?: 0) +
                selectedToppings.sumOf { it.extraPrice }

    // Thành tiền của dòng này
    val totalPrice: Int
        get() = unitPrice * quantity

    // Chuỗi tóm tắt lựa chọn hiển thị ngay dưới tên món
    val optionsSummary: String
        get() = buildList {
            selectedSize?.let { add(it.name) }
            if (selectedToppings.isNotEmpty()) {
                add(selectedToppings.joinToString(", ") { it.name })
            }
            if (note.isNotBlank()) {
                add("Ghi chú: $note")
            }
        }.joinToString(" • ")
}

val sampleCartItems = listOf(
    CartItem(
        id = "c1",
        drink = sampleMenuData[0], // Cappuccino Nóng
        selectedSize = sampleMenuData[0].sizes[1], // Size M
        selectedToppings = listOf(sampleMenuData[0].toppings[0]), // Thêm 1 Shot Espresso
        note = "Ít bọt sữa",
        quantity = 1
    ),
    CartItem(
        id = "c2",
        drink = sampleMenuData[6], // Tiramisu Cà Phê
        selectedSize = sampleMenuData[6].sizes[0],
        quantity = 1
    ),
    CartItem(
        id = "c3",
        drink = sampleMenuData[5], // Bánh Sừng Bò (Croissant)
        selectedSize = sampleMenuData[5].sizes[0],
        selectedToppings = listOf(sampleMenuData[5].toppings[0]),
        quantity = 2
    )
)