package org.mule.module.saaj;

import org.mule.transformer.AbstractTransformerTestCase;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.transformer.XmlToDomDocument;
import org.mule.module.xml.util.XMLUtils;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SOAPBodyToDocumentTransformerTestCase extends AbstractTransformerTestCase {

    private static String SOAP_GET_PEOPLE_RESPONSE = "src/test/resources/soap-people-response.xml";
    private static String XML_GET_PEOPLE_RESPONSE = "src/test/resources/get-people-response.xml";

    public SOAPBodyToDocumentTransformerTestCase() {
        super();
        XMLUnit.setIgnoreWhitespace(true);
        XMLUnit.setXSLTVersion("2.0");
        try {
            XMLUnit.getTransformerFactory();
        }
        catch (TransformerFactoryConfigurationError e) {
            XMLUnit.setTransformerFactory(XMLUtils.TRANSFORMER_FACTORY_JDK5);
        }
    }

    public Transformer getTransformer() throws Exception {
        return new SOAPMessageToDocumentTransformer();
    }

    public Transformer getRoundTripTransformer() throws Exception {
        return null;
    }

    public Object getTestData() {
        try {
            return SaajUtils.getSOAPMessageAsBytes(
                    SaajUtils.buildSOAPMessage(new FileInputStream(new File(SOAP_GET_PEOPLE_RESPONSE))));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getResultData() {
        XmlToDomDocument transformer = new XmlToDomDocument();

        transformer.setReturnClass(Document.class);

        String soapResponse;
        try {
            soapResponse = FileUtils.readFileToString(new File(XML_GET_PEOPLE_RESPONSE));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Document document;
        try {
            document = (Document) transformer.transform(soapResponse);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    public boolean compareResults(Object expected, Object result) {
        if (expected instanceof Document && result instanceof Document) {
            return XMLUnit.compareXML((Document) expected, (Document) result).similar();
        } else if (expected instanceof String && result instanceof String) {
            try {
                String expectedString = this.normalizeString((String) expected);
                String resultString = this.normalizeString((String) result);
                return XMLUnit.compareXML(expectedString, resultString).similar();
            }
            catch (Exception ex) {
                return false;
            }
        }
        // all other comparisons are passed up
        return super.compareResults(expected, result);
    }

}
