/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import SegmentContent.FRAGMENT_COUNT;
import SegmentContent.FRAGMENT_ID;
import SegmentContent.REL_ORIGINAL;
import SegmentContent.REL_SEGMENTS;
import SegmentContent.SIZE;
import java.io.IOException;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestSegmentContent {
    @Test
    public void test() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new SegmentContent());
        testRunner.setProperty(SIZE, "4 B");
        testRunner.enqueue(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        testRunner.run();
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile originalFlowFile = testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(FRAGMENT_ID);
        originalFlowFile.assertAttributeEquals(FRAGMENT_COUNT, "3");
        final List<MockFlowFile> flowFiles = testRunner.getFlowFilesForRelationship(REL_SEGMENTS);
        Assert.assertEquals(3, flowFiles.size());
        final MockFlowFile out1 = flowFiles.get(0);
        final MockFlowFile out2 = flowFiles.get(1);
        final MockFlowFile out3 = flowFiles.get(2);
        out1.assertContentEquals(new byte[]{ 1, 2, 3, 4 });
        out2.assertContentEquals(new byte[]{ 5, 6, 7, 8 });
        out3.assertContentEquals(new byte[]{ 9 });
    }

    @Test
    public void testTransferSmall() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new SegmentContent());
        testRunner.setProperty(SIZE, "4 KB");
        testRunner.enqueue(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        testRunner.run();
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile originalFlowFile = testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(FRAGMENT_ID);
        originalFlowFile.assertAttributeEquals(FRAGMENT_COUNT, "1");
        testRunner.assertTransferCount(REL_SEGMENTS, 1);
        final MockFlowFile out1 = testRunner.getFlowFilesForRelationship(REL_SEGMENTS).get(0);
        out1.assertContentEquals(new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8, 9 });
    }
}

