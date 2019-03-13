package org.javaee7.cdi.nobeans.el.injection;


import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import java.net.URL;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class ScopedBeanTest {
    private static final String WEBAPP_SRC = "src/main/webapp";

    @ArquillianResource
    private URL base;

    @Test
    public void checkRenderedPage() throws Exception {
        WebConversation conv = new WebConversation();
        GetMethodWebRequest getRequest = new GetMethodWebRequest(((base) + "/faces/index.xhtml"));
        String responseText = conv.getResponse(getRequest).getText();
        assert responseText.contains("Hello there!");
    }
}

