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

import org.mule.transport.AbstractConnector;
import org.mule.api.MuleException;
import org.mule.api.lifecycle.InitialisationException;

import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import com.sun.net.ssl.HostnameVerifier;

/**
 * <code>SaajConnector</code> is used to send SOAP messages using SAAJ.
 */

public class SaajConnector extends AbstractConnector {
    SOAPConnectionFactory connectionFactory;
    MessageFactory messageFactory;
    SOAPFactory soapFactory;
    boolean ignoreSSLHostMismatch;

    public SaajConnector() {
        super();
        registerProtocols();
    }

    protected void registerProtocols() {
        registerSupportedProtocol("http");
        registerSupportedProtocol("https");
    }

    public void doInitialise() throws InitialisationException {
        try {
            connectionFactory = SOAPConnectionFactory.newInstance();
            messageFactory = MessageFactory.newInstance();
            soapFactory = SOAPFactory.newInstance();
            if (ignoreSSLHostMismatch) {
                com.sun.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(getLiberalHostnameVerifier());
            }
        } catch (SOAPException e) {
            throw new InitialisationException(e, this);
        }
    }

    public String getProtocol() {
        return "saaj";
    }

    public SOAPConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public MessageFactory getMessageFactory() {
        return messageFactory;
    }

    public SOAPFactory getSoapFactory() {
        return soapFactory;
    }

    public boolean isIgnoreSSLHostMismatch() {
        return ignoreSSLHostMismatch;
    }

    public void setIgnoreSSLHostMismatch(boolean ignoreSSLHostMismatch) {
        this.ignoreSSLHostMismatch = ignoreSSLHostMismatch;
    }

    protected void doDispose() {
    }

    protected void doStart() throws MuleException {
    }

    protected void doStop() throws MuleException {
    }

    protected void doConnect() throws Exception {
    }

    protected void doDisconnect() throws Exception {
    }

    // ToDo Fix deprecated call
    HostnameVerifier getLiberalHostnameVerifier() {
        return new HostnameVerifier() {
            public boolean verify(String s, String s1) {
                logger.error("Warning: URL Host: " + s + " vs. " + s1);
                return true;
            }
        };
    }


}
