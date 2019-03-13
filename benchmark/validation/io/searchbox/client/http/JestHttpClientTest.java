package io.searchbox.client.http;


import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.apache.HttpDeleteWithEntity;
import io.searchbox.client.http.apache.HttpGetWithEntity;
import io.searchbox.core.Search;
import io.searchbox.core.search.sort.Sort;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class JestHttpClientTest {
    JestHttpClient client;

    @Test
    public void constructGetHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("GET", "jest/get", null, null);
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getURI().getPath(), "jest/get");
        Assert.assertTrue((request instanceof HttpGetWithEntity));
    }

    @Test
    public void constructCompressedPutHttpMethod() throws UnsupportedEncodingException {
        client.setRequestCompressionEnabled(true);
        HttpUriRequest request = client.constructHttpMethod("PUT", "jest/put", "data", null);
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getURI().getPath(), "jest/put");
        Assert.assertTrue((request instanceof HttpPut));
        Assert.assertTrue(((((HttpPut) (request)).getEntity()) instanceof GzipCompressingEntity));
    }

    @Test
    public void constructPutHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("PUT", "jest/put", "data", null);
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getURI().getPath(), "jest/put");
        Assert.assertTrue((request instanceof HttpPut));
        Assert.assertFalse(((((HttpPut) (request)).getEntity()) instanceof GzipCompressingEntity));
    }

    @Test
    public void constructPostHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("POST", "jest/post", "data", null);
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getURI().getPath(), "jest/post");
        Assert.assertTrue((request instanceof HttpPost));
    }

    @Test
    public void constructDeleteHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("DELETE", "jest/delete", null, null);
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getURI().getPath(), "jest/delete");
        Assert.assertTrue((request instanceof HttpDeleteWithEntity));
    }

    @Test
    public void constructHeadHttpMethod() throws UnsupportedEncodingException {
        HttpUriRequest request = client.constructHttpMethod("HEAD", "jest/head", null, null);
        Assert.assertNotNull(request);
        Assert.assertEquals(request.getURI().getPath(), "jest/head");
        Assert.assertTrue((request instanceof HttpHead));
    }

    @Test
    public void addHeadersToRequest() throws IOException {
        final String headerKey = "foo";
        final String headerValue = "bar";
        CloseableHttpResponse httpResponseMock = Mockito.mock(CloseableHttpResponse.class);
        Mockito.doReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, 200, "OK")).when(httpResponseMock).getStatusLine();
        Mockito.doReturn(null).when(httpResponseMock).getEntity();
        CloseableHttpClient closeableHttpClientMock = Mockito.mock(CloseableHttpClient.class);
        Mockito.doReturn(httpResponseMock).when(closeableHttpClientMock).execute(ArgumentMatchers.any(HttpUriRequest.class));
        // Construct a new Jest client according to configuration via factory
        JestHttpClient clientWithMockedHttpClient;
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").build());
        clientWithMockedHttpClient = ((JestHttpClient) (factory.getObject()));
        clientWithMockedHttpClient.setHttpClient(closeableHttpClientMock);
        // could reuse the above setup for testing core types against expected
        // HttpUriRequest (more of an end to end test)
        String query = "{\n" + (((((((((((("    \"query\": {\n" + "        \"filtered\" : {\n") + "            \"query\" : {\n") + "                \"query_string\" : {\n") + "                    \"query\" : \"test\"\n") + "                }\n") + "            },\n") + "            \"filter\" : {\n") + "                \"term\" : { \"user\" : \"kimchy\" }\n") + "            }\n") + "        }\n") + "    }\n") + "}");
        Search search = // multiple index or types can be added.
        new Search.Builder(query).addIndex("twitter").addType("tweet").setHeader(headerKey, headerValue).build();
        // send request (not really)
        clientWithMockedHttpClient.execute(search);
        Mockito.verify(closeableHttpClientMock).execute(ArgumentMatchers.argThat(new ArgumentMatcher<HttpUriRequest>() {
            @Override
            public boolean matches(Object o) {
                boolean retval = false;
                if (o instanceof HttpUriRequest) {
                    HttpUriRequest req = ((HttpUriRequest) (o));
                    Header header = req.getFirstHeader(headerKey);
                    if (header != null) {
                        retval = headerValue.equals(header.getValue());
                    }
                }
                return retval;
            }
        }));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void prepareShouldNotRewriteLongToDoubles() throws IOException {
        // Construct a new Jest client according to configuration via factory
        JestHttpClient clientWithMockedHttpClient = ((JestHttpClient) (new JestClientFactory().getObject()));
        // Construct mock Sort
        Sort mockSort = Mockito.mock(Sort.class);
        String query = "{\n" + (((((((("    \"query\": {\n" + "        \"bool\" : {\n") + "            \"should\" : [\n") + "                { \"term\" : { \"id\" : 1234 } },\n") + "                { \"term\" : { \"id\" : 567800000000000000000 } }\n") + "            ]\n") + "         }\n") + "     }\n") + "}");
        Search search = // multiple index or types can be added.
        new Search.Builder(query).addIndex("twitter").addType("tweet").addSort(mockSort).build();
        // Create HttpUriRequest
        HttpUriRequest request = clientWithMockedHttpClient.prepareRequest(search, null);
        // Extract Payload
        HttpEntity entity = ((HttpPost) (request)).getEntity();
        String payload = EntityUtils.toString(entity, Charset.defaultCharset());
        // Verify payload does not have a double
        Assert.assertFalse(payload.contains("1234.0"));
        Assert.assertTrue(payload.contains("1234"));
        // Verify payload does not use scientific notation
        Assert.assertFalse(payload.contains("5.678E20"));
        Assert.assertTrue(payload.contains("567800000000000000000"));
    }

    @Test
    public void createContextInstanceWithPreemptiveAuth() {
        AuthCache authCacheMock = Mockito.mock(AuthCache.class);
        CredentialsProvider credentialsProviderMock = Mockito.mock(CredentialsProvider.class);
        HttpClientContext httpClientContextTemplate = HttpClientContext.create();
        httpClientContextTemplate.setAuthCache(authCacheMock);
        httpClientContextTemplate.setCredentialsProvider(credentialsProviderMock);
        JestHttpClient jestHttpClient = ((JestHttpClient) (new JestClientFactory().getObject()));
        jestHttpClient.setHttpClientContextTemplate(httpClientContextTemplate);
        HttpClientContext httpClientContextResult = jestHttpClient.createContextInstance();
        Assert.assertEquals(authCacheMock, httpClientContextResult.getAuthCache());
        Assert.assertEquals(credentialsProviderMock, httpClientContextResult.getCredentialsProvider());
    }

    @Test
    public void constructGetHttpMethodWithCustomRequestConfig() throws UnsupportedEncodingException {
        final RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(42).build();
        HttpUriRequest request = client.constructHttpMethod("GET", "jest/get", null, requestConfig);
        Assert.assertNotNull(request);
        Assert.assertEquals(requestConfig, getConfig());
    }

    @Test
    public void constructPutHttpMethodWithCustomRequestConfig() throws UnsupportedEncodingException {
        final RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(42).build();
        HttpUriRequest request = client.constructHttpMethod("PUT", "jest/put", "data", requestConfig);
        Assert.assertNotNull(request);
        Assert.assertEquals(requestConfig, getConfig());
    }

    @Test
    public void constructPostHttpMethodWithCustomRequestConfig() throws UnsupportedEncodingException {
        final RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(42).build();
        HttpUriRequest request = client.constructHttpMethod("POST", "jest/post", "data", requestConfig);
        Assert.assertNotNull(request);
        Assert.assertEquals(requestConfig, getConfig());
    }

    @Test
    public void constructDeleteHttpMethodWithCustomRequestConfig() throws UnsupportedEncodingException {
        final RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(42).build();
        HttpUriRequest request = client.constructHttpMethod("DELETE", "jest/delete", null, requestConfig);
        Assert.assertNotNull(request);
        Assert.assertEquals(requestConfig, getConfig());
    }

    @Test
    public void constructHeadHttpMethodWithCustomRequestConfig() throws UnsupportedEncodingException {
        final RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(42).build();
        HttpUriRequest request = client.constructHttpMethod("HEAD", "jest/head", null, requestConfig);
        Assert.assertNotNull(request);
        Assert.assertEquals(requestConfig, getConfig());
    }
}

