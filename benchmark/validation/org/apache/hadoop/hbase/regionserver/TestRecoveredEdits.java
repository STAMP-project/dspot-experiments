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
package org.apache.hadoop.hbase.regionserver;


import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MemoryCompactionPolicy;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests around replay of recovered.edits content.
 */
@Category({ MediumTests.class })
public class TestRecoveredEdits {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRecoveredEdits.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestRecoveredEdits.class);

    private static BlockCache blockCache;

    @Rule
    public TestName testName = new TestName();

    /**
     * HBASE-12782 ITBLL fails for me if generator does anything but 5M per maptask.
     * Create a region. Close it. Then copy into place a file to replay, one that is bigger than
     * configured flush size so we bring on lots of flushes.  Then reopen and confirm all edits
     * made it in.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testReplayWorksThoughLotsOfFlushing() throws IOException {
        for (MemoryCompactionPolicy policy : MemoryCompactionPolicy.values()) {
            testReplayWorksWithMemoryCompactionPolicy(policy);
        }
    }
}

