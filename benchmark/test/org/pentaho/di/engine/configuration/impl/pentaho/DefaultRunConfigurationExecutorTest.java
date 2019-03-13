/**
 * *****************************************************************************
 *
 *  Pentaho Data Integration
 *
 *  Copyright (C) 2017-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *  *******************************************************************************
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 *  this file except in compliance with the License. You may obtain a copy of the
 *  License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 * *****************************************************************************
 */
package org.pentaho.di.engine.configuration.impl.pentaho;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.UserInfo;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.ui.spoon.Spoon;


/**
 * Created by bmorrise on 3/22/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultRunConfigurationExecutorTest {
    private DefaultRunConfigurationExecutor defaultRunConfigurationExecutor;

    @Mock
    private AbstractMeta abstractMeta;

    @Mock
    private AbstractMeta variableSpace;

    @Mock
    private SlaveServer slaveServer;

    @Mock
    private Repository repository;

    @Mock
    private Spoon spoon;

    @Mock
    private UserInfo userInfo;

    @Mock
    private RepositoryDirectoryInterface repositoryDirectory;

    @Test
    public void testExecuteLocalTrans() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(true);
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertTrue(transExecutionConfiguration.isExecutingLocally());
    }

    @Test
    public void testSendResources() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setSendResources(true);
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertTrue(transExecutionConfiguration.isPassingExport());
    }

    @Test
    public void testExecuteRemoteTrans() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setRemote(true);
        defaultRunConfiguration.setServer("Test Server");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        Mockito.doReturn(slaveServer).when(abstractMeta).findSlaveServer("Test Server");
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertFalse(transExecutionConfiguration.isExecutingLocally());
        Assert.assertTrue(transExecutionConfiguration.isExecutingRemotely());
        Assert.assertEquals(transExecutionConfiguration.getRemoteServer(), slaveServer);
    }

    @Test
    public void testExecutePentahoTrans() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setPentaho(true);
        defaultRunConfiguration.setRemote(false);
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertFalse(transExecutionConfiguration.isExecutingLocally());
        Assert.assertFalse(transExecutionConfiguration.isExecutingRemotely());
    }

    @Test
    public void testExecuteClusteredTrans() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setRemote(false);
        defaultRunConfiguration.setClustered(true);
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertTrue(transExecutionConfiguration.isExecutingClustered());
        Assert.assertFalse(transExecutionConfiguration.isExecutingRemotely());
        Assert.assertFalse(transExecutionConfiguration.isExecutingLocally());
    }

    @Test
    public void testExecuteRemoteNotFoundTrans() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setRemote(true);
        defaultRunConfiguration.setServer("Test Server");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        Mockito.doReturn(slaveServer).when(abstractMeta).findSlaveServer(null);
        try {
            defaultRunConfigurationExecutor.execute(defaultRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
            Assert.fail();
        } catch (KettleException e) {
            // expected
        }
    }

    @Test
    public void testExecuteLocalJob() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(true);
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, jobExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertTrue(jobExecutionConfiguration.isExecutingLocally());
    }

    @Test
    public void testExecuteRemoteJob() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setRemote(true);
        defaultRunConfiguration.setServer("Test Server");
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        Mockito.doReturn(slaveServer).when(abstractMeta).findSlaveServer("Test Server");
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, jobExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertFalse(jobExecutionConfiguration.isExecutingLocally());
        Assert.assertTrue(jobExecutionConfiguration.isExecutingRemotely());
        Assert.assertEquals(jobExecutionConfiguration.getRemoteServer(), slaveServer);
    }

    @Test
    public void testExecuteRemoteNotFoundJob() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setRemote(true);
        defaultRunConfiguration.setServer("Test Server");
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        Mockito.doReturn(slaveServer).when(abstractMeta).findSlaveServer(null);
        try {
            defaultRunConfigurationExecutor.execute(defaultRunConfiguration, jobExecutionConfiguration, abstractMeta, variableSpace, null);
            Assert.fail();
        } catch (KettleException e) {
            // expected
        }
    }

    @Test
    public void testExecutePentahoJob() throws Exception {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("Default Configuration");
        defaultRunConfiguration.setLocal(false);
        defaultRunConfiguration.setPentaho(true);
        defaultRunConfiguration.setRemote(false);
        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
        defaultRunConfigurationExecutor.execute(defaultRunConfiguration, jobExecutionConfiguration, abstractMeta, variableSpace, null);
        Assert.assertFalse(jobExecutionConfiguration.isExecutingLocally());
        Assert.assertFalse(jobExecutionConfiguration.isExecutingRemotely());
    }
}

