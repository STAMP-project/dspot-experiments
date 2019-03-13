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
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.nativetask.testutil.ScenarioConfiguration;
import org.apache.hadoop.mapred.nativetask.testutil.TestConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LargeKVTest {
    private static final Logger LOG = LoggerFactory.getLogger(LargeKVTest.class);

    private static Configuration nativeConf = ScenarioConfiguration.getNativeConfiguration();

    private static Configuration normalConf = ScenarioConfiguration.getNormalConfiguration();

    static {
        LargeKVTest.nativeConf.addResource(TestConstants.KVTEST_CONF_PATH);
        LargeKVTest.nativeConf.set(TestConstants.NATIVETASK_KVTEST_CREATEFILE, "true");
        LargeKVTest.normalConf.addResource(TestConstants.KVTEST_CONF_PATH);
        LargeKVTest.normalConf.set(TestConstants.NATIVETASK_KVTEST_CREATEFILE, "false");
    }

    @Test
    public void testKeySize() throws Exception {
        runKVSizeTests(Text.class, IntWritable.class);
    }

    @Test
    public void testValueSize() throws Exception {
        runKVSizeTests(IntWritable.class, Text.class);
    }
}

