# Module 10: Home Settings (Quản Lý Cài Đặt Hệ Thống)

## 1. Giới thiệu
Đây là module số 10 trong dự án Nexus Smart Home. Vai trò của thành phần này là nơi cấu hình tập trung cấp cao nhất (Global Configuration) cho Frontend và System. Module sẽ lưu trữ những tham số như:
- Tên ngôi nhà (`homeName`)
- Múi giờ (`timezone`)
- Giao diện tối/sáng (`darkMode`)
- Cấu hình nhận thông báo (Bật/tắt Noti qua `pushNotif`)

## 2. Điểm Nhấn Kiến Trúc (Singleton DB Design)
Vì đây là những cài đặt chung áp dụng cho toàn bộ ngôi nhà, nên **Chỉ Cho Phép Tồn Tại 1 Record Duy Nhất** trong bảng dữ liệu Cấu Hình. Thiết kế của module này đã ứng dụng mô hình Singleton cho DB:

- Endpoint **không cung cấp** phương thức lập mới (`POST`), phân trang, chia danh sách (`id`), hay XOÁ đi bộ Setting duy nhất này.

- Phương thức trong Backend (`getOrCreateDefaultSetting()`):  
Khi FE muốn gọi Setting ra (nhưng chưa từng có ai ghi DB), Service này sẽ tự động tạo một phiên bản mặc định đút vào DB thay cho người dùng (Khởi gán tên mặc định là: *Nexus Smart Home*, và timezone chuẩn là *Asia/Ho_Chi_Minh*). Mọi câu gọi dữ liệu về sau chỉ trích đúng phần tử ở Index [0] này làm cấu hình.

## 3. Các API Endpoints
- `GET /api/home-settings`: Lấy thông số hệ thống.
- `PUT /api/home-settings`: Sửa đổi cấu hình toàn cầu. (Khối Entity sẽ tiến hành check NULL và đính lại những giá trị mới vào Record thứ nhất trong Database).
