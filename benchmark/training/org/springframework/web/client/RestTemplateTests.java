/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.client;


import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import MediaType.TEXT_PLAIN_VALUE;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
@SuppressWarnings("unchecked")
public class RestTemplateTests {
    private RestTemplate template;

    private ClientHttpRequestFactory requestFactory;

    private ClientHttpRequest request;

    private ClientHttpResponse response;

    private ResponseErrorHandler errorHandler;

    @SuppressWarnings("rawtypes")
    private HttpMessageConverter converter;

    @Test
    public void varArgsTemplateVariables() throws Exception {
        mockSentRequest(HttpMethod.GET, "http://example.com/hotels/42/bookings/21");
        mockResponseStatus(OK);
        template.execute("http://example.com/hotels/{hotel}/bookings/{booking}", HttpMethod.GET, null, null, "42", "21");
        Mockito.verify(response).close();
    }

    @Test
    public void varArgsNullTemplateVariable() throws Exception {
        mockSentRequest(HttpMethod.GET, "http://example.com/-foo");
        mockResponseStatus(OK);
        template.execute("http://example.com/{first}-{last}", HttpMethod.GET, null, null, null, "foo");
        Mockito.verify(response).close();
    }

    @Test
    public void mapTemplateVariables() throws Exception {
        mockSentRequest(HttpMethod.GET, "http://example.com/hotels/42/bookings/42");
        mockResponseStatus(OK);
        Map<String, String> vars = Collections.singletonMap("hotel", "42");
        template.execute("http://example.com/hotels/{hotel}/bookings/{hotel}", HttpMethod.GET, null, null, vars);
        Mockito.verify(response).close();
    }

    @Test
    public void mapNullTemplateVariable() throws Exception {
        mockSentRequest(HttpMethod.GET, "http://example.com/-foo");
        mockResponseStatus(OK);
        Map<String, String> vars = new HashMap<>(2);
        vars.put("first", null);
        vars.put("last", "foo");
        template.execute("http://example.com/{first}-{last}", HttpMethod.GET, null, null, vars);
        Mockito.verify(response).close();
    }

    // SPR-15201
    @Test
    public void uriTemplateWithTrailingSlash() throws Exception {
        String url = "http://example.com/spring/";
        mockSentRequest(HttpMethod.GET, url);
        mockResponseStatus(OK);
        template.execute(url, HttpMethod.GET, null, null);
        Mockito.verify(response).close();
    }

    @Test
    public void errorHandling() throws Exception {
        String url = "http://example.com";
        mockSentRequest(HttpMethod.GET, url);
        mockResponseStatus(INTERNAL_SERVER_ERROR);
        BDDMockito.willThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR)).given(errorHandler).handleError(new URI(url), HttpMethod.GET, response);
        try {
            template.execute(url, HttpMethod.GET, null, null);
            Assert.fail("HttpServerErrorException expected");
        } catch (HttpServerErrorException ex) {
            // expected
        }
        Mockito.verify(response).close();
    }

    @Test
    public void getForObject() throws Exception {
        String expected = "Hello World";
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.GET, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        mockTextResponseBody("Hello World");
        String result = template.getForObject("http://example.com", String.class);
        Assert.assertEquals("Invalid GET result", expected, result);
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Mockito.verify(response).close();
    }

    @Test
    public void getUnsupportedMediaType() throws Exception {
        mockSentRequest(HttpMethod.GET, "http://example.com/resource");
        mockResponseStatus(OK);
        BDDMockito.given(converter.canRead(String.class, null)).willReturn(true);
        MediaType supportedMediaType = new MediaType("foo", "bar");
        BDDMockito.given(converter.getSupportedMediaTypes()).willReturn(Collections.singletonList(supportedMediaType));
        MediaType barBaz = new MediaType("bar", "baz");
        mockResponseBody("Foo", new MediaType("bar", "baz"));
        BDDMockito.given(converter.canRead(String.class, barBaz)).willReturn(false);
        try {
            template.getForObject("http://example.com/{p}", String.class, "resource");
            Assert.fail("UnsupportedMediaTypeException expected");
        } catch (RestClientException ex) {
            // expected
        }
        Mockito.verify(response).close();
    }

    @Test
    public void requestAvoidsDuplicateAcceptHeaderValues() throws Exception {
        HttpMessageConverter firstConverter = Mockito.mock(HttpMessageConverter.class);
        BDDMockito.given(firstConverter.canRead(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(true);
        BDDMockito.given(firstConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        HttpMessageConverter secondConverter = Mockito.mock(HttpMessageConverter.class);
        BDDMockito.given(secondConverter.canRead(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(true);
        BDDMockito.given(secondConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.GET, "http://example.com/", requestHeaders);
        mockResponseStatus(OK);
        mockTextResponseBody("Hello World");
        template.setMessageConverters(Arrays.asList(firstConverter, secondConverter));
        template.getForObject("http://example.com/", String.class);
        Assert.assertEquals("Sent duplicate Accept header values", 1, requestHeaders.getAccept().size());
    }

    @Test
    public void getForEntity() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.GET, "http://example.com", requestHeaders);
        mockTextPlainHttpMessageConverter();
        mockResponseStatus(OK);
        String expected = "Hello World";
        mockTextResponseBody(expected);
        ResponseEntity<String> result = template.getForEntity("http://example.com", String.class);
        Assert.assertEquals("Invalid GET result", expected, result.getBody());
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Assert.assertEquals("Invalid Content-Type header", TEXT_PLAIN, result.getHeaders().getContentType());
        Assert.assertEquals("Invalid status code", OK, result.getStatusCode());
        Mockito.verify(response).close();
    }

    @Test
    public void getForObjectWithCustomUriTemplateHandler() throws Exception {
        DefaultUriBuilderFactory uriTemplateHandler = new DefaultUriBuilderFactory();
        template.setUriTemplateHandler(uriTemplateHandler);
        mockSentRequest(HttpMethod.GET, "http://example.com/hotels/1/pic/pics%2Flogo.png/size/150x150");
        mockResponseStatus(OK);
        BDDMockito.given(response.getHeaders()).willReturn(new org.springframework.http.HttpHeaders());
        BDDMockito.given(response.getBody()).willReturn(StreamUtils.emptyInput());
        Map<String, String> uriVariables = new HashMap<>(2);
        uriVariables.put("hotel", "1");
        uriVariables.put("publicpath", "pics/logo.png");
        uriVariables.put("scale", "150x150");
        String url = "http://example.com/hotels/{hotel}/pic/{publicpath}/size/{scale}";
        template.getForObject(url, String.class, uriVariables);
        Mockito.verify(response).close();
    }

    @Test
    public void headForHeaders() throws Exception {
        mockSentRequest(HttpMethod.HEAD, "http://example.com");
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        org.springframework.http.HttpHeaders result = template.headForHeaders("http://example.com");
        Assert.assertSame("Invalid headers returned", responseHeaders, result);
        Mockito.verify(response).close();
    }

    @Test
    public void postForLocation() throws Exception {
        mockSentRequest(HttpMethod.POST, "http://example.com");
        mockTextPlainHttpMessageConverter();
        mockResponseStatus(OK);
        String helloWorld = "Hello World";
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        URI expected = new URI("http://example.com/hotels");
        responseHeaders.setLocation(expected);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        URI result = template.postForLocation("http://example.com", helloWorld);
        Assert.assertEquals("Invalid POST result", expected, result);
        Mockito.verify(response).close();
    }

    @Test
    public void postForLocationEntityContentType() throws Exception {
        mockSentRequest(HttpMethod.POST, "http://example.com");
        mockTextPlainHttpMessageConverter();
        mockResponseStatus(OK);
        String helloWorld = "Hello World";
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        URI expected = new URI("http://example.com/hotels");
        responseHeaders.setLocation(expected);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(TEXT_PLAIN);
        HttpEntity<String> entity = new HttpEntity(helloWorld, entityHeaders);
        URI result = template.postForLocation("http://example.com", entity);
        Assert.assertEquals("Invalid POST result", expected, result);
        Mockito.verify(response).close();
    }

    @Test
    public void postForLocationEntityCustomHeader() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockTextPlainHttpMessageConverter();
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        URI expected = new URI("http://example.com/hotels");
        responseHeaders.setLocation(expected);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.set("MyHeader", "MyValue");
        HttpEntity<String> entity = new HttpEntity("Hello World", entityHeaders);
        URI result = template.postForLocation("http://example.com", entity);
        Assert.assertEquals("Invalid POST result", expected, result);
        Assert.assertEquals("No custom header set", "MyValue", requestHeaders.getFirst("MyHeader"));
        Mockito.verify(response).close();
    }

    @Test
    public void postForLocationNoLocation() throws Exception {
        mockSentRequest(HttpMethod.POST, "http://example.com");
        mockTextPlainHttpMessageConverter();
        mockResponseStatus(OK);
        URI result = template.postForLocation("http://example.com", "Hello World");
        Assert.assertNull("Invalid POST result", result);
        Mockito.verify(response).close();
    }

    @Test
    public void postForLocationNull() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        template.postForLocation("http://example.com", null);
        Assert.assertEquals("Invalid content length", 0, requestHeaders.getContentLength());
        Mockito.verify(response).close();
    }

    @Test
    public void postForObject() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        String expected = "42";
        mockResponseBody(expected, TEXT_PLAIN);
        String result = template.postForObject("http://example.com", "Hello World", String.class);
        Assert.assertEquals("Invalid POST result", expected, result);
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Mockito.verify(response).close();
    }

    @Test
    public void postForEntity() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        String expected = "42";
        mockResponseBody(expected, TEXT_PLAIN);
        ResponseEntity<String> result = template.postForEntity("http://example.com", "Hello World", String.class);
        Assert.assertEquals("Invalid POST result", expected, result.getBody());
        Assert.assertEquals("Invalid Content-Type", TEXT_PLAIN, result.getHeaders().getContentType());
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Assert.assertEquals("Invalid status code", OK, result.getStatusCode());
        Mockito.verify(response).close();
    }

    @Test
    public void postForObjectNull() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(TEXT_PLAIN);
        responseHeaders.setContentLength(10);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(StreamUtils.emptyInput());
        BDDMockito.given(converter.read(String.class, response)).willReturn(null);
        String result = template.postForObject("http://example.com", null, String.class);
        Assert.assertNull("Invalid POST result", result);
        Assert.assertEquals("Invalid content length", 0, requestHeaders.getContentLength());
        Mockito.verify(response).close();
    }

    @Test
    public void postForEntityNull() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(TEXT_PLAIN);
        responseHeaders.setContentLength(10);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(StreamUtils.emptyInput());
        BDDMockito.given(converter.read(String.class, response)).willReturn(null);
        ResponseEntity<String> result = template.postForEntity("http://example.com", null, String.class);
        Assert.assertFalse("Invalid POST result", result.hasBody());
        Assert.assertEquals("Invalid Content-Type", TEXT_PLAIN, result.getHeaders().getContentType());
        Assert.assertEquals("Invalid content length", 0, requestHeaders.getContentLength());
        Assert.assertEquals("Invalid status code", OK, result.getStatusCode());
        Mockito.verify(response).close();
    }

    @Test
    public void put() throws Exception {
        mockTextPlainHttpMessageConverter();
        mockSentRequest(HttpMethod.PUT, "http://example.com");
        mockResponseStatus(OK);
        template.put("http://example.com", "Hello World");
        Mockito.verify(response).close();
    }

    @Test
    public void putNull() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.PUT, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        template.put("http://example.com", null);
        Assert.assertEquals("Invalid content length", 0, requestHeaders.getContentLength());
        Mockito.verify(response).close();
    }

    @Test
    public void patchForObject() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.PATCH, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        String expected = "42";
        mockResponseBody("42", TEXT_PLAIN);
        String result = template.patchForObject("http://example.com", "Hello World", String.class);
        Assert.assertEquals("Invalid POST result", expected, result);
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Mockito.verify(response).close();
    }

    @Test
    public void patchForObjectNull() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.PATCH, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(TEXT_PLAIN);
        responseHeaders.setContentLength(10);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(StreamUtils.emptyInput());
        String result = template.patchForObject("http://example.com", null, String.class);
        Assert.assertNull("Invalid POST result", result);
        Assert.assertEquals("Invalid content length", 0, requestHeaders.getContentLength());
        Mockito.verify(response).close();
    }

    @Test
    public void delete() throws Exception {
        mockSentRequest(HttpMethod.DELETE, "http://example.com");
        mockResponseStatus(OK);
        template.delete("http://example.com");
        Mockito.verify(response).close();
    }

    @Test
    public void optionsForAllow() throws Exception {
        mockSentRequest(HttpMethod.OPTIONS, "http://example.com");
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        EnumSet<HttpMethod> expected = EnumSet.of(HttpMethod.GET, HttpMethod.POST);
        responseHeaders.setAllow(expected);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        Set<HttpMethod> result = template.optionsForAllow("http://example.com");
        Assert.assertEquals("Invalid OPTIONS result", expected, result);
        Mockito.verify(response).close();
    }

    // SPR-9325, SPR-13860
    @Test
    public void ioException() throws Exception {
        String url = "http://example.com/resource?access_token=123";
        mockSentRequest(HttpMethod.GET, url);
        mockHttpMessageConverter(new MediaType("foo", "bar"), String.class);
        BDDMockito.given(request.execute()).willThrow(new IOException("Socket failure"));
        try {
            template.getForObject(url, String.class);
            Assert.fail("RestClientException expected");
        } catch (ResourceAccessException ex) {
            Assert.assertEquals(("I/O error on GET request for \"http://example.com/resource\": " + "Socket failure; nested exception is java.io.IOException: Socket failure"), ex.getMessage());
        }
    }

    // SPR-15900
    @Test
    public void ioExceptionWithEmptyQueryString() throws Exception {
        // http://example.com/resource?
        URI uri = new URI("http", "example.com", "/resource", "", null);
        BDDMockito.given(converter.canRead(String.class, null)).willReturn(true);
        BDDMockito.given(converter.getSupportedMediaTypes()).willReturn(Collections.singletonList(MediaType.parseMediaType("foo/bar")));
        BDDMockito.given(requestFactory.createRequest(uri, HttpMethod.GET)).willReturn(request);
        BDDMockito.given(request.getHeaders()).willReturn(new org.springframework.http.HttpHeaders());
        BDDMockito.given(request.execute()).willThrow(new IOException("Socket failure"));
        try {
            template.getForObject(uri, String.class);
            Assert.fail("RestClientException expected");
        } catch (ResourceAccessException ex) {
            Assert.assertEquals(("I/O error on GET request for \"http://example.com/resource\": " + "Socket failure; nested exception is java.io.IOException: Socket failure"), ex.getMessage());
        }
    }

    @Test
    public void exchange() throws Exception {
        mockTextPlainHttpMessageConverter();
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        String expected = "42";
        mockResponseBody(expected, TEXT_PLAIN);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.set("MyHeader", "MyValue");
        HttpEntity<String> entity = new HttpEntity("Hello World", entityHeaders);
        ResponseEntity<String> result = template.exchange("http://example.com", HttpMethod.POST, entity, String.class);
        Assert.assertEquals("Invalid POST result", expected, result.getBody());
        Assert.assertEquals("Invalid Content-Type", TEXT_PLAIN, result.getHeaders().getContentType());
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Assert.assertEquals("Invalid custom header", "MyValue", requestHeaders.getFirst("MyHeader"));
        Assert.assertEquals("Invalid status code", OK, result.getStatusCode());
        Mockito.verify(response).close();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void exchangeParameterizedType() throws Exception {
        GenericHttpMessageConverter converter = Mockito.mock(GenericHttpMessageConverter.class);
        template.setMessageConverters(Collections.<HttpMessageConverter<?>>singletonList(converter));
        ParameterizedTypeReference<List<Integer>> intList = new ParameterizedTypeReference<List<Integer>>() {};
        BDDMockito.given(converter.canRead(intList.getType(), null, null)).willReturn(true);
        BDDMockito.given(converter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        BDDMockito.given(converter.canWrite(String.class, String.class, null)).willReturn(true);
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        List<Integer> expected = Collections.singletonList(42);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentType(TEXT_PLAIN);
        responseHeaders.setContentLength(10);
        mockResponseStatus(OK);
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream(Integer.toString(42).getBytes()));
        BDDMockito.given(converter.canRead(intList.getType(), null, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(converter.read(ArgumentMatchers.eq(intList.getType()), ArgumentMatchers.eq(null), ArgumentMatchers.any(HttpInputMessage.class))).willReturn(expected);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.set("MyHeader", "MyValue");
        HttpEntity<String> requestEntity = new HttpEntity("Hello World", entityHeaders);
        ResponseEntity<List<Integer>> result = template.exchange("http://example.com", HttpMethod.POST, requestEntity, intList);
        Assert.assertEquals("Invalid POST result", expected, result.getBody());
        Assert.assertEquals("Invalid Content-Type", TEXT_PLAIN, result.getHeaders().getContentType());
        Assert.assertEquals("Invalid Accept header", TEXT_PLAIN_VALUE, requestHeaders.getFirst("Accept"));
        Assert.assertEquals("Invalid custom header", "MyValue", requestHeaders.getFirst("MyHeader"));
        Assert.assertEquals("Invalid status code", OK, result.getStatusCode());
        Mockito.verify(response).close();
    }

    // SPR-15066
    @Test
    public void requestInterceptorCanAddExistingHeaderValueWithoutBody() throws Exception {
        ClientHttpRequestInterceptor interceptor = ( request, body, execution) -> {
            request.getHeaders().add("MyHeader", "MyInterceptorValue");
            return execution.execute(request, body);
        };
        template.setInterceptors(Collections.singletonList(interceptor));
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.add("MyHeader", "MyEntityValue");
        HttpEntity<Void> entity = new HttpEntity(null, entityHeaders);
        template.exchange("http://example.com", HttpMethod.POST, entity, Void.class);
        MatcherAssert.assertThat(requestHeaders.get("MyHeader"), contains("MyEntityValue", "MyInterceptorValue"));
        Mockito.verify(response).close();
    }

    // SPR-15066
    @Test
    public void requestInterceptorCanAddExistingHeaderValueWithBody() throws Exception {
        ClientHttpRequestInterceptor interceptor = ( request, body, execution) -> {
            request.getHeaders().add("MyHeader", "MyInterceptorValue");
            return execution.execute(request, body);
        };
        template.setInterceptors(Collections.singletonList(interceptor));
        MediaType contentType = MediaType.TEXT_PLAIN;
        BDDMockito.given(converter.canWrite(String.class, contentType)).willReturn(true);
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        mockSentRequest(HttpMethod.POST, "http://example.com", requestHeaders);
        mockResponseStatus(OK);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(contentType);
        entityHeaders.add("MyHeader", "MyEntityValue");
        HttpEntity<String> entity = new HttpEntity("Hello World", entityHeaders);
        template.exchange("http://example.com", HttpMethod.POST, entity, Void.class);
        MatcherAssert.assertThat(requestHeaders.get("MyHeader"), contains("MyEntityValue", "MyInterceptorValue"));
        Mockito.verify(response).close();
    }
}

