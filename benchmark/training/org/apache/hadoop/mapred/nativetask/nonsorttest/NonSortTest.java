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
package org.apache.hadoop.mapred.nativetask.nonsorttest;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapred.nativetask.testutil.ResultVerifier;
import org.apache.hadoop.mapred.nativetask.testutil.ScenarioConfiguration;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;


public class NonSortTest {
    @Test
    public void nonSortTest() throws Exception {
        Configuration nativeConf = ScenarioConfiguration.getNativeConfiguration();
        nativeConf.addResource(TestConstants.NONSORT_TEST_CONF);
        nativeConf.set(TestConstants.NATIVETASK_MAP_OUTPUT_SORT, "false");
        final Job nativeNonSort = getJob(nativeConf, "NativeNonSort", TestConstants.NATIVETASK_NONSORT_TEST_INPUTDIR, TestConstants.NATIVETASK_NONSORT_TEST_NATIVE_OUTPUT);
        Assert.assertTrue(nativeNonSort.waitForCompletion(true));
        Configuration normalConf = ScenarioConfiguration.getNormalConfiguration();
        normalConf.addResource(TestConstants.NONSORT_TEST_CONF);
        final Job hadoopWithSort = getJob(normalConf, "NormalJob", TestConstants.NATIVETASK_NONSORT_TEST_INPUTDIR, TestConstants.NATIVETASK_NONSORT_TEST_NORMAL_OUTPUT);
        Assert.assertTrue(hadoopWithSort.waitForCompletion(true));
        final boolean compareRet = ResultVerifier.verify(TestConstants.NATIVETASK_NONSORT_TEST_NATIVE_OUTPUT, TestConstants.NATIVETASK_NONSORT_TEST_NORMAL_OUTPUT);
        Assert.assertEquals("file compare result: if they are the same ,then return true", true, compareRet);
        ResultVerifier.verifyCounters(hadoopWithSort, nativeNonSort);
    }
}

