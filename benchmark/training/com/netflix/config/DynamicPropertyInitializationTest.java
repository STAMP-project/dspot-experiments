/**
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.config;


import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.junit.Assert;
import org.junit.Test;


public class DynamicPropertyInitializationTest {
    private volatile Object lastModified;

    ConfigurationListener listener = new ConfigurationListener() {
        @Override
        public void configurationChanged(ConfigurationEvent arg0) {
            if (!(arg0.isBeforeUpdate())) {
                lastModified = arg0.getPropertyValue();
            }
        }
    };

    @Test
    public void testDefaultConfig() {
        System.setProperty("xyz", "fromSystem");
        DynamicStringProperty prop = new DynamicStringProperty("xyz", null);
        Assert.assertNotNull(DynamicPropertyFactory.getBackingConfigurationSource());
        Assert.assertEquals("fromSystem", prop.get());
        ConfigurationManager.getConfigInstance().addConfigurationListener(listener);
        // Because SystemProperties default to higher priority than application settings, this set will no-op
        ConfigurationManager.getConfigInstance().setProperty("xyz", "override");
        Assert.assertEquals("fromSystem", prop.get());
        Assert.assertEquals(null, lastModified);
        BaseConfiguration newConfig = new BaseConfiguration();
        newConfig.setProperty("xyz", "fromNewConfig");
        ConfigurationManager.install(newConfig);
        Assert.assertEquals("fromNewConfig", prop.get());
        ConfigurationManager.getConfigInstance().setProperty("xyz", "new");
        Assert.assertEquals("new", lastModified);
        Assert.assertEquals("new", prop.get());
        Assert.assertEquals(3, newConfig.getConfigurationListeners().size());
    }
}

