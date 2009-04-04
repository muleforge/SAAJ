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

import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;

import java.io.FileInputStream;
import java.io.File;

public class SaajConnectorTestCase extends AbstractConnectorTestCase {

    private static String SOAP_GET_PEOPLE_RESPONSE = "src/test/resources/soap-people-response.xml";

    public Connector createConnector() throws Exception {
        SaajConnector c = new SaajConnector();
        c.setName("Test");
        return c;
    }

    public String getTestEndpointURI() {
        return "saaj://localhost:9756/people";
    }

    public Object getValidMessage() throws Exception {
        return SaajUtils.buildSOAPMessage(new FileInputStream(new File(SOAP_GET_PEOPLE_RESPONSE)));
    }
}
