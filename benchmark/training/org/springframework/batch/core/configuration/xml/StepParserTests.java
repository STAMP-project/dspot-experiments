/**
 * Copyright 2006-2009 the original author or authors.
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


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.CompositeStepExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.support.SimpleJobRepository;
import org.springframework.batch.core.step.item.FatalRuntimeException;
import org.springframework.batch.core.step.item.FatalSkippableException;
import org.springframework.batch.core.step.item.ForceRollbackForWriteSkipException;
import org.springframework.batch.core.step.item.SkippableException;
import org.springframework.batch.core.step.item.SkippableRuntimeException;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.support.CompositeItemStream;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.retry.RetryListener;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Thomas Risberg
 * @author Dan Garrette
 */
public class StepParserTests {
    private static ApplicationContext stepParserParentAttributeTestsCtx;

    @Test
    @SuppressWarnings("resource")
    public void testTaskletStepAttributes() throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserTaskletAttributesTests-context.xml");
        @SuppressWarnings({ "rawtypes" })
        Map<String, StepParserStepFactoryBean> beans = ctx.getBeansOfType(StepParserStepFactoryBean.class);
        String factoryName = ((String) (beans.keySet().toArray()[0]));
        @SuppressWarnings("unchecked")
        StepParserStepFactoryBean<Object, Object> factory = beans.get(factoryName);
        TaskletStep bean = ((TaskletStep) (factory.getObject()));
        Assert.assertEquals("wrong start-limit:", 25, bean.getStartLimit());
        Object throttleLimit = ReflectionTestUtils.getField(factory, "throttleLimit");
        Assert.assertEquals(new Integer(10), throttleLimit);
    }

    @Test
    @SuppressWarnings("resource")
    public void testStepParserBeanName() throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserBeanNameTests-context.xml");
        Map<String, Step> beans = ctx.getBeansOfType(Step.class);
        Assert.assertTrue("'s1' bean not found", beans.containsKey("s1"));
        Step s1 = ((Step) (ctx.getBean("s1")));
        Assert.assertEquals("wrong name", "s1", s1.getName());
    }

    @Test(expected = BeanDefinitionParsingException.class)
    @SuppressWarnings("resource")
    public void testStepParserCommitIntervalCompletionPolicy() throws Exception {
        new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserCommitIntervalCompletionPolicyTests-context.xml");
    }

    @Test
    @SuppressWarnings("resource")
    public void testStepParserCommitInterval() throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserCommitIntervalTests-context.xml");
        Map<String, Step> beans = ctx.getBeansOfType(Step.class);
        Assert.assertTrue("'s1' bean not found", beans.containsKey("s1"));
        Step s1 = ((Step) (ctx.getBean("s1")));
        CompletionPolicy completionPolicy = getCompletionPolicy(s1);
        Assert.assertTrue((completionPolicy instanceof SimpleCompletionPolicy));
        Assert.assertEquals(25, ReflectionTestUtils.getField(completionPolicy, "chunkSize"));
    }

    @Test
    @SuppressWarnings("resource")
    public void testStepParserCompletionPolicy() throws Exception {
        ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserCompletionPolicyTests-context.xml");
        Map<String, Step> beans = ctx.getBeansOfType(Step.class);
        Assert.assertTrue("'s1' bean not found", beans.containsKey("s1"));
        Step s1 = ((Step) (ctx.getBean("s1")));
        CompletionPolicy completionPolicy = getCompletionPolicy(s1);
        Assert.assertTrue((completionPolicy instanceof DummyCompletionPolicy));
    }

    @Test(expected = BeanDefinitionParsingException.class)
    @SuppressWarnings("resource")
    public void testStepParserNoCommitIntervalOrCompletionPolicy() throws Exception {
        new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserNoCommitIntervalOrCompletionPolicyTests-context.xml");
    }

    @Test(expected = BeanDefinitionParsingException.class)
    @SuppressWarnings("resource")
    public void testTaskletStepWithBadStepListener() throws Exception {
        new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserBadStepListenerTests-context.xml");
    }

    @Test(expected = BeanDefinitionParsingException.class)
    @SuppressWarnings("resource")
    public void testTaskletStepWithBadRetryListener() throws Exception {
        new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/xml/StepParserBadRetryListenerTests-context.xml");
    }

    @Test
    public void testParentStep() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        // Inline Step
        Assert.assertTrue(((getListener("s1", ctx)) instanceof StepExecutionListenerSupport));
        // Standalone Step
        Assert.assertTrue(((getListener("s2", ctx)) instanceof StepExecutionListenerSupport));
        // Inline With Tasklet Attribute Step
        Assert.assertTrue(((getListener("s3", ctx)) instanceof StepExecutionListenerSupport));
        // Standalone With Tasklet Attribute Step
        Assert.assertTrue(((getListener("s4", ctx)) instanceof StepExecutionListenerSupport));
    }

    @Test
    public void testInheritTransactionAttributes() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        // On Inline - No Merge
        validateTransactionAttributesInherited("s1", ctx);
        // On Standalone - No Merge
        validateTransactionAttributesInherited("s2", ctx);
        // On Inline With Tasklet Ref - No Merge
        validateTransactionAttributesInherited("s3", ctx);
        // On Standalone With Tasklet Ref - No Merge
        validateTransactionAttributesInherited("s4", ctx);
        // On Inline
        validateTransactionAttributesInherited("s5", ctx);
        // On Standalone
        validateTransactionAttributesInherited("s6", ctx);
        // On Inline With Tasklet Ref
        validateTransactionAttributesInherited("s7", ctx);
        // On Standalone With Tasklet Ref
        validateTransactionAttributesInherited("s8", ctx);
    }

    @Test
    public void testInheritFromBean() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(((getTasklet("s9", ctx)) instanceof DummyTasklet));
        Assert.assertTrue(((getTasklet("s10", ctx)) instanceof DummyTasklet));
    }

    @Test
    public void testJobRepositoryDefaults() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(((getJobRepository("defaultRepoStep", ctx)) instanceof SimpleJobRepository));
        Assert.assertTrue(((getJobRepository("defaultRepoStepWithParent", ctx)) instanceof SimpleJobRepository));
        Assert.assertTrue(((getJobRepository("overrideRepoStep", ctx)) instanceof SimpleJobRepository));
        assertDummyJobRepository("injectedRepoStep", "dummyJobRepository", ctx);
        assertDummyJobRepository("injectedRepoStepWithParent", "dummyJobRepository", ctx);
        assertDummyJobRepository("injectedOverrideRepoStep", "dummyJobRepository", ctx);
        assertDummyJobRepository("injectedRepoFromParentStep", "dummyJobRepository2", ctx);
        assertDummyJobRepository("injectedRepoFromParentStepWithParent", "dummyJobRepository2", ctx);
        assertDummyJobRepository("injectedOverrideRepoFromParentStep", "dummyJobRepository2", ctx);
        Assert.assertTrue(((getJobRepository("defaultRepoStandaloneStep", ctx)) instanceof SimpleJobRepository));
        assertDummyJobRepository("specifiedRepoStandaloneStep", "dummyJobRepository2", ctx);
    }

    @Test
    public void testTransactionManagerDefaults() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(((getTransactionManager("defaultTxMgrStep", ctx)) instanceof ResourcelessTransactionManager));
        assertDummyTransactionManager("specifiedTxMgrStep", "dummyTxMgr", ctx);
        assertDummyTransactionManager("defaultTxMgrWithParentStep", "dummyTxMgr", ctx);
        assertDummyTransactionManager("overrideTxMgrOnParentStep", "dummyTxMgr2", ctx);
    }

    @Test
    public void testNonAbstractStep() {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(ctx.containsBean("s11"));
        Object bean = ctx.getBean("s11");
        Assert.assertTrue((bean instanceof DummyStep));
    }

    @Test
    public void testInlineTaskletElementOverridesParentBeanClass() {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(ctx.containsBean("&s12"));
        Object factoryBean = ctx.getBean("&s12");
        Assert.assertTrue((factoryBean instanceof StepParserStepFactoryBean<?, ?>));
        Assert.assertTrue(ctx.containsBean("dummyStep"));
        Object dummyStep = ctx.getBean("dummyStep");
        Assert.assertTrue((dummyStep instanceof DummyStep));
        Assert.assertTrue(ctx.containsBean("s12"));
        Object bean = ctx.getBean("s12");
        Assert.assertTrue((bean instanceof TaskletStep));
    }

    @Test
    public void testTaskletElementOverridesChildBeanClass() {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(ctx.containsBean("&s13"));
        Object factoryBean = ctx.getBean("&s13");
        Assert.assertTrue((factoryBean instanceof StepParserStepFactoryBean<?, ?>));
        Assert.assertTrue(ctx.containsBean("s13"));
        Object bean = ctx.getBean("s13");
        Assert.assertTrue((bean instanceof TaskletStep));
        Assert.assertTrue(ctx.containsBean("&dummyStepWithTaskletOnParent"));
        Object dummyStepFb = ctx.getBean("&dummyStepWithTaskletOnParent");
        Assert.assertTrue((dummyStepFb instanceof StepParserStepFactoryBean<?, ?>));
        Assert.assertTrue(ctx.containsBean("dummyStepWithTaskletOnParent"));
        Object dummyStep = ctx.getBean("dummyStepWithTaskletOnParent");
        Assert.assertTrue((dummyStep instanceof TaskletStep));
        Assert.assertTrue(ctx.containsBean("&standaloneStepWithTasklet"));
        Object standaloneStepFb = ctx.getBean("&standaloneStepWithTasklet");
        Assert.assertTrue((standaloneStepFb instanceof StepParserStepFactoryBean<?, ?>));
        Assert.assertTrue(ctx.containsBean("standaloneStepWithTasklet"));
        Object standaloneStep = ctx.getBean("standaloneStepWithTasklet");
        Assert.assertTrue((standaloneStep instanceof TaskletStep));
    }

    @Test
    public void testTaskletElementOverridesParentBeanClass() {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Assert.assertTrue(ctx.containsBean("&s14"));
        Object factoryBean = ctx.getBean("&s14");
        Assert.assertTrue((factoryBean instanceof StepParserStepFactoryBean<?, ?>));
        Assert.assertTrue(ctx.containsBean("s12"));
        Object bean = ctx.getBean("s12");
        Assert.assertTrue((bean instanceof TaskletStep));
        Assert.assertTrue(ctx.containsBean("&standaloneStepWithTaskletAndDummyParent"));
        Object standaloneWithTaskletFb = ctx.getBean("&standaloneStepWithTaskletAndDummyParent");
        Assert.assertTrue((standaloneWithTaskletFb instanceof StepParserStepFactoryBean<?, ?>));
        Assert.assertTrue(ctx.containsBean("standaloneStepWithTaskletAndDummyParent"));
        Object standaloneWithTasklet = ctx.getBean("standaloneStepWithTaskletAndDummyParent");
        Assert.assertTrue((standaloneWithTasklet instanceof TaskletStep));
        Assert.assertTrue(ctx.containsBean("dummyStep"));
        Object dummyStep = ctx.getBean("dummyStep");
        Assert.assertTrue((dummyStep instanceof DummyStep));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStepWithListsMerge() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Map<Class<? extends Throwable>, Boolean> skippable = new HashMap<>();
        skippable.put(SkippableRuntimeException.class, true);
        skippable.put(SkippableException.class, true);
        skippable.put(FatalRuntimeException.class, false);
        skippable.put(FatalSkippableException.class, false);
        skippable.put(ForceRollbackForWriteSkipException.class, true);
        Map<Class<? extends Throwable>, Boolean> retryable = new HashMap<>();
        retryable.put(DeadlockLoserDataAccessException.class, true);
        retryable.put(FatalSkippableException.class, true);
        retryable.put(ForceRollbackForWriteSkipException.class, true);
        List<Class<? extends ItemStream>> streams = Arrays.asList(CompositeItemStream.class, TestReader.class);
        List<Class<? extends RetryListener>> retryListeners = Arrays.asList(RetryListenerSupport.class, DummyRetryListener.class);
        List<Class<? extends StepExecutionListener>> stepListeners = Arrays.asList(StepExecutionListenerSupport.class, CompositeStepExecutionListener.class);
        List<Class<? extends SkippableRuntimeException>> noRollback = Arrays.asList(FatalRuntimeException.class, SkippableRuntimeException.class);
        StepParserStepFactoryBean<?, ?> fb = ((StepParserStepFactoryBean<?, ?>) (ctx.getBean("&stepWithListsMerge")));
        Map<Class<? extends Throwable>, Boolean> skippableFound = getExceptionMap(fb, "skippableExceptionClasses");
        Map<Class<? extends Throwable>, Boolean> retryableFound = getExceptionMap(fb, "retryableExceptionClasses");
        ItemStream[] streamsFound = ((ItemStream[]) (ReflectionTestUtils.getField(fb, "streams")));
        RetryListener[] retryListenersFound = ((RetryListener[]) (ReflectionTestUtils.getField(fb, "retryListeners")));
        Set<StepExecutionListener> stepListenersFound = ((Set<StepExecutionListener>) (ReflectionTestUtils.getField(fb, "stepExecutionListeners")));
        Collection<Class<? extends Throwable>> noRollbackFound = getExceptionList(fb, "noRollbackExceptionClasses");
        assertSameMaps(skippable, skippableFound);
        assertSameMaps(retryable, retryableFound);
        assertSameCollections(streams, toClassCollection(streamsFound));
        assertSameCollections(retryListeners, toClassCollection(retryListenersFound));
        assertSameCollections(stepListeners, toClassCollection(stepListenersFound));
        assertSameCollections(noRollback, noRollbackFound);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStepWithListsNoMerge() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        Map<Class<? extends Throwable>, Boolean> skippable = new HashMap<>();
        skippable.put(SkippableException.class, true);
        skippable.put(FatalSkippableException.class, false);
        skippable.put(ForceRollbackForWriteSkipException.class, true);
        Map<Class<? extends Throwable>, Boolean> retryable = new HashMap<>();
        retryable.put(FatalSkippableException.class, true);
        retryable.put(ForceRollbackForWriteSkipException.class, true);
        List<Class<CompositeItemStream>> streams = Arrays.asList(CompositeItemStream.class);
        List<Class<DummyRetryListener>> retryListeners = Arrays.asList(DummyRetryListener.class);
        List<Class<CompositeStepExecutionListener>> stepListeners = Arrays.asList(CompositeStepExecutionListener.class);
        List<Class<SkippableRuntimeException>> noRollback = Arrays.asList(SkippableRuntimeException.class);
        StepParserStepFactoryBean<?, ?> fb = ((StepParserStepFactoryBean<?, ?>) (ctx.getBean("&stepWithListsNoMerge")));
        Map<Class<? extends Throwable>, Boolean> skippableFound = getExceptionMap(fb, "skippableExceptionClasses");
        Map<Class<? extends Throwable>, Boolean> retryableFound = getExceptionMap(fb, "retryableExceptionClasses");
        ItemStream[] streamsFound = ((ItemStream[]) (ReflectionTestUtils.getField(fb, "streams")));
        RetryListener[] retryListenersFound = ((RetryListener[]) (ReflectionTestUtils.getField(fb, "retryListeners")));
        Set<StepExecutionListener> stepListenersFound = ((Set<StepExecutionListener>) (ReflectionTestUtils.getField(fb, "stepExecutionListeners")));
        Collection<Class<? extends Throwable>> noRollbackFound = getExceptionList(fb, "noRollbackExceptionClasses");
        assertSameMaps(skippable, skippableFound);
        assertSameMaps(retryable, retryableFound);
        assertSameCollections(streams, toClassCollection(streamsFound));
        assertSameCollections(retryListeners, toClassCollection(retryListenersFound));
        assertSameCollections(stepListeners, toClassCollection(stepListenersFound));
        assertSameCollections(noRollback, noRollbackFound);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testStepWithListsOverrideWithEmpty() throws Exception {
        ApplicationContext ctx = StepParserTests.stepParserParentAttributeTestsCtx;
        StepParserStepFactoryBean<?, ?> fb = ((StepParserStepFactoryBean<?, ?>) (ctx.getBean("&stepWithListsOverrideWithEmpty")));
        Assert.assertEquals(1, getExceptionMap(fb, "skippableExceptionClasses").size());
        Assert.assertEquals(1, getExceptionMap(fb, "retryableExceptionClasses").size());
        Assert.assertEquals(0, ((ItemStream[]) (ReflectionTestUtils.getField(fb, "streams"))).length);
        Assert.assertEquals(0, ((RetryListener[]) (ReflectionTestUtils.getField(fb, "retryListeners"))).length);
        Assert.assertEquals(0, ((Set<StepExecutionListener>) (ReflectionTestUtils.getField(fb, "stepExecutionListeners"))).size());
        Assert.assertEquals(0, getExceptionList(fb, "noRollbackExceptionClasses").size());
    }
}

