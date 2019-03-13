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
package org.springframework.scheduling.aspectj;


import TaskManagementConfigUtils.ASYNC_EXECUTION_ASPECT_BEAN_NAME;
import java.util.function.Supplier;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.context.ConfigurableApplicationContext;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class AnnotationDrivenBeanDefinitionParserTests {
    private ConfigurableApplicationContext context;

    @Test
    public void asyncAspectRegistered() {
        Assert.assertTrue(context.containsBean(ASYNC_EXECUTION_ASPECT_BEAN_NAME));
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void asyncPostProcessorExecutorReference() {
        Object executor = context.getBean("testExecutor");
        Object aspect = context.getBean(ASYNC_EXECUTION_ASPECT_BEAN_NAME);
        Assert.assertSame(executor, ((Supplier) (new DirectFieldAccessor(aspect).getPropertyValue("defaultExecutor"))).get());
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void asyncPostProcessorExceptionHandlerReference() {
        Object exceptionHandler = context.getBean("testExceptionHandler");
        Object aspect = context.getBean(ASYNC_EXECUTION_ASPECT_BEAN_NAME);
        Assert.assertSame(exceptionHandler, ((Supplier) (new DirectFieldAccessor(aspect).getPropertyValue("exceptionHandler"))).get());
    }
}

