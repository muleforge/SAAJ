package org.mule.transport.saaj;

import org.mule.transformer.AbstractTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.transport.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

/**
 * <code>DocumentToSOAPMessageTransformer</code> Transform a org.w3c.dom.Document to a <code>SOAPMessage</code>.
 */
public class DocumentToSOAPMessageTransformer extends AbstractTransformer {

    protected Object doTransform(Object o, String s) throws TransformerException {

        Document document = (Document) o;
        SOAPMessage message;

        try {
            MessageFactory messageFactory = MessageFactory.newInstance();
            message = messageFactory.createMessage();
            SOAPBody body = message.getSOAPBody();
            body.addDocument(document);
            message.saveChanges();
        } catch (SOAPException ex) {
            throw new TransformerException(SaajMessages.failedToBuildSOAPMessage());
        }
        return message;
    }

}
