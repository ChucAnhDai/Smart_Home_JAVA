# Module 2: Rooms (Quản lý thiết lập từng phòng)

## 1. Giới thiệu chức năng
Module Rooms cung cấp các API RESTful giúp thao tác CRUD trên thực thể Phòng của một ngôi nhà thông minh. Nó cho phép phân trang (pagination), sắp xếp (sorting) và tìm kiếm trên danh sách để tối ưu hóa việc trả về dữ liệu cho FE Dashboard.

## 2. Các thành phần và Luồng hoạt động (Workflow)

### 2.1. Cấu trúc REST APIs của Phòng
Hệ thống hiển thị dữ liệu phòng dưới các thao tác tiêu chuẩn (REST):
- `GET /api/rooms` - Lấy danh sách các phòng (Hỗ trợ params phân trang: `page`, `size`, `sortBy`, `sortDir` và tìm kiếm `search`).
- `GET /api/rooms/{id}` - Lấy thông tin một phòng đơn lẻ.
- `POST /api/rooms` - Tạo phòng mới (yêu cầu body chứa `name`, `icon`).
- `PUT /api/rooms/{id}` - Cập nhật thay đổi trên phòng.
- `DELETE /api/rooms/{id}` - Xoá phòng khỏi hệ thống.

*Mọi request tới `/api/rooms/**` đều bị chặn lại tại bộ lọc `JwtAuthenticationFilter` (thuộc lớp chặn Security), do đó cần bao gồm JWT Token trong HttpHeader.*

### 2.2. Luồng truy vấn (Flow Data)

1. Client gọi **RoomController**: Map tham số URL, Query Params với object HTTP Request `CreateRoomReq` hoặc `UpdateRoomReq`.
2. **RoomService / RoomServiceImpl**: Nơi xử lý logic nghiệp vụ trung tâm.
   - Thư viện `ModelMapper` (được đăng ký làm Bean tại `AppConfig`) được Service sử dụng để tự động hoá việc convert qua lại giữa DTO <=> Entity (Room.class <=> CreateRoomReq/RoomResDTO), giúp code gọn gàng, tránh gõ thủ công hàm Get/Set dài dòng.
3. **RoomRepository**: Sử dụng kĩ thuật PagingAndSortingRepository do `JpaRepository` hỗ trợ. 
   - `PageRequest.of(page, size, sort)` được tạo để hướng dẫn MySQL limit và offset.
   - Khi có nhu cầu search, Spring JPA đọc hàm `findByNameContainingIgnoreCase` tự động sinh ra câu SQL query dạng `SELECT * FROM rooms WHERE LOWER(name) LIKE '%search%'`.
4. Result được trả về DTO dạng đối tượng `Page<RoomResDTO>` trở về Client với metadata (như `totalPages`, `totalElements`). Tiết kiệm thời gian lập trình frontend khi làm Grid/Table Layout.

### 2.3. Bắt lỗi (Exception Handling)
- Trong trường hợp Client gọi `GET /api/rooms/99` nhưng id=99 không tồn tại, repository tìm kiếm qua `findById` và trả về một `Optional.empty()`. Hàm `orElseThrow()` lập tức ném ra ngoại lệ `ResourceNotFoundException`.
- Ngay lập tức, `GlobalExceptionHandler` chặn lại ngoại lệ này và in format Error 404 cho Client. Vòng lặp giao tiếp báo lỗi hoàn thiện.
