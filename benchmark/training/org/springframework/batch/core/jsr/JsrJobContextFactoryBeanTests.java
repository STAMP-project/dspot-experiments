/**
 * Copyright 2013-2018 the original author or authors.
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
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.batch.runtime.context.JobContext;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.jsr.configuration.support.BatchPropertyContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;


public class JsrJobContextFactoryBeanTests {
    private JsrJobContextFactoryBean factoryBean;

    private BatchPropertyContext propertyContext;

    @Test
    public void testInitialCreationSingleThread() throws Exception {
        factoryBean.setJobExecution(new JobExecution(5L));
        factoryBean.setBatchPropertyContext(propertyContext);
        Assert.assertTrue(factoryBean.getObjectType().isAssignableFrom(JobContext.class));
        Assert.assertFalse(factoryBean.isSingleton());
        JobContext jobContext1 = factoryBean.getObject();
        JobContext jobContext2 = factoryBean.getObject();
        Assert.assertEquals(5L, jobContext1.getExecutionId());
        Assert.assertEquals(5L, jobContext2.getExecutionId());
        Assert.assertTrue((jobContext1 == jobContext2));
    }

    @Test
    public void testInitialCreationSingleThreadUsingStepScope() throws Exception {
        factoryBean.setBatchPropertyContext(propertyContext);
        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution("step1", new JobExecution(5L)));
        JobContext jobContext = factoryBean.getObject();
        Assert.assertEquals(5L, jobContext.getExecutionId());
        StepSynchronizationManager.close();
    }

    @Test(expected = FactoryBeanNotInitializedException.class)
    public void testNoJobExecutionProvided() throws Exception {
        factoryBean.getObject();
    }

    @Test
    public void testOneJobContextPerThread() throws Exception {
        List<Future<JobContext>> jobContexts = new ArrayList<>();
        AsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        for (int i = 0; i < 4; i++) {
            final long count = i;
            jobContexts.add(executor.submit(new Callable<JobContext>() {
                @Override
                public JobContext call() throws Exception {
                    try {
                        StepSynchronizationManager.register(new org.springframework.batch.core.StepExecution(("step" + count), new JobExecution(count)));
                        JobContext context = factoryBean.getObject();
                        Thread.sleep(1000L);
                        return context;
                    } catch (Throwable ignore) {
                        return null;
                    } finally {
                        StepSynchronizationManager.release();
                    }
                }
            }));
        }
        Set<JobContext> contexts = new HashSet<>();
        for (Future<JobContext> future : jobContexts) {
            contexts.add(future.get());
        }
        Assert.assertEquals(4, contexts.size());
    }
}

