/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.fs.s3a.auth.delegation;


import WordCount.IntSumReducer;
import WordCount.TokenizerMapper;
import java.util.Collection;
import java.util.Objects;
import org.apache.hadoop.examples.WordCount;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.S3ATestConstants;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.MockJob;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.v2.MiniMRYarnCluster;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Submit a job with S3 delegation tokens.
 *
 * YARN will not collect DTs unless it is running secure, and turning
 * security on complicates test setup "significantly".
 * Specifically: buts of MR refuse to work on a local FS unless the
 * native libraries are loaded and it can use lower level POSIX APIs
 * for creating files and directories with specific permissions.
 * In production, this is a good thing. In tests, this is not.
 *
 * To address this, Job to YARN communications are mocked.
 * The client-side job submission is as normal, but the implementation
 * of org.apache.hadoop.mapreduce.protocol.ClientProtocol is mock.
 *
 * It's still an ITest though, as it does use S3A as the source and
 * dest so as to collect URLs.
 */
@RunWith(Parameterized.class)
public class ITestDelegatedMRJob extends AbstractDelegationIT {
    private static final Logger LOG = LoggerFactory.getLogger(ITestDelegatedMRJob.class);

    /**
     * Created in static {@link #setupCluster()} call.
     */
    @SuppressWarnings("StaticNonFinalField")
    private static MiniKerberizedHadoopCluster cluster;

    private final String name;

    private final String tokenBinding;

    private final Text tokenKind;

    /**
     * Created in test setup.
     */
    private MiniMRYarnCluster yarn;

    private Path destPath;

    public ITestDelegatedMRJob(String name, String tokenBinding, Text tokenKind) {
        this.name = name;
        this.tokenBinding = tokenBinding;
        this.tokenKind = tokenKind;
    }

    @Test
    public void testJobSubmissionCollectsTokens() throws Exception {
        describe("Mock Job test");
        JobConf conf = new JobConf(getConfiguration());
        // the input here is the landsat file; which lets
        // us differentiate source URI from dest URI
        Path input = new Path(S3ATestConstants.DEFAULT_CSVTEST_FILE);
        final FileSystem sourceFS = input.getFileSystem(conf);
        // output is in the writable test FS.
        final S3AFileSystem fs = getFileSystem();
        destPath = path(getMethodName());
        fs.delete(destPath, true);
        fs.mkdirs(destPath);
        Path output = new Path(destPath, "output/");
        output = output.makeQualified(fs.getUri(), fs.getWorkingDirectory());
        MockJob job = new MockJob(conf, "word count");
        setJarByClass(WordCount.class);
        setMapperClass(TokenizerMapper.class);
        setCombinerClass(IntSumReducer.class);
        setReducerClass(IntSumReducer.class);
        setOutputKeyClass(Text.class);
        setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        setMaxMapAttempts(1);
        setMaxReduceAttempts(1);
        describe("Executing Mock Job Submission to %s", output);
        submit();
        final JobStatus status = getStatus();
        assertEquals("not a mock job", MockJob.NAME, status.getSchedulingInfo());
        assertEquals("Job State", JobStatus.State.RUNNING, status.getState());
        final Credentials submittedCredentials = Objects.requireNonNull(job.getSubmittedCredentials(), "job submitted credentials");
        final Collection<Token<? extends TokenIdentifier>> tokens = submittedCredentials.getAllTokens();
        // log all the tokens for debugging failed test runs
        ITestDelegatedMRJob.LOG.info("Token Count = {}", tokens.size());
        for (Token<? extends TokenIdentifier> token : tokens) {
            ITestDelegatedMRJob.LOG.info("{}", token);
        }
        // verify the source token exists
        AbstractDelegationIT.lookupToken(submittedCredentials, sourceFS.getUri(), tokenKind);
        // look up the destination token
        AbstractDelegationIT.lookupToken(submittedCredentials, fs.getUri(), tokenKind);
    }
}

