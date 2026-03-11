# Module 11: Energy Monitoring (Quản lý theo dõi năng lượng)

## 1. Giới thiệu chức năng
Module **Energy Monitoring** được thiết kế để liên tục lưu trữ tình hình tiêu thụ điện năng (kW/h) của từng thiết bị trong nhà thông minh tại từng mốc thời gian cụ thể. Việc này giúp Frontend (UI) sau này móc nối dữ liệu để vẽ được Biểu đồ tiêu thụ điện (như Line Chart, Bar Chart) trực quan cho người nhà theo dõi.

## 2. API Endpoints (`/api/energy-monitoring`)
Module này chuyên dụng cho phân tích tĩnh, cung cấp duy nhất 1 điểm cực kỳ mạnh mẽ để trích xuất số liệu:

- `GET /api/energy-monitoring`:  
  Endpoint GET duy nhất, có khả năng lọc theo `deviceId`, khoảng ngày `startTime` đến `endTime` (theo ISO chuẩn 8601).
  *Trả về Page với default sorting (sắp xếp) là theo biến `recordedAt` DESC (Lấy thông báo gần nhất đo được trước).*

## 3. Kiến trúc nội bộ hệ thống Backend
Mặc dù UI Frontend / người tiêu dùng ngoài không gọi HTTP Endpoint nào để **Thêm năng lượng**, nhưng bên trong Layer Backend, API Service cung cấp một cánh cửa nhỏ nội bộ `recordEnergy(deviceId, kwh, time)`:

1. **EnergyMonitoring (Entity & Data)**: 
   Gắn liền với bảng `devices` thông qua `@ManyToOne` (Một thiết bị có hàng trăm mốc thời gian ghi nhận điện lượng khác nhau). Data được lưu tách biệt khỏi `Device` để chống bị chui ra rác khi gọi REST API.
   
2. **Tại sao không tạo POST API cho Energy Monitoring?**
   Thiết kế hệ thống thông minh, luồng dữ liệu điện sẽ được đẩy bằng luồng Realtime qua Mqtt/Socket (Ví dụ, phần cứng đo mạch tự bắn Server), Server chặn luồng bắn này và lưu database âm thầm dưới Backend chứ người dùng làm sao mà điền bằng tay được lượng điện tiêu thụ qua API POST? Do vậy ở Controller chỉ cung cấp quyền read, quyền Write nằm ẩn ở Service!
