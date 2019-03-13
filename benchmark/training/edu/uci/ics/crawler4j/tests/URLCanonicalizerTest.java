package edu.uci.ics.crawler4j.tests;


import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.junit.Assert;
import org.junit.Test;


public class URLCanonicalizerTest {
    @Test
    public void testCanonizalier() throws UnsupportedEncodingException {
        Assert.assertEquals("http://www.example.com/display?category=foo%2Fbar%2Bbaz", URLCanonicalizer.getCanonicalURL("http://www.example.com/display?category=foo/bar+baz"));
        Assert.assertEquals("http://www.example.com/?q=a%2Bb", URLCanonicalizer.getCanonicalURL("http://www.example.com/?q=a+b"));
        Assert.assertEquals("http://www.example.com/display?category=foo%2Fbar%2Bbaz", URLCanonicalizer.getCanonicalURL("http://www.example.com/display?category=foo%2Fbar%2Bbaz"));
        Assert.assertEquals("http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037", URLCanonicalizer.getCanonicalURL("http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037"));
        Assert.assertEquals("http://hostname.com/", URLCanonicalizer.getCanonicalURL("http://hostname.com"));
        Assert.assertEquals("http://hostname.com/", URLCanonicalizer.getCanonicalURL("http://HOSTNAME.com"));
        Assert.assertEquals("http://www.example.com/index.html", URLCanonicalizer.getCanonicalURL("http://www.example.com/index.html?&"));
        Assert.assertEquals("http://www.example.com/index.html", URLCanonicalizer.getCanonicalURL("http://www.example.com/index.html?"));
        Assert.assertEquals("http://www.example.com/", URLCanonicalizer.getCanonicalURL("http://www.example.com"));
        Assert.assertEquals("http://www.example.com/bar.html", URLCanonicalizer.getCanonicalURL("http://www.example.com:80/bar.html"));
        Assert.assertEquals("http://www.example.com/index.html?name=test&rame=base", URLCanonicalizer.getCanonicalURL("http://www.example.com/index.html?name=test&rame=base#123"));
        Assert.assertEquals("http://www.example.com/~username/", URLCanonicalizer.getCanonicalURL("http://www.example.com/%7Eusername/"));
        Assert.assertEquals("http://www.example.com/A/B/index.html", URLCanonicalizer.getCanonicalURL("http://www.example.com//A//B/index.html"));
        Assert.assertEquals("http://www.example.com/index.html?x=y", URLCanonicalizer.getCanonicalURL("http://www.example.com/index.html?&x=y"));
        Assert.assertEquals("http://www.example.com/a.html", URLCanonicalizer.getCanonicalURL("http://www.example.com/../../a.html"));
        Assert.assertEquals("http://www.example.com/a/c/d.html", URLCanonicalizer.getCanonicalURL("http://www.example.com/../a/b/../c/./d.html"));
        Assert.assertEquals("http://foo.bar.com/?baz=1", URLCanonicalizer.getCanonicalURL("http://foo.bar.com?baz=1"));
        Assert.assertEquals("http://www.example.com/index.html?c=d&e=f&a=b", URLCanonicalizer.getCanonicalURL("http://www.example.com/index.html?&c=d&e=f&a=b"));
        Assert.assertEquals("http://www.example.com/index.html?q=a%20b", URLCanonicalizer.getCanonicalURL("http://www.example.com/index.html?q=a b"));
        Assert.assertEquals("http://www.example.com/search?width=100%&height=100%", URLCanonicalizer.getCanonicalURL("http://www.example.com/search?width=100%&height=100%"));
        Assert.assertEquals("http://foo.bar/mydir/myfile?page=2", URLCanonicalizer.getCanonicalURL("?page=2", "http://foo.bar/mydir/myfile"));
        // test href with charset
        Assert.assertEquals("http://www.example.com/3.asp?DengJh=%BA%E91700718", URLCanonicalizer.getCanonicalURL("3.asp?DengJh=?1700718", "http://www.example.com", Charset.forName("gb2312")));
        // https://github.com/yasserg/crawler4j/issues/26
        Assert.assertEquals("http://seagateplastics.com/Stock_Plastics_Catalog/images_catalog/SG2078%20PDF%20(1).pdf)", URLCanonicalizer.getCanonicalURL("http://seagateplastics.com/Stock_Plastics_Catalog/images_catalog/SG2078 PDF (1).pdf)"));
        Assert.assertEquals("http://www.example.com/search/?query=hello%E3%80%80world", URLCanonicalizer.getCanonicalURL("http://www.example.com/search/?query=hello?world"));
        // 
    }
}

