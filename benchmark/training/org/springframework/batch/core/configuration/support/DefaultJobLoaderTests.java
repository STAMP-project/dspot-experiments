/**
 * Copyright 2006-2010 the original author or authors.
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
package org.springframework.batch.core.configuration.support;


import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.StepRegistry;
import org.springframework.batch.core.step.NoSuchStepException;
import org.springframework.batch.core.step.StepLocator;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;


/**
 *
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 */
public class DefaultJobLoaderTests {
    /**
     * The name of the job as defined in the test context used in this test.
     */
    private static final String TEST_JOB_NAME = "test-job";

    /**
     * The name of the step as defined in the test context used in this test.
     */
    private static final String TEST_STEP_NAME = "test-step";

    private JobRegistry jobRegistry = new MapJobRegistry();

    private StepRegistry stepRegistry = new MapStepRegistry();

    private DefaultJobLoader jobLoader = new DefaultJobLoader(jobRegistry, stepRegistry);

    @Test
    public void testClear() throws Exception {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ByteArrayResource(DefaultJobLoaderTests.JOB_XML.getBytes()));
        jobLoader.load(factory);
        Assert.assertEquals(1, ((Map) (ReflectionTestUtils.getField(jobLoader, "contexts"))).size());
        Assert.assertEquals(1, ((Map) (ReflectionTestUtils.getField(jobLoader, "contextToJobNames"))).size());
        jobLoader.clear();
        Assert.assertEquals(0, ((Map) (ReflectionTestUtils.getField(jobLoader, "contexts"))).size());
        Assert.assertEquals(0, ((Map) (ReflectionTestUtils.getField(jobLoader, "contextToJobNames"))).size());
    }

    @Test
    public void testLoadWithExplicitName() throws Exception {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ByteArrayResource(DefaultJobLoaderTests.JOB_XML.getBytes()));
        jobLoader.load(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
        jobLoader.reload(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
    }

    @Test
    public void createWithBothRegistries() {
        final DefaultJobLoader loader = new DefaultJobLoader();
        loader.setJobRegistry(jobRegistry);
        loader.setStepRegistry(stepRegistry);
        loader.afterPropertiesSet();
    }

    @Test
    public void createWithOnlyJobRegistry() {
        final DefaultJobLoader loader = new DefaultJobLoader();
        loader.setJobRegistry(jobRegistry);
        loader.afterPropertiesSet();
    }

    @Test
    public void testRegistryUpdated() throws DuplicateJobException {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ClassPathResource("trivial-context.xml", getClass()));
        jobLoader.load(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
        assertStepExist(DefaultJobLoaderTests.TEST_JOB_NAME, DefaultJobLoaderTests.TEST_STEP_NAME);
    }

    @Test
    public void testMultipleJobsInTheSameContext() throws DuplicateJobException {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ClassPathResource("job-context-with-steps.xml", getClass()));
        jobLoader.load(factory);
        Assert.assertEquals(2, jobRegistry.getJobNames().size());
        assertStepExist("job1", "step11", "step12");
        assertStepDoNotExist("job1", "step21", "step22");
        assertStepExist("job2", "step21", "step22");
        assertStepDoNotExist("job2", "step11", "step12");
    }

    @Test
    public void testMultipleJobsInTheSameContextWithSeparateSteps() throws DuplicateJobException {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ClassPathResource("job-context-with-separate-steps.xml", getClass()));
        jobLoader.load(factory);
        Assert.assertEquals(2, jobRegistry.getJobNames().size());
        assertStepExist("job1", "step11", "step12", "genericStep1", "genericStep2");
        assertStepDoNotExist("job1", "step21", "step22");
        assertStepExist("job2", "step21", "step22", "genericStep1", "genericStep2");
        assertStepDoNotExist("job2", "step11", "step12");
    }

    @Test
    public void testNoStepRegistryAvailable() throws DuplicateJobException {
        final JobLoader loader = new DefaultJobLoader(jobRegistry);
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ClassPathResource("job-context-with-steps.xml", getClass()));
        loader.load(factory);
        // No step registry available so just registering the jobs
        Assert.assertEquals(2, jobRegistry.getJobNames().size());
    }

    @Test
    public void testLoadWithJobThatIsNotAStepLocator() throws DuplicateJobException {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ByteArrayResource(DefaultJobLoaderTests.BASIC_JOB_XML.getBytes()));
        try {
            jobLoader.load(factory);
            Assert.fail(((("Should have failed with a [" + (UnsupportedOperationException.class.getName())) + "] as job does not") + "implement StepLocator."));
        } catch (UnsupportedOperationException e) {
            // Job is not a step locator, can't register steps
        }
    }

    @Test
    public void testLoadWithJobThatIsNotAStepLocatorNoStepRegistry() throws DuplicateJobException {
        final JobLoader loader = new DefaultJobLoader(jobRegistry);
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ByteArrayResource(DefaultJobLoaderTests.BASIC_JOB_XML.getBytes()));
        try {
            loader.load(factory);
        } catch (UnsupportedOperationException e) {
            Assert.fail(((("Should not have failed with a [" + (UnsupportedOperationException.class.getName())) + "] as ") + "stepRegistry is not available for this JobLoader instance."));
        }
    }

    @Test
    public void testReload() throws Exception {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ClassPathResource("trivial-context.xml", getClass()));
        jobLoader.load(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
        assertStepExist(DefaultJobLoaderTests.TEST_JOB_NAME, DefaultJobLoaderTests.TEST_STEP_NAME);
        jobLoader.reload(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
        assertStepExist(DefaultJobLoaderTests.TEST_JOB_NAME, DefaultJobLoaderTests.TEST_STEP_NAME);
    }

    @Test
    public void testReloadWithAutoRegister() throws Exception {
        GenericApplicationContextFactory factory = new GenericApplicationContextFactory(new ClassPathResource("trivial-context-autoregister.xml", getClass()));
        jobLoader.load(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
        assertStepExist(DefaultJobLoaderTests.TEST_JOB_NAME, DefaultJobLoaderTests.TEST_STEP_NAME);
        jobLoader.reload(factory);
        Assert.assertEquals(1, jobRegistry.getJobNames().size());
        assertStepExist(DefaultJobLoaderTests.TEST_JOB_NAME, DefaultJobLoaderTests.TEST_STEP_NAME);
    }

    private static final String BASIC_JOB_XML = String.format(("<beans xmlns='http://www.springframework.org/schema/beans' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " + "xsi:schemaLocation='http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd'><bean class='%s$BasicStubJob'/></beans>"), DefaultJobLoaderTests.class.getName());

    private static final String JOB_XML = String.format(("<beans xmlns='http://www.springframework.org/schema/beans' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " + "xsi:schemaLocation='http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd'><bean class='%s$StubJob'/></beans>"), DefaultJobLoaderTests.class.getName());

    public static class BasicStubJob implements Job {
        @Override
        public void execute(JobExecution execution) {
        }

        @Override
        public JobParametersIncrementer getJobParametersIncrementer() {
            return null;
        }

        @Override
        public String getName() {
            return "job";
        }

        @Override
        public boolean isRestartable() {
            return false;
        }

        @Override
        public JobParametersValidator getJobParametersValidator() {
            return null;
        }
    }

    public static class StubJob extends DefaultJobLoaderTests.BasicStubJob implements StepLocator {
        @Override
        public Collection<String> getStepNames() {
            return Collections.emptyList();
        }

        @Override
        public Step getStep(String stepName) throws NoSuchStepException {
            throw new NoSuchStepException((("Step [" + stepName) + "] does not exist"));
        }
    }
}

