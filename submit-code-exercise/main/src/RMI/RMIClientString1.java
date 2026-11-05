package RMI;

import java.net.URI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientString1 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "Qxj2RXus";
        String serverIp = "36.50.135.242";
        int port = 1099;
        String serviceName = "RMICharacterService";

        try {
            // Kết nối Registry và lookup service
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            CharacterService service = (CharacterService) registry.lookup(serviceName);

            // Bước a: Nhận chuỗi từ server
            String rawString = service.requestCharacter(studentCode, qCode);
            System.out.println("Received: " + rawString);

            if (rawString == null) {
                return;
            }

            // Bước b: Mã hóa URL theo chuẩn URI path
            String encodedString = encodeUriPath(rawString);
            System.out.println("Encoded : " + encodedString);

            // Bước c: Gửi chuỗi kết quả về server
            service.submitCharacter(studentCode, qCode, encodedString);
            System.out.println("Submitted successfully!");

            // Bước d: Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Mã hóa URL theo đúng quy tắc URI path:
     * - Giữ nguyên: a-z, A-Z, 0-9 và _ . ~ ! * ' ( ) ; : @ & = + $ , /
     * - Mã hóa thành %XX (UTF-8): khoảng trắng, ký tự ngoài ASCII, và %, ", <, >, [, ], {, }, |, \, ^, ?, #
     */
    private static String encodeUriPath(String s) throws Exception {
        // Dùng constructor URI 4 tham số: new URI(scheme, authority, path, fragment)
        // Khi scheme=null, authority=null, fragment=null, URI sẽ tự động escape path thành dạng hợp lệ
        // Tuy nhiên '?' và '#' trong path có thể bị xem là phân tách query/fragment nếu không tiền xử lý
        // Cách chuẩn và an toàn nhất là duyệt từng ký tự/byte hoặc tận dụng URI:
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '?') {
                sb.append("%3F");
            } else if (c == '#') {
                sb.append("%23");
            } else if (c == '%') {
                sb.append("%25");
            } else {
                // Tận dụng URI constructor để escape các ký tự còn lại chuẩn xác
                URI uri = new URI(null, null, String.valueOf(c), null);
                sb.append(uri.toASCIIString());
            }
        }
        return sb.toString();
    }
}