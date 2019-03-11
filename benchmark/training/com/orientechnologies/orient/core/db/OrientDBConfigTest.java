/**
 * *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.core.db;


import ATTRIBUTES.VALIDATION;
import OGlobalConfiguration.CLIENT_CONNECTION_STRATEGY;
import OGlobalConfiguration.DB_POOL_MAX;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class OrientDBConfigTest {
    @Test
    public void testBuildSettings() {
        OrientDBConfig settings = OrientDBConfig.builder().addConfig(DB_POOL_MAX, 20).addAttribute(VALIDATION, true).build();
        Assert.assertEquals(settings.getConfigurations().getValue(DB_POOL_MAX), ((Integer) (20)));
        Assert.assertEquals(settings.getAttributes().get(VALIDATION), true);
    }

    @Test
    public void testBuildSettingsFromMap() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(DB_POOL_MAX.getKey(), 20);
        OrientDBConfig settings = OrientDBConfig.builder().fromMap(configs).build();
        Assert.assertEquals(settings.getConfigurations().getValue(DB_POOL_MAX), ((Integer) (20)));
    }

    @Test
    public void testBuildSettingsFromGlobalMap() {
        Map<OGlobalConfiguration, Object> configs = new HashMap<>();
        configs.put(DB_POOL_MAX, 20);
        OrientDBConfig settings = OrientDBConfig.builder().fromGlobalMap(configs).build();
        Assert.assertEquals(settings.getConfigurations().getValue(DB_POOL_MAX), ((Integer) (20)));
    }

    @Test
    public void testParentConfig() {
        OrientDBConfig parent = OrientDBConfig.builder().addConfig(DB_POOL_MAX, 20).addAttribute(VALIDATION, true).build();
        OrientDBConfig settings = OrientDBConfig.builder().addConfig(CLIENT_CONNECTION_STRATEGY, "ROUND_ROBIN_CONNECT").addAttribute(VALIDATION, false).build();
        settings.setParent(parent);
        Assert.assertEquals(settings.getConfigurations().getValue(DB_POOL_MAX), ((Integer) (20)));
        Assert.assertEquals(settings.getConfigurations().getValue(CLIENT_CONNECTION_STRATEGY), "ROUND_ROBIN_CONNECT");
        Assert.assertEquals(settings.getAttributes().get(VALIDATION), false);
    }
}

