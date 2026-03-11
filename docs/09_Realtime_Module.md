# Module 9: Realtime (Quản lý Thời gian thực với WebSocket)

## 1. Mục đích chức năng
Khác với mô hình Client-Server dạng Request/Response đồng bộ thông thường của API RESTful, Hệ thống Quản trị nhà thông minh yêu cầu cơ chế phản hồi theo thời gian thực (Real-time). Khi bất kỳ một thao tác TẮT đèn nào diễn ra ở Web Browser thứ 1, thì Web Browser thứ 2 (hoặc App Mobile) phải ngay lập tức được thông báo về hành động đó mà không cần nạp lại (Reload) trang web.

## 2. Công Nghệ
- **Spring Boot WebSocket (STOMP)**: Đóng vai trò làm Protocol Broker ở backend.
- **SockJS**: Giải pháp tương thích rơi (Fall-back) cho những mạng máy tính hoặc trình duyệt lỗi thời không hỗ trợ WebSocket thuần chủng.

## 3. Kiến Trúc Hoạt Động (Endpoints)

Dữ liệu được bọc qua luồng `DeviceRealtimeDTO` (Gồm ID, Name, Status, isOnline, timestamp).

### A. Endpoint Kết Nối Mạng
- Client phải thiết lập Socket kết nối tại cổng đích TCP: **`/ws/devices`**

### B. Broadcast Channel (Kênh phát sóng)
- Topic đăng ký (Subcribe): **`/topic/device-status`**
- Bất kỳ một gói tin ném vào Topic này, tất cả các Client đang lắng nghe lập tức nhận được bản tin.

### C. Quản Lý Ném Tin (Publish)
Có 2 con đường ném bản tin lên Realtime:
1. **Từ Server -> Server Broadcast**: Giả sử Client vừa bấm gọi API `PATCH /api/devices/{id}/toggle`, phần Database cập nhật xong. Ngay lúc đó, Code Java ở `DeviceService` có thể gọi trực tiếp `RealtimeService.broadcastDeviceStatus(...)` để ném vào kênh Chat báo hiệu xong.
2. **Từ Client -> Relay qua Server -> Các Client còn lại**: Frontend bắn Packet thông qua STOMP tại điểm `/app/device-status`, controller `WebSocketController` sẽ chụp lấy và Relay (Gửi chuyển qua) sang endpoint `/topic/device-status`.
