/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.s3a.commit;


import JobStatus.State.FAILED;
import JobStatus.State.KILLED;
import JobStatus.State.RUNNING;
import MRJobConfig.APPLICATION_ATTEMPT_ID;
import MRJobConfig.TASK_ATTEMPT_ID;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3ATestUtils;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MapFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.concurrent.HadoopExecutors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the job/task commit actions of an S3A Committer, including trying to
 * simulate some failure and retry conditions.
 * Derived from
 * {@code org.apache.hadoop.mapreduce.lib.output.TestFileOutputCommitter}.
 *
 * This is a complex test suite as it tries to explore the full lifecycle
 * of committers, and is designed for subclassing.
 */
@SuppressWarnings({ "unchecked", "ThrowableNotThrown", "unused" })
public abstract class AbstractITCommitProtocol extends AbstractCommitITest {
    private Path outDir;

    private static final Logger LOG = LoggerFactory.getLogger(AbstractITCommitProtocol.class);

    private static final String SUB_DIR = "SUB_DIR";

    protected static final String PART_00000 = "part-m-00000";

    /**
     * Counter to guarantee that even in parallel test runs, no job has the same
     * ID.
     */
    private String jobId;

    // A random task attempt id for testing.
    private String attempt0;

    private TaskAttemptID taskAttempt0;

    private String attempt1;

    private TaskAttemptID taskAttempt1;

    private static final Text KEY_1 = new Text("key1");

    private static final Text KEY_2 = new Text("key2");

    private static final Text VAL_1 = new Text("val1");

    private static final Text VAL_2 = new Text("val2");

    /**
     * A job to abort in test case teardown.
     */
    private List<AbstractITCommitProtocol.JobData> abortInTeardown = new ArrayList<>(1);

    private final AbstractITCommitProtocol.StandardCommitterFactory standardCommitterFactory = new AbstractITCommitProtocol.StandardCommitterFactory();

    /**
     * Functional interface for creating committers, designed to allow
     * different factories to be used to create different failure modes.
     */
    @FunctionalInterface
    public interface CommitterFactory {
        /**
         * Create a committer for a task.
         *
         * @param context
         * 		task context
         * @return new committer
         * @throws IOException
         * 		failure
         */
        AbstractS3ACommitter createCommitter(TaskAttemptContext context) throws IOException;
    }

    /**
     * The normal committer creation factory, uses the abstract methods
     * in the class.
     */
    public class StandardCommitterFactory implements AbstractITCommitProtocol.CommitterFactory {
        @Override
        public AbstractS3ACommitter createCommitter(TaskAttemptContext context) throws IOException {
            return AbstractITCommitProtocol.this.createCommitter(context);
        }
    }

    /**
     * Details on a job for use in {@code startJob} and elsewhere.
     */
    public static class JobData {
        private final Job job;

        private final JobContext jContext;

        private final TaskAttemptContext tContext;

        private final AbstractS3ACommitter committer;

        private final Configuration conf;

        public JobData(Job job, JobContext jContext, TaskAttemptContext tContext, AbstractS3ACommitter committer) {
            this.job = job;
            this.jContext = jContext;
            this.tContext = tContext;
            this.committer = committer;
            conf = job.getConfiguration();
        }
    }

    /**
     * Verify that recovery doesn't work for these committers.
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testRecoveryAndCleanup() throws Exception {
        describe("Test (unsupported) task recovery.");
        AbstractITCommitProtocol.JobData jobData = startJob(true);
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        assertNotNull(("null workPath in committer " + committer), committer.getWorkPath());
        assertNotNull(("null outputPath in committer " + committer), committer.getOutputPath());
        // Commit the task. This will promote data and metadata to where
        // job commits will pick it up on commit or abort.
        committer.commitTask(tContext);
        assertTaskAttemptPathDoesNotExist(committer, tContext);
        Configuration conf2 = jobData.job.getConfiguration();
        conf2.set(TASK_ATTEMPT_ID, attempt0);
        conf2.setInt(APPLICATION_ATTEMPT_ID, 2);
        JobContext jContext2 = new org.apache.hadoop.mapreduce.task.JobContextImpl(conf2, taskAttempt0.getJobID());
        TaskAttemptContext tContext2 = new TaskAttemptContextImpl(conf2, taskAttempt0);
        AbstractS3ACommitter committer2 = createCommitter(tContext2);
        committer2.setupJob(tContext2);
        assertFalse(("recoverySupported in " + committer2), committer2.isRecoverySupported());
        intercept(PathCommitException.class, "recover", () -> committer2.recoverTask(tContext2));
        // at this point, task attempt 0 has failed to recover
        // it should be abortable though. This will be a no-op as it already
        // committed
        describe("aborting task attempt 2; expect nothing to clean up");
        committer2.abortTask(tContext2);
        describe("Aborting job 2; expect pending commits to be aborted");
        committer2.abortJob(jContext2, KILLED);
        // now, state of system may still have pending data
        assertNoMultipartUploadsPending(outDir);
    }

    /**
     * Full test of the expected lifecycle: start job, task, write, commit task,
     * commit job.
     *
     * @throws Exception
     * 		on a failure
     */
    @Test
    public void testCommitLifecycle() throws Exception {
        describe(("Full test of the expected lifecycle:\n" + ((((" start job, task, write, commit task, commit job.\n" + "Verify:\n") + "* no files are visible after task commit\n") + "* the expected file is visible after job commit\n") + "* no outstanding MPUs after job commit")));
        AbstractITCommitProtocol.JobData jobData = startJob(false);
        JobContext jContext = jobData.jContext;
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        validateTaskAttemptWorkingDirectory(committer, tContext);
        // write output
        describe("1. Writing output");
        writeTextOutput(tContext);
        dumpMultipartUploads();
        describe("2. Committing task");
        assertTrue(("No files to commit were found by " + committer), committer.needsTaskCommit(tContext));
        committer.commitTask(tContext);
        // this is only task commit; there MUST be no part- files in the dest dir
        waitForConsistency();
        try {
            applyLocatedFiles(getFileSystem().listFiles(outDir, false), ( status) -> assertFalse(("task committed file to dest :" + status), status.getPath().toString().contains("part")));
        } catch (FileNotFoundException ignored) {
            log().info("Outdir {} is not created by task commit phase ", outDir);
        }
        describe("3. Committing job");
        assertMultipartUploadsPending(outDir);
        committer.commitJob(jContext);
        // validate output
        describe("4. Validating content");
        validateContent(outDir, shouldExpectSuccessMarker());
        assertNoMultipartUploadsPending(outDir);
    }

    @Test
    public void testCommitterWithDuplicatedCommit() throws Exception {
        describe(("Call a task then job commit twice;" + "expect the second task commit to fail."));
        AbstractITCommitProtocol.JobData jobData = startJob(true);
        JobContext jContext = jobData.jContext;
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        // do commit
        commit(committer, jContext, tContext);
        // validate output
        validateContent(outDir, shouldExpectSuccessMarker());
        assertNoMultipartUploadsPending(outDir);
        // commit task to fail on retry
        AbstractITCommitProtocol.expectFNFEonTaskCommit(committer, tContext);
    }

    /**
     * Simulate a failure on the first job commit; expect the
     * second to succeed.
     */
    @Test
    public void testCommitterWithFailure() throws Exception {
        describe("Fail the first job commit then retry");
        AbstractITCommitProtocol.JobData jobData = startJob(new AbstractITCommitProtocol.FailingCommitterFactory(), true);
        JobContext jContext = jobData.jContext;
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        // do commit
        committer.commitTask(tContext);
        // now fail job
        AbstractITCommitProtocol.expectSimulatedFailureOnJobCommit(jContext, committer);
        committer.commitJob(jContext);
        // but the data got there, due to the order of operations.
        validateContent(outDir, shouldExpectSuccessMarker());
        expectJobCommitToFail(jContext, committer);
    }

    /**
     * Simulate a failure on the first job commit; expect the
     * second to succeed.
     */
    @Test
    public void testCommitterWithNoOutputs() throws Exception {
        describe("Have a task and job with no outputs: expect success");
        AbstractITCommitProtocol.JobData jobData = startJob(new AbstractITCommitProtocol.FailingCommitterFactory(), false);
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        // do commit
        committer.commitTask(tContext);
        assertTaskAttemptPathDoesNotExist(committer, tContext);
    }

    @Test
    public void testMapFileOutputCommitter() throws Exception {
        describe(("Test that the committer generates map output into a directory\n" + "starting with the prefix part-"));
        AbstractITCommitProtocol.JobData jobData = startJob(false);
        JobContext jContext = jobData.jContext;
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        Configuration conf = jobData.conf;
        // write output
        writeMapFileOutput(new MapFileOutputFormat().getRecordWriter(tContext), tContext);
        // do commit
        commit(committer, jContext, tContext);
        S3AFileSystem fs = getFileSystem();
        waitForConsistency();
        S3ATestUtils.lsR(fs, outDir, true);
        String ls = ls(outDir);
        describe("\nvalidating");
        // validate output
        verifySuccessMarker(outDir);
        describe("validate output of %s", outDir);
        validateMapFileOutputContent(fs, outDir);
        // Ensure getReaders call works and also ignores
        // hidden filenames (_ or . prefixes)
        describe("listing");
        FileStatus[] filtered = fs.listStatus(outDir, HIDDEN_FILE_FILTER);
        assertEquals(("listed children under " + ls), 1, filtered.length);
        FileStatus fileStatus = filtered[0];
        assertTrue(("Not the part file: " + fileStatus), fileStatus.getPath().getName().startsWith(AbstractITCommitProtocol.PART_00000));
        describe("getReaders()");
        assertEquals(((("Number of MapFile.Reader entries with shared FS " + (outDir)) + " : ") + ls), 1, AbstractITCommitProtocol.getReaders(fs, outDir, conf).length);
        describe("getReaders(new FS)");
        FileSystem fs2 = FileSystem.get(outDir.toUri(), conf);
        assertEquals(((("Number of MapFile.Reader entries with shared FS2 " + (outDir)) + " : ") + ls), 1, AbstractITCommitProtocol.getReaders(fs2, outDir, conf).length);
        describe("MapFileOutputFormat.getReaders");
        assertEquals(((("Number of MapFile.Reader entries with new FS in " + (outDir)) + " : ") + ls), 1, MapFileOutputFormat.getReaders(outDir, conf).length);
    }

    /**
     * A functional interface which an action to test must implement.
     */
    @FunctionalInterface
    public interface ActionToTest {
        void exec(Job job, JobContext jContext, TaskAttemptContext tContext, AbstractS3ACommitter committer) throws Exception;
    }

    @Test
    public void testAbortTaskNoWorkDone() throws Exception {
        executeWork("abort task no work", ( job, jContext, tContext, committer) -> committer.abortTask(tContext));
    }

    @Test
    public void testAbortJobNoWorkDone() throws Exception {
        executeWork("abort task no work", ( job, jContext, tContext, committer) -> committer.abortJob(jContext, RUNNING));
    }

    @Test
    public void testCommitJobButNotTask() throws Exception {
        executeWork(("commit a job while a task's work is pending, " + "expect task writes to be cancelled."), ( job, jContext, tContext, committer) -> {
            // step 1: write the text
            writeTextOutput(tContext);
            // step 2: commit the job
            createCommitter(tContext).commitJob(tContext);
            // verify that no output can be observed
            assertPart0000DoesNotExist(outDir);
            // that includes, no pending MPUs; commitJob is expected to
            // cancel any.
            assertNoMultipartUploadsPending(outDir);
        });
    }

    @Test
    public void testAbortTaskThenJob() throws Exception {
        AbstractITCommitProtocol.JobData jobData = startJob(true);
        AbstractS3ACommitter committer = jobData.committer;
        // do abort
        committer.abortTask(jobData.tContext);
        intercept(FileNotFoundException.class, "", () -> getPart0000(committer.getWorkPath()));
        committer.abortJob(jobData.jContext, FAILED);
        assertJobAbortCleanedUp(jobData);
    }

    @Test
    public void testFailAbort() throws Exception {
        describe("Abort the task, then job (failed), abort the job again");
        AbstractITCommitProtocol.JobData jobData = startJob(true);
        JobContext jContext = jobData.jContext;
        TaskAttemptContext tContext = jobData.tContext;
        AbstractS3ACommitter committer = jobData.committer;
        // do abort
        committer.abortTask(tContext);
        committer.getJobAttemptPath(jContext);
        committer.getTaskAttemptPath(tContext);
        assertPart0000DoesNotExist(outDir);
        assertSuccessMarkerDoesNotExist(outDir);
        describe("Aborting job into %s", outDir);
        committer.abortJob(jContext, FAILED);
        assertTaskAttemptPathDoesNotExist(committer, tContext);
        assertJobAttemptPathDoesNotExist(committer, jContext);
        // try again; expect abort to be idempotent.
        committer.abortJob(jContext, FAILED);
        assertNoMultipartUploadsPending(outDir);
    }

    @Test
    public void testAbortJobNotTask() throws Exception {
        executeWork("abort task no work", ( job, jContext, tContext, committer) -> {
            // write output
            writeTextOutput(tContext);
            committer.abortJob(jContext, RUNNING);
            assertTaskAttemptPathDoesNotExist(committer, tContext);
            assertJobAttemptPathDoesNotExist(committer, jContext);
            assertNoMultipartUploadsPending(outDir);
        });
    }

    /**
     * This looks at what happens with concurrent commits.
     * However, the failure condition it looks for (subdir under subdir)
     * is the kind of failure you see on a rename-based commit.
     *
     * What it will not detect is the fact that both tasks will each commit
     * to the destination directory. That is: whichever commits last wins.
     *
     * There's no way to stop this. Instead it is a requirement that the task
     * commit operation is only executed when the committer is happy to
     * commit only those tasks which it knows have succeeded, and abort those
     * which have not.
     *
     * @throws Exception
     * 		failure
     */
    @Test
    public void testConcurrentCommitTaskWithSubDir() throws Exception {
        Job job = newJob();
        FileOutputFormat.setOutputPath(job, outDir);
        final Configuration conf = job.getConfiguration();
        final JobContext jContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(conf, taskAttempt0.getJobID());
        AbstractS3ACommitter amCommitter = createCommitter(new TaskAttemptContextImpl(conf, taskAttempt0));
        amCommitter.setupJob(jContext);
        final TaskAttemptContext[] taCtx = new TaskAttemptContextImpl[2];
        taCtx[0] = new TaskAttemptContextImpl(conf, taskAttempt0);
        taCtx[1] = new TaskAttemptContextImpl(conf, taskAttempt1);
        final TextOutputFormat[] tof = new LoggingTextOutputFormat[2];
        for (int i = 0; i < (tof.length); i++) {
            tof[i] = new LoggingTextOutputFormat() {
                @Override
                public Path getDefaultWorkFile(TaskAttemptContext context, String extension) throws IOException {
                    final AbstractS3ACommitter foc = ((AbstractS3ACommitter) (getOutputCommitter(context)));
                    return new Path(new Path(foc.getWorkPath(), AbstractITCommitProtocol.SUB_DIR), getUniqueFile(context, getOutputName(context), extension));
                }
            };
        }
        final ExecutorService executor = HadoopExecutors.newFixedThreadPool(2);
        try {
            for (int i = 0; i < (taCtx.length); i++) {
                final int taskIdx = i;
                executor.submit(() -> {
                    final OutputCommitter outputCommitter = tof[taskIdx].getOutputCommitter(taCtx[taskIdx]);
                    outputCommitter.setupTask(taCtx[taskIdx]);
                    final RecordWriter rw = tof[taskIdx].getRecordWriter(taCtx[taskIdx]);
                    writeOutput(rw, taCtx[taskIdx]);
                    describe("Committing Task %d", taskIdx);
                    outputCommitter.commitTask(taCtx[taskIdx]);
                    return null;
                });
            }
        } finally {
            executor.shutdown();
            while (!(executor.awaitTermination(1, TimeUnit.SECONDS))) {
                log().info("Awaiting thread termination!");
            } 
        }
        // if we commit here then all tasks will be committed, so there will
        // be contention for that final directory: both parts will go in.
        describe("\nCommitting Job");
        amCommitter.commitJob(jContext);
        assertPathExists("base output directory", outDir);
        assertPart0000DoesNotExist(outDir);
        Path outSubDir = new Path(outDir, AbstractITCommitProtocol.SUB_DIR);
        assertPathDoesNotExist("Must not end up with sub_dir/sub_dir", new Path(outSubDir, AbstractITCommitProtocol.SUB_DIR));
        // validate output
        // There's no success marker in the subdirectory
        validateContent(outSubDir, false);
    }

    /**
     * Factory for failing committers.
     */
    public class FailingCommitterFactory implements AbstractITCommitProtocol.CommitterFactory {
        @Override
        public AbstractS3ACommitter createCommitter(TaskAttemptContext context) throws IOException {
            return createFailingCommitter(context);
        }
    }

    @Test
    public void testOutputFormatIntegration() throws Throwable {
        Configuration conf = getConfiguration();
        Job job = newJob();
        job.setOutputFormatClass(LoggingTextOutputFormat.class);
        conf = job.getConfiguration();
        conf.set(TASK_ATTEMPT_ID, attempt0);
        conf.setInt(APPLICATION_ATTEMPT_ID, 1);
        JobContext jContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(conf, taskAttempt0.getJobID());
        TaskAttemptContext tContext = new TaskAttemptContextImpl(conf, taskAttempt0);
        LoggingTextOutputFormat outputFormat = ((LoggingTextOutputFormat) (ReflectionUtils.newInstance(tContext.getOutputFormatClass(), conf)));
        AbstractS3ACommitter committer = ((AbstractS3ACommitter) (outputFormat.getOutputCommitter(tContext)));
        // setup
        AbstractITCommitProtocol.JobData jobData = new AbstractITCommitProtocol.JobData(job, jContext, tContext, committer);
        setup(jobData);
        abortInTeardown(jobData);
        LoggingTextOutputFormat.LoggingLineRecordWriter recordWriter = outputFormat.getRecordWriter(tContext);
        IntWritable iw = new IntWritable(1);
        recordWriter.write(iw, iw);
        Path dest = recordWriter.getDest();
        validateTaskAttemptPathDuringWrite(dest);
        recordWriter.close(tContext);
        // at this point
        validateTaskAttemptPathAfterWrite(dest);
        assertTrue(("Committer does not have data to commit " + committer), committer.needsTaskCommit(tContext));
        committer.commitTask(tContext);
        committer.commitJob(jContext);
        // validate output
        verifySuccessMarker(outDir);
    }

    /**
     * Create a committer through reflection then use it to abort
     * a task. This mimics the action of an AM when a container fails and
     * the AM wants to abort the task attempt.
     */
    @Test
    public void testAMWorkflow() throws Throwable {
        describe("Create a committer with a null output path & use as an AM");
        AbstractITCommitProtocol.JobData jobData = startJob(true);
        JobContext jContext = jobData.jContext;
        TaskAttemptContext tContext = jobData.tContext;
        TaskAttemptContext newAttempt = AbstractCommitITest.taskAttemptForJob(MRBuilderUtils.newJobId(1, 1, 1), jContext);
        Configuration conf = jContext.getConfiguration();
        // bind
        LoggingTextOutputFormat.bind(conf);
        OutputFormat<?, ?> outputFormat = ReflectionUtils.newInstance(newAttempt.getOutputFormatClass(), conf);
        Path outputPath = FileOutputFormat.getOutputPath(newAttempt);
        assertNotNull("null output path in new task attempt", outputPath);
        AbstractS3ACommitter committer2 = ((AbstractS3ACommitter) (outputFormat.getOutputCommitter(newAttempt)));
        committer2.abortTask(tContext);
        assertNoMultipartUploadsPending(getOutDir());
    }

    @Test
    public void testParallelJobsToAdjacentPaths() throws Throwable {
        describe("Run two jobs in parallel, assert they both complete");
        AbstractITCommitProtocol.JobData jobData = startJob(true);
        Job job1 = jobData.job;
        AbstractS3ACommitter committer1 = jobData.committer;
        JobContext jContext1 = jobData.jContext;
        TaskAttemptContext tContext1 = jobData.tContext;
        // now build up a second job
        String jobId2 = AbstractCommitITest.randomJobId();
        String attempt20 = ("attempt_" + jobId2) + "_m_000000_0";
        TaskAttemptID taskAttempt20 = TaskAttemptID.forName(attempt20);
        String attempt21 = ("attempt_" + jobId2) + "_m_000001_0";
        TaskAttemptID taskAttempt21 = TaskAttemptID.forName(attempt21);
        Path job1Dest = outDir;
        Path job2Dest = new Path(getOutDir().getParent(), ((getMethodName()) + "job2Dest"));
        // little safety check
        assertNotEquals(job1Dest, job2Dest);
        // create the second job
        Job job2 = newJob(job2Dest, new org.apache.hadoop.mapred.JobConf(getConfiguration()), attempt20);
        Configuration conf2 = job2.getConfiguration();
        conf2.setInt(APPLICATION_ATTEMPT_ID, 1);
        try {
            JobContext jContext2 = new org.apache.hadoop.mapreduce.task.JobContextImpl(conf2, taskAttempt20.getJobID());
            TaskAttemptContext tContext2 = new TaskAttemptContextImpl(conf2, taskAttempt20);
            AbstractS3ACommitter committer2 = createCommitter(job2Dest, tContext2);
            AbstractITCommitProtocol.JobData jobData2 = new AbstractITCommitProtocol.JobData(job2, jContext2, tContext2, committer2);
            setup(jobData2);
            abortInTeardown(jobData2);
            // make sure the directories are different
            assertEquals(job2Dest, committer2.getOutputPath());
            // job2 setup, write some data there
            writeTextOutput(tContext2);
            // at this point, job1 and job2 both have uncommitted tasks
            // commit tasks in order task 2, task 1.
            committer2.commitTask(tContext2);
            committer1.commitTask(tContext1);
            assertMultipartUploadsPending(job1Dest);
            assertMultipartUploadsPending(job2Dest);
            // commit jobs in order job 1, job 2
            committer1.commitJob(jContext1);
            assertNoMultipartUploadsPending(job1Dest);
            getPart0000(job1Dest);
            assertMultipartUploadsPending(job2Dest);
            committer2.commitJob(jContext2);
            getPart0000(job2Dest);
            assertNoMultipartUploadsPending(job2Dest);
        } finally {
            // uncommitted files to this path need to be deleted in tests which fail
            abortMultipartUploadsUnderPath(job2Dest);
        }
    }

    @Test
    public void testS3ACommitterFactoryBinding() throws Throwable {
        describe(("Verify that the committer factory returns this " + "committer when configured to do so"));
        Job job = newJob();
        FileOutputFormat.setOutputPath(job, outDir);
        Configuration conf = job.getConfiguration();
        conf.set(TASK_ATTEMPT_ID, attempt0);
        conf.setInt(APPLICATION_ATTEMPT_ID, 1);
        TaskAttemptContext tContext = new TaskAttemptContextImpl(conf, taskAttempt0);
        String name = getCommitterName();
        S3ACommitterFactory factory = new S3ACommitterFactory();
        assertEquals("Wrong committer from factory", createCommitter(outDir, tContext).getClass(), factory.createOutputCommitter(outDir, tContext).getClass());
    }
}

