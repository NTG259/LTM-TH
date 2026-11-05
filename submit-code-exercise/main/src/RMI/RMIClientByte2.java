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
            Registry registry = LocateRegistry.getRegistry(serverIp, port);

            ByteService service = (ByteService) registry.lookup(serviceName);

            //Nhận mảng byte từ server
            byte[] rawData = service.requestData(studentCode, qCode);
            System.out.println("Received bytes length: " + (rawData != null ? rawData.length : 0));

            if (rawData == null || rawData.length == 0) {
                return;
            }

            byte[] result = findMostFrequentElement(rawData);
            System.out.println("Most frequent byte: " + result[0] + ", count: " + result[1]);

            // Gửi kết quả về server;
            service.submitData(studentCode, qCode, result);
            System.out.println("Submitted data successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nhận byte[] (dữ liệu gốc), trả về byte[] (2 phần tử).
    //   Trả về byte[] để có thể truyền trực tiếp vào submitData(byte[]) mà không cần chuyển đổi.
    private static byte[] findMostFrequentElement(byte[] data) {
        Map<Byte, Integer> freqMap = new LinkedHashMap<>();

        for (byte b : data) {
            freqMap.put(b, freqMap.getOrDefault(b, 0) + 1);
        }

        byte mostFreqByte = data[0];
        int maxCount = -1;

        for (Map.Entry<Byte, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                mostFreqByte = entry.getKey();
            }
        }

        byte[] result = new byte[2];
        result[0] = mostFreqByte;
        result[1] = (byte) maxCount;
        return result;
    }
}