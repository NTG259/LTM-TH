package RMI;

import java.io.Serializable; // Interface đánh dấu để JVM cho phép serialize object này

// "implements Serializable" – bắt buộc nếu muốn truyền object qua RMI.
// RMI dùng ObjectOutputStream để chuyển object thành byte stream gửi qua socket;
// nếu class không implements Serializable thì ném NotSerializableException.
public class TicketSla implements Serializable {

    // serialVersionUID – định danh phiên bản class khi serialize/deserialize.
    // Nếu thiếu, JVM tự tạo ngẫu nhiên mỗi lần biên dịch → hai đầu mạng dùng
    // phiên bản class khác nhau sẽ ném InvalidClassException.
    // "private static final long" là kiểu cố định theo spec Java Serialization.
    private static final long serialVersionUID = 20260517L;

    // Các field private: không để RMI truy cập trực tiếp; chỉ truy cập qua getter/setter.
    private String ticketId;
    private String priority;      // LOW, MEDIUM, HIGH, CRITICAL
    private int openedHoursAgo;
    private boolean breached;
    private String action;

    // Constructor không tham số (no-arg constructor):
    //   Bắt buộc phải có khi class implement Serializable + dùng trong một số
    //   framework/thư viện deserialize. Java không tự thêm nếu đã khai báo constructor có tham số.
    public TicketSla() {
    }

    // Constructor tiện ích để tạo object nhanh với 3 field ban đầu.
    // "this.xxx = xxx" – phân biệt field (this.ticketId) với tham số cùng tên (ticketId).
    public TicketSla(String ticketId, String priority, int openedHoursAgo) {
        this.ticketId = ticketId;
        this.priority = priority;
        this.openedHoursAgo = openedHoursAgo;
    }

    // --- Getter/Setter ---
    // Getter: trả về kiểu dữ liệu của field → client đọc giá trị.
    // Setter: nhận tham số cùng kiểu, gán vào field → client cập nhật giá trị.

    // Trả về String vì ticketId là chuỗi văn bản.
    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    // Trả về String vì priority là nhãn văn bản ("LOW", "HIGH", v.v.).
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    // Trả về int vì số giờ là số nguyên, không cần phần thập phân.
    public int getOpenedHoursAgo() {
        return openedHoursAgo;
    }

    public void setOpenedHoursAgo(int openedHoursAgo) {
        this.openedHoursAgo = openedHoursAgo;
    }

    // Getter của boolean: quy ước đặt tên "isXxx" thay vì "getXxx" (chuẩn JavaBeans).
    // Trả về boolean (kiểu nguyên thủy) chứ không phải Boolean (wrapper) để tránh NullPointerException.
    public boolean isBreached() {
        return breached;
    }

    public void setBreached(boolean breached) {
        this.breached = breached;
    }

    // Trả về String vì action là nhãn văn bản ("MONITOR", "ESCALATE_L1", v.v.).
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    // @Override – annotation báo compiler rằng phương thức này ghi đè phương thức
    //   toString() của Object. Nếu tên sai chính tả, compiler báo lỗi thay vì tạo method mới.
    // Trả về String vì toString() luôn trả về chuỗi mô tả object (quy ước Java).
    @Override
    public String toString() {
        return "TicketSla{" +
                "ticketId='" + ticketId + '\'' +
                ", priority='" + priority + '\'' +
                ", openedHoursAgo=" + openedHoursAgo +
                ", breached=" + breached +
                ", action='" + action + '\'' +
                '}';
    }
}