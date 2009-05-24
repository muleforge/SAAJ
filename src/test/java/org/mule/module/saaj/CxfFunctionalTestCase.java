package org.mule.module.saaj;

import org.mule.tck.FunctionalTestCase;
import org.mule.api.transformer.Transformer;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class CxfFunctionalTestCase extends FunctionalTestCase {

    private static String XML_GET_PEOPLE_RESPONSE = "src/test/resources/get-people-response.xml";

    private static String GET_PEOPLE_SOAP_REQUEST = "<getPeople/>";

    Transformer xmlToDom;
    Transformer domToXml;
    Transformer uglyXmlToPrettyXml;
    Transformer soapMessageToDocument;
    Transformer documentToSoapMessage;


    protected String getConfigResources() {
        return "src/test/resources/cxf-config.xml";
    }

    protected void doSetUp() throws Exception {
        xmlToDom = muleContext.getRegistry().lookupTransformer("xmlToDom");
        domToXml = muleContext.getRegistry().lookupTransformer("domToXml");
        uglyXmlToPrettyXml = muleContext.getRegistry().lookupTransformer("uglyXmlToPrettyXml");
        soapMessageToDocument = muleContext.getRegistry().lookupTransformer("soapMessageToDocument");
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
  
}
