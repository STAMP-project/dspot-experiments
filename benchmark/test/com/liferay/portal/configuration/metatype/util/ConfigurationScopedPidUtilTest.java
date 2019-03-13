/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.portal.configuration.metatype.util;


import ExtendedObjectClassDefinition.Scope.COMPANY;
import ExtendedObjectClassDefinition.Scope.GROUP;
import ExtendedObjectClassDefinition.Scope.PORTLET_INSTANCE;
import ExtendedObjectClassDefinition.Scope.SYSTEM;
import com.liferay.petra.string.StringBundler;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Drew Brokke
 */
public class ConfigurationScopedPidUtilTest {
    @Test
    public void testBuildScopedPid() {
        Assert.assertEquals(_basePid, ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, SYSTEM, _scopePrimKey));
        Assert.assertEquals(_basePid, ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, null, _scopePrimKey));
        Assert.assertEquals(StringBundler.concat(_basePid, COMPANY.getDelimiterString(), _scopePrimKey), ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, COMPANY, _scopePrimKey));
        Assert.assertEquals(StringBundler.concat(_basePid, GROUP.getDelimiterString(), _scopePrimKey), ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, GROUP, _scopePrimKey));
        Assert.assertEquals(StringBundler.concat(_basePid, PORTLET_INSTANCE.getDelimiterString(), _scopePrimKey), ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, PORTLET_INSTANCE, _scopePrimKey));
        try {
            ConfigurationScopedPidUtil.buildConfigurationScopedPid(null, COMPANY, _scopePrimKey);
            Assert.fail(("Build configuration scoped PID must not allow a null base " + "PID"));
        } catch (NullPointerException npe) {
        }
        try {
            ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, COMPANY, null);
            Assert.fail(("Build configuration scoped PID must not allow a null scope " + "primary key"));
        } catch (NullPointerException npe) {
        }
    }

    @Test
    public void testGetBasePid() {
        Assert.assertEquals(null, ConfigurationScopedPidUtil.getBasePid(null));
        Assert.assertEquals(_basePid, ConfigurationScopedPidUtil.getBasePid(_basePid));
        String encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, COMPANY, _scopePrimKey);
        Assert.assertEquals(_basePid, ConfigurationScopedPidUtil.getBasePid(encodedPid));
        encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, GROUP, _scopePrimKey);
        Assert.assertEquals(_basePid, ConfigurationScopedPidUtil.getBasePid(encodedPid));
        encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, PORTLET_INSTANCE, _scopePrimKey);
        Assert.assertEquals(_basePid, ConfigurationScopedPidUtil.getBasePid(encodedPid));
    }

    @Test
    public void testGetScope() {
        Assert.assertEquals(null, ConfigurationScopedPidUtil.getScope(null));
        Assert.assertEquals(SYSTEM, ConfigurationScopedPidUtil.getScope(_basePid));
        String encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, COMPANY, _scopePrimKey);
        Assert.assertEquals(COMPANY, ConfigurationScopedPidUtil.getScope(encodedPid));
        encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, GROUP, _scopePrimKey);
        Assert.assertEquals(GROUP, ConfigurationScopedPidUtil.getScope(encodedPid));
        encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, PORTLET_INSTANCE, _scopePrimKey);
        Assert.assertEquals(PORTLET_INSTANCE, ConfigurationScopedPidUtil.getScope(encodedPid));
    }

    @Test
    public void testGetScopePK() {
        Assert.assertEquals(null, ConfigurationScopedPidUtil.getScopePrimKey(null));
        Assert.assertEquals(null, ConfigurationScopedPidUtil.getScopePrimKey(_basePid));
        String encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, COMPANY, _scopePrimKey);
        Assert.assertEquals(_scopePrimKey, ConfigurationScopedPidUtil.getScopePrimKey(encodedPid));
        encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, GROUP, _scopePrimKey);
        Assert.assertEquals(_scopePrimKey, ConfigurationScopedPidUtil.getScopePrimKey(encodedPid));
        encodedPid = ConfigurationScopedPidUtil.buildConfigurationScopedPid(_basePid, PORTLET_INSTANCE, _scopePrimKey);
        Assert.assertEquals(_scopePrimKey, ConfigurationScopedPidUtil.getScopePrimKey(encodedPid));
    }

    @Test
    public void testGetScopeSeparatorString() {
        Assert.assertEquals("__COMPANY__", COMPANY.getDelimiterString());
        Assert.assertEquals("__GROUP__", GROUP.getDelimiterString());
        Assert.assertEquals("__PORTLET_INSTANCE__", PORTLET_INSTANCE.getDelimiterString());
        Assert.assertEquals("__SYSTEM__", SYSTEM.getDelimiterString());
    }

    private String _basePid;

    private String _scopePrimKey;
}

