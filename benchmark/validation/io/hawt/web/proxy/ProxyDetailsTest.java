package io.hawt.web.proxy;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ProxyDetailsTest {
    @Test
    public void testPathInfoWithUserPasswordPort() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/admin:admin@localhost/8181/jolokia/");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", "admin", details.getUserName());
        Assert.assertEquals("getPassword()", "admin", details.getPassword());
        Assert.assertEquals("getHost()", "localhost", details.getHost());
        Assert.assertEquals("getHostAndPort()", "localhost:8181", details.getHostAndPort());
        Assert.assertEquals("getPort()", 8181, details.getPort());
        Assert.assertEquals("getProxyPath()", "/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "http", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "http://localhost:8181/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testPathInfoWithUserPasswordDefaultPort() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/admin:admin@localhost//jolokia/");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", "admin", details.getUserName());
        Assert.assertEquals("getPassword()", "admin", details.getPassword());
        Assert.assertEquals("getHost()", "localhost", details.getHost());
        Assert.assertEquals("getHostAndPort()", "localhost", details.getHostAndPort());
        Assert.assertEquals("getPort()", 80, details.getPort());
        Assert.assertEquals("getProxyPath()", "/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "http", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "http://localhost/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testPathInfoWithDefaultPort() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/localhost//jolokia/");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "localhost", details.getHost());
        Assert.assertEquals("getHostAndPort()", "localhost", details.getHostAndPort());
        Assert.assertEquals("getPort()", 80, details.getPort());
        Assert.assertEquals("getProxyPath()", "/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "http", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "http://localhost/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testPathInfoWithPort() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/localhost/90/jolokia/");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "localhost", details.getHost());
        Assert.assertEquals("getHostAndPort()", "localhost:90", details.getHostAndPort());
        Assert.assertEquals("getPort()", 90, details.getPort());
        Assert.assertEquals("getProxyPath()", "/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "http", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "http://localhost:90/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testPathInfoWithWhitespace() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/http/localhost/10001/jolokia/read/java.lang:type=MemoryManager,name=Metaspace Manager/Name");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getFullProxyUrl()", "http://localhost:10001/jolokia/read/java.lang:type=MemoryManager,name=Metaspace%20Manager/Name", details.getFullProxyUrl());
    }

    @Test
    public void testDefaultPort() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/somerest-davsclaus2.rhcloud.com/cxf/crm/customerservice/customers/123");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "somerest-davsclaus2.rhcloud.com", details.getHost());
        Assert.assertEquals("getHostAndPort()", "somerest-davsclaus2.rhcloud.com", details.getHostAndPort());
        Assert.assertEquals("getPort()", 80, details.getPort());
        Assert.assertEquals("getProxyPath()", "/cxf/crm/customerservice/customers/123", details.getProxyPath());
        Assert.assertEquals("getScheme()", "http", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "http://somerest-davsclaus2.rhcloud.com/cxf/crm/customerservice/customers/123", details.getFullProxyUrl());
    }

    @Test
    public void testHttpsUrl() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/https://www.myhost.com/443/myApp/jolokia/");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "www.myhost.com", details.getHost());
        Assert.assertEquals("getHostAndPort()", "www.myhost.com:443", details.getHostAndPort());
        Assert.assertEquals("getPort()", 443, details.getPort());
        Assert.assertEquals("getProxyPath()", "/myApp/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "https", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "https://www.myhost.com:443/myApp/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testHttpsUrlWithNoPort() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/https://www.myhost.com/myApp/jolokia/");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "www.myhost.com", details.getHost());
        Assert.assertEquals("getHostAndPort()", "www.myhost.com", details.getHostAndPort());
        Assert.assertEquals("getPort()", 443, details.getPort());
        Assert.assertEquals("getProxyPath()", "/myApp/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "https", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "https://www.myhost.com/myApp/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testQueryStringWithIgnoredParameter() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/https://www.myhost.com/myApp/jolokia/");
        Mockito.when(mockReq.getQueryString()).thenReturn("url=bar");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "www.myhost.com", details.getHost());
        Assert.assertEquals("getHostAndPort()", "www.myhost.com", details.getHostAndPort());
        Assert.assertEquals("getPort()", 443, details.getPort());
        Assert.assertEquals("getProxyPath()", "/myApp/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "https", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "https://www.myhost.com/myApp/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testQueryStringWithMultipleIgnoredParameters() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/https://www.myhost.com/myApp/jolokia/");
        Mockito.when(mockReq.getQueryString()).thenReturn("url=bar&_user=test");
        ProxyDetails details = new ProxyDetails(mockReq);
        Assert.assertEquals("getUserName()", null, details.getUserName());
        Assert.assertEquals("getPassword()", null, details.getPassword());
        Assert.assertEquals("getHost()", "www.myhost.com", details.getHost());
        Assert.assertEquals("getHostAndPort()", "www.myhost.com", details.getHostAndPort());
        Assert.assertEquals("getPort()", 443, details.getPort());
        Assert.assertEquals("getProxyPath()", "/myApp/jolokia/", details.getProxyPath());
        Assert.assertEquals("getScheme()", "https", details.getScheme());
        Assert.assertEquals("getFullProxyUrl()", "https://www.myhost.com/myApp/jolokia/", details.getFullProxyUrl());
    }

    @Test
    public void testIsAllowed() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/localhost/9000/jolokia/").thenReturn("/localhost:8181/jolokia/").thenReturn("/www.myhost.com/jolokia/").thenReturn("/myhost1.com/jolokia/").thenReturn("/myhost22.com/jolokia/").thenReturn("/www.banned.com/jolokia/");
        Set<String> whitelist = new HashSet<>(Arrays.asList("localhost", "www.myhost.com"));
        List<Pattern> regexWhitelist = Collections.singletonList(Pattern.compile("myhost[0-9]+\\.com"));
        ProxyDetails details1 = new ProxyDetails(mockReq);
        ProxyDetails details2 = new ProxyDetails(mockReq);
        ProxyDetails details3 = new ProxyDetails(mockReq);
        ProxyDetails details4 = new ProxyDetails(mockReq);
        ProxyDetails details5 = new ProxyDetails(mockReq);
        ProxyDetails details6 = new ProxyDetails(mockReq);
        Assert.assertTrue("localhost/9000", details1.isAllowed(whitelist));
        Assert.assertTrue("localhost:8181", details2.isAllowed(whitelist));
        Assert.assertTrue("www.myhost.com", details3.isAllowed(whitelist));
        Assert.assertTrue("myhost1.com", details4.isAllowed(regexWhitelist));
        Assert.assertTrue("myhost22.com", details5.isAllowed(regexWhitelist));
        Assert.assertFalse("www.banned.com", details6.isAllowed(whitelist));
    }

    @Test
    public void testIsAllowedWithAllowAll() throws Exception {
        HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockReq.getPathInfo()).thenReturn("/localhost/9000/jolokia/").thenReturn("/www.myhost.com/jolokia/").thenReturn("/www.banned.com/jolokia/");
        Set<String> whitelist = new HashSet<>(Arrays.asList("*"));
        ProxyDetails details1 = new ProxyDetails(mockReq);
        ProxyDetails details2 = new ProxyDetails(mockReq);
        ProxyDetails details3 = new ProxyDetails(mockReq);
        Assert.assertTrue("localhost", details1.isAllowed(whitelist));
        Assert.assertTrue("www.myhost.com", details2.isAllowed(whitelist));
        Assert.assertTrue("www.banned.com", details3.isAllowed(whitelist));
    }
}

