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


import JobContext.JOB_NAMENODES;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSUtilClient;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRClientCluster;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests whether a protected secret passed from JobClient is
 * available to the child task
 */
public class TestMRCredentials {
    static final int NUM_OF_KEYS = 10;

    private static MiniMRClientCluster mrCluster;

    private static MiniDFSCluster dfsCluster;

    private static int numWorkers = 1;

    private static JobConf jConf;

    /**
     * run a distributed job and verify that TokenCache is available
     *
     * @throws IOException
     * 		
     */
    @Test
    public void test() throws IOException {
        // make sure JT starts
        Configuration jobConf = new JobConf(TestMRCredentials.mrCluster.getConfig());
        // provide namenodes names for the job to get the delegation tokens for
        NameNode nn = TestMRCredentials.dfsCluster.getNameNode();
        URI nnUri = DFSUtilClient.getNNUri(nn.getNameNodeAddress());
        jobConf.set(JOB_NAMENODES, ((nnUri + ",") + (nnUri.toString())));
        jobConf.set("mapreduce.job.credentials.json", "keys.json");
        // using argument to pass the file name
        String[] args = new String[]{ "-m", "1", "-r", "1", "-mt", "1", "-rt", "1" };
        int res = -1;
        try {
            res = ToolRunner.run(jobConf, new CredentialsTestJob(), args);
        } catch (Exception e) {
            System.out.println(("Job failed with" + (e.getLocalizedMessage())));
            e.printStackTrace(System.out);
            Assert.fail("Job failed");
        }
        Assert.assertEquals("dist job res is not 0", res, 0);
    }
}

