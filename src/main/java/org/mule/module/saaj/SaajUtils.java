package org.mule.module.saaj;

import org.mule.api.MuleRuntimeException;
import org.mule.module.saaj.i18n.SaajMessages;
import org.w3c.dom.Node;

import javax.xml.soap.*;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Support methods for working with SAAJ
 */
public class SaajUtils {

    private static String MULE_SAAJ_HEADER_URI = "http://www.mulesource.org/schema/mule/saaj/2.2";
    private static String MULE_SAAJ_HEADER_PREFIX = "mule-saaj";

    /**
     * Build a <code>SOAPMessage</code> from an <code>InputStream</code>.
     *
     * @param input          the stream of XML to create the <code>SOAPMessage</code> from.
     * @param messageFactory a reference to the MessageFactory to use
     * @return the <code>SOAPMessage</code>  constructed from the input
     */
    public static SOAPMessage buildSOAPMessage(InputStream input, MessageFactory messageFactory) {
        SOAPMessage soapMessage;
        try {
            soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            StreamSource preppedMsgSrc = new StreamSource(input);
            soapPart.setContent(preppedMsgSrc);
            soapMessage.saveChanges();
        } catch (SOAPException ex) {
            throw new MuleRuntimeException(SaajMessages.failedToBuildSOAPMessage(), ex);
        }
        return soapMessage;
    }

    /**
     * Build a <code>SOAPMessage</code> from an <code>InputStream</code>.
     *
     * @param input          the <code>InputStream</code> to build the message from
     * @param headers        A <code>Map</code> used to populate the SOAP headers of the <code>SOAPMessage</code>
     * @param messageFactory A reference to the <code>MessageFactory</code> to be used
     * @return the constructed <code>SOAPMessage</code>
     */
    public static SOAPMessage buildSOAPMessage(InputStream input, Map<String, String> headers,
                                               MessageFactory messageFactory) {
        SOAPMessage soapMessage;
        try {
            SOAPFactory soapFactory = SOAPFactory.newInstance();
            soapMessage = messageFactory.createMessage();
            SOAPPart soapPart = soapMessage.getSOAPPart();
            StreamSource preppedMsgSrc = new StreamSource(input);
            soapPart.setContent(preppedMsgSrc);
            for (String headerName : headers.keySet()) {
                SOAPHeader header = soapMessage.getSOAPHeader();
                Name name = soapFactory.createName(headerName, MULE_SAAJ_HEADER_PREFIX, MULE_SAAJ_HEADER_URI);
                SOAPHeaderElement headerElement = header.addHeaderElement(name);
                headerElement.addTextNode(headers.get(headerName));
            }
            soapMessage.saveChanges();
        } catch (SOAPException ex) {
            throw new MuleRuntimeException(SaajMessages.failedToBuildSOAPMessage(), ex);
        }
        return soapMessage;
    }

    /**
     * Serializes a <code>SOAPMessage</code> as XML encoded as a byte array
     *
     * @param message the <code>SOAPMessage</code> to encode as bytes
     * @return the encoded <code>SOAPMessage</code>
     */
    public static byte[] getSOAPMessageAsBytes(SOAPMessage message) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            message.writeTo(byteStream);
        } catch (SOAPException ex) {
            throw new MuleRuntimeException(SaajMessages.failedToBuildSOAPMessage(), ex);
        } catch (IOException ex) {
            throw new MuleRuntimeException(SaajMessages.failedToBuildSOAPMessage(), ex);
        }
        return byteStream.toByteArray();
    }

    /**
     * Serialize a <code>SOAPMessage</code> as a String
     *
     * @param message the <code>SOAPMessage</code>
     * @return the String value of the <code>SOAPMessage</code>
     */
    public static String getSOAPMessageAsString(SOAPMessage message) {
        return new String(getSOAPMessageAsBytes(message));
    }

    /**
     * Extract the first <code>Node</code> contaning the payload of a <code>SOAPBody</code>/
     *
     * @param body the <code>SOAPBody</code> to extract the payload from
     * @return the first <code>Node</code> of the payload
     */
    public static Node getBodyContent(SOAPBody body) {
        Iterator iterator = body.getChildElements();

        Node firstNode = null;
        while (iterator.hasNext()) {
            Node currentNode = (Node) iterator.next();
            if (currentNode instanceof SOAPBodyElement) {
                firstNode = currentNode;
                break;
            }
        }
        return firstNode;
    }

    /**
     * Determine if a SOAP fault is present in a <code>SOAPMessage</code>'s body
     *
     * @param soapMessage the <code>SOAPMessage</code> to evaluate
     * @return true if the message contains a fault, false if not
     */
    public static boolean containsFault(SOAPMessage soapMessage) {
        boolean result;
        try {
            result = soapMessage.getSOAPBody().getFault() != null;
        } catch (SOAPException ex) {
            throw new MuleRuntimeException(SaajMessages.failedToEvaluateFault(), ex);
        }
        return result;
    }
}

