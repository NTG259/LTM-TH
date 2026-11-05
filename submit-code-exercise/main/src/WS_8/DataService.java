package WS_8;

import java.util.List;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService(targetNamespace = "http://soap.server.network.judge.dblab.ptit/")
public interface DataService {

    @WebMethod
    List<Integer> getData(
            @WebParam(name = "studentCode") String studentCode,
            @WebParam(name = "qCode") String qCode
    );

    @WebMethod
    void submitDataInt(
            @WebParam(name = "studentCode") String studentCode,
            @WebParam(name = "qCode") String qCode,
            @WebParam(name = "data") int data
    );
}
