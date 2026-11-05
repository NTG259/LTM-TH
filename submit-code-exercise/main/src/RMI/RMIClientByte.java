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
            Registry registry = LocateRegistry.getRegistry(serverIp, port);

            ByteService service = (ByteService) registry.lookup(serviceName);

            //Nhận mảng byte từ server
            byte[] rawData = service.requestData(studentCode, qCode);
            System.out.println("Received byte array length: " + (rawData != null ? rawData.length : 0));

            if (rawData == null || rawData.length == 0) {
                System.out.println("Empty data received.");
                return;
            }

            //Tìm phần tử xuất hiện ít nhất
            byte[] result = findLeastFrequentElement(rawData);
            System.out.println("Result: value = " + result[0] + ", count = " + result[1]);

            // Gửi kết quả về server
            service.submitData(studentCode, qCode, result);
            System.out.println("Submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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