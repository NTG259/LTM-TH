package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientString2 {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "a8pksc24";

        try {
            // Kết nối Registry và lookup service
            Registry registry = LocateRegistry.getRegistry("36.50.135.242", 1099);
            CharacterService service = (CharacterService) registry.lookup("RMICharacterService");

            // Nhận chuỗi số thập phân từ server
            String rawInput = service.requestCharacter(studentCode, qCode);

            System.out.println("Received: " + rawInput);

            // Chuyển chuỗi thành số và gọi hàm tính toán
            int num = Integer.parseInt(rawInput.trim());
            String roman = intToRoman(num);
            System.out.println("Roman: " + roman);

            // Gửi kết quả về server
            service.submitCharacter(studentCode, qCode, roman);
            System.out.println("Submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String intToRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

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