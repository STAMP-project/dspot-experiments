package com.baeldung.networking.uriurl;


import java.io.BufferedReader;
import java.net.URI;
import java.net.URL;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FixMethodOrder
public class URIDemoLiveTest {
    private final Logger log = LoggerFactory.getLogger(URIDemoLiveTest.class);

    String URISTRING = "https://wordpress.org:443/support/topic/page-jumps-within-wordpress/?replies=3#post-2278484";

    // parsed locator
    static String URISCHEME = "https";

    String URISCHEMESPECIFIC;

    static String URIHOST = "wordpress.org";

    static String URIAUTHORITY = "wordpress.org:443";

    static String URIPATH = "/support/topic/page-jumps-within-wordpress/";

    int URIPORT = 443;

    static int URIDEFAULTPORT = 443;

    static String URIQUERY = "replies=3";

    static String URIFRAGMENT = "post-2278484";

    static String URICOMPOUND = (((((((((URIDemoLiveTest.URISCHEME) + "://") + (URIDemoLiveTest.URIHOST)) + ":") + (URIDemoLiveTest.URIDEFAULTPORT)) + (URIDemoLiveTest.URIPATH)) + "?") + (URIDemoLiveTest.URIQUERY)) + "#") + (URIDemoLiveTest.URIFRAGMENT);

    static URI uri;

    URL url;

    BufferedReader in = null;

    String URIContent = "";

    // check parsed URL
    @Test
    public void givenURI_whenURIIsParsed_thenSuccess() {
        Assert.assertNotNull("URI is null", URIDemoLiveTest.uri);
        Assert.assertEquals("URI string is not equal", URIDemoLiveTest.uri.toString(), URISTRING);
        Assert.assertEquals("Scheme is not equal", URIDemoLiveTest.uri.getScheme(), URIDemoLiveTest.URISCHEME);
        Assert.assertEquals("Authority is not equal", URIDemoLiveTest.uri.getAuthority(), URIDemoLiveTest.URIAUTHORITY);
        Assert.assertEquals("Host string is not equal", URIDemoLiveTest.uri.getHost(), URIDemoLiveTest.URIHOST);
        Assert.assertEquals("Path string is not equal", URIDemoLiveTest.uri.getPath(), URIDemoLiveTest.URIPATH);
        Assert.assertEquals("Port number is not equal", URIDemoLiveTest.uri.getPort(), URIPORT);
        Assert.assertEquals("Query string is not equal", URIDemoLiveTest.uri.getQuery(), URIDemoLiveTest.URIQUERY);
        Assert.assertEquals("Fragment string is not equal", URIDemoLiveTest.uri.getFragment(), URIDemoLiveTest.URIFRAGMENT);
    }
}

