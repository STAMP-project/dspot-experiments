/**
 * Copyright 2002-2017 the original author or authors.
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


import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpMethod.TRACE;
import HttpStatus.INTERNAL_SERVER_ERROR;
import HttpStatus.NOT_FOUND;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import MediaType.TEXT_PLAIN;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.AsyncClientHttpRequestExecution;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 */
@SuppressWarnings("deprecation")
public class AsyncRestTemplateIntegrationTests extends AbstractMockWebServerTestCase {
    private final AsyncRestTemplate template = new AsyncRestTemplate(new HttpComponentsAsyncClientHttpRequestFactory());

    @Test
    public void getEntity() throws Exception {
        Future<ResponseEntity<String>> future = template.getForEntity(((baseUrl) + "/{method}"), String.class, "get");
        ResponseEntity<String> entity = future.get();
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, entity.getBody());
        Assert.assertFalse("No headers", entity.getHeaders().isEmpty());
        Assert.assertEquals("Invalid content-type", AbstractMockWebServerTestCase.textContentType, entity.getHeaders().getContentType());
        Assert.assertEquals("Invalid status code", OK, entity.getStatusCode());
    }

    @Test
    public void getEntityFromCompletable() throws Exception {
        ListenableFuture<ResponseEntity<String>> future = template.getForEntity(((baseUrl) + "/{method}"), String.class, "get");
        ResponseEntity<String> entity = future.completable().get();
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, entity.getBody());
        Assert.assertFalse("No headers", entity.getHeaders().isEmpty());
        Assert.assertEquals("Invalid content-type", AbstractMockWebServerTestCase.textContentType, entity.getHeaders().getContentType());
        Assert.assertEquals("Invalid status code", OK, entity.getStatusCode());
    }

    @Test
    public void multipleFutureGets() throws Exception {
        Future<ResponseEntity<String>> future = template.getForEntity(((baseUrl) + "/{method}"), String.class, "get");
        future.get();
        future.get();
    }

    @Test
    public void getEntityCallback() throws Exception {
        ListenableFuture<ResponseEntity<String>> futureEntity = template.getForEntity(((baseUrl) + "/{method}"), String.class, "get");
        futureEntity.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onSuccess(ResponseEntity<String> entity) {
                Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, entity.getBody());
                Assert.assertFalse("No headers", entity.getHeaders().isEmpty());
                Assert.assertEquals("Invalid content-type", AbstractMockWebServerTestCase.textContentType, entity.getHeaders().getContentType());
                Assert.assertEquals("Invalid status code", OK, entity.getStatusCode());
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(futureEntity);
    }

    @Test
    public void getEntityCallbackWithLambdas() throws Exception {
        ListenableFuture<ResponseEntity<String>> futureEntity = template.getForEntity(((baseUrl) + "/{method}"), String.class, "get");
        futureEntity.addCallback(( entity) -> {
            assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, entity.getBody());
            assertFalse("No headers", entity.getHeaders().isEmpty());
            assertEquals("Invalid content-type", AbstractMockWebServerTestCase.textContentType, entity.getHeaders().getContentType());
            assertEquals("Invalid status code", HttpStatus.OK, entity.getStatusCode());
        }, ( ex) -> fail(ex.getMessage()));
        waitTillDone(futureEntity);
    }

    @Test
    public void getNoResponse() throws Exception {
        Future<ResponseEntity<String>> futureEntity = template.getForEntity(((baseUrl) + "/get/nothing"), String.class);
        ResponseEntity<String> entity = futureEntity.get();
        Assert.assertNull("Invalid content", entity.getBody());
    }

    @Test
    public void getNoContentTypeHeader() throws Exception {
        Future<ResponseEntity<byte[]>> futureEntity = template.getForEntity(((baseUrl) + "/get/nocontenttype"), byte[].class);
        ResponseEntity<byte[]> responseEntity = futureEntity.get();
        Assert.assertArrayEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld.getBytes("UTF-8"), responseEntity.getBody());
    }

    @Test
    public void getNoContent() throws Exception {
        Future<ResponseEntity<String>> responseFuture = template.getForEntity(((baseUrl) + "/status/nocontent"), String.class);
        ResponseEntity<String> entity = responseFuture.get();
        Assert.assertEquals("Invalid response code", NO_CONTENT, entity.getStatusCode());
        Assert.assertNull("Invalid content", entity.getBody());
    }

    @Test
    public void getNotModified() throws Exception {
        Future<ResponseEntity<String>> responseFuture = template.getForEntity(((baseUrl) + "/status/notmodified"), String.class);
        ResponseEntity<String> entity = responseFuture.get();
        Assert.assertEquals("Invalid response code", NOT_MODIFIED, entity.getStatusCode());
        Assert.assertNull("Invalid content", entity.getBody());
    }

    @Test
    public void headForHeaders() throws Exception {
        Future<org.springframework.http.HttpHeaders> headersFuture = template.headForHeaders(((baseUrl) + "/get"));
        org.springframework.http.HttpHeaders headers = headersFuture.get();
        Assert.assertTrue("No Content-Type header", headers.containsKey("Content-Type"));
    }

    @Test
    public void headForHeadersCallback() throws Exception {
        ListenableFuture<org.springframework.http.HttpHeaders> headersFuture = template.headForHeaders(((baseUrl) + "/get"));
        headersFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<org.springframework.http.HttpHeaders>() {
            @Override
            public void onSuccess(org.springframework.http.HttpHeaders result) {
                Assert.assertTrue("No Content-Type header", result.containsKey("Content-Type"));
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(headersFuture);
    }

    @Test
    public void headForHeadersCallbackWithLambdas() throws Exception {
        ListenableFuture<org.springframework.http.HttpHeaders> headersFuture = template.headForHeaders(((baseUrl) + "/get"));
        headersFuture.addCallback(( result) -> assertTrue("No Content-Type header", result.containsKey("Content-Type")), ( ex) -> fail(ex.getMessage()));
        waitTillDone(headersFuture);
    }

    @Test
    public void postForLocation() throws Exception {
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.ISO_8859_1));
        HttpEntity<String> entity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, entityHeaders);
        Future<URI> locationFuture = template.postForLocation(((baseUrl) + "/{method}"), entity, "post");
        URI location = locationFuture.get();
        Assert.assertEquals("Invalid location", new URI(((baseUrl) + "/post/1")), location);
    }

    @Test
    public void postForLocationCallback() throws Exception {
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.ISO_8859_1));
        HttpEntity<String> entity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, entityHeaders);
        final URI expected = new URI(((baseUrl) + "/post/1"));
        ListenableFuture<URI> locationFuture = template.postForLocation(((baseUrl) + "/{method}"), entity, "post");
        locationFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<URI>() {
            @Override
            public void onSuccess(URI result) {
                Assert.assertEquals("Invalid location", expected, result);
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(locationFuture);
    }

    @Test
    public void postForLocationCallbackWithLambdas() throws Exception {
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(new MediaType("text", "plain", StandardCharsets.ISO_8859_1));
        HttpEntity<String> entity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, entityHeaders);
        final URI expected = new URI(((baseUrl) + "/post/1"));
        ListenableFuture<URI> locationFuture = template.postForLocation(((baseUrl) + "/{method}"), entity, "post");
        locationFuture.addCallback(( result) -> assertEquals("Invalid location", expected, result), ( ex) -> fail(ex.getMessage()));
        waitTillDone(locationFuture);
    }

    @Test
    public void postForEntity() throws Exception {
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld);
        Future<ResponseEntity<String>> responseEntityFuture = template.postForEntity(((baseUrl) + "/{method}"), requestEntity, String.class, "post");
        ResponseEntity<String> responseEntity = responseEntityFuture.get();
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, responseEntity.getBody());
    }

    @Test
    public void postForEntityCallback() throws Exception {
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld);
        ListenableFuture<ResponseEntity<String>> responseEntityFuture = template.postForEntity(((baseUrl) + "/{method}"), requestEntity, String.class, "post");
        responseEntityFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onSuccess(ResponseEntity<String> result) {
                Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, result.getBody());
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(responseEntityFuture);
    }

    @Test
    public void postForEntityCallbackWithLambdas() throws Exception {
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld);
        ListenableFuture<ResponseEntity<String>> responseEntityFuture = template.postForEntity(((baseUrl) + "/{method}"), requestEntity, String.class, "post");
        responseEntityFuture.addCallback(( result) -> assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, result.getBody()), ( ex) -> fail(ex.getMessage()));
        waitTillDone(responseEntityFuture);
    }

    @Test
    public void put() throws Exception {
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld);
        Future<?> responseEntityFuture = template.put(((baseUrl) + "/{method}"), requestEntity, "put");
        responseEntityFuture.get();
    }

    @Test
    public void putCallback() throws Exception {
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld);
        ListenableFuture<?> responseEntityFuture = template.put(((baseUrl) + "/{method}"), requestEntity, "put");
        responseEntityFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                Assert.assertNull(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(responseEntityFuture);
    }

    @Test
    public void delete() throws Exception {
        Future<?> deletedFuture = template.delete(new URI(((baseUrl) + "/delete")));
        deletedFuture.get();
    }

    @Test
    public void deleteCallback() throws Exception {
        ListenableFuture<?> deletedFuture = template.delete(new URI(((baseUrl) + "/delete")));
        deletedFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                Assert.assertNull(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(deletedFuture);
    }

    @Test
    public void deleteCallbackWithLambdas() throws Exception {
        ListenableFuture<?> deletedFuture = template.delete(new URI(((baseUrl) + "/delete")));
        deletedFuture.addCallback(Assert::assertNull, ( ex) -> fail(ex.getMessage()));
        waitTillDone(deletedFuture);
    }

    @Test
    public void identicalExceptionThroughGetAndCallback() throws Exception {
        final HttpClientErrorException[] callbackException = new HttpClientErrorException[1];
        final CountDownLatch latch = new CountDownLatch(1);
        ListenableFuture<?> future = template.execute(((baseUrl) + "/status/notfound"), GET, null, null);
        future.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                Assert.fail("onSuccess not expected");
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.assertTrue((ex instanceof HttpClientErrorException));
                callbackException[0] = ((HttpClientErrorException) (ex));
                latch.countDown();
            }
        });
        try {
            future.get();
            Assert.fail("Exception expected");
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            Assert.assertTrue((cause instanceof HttpClientErrorException));
            latch.await(5, TimeUnit.SECONDS);
            Assert.assertSame(callbackException[0], cause);
        }
    }

    @Test
    public void notFoundGet() throws Exception {
        try {
            Future<?> future = template.execute(((baseUrl) + "/status/notfound"), GET, null, null);
            future.get();
            Assert.fail("HttpClientErrorException expected");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof HttpClientErrorException));
            HttpClientErrorException cause = ((HttpClientErrorException) (ex.getCause()));
            Assert.assertEquals(NOT_FOUND, cause.getStatusCode());
            Assert.assertNotNull(cause.getStatusText());
            Assert.assertNotNull(cause.getResponseBodyAsString());
        }
    }

    @Test
    public void notFoundCallback() throws Exception {
        ListenableFuture<?> future = template.execute(((baseUrl) + "/status/notfound"), GET, null, null);
        future.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                Assert.fail("onSuccess not expected");
            }

            @Override
            public void onFailure(Throwable t) {
                Assert.assertTrue((t instanceof HttpClientErrorException));
                HttpClientErrorException ex = ((HttpClientErrorException) (t));
                Assert.assertEquals(NOT_FOUND, ex.getStatusCode());
                Assert.assertNotNull(ex.getStatusText());
                Assert.assertNotNull(ex.getResponseBodyAsString());
            }
        });
        waitTillDone(future);
    }

    @Test
    public void notFoundCallbackWithLambdas() throws Exception {
        ListenableFuture<?> future = template.execute(((baseUrl) + "/status/notfound"), GET, null, null);
        future.addCallback(( result) -> fail("onSuccess not expected"), ( ex) -> {
            assertTrue((ex instanceof HttpClientErrorException));
            HttpClientErrorException hcex = ((HttpClientErrorException) (ex));
            assertEquals(HttpStatus.NOT_FOUND, hcex.getStatusCode());
            assertNotNull(hcex.getStatusText());
            assertNotNull(hcex.getResponseBodyAsString());
        });
        waitTillDone(future);
    }

    @Test
    public void serverError() throws Exception {
        try {
            Future<Void> future = template.execute(((baseUrl) + "/status/server"), GET, null, null);
            future.get();
            Assert.fail("HttpServerErrorException expected");
        } catch (ExecutionException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof HttpServerErrorException));
            HttpServerErrorException cause = ((HttpServerErrorException) (ex.getCause()));
            Assert.assertEquals(INTERNAL_SERVER_ERROR, cause.getStatusCode());
            Assert.assertNotNull(cause.getStatusText());
            Assert.assertNotNull(cause.getResponseBodyAsString());
        }
    }

    @Test
    public void serverErrorCallback() throws Exception {
        ListenableFuture<Void> future = template.execute(((baseUrl) + "/status/server"), GET, null, null);
        future.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Assert.fail("onSuccess not expected");
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.assertTrue((ex instanceof HttpServerErrorException));
                HttpServerErrorException hsex = ((HttpServerErrorException) (ex));
                Assert.assertEquals(INTERNAL_SERVER_ERROR, hsex.getStatusCode());
                Assert.assertNotNull(hsex.getStatusText());
                Assert.assertNotNull(hsex.getResponseBodyAsString());
            }
        });
        waitTillDone(future);
    }

    @Test
    public void serverErrorCallbackWithLambdas() throws Exception {
        ListenableFuture<Void> future = template.execute(((baseUrl) + "/status/server"), GET, null, null);
        future.addCallback(( result) -> fail("onSuccess not expected"), ( ex) -> {
            assertTrue((ex instanceof HttpServerErrorException));
            HttpServerErrorException hsex = ((HttpServerErrorException) (ex));
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, hsex.getStatusCode());
            assertNotNull(hsex.getStatusText());
            assertNotNull(hsex.getResponseBodyAsString());
        });
        waitTillDone(future);
    }

    @Test
    public void optionsForAllow() throws Exception {
        Future<Set<HttpMethod>> allowedFuture = template.optionsForAllow(new URI(((baseUrl) + "/get")));
        Set<HttpMethod> allowed = allowedFuture.get();
        Assert.assertEquals("Invalid response", EnumSet.of(GET, OPTIONS, HEAD, TRACE), allowed);
    }

    @Test
    public void optionsForAllowCallback() throws Exception {
        ListenableFuture<Set<HttpMethod>> allowedFuture = template.optionsForAllow(new URI(((baseUrl) + "/get")));
        allowedFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<Set<HttpMethod>>() {
            @Override
            public void onSuccess(Set<HttpMethod> result) {
                Assert.assertEquals("Invalid response", EnumSet.of(GET, OPTIONS, HEAD, TRACE), result);
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(allowedFuture);
    }

    @Test
    public void optionsForAllowCallbackWithLambdas() throws Exception {
        ListenableFuture<Set<HttpMethod>> allowedFuture = template.optionsForAllow(new URI(((baseUrl) + "/get")));
        allowedFuture.addCallback(( result) -> assertEquals("Invalid response", EnumSet.of(HttpMethod.GET, HttpMethod.OPTIONS, HttpMethod.HEAD, HttpMethod.TRACE), result), ( ex) -> fail(ex.getMessage()));
        waitTillDone(allowedFuture);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exchangeGet() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
        Future<ResponseEntity<String>> responseFuture = template.exchange(((baseUrl) + "/{method}"), GET, requestEntity, String.class, "get");
        ResponseEntity<String> response = responseFuture.get();
        Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, response.getBody());
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exchangeGetCallback() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
        ListenableFuture<ResponseEntity<String>> responseFuture = template.exchange(((baseUrl) + "/{method}"), GET, requestEntity, String.class, "get");
        responseFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<ResponseEntity<String>>() {
            @Override
            public void onSuccess(ResponseEntity<String> result) {
                Assert.assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, result.getBody());
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(responseFuture);
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void exchangeGetCallbackWithLambdas() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        HttpEntity<?> requestEntity = new HttpEntity(requestHeaders);
        ListenableFuture<ResponseEntity<String>> responseFuture = template.exchange(((baseUrl) + "/{method}"), GET, requestEntity, String.class, "get");
        responseFuture.addCallback(( result) -> assertEquals("Invalid content", AbstractMockWebServerTestCase.helloWorld, result.getBody()), ( ex) -> fail(ex.getMessage()));
        waitTillDone(responseFuture);
    }

    @Test
    public void exchangePost() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        requestHeaders.setContentType(TEXT_PLAIN);
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, requestHeaders);
        Future<ResponseEntity<Void>> resultFuture = template.exchange(((baseUrl) + "/{method}"), POST, requestEntity, Void.class, "post");
        ResponseEntity<Void> result = resultFuture.get();
        Assert.assertEquals("Invalid location", new URI(((baseUrl) + "/post/1")), result.getHeaders().getLocation());
        Assert.assertFalse(result.hasBody());
    }

    @Test
    public void exchangePostCallback() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        requestHeaders.setContentType(TEXT_PLAIN);
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, requestHeaders);
        ListenableFuture<ResponseEntity<Void>> resultFuture = template.exchange(((baseUrl) + "/{method}"), POST, requestEntity, Void.class, "post");
        final URI expected = new URI(((baseUrl) + "/post/1"));
        resultFuture.addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<ResponseEntity<Void>>() {
            @Override
            public void onSuccess(ResponseEntity<Void> result) {
                Assert.assertEquals("Invalid location", expected, result.getHeaders().getLocation());
                Assert.assertFalse(result.hasBody());
            }

            @Override
            public void onFailure(Throwable ex) {
                Assert.fail(ex.getMessage());
            }
        });
        waitTillDone(resultFuture);
    }

    @Test
    public void exchangePostCallbackWithLambdas() throws Exception {
        org.springframework.http.HttpHeaders requestHeaders = new org.springframework.http.HttpHeaders();
        requestHeaders.set("MyHeader", "MyValue");
        requestHeaders.setContentType(TEXT_PLAIN);
        HttpEntity<String> requestEntity = new HttpEntity(AbstractMockWebServerTestCase.helloWorld, requestHeaders);
        ListenableFuture<ResponseEntity<Void>> resultFuture = template.exchange(((baseUrl) + "/{method}"), POST, requestEntity, Void.class, "post");
        final URI expected = new URI(((baseUrl) + "/post/1"));
        resultFuture.addCallback(( result) -> {
            assertEquals("Invalid location", expected, result.getHeaders().getLocation());
            assertFalse(result.hasBody());
        }, ( ex) -> fail(ex.getMessage()));
        waitTillDone(resultFuture);
    }

    @Test
    public void multipart() throws Exception {
        MultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap();
        parts.add("name 1", "value 1");
        parts.add("name 2", "value 2+1");
        parts.add("name 2", "value 2+2");
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        parts.add("logo", logo);
        HttpEntity<MultiValueMap<String, Object>> requestBody = new HttpEntity(parts);
        Future<URI> future = template.postForLocation(((baseUrl) + "/multipart"), requestBody);
        future.get();
    }

    @Test
    public void getAndInterceptResponse() throws Exception {
        AsyncRestTemplateIntegrationTests.RequestInterceptor interceptor = new AsyncRestTemplateIntegrationTests.RequestInterceptor();
        template.setInterceptors(Collections.singletonList(interceptor));
        ListenableFuture<ResponseEntity<String>> future = template.getForEntity(((baseUrl) + "/get"), String.class);
        interceptor.latch.await(5, TimeUnit.SECONDS);
        Assert.assertNotNull(interceptor.response);
        Assert.assertEquals(OK, interceptor.response.getStatusCode());
        Assert.assertNull(interceptor.exception);
        Assert.assertEquals(AbstractMockWebServerTestCase.helloWorld, future.get().getBody());
    }

    @Test
    public void getAndInterceptError() throws Exception {
        AsyncRestTemplateIntegrationTests.RequestInterceptor interceptor = new AsyncRestTemplateIntegrationTests.RequestInterceptor();
        template.setInterceptors(Collections.singletonList(interceptor));
        template.getForEntity(((baseUrl) + "/status/notfound"), String.class);
        interceptor.latch.await(5, TimeUnit.SECONDS);
        Assert.assertNotNull(interceptor.response);
        Assert.assertEquals(NOT_FOUND, interceptor.response.getStatusCode());
        Assert.assertNull(interceptor.exception);
    }

    private static class RequestInterceptor implements AsyncClientHttpRequestInterceptor {
        private final CountDownLatch latch = new CountDownLatch(1);

        private volatile ClientHttpResponse response;

        private volatile Throwable exception;

        @Override
        public ListenableFuture<ClientHttpResponse> intercept(HttpRequest request, byte[] body, AsyncClientHttpRequestExecution execution) throws IOException {
            ListenableFuture<ClientHttpResponse> future = execution.executeAsync(request, body);
            future.addCallback(( resp) -> {
                response = resp;
                this.latch.countDown();
            }, ( ex) -> {
                exception = ex;
                this.latch.countDown();
            });
            return future;
        }
    }
}

