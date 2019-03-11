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
package org.apache.hadoop.mapred.nativetask.kvtest;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.nativetask.testutil.ResultVerifier;
import org.apache.hadoop.mapred.nativetask.testutil.ScenarioConfiguration;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
public class KVTest {
    private static final Logger LOG = LoggerFactory.getLogger(KVTest.class);

    private static Configuration nativekvtestconf = ScenarioConfiguration.getNativeConfiguration();

    private static Configuration hadoopkvtestconf = ScenarioConfiguration.getNormalConfiguration();

    static {
        KVTest.nativekvtestconf.addResource(TestConstants.KVTEST_CONF_PATH);
        KVTest.hadoopkvtestconf.addResource(TestConstants.KVTEST_CONF_PATH);
    }

    private final Class<?> keyclass;

    private final Class<?> valueclass;

    public KVTest(Class<?> keyclass, Class<?> valueclass) {
        this.keyclass = keyclass;
        this.valueclass = valueclass;
    }

    @Test
    public void testKVCompability() throws Exception {
        final FileSystem fs = FileSystem.get(KVTest.nativekvtestconf);
        final String jobName = (("Test:" + (keyclass.getSimpleName())) + "--") + (valueclass.getSimpleName());
        final String inputPath = ((((TestConstants.NATIVETASK_KVTEST_INPUTDIR) + "/") + (keyclass.getName())) + "/") + (valueclass.getName());
        final String nativeOutputPath = ((((TestConstants.NATIVETASK_KVTEST_NATIVE_OUTPUTDIR) + "/") + (keyclass.getName())) + "/") + (valueclass.getName());
        // if output file exists ,then delete it
        fs.delete(new Path(nativeOutputPath), true);
        KVTest.nativekvtestconf.set(TestConstants.NATIVETASK_KVTEST_CREATEFILE, "true");
        final KVJob nativeJob = new KVJob(jobName, KVTest.nativekvtestconf, keyclass, valueclass, inputPath, nativeOutputPath);
        Assert.assertTrue("job should complete successfully", nativeJob.runJob());
        final String normalOutputPath = ((((TestConstants.NATIVETASK_KVTEST_NORMAL_OUTPUTDIR) + "/") + (keyclass.getName())) + "/") + (valueclass.getName());
        // if output file exists ,then delete it
        fs.delete(new Path(normalOutputPath), true);
        KVTest.hadoopkvtestconf.set(TestConstants.NATIVETASK_KVTEST_CREATEFILE, "false");
        final KVJob normalJob = new KVJob(jobName, KVTest.hadoopkvtestconf, keyclass, valueclass, inputPath, normalOutputPath);
        Assert.assertTrue("job should complete successfully", normalJob.runJob());
        final boolean compareRet = ResultVerifier.verify(normalOutputPath, nativeOutputPath);
        Assert.assertEquals("job output not the same", true, compareRet);
        ResultVerifier.verifyCounters(normalJob.job, nativeJob.job);
        fs.close();
    }
}

