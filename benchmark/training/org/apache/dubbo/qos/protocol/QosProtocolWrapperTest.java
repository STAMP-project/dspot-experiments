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
package org.apache.dubbo.qos.protocol;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.qos.command.BaseCommand;
import org.apache.dubbo.qos.server.Server;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Protocol;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class QosProtocolWrapperTest {
    private URL url = Mockito.mock(URL.class);

    private Invoker invoker = Mockito.mock(Invoker.class);

    private Protocol protocol = Mockito.mock(Protocol.class);

    private QosProtocolWrapper wrapper = new QosProtocolWrapper(protocol);

    private Server server = Server.getInstance();

    @Test
    public void testExport() throws Exception {
        wrapper.export(invoker);
        MatcherAssert.assertThat(server.isStarted(), Matchers.is(true));
        MatcherAssert.assertThat(server.getPort(), Matchers.is(12345));
        MatcherAssert.assertThat(server.isAcceptForeignIp(), Matchers.is(false));
        Mockito.verify(protocol).export(invoker);
    }

    @Test
    public void testRefer() throws Exception {
        wrapper.refer(BaseCommand.class, url);
        MatcherAssert.assertThat(server.isStarted(), Matchers.is(true));
        MatcherAssert.assertThat(server.getPort(), Matchers.is(12345));
        MatcherAssert.assertThat(server.isAcceptForeignIp(), Matchers.is(false));
        Mockito.verify(protocol).refer(BaseCommand.class, url);
    }
}

