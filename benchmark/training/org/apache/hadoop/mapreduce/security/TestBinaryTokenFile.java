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
package org.apache.hadoop.mapreduce.security;


import MRJobConfig.JOB_NAMENODES;
import MRJobConfig.MAPREDUCE_JOB_CREDENTIALS_BINARY;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MRJobConfig;
import org.apache.hadoop.mapreduce.SleepJob;
import org.apache.hadoop.mapreduce.v2.MiniMRYarnCluster;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenIdentifier;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestBinaryTokenFile {
    private static final String KEY_SECURITY_TOKEN_FILE_NAME = "key-security-token-file";

    private static final String DELEGATION_TOKEN_KEY = "Hdfs";

    // my sleep class
    static class MySleepMapper extends SleepJob.SleepMapper {
        /**
         * attempts to access tokenCache as from client
         */
        @Override
        public void map(IntWritable key, IntWritable value, Context context) throws IOException, InterruptedException {
            // get context token storage:
            final Credentials contextCredentials = context.getCredentials();
            final Collection<Token<? extends TokenIdentifier>> contextTokenCollection = contextCredentials.getAllTokens();
            for (Token<? extends TokenIdentifier> t : contextTokenCollection) {
                System.out.println((("Context token: [" + t) + "]"));
            }
            if ((contextTokenCollection.size()) != 2) {
                // one job token and one delegation token
                // fail the test:
                throw new RuntimeException(((("Exactly 2 tokens are expected in the contextTokenCollection: " + "one job token and one delegation token, but was found ") + (contextTokenCollection.size())) + " tokens."));
            }
            final Token<? extends TokenIdentifier> dt = contextCredentials.getToken(new Text(TestBinaryTokenFile.DELEGATION_TOKEN_KEY));
            if (dt == null) {
                throw new RuntimeException((("Token for key [" + (TestBinaryTokenFile.DELEGATION_TOKEN_KEY)) + "] not found in the job context."));
            }
            String tokenFile0 = context.getConfiguration().get(MAPREDUCE_JOB_CREDENTIALS_BINARY);
            if (tokenFile0 != null) {
                throw new RuntimeException((("Token file key [" + (MRJobConfig.MAPREDUCE_JOB_CREDENTIALS_BINARY)) + "] found in the configuration. It should have been removed from the configuration."));
            }
            final String tokenFile = context.getConfiguration().get(TestBinaryTokenFile.KEY_SECURITY_TOKEN_FILE_NAME);
            if (tokenFile == null) {
                throw new RuntimeException((("Token file key [" + (TestBinaryTokenFile.KEY_SECURITY_TOKEN_FILE_NAME)) + "] not found in the job configuration."));
            }
            final Credentials binaryCredentials = new Credentials();
            binaryCredentials.readTokenStorageStream(new DataInputStream(new FileInputStream(tokenFile)));
            final Collection<Token<? extends TokenIdentifier>> binaryTokenCollection = binaryCredentials.getAllTokens();
            if ((binaryTokenCollection.size()) != 1) {
                throw new RuntimeException((("The token collection read from file [" + tokenFile) + "] must have size = 1."));
            }
            final Token<? extends TokenIdentifier> binTok = binaryTokenCollection.iterator().next();
            System.out.println((("The token read from binary file: t = [" + binTok) + "]"));
            // Verify that dt is same as the token in the file:
            if (!(dt.equals(binTok))) {
                throw new RuntimeException(((((("Delegation token in job is not same as the token passed in file:" + " tokenInFile=[") + binTok) + "], dt=[") + dt) + "]."));
            }
            // Now test the user tokens.
            final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
            // Print all the UGI tokens for diagnostic purposes:
            final Collection<Token<? extends TokenIdentifier>> ugiTokenCollection = ugi.getTokens();
            for (Token<? extends TokenIdentifier> t : ugiTokenCollection) {
                System.out.println((("UGI token: [" + t) + "]"));
            }
            final Token<? extends TokenIdentifier> ugiToken = ugi.getCredentials().getToken(new Text(TestBinaryTokenFile.DELEGATION_TOKEN_KEY));
            if (ugiToken == null) {
                throw new RuntimeException((("Token for key [" + (TestBinaryTokenFile.DELEGATION_TOKEN_KEY)) + "] not found among the UGI tokens."));
            }
            if (!(ugiToken.equals(binTok))) {
                throw new RuntimeException(((((("UGI token is not same as the token passed in binary file:" + " tokenInBinFile=[") + binTok) + "], ugiTok=[") + ugiToken) + "]."));
            }
            super.map(key, value, context);
        }
    }

    class MySleepJob extends SleepJob {
        @Override
        public Job createJob(int numMapper, int numReducer, long mapSleepTime, int mapSleepCount, long reduceSleepTime, int reduceSleepCount) throws IOException {
            Job job = super.createJob(numMapper, numReducer, mapSleepTime, mapSleepCount, reduceSleepTime, reduceSleepCount);
            job.setMapperClass(TestBinaryTokenFile.MySleepMapper.class);
            // Populate tokens here because security is disabled.
            setupBinaryTokenFile(job);
            return job;
        }

        private void setupBinaryTokenFile(Job job) {
            // Credentials in the job will not have delegation tokens
            // because security is disabled. Fetch delegation tokens
            // and store in binary token file.
            TestBinaryTokenFile.createBinaryTokenFile(job.getConfiguration());
            job.getConfiguration().set(MAPREDUCE_JOB_CREDENTIALS_BINARY, TestBinaryTokenFile.binaryTokenFileName.toString());
            // NB: the MRJobConfig.MAPREDUCE_JOB_CREDENTIALS_BINARY
            // key now gets deleted from config,
            // so it's not accessible in the job's config. So,
            // we use another key to pass the file name into the job configuration:
            job.getConfiguration().set(TestBinaryTokenFile.KEY_SECURITY_TOKEN_FILE_NAME, TestBinaryTokenFile.binaryTokenFileName.toString());
        }
    }

    private static MiniMRYarnCluster mrCluster;

    private static MiniDFSCluster dfsCluster;

    private static final Path TEST_DIR = new Path(System.getProperty("test.build.data", "/tmp"));

    private static final Path binaryTokenFileName = new Path(TestBinaryTokenFile.TEST_DIR, "tokenFile.binary");

    private static final int NUMWORKERS = 1;// num of data nodes


    private static final int noOfNMs = 1;

    private static Path p1;

    /**
     * run a distributed job and verify that TokenCache is available
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testBinaryTokenFile() throws IOException {
        Configuration conf = getConfig();
        // provide namenodes names for the job to get the delegation tokens for
        final String nnUri = TestBinaryTokenFile.dfsCluster.getURI(0).toString();
        conf.set(JOB_NAMENODES, ((nnUri + ",") + nnUri));
        // using argument to pass the file name
        final String[] args = new String[]{ "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(conf, new TestBinaryTokenFile.MySleepJob(), args);
        } catch (Exception e) {
            System.out.println(("Job failed with " + (e.getLocalizedMessage())));
            e.printStackTrace(System.out);
            Assert.fail("Job failed");
        }
        Assert.assertEquals("dist job res is not 0:", 0, res);
    }

    /**
     * run a distributed job with -tokenCacheFile option parameter and
     * verify that no exception happens.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTokenCacheFile() throws IOException {
        Configuration conf = getConfig();
        TestBinaryTokenFile.createBinaryTokenFile(conf);
        // provide namenodes names for the job to get the delegation tokens for
        final String nnUri = TestBinaryTokenFile.dfsCluster.getURI(0).toString();
        conf.set(JOB_NAMENODES, ((nnUri + ",") + nnUri));
        // using argument to pass the file name
        final String[] args = new String[]{ "-tokenCacheFile", TestBinaryTokenFile.binaryTokenFileName.toString(), "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(conf, new SleepJob(), args);
        } catch (Exception e) {
            System.out.println(("Job failed with " + (e.getLocalizedMessage())));
            e.printStackTrace(System.out);
            Assert.fail("Job failed");
        }
        Assert.assertEquals("dist job res is not 0:", 0, res);
    }
}

