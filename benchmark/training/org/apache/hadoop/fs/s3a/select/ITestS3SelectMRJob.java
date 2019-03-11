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
package org.apache.hadoop.fs.s3a.select;


import StagingCommitter.NAME;
import WordCount.IntSumReducer;
import WordCount.TokenizerMapper;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.examples.WordCount;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.AbstractS3ATestBase;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3AUtils;
import org.apache.hadoop.fs.s3a.commit.CommitConstants;
import org.apache.hadoop.fs.s3a.commit.files.SuccessData;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.DurationInfo;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.junit.Test;


/**
 * Run an MR job with a select query.
 * This is the effective end-to-end test which verifies:
 * <ol>
 *   <li>Passing of select parameters through an MR job conf.</li>
 *   <li>Automatic pick-up of these parameter through TextInputFormat's use
 *   of the mapreduce.lib.input.LineRecordReaderLineRecordReader.</li>
 *   <li>Issuing of S3 Select queries in mapper processes.</li>
 *   <li>Projection of columns in a select.</li>
 *   <li>Ability to switch to the Passthrough decompressor in an MR job.</li>
 *   <li>Saving of results through the S3A Staging committer.</li>
 *   <li>Basic validation of results.</li>
 * </ol>
 * This makes it the most complex of the MR jobs in the hadoop-aws test suite.
 *
 * The query used is
 * {@link ITestS3SelectLandsat#SELECT_PROCESSING_LEVEL_NO_LIMIT},
 * which lists the processing level of all records in the source file,
 * and counts the number in each one by way of the normal word-count
 * routines.
 * This works because the SQL is projecting only the processing level.
 *
 * The result becomes something like (with tabs between fields):
 * <pre>
 * L1GT   370231
 * L1T    689526
 * </pre>
 */
public class ITestS3SelectMRJob extends AbstractS3SelectTest {
    private final Configuration conf = new YarnConfiguration();

    private S3AFileSystem fs;

    private MiniYARNCluster yarnCluster;

    private Path rootPath;

    @Test
    public void testLandsatSelect() throws Exception {
        final Path input = getLandsatGZ();
        final Path output = path("testLandsatSelect").makeQualified(fs.getUri(), fs.getWorkingDirectory());
        final Job job = Job.getInstance(conf, "process level count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        // job with use the staging committer
        final JobConf jobConf = ((JobConf) (job.getConfiguration()));
        jobConf.set(CommitConstants.FS_S3A_COMMITTER_NAME, NAME);
        jobConf.setBoolean(CommitConstants.FS_S3A_COMMITTER_STAGING_UNIQUE_FILENAMES, false);
        final String query = ITestS3SelectLandsat.SELECT_PROCESSING_LEVEL_NO_LIMIT;
        inputMust(jobConf, SELECT_SQL, query);
        inputMust(jobConf, SELECT_INPUT_COMPRESSION, COMPRESSION_OPT_GZIP);
        // input settings
        inputMust(jobConf, SELECT_INPUT_FORMAT, SELECT_FORMAT_CSV);
        inputMust(jobConf, CSV_INPUT_HEADER, CSV_HEADER_OPT_USE);
        // output
        inputMust(jobConf, SELECT_OUTPUT_FORMAT, SELECT_FORMAT_CSV);
        inputMust(jobConf, CSV_OUTPUT_QUOTE_FIELDS, CSV_OUTPUT_QUOTE_FIELDS_AS_NEEEDED);
        // disable the gzip codec, so that the record readers do not
        // get confused
        enablePassthroughCodec(jobConf, ".gz");
        try (DurationInfo ignored = new DurationInfo(AbstractS3ATestBase.LOG, ("SQL " + query))) {
            int exitCode = (job.waitForCompletion(true)) ? 0 : 1;
            assertEquals("Returned error code.", 0, exitCode);
        }
        // log the success info
        Path successPath = new Path(output, "_SUCCESS");
        SuccessData success = SuccessData.load(fs, successPath);
        AbstractS3ATestBase.LOG.info("Job _SUCCESS\n{}", success);
        // process the results by ver
        // 
        AbstractS3ATestBase.LOG.info("Results for query \n{}", query);
        final AtomicLong parts = new AtomicLong(0);
        S3AUtils.applyLocatedFiles(fs.listFiles(output, false), ( status) -> {
            Path path = status.getPath();
            // ignore _SUCCESS, any temp files in subdirectories...
            if (path.getName().startsWith("part-")) {
                parts.incrementAndGet();
                String result = readStringFromFile(path);
                AbstractS3ATestBase.LOG.info("{}\n{}", path, result);
                String[] lines = result.split("\n", (-1));
                int l = lines.length;
                // add a bit of slack here in case some new processing
                // option was added.
                assertTrue(((("Wrong number of lines (" + l) + ") in ") + result), ((l > 0) && (l < 15)));
            }
        });
        assertEquals("More part files created than expected", 1, parts.get());
    }
}

