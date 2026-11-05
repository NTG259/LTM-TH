package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.LinkedHashMap;
import java.util.Map;

public class RMIClientByte2 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "rw00Kica";
        String serverIp = "36.50.135.242";
        int port = 1099;
        String serviceName = "RMIByteService";

        try {
            // Kết nối Registry và lookup service
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            ByteService service = (ByteService) registry.lookup(serviceName);

            // Bước a: Nhận mảng dữ liệu byte[] từ server
            byte[] rawData = service.requestData(studentCode, qCode);
            System.out.println("Received bytes length: " + (rawData != null ? rawData.length : 0));

            if (rawData == null || rawData.length == 0) {
                return;
            }

            // Bước b: Tìm phần tử có tần suất xuất hiện nhiều nhất (ưu tiên gặp đầu tiên)
            byte[] result = findMostFrequentElement(rawData);
            System.out.println("Most frequent byte: " + result[0] + ", count: " + result[1]);

            // Bước c: Gửi mảng byte kết quả [phần tử, số lần xuất hiện]
            service.submitData(studentCode, qCode, result);
            System.out.println("Submitted data successfully.");

            // Bước d: Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] findMostFrequentElement(byte[] data) {
        // LinkedHashMap giữ nguyên thứ tự xuất hiện đầu tiên của các byte
        Map<Byte, Integer> freqMap = new LinkedHashMap<>();

        for (byte b : data) {
            freqMap.put(b, freqMap.getOrDefault(b, 0) + 1);
        }

        byte mostFreqByte = data[0];
        int maxCount = -1;

        // Dùng dấu '>' (không dùng '>=') để ưu tiên giữ lại phần tử xuất hiện trước
        for (Map.Entry<Byte, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFreqByte = entry.getKey();
            }
        }

        return new byte[]{mostFreqByte, (byte) maxCount};
    }
}