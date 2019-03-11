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
package org.apache.rocketmq.tools.command.broker;


import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.srvutil.ServerUtil;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExtImpl;
import org.junit.Test;


public class SendMsgStatusCommandTest {
    private static DefaultMQAdminExt defaultMQAdminExt;

    private static DefaultMQAdminExtImpl defaultMQAdminExtImpl;

    private static MQClientInstance mqClientInstance = MQClientManager.getInstance().getAndCreateMQClientInstance(new ClientConfig());

    private static MQClientAPIImpl mQClientAPIImpl;

    @Test
    public void testExecute() {
        SendMsgStatusCommand cmd = new SendMsgStatusCommand();
        Options options = ServerUtil.buildCommandlineOptions(new Options());
        String[] subargs = new String[]{ "-b 127.0.0.1:10911", "-s 1024 -c 10" };
        final CommandLine commandLine = ServerUtil.parseCmdLine(("mqadmin " + (cmd.commandName())), subargs, cmd.buildCommandlineOptions(options), new PosixParser());
        // cmd.execute(commandLine, options, null);
    }
}

