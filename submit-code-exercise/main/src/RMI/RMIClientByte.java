package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

public class RMIClientByte {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "EsVsON2T";
        String serverIp = "36.50.135.242";
        int port = 1099; // Cổng mặc định của RMI Registry
        String serviceName = "RMIByteService";

        try {
            // Kết nối tới RMI Registry trên server
            Registry registry = LocateRegistry.getRegistry(serverIp, port);

            // Lookup đối tượng từ xa theo tên đã đăng ký
            ByteService service = (ByteService) registry.lookup(serviceName);

            // Bước a: Triệu gọi requestData để lấy mảng byte
            byte[] rawData = service.requestData(studentCode, qCode);
            System.out.println("Received byte array length: " + (rawData != null ? rawData.length : 0));

            if (rawData == null || rawData.length == 0) {
                System.out.println("Empty data received.");
                return;
            }

            // Bước b: Tìm phần tử có số lần xuất hiện ít nhất (ưu tiên phần tử gặp đầu tiên)
            byte[] result = findLeastFrequentElement(rawData);
            System.out.println("Result: value = " + result[0] + ", count = " + result[1]);

            // Bước c: Triệu gọi submitData để gửi kết quả trở lại server
            service.submitData(studentCode, qCode, result);
            System.out.println("Submitted successfully!");

            // Bước d: Kết thúc chương trình client
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dùng LinkedHashMap để đếm tần suất và giữ nguyên thứ tự xuất hiện ban đầu
     */
    private static byte[] findLeastFrequentElement(byte[] data) {
        Map<Byte, Integer> freqMap = new LinkedHashMap<>();

        for (byte b : data) {
            freqMap.put(b, freqMap.getOrDefault(b, 0) + 1);
        }

        byte minElem = data[0];
        int minCount = Integer.MAX_VALUE;

        // Duyệt theo thứ tự chèn của LinkedHashMap để đảm bảo ưu tiên phần tử xuất hiện trước
        for (Map.Entry<Byte, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() < minCount) {
                minCount = entry.getValue();
                minElem = entry.getKey();
            }
        }

        return new byte[]{minElem, (byte) minCount};
    }
}