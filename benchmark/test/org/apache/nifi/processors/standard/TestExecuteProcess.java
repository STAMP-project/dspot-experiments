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


import ExecuteProcess.ATTRIBUTE_COMMAND;
import ExecuteProcess.ATTRIBUTE_COMMAND_ARGS;
import ExecuteProcess.BATCH_DURATION;
import ExecuteProcess.COMMAND;
import ExecuteProcess.COMMAND_ARGUMENTS;
import ExecuteProcess.REDIRECT_ERROR_STREAM;
import ExecuteProcess.REL_SUCCESS;
import ExecuteProcess.WORKING_DIR;
import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processors.standard.util.ArgumentUtils;
import org.apache.nifi.util.LogMessage;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestExecuteProcess {
    @Test
    public void testSplitArgs() {
        final List<String> nullArgs = ArgumentUtils.splitArgs(null, ' ');
        Assert.assertNotNull(nullArgs);
        Assert.assertTrue(nullArgs.isEmpty());
        final List<String> zeroArgs = ArgumentUtils.splitArgs("  ", ' ');
        Assert.assertNotNull(zeroArgs);
        Assert.assertEquals(3, zeroArgs.size());
        String[] expectedArray = new String[]{ "", "", "" };
        Assert.assertArrayEquals(expectedArray, zeroArgs.toArray(new String[0]));
        final List<String> singleArg = ArgumentUtils.splitArgs("    hello   ", ';');
        Assert.assertEquals(1, singleArg.size());
        Assert.assertEquals("    hello   ", singleArg.get(0));
        final List<String> twoArg = ArgumentUtils.splitArgs("   hello ;   good-bye   ", ';');
        Assert.assertEquals(2, twoArg.size());
        Assert.assertEquals("   hello ", twoArg.get(0));
        Assert.assertEquals("   good-bye   ", twoArg.get(1));
        final List<String> oneUnnecessarilyQuotedArg = ArgumentUtils.splitArgs("  \"hello\" ", ';');
        Assert.assertEquals(1, oneUnnecessarilyQuotedArg.size());
        Assert.assertEquals("  hello ", oneUnnecessarilyQuotedArg.get(0));
        final List<String> twoQuotedArg = ArgumentUtils.splitArgs("\"   hello\" \"good   bye\"", ' ');
        Assert.assertEquals(2, twoQuotedArg.size());
        Assert.assertEquals("   hello", twoQuotedArg.get(0));
        Assert.assertEquals("good   bye", twoQuotedArg.get(1));
        final List<String> twoArgOneQuotedPerDelimiterArg = ArgumentUtils.splitArgs("one;two;three\";\"and\";\"half\"", ';');
        Assert.assertEquals(3, twoArgOneQuotedPerDelimiterArg.size());
        Assert.assertEquals("one", twoArgOneQuotedPerDelimiterArg.get(0));
        Assert.assertEquals("two", twoArgOneQuotedPerDelimiterArg.get(1));
        Assert.assertEquals("three;and;half", twoArgOneQuotedPerDelimiterArg.get(2));
        final List<String> twoArgOneWholeQuotedArgOneEmptyArg = ArgumentUtils.splitArgs("one;two;\"three;and;half\";", ';');
        Assert.assertEquals(4, twoArgOneWholeQuotedArgOneEmptyArg.size());
        Assert.assertEquals("one", twoArgOneWholeQuotedArgOneEmptyArg.get(0));
        Assert.assertEquals("two", twoArgOneWholeQuotedArgOneEmptyArg.get(1));
        Assert.assertEquals("three;and;half", twoArgOneWholeQuotedArgOneEmptyArg.get(2));
        Assert.assertEquals("", twoArgOneWholeQuotedArgOneEmptyArg.get(3));
    }

    @Test
    public void validateProcessInterruptOnStop() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setVariable("command", "ping");
        runner.setProperty(COMMAND, "${command}");
        runner.setProperty(COMMAND_ARGUMENTS, "nifi.apache.org");
        runner.setProperty(BATCH_DURATION, "500 millis");
        runner.run();
        Thread.sleep(500);
        ExecuteProcess processor = ((ExecuteProcess) (runner.getProcessor()));
        try {
            Field executorF = ExecuteProcess.class.getDeclaredField("executor");
            executorF.setAccessible(true);
            ExecutorService executor = ((ExecutorService) (executorF.get(processor)));
            Assert.assertTrue(executor.isShutdown());
            Assert.assertTrue(executor.isTerminated());
            Field processF = ExecuteProcess.class.getDeclaredField("externalProcess");
            processF.setAccessible(true);
            Process process = ((Process) (processF.get(processor)));
            Assert.assertFalse(process.isAlive());
        } catch (Exception e) {
            Assert.fail();
        }
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        if (!(flowFiles.isEmpty())) {
            Assert.assertTrue(flowFiles.get(0).getAttribute("command").equals("ping"));
        }
    }

    @Test
    public void testBigInputSplit() {
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi", "TRACE");
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.nifi.processors.standard", "DEBUG");
        String workingDirName = "/var/test";
        String testFile = "Novo_dicion?rio_da_l?ngua_portuguesa_by_C?ndido_de_Figueiredo.txt";
        // String testFile = "eclipse-java-luna-SR2-win32.zip";
        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(COMMAND, "cmd");
        runner.setProperty(COMMAND_ARGUMENTS, (" /c type " + testFile));
        runner.setProperty(WORKING_DIR, workingDirName);
        runner.setProperty(BATCH_DURATION, "150 millis");
        File inFile = new File(workingDirName, testFile);
        System.out.println(inFile.getAbsolutePath());
        // runner.run(1,false,true);
        ProcessContext processContext = runner.getProcessContext();
        ExecuteProcess processor = ((ExecuteProcess) (runner.getProcessor()));
        processor.updateScheduledTrue();
        processor.setupExecutor(processContext);
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        // runner.run(5,true,false);
        final List<MockFlowFile> flowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        long totalFlowFilesSize = 0;
        for (final MockFlowFile flowFile : flowFiles) {
            System.out.println(flowFile);
            totalFlowFilesSize += flowFile.getSize();
            // System.out.println(new String(flowFile.toByteArray()));
        }
        // assertEquals(inFile.length(), totalFlowFilesSize);
    }

    @Test
    public void testNotRedirectErrorStream() {
        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(COMMAND, "cd");
        runner.setProperty(COMMAND_ARGUMENTS, "does-not-exist");
        ProcessContext processContext = runner.getProcessContext();
        ExecuteProcess processor = ((ExecuteProcess) (runner.getProcessor()));
        processor.updateScheduledTrue();
        processor.setupExecutor(processContext);
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        if (isCommandFailed(runner))
            return;

        // ExecuteProcess doesn't wait for finishing to drain error stream if it's configure NOT to redirect stream.
        // This causes test failure when draining the error stream didn't finish
        // fast enough before the thread of this test case method checks the warn msg count.
        // So, this loop wait for a while until the log msg count becomes expected number, otherwise let it fail.
        final int expectedWarningMessages = 1;
        final int maxRetry = 5;
        for (int i = 0; (i < maxRetry) && ((runner.getLogger().getWarnMessages().size()) < expectedWarningMessages); i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }
        final List<LogMessage> warnMessages = runner.getLogger().getWarnMessages();
        Assert.assertEquals(("If redirect error stream is false, " + "the output should be logged as a warning so that user can notice on bulletin."), expectedWarningMessages, warnMessages.size());
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, succeeded.size());
    }

    @Test
    public void testRedirectErrorStream() {
        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(COMMAND, "cd");
        runner.setProperty(COMMAND_ARGUMENTS, "does-not-exist");
        runner.setProperty(REDIRECT_ERROR_STREAM, "true");
        ProcessContext processContext = runner.getProcessContext();
        ExecuteProcess processor = ((ExecuteProcess) (runner.getProcessor()));
        processor.updateScheduledTrue();
        processor.setupExecutor(processContext);
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        if (isCommandFailed(runner))
            return;

        final List<LogMessage> warnMessages = runner.getLogger().getWarnMessages();
        Assert.assertEquals(("If redirect error stream is true " + "the output should be sent as a content of flow-file."), 0, warnMessages.size());
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, succeeded.size());
    }

    @Test
    public void testRedirectErrorStreamWithExpressions() {
        final TestRunner runner = TestRunners.newTestRunner(ExecuteProcess.class);
        runner.setProperty(COMMAND, "ls");
        runner.setProperty(COMMAND_ARGUMENTS, "${literal('does-not-exist'):toUpper()}");
        runner.setProperty(REDIRECT_ERROR_STREAM, "true");
        ProcessContext processContext = runner.getProcessContext();
        ExecuteProcess processor = ((ExecuteProcess) (runner.getProcessor()));
        processor.updateScheduledTrue();
        processor.setupExecutor(processContext);
        processor.onTrigger(processContext, runner.getProcessSessionFactory());
        if (isCommandFailed(runner))
            return;

        final List<LogMessage> warnMessages = runner.getLogger().getWarnMessages();
        Assert.assertEquals(("If redirect error stream is true " + "the output should be sent as a content of flow-file."), 0, warnMessages.size());
        final List<MockFlowFile> succeeded = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, succeeded.size());
        Assert.assertTrue(new String(succeeded.get(0).toByteArray()).contains("DOES-NOT-EXIST"));
        Assert.assertEquals(succeeded.get(0).getAttribute(ATTRIBUTE_COMMAND), "ls");
        Assert.assertEquals(succeeded.get(0).getAttribute(ATTRIBUTE_COMMAND_ARGS), "DOES-NOT-EXIST");
    }
}

