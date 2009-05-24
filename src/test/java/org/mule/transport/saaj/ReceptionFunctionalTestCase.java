package org.mule.transport.saaj;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.Transformer;
import org.mule.module.client.MuleClient;
import org.mule.module.xml.transformer.XmlPrettyPrinter;
import org.mule.tck.FunctionalTestCase;
import org.custommonkey.xmlunit.XMLUnit;

import java.util.HashMap;
import java.util.Map;

public class ReceptionFunctionalTestCase extends FunctionalTestCase {

    Transformer xmlToDom;

    protected String getConfigResources() {
        return "src/test/resources/saaj-receiver-config.xml";
    }

    protected void doSetUp() throws Exception {
        xmlToDom = muleContext.getRegistry().lookupTransformer("xmlToDom");
        super.doSetUp();
    }

    @SuppressWarnings({"unchecked"})
    public void testMessageDispatched() throws Exception {

        MuleClient client = new MuleClient(muleContext);
        Map properties = new HashMap();
        properties.put("header1", "a header");
        properties.put("header2", "another header");
        client.send("vm://document.in", xmlToDom.transform(ADD_PERSON_REQUEST), properties);
        MuleMessage result = client.request("vm://soap.out", 15000);
        assertNotNull(result);
        assertTrue(compareXML(ADD_PERSON_SOAP_REQUEST, result.getPayloadAsString()));
    }

    @SuppressWarnings({"unchecked"})
    public void testMessageDispatchedWithCustomNamespace() throws Exception {

        MuleClient client = new MuleClient(muleContext);
        Map properties = new HashMap();
        properties.put("header1", "a header");
        properties.put("header2", "another header");
        client.send("vm://namespace.in", xmlToDom.transform(ADD_PERSON_REQUEST), properties);
        MuleMessage result = client.request("vm://namespace.out", 15000);
        assertNotNull(result);
        assertTrue(compareXML(ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS, result.getPayloadAsString()));
    }

     public void testMessageTransformed() throws Exception {
        MuleClient client = new MuleClient(muleContext);
        client.send("vm://soap.in", ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS.getBytes(), null);
        MuleMessage result = client.request("vm://soap.out", 15000);
        assertNotNull(result);
        assertEquals("a header", result.getProperty("header1"));
        assertEquals("another header", result.getProperty("header2"));
    }

    boolean compareXML(String s1, String s2) {
        String xml1;
        String xml2;

        try {
            Transformer transformer = new XmlPrettyPrinter();
            xml1 = (String) transformer.transform(s1);
            xml2 = (String) transformer.transform(s2);
            return XMLUnit.compareXML(xml1, xml2).similar();
        } catch (Exception ex) {
            return false;

        }
    }

    // ToDo Externalize this to a file
    private static String ADD_PERSON_REQUEST =
            " <ser:addPerson1  xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1>";

    // ToDo Externalize this to a file
    private static String ADD_PERSON_SOAP_REQUEST =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<SOAP-ENV:Header>" +
                    "<mule-saaj:header1 xmlns:mule-saaj=\"http://www.mulesource.org/schema/mule/saaj/2.2\">a header</mule-saaj:header1>" +
                    "<mule-saaj:header2 xmlns:mule-saaj=\"http://www.mulesource.org/schema/mule/saaj/2.2\">another header</mule-saaj:header2>" +
                    "</SOAP-ENV:Header><SOAP-ENV:Body><ser:addPerson1 xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1></SOAP-ENV:Body></SOAP-ENV:Envelope>";

    // ToDo Externalize this to a file
    private static String ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><demic:header1 xmlns:demic=\"http://demic.com\">a header</demic:header1><demic:header2 xmlns:demic=\"http://demic.com\">another header</demic:header2></SOAP-ENV:Header><SOAP-ENV:Body><ser:addPerson1 xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1></SOAP-ENV:Body></SOAP-ENV:Envelope>";
}
