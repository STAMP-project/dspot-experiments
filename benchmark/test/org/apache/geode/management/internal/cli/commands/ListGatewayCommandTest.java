/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.management.internal.cli.commands;


import CliStrings.HEADER_GATEWAYS;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.management.GatewayReceiverMXBean;
import org.apache.geode.management.internal.SystemManagementService;
import org.apache.geode.management.internal.cli.result.model.ResultModel;
import org.apache.geode.test.junit.assertions.CommandResultAssert;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;


public class ListGatewayCommandTest {
    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private final Map<String, GatewayReceiverMXBean> receiverBeans = new HashMap<>();

    private ListGatewayCommand command;

    private DistributedMember member;

    private SystemManagementService service;

    private GatewayReceiverMXBean receiverMXBean;

    @Test
    public void listGatewaysDisplaysGatewaySendersAndReceivers() {
        ResultModel crd = new ResultModel();
        crd.setHeader(HEADER_GATEWAYS);
        Mockito.doReturn(new String[]{ "10.118.19.31(server-ny-2:33256)<v2>:1029", "10.118.19.31(server-ny-1:33206)<v1>:1028" }).when(receiverMXBean).getConnectedGatewaySenders();
        command.accumulateListGatewayResult(crd, Collections.EMPTY_MAP, receiverBeans);
        new CommandResultAssert(new org.apache.geode.management.internal.cli.result.ModelCommandResult(crd)).hasTableSection("gatewayReceivers").hasColumn("Senders Connected").containsExactly("10.118.19.31(server-ny-2:33256)<v2>:1029, 10.118.19.31(server-ny-1:33206)<v1>:1028");
    }

    @Test
    public void listGatewaysDisplaysGatewayReceiversWhenEmpty() {
        ResultModel crd = new ResultModel();
        Mockito.doReturn(new String[0]).when(receiverMXBean).getConnectedGatewaySenders();
        command.accumulateListGatewayResult(crd, Collections.EMPTY_MAP, receiverBeans);
        new CommandResultAssert(new org.apache.geode.management.internal.cli.result.ModelCommandResult(crd)).hasTableSection("gatewayReceivers").hasColumn("Senders Connected").containsExactly("");
    }

    @Test
    public void listGatewaysDisplaysGatewayReceiversWhenNull() {
        ResultModel crd = new ResultModel();
        Mockito.doReturn(null).when(receiverMXBean).getConnectedGatewaySenders();
        command.accumulateListGatewayResult(crd, Collections.EMPTY_MAP, receiverBeans);
        new CommandResultAssert(new org.apache.geode.management.internal.cli.result.ModelCommandResult(crd)).hasTableSection("gatewayReceivers").hasColumn("Senders Connected").containsExactly("");
    }
}

