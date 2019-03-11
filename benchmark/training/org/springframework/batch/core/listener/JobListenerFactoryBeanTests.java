/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.batch.core.listener;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.xml.AbstractTestComponent;
import org.springframework.core.Ordered;


/**
 *
 *
 * @author Lucas Ward
 */
public class JobListenerFactoryBeanTests {
    JobListenerFactoryBean factoryBean;

    @Test
    public void testWithInterface() throws Exception {
        JobListenerFactoryBeanTests.JobListenerWithInterface delegate = new JobListenerFactoryBeanTests.JobListenerWithInterface();
        factoryBean.setDelegate(delegate);
        JobExecutionListener listener = ((JobExecutionListener) (factoryBean.getObject()));
        JobExecution jobExecution = new JobExecution(11L);
        listener.beforeJob(jobExecution);
        listener.afterJob(jobExecution);
        Assert.assertTrue(delegate.beforeJobCalled);
        Assert.assertTrue(delegate.afterJobCalled);
    }

    @Test
    public void testWithAnnotations() throws Exception {
        JobListenerFactoryBeanTests.AnnotatedTestClass delegate = new JobListenerFactoryBeanTests.AnnotatedTestClass();
        factoryBean.setDelegate(delegate);
        JobExecutionListener listener = ((JobExecutionListener) (factoryBean.getObject()));
        JobExecution jobExecution = new JobExecution(11L);
        listener.beforeJob(jobExecution);
        listener.afterJob(jobExecution);
        Assert.assertTrue(delegate.beforeJobCalled);
        Assert.assertTrue(delegate.afterJobCalled);
    }

    @Test
    public void testFactoryMethod() throws Exception {
        JobListenerFactoryBeanTests.JobListenerWithInterface delegate = new JobListenerFactoryBeanTests.JobListenerWithInterface();
        Object listener = JobListenerFactoryBean.getListener(delegate);
        Assert.assertTrue((listener instanceof JobExecutionListener));
        ((JobExecutionListener) (listener)).afterJob(new JobExecution(11L));
        Assert.assertTrue(delegate.afterJobCalled);
    }

    @Test
    public void testVanillaInterfaceWithProxy() throws Exception {
        JobListenerFactoryBeanTests.JobListenerWithInterface delegate = new JobListenerFactoryBeanTests.JobListenerWithInterface();
        ProxyFactory factory = new ProxyFactory(delegate);
        factoryBean.setDelegate(factory.getProxy());
        Object listener = factoryBean.getObject();
        Assert.assertTrue((listener instanceof JobExecutionListener));
    }

    @Test
    public void testUseInHashSet() throws Exception {
        JobListenerFactoryBeanTests.JobListenerWithInterface delegate = new JobListenerFactoryBeanTests.JobListenerWithInterface();
        Object listener = JobListenerFactoryBean.getListener(delegate);
        Object other = JobListenerFactoryBean.getListener(delegate);
        Assert.assertTrue((listener instanceof JobExecutionListener));
        Set<JobExecutionListener> listeners = new HashSet<>();
        listeners.add(((JobExecutionListener) (listener)));
        listeners.add(((JobExecutionListener) (other)));
        Assert.assertTrue(listeners.contains(listener));
        Assert.assertEquals(1, listeners.size());
    }

    @Test
    public void testAnnotationsIsListener() throws Exception {
        Assert.assertTrue(JobListenerFactoryBean.isListener(new Object() {
            @BeforeJob
            public void foo(JobExecution execution) {
            }
        }));
    }

    @Test
    public void testInterfaceIsListener() throws Exception {
        Assert.assertTrue(JobListenerFactoryBean.isListener(new JobListenerFactoryBeanTests.JobListenerWithInterface()));
    }

    @Test
    public void testAnnotationsWithOrdered() throws Exception {
        Object delegate = new Ordered() {
            @BeforeJob
            public void foo(JobExecution execution) {
            }

            @Override
            public int getOrder() {
                return 3;
            }
        };
        JobExecutionListener listener = JobListenerFactoryBean.getListener(delegate);
        Assert.assertTrue("Listener is not of correct type", (listener instanceof Ordered));
        Assert.assertEquals(3, getOrder());
    }

    @Test
    public void testEqualityOfProxies() throws Exception {
        JobListenerFactoryBeanTests.JobListenerWithInterface delegate = new JobListenerFactoryBeanTests.JobListenerWithInterface();
        Object listener1 = JobListenerFactoryBean.getListener(delegate);
        Object listener2 = JobListenerFactoryBean.getListener(delegate);
        Assert.assertEquals(listener1, listener2);
    }

    @Test
    public void testEmptySignatureAnnotation() {
        AbstractTestComponent delegate = new AbstractTestComponent() {
            @AfterJob
            public void aMethod() {
                executed = true;
            }
        };
        factoryBean.setDelegate(delegate);
        JobExecutionListener listener = ((JobExecutionListener) (factoryBean.getObject()));
        listener.afterJob(new JobExecution(1L));
        Assert.assertTrue(delegate.isExecuted());
    }

    @Test
    public void testRightSignatureAnnotation() {
        AbstractTestComponent delegate = new AbstractTestComponent() {
            @AfterJob
            public void aMethod(JobExecution jobExecution) {
                executed = true;
                Assert.assertEquals(new Long(25), jobExecution.getId());
            }
        };
        factoryBean.setDelegate(delegate);
        JobExecutionListener listener = ((JobExecutionListener) (factoryBean.getObject()));
        listener.afterJob(new JobExecution(25L));
        Assert.assertTrue(delegate.isExecuted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongSignatureAnnotation() {
        AbstractTestComponent delegate = new AbstractTestComponent() {
            @AfterJob
            public void aMethod(Integer item) {
                executed = true;
            }
        };
        factoryBean.setDelegate(delegate);
        factoryBean.getObject();
    }

    @Test
    public void testEmptySignatureNamedMethod() {
        AbstractTestComponent delegate = new AbstractTestComponent() {
            @SuppressWarnings("unused")
            public void aMethod() {
                executed = true;
            }
        };
        factoryBean.setDelegate(delegate);
        Map<String, String> metaDataMap = new HashMap<>();
        metaDataMap.put(JobListenerMetaData.AFTER_JOB.getPropertyName(), "aMethod");
        factoryBean.setMetaDataMap(metaDataMap);
        JobExecutionListener listener = ((JobExecutionListener) (factoryBean.getObject()));
        listener.afterJob(new JobExecution(1L));
        Assert.assertTrue(delegate.isExecuted());
    }

    @Test
    public void testRightSignatureNamedMethod() {
        AbstractTestComponent delegate = new AbstractTestComponent() {
            @SuppressWarnings("unused")
            public void aMethod(JobExecution jobExecution) {
                executed = true;
                Assert.assertEquals(new Long(25), jobExecution.getId());
            }
        };
        factoryBean.setDelegate(delegate);
        Map<String, String> metaDataMap = new HashMap<>();
        metaDataMap.put(JobListenerMetaData.AFTER_JOB.getPropertyName(), "aMethod");
        factoryBean.setMetaDataMap(metaDataMap);
        JobExecutionListener listener = ((JobExecutionListener) (factoryBean.getObject()));
        listener.afterJob(new JobExecution(25L));
        Assert.assertTrue(delegate.isExecuted());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongSignatureNamedMethod() {
        AbstractTestComponent delegate = new AbstractTestComponent() {
            @SuppressWarnings("unused")
            public void aMethod(Integer item) {
                executed = true;
            }
        };
        factoryBean.setDelegate(delegate);
        Map<String, String> metaDataMap = new HashMap<>();
        metaDataMap.put(JobListenerMetaData.AFTER_JOB.getPropertyName(), "aMethod");
        factoryBean.setMetaDataMap(metaDataMap);
        factoryBean.getObject();
    }

    private class JobListenerWithInterface implements JobExecutionListener {
        boolean beforeJobCalled = false;

        boolean afterJobCalled = false;

        @Override
        public void afterJob(JobExecution jobExecution) {
            afterJobCalled = true;
        }

        @Override
        public void beforeJob(JobExecution jobExecution) {
            beforeJobCalled = true;
        }
    }

    private class AnnotatedTestClass {
        boolean beforeJobCalled = false;

        boolean afterJobCalled = false;

        @BeforeJob
        public void before() {
            beforeJobCalled = true;
        }

        @AfterJob
        public void after() {
            afterJobCalled = true;
        }
    }
}

