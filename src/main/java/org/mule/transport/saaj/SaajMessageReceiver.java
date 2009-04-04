/*
 * $Id: MessageReceiver.vm 11079 2008-02-27 15:52:01Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.saaj;

import org.mule.transport.ConnectException;
import org.mule.transport.AbstractMessageReceiver;
import org.mule.transport.http.HttpMessageReceiver;
import org.mule.transport.http.HttpConnector;
import org.mule.api.service.Service;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.endpoint.ImmutableEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.transport.Connector;
import org.mule.api.transport.MessageReceiver;
import org.mule.api.MuleMessage;

/**
 * <code>SaajMessageReceiver</code> TODO document
 */
public class SaajMessageReceiver extends HttpMessageReceiver {

    public SaajMessageReceiver(Connector connector, Service service,
                               InboundEndpoint endpoint)
            throws CreateException {
        super(connector, service, endpoint);
    }

    protected MessageReceiver getTargetReceiver(MuleMessage message, ImmutableEndpoint endpoint)
            throws ConnectException {
        return this;
    }
}

