package DataType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UDPClient {
    public static void main(String[] args) {
        // 1. ĐỊNH DANH TRÊN MẠNG (IP & PORT)
        // Địa chỉ IP (Tầng Network - Layer 3): Định danh máy chủ đích trên mạng Internet (giống như địa chỉ tòa nhà).
        String serverHost = "36.50.135.242";
        // Cổng dịch vụ - Port (Tầng Transport - Layer 4): Định danh tiến trình/ứng dụng cụ thể đang chạy trên máy chủ đó (giống như số phòng trong tòa nhà).
        int serverPort = 2207;

        String studentCode = "B23DCCN259";
        String qCode = "02bzq4dt";

        // 2. KHỞI TẠO SOCKET UDP (DatagramSocket)
        // - UDP là giao thức PHI KẾT NỐI (Connectionless): Không có bước bắt tay 3 bước (3-way handshake) như TCP.
        // - DatagramSocket đóng vai trò là "hòm thư" hoặc "cổng giao tiếp" của phía Client để phóng và hứng các gói tin (Datagram).
        // - Khi gọi new DatagramSocket() không truyền tham số, Hệ điều hành sẽ tự động cấp phát cho Client một cổng tạm thời (Ephemeral Port - thường từ 49152 đến 65535) để nhận phản hồi từ Server.
        // - Đặt trong ngoặc tròn của try (...) là cú pháp try-with-resources: Tự động gọi hàm socket.close() khi chạy xong khối lệnh để giải phóng Port cho hệ điều hành, tránh rò rỉ tài nguyên (Resource Leak).
        try (DatagramSocket socket = new DatagramSocket()) {

            // 3. PHÂN GIẢI ĐỊA CHỈ (InetAddress)
            // - InetAddress đại diện cho một địa chỉ IP trong Java.
            // - getByName(serverHost): Nếu truyền vào tên miền (VD: "google.com"), hàm này sẽ gọi DNS để phân giải ra IP. Nếu truyền sẵn chuỗi IP, nó sẽ kiểm tra định dạng và đóng gói thành đối tượng InetAddress để Socket có thể hiểu được.
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // =========================================================================
            // a. GỬI THÔNG ĐIỆP LÊN SERVER
            // =========================================================================
            String sendMsg = ";" + studentCode + ";" + qCode;

            // - Dữ liệu truyền qua cáp mạng vật lý bắt buộc phải là dòng bit/byte thô (byte array).
            // - getBytes(StandardCharsets.UTF_8): Mã hóa (Encode) chuỗi ký tự sang mảng byte theo bảng mã UTF-8. Cần chỉ rõ UTF_8 để tránh lỗi lệch bảng mã mặc định giữa máy Client (VD: Windows dùng CP1252) và máy Server (Linux dùng UTF-8).
            byte[] sendData = sendMsg.getBytes(StandardCharsets.UTF_8);

            // 4. ĐÓNG GÓI BỨC THƯ GỬI ĐI (DatagramPacket - Chế độ Gửi)
            // - Khác với TCP (dùng luồng Stream nối thông 2 đầu nên chỉ cần đẩy dữ liệu vào luồng), UDP gửi từng gói tin rời rạc và độc lập.
            // - Do không có kết nối duy trì sẵn, MỌI gói tin DatagramPacket gửi đi đều bắt buộc phải ghi rõ địa chỉ người nhận (serverAddress) và cổng nhận (serverPort) ngay trên vỏ gói tin (Header).
            // - 4 tham số: (Mảng byte dữ liệu, Độ dài cần gửi, Địa chỉ IP đích, Port đích).
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

            // - Đẩy gói tin xuống tầng dưới của Hệ điều hành (OS Network Stack) để truyền đi.
            // - Lưu ý bản chất UDP: Hàm send() chỉ đảm bảo gói tin đã rời khỏi máy bạn, KHÔNG đảm bảo Server đã nhận được (không có cơ chế báo nhận ACK như TCP).
            socket.send(sendPacket);

            // =========================================================================
            // b. NHẬN THÔNG ĐIỆP TỪ SERVER
            // =========================================================================
            // 5. CHUẨN BỊ BỘ ĐỆM NHẬN (Buffer)
            // - Vì UDP đọc cả gói tin trong 1 lần (Message-oriented) chứ không đọc từng byte nối tiếp như TCP, ta phải cấp phát sẵn một mảng byte đủ lớn để "hứng" trọn gói tin từ Server.
            // - Kích thước 4096 bytes (4KB) là mức an toàn phổ biến cho các bài toán chuỗi văn bản (Kích thước tối đa lý thuyết của 1 gói UDP là 65,507 bytes, nhưng thực tế thường giới hạn dưới 1500 bytes theo chuẩn MTU của mạng Ethernet để tránh phân mảnh).
            byte[] receiveBuffer = new byte[4096];

            // 6. TẠO GÓI TIN RỖNG ĐỂ HỨNG DỮ LIỆU (DatagramPacket - Chế độ Nhận)
            // - Khởi tạo gói tin nhận chỉ cần 2 tham số: (Mảng byte trống để chứa dữ liệu, Độ dài tối đa cho phép ghi vào mảng).
            // - Khi gói tin từ Server bay tới, đối tượng receivePacket này không chỉ chứa dữ liệu (Payload) mà còn tự động lưu lại IP và Port của người gửi (có thể lấy ra bằng receivePacket.getAddress() và getPort()).
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            // 7. LẮNG NGHE VÀ NHẬN GÓI TIN (Blocking Call)
            // - socket.receive() là một hàm ĐỒNG BỘ CHẶN (Blocking Method): Chương trình sẽ "đứng hình" (tạm dừng luồng hiện tại) ở dòng này cho đến khi có một gói tin UDP bay vào đúng cổng của Socket.
            // - Lưu ý thực tế: Nếu gói tin gửi đi lúc nãy bị rớt mạng hoặc Server chết, hàm receive() sẽ đợi mãi mãi. Trong thực tế, người ta thường dùng thêm socket.setSoTimeout(5000) để giới hạn thời gian chờ (VD: quá 5 giây không thấy gì sẽ báo lỗi SocketTimeoutException).
            socket.receive(receivePacket);

            // 8. GIẢI MÃ DỮ LIỆU NHẬN ĐƯỢC (Byte Array -> String)
            // - receivePacket.getData(): Lấy lại mảng byte bộ đệm (có độ dài cố định 4096 byte).
            // - 0: Vị trí bắt đầu đọc (offset = 0, đọc từ byte đầu tiên).
            // - receivePacket.getLength(): CỰC KỲ QUAN TRỌNG! Hàm này trả về số byte THỰC TẾ mà Server đã gửi (ví dụ Server chỉ gửi 25 byte). Nếu không truyền getLength() mà biến cả mảng 4096 byte thành String, chuỗi sẽ bị dính 4071 ký tự rỗng ('\0' - null byte) ở đuôi, gây lỗi sai định dạng hoặc NumberFormatException khi ép kiểu số.
            String receivedMsg = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
            System.out.println("Nhận từ server: " + receivedMsg);

            // =========================================================================
            // XỬ LÝ LOGIC BÀI TOÁN (Tách chuỗi và tìm số còn thiếu)
            // =========================================================================
            String[] parts = receivedMsg.split(";");
            String requestId = parts[0];
            int n = Integer.parseInt(parts[1]);

            Set<Integer> existingNumbers = new HashSet<>();
            if (parts.length > 2 && !parts[2].isEmpty()) {
                String[] numStrings = parts[2].split(",");
                for (String numStr : numStrings) {
                    existingNumbers.add(Integer.parseInt(numStr.trim()));
                }
            }

            // c. Tìm kiếm các giá trị còn thiếu trong khoảng từ 1 đến n
            List<String> missingNumbers = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                if (!existingNumbers.contains(i)) {
                    missingNumbers.add(String.valueOf(i));
                }
            }

            // =========================================================================
            // c (tiếp). ĐÓNG GÓI KẾT QUẢ VÀ GỬI LẠI SERVER
            // =========================================================================
            String resultMsg = requestId + ";" + String.join(",", missingNumbers);
            System.out.println("Gửi lên server: " + resultMsg);

            // Chuyển chuỗi kết quả thành mảng byte
            byte[] resultData = resultMsg.getBytes(StandardCharsets.UTF_8);

            // Tiếp tục đóng gói vào một DatagramPacket mới.
            // Vì UDP không lưu trạng thái kết nối (Stateless), lần gửi thứ 2 này vẫn phải đính kèm lại serverAddress và serverPort y hệt lần gửi đầu tiên.
            DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, serverAddress, serverPort);

            // Bắn gói tin kết quả lên Server
            socket.send(resultPacket);

            // d. Kết thúc khối try, socket.close() được tự động kích hoạt để thu hồi Port.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}