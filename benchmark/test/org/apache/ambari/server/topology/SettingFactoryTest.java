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
 * distributed under the License is distribut
 * ed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.server.topology;


import Setting.SETTING_NAME_RECOVERY_ENABLED;
import Setting.SETTING_NAME_RECOVERY_SETTINGS;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the SettingFactory class
 */
public class SettingFactoryTest {
    /**
     * Test collection of recovery_settings
     */
    @Test
    public void testGetSettingWithSetOfProperties() {
        SettingFactory settingFactory = new SettingFactory();
        Map<String, Set<HashMap<String, String>>> properties;
        Setting setting = settingFactory.getSetting(createSettingWithSetOfProperties());
        Set<HashMap<String, String>> propertyValues = setting.getSettingValue(SETTING_NAME_RECOVERY_SETTINGS);
        Assert.assertEquals(propertyValues.size(), 1);
        Assert.assertEquals(propertyValues.iterator().next().get(SETTING_NAME_RECOVERY_ENABLED), "true");
    }

    /**
     * Test single recovery_settings at root level
     */
    @Test
    public void testGetSettingWithoutSetOfProperties() {
        SettingFactory settingFactory = new SettingFactory();
        Map<String, Set<HashMap<String, String>>> properties;
        Setting setting = settingFactory.getSetting(createSettingWithoutSetOfProperties());
        Set<HashMap<String, String>> propertyValues = setting.getSettingValue(SETTING_NAME_RECOVERY_SETTINGS);
        Assert.assertEquals(propertyValues.size(), 1);
        Assert.assertEquals(propertyValues.iterator().next().get(SETTING_NAME_RECOVERY_ENABLED), "true");
    }
}

