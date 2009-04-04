package org.mule.transport.saaj;

import org.apache.commons.io.FileUtils;
import org.mule.api.MuleMessage;
import org.mule.api.transport.DispatchException;
import org.mule.api.transformer.Transformer;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class SaajMessageDispatcherFunctionalTestCase extends FunctionalTestCase {

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
        MuleMessage result = client.send("saaj://localhost:9756/people", message, null);
        assertNotNull(result);
        assertEquals(getPeopleResponse,
                uglyXmlToPrettyXml.transform(domToXml.transform(soapMessageToDocument.transform(result.getPayload()))));
    }

    public void testAddPersonWithFault() throws Exception {
        MuleClient client = new MuleClient(muleContext);
        String exceptionMessage = null;

        try {
            client.send("saaj://localhost:9756/people",
                    documentToSoapMessage.transform(xmlToDom.transform(FAULTY_ADD_PERSON_SOAP_REQUEST)),
                    null);
        } catch (DispatchException ex) {
            exceptionMessage = ex.getCause().getMessage();
        }
        assertNotNull(exceptionMessage);
        assertEquals(ADD_PERSON_FAULT_MESSAGE, exceptionMessage);
    }

    public void testSynchronousAddPerson() throws Exception {
        Map properties = new HashMap();
        properties.put("SOAP_HEADER","test123");
        MuleClient client = new MuleClient(muleContext);
        MuleMessage response =
                client.send("saaj://localhost:9756/people",
                        documentToSoapMessage.transform(xmlToDom.transform(ADD_PERSON_SOAP_REQUEST)), properties);
        assertNotNull(response);
    }

    private static String GET_PEOPLE_SOAP_REQUEST = "<getPeople/>";

    private static String ADD_PERSON_SOAP_REQUEST =
            " <ser:addPerson1  xmlns:ser=\"http://services.testmodels.tck.mule.org/\">\n" +
                    "         <ser:arg0>John</ser:arg0>\n" +
                    "         <ser:arg1>DEmic</ser:arg1>\n" +
                    "      </ser:addPerson1>";


    private static String FAULTY_ADD_PERSON_SOAP_REQUEST = " <addPerson>\n" +
            "         <arg0>\n" +
            "            <address>\n" +
            "               <address></address>\n" +
            "               <postcode></postcode>\n" +
            "            </address>\n" +
            "            <firstName>Ross</firstName>\n" +
            "            <lastName></lastName>\n" +
            "         </arg0>\n" +
            "      </addPerson>";

    private static String ADD_PERSON_FAULT_MESSAGE = "SOAPFault encountered.\n" +
            " \tfault code   : [soap:Server]\n" +
            " \tfault string : [null person, first name or last name]\n" +
            " \tfault actor  : [null]\n" +
            " \tfault details: [none]";
}
