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
package org.axonframework.spring.config.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanFactory;


public class AnnotationQueryHandlerBeanPostProcessorTest {
    private AnnotationQueryHandlerBeanPostProcessor testSubject;

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testQueryHandlerCallsRedirectToAdapter() throws Exception {
        BeanFactory mockBeanFactory = Mockito.mock(BeanFactory.class);
        testSubject.setBeanFactory(mockBeanFactory);
        Object result1 = testSubject.postProcessBeforeInitialization(new AnnotationQueryHandlerBeanPostProcessorTest.AnnotatedQueryHandler(), "beanName");
        Object postProcessedBean = testSubject.postProcessAfterInitialization(result1, "beanName");
        Assert.assertTrue((postProcessedBean instanceof MessageHandler<?>));
        Assert.assertTrue((postProcessedBean instanceof AnnotationQueryHandlerBeanPostProcessorTest.AnnotatedQueryHandler));
        MessageHandler<QueryMessage<?, ?>> queryHandler = ((MessageHandler<QueryMessage<?, ?>>) (postProcessedBean));
        AnnotationQueryHandlerBeanPostProcessorTest.AnnotatedQueryHandler annotatedQueryHandler = ((AnnotationQueryHandlerBeanPostProcessorTest.AnnotatedQueryHandler) (postProcessedBean));
        QueryMessage<AnnotationQueryHandlerBeanPostProcessorTest.MyQuery, Integer> myCommand = new org.axonframework.queryhandling.GenericQueryMessage(new AnnotationQueryHandlerBeanPostProcessorTest.MyQuery(), ResponseTypes.instanceOf(Integer.class));
        Assert.assertEquals(0, queryHandler.handle(myCommand));
        Assert.assertEquals(1, annotatedQueryHandler.getInvocationCount());
    }

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testQueryHandlerCallsRedirectToAdapterWhenUsingCustomAnnotation() throws Exception {
        BeanFactory mockBeanFactory = Mockito.mock(BeanFactory.class);
        testSubject.setBeanFactory(mockBeanFactory);
        Object result1 = testSubject.postProcessBeforeInitialization(new AnnotationQueryHandlerBeanPostProcessorTest.CustomAnnotatedQueryHandler(), "beanName");
        Object postProcessedBean = testSubject.postProcessAfterInitialization(result1, "beanName");
        Assert.assertTrue((postProcessedBean instanceof MessageHandler<?>));
        Assert.assertTrue((postProcessedBean instanceof AnnotationQueryHandlerBeanPostProcessorTest.CustomAnnotatedQueryHandler));
        MessageHandler<QueryMessage<?, ?>> queryHandler = ((MessageHandler<QueryMessage<?, ?>>) (postProcessedBean));
        AnnotationQueryHandlerBeanPostProcessorTest.CustomAnnotatedQueryHandler annotatedQueryHandler = ((AnnotationQueryHandlerBeanPostProcessorTest.CustomAnnotatedQueryHandler) (postProcessedBean));
        QueryMessage<AnnotationQueryHandlerBeanPostProcessorTest.MyQuery, Integer> myCommand = new org.axonframework.queryhandling.GenericQueryMessage(new AnnotationQueryHandlerBeanPostProcessorTest.MyQuery(), ResponseTypes.instanceOf(Integer.class));
        Assert.assertEquals(0, queryHandler.handle(myCommand));
        Assert.assertEquals(1, annotatedQueryHandler.getInvocationCount());
    }

    public static class AnnotatedQueryHandler {
        private int invocationCount;

        @SuppressWarnings({ "UnusedDeclaration" })
        @QueryHandler
        public Integer handleCommand(AnnotationQueryHandlerBeanPostProcessorTest.MyQuery query) {
            return (invocationCount)++;
        }

        public int getInvocationCount() {
            return invocationCount;
        }
    }

    public static class CustomAnnotatedQueryHandler {
        private int invocationCount;

        @SuppressWarnings({ "UnusedDeclaration" })
        @AnnotationQueryHandlerBeanPostProcessorTest.MyCustomQuery
        public Integer handleCommand(AnnotationQueryHandlerBeanPostProcessorTest.MyQuery query) {
            return (invocationCount)++;
        }

        public int getInvocationCount() {
            return invocationCount;
        }
    }

    private static class MyQuery {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @QueryHandler
    private static @interface MyCustomQuery {}
}

