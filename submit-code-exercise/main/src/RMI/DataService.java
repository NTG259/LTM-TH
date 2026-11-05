package RMI;

import java.rmi.Remote;        // Marker interface để RMI nhận biết đây là remote interface
import java.rmi.RemoteException; // Checked exception bắt buộc cho mọi phương thức RMI

// "extends Remote" – bắt buộc để RMI tạo proxy stub cho interface này.
public interface DataService extends Remote {

    // Kiểu trả về Object:
    //   Server có thể trả về nhiều loại dữ liệu khác nhau tùy câu hỏi:
    //   int[], List<Integer>, String, v.v. Dùng Object là kiểu cha của tất cả
    //   để interface dùng được cho mọi bài.
    //   Nhược điểm: phía client phải tự ép kiểu (cast) sau khi nhận về.
    //   Object implement Serializable? – KHÔNG tự động, nhưng các kiểu con cụ thể
    //   (int[], ArrayList, String) đều implement Serializable, nên RMI vẫn truyền được.
    public Object requestData(String studentCode, String qCode) throws RemoteException;

    // Kiểu tham số Object:
    //   Tương tự, kết quả gửi lên cũng có thể là nhiều kiểu khác nhau tùy bài.
    //   Dùng Object để interface không phải viết nhiều phiên bản cho từng kiểu.
    // Kiểu trả về void: chỉ gửi dữ liệu, không nhận lại.
    public void submitData(String studentCode, String qCode, Object data) throws RemoteException;
}