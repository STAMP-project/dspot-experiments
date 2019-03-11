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
package org.apache.rocketmq.tools.command;


import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.remoting.exception.RemotingConnectException;
import org.apache.rocketmq.remoting.exception.RemotingSendRequestException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExt;
import org.apache.rocketmq.tools.admin.DefaultMQAdminExtImpl;
import org.junit.Test;


public class CommandUtilTest {
    private DefaultMQAdminExt defaultMQAdminExt;

    private DefaultMQAdminExtImpl defaultMQAdminExtImpl;

    private MQClientInstance mqClientInstance = MQClientManager.getInstance().getAndCreateMQClientInstance(new ClientConfig());

    private MQClientAPIImpl mQClientAPIImpl;

    @Test
    public void testFetchMasterAndSlaveDistinguish() throws InterruptedException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        Map<String, List<String>> result = CommandUtil.fetchMasterAndSlaveDistinguish(defaultMQAdminExtImpl, "default-cluster");
        assertThat(result.get(null).get(0)).isEqualTo("127.0.0.1:10911");
    }

    @Test
    public void testFetchMasterAddrByClusterName() throws InterruptedException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException {
        Set<String> result = CommandUtil.fetchMasterAddrByClusterName(defaultMQAdminExtImpl, "default-cluster");
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testFetchBrokerNameByClusterName() throws Exception {
        Set<String> result = CommandUtil.fetchBrokerNameByClusterName(defaultMQAdminExtImpl, "default-cluster");
        assertThat(result.contains("default-broker")).isTrue();
        assertThat(result.contains("default-broker-one")).isTrue();
        assertThat(result.size()).isEqualTo(2);
    }
}

