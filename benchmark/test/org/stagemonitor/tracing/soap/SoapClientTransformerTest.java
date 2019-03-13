package org.stagemonitor.tracing.soap;


import java.net.URL;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.Handler;
import org.junit.Test;
import org.stagemonitor.core.util.AbstractEmbeddedServerTest;


public class SoapClientTransformerTest extends AbstractEmbeddedServerTest {
    @Test
    public void test() throws Exception {
        QName serviceName = new QName("http://www.jboss.org/jbossas/quickstarts/wshelloworld/HelloWorld", "HelloWorldService");
        Service service = Service.create(new URL(("http://localhost:" + (getPort()))), serviceName);
        SoapClientTransformerTest.HelloWorldService helloWorldService = service.getPort(SoapClientTransformerTest.HelloWorldService.class);
        assertThat(helloWorldService).isInstanceOf(BindingProvider.class);
        final BindingProvider bindingProvider = ((BindingProvider) (helloWorldService));
        boolean clientHandlerFound = false;
        for (Handler handler : bindingProvider.getBinding().getHandlerChain()) {
            if (handler instanceof TracingClientSOAPHandler) {
                clientHandlerFound = true;
            }
        }
        assertThat(clientHandlerFound).overridingErrorMessage("No %s found in %s", TracingClientSOAPHandler.class.getSimpleName(), bindingProvider.getBinding().getHandlerChain()).isTrue();
    }

    @WebService(targetNamespace = "http://www.jboss.org/jbossas/quickstarts/wshelloworld/HelloWorld")
    public interface HelloWorldService {
        @WebMethod
        String sayHello();
    }
}

