# ⚙️ Coffee Shop App — Backend (Python REST API + SQLite)

Tài liệu thiết kế và hướng dẫn triển khai phần **Backend** cho đồ án **D2C F&B Platform**, phục vụ kết nối với ứng dụng Android tại `../android`.

---

## 🛠️ Tech Stack đề xuất

| Thành phần | Lựa chọn khuyến nghị | Lý do |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Python 3.10+ | Dễ viết, dễ đọc, nhiều thư viện hỗ trợ |
| **Framework** | **FastAPI** | Tự sinh Swagger UI (`/docs`) cho team Mobile test API nhanh, validate JSON bằng Pydantic, hỗ trợ sẵn `async` & `WebSocket` |
| **Database** | **SQLite** + **SQLAlchemy** (hoặc **SQLModel**) | Nhẹ, không cần cài DB server riêng, dễ đóng gói demo |
| **Auth** | **JWT** (`PyJWT` / `python-jose`) + `passlib[bcrypt]` | Phân quyền rõ `USER` và `BARISTA` |

> [!TIP]
> **Lưu ý quan trọng khi dùng SQLite:** Hãy bật chế độ **WAL (`PRAGMA journal_mode=WAL;`)** khi khởi tạo kết nối DB để tránh lỗi `database is locked` khi app Khách hàng và Barista cùng đọc/ghi hoặc polling liên tục.

---

## 📦 Data Contract (Khớp 100% với Models ở Frontend)

Các model bên dưới tương ứng trực tiếp với code tại `android/app/src/main/java/com/example/drinkapp/model/`:

### 1. Menu (`MenuModels.kt`)
* **`FoodItem`**:
  * `id` *(string)*
  * `category` *(string)* — VD: `"Cà phê"`, `"Bánh ngọt"`
  * `name` *(string)*
  * `description` *(string)*
  * `basePrice` *(int — VND)*
  * `sizes` *(list of `FoodOption`: `id`, `name`, `extraPrice`)*
  * `toppings` *(list of `FoodOption`: `id`, `name`, `extraPrice`)*
  * *(Mở rộng cho Barista)*: `isAvailable` *(bool)*, `imageUrl` *(string — lưu ảnh tĩnh tại `/static/images/...`)*

### 2. Đơn hàng & Giao nhận (`OrderModel.kt`)
* **`OrderStatus`**: `NEW` (`"Mới"`) | `PREPARING` (`"Đang pha"`) | `READY` (`"Hoàn tất"`) | `CANCELLED` (`"Đã hủy"`)
* **`ShippingStatus`**: `PENDING` (`"Chưa gọi Shipper"`) | `SEARCHING` (`"Đang tìm tài xế..."`) | `DELIVERING` (`"Tài xế đang giao"`) | `DELIVERED` (`"Giao thành công"`)
* **`BaristaOrder`**:
  * `orderId` *(string — VD: `"#CF-104"`)*
  * `customerName`, `customerPhone`, `deliveryAddress` *(string — tách riêng để đẩy thẳng sang API Shipper)*
  * `orderTime` *(string / ISO-8601)*
  * `status` *(`OrderStatus`)*
  * `items` *(list of `OrderItemDetail`: `drinkName`, `quantity`, `sizeAndToppings`, `note`)*
  * `totalPrice` *(int — tiền thu hộ COD)*
  * `cancelReason` *(string | null)*
  * `shippingStatus` *(`ShippingStatus`)*
  * `driverName`, `driverPhone`, `driverPlate` *(string | null — nhận từ API/Webhook Shipper)*

### 3. Voucher (`VoucherModel.kt`)
* **`DiscountType`**: `PERCENT` | `FIXED_AMOUNT`
* **`Voucher`**: `code`, `description`, `discountType`, `discountValue`, `minSubTotal`, `maxDiscount`, `remainingUsage`, `expiryDate`

---

## 🔌 Danh sách REST API cần xây dựng

| Nhóm | Method & Endpoint | Mô tả |
| :--- | :--- | :--- |
| **Auth** | `POST /auth/register` | Đăng ký tài khoản Khách hàng |
| | `POST /auth/login` | Đăng nhập, trả về `access_token` và `role` (`USER` / `BARISTA`) |
| **Store** | `GET /store/status` | Lấy trạng thái quán (`OPEN`, `BUSY_15`, `BUSY_30`, `BUSY_60`, `CLOSED`) |
| | `PUT /store/status` | *(Barista)* Cập nhật trạng thái quán |
| **Menu** | `GET /menu` | Lấy danh sách món (lọc theo `category`, tìm kiếm `q`) |
| | `POST /menu`, `PUT /menu/{id}`, `DELETE /menu/{id}` | *(Barista)* Thêm, sửa, bật/tắt hết hàng, xóa món |
| **Voucher** | `GET /vouchers` | Lấy danh sách voucher khả dụng |
| | `POST /vouchers/validate` | Kiểm tra mã giảm giá theo `subTotal` *(chỉ trừ tiền món, không trừ phí ship)* |
| | `POST /vouchers` | *(Barista)* Tạo voucher mới |
| **Orders** | `POST /orders` | *(User)* Tạo đơn hàng mới & trừ lượt voucher |
| | `GET /orders` | Lấy danh sách đơn *(Barista xem tất cả, User xem đơn của mình)* |
| | `GET /orders/{orderId}` | Chi tiết & trạng thái real-time của đơn *(FE polling 3–5s/lần)* |
| | `PATCH /orders/{orderId}/status` | *(Barista)* Đổi trạng thái đơn hoặc hủy đơn kèm `cancelReason` |
| | `POST /orders/{orderId}/rating` | *(User)* Đánh giá đơn hàng (1–5 sao) |

---

## 🛵 Tích hợp API bên thứ ba: AhaMove / Lalamove

Frontend đã có sẵn nút **"Gọi Shipper Lalamove/AhaMove"** ở màn hình Barista và các trường `driverName`, `driverPhone`, `driverPlate`, `shippingStatus` để hiển thị cho Khách hàng.

Backend đóng vai trò **cầu nối trung gian** (Android không gọi trực tiếp sang AhaMove/Lalamove để tránh lộ API Key). Có **2 phương án triển khai**:

### Phương án A: Kết nối Sandbox thật (AhaMove / Lalamove API)
Nếu nhóm có tài khoản Sandbox/Developer của AhaMove hoặc Lalamove, luồng xử lý gồm 3 bước:
1. **Ước tính phí ship (`Quotation`):** Gọi API báo giá của AhaMove/Lalamove từ địa chỉ quán -> `deliveryAddress` để trả phí ship về cho màn hình Checkout.
2. **Tạo đơn giao hàng (`Create Order`):** Khi Barista bấm `POST /orders/{orderId}/call-shipper`, BE lấy `customerName`, `customerPhone`, `deliveryAddress`, `totalPrice` (COD) gửi lên API tạo đơn của đối tác, nhận về `externalOrderId` và chuyển `shippingStatus = "SEARCHING"`.
3. **Hứng Webhook cập nhật tài xế:**
   * Tạo endpoint `POST /webhooks/shipper` (public ra internet qua `ngrok` nếu chạy local) để AhaMove/Lalamove bắn dữ liệu về mỗi khi có tài xế nhận đơn / đang giao / hoàn tất.
   * BE cập nhật `driverName`, `driverPhone`, `driverPlate` và `shippingStatus` (`DELIVERING` -> `DELIVERED`) vào SQLite để app Android lấy qua Polling/WebSocket.

### Phương án B: Giả lập (Mock) trên Backend *(Khuyên dùng nếu chưa có Key Sandbox)*
Nếu chưa đăng ký được Sandbox Key của AhaMove/Lalamove, bạn vẫn giữ nguyên kiến trúc API như thật để FE không phải sửa code:
1. Khi Barista gọi `POST /orders/{orderId}/call-shipper`, BE lập tức đổi `shippingStatus = "SEARCHING"`.
2. Dùng `BackgroundTasks` (của FastAPI) hoặc `asyncio.create_task` chạy ngầm:
   * Sau **3 giây**: Tự sinh thông tin tài xế mẫu (`driverName`, `driverPhone`, `driverPlate`) và đổi `shippingStatus = "DELIVERING"`.
   * Sau **10–15 giây**: Đổi `shippingStatus = "DELIVERED"`.
3. App Android chỉ việc gọi `GET /orders/{orderId}` định kỳ (hoặc qua WebSocket) là thấy trạng thái nhảy tự động như gọi Shipper thật.

---

## 🚀 Hướng dẫn cài đặt & chạy Server

1. **Tạo môi trường ảo & cài thư viện:**
   ```bash
   cd BE
   python -m venv .venv

   # Windows:
   .venv\Scripts\activate
   # macOS / Linux:
   source .venv/bin/activate

   pip install -r requirements.txt
   ```

2. **Chạy server** *(nhớ dùng `--host 0.0.0.0` để Android Emulator / máy thật truy cập được)*:
   ```bash
   uvicorn main:app --host 0.0.0.0 --port 8000 --reload
   ```

3. **Kiểm tra API Docs:**
   * Truy cập `http://localhost:8000/docs` (Swagger UI).

---

## 🔗 Lưu ý kết nối với Android
* **Android Emulator:** Kết nối về `http://10.0.2.2:8000` *(không dùng `localhost`)*.
* **Điện thoại thật (cùng Wi-Fi):** Kết nối về `http://<IP-LAN-máy-tính>:8000` *(nhớ mở port `8000` trong Windows Firewall nếu bị chặn)*.
* **Test Webhook AhaMove/Lalamove ở local:** Chạy `ngrok http 8000` để lấy URL HTTPS công khai dán vào cấu hình Webhook của đối tác.
