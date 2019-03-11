package org.jboss.as.test.integration.naming.remote.multiple;


import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Regression test for AS7-5718
 *
 * @author jlivings@redhat.com
 */
@RunWith(Arquillian.class)
@RunAsClient
public class NestedRemoteContextTestCase {
    @ArquillianResource(CallEjbServlet.class)
    private URL callEjbUrl;

    private static final Package thisPackage = NestedRemoteContextTestCase.class.getPackage();

    @Test
    public void testLifeCycle() throws Exception {
        String result = HttpRequest.get(((callEjbUrl.toExternalForm()) + "CallEjbServlet"), 1000, TimeUnit.SECONDS);
        Assert.assertEquals("TestHello", result);
    }
}

