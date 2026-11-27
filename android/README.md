# 📱 Coffee Shop App — Frontend (Android)

Phần **Frontend (Mobile)** của đồ án **D2C F&B Platform**, xây dựng hoàn toàn bằng **Kotlin** & **Jetpack Compose** theo kiến trúc **MVVM**.

> [!IMPORTANT]
> Khi mở dự án bằng Android Studio, hãy chọn mở đúng thư mục **`android/`** (nơi chứa `settings.gradle.kts` và `build.gradle.kts`), **không** mở thư mục gốc bên ngoài.

---

## ✨ Tính năng đã hoàn thiện (UI/UX)

Ứng dụng hỗ trợ **2 luồng trải nghiệm song song** trên cùng một app, phân quyền khi đăng nhập:

### 🧑‍💻 1. Luồng Khách hàng (User Role)
| Tính năng | Mô tả |
| :--- | :--- |
| **Menu động & Tìm kiếm** | Phân loại danh mục (*Cà phê*, *Bánh ngọt*...), tìm kiếm món nhanh. |
| **Tùy biến món (Customization)** | Chọn Size (bắt buộc), chọn nhiều Topping, thêm ghi chú riêng cho bếp. |
| **Giỏ hàng thông minh** | Tự động tách dòng cho các món giống nhau nhưng khác tùy chọn *(VD: 1 ly ít đá, 1 ly nhiều đá)*. |
| **Tính phí Ship tự động** | Ước tính phí vận chuyển dựa trên địa chỉ giao hàng *(sẵn sàng ghép Map API)*. |
| **Áp dụng Voucher** | Hỗ trợ mã giảm giá theo `%` hoặc số tiền cố định, kiểm tra điều kiện đơn tối thiểu *(chỉ giảm tiền món, không giảm phí ship)*. |
| **Theo dõi đơn Real-time** | Cập nhật tiến độ pha chế (`Mới` → `Đang pha` → `Hoàn tất`) & hiển thị thông tin **Shipper AhaMove/Lalamove**. |
| **Tương tác sau mua** | Đánh giá chất lượng (1–5 sao), lưu món yêu thích, đặt lại đơn cũ nhanh chóng. |

### 👨‍🍳 2. Luồng Quầy Pha Chế (Barista Role)
*(Ở chế độ Mock Data: đăng nhập với tên có chứa từ khóa `"barista"`)*

| Tính năng | Mô tả |
| :--- | :--- |
| **Điều khiển trạng thái Quán** | Bật/tắt linh hoạt (`Đang mở cửa`, `Bận 15 phút`, `Bận 30 phút`, `Bận 1 tiếng`, `Đóng cửa`) — đồng bộ sang app Khách hàng. |
| **Quản lý Đơn hàng** | Tiếp nhận đơn, chuyển trạng thái (`Mới` → `Đang pha` → `Hoàn tất`), làm nổi bật ghi chú đặc biệt, hủy đơn kèm lý do. |
| **Điều phối Shipper** | Bấm **"Gọi Shipper Lalamove/AhaMove"** khi pha xong để kích hoạt BE gọi tài xế & nhận thông tin shipper trả về. |
| **Quản lý Kho & Khuyến mãi** | Thêm/sửa món mới, bật/tắt trạng thái hết hàng, tạo Voucher giảm giá mới. |

---

## 🛠️ Công nghệ & Cấu trúc thư mục

* **Ngôn ngữ:** Kotlin
* **UI:** Jetpack Compose (Material Design 3)
* **Kiến trúc:** MVVM (`Model` – `View` – `ViewModel`)
* **State Management:** Compose State (`mutableStateOf`, `mutableStateListOf`)

```text
android/app/src/main/java/com/example/drinkapp/
├── model/          # Data classes & Mock Data (MenuModels, OrderModel, VoucherModel, CartModel)
├── ui/             # Các màn hình Compose (Login, Menu, DrinkDetail, Cart, Checkout, Barista, ...)
│   └── theme/      # Color, Typography, Theme (Material 3)
├── viewmodel/      # CoffeeViewModel.kt — Trung tâm quản lý State & logic nghiệp vụ
└── MainActivity.kt
```

---

## 🔌 Hướng dẫn ghép nối Backend (`../BE`)

Ứng dụng hiện chạy bằng **Mock Data** và đã tách sẵn cấu trúc để tích hợp API nhanh chóng:

1. **Thêm thư viện mạng:** Cài `Retrofit` + `Gson` (hoặc `Moshi`) trong `app/build.gradle.kts`.
2. **Cho phép HTTP ở môi trường dev:** Thêm `android:usesCleartextTraffic="true"` trong `AndroidManifest.xml` nếu gọi API local bằng `http://`.
3. **Cấu hình `BASE_URL`:**
   * **Android Emulator:** `http://10.0.2.2:8000/`
   * **Điện thoại thật (cùng Wi-Fi):** `http://<IP-LAN-máy-chạy-BE>:8000/`
4. **Thay thế Mock Data trong `CoffeeViewModel.kt`:**
   * Tạo lớp `Repository` gọi API trong `viewModelScope.launch` để đổ dữ liệu vào `menuList`, `baristaOrders`, `vouchers`, `storeStatus`.
   * Ở `callShipper()`, gọi API `POST /orders/{orderId}/call-shipper` và dùng Polling/WebSocket để cập nhật `shippingStatus`, `driverName`, `driverPhone`, `driverPlate`.
   * Ở `LoginScreen.kt`, có thể copy thêm 1 ô nhập Password để gọi API `/auth/login` và lưu JWT Token.

---

## 💡 Test nhanh giao diện (Mock Mode)
* **Vào màn hình Barista:** Nhập tên bất kỳ có chứa `"barista"` *(VD: `barista123`, `Quầy barista`)*.
* **Vào màn hình Khách hàng:** Nhập tên bất kỳ không chứa `"barista"` *(VD: `Trần Gia Huy`)*.
