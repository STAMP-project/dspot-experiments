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
package org.springframework.scheduling.config;


import TaskManagementConfigUtils.ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME;
import TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Mark Fisher
 * @author Stephane Nicoll
 */
public class AnnotationDrivenBeanDefinitionParserTests {
    private ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("annotationDrivenContext.xml", AnnotationDrivenBeanDefinitionParserTests.class);

    @Test
    public void asyncPostProcessorRegistered() {
        Assert.assertTrue(context.containsBean(ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    @Test
    public void scheduledPostProcessorRegistered() {
        Assert.assertTrue(context.containsBean(SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    @Test
    public void asyncPostProcessorExecutorReference() {
        Object executor = context.getBean("testExecutor");
        Object postProcessor = context.getBean(ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME);
        Assert.assertSame(executor, ((Supplier<?>) (new DirectFieldAccessor(postProcessor).getPropertyValue("executor"))).get());
    }

    @Test
    public void scheduledPostProcessorSchedulerReference() {
        Object scheduler = context.getBean("testScheduler");
        Object postProcessor = context.getBean(SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME);
        Assert.assertSame(scheduler, new DirectFieldAccessor(postProcessor).getPropertyValue("scheduler"));
    }

    @Test
    public void asyncPostProcessorExceptionHandlerReference() {
        Object exceptionHandler = context.getBean("testExceptionHandler");
        Object postProcessor = context.getBean(ASYNC_ANNOTATION_PROCESSOR_BEAN_NAME);
        Assert.assertSame(exceptionHandler, ((Supplier<?>) (new DirectFieldAccessor(postProcessor).getPropertyValue("exceptionHandler"))).get());
    }
}

