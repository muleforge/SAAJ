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

import org.mule.tck.FunctionalTestCase;

/**
 * TODO
 */
public class SaajNamespaceHandlerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "saaj-namespace-config.xml";
    }

    public void testSaajConfig() throws Exception
    {
        SaajConnector c = (SaajConnector) muleContext.getRegistry().lookupConnector("saajConnector");
        assertNotNull(c);
        assertTrue(c.isConnected());
        assertTrue(c.isStarted());
        //TODO Assert specific properties are configured correctly
    }
}
