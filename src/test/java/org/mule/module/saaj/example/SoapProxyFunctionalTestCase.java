package org.mule.module.saaj.example;

import org.mule.tck.FunctionalTestCase;
import org.mule.module.client.MuleClient;
import org.mule.api.MuleMessage;

public class SoapProxyFunctionalTestCase extends FunctionalTestCase {

    protected String getConfigResources() {
        return "src/test/resources/examples/saaj-soap-proxy-config.xml";
    }

    public void testMessageSent() throws Exception {
        MuleClient client = new MuleClient(muleContext);

        MuleMessage soapResponse = client.send("http://localhost:9756/soap", ADD_PERSON_SOAP_REQUEST, null);
        assertNotNull(soapResponse);

    }

    private static String ADD_PERSON_SOAP_REQUEST =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                    "<SOAP-ENV:Header>" +
                    "<mule-saaj:priority xmlns:mule-saaj=\"http://www.mulesource.org/schema/mule/saaj/2.2\">HIGH</mule-saaj:priority>" +
                    "<mule-saaj:header2 xmlns:mule-saaj=\"http://www.mulesource.org/schema/mule/saaj/2.2\">another header</mule-saaj:header2>" +
                    "</SOAP-ENV:Header><SOAP-ENV:Body><ser:addPerson1 xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1></SOAP-ENV:Body></SOAP-ENV:Envelope>";
}