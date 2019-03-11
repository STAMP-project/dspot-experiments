package org.jboss.as.test.integration.ws.context.application;


import java.net.URL;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;


/**
 * Base class for context.application tests. Unit test checks if context-root parameter is honored regardles of deploy content.
 *
 * @author baranowb
 */
public class ContextRootTestBase {
    protected static final String TEST_PATH = "/test1/";

    @ArquillianResource
    URL baseUrl;

    @Test
    public void testContextRoot() {
        Assert.assertEquals("Wrong context root!", ContextRootTestBase.TEST_PATH, baseUrl.getPath());
    }
}

