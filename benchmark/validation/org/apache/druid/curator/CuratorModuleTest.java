/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.curator;


import com.google.inject.Injector;
import java.util.Properties;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.ensemble.exhibitor.ExhibitorEnsembleProvider;
import org.apache.curator.ensemble.fixed.FixedEnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Assert;
import org.junit.Test;

import static CuratorModule.CURATOR_CONFIG_PREFIX;
import static CuratorModule.EXHIBITOR_CONFIG_PREFIX;


/**
 *
 */
public final class CuratorModuleTest {
    private static final String curatorHostKey = (CURATOR_CONFIG_PREFIX) + ".host";

    private static final String exhibitorHostsKey = (EXHIBITOR_CONFIG_PREFIX) + ".hosts";

    @Test
    public void defaultEnsembleProvider() {
        Injector injector = newInjector(new Properties());
        injector.getInstance(CuratorFramework.class);// initialize related components

        EnsembleProvider ensembleProvider = injector.getInstance(EnsembleProvider.class);
        Assert.assertTrue("EnsembleProvider should be FixedEnsembleProvider", (ensembleProvider instanceof FixedEnsembleProvider));
        Assert.assertEquals("The connectionString should be 'localhost'", "localhost", ensembleProvider.getConnectionString());
    }

    @Test
    public void fixedZkHosts() {
        Properties props = new Properties();
        props.put(CuratorModuleTest.curatorHostKey, "hostA");
        Injector injector = newInjector(props);
        injector.getInstance(CuratorFramework.class);// initialize related components

        EnsembleProvider ensembleProvider = injector.getInstance(EnsembleProvider.class);
        Assert.assertTrue("EnsembleProvider should be FixedEnsembleProvider", (ensembleProvider instanceof FixedEnsembleProvider));
        Assert.assertEquals("The connectionString should be 'hostA'", "hostA", ensembleProvider.getConnectionString());
    }

    @Test
    public void exhibitorEnsembleProvider() {
        Properties props = new Properties();
        props.put(CuratorModuleTest.curatorHostKey, "hostA");
        props.put(CuratorModuleTest.exhibitorHostsKey, "[\"hostB\"]");
        Injector injector = newInjector(props);
        injector.getInstance(CuratorFramework.class);// initialize related components

        EnsembleProvider ensembleProvider = injector.getInstance(EnsembleProvider.class);
        Assert.assertTrue("EnsembleProvider should be ExhibitorEnsembleProvider", (ensembleProvider instanceof ExhibitorEnsembleProvider));
    }

    @Test
    public void emptyExhibitorHosts() {
        Properties props = new Properties();
        props.put(CuratorModuleTest.curatorHostKey, "hostB");
        props.put(CuratorModuleTest.exhibitorHostsKey, "[]");
        Injector injector = newInjector(props);
        injector.getInstance(CuratorFramework.class);// initialize related components

        EnsembleProvider ensembleProvider = injector.getInstance(EnsembleProvider.class);
        Assert.assertTrue("EnsembleProvider should be FixedEnsembleProvider", (ensembleProvider instanceof FixedEnsembleProvider));
        Assert.assertEquals("The connectionString should be 'hostB'", "hostB", ensembleProvider.getConnectionString());
    }
}

