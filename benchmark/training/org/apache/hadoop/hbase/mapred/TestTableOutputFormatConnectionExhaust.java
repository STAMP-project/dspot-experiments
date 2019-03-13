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
package org.apache.hadoop.hbase.mapred;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Spark creates many instances of TableOutputFormat within a single process.  We need to make
 * sure we can have many instances and not leak connections.
 *
 * This test creates a few TableOutputFormats and shouldn't fail due to ZK connection exhaustion.
 */
@Category(MediumTests.class)
public class TestTableOutputFormatConnectionExhaust {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTableOutputFormatConnectionExhaust.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTableOutputFormatConnectionExhaust.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    static final String TABLE = "TestTableOutputFormatConnectionExhaust";

    static final String FAMILY = "family";

    @Test
    public void testConnectionExhaustion() throws IOException {
        int MAX_INSTANCES = 5;// fails on iteration 3 if zk connections leak

        for (int i = 0; i < MAX_INSTANCES; i++) {
            final int iter = i;
            try {
                TestTableOutputFormatConnectionExhaust.openCloseTableOutputFormat(iter);
            } catch (Exception e) {
                TestTableOutputFormatConnectionExhaust.LOG.error("Exception encountered", e);
                Assert.fail(("Failed on iteration " + i));
            }
        }
    }
}

