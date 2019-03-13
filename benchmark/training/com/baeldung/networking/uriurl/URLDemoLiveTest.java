package com.baeldung.networking.uriurl;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FixMethodOrder
public class URLDemoLiveTest {
    private final Logger log = LoggerFactory.getLogger(URLDemoLiveTest.class);

    static String URLSTRING = "https://wordpress.org:443/support/topic/page-jumps-within-wordpress/?replies=3#post-2278484";

    // parsed locator
    static String URLPROTOCOL = "https";

    String URLAUTHORITY = "wordpress.org:443";

    static String URLHOST = "wordpress.org";

    static String URLPATH = "/support/topic/page-jumps-within-wordpress/";

    String URLFILENAME = "/support/topic/page-jumps-within-wordpress/?replies=3";

    int URLPORT = 443;

    static int URLDEFAULTPORT = 443;

    static String URLQUERY = "replies=3";

    static String URLREFERENCE = "post-2278484";

    static String URLCOMPOUND = (((((((((URLDemoLiveTest.URLPROTOCOL) + "://") + (URLDemoLiveTest.URLHOST)) + ":") + (URLDemoLiveTest.URLDEFAULTPORT)) + (URLDemoLiveTest.URLPATH)) + "?") + (URLDemoLiveTest.URLQUERY)) + "#") + (URLDemoLiveTest.URLREFERENCE);

    static URL url;

    URLConnection urlConnection = null;

    HttpURLConnection connection = null;

    BufferedReader in = null;

    String urlContent = "";

    // check parsed URL
    @Test
    public void givenURL_whenURLIsParsed_thenSuccess() {
        Assert.assertNotNull("URL is null", URLDemoLiveTest.url);
        Assert.assertEquals("URL string is not equal", URLDemoLiveTest.url.toString(), URLDemoLiveTest.URLSTRING);
        Assert.assertEquals("Protocol is not equal", URLDemoLiveTest.url.getProtocol(), URLDemoLiveTest.URLPROTOCOL);
        Assert.assertEquals("Authority is not equal", URLDemoLiveTest.url.getAuthority(), URLAUTHORITY);
        Assert.assertEquals("Host string is not equal", URLDemoLiveTest.url.getHost(), URLDemoLiveTest.URLHOST);
        Assert.assertEquals("Path string is not equal", URLDemoLiveTest.url.getPath(), URLDemoLiveTest.URLPATH);
        Assert.assertEquals("File string is not equal", URLDemoLiveTest.url.getFile(), URLFILENAME);
        Assert.assertEquals("Port number is not equal", URLDemoLiveTest.url.getPort(), URLPORT);
        Assert.assertEquals("Default port number is not equal", URLDemoLiveTest.url.getDefaultPort(), URLDemoLiveTest.URLDEFAULTPORT);
        Assert.assertEquals("Query string is not equal", URLDemoLiveTest.url.getQuery(), URLDemoLiveTest.URLQUERY);
        Assert.assertEquals("Reference string is not equal", URLDemoLiveTest.url.getRef(), URLDemoLiveTest.URLREFERENCE);
    }

    // Obtain the content from location
    @Test
    public void givenURL_whenOpenConnectionAndContentIsNotEmpty_thenSuccess() throws IOException {
        try {
            urlConnection = URLDemoLiveTest.url.openConnection();
        } catch (IOException ex) {
            urlConnection = null;
            ex.printStackTrace();
        }
        Assert.assertNotNull("URL Connection is null", urlConnection);
        connection = null;
        Assert.assertTrue("URLConnection is not HttpURLConnection", ((urlConnection) instanceof HttpURLConnection));
        if ((urlConnection) instanceof HttpURLConnection) {
            connection = ((HttpURLConnection) (urlConnection));
        }
        Assert.assertNotNull("Connection is null", connection);
        log.info((((connection.getResponseCode()) + " ") + (connection.getResponseMessage())));
        try {
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } catch (IOException ex) {
            in = null;
            ex.printStackTrace();
        }
        Assert.assertNotNull("Input stream failed", in);
        String current;
        try {
            while ((current = in.readLine()) != null) {
                urlContent += current;
            } 
        } catch (IOException ex) {
            urlContent = null;
            ex.printStackTrace();
        }
        Assert.assertNotNull("Content is null", urlContent);
        Assert.assertTrue("Content is empty", ((urlContent.length()) > 0));
    }
}

