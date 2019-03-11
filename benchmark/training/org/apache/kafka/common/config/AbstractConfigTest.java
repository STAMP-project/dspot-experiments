/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.config;


import ConfigDef.Type.STRING;
import Importance.HIGH;
import Importance.LOW;
import SaslConfigs.DEFAULT_KERBEROS_MIN_TIME_BEFORE_RELOGIN;
import Type.CLASS;
import Type.LIST;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.types.Password;
import org.apache.kafka.common.metrics.FakeMetricsReporter;
import org.apache.kafka.common.metrics.JmxReporter;
import org.apache.kafka.common.metrics.MetricsReporter;
import org.apache.kafka.common.security.TestSecurityConfig;
import org.junit.Assert;
import org.junit.Test;


public class AbstractConfigTest {
    @Test
    public void testConfiguredInstances() {
        testValidInputs("");
        testValidInputs("org.apache.kafka.common.metrics.FakeMetricsReporter");
        testValidInputs("org.apache.kafka.common.metrics.FakeMetricsReporter, org.apache.kafka.common.metrics.FakeMetricsReporter");
        testInvalidInputs(",");
        testInvalidInputs("org.apache.kafka.clients.producer.unknown-metrics-reporter");
        testInvalidInputs("test1,test2");
        testInvalidInputs("org.apache.kafka.common.metrics.FakeMetricsReporter,");
    }

    @Test
    public void testEmptyList() {
        AbstractConfig conf;
        ConfigDef configDef = new ConfigDef().define("a", LIST, "", new ConfigDef.NonNullValidator(), HIGH, "doc");
        conf = new AbstractConfig(configDef, Collections.emptyMap());
        Assert.assertEquals(Collections.emptyList(), conf.getList("a"));
        conf = new AbstractConfig(configDef, Collections.singletonMap("a", ""));
        Assert.assertEquals(Collections.emptyList(), conf.getList("a"));
        conf = new AbstractConfig(configDef, Collections.singletonMap("a", "b,c,d"));
        Assert.assertEquals(Arrays.asList("b", "c", "d"), conf.getList("a"));
    }

    @Test
    public void testOriginalsWithPrefix() {
        Properties props = new Properties();
        props.put("foo.bar", "abc");
        props.put("setting", "def");
        AbstractConfigTest.TestConfig config = new AbstractConfigTest.TestConfig(props);
        Map<String, Object> originalsWithPrefix = originalsWithPrefix("foo.");
        Assert.assertTrue(unused().contains("foo.bar"));
        originalsWithPrefix.get("bar");
        Assert.assertFalse(unused().contains("foo.bar"));
        Map<String, Object> expected = new HashMap<>();
        expected.put("bar", "abc");
        Assert.assertEquals(expected, originalsWithPrefix);
    }

    @Test
    public void testValuesWithPrefixOverride() {
        String prefix = "prefix.";
        Properties props = new Properties();
        props.put("sasl.mechanism", "PLAIN");
        props.put("prefix.sasl.mechanism", "GSSAPI");
        props.put("prefix.sasl.kerberos.kinit.cmd", "/usr/bin/kinit2");
        props.put("prefix.ssl.truststore.location", "my location");
        props.put("sasl.kerberos.service.name", "service name");
        props.put("ssl.keymanager.algorithm", "algorithm");
        TestSecurityConfig config = new TestSecurityConfig(props);
        Map<String, Object> valuesWithPrefixOverride = valuesWithPrefixOverride(prefix);
        // prefix overrides global
        Assert.assertTrue(unused().contains("prefix.sasl.mechanism"));
        Assert.assertTrue(unused().contains("sasl.mechanism"));
        Assert.assertEquals("GSSAPI", valuesWithPrefixOverride.get("sasl.mechanism"));
        Assert.assertFalse(unused().contains("sasl.mechanism"));
        Assert.assertFalse(unused().contains("prefix.sasl.mechanism"));
        // prefix overrides default
        Assert.assertTrue(unused().contains("prefix.sasl.kerberos.kinit.cmd"));
        Assert.assertFalse(unused().contains("sasl.kerberos.kinit.cmd"));
        Assert.assertEquals("/usr/bin/kinit2", valuesWithPrefixOverride.get("sasl.kerberos.kinit.cmd"));
        Assert.assertFalse(unused().contains("sasl.kerberos.kinit.cmd"));
        Assert.assertFalse(unused().contains("prefix.sasl.kerberos.kinit.cmd"));
        // prefix override with no default
        Assert.assertTrue(unused().contains("prefix.ssl.truststore.location"));
        Assert.assertFalse(unused().contains("ssl.truststore.location"));
        Assert.assertEquals("my location", valuesWithPrefixOverride.get("ssl.truststore.location"));
        Assert.assertFalse(unused().contains("ssl.truststore.location"));
        Assert.assertFalse(unused().contains("prefix.ssl.truststore.location"));
        // global overrides default
        Assert.assertTrue(unused().contains("ssl.keymanager.algorithm"));
        Assert.assertEquals("algorithm", valuesWithPrefixOverride.get("ssl.keymanager.algorithm"));
        Assert.assertFalse(unused().contains("ssl.keymanager.algorithm"));
        // global with no default
        Assert.assertTrue(unused().contains("sasl.kerberos.service.name"));
        Assert.assertEquals("service name", valuesWithPrefixOverride.get("sasl.kerberos.service.name"));
        Assert.assertFalse(unused().contains("sasl.kerberos.service.name"));
        // unset with default
        Assert.assertFalse(unused().contains("sasl.kerberos.min.time.before.relogin"));
        Assert.assertEquals(DEFAULT_KERBEROS_MIN_TIME_BEFORE_RELOGIN, valuesWithPrefixOverride.get("sasl.kerberos.min.time.before.relogin"));
        Assert.assertFalse(unused().contains("sasl.kerberos.min.time.before.relogin"));
        // unset with no default
        Assert.assertFalse(unused().contains("ssl.key.password"));
        Assert.assertNull(valuesWithPrefixOverride.get("ssl.key.password"));
        Assert.assertFalse(unused().contains("ssl.key.password"));
    }

    @Test
    public void testValuesWithSecondaryPrefix() {
        String prefix = "listener.name.listener1.";
        Password saslJaasConfig1 = new Password("test.myLoginModule1 required;");
        Password saslJaasConfig2 = new Password("test.myLoginModule2 required;");
        Password saslJaasConfig3 = new Password("test.myLoginModule3 required;");
        Properties props = new Properties();
        props.put("listener.name.listener1.test-mechanism.sasl.jaas.config", saslJaasConfig1.value());
        props.put("test-mechanism.sasl.jaas.config", saslJaasConfig2.value());
        props.put("sasl.jaas.config", saslJaasConfig3.value());
        props.put("listener.name.listener1.gssapi.sasl.kerberos.kinit.cmd", "/usr/bin/kinit2");
        props.put("listener.name.listener1.gssapi.sasl.kerberos.service.name", "testkafka");
        props.put("listener.name.listener1.gssapi.sasl.kerberos.min.time.before.relogin", "60000");
        props.put("ssl.provider", "TEST");
        TestSecurityConfig config = new TestSecurityConfig(props);
        Map<String, Object> valuesWithPrefixOverride = valuesWithPrefixOverride(prefix);
        // prefix with mechanism overrides global
        Assert.assertTrue(unused().contains("listener.name.listener1.test-mechanism.sasl.jaas.config"));
        Assert.assertTrue(unused().contains("test-mechanism.sasl.jaas.config"));
        Assert.assertEquals(saslJaasConfig1, valuesWithPrefixOverride.get("test-mechanism.sasl.jaas.config"));
        Assert.assertEquals(saslJaasConfig3, valuesWithPrefixOverride.get("sasl.jaas.config"));
        Assert.assertFalse(unused().contains("listener.name.listener1.test-mechanism.sasl.jaas.config"));
        Assert.assertFalse(unused().contains("test-mechanism.sasl.jaas.config"));
        Assert.assertFalse(unused().contains("sasl.jaas.config"));
        // prefix with mechanism overrides default
        Assert.assertFalse(unused().contains("sasl.kerberos.kinit.cmd"));
        Assert.assertTrue(unused().contains("listener.name.listener1.gssapi.sasl.kerberos.kinit.cmd"));
        Assert.assertFalse(unused().contains("gssapi.sasl.kerberos.kinit.cmd"));
        Assert.assertFalse(unused().contains("sasl.kerberos.kinit.cmd"));
        Assert.assertEquals("/usr/bin/kinit2", valuesWithPrefixOverride.get("gssapi.sasl.kerberos.kinit.cmd"));
        Assert.assertFalse(unused().contains("listener.name.listener1.sasl.kerberos.kinit.cmd"));
        // prefix override for mechanism with no default
        Assert.assertFalse(unused().contains("sasl.kerberos.service.name"));
        Assert.assertTrue(unused().contains("listener.name.listener1.gssapi.sasl.kerberos.service.name"));
        Assert.assertFalse(unused().contains("gssapi.sasl.kerberos.service.name"));
        Assert.assertFalse(unused().contains("sasl.kerberos.service.name"));
        Assert.assertEquals("testkafka", valuesWithPrefixOverride.get("gssapi.sasl.kerberos.service.name"));
        Assert.assertFalse(unused().contains("listener.name.listener1.gssapi.sasl.kerberos.service.name"));
        // unset with no default
        Assert.assertTrue(unused().contains("ssl.provider"));
        Assert.assertNull(valuesWithPrefixOverride.get("gssapi.ssl.provider"));
        Assert.assertTrue(unused().contains("ssl.provider"));
    }

    @Test
    public void testValuesWithPrefixAllOrNothing() {
        String prefix1 = "prefix1.";
        String prefix2 = "prefix2.";
        Properties props = new Properties();
        props.put("sasl.mechanism", "PLAIN");
        props.put("prefix1.sasl.mechanism", "GSSAPI");
        props.put("prefix1.sasl.kerberos.kinit.cmd", "/usr/bin/kinit2");
        props.put("prefix1.ssl.truststore.location", "my location");
        props.put("sasl.kerberos.service.name", "service name");
        props.put("ssl.keymanager.algorithm", "algorithm");
        TestSecurityConfig config = new TestSecurityConfig(props);
        Map<String, Object> valuesWithPrefixAllOrNothing1 = valuesWithPrefixAllOrNothing(prefix1);
        // All prefixed values are there
        Assert.assertEquals("GSSAPI", valuesWithPrefixAllOrNothing1.get("sasl.mechanism"));
        Assert.assertEquals("/usr/bin/kinit2", valuesWithPrefixAllOrNothing1.get("sasl.kerberos.kinit.cmd"));
        Assert.assertEquals("my location", valuesWithPrefixAllOrNothing1.get("ssl.truststore.location"));
        // Non-prefixed values are missing
        Assert.assertFalse(valuesWithPrefixAllOrNothing1.containsKey("sasl.kerberos.service.name"));
        Assert.assertFalse(valuesWithPrefixAllOrNothing1.containsKey("ssl.keymanager.algorithm"));
        Map<String, Object> valuesWithPrefixAllOrNothing2 = valuesWithPrefixAllOrNothing(prefix2);
        Assert.assertTrue(valuesWithPrefixAllOrNothing2.containsKey("sasl.kerberos.service.name"));
        Assert.assertTrue(valuesWithPrefixAllOrNothing2.containsKey("ssl.keymanager.algorithm"));
    }

    @Test
    public void testUnused() {
        Properties props = new Properties();
        String configValue = "org.apache.kafka.common.config.AbstractConfigTest$ConfiguredFakeMetricsReporter";
        props.put(AbstractConfigTest.TestConfig.METRIC_REPORTER_CLASSES_CONFIG, configValue);
        props.put(AbstractConfigTest.FakeMetricsReporterConfig.EXTRA_CONFIG, "my_value");
        AbstractConfigTest.TestConfig config = new AbstractConfigTest.TestConfig(props);
        Assert.assertTrue("metric.extra_config should be marked unused before getConfiguredInstances is called", unused().contains(AbstractConfigTest.FakeMetricsReporterConfig.EXTRA_CONFIG));
        getConfiguredInstances(AbstractConfigTest.TestConfig.METRIC_REPORTER_CLASSES_CONFIG, MetricsReporter.class);
        Assert.assertTrue("All defined configurations should be marked as used", unused().isEmpty());
    }

    @Test
    public void testClassConfigs() {
        class RestrictedClassLoader extends ClassLoader {
            public RestrictedClassLoader() {
                super(null);
            }

            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if ((name.equals(AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS.getName())) || (name.equals(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName())))
                    return null;
                else
                    return AbstractConfigTest.ClassTestConfig.class.getClassLoader().loadClass(name);

            }
        }
        ClassLoader restrictedClassLoader = new RestrictedClassLoader();
        ClassLoader defaultClassLoader = AbstractConfig.class.getClassLoader();
        // Test default classloading where all classes are visible to thread context classloader
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
        AbstractConfigTest.ClassTestConfig testConfig = new AbstractConfigTest.ClassTestConfig();
        testConfig.checkInstances(AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS, AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS);
        // Test default classloading where default classes are not visible to thread context classloader
        // Static classloading is used for default classes, so instance creation should succeed.
        Thread.currentThread().setContextClassLoader(restrictedClassLoader);
        testConfig = new AbstractConfigTest.ClassTestConfig();
        testConfig.checkInstances(AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS, AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS);
        // Test class overrides with names or classes where all classes are visible to thread context classloader
        Thread.currentThread().setContextClassLoader(defaultClassLoader);
        AbstractConfigTest.ClassTestConfig.testOverrides();
        // Test class overrides with names or classes where all classes are visible to Kafka classloader, context classloader is null
        Thread.currentThread().setContextClassLoader(null);
        AbstractConfigTest.ClassTestConfig.testOverrides();
        // Test class overrides where some classes are not visible to thread context classloader
        Thread.currentThread().setContextClassLoader(restrictedClassLoader);
        // Properties specified as classes should succeed
        testConfig = new AbstractConfigTest.ClassTestConfig(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, Arrays.asList(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS));
        testConfig.checkInstances(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS);
        testConfig = new AbstractConfigTest.ClassTestConfig(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, Arrays.asList(AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS));
        testConfig.checkInstances(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS);
        // Properties specified as classNames should fail to load classes
        try {
            new AbstractConfigTest.ClassTestConfig(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName(), null);
            Assert.fail("Config created with class property that cannot be loaded");
        } catch (ConfigException e) {
            // Expected Exception
        }
        try {
            testConfig = new AbstractConfigTest.ClassTestConfig(null, Arrays.asList(AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS.getName(), AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName()));
            getConfiguredInstances("list.prop", MetricsReporter.class);
            Assert.fail("Should have failed to load class");
        } catch (KafkaException e) {
            // Expected Exception
        }
        try {
            testConfig = new AbstractConfigTest.ClassTestConfig(null, (((AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS.getName()) + ",") + (AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName())));
            getConfiguredInstances("list.prop", MetricsReporter.class);
            Assert.fail("Should have failed to load class");
        } catch (KafkaException e) {
            // Expected Exception
        }
    }

    private static class ClassTestConfig extends AbstractConfig {
        static final Class<?> DEFAULT_CLASS = FakeMetricsReporter.class;

        static final Class<?> VISIBLE_CLASS = JmxReporter.class;

        static final Class<?> RESTRICTED_CLASS = AbstractConfigTest.ConfiguredFakeMetricsReporter.class;

        private static final ConfigDef CONFIG;

        static {
            CONFIG = new ConfigDef().define("class.prop", CLASS, AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS, HIGH, "docs").define("list.prop", LIST, Arrays.asList(AbstractConfigTest.ClassTestConfig.DEFAULT_CLASS), HIGH, "docs");
        }

        public ClassTestConfig() {
            super(AbstractConfigTest.ClassTestConfig.CONFIG, new Properties());
        }

        public ClassTestConfig(Object classPropOverride, Object listPropOverride) {
            super(AbstractConfigTest.ClassTestConfig.CONFIG, AbstractConfigTest.ClassTestConfig.overrideProps(classPropOverride, listPropOverride));
        }

        void checkInstances(Class<?> expectedClassPropClass, Class<?>... expectedListPropClasses) {
            Assert.assertEquals(expectedClassPropClass, getConfiguredInstance("class.prop", MetricsReporter.class).getClass());
            List<?> list = getConfiguredInstances("list.prop", MetricsReporter.class);
            for (int i = 0; i < (list.size()); i++)
                Assert.assertEquals(expectedListPropClasses[i], list.get(i).getClass());

        }

        static void testOverrides() {
            AbstractConfigTest.ClassTestConfig testConfig1 = new AbstractConfigTest.ClassTestConfig(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, Arrays.asList(AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS));
            testConfig1.checkInstances(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS);
            AbstractConfigTest.ClassTestConfig testConfig2 = new AbstractConfigTest.ClassTestConfig(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName(), Arrays.asList(AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS.getName(), AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName()));
            testConfig2.checkInstances(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS);
            AbstractConfigTest.ClassTestConfig testConfig3 = new AbstractConfigTest.ClassTestConfig(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName(), (((AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS.getName()) + ",") + (AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS.getName())));
            testConfig3.checkInstances(AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS, AbstractConfigTest.ClassTestConfig.VISIBLE_CLASS, AbstractConfigTest.ClassTestConfig.RESTRICTED_CLASS);
        }

        private static Map<String, Object> overrideProps(Object classProp, Object listProp) {
            Map<String, Object> props = new HashMap<>();
            if (classProp != null)
                props.put("class.prop", classProp);

            if (listProp != null)
                props.put("list.prop", listProp);

            return props;
        }
    }

    private static class TestConfig extends AbstractConfig {
        private static final ConfigDef CONFIG;

        public static final String METRIC_REPORTER_CLASSES_CONFIG = "metric.reporters";

        private static final String METRIC_REPORTER_CLASSES_DOC = "A list of classes to use as metrics reporters.";

        static {
            CONFIG = new ConfigDef().define(AbstractConfigTest.TestConfig.METRIC_REPORTER_CLASSES_CONFIG, LIST, "", LOW, AbstractConfigTest.TestConfig.METRIC_REPORTER_CLASSES_DOC);
        }

        public TestConfig(Map<?, ?> props) {
            super(AbstractConfigTest.TestConfig.CONFIG, props);
        }
    }

    public static class ConfiguredFakeMetricsReporter extends FakeMetricsReporter {
        @Override
        public void configure(Map<String, ?> configs) {
            AbstractConfigTest.FakeMetricsReporterConfig config = new AbstractConfigTest.FakeMetricsReporterConfig(configs);
            // Calling getString() should have the side effect of marking that config as used.
            getString(AbstractConfigTest.FakeMetricsReporterConfig.EXTRA_CONFIG);
        }
    }

    public static class FakeMetricsReporterConfig extends AbstractConfig {
        public static final String EXTRA_CONFIG = "metric.extra_config";

        private static final String EXTRA_CONFIG_DOC = "An extraneous configuration string.";

        private static final ConfigDef CONFIG = new ConfigDef().define(AbstractConfigTest.FakeMetricsReporterConfig.EXTRA_CONFIG, STRING, "", ConfigDef.Importance.LOW, AbstractConfigTest.FakeMetricsReporterConfig.EXTRA_CONFIG_DOC);

        public FakeMetricsReporterConfig(Map<?, ?> props) {
            super(AbstractConfigTest.FakeMetricsReporterConfig.CONFIG, props);
        }
    }
}

