/**
 * Copyright (c) 2014, Cloudera, Inc. All Rights Reserved.
 *
 * Cloudera, Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"). You may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */
package com.cloudera.oryx.common.settings;


import com.cloudera.oryx.common.OryxTest;
import com.typesafe.config.Config;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link ConfigUtils}.
 */
public final class ConfigUtilsTest extends OryxTest {
    @Test
    public void testDefaultConfig() {
        Config config = ConfigUtils.getDefault();
        Assert.assertEquals("yarn", config.getString("oryx.batch.streaming.master"));
    }

    @Test
    public void testSerialize() {
        String serialized = ConfigUtils.serialize(ConfigUtils.getDefault());
        OryxTest.assertContains(serialized, "update-class");
        Config deserialized = ConfigUtils.deserialize(serialized);
        Assert.assertEquals(ConfigUtils.getDefault().getString("oryx.serving.api.port"), deserialized.getString("oryx.serving.api.port"));
    }

    @Test
    public void testOptionalString() {
        Config config = ConfigUtils.overlayOn(Collections.singletonMap("nonexistent", "null"), ConfigUtils.getDefault());
        Assert.assertNull(ConfigUtils.getOptionalString(config, "nonexistent"));
    }

    @Test
    public void testOptionalStringList() {
        Config config = ConfigUtils.overlayOn(Collections.singletonMap("nonexistent", "null"), ConfigUtils.getDefault());
        Assert.assertNull(ConfigUtils.getOptionalStringList(config, "nonexistent"));
    }

    @Test
    public void testOptionalDouble() {
        Config config = ConfigUtils.overlayOn(Collections.singletonMap("nonexistent", "null"), ConfigUtils.getDefault());
        Assert.assertNull(ConfigUtils.getOptionalDouble(config, "nonexistent"));
    }

    @Test
    public void testOverlayOn() {
        Map<String, Object> overlay = new HashMap<>();
        overlay.put("foo", "bar");
        Config config = ConfigUtils.overlayOn(overlay, ConfigUtils.getDefault());
        Assert.assertEquals("bar", config.getString("foo"));
    }

    @Test
    public void testSetPath() throws Exception {
        Path cwd = Paths.get(".");
        Map<String, Object> map = new HashMap<>();
        ConfigUtils.set(map, "cwd", cwd);
        ConfigUtils.set(map, "temp", Paths.get("/tmp"));
        Assert.assertEquals((("\"" + (cwd.toRealPath(LinkOption.NOFOLLOW_LINKS).toUri())) + "\""), map.get("cwd"));
        Assert.assertEquals("\"file:///tmp/\"", map.get("temp"));
    }

    @Test
    public void testPrettyPrint() {
        String pretty = ConfigUtils.prettyPrint(ConfigUtils.getDefault());
        // Spot checks:
        OryxTest.assertContains(pretty, "oryx {");
        OryxTest.assertContains(pretty, "batch {");
        OryxTest.assertContains(pretty, "master=yarn");
        OryxTest.assertContains(pretty, "password=*****");
        OryxTest.assertContains(pretty, "keystore-password=*****");
    }

    @Test
    public void testRedact() {
        String redacted = ConfigUtils.redact(("  password=foo \nPassword=foo\nPASSWORD = foo\n" + " the-password= foo \nThe-Password =foo"));
        Assert.assertFalse(redacted.contains("foo"));
        OryxTest.assertContains(redacted, "*****");
        OryxTest.assertContains(redacted, "password=");
        OryxTest.assertContains(redacted, "Password=");
        OryxTest.assertContains(redacted, "PASSWORD = ");
        OryxTest.assertContains(redacted, "the-password= ");
        OryxTest.assertContains(redacted, "The-Password =");
    }

    @Test
    public void tetKeyValuesToProperties() {
        Assert.assertEquals(new Properties(), ConfigUtils.keyValueToProperties());
        Properties expected = new Properties();
        expected.setProperty("foo", "1");
        expected.setProperty("2.0", "bing");
        Assert.assertEquals(expected, ConfigUtils.keyValueToProperties("foo", 1, 2.0, "bing"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadKeyValuesToProperties() {
        ConfigUtils.keyValueToProperties("key");
    }
}

