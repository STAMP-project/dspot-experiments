/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.internal.annotation;


import LogLevel.INFO;
import LogLevel.TRACE;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.logging.LogLevel;
import com.linecorp.armeria.internal.annotation.AnnotatedHttpServiceFactory.DecoratorAndOrder;
import com.linecorp.armeria.server.DecoratingServiceFunction;
import com.linecorp.armeria.server.Service;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.annotation.Decorator;
import com.linecorp.armeria.server.annotation.DecoratorFactory;
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction;
import com.linecorp.armeria.server.annotation.decorator.LoggingDecorator;
import com.linecorp.armeria.server.annotation.decorator.LoggingDecoratorFactoryFunction;
import com.linecorp.armeria.server.annotation.decorator.RateLimitingDecorator;
import com.linecorp.armeria.server.annotation.decorator.RateLimitingDecoratorFactoryFunction;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;


public class AnnotatedHttpServiceFactoryTest {
    @Test
    public void ofNoOrdering() throws NoSuchMethodException {
        final List<DecoratorAndOrder> list = AnnotatedHttpServiceFactory.collectDecorators(AnnotatedHttpServiceFactoryTest.TestClass.class, AnnotatedHttpServiceFactoryTest.TestClass.class.getMethod("noOrdering"));
        assertThat(AnnotatedHttpServiceFactoryTest.values(list)).containsExactly(AnnotatedHttpServiceFactoryTest.Decorator1.class, LoggingDecoratorFactoryFunction.class, LoggingDecoratorFactoryFunction.class, AnnotatedHttpServiceFactoryTest.Decorator2.class);
        assertThat(AnnotatedHttpServiceFactoryTest.orders(list)).containsExactly(0, 0, 0, 0);
        final LoggingDecorator info = ((LoggingDecorator) (list.get(1).annotation()));
        assertThat(info.requestLogLevel()).isEqualTo(INFO);
        final LoggingDecorator trace = ((LoggingDecorator) (list.get(2).annotation()));
        assertThat(trace.requestLogLevel()).isEqualTo(TRACE);
    }

    @Test
    public void ofMethodScopeOrdering() throws NoSuchMethodException {
        final List<DecoratorAndOrder> list = AnnotatedHttpServiceFactory.collectDecorators(AnnotatedHttpServiceFactoryTest.TestClass.class, AnnotatedHttpServiceFactoryTest.TestClass.class.getMethod("methodScopeOrdering"));
        assertThat(AnnotatedHttpServiceFactoryTest.values(list)).containsExactly(AnnotatedHttpServiceFactoryTest.Decorator1.class, LoggingDecoratorFactoryFunction.class, RateLimitingDecoratorFactoryFunction.class, AnnotatedHttpServiceFactoryTest.Decorator1.class, LoggingDecoratorFactoryFunction.class, AnnotatedHttpServiceFactoryTest.Decorator2.class);
        assertThat(AnnotatedHttpServiceFactoryTest.orders(list)).containsExactly(0, 0, 0, 1, 2, 3);
        final LoggingDecorator info = ((LoggingDecorator) (list.get(1).annotation()));
        assertThat(info.requestLogLevel()).isEqualTo(INFO);
        final RateLimitingDecorator limit = ((RateLimitingDecorator) (list.get(2).annotation()));
        assertThat(limit.value()).isEqualTo(1);
        final LoggingDecorator trace = ((LoggingDecorator) (list.get(4).annotation()));
        assertThat(trace.requestLogLevel()).isEqualTo(TRACE);
    }

    @Test
    public void ofGlobalScopeOrdering() throws NoSuchMethodException {
        final List<DecoratorAndOrder> list = AnnotatedHttpServiceFactory.collectDecorators(AnnotatedHttpServiceFactoryTest.TestClass.class, AnnotatedHttpServiceFactoryTest.TestClass.class.getMethod("globalScopeOrdering"));
        assertThat(AnnotatedHttpServiceFactoryTest.values(list)).containsExactly(LoggingDecoratorFactoryFunction.class, AnnotatedHttpServiceFactoryTest.Decorator1.class, LoggingDecoratorFactoryFunction.class, AnnotatedHttpServiceFactoryTest.Decorator2.class);
        assertThat(AnnotatedHttpServiceFactoryTest.orders(list)).containsExactly((-1), 0, 0, 1);
        final LoggingDecorator trace = ((LoggingDecorator) (list.get(0).annotation()));
        assertThat(trace.requestLogLevel()).isEqualTo(TRACE);
        final LoggingDecorator info = ((LoggingDecorator) (list.get(2).annotation()));
        assertThat(info.requestLogLevel()).isEqualTo(INFO);
    }

    @Test
    public void ofUserDefinedRepeatableDecorator() throws NoSuchMethodException {
        final List<DecoratorAndOrder> list = AnnotatedHttpServiceFactory.collectDecorators(AnnotatedHttpServiceFactoryTest.TestClass.class, AnnotatedHttpServiceFactoryTest.TestClass.class.getMethod("userDefinedRepeatableDecorator"));
        assertThat(AnnotatedHttpServiceFactoryTest.values(list)).containsExactly(AnnotatedHttpServiceFactoryTest.Decorator1.class, LoggingDecoratorFactoryFunction.class, AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecoratorFactory.class, AnnotatedHttpServiceFactoryTest.Decorator2.class, AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecoratorFactory.class);
        assertThat(AnnotatedHttpServiceFactoryTest.orders(list)).containsExactly(0, 0, 1, 2, 3);
        final LoggingDecorator info = ((LoggingDecorator) (list.get(1).annotation()));
        assertThat(info.requestLogLevel()).isEqualTo(INFO);
        final AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator udd1 = ((AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator) (list.get(2).annotation()));
        assertThat(udd1.value()).isEqualTo(1);
        final AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator udd2 = ((AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator) (list.get(4).annotation()));
        assertThat(udd2.value()).isEqualTo(2);
    }

    static class Decorator1 implements DecoratingServiceFunction<HttpRequest, HttpResponse> {
        @Override
        public HttpResponse serve(Service<HttpRequest, HttpResponse> delegate, ServiceRequestContext ctx, HttpRequest req) throws Exception {
            return delegate.serve(ctx, req);
        }
    }

    static class Decorator2 implements DecoratingServiceFunction<HttpRequest, HttpResponse> {
        @Override
        public HttpResponse serve(Service<HttpRequest, HttpResponse> delegate, ServiceRequestContext ctx, HttpRequest req) throws Exception {
            return delegate.serve(ctx, req);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @interface UserDefinedRepeatableDecorators {
        AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator[] value();
    }

    @DecoratorFactory(AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecoratorFactory.class)
    @Repeatable(AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorators.class)
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE, ElementType.METHOD })
    @interface UserDefinedRepeatableDecorator {
        // To identify the decorator instance.
        int value();

        // For ordering.
        int order() default 0;
    }

    static class UserDefinedRepeatableDecoratorFactory implements DecoratorFactoryFunction<AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator> {
        @Override
        public Function<Service<HttpRequest, HttpResponse>, ? extends Service<HttpRequest, HttpResponse>> newDecorator(AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator parameter) {
            return ( service) -> new com.linecorp.armeria.server.SimpleDecoratingService<HttpRequest, HttpResponse>(service) {
                @Override
                public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
                    return service.serve(ctx, req);
                }
            };
        }
    }

    @Decorator(AnnotatedHttpServiceFactoryTest.Decorator1.class)
    @LoggingDecorator(requestLogLevel = LogLevel.INFO)
    static class TestClass {
        @LoggingDecorator
        @Decorator(AnnotatedHttpServiceFactoryTest.Decorator2.class)
        public String noOrdering() {
            return "";
        }

        @RateLimitingDecorator(1)
        @Decorator(value = AnnotatedHttpServiceFactoryTest.Decorator1.class, order = 1)
        @LoggingDecorator(order = 2)
        @Decorator(value = AnnotatedHttpServiceFactoryTest.Decorator2.class, order = 3)
        public String methodScopeOrdering() {
            return "";
        }

        @Decorator(value = AnnotatedHttpServiceFactoryTest.Decorator2.class, order = 1)
        @LoggingDecorator(order = -1)
        public String globalScopeOrdering() {
            return "";
        }

        @AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator(value = 1, order = 1)
        @Decorator(value = AnnotatedHttpServiceFactoryTest.Decorator2.class, order = 2)
        @AnnotatedHttpServiceFactoryTest.UserDefinedRepeatableDecorator(value = 2, order = 3)
        public String userDefinedRepeatableDecorator() {
            return "";
        }
    }
}

