package org.mule.transport.saaj;

import org.mule.tck.FunctionalTestCase;
import org.mule.module.client.MuleClient;
import org.mule.module.xml.transformer.XmlPrettyPrinter;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.Transformer;
import org.custommonkey.xmlunit.XMLUnit;

import java.util.Map;
import java.util.HashMap;

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
    }

    @SuppressWarnings({"unchecked"})
    public void testReceiveMessage() throws Exception {
        MuleClient client = new MuleClient(muleContext);

        Map properties = new HashMap();
        properties.put("header1", "a header");
        properties.put("header2", "another header");
        client.send("vm://in",
                documentToSoapMessage.transform(xmlToDom.transform(ADD_PERSON_SOAP_REQUEST)), properties);

        MuleMessage response = client.request("vm://out", 15000);
        assertNotNull(response);

        String xml1;
        String xml2;

        Transformer transformer = new XmlPrettyPrinter();
        xml1 = (String) transformer.transform(ADD_PERSON_SOAP_REQUEST);
        xml2 = (String) transformer.transform(domToXml.transform(response.getPayload()));
        assertTrue(XMLUnit.compareXML(xml1, xml2).similar());
        assertEquals("a header", response.getProperty("header1"));
        assertEquals("another header", response.getProperty("another header"));

    }

    private static String ADD_PERSON_SOAP_REQUEST =
            " <ser:addPerson1  xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1>";
}
