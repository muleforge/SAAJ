package org.mule.transport.saaj;

import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.MuleMessage;
import org.mule.transport.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;

import javax.xml.soap.*;
import java.util.Iterator;

/**
 * <code>DocumentToSOAPMessageTransformer</code> transforms a org.w3c.dom.Document to a SOAP message encoded as a byte array.
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


    /**
     * Propagates message properties from the <code>MuleMessage</code> to the <code>SOAPMessage</code> as SOAP headers
     * @param soapMessage the SOAP message
     * @param muleMessage the Mule message
     */
    void populateHeaders(SOAPMessage soapMessage, MuleMessage muleMessage) {
        try {
            if (soapMessage.getSOAPHeader() != null) {
                Iterator elements = soapMessage.getSOAPHeader().getChildElements();
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
