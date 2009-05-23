package org.mule.transport.saaj;

import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.MuleMessage;
import org.mule.transport.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;

import javax.xml.soap.*;

/**
 * <code>DocumentToSOAPMessageTransformer</code> transforms a org.w3c.dom.Document to a SOAP message encoded as a byte array.
 */
public class DocumentToSOAPMessageTransformer extends AbstractMessageAwareTransformer {

    private static String MULE_SAAJ_HEADER_URI = "http://www.mulesource.org/schema/mule/saaj/2.2";
    private static String MULE_SAAJ_HEADER_PREFIX = "mule-saaj";

    private SOAPFactory soapFactory;
    private MessageFactory messageFactory;

    public DocumentToSOAPMessageTransformer() throws Exception {
        soapFactory = SOAPFactory.newInstance();
        messageFactory = MessageFactory.newInstance();
    }

    public Object transform(MuleMessage muleMessage, String s) throws TransformerException {

        Document document = (Document) muleMessage.getPayload();
        SOAPMessage soapMessage;

        try {
            soapMessage = messageFactory.createMessage();
            SOAPBody body = soapMessage.getSOAPBody();
            body.addDocument(document);
            populateHeaders(muleMessage, soapMessage);
            soapMessage.saveChanges();
        } catch (SOAPException ex) {
            throw new TransformerException(SaajMessages.failedToBuildSOAPMessage());
        }

        return SaajUtils.getSOAPMessageAsBytes(soapMessage);
    }


    void populateHeaders(MuleMessage muleMessage, SOAPMessage soapMessage) throws SOAPException {
        for (Object n : muleMessage.getPropertyNames()) {
            String propertyName = (String) n;
            SOAPHeader header = soapMessage.getSOAPHeader();

            Name name = soapFactory.createName(propertyName, MULE_SAAJ_HEADER_PREFIX, MULE_SAAJ_HEADER_URI);
            SOAPHeaderElement headerElement = header.addHeaderElement(name);
            headerElement.addTextNode(muleMessage.getProperty(propertyName).toString());
        }
    }

}
