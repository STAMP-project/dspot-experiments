package com.android.volley.toolbox;


import com.android.volley.Request;
import com.android.volley.mock.TestRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.com.android.volley.toolbox.HttpResponse;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.message.BasicHeader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;


@RunWith(RobolectricTestRunner.class)
public class AdaptedHttpStackTest {
    private static final Request<?> REQUEST = new TestRequest.Get();

    private static final Map<String, String> ADDITIONAL_HEADERS = Collections.emptyMap();

    @Mock
    private HttpStack mHttpStack;

    @Mock
    private HttpResponse mHttpResponse;

    @Mock
    private StatusLine mStatusLine;

    @Mock
    private HttpEntity mHttpEntity;

    @Mock
    private InputStream mContent;

    private AdaptedHttpStack mAdaptedHttpStack;

    @Test(expected = SocketTimeoutException.class)
    public void requestTimeout() throws Exception {
        Mockito.when(mHttpStack.performRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS)).thenThrow(new ConnectTimeoutException());
        mAdaptedHttpStack.executeRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS);
    }

    @Test
    public void emptyResponse() throws Exception {
        Mockito.when(mHttpStack.performRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS)).thenReturn(mHttpResponse);
        Mockito.when(mStatusLine.getStatusCode()).thenReturn(12345);
        Mockito.when(mHttpResponse.getAllHeaders()).thenReturn(new Header[0]);
        com.android.volley.toolbox.HttpResponse response = mAdaptedHttpStack.executeRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS);
        Assert.assertEquals(12345, response.getStatusCode());
        Assert.assertEquals(Collections.emptyList(), response.getHeaders());
        Assert.assertNull(response.getContent());
    }

    @Test
    public void nonEmptyResponse() throws Exception {
        Mockito.when(mHttpStack.performRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS)).thenReturn(mHttpResponse);
        Mockito.when(mStatusLine.getStatusCode()).thenReturn(12345);
        Mockito.when(mHttpResponse.getAllHeaders()).thenReturn(new Header[0]);
        Mockito.when(mHttpResponse.getEntity()).thenReturn(mHttpEntity);
        Mockito.when(mHttpEntity.getContentLength()).thenReturn(((long) (Integer.MAX_VALUE)));
        Mockito.when(mHttpEntity.getContent()).thenReturn(mContent);
        com.android.volley.toolbox.HttpResponse response = mAdaptedHttpStack.executeRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS);
        Assert.assertEquals(12345, response.getStatusCode());
        Assert.assertEquals(Collections.emptyList(), response.getHeaders());
        Assert.assertEquals(Integer.MAX_VALUE, response.getContentLength());
        Assert.assertSame(mContent, response.getContent());
    }

    @Test(expected = IOException.class)
    public void responseTooBig() throws Exception {
        Mockito.when(mHttpStack.performRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS)).thenReturn(mHttpResponse);
        Mockito.when(mStatusLine.getStatusCode()).thenReturn(12345);
        Mockito.when(mHttpResponse.getAllHeaders()).thenReturn(new Header[0]);
        Mockito.when(mHttpResponse.getEntity()).thenReturn(mHttpEntity);
        Mockito.when(mHttpEntity.getContentLength()).thenReturn(((Integer.MAX_VALUE) + 1L));
        Mockito.when(mHttpEntity.getContent()).thenReturn(mContent);
        mAdaptedHttpStack.executeRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS);
    }

    @Test
    public void responseWithHeaders() throws Exception {
        Mockito.when(mHttpStack.performRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS)).thenReturn(mHttpResponse);
        Mockito.when(mStatusLine.getStatusCode()).thenReturn(12345);
        Mockito.when(mHttpResponse.getAllHeaders()).thenReturn(new Header[]{ new BasicHeader("header1", "value1_B"), new BasicHeader("header3", "value3"), new BasicHeader("HEADER2", "value2"), new BasicHeader("header1", "value1_A") });
        com.android.volley.toolbox.HttpResponse response = mAdaptedHttpStack.executeRequest(AdaptedHttpStackTest.REQUEST, AdaptedHttpStackTest.ADDITIONAL_HEADERS);
        Assert.assertEquals(12345, response.getStatusCode());
        Assert.assertNull(response.getContent());
        List<com.android.volley.Header> expectedHeaders = new ArrayList<>();
        expectedHeaders.add(new com.android.volley.Header("header1", "value1_B"));
        expectedHeaders.add(new com.android.volley.Header("header3", "value3"));
        expectedHeaders.add(new com.android.volley.Header("HEADER2", "value2"));
        expectedHeaders.add(new com.android.volley.Header("header1", "value1_A"));
        Assert.assertEquals(expectedHeaders, response.getHeaders());
    }
}

