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
package org.springframework.web.reactive.result.method.annotation;


import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.InvocableHandlerMethod;
import org.springframework.web.reactive.result.method.SyncHandlerMethodArgumentResolver;
import org.springframework.web.reactive.result.method.SyncInvocableHandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link ControllerMethodResolver}.
 *
 * @author Rossen Stoyanchev
 */
public class ControllerMethodResolverTests {
    private ControllerMethodResolver methodResolver;

    private HandlerMethod handlerMethod;

    @Test
    public void requestMappingArgumentResolvers() {
        InvocableHandlerMethod invocable = this.methodResolver.getRequestMappingMethod(this.handlerMethod);
        List<HandlerMethodArgumentResolver> resolvers = invocable.getResolvers();
        AtomicInteger index = new AtomicInteger((-1));
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestBodyArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestPartMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(CookieValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ExpressionValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(SessionAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(HttpEntityArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ErrorsMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ServerWebExchangeArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PrincipalArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(SessionStatusMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(WebSessionArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomSyncArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
    }

    @Test
    public void modelAttributeArgumentResolvers() {
        List<InvocableHandlerMethod> methods = this.methodResolver.getModelAttributeMethods(this.handlerMethod);
        Assert.assertEquals("Expected one each from Controller + ControllerAdvice", 2, methods.size());
        InvocableHandlerMethod invocable = methods.get(0);
        List<HandlerMethodArgumentResolver> resolvers = invocable.getResolvers();
        AtomicInteger index = new AtomicInteger((-1));
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(CookieValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ExpressionValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(SessionAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ErrorsMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ServerWebExchangeArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PrincipalArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(WebSessionArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomSyncArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
    }

    @Test
    public void initBinderArgumentResolvers() {
        List<SyncInvocableHandlerMethod> methods = this.methodResolver.getInitBinderMethods(this.handlerMethod);
        Assert.assertEquals("Expected one each from Controller + ControllerAdvice", 2, methods.size());
        SyncInvocableHandlerMethod invocable = methods.get(0);
        List<SyncHandlerMethodArgumentResolver> resolvers = invocable.getResolvers();
        AtomicInteger index = new AtomicInteger((-1));
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(CookieValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ExpressionValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ServerWebExchangeArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomSyncArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
    }

    @Test
    public void exceptionHandlerArgumentResolvers() {
        InvocableHandlerMethod invocable = this.methodResolver.getExceptionHandlerMethod(new ResponseStatusException(HttpStatus.BAD_REQUEST, "reason"), this.handlerMethod);
        Assert.assertNotNull("No match", invocable);
        Assert.assertEquals(ControllerMethodResolverTests.TestController.class, invocable.getBeanType());
        List<HandlerMethodArgumentResolver> resolvers = invocable.getResolvers();
        AtomicInteger index = new AtomicInteger((-1));
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PathVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(MatrixVariableMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestHeaderMapMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(CookieValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ExpressionValueMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(SessionAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestAttributeMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ModelArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ServerWebExchangeArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(PrincipalArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(WebSessionArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(ControllerMethodResolverTests.CustomSyncArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
        Assert.assertEquals(RequestParamMethodArgumentResolver.class, ControllerMethodResolverTests.next(resolvers, index).getClass());
    }

    @Test
    public void exceptionHandlerFromControllerAdvice() {
        InvocableHandlerMethod invocable = this.methodResolver.getExceptionHandlerMethod(new IllegalStateException("reason"), this.handlerMethod);
        Assert.assertNotNull(invocable);
        Assert.assertEquals(ControllerMethodResolverTests.TestControllerAdvice.class, invocable.getBeanType());
    }

    @Controller
    static class TestController {
        @InitBinder
        void initDataBinder() {
        }

        @ModelAttribute
        void initModel() {
        }

        @GetMapping
        void handle() {
        }

        @ExceptionHandler
        void handleException(ResponseStatusException ex) {
        }
    }

    @ControllerAdvice
    static class TestControllerAdvice {
        @InitBinder
        void initDataBinder() {
        }

        @ModelAttribute
        void initModel() {
        }

        @ExceptionHandler
        void handleException(IllegalStateException ex) {
        }
    }

    static class CustomArgumentResolver implements HandlerMethodArgumentResolver {
        @Override
        public boolean supportsParameter(MethodParameter p) {
            return false;
        }

        @Override
        public Mono<Object> resolveArgument(MethodParameter p, BindingContext c, ServerWebExchange e) {
            return null;
        }
    }

    static class CustomSyncArgumentResolver extends ControllerMethodResolverTests.CustomArgumentResolver implements SyncHandlerMethodArgumentResolver {
        @Override
        public Object resolveArgumentValue(MethodParameter p, BindingContext c, ServerWebExchange e) {
            return null;
        }
    }
}

