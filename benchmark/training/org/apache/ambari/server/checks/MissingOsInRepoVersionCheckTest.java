/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.checks;


import UpgradeCheckStatus.FAIL;
import UpgradeCheckStatus.PASS;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.orm.dao.RepositoryVersionDAO;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Host;
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
public class MissingOsInRepoVersionCheckTest extends EasyMockSupport {
    public static final String CLUSTER_NAME = "cluster";

    public static final StackId SOURCE_STACK = new StackId("HDP-2.6");

    public static final String OS_FAMILY_IN_CLUSTER = "centos7";

    private MissingOsInRepoVersionCheck prerequisite;

    @Mock
    private Clusters clusters;

    @Mock
    private Cluster cluster;

    @Mock
    private Host host;

    @Mock
    private AmbariMetaInfo ambariMetaInfo;

    @Mock
    private RepositoryVersionDAO repositoryVersionDAO;

    private MockCheckHelper m_checkHelper = new MockCheckHelper();

    @Test
    public void testSuccessWhenOsExistsBothInTargetAndSource() throws Exception {
        sourceStackRepoIs(MissingOsInRepoVersionCheckTest.OS_FAMILY_IN_CLUSTER);
        UpgradeCheckRequest request = request(targetRepo(MissingOsInRepoVersionCheckTest.OS_FAMILY_IN_CLUSTER));
        replayAll();
        UpgradeCheckResult check = performPrerequisite(request);
        verifyAll();
        Assert.assertEquals(PASS, check.getStatus());
    }

    @Test
    public void testFailsWhenOsDoesntExistInSource() throws Exception {
        sourceStackRepoIs("different-os");
        UpgradeCheckRequest request = request(targetRepo(MissingOsInRepoVersionCheckTest.OS_FAMILY_IN_CLUSTER));
        replayAll();
        UpgradeCheckResult check = performPrerequisite(request);
        Assert.assertEquals(FAIL, check.getStatus());
        verifyAll();
    }

    @Test
    public void testFailsWhenOsDoesntExistInTarget() throws Exception {
        sourceStackRepoIs(MissingOsInRepoVersionCheckTest.OS_FAMILY_IN_CLUSTER);
        UpgradeCheckRequest request = request(targetRepo("different-os"));
        replayAll();
        UpgradeCheckResult check = performPrerequisite(request);
        Assert.assertEquals(FAIL, check.getStatus());
        verifyAll();
    }
}

