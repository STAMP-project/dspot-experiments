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
package org.springframework.scheduling.config;


import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CustomizableThreadCreator;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 */
public class ExecutorBeanDefinitionParserTests {
    private ApplicationContext context;

    @Test
    public void defaultExecutor() throws Exception {
        ThreadPoolTaskExecutor executor = this.context.getBean("default", ThreadPoolTaskExecutor.class);
        Assert.assertEquals(1, getCorePoolSize(executor));
        Assert.assertEquals(Integer.MAX_VALUE, getMaxPoolSize(executor));
        Assert.assertEquals(Integer.MAX_VALUE, getQueueCapacity(executor));
        Assert.assertEquals(60, getKeepAliveSeconds(executor));
        Assert.assertEquals(false, getAllowCoreThreadTimeOut(executor));
        FutureTask<String> task = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "foo";
            }
        });
        executor.execute(task);
        Assert.assertEquals("foo", task.get());
    }

    @Test
    public void singleSize() {
        Object executor = this.context.getBean("singleSize");
        Assert.assertEquals(42, getCorePoolSize(executor));
        Assert.assertEquals(42, getMaxPoolSize(executor));
    }

    @Test(expected = BeanCreationException.class)
    public void invalidPoolSize() {
        this.context.getBean("invalidPoolSize");
    }

    @Test
    public void rangeWithBoundedQueue() {
        Object executor = this.context.getBean("rangeWithBoundedQueue");
        Assert.assertEquals(7, getCorePoolSize(executor));
        Assert.assertEquals(42, getMaxPoolSize(executor));
        Assert.assertEquals(11, getQueueCapacity(executor));
    }

    @Test
    public void rangeWithUnboundedQueue() {
        Object executor = this.context.getBean("rangeWithUnboundedQueue");
        Assert.assertEquals(9, getCorePoolSize(executor));
        Assert.assertEquals(9, getMaxPoolSize(executor));
        Assert.assertEquals(37, getKeepAliveSeconds(executor));
        Assert.assertEquals(true, getAllowCoreThreadTimeOut(executor));
        Assert.assertEquals(Integer.MAX_VALUE, getQueueCapacity(executor));
    }

    @Test
    public void propertyPlaceholderWithSingleSize() {
        Object executor = this.context.getBean("propertyPlaceholderWithSingleSize");
        Assert.assertEquals(123, getCorePoolSize(executor));
        Assert.assertEquals(123, getMaxPoolSize(executor));
        Assert.assertEquals(60, getKeepAliveSeconds(executor));
        Assert.assertEquals(false, getAllowCoreThreadTimeOut(executor));
        Assert.assertEquals(Integer.MAX_VALUE, getQueueCapacity(executor));
    }

    @Test
    public void propertyPlaceholderWithRange() {
        Object executor = this.context.getBean("propertyPlaceholderWithRange");
        Assert.assertEquals(5, getCorePoolSize(executor));
        Assert.assertEquals(25, getMaxPoolSize(executor));
        Assert.assertEquals(false, getAllowCoreThreadTimeOut(executor));
        Assert.assertEquals(10, getQueueCapacity(executor));
    }

    @Test
    public void propertyPlaceholderWithRangeAndCoreThreadTimeout() {
        Object executor = this.context.getBean("propertyPlaceholderWithRangeAndCoreThreadTimeout");
        Assert.assertEquals(99, getCorePoolSize(executor));
        Assert.assertEquals(99, getMaxPoolSize(executor));
        Assert.assertEquals(true, getAllowCoreThreadTimeOut(executor));
    }

    @Test(expected = BeanCreationException.class)
    public void propertyPlaceholderWithInvalidPoolSize() {
        this.context.getBean("propertyPlaceholderWithInvalidPoolSize");
    }

    @Test
    public void threadNamePrefix() {
        CustomizableThreadCreator executor = this.context.getBean("default", CustomizableThreadCreator.class);
        Assert.assertEquals("default-", executor.getThreadNamePrefix());
    }

    @Test
    public void typeCheck() {
        Assert.assertTrue(this.context.isTypeMatch("default", Executor.class));
        Assert.assertTrue(this.context.isTypeMatch("default", TaskExecutor.class));
        Assert.assertTrue(this.context.isTypeMatch("default", ThreadPoolTaskExecutor.class));
    }
}

