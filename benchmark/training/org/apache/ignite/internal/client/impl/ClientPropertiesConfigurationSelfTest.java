/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.client.impl;


import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.apache.ignite.internal.client.GridClientConfiguration;
import org.apache.ignite.internal.client.GridClientDataConfiguration;
import org.apache.ignite.internal.client.GridClientPartitionAffinity;
import org.apache.ignite.internal.client.balancer.GridClientRandomBalancer;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;


/**
 * Properties-based configuration self test.
 */
public class ClientPropertiesConfigurationSelfTest extends GridCommonAbstractTest {
    /**
     * Grid client spring configuration.
     */
    private static final URL GRID_CLIENT_SPRING_CONFIG;

    /**
     * Grid client properties-based configuration.
     */
    private static final URL GRID_CLIENT_CONFIG;

    /**
     *
     */
    static {
        GRID_CLIENT_SPRING_CONFIG = U.resolveIgniteUrl("/modules/clients/config/grid-client-spring-config.xml");
        GRID_CLIENT_CONFIG = U.resolveIgniteUrl("/modules/clients/config/grid-client-config.properties");
    }

    /**
     * Test client configuration loaded from the properties.
     *
     * @throws Exception
     * 		In case of exception.
     */
    @Test
    public void testCreation() throws Exception {
        // Validate default configuration.
        GridClientConfiguration cfg = new GridClientConfiguration();
        cfg.setServers(Arrays.asList("localhost:11211"));
        validateConfig(0, cfg);
        // Validate default properties-based configuration.
        cfg = new GridClientConfiguration();
        cfg.setServers(Arrays.asList("localhost:11211"));
        validateConfig(0, cfg);
        // Validate loaded configuration.
        Properties props = loadProperties(1, ClientPropertiesConfigurationSelfTest.GRID_CLIENT_CONFIG);
        validateConfig(0, new GridClientConfiguration(props));
        // Validate loaded configuration with changed key prefixes.
        Properties props2 = new Properties();
        for (Map.Entry<Object, Object> e : props.entrySet())
            props2.put(("new." + (e.getKey())), e.getValue());

        validateConfig(0, new GridClientConfiguration("new.ignite.client", props2));
        validateConfig(0, new GridClientConfiguration("new.ignite.client.", props2));
        // Validate loaded test configuration.
        File tmp = uncommentProperties(ClientPropertiesConfigurationSelfTest.GRID_CLIENT_CONFIG);
        props = loadProperties(25, tmp.toURI().toURL());
        validateConfig(2, new GridClientConfiguration(props));
        // Validate loaded test configuration with changed key prefixes.
        props2 = new Properties();
        for (Map.Entry<Object, Object> e : props.entrySet())
            props2.put(("new." + (e.getKey())), e.getValue());

        validateConfig(2, new GridClientConfiguration("new.ignite.client", props2));
        validateConfig(2, new GridClientConfiguration("new.ignite.client.", props2));
        // Validate loaded test configuration with empty key prefixes.
        props2 = new Properties();
        for (Map.Entry<Object, Object> e : props.entrySet())
            props2.put(e.getKey().toString().replace("ignite.client.", ""), e.getValue());

        validateConfig(2, new GridClientConfiguration("", props2));
        validateConfig(2, new GridClientConfiguration(".", props2));
    }

    /**
     * Validate spring client configuration.
     *
     * @throws Exception
     * 		In case of any exception.
     */
    @Test
    public void testSpringConfig() throws Exception {
        GridClientConfiguration cfg = new FileSystemXmlApplicationContext(ClientPropertiesConfigurationSelfTest.GRID_CLIENT_SPRING_CONFIG.toString()).getBean(GridClientConfiguration.class);
        assertEquals(Arrays.asList("127.0.0.1:11211"), new java.util.ArrayList(cfg.getServers()));
        assertNull(cfg.getSecurityCredentialsProvider());
        Collection<GridClientDataConfiguration> dataCfgs = cfg.getDataConfigurations();
        assertEquals(1, dataCfgs.size());
        GridClientDataConfiguration dataCfg = dataCfgs.iterator().next();
        assertEquals("partitioned", dataCfg.getName());
        assertNotNull(dataCfg.getPinnedBalancer());
        assertEquals(GridClientRandomBalancer.class, dataCfg.getPinnedBalancer().getClass());
        assertNotNull(dataCfg.getAffinity());
        assertEquals(GridClientPartitionAffinity.class, dataCfg.getAffinity().getClass());
    }
}

