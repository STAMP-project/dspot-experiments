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


import CommitConstants.S3A_COMMITTER_FACTORY;
import JobConf.MAPRED_MAP_TASK_JAVA_OPTS;
import S3AUtils.HIDDEN_FILE_FILTER;
import com.google.common.collect.Sets;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3ATestConstants;
import org.apache.hadoop.fs.s3a.commit.files.SuccessData;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.v2.MiniMRYarnCluster;
import org.apache.hadoop.util.DurationInfo;
import org.apache.log4j.BasicConfigurator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.fs.s3a.commit.CommitConstants.CommitConstants.TEMPORARY;


/**
 * Full integration test of an MR job.
 */
public abstract class AbstractITCommitMRJob extends AbstractCommitITest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractITCommitMRJob.class);

    private static final int TEST_FILE_COUNT = 2;

    private static final int SCALE_TEST_FILE_COUNT = 20;

    private static MiniDFSClusterService hdfs;

    private static MiniMRYarnCluster yarn = null;

    private static JobConf conf = null;

    private boolean uniqueFilenames = false;

    private boolean scaleTest;

    @Rule
    public final TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void testMRJob() throws Exception {
        S3AFileSystem fs = getFileSystem();
        // final dest is in S3A
        Path outputPath = path("testMRJob");
        String commitUUID = UUID.randomUUID().toString();
        String suffix = (uniqueFilenames) ? "-" + commitUUID : "";
        int numFiles = getTestFileCount();
        List<String> expectedFiles = new ArrayList<>(numFiles);
        Set<String> expectedKeys = Sets.newHashSet();
        for (int i = 0; i < numFiles; i += 1) {
            File file = temp.newFile(((String.valueOf(i)) + ".text"));
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(("file " + i).getBytes(StandardCharsets.UTF_8));
            }
            String filename = String.format("part-m-%05d%s", i, suffix);
            Path path = new Path(outputPath, filename);
            expectedFiles.add(path.toString());
            expectedKeys.add(("/" + (fs.pathToKey(path))));
        }
        Collections.sort(expectedFiles);
        Job mrJob = Job.getInstance(AbstractITCommitMRJob.yarn.getConfig(), "test-committer-job");
        JobConf jobConf = ((JobConf) (mrJob.getConfiguration()));
        jobConf.setBoolean(FS_S3A_COMMITTER_STAGING_UNIQUE_FILENAMES, uniqueFilenames);
        bindCommitter(jobConf, S3A_COMMITTER_FACTORY, committerName());
        // pass down the scale test flag
        jobConf.setBoolean(S3ATestConstants.KEY_SCALE_TESTS_ENABLED, scaleTest);
        mrJob.setOutputFormatClass(LoggingTextOutputFormat.class);
        FileOutputFormat.setOutputPath(mrJob, outputPath);
        File mockResultsFile = temp.newFile("committer.bin");
        mockResultsFile.delete();
        String committerPath = "file:" + mockResultsFile;
        jobConf.set("mock-results-file", committerPath);
        jobConf.set(FS_S3A_COMMITTER_STAGING_UUID, commitUUID);
        mrJob.setInputFormatClass(TextInputFormat.class);
        FileInputFormat.addInputPath(mrJob, new Path(temp.getRoot().toURI()));
        mrJob.setMapperClass(AbstractITCommitMRJob.MapClass.class);
        mrJob.setNumReduceTasks(0);
        // an attempt to set up log4j properly, which clearly doesn't work
        URL log4j = getClass().getClassLoader().getResource("log4j.properties");
        if ((log4j != null) && (log4j.getProtocol().equals("file"))) {
            Path log4jPath = new Path(log4j.toURI());
            AbstractITCommitMRJob.LOG.debug("Using log4j path {}", log4jPath);
            mrJob.addFileToClassPath(log4jPath);
            String sysprops = String.format("-Xmx256m -Dlog4j.configuration=%s", log4j);
            jobConf.set(MAPRED_MAP_TASK_JAVA_OPTS, sysprops);
            jobConf.set("yarn.app.mapreduce.am.command-opts", sysprops);
        }
        applyCustomConfigOptions(jobConf);
        // fail fast if anything goes wrong
        mrJob.setMaxMapAttempts(1);
        mrJob.submit();
        try (DurationInfo d = new DurationInfo(AbstractITCommitMRJob.LOG, "Job Execution")) {
            boolean succeeded = mrJob.waitForCompletion(true);
            assertTrue("MR job failed", succeeded);
        }
        waitForConsistency();
        assertIsDirectory(outputPath);
        FileStatus[] results = fs.listStatus(outputPath, HIDDEN_FILE_FILTER);
        int fileCount = results.length;
        List<String> actualFiles = new ArrayList<>(fileCount);
        assertTrue("No files in output directory", (fileCount != 0));
        AbstractITCommitMRJob.LOG.info("Found {} files", fileCount);
        for (FileStatus result : results) {
            AbstractITCommitMRJob.LOG.debug("result: {}", result);
            actualFiles.add(result.getPath().toString());
        }
        Collections.sort(actualFiles);
        // load in the success data marker: this guarantees that a s3guard
        // committer was used
        Path success = new Path(outputPath, _SUCCESS);
        FileStatus status = fs.getFileStatus(success);
        assertTrue(("0 byte success file - not a s3guard committer " + success), ((status.getLen()) > 0));
        SuccessData successData = SuccessData.load(fs, success);
        String commitDetails = successData.toString();
        AbstractITCommitMRJob.LOG.info((("Committer name " + (committerName())) + "\n{}"), commitDetails);
        AbstractITCommitMRJob.LOG.info("Committer statistics: \n{}", successData.dumpMetrics("  ", " = ", "\n"));
        AbstractITCommitMRJob.LOG.info("Diagnostics\n{}", successData.dumpDiagnostics("  ", " = ", "\n"));
        assertEquals(("Wrong committer in " + commitDetails), committerName(), successData.getCommitter());
        List<String> successFiles = successData.getFilenames();
        assertTrue(("No filenames in " + commitDetails), (!(successFiles.isEmpty())));
        assertEquals("Should commit the expected files", expectedFiles, actualFiles);
        Set<String> summaryKeys = Sets.newHashSet();
        summaryKeys.addAll(successFiles);
        assertEquals(("Summary keyset doesn't list the the expected paths " + commitDetails), expectedKeys, summaryKeys);
        assertPathDoesNotExist("temporary dir", new Path(outputPath, TEMPORARY));
        customPostExecutionValidation(outputPath, successData);
    }

    /**
     * Test Mapper.
     *  This is executed in separate process, and must not make any assumptions
     *  about external state.
     */
    public static class MapClass extends Mapper<LongWritable, Text, LongWritable, Text> {
        private int operations;

        private String id = "";

        private LongWritable l = new LongWritable();

        private Text t = new Text();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            super.setup(context);
            // force in Log4J logging
            BasicConfigurator.configure();
            boolean scaleMap = context.getConfiguration().getBoolean(S3ATestConstants.KEY_SCALE_TESTS_ENABLED, false);
            operations = (scaleMap) ? 1000 : 10;
            id = context.getTaskAttemptID().toString();
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            for (int i = 0; i < (operations); i++) {
                l.set(i);
                t.set(String.format("%s:%05d", id, i));
                context.write(l, t);
            }
        }
    }
}

