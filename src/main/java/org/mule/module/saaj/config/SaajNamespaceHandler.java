/*
 * $Id: NamespaceHandler.vm 10621 2008-01-30 12:15:16Z dirk.olmes $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.saaj.config;

import org.mule.config.spring.parsers.specific.TransformerDefinitionParser;
import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.module.saaj.*;

/**
 * Registers a Bean Definition Parser for handling the SAAJ transformers.
 */
public class SaajNamespaceHandler extends AbstractMuleNamespaceHandler {
    public void init() {
        registerBeanDefinitionParser("soap-message-to-document-transformer",
                new TransformerDefinitionParser(SOAPMessageToDocumentTransformer.class));
        registerBeanDefinitionParser("document-to-soap-message-transformer",
                new TransformerDefinitionParser(DocumentToSOAPMessageTransformer.class));
        
    }
}