/**
 * Copyright 2002-2008 the original author or authors.
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
package org.springframework.batch.core.configuration.xml;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.step.item.SimpleChunkProcessor;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.CannotSerializeTransactionException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.retry.RetryListener;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Dan Garrette
 * @author Dave Syer
 * @since 2.0
 */
public class ChunkElementParserTests {
    @Test
    @SuppressWarnings("resource")
    public void testSimpleAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementSimpleAttributeParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue("Wrong processor type", (chunkProcessor instanceof SimpleChunkProcessor));
    }

    @Test
    @SuppressWarnings("resource")
    public void testCommitIntervalLateBinding() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementLateBindingParserTests-context.xml");
        Step step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
    }

    @Test
    @SuppressWarnings("resource")
    public void testSkipAndRetryAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementSkipAndRetryAttributeParserTests-context.xml");
        Step step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
    }

    @Test
    @SuppressWarnings("resource")
    public void testIllegalSkipAndRetryAttributes() throws Exception {
        try {
            ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementIllegalSkipAndRetryAttributeParserTests-context.xml");
            Step step = context.getBean("s1", Step.class);
            Assert.assertNotNull("Step not parsed", step);
            Assert.fail("Expected BeanCreationException");
        } catch (BeanCreationException e) {
            // expected
        }
    }

    @Test
    public void testRetryPolicyAttribute() throws Exception {
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementRetryPolicyParserTests-context.xml");
        Map<Class<? extends Throwable>, Boolean> retryable = getNestedExceptionMap("s1", context, "tasklet.chunkProcessor.batchRetryTemplate.regular.retryPolicy.exceptionClassifier", "exceptionClassifier");
        Assert.assertEquals(2, retryable.size());
        Assert.assertTrue(retryable.containsKey(NullPointerException.class));
        Assert.assertTrue(retryable.containsKey(ArithmeticException.class));
    }

    @Test
    public void testRetryPolicyElement() throws Exception {
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementRetryPolicyParserTests-context.xml");
        SimpleRetryPolicy policy = ((SimpleRetryPolicy) (getPolicy("s2", context, "tasklet.chunkProcessor.batchRetryTemplate.regular.retryPolicy.exceptionClassifier")));
        Assert.assertEquals(2, policy.getMaxAttempts());
    }

    @Test
    public void testSkipPolicyAttribute() throws Exception {
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementSkipPolicyParserTests-context.xml");
        SkipPolicy policy = getSkipPolicy("s1", context);
        Assert.assertTrue(policy.shouldSkip(new NullPointerException(), 0));
        Assert.assertTrue(policy.shouldSkip(new ArithmeticException(), 0));
    }

    @Test
    public void testSkipPolicyElement() throws Exception {
        @SuppressWarnings("resource")
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementSkipPolicyParserTests-context.xml");
        SkipPolicy policy = getSkipPolicy("s2", context);
        Assert.assertFalse(policy.shouldSkip(new NullPointerException(), 0));
        Assert.assertTrue(policy.shouldSkip(new ArithmeticException(), 0));
    }

    @Test
    @SuppressWarnings("resource")
    public void testProcessorTransactionalAttributes() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementTransactionalAttributeParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Boolean processorTransactional = ((Boolean) (ReflectionTestUtils.getField(chunkProcessor, "processorTransactional")));
        Assert.assertFalse("Flag not set", processorTransactional);
    }

    @Test
    @SuppressWarnings("resource")
    public void testProcessorTransactionalNotAllowedOnSimpleProcessor() throws Exception {
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementIllegalAttributeParserTests-context.xml");
        Object step = context.getBean("s1", Step.class);
        Assert.assertNotNull("Step not parsed", step);
        Object tasklet = ReflectionTestUtils.getField(step, "tasklet");
        Object chunkProcessor = ReflectionTestUtils.getField(tasklet, "chunkProcessor");
        Assert.assertTrue((chunkProcessor instanceof SimpleChunkProcessor<?, ?>));
    }

    @Test
    public void testProcessorNonTransactionalNotAllowedWithTransactionalReader() throws Exception {
        try {
            new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/ChunkElementIllegalTransactionalAttributeParserTests-context.xml");
            Assert.fail("Expected BeanCreationException");
        } catch (BeanCreationException e) {
            String msg = e.getMessage();
            Assert.assertTrue(("Wrong message: " + msg), msg.contains("The field 'processor-transactional' cannot be false if 'reader-transactional"));
        }
    }

    @Test
    public void testRetryable() throws Exception {
        Map<Class<? extends Throwable>, Boolean> retryable = getRetryableExceptionClasses("s1", getContext());
        System.err.println(retryable);
        Assert.assertEquals(3, retryable.size());
        containsClassified(retryable, PessimisticLockingFailureException.class, true);
        containsClassified(retryable, CannotSerializeTransactionException.class, false);
    }

    @Test
    public void testRetryableInherited() throws Exception {
        Map<Class<? extends Throwable>, Boolean> retryable = getRetryableExceptionClasses("s3", getContext());
        System.err.println(retryable);
        Assert.assertEquals(2, retryable.size());
        containsClassified(retryable, IOException.class, true);
    }

    @Test
    public void testRetryableInheritedMerge() throws Exception {
        Map<Class<? extends Throwable>, Boolean> retryable = getRetryableExceptionClasses("s4", getContext());
        System.err.println(retryable);
        Assert.assertEquals(3, retryable.size());
        containsClassified(retryable, IOException.class, true);
    }

    @Test
    public void testInheritSkippable() throws Exception {
        Map<Class<? extends Throwable>, Boolean> skippable = getSkippableExceptionClasses("s1", getContext());
        System.err.println(skippable);
        Assert.assertEquals(5, skippable.size());
        containsClassified(skippable, NullPointerException.class, true);
        containsClassified(skippable, ArithmeticException.class, true);
        containsClassified(skippable, CannotAcquireLockException.class, false);
        containsClassified(skippable, DeadlockLoserDataAccessException.class, false);
    }

    @Test
    public void testInheritSkippableWithNoMerge() throws Exception {
        Map<Class<? extends Throwable>, Boolean> skippable = getSkippableExceptionClasses("s2", getContext());
        Assert.assertEquals(3, skippable.size());
        containsClassified(skippable, IllegalArgumentException.class, true);
        Assert.assertFalse(skippable.containsKey(ArithmeticException.class));
        containsClassified(skippable, ConcurrencyFailureException.class, false);
        Assert.assertFalse(skippable.containsKey(DeadlockLoserDataAccessException.class));
    }

    @Test
    public void testInheritStreams() throws Exception {
        Collection<ItemStream> streams = getStreams("s1", getContext());
        Assert.assertEquals(2, streams.size());
        boolean c = false;
        for (ItemStream o : streams) {
            if (o instanceof CompositeItemStream) {
                c = true;
            }
        }
        Assert.assertTrue(c);
    }

    @Test
    public void testInheritRetryListeners() throws Exception {
        Collection<RetryListener> retryListeners = getRetryListeners("s1", getContext());
        Assert.assertEquals(2, retryListeners.size());
        boolean g = false;
        boolean h = false;
        for (RetryListener o : retryListeners) {
            if (o instanceof RetryListenerSupport) {
                g = true;
            } else
                if (o instanceof DummyRetryListener) {
                    h = true;
                }

        }
        Assert.assertTrue(g);
        Assert.assertTrue(h);
    }

    @Test
    public void testInheritStreamsWithNoMerge() throws Exception {
        Collection<ItemStream> streams = getStreams("s2", getContext());
        Assert.assertEquals(1, streams.size());
        boolean c = false;
        for (ItemStream o : streams) {
            if (o instanceof CompositeItemStream) {
                c = true;
            }
        }
        Assert.assertTrue(c);
    }

    @Test
    public void testInheritRetryListenersWithNoMerge() throws Exception {
        Collection<RetryListener> retryListeners = getRetryListeners("s2", getContext());
        Assert.assertEquals(1, retryListeners.size());
        boolean h = false;
        for (RetryListener o : retryListeners) {
            if (o instanceof DummyRetryListener) {
                h = true;
            }
        }
        Assert.assertTrue(h);
    }
}

