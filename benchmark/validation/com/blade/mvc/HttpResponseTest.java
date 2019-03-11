package com.blade.mvc;


import Const.CONTENT_TYPE_HTML;
import com.blade.BaseTestCase;
import com.blade.mvc.http.Response;
import java.util.Collections;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * HttpResponse TestCase
 *
 * @author biezhi
2017/6/3
 */
public class HttpResponseTest extends BaseTestCase {
    private static final String CONTENT_TYPE = "Content-Type";

    @Test
    public void testStatus() {
        Response mockResponse = mockHttpResponse(666);
        Assert.assertEquals(666, mockResponse.statusCode());
    }

    @Test
    public void testBadRequest() {
        Response mockResponse = mockHttpResponse(200);
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        response.badRequest();
        Assert.assertEquals(400, response.statusCode());
    }

    @Test
    public void testUnauthorized() {
        Response mockResponse = mockHttpResponse(200);
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        response.unauthorized();
        Assert.assertEquals(401, response.statusCode());
    }

    @Test
    public void testNotFound() {
        Response mockResponse = mockHttpResponse(200);
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        response.notFound();
        Assert.assertEquals(404, response.statusCode());
    }

    @Test
    public void testContentType() {
        Response mockResponse = mockHttpResponse(200);
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        response.contentType(CONTENT_TYPE_HTML);
        Assert.assertEquals(CONTENT_TYPE_HTML, response.contentType());
        response.contentType("hello.world");
        Assert.assertEquals("hello.world", response.contentType());
    }

    @Test
    public void testHeaders() {
        Response mockResponse = mockHttpResponse(200);
        Mockito.when(mockResponse.headers()).thenReturn(new HashMap());
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        Assert.assertEquals(0, response.headers().size());
        response.header("a", "123");
        Assert.assertEquals(1, response.headers().size());
    }

    @Test
    public void testHeader() {
        Response mockResponse = mockHttpResponse(200);
        Mockito.when(mockResponse.headers()).thenReturn(Collections.singletonMap("Server", "Nginx"));
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        Assert.assertEquals(1, response.headers().size());
        Assert.assertEquals("Nginx", response.headers().get("Server"));
    }

    @Test
    public void testCookie() {
        Response mockResponse = mockHttpResponse(200);
        Mockito.when(mockResponse.cookies()).thenReturn(Collections.singletonMap("c1", "value1"));
        Response response = new com.blade.mvc.http.HttpResponse(mockResponse);
        Assert.assertEquals(1, response.cookies().size());
        Assert.assertEquals("value1", response.cookies().get("c1"));
    }
}

