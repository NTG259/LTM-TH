package RMI;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClientObject {

    public static void main(String[] args) {
        String studentCode = "B23DCCN259";
        String qCode = "jxbEuYDe";

        try {
            Registry registry = LocateRegistry.getRegistry("36.50.135.242", 1099);
            ObjectService service = (ObjectService) registry.lookup("RMIObjectService");

            TicketSla ticket = (TicketSla) service.requestObject(studentCode, qCode);
            if (ticket == null) {
                return;
            }

            String priority = ticket.getPriority();
            int hours = ticket.getOpenedHoursAgo();
            boolean isBreached = false;

            if ("CRITICAL".equalsIgnoreCase(priority)) {
                isBreached = hours > 2;
            } else if ("HIGH".equalsIgnoreCase(priority)) {
                isBreached = hours > 8;
            } else if ("MEDIUM".equalsIgnoreCase(priority)) {
                isBreached = hours > 24;
            } else if ("LOW".equalsIgnoreCase(priority)) {
                isBreached = hours > 72;
            }

            ticket.setBreached(isBreached);

            // Thiết lập action
            if (!isBreached) {
                ticket.setAction("MONITOR");
            } else if ("CRITICAL".equalsIgnoreCase(priority) || hours > 96) {
                ticket.setAction("ESCALATE_L2");
            } else {
                ticket.setAction("ESCALATE_L1");
            }

            System.out.println("Updated: " + ticket);

            // Gửi lại object đã cập nhật
            service.submitObject(studentCode, qCode, ticket);
            System.out.println("Submitted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}