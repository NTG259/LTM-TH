package DataType;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args) {
        String serverHost = "36.50.135.242";
        int serverPort = 2207;
        String studentCode = "B23DCCN259";
        String qCode = "sq76f9hS";

        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress serverAddress = InetAddress.getByName(serverHost);

            // a. Gửi thông điệp chứa mã sinh viên và mã câu hỏi (;studentCode;qCode)
            String initMsg = ";" + studentCode + ";" + qCode;
            byte[] sendInitData = initMsg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket initPacket = new DatagramPacket(
                    sendInitData, sendInitData.length, serverAddress, serverPort
            );
            socket.send(initPacket);

            // b. Nhận thông điệp từ server theo định dạng: requestId;a1,a2,...,a50
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            String receivedMsg = new String(
                    receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8
            ).trim();
            System.out.println("Nhận từ Server: " + receivedMsg);

            // Tách requestId và chuỗi các số nguyên
            String[] parts = receivedMsg.split(";", 2);
            String requestId = parts[0].trim();
            String numbersPart = parts.length > 1 ? parts[1].trim() : "";

            // c. Tìm giá trị lớn nhất (max) và nhỏ nhất (min)
            String[] numStrings = numbersPart.split(",");
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;

            for (String numStr : numStrings) {
                if (!numStr.trim().isEmpty()) {
                    int val = Integer.parseInt(numStr.trim());
                    if (val > max) {
                        max = val;
                    }
                    if (val < min) {
                        min = val;
                    }
                }
            }

            // Gửi kết quả lên server theo định dạng: requestId;max,min
            String responseMsg = requestId + ";" + max + "," + min;
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
}