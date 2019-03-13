/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package org.janusgraph.diskstorage.configuration;


import BasicConfiguration.Restriction;
import ConfigOption.Type;
import GraphDatabaseConfiguration.LOG_NS;
import ReflectiveConfigOptionLoader.INSTANCE;
import com.google.common.collect.ImmutableSet;
import java.util.stream.StreamSupport;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class ConfigurationTest {
    @Test
    public void testConfigHierarchy() {
        ConfigNamespace root = new ConfigNamespace(null, "config", "root");
        ConfigNamespace indexes = new ConfigNamespace(root, "indexes", "Index definitions", true);
        ConfigNamespace storage = new ConfigNamespace(root, "storage", "Storage definitions");
        ConfigNamespace special = new ConfigNamespace(storage, "special", "Special storage definitions");
        ConfigOption<String[]> hostnames = new ConfigOption(storage, "hostname", "Storage backend hostname", Type.LOCAL, String[].class);
        ConfigOption<Boolean> partition = new ConfigOption(storage, "partition", "whether to enable partition", Type.MASKABLE, false);
        ConfigOption<Long> locktime = new ConfigOption(storage, "locktime", "how long to lock", Type.FIXED, 500L);
        ConfigOption<Byte> bits = new ConfigOption(storage, "bits", "number of unique bits", Type.GLOBAL_OFFLINE, ((byte) (8)));
        ConfigOption<Short> retry = new ConfigOption(special, "retry", "retry wait time", Type.GLOBAL, ((short) (200)));
        ConfigOption<Double> bar = new ConfigOption(special, "bar", "bar", Type.GLOBAL, 1.5);
        ConfigOption<Integer> bim = new ConfigOption(special, "bim", "bim", Type.MASKABLE, Integer.class);
        ConfigOption<String> indexback = new ConfigOption(indexes, "name", "index name", Type.MASKABLE, String.class);
        ConfigOption<Integer> ping = new ConfigOption(indexes, "ping", "ping time", Type.LOCAL, 100);
        ConfigOption<Boolean> presort = new ConfigOption(indexes, "presort", "presort result set", Type.LOCAL, false);
        // Local configuration
        ModifiableConfiguration config = new ModifiableConfiguration(root, new org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration(new BaseConfiguration()), Restriction.LOCAL);
        UserModifiableConfiguration userconfig = new UserModifiableConfiguration(config);
        Assertions.assertFalse(config.get(partition));
        Assertions.assertEquals("false", userconfig.get("storage.partition"));
        userconfig.set("storage.partition", true);
        Assertions.assertEquals("true", userconfig.get("storage.partition"));
        userconfig.set("storage.hostname", new String[]{ "localhost", "some.where.org" });
        Assertions.assertEquals("[localhost,some.where.org]", userconfig.get("storage.hostname"));
        userconfig.set("storage.hostname", "localhost");
        Assertions.assertEquals("[localhost]", userconfig.get("storage.hostname"));
        Assertions.assertEquals("null", userconfig.get("storage.special.bim"));
        Assertions.assertEquals("", userconfig.get("indexes"));
        userconfig.set("indexes.search.name", "foo");
        Assertions.assertEquals("+ search", userconfig.get("indexes").trim());
        Assertions.assertEquals("foo", userconfig.get("indexes.search.name"));
        Assertions.assertEquals("100", userconfig.get("indexes.search.ping"));
        userconfig.set("indexes.search.ping", 400L);
        Assertions.assertEquals("400", userconfig.get("indexes.search.ping"));
        Assertions.assertFalse(config.isFrozen());
        try {
            userconfig.set("storage.locktime", 500);
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            config.set(retry, ((short) (100)));
            Assertions.fail();
        } catch (IllegalArgumentException ignored) {
        }
        // System.out.println(userconfig.get("storage"));
        userconfig.close();
        ReadConfiguration localConfig = userconfig.getConfiguration();
        config = new ModifiableConfiguration(root, new org.janusgraph.diskstorage.configuration.backend.CommonsConfiguration(new BaseConfiguration()), Restriction.GLOBAL);
        userconfig = new UserModifiableConfiguration(config);
        userconfig.set("storage.locktime", 1111);
        userconfig.set("storage.bits", 5);
        userconfig.set("storage.special.retry", 222);
        Assertions.assertEquals("5", userconfig.get("storage.bits"));
        Assertions.assertEquals("222", userconfig.get("storage.special.retry"));
        config.freezeConfiguration();
        userconfig.set("storage.special.retry", 333);
        Assertions.assertEquals("333", userconfig.get("storage.special.retry"));
        try {
            userconfig.set("storage.bits", 6);
        } catch (IllegalArgumentException ignored) {
        }
        userconfig.set("storage.bits", 6);
        try {
            userconfig.set("storage.locktime", 1221);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            userconfig.set("storage.locktime", 1221);
        } catch (IllegalArgumentException ignored) {
        }
        userconfig.set("indexes.find.name", "lulu");
        userconfig.close();
        ReadConfiguration globalConfig = userconfig.getConfiguration();
        MixedConfiguration mixed = new MixedConfiguration(root, globalConfig, localConfig);
        Assertions.assertEquals(ImmutableSet.of("search", "find"), mixed.getContainedNamespaces(indexes));
        Configuration search = mixed.restrictTo("search");
        Assertions.assertEquals("foo", search.get(indexback));
        Assertions.assertEquals(400, search.get(ping).intValue());
        Assertions.assertEquals(100, mixed.get(ping, "find").intValue());
        Assertions.assertEquals(false, mixed.get(presort, "find"));
        Assertions.assertEquals(400, mixed.get(ping, "search").intValue());
        Assertions.assertEquals(false, mixed.get(presort, "search"));
        Assertions.assertFalse(mixed.has(bim));
        Assertions.assertTrue(mixed.has(bits));
        Assertions.assertEquals(5, mixed.getSubset(storage).size());
        Assertions.assertEquals(1.5, ((double) (mixed.get(bar))));
        Assertions.assertEquals("localhost", mixed.get(hostnames)[0]);
        Assertions.assertEquals(1111, mixed.get(locktime).longValue());
        mixed.close();
        // System.out.println(ConfigElement.toString(root));
    }

    @Test
    public void testDisableConfOptReflection() {
        INSTANCE.setEnabled(false);
        INSTANCE.loadStandard(this.getClass());
        Assertions.assertFalse(StreamSupport.stream(LOG_NS.getChildren().spliterator(), false).anyMatch(( elem) -> (elem instanceof ConfigOption<?>) && (elem.getName().equals("max-write-time"))));
        INSTANCE.setEnabled(true);
        INSTANCE.loadStandard(this.getClass());
        Assertions.assertTrue(StreamSupport.stream(LOG_NS.getChildren().spliterator(), false).anyMatch(( elem) -> (elem instanceof ConfigOption<?>) && (elem.getName().equals("max-write-time"))));
    }
}

