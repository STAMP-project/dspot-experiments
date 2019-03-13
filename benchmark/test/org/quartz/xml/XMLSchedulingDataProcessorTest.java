package org.quartz.xml;


import java.io.File;
import java.util.Calendar;
import junit.framework.TestCase;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.quartz.JobBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.impl.SchedulerRepository;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.jdbcjobstore.JdbcQuartzTestUtilities;
import org.quartz.impl.jdbcjobstore.JobStoreTX;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.ClassLoadHelper;

import static XMLSchedulingDataProcessor.QUARTZ_XML_DEFAULT_FILE_NAME;


/**
 * Unit test for XMLSchedulingDataProcessor.
 *
 * @author Zemian Deng
 * @author Tomasz Nurkiewicz (QTZ-273)
 */
public class XMLSchedulingDataProcessorTest extends TestCase {
    /**
     * QTZ-185
     * <p>The default XMLSchedulingDataProcessor will setOverWriteExistingData(true), and we want to
     * test programmatically overriding this value.
     *
     * <p>Note that XMLSchedulingDataProcessor#processFileAndScheduleJobs(Scheduler,boolean) will only
     * read default "quartz_data.xml" in current working directory. So to test this, we must create
     * this file. If this file already exist, it will be overwritten!
     */
    public void testOverwriteFlag() throws Exception {
        // Prepare a quartz_data.xml in current working directory by copy a test case file.
        File file = new File(QUARTZ_XML_DEFAULT_FILE_NAME);
        copyResourceToFile("/org/quartz/xml/simple-job-trigger.xml", file);
        Scheduler scheduler = null;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory("org/quartz/xml/quartz-test.properties");
            scheduler = factory.getScheduler();
            // Let's setup a fixture job data that we know test is not going modify it.
            JobDetail job = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("job1").usingJobData("foo", "dont_chg_me").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("job1").withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();
            scheduler.scheduleJob(job, trigger);
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            try {
                processor.processFileAndScheduleJobs(scheduler, false);
                TestCase.fail("OverWriteExisting flag didn't work. We should get Exception when overwrite is set to false.");
            } catch (ObjectAlreadyExistsException e) {
                // This is expected. Do nothing.
            }
            // We should still have what we start with.
            TestCase.assertEquals(1, scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT")).size());
            TestCase.assertEquals(1, scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")).size());
            job = scheduler.getJobDetail(JobKey.jobKey("job1"));
            String fooValue = job.getJobDataMap().getString("foo");
            TestCase.assertEquals("dont_chg_me", fooValue);
        } finally {
            // remove test file
            if ((file.exists()) && (!(file.delete())))
                throw new RuntimeException(("Failed to remove test file " + file));

            // shutdown scheduler
            if (scheduler != null)
                scheduler.shutdown();

        }
    }

    /**
     * QTZ-187
     */
    public void testDirectivesNoOverwriteWithIgnoreDups() throws Exception {
        Scheduler scheduler = null;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory("org/quartz/xml/quartz-test.properties");
            scheduler = factory.getScheduler();
            // Setup existing job with same names as in xml data.
            JobDetail job = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("job1").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("job1").withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();
            scheduler.scheduleJob(job, trigger);
            job = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("job2").build();
            trigger = TriggerBuilder.newTrigger().withIdentity("job2").withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();
            scheduler.scheduleJob(job, trigger);
            // Now load the xml data with directives: overwrite-existing-data=false, ignore-duplicates=true
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            processor.processFileAndScheduleJobs("org/quartz/xml/directives_no-overwrite_ignoredups.xml", scheduler);
            TestCase.assertEquals(2, scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT")).size());
            TestCase.assertEquals(2, scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")).size());
        } finally {
            if (scheduler != null)
                scheduler.shutdown();

        }
    }

    public void testDirectivesOverwriteWithNoIgnoreDups() throws Exception {
        Scheduler scheduler = null;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory("org/quartz/xml/quartz-test.properties");
            scheduler = factory.getScheduler();
            // Setup existing job with same names as in xml data.
            JobDetail job = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("job1").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("job1").withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();
            scheduler.scheduleJob(job, trigger);
            job = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("job2").build();
            trigger = TriggerBuilder.newTrigger().withIdentity("job2").withSchedule(SimpleScheduleBuilder.repeatHourlyForever()).build();
            scheduler.scheduleJob(job, trigger);
            // Now load the xml data with directives: overwrite-existing-data=false, ignore-duplicates=true
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            processor.processFileAndScheduleJobs("org/quartz/xml/directives_overwrite_no-ignoredups.xml", scheduler);
            TestCase.assertEquals(2, scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT")).size());
            TestCase.assertEquals(2, scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")).size());
        } finally {
            if (scheduler != null)
                scheduler.shutdown();

        }
    }

    /**
     * QTZ-180
     */
    public void testXsdSchemaValidationOnVariousTriggers() throws Exception {
        Scheduler scheduler = null;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory("org/quartz/xml/quartz-test.properties");
            scheduler = factory.getScheduler();
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            processor.processFileAndScheduleJobs("org/quartz/xml/job-scheduling-data-2.0_trigger-samples.xml", scheduler);
            TestCase.assertEquals(1, scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT")).size());
            TestCase.assertEquals(35, scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")).size());
        } finally {
            if (scheduler != null)
                scheduler.shutdown();

        }
    }

    public void testQTZ327SimpleTriggerNoRepeat() throws Exception {
        Scheduler scheduler = null;
        try {
            StdSchedulerFactory factory = new StdSchedulerFactory("org/quartz/xml/quartz-test.properties");
            scheduler = factory.getScheduler();
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            processor.processFileAndScheduleJobs("org/quartz/xml/simple-job-trigger-no-repeat.xml", scheduler);
            TestCase.assertEquals(1, scheduler.getJobKeys(GroupMatcher.jobGroupEquals("DEFAULT")).size());
            TestCase.assertEquals(1, scheduler.getTriggerKeys(GroupMatcher.triggerGroupEquals("DEFAULT")).size());
        } finally {
            if (scheduler != null)
                scheduler.shutdown();

        }
    }

    /**
     * QTZ-273
     */
    public void testTimeZones() throws Exception {
        Scheduler scheduler = null;
        try {
            // given
            StdSchedulerFactory factory = new StdSchedulerFactory("org/quartz/xml/quartz-test.properties");
            scheduler = factory.getScheduler();
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            // when
            processor.processFileAndScheduleJobs("org/quartz/xml/simple-job-trigger-with-timezones.xml", scheduler);
            // then
            Trigger trigger = scheduler.getTrigger(new TriggerKey("job1", "DEFAULT"));
            TestCase.assertNotNull(trigger);
            TestCase.assertEquals(dateOfGMT_UTC(18, 0, 0, 1, Calendar.JANUARY, 2012), trigger.getStartTime());
            TestCase.assertEquals(dateOfGMT_UTC(19, 0, 0, 1, Calendar.JANUARY, 2012), trigger.getEndTime());
            trigger = scheduler.getTrigger(new TriggerKey("job2", "DEFAULT"));
            TestCase.assertNotNull(trigger);
            TestCase.assertEquals(dateOfLocalTime(6, 0, 0, 1, Calendar.JANUARY, 2012), trigger.getStartTime());
            TestCase.assertEquals(dateOfGMT_UTC(19, 0, 0, 1, Calendar.JANUARY, 2012), trigger.getEndTime());
        } finally {
            if (scheduler != null)
                scheduler.shutdown();

        }
    }

    /**
     * An empty job for testing purpose.
     */
    public static class MyJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            // 
        }
    }

    /**
     * Test for QTZ-353, where it requires a JDBC storage
     */
    public void testRemoveJobClassNotFound() throws Exception {
        String DB_NAME = "XmlDeleteNonExistsJobTestDatasase";
        String SCHEDULER_NAME = "XmlDeleteNonExistsJobTestScheduler";
        JdbcQuartzTestUtilities.createDatabase(DB_NAME);
        JobStoreTX jobStore = new JobStoreTX();
        jobStore.setDataSource(DB_NAME);
        jobStore.setTablePrefix("QRTZ_");
        jobStore.setInstanceId("AUTO");
        DirectSchedulerFactory.getInstance().createScheduler(SCHEDULER_NAME, "AUTO", new SimpleThreadPool(4, Thread.NORM_PRIORITY), jobStore);
        Scheduler scheduler = SchedulerRepository.getInstance().lookup(SCHEDULER_NAME);
        try {
            JobDetail jobDetail = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("testjob1", "DEFAULT").usingJobData("foo", "foo").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("testjob1", "DEFAULT").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
            scheduler.scheduleJob(jobDetail, trigger);
            JobDetail jobDetail2 = scheduler.getJobDetail(jobDetail.getKey());
            Trigger trigger2 = scheduler.getTrigger(trigger.getKey());
            Assert.assertThat(jobDetail2.getJobDataMap().getString("foo"), Matchers.is("foo"));
            Assert.assertThat(trigger2, Matchers.instanceOf(CronTrigger.class));
            modifyStoredJobClassName();
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            processor.processFileAndScheduleJobs("org/quartz/xml/delete-no-jobclass.xml", scheduler);
            jobDetail2 = scheduler.getJobDetail(jobDetail.getKey());
            trigger2 = scheduler.getTrigger(trigger.getKey());
            Assert.assertThat(trigger2, Matchers.nullValue());
            Assert.assertThat(jobDetail2, Matchers.nullValue());
            jobDetail2 = scheduler.getJobDetail(new JobKey("job1", "DEFAULT"));
            trigger2 = scheduler.getTrigger(new TriggerKey("job1", "DEFAULT"));
            Assert.assertThat(jobDetail2.getJobDataMap().getString("foo"), Matchers.is("bar"));
            Assert.assertThat(trigger2, Matchers.instanceOf(SimpleTrigger.class));
        } finally {
            scheduler.shutdown(false);
            JdbcQuartzTestUtilities.destroyDatabase(DB_NAME);
        }
    }

    public void testOverwriteJobClassNotFound() throws Exception {
        String DB_NAME = "XmlDeleteNonExistsJobTestDatasase";
        String SCHEDULER_NAME = "XmlDeleteNonExistsJobTestScheduler";
        JdbcQuartzTestUtilities.createDatabase(DB_NAME);
        JobStoreTX jobStore = new JobStoreTX();
        jobStore.setDataSource(DB_NAME);
        jobStore.setTablePrefix("QRTZ_");
        jobStore.setInstanceId("AUTO");
        DirectSchedulerFactory.getInstance().createScheduler(SCHEDULER_NAME, "AUTO", new SimpleThreadPool(4, Thread.NORM_PRIORITY), jobStore);
        Scheduler scheduler = SchedulerRepository.getInstance().lookup(SCHEDULER_NAME);
        try {
            JobDetail jobDetail = JobBuilder.newJob(XMLSchedulingDataProcessorTest.MyJob.class).withIdentity("job1", "DEFAULT").usingJobData("foo", "foo").build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity("job1", "DEFAULT").withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?")).build();
            scheduler.scheduleJob(jobDetail, trigger);
            JobDetail jobDetail2 = scheduler.getJobDetail(jobDetail.getKey());
            Trigger trigger2 = scheduler.getTrigger(trigger.getKey());
            Assert.assertThat(jobDetail2.getJobDataMap().getString("foo"), Matchers.is("foo"));
            Assert.assertThat(trigger2, Matchers.instanceOf(CronTrigger.class));
            modifyStoredJobClassName();
            ClassLoadHelper clhelper = new CascadingClassLoadHelper();
            clhelper.initialize();
            XMLSchedulingDataProcessor processor = new XMLSchedulingDataProcessor(clhelper);
            processor.processFileAndScheduleJobs("org/quartz/xml/overwrite-no-jobclass.xml", scheduler);
            jobDetail2 = scheduler.getJobDetail(jobDetail.getKey());
            trigger2 = scheduler.getTrigger(trigger.getKey());
            Assert.assertThat(jobDetail2.getJobDataMap().getString("foo"), Matchers.is("bar"));
            Assert.assertThat(trigger2, Matchers.instanceOf(SimpleTrigger.class));
        } finally {
            scheduler.shutdown(false);
            JdbcQuartzTestUtilities.destroyDatabase(DB_NAME);
        }
    }
}

