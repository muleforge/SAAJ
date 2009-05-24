package org.mule.transport.saaj;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;


public class SoapMessageToDocumentTransformerFunctionalTestCase extends FunctionalTestCase {

    protected String getConfigResources() {
        return "src/test/resources/saaj-dispatcher-config.xml";
    }

    public void testMessageTransformed() throws Exception {
        MuleClient client = new MuleClient(muleContext);
        client.send("vm://in", ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS.getBytes(), null);
        MuleMessage result = client.request("vm://out", 15000);
        assertNotNull(result);
        assertEquals("a header", result.getProperty("header1"));
        assertEquals("another header", result.getProperty("header2"));

    }

    private static String ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><demic:header1 xmlns:demic=\"http://demic.com\">a header</demic:header1><demic:header2 xmlns:demic=\"http://demic.com\">another header</demic:header2></SOAP-ENV:Header><SOAP-ENV:Body><ser:addPerson1 xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1></SOAP-ENV:Body></SOAP-ENV:Envelope>";

}
