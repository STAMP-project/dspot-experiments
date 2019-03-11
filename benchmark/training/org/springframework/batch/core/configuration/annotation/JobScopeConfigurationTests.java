/**
 * Copyright 2006-2018 the original author or authors.
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
package org.springframework.batch.core.configuration.annotation;


import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.JobSynchronizationManager;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 * @author Mahmoud Ben Hassine
 */
public class JobScopeConfigurationTests {
    private ConfigurableApplicationContext context;

    private JobExecution jobExecution;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testXmlJobScopeWithProxyTargetClass() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/annotation/JobScopeConfigurationTestsProxyTargetClass-context.xml");
        JobSynchronizationManager.register(jobExecution);
        JobScopeConfigurationTests.SimpleHolder value = context.getBean(JobScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testXmlJobScopeWithInterface() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/annotation/JobScopeConfigurationTestsInterface-context.xml");
        JobSynchronizationManager.register(jobExecution);
        @SuppressWarnings("unchecked")
        Callable<String> value = context.getBean(Callable.class);
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testXmlJobScopeWithInheritance() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/annotation/JobScopeConfigurationTestsInheritance-context.xml");
        JobSynchronizationManager.register(jobExecution);
        JobScopeConfigurationTests.SimpleHolder value = ((JobScopeConfigurationTests.SimpleHolder) (context.getBean("child")));
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testJobScopeWithProxyTargetClass() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationRequiringProxyTargetClass.class);
        JobScopeConfigurationTests.SimpleHolder value = context.getBean(JobScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testStepScopeXmlImportUsingNamespace() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationXmlImportUsingNamespace.class);
        JobScopeConfigurationTests.SimpleHolder value = ((JobScopeConfigurationTests.SimpleHolder) (context.getBean("xmlValue")));
        Assert.assertEquals("JOB", value.call());
        value = ((JobScopeConfigurationTests.SimpleHolder) (context.getBean("javaValue")));
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testJobScopeWithProxyTargetClassInjected() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationInjectingProxy.class);
        JobScopeConfigurationTests.SimpleHolder value = context.getBean(JobScopeConfigurationTests.Wrapper.class).getValue();
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testIntentionallyBlowUpOnMissingContextWithProxyTargetClass() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationRequiringProxyTargetClass.class);
        JobSynchronizationManager.release();
        expected.expect(BeanCreationException.class);
        expected.expectMessage("job scope");
        JobScopeConfigurationTests.SimpleHolder value = context.getBean(JobScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testIntentionallyBlowupWithForcedInterface() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationForcingInterfaceProxy.class);
        JobSynchronizationManager.release();
        expected.expect(BeanCreationException.class);
        expected.expectMessage("job scope");
        JobScopeConfigurationTests.SimpleHolder value = context.getBean(JobScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testJobScopeWithDefaults() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationWithDefaults.class);
        @SuppressWarnings("unchecked")
        Callable<String> value = context.getBean(Callable.class);
        Assert.assertEquals("JOB", value.call());
    }

    @Test
    public void testIntentionallyBlowUpOnMissingContextWithInterface() throws Exception {
        init(JobScopeConfigurationTests.JobScopeConfigurationWithDefaults.class);
        JobSynchronizationManager.release();
        expected.expect(BeanCreationException.class);
        expected.expectMessage("job scope");
        @SuppressWarnings("unchecked")
        Callable<String> value = context.getBean(Callable.class);
        Assert.assertEquals("JOB", value.call());
    }

    public static class SimpleCallable implements Callable<String> {
        private final String value;

        private SimpleCallable(String value) {
            this.value = value;
        }

        @Override
        public String call() throws Exception {
            return value;
        }
    }

    public static class SimpleHolder {
        private final String value;

        protected SimpleHolder() {
            value = "<WRONG>";
        }

        public SimpleHolder(String value) {
            this.value = value;
        }

        public String call() throws Exception {
            return value;
        }
    }

    public static class Wrapper {
        private JobScopeConfigurationTests.SimpleHolder value;

        public Wrapper(JobScopeConfigurationTests.SimpleHolder value) {
            this.value = value;
        }

        public JobScopeConfigurationTests.SimpleHolder getValue() {
            return value;
        }
    }

    public static class TaskletSupport implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            return RepeatStatus.FINISHED;
        }
    }

    @Configuration
    @ImportResource("org/springframework/batch/core/configuration/annotation/JobScopeConfigurationTestsXmlImportUsingNamespace-context.xml")
    @EnableBatchProcessing
    public static class JobScopeConfigurationXmlImportUsingNamespace {
        @Bean
        @JobScope
        protected JobScopeConfigurationTests.SimpleHolder javaValue(@Value("#{jobName}")
        final String value) {
            return new JobScopeConfigurationTests.SimpleHolder(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class JobScopeConfigurationInjectingProxy {
        @Bean
        public JobScopeConfigurationTests.Wrapper wrapper(JobScopeConfigurationTests.SimpleHolder value) {
            return new JobScopeConfigurationTests.Wrapper(value);
        }

        @Bean
        @Scope(value = "job", proxyMode = ScopedProxyMode.TARGET_CLASS)
        protected JobScopeConfigurationTests.SimpleHolder value(@Value("#{jobName}")
        final String value) {
            return new JobScopeConfigurationTests.SimpleHolder(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class JobScopeConfigurationRequiringProxyTargetClass {
        @Bean
        @Scope(value = "job", proxyMode = ScopedProxyMode.TARGET_CLASS)
        protected JobScopeConfigurationTests.SimpleHolder value(@Value("#{jobName}")
        final String value) {
            return new JobScopeConfigurationTests.SimpleHolder(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class JobScopeConfigurationWithDefaults {
        @Bean
        @JobScope
        protected Callable<String> value(@Value("#{jobName}")
        final String value) {
            return new JobScopeConfigurationTests.SimpleCallable(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class JobScopeConfigurationForcingInterfaceProxy {
        @Bean
        @Scope(value = "job", proxyMode = ScopedProxyMode.INTERFACES)
        protected JobScopeConfigurationTests.SimpleHolder value(@Value("#{jobName}")
        final String value) {
            return new JobScopeConfigurationTests.SimpleHolder(value);
        }
    }
}

