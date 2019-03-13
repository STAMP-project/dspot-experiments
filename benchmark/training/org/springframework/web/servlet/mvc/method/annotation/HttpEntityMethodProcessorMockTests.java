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
package org.springframework.web.servlet.mvc.method.annotation;


import HttpHeaders.ACCEPT_RANGES;
import HttpHeaders.ETAG;
import HttpHeaders.IF_MATCH;
import HttpHeaders.IF_MODIFIED_SINCE;
import HttpHeaders.IF_NONE_MATCH;
import HttpHeaders.IF_UNMODIFIED_SINCE;
import HttpHeaders.RANGE;
import HttpMethod.GET;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.OK;
import HttpStatus.PARTIAL_CONTENT;
import MediaType.ALL;
import MediaType.TEXT_HTML;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Test fixture for {@link HttpEntityMethodProcessor} delegating to a mock
 * {@link HttpMessageConverter}.
 *
 * <p>Also see {@link HttpEntityMethodProcessorTests}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class HttpEntityMethodProcessorMockTests {
    private static final ZoneId GMT = ZoneId.of("GMT");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private HttpEntityMethodProcessor processor;

    private HttpMessageConverter<String> stringHttpMessageConverter;

    private HttpMessageConverter<Resource> resourceMessageConverter;

    private HttpMessageConverter<Object> resourceRegionMessageConverter;

    private MethodParameter paramHttpEntity;

    private MethodParameter paramRequestEntity;

    private MethodParameter paramResponseEntity;

    private MethodParameter paramInt;

    private MethodParameter returnTypeResponseEntity;

    private MethodParameter returnTypeResponseEntityProduces;

    private MethodParameter returnTypeResponseEntityResource;

    private MethodParameter returnTypeHttpEntity;

    private MethodParameter returnTypeHttpEntitySubclass;

    private MethodParameter returnTypeInt;

    private ModelAndViewContainer mavContainer;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private ServletWebRequest webRequest;

    @Test
    public void supportsParameter() {
        Assert.assertTrue("HttpEntity parameter not supported", processor.supportsParameter(paramHttpEntity));
        Assert.assertTrue("RequestEntity parameter not supported", processor.supportsParameter(paramRequestEntity));
        Assert.assertFalse("ResponseEntity parameter supported", processor.supportsParameter(paramResponseEntity));
        Assert.assertFalse("non-entity parameter supported", processor.supportsParameter(paramInt));
    }

    @Test
    public void supportsReturnType() {
        Assert.assertTrue("ResponseEntity return type not supported", processor.supportsReturnType(returnTypeResponseEntity));
        Assert.assertTrue("HttpEntity return type not supported", processor.supportsReturnType(returnTypeHttpEntity));
        Assert.assertTrue("Custom HttpEntity subclass not supported", processor.supportsReturnType(returnTypeHttpEntitySubclass));
        Assert.assertFalse("RequestEntity parameter supported", processor.supportsReturnType(paramRequestEntity));
        Assert.assertFalse("non-ResponseBody return type supported", processor.supportsReturnType(returnTypeInt));
    }

    @Test
    public void shouldResolveHttpEntityArgument() throws Exception {
        String body = "Foo";
        MediaType contentType = TEXT_PLAIN;
        servletRequest.addHeader("Content-Type", contentType.toString());
        servletRequest.setContent(body.getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(stringHttpMessageConverter.canRead(String.class, contentType)).willReturn(true);
        BDDMockito.given(stringHttpMessageConverter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn(body);
        Object result = processor.resolveArgument(paramHttpEntity, mavContainer, webRequest, null);
        Assert.assertTrue((result instanceof HttpEntity));
        Assert.assertFalse("The requestHandled flag shouldn't change", mavContainer.isRequestHandled());
        Assert.assertEquals("Invalid argument", body, ((HttpEntity<?>) (result)).getBody());
    }

    @Test
    public void shouldResolveRequestEntityArgument() throws Exception {
        String body = "Foo";
        MediaType contentType = TEXT_PLAIN;
        servletRequest.addHeader("Content-Type", contentType.toString());
        servletRequest.setMethod("GET");
        servletRequest.setServerName("www.example.com");
        servletRequest.setServerPort(80);
        servletRequest.setRequestURI("/path");
        servletRequest.setContent(body.getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(stringHttpMessageConverter.canRead(String.class, contentType)).willReturn(true);
        BDDMockito.given(stringHttpMessageConverter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn(body);
        Object result = processor.resolveArgument(paramRequestEntity, mavContainer, webRequest, null);
        Assert.assertTrue((result instanceof RequestEntity));
        Assert.assertFalse("The requestHandled flag shouldn't change", mavContainer.isRequestHandled());
        RequestEntity<?> requestEntity = ((RequestEntity<?>) (result));
        Assert.assertEquals("Invalid method", GET, requestEntity.getMethod());
        // using default port (which is 80), so do not need to append the port (-1 means ignore)
        URI uri = new URI("http", null, "www.example.com", (-1), "/path", null, null);
        Assert.assertEquals("Invalid url", uri, requestEntity.getUrl());
        Assert.assertEquals("Invalid argument", body, requestEntity.getBody());
    }

    @Test
    public void shouldFailResolvingWhenConverterCannotRead() throws Exception {
        MediaType contentType = TEXT_PLAIN;
        servletRequest.setMethod("POST");
        servletRequest.addHeader("Content-Type", contentType.toString());
        BDDMockito.given(stringHttpMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(contentType));
        BDDMockito.given(stringHttpMessageConverter.canRead(String.class, contentType)).willReturn(false);
        this.thrown.expect(HttpMediaTypeNotSupportedException.class);
        processor.resolveArgument(paramHttpEntity, mavContainer, webRequest, null);
    }

    @Test
    public void shouldFailResolvingWhenContentTypeNotSupported() throws Exception {
        servletRequest.setMethod("POST");
        servletRequest.setContent("some content".getBytes(StandardCharsets.UTF_8));
        this.thrown.expect(HttpMediaTypeNotSupportedException.class);
        processor.resolveArgument(paramHttpEntity, mavContainer, webRequest, null);
    }

    @Test
    public void shouldHandleReturnValue() throws Exception {
        String body = "Foo";
        ResponseEntity<String> returnValue = new ResponseEntity(body, HttpStatus.OK);
        MediaType accepted = TEXT_PLAIN;
        servletRequest.addHeader("Accept", accepted.toString());
        initStringMessageConversion(accepted);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
        Mockito.verify(stringHttpMessageConverter).write(ArgumentMatchers.eq(body), ArgumentMatchers.eq(accepted), ArgumentMatchers.isA(HttpOutputMessage.class));
    }

    @Test
    public void shouldHandleReturnValueWithProducibleMediaType() throws Exception {
        String body = "Foo";
        ResponseEntity<String> returnValue = new ResponseEntity(body, HttpStatus.OK);
        servletRequest.addHeader("Accept", "text/*");
        servletRequest.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(TEXT_HTML));
        BDDMockito.given(stringHttpMessageConverter.canWrite(String.class, TEXT_HTML)).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityProduces, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
        Mockito.verify(stringHttpMessageConverter).write(ArgumentMatchers.eq(body), ArgumentMatchers.eq(TEXT_HTML), ArgumentMatchers.isA(HttpOutputMessage.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldHandleReturnValueWithResponseBodyAdvice() throws Exception {
        servletRequest.addHeader("Accept", "text/*");
        servletRequest.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(TEXT_HTML));
        ResponseEntity<String> returnValue = new ResponseEntity(HttpStatus.OK);
        ResponseBodyAdvice<String> advice = Mockito.mock(ResponseBodyAdvice.class);
        BDDMockito.given(advice.supports(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(true);
        BDDMockito.given(advice.beforeBodyWrite(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn("Foo");
        HttpEntityMethodProcessor processor = new HttpEntityMethodProcessor(Collections.singletonList(stringHttpMessageConverter), null, Collections.singletonList(advice));
        Mockito.reset(stringHttpMessageConverter);
        BDDMockito.given(stringHttpMessageConverter.canWrite(String.class, TEXT_HTML)).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
        Mockito.verify(stringHttpMessageConverter).write(ArgumentMatchers.eq("Foo"), ArgumentMatchers.eq(TEXT_HTML), ArgumentMatchers.isA(HttpOutputMessage.class));
    }

    @Test
    public void shouldFailHandlingWhenContentTypeNotSupported() throws Exception {
        String body = "Foo";
        ResponseEntity<String> returnValue = new ResponseEntity(body, HttpStatus.OK);
        MediaType accepted = MediaType.APPLICATION_ATOM_XML;
        servletRequest.addHeader("Accept", accepted.toString());
        BDDMockito.given(stringHttpMessageConverter.canWrite(String.class, null)).willReturn(true);
        BDDMockito.given(stringHttpMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        this.thrown.expect(HttpMediaTypeNotAcceptableException.class);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
    }

    @Test
    public void shouldFailHandlingWhenConverterCannotWrite() throws Exception {
        String body = "Foo";
        ResponseEntity<String> returnValue = new ResponseEntity(body, HttpStatus.OK);
        MediaType accepted = TEXT_PLAIN;
        servletRequest.addHeader("Accept", accepted.toString());
        BDDMockito.given(stringHttpMessageConverter.canWrite(String.class, null)).willReturn(true);
        BDDMockito.given(stringHttpMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        BDDMockito.given(stringHttpMessageConverter.canWrite(String.class, accepted)).willReturn(false);
        this.thrown.expect(HttpMediaTypeNotAcceptableException.class);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityProduces, mavContainer, webRequest);
    }

    // SPR-9142
    @Test
    public void shouldFailHandlingWhenAcceptHeaderIllegal() throws Exception {
        ResponseEntity<String> returnValue = new ResponseEntity("Body", HttpStatus.ACCEPTED);
        servletRequest.addHeader("Accept", "01");
        this.thrown.expect(HttpMediaTypeNotAcceptableException.class);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
    }

    @Test
    public void shouldHandleResponseHeaderNoBody() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.set("headerName", "headerValue");
        ResponseEntity<String> returnValue = new ResponseEntity(headers, HttpStatus.ACCEPTED);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
        Assert.assertEquals("headerValue", servletResponse.getHeader("headerName"));
    }

    @Test
    public void shouldHandleResponseHeaderAndBody() throws Exception {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("header", "headerValue");
        ResponseEntity<String> returnValue = new ResponseEntity("body", responseHeaders, HttpStatus.ACCEPTED);
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        ArgumentCaptor<HttpOutputMessage> outputMessage = ArgumentCaptor.forClass(HttpOutputMessage.class);
        Mockito.verify(stringHttpMessageConverter).write(ArgumentMatchers.eq("body"), ArgumentMatchers.eq(TEXT_PLAIN), outputMessage.capture());
        Assert.assertTrue(mavContainer.isRequestHandled());
        Assert.assertEquals("headerValue", outputMessage.getValue().getHeaders().get("header").get(0));
    }

    @Test
    public void shouldHandleLastModifiedWithHttp304() throws Exception {
        long currentTime = new Date().getTime();
        long oneMinuteAgo = currentTime - (1000 * 60);
        ZonedDateTime dateTime = Instant.ofEpochMilli(currentTime).atZone(HttpEntityMethodProcessorMockTests.GMT);
        servletRequest.addHeader(IF_MODIFIED_SINCE, DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
        ResponseEntity<String> returnValue = ResponseEntity.ok().lastModified(oneMinuteAgo).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(NOT_MODIFIED, null, null, oneMinuteAgo);
    }

    @Test
    public void handleEtagWithHttp304() throws Exception {
        String etagValue = "\"deadb33f8badf00d\"";
        servletRequest.addHeader(IF_NONE_MATCH, etagValue);
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(NOT_MODIFIED, null, etagValue, (-1));
    }

    // SPR-14559
    @Test
    public void shouldHandleInvalidIfNoneMatchWithHttp200() throws Exception {
        String etagValue = "\"deadb33f8badf00d\"";
        servletRequest.addHeader(IF_NONE_MATCH, "unquoted");
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(OK, "body", etagValue, (-1));
    }

    @Test
    public void shouldHandleETagAndLastModifiedWithHttp304() throws Exception {
        long currentTime = new Date().getTime();
        long oneMinuteAgo = currentTime - (1000 * 60);
        String etagValue = "\"deadb33f8badf00d\"";
        ZonedDateTime dateTime = Instant.ofEpochMilli(currentTime).atZone(HttpEntityMethodProcessorMockTests.GMT);
        servletRequest.addHeader(IF_MODIFIED_SINCE, DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
        servletRequest.addHeader(IF_NONE_MATCH, etagValue);
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).lastModified(oneMinuteAgo).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(NOT_MODIFIED, null, etagValue, oneMinuteAgo);
    }

    @Test
    public void shouldHandleNotModifiedResponse() throws Exception {
        long currentTime = new Date().getTime();
        long oneMinuteAgo = currentTime - (1000 * 60);
        String etagValue = "\"deadb33f8badf00d\"";
        ResponseEntity<String> returnValue = ResponseEntity.status(NOT_MODIFIED).eTag(etagValue).lastModified(oneMinuteAgo).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(NOT_MODIFIED, null, etagValue, oneMinuteAgo);
    }

    @Test
    public void shouldHandleChangedETagAndLastModified() throws Exception {
        long currentTime = new Date().getTime();
        long oneMinuteAgo = currentTime - (1000 * 60);
        String etagValue = "\"deadb33f8badf00d\"";
        String changedEtagValue = "\"changed-etag-value\"";
        ZonedDateTime dateTime = Instant.ofEpochMilli(currentTime).atZone(HttpEntityMethodProcessorMockTests.GMT);
        servletRequest.addHeader(IF_MODIFIED_SINCE, DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
        servletRequest.addHeader(IF_NONE_MATCH, etagValue);
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(changedEtagValue).lastModified(oneMinuteAgo).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(OK, null, changedEtagValue, oneMinuteAgo);
    }

    // SPR-13496
    @Test
    public void shouldHandleConditionalRequestIfNoneMatchWildcard() throws Exception {
        String wildcardValue = "*";
        String etagValue = "\"some-etag\"";
        servletRequest.setMethod("POST");
        servletRequest.addHeader(IF_NONE_MATCH, wildcardValue);
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(OK, "body", etagValue, (-1));
    }

    // SPR-13626
    @Test
    public void shouldHandleGetIfNoneMatchWildcard() throws Exception {
        String wildcardValue = "*";
        String etagValue = "\"some-etag\"";
        servletRequest.addHeader(IF_NONE_MATCH, wildcardValue);
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(OK, "body", etagValue, (-1));
    }

    // SPR-13626
    @Test
    public void shouldHandleIfNoneMatchIfMatch() throws Exception {
        String etagValue = "\"some-etag\"";
        servletRequest.addHeader(IF_NONE_MATCH, etagValue);
        servletRequest.addHeader(IF_MATCH, "ifmatch");
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(NOT_MODIFIED, null, etagValue, (-1));
    }

    // SPR-13626
    @Test
    public void shouldHandleIfNoneMatchIfUnmodifiedSince() throws Exception {
        String etagValue = "\"some-etag\"";
        servletRequest.addHeader(IF_NONE_MATCH, etagValue);
        ZonedDateTime dateTime = Instant.ofEpochMilli(new Date().getTime()).atZone(HttpEntityMethodProcessorMockTests.GMT);
        servletRequest.addHeader(IF_UNMODIFIED_SINCE, DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
        ResponseEntity<String> returnValue = ResponseEntity.ok().eTag(etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(NOT_MODIFIED, null, etagValue, (-1));
    }

    @Test
    public void shouldHandleResource() throws Exception {
        ResponseEntity<Resource> returnValue = ResponseEntity.ok(new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8)));
        BDDMockito.given(resourceMessageConverter.canWrite(ByteArrayResource.class, null)).willReturn(true);
        BDDMockito.given(resourceMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(ALL));
        BDDMockito.given(resourceMessageConverter.canWrite(ByteArrayResource.class, APPLICATION_OCTET_STREAM)).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityResource, mavContainer, webRequest);
        BDDMockito.then(resourceMessageConverter).should(Mockito.times(1)).write(ArgumentMatchers.any(ByteArrayResource.class), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.any(HttpOutputMessage.class));
        Assert.assertEquals(200, servletResponse.getStatus());
    }

    @Test
    public void shouldHandleResourceByteRange() throws Exception {
        ResponseEntity<Resource> returnValue = ResponseEntity.ok(new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8)));
        servletRequest.addHeader("Range", "bytes=0-5");
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(null))).willReturn(true);
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM))).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityResource, mavContainer, webRequest);
        BDDMockito.then(resourceRegionMessageConverter).should(Mockito.times(1)).write(ArgumentMatchers.anyCollection(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.argThat(( outputMessage) -> "bytes".equals(outputMessage.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES))));
        Assert.assertEquals(206, servletResponse.getStatus());
    }

    @Test
    public void handleReturnTypeResourceIllegalByteRange() throws Exception {
        ResponseEntity<Resource> returnValue = ResponseEntity.ok(new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8)));
        servletRequest.addHeader("Range", "illegal");
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(null))).willReturn(true);
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM))).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityResource, mavContainer, webRequest);
        BDDMockito.then(resourceRegionMessageConverter).should(Mockito.never()).write(ArgumentMatchers.anyCollection(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.any(HttpOutputMessage.class));
        Assert.assertEquals(416, servletResponse.getStatus());
    }

    // SPR-16754
    @Test
    public void disableRangeSupportForStreamingResponses() throws Exception {
        InputStream is = new ByteArrayInputStream("Content".getBytes(StandardCharsets.UTF_8));
        InputStreamResource resource = new InputStreamResource(is, "test");
        ResponseEntity<Resource> returnValue = ResponseEntity.ok(resource);
        servletRequest.addHeader("Range", "bytes=0-5");
        BDDMockito.given(resourceMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(null))).willReturn(true);
        BDDMockito.given(resourceMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM))).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityResource, mavContainer, webRequest);
        BDDMockito.then(resourceMessageConverter).should(Mockito.times(1)).write(ArgumentMatchers.any(InputStreamResource.class), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.any(HttpOutputMessage.class));
        Assert.assertEquals(200, servletResponse.getStatus());
        Assert.assertThat(servletResponse.getHeader(ACCEPT_RANGES), Matchers.isEmptyOrNullString());
    }

    // SPR-16921
    @Test
    public void disableRangeSupportIfContentRangePresent() throws Exception {
        ResponseEntity<Resource> returnValue = ResponseEntity.status(PARTIAL_CONTENT).header(RANGE, "bytes=0-5").body(new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8)));
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(null))).willReturn(true);
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM))).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResponseEntityResource, mavContainer, webRequest);
        BDDMockito.then(resourceRegionMessageConverter).should(Mockito.never()).write(ArgumentMatchers.anyCollection(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertEquals(206, servletResponse.getStatus());
    }

    // SPR-14767
    @Test
    public void shouldHandleValidatorHeadersInputResponses() throws Exception {
        servletRequest.setMethod("PUT");
        String etagValue = "\"some-etag\"";
        ResponseEntity<String> returnValue = ResponseEntity.ok().header(ETAG, etagValue).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(OK, "body", etagValue, (-1));
    }

    @Test
    public void shouldNotFailPreconditionForPutRequests() throws Exception {
        servletRequest.setMethod("PUT");
        ZonedDateTime dateTime = Instant.ofEpochMilli(new Date().getTime()).atZone(HttpEntityMethodProcessorMockTests.GMT);
        servletRequest.addHeader(IF_UNMODIFIED_SINCE, DateTimeFormatter.RFC_1123_DATE_TIME.format(dateTime));
        long justModified = (dateTime.plus(1, ChronoUnit.SECONDS).toEpochSecond()) * 1000;
        ResponseEntity<String> returnValue = ResponseEntity.ok().lastModified(justModified).body("body");
        initStringMessageConversion(TEXT_PLAIN);
        processor.handleReturnValue(returnValue, returnTypeResponseEntity, mavContainer, webRequest);
        assertConditionalResponse(OK, null, null, justModified);
    }

    @Test
    public void varyHeader() throws Exception {
        String[] entityValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] existingValues = new String[]{  };
        String[] expected = new String[]{ "Accept-Language, User-Agent" };
        testVaryHeader(entityValues, existingValues, expected);
    }

    @Test
    public void varyHeaderWithExistingWildcard() throws Exception {
        String[] entityValues = new String[]{ "Accept-Language" };
        String[] existingValues = new String[]{ "*" };
        String[] expected = new String[]{ "*" };
        testVaryHeader(entityValues, existingValues, expected);
    }

    @Test
    public void varyHeaderWithExistingCommaValues() throws Exception {
        String[] entityValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] existingValues = new String[]{ "Accept-Encoding", "Accept-Language" };
        String[] expected = new String[]{ "Accept-Encoding", "Accept-Language", "User-Agent" };
        testVaryHeader(entityValues, existingValues, expected);
    }

    @Test
    public void varyHeaderWithExistingCommaSeparatedValues() throws Exception {
        String[] entityValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] existingValues = new String[]{ "Accept-Encoding, Accept-Language" };
        String[] expected = new String[]{ "Accept-Encoding, Accept-Language", "User-Agent" };
        testVaryHeader(entityValues, existingValues, expected);
    }

    @Test
    public void handleReturnValueVaryHeader() throws Exception {
        String[] entityValues = new String[]{ "Accept-Language", "User-Agent" };
        String[] existingValues = new String[]{ "Accept-Encoding, Accept-Language" };
        String[] expected = new String[]{ "Accept-Encoding, Accept-Language", "User-Agent" };
        testVaryHeader(entityValues, existingValues, expected);
    }

    @SuppressWarnings("unused")
    public static class CustomHttpEntity extends HttpEntity<Object> {}
}

