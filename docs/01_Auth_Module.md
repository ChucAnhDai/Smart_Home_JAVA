# Module 1: Authentication & Authorization (Xác thực và phân quyền)

## 1. Giới thiệu chức năng
Module Authentication & Authorization đảm nhiệm việc xác minh danh tính người dùng và cấp quyền để truy cập vào các API được bảo vệ của hệ thống Smart Home.

## 2. Các thành phần và Luồng hoạt động (Workflow)

### 2.1. Đăng ký (Register)
- **Luồng:** `Client` -> `AuthController (/api/auth/register)` -> `AuthService` -> `UserRepository` -> `Database`
- **Cách hoạt động:** Khi người dùng gửi request tạo tài khoản với `fullName`, `email`, `password`.
  - Passwords sẽ được băm (hash) bằng thuật toán `BCrypt` thông qua Bean `PasswordEncoder`.
  - Tài khoản mặc định sẽ được gán role `MEMBER` và lưu xuống database.
  - Sau đó, hệ thống sinh ra một chuỗi JWT Token và trả về cho Client.

### 2.2. Đăng nhập (Login)
- **Luồng:** `Client` -> `AuthController (/api/auth/login)` -> `AuthenticationManager` -> `CustomUserDetailsService` -> `UserRepository` -> `Database`
- **Cách hoạt động:** 
  - Người dùng gửi `email` và `password`.
  - `AuthenticationManager` thẩm định thông tin này thông qua `CustomUserDetailsService` (nơi tìm kiếm user bằng `FindByEmail`).
  - Nếu thông tin sai, ném ra ngoại lệ `BadCredentialsException` (được bắt tại `GlobalExceptionHandler` trả về HTTP 401 Unauthorized).
  - Nếu đúng, hệ thống sinh ra một chuỗi token thông qua `JwtUtils.generateToken(user)` và trả về.

### 2.3. Kiểm tra quyền truy cập API bằng Filter
- **Cách hoạt động của JWT Filter (`JwtAuthenticationFilter.java`):**
  - Mọi request của Client gửi lên những đường dẫn ngoài `api/auth/**` đều đi qua `JwtAuthenticationFilter`.
  - Filter tiến hành kiểm tra header `Authorization: Bearer <token>`.
  - Token này sau đó được bóc tách bằng key bí mật (chứa trong `application.properties`) thông qua `JwtUtils.extractUsername(token)`.
  - Hệ thống lấy thông tin Role người dùng đem đưa vào `SecurityContextHolder`. Request được cho phép qua và tiếp tục gọi đến Controller đích.

### 2.4. Bắt lỗi (Exception Handling)
- `GlobalExceptionHandler` được định nghĩa với `@ControllerAdvice` để xử lý tập trung mọi Exceptions được ném ra từ Controller (như `ResourceNotFoundException`) nhằm đảm bảo Response trả ra cho Client luôn có định dạng `{"error": "...", "message": "..."}` và http Status Codes rõ ràng (404, 401, 500).

## 3. Tổng kết
Hệ thống xác thực đạt chuẩn Stateless Security với JWT, ngăn chặn các truy cập trái phép và bảo vệ an toàn mọi modules khác trong hệ thống Smart Home.