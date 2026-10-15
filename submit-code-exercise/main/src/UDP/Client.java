package UDP;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    public static void main(String[] args) {
        String serverHost = "36.50.135.242"; // Thay bằng địa chỉ IP của Server khi thi/chấm bài
        int serverPort = 2209;
        String studentCode = "B23DCCN259"; // Thay bằng mã sinh viên của bạn
        String qCode = "J97SoEYS";         // Thay bằng mã câu hỏi của bạn

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // a. Gửi thông điệp chứa mã sinh viên và mã câu hỏi (;studentCode;qCode)
            String initMsg = ";" + studentCode + ";" + qCode;
            byte[] sendInitData = initMsg.getBytes();
            DatagramPacket initPacket = new DatagramPacket(
                    sendInitData, sendInitData.length, serverAddress, serverPort
            );
            socket.send(initPacket);

            // b. Nhận thông điệp từ server (08 byte đầu là requestId, phần còn lại là Object Student)
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            // Tách 8 byte đầu lấy requestId
            byte[] requestIdBytes = new byte[8];
            System.arraycopy(receivePacket.getData(), 0, requestIdBytes, 0, 8);
            String requestId = new String(requestIdBytes);

            // Tách các byte còn lại để đọc đối tượng Student
            int objectLength = receivePacket.getLength() - 8;
            ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, objectLength);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Student student = (Student) ois.readObject();

            // c. Chuẩn hóa tên và tạo email
            String rawName = student.getName();
            if (rawName != null && !rawName.trim().isEmpty()) {
                student.setName(formatName(rawName));
                student.setEmail(generateEmail(rawName));
            }

            // Đóng gói lại: 8 byte đầu là requestId + các byte tiếp theo là đối tượng Student
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(student);
            oos.flush();
            byte[] objectBytes = baos.toByteArray();

            byte[] finalSendData = new byte[8 + objectBytes.length];
            System.arraycopy(requestIdBytes, 0, finalSendData, 0, 8);
            System.arraycopy(objectBytes, 0, finalSendData, 8, objectBytes.length);

            // Gửi lại lên Server
            DatagramPacket sendFinalPacket = new DatagramPacket(
                    finalSendData, finalSendData.length, serverAddress, serverPort
            );
            socket.send(sendFinalPacket);

            // d. Socket tự động đóng nhờ cấu trúc try-with-resources
            System.out.println("Đã xử lý và gửi thành công: " + student);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Chuẩn hóa tên: Chữ cái đầu mỗi từ viết hoa, các chữ còn lại viết thường
     */
    private static String formatName(String rawName) {
        String[] words = rawName.trim().split("\\s+");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                formattedName.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return formattedName.toString().trim();
    }

    /**
     * Tạo email theo quy tắc: ten + chu_cai_dau_ho_va_ten_dem + @ptit.edu.vn
     * Ví dụ: "nguyen van tuan nam" -> "namnvt@ptit.edu.vn"
     */
    private static String generateEmail(String rawName) {
        String[] words = rawName.trim().toLowerCase().split("\\s+");
        if (words.length == 0) return "";

        StringBuilder email = new StringBuilder();
        // Lấy phần tên (từ cuối cùng)
        email.append(words[words.length - 1]);

        // Ghép chữ cái đầu của họ và tên đệm (các từ đứng trước từ cuối)
        for (int i = 0; i < words.length - 1; i++) {
            if (!words[i].isEmpty()) {
                email.append(words[i].charAt(0));
            }
        }
        email.append("@ptit.edu.vn");
        return email.toString();
    }
}