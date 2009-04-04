/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.saaj;

import org.mule.api.MessagingException;
import org.mule.api.transport.MessageTypeNotSupportedException;
import org.mule.transport.AbstractMessageAdapter;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;

/**
 * <code>SaajMessageAdapter</code> allows a <code>MuleEvent</code> to access the
 * properties and payload of a Saaj SOAPMessage in a uniform way.  It expects a
 * message of type <code>SOAPMessage</code> and will throw a
 * <code>MessageTypeNotSupportedException</code>.
 */
public class SaajMessageAdapter extends AbstractMessageAdapter {

    private SOAPMessage soapMessage;

    public SaajMessageAdapter(Object message) throws MessagingException {
        if (message instanceof SOAPMessage) {
            this.soapMessage = (SOAPMessage) message;
            populateHeaders();
            logger.debug(String.format("SaajMessageAdapter created with payload: %s", message));
        } else {
            throw new MessageTypeNotSupportedException(message, getClass());
        }
    }

    public String getPayloadAsString(String encoding) throws Exception {
        return soapMessage.toString();
    }

    public byte[] getPayloadAsBytes() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bytes);
        out.writeObject(soapMessage);
        return bytes.toByteArray();
    }

    public Object getPayload() {
        return soapMessage;
    }

    void populateHeaders() {
        try {
            if (soapMessage.getSOAPHeader() != null) {
                Iterator elements = soapMessage.getSOAPHeader().getChildElements();
                while (elements.hasNext()) {
                    SOAPHeaderElement header = (SOAPHeaderElement) elements.next();
                    String headerName = header.getLocalName();
                    String headerValue = header.getValue();
                    logger.debug(String.format("Adding \"%s\" message property with value \"%s\" from the SOAP header",
                            headerName, headerValue));
                    super.setProperty(headerName, headerValue);
                }
            }
        } catch (SOAPException e) {
            logger.warn("Could not add SOAP header");
        }
    }
}
