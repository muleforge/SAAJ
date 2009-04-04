package org.mule.transport.saaj;

import org.mule.transformer.AbstractMessageAwareTransformer;
import org.mule.api.transformer.TransformerException;
import org.mule.api.context.MuleContextAware;
import org.mule.api.MuleContext;
import org.mule.api.MuleMessage;
import org.mule.transport.saaj.i18n.SaajMessages;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPException;

/**
 * <code>MIMEHeaderTransformer</code> Add a MIME header to a <code>SOAPMessage</code>
 */
public class MIMEHeaderTransformer extends AbstractMessageAwareTransformer implements MuleContextAware {

    private MuleContext muleContext;

    private String key;
    private String value;

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Object transform(MuleMessage message, String s) throws TransformerException {
        logger.debug(String.format("Setting MIME header %s to %s", key, value));

        if (muleContext != null && muleContext.getExpressionManager().isValidExpression(value)) {
            value = (String) muleContext.getExpressionManager().evaluate(value, message);
        }

        SOAPMessage soapMessage = (SOAPMessage) message.getPayload();
        soapMessage.getMimeHeaders().addHeader(key, value);
        try {
            soapMessage.saveChanges();
        } catch (SOAPException e) {
            throw new TransformerException(SaajMessages.failedToAddMIMEHeader(), e);
        }
        return message;
    }

    public void setMuleContext(MuleContext context) {
        this.muleContext = context;
    }

}
