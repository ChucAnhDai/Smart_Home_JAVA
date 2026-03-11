# Module 6: Users (Quản lý Người dùng)

## 1. Giới thiệu chức năng
Module **Users** cung cấp các công cụ nâng cao để ban quản trị (Admin) của ngôi nhà thông minh theo dõi, quản lý, phân quyền và kiểm soát toàn bộ các thành viên khác đang sử dụng hệ thống Nexus. Nó đảm bảo những người tham gia có vai trò (Role) và những mức truy cập phù hợp.

## 2. Các Endpoint API tại `/api/users`

Module cung cấp một loạt endpoint RESTful linh hoạt phục vụ nhu cầu CRUD đối với tài khoản:

### 2.1 Xem & Truy Vấn
- `GET /api/users`: API truy vấn nhiều người dùng đi kèm khả năng lọc với từ khóa (`?search=`), hỗ trợ tìm kiếm dựa trên nội dung Tên (fullName) hoặc Địa chỉ (email). Cũng cấp luôn tham số để backend thực hiện Phân trang dữ liệu trực tiếp (`page, size, sort`).
- `GET /api/users/{id}`: Xem chi tiết cấu hình và dữ liệu của một End-user bất kỳ theo ID. Đảm bảo dữ liệu nhạy cảm (mật khẩu) không bị để rò rỉ dưới dạng JSON trong Responses nhờ quá trình mapping DTO.

### 2.2 Quản Lý Vòng Đời & Ủy Quyền
- `POST /api/users`: Action Admin lập tài khoản mới. Service sẽ mã hóa password thông qua quá trình Hash của mô đun `PasswordEncoder` trước khi điền vô CSDL.
- `PUT /api/users/{id}`: Cho phép thay đổi Name, Email, Status thông qua ID. Nếu admin thay đổi địa chỉ email của người này, backend sẽ chạy logic kiểm định (validate) chặt để phòng ngừa email mới bị "đụng hàng" trùng lắp với user khác.  
- `DELETE /api/users/{id}`: Xóa triệt để người dùng đó ra khỏi Nexus Smart Home.

### 2.3 Phân Quyền Nhanh (Role Change)
- `PATCH /api/users/{id}/role`: HTTP PATCH endpoint được tạo riêng chỉ dành cho cập nhật quyền nhằm mục đích an ninh. Truyền vào Query Params như `?role=ADMIN` hoặc `?role=GUEST`.

## 3. Quá trình Thiết kế Logic Hệ Thống & Annotation (Java)

1. **Entities & DTO (Mô hình Dữ liệu Trắng Đen):**
   - Entity `User` chính là chủ thể map với DB có gắn `@PrePersist` đảm bảo mỗi user tự kích hoạt Default là "ACTIVE" và Role là "MEMBER" nếu bị bỏ trống trong input. Lớp tham gia implement Interface `UserDetails` của Spring Security để giúp xác thực Auth JWT.
   - Khi request dữ liệu, hệ thống hướng dẫn qua `CreateUserReq` & `UpdateUserReq`. Trong khi nhận phản hồi, Request bị ép dùng lớp trả về thông minh `UserResDTO` - hoàn toàn không có thuộc tính `password`.

2. **Spring Data JPA & Controller:**
   - Trong Interface `UserRepository`, kỹ thuật Native JPQL String được dùng dưới @Query. Đây là một phương thức mạnh để tìm chữ "Gần giống" bằng lệnh `LIKE` trên hai cột kết hợp `email` và `fullName` không phân biệt HOA-thường (IGNORECASE / LOWER).
   - Controller và Service kết nối nhịp nhàng, xử lý lỗi (Báo lỗi 404 cho User Not Found, Báo lỗi 500 khi trùng lặp email). Mọi Response được gói trong HTTP Status ResponseEntity.
