package org.mule.module.saaj;

import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.MuleMessage;
import org.mule.module.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;

/**
 * <code>SOAPBodyToDocumentTransformer</code> Transform the payload of a <code>SOAPBody</code>,
 * from a <code>SOAPMessage</code>,to a <code>org.w3c.dom.Document</code>.  Headers in the SOAP
 * message are propgating as properties on the <code>MuleMessage</code>.
 */
public class SOAPMessageToDocumentTransformer extends AbstractMessageAwareTransformer {

    DocumentBuilder builder;

    public SOAPMessageToDocumentTransformer() {
        super();
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MuleRuntimeException(SaajMessages.failedToCreateDocumentBuilder(), e);
        }
    }

    public Object transform(MuleMessage muleMessage, String s) throws TransformerException {

        InputStream inputStream;

        if (muleMessage.getPayload() instanceof byte[]) {
            byte[] in = (byte[]) muleMessage.getPayload();
            inputStream = new ByteArrayInputStream(in);
        } else if (muleMessage.getPayload() instanceof InputStream) {
            inputStream = (InputStream) muleMessage.getPayload();
        } else {
            throw new MuleRuntimeException(SaajMessages.failedToExtractSoapBody());
        }

        SOAPMessage soapMessage = SaajUtils.buildSOAPMessage(inputStream);
        Document result = builder.newDocument();
        Node soapBody;
        try {
            soapBody = result.importNode(SaajUtils.getBodyContent(soapMessage.getSOAPBody()), true);
        } catch (SOAPException e) {
            throw new MuleRuntimeException(SaajMessages.failedToExtractSoapBody(), e);
        }
        result.appendChild(soapBody);
        populateHeaders(soapMessage, muleMessage);
        return result;
    }

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
