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


import MergeContent.MERGE_FORMAT;
import MergeContent.MERGE_FORMAT_CONCAT;
import MergeContent.MERGE_STRATEGY;
import MergeContent.MERGE_STRATEGY_DEFRAGMENT;
import MergeContent.REL_FAILURE;
import MergeContent.REL_MERGED;
import SplitContent.BYTE_SEQUENCE;
import SplitContent.BYTE_SEQUENCE_LOCATION;
import SplitContent.FORMAT;
import SplitContent.KEEP_SEQUENCE;
import SplitContent.LEADING_POSITION;
import SplitContent.REL_ORIGINAL;
import SplitContent.REL_SPLITS;
import SplitContent.TRAILING_POSITION;
import SplitContent.UTF8_FORMAT;
import java.io.IOException;
import java.util.List;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestSplitContent {
    @Test
    public void testTextFormatLeadingPosition() {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(FORMAT, UTF8_FORMAT.getValue());
        runner.setProperty(BYTE_SEQUENCE, "ub");
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, LEADING_POSITION.getValue());
        runner.enqueue("rub-a-dub-dub".getBytes());
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "4");
        runner.assertTransferCount(REL_SPLITS, 4);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("r");
        splits.get(1).assertContentEquals("ub-a-d");
        splits.get(2).assertContentEquals("ub-d");
        splits.get(3).assertContentEquals("ub");
    }

    @Test
    public void testTextFormatSplits() {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(FORMAT, UTF8_FORMAT.getValue());
        runner.setProperty(BYTE_SEQUENCE, "test");
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, LEADING_POSITION.getValue());
        final byte[] input = "This is a test. This is another test. And this is yet another test. Finally this is the last Test.".getBytes();
        runner.enqueue(input);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "4");
        runner.assertTransferCount(REL_SPLITS, 4);
        runner.assertQueueEmpty();
        List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("This is a ");
        splits.get(1).assertContentEquals("test. This is another ");
        splits.get(2).assertContentEquals("test. And this is yet another ");
        splits.get(3).assertContentEquals("test. Finally this is the last Test.");
        runner.clearTransferState();
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.enqueue(input);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "4");
        runner.assertTransferCount(REL_SPLITS, 4);
        splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("This is a ");
        splits.get(1).assertContentEquals(". This is another ");
        splits.get(2).assertContentEquals(". And this is yet another ");
        splits.get(3).assertContentEquals(". Finally this is the last Test.");
        runner.clearTransferState();
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, TRAILING_POSITION.getValue());
        runner.enqueue(input);
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "4");
        runner.assertTransferCount(REL_SPLITS, 4);
        splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("This is a test");
        splits.get(1).assertContentEquals(". This is another test");
        splits.get(2).assertContentEquals(". And this is yet another test");
        splits.get(3).assertContentEquals(". Finally this is the last Test.");
        runner.clearTransferState();
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, TRAILING_POSITION.getValue());
        runner.enqueue("This is a test. This is another test. And this is yet another test. Finally this is the last test".getBytes());
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "4");
        runner.assertTransferCount(REL_SPLITS, 4);
        splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("This is a test");
        splits.get(1).assertContentEquals(". This is another test");
        splits.get(2).assertContentEquals(". And this is yet another test");
        splits.get(3).assertContentEquals(". Finally this is the last test");
        runner.clearTransferState();
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, LEADING_POSITION.getValue());
        runner.enqueue("This is a test. This is another test. And this is yet another test. Finally this is the last test".getBytes());
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "5");
        runner.assertTransferCount(REL_SPLITS, 5);
        splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("This is a ");
        splits.get(1).assertContentEquals("test. This is another ");
        splits.get(2).assertContentEquals("test. And this is yet another ");
        splits.get(3).assertContentEquals("test. Finally this is the last ");
        splits.get(4).assertContentEquals("test");
        runner.clearTransferState();
    }

    @Test
    public void testTextFormatTrailingPosition() {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(FORMAT, UTF8_FORMAT.getValue());
        runner.setProperty(BYTE_SEQUENCE, "ub");
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, TRAILING_POSITION.getValue());
        runner.enqueue("rub-a-dub-dub".getBytes());
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "3");
        runner.assertTransferCount(REL_SPLITS, 3);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals("rub");
        splits.get(1).assertContentEquals("-a-dub");
        splits.get(2).assertContentEquals("-dub");
    }

    @Test
    public void testSmallSplits() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.setProperty(BYTE_SEQUENCE.getName(), "FFFF");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, ((byte) (255)), ((byte) (255)), ((byte) (255)), 5, 4, 3, 2, 1 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "2");
        runner.assertTransferCount(REL_SPLITS, 2);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        final MockFlowFile split2 = splits.get(1);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4, 5 });
        split2.assertContentEquals(new byte[]{ ((byte) (255)), 5, 4, 3, 2, 1 });
    }

    @Test
    public void testWithSingleByteSplit() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.setProperty(BYTE_SEQUENCE.getName(), "FF");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, ((byte) (255)), 5, 4, 3, 2, 1 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "2");
        runner.assertTransferCount(REL_SPLITS, 2);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        final MockFlowFile split2 = splits.get(1);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4, 5 });
        split2.assertContentEquals(new byte[]{ 5, 4, 3, 2, 1 });
    }

    @Test
    public void testWithLargerSplit() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.setProperty(BYTE_SEQUENCE.getName(), "05050505");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 4, 3, 2, 1 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile originalFlowFile = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(SplitContent.FRAGMENT_ID);
        originalFlowFile.assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "2");
        runner.assertTransferCount(REL_SPLITS, 2);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        final MockFlowFile split2 = splits.get(1);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4 });
        split2.assertContentEquals(new byte[]{ 5, 5, 4, 3, 2, 1 });
    }

    @Test
    public void testKeepingSequence() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE.getName(), "05050505");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, 5, 5, 5, 5, 5, 4, 3, 2, 1 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "2");
        runner.assertTransferCount(REL_SPLITS, 2);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        final MockFlowFile split2 = splits.get(1);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4, 5, 5, 5, 5 });
        split2.assertContentEquals(new byte[]{ 5, 5, 4, 3, 2, 1 });
    }

    @Test
    public void testEndsWithSequence() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.setProperty(BYTE_SEQUENCE.getName(), "05050505");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, 5, 5, 5 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "1");
        runner.assertTransferCount(REL_SPLITS, 1);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4 });
    }

    @Test
    public void testEndsWithSequenceAndKeepSequence() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE.getName(), "05050505");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, 5, 5, 5 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "1");
        runner.assertTransferCount(REL_SPLITS, 1);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4, 5, 5, 5, 5 });
    }

    @Test
    public void testStartsWithSequence() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.setProperty(BYTE_SEQUENCE.getName(), "05050505");
        runner.enqueue(new byte[]{ 5, 5, 5, 5, 1, 2, 3, 4 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "1");
        runner.assertTransferCount(REL_SPLITS, 1);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4 });
    }

    @Test
    public void testStartsWithSequenceAndKeepSequence() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE.getName(), "05050505");
        runner.enqueue(new byte[]{ 5, 5, 5, 5, 1, 2, 3, 4 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "2");
        runner.assertTransferCount(REL_SPLITS, 2);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals(new byte[]{ 5, 5, 5, 5 });
        splits.get(1).assertContentEquals(new byte[]{ 1, 2, 3, 4 });
    }

    @Test
    public void testSmallSplitsThenMerge() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(KEEP_SEQUENCE, "true");
        runner.setProperty(BYTE_SEQUENCE.getName(), "FFFF");
        runner.enqueue(new byte[]{ 1, 2, 3, 4, 5, ((byte) (255)), ((byte) (255)), ((byte) (255)), 5, 4, 3, 2, 1 });
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "2");
        runner.assertTransferCount(REL_SPLITS, 2);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        final MockFlowFile split1 = splits.get(0);
        final MockFlowFile split2 = splits.get(1);
        split1.assertContentEquals(new byte[]{ 1, 2, 3, 4, 5, ((byte) (255)), ((byte) (255)) });
        split2.assertContentEquals(new byte[]{ ((byte) (255)), 5, 4, 3, 2, 1 });
        final TestRunner mergeRunner = TestRunners.newTestRunner(new MergeContent());
        mergeRunner.setProperty(MERGE_FORMAT, MERGE_FORMAT_CONCAT);
        mergeRunner.setProperty(MERGE_STRATEGY, MERGE_STRATEGY_DEFRAGMENT);
        mergeRunner.enqueue(splits.toArray(new MockFlowFile[0]));
        mergeRunner.run();
        mergeRunner.assertTransferCount(REL_MERGED, 1);
        mergeRunner.assertTransferCount(MergeContent.REL_ORIGINAL, 2);
        mergeRunner.assertTransferCount(REL_FAILURE, 0);
        final List<MockFlowFile> packed = mergeRunner.getFlowFilesForRelationship(REL_MERGED);
        packed.get(0).assertContentEquals(new byte[]{ 1, 2, 3, 4, 5, ((byte) (255)), ((byte) (255)), ((byte) (255)), 5, 4, 3, 2, 1 });
    }

    @Test
    public void testNoSplitterInString() {
        String content = "UVAT";
        final TestRunner runner = TestRunners.newTestRunner(new SplitContent());
        runner.setProperty(FORMAT, UTF8_FORMAT.getValue());
        runner.setProperty(BYTE_SEQUENCE, ",");
        runner.setProperty(KEEP_SEQUENCE, "false");
        runner.setProperty(BYTE_SEQUENCE_LOCATION, TRAILING_POSITION.getValue());
        runner.enqueue(content.getBytes());
        runner.run();
        runner.assertTransferCount(REL_SPLITS, 1);
        MockFlowFile splitResult = runner.getFlowFilesForRelationship(REL_SPLITS).get(0);
        splitResult.assertAttributeExists(SplitContent.FRAGMENT_ID);
        splitResult.assertAttributeExists(SplitContent.SEGMENT_ORIGINAL_FILENAME);
        splitResult.assertAttributeEquals(SplitContent.FRAGMENT_COUNT, "1");
        splitResult.assertAttributeEquals(SplitContent.FRAGMENT_INDEX, "1");
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertQueueEmpty();
        final List<MockFlowFile> splits = runner.getFlowFilesForRelationship(REL_SPLITS);
        splits.get(0).assertContentEquals(content);
    }
}

