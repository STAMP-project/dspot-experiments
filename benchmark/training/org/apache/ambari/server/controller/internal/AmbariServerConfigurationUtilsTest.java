/**
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
package org.apache.ambari.server.controller.internal;


import org.apache.ambari.server.configuration.AmbariServerConfigurationCategory;
import org.apache.ambari.server.configuration.AmbariServerConfigurationKey;
import org.apache.ambari.server.configuration.ConfigurationPropertyType;
import org.junit.Assert;
import org.junit.Test;


public class AmbariServerConfigurationUtilsTest {
    @Test
    public void testGetConfigurationKey() {
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED, AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED, AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        // Test Regex Key
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS, AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.key()));
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS, AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.knox.groups"));
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS, AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.not.knox.groups"));
        Assert.assertNull(AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "invalid.tproxy.proxyuser.not.knox.groups"));
        Assert.assertNull(AmbariServerConfigurationUtils.getConfigurationKey(((AmbariServerConfigurationCategory) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertNull(AmbariServerConfigurationUtils.getConfigurationKey(((String) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertNull(AmbariServerConfigurationUtils.getConfigurationKey("invalid", AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertNull(AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), null));
        Assert.assertNull(AmbariServerConfigurationUtils.getConfigurationKey(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), "invalid"));
    }

    @Test
    public void testGetConfigurationPropertyType() {
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationPropertyType(), AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationPropertyType(), AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        // Test Regex Key
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType(), AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.key()));
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType(), AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.knox.groups"));
        Assert.assertSame(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType(), AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.not.knox.groups"));
        Assert.assertSame(ConfigurationPropertyType.UNKNOWN, AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "invalid.tproxy.proxyuser.not.knox.groups"));
        Assert.assertSame(ConfigurationPropertyType.UNKNOWN, AmbariServerConfigurationUtils.getConfigurationPropertyType(((AmbariServerConfigurationCategory) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertSame(ConfigurationPropertyType.UNKNOWN, AmbariServerConfigurationUtils.getConfigurationPropertyType(((String) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertSame(ConfigurationPropertyType.UNKNOWN, AmbariServerConfigurationUtils.getConfigurationPropertyType("invalid", AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertSame(ConfigurationPropertyType.UNKNOWN, AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), null));
        Assert.assertSame(ConfigurationPropertyType.UNKNOWN, AmbariServerConfigurationUtils.getConfigurationPropertyType(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), "invalid"));
    }

    @Test
    public void testGetConfigurationPropertyTypeName() {
        Assert.assertEquals(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationPropertyType().name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertEquals(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationPropertyType().name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        // Test Regex Key
        Assert.assertEquals(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType().name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.key()));
        Assert.assertEquals(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType().name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.knox.groups"));
        Assert.assertEquals(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType().name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.not.knox.groups"));
        Assert.assertEquals(ConfigurationPropertyType.UNKNOWN.name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "invalid.tproxy.proxyuser.not.knox.groups"));
        Assert.assertEquals(ConfigurationPropertyType.UNKNOWN.name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(((AmbariServerConfigurationCategory) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertEquals(ConfigurationPropertyType.UNKNOWN.name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(((String) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertEquals(ConfigurationPropertyType.UNKNOWN.name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName("invalid", AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertEquals(ConfigurationPropertyType.UNKNOWN.name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), null));
        Assert.assertEquals(ConfigurationPropertyType.UNKNOWN.name(), AmbariServerConfigurationUtils.getConfigurationPropertyTypeName(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), "invalid"));
    }

    @Test
    public void isPassword() {
        Assert.assertEquals(((AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationPropertyType()) == (ConfigurationPropertyType.PASSWORD)), AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertEquals(((AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationPropertyType()) == (ConfigurationPropertyType.PASSWORD)), AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        // Test Regex Key
        Assert.assertEquals(((AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType()) == (ConfigurationPropertyType.PASSWORD)), AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.key()));
        Assert.assertEquals(((AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType()) == (ConfigurationPropertyType.PASSWORD)), AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.knox.groups"));
        Assert.assertEquals(((AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationPropertyType()) == (ConfigurationPropertyType.PASSWORD)), AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "ambari.tproxy.proxyuser.not.knox.groups"));
        Assert.assertFalse(AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.TPROXY_ALLOWED_GROUPS.getConfigurationCategory().getCategoryName(), "invalid.tproxy.proxyuser.not.knox.groups"));
        Assert.assertFalse(AmbariServerConfigurationUtils.isPassword(((AmbariServerConfigurationCategory) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertFalse(AmbariServerConfigurationUtils.isPassword(((String) (null)), AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertFalse(AmbariServerConfigurationUtils.isPassword("invalid", AmbariServerConfigurationKey.TPROXY_AUTHENTICATION_ENABLED.key()));
        Assert.assertFalse(AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), null));
        Assert.assertFalse(AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationCategory.TPROXY_CONFIGURATION.getCategoryName(), "invalid"));
        // This is known to be a password
        Assert.assertTrue(AmbariServerConfigurationUtils.isPassword(AmbariServerConfigurationKey.BIND_PASSWORD));
    }
}

