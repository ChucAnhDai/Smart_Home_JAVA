# Module 12: Dashboard & Statistics (Thống kê và Bảng điều khiển)

## 1. Mục đích hoạt động
Module **Dashboard** là mảnh ghép quan trọng cuối cùng trước khi hoàn thành phiên bản 1.0 của Smart Home Nexus. Nhiệm vụ của nó là thu thập, "gom", tính toán từ các bảng cơ sở dữ liệu có sẵn (devices, users, rooms, energy, logs) và móc nối để ném ra một bản Báo cáo duy nhất lên Trang chủ quản trị (Home/Index). Khi người quản trị đăng nhập vào, đây là nơi họ nhìn được Tổng quan (Chim bay).

Module này Không có Entity và Repository riêng vì nó đọc chùa từ người khác.

## 2. API Endpoints tại `/api/dashboard`

Module cung cấp các dạng báo biểu sau (bằng cấu trúc REST `GET`):

### 2.1 Lấy toàn bộ tổng quan 1 lần (`/api/dashboard/overview`)
- Điểm thu hút dữ liệu trả ra cho `DashboardStatsDTO`. Bao gồm tổng số lượng người dùng có trong nhà, phòng có trong nhà, tổng thiết bị, số đồ đang bật, tắt, online. Đặc biệt có tính năng tính toán điện năng bằng JPQL (`SUM()`) và xuất mảng 5 activities mới nhất. Rất gọn cho một lần render Dashboard UI.

### 2.2 Các Endpoint thống kê rẽ nhánh nhỏ
Trong trường hợp Web/App cần gọi tải nhỏ giọt từng biểu đồ mà khỏi sợ chậm query lớn, các API nhỏ gồm:
- `/api/dashboard/devices`: Trả số lượng tỷ lệ giữa ON/OFF/Online dưới dạng Map Key-Value.
- `/api/dashboard/energy`: Trích năng lượng tiêu thụ theo thời gian.
- `/api/dashboard/recent-activities?limit=10`: Tải 10 (hoặc số tùy ý) sự kiện thay đổi mới nhất cho dòng Timeline UI.

## 3. Kiến trúc nội bộ hệ thống Backend
- **DashboardServiceImpl.java**: Phải Injection tới 5 Repositories khác nhau (`DeviceRepository`, `Room`, `User`, `Energy`, `ActivityLog`). Bằng cách sử dụng các hàm tối ưu hóa như `countByStatusIgnoreCase` được nhúng ở các Class kia, quá trình kiểm kê Dashboard xảy ra cực nhanh trong vài miliseconds thay vì phải `findAll()` về List Java cực tốn RAM.
- **Tính Năng Chống Null (COALESCE)**: Năng lượng đầu vào có thể là Null nếu nhà chưa có ai gắn thiết bị. Nên query `SELECT COALESCE(SUM(e.energyKwh), 0.0)` sẽ giúp DB không bị nổ lỗi NullPointer exception.
