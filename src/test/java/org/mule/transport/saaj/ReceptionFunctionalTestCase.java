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
    Transformer domToXml;
    Transformer uglyXmlToPrettyXml;
    Transformer soapMessageToDocument;
    Transformer documentToSoapMessage;


    protected String getConfigResources() {
        return "src/test/resources/saaj-receiver-config.xml";
    }

    protected void doSetUp() throws Exception {
        xmlToDom = muleContext.getRegistry().lookupTransformer("xmlToDom");
        domToXml = muleContext.getRegistry().lookupTransformer("domToXml");
        uglyXmlToPrettyXml = muleContext.getRegistry().lookupTransformer("uglyXmlToPrettyXml");
        soapMessageToDocument = muleContext.getRegistry().lookupTransformer("soapBodyToDocument");
        documentToSoapMessage = muleContext.getRegistry().lookupTransformer("documentToSoapMessage");

        super.doSetUp();
    }

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
