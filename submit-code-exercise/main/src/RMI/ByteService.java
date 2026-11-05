package RMI;

import java.rmi.Remote;        // Marker interface – đánh dấu đây là remote interface
import java.rmi.RemoteException; // Checked exception bắt buộc cho mọi phương thức RMI

// "extends Remote" – bắt buộc để RMI runtime nhận ra interface này là remote,
// từ đó tạo stub (đại diện phía client) và skeleton (đại diện phía server) tự động.
public interface ByteService extends Remote {

    // Kiểu trả về byte[]:
    //   Server trả về dữ liệu nhị phân thô. Dùng byte[] thay vì String để tránh
    //   mất mát thông tin do charset conversion; RMI serialize byte[] nguyên vẹn qua mạng.
    // "throws RemoteException" – bắt buộc khai báo vì đây là checked exception;
    //   nếu thiếu, compiler báo lỗi. Mọi lời gọi qua mạng đều có thể ném exception này
    //   (mạng đứt, server crash, v.v.).
    public byte[] requestData(String studentCode, String qCode) throws RemoteException;

    // Kiểu trả về void:
    //   Phương thức chỉ gửi dữ liệu lên server, không cần nhận lại gì.
    //   void = "không có giá trị trả về". Lỗi nếu có vẫn báo qua RemoteException.
    public void submitData(String studentCode, String qCode, byte[] data) throws RemoteException;
}