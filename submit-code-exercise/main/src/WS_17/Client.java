package WS_17;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class Client {
    static final String ENDPOINT = "http://36.50.135.242:2221/DataService";
    static final String SC = "B23DCCN259", QC = "HVFWqBE0";
    static final String NS = "xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
                             "xmlns:tns=\"http://soap.server.network.judge.dblab.ptit/\"";

    public static void main(String[] args) throws Exception {
        List<Integer> data = getData();
        submitDataString(findLargest(data));
    }

    static List<Integer> getData() throws Exception {
        String resp = soap("<tns:getData><studentCode>" + SC + "</studentCode><qCode>" + QC + "</qCode></tns:getData>");
        List<Integer> list = new ArrayList<>();
        NodeList nodes = parse(resp).getElementsByTagName("return");
        for (int i = 0; i < nodes.getLength(); i++)
            list.add(Integer.parseInt(nodes.item(i).getTextContent().trim()));
        return list;
    }

    static void submitDataString(String data) throws Exception {
        soap("<tns:submitDataString><studentCode>" + SC + "</studentCode><qCode>" + QC + "</qCode><data>" + data + "</data></tns:submitDataString>");
    }

    static String soap(String body) throws Exception {
        String env = "<soapenv:Envelope " + NS + "><soapenv:Header/><soapenv:Body>" + body + "</soapenv:Body></soapenv:Envelope>";
        HttpURLConnection c = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        c.setRequestMethod("POST");
        c.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        c.setRequestProperty("SOAPAction", "\"\"");
        c.setDoOutput(true);
        c.getOutputStream().write(env.getBytes("UTF-8"));
        InputStream is = c.getResponseCode() == 200 ? c.getInputStream() : c.getErrorStream();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line; while ((line = br.readLine()) != null) sb.append(line);
        }
        return sb.toString();
    }

    static Document parse(String xml) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        return f.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes("UTF-8")));
    }

    static String findLargest(List<Integer> list) {
        List<String> strs = new ArrayList<>();
        for (int n : list) strs.add(String.valueOf(n));
        strs.sort((a, b) -> (b + a).compareTo(a + b));
        if (strs.get(0).equals("0")) return "0";
        return String.join("", strs);
    }
}
