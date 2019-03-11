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


import ExecuteStreamCommand.ARG_DELIMITER;
import ExecuteStreamCommand.EXECUTION_ARGUMENTS;
import ExecuteStreamCommand.EXECUTION_COMMAND;
import ExecuteStreamCommand.IGNORE_STDIN;
import ExecuteStreamCommand.NONZERO_STATUS_RELATIONSHIP;
import ExecuteStreamCommand.ORIGINAL_RELATIONSHIP;
import ExecuteStreamCommand.OUTPUT_STREAM_RELATIONSHIP;
import ExecuteStreamCommand.PUT_ATTRIBUTE_MAX_LENGTH;
import ExecuteStreamCommand.PUT_OUTPUT_IN_ATTRIBUTE;
import ExecuteStreamCommand.WORKING_DIR;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.nifi.processors.standard.util.ArgumentUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestExecuteStreamCommand {
    @Test
    public void testExecuteJar() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/TestSuccess.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(OUTPUT_STREAM_RELATIONSHIP);
        MockFlowFile outputFlowFile = flowFiles.get(0);
        byte[] byteArray = outputFlowFile.toByteArray();
        String result = new String(byteArray);
        Assert.assertTrue(Pattern.compile("Test was a success\r?\n").matcher(result).find());
        Assert.assertEquals("0", outputFlowFile.getAttribute("execution.status"));
        Assert.assertEquals("java", outputFlowFile.getAttribute("execution.command"));
        Assert.assertEquals("-jar;", outputFlowFile.getAttribute("execution.command.args").substring(0, 5));
        String attribute = outputFlowFile.getAttribute("execution.command.args");
        String expected = ((((((("src" + (File.separator)) + "test") + (File.separator)) + "resources") + (File.separator)) + "ExecuteCommand") + (File.separator)) + "TestSuccess.jar";
        Assert.assertEquals(expected, attribute.substring(((attribute.length()) - (expected.length()))));
        MockFlowFile originalFlowFile = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP).get(0);
        Assert.assertEquals(outputFlowFile.getAttribute("execution.status"), originalFlowFile.getAttribute("execution.status"));
        Assert.assertEquals(outputFlowFile.getAttribute("execution.command"), originalFlowFile.getAttribute("execution.command"));
        Assert.assertEquals(outputFlowFile.getAttribute("execution.command.args"), originalFlowFile.getAttribute("execution.command.args"));
    }

    @Test
    public void testExecuteJarWithBadPath() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/noSuchFile.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        controller.assertTransferCount(NONZERO_STATUS_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(NONZERO_STATUS_RELATIONSHIP);
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertEquals(0, flowFile.getSize());
        Assert.assertEquals("Error: Unable to access jarfile", flowFile.getAttribute("execution.error").substring(0, 31));
        Assert.assertTrue(flowFile.isPenalized());
    }

    @Test
    public void testExecuteIngestAndUpdate() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestIngestAndUpdate.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        File dummy10MBytes = new File("target/10MB.txt");
        try (FileOutputStream fos = new FileOutputStream(dummy10MBytes)) {
            byte[] bytes = Files.readAllBytes(dummy.toPath());
            Assert.assertEquals(1000, bytes.length);
            for (int i = 0; i < 10000; i++) {
                fos.write(bytes, 0, 1000);
            }
        }
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy10MBytes.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(OUTPUT_STREAM_RELATIONSHIP);
        byte[] byteArray = flowFiles.get(0).toByteArray();
        String result = new String(byteArray);
        Assert.assertTrue(Pattern.compile("nifi-standard-processors:ModifiedResult\r?\n").matcher(result).find());
    }

    @Test
    public void testLoggingToStdErr() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestLogStdErr.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1mb.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.setValidateExpressionUsage(false);
        controller.enqueue(dummy.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(OUTPUT_STREAM_RELATIONSHIP);
        MockFlowFile flowFile = flowFiles.get(0);
        Assert.assertEquals(0, flowFile.getSize());
        Assert.assertEquals("fffffffffffffffffffffffffffffff", flowFile.getAttribute("execution.error").substring(0, 31));
    }

    @Test
    public void testExecuteIngestAndUpdateWithWorkingDir() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestIngestAndUpdate.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(WORKING_DIR, "target");
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(OUTPUT_STREAM_RELATIONSHIP);
        byte[] byteArray = flowFiles.get(0).toByteArray();
        String result = new String(byteArray);
        final String quotedSeparator = Pattern.quote(File.separator);
        Assert.assertTrue(Pattern.compile((((quotedSeparator + "nifi-standard-processors") + quotedSeparator) + "target:ModifiedResult\r?\n")).matcher(result).find());
    }

    @Test
    public void testIgnoredStdin() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestIngestAndUpdate.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(WORKING_DIR, "target");
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(IGNORE_STDIN, "true");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(OUTPUT_STREAM_RELATIONSHIP);
        byte[] byteArray = flowFiles.get(0).toByteArray();
        String result = new String(byteArray);
        Assert.assertTrue("TestIngestAndUpdate.jar should not have received anything to modify", Pattern.compile("target:ModifiedResult\r?\n$").matcher(result).find());
    }

    @Test
    public void testDynamicEnvironment() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/TestDynamicEnvironment.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.setProperty("NIFI_TEST_1", "testvalue1");
        controller.setProperty("NIFI_TEST_2", "testvalue2");
        controller.enqueue(dummy.toPath());
        controller.setProperty(WORKING_DIR, "target");
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(OUTPUT_STREAM_RELATIONSHIP);
        byte[] byteArray = flowFiles.get(0).toByteArray();
        String result = new String(byteArray);
        Set<String> dynamicEnvironmentVariables = new HashSet<>(Arrays.asList(result.split("\r?\n")));
        Assert.assertFalse("Should contain at least two environment variables starting with NIFI", ((dynamicEnvironmentVariables.size()) < 2));
        Assert.assertTrue("NIFI_TEST_1 environment variable is missing", dynamicEnvironmentVariables.contains("NIFI_TEST_1=testvalue1"));
        Assert.assertTrue("NIFI_TEST_2 environment variable is missing", dynamicEnvironmentVariables.contains("NIFI_TEST_2=testvalue2"));
    }

    @Test
    public void testSmallEchoPutToAttribute() throws Exception {
        File dummy = new File("src/test/resources/hello.txt");
        Assert.assertTrue(dummy.exists());
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue("".getBytes());
        if (TestExecuteStreamCommand.isWindows()) {
            controller.setProperty(EXECUTION_COMMAND, "cmd.exe");
            controller.setProperty(EXECUTION_ARGUMENTS, "/c;echo Hello");
            controller.setProperty(ARG_DELIMITER, ";");
        } else {
            controller.setProperty(EXECUTION_COMMAND, "echo");
            controller.setProperty(EXECUTION_ARGUMENTS, "Hello");
        }
        controller.setProperty(IGNORE_STDIN, "true");
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "executeStreamCommand.output");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        MockFlowFile outputFlowFile = flowFiles.get(0);
        outputFlowFile.assertContentEquals("");
        String ouput = outputFlowFile.getAttribute("executeStreamCommand.output");
        Assert.assertTrue(ouput.startsWith("Hello"));
        Assert.assertEquals("0", outputFlowFile.getAttribute("execution.status"));
        Assert.assertEquals((TestExecuteStreamCommand.isWindows() ? "cmd.exe" : "echo"), outputFlowFile.getAttribute("execution.command"));
    }

    @Test
    public void testExecuteJarPutToAttribute() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/TestSuccess.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "executeStreamCommand.output");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        MockFlowFile outputFlowFile = flowFiles.get(0);
        String result = outputFlowFile.getAttribute("executeStreamCommand.output");
        outputFlowFile.assertContentEquals(dummy);
        Assert.assertTrue(Pattern.compile("Test was a success\r?\n").matcher(result).find());
        Assert.assertEquals("0", outputFlowFile.getAttribute("execution.status"));
        Assert.assertEquals("java", outputFlowFile.getAttribute("execution.command"));
        Assert.assertEquals("-jar;", outputFlowFile.getAttribute("execution.command.args").substring(0, 5));
        String attribute = outputFlowFile.getAttribute("execution.command.args");
        String expected = ((((((("src" + (File.separator)) + "test") + (File.separator)) + "resources") + (File.separator)) + "ExecuteCommand") + (File.separator)) + "TestSuccess.jar";
        Assert.assertEquals(expected, attribute.substring(((attribute.length()) - (expected.length()))));
    }

    @Test
    public void testExecuteJarToAttributeConfiguration() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/TestSuccess.jar");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue("small test".getBytes());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(PUT_ATTRIBUTE_MAX_LENGTH, "10");
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "outputDest");
        Assert.assertEquals(1, controller.getProcessContext().getAvailableRelationships().size());
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        controller.assertTransferCount(NONZERO_STATUS_RELATIONSHIP, 0);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        MockFlowFile outputFlowFile = flowFiles.get(0);
        outputFlowFile.assertContentEquals("small test".getBytes());
        String result = outputFlowFile.getAttribute("outputDest");
        Assert.assertTrue(Pattern.compile("Test was a").matcher(result).find());
        Assert.assertEquals("0", outputFlowFile.getAttribute("execution.status"));
        Assert.assertEquals("java", outputFlowFile.getAttribute("execution.command"));
        Assert.assertEquals("-jar;", outputFlowFile.getAttribute("execution.command.args").substring(0, 5));
        String attribute = outputFlowFile.getAttribute("execution.command.args");
        String expected = ((((((("src" + (File.separator)) + "test") + (File.separator)) + "resources") + (File.separator)) + "ExecuteCommand") + (File.separator)) + "TestSuccess.jar";
        Assert.assertEquals(expected, attribute.substring(((attribute.length()) - (expected.length()))));
    }

    @Test
    public void testExecuteIngestAndUpdatePutToAttribute() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestIngestAndUpdate.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        File dummy10MBytes = new File("target/10MB.txt");
        byte[] bytes = Files.readAllBytes(dummy.toPath());
        try (FileOutputStream fos = new FileOutputStream(dummy10MBytes)) {
            for (int i = 0; i < 10000; i++) {
                fos.write(bytes, 0, 1000);
            }
        }
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy10MBytes.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "outputDest");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        String result = flowFiles.get(0).getAttribute("outputDest");
        Assert.assertTrue(Pattern.compile("nifi-standard-processors:ModifiedResult\r?\n").matcher(result).find());
    }

    @Test
    public void testLargePutToAttribute() throws IOException {
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        File dummy10MBytes = new File("target/10MB.txt");
        byte[] bytes = Files.readAllBytes(dummy.toPath());
        try (FileOutputStream fos = new FileOutputStream(dummy10MBytes)) {
            for (int i = 0; i < 10000; i++) {
                fos.write(bytes, 0, 1000);
            }
        }
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue("".getBytes());
        if (TestExecuteStreamCommand.isWindows()) {
            controller.setProperty(EXECUTION_COMMAND, "cmd.exe");
            controller.setProperty(EXECUTION_ARGUMENTS, ("/c;type " + (dummy10MBytes.getAbsolutePath())));
            controller.setProperty(ARG_DELIMITER, ";");
        } else {
            controller.setProperty(EXECUTION_COMMAND, "cat");
            controller.setProperty(EXECUTION_ARGUMENTS, dummy10MBytes.getAbsolutePath());
        }
        controller.setProperty(IGNORE_STDIN, "true");
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "executeStreamCommand.output");
        controller.setProperty(PUT_ATTRIBUTE_MAX_LENGTH, "256");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        flowFiles.get(0).assertAttributeEquals("execution.status", "0");
        String result = flowFiles.get(0).getAttribute("executeStreamCommand.output");
        Assert.assertTrue(Pattern.compile("a{256}").matcher(result).matches());
    }

    @Test
    public void testExecuteIngestAndUpdateWithWorkingDirPutToAttribute() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestIngestAndUpdate.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(WORKING_DIR, "target");
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "streamOutput");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        String result = flowFiles.get(0).getAttribute("streamOutput");
        final String quotedSeparator = Pattern.quote(File.separator);
        Assert.assertTrue(Pattern.compile((((quotedSeparator + "nifi-standard-processors") + quotedSeparator) + "target:ModifiedResult\r?\n")).matcher(result).find());
    }

    @Test
    public void testIgnoredStdinPutToAttribute() throws IOException {
        File exJar = new File("src/test/resources/ExecuteCommand/TestIngestAndUpdate.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(WORKING_DIR, "target");
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(IGNORE_STDIN, "true");
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "executeStreamCommand.output");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        String result = flowFiles.get(0).getAttribute("executeStreamCommand.output");
        Assert.assertTrue("TestIngestAndUpdate.jar should not have received anything to modify", Pattern.compile("target:ModifiedResult\r?\n?").matcher(result).find());
    }

    @Test
    public void testDynamicEnvironmentPutToAttribute() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/TestDynamicEnvironment.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.setProperty("NIFI_TEST_1", "testvalue1");
        controller.setProperty("NIFI_TEST_2", "testvalue2");
        controller.enqueue(dummy.toPath());
        controller.setProperty(WORKING_DIR, "target");
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "executeStreamCommand.output");
        controller.run(1);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        String result = flowFiles.get(0).getAttribute("executeStreamCommand.output");
        Set<String> dynamicEnvironmentVariables = new HashSet<>(Arrays.asList(result.split("\r?\n")));
        Assert.assertFalse("Should contain at least two environment variables starting with NIFI", ((dynamicEnvironmentVariables.size()) < 2));
        Assert.assertTrue("NIFI_TEST_1 environment variable is missing", dynamicEnvironmentVariables.contains("NIFI_TEST_1=testvalue1"));
        Assert.assertTrue("NIFI_TEST_2 environment variable is missing", dynamicEnvironmentVariables.contains("NIFI_TEST_2=testvalue2"));
    }

    @Test
    public void testQuotedArguments() throws Exception {
        List<String> args = ArgumentUtils.splitArgs("echo -n \"arg1 arg2 arg3\"", ' ');
        Assert.assertEquals(3, args.size());
        args = ArgumentUtils.splitArgs("echo;-n;\"arg1 arg2 arg3\"", ';');
        Assert.assertEquals(3, args.size());
    }

    @Test
    public void testInvalidDelimiter() throws Exception {
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.setProperty(EXECUTION_COMMAND, "echo");
        controller.assertValid();
        controller.setProperty(ARG_DELIMITER, "foo");
        controller.assertNotValid();
        controller.setProperty(ARG_DELIMITER, "f");
        controller.assertValid();
    }

    @Test
    public void testExecuteJarPutToAttributeBadPath() throws Exception {
        File exJar = new File("src/test/resources/ExecuteCommand/noSuchFile.jar");
        File dummy = new File("src/test/resources/ExecuteCommand/1000bytes.txt");
        String jarPath = exJar.getAbsolutePath();
        exJar.setExecutable(true);
        final TestRunner controller = TestRunners.newTestRunner(ExecuteStreamCommand.class);
        controller.enqueue(dummy.toPath());
        controller.setProperty(EXECUTION_COMMAND, "java");
        controller.setProperty(EXECUTION_ARGUMENTS, ("-jar;" + jarPath));
        controller.setProperty(PUT_OUTPUT_IN_ATTRIBUTE, "executeStreamCommand.output");
        controller.run(1);
        controller.assertTransferCount(OUTPUT_STREAM_RELATIONSHIP, 0);
        controller.assertTransferCount(NONZERO_STATUS_RELATIONSHIP, 0);
        controller.assertTransferCount(ORIGINAL_RELATIONSHIP, 1);
        List<MockFlowFile> flowFiles = controller.getFlowFilesForRelationship(ORIGINAL_RELATIONSHIP);
        MockFlowFile outputFlowFile = flowFiles.get(0);
        String result = outputFlowFile.getAttribute("executeStreamCommand.output");
        outputFlowFile.assertContentEquals(dummy);
        Assert.assertTrue(result.isEmpty());// java -jar with bad path only prints to standard error not standard out

        Assert.assertEquals("1", outputFlowFile.getAttribute("execution.status"));// java -jar with bad path exits with code 1

        Assert.assertEquals("java", outputFlowFile.getAttribute("execution.command"));
        Assert.assertEquals("-jar;", outputFlowFile.getAttribute("execution.command.args").substring(0, 5));
        String attribute = outputFlowFile.getAttribute("execution.command.args");
        String expected = ((((((("src" + (File.separator)) + "test") + (File.separator)) + "resources") + (File.separator)) + "ExecuteCommand") + (File.separator)) + "noSuchFile.jar";
        Assert.assertEquals(expected, attribute.substring(((attribute.length()) - (expected.length()))));
    }
}

