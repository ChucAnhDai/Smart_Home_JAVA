# Module 5: Automation Rules (Quản lý Tự động hóa)

## 1. Giới thiệu chức năng
Module **Automation Rules** chịu trách nhiệm quản lý các kịch bản tự động hóa cho hệ thống nhà thông minh Nexus. Module này lưu trữ các sự kiện, điều kiện (conditions) và hành động (actions) tương ứng để kích hoạt thiết bị theo kịch bản.

Ví dụ: "Nếu thời gian là 18:00 (conditionText), thì Bật toàn bộ đèn phòng khách (actionText)". 

## 2. API Endpoints tại `/api/automation-rules`
- `POST /api/automation-rules`: Tạo mới một ngữ cảnh tự động hóa. Payload bao gồm chuỗi JSON cho `conditionText` và `actionText`. Mặc định `isActive` là `true`.
- `GET /api/automation-rules`: Tra cứu danh sách có hỗ trợ phân trang (Pagination), Sắp xếp (Sorting) và lọc tên (Search).
- `GET /api/automation-rules/{id}`: Truy xuất thông tin chi tiết một luật tự động hóa.
- `PUT /api/automation-rules/{id}`: Cập nhật thông tin cấu hình luật (ví dụ tạm thời tắt bằng cách truyền `isActive: false` hoặc sửa actions).
- `DELETE /api/automation-rules/{id}`: Xóa luật.

## 3. Cấu trúc Database và Code
- Các trường `conditionText` và `actionText` được ánh xạ bằng kiểu `<JSON>` trong Database để hỗ trợ cấu trúc dữ liệu linh hoạt (khi truy vấn bằng MySQL) thay vì Text đơn thuần. Tuy trong mã Entity Java chúng đang được map ở định dạng `String`, việc chứa JSON object đảm bảo Frontend có thể mở rộng logic phức tạp một cách độc lập hoàn toàn.
- Kiến trúc chuẩn Repository-Service-Controller đã được áp dụng, giúp các chức năng được tách biệt rõ ràng và hỗ trợ bảo trì, mở rộng hệ thống hiệu quả.
