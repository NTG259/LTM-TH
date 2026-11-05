package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class RMIClientData1 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "SKGbnG0k";

        try {
            Registry registry = LocateRegistry.getRegistry("36.50.135.242", 1099);
            DataService service = (DataService) registry.lookup("RMIDataService");

            // Ép kiểu trực tiếp sang int[]
            int[] data = (int[]) service.requestData(studentCode, qCode);

            List<Integer> peaks = new ArrayList<>();
            int n = data.length;

            // Tìm đỉnh cục bộ (rút gọn điều kiện biên vào chung 1 lệnh if)
            for (int i = 0; i < n; i++) {
                if ((i == 0 || data[i] > data[i - 1]) &&
                        (i == n - 1 || data[i] > data[i + 1])) {
                    peaks.add(i + 1);
                }
            }

            System.out.println("Peak positions (1-based): " + peaks);

            // Gửi kết quả về server (gửi ArrayList vì List implements Serializable)
            service.submitData(studentCode, qCode, peaks);
            System.out.println("Submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}