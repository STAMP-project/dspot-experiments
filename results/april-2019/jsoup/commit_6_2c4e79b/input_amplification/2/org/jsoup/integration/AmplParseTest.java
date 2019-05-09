package org.jsoup.integration;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;


public class AmplParseTest {
    @Test(timeout = 10000)
    public void testGoogleSearchIpod_literalMutationNumber14851_failAssert0_add18432_failAssert0() throws IOException {
        try {
            {
                File in = AmplParseTest.getFile("/htmltests/google-ipod.html");
                Document doc = Jsoup.parse(in, "UTF-8", "http://www.google.com/search?hl=en&q=ipod&aq=f&oq=&aqi=g10");
                doc.title();
                Elements results = doc.select("h3.r > a");
                results.size();
                results.get(-1).attr("href");
                results.get(-1).attr("href");
                results.get(1).attr("href");
                org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber14851 should have thrown ArrayIndexOutOfBoundsException");
            }
            org.junit.Assert.fail("testGoogleSearchIpod_literalMutationNumber14851_failAssert0_add18432 should have thrown ArrayIndexOutOfBoundsException");
        } catch (ArrayIndexOutOfBoundsException expected) {
            Assert.assertEquals(null, expected.getMessage());
        }
    }

    public static File getFile(String resourceName) {
        try {
            File file = new File(AmplParseTest.class.getResource(resourceName).toURI());
            return file;
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    public static InputStream inputStreamFrom(String s) {
        try {
            return new ByteArrayInputStream(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

