/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.checks;


import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import UpgradeCheckStatus.WARNING;
import java.util.Collections;
import org.apache.ambari.server.stack.upgrade.UpgradePack;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeHelper;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.ServiceComponentSupport;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.spi.upgrade.UpgradeCheckRequest;
import org.apache.ambari.spi.upgrade.UpgradeCheckResult;
import org.easymock.EasyMockRunner;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class ComponentExistsInRepoCheckTest extends EasyMockSupport {
    public static final String STACK_NAME = "HDP";

    public static final String STACK_VERSION = "2.2.0";

    private ComponentsExistInRepoCheck check = new ComponentsExistInRepoCheck();

    @Mock
    private Clusters clusters;

    @Mock
    private Cluster cluster;

    @Mock
    private ServiceComponentSupport serviceComponentSupport;

    @Mock
    private UpgradeHelper upgradeHelper;

    private UpgradeCheckRequest request;

    private StackId sourceStackId = new StackId(ComponentExistsInRepoCheckTest.STACK_NAME, "1.0");

    @Test
    public void testPassesWhenNoUnsupportedInTargetStack() throws Exception {
        expect(serviceComponentSupport.allUnsupported(cluster, ComponentExistsInRepoCheckTest.STACK_NAME, ComponentExistsInRepoCheckTest.STACK_VERSION)).andReturn(Collections.emptyList()).anyTimes();
        replayAll();
        UpgradeCheckResult result = check.perform(request);
        Assert.assertEquals(PASS, result.getStatus());
    }

    @Test
    public void testFailsWhenUnsupportedFoundInTargetStack() throws Exception {
        expect(serviceComponentSupport.allUnsupported(cluster, ComponentExistsInRepoCheckTest.STACK_NAME, ComponentExistsInRepoCheckTest.STACK_VERSION)).andReturn(Collections.singletonList("ANY_SERVICE")).anyTimes();
        suggestedUpgradePackIs(new UpgradePack());
        replayAll();
        UpgradeCheckResult result = check.perform(request);
        Assert.assertEquals(FAIL, result.getStatus());
    }

    @Test
    public void testWarnsWhenUnsupportedFoundInTargetStackAndUpgradePackHasAutoDeleteTask() throws Exception {
        expect(serviceComponentSupport.allUnsupported(cluster, ComponentExistsInRepoCheckTest.STACK_NAME, ComponentExistsInRepoCheckTest.STACK_VERSION)).andReturn(Collections.singletonList("ANY_SERVICE")).anyTimes();
        suggestedUpgradePackIs(upgradePackWithDeleteUnsupportedTask());
        replayAll();
        UpgradeCheckResult result = check.perform(request);
        Assert.assertEquals(WARNING, result.getStatus());
    }
}

