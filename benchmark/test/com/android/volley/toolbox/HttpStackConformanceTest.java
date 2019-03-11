package com.android.volley.toolbox;


import HttpHeaderParser.HEADER_CONTENT_TYPE;
import Request.Method.POST;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;


/**
 * Tests to validate that HttpStack implementations conform with expected behavior.
 */
@RunWith(RobolectricTestRunner.class)
public class HttpStackConformanceTest {
    @Mock
    private RetryPolicy mMockRetryPolicy;

    @Mock
    private Request mMockRequest;

    @Mock
    private HttpURLConnection mMockConnection;

    @Mock
    private OutputStream mMockOutputStream;

    @Spy
    private HurlStack mHurlStack = new HurlStack();

    @Mock
    private HttpClient mMockHttpClient;

    private HttpClientStack mHttpClientStack;

    private final HttpStackConformanceTest.TestCase[] mTestCases = new HttpStackConformanceTest.TestCase[]{ // TestCase for HurlStack.
    new HttpStackConformanceTest.TestCase() {
        @Override
        public HttpStack getStack() {
            return mHurlStack;
        }

        @Override
        public void setOutputHeaderMap(final Map<String, String> outputHeaderMap) {
            Mockito.doAnswer(new Answer<Void>() {
                @Override
                public Void answer(InvocationOnMock invocation) {
                    outputHeaderMap.put(invocation.<String>getArgument(0), invocation.<String>getArgument(1));
                    return null;
                }
            }).when(mMockConnection).setRequestProperty(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
            Mockito.doAnswer(new Answer<Map<String, List<String>>>() {
                @Override
                public Map<String, List<String>> answer(InvocationOnMock invocation) {
                    Map<String, List<String>> result = new HashMap<>();
                    for (Map.Entry<String, String> entry : outputHeaderMap.entrySet()) {
                        result.put(entry.getKey(), Collections.singletonList(entry.getValue()));
                    }
                    return result;
                }
            }).when(mMockConnection).getRequestProperties();
        }
    }, // TestCase for HttpClientStack.
    new HttpStackConformanceTest.TestCase() {
        @Override
        public HttpStack getStack() {
            return mHttpClientStack;
        }

        @Override
        public void setOutputHeaderMap(final Map<String, String> outputHeaderMap) {
            try {
                Mockito.doAnswer(new Answer<Void>() {
                    @Override
                    public Void answer(InvocationOnMock invocation) throws Throwable {
                        HttpRequest request = invocation.getArgument(0);
                        for (Header header : request.getAllHeaders()) {
                            if (outputHeaderMap.containsKey(header.getName())) {
                                Assert.fail(("Multiple values for header " + (header.getName())));
                            }
                            outputHeaderMap.put(header.getName(), header.getValue());
                        }
                        return null;
                    }
                }).when(mMockHttpClient).execute(ArgumentMatchers.any(HttpUriRequest.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    } };

    @Test
    public void headerPrecedence() throws Exception {
        Map<String, String> additionalHeaders = new HashMap<>();
        additionalHeaders.put("A", "AddlA");
        additionalHeaders.put("B", "AddlB");
        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("A", "RequestA");
        requestHeaders.put("C", "RequestC");
        Mockito.when(mMockRequest.getHeaders()).thenReturn(requestHeaders);
        Mockito.when(mMockRequest.getMethod()).thenReturn(POST);
        Mockito.when(mMockRequest.getBody()).thenReturn(new byte[0]);
        Mockito.when(mMockRequest.getBodyContentType()).thenReturn("BodyContentType");
        for (HttpStackConformanceTest.TestCase testCase : mTestCases) {
            // Test once without a Content-Type header in getHeaders().
            Map<String, String> combinedHeaders = new HashMap<>();
            testCase.setOutputHeaderMap(combinedHeaders);
            testCase.getStack().performRequest(mMockRequest, additionalHeaders);
            Map<String, String> expectedHeaders = new HashMap<>();
            expectedHeaders.put("A", "RequestA");
            expectedHeaders.put("B", "AddlB");
            expectedHeaders.put("C", "RequestC");
            expectedHeaders.put(HEADER_CONTENT_TYPE, "BodyContentType");
            Assert.assertEquals(expectedHeaders, combinedHeaders);
            // Reset and test again with a Content-Type header in getHeaders().
            combinedHeaders.clear();
            requestHeaders.put(HEADER_CONTENT_TYPE, "RequestContentType");
            expectedHeaders.put(HEADER_CONTENT_TYPE, "RequestContentType");
            testCase.getStack().performRequest(mMockRequest, additionalHeaders);
            Assert.assertEquals(expectedHeaders, combinedHeaders);
            // Clear the Content-Type header for the next TestCase.
            requestHeaders.remove(HEADER_CONTENT_TYPE);
        }
    }

    private interface TestCase {
        HttpStack getStack();

        void setOutputHeaderMap(Map<String, String> outputHeaderMap);
    }
}

