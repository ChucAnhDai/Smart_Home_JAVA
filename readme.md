
Đồ án: "Xây dựng hệ thống Website quản lý các thiết bị nhà thông minh".

Công nghệ dự kiến sử dụng:
- Backend: Java (Spring Boot)
- Database: MySQL (chạy bằng XAMPP)
- Frontend: HTML/CSS/JS (tôi đã có file UI_UX.html)
- Kiến trúc: RESTful API
- Có thể deploy sau khi hoàn thành hệ thống.

Thiết kế Cơ sở dữ liệu (Database ERD & Tables) (Tham khảo)

Các Bảng (Tables) Khuyến nghị:
    users
        id
        (PK), full_name, email, password, role (ENUM: 'ADMIN', 'MEMBER', 'GUEST'), status (Active/Inactive), last_active, created_at
    rooms
        id
        (PK), name (Living Room, Bedroom...), icon (emoji), created_at
    device_types
        id
        (PK), name (Light, Fan, AC, Camera, Lock, Climate...), icon
    devices
        id
        (PK), name (Ví dụ: Living Room Light), room_id (FK), type_id (FK), status (ENUM: 'ON', 'OFF'), is_online (Boolean), energy_kw (Float), last_active_time, created_at
    activity_logs
        id
        (PK), device_id (FK), event_type (Turned ON, Offline, Locked), description, created_at (Thời gian log)
    automation_rules
        id
        (PK), name (Ví dụ: Bedtime Routine), condition_text (JSON lưu điều kiện IF), action_text (JSON lưu hành động THEN), is_active (Boolean), created_at
    notifications
        id
        (PK), message, type (ENUM: 'INFO', 'WARNING', 'CRITICAL'), is_read (Boolean), created_at
    home_settings
        id
        (PK), home_name, timezone, dark_mode (Boolean), push_notif (Boolean)
    energy_monitoring
        id
        (PK), device_id (FK), energy_kw (Float), recorded_at

Yêu cầu:

- Chuẩn hóa dữ liệu
- Có khóa chính và khóa ngoại
- Thiết kế phù hợp cho hệ thống quản lý Smart Home
- Viết SQL tạo bảng MySQL
- Lưu ý hãy tạo file sql và tôi sẽ tự chạy thủ công trên xampp
Sử dụng MySQL chạy bằng XAMPP.

---------------------------------------------------------------------------------------
prompt 3

Hãy thiết kế cấu trúc project Spring Boot cho hệ thống Smart Home.

Công nghệ:

- Java Spring Boot
- MySQL (chạy bằng XAMPP)
- REST API
- Maven

Yêu cầu cấu trúc theo kiến trúc:

src/main/java/com/smarthome/nexus
│
├── NexusApplication.java         # Class khởi chạy ứng dụng
│
├── config/                       # Các File cấu hình hệ thống
│   ├── SecurityConfig.java       # Cấu hình Spring Security (JWT, CORS)
│   ├── AppConfig.java            # Cấu hình các Bean (RestTemplate, ModelMapper)
│   └── WebSocketConfig.java      # Nếu sử dụng realtime cho Notification
│
├── security/                     # Xử lý bảo mật JWT
│   ├── JwtTokenProvider.java 
│   ├── JwtAuthenticationFilter.java 
│   └── CustomUserDetails.java
│
├── controller/                   # Lớp API (Chỉ nhận Request và trả Response)
│   ├── AuthController.java
│   ├── DeviceController.java
│   ├── RoomController.java
│   └── AutomationController.java
│
├── service/                      # Lớp Business Logic (Xử lý nghiệp vụ chính)
│   ├── DeviceService.java        # Interface 
│   ├── RoomService.java
│   └── impl/
│       ├── DeviceServiceImpl.java # Class triển khai của Interface
│       └── RoomServiceImpl.java
│
├── repository/                   # Lớp Data Access (Tương tác Database)
│   ├── DeviceRepository.java     # extends JpaRepository
│   ├── UserRepository.java
│   └── RoomRepository.java
│
├── entity/                       # Các model ánh xạ với bảng Database (@Entity)
│   ├── User.java
│   ├── Device.java
│   ├── Room.java
│   └── ActivityLog.java
│
├── dto/                          # Đối tượng truyền đổi dữ liệu
│   ├── request/                  # Các class như CreateDeviceReq, LoginReq
│   └── response/                 # Các class như DeviceResDTO, RoomStatsDTO
│
├── exception/                    # Quản lý lỗi tập trung
│   ├── GlobalExceptionHandler.java (@ControllerAdvice xử lý mọi Exeption)
│   └── ResourceNotFoundException.java
│
└── util/                         # Cung cấp các hàm dùng chung (helper)
    ├── SecurityUtils.java
    └── DateTimeUtils.java


CI/CD là:

GitHub Actions


Workflow
1. Hoàn thiện Backend
2. Test API bằng Postman
3. Fix bug
4. Làm Frontend HTML/JS
5. Kết nối API
6. Test toàn hệ thống
7. Chuẩn bị demo


Thứ tự làm đồ án khi hoàn thành:

1. Test toàn bộ API bằng Postman
2. Fix bug backend
3. Viết Dashboard frontend
4. Viết Device management UI
5. Viết Energy monitoring UI
6. Demo realtime WebSocket


Dashboard UI

Hiển thị:

Total Devices
Devices Online
Devices ON
Devices OFF
Energy Consumption
Recent Activity


# Hướng Dẫn Cài Đặt và Sử Dụng - Nexus Smart Home

Chào mừng bạn đến với tài liệu hướng dẫn cài đặt và sử dụng hệ thống **Nexus Smart Home**. Tài liệu này sẽ giúp bạn hoặc những người khác setup dự án từ con số không, cách chạy web và cách khắc phục những lỗi thường gặp nhất.

---

## 1. Yêu Cầu Bắt Buộc (Prerequisites)
Trước khi cài đặt và chạy dự án, máy tính của bạn **bắt buộc** phải có các phần mềm sau:

1. **Java Development Kit (JDK 17 trở lên):** Hệ thống được xây dựng trên nền tảng Spring Boot 3.x, do đó yêu cầu tối thiểu là Java 17.
2. **Maven (3.8+):** Dùng để quản lý các thư viện (dependencies) và build dự án.
3. **MySQL Server (8.0 trở lên):** Dùng làm hệ quản trị cơ sở dữ liệu chính. Bạn bè có thể cài MySQL Workbench hoặc XAMPP (bật module MySQL) để dễ thao tác.
4. **Git:** (Tùy chọn) Để clone code từ repository về máy.
5. **Trình duyệt Web hiện đại:** Chrome, Edge, Firefox, hoặc Safari để sử dụng giao diện (UI).

---

## 2. Quá Trình Cài Đặt và Cấu Hình (Installation & Setup)

### Bước 2.1: Chuẩn bị Cơ Sở Dữ Liệu
1. Mở **MySQL (qua MySQL Workbench, phpMyAdmin của XAMPP, hoặc Command Line)**.
2. Tạo một database rỗng mang tên `nexus_smarthome`. Bạn có thể chạy nhanh lệnh sau:
   ```sql
   CREATE DATABASE nexus_smarthome CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. *(Tuỳ chọn)* Nếu bạn muốn có sẵn cấu trúc bảng và một vài dữ liệu mẫu, hãy import (chạy) file `nexus_smarthome.sql` nằm ngay tại thư mục gốc của project. Nếu không, Spring Boot (Hibernate) cũng sẽ tự tạo bảng tự động khi chạy code lần đầu.

### Bước 2.2: Cấu hình kết nối MySQL trong Project
1. Mở thư mục code của project bằng IDE (IntelliJ IDEA, Eclipse, hoặc VS Code).
2. Mở file cấu hình tại đường dẫn: `src/main/resources/application.properties`
3. Tìm đến các dòng sau và sửa lại cho đúng với thông tin **MySQL ở máy tính của bạn**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/nexus_smarthome?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=mat_khau_cua_ban
   ```
   *Lưu ý: Nếu dùng XAMPP mặc định thì `username=root` và `password=` (để nguyên trống).*

---

## 3. Cách Chạy Web (Run the Application)

Dự án này là khối thống nhất (Monolithic) bao gồm cả Backend (Spring Boot Java) lẫn Frontend HTML/CSS/JS thuần (nằm bên trong thư mục `static`), do đó bạn chỉ cần khởi động một mình Backend là chạy được toàn bộ trang Web.

### Có 2 cách để chạy:

**Cách 1: Chạy bằng Terminal / Command Prompt**
1. Mở Terminal / CMD tại thư mục gốc của project (nơi chứa file `pom.xml`).
2. Chạy lệnh:
   ```bash
   mvn clean install -DskipTests
   mvn spring-boot:run
   ```
3. Đợi Terminal báo dòng chữ `Started SmartHomeApplication in ... seconds` là thành công.

**Cách 2: Chạy trực tiếp từ IDE (IntelliJ / Eclipse)**
1. Tìm file `SmartHomeApplication.java` nằm tại đường dẫn `src/main/java/com/smarthome/nexus/SmartHomeApplication.java`.
2. Click chuột phải, chọn **Run 'SmartHomeApplication.main()'**.

### Sử Dụng Web
- Mở trình duyệt và truy cập vào địa chỉ: **[http://localhost:8081](http://localhost:8081)**
- Trang web sẽ tự điều hướng bạn vào màn hình Đăng Nhập (`login.html`) hoặc Dashboard (`index.html`).
- **Lưu ý Cổng (Port):** Code đang được cài đặt sẵn cổng **8081** (không phải 8080) nhằm tránh đụng độ với các project khác.

---

## 4. Cách Fix Các Lỗi Thường Gặp (Troubleshooting)

Trong quá trình cài đặt và chạy, bạn có thể gặp một số lỗi. Dưới đây là cách giải quyết:

### Lỗi 1: `Web server failed to start. Port 8081 was already in use.`
- **Nguyên nhân:** Có một phần mềm khác trên máy của bạn đang dùng cổng 8081.
- **Cách fix:** 
  1. Mở file `application.properties`.
  2. Tìm dòng `server.port=8081` và đổi thành một số khác, ví dụ: `server.port=8888`.
  3. Lưu lại và chạy lại code. Nhớ truy cập browser qua cổng mới `http://localhost:8888`.

### Lỗi 2: `Access denied for user 'root'@'localhost' (using password: NO/YES)`
- **Nguyên nhân:** Sai thông tin đăng nhập vào MySQL.
- **Cách fix:** Mở file `application.properties` và kiểm tra lại `spring.datasource.username` và `spring.datasource.password`. Hãy chắc chắn rằng bạn đã nhập đúng mật khẩu cơ sở dữ liệu cài trên máy bạn.

### Lỗi 3: Lệnh SQL ném Exception: `Unknown database 'nexus_smarthome'`
- **Nguyên nhân:** Bạn quên chưa tạo database trước khi chạy code.
- **Cách fix:** Hãy mở MySQL Server, tiến hành tạo một database mới mang tên exacy là `nexus_smarthome`, sau đó ấn chạy code lại.

### Lỗi 4: Giao diện Web trắng trơn, vỡ layout, hoặc API báo `401 Unauthorized` liên tục
- **Nguyên nhân:** Có thể do trình duyệt lưu cache file CSS/JS cũ.
- **Cách fix:** Ở trên trình duyệt, ấn tổ hợp phím **Ctrl + F5** (hoặc Cmd + Shift + R trên Mac) để Hard Load (Tải lại và xóa cache) trang web.
- Nếu bạn vừa xóa Database rồi chạy lại từ đầu, token JWT cũ ở trình duyệt sẽ bị lỗi hạn. Mở `F12 > Application > Local Storage` xoá mục `nexus_jwt_token` rồi F5 lại để quay về trang Login.

### Lỗi 5: Lỗi thiếu cột SQL `Field 'energy_kw' doesn't have a default value` khi hệ thống lưu Data điện năng
- **Nguyên nhân:** Database lưu vết cũ trong lúc Hibernate cấu hình auto-update.
- **Cách fix:** Bạn có thể Reset sạch sẽ DB. Vào MySQL thực hiện lệnh `DROP DATABASE nexus_smarthome;` sau đó Create lại từ đầu, đổi file properties thành `spring.jpa.hibernate.ddl-auto=create` (chạy xong 1 lần đổi lại thành `update` như cũ).

---

## 5. Lời Nét Khuyên Dùng
- Nếu chưa có tài khoản, hãy nhấn "Đăng ký". Tài khoản đầu tiên đăng ký cũng ở cấp độ MEMBER, tuy nhiên bạn có thể vào DB chỉnh tay thuộc tính `Role` thành `ADMIN` để trải nghiệm toàn bộ quyền Quản trị tối cao trên Dashboard!
- Để trải nghiệm sự Realtime (Thời gian thực), hãy mở trang web trên 2 cửa sổ/máy tính khác nhau, và dùng tài khoản cùng điều khiển một thiết bị, bạn sẽ thấy tab kia tự động cập nhật ngay lập tức mà không cần F5!
