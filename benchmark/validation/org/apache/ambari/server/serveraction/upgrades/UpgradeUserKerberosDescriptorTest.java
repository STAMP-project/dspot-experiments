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
package org.apache.ambari.server.serveraction.upgrades;


import Direction.DOWNGRADE;
import Direction.UPGRADE;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.ambari.server.actionmanager.HostRoleCommand;
import org.apache.ambari.server.agent.ExecutionCommand;
import org.apache.ambari.server.api.services.AmbariMetaInfo;
import org.apache.ambari.server.orm.dao.ArtifactDAO;
import org.apache.ambari.server.orm.entities.ArtifactEntity;
import org.apache.ambari.server.orm.entities.RepositoryVersionEntity;
import org.apache.ambari.server.orm.entities.UpgradeEntity;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeContext;
import org.apache.ambari.server.stack.upgrade.orchestrate.UpgradeContextFactory;
import org.apache.ambari.server.state.Cluster;
import org.apache.ambari.server.state.Clusters;
import org.apache.ambari.server.state.StackId;
import org.apache.ambari.server.state.kerberos.KerberosDescriptor;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorFactory;
import org.apache.ambari.server.state.kerberos.KerberosDescriptorUpdateHelper;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Tests OozieConfigCalculation logic
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(KerberosDescriptorUpdateHelper.class)
public class UpgradeUserKerberosDescriptorTest {
    private Clusters clusters;

    private Cluster cluster;

    private UpgradeEntity upgrade;

    private UpgradeContext upgradeContext;

    private AmbariMetaInfo ambariMetaInfo;

    private KerberosDescriptorFactory kerberosDescriptorFactory;

    private ArtifactDAO artifactDAO;

    private UpgradeContextFactory upgradeContextFactory;

    private TreeMap<String, Field> fields = new TreeMap<>();

    private StackId HDP_24 = new StackId("HDP", "2.4");

    @Test
    public void testUpgrade() throws Exception {
        StackId stackId = new StackId("HDP", "2.5");
        RepositoryVersionEntity repositoryVersion = EasyMock.createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion.getStackId()).andReturn(stackId).atLeastOnce();
        expect(upgradeContext.getDirection()).andReturn(UPGRADE).atLeastOnce();
        expect(upgradeContext.getRepositoryVersion()).andReturn(repositoryVersion).atLeastOnce();
        replay(repositoryVersion, upgradeContext);
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put("clusterName", "c1");
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName("c1");
        HostRoleCommand hrc = EasyMock.createMock(HostRoleCommand.class);
        expect(hrc.getRequestId()).andReturn(1L).anyTimes();
        expect(hrc.getStageId()).andReturn(2L).anyTimes();
        expect(hrc.getExecutionCommandWrapper()).andReturn(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand)).anyTimes();
        replay(hrc);
        UpgradeUserKerberosDescriptor action = new UpgradeUserKerberosDescriptor();
        injectFields(action);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hrc);
        ArtifactEntity entity = EasyMock.createNiceMock(ArtifactEntity.class);
        expect(entity.getArtifactData()).andReturn(null).anyTimes();
        expect(entity.getForeignKeys()).andReturn(null).anyTimes();
        expect(artifactDAO.findByNameAndForeignKeys(anyString(), ((TreeMap<String, String>) (anyObject())))).andReturn(entity).atLeastOnce();
        KerberosDescriptor userDescriptor = EasyMock.createMock(KerberosDescriptor.class);
        KerberosDescriptor newDescriptor = EasyMock.createMock(KerberosDescriptor.class);
        KerberosDescriptor previousDescriptor = EasyMock.createMock(KerberosDescriptor.class);
        KerberosDescriptor updatedKerberosDescriptor = EasyMock.createMock(KerberosDescriptor.class);
        PowerMockito.mockStatic(KerberosDescriptorUpdateHelper.class);
        PowerMockito.when(KerberosDescriptorUpdateHelper.updateUserKerberosDescriptor(previousDescriptor, newDescriptor, userDescriptor)).thenReturn(updatedKerberosDescriptor);
        expect(kerberosDescriptorFactory.createInstance(((Map) (null)))).andReturn(userDescriptor).atLeastOnce();
        expect(ambariMetaInfo.getKerberosDescriptor("HDP", "2.5", false)).andReturn(newDescriptor).atLeastOnce();
        expect(ambariMetaInfo.getKerberosDescriptor("HDP", "2.4", false)).andReturn(previousDescriptor).atLeastOnce();
        expect(updatedKerberosDescriptor.toMap()).andReturn(null).once();
        expect(artifactDAO.merge(entity)).andReturn(entity).once();
        Capture<ArtifactEntity> createCapture = Capture.newInstance();
        artifactDAO.create(capture(createCapture));
        EasyMock.expectLastCall().once();
        replay(artifactDAO, entity, ambariMetaInfo, kerberosDescriptorFactory, updatedKerberosDescriptor);
        action.execute(null);
        verify(artifactDAO, updatedKerberosDescriptor);
        Assert.assertEquals(createCapture.getValue().getArtifactName(), "kerberos_descriptor_backup");
    }

    @Test
    public void testDowngrade() throws Exception {
        StackId stackId = new StackId("HDP", "2.5");
        RepositoryVersionEntity repositoryVersion = EasyMock.createNiceMock(RepositoryVersionEntity.class);
        expect(repositoryVersion.getStackId()).andReturn(stackId).atLeastOnce();
        expect(upgradeContext.getDirection()).andReturn(DOWNGRADE).atLeastOnce();
        expect(upgradeContext.getRepositoryVersion()).andReturn(repositoryVersion).atLeastOnce();
        replay(repositoryVersion, upgradeContext);
        Map<String, String> commandParams = new HashMap<>();
        commandParams.put("clusterName", "c1");
        ExecutionCommand executionCommand = new ExecutionCommand();
        executionCommand.setCommandParams(commandParams);
        executionCommand.setClusterName("c1");
        HostRoleCommand hrc = EasyMock.createMock(HostRoleCommand.class);
        expect(hrc.getRequestId()).andReturn(1L).anyTimes();
        expect(hrc.getStageId()).andReturn(2L).anyTimes();
        expect(hrc.getExecutionCommandWrapper()).andReturn(new org.apache.ambari.server.actionmanager.ExecutionCommandWrapper(executionCommand)).anyTimes();
        replay(hrc);
        UpgradeUserKerberosDescriptor action = new UpgradeUserKerberosDescriptor();
        injectFields(action);
        action.setExecutionCommand(executionCommand);
        action.setHostRoleCommand(hrc);
        ArtifactEntity entity = EasyMock.createNiceMock(ArtifactEntity.class);
        expect(entity.getArtifactData()).andReturn(null).anyTimes();
        expect(entity.getForeignKeys()).andReturn(null).anyTimes();
        expect(artifactDAO.findByNameAndForeignKeys(anyString(), ((TreeMap<String, String>) (anyObject())))).andReturn(entity).atLeastOnce();
        KerberosDescriptor userDescriptor = EasyMock.createMock(KerberosDescriptor.class);
        expect(kerberosDescriptorFactory.createInstance(((Map) (null)))).andReturn(userDescriptor).atLeastOnce();
        Capture<ArtifactEntity> createCapture = Capture.newInstance();
        artifactDAO.create(capture(createCapture));
        EasyMock.expectLastCall().once();
        artifactDAO.remove(entity);
        EasyMock.expectLastCall().atLeastOnce();
        replay(artifactDAO, entity, ambariMetaInfo, kerberosDescriptorFactory);
        action.execute(null);
        verify(artifactDAO);
        Assert.assertEquals(createCapture.getValue().getArtifactName(), "kerberos_descriptor");
    }
}

