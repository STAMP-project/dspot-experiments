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


import MediaType.TEXT_PLAIN;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.Part;
import javax.validation.constraints.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockMultipartFile;
import org.springframework.mock.web.test.MockMultipartHttpServletRequest;
import org.springframework.mock.web.test.MockPart;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;


/**
 * Test fixture with {@link RequestPartMethodArgumentResolver} and mock {@link HttpMessageConverter}.
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 */
public class RequestPartMethodArgumentResolverTests {
    private HttpMessageConverter<RequestPartMethodArgumentResolverTests.SimpleBean> messageConverter;

    private RequestPartMethodArgumentResolver resolver;

    private MultipartFile multipartFile1;

    private MultipartFile multipartFile2;

    private MockMultipartHttpServletRequest multipartRequest;

    private NativeWebRequest webRequest;

    private MethodParameter paramRequestPart;

    private MethodParameter paramNamedRequestPart;

    private MethodParameter paramValidRequestPart;

    private MethodParameter paramMultipartFile;

    private MethodParameter paramMultipartFileList;

    private MethodParameter paramMultipartFileArray;

    private MethodParameter paramInt;

    private MethodParameter paramMultipartFileNotAnnot;

    private MethodParameter paramPart;

    private MethodParameter paramPartList;

    private MethodParameter paramPartArray;

    private MethodParameter paramRequestParamAnnot;

    private MethodParameter optionalMultipartFile;

    private MethodParameter optionalMultipartFileList;

    private MethodParameter optionalPart;

    private MethodParameter optionalPartList;

    private MethodParameter optionalRequestPart;

    @Test
    public void supportsParameter() {
        Assert.assertTrue(resolver.supportsParameter(paramRequestPart));
        Assert.assertTrue(resolver.supportsParameter(paramNamedRequestPart));
        Assert.assertTrue(resolver.supportsParameter(paramValidRequestPart));
        Assert.assertTrue(resolver.supportsParameter(paramMultipartFile));
        Assert.assertTrue(resolver.supportsParameter(paramMultipartFileList));
        Assert.assertTrue(resolver.supportsParameter(paramMultipartFileArray));
        Assert.assertFalse(resolver.supportsParameter(paramInt));
        Assert.assertTrue(resolver.supportsParameter(paramMultipartFileNotAnnot));
        Assert.assertTrue(resolver.supportsParameter(paramPart));
        Assert.assertTrue(resolver.supportsParameter(paramPartList));
        Assert.assertTrue(resolver.supportsParameter(paramPartArray));
        Assert.assertFalse(resolver.supportsParameter(paramRequestParamAnnot));
        Assert.assertTrue(resolver.supportsParameter(optionalMultipartFile));
        Assert.assertTrue(resolver.supportsParameter(optionalMultipartFileList));
        Assert.assertTrue(resolver.supportsParameter(optionalPart));
        Assert.assertTrue(resolver.supportsParameter(optionalPartList));
        Assert.assertTrue(resolver.supportsParameter(optionalRequestPart));
    }

    @Test
    public void resolveMultipartFile() throws Exception {
        Object actual = resolver.resolveArgument(paramMultipartFile, null, webRequest, null);
        Assert.assertSame(multipartFile1, actual);
    }

    @Test
    public void resolveMultipartFileList() throws Exception {
        Object actual = resolver.resolveArgument(paramMultipartFileList, null, webRequest, null);
        Assert.assertTrue((actual instanceof List));
        Assert.assertEquals(Arrays.asList(multipartFile1, multipartFile2), actual);
    }

    @Test
    public void resolveMultipartFileArray() throws Exception {
        Object actual = resolver.resolveArgument(paramMultipartFileArray, null, webRequest, null);
        Assert.assertNotNull(actual);
        Assert.assertTrue((actual instanceof MultipartFile[]));
        MultipartFile[] parts = ((MultipartFile[]) (actual));
        Assert.assertEquals(2, parts.length);
        Assert.assertEquals(parts[0], multipartFile1);
        Assert.assertEquals(parts[1], multipartFile2);
    }

    @Test
    public void resolveMultipartFileNotAnnotArgument() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MultipartFile expected = new MockMultipartFile("multipartFileNotAnnot", "Hello World".getBytes());
        request.addFile(expected);
        request.addFile(new MockMultipartFile("otherPart", "", "text/plain", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object result = resolver.resolveArgument(paramMultipartFileNotAnnot, null, webRequest, null);
        Assert.assertTrue((result instanceof MultipartFile));
        Assert.assertEquals("Invalid result", expected, result);
    }

    @Test
    public void resolvePartArgument() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        MockPart expected = new MockPart("part", "Hello World".getBytes());
        request.addPart(expected);
        request.addPart(new MockPart("otherPart", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object result = resolver.resolveArgument(paramPart, null, webRequest, null);
        Assert.assertTrue((result instanceof Part));
        Assert.assertEquals("Invalid result", expected, result);
    }

    @Test
    public void resolvePartListArgument() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        MockPart part1 = new MockPart("requestPart", "Hello World 1".getBytes());
        MockPart part2 = new MockPart("requestPart", "Hello World 2".getBytes());
        request.addPart(part1);
        request.addPart(part2);
        request.addPart(new MockPart("otherPart", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object result = resolver.resolveArgument(paramPartList, null, webRequest, null);
        Assert.assertTrue((result instanceof List));
        Assert.assertEquals(Arrays.asList(part1, part2), result);
    }

    @Test
    public void resolvePartArrayArgument() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        MockPart part1 = new MockPart("requestPart", "Hello World 1".getBytes());
        MockPart part2 = new MockPart("requestPart", "Hello World 2".getBytes());
        request.addPart(part1);
        request.addPart(part2);
        request.addPart(new MockPart("otherPart", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object result = resolver.resolveArgument(paramPartArray, null, webRequest, null);
        Assert.assertTrue((result instanceof Part[]));
        Part[] parts = ((Part[]) (result));
        Assert.assertEquals(2, parts.length);
        Assert.assertEquals(parts[0], part1);
        Assert.assertEquals(parts[1], part2);
    }

    @Test
    public void resolveRequestPart() throws Exception {
        testResolveArgument(new RequestPartMethodArgumentResolverTests.SimpleBean("foo"), paramRequestPart);
    }

    @Test
    public void resolveNamedRequestPart() throws Exception {
        testResolveArgument(new RequestPartMethodArgumentResolverTests.SimpleBean("foo"), paramNamedRequestPart);
    }

    @Test
    public void resolveNamedRequestPartNotPresent() throws Exception {
        testResolveArgument(null, paramNamedRequestPart);
    }

    @Test
    public void resolveRequestPartNotValid() throws Exception {
        try {
            testResolveArgument(new RequestPartMethodArgumentResolverTests.SimpleBean(null), paramValidRequestPart);
            Assert.fail("Expected exception");
        } catch (MethodArgumentNotValidException ex) {
            Assert.assertEquals("requestPart", ex.getBindingResult().getObjectName());
            Assert.assertEquals(1, ex.getBindingResult().getErrorCount());
            Assert.assertNotNull(ex.getBindingResult().getFieldError("name"));
        }
    }

    @Test
    public void resolveRequestPartValid() throws Exception {
        testResolveArgument(new RequestPartMethodArgumentResolverTests.SimpleBean("foo"), paramValidRequestPart);
    }

    @Test
    public void resolveRequestPartRequired() throws Exception {
        try {
            testResolveArgument(null, paramValidRequestPart);
            Assert.fail("Expected exception");
        } catch (MissingServletRequestPartException ex) {
            Assert.assertEquals("requestPart", ex.getRequestPartName());
        }
    }

    @Test
    public void resolveRequestPartNotRequired() throws Exception {
        testResolveArgument(new RequestPartMethodArgumentResolverTests.SimpleBean("foo"), paramValidRequestPart);
    }

    @Test(expected = MultipartException.class)
    public void isMultipartRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        resolver.resolveArgument(paramMultipartFile, new ModelAndViewContainer(), new org.springframework.web.context.request.ServletWebRequest(request), null);
    }

    // SPR-9079
    @Test
    public void isMultipartRequestPut() throws Exception {
        this.multipartRequest.setMethod("PUT");
        Object actualValue = resolver.resolveArgument(paramMultipartFile, null, webRequest, null);
        Assert.assertSame(multipartFile1, actualValue);
    }

    @Test
    public void resolveOptionalMultipartFileArgument() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MultipartFile expected = new MockMultipartFile("optionalMultipartFile", "Hello World".getBytes());
        request.addFile(expected);
        request.addFile(new MockMultipartFile("otherPart", "", "text/plain", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalMultipartFile, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", expected, ((Optional<?>) (actualValue)).get());
        actualValue = resolver.resolveArgument(optionalMultipartFile, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", expected, ((Optional<?>) (actualValue)).get());
    }

    @Test
    public void resolveOptionalMultipartFileArgumentNotPresent() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalMultipartFile, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalMultipartFile, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalMultipartFileArgumentWithoutMultipartRequest() throws Exception {
        webRequest = new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest());
        Object actualValue = resolver.resolveArgument(optionalMultipartFile, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalMultipartFile, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalMultipartFileList() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MultipartFile expected = new MockMultipartFile("requestPart", "Hello World".getBytes());
        request.addFile(expected);
        request.addFile(new MockMultipartFile("otherPart", "", "text/plain", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalMultipartFileList, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", Collections.singletonList(expected), ((Optional<?>) (actualValue)).get());
        actualValue = resolver.resolveArgument(optionalMultipartFileList, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", Collections.singletonList(expected), ((Optional<?>) (actualValue)).get());
    }

    @Test
    public void resolveOptionalMultipartFileListNotPresent() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalMultipartFileList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalMultipartFileList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalMultipartFileListWithoutMultipartRequest() throws Exception {
        webRequest = new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest());
        Object actualValue = resolver.resolveArgument(optionalMultipartFileList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalMultipartFileList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalPartArgument() throws Exception {
        MockPart expected = new MockPart("optionalPart", "Hello World".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        request.addPart(expected);
        request.addPart(new MockPart("otherPart", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalPart, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", expected, ((Optional<?>) (actualValue)).get());
        actualValue = resolver.resolveArgument(optionalPart, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", expected, ((Optional<?>) (actualValue)).get());
    }

    @Test
    public void resolveOptionalPartArgumentNotPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalPartArgumentWithoutMultipartRequest() throws Exception {
        webRequest = new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest());
        Object actualValue = resolver.resolveArgument(optionalPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalPartList() throws Exception {
        MockPart expected = new MockPart("requestPart", "Hello World".getBytes());
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        request.addPart(expected);
        request.addPart(new MockPart("otherPart", "Hello World".getBytes()));
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalPartList, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", Collections.singletonList(expected), ((Optional<?>) (actualValue)).get());
        actualValue = resolver.resolveArgument(optionalPartList, null, webRequest, null);
        Assert.assertTrue((actualValue instanceof Optional));
        Assert.assertEquals("Invalid result", Collections.singletonList(expected), ((Optional<?>) (actualValue)).get());
    }

    @Test
    public void resolveOptionalPartListNotPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalPartList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalPartList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalPartListWithoutMultipartRequest() throws Exception {
        webRequest = new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest());
        Object actualValue = resolver.resolveArgument(optionalPartList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalPartList, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalRequestPart() throws Exception {
        RequestPartMethodArgumentResolverTests.SimpleBean simpleBean = new RequestPartMethodArgumentResolverTests.SimpleBean("foo");
        BDDMockito.given(messageConverter.canRead(RequestPartMethodArgumentResolverTests.SimpleBean.class, TEXT_PLAIN)).willReturn(true);
        BDDMockito.given(messageConverter.read(ArgumentMatchers.eq(RequestPartMethodArgumentResolverTests.SimpleBean.class), ArgumentMatchers.isA(HttpInputMessage.class))).willReturn(simpleBean);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        Object actualValue = resolver.resolveArgument(optionalRequestPart, mavContainer, webRequest, new RequestPartMethodArgumentResolverTests.ValidatingBinderFactory());
        Assert.assertEquals("Invalid argument value", Optional.of(simpleBean), actualValue);
        Assert.assertFalse("The requestHandled flag shouldn't change", mavContainer.isRequestHandled());
        actualValue = resolver.resolveArgument(optionalRequestPart, mavContainer, webRequest, new RequestPartMethodArgumentResolverTests.ValidatingBinderFactory());
        Assert.assertEquals("Invalid argument value", Optional.of(simpleBean), actualValue);
        Assert.assertFalse("The requestHandled flag shouldn't change", mavContainer.isRequestHandled());
    }

    @Test
    public void resolveOptionalRequestPartNotPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("multipart/form-data");
        webRequest = new org.springframework.web.context.request.ServletWebRequest(request);
        Object actualValue = resolver.resolveArgument(optionalRequestPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalRequestPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    @Test
    public void resolveOptionalRequestPartWithoutMultipartRequest() throws Exception {
        webRequest = new org.springframework.web.context.request.ServletWebRequest(new MockHttpServletRequest());
        Object actualValue = resolver.resolveArgument(optionalRequestPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
        actualValue = resolver.resolveArgument(optionalRequestPart, null, webRequest, null);
        Assert.assertEquals("Invalid argument value", Optional.empty(), actualValue);
    }

    private static class SimpleBean {
        @NotNull
        private final String name;

        public SimpleBean(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }
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
}

