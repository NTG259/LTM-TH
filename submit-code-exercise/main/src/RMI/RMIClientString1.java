package RMI;

import java.net.URI;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientString1 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "Qxj2RXus";

        try {
            // Kết nối trực tiếp và lookup service
            Registry registry = LocateRegistry.getRegistry("36.50.135.242", 1099);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // Nhận chuỗi từ server
            String rawString = service.requestCharacter(studentCode, qCode);

            System.out.println("Received: " + rawString);

            // Mã hóa URL nguyên khối một dòng duy nhất theo đúng gợi ý của đề
            String encodedString = new URI(null, null, rawString, null).toASCIIString();
            System.out.println("Encoded : " + encodedString);

            // Gửi chuỗi kết quả về server
            service.submitCharacter(studentCode, qCode, encodedString);
            System.out.println("Submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}