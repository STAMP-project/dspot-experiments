/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.conf;


import Constants.SITE_PROPERTIES;
import PropertyKey.DisplayType.CREDENTIALS;
import PropertyKey.HOME;
import PropertyKey.LOGGER_TYPE;
import PropertyKey.LOGSERVER_THREADS_MAX;
import PropertyKey.LOGS_DIR;
import PropertyKey.MASTER_HEARTBEAT_TIMEOUT;
import PropertyKey.MASTER_HOSTNAME;
import PropertyKey.MASTER_JOURNAL_FOLDER;
import PropertyKey.MASTER_MASTER_HEARTBEAT_INTERVAL;
import PropertyKey.MASTER_RPC_PORT;
import PropertyKey.MASTER_TIERED_STORE_GLOBAL_LEVEL0_ALIAS;
import PropertyKey.MASTER_WEB_HOSTNAME;
import PropertyKey.MASTER_WEB_PORT;
import PropertyKey.MASTER_WORKER_THREADS_MAX;
import PropertyKey.MASTER_WORKER_TIMEOUT_MS;
import PropertyKey.PROXY_STREAM_CACHE_TIMEOUT_MS;
import PropertyKey.PROXY_WEB_BIND_HOST;
import PropertyKey.S3A_ACCESS_KEY;
import PropertyKey.SECURITY_LOGIN_USERNAME;
import PropertyKey.SITE_CONF_DIR;
import PropertyKey.TEST_MODE;
import PropertyKey.Template.MASTER_MOUNT_TABLE_OPTION;
import PropertyKey.Template.MASTER_MOUNT_TABLE_OPTION_PROPERTY;
import PropertyKey.Template.MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS;
import PropertyKey.USER_FILE_BUFFER_BYTES;
import PropertyKey.USER_FILE_METADATA_SYNC_INTERVAL;
import PropertyKey.WEB_THREADS;
import PropertyKey.WORKER_BLOCK_THREADS_MAX;
import PropertyKey.WORK_DIR;
import PropertyKey.ZOOKEEPER_ADDRESS;
import Source.DEFAULT;
import Source.SYSTEM_PROPERTY;
import Source.Type.SITE_PROPERTY;
import Template.LOCALITY_TIER;
import Template.MASTER_JOURNAL_UFS_OPTION_PROPERTY;
import alluxio.AlluxioTestDirectory;
import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.DefaultSupplier;
import alluxio.SystemPropertyRule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.powermock.reflect.Whitebox;

import static PropertyKey.S3A_SECRET_KEY;
import static PropertyKey.SECURITY_LOGIN_USERNAME;


/**
 * Unit tests for the {@link alluxio.conf.InstancedConfiguration} class.
 */
public class ConfigurationTest {
    private InstancedConfiguration mConfiguration = ConfigurationTestUtils.defaults();

    @Rule
    public final ExpectedException mThrown = ExpectedException.none();

    @Rule
    public final TemporaryFolder mFolder = new TemporaryFolder();

    @Test
    public void defaultLoggerCorrectlyLoaded() throws Exception {
        // Avoid interference from system properties. site-properties will not be loaded during tests
        try (Closeable p = new SystemPropertyRule(LOGGER_TYPE.toString(), null).toResource()) {
            String loggerType = mConfiguration.get(LOGGER_TYPE);
            Assert.assertEquals("Console", loggerType);
        }
    }

    @Test
    public void alias() throws Exception {
        try (Closeable p = new SystemPropertyRule("alluxio.master.worker.timeout.ms", "100").toResource()) {
            resetConf();
            Assert.assertEquals(100, mConfiguration.getMs(MASTER_WORKER_TIMEOUT_MS));
        }
    }

    @Test
    public void isSet() {
        Assert.assertFalse(mConfiguration.isSet(ZOOKEEPER_ADDRESS));
        mConfiguration.set(ZOOKEEPER_ADDRESS, "address");
        Assert.assertTrue(mConfiguration.isSet(ZOOKEEPER_ADDRESS));
    }

    @Test
    public void isSetResolve() {
        mConfiguration.unset(MASTER_HOSTNAME);
        mConfiguration.set(MASTER_WEB_HOSTNAME, "${alluxio.master.hostname}");
        Assert.assertFalse(mConfiguration.isSet(MASTER_WEB_HOSTNAME));
        mConfiguration.set(MASTER_HOSTNAME, "localhost");
        Assert.assertTrue(mConfiguration.isSet(MASTER_WEB_HOSTNAME));
    }

    @Test
    public void getInt() {
        mConfiguration.set(WEB_THREADS, "1");
        Assert.assertEquals(1, mConfiguration.getInt(WEB_THREADS));
    }

    @Test
    public void getIntResolve() {
        mConfiguration.set(LOGSERVER_THREADS_MAX, "${alluxio.master.worker.threads.max}");
        mConfiguration.set(MASTER_WORKER_THREADS_MAX, "${alluxio.worker.block.threads.max}");
        mConfiguration.set(WORKER_BLOCK_THREADS_MAX, "10");
        Assert.assertEquals(10, mConfiguration.getInt(LOGSERVER_THREADS_MAX));
    }

    @Test
    public void getMalformedIntThrowsException() {
        mConfiguration.set(WEB_THREADS, "9448367483758473854738");// bigger than MAX_INT

        mThrown.expect(RuntimeException.class);
        mConfiguration.getInt(WEB_THREADS);
    }

    @Test
    public void getLong() {
        mConfiguration.set(WEB_THREADS, "12345678910");// bigger than MAX_INT

        Assert.assertEquals(12345678910L, mConfiguration.getLong(WEB_THREADS));
    }

    @Test
    public void getMalformedLongThrowsException() {
        mConfiguration.set(WEB_THREADS, "999999999999999999999999999999999999");// bigger than MAX_LONG

        mThrown.expect(RuntimeException.class);
        mConfiguration.getLong(WEB_THREADS);
    }

    @Test
    public void getDouble() {
        mConfiguration.set(WEB_THREADS, "1.1");
        /* tolerance= */
        Assert.assertEquals(1.1, mConfiguration.getDouble(WEB_THREADS), 1.0E-4);
    }

    @Test
    public void getMalformedDoubleThrowsException() {
        mConfiguration.set(WEB_THREADS, "1a");
        mThrown.expect(RuntimeException.class);
        mConfiguration.getDouble(WEB_THREADS);
    }

    @Test
    public void getFloat() {
        mConfiguration.set(WEB_THREADS, "1.1");
        /* tolerance= */
        Assert.assertEquals(1.1, mConfiguration.getFloat(WEB_THREADS), 1.0E-4);
    }

    @Test
    public void getMalformedFloatThrowsException() {
        mConfiguration.set(WEB_THREADS, "1a");
        mThrown.expect(RuntimeException.class);
        mConfiguration.getFloat(WEB_THREADS);
    }

    @Test
    public void getTrueBoolean() {
        mConfiguration.set(WEB_THREADS, "true");
        Assert.assertTrue(mConfiguration.getBoolean(WEB_THREADS));
    }

    @Test
    public void getTrueBooleanUppercase() {
        mConfiguration.set(WEB_THREADS, "True");
        Assert.assertTrue(mConfiguration.getBoolean(WEB_THREADS));
    }

    @Test
    public void getTrueBooleanMixcase() {
        mConfiguration.set(WEB_THREADS, "tRuE");
        Assert.assertTrue(mConfiguration.getBoolean(WEB_THREADS));
    }

    @Test
    public void getFalseBoolean() {
        mConfiguration.set(WEB_THREADS, "false");
        Assert.assertFalse(mConfiguration.getBoolean(WEB_THREADS));
    }

    @Test
    public void getFalseBooleanUppercase() {
        mConfiguration.set(WEB_THREADS, "False");
        Assert.assertFalse(mConfiguration.getBoolean(WEB_THREADS));
    }

    @Test
    public void getFalseBooleanMixcase() {
        mConfiguration.set(WEB_THREADS, "fAlSe");
        Assert.assertFalse(mConfiguration.getBoolean(WEB_THREADS));
    }

    @Test
    public void getMalformedBooleanThrowsException() {
        mConfiguration.set(WEB_THREADS, "x");
        mThrown.expect(RuntimeException.class);
        mConfiguration.getBoolean(WEB_THREADS);
    }

    @Test
    public void getList() {
        mConfiguration.set(WEB_THREADS, "a,b,c");
        Assert.assertEquals(Lists.newArrayList("a", "b", "c"), mConfiguration.getList(WEB_THREADS, ","));
    }

    private enum TestEnum {

        VALUE;}

    @Test
    public void getEnum() {
        mConfiguration.set(WEB_THREADS, "VALUE");
        Assert.assertEquals(ConfigurationTest.TestEnum.VALUE, mConfiguration.getEnum(WEB_THREADS, ConfigurationTest.TestEnum.class));
    }

    @Test
    public void getMalformedEnum() {
        mConfiguration.set(WEB_THREADS, "not_a_value");
        mThrown.expect(RuntimeException.class);
        mConfiguration.getEnum(WEB_THREADS, ConfigurationTest.TestEnum.class);
    }

    @Test
    public void getBytes() {
        mConfiguration.set(WEB_THREADS, "10b");
        Assert.assertEquals(10, mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getBytesKb() {
        mConfiguration.set(WEB_THREADS, "10kb");
        Assert.assertEquals((10 * (Constants.KB)), mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getBytesMb() {
        mConfiguration.set(WEB_THREADS, "10mb");
        Assert.assertEquals((10 * (Constants.MB)), mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getBytesGb() {
        mConfiguration.set(WEB_THREADS, "10gb");
        Assert.assertEquals((10 * ((long) (Constants.GB))), mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getBytesGbUppercase() {
        mConfiguration.set(WEB_THREADS, "10GB");
        Assert.assertEquals((10 * ((long) (Constants.GB))), mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getBytesTb() {
        mConfiguration.set(WEB_THREADS, "10tb");
        Assert.assertEquals((10 * (Constants.TB)), mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getBytespT() {
        mConfiguration.set(WEB_THREADS, "10pb");
        Assert.assertEquals((10 * (Constants.PB)), mConfiguration.getBytes(WEB_THREADS));
    }

    @Test
    public void getMalformedBytesThrowsException() {
        mConfiguration.set(WEB_THREADS, "100a");
        mThrown.expect(RuntimeException.class);
        mConfiguration.getBoolean(WEB_THREADS);
    }

    @Test
    public void getMs() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "100");
        Assert.assertEquals(100, mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsMS() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "100ms");
        Assert.assertEquals(100, mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsMillisecond() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "100millisecond");
        Assert.assertEquals(100, mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsS() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10s");
        Assert.assertEquals((10 * (Constants.SECOND)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsSUppercase() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10S");
        Assert.assertEquals((10 * (Constants.SECOND)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsSEC() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10sec");
        Assert.assertEquals((10 * (Constants.SECOND)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsSecond() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10second");
        Assert.assertEquals((10 * (Constants.SECOND)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsM() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10m");
        Assert.assertEquals((10 * (Constants.MINUTE)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsMIN() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10min");
        Assert.assertEquals((10 * (Constants.MINUTE)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsMinute() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10minute");
        Assert.assertEquals((10 * (Constants.MINUTE)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsH() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10h");
        Assert.assertEquals((10 * (Constants.HOUR)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsHR() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10hr");
        Assert.assertEquals((10 * (Constants.HOUR)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsHour() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10hour");
        Assert.assertEquals((10 * (Constants.HOUR)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsD() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10d");
        Assert.assertEquals((10 * (Constants.DAY)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getMsDay() {
        mConfiguration.set(PROXY_STREAM_CACHE_TIMEOUT_MS, "10day");
        Assert.assertEquals((10 * (Constants.DAY)), mConfiguration.getMs(PROXY_STREAM_CACHE_TIMEOUT_MS));
    }

    @Test
    public void getNegativeSyncInterval() {
        mConfiguration.set(USER_FILE_METADATA_SYNC_INTERVAL, "-1");
        Assert.assertEquals((-1), mConfiguration.getMs(USER_FILE_METADATA_SYNC_INTERVAL));
    }

    @Test
    public void getNegativeSyncIntervalS() {
        mConfiguration.set(USER_FILE_METADATA_SYNC_INTERVAL, "-1s");
        Assert.assertTrue(((mConfiguration.getMs(USER_FILE_METADATA_SYNC_INTERVAL)) < 0));
    }

    @Test
    public void getZeroSyncInterval() {
        mConfiguration.set(USER_FILE_METADATA_SYNC_INTERVAL, "0");
        Assert.assertEquals(0, mConfiguration.getMs(USER_FILE_METADATA_SYNC_INTERVAL));
    }

    @Test
    public void getZeroSyncIntervalS() {
        mConfiguration.set(USER_FILE_METADATA_SYNC_INTERVAL, "0s");
        Assert.assertEquals(0, mConfiguration.getMs(USER_FILE_METADATA_SYNC_INTERVAL));
    }

    @Test
    public void getPositiveSyncInterval() {
        mConfiguration.set(USER_FILE_METADATA_SYNC_INTERVAL, "10");
        Assert.assertEquals(10, mConfiguration.getMs(USER_FILE_METADATA_SYNC_INTERVAL));
    }

    @Test
    public void getPosiviteSyncIntervalS() {
        mConfiguration.set(USER_FILE_METADATA_SYNC_INTERVAL, "10s");
        Assert.assertEquals((10 * (Constants.SECOND_MS)), mConfiguration.getMs(USER_FILE_METADATA_SYNC_INTERVAL));
    }

    @Test
    public void getUnsetValueThrowsException() {
        mThrown.expect(RuntimeException.class);
        mConfiguration.get(S3A_ACCESS_KEY);
    }

    @Test
    public void getNestedProperties() {
        mConfiguration.set(MASTER_MOUNT_TABLE_OPTION_PROPERTY.format("foo", WEB_THREADS.toString()), "val1");
        mConfiguration.set(MASTER_MOUNT_TABLE_OPTION_PROPERTY.format("foo", "alluxio.unknown.property"), "val2");
        Map<String, String> expected = new HashMap<>();
        expected.put(WEB_THREADS.toString(), "val1");
        expected.put("alluxio.unknown.property", "val2");
        Assert.assertThat(mConfiguration.getNestedProperties(MASTER_MOUNT_TABLE_OPTION.format("foo")), CoreMatchers.is(expected));
    }

    @Test
    public void getNestedPropertiesEmptyTrailingProperty() {
        mConfiguration.set(MASTER_MOUNT_TABLE_OPTION_PROPERTY.format("foo", ""), "val");
        Map<String, String> empty = new HashMap<>();
        Assert.assertThat(mConfiguration.getNestedProperties(MASTER_MOUNT_TABLE_OPTION.format("foo")), CoreMatchers.is(empty));
    }

    @Test
    public void getNestedPropertiesWrongPrefix() {
        mConfiguration.set(MASTER_MOUNT_TABLE_OPTION_PROPERTY.format("foo", WEB_THREADS.toString()), "val");
        Map<String, String> empty = new HashMap<>();
        Assert.assertThat(mConfiguration.getNestedProperties(HOME), CoreMatchers.is(empty));
        Assert.assertThat(mConfiguration.getNestedProperties(MASTER_MOUNT_TABLE_OPTION.format("bar")), CoreMatchers.is(empty));
    }

    @Test
    public void getClassTest() {
        // The name getClass is already reserved.
        mConfiguration.set(WEB_THREADS, "java.lang.String");
        Assert.assertEquals(String.class, mConfiguration.getClass(WEB_THREADS));
    }

    @Test
    public void getMalformedClassThrowsException() {
        mConfiguration.set(WEB_THREADS, "java.util.not.a.class");
        mThrown.expect(RuntimeException.class);
        mConfiguration.getClass(WEB_THREADS);
    }

    @Test
    public void getTemplatedKey() {
        mConfiguration.set(MASTER_TIERED_STORE_GLOBAL_LEVEL0_ALIAS, "test");
        Assert.assertEquals("test", mConfiguration.get(MASTER_TIERED_STORE_GLOBAL_LEVEL_ALIAS.format(0)));
    }

    @Test
    public void variableSubstitution() {
        mConfiguration.merge(ImmutableMap.of(WORK_DIR, "value", LOGS_DIR, "${alluxio.work.dir}/logs"), SYSTEM_PROPERTY);
        String substitution = mConfiguration.get(LOGS_DIR);
        Assert.assertEquals("value/logs", substitution);
    }

    @Test
    public void twoVariableSubstitution() {
        mConfiguration.merge(ImmutableMap.of(MASTER_HOSTNAME, "value1", MASTER_RPC_PORT, "value2", MASTER_JOURNAL_FOLDER, "${alluxio.master.hostname}-${alluxio.master.port}"), SYSTEM_PROPERTY);
        String substitution = mConfiguration.get(MASTER_JOURNAL_FOLDER);
        Assert.assertEquals("value1-value2", substitution);
    }

    @Test
    public void recursiveVariableSubstitution() {
        mConfiguration.merge(ImmutableMap.of(WORK_DIR, "value", LOGS_DIR, "${alluxio.work.dir}/logs", SITE_CONF_DIR, "${alluxio.logs.dir}/conf"), SYSTEM_PROPERTY);
        String substitution2 = mConfiguration.get(SITE_CONF_DIR);
        Assert.assertEquals("value/logs/conf", substitution2);
    }

    @Test
    public void systemVariableSubstitution() throws Exception {
        try (Closeable p = new SystemPropertyRule(MASTER_HOSTNAME.toString(), "new_master").toResource()) {
            resetConf();
            Assert.assertEquals("new_master", mConfiguration.get(MASTER_HOSTNAME));
        }
    }

    @Test
    public void systemPropertySubstitution() throws Exception {
        try (Closeable p = new SystemPropertyRule("user.home", "/home").toResource()) {
            resetConf();
            mConfiguration.set(WORK_DIR, "${user.home}/work");
            Assert.assertEquals("/home/work", mConfiguration.get(WORK_DIR));
        }
    }

    @Test
    public void circularSubstitution() throws Exception {
        mConfiguration.set(HOME, String.format("${%s}", HOME.toString()));
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(HOME.toString());
        mConfiguration.get(HOME);
    }

    @Test
    public void userFileBufferBytesOverFlowException() {
        mConfiguration.set(USER_FILE_BUFFER_BYTES, ((String.valueOf(((Integer.MAX_VALUE) + 1))) + "B"));
        mThrown.expect(IllegalStateException.class);
        mConfiguration.validate();
    }

    @Test
    public void shortMasterHeartBeatTimeout() {
        mConfiguration.set(MASTER_MASTER_HEARTBEAT_INTERVAL, "5min");
        mConfiguration.set(MASTER_HEARTBEAT_TIMEOUT, "4min");
        mThrown.expect(IllegalStateException.class);
        mConfiguration.validate();
    }

    @Test
    public void setUserFileBufferBytesMaxInteger() {
        mConfiguration.set(USER_FILE_BUFFER_BYTES, ((String.valueOf(Integer.MAX_VALUE)) + "B"));
        Assert.assertEquals(Integer.MAX_VALUE, ((int) (mConfiguration.getBytes(USER_FILE_BUFFER_BYTES))));
    }

    @Test
    public void setUserFileBufferBytes1GB() {
        mConfiguration.set(USER_FILE_BUFFER_BYTES, "1GB");
        Assert.assertEquals(1073741824, ((int) (mConfiguration.getBytes(USER_FILE_BUFFER_BYTES))));
    }

    @Test
    public void unset() {
        Assert.assertFalse(mConfiguration.isSet(SECURITY_LOGIN_USERNAME));
        mConfiguration.set(SECURITY_LOGIN_USERNAME, "test");
        Assert.assertTrue(mConfiguration.isSet(SECURITY_LOGIN_USERNAME));
        mConfiguration.unset(SECURITY_LOGIN_USERNAME);
        Assert.assertFalse(mConfiguration.isSet(SECURITY_LOGIN_USERNAME));
    }

    @Test
    public void validateTieredLocality() throws Exception {
        // Pre-load the Configuration class so that the exception is thrown when we call init(), not
        // during class loading.
        resetConf();
        HashMap<String, String> sysProps = new HashMap<>();
        sysProps.put(LOCALITY_TIER.format("unknownTier").toString(), "val");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            mThrown.expect(IllegalStateException.class);
            mThrown.expectMessage(("Tier unknownTier is configured by alluxio.locality.unknownTier, but " + "does not exist in the tier list [node, rack] configured by alluxio.locality.order"));
            resetConf();
        }
    }

    @Test
    public void propertyTestModeEqualsTrue() throws Exception {
        Assert.assertTrue(mConfiguration.getBoolean(TEST_MODE));
    }

    @Test
    public void sitePropertiesNotLoadedInTest() throws Exception {
        Properties props = new Properties();
        props.setProperty(LOGGER_TYPE.toString(), "TEST_LOGGER");
        File propsFile = mFolder.newFile(SITE_PROPERTIES);
        props.store(new FileOutputStream(propsFile), "ignored header");
        // Avoid interference from system properties. Reset SITE_CONF_DIR to include the temp
        // site-properties file
        HashMap<String, String> sysProps = new HashMap<>();
        sysProps.put(LOGGER_TYPE.toString(), null);
        sysProps.put(SITE_CONF_DIR.toString(), mFolder.getRoot().getAbsolutePath());
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            mConfiguration = ConfigurationTestUtils.defaults();
            Assert.assertEquals(LOGGER_TYPE.getDefaultValue(), mConfiguration.get(LOGGER_TYPE));
        }
    }

    @Test
    public void sitePropertiesLoadedNotInTest() throws Exception {
        Properties props = new Properties();
        props.setProperty(LOGGER_TYPE.toString(), "TEST_LOGGER");
        File propsFile = mFolder.newFile(SITE_PROPERTIES);
        props.store(new FileOutputStream(propsFile), "ignored header");
        // Avoid interference from system properties. Reset SITE_CONF_DIR to include the temp
        // site-properties file
        HashMap<String, String> sysProps = new HashMap<>();
        sysProps.put(LOGGER_TYPE.toString(), null);
        sysProps.put(SITE_CONF_DIR.toString(), mFolder.getRoot().getAbsolutePath());
        sysProps.put(TEST_MODE.toString(), "false");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            resetConf();
            Assert.assertEquals("TEST_LOGGER", mConfiguration.get(LOGGER_TYPE));
        }
    }

    @Test
    public void setIgnoredPropertiesInSiteProperties() throws Exception {
        resetConf();
        Properties siteProps = new Properties();
        siteProps.setProperty(LOGS_DIR.toString(), "/tmp/logs1");
        File propsFile = mFolder.newFile(SITE_PROPERTIES);
        siteProps.store(new FileOutputStream(propsFile), "tmp site properties file");
        Map<String, String> sysProps = new HashMap<>();
        sysProps.put(SITE_CONF_DIR.toString(), mFolder.getRoot().getAbsolutePath());
        sysProps.put(TEST_MODE.toString(), "false");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            mThrown.expect(IllegalStateException.class);
            resetConf();
        }
    }

    @Test
    public void setIgnoredPropertiesInSystemProperties() throws Exception {
        Properties siteProps = new Properties();
        File propsFile = mFolder.newFile(SITE_PROPERTIES);
        siteProps.store(new FileOutputStream(propsFile), "tmp site properties file");
        Map<String, String> sysProps = new HashMap<>();
        sysProps.put(LOGS_DIR.toString(), "/tmp/logs1");
        sysProps.put(SITE_CONF_DIR.toString(), mFolder.getRoot().getAbsolutePath());
        sysProps.put(TEST_MODE.toString(), "false");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            resetConf();
            Assert.assertEquals(SYSTEM_PROPERTY, mConfiguration.getSource(LOGS_DIR));
            Assert.assertEquals("/tmp/logs1", mConfiguration.get(LOGS_DIR));
        }
    }

    @Test
    public void noWhitespaceTrailingInSiteProperties() throws Exception {
        Properties siteProps = new Properties();
        siteProps.setProperty(MASTER_HOSTNAME.toString(), " host-1 ");
        siteProps.setProperty(WEB_THREADS.toString(), "\t123\t");
        File propsFile = mFolder.newFile(SITE_PROPERTIES);
        siteProps.store(new FileOutputStream(propsFile), "tmp site properties file");
        // Avoid interference from system properties. Reset SITE_CONF_DIR to include the temp
        // site-properties file
        HashMap<String, String> sysProps = new HashMap<>();
        sysProps.put(SITE_CONF_DIR.toString(), mFolder.getRoot().getAbsolutePath());
        sysProps.put(TEST_MODE.toString(), "false");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            resetConf();
            Assert.assertEquals("host-1", mConfiguration.get(MASTER_HOSTNAME));
            Assert.assertEquals("123", mConfiguration.get(WEB_THREADS));
        }
    }

    @Test
    public void source() throws Exception {
        Properties siteProps = new Properties();
        File propsFile = mFolder.newFile(SITE_PROPERTIES);
        siteProps.setProperty(MASTER_HOSTNAME.toString(), "host-1");
        siteProps.setProperty(MASTER_WEB_PORT.toString(), "1234");
        siteProps.store(new FileOutputStream(propsFile), "tmp site properties file");
        Map<String, String> sysProps = new HashMap<>();
        sysProps.put(LOGS_DIR.toString(), "/tmp/logs1");
        sysProps.put(MASTER_WEB_PORT.toString(), "4321");
        sysProps.put(SITE_CONF_DIR.toString(), mFolder.getRoot().getAbsolutePath());
        sysProps.put(TEST_MODE.toString(), "false");
        try (Closeable p = new SystemPropertyRule(sysProps).toResource()) {
            resetConf();
            // set only in site prop
            Assert.assertEquals(SITE_PROPERTY, mConfiguration.getSource(MASTER_HOSTNAME).getType());
            // set both in site and system prop
            Assert.assertEquals(SYSTEM_PROPERTY, mConfiguration.getSource(MASTER_WEB_PORT));
            // set only in system prop
            Assert.assertEquals(SYSTEM_PROPERTY, mConfiguration.getSource(LOGS_DIR));
            // set neither in system prop
            Assert.assertEquals(DEFAULT, mConfiguration.getSource(MASTER_RPC_PORT));
        }
    }

    @Test
    public void getRuntimeDefault() throws Exception {
        AtomicInteger x = new AtomicInteger(100);
        PropertyKey key = new PropertyKey.Builder("testKey").setDefaultSupplier(new DefaultSupplier(() -> x.get(), "finds x")).build();
        Assert.assertEquals(100, mConfiguration.getInt(key));
        x.set(20);
        Assert.assertEquals(20, mConfiguration.getInt(key));
    }

    @Test
    public void toMap() throws Exception {
        // Create a nested property to test
        String testKeyName = "alluxio.extensions.dir";
        PropertyKey nestedKey = SECURITY_LOGIN_USERNAME;
        String nestedValue = String.format("${%s}.test", testKeyName);
        mConfiguration.set(nestedKey, nestedValue);
        Map<String, String> resolvedMap = mConfiguration.toMap();
        // Test if the value of the created nested property is correct
        Assert.assertEquals(mConfiguration.get(PropertyKey.fromString(testKeyName)), resolvedMap.get(testKeyName));
        String nestedResolvedValue = String.format("%s.test", resolvedMap.get(testKeyName));
        Assert.assertEquals(nestedResolvedValue, resolvedMap.get(nestedKey.toString()));
        // Test if the values in the resolvedMap is resolved
        String resolvedValue1 = String.format("%s/extensions", resolvedMap.get("alluxio.home"));
        Assert.assertEquals(resolvedValue1, resolvedMap.get(testKeyName));
        String resolvedValue2 = String.format("%s/logs", resolvedMap.get("alluxio.work.dir"));
        Assert.assertEquals(resolvedValue2, resolvedMap.get("alluxio.logs.dir"));
        // Test if the resolvedMap include all kinds of properties
        Assert.assertTrue(resolvedMap.containsKey("alluxio.debug"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.fuse.fs.name"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.logserver.logs.dir"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.master.journal.folder"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.proxy.web.port"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.security.authentication.type"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.user.block.master.client.threads"));
        Assert.assertTrue(resolvedMap.containsKey("alluxio.worker.bind.host"));
    }

    @Test
    public void toRawMap() throws Exception {
        // Create a nested property to test
        PropertyKey testKey = SECURITY_LOGIN_USERNAME;
        String testValue = String.format("${%s}.test", "alluxio.extensions.dir");
        mConfiguration.set(testKey, testValue);
        Map<String, String> rawMap = mConfiguration.toMap(ConfigurationValueOptions.defaults().useRawValue(true));
        // Test if the value of the created nested property remains raw
        Assert.assertEquals(testValue, rawMap.get(testKey.toString()));
        // Test if some value in raw map is of ${VALUE} format
        String regexString = "(\\$\\{([^{}]*)\\})";
        Pattern confRegex = Pattern.compile(regexString);
        Assert.assertTrue(confRegex.matcher(rawMap.get("alluxio.logs.dir")).find());
    }

    @Test
    public void getCredentialsDisplayValue() {
        PropertyKey testKey = S3A_SECRET_KEY;
        String testValue = "12345";
        Assert.assertEquals(CREDENTIALS, testKey.getDisplayType());
        mConfiguration.set(testKey, testValue);
        Assert.assertNotEquals(testValue, mConfiguration.get(testKey, ConfigurationValueOptions.defaults().useDisplayValue(true)));
        Assert.assertNotEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true)).get(testKey.getName()));
    }

    @Test
    public void getDefaultDisplayValue() {
        PropertyKey testKey = SECURITY_LOGIN_USERNAME;
        String testValue = "12345";
        Assert.assertEquals(PropertyKey.DisplayType.DEFAULT, testKey.getDisplayType());
        mConfiguration.set(testKey, testValue);
        Assert.assertEquals(testValue, mConfiguration.get(testKey, ConfigurationValueOptions.defaults().useDisplayValue(true)));
        Assert.assertEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true)).get(testKey.getName()));
    }

    @Test
    public void getNestedCredentialsDisplayValue() {
        PropertyKey nestedProperty = PropertyKey.fromString("alluxio.master.journal.ufs.option.aws.secretKey");
        String testValue = "12345";
        mConfiguration.set(nestedProperty, testValue);
        Assert.assertNotEquals(testValue, mConfiguration.get(nestedProperty, ConfigurationValueOptions.defaults().useDisplayValue(true)));
        Assert.assertNotEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true)).get(nestedProperty.getName()));
        Assert.assertNotEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true).useRawValue(true)).get(nestedProperty.getName()));
    }

    @Test
    public void getNestedDefaultDisplayValue() {
        PropertyKey nestedProperty = PropertyKey.fromString("alluxio.master.journal.ufs.option.alluxio.underfs.hdfs.configuration");
        String testValue = "conf/core-site.xml:conf/hdfs-site.xml";
        mConfiguration.set(nestedProperty, testValue);
        Assert.assertEquals(testValue, mConfiguration.get(nestedProperty, ConfigurationValueOptions.defaults().useDisplayValue(true)));
        Assert.assertEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true)).get(nestedProperty.getName()));
        Assert.assertEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true).useRawValue(true)).get(nestedProperty.getName()));
    }

    @Test
    public void getTemplateCredentialsDisplayValue() {
        PropertyKey templateProperty = PropertyKey.fromString("fs.azure.account.key.someone.blob.core.windows.net");
        String testValue = "12345";
        mConfiguration.set(templateProperty, testValue);
        Assert.assertNotEquals(testValue, mConfiguration.get(templateProperty, ConfigurationValueOptions.defaults().useDisplayValue(true)));
        Assert.assertNotEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true)).get(templateProperty.getName()));
        Assert.assertNotEquals(testValue, mConfiguration.toMap(ConfigurationValueOptions.defaults().useDisplayValue(true).useRawValue(true)).get(templateProperty.getName()));
    }

    @Test
    public void getCredentialsDisplayValueIdentical() {
        PropertyKey testKey = S3A_SECRET_KEY;
        String testValue = "12345";
        Assert.assertEquals(CREDENTIALS, testKey.getDisplayType());
        mConfiguration.set(testKey, testValue);
        String displayValue1 = mConfiguration.get(testKey, ConfigurationValueOptions.defaults().useDisplayValue(true));
        String testValue2 = "abc";
        mConfiguration.set(testKey, testValue2);
        String displayValue2 = mConfiguration.get(testKey, ConfigurationValueOptions.defaults().useDisplayValue(true));
        Assert.assertEquals(displayValue1, displayValue2);
    }

    @Test
    public void extensionProperty() {
        // simulate the case a ext key is picked by site property, unrecognized
        String fakeKeyName = "fake.extension.key";
        mConfiguration.merge(ImmutableMap.of(fakeKeyName, "value"), Source.siteProperty("ignored"));
        Assert.assertFalse(PropertyKey.fromString(fakeKeyName).isBuiltIn());
        // simulate the case the same key is built again inside the extension
        PropertyKey fakeExtensionKey = new PropertyKey.Builder(fakeKeyName).build();
        Assert.assertEquals("value", mConfiguration.get(fakeExtensionKey));
        Assert.assertTrue(PropertyKey.fromString(fakeKeyName).isBuiltIn());
    }

    @Test
    public void findPropertiesFileClasspath() throws Exception {
        try (Closeable p = new SystemPropertyRule(TEST_MODE.toString(), "false").toResource()) {
            File dir = AlluxioTestDirectory.createTemporaryDirectory("findPropertiesFileClasspath");
            Whitebox.invokeMethod(ClassLoader.getSystemClassLoader(), "addURL", dir.toURI().toURL());
            File props = new File(dir, "alluxio-site.properties");
            try (BufferedWriter writer = Files.newBufferedWriter(props.toPath())) {
                writer.write(String.format("%s=%s", MASTER_HOSTNAME, "test_hostname"));
            }
            resetConf();
            Assert.assertEquals("test_hostname", mConfiguration.get(MASTER_HOSTNAME));
            Assert.assertEquals(Source.siteProperty(props.getPath()), mConfiguration.getSource(MASTER_HOSTNAME));
            props.delete();
        }
    }

    @Test
    public void noPropertiesAnywhere() throws Exception {
        try (Closeable p = new SystemPropertyRule(TEST_MODE.toString(), "false").toResource()) {
            mConfiguration.unset(SITE_CONF_DIR);
            resetConf();
            Assert.assertEquals("0.0.0.0", mConfiguration.get(PROXY_WEB_BIND_HOST));
        }
    }

    @Test
    public void initConfWithExtenstionProperty() throws Exception {
        try (Closeable p = new SystemPropertyRule("alluxio.master.journal.ufs.option.fs.obs.endpoint", "foo").toResource()) {
            resetConf();
            Assert.assertEquals("foo", mConfiguration.get(MASTER_JOURNAL_UFS_OPTION_PROPERTY.format("fs.obs.endpoint")));
        }
    }
}

