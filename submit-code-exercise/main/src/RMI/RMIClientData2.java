package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class RMIClientData2 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "kEPtoqWA";
        String serverIp = "36.50.135.242";
        int port = 1099;
        String serviceName = "RMIDataService";

        try {
            // Bước a: Kết nối Registry và lookup đối tượng RMIDataService
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            DataService service = (DataService) registry.lookup(serviceName);

            // Bước b: Triệu gọi requestData nhận chuỗi CSV các số thực
            Object rawObj = service.requestData(studentCode, qCode);
            System.out.println("Received: " + rawObj);

            if (rawObj == null) {
                return;
            }

            String csvData = rawObj.toString().trim();
            if (csvData.isEmpty()) {
                return;
            }

            // Parse chuỗi CSV thành danh sách Double
            String[] tokens = csvData.split(",");
            List<Double> numbers = new ArrayList<>();
            double sum = 0.0;

            for (String token : tokens) {
                double val = Double.parseDouble(token.trim());
                numbers.add(val);
                sum += val;
            }

            int n = numbers.size();

            // Bước c: Tính toán các chỉ số thống kê
            // 1. Giá trị trung bình (Average)
            double avg = sum / n;

            // 2. Độ lệch chuẩn trên toàn bộ dãy (Population Standard Deviation - chia cho N)
            double varianceSum = 0.0;
            for (double val : numbers) {
                varianceSum += Math.pow(val - avg, 2);
            }
            double stddev = Math.sqrt(varianceSum / n);

            // 3. Phân vị 95: sắp xếp tăng dần, vị trí ceil(n * 0.95) - 1
            Collections.sort(numbers);
            int p95Index = (int) Math.ceil(n * 0.95) - 1;
            // Đảm bảo chỉ số nằm trong phạm vi mảng hợp lệ
            if (p95Index < 0) p95Index = 0;
            if (p95Index >= n) p95Index = n - 1;
            double p95 = numbers.get(p95Index);

            // Bước d: Định dạng kết quả làm tròn 2 chữ số thập phân (dùng Locale.US để tránh dấu phẩy ",")
            String result = String.format(Locale.US, "average=%.2f;stddev=%.2f;p95=%.2f", avg, stddev, p95);
            System.out.println("Submit string: " + result);

            service.submitData(studentCode, qCode, result);
            System.out.println("Submitted successfully!");

            // Bước e: Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}