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


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


/**
 * Tests for {@link TaskSchedulingAutoConfiguration}.
 *
 * @author Stephane Nicoll
 */
public class TaskSchedulingAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(TaskSchedulingAutoConfigurationTests.TestConfiguration.class).withConfiguration(AutoConfigurations.of(TaskSchedulingAutoConfiguration.class));

    @Test
    public void noSchedulingDoesNotExposeTaskScheduler() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void enableSchedulingWithNoTaskExecutorAutoConfiguresOne() {
        this.contextRunner.withPropertyValues("spring.task.scheduling.shutdown.await-termination=true", "spring.task.scheduling.shutdown.await-termination-period=30s", "spring.task.scheduling.thread-name-prefix=scheduling-test-").withUserConfiguration(TaskSchedulingAutoConfigurationTests.SchedulingConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            TaskExecutor taskExecutor = context.getBean(.class);
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            Thread.sleep(15);
            assertThat(taskExecutor).hasFieldOrPropertyWithValue("waitForTasksToCompleteOnShutdown", true);
            assertThat(taskExecutor).hasFieldOrPropertyWithValue("awaitTerminationSeconds", 30);
            assertThat(bean.threadNames).allMatch(( name) -> name.contains("scheduling-test-"));
        });
    }

    @Test
    public void enableSchedulingWithNoTaskExecutorAppliesCustomizers() {
        this.contextRunner.withPropertyValues("spring.task.scheduling.thread-name-prefix=scheduling-test-").withUserConfiguration(TaskSchedulingAutoConfigurationTests.SchedulingConfiguration.class, TaskSchedulingAutoConfigurationTests.TaskSchedulerCustomizerConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            Thread.sleep(15);
            assertThat(bean.threadNames).allMatch(( name) -> name.contains("customized-scheduler-"));
        });
    }

    @Test
    public void enableSchedulingWithExistingTaskSchedulerBacksOff() {
        this.contextRunner.withUserConfiguration(TaskSchedulingAutoConfigurationTests.SchedulingConfiguration.class, TaskSchedulingAutoConfigurationTests.TaskSchedulerConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            assertThat(context.getBean(.class)).isInstanceOf(.class);
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            Thread.sleep(15);
            assertThat(bean.threadNames).containsExactly("test-1");
        });
    }

    @Test
    public void enableSchedulingWithExistingScheduledExecutorServiceBacksOff() {
        this.contextRunner.withUserConfiguration(TaskSchedulingAutoConfigurationTests.SchedulingConfiguration.class, TaskSchedulingAutoConfigurationTests.ScheduledExecutorServiceConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).hasSingleBean(.class);
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            Thread.sleep(15);
            assertThat(bean.threadNames).allMatch(( name) -> name.contains("pool-"));
        });
    }

    @Test
    public void enableSchedulingWithConfigurerBacksOff() {
        this.contextRunner.withUserConfiguration(TaskSchedulingAutoConfigurationTests.SchedulingConfiguration.class, TaskSchedulingAutoConfigurationTests.SchedulingConfigurerConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            org.springframework.boot.autoconfigure.task.TestBean bean = context.getBean(.class);
            Thread.sleep(15);
            assertThat(bean.threadNames).containsExactly("test-1");
        });
    }

    @Configuration
    @EnableScheduling
    static class SchedulingConfiguration {}

    @Configuration
    static class TaskSchedulerConfiguration {
        @Bean
        public TaskScheduler customTaskScheduler() {
            return new TaskSchedulingAutoConfigurationTests.TestTaskScheduler();
        }
    }

    @Configuration
    static class ScheduledExecutorServiceConfiguration {
        @Bean
        public ScheduledExecutorService customScheduledExecutorService() {
            return Executors.newScheduledThreadPool(2);
        }
    }

    @Configuration
    static class TaskSchedulerCustomizerConfiguration {
        @Bean
        public TaskSchedulerCustomizer testTaskSchedulerCustomizer() {
            return ( taskScheduler) -> taskScheduler.setThreadNamePrefix("customized-scheduler-");
        }
    }

    @Configuration
    static class SchedulingConfigurerConfiguration implements SchedulingConfigurer {
        private final TaskScheduler taskScheduler = new TaskSchedulingAutoConfigurationTests.TestTaskScheduler();

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setScheduler(this.taskScheduler);
        }
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public TaskSchedulingAutoConfigurationTests.TestBean testBean() {
            return new TaskSchedulingAutoConfigurationTests.TestBean();
        }
    }

    static class TestBean {
        private final Set<String> threadNames = ConcurrentHashMap.newKeySet();

        @Scheduled(fixedRate = 10)
        public void accumulate() {
            this.threadNames.add(Thread.currentThread().getName());
        }
    }

    static class TestTaskScheduler extends ThreadPoolTaskScheduler {
        TestTaskScheduler() {
            setPoolSize(1);
            setThreadNamePrefix("test-");
            afterPropertiesSet();
        }
    }
}

