package org.mule.module.saaj;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.module.saaj.i18n.SaajMessages;
import org.mule.transformer.AbstractMessageTransformer;
import org.w3c.dom.Document;

import javax.xml.soap.*;

/**
 * <code>DocumentToSOAPMessageTransformer</code> transforms a org.w3c.dom.Document to a SOAP message using
 * SAAJ. The transformed result is serialized as a byte array.
 */
public class DocumentToSOAPMessageTransformer extends AbstractMessageTransformer {

    private boolean propagateHeaders = true;
    private String headerURI = "http://www.mulesource.org/schema/mule/saaj/2.2";
    private String headerPrefix = "mule-saaj";

    private SOAPFactory soapFactory;
    private MessageFactory messageFactory;

    public DocumentToSOAPMessageTransformer() throws Exception {
        soapFactory = SOAPFactory.newInstance();
        messageFactory = MessageFactory.newInstance();
    }

    public void setPropagateHeaders(boolean propagateHeaders) {
        this.propagateHeaders = propagateHeaders;
    }

    public void setHeaderURI(String headerURI) {
        this.headerURI = headerURI;
    }

    public void setHeaderPrefix(String headerPrefix) {
        this.headerPrefix = headerPrefix;
    }

    @Override
    public Object transformMessage(MuleMessage muleMessage, String s) throws TransformerException {

        Document document = (Document) muleMessage.getPayload();
        SOAPMessage soapMessage;

        try {
            soapMessage = messageFactory.createMessage();
            SOAPBody body = soapMessage.getSOAPBody();
            body.addDocument(document);
            if (propagateHeaders) {
                propagateHeaders(muleMessage, soapMessage);
            }
            soapMessage.saveChanges();
        } catch (SOAPException ex) {
            throw new TransformerException(SaajMessages.failedToBuildSOAPMessage());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Transformation result: " + SaajUtils.getSOAPMessageAsString(soapMessage));
        }

        return SaajUtils.getSOAPMessageAsBytes(soapMessage);
    }

    void propagateHeaders(MuleMessage muleMessage, SOAPMessage soapMessage) throws SOAPException {
        for (Object n : muleMessage.getInboundPropertyNames()) {
            String propertyName = (String) n;
            SOAPHeader header = soapMessage.getSOAPHeader();

            Name name = soapFactory.createName(propertyName, headerPrefix, headerURI);
            SOAPHeaderElement headerElement = header.addHeaderElement(name);
            headerElement.addTextNode(muleMessage.getInboundProperty(propertyName).toString());
        }
    }

}
