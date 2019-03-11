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
package org.apache.rocketmq.tools.command.message;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.srvutil.ServerUtil;
import org.apache.rocketmq.tools.command.SubCommandException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ConsumeMessageCommandTest {
    private static ConsumeMessageCommand consumeMessageCommand;

    @Test
    public void testExecuteDefault() throws SubCommandException {
        PrintStream out = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        String[] subargs = new String[]{ "-t mytopic", "-n localhost:9876" };
        CommandLine commandLine = ServerUtil.parseCmdLine(("mqadmin " + (ConsumeMessageCommandTest.consumeMessageCommand.commandName())), subargs, ConsumeMessageCommandTest.consumeMessageCommand.buildCommandlineOptions(options), new PosixParser());
        ConsumeMessageCommandTest.consumeMessageCommand.execute(commandLine, options, null);
        System.setOut(out);
        String s = new String(bos.toByteArray());
        Assert.assertTrue(s.contains("Consume ok"));
    }

    @Test
    public void testExecuteByCondition() throws SubCommandException {
        PrintStream out = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        String[] subargs = new String[]{ "-t mytopic", "-b localhost", "-i 0", "-n localhost:9876" };
        CommandLine commandLine = ServerUtil.parseCmdLine(("mqadmin " + (ConsumeMessageCommandTest.consumeMessageCommand.commandName())), subargs, ConsumeMessageCommandTest.consumeMessageCommand.buildCommandlineOptions(options), new PosixParser());
        ConsumeMessageCommandTest.consumeMessageCommand.execute(commandLine, options, null);
        System.setOut(out);
        String s = new String(bos.toByteArray());
        Assert.assertTrue(s.contains("Consume ok"));
    }

    @Test
    public void testExecuteDefaultWhenPullMessageByQueueGotException() throws IllegalAccessException, InterruptedException, NoSuchFieldException, MQBrokerException, MQClientException, RemotingException, SubCommandException {
        DefaultMQPullConsumer defaultMQPullConsumer = Mockito.mock(DefaultMQPullConsumer.class);
        Mockito.when(defaultMQPullConsumer.pull(ArgumentMatchers.any(MessageQueue.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenThrow(Exception.class);
        Field producerField = ConsumeMessageCommand.class.getDeclaredField("defaultMQPullConsumer");
        producerField.setAccessible(true);
        producerField.set(ConsumeMessageCommandTest.consumeMessageCommand, defaultMQPullConsumer);
        PrintStream out = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        String[] subargs = new String[]{ "-t topic-not-existu", "-n localhost:9876" };
        CommandLine commandLine = ServerUtil.parseCmdLine(("mqadmin " + (ConsumeMessageCommandTest.consumeMessageCommand.commandName())), subargs, ConsumeMessageCommandTest.consumeMessageCommand.buildCommandlineOptions(options), new PosixParser());
        ConsumeMessageCommandTest.consumeMessageCommand.execute(commandLine, options, null);
        System.setOut(out);
        String s = new String(bos.toByteArray());
        Assert.assertTrue((!(s.contains("Consume ok"))));
    }

    @Test
    public void testExecuteByConditionWhenPullMessageByQueueGotException() throws IllegalAccessException, InterruptedException, NoSuchFieldException, MQBrokerException, MQClientException, RemotingException, SubCommandException {
        DefaultMQPullConsumer defaultMQPullConsumer = Mockito.mock(DefaultMQPullConsumer.class);
        Mockito.when(defaultMQPullConsumer.pull(ArgumentMatchers.any(MessageQueue.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenThrow(Exception.class);
        Field producerField = ConsumeMessageCommand.class.getDeclaredField("defaultMQPullConsumer");
        producerField.setAccessible(true);
        producerField.set(ConsumeMessageCommandTest.consumeMessageCommand, defaultMQPullConsumer);
        PrintStream out = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        String[] subargs = new String[]{ "-t mytopic", "-b localhost", "-i 0", "-n localhost:9876" };
        CommandLine commandLine = ServerUtil.parseCmdLine(("mqadmin " + (ConsumeMessageCommandTest.consumeMessageCommand.commandName())), subargs, ConsumeMessageCommandTest.consumeMessageCommand.buildCommandlineOptions(options), new PosixParser());
        ConsumeMessageCommandTest.consumeMessageCommand.execute(commandLine, options, null);
        System.setOut(out);
        String s = new String(bos.toByteArray());
        Assert.assertTrue((!(s.contains("Consume ok"))));
    }
}

