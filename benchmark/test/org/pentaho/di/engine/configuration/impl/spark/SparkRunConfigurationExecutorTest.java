/**
 * *****************************************************************************
 *
 *  Pentaho Data Integration
 * s *
 *  Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.engine.configuration.impl.spark;


import SparkRunConfigurationExecutor.AEL_SECURITY_CAPABILITY_ID;
import SparkRunConfigurationExecutor.DEFAULT_HOST;
import SparkRunConfigurationExecutor.DEFAULT_PROTOCOL;
import SparkRunConfigurationExecutor.DEFAULT_WEBSOCKET_PORT;
import SparkRunConfigurationExecutor.JAAS_CAPABILITY_ID;
import java.util.Dictionary;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.capabilities.api.ICapability;
import org.pentaho.capabilities.api.ICapabilityProvider;
import org.pentaho.capabilities.impl.DefaultCapabilityManager;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.trans.TransExecutionConfiguration;


/**
 * Created by bmorrise on 3/22/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class SparkRunConfigurationExecutorTest {
    private SparkRunConfigurationExecutor sparkRunConfigurationExecutor;

    @Mock
    private Dictionary<String, Object> properties;

    @Mock
    private AbstractMeta abstractMeta;

    @Mock
    private VariableSpace variableSpace;

    private DefaultCapabilityManager capabilityManager;

    private ICapabilityProvider capabilityProvider;

    @Test
    public void testWebSocketVersionExecute() {
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("Spark Configuration");
        sparkRunConfiguration.setSchema("http://");
        sparkRunConfiguration.setUrl("127.0.0.2:8121");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        sparkRunConfigurationExecutor.execute(sparkRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Mockito.verify(variableSpace).setVariable("engine", "remote");
        Mockito.verify(variableSpace).setVariable("engine.remote", "spark");
        Mockito.verify(variableSpace).setVariable("engine.host", "127.0.0.2");
        Mockito.verify(variableSpace).setVariable("engine.port", "8121");
    }

    @Test
    public void testWebSocketVersionExecuteNoPort() {
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("Spark Configuration");
        Mockito.doReturn("2.0").when(variableSpace).getVariable("KETTLE_AEL_PDI_DAEMON_VERSION", "2.0");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        sparkRunConfigurationExecutor.execute(sparkRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Mockito.verify(variableSpace).setVariable("engine", "remote");
        Mockito.verify(variableSpace).setVariable("engine.remote", "spark");
        Mockito.verify(variableSpace).setVariable("engine.protocol", DEFAULT_PROTOCOL);
        Mockito.verify(variableSpace).setVariable("engine.host", DEFAULT_HOST);
        Mockito.verify(variableSpace).setVariable("engine.port", DEFAULT_WEBSOCKET_PORT);
    }

    @Test
    public void testWssWebSocketVersionExecute() {
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("Spark Configuration");
        sparkRunConfiguration.setSchema("https://");
        sparkRunConfiguration.setUrl("127.0.0.2:8121");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        sparkRunConfigurationExecutor.execute(sparkRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Mockito.verify(variableSpace).setVariable("engine.protocol", "https");
        Mockito.verify(variableSpace).setVariable("engine.host", "127.0.0.2");
        Mockito.verify(variableSpace).setVariable("engine.port", "8121");
    }

    @Test
    public void testUrlWssWebSocketVersionExecute() {
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("Spark Configuration");
        sparkRunConfiguration.setSchema("http://");
        sparkRunConfiguration.setUrl("  127.0.0.2:8121  ");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        sparkRunConfigurationExecutor.execute(sparkRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Mockito.verify(variableSpace).setVariable("engine.protocol", "http");
        Mockito.verify(variableSpace).setVariable("engine.host", "127.0.0.2");
        Mockito.verify(variableSpace).setVariable("engine.port", "8121");
    }

    @Test
    public void testExecuteWithAelSecurityInstalled() {
        ICapability aelSecurityCapability = Mockito.mock(ICapability.class);
        setCapability(aelSecurityCapability, AEL_SECURITY_CAPABILITY_ID, true);
        ICapability jaasCapability = Mockito.mock(ICapability.class);
        setCapability(jaasCapability, JAAS_CAPABILITY_ID, false);
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("Spark Configuration");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        sparkRunConfigurationExecutor.execute(sparkRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Mockito.verify(jaasCapability).isInstalled();
        Mockito.verify(jaasCapability).install();
    }

    @Test
    public void testExecuteWithNoAelSecurityInstalled() {
        ICapability aelSecurityCapability = Mockito.mock(ICapability.class);
        setCapability(aelSecurityCapability, AEL_SECURITY_CAPABILITY_ID, false);
        ICapability jaasCapability = Mockito.mock(ICapability.class);
        setCapability(jaasCapability, JAAS_CAPABILITY_ID, false);
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("Spark Configuration");
        TransExecutionConfiguration transExecutionConfiguration = new TransExecutionConfiguration();
        sparkRunConfigurationExecutor.execute(sparkRunConfiguration, transExecutionConfiguration, abstractMeta, variableSpace, null);
        Mockito.verify(jaasCapability, Mockito.never()).isInstalled();
    }
}

