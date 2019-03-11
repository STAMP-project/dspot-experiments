package com.thinkaurelius.titan.diskstorage.configuration;


import BasicConfiguration.Restriction;
import ConfigOption.Type;
import GraphDatabaseConfiguration.LOG_NS;
import ReflectiveConfigOptionLoader.INSTANCE;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Assert;
import org.junit.Test;


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
        ConfigOption<String[]> hostnames = new ConfigOption<String[]>(storage, "hostname", "Storage backend hostname", Type.LOCAL, String[].class);
        ConfigOption<Boolean> partition = new ConfigOption<Boolean>(storage, "partition", "whether to enable partition", Type.MASKABLE, false);
        ConfigOption<Long> locktime = new ConfigOption<Long>(storage, "locktime", "how long to lock", Type.FIXED, 500L);
        ConfigOption<Byte> bits = new ConfigOption<Byte>(storage, "bits", "number of unique bits", Type.GLOBAL_OFFLINE, ((byte) (8)));
        ConfigOption<Short> retry = new ConfigOption<Short>(special, "retry", "retry wait time", Type.GLOBAL, ((short) (200)));
        ConfigOption<Double> bar = new ConfigOption<Double>(special, "bar", "bar", Type.GLOBAL, 1.5);
        ConfigOption<Integer> bim = new ConfigOption<Integer>(special, "bim", "bim", Type.MASKABLE, Integer.class);
        ConfigOption<String> indexback = new ConfigOption<String>(indexes, "name", "index name", Type.MASKABLE, String.class);
        ConfigOption<Integer> ping = new ConfigOption<Integer>(indexes, "ping", "ping time", Type.LOCAL, 100);
        ConfigOption<Boolean> presort = new ConfigOption<Boolean>(indexes, "presort", "presort result set", Type.LOCAL, false);
        // Local configuration
        ModifiableConfiguration config = new ModifiableConfiguration(root, new com.thinkaurelius.titan.diskstorage.configuration.backend.CommonsConfiguration(new BaseConfiguration()), Restriction.LOCAL);
        UserModifiableConfiguration userconfig = new UserModifiableConfiguration(config);
        Assert.assertFalse(config.get(partition));
        Assert.assertEquals("false", userconfig.get("storage.partition"));
        userconfig.set("storage.partition", true);
        Assert.assertEquals("true", userconfig.get("storage.partition"));
        userconfig.set("storage.hostname", new String[]{ "localhost", "some.where.org" });
        Assert.assertEquals("[localhost,some.where.org]", userconfig.get("storage.hostname"));
        userconfig.set("storage.hostname", "localhost");
        Assert.assertEquals("[localhost]", userconfig.get("storage.hostname"));
        Assert.assertEquals("null", userconfig.get("storage.special.bim"));
        Assert.assertEquals("", userconfig.get("indexes"));
        userconfig.set("indexes.search.name", "foo");
        Assert.assertEquals("+ search", userconfig.get("indexes").trim());
        Assert.assertEquals("foo", userconfig.get("indexes.search.name"));
        Assert.assertEquals("100", userconfig.get("indexes.search.ping"));
        userconfig.set("indexes.search.ping", 400L);
        Assert.assertEquals("400", userconfig.get("indexes.search.ping"));
        Assert.assertFalse(config.isFrozen());
        try {
            userconfig.set("storage.locktime", 500);
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        try {
            config.set(retry, ((short) (100)));
            Assert.fail();
        } catch (IllegalArgumentException e) {
        }
        // System.out.println(userconfig.get("storage"));
        userconfig.close();
        ReadConfiguration localConfig = userconfig.getConfiguration();
        config = new ModifiableConfiguration(root, new com.thinkaurelius.titan.diskstorage.configuration.backend.CommonsConfiguration(new BaseConfiguration()), Restriction.GLOBAL);
        userconfig = new UserModifiableConfiguration(config);
        userconfig.set("storage.locktime", 1111);
        userconfig.set("storage.bits", 5);
        userconfig.set("storage.special.retry", 222);
        Assert.assertEquals("5", userconfig.get("storage.bits"));
        Assert.assertEquals("222", userconfig.get("storage.special.retry"));
        config.freezeConfiguration();
        userconfig.set("storage.special.retry", 333);
        Assert.assertEquals("333", userconfig.get("storage.special.retry"));
        try {
            userconfig.set("storage.bits", 6);
        } catch (IllegalArgumentException e) {
        }
        userconfig.set("storage.bits", 6);
        try {
            userconfig.set("storage.locktime", 1221);
        } catch (IllegalArgumentException e) {
        }
        try {
            userconfig.set("storage.locktime", 1221);
        } catch (IllegalArgumentException e) {
        }
        userconfig.set("indexes.find.name", "lulu");
        userconfig.close();
        ReadConfiguration globalConfig = userconfig.getConfiguration();
        MixedConfiguration mixed = new MixedConfiguration(root, globalConfig, localConfig);
        Assert.assertEquals(ImmutableSet.of("search", "find"), mixed.getContainedNamespaces(indexes));
        Configuration search = mixed.restrictTo("search");
        Assert.assertEquals("foo", search.get(indexback));
        Assert.assertEquals(400, search.get(ping).intValue());
        Assert.assertEquals(100, mixed.get(ping, "find").intValue());
        Assert.assertEquals(false, mixed.get(presort, "find").booleanValue());
        Assert.assertEquals(400, mixed.get(ping, "search").intValue());
        Assert.assertEquals(false, mixed.get(presort, "search").booleanValue());
        Assert.assertFalse(mixed.has(bim));
        Assert.assertTrue(mixed.has(bits));
        Assert.assertEquals(5, mixed.getSubset(storage).size());
        Assert.assertEquals(1.5, mixed.get(bar).doubleValue(), 0.0);
        Assert.assertEquals("localhost", mixed.get(hostnames)[0]);
        Assert.assertEquals(1111, mixed.get(locktime).longValue());
        mixed.close();
        // System.out.println(ConfigElement.toString(root));
    }

    @Test
    public void testDisableConfOptReflection() {
        INSTANCE.setEnabled(false);
        INSTANCE.loadStandard(this.getClass());
        Assert.assertFalse(Iterables.any(LOG_NS.getChildren(), new Predicate<ConfigElement>() {
            @Override
            public boolean apply(ConfigElement elem) {
                return (elem instanceof ConfigOption<?>) && (elem.getName().equals("max-write-time"));
            }
        }));
        INSTANCE.setEnabled(true);
        INSTANCE.loadStandard(this.getClass());
        Assert.assertTrue(Iterables.any(LOG_NS.getChildren(), new Predicate<ConfigElement>() {
            @Override
            public boolean apply(ConfigElement elem) {
                return (elem instanceof ConfigOption<?>) && (elem.getName().equals("max-write-time"));
            }
        }));
    }
}

