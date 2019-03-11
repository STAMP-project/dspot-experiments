package org.jboss.as.test.integration.web.response;


import HttpServletResponse.SC_NOT_FOUND;
import HttpServletResponse.SC_OK;
import java.net.URL;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests the "default servlet" of the web container
 *
 * @author Jaikiran Pai
 */
@RunWith(Arquillian.class)
@RunAsClient
public class DefaultServletTestCase {
    private static final String WEB_APP_CONTEXT = "default-servlet-test";

    private static final String APP_XHTML_FILE_NAME = "app.xhtml";

    private static final Logger logger = Logger.getLogger(DefaultServletTestCase.class);

    @ArquillianResource
    URL url;

    private HttpClient httpclient;

    /**
     * Tests that the default servlet doesn't show the source (code) of a resource when an incorrect URL is used to access that resource.
     *
     * @throws Exception
     * 		
     * @see https://developer.jboss.org/thread/266805 for more details
     */
    @Test
    public void testForbidSourceFileAccess() throws Exception {
        // first try accessing the valid URL and expect it to serve the right content
        final String correctURL = (url.toString()) + (DefaultServletTestCase.APP_XHTML_FILE_NAME);
        final HttpGet httpGetCorrectURL = new HttpGet(correctURL);
        final HttpResponse response = this.httpclient.execute(httpGetCorrectURL);
        Assert.assertEquals(("Unexpected response code for URL " + correctURL), SC_OK, response.getStatusLine().getStatusCode());
        final String content = EntityUtils.toString(response.getEntity());
        Assert.assertTrue(("Unexpected content served at " + correctURL), content.contains("Hello World"));
        // now try accessing the same URL with a "." at the end of the resource name.
        // This should throw a 404 error and NOT show up the "source" content of the resource
        final String nonExistentURL = ((url.toString()) + (DefaultServletTestCase.APP_XHTML_FILE_NAME)) + ".";
        final HttpGet httpGetNonExistentURL = new HttpGet(nonExistentURL);
        final HttpResponse responseForNonExistentURL = this.httpclient.execute(httpGetNonExistentURL);
        Assert.assertEquals(("Unexpected response code for URL " + nonExistentURL), SC_NOT_FOUND, responseForNonExistentURL.getStatusLine().getStatusCode());
    }
}

