package org.mule.transport.saaj;

import org.mule.transformer.AbstractTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.MuleRuntimeException;
import org.mule.transport.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.DocumentBuilder;

/**
 * <code>SOAPBodyToDocumentTransformer</code> Transform the payload of a <code>SOAPBody</code>,
 * from a <code>SOAPMessage</code>,to a <code>org.w3c.dom.Document</code>.
 */
public class SOAPBodyToDocumentTransformer extends AbstractTransformer {

    DocumentBuilder builder;

    public SOAPBodyToDocumentTransformer() {
        super();
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new MuleRuntimeException(SaajMessages.failedToCreateDocumentBuilder(), e);
        }
    }

    protected Object doTransform(Object o, String s) throws TransformerException {
        SOAPMessage soapMessage = (SOAPMessage) o;
        Document result = builder.newDocument();
        Node soapBody;
        try {
            soapBody = result.importNode(SaajUtils.getBodyContent(soapMessage.getSOAPBody()), true);
        } catch (SOAPException e) {
            throw new MuleRuntimeException(SaajMessages.failedToExtractSoapBody(), e);
        }
        result.appendChild(soapBody);
        return result;
    }
}
