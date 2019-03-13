/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.launch.support;


import BatchStatus.ABANDONED;
import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STARTED;
import BatchStatus.STOPPED;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.converter.DefaultJobParametersConverter;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.step.JobRepositorySupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;


/**
 *
 *
 * @author Lucas Ward
 */
public class CommandLineJobRunnerTests {
    private String jobPath = ClassUtils.addResourcePathToPackagePath(CommandLineJobRunnerTests.class, "launcher-with-environment.xml");

    private String jobName = "test-job";

    private String jobKey = "job.Key=myKey";

    private String scheduleDate = "schedule.Date=01/23/2008";

    private String vendorId = "vendor.id=33243243";

    private String[] args = new String[]{ jobPath, jobName, jobKey, scheduleDate, vendorId };

    private InputStream stdin;

    @Test
    public void testMain() throws Exception {
        CommandLineJobRunner.main(args);
        Assert.assertTrue("Injected JobParametersConverter not used instead of default", CommandLineJobRunnerTests.StubJobParametersConverter.called);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.getStatus());
    }

    @Test
    public void testWithJobLocator() throws Exception {
        jobPath = ClassUtils.addResourcePathToPackagePath(CommandLineJobRunnerTests.class, "launcher-with-locator.xml");
        CommandLineJobRunner.main(new String[]{ jobPath, jobName, jobKey });
        Assert.assertTrue("Injected JobParametersConverter not used instead of default", CommandLineJobRunnerTests.StubJobParametersConverter.called);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.getStatus());
    }

    @Test
    public void testJobAlreadyRunning() throws Throwable {
        CommandLineJobRunnerTests.StubJobLauncher.throwExecutionRunningException = true;
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testInvalidArgs() throws Exception {
        String[] args = new String[]{  };
        CommandLineJobRunner.presetSystemExiter(new CommandLineJobRunnerTests.StubSystemExiter());
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
        Assert.assertTrue(("Wrong error message: " + errorMessage), errorMessage.contains("At least 2 arguments are required: JobPath/JobClass and jobIdentifier."));
    }

    @Test
    public void testWrongJobName() throws Exception {
        String[] args = new String[]{ jobPath, "no-such-job" };
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
        Assert.assertTrue(("Wrong error message: " + errorMessage), ((errorMessage.contains("No bean named 'no-such-job' is defined")) || (errorMessage.contains("No bean named 'no-such-job' available"))));
    }

    @Test
    public void testWithNoParameters() throws Throwable {
        String[] args = new String[]{ jobPath, jobName };
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(new JobParameters(), CommandLineJobRunnerTests.StubJobLauncher.jobParameters);
    }

    @Test
    public void testWithInvalidStdin() throws Throwable {
        System.setIn(new InputStream() {
            @Override
            public int available() throws IOException {
                throw new IOException("Planned");
            }

            @Override
            public int read() {
                return -1;
            }
        });
        CommandLineJobRunner.main(new String[]{ jobPath, jobName });
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubJobLauncher.jobParameters.getParameters().size());
    }

    @Test
    public void testWithStdinCommandLine() throws Throwable {
        System.setIn(new InputStream() {
            char[] input = ((((jobPath) + "\n") + (jobName)) + "\nfoo=bar\nspam=bucket").toCharArray();

            int index = 0;

            @Override
            public int available() {
                return (input.length) - (index);
            }

            @Override
            public int read() {
                return (index) < ((input.length) - 1) ? ((int) (input[((index)++)])) : -1;
            }
        });
        CommandLineJobRunner.main(new String[0]);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(2, CommandLineJobRunnerTests.StubJobLauncher.jobParameters.getParameters().size());
    }

    @Test
    public void testWithStdinCommandLineWithEmptyLines() throws Throwable {
        System.setIn(new InputStream() {
            char[] input = ((((jobPath) + "\n") + (jobName)) + "\nfoo=bar\n\nspam=bucket\n\n").toCharArray();

            int index = 0;

            @Override
            public int available() {
                return (input.length) - (index);
            }

            @Override
            public int read() {
                return (index) < ((input.length) - 1) ? ((int) (input[((index)++)])) : -1;
            }
        });
        CommandLineJobRunner.main(new String[0]);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(2, CommandLineJobRunnerTests.StubJobLauncher.jobParameters.getParameters().size());
    }

    @Test
    public void testWithStdinParameters() throws Throwable {
        String[] args = new String[]{ jobPath, jobName };
        System.setIn(new InputStream() {
            char[] input = "foo=bar\nspam=bucket".toCharArray();

            int index = 0;

            @Override
            public int available() {
                return (input.length) - (index);
            }

            @Override
            public int read() {
                return (index) < ((input.length) - 1) ? ((int) (input[((index)++)])) : -1;
            }
        });
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(2, CommandLineJobRunnerTests.StubJobLauncher.jobParameters.getParameters().size());
    }

    @Test
    public void testWithInvalidParameters() throws Throwable {
        String[] args = new String[]{ jobPath, jobName, "foo" };
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
        Assert.assertTrue(("Wrong error message: " + errorMessage), errorMessage.contains("in the form name=value"));
    }

    @Test
    public void testStop() throws Throwable {
        String[] args = new String[]{ jobPath, "-stop", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(3L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testStopFailed() throws Throwable {
        String[] args = new String[]{ jobPath, "-stop", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(0L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testStopFailedAndRestarted() throws Throwable {
        String[] args = new String[]{ jobPath, "-stop", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(5L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testStopRestarted() throws Throwable {
        String[] args = new String[]{ jobPath, "-stop", jobName };
        JobInstance jobInstance = new JobInstance(3L, jobName);
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(jobInstance);
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testAbandon() throws Throwable {
        String[] args = new String[]{ jobPath, "-abandon", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(2L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testAbandonRunning() throws Throwable {
        String[] args = new String[]{ jobPath, "-abandon", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(3L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testAbandonAbandoned() throws Throwable {
        String[] args = new String[]{ jobPath, "-abandon", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(4L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
    }

    @Test
    public void testRestart() throws Throwable {
        String[] args = new String[]{ jobPath, "-restart", jobName };
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "bar").toJobParameters();
        JobInstance jobInstance = new JobInstance(0L, jobName);
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(jobInstance);
        CommandLineJobRunnerTests.StubJobExplorer.jobParameters = jobParameters;
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(jobParameters, CommandLineJobRunnerTests.StubJobLauncher.jobParameters);
        CommandLineJobRunnerTests.StubJobExplorer.jobParameters = new JobParameters();
    }

    @Test
    public void testRestartExecution() throws Throwable {
        String[] args = new String[]{ jobPath, "-restart", "11" };
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "bar").toJobParameters();
        JobExecution jobExecution = new JobExecution(new JobInstance(0L, jobName), 11L, jobParameters, null);
        jobExecution.setStatus(FAILED);
        CommandLineJobRunnerTests.StubJobExplorer.jobExecution = jobExecution;
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(jobParameters, CommandLineJobRunnerTests.StubJobLauncher.jobParameters);
    }

    @Test
    public void testRestartExecutionNotFailed() throws Throwable {
        String[] args = new String[]{ jobPath, "-restart", "11" };
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "bar").toJobParameters();
        JobExecution jobExecution = new JobExecution(new JobInstance(0L, jobName), 11L, jobParameters, null);
        jobExecution.setStatus(COMPLETED);
        CommandLineJobRunnerTests.StubJobExplorer.jobExecution = jobExecution;
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
        Assert.assertEquals(null, CommandLineJobRunnerTests.StubJobLauncher.jobParameters);
    }

    @Test
    public void testRestartNotFailed() throws Throwable {
        String[] args = new String[]{ jobPath, "-restart", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(123L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
        Assert.assertTrue(("Wrong error message: " + errorMessage), errorMessage.contains("No failed or stopped execution found"));
    }

    @Test
    public void testNext() throws Throwable {
        String[] args = new String[]{ jobPath, "-next", jobName, "bar=foo" };
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "bar").addString("bar", "foo").toJobParameters();
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = Arrays.asList(new JobInstance(2L, jobName));
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        jobParameters = new JobParametersBuilder().addString("foo", "spam").addString("bar", "foo").toJobParameters();
        Assert.assertEquals(jobParameters, CommandLineJobRunnerTests.StubJobLauncher.jobParameters);
    }

    @Test
    public void testNextFirstInSequence() throws Throwable {
        String[] args = new String[]{ jobPath, "-next", jobName };
        CommandLineJobRunnerTests.StubJobExplorer.jobInstances = new ArrayList();
        CommandLineJobRunner.main(args);
        Assert.assertEquals(0, CommandLineJobRunnerTests.StubSystemExiter.status);
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", "spam").toJobParameters();
        Assert.assertEquals(jobParameters, CommandLineJobRunnerTests.StubJobLauncher.jobParameters);
    }

    @Test
    public void testNextWithNoParameters() throws Exception {
        jobPath = ClassUtils.addResourcePathToPackagePath(CommandLineJobRunnerTests.class, "launcher-with-locator.xml");
        CommandLineJobRunner.main(new String[]{ jobPath, "-next", "test-job2", jobKey });
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.getStatus());
        String errorMessage = CommandLineJobRunner.getErrorMessage();
        Assert.assertTrue(("Wrong error message: " + errorMessage), errorMessage.contains(" No job parameters incrementer found"));
    }

    @Test
    public void testDestroyCallback() throws Throwable {
        String[] args = new String[]{ jobPath, jobName };
        CommandLineJobRunner.main(args);
        Assert.assertTrue(CommandLineJobRunnerTests.StubJobLauncher.destroyed);
    }

    @Test
    public void testJavaConfig() throws Exception {
        String[] args = new String[]{ "org.springframework.batch.core.launch.support.CommandLineJobRunnerTests$Configuration1", "invalidJobName" };
        CommandLineJobRunner.presetSystemExiter(new CommandLineJobRunnerTests.StubSystemExiter());
        CommandLineJobRunner.main(args);
        Assert.assertEquals(1, CommandLineJobRunnerTests.StubSystemExiter.status);
        String errorMessage = CommandLineJobRunner.getErrorMessage();
        Assert.assertTrue(("Wrong error message: " + errorMessage), errorMessage.contains("A JobLauncher must be provided.  Please add one to the configuration."));
    }

    public static class StubSystemExiter implements SystemExiter {
        private static int status;

        @Override
        public void exit(int status) {
            CommandLineJobRunnerTests.StubSystemExiter.status = status;
        }

        public static int getStatus() {
            return CommandLineJobRunnerTests.StubSystemExiter.status;
        }
    }

    public static class StubJobLauncher implements JobLauncher {
        public static JobExecution jobExecution;

        public static boolean throwExecutionRunningException = false;

        public static JobParameters jobParameters;

        private static boolean destroyed = false;

        @Override
        public JobExecution run(Job job, JobParameters jobParameters) throws JobExecutionAlreadyRunningException {
            CommandLineJobRunnerTests.StubJobLauncher.jobParameters = jobParameters;
            if (CommandLineJobRunnerTests.StubJobLauncher.throwExecutionRunningException) {
                throw new JobExecutionAlreadyRunningException("");
            }
            return CommandLineJobRunnerTests.StubJobLauncher.jobExecution;
        }

        public void destroy() {
            CommandLineJobRunnerTests.StubJobLauncher.destroyed = true;
        }

        public static void tearDown() {
            CommandLineJobRunnerTests.StubJobLauncher.jobExecution = null;
            CommandLineJobRunnerTests.StubJobLauncher.throwExecutionRunningException = false;
            CommandLineJobRunnerTests.StubJobLauncher.jobParameters = null;
            CommandLineJobRunnerTests.StubJobLauncher.destroyed = false;
        }
    }

    public static class StubJobRepository extends JobRepositorySupport {}

    public static class StubJobExplorer implements JobExplorer {
        static List<JobInstance> jobInstances = new ArrayList<>();

        static JobExecution jobExecution;

        static JobParameters jobParameters = new JobParameters();

        @Override
        public Set<JobExecution> findRunningJobExecutions(String jobName) {
            throw new UnsupportedOperationException();
        }

        @Override
        public JobExecution getJobExecution(Long executionId) {
            if ((CommandLineJobRunnerTests.StubJobExplorer.jobExecution) != null) {
                return CommandLineJobRunnerTests.StubJobExplorer.jobExecution;
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public List<JobExecution> getJobExecutions(JobInstance jobInstance) {
            if ((jobInstance.getId()) == 0) {
                return Arrays.asList(createJobExecution(jobInstance, FAILED));
            }
            if ((jobInstance.getId()) == 1) {
                return null;
            }
            if ((jobInstance.getId()) == 2) {
                return Arrays.asList(createJobExecution(jobInstance, STOPPED));
            }
            if ((jobInstance.getId()) == 3) {
                return Arrays.asList(createJobExecution(jobInstance, STARTED));
            }
            if ((jobInstance.getId()) == 4) {
                return Arrays.asList(createJobExecution(jobInstance, ABANDONED));
            }
            if ((jobInstance.getId()) == 5) {
                return Arrays.asList(createJobExecution(jobInstance, STARTED), createJobExecution(jobInstance, FAILED));
            }
            return Arrays.asList(createJobExecution(jobInstance, COMPLETED));
        }

        private JobExecution createJobExecution(JobInstance jobInstance, BatchStatus status) {
            JobExecution jobExecution = new JobExecution(jobInstance, 1L, CommandLineJobRunnerTests.StubJobExplorer.jobParameters, null);
            jobExecution.setStatus(status);
            jobExecution.setStartTime(new Date());
            if (status != (BatchStatus.STARTED)) {
                jobExecution.setEndTime(new Date());
            }
            return jobExecution;
        }

        @Override
        public JobInstance getJobInstance(Long instanceId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<JobInstance> getJobInstances(String jobName, int start, int count) {
            if ((CommandLineJobRunnerTests.StubJobExplorer.jobInstances) == null) {
                return new ArrayList<>();
            }
            List<JobInstance> result = CommandLineJobRunnerTests.StubJobExplorer.jobInstances;
            CommandLineJobRunnerTests.StubJobExplorer.jobInstances = null;
            return result;
        }

        @Override
        public StepExecution getStepExecution(Long jobExecutionId, Long stepExecutionId) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<String> getJobNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<JobInstance> findJobInstancesByJobName(String jobName, int start, int count) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getJobInstanceCount(String jobName) throws NoSuchJobException {
            int count = 0;
            for (JobInstance jobInstance : CommandLineJobRunnerTests.StubJobExplorer.jobInstances) {
                if (jobInstance.getJobName().equals(jobName)) {
                    count++;
                }
            }
            if (count == 0) {
                throw new NoSuchJobException(("Unable to find job instances for " + jobName));
            } else {
                return count;
            }
        }
    }

    public static class StubJobParametersConverter implements JobParametersConverter {
        JobParametersConverter delegate = new DefaultJobParametersConverter();

        static boolean called = false;

        @Override
        public JobParameters getJobParameters(Properties properties) {
            CommandLineJobRunnerTests.StubJobParametersConverter.called = true;
            return delegate.getJobParameters(properties);
        }

        @Override
        public Properties getProperties(JobParameters params) {
            throw new UnsupportedOperationException();
        }
    }

    @Configuration
    public static class Configuration1 {
        @Bean
        public String bean1() {
            return "bean1";
        }
    }
}

