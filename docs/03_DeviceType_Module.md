# Module 3: Device Types (Loại Thiết Bị)

## 1. Giới thiệu chức năng
Module **Device Types** đảm nhận việc chuẩn hoá, định nghĩa, và phân loại toàn bộ hệ thống các thiết bị trong nhà thành những nhóm cụ thể (như: Quạt, Đèn, Điều hòa, Khóa cửa thông minh, Hệ thống an ninh...). Module giúp Backend và Frontend thống nhất một cách phân mảnh dữ liệu rõ ràng thay vì nhập tự do loại thiết bị.

## 2. Cấu trúc và Luồng đi dữ liệu (Data flow)

### Các Endpoint API:
- `POST /api/device-types` - Body chứa `{ "name": "Light", "icon": "💡" }`.
- `GET /api/device-types` - Có hỗ trợ `?page=0&size=10&search=Ligh` để Filter nhanh theo tên.
- `GET /api/device-types/{id}` - Lấy theo từng ID
- `PUT /api/device-types/{id}` - Cập nhật
- `DELETE /api/device-types/{id}` - Xóa

### Luồng Hoạt Động Cốt Lõi:
1. `DeviceTypeController` tiếp nhận yêu cầu từ client với DTO (`CreateDeviceTypeReq` / `UpdateDeviceTypeReq`).
2. Controller ủy quyền xử lý nghiệp vụ thông qua Interface `DeviceTypeService` hướng đến `DeviceTypeServiceImpl`.
3. `DeviceTypeServiceImpl` biến đổi Object mapping (bởi Bean `ModelMapper`) từ các Req DTO sang đối tượng bảng `@Entity DeviceType`.
4. Gọi hàm `save()`, `findAll()`, `findById()`, `delete()` trong Interface `DeviceTypeRepository`. (Đặc biệt hàm `findByNameContainingIgnoreCase` từ Spring JPA giúp tìm kiếm `ILIKE` với Database dễ dàng mà không cần viết Query thủ công).
5. Cuối cùng, Trả lại kết quả Response, hoặc Ném ra lỗi `ResourceNotFoundException` nếu người dùng can thiệp vào một Loại thiết bị không tồn tại, được Controller Advice chặn và convert qua HTTP 404 (Not Found) message json chuẩn.
