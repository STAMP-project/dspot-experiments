/**
 * Copyright 2017 NAVER Corp.
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
package com.navercorp.pinpoint.plugin.hikaricp;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Taejin Koo
 */
public class HikariCpConfigTest {
    @Test
    public void configTest1() throws Exception {
        HikariCpConfig hikariCpConfig = createHikariCpConfig("false", "false");
        Assert.assertFalse(hikariCpConfig.isPluginEnable());
        Assert.assertFalse(hikariCpConfig.isProfileClose());
    }

    @Test
    public void configTest2() throws Exception {
        HikariCpConfig hikariCpConfig = createHikariCpConfig("false", "true");
        Assert.assertFalse(hikariCpConfig.isPluginEnable());
        Assert.assertTrue(hikariCpConfig.isProfileClose());
    }

    @Test
    public void configTest3() throws Exception {
        HikariCpConfig hikariCpConfig = createHikariCpConfig("true", "false");
        Assert.assertTrue(hikariCpConfig.isPluginEnable());
        Assert.assertFalse(hikariCpConfig.isProfileClose());
    }

    @Test
    public void configTest4() throws Exception {
        HikariCpConfig hikariCpConfig = createHikariCpConfig("true", "true");
        Assert.assertTrue(hikariCpConfig.isPluginEnable());
        Assert.assertTrue(hikariCpConfig.isProfileClose());
    }
}

