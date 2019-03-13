package com.android.volley.toolbox;


import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.mock.TestRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class BaseHttpStackTest {
    private static final Request<?> REQUEST = new TestRequest.Get();

    private static final Map<String, String> ADDITIONAL_HEADERS = Collections.emptyMap();

    @Mock
    private InputStream mContent;

    @Test
    public void legacyRequestWithoutBody() throws Exception {
        BaseHttpStack stack = new BaseHttpStack() {
            @Override
            public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws AuthFailureError, IOException {
                Assert.assertSame(BaseHttpStackTest.REQUEST, request);
                Assert.assertSame(BaseHttpStackTest.ADDITIONAL_HEADERS, additionalHeaders);
                return new HttpResponse(12345, Collections.<Header>emptyList());
            }
        };
        org.apache.http.HttpResponse resp = stack.performRequest(BaseHttpStackTest.REQUEST, BaseHttpStackTest.ADDITIONAL_HEADERS);
        Assert.assertEquals(12345, resp.getStatusLine().getStatusCode());
        Assert.assertEquals(0, resp.getAllHeaders().length);
        Assert.assertNull(resp.getEntity());
    }

    @Test
    public void legacyResponseWithBody() throws Exception {
        BaseHttpStack stack = new BaseHttpStack() {
            @Override
            public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws AuthFailureError, IOException {
                Assert.assertSame(BaseHttpStackTest.REQUEST, request);
                Assert.assertSame(BaseHttpStackTest.ADDITIONAL_HEADERS, additionalHeaders);
                return new HttpResponse(12345, Collections.<Header>emptyList(), 555, mContent);
            }
        };
        org.apache.http.HttpResponse resp = stack.performRequest(BaseHttpStackTest.REQUEST, BaseHttpStackTest.ADDITIONAL_HEADERS);
        Assert.assertEquals(12345, resp.getStatusLine().getStatusCode());
        Assert.assertEquals(0, resp.getAllHeaders().length);
        Assert.assertEquals(555L, resp.getEntity().getContentLength());
        Assert.assertSame(mContent, resp.getEntity().getContent());
    }

    @Test
    public void legacyResponseHeaders() throws Exception {
        BaseHttpStack stack = new BaseHttpStack() {
            @Override
            public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws AuthFailureError, IOException {
                Assert.assertSame(BaseHttpStackTest.REQUEST, request);
                Assert.assertSame(BaseHttpStackTest.ADDITIONAL_HEADERS, additionalHeaders);
                List<Header> headers = new ArrayList<>();
                headers.add(new Header("HeaderA", "ValueA"));
                headers.add(new Header("HeaderB", "ValueB_1"));
                headers.add(new Header("HeaderB", "ValueB_2"));
                return new HttpResponse(12345, headers);
            }
        };
        org.apache.http.HttpResponse resp = stack.performRequest(BaseHttpStackTest.REQUEST, BaseHttpStackTest.ADDITIONAL_HEADERS);
        Assert.assertEquals(12345, resp.getStatusLine().getStatusCode());
        Assert.assertEquals(3, resp.getAllHeaders().length);
        Assert.assertEquals("HeaderA", resp.getAllHeaders()[0].getName());
        Assert.assertEquals("ValueA", resp.getAllHeaders()[0].getValue());
        Assert.assertEquals("HeaderB", resp.getAllHeaders()[1].getName());
        Assert.assertEquals("ValueB_1", resp.getAllHeaders()[1].getValue());
        Assert.assertEquals("HeaderB", resp.getAllHeaders()[2].getName());
        Assert.assertEquals("ValueB_2", resp.getAllHeaders()[2].getValue());
        Assert.assertNull(resp.getEntity());
    }
}

