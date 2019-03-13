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
package org.springframework.messaging.handler.invocation.reactive;


import DestinationPatternsMessageCondition.LOOKUP_DESTINATION_HEADER;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.messaging.handler.invocation.AbstractExceptionHandlerMethodResolver;
import org.springframework.messaging.handler.invocation.TestExceptionResolver;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link AbstractMethodMessageHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class MethodMessageHandlerTests {
    @Test(expected = IllegalStateException.class)
    public void duplicateMapping() {
        initMethodMessageHandler(MethodMessageHandlerTests.DuplicateMappingsController.class);
    }

    @Test
    public void registeredMappings() {
        MethodMessageHandlerTests.TestMethodMessageHandler messageHandler = initMethodMessageHandler(MethodMessageHandlerTests.TestController.class);
        Map<String, HandlerMethod> mappings = getHandlerMethods();
        Assert.assertEquals(5, mappings.keySet().size());
        Assert.assertThat(mappings.keySet(), Matchers.containsInAnyOrder("/handleMessage", "/handleMessageWithArgument", "/handleMessageWithError", "/handleMessageMatch1", "/handleMessageMatch2"));
    }

    @Test
    public void bestMatch() throws NoSuchMethodException {
        MethodMessageHandlerTests.TestMethodMessageHandler handler = new MethodMessageHandlerTests.TestMethodMessageHandler();
        MethodMessageHandlerTests.TestController controller = new MethodMessageHandlerTests.TestController();
        handler.register(controller, MethodMessageHandlerTests.TestController.class.getMethod("handleMessageMatch1"), "/bestmatch/{foo}/path");
        handler.register(controller, MethodMessageHandlerTests.TestController.class.getMethod("handleMessageMatch2"), "/bestmatch/*/*");
        afterPropertiesSet();
        Message<?> message = new org.springframework.messaging.support.GenericMessage("body", Collections.singletonMap(LOOKUP_DESTINATION_HEADER, "/bestmatch/bar/path"));
        handler.handleMessage(message).block(Duration.ofSeconds(5));
        StepVerifier.create(((Publisher<Object>) (handler.getLastReturnValue()))).expectNext("handleMessageMatch1").verifyComplete();
    }

    @Test
    public void argumentResolution() {
        ArgumentResolverConfigurer configurer = new ArgumentResolverConfigurer();
        configurer.addCustomResolver(new StubArgumentResolver(String.class, "foo"));
        MethodMessageHandlerTests.TestMethodMessageHandler handler = initMethodMessageHandler(( theHandler) -> theHandler.setArgumentResolverConfigurer(configurer), MethodMessageHandlerTests.TestController.class);
        Message<?> message = new org.springframework.messaging.support.GenericMessage("body", Collections.singletonMap(LOOKUP_DESTINATION_HEADER, "/handleMessageWithArgument"));
        handler.handleMessage(message).block(Duration.ofSeconds(5));
        StepVerifier.create(((Publisher<Object>) (handler.getLastReturnValue()))).expectNext("handleMessageWithArgument,payload=foo").verifyComplete();
    }

    @Test
    public void handleException() {
        MethodMessageHandlerTests.TestMethodMessageHandler handler = initMethodMessageHandler(MethodMessageHandlerTests.TestController.class);
        Message<?> message = new org.springframework.messaging.support.GenericMessage("body", Collections.singletonMap(LOOKUP_DESTINATION_HEADER, "/handleMessageWithError"));
        handler.handleMessage(message).block(Duration.ofSeconds(5));
        StepVerifier.create(((Publisher<Object>) (handler.getLastReturnValue()))).expectNext("handleIllegalStateException,ex=rejected").verifyComplete();
    }

    @SuppressWarnings("unused")
    private static class TestController {
        public Mono<String> handleMessage() {
            return delay("handleMessage");
        }

        @SuppressWarnings("rawtypes")
        public Mono<String> handleMessageWithArgument(String payload) {
            return delay(("handleMessageWithArgument,payload=" + payload));
        }

        public Mono<String> handleMessageWithError() {
            return Mono.delay(Duration.ofMillis(10)).flatMap(( aLong) -> Mono.error(new IllegalStateException("rejected")));
        }

        public Mono<String> handleMessageMatch1() {
            return delay("handleMessageMatch1");
        }

        public Mono<String> handleMessageMatch2() {
            return delay("handleMessageMatch2");
        }

        public Mono<String> handleIllegalStateException(IllegalStateException ex) {
            return delay(("handleIllegalStateException,ex=" + (ex.getMessage())));
        }

        private Mono<String> delay(String value) {
            return Mono.delay(Duration.ofMillis(10)).map(( aLong) -> value);
        }
    }

    @SuppressWarnings("unused")
    private static class DuplicateMappingsController {
        void handleMessageFoo() {
        }

        void handleMessageFoo(String foo) {
        }
    }

    private static class TestMethodMessageHandler extends AbstractMethodMessageHandler<String> {
        private final TestReturnValueHandler returnValueHandler = new TestReturnValueHandler();

        private PathMatcher pathMatcher = new AntPathMatcher();

        @Override
        protected List<? extends HandlerMethodArgumentResolver> initArgumentResolvers() {
            return Collections.emptyList();
        }

        @Override
        protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
            return Collections.singletonList(this.returnValueHandler);
        }

        @Override
        protected Predicate<Class<?>> initHandlerPredicate() {
            return ( handlerType) -> handlerType.getName().endsWith("Controller");
        }

        @Nullable
        public Object getLastReturnValue() {
            return this.returnValueHandler.getLastReturnValue();
        }

        public void register(Object handler, Method method, String mapping) {
            super.registerHandlerMethod(handler, method, mapping);
        }

        @Override
        protected String getMappingForMethod(Method method, Class<?> handlerType) {
            String methodName = method.getName();
            if (methodName.startsWith("handleMessage")) {
                return "/" + methodName;
            }
            return null;
        }

        @Override
        protected Set<String> getDirectLookupMappings(String mapping) {
            return Collections.singleton(mapping);
        }

        @Override
        @Nullable
        protected String getDestination(Message<?> message) {
            return ((String) (message.getHeaders().get(LOOKUP_DESTINATION_HEADER)));
        }

        @Override
        protected String getMatchingMapping(String mapping, Message<?> message) {
            String destination = getDestination(message);
            org.springframework.util.Assert.notNull(destination, "No destination");
            return (mapping.equals(destination)) || (this.pathMatcher.match(mapping, destination)) ? mapping : null;
        }

        @Override
        protected Comparator<String> getMappingComparator(Message<?> message) {
            return ( info1, info2) -> {
                DestinationPatternsMessageCondition cond1 = new DestinationPatternsMessageCondition(info1);
                DestinationPatternsMessageCondition cond2 = new DestinationPatternsMessageCondition(info2);
                return cond1.compareTo(cond2, message);
            };
        }

        @Override
        protected AbstractExceptionHandlerMethodResolver createExceptionMethodResolverFor(Class<?> beanType) {
            return new TestExceptionResolver(beanType);
        }
    }
}

