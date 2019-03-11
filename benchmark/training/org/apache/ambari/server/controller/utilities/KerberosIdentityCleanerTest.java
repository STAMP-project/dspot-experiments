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
package org.apache.ambari.server.controller.utilities;


import SecurityType.KERBEROS;
import SecurityType.NONE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.ambari.server.controller.KerberosHelper;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.serveraction.kerberos.Component;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.Service;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorFactory;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.junit.Rule;
import org.junit.Test;


public class KerberosIdentityCleanerTest extends EasyMockSupport {
    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    private static final String HOST = "c6401";

    private static final String HOST2 = "c6402";

    private static final String OOZIE = "OOZIE";

    private static final String OOZIE_SERVER = "OOZIE_SERVER";

    private static final String OOZIE_2 = "OOZIE2";

    private static final String OOZIE_SERVER_2 = "OOZIE_SERVER2";

    private static final String YARN_2 = "YARN2";

    private static final String RESOURCE_MANAGER_2 = "RESOURCE_MANAGER2";

    private static final String YARN = "YARN";

    private static final String RESOURCE_MANAGER = "RESOURCE_MANAGER";

    private static final String HDFS = "HDFS";

    private static final String NAMENODE = "NAMENODE";

    private static final String DATANODE = "DATANODE";

    private static final long CLUSTER_ID = 1;

    @Mock
    private KerberosHelper kerberosHelper;

    @Mock
    private Clusters clusters;

    @Mock
    private Cluster cluster;

    private Map<String, Service> installedServices = new HashMap<>();

    private KerberosDescriptorFactory kerberosDescriptorFactory = new KerberosDescriptorFactory();

    private KerberosIdentityCleaner kerberosIdentityCleaner;

    private KerberosDescriptor kerberosDescriptor;

    @Test
    public void removesAllKerberosIdentitesOfComponentAfterComponentWasUninstalled() throws Exception {
        installComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        kerberosHelper.deleteIdentities(cluster, Collections.singletonList(new Component(KerberosIdentityCleanerTest.HOST, KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, (-1L))), null);
        expectLastCall().once();
        replayAll();
        uninstallComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        verifyAll();
    }

    @Test
    public void skipsRemovingIdentityThatIsSharedByPrincipalName() throws Exception {
        installComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        installComponent(KerberosIdentityCleanerTest.OOZIE_2, KerberosIdentityCleanerTest.OOZIE_SERVER_2, KerberosIdentityCleanerTest.HOST);
        kerberosHelper.deleteIdentities(cluster, Collections.singletonList(new Component(KerberosIdentityCleanerTest.HOST, KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, (-1L))), null);
        expectLastCall().once();
        replayAll();
        uninstallComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        verifyAll();
    }

    @Test
    public void skipsRemovingIdentityThatIsSharedByKeyTabFilePath() throws Exception {
        installComponent(KerberosIdentityCleanerTest.YARN, KerberosIdentityCleanerTest.RESOURCE_MANAGER, KerberosIdentityCleanerTest.HOST);
        installComponent(KerberosIdentityCleanerTest.YARN_2, KerberosIdentityCleanerTest.RESOURCE_MANAGER_2, KerberosIdentityCleanerTest.HOST);
        kerberosHelper.deleteIdentities(cluster, Collections.singletonList(new Component(KerberosIdentityCleanerTest.HOST, KerberosIdentityCleanerTest.YARN, KerberosIdentityCleanerTest.RESOURCE_MANAGER, (-1L))), null);
        expectLastCall().once();
        replayAll();
        uninstallComponent(KerberosIdentityCleanerTest.YARN, KerberosIdentityCleanerTest.RESOURCE_MANAGER, KerberosIdentityCleanerTest.HOST);
        verifyAll();
    }

    @Test
    public void skipsRemovingIdentityWhenClusterIsNotKerberized() throws Exception {
        reset(cluster);
        expect(cluster.getSecurityType()).andReturn(NONE).anyTimes();
        replayAll();
        uninstallComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        verifyAll();
    }

    @Test
    public void removesServiceIdentitiesSkipComponentIdentitiesAfterServiceWasUninstalled() throws Exception {
        installComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        kerberosHelper.deleteIdentities(cluster, hdfsComponents(), null);
        expectLastCall().once();
        replayAll();
        uninstallService(KerberosIdentityCleanerTest.HDFS, hdfsComponents());
        verifyAll();
    }

    /**
     * Ensures that when an upgrade is in progress, new requests are not created
     * to remove identities since it would interfere with the long running
     * upgrade.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void skipsRemovingIdentityWhenClusterIsUpgrading() throws Exception {
        installComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        reset(cluster);
        expect(cluster.getSecurityType()).andReturn(KERBEROS).once();
        expect(cluster.getUpgradeInProgress()).andReturn(createNiceMock(UpgradeEntity.class)).once();
        replayAll();
        uninstallComponent(KerberosIdentityCleanerTest.OOZIE, KerberosIdentityCleanerTest.OOZIE_SERVER, KerberosIdentityCleanerTest.HOST);
        verifyAll();
    }
}

