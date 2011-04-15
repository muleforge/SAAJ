package org.mule.module.saaj;

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
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
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
        Transformer transformer = new DocumentToSOAPMessageTransformer();
        transformer.setMuleContext(muleContext);
        return transformer;
    }

    public Transformer getRoundTripTransformer() throws Exception {
        return null;
    }

    public Object getTestData() {
        XmlToDomDocument transformer = new XmlToDomDocument();
        transformer.setReturnClass(Document.class);
        transformer.setMuleContext(muleContext);

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
            return SaajUtils.getSOAPMessageAsBytes(
                    SaajUtils.buildSOAPMessage(new FileInputStream(new File(SOAP_GET_PEOPLE_REQUEST)), MessageFactory.newInstance()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SOAPException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean compareResults(Object expected, Object result) {
        String xml1;
        String xml2;

        try {
            Transformer transformer = new XmlPrettyPrinter();
            transformer.setMuleContext(muleContext);
            xml1 = (String) transformer.transform(normalizeString(new String((byte[]) expected)));
            xml2 = (String) transformer.transform(normalizeString(new String((byte[]) result)));
            return XMLUnit.compareXML(xml1, xml2).similar();
        } catch (Exception ex) {
            return false;

        }
    }
}