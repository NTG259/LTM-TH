package String;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Client2 {
    public static void main(String[] args) {
        String serverHost = "36.50.135.242";
        int serverPort = 2208;
        String studentCode = "B23DCCN259";
        String qCode = "EGKOfWM5";

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // a. Gửi thông điệp chứa mã sinh viên và mã câu hỏi (;studentCode;qCode)
            String initMsg = ";" + studentCode + ";" + qCode;
            byte[] sendInitData = initMsg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket initPacket = new DatagramPacket(
                    sendInitData, sendInitData.length, serverAddress, serverPort
            );
            socket.send(initPacket);

            // b. Nhận thông điệp từ server theo định dạng: requestId;data
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String receivedMsg = new String(
                    receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8
            ).trim();
            System.out.println("Nhận từ Server: " + receivedMsg);

            // Tách requestId và data (tách thành 2 phần qua dấu ';' đầu tiên)
            String[] parts = receivedMsg.split(";", 2);
            String requestId = parts[0].trim();
            String data = parts.length > 1 ? parts[1].trim() : "";

            // c. Chuẩn hóa chuỗi: Chữ cái đầu mỗi từ in hoa, các chữ còn lại in thường
            String normalizedData = normalizeString(data);
            String responseMsg = requestId + ";" + normalizedData;
            System.out.println("Gửi lên Server: " + responseMsg);

            byte[] sendResultData = responseMsg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket resultPacket = new DatagramPacket(
                    sendResultData, sendResultData.length, serverAddress, serverPort
            );
            socket.send(resultPacket);

            // d. Đóng socket (tự động thực hiện nhờ khối try-with-resources)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Chuẩn hóa chuỗi: Ký tự đầu tiên của từng từ in hoa, các ký tự còn lại in thường,
     * các từ cách nhau đúng 1 khoảng trắng.
     */
    private static String normalizeString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "";
        }
        String[] words = input.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
