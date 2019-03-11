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
package org.apache.hadoop.streaming;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.mapred.ClusterMapReduceTestCase;
import org.apache.hadoop.mapred.SkipBadRecords;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestStreamingBadRecords extends ClusterMapReduceTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestStreamingBadRecords.class);

    private static final List<String> MAPPER_BAD_RECORDS = Arrays.asList("hey022", "hey023", "hey099");

    private static final List<String> REDUCER_BAD_RECORDS = Arrays.asList("hey001", "hey018");

    private static final String badMapper = UtilTest.makeJavaCommand(TestStreamingBadRecords.BadApp.class, new String[]{  });

    private static final String badReducer = UtilTest.makeJavaCommand(TestStreamingBadRecords.BadApp.class, new String[]{ "true" });

    private static final int INPUTSIZE = 100;

    public TestStreamingBadRecords() throws IOException {
        UtilTest utilTest = new UtilTest(getClass().getName());
        utilTest.checkUserDir();
        utilTest.redirectIfAntJunit();
    }

    /* Disable test as skipping bad records not supported in 0.23 */
    /* public void testSkip() throws Exception {
    JobConf clusterConf = createJobConf();
    createInput();
    int attSkip =0;
    SkipBadRecords.setAttemptsToStartSkipping(clusterConf,attSkip);
    //the no of attempts to successfully complete the task depends 
    //on the no of bad records.
    int mapperAttempts = attSkip+1+MAPPER_BAD_RECORDS.size();
    int reducerAttempts = attSkip+1+REDUCER_BAD_RECORDS.size();

    String[] args =  new String[] {
    "-input", (new Path(getInputDir(), "text.txt")).toString(),
    "-output", getOutputDir().toString(),
    "-mapper", badMapper,
    "-reducer", badReducer,
    "-verbose",
    "-inputformat", "org.apache.hadoop.mapred.KeyValueTextInputFormat",
    "-jobconf", "mapreduce.task.skip.start.attempts="+attSkip,
    "-jobconf", "mapreduce.job.skip.outdir=none",
    "-jobconf", "mapreduce.map.maxattempts="+mapperAttempts,
    "-jobconf", "mapreduce.reduce.maxattempts="+reducerAttempts,
    "-jobconf", "mapreduce.map.skip.maxrecords="+Long.MAX_VALUE,
    "-jobconf", "mapreduce.reduce.skip.maxgroups="+Long.MAX_VALUE,
    "-jobconf", "mapreduce.job.maps=1",
    "-jobconf", "mapreduce.job.reduces=1",
    "-jobconf", "fs.default.name="+clusterConf.get("fs.default.name"),
    "-jobconf", "mapreduce.jobtracker.address=" + 
    clusterConf.get(JTConfig.JT_IPC_ADDRESS),
    "-jobconf", "mapreduce.jobtracker.http.address="
    +clusterConf.get(JTConfig.JT_HTTP_ADDRESS),
    "-jobconf", "mapreduce.task.files.preserve.failedtasks=true",
    "-jobconf", "stream.tmpdir="+System.getProperty("test.build.data","/tmp"),
    "-jobconf", "mapred.jar=" + TestStreaming.STREAMING_JAR,
    "-jobconf", "mapreduce.framework.name=yarn"
    };
    StreamJob job = new StreamJob(args, false);      
    job.go();
    validateOutput(job.running_, false);
    //validate that there is no skip directory as it has been set to "none"
    assertTrue(SkipBadRecords.getSkipOutputPath(job.jobConf_)==null);
    }
     */
    /* Disable test as skipping bad records not supported in 0.23 */
    /* public void testNarrowDown() throws Exception {
    createInput();
    JobConf clusterConf = createJobConf();
    String[] args =  new String[] {
    "-input", (new Path(getInputDir(), "text.txt")).toString(),
    "-output", getOutputDir().toString(),
    "-mapper", badMapper,
    "-reducer", badReducer,
    "-verbose",
    "-inputformat", "org.apache.hadoop.mapred.KeyValueTextInputFormat",
    "-jobconf", "mapreduce.task.skip.start.attempts=1",
    //actually fewer attempts are required than specified
    //but to cater to the case of slow processed counter update, need to 
    //have more attempts
    "-jobconf", "mapreduce.map.maxattempts=20",
    "-jobconf", "mapreduce.reduce.maxattempts=15",
    "-jobconf", "mapreduce.map.skip.maxrecords=1",
    "-jobconf", "mapreduce.reduce.skip.maxgroups=1",
    "-jobconf", "mapreduce.job.maps=1",
    "-jobconf", "mapreduce.job.reduces=1",
    "-jobconf", "fs.default.name="+clusterConf.get("fs.default.name"),
    "-jobconf", "mapreduce.jobtracker.address="+clusterConf.get(JTConfig.JT_IPC_ADDRESS),
    "-jobconf", "mapreduce.jobtracker.http.address="
    +clusterConf.get(JTConfig.JT_HTTP_ADDRESS),
    "-jobconf", "mapreduce.task.files.preserve.failedtasks=true",
    "-jobconf", "stream.tmpdir="+System.getProperty("test.build.data","/tmp"),
    "-jobconf", "mapred.jar=" + TestStreaming.STREAMING_JAR,
    "-jobconf", "mapreduce.framework.name=yarn"
    };
    StreamJob job = new StreamJob(args, false);      
    job.go();

    validateOutput(job.running_, true);
    assertTrue(SkipBadRecords.getSkipOutputPath(job.jobConf_)!=null);
    }
     */
    @Test
    public void testNoOp() {
        // Added to avoid warnings when running this disabled test
    }

    static class App {
        boolean isReducer;

        public App(String[] args) throws Exception {
            if ((args.length) > 0) {
                isReducer = Boolean.parseBoolean(args[0]);
            }
            String counter = SkipBadRecords.COUNTER_MAP_PROCESSED_RECORDS;
            if (isReducer) {
                counter = SkipBadRecords.COUNTER_REDUCE_PROCESSED_GROUPS;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            String line;
            int count = 0;
            while ((line = in.readLine()) != null) {
                processLine(line);
                count++;
                if (count >= 10) {
                    System.err.println(((((("reporter:counter:" + (SkipBadRecords.COUNTER_GROUP)) + ",") + counter) + ",") + count));
                    count = 0;
                }
            } 
        }

        protected void processLine(String line) throws Exception {
            System.out.println(line);
        }

        public static void main(String[] args) throws Exception {
            new TestStreamingBadRecords.App(args);
        }
    }

    static class BadApp extends TestStreamingBadRecords.App {
        public BadApp(String[] args) throws Exception {
            super(args);
        }

        protected void processLine(String line) throws Exception {
            List<String> badRecords = TestStreamingBadRecords.MAPPER_BAD_RECORDS;
            if (isReducer) {
                badRecords = TestStreamingBadRecords.REDUCER_BAD_RECORDS;
            }
            if (((badRecords.size()) > 0) && (line.contains(badRecords.get(0)))) {
                TestStreamingBadRecords.LOG.warn("Encountered BAD record");
                System.exit((-1));
            } else
                if (((badRecords.size()) > 1) && (line.contains(badRecords.get(1)))) {
                    TestStreamingBadRecords.LOG.warn("Encountered BAD record");
                    throw new Exception("Got bad record..crashing");
                } else
                    if (((badRecords.size()) > 2) && (line.contains(badRecords.get(2)))) {
                        TestStreamingBadRecords.LOG.warn("Encountered BAD record");
                        System.exit((-1));
                    }


            super.processLine(line);
        }

        public static void main(String[] args) throws Exception {
            new TestStreamingBadRecords.BadApp(args);
        }
    }
}

