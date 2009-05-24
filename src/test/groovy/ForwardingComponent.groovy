import org.mule.api.lifecycle.Callable
import org.mule.api.MuleEventContext
import groovy.xml.MarkupBuilder

class ForwardingComponent implements Callable {

    public Object onCall(MuleEventContext context) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
        xml.result() {
            id(1234)
        }

        context.dispatchEvent(context.getMessage(), "vm://messages")

        return writer.toString();
    }
}