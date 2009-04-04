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

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.MuleRuntimeException;
import org.mule.api.transport.MessageAdapter;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.endpoint.EndpointURI;
import org.mule.transport.AbstractMessageDispatcher;
import org.mule.transport.saaj.i18n.SaajMessages;
import org.mule.DefaultMuleMessage;

import javax.xml.soap.*;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * The <code>SaajMessageDispatcher</code> handles the dispatch of <code>SOAPMessage</code> using a
 * <code>SOAPConnection</code>.
 */
public class SaajMessageDispatcher extends AbstractMessageDispatcher {

    private static String MULE_SAAJ_HEADER_URI = "http://www.mulesource.org/schema/mule/saaj/2.2";
    private static String MULE_SAAJ_HEADER_PREFIX = "mule-saaj";

    SaajConnector cnn;
    SOAPConnection connection;

    public SaajMessageDispatcher(OutboundEndpoint endpoint) {
        super(endpoint);
        cnn = (SaajConnector) endpoint.getConnector();
    }

    public void doConnect() throws Exception {
        try {
            connection = cnn.getConnectionFactory().createConnection();
        } catch (SOAPException e) {
            throw new MuleRuntimeException(SaajMessages.couldNotCreateSaajConnection(), e);
        }
    }

    public void doDisconnect() throws Exception {
        connection.close();
    }

    public void doDispatch(MuleEvent event) throws Exception {
        logger.debug("Sending SOAP message asynchronously");
        sendSoapRequest(event, rewriteAddress(event.getEndpoint().getEndpointURI()));
    }

    public MuleMessage doSend(MuleEvent event) throws Exception {
        logger.debug("Sending SOAP message synchronously");
        SOAPMessage response = sendSoapRequest(event, rewriteAddress(event.getEndpoint().getEndpointURI()));
        SaajMessageAdapter adapter = new SaajMessageAdapter(response);
        addMIMEHeaders(adapter, response);
        return new DefaultMuleMessage(adapter);
    }

    SOAPMessage sendSoapRequest(MuleEvent event, String address) throws Exception {
        SOAPMessage message = (SOAPMessage) event.transformMessage();
        propagateHeaders(event.getMessage(), message);

        if (logger.isDebugEnabled()) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            message.writeTo(out);
            logger.debug("SOAP request: " + out);
        }

        SOAPMessage response = connection.call(message, address);

        if (response != null) {
            if (logger.isDebugEnabled()) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.writeTo(out);
                logger.debug("SOAP response: " + out);
            }
        } else {
            throw new MuleRuntimeException(SaajMessages.soapResponseIsNull());
        }

        if (response.getSOAPBody().getFault() != null) {
            SOAPFault fault = response.getSOAPBody().getFault();
            String faultCode = fault.getFaultCode();
            String faultString = fault.getFaultString();
            String faultActor = fault.getFaultActor();
            String faultDetails = serializeFaultDetails(fault);

            throw new MuleRuntimeException((SaajMessages.soapFault(faultCode, faultString,
                    faultActor, faultDetails)));
        }
        return response;
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

    private void addMIMEHeaders(MessageAdapter muleMessage, SOAPMessage soapMessage) {
        Iterator headers = soapMessage.getMimeHeaders().getAllHeaders();
        while (headers.hasNext()) {
            MimeHeader header = (MimeHeader) headers.next();
            muleMessage.setProperty(header.getName(), header.getValue());
        }
    }

    private String rewriteAddress(EndpointURI uri) {
        String result;
        if (uri.getUser() != null) {
            String password = "";
            if (uri.getPassword() != null) {
                password = uri.getPassword();
            }
            result = String.format("%s://%s:%s@%s", uri.getScheme().replace("saaj", "http"),
                    uri.getUser(), password, uri.getAddress().split("saaj?//")[1]);
        } else {
            result = uri.getAddress().replace("saaj", "http");
        }
        return result;
    }

    private void propagateHeaders(MuleMessage muleMessage, SOAPMessage soapMessage) throws SOAPException {
        for (Object n : muleMessage.getPropertyNames()) {
            String propertyName = (String) n;
            SOAPHeader header = soapMessage.getSOAPHeader();

            Name name = cnn.getSoapFactory().createName(propertyName, MULE_SAAJ_HEADER_PREFIX, MULE_SAAJ_HEADER_URI);
            SOAPHeaderElement headerElement = header.addHeaderElement(name);
            headerElement.addTextNode(muleMessage.getProperty(propertyName).toString());
        }
        soapMessage.saveChanges();
    }
}

