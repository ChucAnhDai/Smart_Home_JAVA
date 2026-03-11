# Module 7: Activity Logs (Nhật ký hoạt động)

## 1. Giới thiệu chức năng
Module **Activity Logs** lưu trữ toàn bộ lịch sử các luồng sự kiện xảy ra trên hệ thống nhà thông minh Nexus. Module này chỉ hỗ trợ tác vụ **XEM** cho frontend, bao gồm thiết bị nào đã được BẬT/TẮT, thiết bị mất kết nối (OFFLINE), hoặc các tác vụ hệ thống khác. Nhật ký này chỉ có thể được tạo (ghi) nội bộ ở phía Backend bởi các API điều khiển thiết bị (Như Bắt thiết bị) và không thể bị sửa/xóa bởi người dùng ngoài.

## 2. API Endpoints tại `/api/activity-logs`

### 2.1 Truy Vấn Lịch Sử
- `GET /api/activity-logs`: Trả về dữ liệu gốc.
- Các Filter (Query Parameter) đi kèm với endpoint:
    - `deviceId`: Chỉ xem list Activity_Logs cho một thiết bị cụ thể.
    - `startTime` & `endTime`: Truyền vào các ISO Date `YYYY-MM-DD` để lọc logs sinh ra trong khung giờ/khoảng ngày đó.
    - `page`, `size`, `sortBy`, `sortDir` để thực hiện chia và xếp trang theo thứ tự (mặc định là Sort theo ID ngẫu ngược - xem logs mới nhất trước).

## 3. Kiến trúc nội bộ hệ thống Backend
- **Repository Layer (`ActivityLogRepository.java`)**: 
  Thực hiện truy vấn dữ liệu động qua Annotation `@Query` với cú pháp JPQL (Java Persistence Query Language). Các thuộc tính tùy chọn điều kiện (như nếu `deviceId == null` thì không dùng làm điều kiện lọc) giúp backend có khả năng Filter cực linh hoạt.
  
- **Service Layer (`ActivityLogService.java`)**: 
  - Khởi tạo hàm `logActivity(...)`: Dành cho các Module khác (VD Module Device) có thể gọi để ghi lại log. Phương thức này không lộ diện làm Endpoint.
  - Hàm biến đổi DTO: Manual Mapping tay thủ công chứ không dùng ModelMapper vì đây là Object mỏng nhưng yêu cầu nối dữ liệu (Trích DeviceID, DeviceName từ một Device đã liên kết) vì `ActivityLog` và `Device` đang sử dụng quan hệ `@ManyToOne (Lazy Fetch)`. 

Mọi thay đổi trên trạng thái thiết bị sẽ được cập tiếp nối bằng việc gọi hành động thông báo (`notifications`) và ghi Log sau này!
