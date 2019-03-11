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
package org.apache.geode.internal.cache.tier.sockets.command;


import Operation.MANAGE;
import Resource.CLUSTER;
import Target.QUERY;
import org.apache.geode.cache.query.cq.internal.command.StopCQ;
import org.apache.geode.test.dunit.rules.CQUnitTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;


public class StopCQTest {
    @Rule
    public CQUnitTestRule cqRule = new CQUnitTestRule();

    @Test
    public void needClusterManageQueryToStopCQ() throws Exception {
        StopCQ stopCQ = Mockito.mock(StopCQ.class);
        Mockito.doCallRealMethod().when(stopCQ).cmdExecute(cqRule.message, cqRule.connection, cqRule.securityService, 0);
        stopCQ.cmdExecute(cqRule.message, cqRule.connection, cqRule.securityService, 0);
        Mockito.verify(cqRule.securityService).authorize(CLUSTER, MANAGE, QUERY);
    }
}

