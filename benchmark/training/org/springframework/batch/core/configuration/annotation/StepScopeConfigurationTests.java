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
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepSynchronizationManager;
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
public class StepScopeConfigurationTests {
    private ConfigurableApplicationContext context;

    private StepExecution stepExecution;

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testXmlStepScopeWithProxyTargetClass() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/annotation/StepScopeConfigurationTestsProxyTargetClass-context.xml");
        StepSynchronizationManager.register(stepExecution);
        StepScopeConfigurationTests.SimpleHolder value = context.getBean(StepScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testXmlStepScopeWithInterface() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/annotation/StepScopeConfigurationTestsInterface-context.xml");
        StepSynchronizationManager.register(stepExecution);
        @SuppressWarnings("unchecked")
        Callable<String> value = context.getBean(Callable.class);
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testXmlStepScopeWithInheritance() throws Exception {
        context = new ClassPathXmlApplicationContext("org/springframework/batch/core/configuration/annotation/StepScopeConfigurationTestsInheritance-context.xml");
        StepSynchronizationManager.register(stepExecution);
        StepScopeConfigurationTests.SimpleHolder value = ((StepScopeConfigurationTests.SimpleHolder) (context.getBean("child")));
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testStepScopeWithProxyTargetClass() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationRequiringProxyTargetClass.class);
        StepScopeConfigurationTests.SimpleHolder value = context.getBean(StepScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testStepScopeXmlImportUsingNamespace() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationXmlImportUsingNamespace.class);
        StepScopeConfigurationTests.SimpleHolder value = ((StepScopeConfigurationTests.SimpleHolder) (context.getBean("xmlValue")));
        Assert.assertEquals("STEP", value.call());
        value = ((StepScopeConfigurationTests.SimpleHolder) (context.getBean("javaValue")));
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testStepScopeWithProxyTargetClassInjected() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationInjectingProxy.class);
        StepScopeConfigurationTests.SimpleHolder value = context.getBean(StepScopeConfigurationTests.Wrapper.class).getValue();
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testIntentionallyBlowUpOnMissingContextWithProxyTargetClass() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationRequiringProxyTargetClass.class);
        StepSynchronizationManager.release();
        expected.expect(BeanCreationException.class);
        expected.expectMessage("step scope");
        StepScopeConfigurationTests.SimpleHolder value = context.getBean(StepScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testIntentionallyBlowupWithForcedInterface() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationForcingInterfaceProxy.class);
        StepSynchronizationManager.release();
        expected.expect(BeanCreationException.class);
        expected.expectMessage("step scope");
        StepScopeConfigurationTests.SimpleHolder value = context.getBean(StepScopeConfigurationTests.SimpleHolder.class);
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testStepScopeWithDefaults() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationWithDefaults.class);
        @SuppressWarnings("unchecked")
        Callable<String> value = context.getBean(Callable.class);
        Assert.assertEquals("STEP", value.call());
    }

    @Test
    public void testIntentionallyBlowUpOnMissingContextWithInterface() throws Exception {
        init(StepScopeConfigurationTests.StepScopeConfigurationWithDefaults.class);
        StepSynchronizationManager.release();
        expected.expect(BeanCreationException.class);
        expected.expectMessage("step scope");
        @SuppressWarnings("unchecked")
        Callable<String> value = context.getBean(Callable.class);
        Assert.assertEquals("STEP", value.call());
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
        private StepScopeConfigurationTests.SimpleHolder value;

        public Wrapper(StepScopeConfigurationTests.SimpleHolder value) {
            this.value = value;
        }

        public StepScopeConfigurationTests.SimpleHolder getValue() {
            return value;
        }
    }

    @Configuration
    @ImportResource("org/springframework/batch/core/configuration/annotation/StepScopeConfigurationTestsXmlImportUsingNamespace-context.xml")
    @EnableBatchProcessing
    public static class StepScopeConfigurationXmlImportUsingNamespace {
        @Bean
        @StepScope
        protected StepScopeConfigurationTests.SimpleHolder javaValue(@Value("#{stepExecution.stepName}")
        final String value) {
            return new StepScopeConfigurationTests.SimpleHolder(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class StepScopeConfigurationInjectingProxy {
        @Bean
        public StepScopeConfigurationTests.Wrapper wrapper(StepScopeConfigurationTests.SimpleHolder value) {
            return new StepScopeConfigurationTests.Wrapper(value);
        }

        @Bean
        @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
        protected StepScopeConfigurationTests.SimpleHolder value(@Value("#{stepExecution.stepName}")
        final String value) {
            return new StepScopeConfigurationTests.SimpleHolder(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class StepScopeConfigurationRequiringProxyTargetClass {
        @Bean
        @Scope(value = "step", proxyMode = ScopedProxyMode.TARGET_CLASS)
        protected StepScopeConfigurationTests.SimpleHolder value(@Value("#{stepExecution.stepName}")
        final String value) {
            return new StepScopeConfigurationTests.SimpleHolder(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class StepScopeConfigurationWithDefaults {
        @Bean
        @StepScope
        protected Callable<String> value(@Value("#{stepExecution.stepName}")
        final String value) {
            return new StepScopeConfigurationTests.SimpleCallable(value);
        }
    }

    @Configuration
    @EnableBatchProcessing
    public static class StepScopeConfigurationForcingInterfaceProxy {
        @Bean
        @Scope(value = "step", proxyMode = ScopedProxyMode.INTERFACES)
        protected StepScopeConfigurationTests.SimpleHolder value(@Value("#{stepExecution.stepName}")
        final String value) {
            return new StepScopeConfigurationTests.SimpleHolder(value);
        }
    }

    public static class TaskletSupport implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            return RepeatStatus.FINISHED;
        }
    }
}

