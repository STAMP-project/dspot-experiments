/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.source;


import ExecSourceConfigurationConstants.CONFIG_BATCH_SIZE;
import ExecSourceConfigurationConstants.CONFIG_BATCH_TIME_OUT;
import ExecSourceConfigurationConstants.CONFIG_RESTART;
import ExecSourceConfigurationConstants.CONFIG_RESTART_THROTTLE;
import com.google.common.base.Charsets;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.regex.Pattern;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.flume.Channel;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.apache.flume.lifecycle.LifecycleException;
import org.junit.Assert;
import org.junit.Test;


public class TestExecSource {
    private AbstractSource source;

    private Channel channel = new MemoryChannel();

    private Context context = new Context();

    private ChannelSelector rcs = new ReplicatingChannelSelector();

    @Test
    public void testProcess() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        // Generates a random files for input\output
        File inputFile = File.createTempFile("input", null);
        File ouputFile = File.createTempFile("ouput", null);
        FileUtils.forceDeleteOnExit(inputFile);
        FileUtils.forceDeleteOnExit(ouputFile);
        // Generates input file with a random data set (10 lines, 200 characters each)
        FileOutputStream outputStream1 = new FileOutputStream(inputFile);
        for (int i = 0; i < 10; i++) {
            outputStream1.write(RandomStringUtils.randomAlphanumeric(200).getBytes());
            outputStream1.write('\n');
        }
        outputStream1.close();
        String command = (SystemUtils.IS_OS_WINDOWS) ? String.format("cmd /c type %s", inputFile.getAbsolutePath()) : String.format("cat %s", inputFile.getAbsolutePath());
        context.put("command", command);
        context.put("keep-alive", "1");
        context.put("capacity", "1000");
        context.put("transactionCapacity", "1000");
        Configurables.configure(source, context);
        source.start();
        Thread.sleep(2000);
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        Event event;
        FileOutputStream outputStream = new FileOutputStream(ouputFile);
        while ((event = channel.take()) != null) {
            outputStream.write(event.getBody());
            outputStream.write('\n');
        } 
        outputStream.close();
        transaction.commit();
        transaction.close();
        Assert.assertEquals(FileUtils.checksumCRC32(inputFile), FileUtils.checksumCRC32(ouputFile));
    }

    @Test
    public void testShellCommandSimple() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        if (SystemUtils.IS_OS_WINDOWS) {
            runTestShellCmdHelper("powershell -ExecutionPolicy Unrestricted -command", "1..5", new String[]{ "1", "2", "3", "4", "5" });
        } else {
            runTestShellCmdHelper("/bin/bash -c", "seq 5", new String[]{ "1", "2", "3", "4", "5" });
        }
    }

    @Test
    public void testShellCommandBackTicks() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        // command with backticks
        if (SystemUtils.IS_OS_WINDOWS) {
            runTestShellCmdHelper("powershell -ExecutionPolicy Unrestricted -command", "$(1..5)", new String[]{ "1", "2", "3", "4", "5" });
        } else {
            runTestShellCmdHelper("/bin/bash -c", "echo `seq 5`", new String[]{ "1 2 3 4 5" });
            runTestShellCmdHelper("/bin/bash -c", "echo $(seq 5)", new String[]{ "1 2 3 4 5" });
        }
    }

    @Test
    public void testShellCommandComplex() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        // command with wildcards & pipes
        String[] expected = new String[]{ "1234", "abcd", "ijk", "xyz", "zzz" };
        // pipes
        if (SystemUtils.IS_OS_WINDOWS) {
            runTestShellCmdHelper("powershell -ExecutionPolicy Unrestricted -command", "'zzz','1234','xyz','abcd','ijk' | sort", expected);
        } else {
            runTestShellCmdHelper("/bin/bash -c", "echo zzz 1234 xyz abcd ijk | xargs -n1 echo | sort -f", expected);
        }
    }

    @Test
    public void testShellCommandScript() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        // mini script
        if (SystemUtils.IS_OS_WINDOWS) {
            runTestShellCmdHelper("powershell -ExecutionPolicy Unrestricted -command", "foreach ($i in 1..5) { $i }", new String[]{ "1", "2", "3", "4", "5" });
            // shell arithmetic
            runTestShellCmdHelper("powershell -ExecutionPolicy Unrestricted -command", "if(2+2 -gt 3) { 'good' } else { 'not good' } ", new String[]{ "good" });
        } else {
            runTestShellCmdHelper("/bin/bash -c", "for i in {1..5}; do echo $i;done", new String[]{ "1", "2", "3", "4", "5" });
            // shell arithmetic
            runTestShellCmdHelper("/bin/bash -c", ("if ((2+2>3)); " + "then  echo good; else echo not good; fi"), new String[]{ "good" });
        }
    }

    @Test
    public void testShellCommandEmbeddingAndEscaping() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        // mini script
        String fileName = (SystemUtils.IS_OS_WINDOWS) ? "src\\test\\resources\\test_command.ps1" : "src/test/resources/test_command.txt";
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        try {
            String shell = (SystemUtils.IS_OS_WINDOWS) ? "powershell -ExecutionPolicy Unrestricted -command" : "/bin/bash -c";
            String command1 = reader.readLine();
            Assert.assertNotNull(command1);
            String[] output1 = new String[]{ "'1'", "\"2\"", "\\3", "\\4" };
            runTestShellCmdHelper(shell, command1, output1);
            String command2 = reader.readLine();
            Assert.assertNotNull(command2);
            String[] output2 = new String[]{ "1", "2", "3", "4", "5" };
            runTestShellCmdHelper(shell, command2, output2);
            String command3 = reader.readLine();
            Assert.assertNotNull(command3);
            String[] output3 = new String[]{ "2", "3", "4", "5", "6" };
            runTestShellCmdHelper(shell, command3, output3);
        } finally {
            reader.close();
        }
    }

    @Test
    public void testMonitoredCounterGroup() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        // mini script
        if (SystemUtils.IS_OS_WINDOWS) {
            runTestShellCmdHelper("powershell -ExecutionPolicy Unrestricted -command", "foreach ($i in 1..5) { $i }", new String[]{ "1", "2", "3", "4", "5" });
        } else {
            runTestShellCmdHelper("/bin/bash -c", "for i in {1..5}; do echo $i;done", new String[]{ "1", "2", "3", "4", "5" });
        }
        ObjectName objName = null;
        try {
            objName = new ObjectName((("org.apache.flume.source" + ":type=") + (source.getName())));
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            String[] strAtts = new String[]{ "Type", "EventReceivedCount", "EventAcceptedCount" };
            AttributeList attrList = mbeanServer.getAttributes(objName, strAtts);
            Assert.assertNotNull(attrList.get(0));
            Assert.assertEquals("Expected Value: Type", "Type", ((Attribute) (attrList.get(0))).getName());
            Assert.assertEquals("Expected Value: SOURCE", "SOURCE", ((Attribute) (attrList.get(0))).getValue());
            Assert.assertNotNull(attrList.get(1));
            Assert.assertEquals("Expected Value: EventReceivedCount", "EventReceivedCount", ((Attribute) (attrList.get(1))).getName());
            Assert.assertEquals("Expected Value: 5", "5", ((Attribute) (attrList.get(1))).getValue().toString());
            Assert.assertNotNull(attrList.get(2));
            Assert.assertEquals("Expected Value: EventAcceptedCount", "EventAcceptedCount", ((Attribute) (attrList.get(2))).getName());
            Assert.assertEquals("Expected Value: 5", "5", ((Attribute) (attrList.get(2))).getValue().toString());
        } catch (Exception ex) {
            System.out.println((("Unable to retreive the monitored counter: " + objName) + (ex.getMessage())));
        }
    }

    @Test
    public void testBatchTimeout() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        String filePath = "/tmp/flume-execsource." + (Thread.currentThread().getId());
        String eventBody = "TestMessage";
        FileOutputStream outputStream = new FileOutputStream(filePath);
        context.put(CONFIG_BATCH_SIZE, "50000");
        context.put(CONFIG_BATCH_TIME_OUT, "750");
        context.put("shell", (SystemUtils.IS_OS_WINDOWS ? "powershell -ExecutionPolicy Unrestricted -command" : "/bin/bash -c"));
        context.put("command", (SystemUtils.IS_OS_WINDOWS ? ("Get-Content " + filePath) + " | Select-Object -Last 10" : "tail -f " + filePath));
        Configurables.configure(source, context);
        source.start();
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        for (int lineNumber = 0; lineNumber < 3; lineNumber++) {
            outputStream.write(eventBody.getBytes());
            outputStream.write(String.valueOf(lineNumber).getBytes());
            outputStream.write('\n');
            outputStream.flush();
        }
        outputStream.close();
        Thread.sleep(1500);
        for (int i = 0; i < 3; i++) {
            Event event = channel.take();
            Assert.assertNotNull(event);
            Assert.assertNotNull(event.getBody());
            Assert.assertEquals((eventBody + (String.valueOf(i))), new String(event.getBody()));
        }
        transaction.commit();
        transaction.close();
        source.stop();
        File file = new File(filePath);
        FileUtils.forceDelete(file);
    }

    @Test
    public void testRestart() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        context.put(CONFIG_RESTART_THROTTLE, "10");
        context.put(CONFIG_RESTART, "true");
        context.put("command", (SystemUtils.IS_OS_WINDOWS ? "cmd /c echo flume" : "echo flume"));
        Configurables.configure(source, context);
        source.start();
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            Event event = channel.take();
            Assert.assertNotNull(event);
            Assert.assertNotNull(event.getBody());
            Assert.assertEquals("flume", new String(event.getBody(), Charsets.UTF_8));
        }
        // ensure restartThrottle was turned down as expected
        Assert.assertTrue((((System.currentTimeMillis()) - start) < 10000L));
        transaction.commit();
        transaction.close();
        source.stop();
    }

    /**
     * Tests to make sure that the shutdown mechanism works. There are races
     * in this test if the system has another sleep command running with the
     * same sleep interval but we pick rarely used sleep times and make an
     * effort to detect if our sleep time is already in use. Note the
     * ps -ef command should work on both macs and linux.
     */
    @Test
    public void testShutdown() throws Exception {
        int seconds = 272;// pick a rare sleep time

        // now find one that is not in use
        boolean searchForCommand = true;
        while (searchForCommand) {
            searchForCommand = false;
            String command = (SystemUtils.IS_OS_WINDOWS) ? "cmd /c sleep " + seconds : "sleep " + seconds;
            String searchTxt = (SystemUtils.IS_OS_WINDOWS) ? "sleep.exe" : ("\b" + command) + "\b";
            Pattern pattern = Pattern.compile(searchTxt);
            for (String line : TestExecSource.exec((SystemUtils.IS_OS_WINDOWS ? "cmd /c tasklist /FI \"SESSIONNAME eq Console\"" : "ps -ef"))) {
                if (pattern.matcher(line).find()) {
                    seconds++;
                    searchForCommand = true;
                    break;
                }
            }
        } 
        // yes in the mean time someone could use our sleep time
        // but this should be a fairly rare scenario
        String command = "sleep " + seconds;
        Pattern pattern = Pattern.compile((("\b" + command) + "\b"));
        context.put(CONFIG_RESTART, "false");
        context.put("command", command);
        Configurables.configure(source, context);
        source.start();
        Thread.sleep(1000L);
        source.stop();
        Thread.sleep(1000L);
        for (String line : TestExecSource.exec((SystemUtils.IS_OS_WINDOWS ? "cmd /c tasklist /FI \"SESSIONNAME eq Console\"" : "ps -ef"))) {
            if (pattern.matcher(line).find()) {
                Assert.fail((("Found [" + line) + "]"));
            }
        }
    }
}

