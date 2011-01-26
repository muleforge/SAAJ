package org.mule.module.saaj.example;

import org.mule.api.lifecycle.Callable;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleException;

public class ForwardingComponent implements Callable {

    public Object onCall(MuleEventContext context) throws MuleException {

        context.dispatchEvent(context.getMessage(), "jms://messages");

        return "<status>SUBMITTED</status>";
    }
}