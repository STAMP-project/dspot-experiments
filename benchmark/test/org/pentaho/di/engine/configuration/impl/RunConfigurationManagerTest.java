/**
 * *****************************************************************************
 *
 *  Pentaho Data Integration
 *
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
package org.pentaho.di.engine.configuration.impl;


import DefaultRunConfiguration.TYPE;
import DefaultRunConfigurationProvider.DEFAULT_CONFIG_NAME;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.engine.configuration.api.RunConfiguration;
import org.pentaho.di.engine.configuration.api.RunConfigurationProvider;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfiguration;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfigurationExecutor;
import org.pentaho.di.engine.configuration.impl.pentaho.DefaultRunConfigurationProvider;
import org.pentaho.di.engine.configuration.impl.spark.SparkRunConfiguration;
import org.pentaho.di.engine.configuration.impl.spark.SparkRunConfigurationExecutor;
import org.pentaho.di.engine.configuration.impl.spark.SparkRunConfigurationProvider;
import org.pentaho.metastore.stores.memory.MemoryMetaStore;
import org.pentaho.osgi.metastore.locator.api.MetastoreLocator;


/**
 * Created by bmorrise on 3/15/17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RunConfigurationManagerTest {
    private RunConfigurationManager executionConfigurationManager;

    @Mock
    private DefaultRunConfigurationExecutor defaultRunConfigurationExecutor;

    @Test
    public void testGetTypes() {
        String[] types = executionConfigurationManager.getTypes();
        Assert.assertTrue(Arrays.asList(types).contains(TYPE));
        Assert.assertTrue(Arrays.asList(types).contains(SparkRunConfiguration.TYPE));
    }

    @Test
    public void testLoad() {
        List<RunConfiguration> runConfigurations = executionConfigurationManager.load();
        Assert.assertEquals(runConfigurations.size(), 3);// Includes default

    }

    @Test
    public void testLoadByName() {
        DefaultRunConfiguration defaultRunConfiguration = ((DefaultRunConfiguration) (executionConfigurationManager.load("Default Configuration")));
        Assert.assertNotNull(defaultRunConfiguration);
        Assert.assertEquals(defaultRunConfiguration.getName(), "Default Configuration");
    }

    @Test
    public void testSaveAndDelete() {
        DefaultRunConfiguration defaultRunConfiguration = new DefaultRunConfiguration();
        defaultRunConfiguration.setName("New Run Configuration");
        executionConfigurationManager.save(defaultRunConfiguration);
        DefaultRunConfiguration loadedRunConfiguration = ((DefaultRunConfiguration) (executionConfigurationManager.load("New Run Configuration")));
        Assert.assertEquals(loadedRunConfiguration.getName(), defaultRunConfiguration.getName());
        executionConfigurationManager.delete("New Run Configuration");
        loadedRunConfiguration = ((DefaultRunConfiguration) (executionConfigurationManager.load("New Run Configuration")));
        Assert.assertNull(loadedRunConfiguration);
    }

    @Test
    public void testGetNames() {
        List<String> names = executionConfigurationManager.getNames();
        Assert.assertTrue(names.contains(DEFAULT_CONFIG_NAME));
        Assert.assertTrue(names.contains("Default Configuration"));
        Assert.assertTrue(names.contains("Spark Configuration"));
    }

    @Test
    public void testGetRunConfigurationByType() {
        DefaultRunConfiguration defaultRunConfiguration = ((DefaultRunConfiguration) (executionConfigurationManager.getRunConfigurationByType(TYPE)));
        SparkRunConfiguration sparkRunConfiguration = ((SparkRunConfiguration) (executionConfigurationManager.getRunConfigurationByType(SparkRunConfiguration.TYPE)));
        Assert.assertNotNull(defaultRunConfiguration);
        Assert.assertNotNull(sparkRunConfiguration);
    }

    @Test
    public void testGetExecutor() {
        DefaultRunConfigurationExecutor defaultRunConfigurationExecutor = ((DefaultRunConfigurationExecutor) (executionConfigurationManager.getExecutor(TYPE)));
        Assert.assertNotNull(defaultRunConfigurationExecutor);
    }

    @Test
    public void testOrdering() {
        MemoryMetaStore memoryMetaStore = new MemoryMetaStore();
        MetastoreLocator metastoreLocator = RunConfigurationManagerTest.createMetastoreLocator(memoryMetaStore);
        DefaultRunConfigurationProvider defaultRunConfigurationProvider = new DefaultRunConfigurationProvider(metastoreLocator, defaultRunConfigurationExecutor);
        SparkRunConfigurationExecutor sparkRunConfigurationExecutor = new SparkRunConfigurationExecutor(null);
        SparkRunConfigurationProvider sparkRunConfigurationProvider = new SparkRunConfigurationProvider(metastoreLocator, sparkRunConfigurationExecutor);
        List<RunConfigurationProvider> runConfigurationProviders = new ArrayList<>();
        runConfigurationProviders.add(sparkRunConfigurationProvider);
        executionConfigurationManager = new RunConfigurationManager(runConfigurationProviders);
        executionConfigurationManager.setDefaultRunConfigurationProvider(defaultRunConfigurationProvider);
        DefaultRunConfiguration defaultRunConfiguration1 = new DefaultRunConfiguration();
        defaultRunConfiguration1.setName("z");
        executionConfigurationManager.save(defaultRunConfiguration1);
        DefaultRunConfiguration defaultRunConfiguration2 = new DefaultRunConfiguration();
        defaultRunConfiguration2.setName("f");
        executionConfigurationManager.save(defaultRunConfiguration2);
        DefaultRunConfiguration defaultRunConfiguration3 = new DefaultRunConfiguration();
        defaultRunConfiguration3.setName("x");
        executionConfigurationManager.save(defaultRunConfiguration3);
        SparkRunConfiguration sparkRunConfiguration = new SparkRunConfiguration();
        sparkRunConfiguration.setName("d");
        executionConfigurationManager.save(sparkRunConfiguration);
        DefaultRunConfiguration defaultRunConfiguration5 = new DefaultRunConfiguration();
        defaultRunConfiguration5.setName("a");
        executionConfigurationManager.save(defaultRunConfiguration5);
        List<RunConfiguration> runConfigurations = executionConfigurationManager.load();
        Assert.assertEquals(runConfigurations.get(0).getName(), DEFAULT_CONFIG_NAME);
        Assert.assertEquals(runConfigurations.get(1).getName(), "a");
        Assert.assertEquals(runConfigurations.get(2).getName(), "d");
        Assert.assertEquals(runConfigurations.get(3).getName(), "f");
        Assert.assertEquals(runConfigurations.get(4).getName(), "x");
        Assert.assertEquals(runConfigurations.get(5).getName(), "z");
        List<String> names = executionConfigurationManager.getNames();
        Assert.assertEquals(names.get(0), DEFAULT_CONFIG_NAME);
        Assert.assertEquals(names.get(1), "a");
        Assert.assertEquals(names.get(2), "d");
        Assert.assertEquals(names.get(3), "f");
        Assert.assertEquals(names.get(4), "x");
        Assert.assertEquals(names.get(5), "z");
    }
}

