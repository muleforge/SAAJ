package org.mule.transport.saaj;

import org.apache.commons.io.FileUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.Transformer;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class DispatchFunctionalTestCase extends FunctionalTestCase {

    private static String XML_GET_PEOPLE_RESPONSE = "src/test/resources/get-people-response.xml";

    Transformer xmlToDom;
    Transformer domToXml;
    Transformer uglyXmlToPrettyXml;
    Transformer soapMessageToDocument;
    Transformer documentToSoapMessage;

    protected String getConfigResources() {
        return "src/test/resources/saaj-dispatcher-config.xml";
    }

    protected void doSetUp() throws Exception {
        xmlToDom = muleContext.getRegistry().lookupTransformer("xmlToDom");
        domToXml = muleContext.getRegistry().lookupTransformer("domToXml");
        uglyXmlToPrettyXml = muleContext.getRegistry().lookupTransformer("uglyXmlToPrettyXml");
        soapMessageToDocument = muleContext.getRegistry().lookupTransformer("soapBodyToDocument");
        documentToSoapMessage = muleContext.getRegistry().lookupTransformer("documentToSoapMessage");
    }

    public void testGetPeople() throws Exception {
        String getPeopleResponse = FileUtils.readFileToString(new File(XML_GET_PEOPLE_RESPONSE));
        MuleClient client = new MuleClient(muleContext);
        Object message = documentToSoapMessage.transform(xmlToDom.transform(GET_PEOPLE_SOAP_REQUEST));
        MuleMessage result = client.send("http://localhost:9756/people", message, null);
        assertNotNull(result);
        assertEquals(getPeopleResponse,
                uglyXmlToPrettyXml.transform(domToXml.transform(soapMessageToDocument.transform(result.getPayload()))));
    }

    public void testSynchronousAddPerson() throws Exception {
        Map properties = new HashMap();
        MuleClient client = new MuleClient(muleContext);
        MuleMessage response =
                client.send("http://localhost:9756/people",
                        documentToSoapMessage.transform(xmlToDom.transform(ADD_PERSON_SOAP_REQUEST)), properties);
        assertNotNull(response);
    }

    @SuppressWarnings({"unchecked"})
    public void testSynchronousAddPersonWithMessageProperties() throws Exception {
        Map properties = new HashMap();
        properties.put("header1","a header");
        properties.put("header2","another header");

        MuleClient client = new MuleClient(muleContext);
        MuleMessage response =
                client.send("http://localhost:9756/people",
                        documentToSoapMessage.transform(xmlToDom.transform(ADD_PERSON_SOAP_REQUEST)), properties);
        assertNotNull(response);
    }

    public void testSOAPMessageSentAndTransformed() throws Exception {
        MuleClient client = new MuleClient(muleContext);
        client.send("vm://in", ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS.getBytes(), null);
        MuleMessage result = client.request("vm://out",15000);
        assertNotNull(result);
        assertEquals("a header", result.getProperty("header1"));
        
    }

    private static String GET_PEOPLE_SOAP_REQUEST = "<getPeople/>";

    private static String ADD_PERSON_SOAP_REQUEST =
            " <ser:addPerson1  xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1>";

      private static String ADD_PERSON_SOAP_REQUEST_WITH_CUSTOM_NS =
            "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><demic:header1 xmlns:demic=\"http://demic.com\">a header</demic:header1><demic:header2 xmlns:demic=\"http://demic.com\">another header</demic:header2></SOAP-ENV:Header><SOAP-ENV:Body><ser:addPerson1 xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1></SOAP-ENV:Body></SOAP-ENV:Envelope>";

}
