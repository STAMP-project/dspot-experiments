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


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.method.ControllerAdviceBean;


/**
 * Unit tests for {@link RequestResponseBodyAdviceChain}.
 *
 * @author Rossen Stoyanchev
 * @since 4.2
 */
public class RequestResponseBodyAdviceChainTests {
    private String body;

    private MediaType contentType;

    private Class<? extends HttpMessageConverter<?>> converterType;

    private MethodParameter paramType;

    private MethodParameter returnType;

    private ServerHttpRequest request;

    private ServerHttpResponse response;

    @SuppressWarnings("unchecked")
    @Test
    public void requestBodyAdvice() throws IOException {
        RequestBodyAdvice requestAdvice = Mockito.mock(RequestBodyAdvice.class);
        ResponseBodyAdvice<String> responseAdvice = Mockito.mock(ResponseBodyAdvice.class);
        List<Object> advice = Arrays.asList(requestAdvice, responseAdvice);
        RequestResponseBodyAdviceChain chain = new RequestResponseBodyAdviceChain(advice);
        HttpInputMessage wrapped = new org.springframework.http.server.ServletServerHttpRequest(new MockHttpServletRequest());
        BDDMockito.given(requestAdvice.supports(this.paramType, String.class, this.converterType)).willReturn(true);
        BDDMockito.given(requestAdvice.beforeBodyRead(ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.paramType), ArgumentMatchers.eq(String.class), ArgumentMatchers.eq(this.converterType))).willReturn(wrapped);
        Assert.assertSame(wrapped, chain.beforeBodyRead(this.request, this.paramType, String.class, this.converterType));
        String modified = "body++";
        BDDMockito.given(requestAdvice.afterBodyRead(ArgumentMatchers.eq(this.body), ArgumentMatchers.eq(this.request), ArgumentMatchers.eq(this.paramType), ArgumentMatchers.eq(String.class), ArgumentMatchers.eq(this.converterType))).willReturn(modified);
        Assert.assertEquals(modified, chain.afterBodyRead(this.body, this.request, this.paramType, String.class, this.converterType));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void responseBodyAdvice() {
        RequestBodyAdvice requestAdvice = Mockito.mock(RequestBodyAdvice.class);
        ResponseBodyAdvice<String> responseAdvice = Mockito.mock(ResponseBodyAdvice.class);
        List<Object> advice = Arrays.asList(requestAdvice, responseAdvice);
        RequestResponseBodyAdviceChain chain = new RequestResponseBodyAdviceChain(advice);
        String expected = "body++";
        BDDMockito.given(responseAdvice.supports(this.returnType, this.converterType)).willReturn(true);
        BDDMockito.given(responseAdvice.beforeBodyWrite(ArgumentMatchers.eq(this.body), ArgumentMatchers.eq(this.returnType), ArgumentMatchers.eq(this.contentType), ArgumentMatchers.eq(this.converterType), ArgumentMatchers.same(this.request), ArgumentMatchers.same(this.response))).willReturn(expected);
        String actual = ((String) (chain.beforeBodyWrite(this.body, this.returnType, this.contentType, this.converterType, this.request, this.response)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void controllerAdvice() {
        Object adviceBean = new ControllerAdviceBean(new RequestResponseBodyAdviceChainTests.MyControllerAdvice());
        RequestResponseBodyAdviceChain chain = new RequestResponseBodyAdviceChain(Collections.singletonList(adviceBean));
        String actual = ((String) (chain.beforeBodyWrite(this.body, this.returnType, this.contentType, this.converterType, this.request, this.response)));
        Assert.assertEquals("body-MyControllerAdvice", actual);
    }

    @Test
    public void controllerAdviceNotApplicable() {
        Object adviceBean = new ControllerAdviceBean(new RequestResponseBodyAdviceChainTests.TargetedControllerAdvice());
        RequestResponseBodyAdviceChain chain = new RequestResponseBodyAdviceChain(Collections.singletonList(adviceBean));
        String actual = ((String) (chain.beforeBodyWrite(this.body, this.returnType, this.contentType, this.converterType, this.request, this.response)));
        Assert.assertEquals(this.body, actual);
    }

    @ControllerAdvice
    private static class MyControllerAdvice implements ResponseBodyAdvice<String> {
        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public String beforeBodyWrite(String body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
            return body + "-MyControllerAdvice";
        }
    }

    @ControllerAdvice(annotations = Controller.class)
    private static class TargetedControllerAdvice implements ResponseBodyAdvice<String> {
        @Override
        public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
            return true;
        }

        @Override
        public String beforeBodyWrite(String body, MethodParameter returnType, MediaType contentType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest request, ServerHttpResponse response) {
            return body + "-TargetedControllerAdvice";
        }
    }
}

