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


import HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;
import MediaType.ALL;
import MediaType.APPLICATION_OCTET_STREAM;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN;
import MediaType.TEXT_PLAIN_VALUE;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * Test fixture for {@link RequestResponseBodyMethodProcessor} delegating to a
 * mock HttpMessageConverter.
 *
 * <p>Also see {@link RequestResponseBodyMethodProcessorTests}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class RequestResponseBodyMethodProcessorMockTests {
    private HttpMessageConverter<String> stringMessageConverter;

    private HttpMessageConverter<Resource> resourceMessageConverter;

    private HttpMessageConverter<Object> resourceRegionMessageConverter;

    private RequestResponseBodyMethodProcessor processor;

    private ModelAndViewContainer mavContainer;

    private MockHttpServletRequest servletRequest;

    private MockHttpServletResponse servletResponse;

    private NativeWebRequest webRequest;

    private MethodParameter paramRequestBodyString;

    private MethodParameter paramInt;

    private MethodParameter paramValidBean;

    private MethodParameter paramStringNotRequired;

    private MethodParameter paramOptionalString;

    private MethodParameter returnTypeString;

    private MethodParameter returnTypeInt;

    private MethodParameter returnTypeStringProduces;

    private MethodParameter returnTypeResource;

    @Test
    public void supportsParameter() {
        Assert.assertTrue("RequestBody parameter not supported", processor.supportsParameter(paramRequestBodyString));
        Assert.assertFalse("non-RequestBody parameter supported", processor.supportsParameter(paramInt));
    }

    @Test
    public void supportsReturnType() {
        Assert.assertTrue("ResponseBody return type not supported", processor.supportsReturnType(returnTypeString));
        Assert.assertFalse("non-ResponseBody return type supported", processor.supportsReturnType(returnTypeInt));
    }

    @Test
    public void resolveArgument() throws Exception {
        MediaType contentType = MediaType.TEXT_PLAIN;
        servletRequest.addHeader("Content-Type", contentType.toString());
        String body = "Foo";
        servletRequest.setContent(body.getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(stringMessageConverter.canRead(String.class, contentType)).willReturn(true);
        BDDMockito.given(stringMessageConverter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn(body);
        Object result = processor.resolveArgument(paramRequestBodyString, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory());
        Assert.assertEquals("Invalid argument", body, result);
        Assert.assertFalse("The requestHandled flag shouldn't change", mavContainer.isRequestHandled());
    }

    @Test
    public void resolveArgumentNotValid() throws Exception {
        try {
            testResolveArgumentWithValidation(new RequestResponseBodyMethodProcessorMockTests.SimpleBean(null));
            Assert.fail("Expected exception");
        } catch (MethodArgumentNotValidException e) {
            Assert.assertEquals("simpleBean", e.getBindingResult().getObjectName());
            Assert.assertEquals(1, e.getBindingResult().getErrorCount());
            Assert.assertNotNull(e.getBindingResult().getFieldError("name"));
        }
    }

    @Test
    public void resolveArgumentValid() throws Exception {
        testResolveArgumentWithValidation(new RequestResponseBodyMethodProcessorMockTests.SimpleBean("name"));
    }

    @Test(expected = HttpMediaTypeNotSupportedException.class)
    public void resolveArgumentCannotRead() throws Exception {
        MediaType contentType = MediaType.TEXT_PLAIN;
        servletRequest.addHeader("Content-Type", contentType.toString());
        servletRequest.setContent("payload".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(stringMessageConverter.canRead(String.class, contentType)).willReturn(false);
        processor.resolveArgument(paramRequestBodyString, mavContainer, webRequest, null);
    }

    @Test(expected = HttpMediaTypeNotSupportedException.class)
    public void resolveArgumentNoContentType() throws Exception {
        servletRequest.setContent("payload".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(stringMessageConverter.canRead(String.class, APPLICATION_OCTET_STREAM)).willReturn(false);
        processor.resolveArgument(paramRequestBodyString, mavContainer, webRequest, null);
    }

    @Test(expected = HttpMediaTypeNotSupportedException.class)
    public void resolveArgumentInvalidContentType() throws Exception {
        this.servletRequest.setContentType("bad");
        servletRequest.setContent("payload".getBytes(StandardCharsets.UTF_8));
        processor.resolveArgument(paramRequestBodyString, mavContainer, webRequest, null);
    }

    // SPR-9942
    @Test(expected = HttpMessageNotReadableException.class)
    public void resolveArgumentRequiredNoContent() throws Exception {
        servletRequest.setContentType(TEXT_PLAIN_VALUE);
        servletRequest.setContent(new byte[0]);
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(stringMessageConverter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn(null);
        Assert.assertNull(processor.resolveArgument(paramRequestBodyString, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void resolveArgumentNotGetRequests() throws Exception {
        servletRequest.setMethod("GET");
        servletRequest.setContent(new byte[0]);
        BDDMockito.given(stringMessageConverter.canRead(String.class, APPLICATION_OCTET_STREAM)).willReturn(false);
        Assert.assertNull(processor.resolveArgument(paramStringNotRequired, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void resolveArgumentNotRequiredWithContent() throws Exception {
        servletRequest.setContentType("text/plain");
        servletRequest.setContent("body".getBytes());
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(stringMessageConverter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn("body");
        Assert.assertEquals("body", processor.resolveArgument(paramStringNotRequired, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void resolveArgumentNotRequiredNoContent() throws Exception {
        servletRequest.setContentType("text/plain");
        servletRequest.setContent(new byte[0]);
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        Assert.assertNull(processor.resolveArgument(paramStringNotRequired, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    // SPR-13417
    @Test
    public void resolveArgumentNotRequiredNoContentNoContentType() throws Exception {
        servletRequest.setContent(new byte[0]);
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(stringMessageConverter.canRead(String.class, APPLICATION_OCTET_STREAM)).willReturn(false);
        Assert.assertNull(processor.resolveArgument(paramStringNotRequired, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void resolveArgumentOptionalWithContent() throws Exception {
        servletRequest.setContentType("text/plain");
        servletRequest.setContent("body".getBytes());
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(stringMessageConverter.read(ArgumentMatchers.eq(String.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn("body");
        Assert.assertEquals(Optional.of("body"), processor.resolveArgument(paramOptionalString, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void resolveArgumentOptionalNoContent() throws Exception {
        servletRequest.setContentType("text/plain");
        servletRequest.setContent(new byte[0]);
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        Assert.assertEquals(Optional.empty(), processor.resolveArgument(paramOptionalString, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void resolveArgumentOptionalNoContentNoContentType() throws Exception {
        servletRequest.setContent(new byte[0]);
        BDDMockito.given(stringMessageConverter.canRead(String.class, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(stringMessageConverter.canRead(String.class, APPLICATION_OCTET_STREAM)).willReturn(false);
        Assert.assertEquals(Optional.empty(), processor.resolveArgument(paramOptionalString, mavContainer, webRequest, new RequestResponseBodyMethodProcessorMockTests.ValidatingBinderFactory()));
    }

    @Test
    public void handleReturnValue() throws Exception {
        MediaType accepted = MediaType.TEXT_PLAIN;
        servletRequest.addHeader("Accept", accepted.toString());
        String body = "Foo";
        BDDMockito.given(stringMessageConverter.canWrite(String.class, null)).willReturn(true);
        BDDMockito.given(stringMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        BDDMockito.given(stringMessageConverter.canWrite(String.class, accepted)).willReturn(true);
        processor.handleReturnValue(body, returnTypeString, mavContainer, webRequest);
        Assert.assertTrue("The requestHandled flag wasn't set", mavContainer.isRequestHandled());
        Mockito.verify(stringMessageConverter).write(ArgumentMatchers.eq(body), ArgumentMatchers.eq(accepted), ArgumentMatchers.isA(HttpOutputMessage.class));
    }

    @Test
    public void handleReturnValueProduces() throws Exception {
        String body = "Foo";
        servletRequest.addHeader("Accept", "text/*");
        servletRequest.setAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, Collections.singleton(TEXT_HTML));
        BDDMockito.given(stringMessageConverter.canWrite(String.class, TEXT_HTML)).willReturn(true);
        processor.handleReturnValue(body, returnTypeStringProduces, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
        Mockito.verify(stringMessageConverter).write(ArgumentMatchers.eq(body), ArgumentMatchers.eq(TEXT_HTML), ArgumentMatchers.isA(HttpOutputMessage.class));
    }

    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void handleReturnValueNotAcceptable() throws Exception {
        MediaType accepted = MediaType.APPLICATION_ATOM_XML;
        servletRequest.addHeader("Accept", accepted.toString());
        BDDMockito.given(stringMessageConverter.canWrite(String.class, null)).willReturn(true);
        BDDMockito.given(stringMessageConverter.getSupportedMediaTypes()).willReturn(Arrays.asList(TEXT_PLAIN));
        BDDMockito.given(stringMessageConverter.canWrite(String.class, accepted)).willReturn(false);
        processor.handleReturnValue("Foo", returnTypeString, mavContainer, webRequest);
    }

    @Test(expected = HttpMediaTypeNotAcceptableException.class)
    public void handleReturnValueNotAcceptableProduces() throws Exception {
        MediaType accepted = MediaType.TEXT_PLAIN;
        servletRequest.addHeader("Accept", accepted.toString());
        BDDMockito.given(stringMessageConverter.canWrite(String.class, null)).willReturn(true);
        BDDMockito.given(stringMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(TEXT_PLAIN));
        BDDMockito.given(stringMessageConverter.canWrite(String.class, accepted)).willReturn(false);
        processor.handleReturnValue("Foo", returnTypeStringProduces, mavContainer, webRequest);
    }

    @Test
    public void handleReturnTypeResource() throws Exception {
        Resource returnValue = new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8));
        BDDMockito.given(resourceMessageConverter.canWrite(ByteArrayResource.class, null)).willReturn(true);
        BDDMockito.given(resourceMessageConverter.getSupportedMediaTypes()).willReturn(Collections.singletonList(ALL));
        BDDMockito.given(resourceMessageConverter.canWrite(ByteArrayResource.class, APPLICATION_OCTET_STREAM)).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResource, mavContainer, webRequest);
        BDDMockito.then(resourceMessageConverter).should(Mockito.times(1)).write(ArgumentMatchers.any(ByteArrayResource.class), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.any(HttpOutputMessage.class));
        Assert.assertEquals(200, servletResponse.getStatus());
    }

    // SPR-9841
    @Test
    public void handleReturnValueMediaTypeSuffix() throws Exception {
        String body = "Foo";
        MediaType accepted = MediaType.APPLICATION_XHTML_XML;
        List<MediaType> supported = Collections.singletonList(MediaType.valueOf("application/*+xml"));
        servletRequest.addHeader("Accept", accepted);
        BDDMockito.given(stringMessageConverter.canWrite(String.class, null)).willReturn(true);
        BDDMockito.given(stringMessageConverter.getSupportedMediaTypes()).willReturn(supported);
        BDDMockito.given(stringMessageConverter.canWrite(String.class, accepted)).willReturn(true);
        processor.handleReturnValue(body, returnTypeStringProduces, mavContainer, webRequest);
        Assert.assertTrue(mavContainer.isRequestHandled());
        Mockito.verify(stringMessageConverter).write(ArgumentMatchers.eq(body), ArgumentMatchers.eq(accepted), ArgumentMatchers.isA(HttpOutputMessage.class));
    }

    @Test
    public void handleReturnTypeResourceByteRange() throws Exception {
        Resource returnValue = new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8));
        servletRequest.addHeader("Range", "bytes=0-5");
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(null))).willReturn(true);
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM))).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResource, mavContainer, webRequest);
        BDDMockito.then(resourceRegionMessageConverter).should(Mockito.times(1)).write(ArgumentMatchers.anyCollection(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.argThat(( outputMessage) -> "bytes".equals(outputMessage.getHeaders().getFirst(HttpHeaders.ACCEPT_RANGES))));
        Assert.assertEquals(206, servletResponse.getStatus());
    }

    @Test
    public void handleReturnTypeResourceIllegalByteRange() throws Exception {
        Resource returnValue = new ByteArrayResource("Content".getBytes(StandardCharsets.UTF_8));
        servletRequest.addHeader("Range", "illegal");
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(null))).willReturn(true);
        BDDMockito.given(resourceRegionMessageConverter.canWrite(ArgumentMatchers.any(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM))).willReturn(true);
        processor.handleReturnValue(returnValue, returnTypeResource, mavContainer, webRequest);
        BDDMockito.then(resourceRegionMessageConverter).should(Mockito.never()).write(ArgumentMatchers.anyCollection(), ArgumentMatchers.eq(APPLICATION_OCTET_STREAM), ArgumentMatchers.any(HttpOutputMessage.class));
        Assert.assertEquals(416, servletResponse.getStatus());
    }

    private final class ValidatingBinderFactory implements WebDataBinderFactory {
        @Override
        public WebDataBinder createBinder(NativeWebRequest webRequest, @Nullable
        Object target, String objectName) throws Exception {
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.afterPropertiesSet();
            WebDataBinder dataBinder = new WebDataBinder(target, objectName);
            dataBinder.setValidator(validator);
            return dataBinder;
        }
    }

    @SuppressWarnings("unused")
    private static class SimpleBean {
        @NotNull
        private final String name;

        public SimpleBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

