/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.scheduling.annotation;


import TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME;
import TestGroup.PERFORMANCE;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskHolder;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.tests.Assume;


/**
 * Tests use of @EnableScheduling on @Configuration classes.
 *
 * @author Chris Beams
 * @author Sam Brannen
 * @since 3.1
 */
public class EnableSchedulingTests {
    private AnnotationConfigApplicationContext ctx;

    @Test
    public void withFixedRateTask() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.FixedRateTaskConfig.class);
        Assert.assertEquals(2, ctx.getBean(ScheduledTaskHolder.class).getScheduledTasks().size());
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(AtomicInteger.class).get(), greaterThanOrEqualTo(10));
    }

    @Test
    public void withSubclass() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.FixedRateTaskConfigSubclass.class);
        Assert.assertEquals(2, ctx.getBean(ScheduledTaskHolder.class).getScheduledTasks().size());
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(AtomicInteger.class).get(), greaterThanOrEqualTo(10));
    }

    @Test
    public void withExplicitScheduler() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.ExplicitSchedulerConfig.class);
        Assert.assertEquals(1, ctx.getBean(ScheduledTaskHolder.class).getScheduledTasks().size());
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(AtomicInteger.class).get(), greaterThanOrEqualTo(10));
        Assert.assertThat(ctx.getBean(EnableSchedulingTests.ExplicitSchedulerConfig.class).threadName, startsWith("explicitScheduler-"));
        Assert.assertTrue(Arrays.asList(ctx.getDefaultListableBeanFactory().getDependentBeans("myTaskScheduler")).contains(SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME));
    }

    @Test
    public void withExplicitSchedulerAmbiguity_andSchedulingEnabled() {
        // No exception raised as of 4.3, aligned with the behavior for @Async methods (SPR-14030)
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.AmbiguousExplicitSchedulerConfig.class);
    }

    @Test
    public void withExplicitScheduledTaskRegistrar() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.ExplicitScheduledTaskRegistrarConfig.class);
        Assert.assertEquals(1, ctx.getBean(ScheduledTaskHolder.class).getScheduledTasks().size());
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(AtomicInteger.class).get(), greaterThanOrEqualTo(10));
        Assert.assertThat(ctx.getBean(EnableSchedulingTests.ExplicitScheduledTaskRegistrarConfig.class).threadName, startsWith("explicitScheduler1"));
    }

    @Test
    public void withAmbiguousTaskSchedulers_butNoActualTasks() {
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.SchedulingEnabled_withAmbiguousTaskSchedulers_butNoActualTasks.class);
    }

    @Test
    public void withAmbiguousTaskSchedulers_andSingleTask() {
        // No exception raised as of 4.3, aligned with the behavior for @Async methods (SPR-14030)
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.SchedulingEnabled_withAmbiguousTaskSchedulers_andSingleTask.class);
    }

    @Test
    public void withAmbiguousTaskSchedulers_andSingleTask_disambiguatedByScheduledTaskRegistrarBean() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.SchedulingEnabled_withAmbiguousTaskSchedulers_andSingleTask_disambiguatedByScheduledTaskRegistrar.class);
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(EnableSchedulingTests.ThreadAwareWorker.class).executedByThread, startsWith("explicitScheduler2-"));
    }

    @Test
    public void withAmbiguousTaskSchedulers_andSingleTask_disambiguatedBySchedulerNameAttribute() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.SchedulingEnabled_withAmbiguousTaskSchedulers_andSingleTask_disambiguatedBySchedulerNameAttribute.class);
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(EnableSchedulingTests.ThreadAwareWorker.class).executedByThread, startsWith("explicitScheduler2-"));
    }

    @Test
    public void withTaskAddedVia_configureTasks() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.SchedulingEnabled_withTaskAddedVia_configureTasks.class);
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(EnableSchedulingTests.ThreadAwareWorker.class).executedByThread, startsWith("taskScheduler-"));
    }

    @Test
    public void withTriggerTask() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.TriggerTaskConfig.class);
        Thread.sleep(100);
        Assert.assertThat(ctx.getBean(AtomicInteger.class).get(), greaterThan(1));
    }

    @Test
    public void withInitiallyDelayedFixedRateTask() throws InterruptedException {
        Assume.group(PERFORMANCE);
        ctx = new AnnotationConfigApplicationContext(EnableSchedulingTests.FixedRateTaskConfig_withInitialDelay.class);
        Thread.sleep(1950);
        AtomicInteger counter = ctx.getBean(AtomicInteger.class);
        // The @Scheduled method should have been called at least once but
        // not more times than the delay allows.
        Assert.assertThat(counter.get(), both(greaterThan(0)).and(lessThanOrEqualTo(10)));
    }

    @Configuration
    @EnableScheduling
    static class FixedRateTaskConfig implements SchedulingConfigurer {
        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.addFixedRateTask(() -> {
            }, 100);
        }

        @Bean
        public AtomicInteger counter() {
            return new AtomicInteger();
        }

        @Scheduled(fixedRate = 10)
        public void task() {
            counter().incrementAndGet();
        }
    }

    @Configuration
    static class FixedRateTaskConfigSubclass extends EnableSchedulingTests.FixedRateTaskConfig {}

    @Configuration
    @EnableScheduling
    static class ExplicitSchedulerConfig {
        String threadName;

        @Bean
        public TaskScheduler myTaskScheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler-");
            return scheduler;
        }

        @Bean
        public AtomicInteger counter() {
            return new AtomicInteger();
        }

        @Scheduled(fixedRate = 10)
        public void task() {
            threadName = Thread.currentThread().getName();
            counter().incrementAndGet();
        }
    }

    @Configuration
    @EnableScheduling
    static class AmbiguousExplicitSchedulerConfig {
        @Bean
        public TaskScheduler taskScheduler1() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler1");
            return scheduler;
        }

        @Bean
        public TaskScheduler taskScheduler2() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler2");
            return scheduler;
        }

        @Scheduled(fixedRate = 10)
        public void task() {
        }
    }

    @Configuration
    @EnableScheduling
    static class ExplicitScheduledTaskRegistrarConfig implements SchedulingConfigurer {
        String threadName;

        @Bean
        public TaskScheduler taskScheduler1() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler1");
            return scheduler;
        }

        @Bean
        public TaskScheduler taskScheduler2() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler2");
            return scheduler;
        }

        @Bean
        public AtomicInteger counter() {
            return new AtomicInteger();
        }

        @Scheduled(fixedRate = 10)
        public void task() {
            threadName = Thread.currentThread().getName();
            counter().incrementAndGet();
        }

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setScheduler(taskScheduler1());
        }
    }

    @Configuration
    @EnableScheduling
    static class SchedulingEnabled_withAmbiguousTaskSchedulers_butNoActualTasks {
        @Bean
        public TaskScheduler taskScheduler1() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler1");
            return scheduler;
        }

        @Bean
        public TaskScheduler taskScheduler2() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler2");
            return scheduler;
        }
    }

    @Configuration
    @EnableScheduling
    static class SchedulingEnabled_withAmbiguousTaskSchedulers_andSingleTask {
        @Scheduled(fixedRate = 10L)
        public void task() {
        }

        @Bean
        public TaskScheduler taskScheduler1() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler1");
            return scheduler;
        }

        @Bean
        public TaskScheduler taskScheduler2() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler2");
            return scheduler;
        }
    }

    static class ThreadAwareWorker {
        String executedByThread;
    }

    @Configuration
    @EnableScheduling
    static class SchedulingEnabled_withAmbiguousTaskSchedulers_andSingleTask_disambiguatedByScheduledTaskRegistrar implements SchedulingConfigurer {
        @Scheduled(fixedRate = 10)
        public void task() {
            worker().executedByThread = Thread.currentThread().getName();
        }

        @Bean
        public EnableSchedulingTests.ThreadAwareWorker worker() {
            return new EnableSchedulingTests.ThreadAwareWorker();
        }

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setScheduler(taskScheduler2());
        }

        @Bean
        public TaskScheduler taskScheduler1() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler1-");
            return scheduler;
        }

        @Bean
        public TaskScheduler taskScheduler2() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler2-");
            return scheduler;
        }
    }

    @Configuration
    @EnableScheduling
    static class SchedulingEnabled_withAmbiguousTaskSchedulers_andSingleTask_disambiguatedBySchedulerNameAttribute implements SchedulingConfigurer {
        @Scheduled(fixedRate = 10)
        public void task() {
            worker().executedByThread = Thread.currentThread().getName();
        }

        @Bean
        public EnableSchedulingTests.ThreadAwareWorker worker() {
            return new EnableSchedulingTests.ThreadAwareWorker();
        }

        @Bean
        public TaskScheduler taskScheduler1() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler1-");
            return scheduler;
        }

        @Bean
        public TaskScheduler taskScheduler2() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.setThreadNamePrefix("explicitScheduler2-");
            return scheduler;
        }

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setScheduler(taskScheduler2());
        }
    }

    @Configuration
    @EnableScheduling
    static class SchedulingEnabled_withTaskAddedVia_configureTasks implements SchedulingConfigurer {
        @Bean
        public EnableSchedulingTests.ThreadAwareWorker worker() {
            return new EnableSchedulingTests.ThreadAwareWorker();
        }

        @Bean
        public TaskScheduler taskScheduler() {
            return new ThreadPoolTaskScheduler();
        }

        @Override
        public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
            taskRegistrar.setScheduler(taskScheduler());
            taskRegistrar.addFixedRateTask(new IntervalTask(new Runnable() {
                @Override
                public void run() {
                    worker().executedByThread = Thread.currentThread().getName();
                }
            }, 10, 0));
        }
    }

    @Configuration
    static class TriggerTaskConfig {
        @Bean
        public AtomicInteger counter() {
            return new AtomicInteger();
        }

        @Bean
        public TaskScheduler scheduler() {
            ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
            scheduler.initialize();
            scheduler.schedule(() -> counter().incrementAndGet(), ( triggerContext) -> new Date(((new Date().getTime()) + 10)));
            return scheduler;
        }
    }

    @Configuration
    @EnableScheduling
    static class FixedRateTaskConfig_withInitialDelay {
        @Bean
        public AtomicInteger counter() {
            return new AtomicInteger();
        }

        @Scheduled(initialDelay = 1000, fixedRate = 100)
        public void task() {
            counter().incrementAndGet();
        }
    }
}

