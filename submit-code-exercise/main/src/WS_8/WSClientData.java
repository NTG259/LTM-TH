package WS_8;

import java.net.URL;
import java.util.List;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class WSClientData {

    public static void main(String[] args) {
        // Điền mã câu hỏi thực tế trên đề thi của bạn vào biến qCode
        String studentCode = "B23DCCN259";
        String qCode = "ljmHXSKB";
        String serverIp = "36.50.135.242";
        String wsdlUrl = "http://" + serverIp + ":2221/DataService?wsdl";

        try {
            // Khởi tạo kết nối tới Web Service
            URL url = new URL(wsdlUrl);
            QName qname = new QName("http://soap.server.network.judge.dblab.ptit/", "DataService");
            Service service = Service.create(url, qname);
            DataService port = service.getPort(DataService.class);

            // Bước a: Triệu gọi getData để lấy mảng số nguyên
            List<Integer> numbers = port.getData(studentCode, qCode);
            System.out.println("Received list: " + numbers);

            if (numbers == null || numbers.isEmpty()) {
                System.out.println("Danh sach rong!");
                return;
            }

            // Bước b: Tính tổng các phần tử trong danh sách
            int sum = 0;
            for (Integer num : numbers) {
                sum += num;
            }
            System.out.println("Calculated sum: " + sum);

            // Bước c: Triệu gọi submitDataInt để gửi tổng trở lại server
            port.submitDataInt(studentCode, qCode, sum);
            System.out.println("Submitted successfully!");

            // Bước d: Chương trình tự động kết thúc.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
