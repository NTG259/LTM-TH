package RMI;

import java.rmi.Remote;        // Marker interface để RMI nhận biết đây là remote interface
import java.rmi.RemoteException; // Checked exception bắt buộc cho mọi phương thức RMI

// "extends Remote" – khai báo đây là remote interface.
// Interface thuần Java không cần extends Remote; chỉ interface dùng trong RMI mới cần.
public interface CharacterService extends Remote {

    // Kiểu trả về String:
    //   Server trả về dữ liệu dạng văn bản (chuỗi ký tự).
    //   String implement Serializable sẵn, nên RMI có thể truyền qua mạng trực tiếp
    //   mà không cần cài đặt thêm gì.
    // "throws RemoteException" – checked exception, bắt buộc phải khai báo trong interface
    //   để phía client biết phải xử lý (try-catch hoặc throws tiếp).
    public String requestCharacter(String studentCode, String qCode) throws RemoteException;

    // Kiểu trả về void:
    //   Chỉ gửi kết quả lên server, không nhận lại giá trị.
    //   Tham số strSubmit là String vì kết quả xử lý cũng là văn bản.
    public void submitCharacter(String studentCode, String qCode, String strSubmit) throws RemoteException;
}