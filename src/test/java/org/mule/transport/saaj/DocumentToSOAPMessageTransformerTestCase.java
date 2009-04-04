package org.mule.transport.saaj;

import org.mule.transformer.AbstractTransformerTestCase;
import org.mule.api.transformer.Transformer;
import org.mule.api.transformer.TransformerException;
import org.mule.module.xml.transformer.XmlToDomDocument;
import org.mule.module.xml.transformer.XmlPrettyPrinter;
import org.mule.module.xml.util.XMLUtils;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.custommonkey.xmlunit.XMLUnit;

import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.soap.SOAPMessage;
import java.io.*;

public class DocumentToSOAPMessageTransformerTestCase extends AbstractTransformerTestCase {

    private static String GET_PEOPLE_REQUEST = "src/test/resources/get-people-request.xml";
    private static String SOAP_GET_PEOPLE_REQUEST = "src/test/resources/soap-get-people-request.xml";

    public DocumentToSOAPMessageTransformerTestCase() {
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
        return new DocumentToSOAPMessageTransformer();
    }

    public Transformer getRoundTripTransformer() throws Exception {
        return null;
    }

    public Object getTestData() {
        XmlToDomDocument transformer = new XmlToDomDocument();
        transformer.setReturnClass(Document.class);

        String body;
        try {
            body = FileUtils.readFileToString(new File(GET_PEOPLE_REQUEST));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Document document;
        try {
            document = (Document) transformer.transform(body);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    public Object getResultData() {
        try {
            return SaajUtils.buildSOAPMessage(new FileInputStream(new File(SOAP_GET_PEOPLE_REQUEST)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean compareResults(Object expected, Object result) {

        if (expected instanceof SOAPMessage && result instanceof SOAPMessage) {
            SOAPMessage s1 = (SOAPMessage) expected;
            SOAPMessage s2 = (SOAPMessage) result;

            try {
                ByteArrayOutputStream out1 = new ByteArrayOutputStream();
                s1.writeTo(out1);
                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                s2.writeTo(out2);

                Transformer transformer = new XmlPrettyPrinter();
                String xml1 = (String) transformer.transform(normalizeString(out1.toString()));
                String xml2 = (String) transformer.transform(normalizeString(out2.toString()));

                return XMLUnit.compareXML(xml1, xml2).similar();
            } catch (Exception ex) {
                return false;
            }
        } else if (expected instanceof Document && result instanceof Document) {
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
        return super.compareResults(expected, result);
    }
}