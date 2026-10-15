package String;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class Client {
    public static void main(String[] args) {
        String serverHost = "36.50.135.242";
        int serverPort = 2208;
        String studentCode = "B23DCCN259";
        String qCode = "XKRyHaIX";

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

            // Tách requestId và data (tách làm 2 phần qua dấu ';' đầu tiên)
            String[] parts = receivedMsg.split(";", 2);
            String requestId = parts[0].trim();
            String data = parts.length > 1 ? parts[1].trim() : "";

            // c. Tìm ký tự xuất hiện nhiều nhất và các vị trí của nó
            String processedResult = findMostFrequentCharAndPositions(data);
            String responseMsg = requestId + ";" + processedResult;
            System.out.println("Gửi lên Server: " + responseMsg);

            byte[] sendResultData = responseMsg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket resultPacket = new DatagramPacket(
                    sendResultData, sendResultData.length, serverAddress, serverPort
            );
            socket.send(resultPacket);

            // d. Socket tự động đóng nhờ khối try-with-resources
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Tìm ký tự xuất hiện nhiều nhất (ưu tiên ký tự xuất hiện trước nếu bằng nhau)
     * và trả về định dạng: "ký_tự:vị_trí_1,vị_trí_2," (vị trí tính từ 1)
     */
    private static String findMostFrequentCharAndPositions(String data) {
        // Dùng LinkedHashMap để giữ nguyên thứ tự xuất hiện đầu tiên của các ký tự
        Map<Character, Integer> freqMap = new LinkedHashMap<>();
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        char maxChar = 0;
        int maxCount = -1;
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            // Dùng dấu '>' (không dùng '>=') để giữ lại ký tự xuất hiện đầu tiên khi có cùng tần suất
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxChar = entry.getKey();
            }
        }

        // Tìm tất cả các vị trí (bắt đầu từ 1) của maxChar
        StringBuilder sb = new StringBuilder();
        sb.append(maxChar).append(":");
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == maxChar) {
                sb.append(i + 1).append(",");
            }
        }

        return sb.toString();
    }
}