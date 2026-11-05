package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientString2 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "a8pksc24";
        String serverIp = "36.50.135.242";
        int port = 1099;
        String serviceName = "RMICharacterService";

        try {
            // Kết nối Registry và lookup service
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            CharacterService service = (CharacterService) registry.lookup(serviceName);

            // Bước a: Nhận chuỗi số thập phân từ server
            String rawInput = service.requestCharacter(studentCode, qCode);
            System.out.println("Received: " + rawInput);

            if (rawInput == null || rawInput.trim().isEmpty()) {
                return;
            }

            // Parse số nguyên từ chuỗi (loại bỏ khoảng trắng thừa nếu có)
            int num = Integer.parseInt(rawInput.trim());

            // Bước b: Chuyển đổi sang số La Mã
            String roman = intToRoman(num);
            System.out.println("Roman: " + roman);

            // Bước c: Gửi chuỗi kết quả về server
            service.submitCharacter(studentCode, qCode, roman);
            System.out.println("Submitted successfully!");

            // Bước d: Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Thuật toán Greedy chuyển đổi số nguyên thành số La Mã
     */
    private static String intToRoman(int num) {
        int[] values = {
                1000, 900, 500, 400,
                100, 90, 50, 40,
                10, 9, 5, 4,
                1
        };

        String[] symbols = {
                "M", "CM", "D", "CD",
                "C", "XC", "L", "XL",
                "X", "IX", "V", "IV",
                "I"
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length && num > 0; i++) {
            while (num >= values[i]) {
                num -= values[i];
                sb.append(symbols[i]);
            }
        }
        return sb.toString();
    }
}