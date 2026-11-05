package RMI;

import java.io.Serializable; // Interface đánh dấu object có thể serialize (chuyển thành bytes để truyền mạng)
import java.rmi.Remote;        // Marker interface để RMI nhận biết đây là remote interface
import java.rmi.RemoteException; // Checked exception bắt buộc cho mọi phương thức RMI

// "extends Remote" – bắt buộc để RMI tạo proxy stub cho interface này.
public interface ObjectService extends Remote {

    // Kiểu trả về Serializable (thay vì Object):
    //   RMI truyền dữ liệu qua mạng bằng cách serialize object thành byte stream.
    //   Khai báo Serializable thay vì Object đảm bảo tại compile-time rằng
    //   object trả về PHẢI implement Serializable – nếu không, sẽ có lỗi runtime
    //   java.io.NotSerializableException khi RMI cố truyền qua mạng.
    //   → Dùng Serializable an toàn hơn Object vì phát hiện lỗi sớm hơn.
    Serializable requestObject(String studentCode, String qCode) throws RemoteException;

    // Tham số Serializable: đảm bảo object gửi lên luôn serialize được.
    // void: không cần nhận lại giá trị từ server.
    // Lưu ý: không có "public" modifier – trong interface, mọi phương thức đều public
    //         theo mặc định; viết hay không viết "public" đều như nhau.
    void submitObject(String studentCode, String qCode, Serializable object) throws RemoteException;
}