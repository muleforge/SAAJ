package org.mule.module.saaj;

import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.MuleRuntimeException;
import org.mule.api.MuleMessage;
import org.mule.module.saaj.i18n.SaajMessages;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.soap.*;
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

    private boolean throwExceptionOnFault = true;

    DocumentBuilder builder;
    MessageFactory messageFactory;


    public SOAPMessageToDocumentTransformer() throws SOAPException {
        super();
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            messageFactory = MessageFactory.newInstance();
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

        SOAPMessage soapMessage = SaajUtils.buildSOAPMessage(inputStream, messageFactory);

        if (throwExceptionOnFault && SaajUtils.containsFault(soapMessage)) {
            throwFaultException(soapMessage);
        }

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

    private void throwFaultException(SOAPMessage soapMessage) {
        SOAPFault fault;
        try {
            fault = soapMessage.getSOAPBody().getFault();
        } catch (SOAPException ex) {
            throw new MuleRuntimeException(SaajMessages.failedToEvaluateFault(), ex);
        }
        String faultCode = fault.getFaultCode();
        String faultString = fault.getFaultString();
        String faultActor = fault.getFaultActor();
        String faultDetails = serializeFaultDetails(fault);

        throw new MuleRuntimeException((SaajMessages.soapFault(faultCode, faultString,
                faultActor, faultDetails)));
    }

    private String serializeFaultDetails(SOAPFault fault) {
        String result = "none";
        if (fault.getDetail() != null) {
            StringBuilder faultDetails = new StringBuilder();
            Iterator detailEntries = fault.getDetail().getDetailEntries();
            while (detailEntries.hasNext()) {
                DetailEntry detail = (DetailEntry) detailEntries.next();
                faultDetails.append(detail.getTextContent());
            }
            result = faultDetails.toString();
        }
        return result;
    }


    public void setThrowExceptionOnFault(boolean throwExceptionOnFault) {
        this.throwExceptionOnFault = throwExceptionOnFault;
    }
}
