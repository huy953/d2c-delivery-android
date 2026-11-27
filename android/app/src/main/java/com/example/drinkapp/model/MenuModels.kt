package com.example.drinkapp.model

data class FoodOption(
    val id: String,
    val name: String,
    val extraPrice: Int
)
typealias DrinkOption = FoodOption

data class FoodItem(
    val id: String,
    val category: String, // "Cà phê", "Bánh ngọt"
    val name: String,
    val description: String,
    val basePrice: Int,
    val sizes: List<FoodOption> = emptyList(),
    val toppings: List<FoodOption> = emptyList()
)
typealias DrinkItem = FoodItem

val sampleMenuData = listOf(
    FoodItem(
        id = "1",
        category = "Cà phê",
        name = "Cappuccino Nóng",
        description = "Cà phê Espresso đậm vị pha cùng sữa tươi đánh bọt dày mịn.",
        basePrice = 45000,
        sizes = listOf(
            FoodOption("s1", "Size S (Nhỏ)", 0),
            FoodOption("s2", "Size M (Vừa)", 6000),
            FoodOption("s3", "Size L (Lớn)", 12000)
        ),
        toppings = listOf(
            FoodOption("t1", "Thêm 1 Shot Espresso", 10000),
            FoodOption("t2", "Sốt Caramel", 8000),
            FoodOption("t3", "Kem sữa béo (Macchiato)", 10000)
        )
    ),
    FoodItem(
        id = "2",
        category = "Cà phê",
        name = "Bạc Xỉu Đá",
        description = "Cà phê phin nguyên chất pha với nhiều sữa tươi và sữa đặc béo ngọt.",
        basePrice = 35000,
        sizes = listOf(
            FoodOption("s1", "Size M (Vừa)", 0),
            FoodOption("s2", "Size L (Lớn)", 6000)
        ),
        toppings = listOf(
            FoodOption("t1", "Thêm 1 Shot Espresso", 10000),
            FoodOption("t4", "Thạch cà phê", 7000)
        )
    ),
    FoodItem(
        id = "3",
        category = "Cà phê",
        name = "Americano Lạnh",
        description = "Cà phê Espresso pha loãng với nhiều nước, vị thanh nhẹ ít chua.",
        basePrice = 39000,
        sizes = listOf(
            FoodOption("s1", "Size M (Vừa)", 0),
            FoodOption("s2", "Size L (Lớn)", 6000)
        ),
        toppings = listOf(
            FoodOption("t1", "Thêm 1 Shot Espresso", 10000)
        )
    ),
    FoodItem(
        id = "4",
        category = "Cà phê",
        name = "Hot Latte",
        description = "Cà phê pha với nhiều sữa tươi nóng, vị béo dịu và lớp bọt mỏng.",
        basePrice = 45000,
        sizes = listOf(
            FoodOption("s1", "Size M (Vừa)", 0),
            FoodOption("s2", "Size L (Lớn)", 7000)
        ),
        toppings = listOf(
            FoodOption("t1", "Thêm 1 Shot Espresso", 10000),
            FoodOption("t2", "Sốt Caramel", 8000)
        )
    ),
    FoodItem(
        id = "5",
        category = "Cà phê",
        name = "Cà Phê Đen Đá",
        description = "Cà phê rang xay nguyên chất pha phin truyền thống, đậm đắng hậu ngọt.",
        basePrice = 29000,
        sizes = listOf(
            FoodOption("s1", "Size M (Vừa)", 0),
            FoodOption("s2", "Size L (Lớn)", 5000)
        ),
        toppings = listOf(
            FoodOption("t4", "Thạch cà phê", 7000)
        )
    ),
    FoodItem(
        id = "6",
        category = "Bánh ngọt",
        name = "Bánh Sừng Bò (Croissant)",
        description = "Bánh ngàn lớp nướng giòn rụm thơm lừng hương bơ.",
        basePrice = 32000,
        sizes = listOf(
            FoodOption("s1", "1 Bánh (Hâm nóng)", 0)
        ),
        toppings = listOf(
            FoodOption("t7", "Sốt sô-cô-la chấm kèm", 8000)
        )
    ),
    FoodItem(
        id = "7",
        category = "Bánh ngọt",
        name = "Tiramisu Cà Phê",
        description = "Bánh lạnh mềm mịn đậm vị cà phê Espresso và bột cacao.",
        basePrice = 42000,
        sizes = listOf(
            FoodOption("s1", "1 Phần tiêu chuẩn", 0)
        )
    )
)
