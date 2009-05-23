/*
 * $Id: NamespaceHandler.vm 10621 2008-01-30 12:15:16Z dirk.olmes $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.saaj.config;

import org.mule.config.spring.parsers.generic.OrphanDefinitionParser;
import org.mule.config.spring.parsers.generic.ChildDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.GenericEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.TransformerDefinitionParser;
import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.transport.saaj.*;
import org.mule.endpoint.URIBuilder;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Registers a Bean Definition Parser for handling <code><saaj:connector></code> elements.
 */
public class SaajNamespaceHandler extends AbstractMuleNamespaceHandler {
    public void init() {
        registerBeanDefinitionParser("soapbody-to-document-transformer",
                new TransformerDefinitionParser(SOAPBodyToDocumentTransformer.class));
        registerBeanDefinitionParser("document-to-soap-message-transformer",
                new TransformerDefinitionParser(DocumentToSOAPMessageTransformer.class));
        

    }
}