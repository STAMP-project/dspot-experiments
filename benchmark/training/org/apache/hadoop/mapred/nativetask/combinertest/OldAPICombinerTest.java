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
package org.apache.hadoop.mapred.nativetask.combinertest;


import TaskCounter.REDUCE_INPUT_RECORDS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.mapred.nativetask.testutil.ResultVerifier;
import org.apache.hadoop.mapred.nativetask.testutil.ScenarioConfiguration;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.apache.hadoop.mapreduce.Counter;
import org.junit.Assert;
import org.junit.Test;


public class OldAPICombinerTest {
    private FileSystem fs;

    private String inputpath;

    @Test
    public void testWordCountCombinerWithOldAPI() throws Exception {
        final Configuration nativeConf = ScenarioConfiguration.getNativeConfiguration();
        nativeConf.addResource(TestConstants.COMBINER_CONF_PATH);
        final String nativeoutput = TestConstants.NATIVETASK_OLDAPI_COMBINER_TEST_NATIVE_OUTPUTPATH;
        final JobConf nativeJob = OldAPICombinerTest.getOldAPIJobconf(nativeConf, "nativeCombinerWithOldAPI", inputpath, nativeoutput);
        RunningJob nativeRunning = JobClient.runJob(nativeJob);
        Counter nativeReduceGroups = nativeRunning.getCounters().findCounter(REDUCE_INPUT_RECORDS);
        final Configuration normalConf = ScenarioConfiguration.getNormalConfiguration();
        normalConf.addResource(TestConstants.COMBINER_CONF_PATH);
        final String normaloutput = TestConstants.NATIVETASK_OLDAPI_COMBINER_TEST_NORMAL_OUTPUTPATH;
        final JobConf normalJob = OldAPICombinerTest.getOldAPIJobconf(normalConf, "normalCombinerWithOldAPI", inputpath, normaloutput);
        RunningJob normalRunning = JobClient.runJob(normalJob);
        Counter normalReduceGroups = normalRunning.getCounters().findCounter(REDUCE_INPUT_RECORDS);
        final boolean compareRet = ResultVerifier.verify(nativeoutput, normaloutput);
        Assert.assertEquals("file compare result: if they are the same ,then return true", true, compareRet);
        Assert.assertEquals("The input reduce record count must be same", nativeReduceGroups.getValue(), normalReduceGroups.getValue());
    }
}

