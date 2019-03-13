/**
 * Copyright 2014 the original author or authors.
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
package org.springframework.batch.core.jsr;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.batch.runtime.context.StepContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.jsr.configuration.support.BatchPropertyContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


public class JsrStepContextFactoryBeanTests {
    private JsrStepContextFactoryBean factory;

    @Mock
    private BatchPropertyContext propertyContext;

    @Test(expected = FactoryBeanNotInitializedException.class)
    public void testNoStepExecutionRegistered() throws Exception {
        factory.getObject();
    }

    @Test
    public void getObjectSingleThread() throws Exception {
        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution("step1", new JobExecution(5L), 3L));
        StepContext context1 = factory.getObject();
        StepContext context2 = factory.getObject();
        Assert.assertTrue((context1 == context2));
        Assert.assertEquals(3L, context1.getStepExecutionId());
        StepSynchronizationManager.close();
        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution("step2", new JobExecution(5L), 2L));
        StepContext context3 = factory.getObject();
        StepContext context4 = factory.getObject();
        Assert.assertTrue((context3 == context4));
        Assert.assertTrue((context3 != context2));
        Assert.assertEquals(2L, context3.getStepExecutionId());
        StepSynchronizationManager.close();
    }

    @Test
    public void getObjectSingleThreadWithProperties() throws Exception {
        Properties props = new Properties();
        props.put("key1", "value1");
        Mockito.when(propertyContext.getStepProperties("step3")).thenReturn(props);
        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution("step3", new JobExecution(5L), 3L));
        StepContext context1 = factory.getObject();
        StepContext context2 = factory.getObject();
        Assert.assertTrue((context1 == context2));
        Assert.assertEquals(3L, context1.getStepExecutionId());
        Assert.assertEquals("value1", context1.getProperties().get("key1"));
        StepSynchronizationManager.close();
    }

    @Test
    public void getObjectMultiThread() throws Exception {
        List<Future<StepContext>> stepContexts = new ArrayList<>();
        AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        for (int i = 0; i < 4; i++) {
            final long count = i;
            stepContexts.add(executor.submit(new Callable<StepContext>() {
                @Override
                public StepContext call() throws Exception {
                    try {
                        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution(("step" + count), new JobExecution(count)));
                        StepContext context = factory.getObject();
                        Thread.sleep(1000L);
                        return context;
                    } catch (Throwable ignore) {
                        return null;
                    } finally {
                        StepSynchronizationManager.close();
                    }
                }
            }));
        }
        Set<StepContext> contexts = new HashSet<>();
        for (Future<StepContext> future : stepContexts) {
            contexts.add(future.get());
        }
        Assert.assertEquals(4, contexts.size());
    }

    @Test
    public void getObjectMultiThreadWithProperties() throws Exception {
        for (int i = 0; i < 4; i++) {
            Properties props = new Properties();
            props.put(("step" + i), (("step" + i) + "value"));
            Mockito.when(propertyContext.getStepProperties(("step" + i))).thenReturn(props);
        }
        List<Future<StepContext>> stepContexts = new ArrayList<>();
        AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        for (int i = 0; i < 4; i++) {
            final long count = i;
            stepContexts.add(executor.submit(new Callable<StepContext>() {
                @Override
                public StepContext call() throws Exception {
                    try {
                        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution(("step" + count), new JobExecution(count)));
                        StepContext context = factory.getObject();
                        Thread.sleep(1000L);
                        return context;
                    } catch (Throwable ignore) {
                        return null;
                    } finally {
                        StepSynchronizationManager.close();
                    }
                }
            }));
        }
        Set<StepContext> contexts = new HashSet<>();
        for (Future<StepContext> future : stepContexts) {
            contexts.add(future.get());
        }
        Assert.assertEquals(4, contexts.size());
        for (StepContext stepContext : contexts) {
            Assert.assertEquals(((stepContext.getStepName()) + "value"), stepContext.getProperties().get(stepContext.getStepName()));
        }
    }
}

