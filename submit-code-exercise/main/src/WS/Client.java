package WS;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Client {

    static final String ENDPOINT = "http://36.50.135.242:2221/DataService";
    static final String STUDENT_CODE = "B23DCCN259";
    static final String Q_CODE = "HVFWqBE0";

    public static void main(String[] args) throws Exception {
        // a. Triệu gọi getData
        List<Integer> numbers = getData(STUDENT_CODE, Q_CODE);
        System.out.println("Received: " + numbers);

        // b. Tìm số lớn nhất có thể ghép được
        String largest = findLargestNumber(numbers);
        System.out.println("Largest number: " + largest);

        // c. Gửi kết quả về server
        submitDataString(STUDENT_CODE, Q_CODE, largest);
        System.out.println("Result submitted successfully.");

        // d. Kết thúc
    }

    private static List<Integer> getData(String studentCode, String qCode) throws Exception {
        String soap =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                             "xmlns:tns=\"http://soap.server.network.judge.dblab.ptit/\">" +
              "<soapenv:Header/>" +
              "<soapenv:Body>" +
                "<tns:getData>" +
                  "<studentCode>" + studentCode + "</studentCode>" +
                  "<qCode>" + qCode + "</qCode>" +
                "</tns:getData>" +
              "</soapenv:Body>" +
            "</soapenv:Envelope>";

        String response = sendSoap(soap);
        System.out.println("Raw response: " + response);
        return parseIntList(response);
    }

    private static void submitDataString(String studentCode, String qCode, String data) throws Exception {
        String soap =
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                             "xmlns:tns=\"http://soap.server.network.judge.dblab.ptit/\">" +
              "<soapenv:Header/>" +
              "<soapenv:Body>" +
                "<tns:submitDataString>" +
                  "<studentCode>" + studentCode + "</studentCode>" +
                  "<qCode>" + qCode + "</qCode>" +
                  "<data>" + data + "</data>" +
                "</tns:submitDataString>" +
              "</soapenv:Body>" +
            "</soapenv:Envelope>";

        String response = sendSoap(soap);
        System.out.println("Submit response: " + response);
    }

    private static String sendSoap(String soapEnvelope) throws Exception {
        URL url = new URL(ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        conn.setRequestProperty("SOAPAction", "\"\"");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(soapEnvelope.getBytes("UTF-8"));
        }

        int status = conn.getResponseCode();
        InputStream is = (status == 200) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    private static List<Integer> parseIntList(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));

        List<Integer> result = new ArrayList<>();
        // JAX-WS thường wrap từng phần tử trong tag <return>
        for (String tag : new String[]{"return", "item", "arg0"}) {
            NodeList nodes = doc.getElementsByTagName(tag);
            if (nodes.getLength() > 0) {
                for (int i = 0; i < nodes.getLength(); i++) {
                    String text = nodes.item(i).getTextContent().trim();
                    if (!text.isEmpty()) result.add(Integer.parseInt(text));
                }
                break;
            }
        }
        return result;
    }

    private static String findLargestNumber(List<Integer> list) {
        if (list == null || list.isEmpty()) return "";

        List<String> strs = new ArrayList<>();
        for (Integer n : list) strs.add(String.valueOf(n));

        // So sánh: nếu (b+a) > (a+b) thì b đứng trước a
        strs.sort((a, b) -> (b + a).compareTo(a + b));

        if (strs.get(0).equals("0")) return "0";

        StringBuilder sb = new StringBuilder();
        for (String s : strs) sb.append(s);
        return sb.toString();
    }
}
