# Module 8: Notifications (Quản lý Thông báo)

## 1. Mục đích hoạt động
Module **Notifications** là một module thụ động ở góc độ từ người dùng gửi lên. Nó chỉ mở các endpoint để **Đọc (GET)** và thao tác đánh dấu đã đọc (**Đổi trạng thái - PATCH**), không cung cấp endpoint POST cho người dùng thêm mới thông báo.

Thay vào đó, nó cung cấp một hàm Public `createNotification(message, type)` ở Layer Service để các module khác trong lõi hệ thống gọi vào khi có rủi ro hoặc sự kiện, như ở Module Device (thiết bị offline) hoặc Module Automation (chạy lệnh xong).

## 2. Các API Endpoints (`/api/notifications`)
- `GET /api/notifications`: Truy vấn lấy danh sách thông báo. Mặc định sẽ Order By `id` với hướng `desc` để thông báo mới nhất hiện lên trên cùng của danh sách. 
  - Khả năng lọc: Nếu truyền `?type=WARNING` hoặc `?type=CRITICAL`, API sẽ chỉ lọc trả về nhóm sự kiện đó.
- `PATCH /api/notifications/{id}/read`: Cho phép Client đánh dấu một thông báo vừa bấm vào xem chi tiết là "Đã đọc", backend sẽ gạt thuộc tính `isRead = true`.
- `PATCH /api/notifications/read-all`: Đánh dấu toàn bộ. Hỗ trợ cho nút "Mark all as read" ở Frontend.

## 3. Kiến trúc CSDL Nhúng
- `type` được cài đặt riêng bằng một kiểu `ENUM (INFO, WARNING, CRITICAL)`. Việc chuẩn hoá `NotificationType` thành class thay vì để dạng String Text tuỳ ý giúp tránh các trường hợp rác hoặc đánh vần sai trong quá trình Insert data (vd ghi nhầm `IFNO`). Spring Boot sẽ tự chặn đứng ngay từ vòng Request nếu kiểu ENUM truyền xuống API không hợp lệ.
