# Module 4: Devices (Quản lý Thiết bị)

## 1. Giới thiệu chức năng
Module **Devices** đóng vai trò là "trái tim" của hệ thống nhà thông minh Nexus. Module quản lý vòng đời của thiết bị: từ khi thiết bị được thêm vào một căn phòng (`Room`), gán một phân loại (`DeviceType`), giám sát trạng thái trực tuyến/ngoại tuyến (`isOnline`), cho đến điều khiển Bật/Tắt thiết bị và theo dõi điện năng (`energyKw`).

## 2. Các Endpoint API

Module cung cấp một loạt các endpoint toàn diện định tuyến tại `/api/devices`:

### 2.1 Quản lý CRUD cơ bản
- `POST /api/devices`: Tạo thiết bị mới (yêu cầu `name`, `roomId`, `typeId`). Mặc định thiết bị mới sẽ ở trạng thái "OFF", "Online" và tiêu thụ "0.0 kw".
- `PUT /api/devices/{id}`: Cập nhật thông tin cấu hình của thiết bị.
- `DELETE /api/devices/{id}`: Xoá thiết bị khỏi nhà.

### 2.2 Đọc và Tra cứu chuyên sâu (Tìm kiếm, Lọc, Phân trang)
- `GET /api/devices`: Liệt kê tất cả thiết bị. Có thể search theo tên với `?search=...`. 
- `GET /api/devices/{id}`: Tra cứu chi tiết 1 thiết bị thông qua ID.
- `GET /api/devices/room/{roomId}`: Trích xuất toàn bộ thiết bị đang được lắp đặt ở một phòng cụ thể.
- `GET /api/devices/type/{typeId}`: Lọc ra tất cả các thiết bị cùng chung chuẩn loại (ví dụ: Xem toàn bộ các đèn trong nhà).

### 2.3 Điều khiển nhanh (State Control)
Module cung cấp các endpoint dạng `PATCH` chuẩn HTTP (dùng cho cập nhật một phần dữ liệu) để người dùng điều khiển thiết bị:
- `PATCH /api/devices/{id}/turn-on`: Cưỡng ép bật thiết bị (Set "ON").
- `PATCH /api/devices/{id}/turn-off`: Cưỡng ép tắt thiết bị (Set "OFF").
- `PATCH /api/devices/{id}/toggle`: Công tắc đảo trạng thái (Nếu đang "ON" thì chuyển sang "OFF", và ngược lại). Mọi thay đổi đều được cập nhật thời gian vào cột `lastActiveTime`.

## 3. Cấu trúc Liên kết (Mapping & Relationships)
- Trong CSDL, Entity `Device` có mối quan hệ **N-1 (ManyToOne)** với `Room` và `DeviceType` (Mỗi thiết bị chỉ nằm trong đúng 1 Căn Phòng và thuộc 1 Phân Loại nhất định).
- Việc Load dữ liệu trong Entity được cấu hình với `@ManyToOne(fetch = FetchType.LAZY)` để tránh truy vấn thừa thãi khi không cần thiết (N+1 query problem).
- Khi Response trả ra ngoài cho FE, ModelMapper sẽ tự động join các thông tin cần thiết vào `RoomResDTO` và `DeviceTypeResDTO` được bọc lồng bên trong `DeviceResDTO` để FE có đủ thông tin render UI dễ dàng như: tên phòng, icon thiết bị, v.v.
