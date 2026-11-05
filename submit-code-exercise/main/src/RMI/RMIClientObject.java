package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientObject {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "jxbEuYDe";
        String serverIp = "36.50.135.242";
        int port = 1099;
        String serviceName = "RMIObjectService";

        try {
            // Bước a: Kết nối Registry Server và lookup service
            Registry registry = LocateRegistry.getRegistry(serverIp, port);
            ObjectService service = (ObjectService) registry.lookup(serviceName);

            // Bước b: Nhận đối tượng TicketSla từ server
            TicketSla ticket = (TicketSla) service.requestObject(studentCode, qCode);
            System.out.println("Received: " + ticket);

            if (ticket == null) {
                return;
            }

            // Bước c: Xử lý logic SLA
            String priority = ticket.getPriority();
            int hours = ticket.getOpenedHoursAgo();
            boolean isBreached = false;

            if (priority != null) {
                switch (priority.trim().toUpperCase()) {
                    case "CRITICAL":
                        isBreached = (hours > 2);
                        break;
                    case "HIGH":
                        isBreached = (hours > 8);
                        break;
                    case "MEDIUM":
                        isBreached = (hours > 24);
                        break;
                    case "LOW":
                        isBreached = (hours > 72);
                        break;
                    default:
                        break;
                }
            }

            ticket.setBreached(isBreached);

            // Xác định action
            if (!isBreached) {
                ticket.setAction("MONITOR");
            } else {
                if ("CRITICAL".equalsIgnoreCase(priority) || hours > 96) {
                    ticket.setAction("ESCALATE_L2");
                } else {
                    ticket.setAction("ESCALATE_L1");
                }
            }

            System.out.println("Updated: " + ticket);

            // Bước d: Gửi lại đối tượng đã cập nhật
            service.submitObject(studentCode, qCode, ticket);
            System.out.println("Submitted successfully!");

            // Bước e: Kết thúc chương trình
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}