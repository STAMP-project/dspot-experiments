package com.blade.mvc;


import Const.CONTENT_TYPE_HTML;
import Const.CONTENT_TYPE_JSON;
import HttpMethod.AFTER;
import HttpMethod.BEFORE;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.POST;
import HttpMethod.PUT;
import com.blade.BaseTestCase;
import com.blade.mvc.http.Cookie;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.multipart.FileItem;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * HttpRequest TestCase
 *
 * @author biezhi
2017/6/3
 */
public class HttpRequestTest extends BaseTestCase {
    @Test
    public void testMethod() {
        Assert.assertEquals(mockHttpRequest("GET").method(), "GET");
        Assert.assertEquals(mockHttpRequest("GET").httpMethod(), GET);
        Assert.assertEquals(mockHttpRequest("POST").method(), "POST");
        Assert.assertEquals(mockHttpRequest("POST").httpMethod(), POST);
        Assert.assertEquals(mockHttpRequest("PUT").method(), "PUT");
        Assert.assertEquals(mockHttpRequest("PUT").httpMethod(), PUT);
        Assert.assertEquals(mockHttpRequest("DELETE").method(), "DELETE");
        Assert.assertEquals(mockHttpRequest("DELETE").httpMethod(), DELETE);
        Assert.assertEquals(mockHttpRequest("BEFORE").method(), "BEFORE");
        Assert.assertEquals(mockHttpRequest("BEFORE").httpMethod(), BEFORE);
        Assert.assertEquals(mockHttpRequest("AFTER").method(), "AFTER");
        Assert.assertEquals(mockHttpRequest("AFTER").httpMethod(), AFTER);
    }

    @Test
    public void testHost() {
        HttpRequest request = mockHttpRequest("GET");
        Mockito.when(request.host()).thenReturn("127.0.0.1");
        Assert.assertEquals(request.host(), "127.0.0.1");
        Mockito.when(request.host()).thenReturn("localhost");
        Assert.assertEquals(request.host(), "localhost");
    }

    @Test
    public void testCookie() {
        Map<String, Cookie> cookieMap = new HashMap<>();
        Cookie c1 = new Cookie();
        c1.name("c1");
        c1.value("hello1");
        cookieMap.put("c1", c1);
        Cookie c2 = new Cookie();
        c2.name("c1");
        c2.value("hello1");
        c2.httpOnly(true);
        cookieMap.put("c2", c2);
        Cookie c3 = new Cookie();
        c3.name("c3");
        c3.value("hello3");
        c3.secure(false);
        cookieMap.put("c3", c3);
        Cookie c4 = new Cookie();
        c4.name("c4");
        c4.value("hello4");
        c4.domain("www.github.com");
        c4.path("/github");
        cookieMap.put("c4", c4);
        HttpRequest request = mockHttpRequest("GET");
        Mockito.when(request.cookies()).thenReturn(cookieMap);
        Mockito.when(request.cookie("c1")).thenReturn(cookieMap.get("c1").value());
        Mockito.when(request.cookieRaw("c2")).thenReturn(cookieMap.get("c2"));
        Mockito.when(request.cookieRaw("c3")).thenReturn(cookieMap.get("c3"));
        Mockito.when(request.cookieRaw("c4")).thenReturn(cookieMap.get("c4"));
        Assert.assertEquals(request.cookies(), cookieMap);
        Assert.assertEquals(request.cookies().size(), cookieMap.size());
        Assert.assertEquals(request.cookie("c1"), "hello1");
        Assert.assertTrue(request.cookieRaw("c2").httpOnly());
        Assert.assertFalse(request.cookieRaw("c3").secure());
        Assert.assertEquals(request.cookieRaw("c3").path(), "/");
        Assert.assertEquals(request.cookieRaw("c4").domain(), "www.github.com");
        Assert.assertEquals(request.cookieRaw("c4").path(), "/github");
    }

    @Test
    public void testPathParam() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("id", "6");
        pathParams.put("age", "24");
        pathParams.put("name", "jack");
        Mockito.when(mockRequest.pathParams()).thenReturn(pathParams);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals(Long.valueOf(6), request.pathLong("id"));
        Assert.assertEquals(Integer.valueOf(24), request.pathInt("age"));
        Assert.assertEquals("jack", request.pathString("name"));
    }

    @Test
    public void testUri() throws Exception {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.url()).thenReturn("/a");
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals("/a", request.uri());
        Mockito.when(mockRequest.url()).thenReturn("/a/b?username=jack");
        request = new HttpRequest(mockRequest);
        Assert.assertEquals("/a/b", request.uri());
    }

    @Test
    public void testUrl() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.url()).thenReturn("/hello?name=q1");
        Assert.assertEquals("/hello?name=q1", mockRequest.url());
    }

    @Test
    public void testUserAgent() {
        Map<String, String> headers = Collections.singletonMap("User-Agent", firefoxUA);
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.headers()).thenReturn(headers);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals(firefoxUA, request.userAgent());
    }

    @Test
    public void testProtocol() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.protocol()).thenReturn("HTTP/1.1");
        Assert.assertEquals("HTTP/1.1", mockRequest.protocol());
    }

    @Test
    public void testQueryString() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.url()).thenReturn("/hello?name=q1");
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals("name=q1", request.queryString());
    }

    @Test
    public void testQueryParam() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, List<String>> parameters = new HashMap<>();
        parameters.put("name", Collections.singletonList("jack"));
        parameters.put("price", Collections.singletonList("22.1"));
        parameters.put("age", Collections.singletonList("25"));
        parameters.put("id", Collections.singletonList("220291"));
        Mockito.when(mockRequest.parameters()).thenReturn(parameters);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals("jack", request.query("name").get());
        Assert.assertEquals(Double.valueOf(22.1), request.queryDouble("price").get());
        Assert.assertEquals(Long.valueOf(220291), request.queryLong("id").get());
        Assert.assertEquals(Integer.valueOf(25), request.queryInt("age").get());
    }

    @Test
    public void testAddress() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.address()).thenReturn("127.0.0.1");
        Assert.assertEquals("127.0.0.1", mockRequest.address());
    }

    @Test
    public void testContentType() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.contentType()).thenReturn(CONTENT_TYPE_HTML);
        Assert.assertEquals(CONTENT_TYPE_HTML, mockRequest.contentType());
        Mockito.when(mockRequest.contentType()).thenReturn(CONTENT_TYPE_JSON);
        Assert.assertEquals(CONTENT_TYPE_JSON, mockRequest.contentType());
    }

    @Test
    public void testIsSecure() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.isSecure()).thenReturn(false);
        Assert.assertEquals(Boolean.FALSE, mockRequest.isSecure());
    }

    @Test
    public void testIsAjax() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, String> headers = Collections.singletonMap("x-requested-with", "XMLHttpRequest");
        Mockito.when(mockRequest.headers()).thenReturn(headers);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals(Boolean.TRUE, request.isAjax());
        Mockito.when(mockRequest.headers()).thenReturn(Collections.EMPTY_MAP);
        request = new HttpRequest(mockRequest);
        Assert.assertEquals(Boolean.FALSE, request.isAjax());
    }

    @Test
    public void testIsIE() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, String> headers = Collections.singletonMap("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)");
        Mockito.when(mockRequest.headers()).thenReturn(headers);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals(Boolean.TRUE, request.isIE());
        Mockito.when(mockRequest.headers()).thenReturn(Collections.EMPTY_MAP);
        request = new HttpRequest(mockRequest);
        Assert.assertEquals(Boolean.FALSE, request.isIE());
    }

    @Test
    public void testHeaders() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, String> headers = new HashMap<>();
        headers.put("h1", "a1");
        headers.put("h2", "a2");
        Mockito.when(mockRequest.headers()).thenReturn(headers);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals("a1", request.header("h1"));
        Assert.assertEquals("a2", request.header("h2"));
    }

    @Test
    public void testKeepAlive() {
        Request mockRequest = mockHttpRequest("GET");
        Mockito.when(mockRequest.keepAlive()).thenReturn(true);
        Assert.assertEquals(Boolean.TRUE, mockRequest.keepAlive());
    }

    @Test
    public void testAttribute() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, Object> attr = new HashMap<>();
        attr.put("name", "biezhi");
        Mockito.when(mockRequest.attributes()).thenReturn(attr);
        Request request = new HttpRequest(mockRequest);
        Assert.assertEquals("biezhi", request.attribute("name"));
    }

    @Test
    public void testFileItems() {
        Request mockRequest = mockHttpRequest("GET");
        Map<String, FileItem> attr = new HashMap<>();
        FileItem fileItem = new FileItem();
        fileItem.setName("file");
        fileItem.setFileName("hello.png");
        fileItem.setPath("/usr/hello.png");
        fileItem.setContentType("image/png");
        fileItem.setLength(20445L);
        attr.put("img", fileItem);
        Mockito.when(mockRequest.fileItems()).thenReturn(attr);
        Request request = new HttpRequest(mockRequest);
        FileItem img = request.fileItem("img").get();
        TestCase.assertNotNull(img);
        TestCase.assertNull(img.getFile());
        Assert.assertEquals("file", img.getName());
        Assert.assertEquals("hello.png", img.getFileName());
        Assert.assertEquals("/usr/hello.png", img.getPath());
        Assert.assertEquals(Long.valueOf(20445), Optional.of(img.getLength()).get());
        Assert.assertEquals("image/png", img.getContentType());
    }
}

