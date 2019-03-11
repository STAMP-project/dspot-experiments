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
package org.springframework.security.web.reactive.result.method.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.expression.BeanResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.method.ResolvableMethod;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthenticationPrincipalArgumentResolverTests {
    @Mock
    ServerWebExchange exchange;

    @Mock
    BindingContext bindingContext;

    @Mock
    Authentication authentication;

    @Mock
    BeanResolver beanResolver;

    ResolvableMethod authenticationPrincipal = ResolvableMethod.on(getClass()).named("authenticationPrincipal").build();

    ResolvableMethod spel = ResolvableMethod.on(getClass()).named("spel").build();

    ResolvableMethod meta = ResolvableMethod.on(getClass()).named("meta").build();

    ResolvableMethod bean = ResolvableMethod.on(getClass()).named("bean").build();

    AuthenticationPrincipalArgumentResolver resolver;

    @Test
    public void supportsParameterAuthenticationPrincipal() throws Exception {
        assertThat(resolver.supportsParameter(this.authenticationPrincipal.arg(String.class))).isTrue();
    }

    @Test
    public void supportsParameterCurrentUser() throws Exception {
        assertThat(resolver.supportsParameter(this.meta.arg(String.class))).isTrue();
    }

    @Test
    public void resolveArgumentWhenIsAuthenticationThenObtainsPrincipal() throws Exception {
        MethodParameter parameter = this.authenticationPrincipal.arg(String.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.block()).isEqualTo(authentication.getPrincipal());
    }

    @Test
    public void resolveArgumentWhenIsNotAuthenticationThenMonoEmpty() throws Exception {
        MethodParameter parameter = this.authenticationPrincipal.arg(String.class);
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(() -> ""));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument).isNotNull();
        assertThat(argument.block()).isNull();
    }

    @Test
    public void resolveArgumentWhenIsEmptyThenMonoEmpty() throws Exception {
        MethodParameter parameter = this.authenticationPrincipal.arg(String.class);
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.empty());
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument).isNotNull();
        assertThat(argument.block()).isNull();
    }

    @Test
    public void resolveArgumentWhenMonoIsAuthenticationThenObtainsPrincipal() throws Exception {
        MethodParameter parameter = this.authenticationPrincipal.arg(Mono.class, String.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.cast(Mono.class).block().block()).isEqualTo(authentication.getPrincipal());
    }

    @Test
    public void resolveArgumentWhenMonoIsAuthenticationAndNoGenericThenObtainsPrincipal() throws Exception {
        MethodParameter parameter = ResolvableMethod.on(getClass()).named("authenticationPrincipalNoGeneric").build().arg(Mono.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.cast(Mono.class).block().block()).isEqualTo(authentication.getPrincipal());
    }

    @Test
    public void resolveArgumentWhenSpelThenObtainsPrincipal() throws Exception {
        AuthenticationPrincipalArgumentResolverTests.MyUser user = new AuthenticationPrincipalArgumentResolverTests.MyUser(3L);
        MethodParameter parameter = this.spel.arg(Long.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.block()).isEqualTo(user.getId());
    }

    @Test
    public void resolveArgumentWhenBeanThenObtainsPrincipal() throws Exception {
        AuthenticationPrincipalArgumentResolverTests.MyUser user = new AuthenticationPrincipalArgumentResolverTests.MyUser(3L);
        MethodParameter parameter = this.bean.arg(Long.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(user);
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mockito.when(this.beanResolver.resolve(ArgumentMatchers.any(), ArgumentMatchers.eq("beanName"))).thenReturn(new AuthenticationPrincipalArgumentResolverTests.Bean());
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.block()).isEqualTo(user.getId());
    }

    @Test
    public void resolveArgumentWhenMetaThenObtainsPrincipal() throws Exception {
        MethodParameter parameter = this.meta.arg(String.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.block()).isEqualTo("user");
    }

    @Test
    public void resolveArgumentWhenErrorOnInvalidTypeImplicit() throws Exception {
        MethodParameter parameter = ResolvableMethod.on(getClass()).named("errorOnInvalidTypeWhenImplicit").build().arg(Integer.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.block()).isNull();
    }

    @Test
    public void resolveArgumentWhenErrorOnInvalidTypeExplicitFalse() throws Exception {
        MethodParameter parameter = ResolvableMethod.on(getClass()).named("errorOnInvalidTypeWhenExplicitFalse").build().arg(Integer.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThat(argument.block()).isNull();
    }

    @Test
    public void resolveArgumentWhenErrorOnInvalidTypeExplicitTrue() throws Exception {
        MethodParameter parameter = ResolvableMethod.on(getClass()).named("errorOnInvalidTypeWhenExplicitTrue").build().arg(Integer.class);
        Mockito.when(authentication.getPrincipal()).thenReturn("user");
        Mockito.when(exchange.getPrincipal()).thenReturn(Mono.just(authentication));
        Mono<Object> argument = resolver.resolveArgument(parameter, bindingContext, exchange);
        assertThatThrownBy(() -> argument.block()).isInstanceOf(ClassCastException.class);
    }

    static class Bean {
        public Long methodName(AuthenticationPrincipalArgumentResolverTests.MyUser user) {
            return user.getId();
        }
    }

    static class MyUser {
        private final Long id;

        MyUser(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }
    }

    @Target({ ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @AuthenticationPrincipal
    public @interface CurrentUser {}
}

