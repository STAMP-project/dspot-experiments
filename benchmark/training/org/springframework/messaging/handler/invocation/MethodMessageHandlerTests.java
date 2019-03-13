/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.messaging.handler.invocation;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.handler.DestinationPatternsMessageCondition;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;


/**
 * Test fixture for
 * {@link org.springframework.messaging.handler.invocation.AbstractMethodMessageHandler}.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 */
public class MethodMessageHandlerTests {
    private static final String DESTINATION_HEADER = "destination";

    private MethodMessageHandlerTests.TestMethodMessageHandler messageHandler;

    private MethodMessageHandlerTests.TestController testController;

    @Test(expected = IllegalStateException.class)
    public void duplicateMapping() {
        this.messageHandler.registerHandler(new MethodMessageHandlerTests.DuplicateMappingsController());
    }

    @Test
    public void registeredMappings() {
        Map<String, HandlerMethod> handlerMethods = getHandlerMethods();
        Assert.assertNotNull(handlerMethods);
        Assert.assertThat(handlerMethods.keySet(), Matchers.hasSize(3));
    }

    @Test
    public void patternMatch() throws Exception {
        Method method = this.testController.getClass().getMethod("handlerPathMatchWildcard");
        this.messageHandler.registerHandlerMethod(this.testController, method, "/handlerPathMatch*");
        this.messageHandler.handleMessage(toDestination("/test/handlerPathMatchFoo"));
        Assert.assertEquals("pathMatchWildcard", this.testController.method);
    }

    @Test
    public void bestMatch() throws Exception {
        Method method = this.testController.getClass().getMethod("bestMatch");
        this.messageHandler.registerHandlerMethod(this.testController, method, "/bestmatch/{foo}/path");
        method = this.testController.getClass().getMethod("secondBestMatch");
        this.messageHandler.registerHandlerMethod(this.testController, method, "/bestmatch/*/*");
        this.messageHandler.handleMessage(toDestination("/test/bestmatch/bar/path"));
        Assert.assertEquals("bestMatch", this.testController.method);
    }

    @Test
    public void argumentResolution() {
        this.messageHandler.handleMessage(toDestination("/test/handlerArgumentResolver"));
        Assert.assertEquals("handlerArgumentResolver", this.testController.method);
        Assert.assertNotNull(this.testController.arguments.get("message"));
    }

    @Test
    public void handleException() {
        this.messageHandler.handleMessage(toDestination("/test/handlerThrowsExc"));
        Assert.assertEquals("illegalStateException", this.testController.method);
        Assert.assertNotNull(this.testController.arguments.get("exception"));
    }

    @SuppressWarnings("unused")
    private static class TestController {
        public String method;

        private Map<String, Object> arguments = new LinkedHashMap<>();

        public void handlerPathMatchWildcard() {
            this.method = "pathMatchWildcard";
        }

        @SuppressWarnings("rawtypes")
        public void handlerArgumentResolver(Message message) {
            this.method = "handlerArgumentResolver";
            this.arguments.put("message", message);
        }

        public void handlerThrowsExc() {
            throw new IllegalStateException();
        }

        public void bestMatch() {
            this.method = "bestMatch";
        }

        public void secondBestMatch() {
            this.method = "secondBestMatch";
        }

        public void handleIllegalStateException(IllegalStateException exception) {
            this.method = "illegalStateException";
            this.arguments.put("exception", exception);
        }
    }

    @SuppressWarnings("unused")
    private static class DuplicateMappingsController {
        public void handlerFoo() {
        }

        public void handlerFoo(String arg) {
        }
    }

    private static class TestMethodMessageHandler extends AbstractMethodMessageHandler<String> {
        private PathMatcher pathMatcher = new AntPathMatcher();

        public void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }

        public void registerHandlerMethod(Object handler, Method method, String mapping) {
            super.registerHandlerMethod(handler, method, mapping);
        }

        @Override
        protected List<? extends HandlerMethodArgumentResolver> initArgumentResolvers() {
            List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
            resolvers.add(new org.springframework.messaging.handler.annotation.support.MessageMethodArgumentResolver(new SimpleMessageConverter()));
            resolvers.addAll(getCustomArgumentResolvers());
            return resolvers;
        }

        @Override
        protected List<? extends HandlerMethodReturnValueHandler> initReturnValueHandlers() {
            List<HandlerMethodReturnValueHandler> handlers = new ArrayList<>();
            handlers.addAll(getCustomReturnValueHandlers());
            return handlers;
        }

        @Override
        protected boolean isHandler(Class<?> beanType) {
            return beanType.getName().contains("Controller");
        }

        @Override
        protected String getMappingForMethod(Method method, Class<?> handlerType) {
            String methodName = method.getName();
            if (methodName.startsWith("handler")) {
                return "/" + methodName;
            }
            return null;
        }

        @Override
        protected Set<String> getDirectLookupDestinations(String mapping) {
            Set<String> result = new LinkedHashSet<>();
            if (!(this.pathMatcher.isPattern(mapping))) {
                result.add(mapping);
            }
            return result;
        }

        @Override
        protected String getDestination(Message<?> message) {
            return ((String) (message.getHeaders().get(MethodMessageHandlerTests.DESTINATION_HEADER)));
        }

        @Override
        protected String getMatchingMapping(String mapping, Message<?> message) {
            String destination = getLookupDestination(getDestination(message));
            org.springframework.util.Assert.notNull(destination, "No destination");
            return (mapping.equals(destination)) || (this.pathMatcher.match(mapping, destination)) ? mapping : null;
        }

        @Override
        protected Comparator<String> getMappingComparator(final Message<?> message) {
            return ( info1, info2) -> {
                DestinationPatternsMessageCondition cond1 = new DestinationPatternsMessageCondition(info1);
                DestinationPatternsMessageCondition cond2 = new DestinationPatternsMessageCondition(info2);
                return cond1.compareTo(cond2, message);
            };
        }

        @Override
        protected AbstractExceptionHandlerMethodResolver createExceptionHandlerMethodResolverFor(Class<?> beanType) {
            return new TestExceptionResolver(beanType);
        }
    }
}

