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

import org.mule.transport.AbstractMessageAdapterTestCase;
import org.mule.api.transport.MessageAdapter;
import org.mule.api.MessagingException;

import java.io.FileInputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class SaajMessageAdapterWithHeadersTestCase extends AbstractMessageAdapterTestCase {

    private static String XML_GET_PEOPLE_RESPONSE = "src/test/resources/soap-get-people-request.xml";

    public Object getValidMessage() throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("TESTHEADER", "FOO");
        return SaajUtils.buildSOAPMessage(new FileInputStream(new File(XML_GET_PEOPLE_RESPONSE)), headers);
    }

    public MessageAdapter createAdapter(Object payload) throws MessagingException {
        return new SaajMessageAdapter(payload);
    }


}