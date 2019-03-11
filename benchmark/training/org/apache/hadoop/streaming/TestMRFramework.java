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


import JTConfig.JT_IPC_ADDRESS;
import MRConfig.CLASSIC_FRAMEWORK_NAME;
import MRConfig.FRAMEWORK_NAME;
import MRConfig.LOCAL_FRAMEWORK_NAME;
import MRConfig.YARN_FRAMEWORK_NAME;
import org.apache.hadoop.mapred.JobConf;
import org.junit.Assert;
import org.junit.Test;


public class TestMRFramework {
    @Test
    public void testFramework() {
        JobConf jobConf = new JobConf();
        jobConf.set(JT_IPC_ADDRESS, LOCAL_FRAMEWORK_NAME);
        jobConf.set(FRAMEWORK_NAME, YARN_FRAMEWORK_NAME);
        Assert.assertFalse("Expected 'isLocal' to be false", StreamUtil.isLocalJobTracker(jobConf));
        jobConf.set(JT_IPC_ADDRESS, LOCAL_FRAMEWORK_NAME);
        jobConf.set(FRAMEWORK_NAME, CLASSIC_FRAMEWORK_NAME);
        Assert.assertFalse("Expected 'isLocal' to be false", StreamUtil.isLocalJobTracker(jobConf));
        jobConf.set(JT_IPC_ADDRESS, "jthost:9090");
        jobConf.set(FRAMEWORK_NAME, LOCAL_FRAMEWORK_NAME);
        Assert.assertTrue("Expected 'isLocal' to be true", StreamUtil.isLocalJobTracker(jobConf));
    }
}

