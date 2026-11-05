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
            // LocateRegistry.getRegistry – trả về Registry stub trỏ tới server.
            Registry registry = LocateRegistry.getRegistry(serverIp, port);

            // Ép kiểu Remote → DataService để gọi requestData/submitData.
            DataService service = (DataService) registry.lookup(serviceName);

            // Bước b: Nhận dữ liệu từ server
            // Kiểu Object: DataService.requestData trả về Object (có thể là String CSV,
            //   hay kiểu khác tùy server). Dùng Object vì interface không biết trước kiểu cụ thể.
            Object rawObj = service.requestData(studentCode, qCode);
            System.out.println("Received: " + rawObj);

            // rawObj.toString() – gọi phương thức toString() của Object để lấy String,
            //   an toàn hơn ép kiểu (String) rawObj vì không ném ClassCastException.
            String csvData = rawObj.toString().trim();

            // String.split(",") – trả về String[] (mảng chuỗi), chia theo dấu phẩy.
            String[] tokens = csvData.split(",");

            // List<Double> thay vì double[] vì số lượng phần tử chưa biết trước;
            //   Double là wrapper (object) vì List generic không nhận double nguyên thủy.
            List<Double> numbers = new ArrayList<>();
            double sum = 0.0;

            for (String token : tokens) {
                // Double.parseDouble(String) – static method, trả về double (nguyên thủy).
                double val = Double.parseDouble(token.trim());
                numbers.add(val); // auto-boxing: double → Double để thêm vào List<Double>
                sum += val;
            }

            int n = numbers.size();

            // Bước c: Tính toán các chỉ số thống kê
            // Các biến double: dùng kiểu dấu phẩy động vì kết quả có phần thập phân.
            double avg = sum / n;

            double varianceSum = 0.0;
            for (double val : numbers) {
                // Math.pow(x, 2) – trả về double; Math.sqrt – trả về double.
                varianceSum += Math.pow(val - avg, 2);
            }
            double stddev = Math.sqrt(varianceSum / n);

            Collections.sort(numbers); // sắp xếp tại chỗ; trả về void.
            // Math.ceil trả về double → ép kiểu (int) để dùng làm chỉ số mảng.
            int p95Index = (int) Math.ceil(n * 0.95) - 1;
            if (p95Index < 0) p95Index = 0;
            if (p95Index >= n) p95Index = n - 1;
            double p95 = numbers.get(p95Index); // auto-unboxing: Double → double

            // Bước d: Định dạng kết quả
            // String.format trả về String: %.2f = số thực làm tròn 2 chữ số thập phân.
            // Locale.US: đảm bảo dùng "." làm dấu thập phân (không phải "," như tiếng Việt/Đức).
            String result = String.format(Locale.US, "average=%.2f;stddev=%.2f;p95=%.2f", avg, stddev, p95);
            System.out.println("Submit string: " + result);

            // submitData nhận Object → String tương thích vì String là con của Object.
            service.submitData(studentCode, qCode, result);
            System.out.println("Submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}