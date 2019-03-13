/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.task;


import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.task.TaskExecutorBuilder;
import org.springframework.boot.task.TaskExecutorCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link TaskExecutionAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Camille Vienot
 */
public class TaskExecutionAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(TaskExecutionAutoConfiguration.class));

    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void taskExecutorBuilderShouldApplyCustomSettings() {
        this.contextRunner.withPropertyValues("spring.task.execution.pool.queue-capacity=10", "spring.task.execution.pool.core-size=2", "spring.task.execution.pool.max-size=4", "spring.task.execution.pool.allow-core-thread-timeout=true", "spring.task.execution.pool.keep-alive=5s", "spring.task.execution.shutdown.await-termination=true", "spring.task.execution.shutdown.await-termination-period=30s", "spring.task.execution.thread-name-prefix=mytest-").run(assertTaskExecutor(( taskExecutor) -> {
            assertThat(taskExecutor).hasFieldOrPropertyWithValue("queueCapacity", 10);
            assertThat(taskExecutor.getCorePoolSize()).isEqualTo(2);
            assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(4);
            assertThat(taskExecutor).hasFieldOrPropertyWithValue("allowCoreThreadTimeOut", true);
            assertThat(taskExecutor.getKeepAliveSeconds()).isEqualTo(5);
            assertThat(taskExecutor).hasFieldOrPropertyWithValue("waitForTasksToCompleteOnShutdown", true);
            assertThat(taskExecutor).hasFieldOrPropertyWithValue("awaitTerminationSeconds", 30);
            assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("mytest-");
        }));
    }

    @Test
    public void taskExecutorBuilderWhenHasCustomBuilderShouldUseCustomBuilder() {
        this.contextRunner.withUserConfiguration(TaskExecutionAutoConfigurationTests.CustomTaskExecutorBuilderConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).isSameAs(context.getBean(.class).taskExecutorBuilder);
        });
    }

    @Test
    public void taskExecutorBuilderShouldUseTaskDecorator() {
        this.contextRunner.withUserConfiguration(TaskExecutionAutoConfigurationTests.TaskDecoratorConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            ThreadPoolTaskExecutor executor = context.getBean(.class).build();
            assertThat(ReflectionTestUtils.getField(executor, "taskDecorator")).isSameAs(context.getBean(.class));
        });
    }

    @Test
    public void taskExecutorAutoConfigured() {
        this.contextRunner.run(( context) -> {
            assertThat(this.output.toString()).doesNotContain("Initializing ExecutorService");
            assertThat(context).hasSingleBean(.class);
            assertThat(context).hasBean("applicationTaskExecutor");
            assertThat(context).getBean("applicationTaskExecutor").isInstanceOf(.class);
            assertThat(this.output.toString()).contains("Initializing ExecutorService");
        });
    }

    @Test
    public void taskExecutorWhenHasCustomTaskExecutorShouldBackOff() {
        this.contextRunner.withUserConfiguration(TaskExecutionAutoConfigurationTests.CustomTaskExecutorConfig.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).isSameAs(context.getBean("customTaskExecutor"));
        });
    }

    @Test
    public void taskExecutorBuilderShouldApplyCustomizer() {
        this.contextRunner.withUserConfiguration(TaskExecutionAutoConfigurationTests.TaskExecutorCustomizerConfig.class).run(( context) -> {
            TaskExecutorCustomizer customizer = context.getBean(.class);
            ThreadPoolTaskExecutor executor = context.getBean(.class).build();
            verify(customizer).customize(executor);
        });
    }

    @Test
    public void enableAsyncUsesAutoConfiguredOneByDefault() {
        this.contextRunner.withPropertyValues("spring.task.execution.thread-name-prefix=task-test-").withUserConfiguration(TaskExecutionAutoConfigurationTests.AsyncConfiguration.class, TaskExecutionAutoConfigurationTests.TestBean.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            String text = bean.echo("something").get();
            assertThat(text).contains("task-test-").contains("something");
        });
    }

    @Test
    public void enableAsyncUsesAutoConfiguredOneByDefaultEvenThoughSchedulingIsConfigured() {
        this.contextRunner.withPropertyValues("spring.task.execution.thread-name-prefix=task-test-").withConfiguration(AutoConfigurations.of(TaskSchedulingAutoConfiguration.class)).withUserConfiguration(TaskExecutionAutoConfigurationTests.AsyncConfiguration.class, TaskExecutionAutoConfigurationTests.SchedulingConfiguration.class, TaskExecutionAutoConfigurationTests.TestBean.class).run(( context) -> {
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            String text = bean.echo("something").get();
            assertThat(text).contains("task-test-").contains("something");
        });
    }

    @Configuration
    static class CustomTaskExecutorBuilderConfig {
        private final TaskExecutorBuilder taskExecutorBuilder = new TaskExecutorBuilder();

        @Bean
        public TaskExecutorBuilder customTaskExecutorBuilder() {
            return this.taskExecutorBuilder;
        }
    }

    @Configuration
    static class TaskExecutorCustomizerConfig {
        @Bean
        public TaskExecutorCustomizer mockTaskExecutorCustomizer() {
            return Mockito.mock(TaskExecutorCustomizer.class);
        }
    }

    @Configuration
    static class TaskDecoratorConfig {
        @Bean
        public TaskDecorator mockTaskDecorator() {
            return Mockito.mock(TaskDecorator.class);
        }
    }

    @Configuration
    static class CustomTaskExecutorConfig {
        @Bean
        public Executor customTaskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Configuration
    @EnableAsync
    static class AsyncConfiguration {}

    @Configuration
    @EnableScheduling
    static class SchedulingConfiguration {}

    static class TestBean {
        @Async
        public Future<String> echo(String text) {
            return new org.springframework.scheduling.annotation.AsyncResult((((Thread.currentThread().getName()) + " ") + text));
        }
    }
}

