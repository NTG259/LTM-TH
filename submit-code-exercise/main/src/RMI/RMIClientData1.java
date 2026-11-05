package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIClientData1 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "SKGbnG0k";
        String serverIp = "36.50.135.242";
        int port = 1099;
        String serviceName = "RMIDataService";

        try {
            // Bước a: Kết nối tới Registry Server và lookup service
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            DataService service = (DataService) registry.lookup(serviceName);

            // Bước b: Nhận dữ liệu mảng số nguyên từ server
            Object receivedObj = service.requestData(studentCode, qCode);
            System.out.println("Received: " + receivedObj);

            int[] arr = toIntArray(receivedObj);
            if (arr == null || arr.length == 0) {
                return;
            }

            // Bước c: Tìm vị trí các đỉnh cục bộ (chỉ số tính từ 1)
            List<Integer> peakPositions = findPeakPositions(arr);
            System.out.println("Peak positions (1-based): " + peakPositions);

            // Bước d: Gửi danh sách vị trí đỉnh cục bộ về server
            service.submitData(studentCode, qCode, peakPositions);
            System.out.println("Submitted successfully!");

            // Bước e: Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Xác định đỉnh cục bộ:
     * - Mảng 1 phần tử: chính là đỉnh cục bộ
     * - Phần tử đầu (i = 0): arr[0] > arr[1]
     * - Phần tử cuối (i = n - 1): arr[n - 1] > arr[n - 2]
     * - Phần tử ở giữa (0 < i < n - 1): arr[i] > arr[i - 1] && arr[i] > arr[i + 1]
     * Vị trí lưu vào kết quả là (i + 1)
     */
    private static List<Integer> findPeakPositions(int[] arr) {
        List<Integer> peaks = new ArrayList<>();
        int n = arr.length;

        if (n == 1) {
            peaks.add(1);
            return peaks;
        }

        for (int i = 0; i < n; i++) {
            if (i == 0) {
                if (arr[0] > arr[1]) {
                    peaks.add(1);
                }
            } else if (i == n - 1) {
                if (arr[n - 1] > arr[n - 2]) {
                    peaks.add(n);
                }
            } else {
                if (arr[i] > arr[i - 1] && arr[i] > arr[i + 1]) {
                    peaks.add(i + 1);
                }
            }
        }

        return peaks;
    }

    /**
     * Chuyển đổi Object nhận về thành mảng int[] chuẩn
     */
    @SuppressWarnings("unchecked")
    private static int[] toIntArray(Object obj) {
        if (obj instanceof int[]) {
            return (int[]) obj;
        } else if (obj instanceof Integer[]) {
            Integer[] boxed = (Integer[]) obj;
            int[] res = new int[boxed.length];
            for (int i = 0; i < boxed.length; i++) {
                res[i] = boxed[i];
            }
            return res;
        } else if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            int[] res = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                res[i] = (Integer) list.get(i);
            }
            return res;
        }
        return null;
    }
}