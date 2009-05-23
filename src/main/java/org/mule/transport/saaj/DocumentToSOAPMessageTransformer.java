package org.mule.transport.saaj;

import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.MuleMessage;
import org.mule.transport.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import java.util.Iterator;

/**
 * <code>DocumentToSOAPMessageTransformer</code> Transform a org.w3c.dom.Document to a <code>SOAPMessage</code>.
 */
public class DocumentToSOAPMessageTransformer extends AbstractMessageAwareTransformer {

    public Object transform(MuleMessage muleMessage, String s) throws TransformerException {

        Document document = (Document) muleMessage.getPayload();
        SOAPMessage soapMessage;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            soapMessage = messageFactory.createMessage();
            SOAPBody body = soapMessage.getSOAPBody();
            body.addDocument(document);
            soapMessage.saveChanges();
        } catch (SOAPException ex) {
            throw new TransformerException(SaajMessages.failedToBuildSOAPMessage());
        }

        populateHeaders(soapMessage, muleMessage);
        return SaajUtils.getSOAPMessageAsBytes(soapMessage);
    }


    void populateHeaders(SOAPMessage soapMmessage, MuleMessage muleMessage) {
        try {
            if (soapMmessage.getSOAPHeader() != null) {
                Iterator elements = soapMmessage.getSOAPHeader().getChildElements();
                while (elements.hasNext()) {
                    SOAPHeaderElement header = (SOAPHeaderElement) elements.next();
                    String headerName = header.getLocalName();
                    String headerValue = header.getValue();
                    logger.debug(String.format("Adding \"%s\" message property with value \"%s\" from the SOAP header",
                            headerName, headerValue));
                    muleMessage.setProperty(headerName, headerValue);
                }
            }
        } catch (SOAPException e) {
            logger.warn("Could not add SOAP header");
        }
    }

}
