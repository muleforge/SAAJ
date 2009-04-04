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

import org.mule.tck.AbstractMuleTestCase;
import org.mule.api.endpoint.EndpointURI;
import org.mule.endpoint.MuleEndpointURI;

public class SaajEndpointTestCase extends AbstractMuleTestCase {
   
    public void testValidEndpointURI() throws Exception {

        EndpointURI url = new MuleEndpointURI("saaj://localhost:7856/people");
        url.initialise();
        assertEquals("saaj", url.getScheme());
        assertNull(url.getEndpointName());
        assertEquals(7856, url.getPort());
        assertEquals("localhost", url.getHost());
        assertEquals("saaj://localhost:7856/people", url.getAddress());
        assertEquals(0, url.getParams().size());
    }

}
