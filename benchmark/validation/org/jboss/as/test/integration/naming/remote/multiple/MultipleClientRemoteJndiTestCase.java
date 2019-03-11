package org.jboss.as.test.integration.naming.remote.multiple;


import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
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
public class MultipleClientRemoteJndiTestCase {
    @ArquillianResource(RunRmiServlet.class)
    @OperateOnDeployment("one")
    private URL urlOne;

    @ArquillianResource(RunRmiServlet.class)
    @OperateOnDeployment("two")
    private URL urlTwo;

    private static final Package thisPackage = MultipleClientRemoteJndiTestCase.class.getPackage();

    @Test
    public void testLifeCycle() throws Exception {
        String result1 = HttpRequest.get(((urlOne.toExternalForm()) + "RunRmiServlet"), 1000, TimeUnit.SECONDS);
        Assert.assertEquals("Test", result1);
        String result2 = HttpRequest.get(((urlTwo.toExternalForm()) + "RunRmiServlet"), 1000, TimeUnit.SECONDS);
        Assert.assertEquals("Test", result2);
    }
}

