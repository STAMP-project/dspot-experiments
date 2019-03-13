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


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapred.nativetask.testutil.ResultVerifier;
import org.apache.hadoop.mapred.nativetask.testutil.ScenarioConfiguration;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;


public class CombinerTest {
    private FileSystem fs;

    private String inputpath;

    private String nativeoutputpath;

    private String hadoopoutputpath;

    @Test
    public void testWordCountCombiner() throws Exception {
        final Configuration nativeConf = ScenarioConfiguration.getNativeConfiguration();
        nativeConf.addResource(TestConstants.COMBINER_CONF_PATH);
        final Job nativejob = CombinerTest.getJob("nativewordcount", nativeConf, inputpath, nativeoutputpath);
        final Configuration commonConf = ScenarioConfiguration.getNormalConfiguration();
        commonConf.addResource(TestConstants.COMBINER_CONF_PATH);
        final Job normaljob = CombinerTest.getJob("normalwordcount", commonConf, inputpath, hadoopoutputpath);
        Assert.assertTrue(nativejob.waitForCompletion(true));
        Assert.assertTrue(normaljob.waitForCompletion(true));
        Assert.assertEquals(true, ResultVerifier.verify(nativeoutputpath, hadoopoutputpath));
        ResultVerifier.verifyCounters(normaljob, nativejob, true);
    }
}

