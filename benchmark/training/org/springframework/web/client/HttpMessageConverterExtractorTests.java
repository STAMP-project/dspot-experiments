/**
 * Copyright 2002-2019 the original author or authors.
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


import HttpStatus.CONTINUE;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;


/**
 * Test fixture for {@link HttpMessageConverter}.
 *
 * @author Arjen Poutsma
 * @author Brian Clozel
 */
public class HttpMessageConverterExtractorTests {
    private HttpMessageConverterExtractor<?> extractor;

    private final ClientHttpResponse response = Mockito.mock(ClientHttpResponse.class);

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void noContent() throws IOException {
        HttpMessageConverter<?> converter = Mockito.mock(HttpMessageConverter.class);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(NO_CONTENT.value());
        Object result = extractor.extractData(response);
        Assert.assertNull(result);
    }

    @Test
    public void notModified() throws IOException {
        HttpMessageConverter<?> converter = Mockito.mock(HttpMessageConverter.class);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(NOT_MODIFIED.value());
        Object result = extractor.extractData(response);
        Assert.assertNull(result);
    }

    @Test
    public void informational() throws IOException {
        HttpMessageConverter<?> converter = Mockito.mock(HttpMessageConverter.class);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(CONTINUE.value());
        Object result = extractor.extractData(response);
        Assert.assertNull(result);
    }

    @Test
    public void zeroContentLength() throws IOException {
        HttpMessageConverter<?> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        responseHeaders.setContentLength(0);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        Object result = extractor.extractData(response);
        Assert.assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void emptyMessageBody() throws IOException {
        HttpMessageConverter<String> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream("".getBytes()));
        Object result = extractor.extractData(response);
        Assert.assertNull(result);
    }

    // gh-22265
    @Test
    @SuppressWarnings("unchecked")
    public void nullMessageBody() throws IOException {
        HttpMessageConverter<String> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(null);
        Object result = extractor.extractData(response);
        Assert.assertNull(result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void normal() throws IOException {
        HttpMessageConverter<String> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        MediaType contentType = MediaType.TEXT_PLAIN;
        responseHeaders.setContentType(contentType);
        String expected = "Foo";
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream(expected.getBytes()));
        BDDMockito.given(converter.canRead(String.class, contentType)).willReturn(true);
        BDDMockito.given(converter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.any(HttpInputMessage.class))).willReturn(expected);
        Object result = extractor.extractData(response);
        Assert.assertEquals(expected, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void cannotRead() throws IOException {
        HttpMessageConverter<String> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        MediaType contentType = MediaType.TEXT_PLAIN;
        responseHeaders.setContentType(contentType);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream("Foobar".getBytes()));
        BDDMockito.given(converter.canRead(String.class, contentType)).willReturn(false);
        exception.expect(RestClientException.class);
        extractor.extractData(response);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void generics() throws IOException {
        GenericHttpMessageConverter<String> converter = Mockito.mock(GenericHttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        MediaType contentType = MediaType.TEXT_PLAIN;
        responseHeaders.setContentType(contentType);
        String expected = "Foo";
        ParameterizedTypeReference<List<String>> reference = new ParameterizedTypeReference<List<String>>() {};
        Type type = reference.getType();
        extractor = new HttpMessageConverterExtractor<List<String>>(type, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream(expected.getBytes()));
        BDDMockito.given(converter.canRead(type, null, contentType)).willReturn(true);
        BDDMockito.given(converter.read(ArgumentMatchers.eq(type), ArgumentMatchers.eq(null), ArgumentMatchers.any(HttpInputMessage.class))).willReturn(expected);
        Object result = extractor.extractData(response);
        Assert.assertEquals(expected, result);
    }

    // SPR-13592
    @Test
    @SuppressWarnings("unchecked")
    public void converterThrowsIOException() throws IOException {
        HttpMessageConverter<String> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        MediaType contentType = MediaType.TEXT_PLAIN;
        responseHeaders.setContentType(contentType);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream("Foobar".getBytes()));
        BDDMockito.given(converter.canRead(String.class, contentType)).willReturn(true);
        BDDMockito.given(converter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.any(HttpInputMessage.class))).willThrow(IOException.class);
        exception.expect(RestClientException.class);
        exception.expectMessage(("Error while extracting response for type " + "[class java.lang.String] and content type [text/plain]"));
        exception.expectCause(Matchers.instanceOf(IOException.class));
        extractor.extractData(response);
    }

    // SPR-13592
    @Test
    @SuppressWarnings("unchecked")
    public void converterThrowsHttpMessageNotReadableException() throws IOException {
        HttpMessageConverter<String> converter = Mockito.mock(HttpMessageConverter.class);
        org.springframework.http.HttpHeaders responseHeaders = new org.springframework.http.HttpHeaders();
        MediaType contentType = MediaType.TEXT_PLAIN;
        responseHeaders.setContentType(contentType);
        extractor = new HttpMessageConverterExtractor(String.class, createConverterList(converter));
        BDDMockito.given(response.getRawStatusCode()).willReturn(OK.value());
        BDDMockito.given(response.getHeaders()).willReturn(responseHeaders);
        BDDMockito.given(response.getBody()).willReturn(new ByteArrayInputStream("Foobar".getBytes()));
        BDDMockito.given(converter.canRead(String.class, contentType)).willThrow(HttpMessageNotReadableException.class);
        exception.expect(RestClientException.class);
        exception.expectMessage(("Error while extracting response for type " + "[class java.lang.String] and content type [text/plain]"));
        exception.expectCause(Matchers.instanceOf(HttpMessageNotReadableException.class));
        extractor.extractData(response);
    }
}

