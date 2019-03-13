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


import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class InitWithNullConfigTest {
    @Test
    public void testCreateProperty() {
        try {
            DynamicPropertyFactory.initWithConfigurationSource(((AbstractConfiguration) (null)));
            Assert.fail("NPE expected");
        } catch (NullPointerException e) {
            Assert.assertNotNull(e);
        }
        DynamicBooleanProperty prop = DynamicPropertyFactory.getInstance().getBooleanProperty("abc", false);
        BaseConfiguration baseConfig = new BaseConfiguration();
        DynamicPropertyFactory.initWithConfigurationSource(baseConfig);
        baseConfig.setProperty("abc", "true");
        Assert.assertTrue(prop.get());
    }
}

