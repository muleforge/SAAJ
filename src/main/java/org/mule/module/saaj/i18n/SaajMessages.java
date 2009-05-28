/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.saaj.i18n;

import org.mule.config.i18n.MessageFactory;
import org.mule.config.i18n.Message;

public class SaajMessages extends MessageFactory {
    private static final String BUNDLE_PATH = getBundlePath("saaj");

    private static final SaajMessages factory = new SaajMessages();

    public static Message failedToExtractSoapBody() {
        return factory.createMessage(BUNDLE_PATH, 1);
    }

    public static Message failedToCreateDocumentBuilder() {
        return factory.createMessage(BUNDLE_PATH, 2);
    }

    public static Message couldNotCreateSaajConnection() {
        return factory.createMessage(BUNDLE_PATH, 3);
    }

    public static Message soapResponseIsNull() {
        return factory.createMessage(BUNDLE_PATH, 4);
    }

    public static Message soapFault(String faultCode, String faultString, String faultActor, String faultDetails) {
        return factory.createMessage(BUNDLE_PATH, 5, faultCode, faultString, faultActor, faultDetails);
    }

    public static Message failedToCreateDocumentFactories() {
        return factory.createMessage(BUNDLE_PATH, 6);
    }

    public static Message failedToBuildSOAPMessage() {
        return factory.createMessage(BUNDLE_PATH, 7);
    }

    public static Message failedToAddMIMEHeader() {
        return factory.createMessage(BUNDLE_PATH, 8);
    }

     public static Message failedToGetSOAPMessageAsByteArray() {
        return factory.createMessage(BUNDLE_PATH, 9);
    }

    public static Message failedToEvaluateFault() {
        return factory.createMessage(BUNDLE_PATH, 10);
    }
}
