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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.rocketmq.srvutil.ServerUtil;
import org.apache.rocketmq.tools.command.SubCommandException;
import org.junit.Assert;
import org.junit.Test;


public class SendMessageCommandTest {
    private static SendMessageCommand sendMessageCommand = new SendMessageCommand();

    @Test
    public void testExecute() throws SubCommandException {
        PrintStream out = System.out;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        String[] subargs = new String[]{ "-t mytopic", "-p 'send message test'", "-c tagA", "-k order-16546745756" };
        CommandLine commandLine = ServerUtil.parseCmdLine(("mqadmin " + (SendMessageCommandTest.sendMessageCommand.commandName())), subargs, SendMessageCommandTest.sendMessageCommand.buildCommandlineOptions(options), new PosixParser());
        SendMessageCommandTest.sendMessageCommand.execute(commandLine, options, null);
        subargs = new String[]{ "-t mytopic", "-p 'send message test'", "-c tagA", "-k order-16546745756", "-b brokera", "-i 1" };
        commandLine = ServerUtil.parseCmdLine(("mqadmin " + (SendMessageCommandTest.sendMessageCommand.commandName())), subargs, SendMessageCommandTest.sendMessageCommand.buildCommandlineOptions(options), new PosixParser());
        SendMessageCommandTest.sendMessageCommand.execute(commandLine, options, null);
        System.setOut(out);
        String s = new String(bos.toByteArray());
        Assert.assertTrue(s.contains("SEND_OK"));
    }
}

