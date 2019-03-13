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
package org.apache.hadoop.hbase.io.hfile;


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test {@link HFileScanner#seekTo(Cell)} and its variants.
 */
@Category({ IOTests.class, SmallTests.class })
@RunWith(Parameterized.class)
public class TestSeekTo {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSeekTo.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private final DataBlockEncoding encoding;

    static boolean switchKVs = false;

    public TestSeekTo(DataBlockEncoding encoding) {
        this.encoding = encoding;
    }

    @Test
    public void testSeekBefore() throws Exception {
        testSeekBeforeInternals(TagUsage.NO_TAG);
        testSeekBeforeInternals(TagUsage.ONLY_TAG);
        testSeekBeforeInternals(TagUsage.PARTIAL_TAG);
    }

    @Test
    public void testSeekBeforeWithReSeekTo() throws Exception {
        testSeekBeforeWithReSeekToInternals(TagUsage.NO_TAG);
        testSeekBeforeWithReSeekToInternals(TagUsage.ONLY_TAG);
        testSeekBeforeWithReSeekToInternals(TagUsage.PARTIAL_TAG);
    }

    @Test
    public void testSeekTo() throws Exception {
        testSeekToInternals(TagUsage.NO_TAG);
        testSeekToInternals(TagUsage.ONLY_TAG);
        testSeekToInternals(TagUsage.PARTIAL_TAG);
    }

    @Test
    public void testBlockContainingKey() throws Exception {
        testBlockContainingKeyInternals(TagUsage.NO_TAG);
        testBlockContainingKeyInternals(TagUsage.ONLY_TAG);
        testBlockContainingKeyInternals(TagUsage.PARTIAL_TAG);
    }
}

