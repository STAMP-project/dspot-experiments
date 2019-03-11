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


import FetchFile.COMPLETION_DELETE;
import FetchFile.COMPLETION_MOVE;
import FetchFile.COMPLETION_NONE;
import FetchFile.COMPLETION_STRATEGY;
import FetchFile.CONFLICT_FAIL;
import FetchFile.CONFLICT_KEEP_INTACT;
import FetchFile.CONFLICT_RENAME;
import FetchFile.CONFLICT_REPLACE;
import FetchFile.CONFLICT_STRATEGY;
import FetchFile.FILENAME;
import FetchFile.MOVE_DESTINATION_DIR;
import FetchFile.REL_FAILURE;
import FetchFile.REL_NOT_FOUND;
import FetchFile.REL_SUCCESS;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestFetchFile {
    @Test
    public void notFound() throws IOException {
        final File sourceFile = new File("notFound");
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_NONE.getValue());
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
    }

    @Test
    public void testSimpleSuccess() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_NONE.getValue());
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(content);
        Assert.assertTrue(sourceFile.exists());
    }

    @Test
    public void testDeleteOnComplete() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_DELETE.getValue());
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(content);
        Assert.assertFalse(sourceFile.exists());
    }

    @Test
    public void testMoveOnCompleteWithTargetDirExisting() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.assertValid();
        final File destDir = new File("target/move-target");
        destDir.mkdirs();
        Assert.assertTrue(destDir.exists());
        final File destFile = new File(destDir, sourceFile.getName());
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(content);
        Assert.assertFalse(sourceFile.exists());
        Assert.assertTrue(destFile.exists());
    }

    @Test
    public void testMoveOnCompleteWithTargetDirMissing() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.assertValid();
        final File destDir = new File("target/move-target");
        if (destDir.exists()) {
            destDir.delete();
        }
        Assert.assertFalse(destDir.exists());
        final File destFile = new File(destDir, sourceFile.getName());
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(content);
        Assert.assertFalse(sourceFile.exists());
        Assert.assertTrue(destFile.exists());
    }

    @Test
    public void testMoveOnCompleteWithTargetExistsButNotWritable() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.assertValid();
        final File destDir = new File("target/move-target");
        if (!(destDir.exists())) {
            destDir.mkdirs();
        }
        destDir.setWritable(false);
        Assert.assertTrue(destDir.exists());
        Assert.assertFalse(destDir.canWrite());
        final File destFile = new File(destDir, sourceFile.getName());
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        runner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals("");
        Assert.assertTrue(sourceFile.exists());
        Assert.assertFalse(destFile.exists());
    }

    @Test
    public void testMoveOnCompleteWithParentOfTargetDirNotAccessible() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final String moveTargetParent = "target/fetch-file";
        final String moveTarget = moveTargetParent + "/move-target";
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, moveTarget);
        runner.assertValid();
        // Make the parent of move-target non-writable and non-readable
        final File moveTargetParentDir = new File(moveTargetParent);
        moveTargetParentDir.mkdirs();
        moveTargetParentDir.setReadable(false);
        moveTargetParentDir.setWritable(false);
        try {
            runner.enqueue(new byte[0]);
            runner.run();
            runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
            runner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals("");
            Assert.assertTrue(sourceFile.exists());
        } finally {
            moveTargetParentDir.setReadable(true);
            moveTargetParentDir.setWritable(true);
        }
    }

    @Test
    public void testMoveAndReplace() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.setProperty(CONFLICT_STRATEGY, CONFLICT_REPLACE.getValue());
        runner.assertValid();
        final File destDir = new File("target/move-target");
        final File destFile = new File(destDir, sourceFile.getName());
        Files.write(destFile.toPath(), "Good-bye".getBytes(), StandardOpenOption.CREATE);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(content);
        final byte[] replacedContent = Files.readAllBytes(destFile.toPath());
        Assert.assertTrue(Arrays.equals(content, replacedContent));
        Assert.assertFalse(sourceFile.exists());
        Assert.assertTrue(destFile.exists());
    }

    @Test
    public void testMoveAndKeep() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.setProperty(CONFLICT_STRATEGY, CONFLICT_KEEP_INTACT.getValue());
        runner.assertValid();
        final File destDir = new File("target/move-target");
        final File destFile = new File(destDir, sourceFile.getName());
        final byte[] goodBye = "Good-bye".getBytes();
        Files.write(destFile.toPath(), goodBye);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(content);
        final byte[] replacedContent = Files.readAllBytes(destFile.toPath());
        Assert.assertTrue(Arrays.equals(goodBye, replacedContent));
        Assert.assertFalse(sourceFile.exists());
        Assert.assertTrue(destFile.exists());
    }

    @Test
    public void testMoveAndFail() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.setProperty(CONFLICT_STRATEGY, CONFLICT_FAIL.getValue());
        runner.assertValid();
        final File destDir = new File("target/move-target");
        final File destFile = new File(destDir, sourceFile.getName());
        final byte[] goodBye = "Good-bye".getBytes();
        Files.write(destFile.toPath(), goodBye);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final byte[] replacedContent = Files.readAllBytes(destFile.toPath());
        Assert.assertTrue(Arrays.equals(goodBye, replacedContent));
        Assert.assertTrue(sourceFile.exists());
        Assert.assertTrue(destFile.exists());
    }

    @Test
    public void testMoveAndRename() throws IOException {
        final File sourceFile = new File("target/1.txt");
        final byte[] content = "Hello, World!".getBytes();
        Files.write(sourceFile.toPath(), content, StandardOpenOption.CREATE);
        final TestRunner runner = TestRunners.newTestRunner(new FetchFile());
        runner.setProperty(FILENAME, sourceFile.getAbsolutePath());
        runner.setProperty(COMPLETION_STRATEGY, COMPLETION_MOVE.getValue());
        runner.assertNotValid();
        runner.setProperty(MOVE_DESTINATION_DIR, "target/move-target");
        runner.setProperty(CONFLICT_STRATEGY, CONFLICT_RENAME.getValue());
        runner.assertValid();
        final File destDir = new File("target/move-target");
        final File destFile = new File(destDir, sourceFile.getName());
        final byte[] goodBye = "Good-bye".getBytes();
        Files.write(destFile.toPath(), goodBye);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final byte[] replacedContent = Files.readAllBytes(destFile.toPath());
        Assert.assertTrue(Arrays.equals(goodBye, replacedContent));
        Assert.assertFalse(sourceFile.exists());
        Assert.assertTrue(destFile.exists());
        Assert.assertEquals(2, destDir.list().length);
    }
}

