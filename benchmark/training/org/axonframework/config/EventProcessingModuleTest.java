/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.config;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.axonframework.common.AxonConfigurationException;
import org.axonframework.common.Registration;
import org.axonframework.eventhandling.async.FullConcurrencyPolicy;
import org.axonframework.eventhandling.async.SequentialPolicy;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.interceptors.CorrelationDataInterceptor;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;


public class EventProcessingModuleTest {
    private Configurer configurer;

    @Test
    public void testAssignmentRules() {
        Map<String, EventProcessingModuleTest.StubEventProcessor> processors = new HashMap<>();
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        EventProcessingModuleTest.AnnotatedBean annotatedBean = new EventProcessingModuleTest.AnnotatedBean();
        EventProcessingModuleTest.AnnotatedBeanSubclass annotatedBeanSubclass = new EventProcessingModuleTest.AnnotatedBeanSubclass();
        // --> java.util.concurrent
        // --> java.util.concurrent
        // --> java.lang
        // --> java.lang
        configurer.eventProcessing().registerEventProcessorFactory(( name, config, eventHandlerInvoker) -> {
            org.axonframework.config.StubEventProcessor processor = new org.axonframework.config.StubEventProcessor(name, eventHandlerInvoker);
            processors.put(name, processor);
            return processor;
        }).assignHandlerInstancesMatching("java.util.concurrent", "concurrent"::equals).registerEventHandler(( c) -> new Object()).registerEventHandler(( c) -> "").registerEventHandler(( c) -> "concurrent").registerEventHandler(( c) -> map).registerEventHandler(( c) -> annotatedBean).registerEventHandler(( c) -> annotatedBeanSubclass);
        Configuration configuration = configurer.start();
        Assert.assertEquals(3, configuration.eventProcessingConfiguration().eventProcessors().size());
        Assert.assertTrue(processors.get("java.util.concurrent").getEventHandlers().contains("concurrent"));
        Assert.assertTrue(processors.get("java.util.concurrent").getEventHandlers().contains(map));
        Assert.assertTrue(processors.get("java.lang").getEventHandlers().contains(""));
        Assert.assertTrue(processors.get("processingGroup").getEventHandlers().contains(annotatedBean));
        Assert.assertTrue(processors.get("processingGroup").getEventHandlers().contains(annotatedBeanSubclass));
    }

    @Test
    public void testProcessorsDefaultToSubscribingWhenUsingSimpleEventBus() {
        Configuration configuration = DefaultConfigurer.defaultConfiguration().configureEventBus(( c) -> SimpleEventBus.builder().build()).eventProcessing(( ep) -> ep.registerEventHandler(( c) -> new org.axonframework.config.SubscribingEventHandler()).registerEventHandler(( c) -> new org.axonframework.config.TrackingEventHandler())).start();
        Assert.assertTrue(configuration.eventProcessingConfiguration().eventProcessor("subscribing").isPresent());
        Assert.assertTrue(configuration.eventProcessingConfiguration().eventProcessor("subscribing").map(( p) -> p instanceof SubscribingEventProcessor).orElse(false));
        Assert.assertTrue(configuration.eventProcessingConfiguration().eventProcessor("tracking").isPresent());
        Assert.assertTrue(configuration.eventProcessingConfiguration().eventProcessor("tracking").map(( p) -> p instanceof SubscribingEventProcessor).orElse(false));
    }

    @Test(expected = AxonConfigurationException.class)
    public void testAssigningATrackingProcessorFailsWhenUsingSimpleEventBus() {
        DefaultConfigurer.defaultConfiguration().configureEventBus(( c) -> SimpleEventBus.builder().build()).eventProcessing(( ep) -> ep.registerEventHandler(( c) -> new org.axonframework.config.SubscribingEventHandler()).registerEventHandler(( c) -> new org.axonframework.config.TrackingEventHandler()).registerTrackingEventProcessor("tracking")).start();
    }

    @Test
    public void testAssignmentRulesOverrideThoseWithLowerPriority() {
        Map<String, EventProcessingModuleTest.StubEventProcessor> processors = new HashMap<>();
        ConcurrentHashMap<Object, Object> map = new ConcurrentHashMap<>();
        // --> java.util.concurrent2
        // --> java.lang
        // --> java.lang
        configurer.eventProcessing().registerEventProcessorFactory(( name, config, handlers) -> {
            org.axonframework.config.StubEventProcessor processor = new org.axonframework.config.StubEventProcessor(name, handlers);
            processors.put(name, processor);
            return processor;
        }).assignHandlerInstancesMatching("java.util.concurrent", "concurrent"::equals).assignHandlerInstancesMatching("java.util.concurrent2", 1, "concurrent"::equals).registerEventHandler(( c) -> new Object()).registerEventHandler(( c) -> "").registerEventHandler(( c) -> "concurrent").registerEventHandler(( c) -> map);// --> java.util.concurrent

        Configuration configuration = configurer.start();
        Assert.assertEquals(3, configuration.eventProcessingConfiguration().eventProcessors().size());
        Assert.assertTrue(processors.get("java.util.concurrent2").getEventHandlers().contains("concurrent"));
        Assert.assertTrue(((processors.get("java.util.concurrent2").getHandlerInterceptors().iterator().next()) instanceof CorrelationDataInterceptor));
        Assert.assertTrue(processors.get("java.util.concurrent").getEventHandlers().contains(map));
        Assert.assertTrue(((processors.get("java.util.concurrent").getHandlerInterceptors().iterator().next()) instanceof CorrelationDataInterceptor));
        Assert.assertTrue(processors.get("java.lang").getEventHandlers().contains(""));
        Assert.assertTrue(((processors.get("java.lang").getHandlerInterceptors().iterator().next()) instanceof CorrelationDataInterceptor));
    }

    @Test
    public void testDefaultAssignToKeepsAnnotationScanning() {
        Map<String, EventProcessingModuleTest.StubEventProcessor> processors = new HashMap<>();
        EventProcessingModuleTest.AnnotatedBean annotatedBean = new EventProcessingModuleTest.AnnotatedBean();
        Object object = new Object();
        // --> java.util.concurrent
        // --> default
        configurer.eventProcessing().registerEventProcessorFactory(( name, config, handlers) -> {
            org.axonframework.config.StubEventProcessor processor = new org.axonframework.config.StubEventProcessor(name, handlers);
            processors.put(name, processor);
            return processor;
        }).assignHandlerInstancesMatching("java.util.concurrent", "concurrent"::equals).byDefaultAssignTo("default").registerEventHandler(( c) -> object).registerEventHandler(( c) -> "concurrent").registerEventHandler(( c) -> annotatedBean);// --> processingGroup

        configurer.start();
        Assert.assertEquals(3, processors.size());
        Assert.assertTrue(processors.get("default").getEventHandlers().contains(object));
        Assert.assertTrue(processors.get("java.util.concurrent").getEventHandlers().contains("concurrent"));
        Assert.assertTrue(processors.get("processingGroup").getEventHandlers().contains(annotatedBean));
    }

    @Test
    public void testTypeAssignment() {
        configurer.eventProcessing().assignHandlerTypesMatching("myGroup", ( c) -> "java.lang".equals(c.getPackage().getName())).registerSaga(Object.class).registerSaga(ConcurrentMap.class).registerSaga(String.class).registerEventHandler(( c) -> new HashMap<>());
        EventProcessingConfiguration configuration = configurer.start().eventProcessingConfiguration();
        Assert.assertEquals(3, configuration.eventProcessors().size());
        Assert.assertTrue(configuration.eventProcessor("myGroup").isPresent());
        Assert.assertTrue(configuration.eventProcessor("java.util").isPresent());
        Assert.assertTrue(configuration.eventProcessor("ConcurrentMapProcessor").isPresent());
    }

    @Test
    public void testAssignSequencingPolicy() throws NoSuchFieldException {
        Object mockHandler = new Object();
        Object specialHandler = new Object();
        SequentialPolicy sequentialPolicy = new SequentialPolicy();
        FullConcurrencyPolicy fullConcurrencyPolicy = new FullConcurrencyPolicy();
        configurer.eventProcessing().registerEventHandler(( c) -> mockHandler).registerEventHandler(( c) -> specialHandler).assignHandlerInstancesMatching("special", specialHandler::equals).byDefaultAssignTo("default").registerDefaultSequencingPolicy(( c) -> sequentialPolicy).registerSequencingPolicy("special", ( c) -> fullConcurrencyPolicy);
        Configuration config = configurer.start();
        AbstractEventProcessor defaultProcessor = config.eventProcessingConfiguration().eventProcessor("default", AbstractEventProcessor.class).get();
        AbstractEventProcessor specialProcessor = config.eventProcessingConfiguration().eventProcessor("special", AbstractEventProcessor.class).get();
        MultiEventHandlerInvoker defaultInvoker = getFieldValue(AbstractEventProcessor.class.getDeclaredField("eventHandlerInvoker"), defaultProcessor);
        MultiEventHandlerInvoker specialInvoker = getFieldValue(AbstractEventProcessor.class.getDeclaredField("eventHandlerInvoker"), specialProcessor);
        Assert.assertEquals(sequentialPolicy, getSequencingPolicy());
        Assert.assertEquals(fullConcurrencyPolicy, getSequencingPolicy());
    }

    @Test
    public void testAssignInterceptors() {
        EventProcessingModuleTest.StubInterceptor interceptor1 = new EventProcessingModuleTest.StubInterceptor();
        EventProcessingModuleTest.StubInterceptor interceptor2 = new EventProcessingModuleTest.StubInterceptor();
        // --> java.util.concurrent2
        // --> java.lang
        configurer.eventProcessing().registerEventProcessor("default", ( name, config, handlers) -> new org.axonframework.config.StubEventProcessor(name, handlers)).byDefaultAssignTo("default").assignHandlerInstancesMatching("concurrent", 1, "concurrent"::equals).registerEventHandler(( c) -> new Object()).registerEventHandler(( c) -> "concurrent").registerHandlerInterceptor("default", ( c) -> interceptor1).registerDefaultHandlerInterceptor(( c, n) -> interceptor2);
        Configuration config = configurer.start();
        // CorrelationDataInterceptor is automatically configured
        Assert.assertEquals(3, config.eventProcessingConfiguration().eventProcessor("default").get().getHandlerInterceptors().size());
    }

    @Test
    public void testConfigureMonitor() throws Exception {
        MessageCollectingMonitor subscribingMonitor = new MessageCollectingMonitor();
        MessageCollectingMonitor trackingMonitor = new MessageCollectingMonitor(1);
        CountDownLatch tokenStoreInvocation = new CountDownLatch(1);
        buildComplexEventHandlingConfiguration(tokenStoreInvocation);
        configurer.eventProcessing().registerMessageMonitor("subscribing", ( c) -> subscribingMonitor).registerMessageMonitor("tracking", ( c) -> trackingMonitor);
        Configuration config = configurer.start();
        try {
            config.eventBus().publish(new GenericEventMessage<Object>("test"));
            Assert.assertEquals(1, subscribingMonitor.getMessages().size());
            Assert.assertTrue(trackingMonitor.await(10, TimeUnit.SECONDS));
            Assert.assertTrue(tokenStoreInvocation.await(10, TimeUnit.SECONDS));
        } finally {
            config.shutdown();
        }
    }

    @Test
    public void testConfigureDefaultListenerInvocationErrorHandler() throws Exception {
        GenericEventMessage<Boolean> errorThrowingEventMessage = new GenericEventMessage(true);
        int expectedListenerInvocationErrorHandlerCalls = 2;
        EventProcessingModuleTest.StubErrorHandler errorHandler = new EventProcessingModuleTest.StubErrorHandler(2);
        CountDownLatch tokenStoreInvocation = new CountDownLatch(1);
        buildComplexEventHandlingConfiguration(tokenStoreInvocation);
        configurer.eventProcessing().registerDefaultListenerInvocationErrorHandler(( config) -> errorHandler);
        Configuration config = configurer.start();
        // noinspection Duplicates
        try {
            config.eventBus().publish(errorThrowingEventMessage);
            Assert.assertTrue(tokenStoreInvocation.await(10, TimeUnit.SECONDS));
            Assert.assertTrue(errorHandler.await(10, TimeUnit.SECONDS));
            Assert.assertEquals(expectedListenerInvocationErrorHandlerCalls, errorHandler.getErrorCounter());
        } finally {
            config.shutdown();
        }
    }

    @Test
    public void testConfigureListenerInvocationErrorHandlerPerEventProcessor() throws Exception {
        GenericEventMessage<Boolean> errorThrowingEventMessage = new GenericEventMessage(true);
        int expectedErrorHandlerCalls = 1;
        EventProcessingModuleTest.StubErrorHandler subscribingErrorHandler = new EventProcessingModuleTest.StubErrorHandler(1);
        EventProcessingModuleTest.StubErrorHandler trackingErrorHandler = new EventProcessingModuleTest.StubErrorHandler(1);
        CountDownLatch tokenStoreInvocation = new CountDownLatch(1);
        buildComplexEventHandlingConfiguration(tokenStoreInvocation);
        configurer.eventProcessing().registerListenerInvocationErrorHandler("subscribing", ( config) -> subscribingErrorHandler).registerListenerInvocationErrorHandler("tracking", ( config) -> trackingErrorHandler);
        Configuration config = configurer.start();
        // noinspection Duplicates
        try {
            config.eventBus().publish(errorThrowingEventMessage);
            Assert.assertEquals(expectedErrorHandlerCalls, subscribingErrorHandler.getErrorCounter());
            Assert.assertTrue(tokenStoreInvocation.await(10, TimeUnit.SECONDS));
            Assert.assertTrue(trackingErrorHandler.await(10, TimeUnit.SECONDS));
            Assert.assertEquals(expectedErrorHandlerCalls, trackingErrorHandler.getErrorCounter());
        } finally {
            config.shutdown();
        }
    }

    @Test
    public void testConfigureDefaultErrorHandler() throws Exception {
        GenericEventMessage<Integer> failingEventMessage = new GenericEventMessage(1000);
        int expectedErrorHandlerCalls = 2;
        EventProcessingModuleTest.StubErrorHandler errorHandler = new EventProcessingModuleTest.StubErrorHandler(2);
        CountDownLatch tokenStoreInvocation = new CountDownLatch(1);
        buildComplexEventHandlingConfiguration(tokenStoreInvocation);
        configurer.eventProcessing().registerDefaultListenerInvocationErrorHandler(( c) -> PropagatingErrorHandler.instance()).registerDefaultErrorHandler(( config) -> errorHandler);
        Configuration config = configurer.start();
        // noinspection Duplicates
        try {
            config.eventBus().publish(failingEventMessage);
            Assert.assertTrue(tokenStoreInvocation.await(10, TimeUnit.SECONDS));
            Assert.assertTrue(errorHandler.await(10, TimeUnit.SECONDS));
            Assert.assertEquals(expectedErrorHandlerCalls, errorHandler.getErrorCounter());
        } finally {
            config.shutdown();
        }
    }

    @Test
    public void testConfigureErrorHandlerPerEventProcessor() throws Exception {
        GenericEventMessage<Integer> failingEventMessage = new GenericEventMessage(1000);
        int expectedErrorHandlerCalls = 1;
        EventProcessingModuleTest.StubErrorHandler subscribingErrorHandler = new EventProcessingModuleTest.StubErrorHandler(1);
        EventProcessingModuleTest.StubErrorHandler trackingErrorHandler = new EventProcessingModuleTest.StubErrorHandler(1);
        CountDownLatch tokenStoreInvocation = new CountDownLatch(1);
        buildComplexEventHandlingConfiguration(tokenStoreInvocation);
        configurer.eventProcessing().registerDefaultListenerInvocationErrorHandler(( c) -> PropagatingErrorHandler.instance()).registerErrorHandler("subscribing", ( config) -> subscribingErrorHandler).registerErrorHandler("tracking", ( config) -> trackingErrorHandler);
        Configuration config = configurer.start();
        // noinspection Duplicates
        try {
            config.eventBus().publish(failingEventMessage);
            Assert.assertEquals(expectedErrorHandlerCalls, subscribingErrorHandler.getErrorCounter());
            Assert.assertTrue(tokenStoreInvocation.await(10, TimeUnit.SECONDS));
            Assert.assertTrue(trackingErrorHandler.await(10, TimeUnit.SECONDS));
            Assert.assertEquals(expectedErrorHandlerCalls, trackingErrorHandler.getErrorCounter());
        } finally {
            config.shutdown();
        }
    }

    @SuppressWarnings("WeakerAccess")
    private static class StubEventProcessor implements EventProcessor {
        private final String name;

        private final EventHandlerInvoker eventHandlerInvoker;

        private final List<MessageHandlerInterceptor<? super EventMessage<?>>> interceptors = new ArrayList<>();

        public StubEventProcessor(String name, EventHandlerInvoker eventHandlerInvoker) {
            this.name = name;
            this.eventHandlerInvoker = eventHandlerInvoker;
        }

        @Override
        public String getName() {
            return name;
        }

        public EventHandlerInvoker getEventHandlerInvoker() {
            return eventHandlerInvoker;
        }

        public List<?> getEventHandlers() {
            return eventHandlers();
        }

        @Override
        public Registration registerHandlerInterceptor(MessageHandlerInterceptor<? super EventMessage<?>> interceptor) {
            interceptors.add(interceptor);
            return () -> interceptors.remove(interceptor);
        }

        @Override
        public List<MessageHandlerInterceptor<? super EventMessage<?>>> getHandlerInterceptors() {
            return interceptors;
        }

        @Override
        public void start() {
            // noop
        }

        @Override
        public void shutDown() {
            // noop
        }
    }

    @SuppressWarnings("WeakerAccess")
    @ProcessingGroup("processingGroup")
    public static class AnnotatedBean {}

    @SuppressWarnings("WeakerAccess")
    public static class AnnotatedBeanSubclass extends EventProcessingModuleTest.AnnotatedBean {}

    private static class StubInterceptor implements MessageHandlerInterceptor<EventMessage<?>> {
        @Override
        public Object handle(UnitOfWork<? extends EventMessage<?>> unitOfWork, InterceptorChain interceptorChain) throws Exception {
            return interceptorChain.proceed();
        }
    }

    @SuppressWarnings("unused")
    @ProcessingGroup("subscribing")
    private class SubscribingEventHandler {
        @EventHandler
        public void handle(Integer event, UnitOfWork unitOfWork) {
            throw new IllegalStateException();
        }

        @EventHandler
        public void handle(Boolean event) {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings("unused")
    @ProcessingGroup("tracking")
    private class TrackingEventHandler {
        @EventHandler
        public void handle(String event) {
        }

        @EventHandler
        public void handle(Integer event, UnitOfWork unitOfWork) {
            throw new IllegalStateException();
        }

        @EventHandler
        public void handle(Boolean event) {
            throw new IllegalStateException();
        }
    }

    private class StubErrorHandler implements ErrorHandler , ListenerInvocationErrorHandler {
        private final CountDownLatch latch;

        private final AtomicInteger errorCounter = new AtomicInteger();

        private StubErrorHandler(int count) {
            this.latch = new CountDownLatch(count);
        }

        @Override
        public void handleError(ErrorContext errorContext) {
            errorCounter.incrementAndGet();
            latch.countDown();
        }

        @Override
        public void onError(Exception exception, EventMessage<?> event, EventMessageHandler eventHandler) {
            errorCounter.incrementAndGet();
            latch.countDown();
        }

        @SuppressWarnings("WeakerAccess")
        public int getErrorCounter() {
            return errorCounter.get();
        }

        public boolean await(long timeout, TimeUnit timeUnit) throws InterruptedException {
            return latch.await(timeout, timeUnit);
        }
    }
}

