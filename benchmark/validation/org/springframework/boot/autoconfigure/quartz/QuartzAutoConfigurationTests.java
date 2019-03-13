/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.autoconfigure.quartz;


import java.util.concurrent.Executor;
import javax.sql.DataSource;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.quartz.Calendar;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.calendar.MonthlyCalendar;
import org.quartz.impl.calendar.WeeklyCalendar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.testsupport.rule.OutputCapture;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.Assert;


/**
 * Tests for {@link QuartzAutoConfiguration}.
 *
 * @author Vedran Pavic
 * @author Stephane Nicoll
 */
public class QuartzAutoConfigurationTests {
    @Rule
    public final OutputCapture output = new OutputCapture();

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withPropertyValues("spring.datasource.generate-unique-name=true").withConfiguration(AutoConfigurations.of(QuartzAutoConfiguration.class));

    @Test
    public void withNoDataSource() {
        this.contextRunner.run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(.class);
        });
    }

    @Test
    public void withDataSourceUseMemoryByDefault() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getMetaData().getJobStoreClass()).isAssignableFrom(.class);
        });
    }

    @Test
    public void withDataSource() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.QuartzJobsConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class)).withPropertyValues("spring.quartz.job-store-type=jdbc").run(assertDataSourceJobStore("dataSource"));
    }

    @Test
    public void withDataSourceNoTransactionManager() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.QuartzJobsConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withPropertyValues("spring.quartz.job-store-type=jdbc").run(assertDataSourceJobStore("dataSource"));
    }

    @Test
    public void dataSourceWithQuartzDataSourceQualifierUsedWhenMultiplePresent() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.QuartzJobsConfiguration.class, QuartzAutoConfigurationTests.MultipleDataSourceConfiguration.class).withPropertyValues("spring.quartz.job-store-type=jdbc").run(assertDataSourceJobStore("quartzDataSource"));
    }

    @Test
    public void withTaskExecutor() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.MockExecutorConfiguration.class).withPropertyValues("spring.quartz.properties.org.quartz.threadPool.threadCount=50").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getMetaData().getThreadPoolSize()).isEqualTo(50);
            Executor executor = context.getBean(.class);
            verifyZeroInteractions(executor);
        });
    }

    @Test
    public void withOverwriteExistingJobs() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.OverwriteTriggerConfiguration.class).withPropertyValues("spring.quartz.overwrite-existing-jobs=true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            Trigger fooTrigger = scheduler.getTrigger(TriggerKey.triggerKey("fooTrigger"));
            assertThat(fooTrigger).isNotNull();
            assertThat(((SimpleTrigger) (fooTrigger)).getRepeatInterval()).isEqualTo(30000);
        });
    }

    @Test
    public void withConfiguredJobAndTrigger() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.QuartzFullConfiguration.class).withPropertyValues("test-name=withConfiguredJobAndTrigger").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getJobDetail(JobKey.jobKey("fooJob"))).isNotNull();
            assertThat(scheduler.getTrigger(TriggerKey.triggerKey("fooTrigger"))).isNotNull();
            Thread.sleep(1000L);
            assertThat(this.output.toString()).contains("withConfiguredJobAndTrigger");
            assertThat(this.output.toString()).contains("jobDataValue");
        });
    }

    @Test
    public void withConfiguredCalendars() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.QuartzCalendarsConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getCalendar("weekly")).isNotNull();
            assertThat(scheduler.getCalendar("monthly")).isNotNull();
        });
    }

    @Test
    public void withQuartzProperties() {
        this.contextRunner.withPropertyValues("spring.quartz.properties.org.quartz.scheduler.instanceId=FOO").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getSchedulerInstanceId()).isEqualTo("FOO");
        });
    }

    @Test
    public void withCustomizer() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.QuartzCustomConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            Scheduler scheduler = context.getBean(.class);
            assertThat(scheduler.getSchedulerName()).isEqualTo("fooScheduler");
        });
    }

    @Test
    public void validateDefaultProperties() {
        this.contextRunner.withUserConfiguration(QuartzAutoConfigurationTests.ManualSchedulerConfiguration.class).run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            SchedulerFactoryBean schedulerFactory = context.getBean(.class);
            QuartzProperties properties = new QuartzProperties();
            assertThat(properties.isAutoStartup()).isEqualTo(schedulerFactory.isAutoStartup());
            assertThat(schedulerFactory).hasFieldOrPropertyWithValue("startupDelay", ((int) (properties.getStartupDelay().getSeconds())));
            assertThat(schedulerFactory).hasFieldOrPropertyWithValue("waitForJobsToCompleteOnShutdown", properties.isWaitForJobsToCompleteOnShutdown());
            assertThat(schedulerFactory).hasFieldOrPropertyWithValue("overwriteExistingJobs", properties.isOverwriteExistingJobs());
        });
    }

    @Test
    public void withCustomConfiguration() {
        this.contextRunner.withPropertyValues("spring.quartz.auto-startup=false", "spring.quartz.startup-delay=1m", "spring.quartz.wait-for-jobs-to-complete-on-shutdown=true", "spring.quartz.overwrite-existing-jobs=true").run(( context) -> {
            assertThat(context).hasSingleBean(.class);
            SchedulerFactoryBean schedulerFactory = context.getBean(.class);
            assertThat(schedulerFactory.isAutoStartup()).isFalse();
            assertThat(schedulerFactory).hasFieldOrPropertyWithValue("startupDelay", 60);
            assertThat(schedulerFactory).hasFieldOrPropertyWithValue("waitForJobsToCompleteOnShutdown", true);
            assertThat(schedulerFactory).hasFieldOrPropertyWithValue("overwriteExistingJobs", true);
        });
    }

    @Test
    public void schedulerNameWithDedicatedProperty() {
        this.contextRunner.withPropertyValues("spring.quartz.scheduler-name=testScheduler").run(assertSchedulerName("testScheduler"));
    }

    @Test
    public void schedulerNameWithQuartzProperty() {
        this.contextRunner.withPropertyValues("spring.quartz.properties.org.quartz.scheduler.instanceName=testScheduler").run(assertSchedulerName("testScheduler"));
    }

    @Test
    public void schedulerNameWithDedicatedPropertyTakesPrecedence() {
        this.contextRunner.withPropertyValues("spring.quartz.scheduler-name=specificTestScheduler", "spring.quartz.properties.org.quartz.scheduler.instanceName=testScheduler").run(assertSchedulerName("specificTestScheduler"));
    }

    @Test
    public void schedulerNameUseBeanNameByDefault() {
        this.contextRunner.withPropertyValues().run(assertSchedulerName("quartzScheduler"));
    }

    @Import(QuartzAutoConfigurationTests.ComponentThatUsesScheduler.class)
    @Configuration
    protected static class BaseQuartzConfiguration {}

    @Configuration
    protected static class QuartzJobsConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        public JobDetail fooJob() {
            return JobBuilder.newJob().ofType(QuartzAutoConfigurationTests.FooJob.class).withIdentity("fooJob").storeDurably().build();
        }

        @Bean
        public JobDetail barJob() {
            return JobBuilder.newJob().ofType(QuartzAutoConfigurationTests.FooJob.class).withIdentity("barJob").storeDurably().build();
        }
    }

    @Configuration
    protected static class QuartzFullConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        public JobDetail fooJob() {
            return JobBuilder.newJob().ofType(QuartzAutoConfigurationTests.FooJob.class).withIdentity("fooJob").usingJobData("jobDataKey", "jobDataValue").storeDurably().build();
        }

        @Bean
        public Trigger fooTrigger() {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever();
            return TriggerBuilder.newTrigger().forJob(fooJob()).withIdentity("fooTrigger").withSchedule(scheduleBuilder).build();
        }
    }

    @Configuration
    @Import(QuartzAutoConfigurationTests.QuartzFullConfiguration.class)
    protected static class OverwriteTriggerConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        public Trigger anotherFooTrigger(JobDetail fooJob) {
            SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(30).repeatForever();
            return TriggerBuilder.newTrigger().forJob(fooJob).withIdentity("fooTrigger").withSchedule(scheduleBuilder).build();
        }
    }

    @Configuration
    protected static class QuartzCalendarsConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        public Calendar weekly() {
            return new WeeklyCalendar();
        }

        @Bean
        public Calendar monthly() {
            return new MonthlyCalendar();
        }
    }

    @Configuration
    protected static class MockExecutorConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        public Executor executor() {
            return Mockito.mock(Executor.class);
        }
    }

    @Configuration
    protected static class QuartzCustomConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        public SchedulerFactoryBeanCustomizer customizer() {
            return ( schedulerFactoryBean) -> schedulerFactoryBean.setSchedulerName("fooScheduler");
        }
    }

    @Configuration
    protected static class ManualSchedulerConfiguration {
        @Bean
        public SchedulerFactoryBean quartzScheduler() {
            return new SchedulerFactoryBean();
        }
    }

    @Configuration
    protected static class MultipleDataSourceConfiguration extends QuartzAutoConfigurationTests.BaseQuartzConfiguration {
        @Bean
        @Primary
        public DataSource applicationDataSource() throws Exception {
            return createTestDataSource();
        }

        @QuartzDataSource
        @Bean
        public DataSource quartzDataSource() throws Exception {
            return createTestDataSource();
        }

        private DataSource createTestDataSource() throws Exception {
            DataSourceProperties properties = new DataSourceProperties();
            properties.setGenerateUniqueName(true);
            properties.afterPropertiesSet();
            return properties.initializeDataSourceBuilder().build();
        }
    }

    public static class ComponentThatUsesScheduler {
        public ComponentThatUsesScheduler(Scheduler scheduler) {
            Assert.notNull(scheduler, "Scheduler must not be null");
        }
    }

    public static class FooJob extends QuartzJobBean {
        @Autowired
        private Environment env;

        private String jobDataKey;

        @Override
        protected void executeInternal(JobExecutionContext context) {
            System.out.println((((this.env.getProperty("test-name", "unknown")) + " - ") + (this.jobDataKey)));
        }

        public void setJobDataKey(String jobDataKey) {
            this.jobDataKey = jobDataKey;
        }
    }
}

