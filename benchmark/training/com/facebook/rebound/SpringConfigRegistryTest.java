/**
 * Copyright (c) 2013, Facebook, Inc.
 *  All rights reserved.
 *
 *  This source code is licensed under the BSD-style license found in the
 *  LICENSE file in the root directory of this source tree. An additional grant
 *  of patent rights can be found in the PATENTS file in the same directory.
 */
package com.facebook.rebound;


import SpringConfig.defaultConfig;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class SpringConfigRegistryTest {
    private static final String CONFIG_NAME = "test config";

    private static final double TENSION = defaultConfig.tension;

    private static final double FRICTION = defaultConfig.friction;

    private SpringConfigRegistry mSpringConfigRegistrySpy;

    @Test
    public void testAddSpringConfig() {
        SpringConfig config = new SpringConfig(SpringConfigRegistryTest.TENSION, SpringConfigRegistryTest.FRICTION);
        mSpringConfigRegistrySpy.addSpringConfig(config, SpringConfigRegistryTest.CONFIG_NAME);
        Map<SpringConfig, String> configs = mSpringConfigRegistrySpy.getAllSpringConfig();
        Assert.assertEquals(1, configs.size());
        Assert.assertTrue(configs.containsKey(config));
        String configName = configs.get(config);
        Assert.assertEquals(configName, SpringConfigRegistryTest.CONFIG_NAME);
    }

    @Test
    public void testRemoveSpringConfig() {
        SpringConfig config = new SpringConfig(SpringConfigRegistryTest.TENSION, SpringConfigRegistryTest.FRICTION);
        mSpringConfigRegistrySpy.addSpringConfig(config, SpringConfigRegistryTest.CONFIG_NAME);
        Map<SpringConfig, String> configs = mSpringConfigRegistrySpy.getAllSpringConfig();
        Assert.assertEquals(1, configs.size());
        mSpringConfigRegistrySpy.removeSpringConfig(config);
        configs = mSpringConfigRegistrySpy.getAllSpringConfig();
        Assert.assertEquals(0, configs.size());
    }

    @Test
    public void testRemoveAllSpringConfig() {
        SpringConfig configA = new SpringConfig(0, 0);
        SpringConfig configB = new SpringConfig(0, 0);
        SpringConfig configC = new SpringConfig(0, 0);
        mSpringConfigRegistrySpy.addSpringConfig(configA, "a");
        mSpringConfigRegistrySpy.addSpringConfig(configB, "b");
        mSpringConfigRegistrySpy.addSpringConfig(configC, "c");
        Map<SpringConfig, String> configs = mSpringConfigRegistrySpy.getAllSpringConfig();
        Assert.assertEquals(3, configs.size());
        mSpringConfigRegistrySpy.removeAllSpringConfig();
        configs = mSpringConfigRegistrySpy.getAllSpringConfig();
        Assert.assertEquals(0, configs.size());
    }
}

