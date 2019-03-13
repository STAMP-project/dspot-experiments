/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.setting;


import com.google.common.collect.ImmutableMap;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import org.apache.ibatis.exceptions.PersistenceException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.config.PropertyDefinitions;


public class ThreadLocalSettingsTest {
    private static final String A_KEY = "a_key";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ThreadLocalSettingsTest.MapSettingLoader dbSettingLoader = new ThreadLocalSettingsTest.MapSettingLoader();

    private ThreadLocalSettings underTest = null;

    @Test
    public void can_not_add_property_if_no_cache() {
        underTest = create(Collections.emptyMap());
        underTest.set("foo", "wiz");
        assertThat(underTest.get("foo")).isNotPresent();
    }

    @Test
    public void can_not_remove_system_property_if_no_cache() {
        underTest = create(ImmutableMap.of("foo", "bar"));
        underTest.remove("foo");
        assertThat(underTest.get("foo")).hasValue("bar");
    }

    @Test
    public void add_property_to_cache() {
        underTest = create(Collections.emptyMap());
        underTest.load();
        underTest.set("foo", "bar");
        assertThat(underTest.get("foo")).hasValue("bar");
        underTest.unload();
        // no more cache
        assertThat(underTest.get("foo")).isNotPresent();
    }

    @Test
    public void remove_property_from_cache() {
        underTest = create(Collections.emptyMap());
        underTest.load();
        underTest.set("foo", "bar");
        assertThat(underTest.get("foo")).hasValue("bar");
        underTest.remove("foo");
        assertThat(underTest.get("foo")).isNotPresent();
        underTest.unload();
        // no more cache
        assertThat(underTest.get("foo")).isNotPresent();
    }

    /**
     * SONAR-8216 System info page fails when a setting is defined both in sonar.properties and in DB
     */
    @Test
    public void getProperties_does_not_fail_on_duplicated_key() {
        insertPropertyIntoDb("foo", "from_db");
        underTest = create(ImmutableMap.of("foo", "from_system"));
        assertThat(underTest.get("foo")).hasValue("from_system");
        assertThat(underTest.getProperties().get("foo")).isEqualTo("from_system");
    }

    @Test
    public void load_encryption_secret_key_from_system_properties() throws Exception {
        File secretKey = temp.newFile();
        underTest = create(ImmutableMap.of("foo", "bar", "sonar.secretKeyPath", secretKey.getAbsolutePath()));
        assertThat(underTest.getEncryption().hasSecretKey()).isTrue();
    }

    @Test
    public void encryption_secret_key_is_undefined_by_default() {
        underTest = create(ImmutableMap.of("foo", "bar", "sonar.secretKeyPath", "unknown/path/to/sonar-secret.txt"));
        assertThat(underTest.getEncryption().hasSecretKey()).isFalse();
    }

    @Test
    public void load_system_properties() {
        underTest = create(ImmutableMap.of("foo", "1", "bar", "2"));
        assertThat(underTest.get("foo")).hasValue("1");
        assertThat(underTest.get("missing")).isNotPresent();
        assertThat(underTest.getProperties()).containsOnly(entry("foo", "1"), entry("bar", "2"));
    }

    @Test
    public void database_properties_are_not_cached_by_default() {
        insertPropertyIntoDb("foo", "from db");
        underTest = create(Collections.emptyMap());
        assertThat(underTest.get("foo")).hasValue("from db");
        deletePropertyFromDb("foo");
        // no cache, change is visible immediately
        assertThat(underTest.get("foo")).isNotPresent();
    }

    @Test
    public void overwritten_system_settings_have_precedence_over_system_and_databse() {
        underTest = create(ImmutableMap.of("foo", "from system"));
        underTest.setSystemProperty("foo", "donut");
        assertThat(underTest.get("foo")).hasValue("donut");
    }

    @Test
    public void overwritten_system_settings_have_precedence_over_databse() {
        insertPropertyIntoDb("foo", "from db");
        underTest = create(Collections.emptyMap());
        underTest.setSystemProperty("foo", "donut");
        assertThat(underTest.get("foo")).hasValue("donut");
    }

    @Test
    public void system_settings_have_precedence_over_database() {
        insertPropertyIntoDb("foo", "from db");
        underTest = create(ImmutableMap.of("foo", "from system"));
        assertThat(underTest.get("foo")).hasValue("from system");
    }

    @Test
    public void getProperties_are_all_properties_with_value() {
        insertPropertyIntoDb("db", "from db");
        insertPropertyIntoDb("empty", "");
        underTest = create(ImmutableMap.of("system", "from system"));
        assertThat(underTest.getProperties()).containsOnly(entry("system", "from system"), entry("db", "from db"), entry("empty", ""));
    }

    @Test
    public void getProperties_is_not_cached_in_thread_cache() {
        insertPropertyIntoDb("foo", "bar");
        underTest = create(Collections.emptyMap());
        underTest.load();
        assertThat(underTest.getProperties()).containsOnly(entry("foo", "bar"));
        insertPropertyIntoDb("foo2", "bar2");
        assertThat(underTest.getProperties()).containsOnly(entry("foo", "bar"), entry("foo2", "bar2"));
        underTest.unload();
        assertThat(underTest.getProperties()).containsOnly(entry("foo", "bar"), entry("foo2", "bar2"));
    }

    @Test
    public void load_creates_a_thread_specific_cache() throws InterruptedException {
        insertPropertyIntoDb(ThreadLocalSettingsTest.A_KEY, "v1");
        underTest = create(Collections.emptyMap());
        underTest.load();
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).hasValue("v1");
        deletePropertyFromDb(ThreadLocalSettingsTest.A_KEY);
        // the main thread still has "v1" in cache, but not new thread
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).hasValue("v1");
        verifyValueInNewThread(underTest, null);
        insertPropertyIntoDb(ThreadLocalSettingsTest.A_KEY, "v2");
        // the main thread still has the old value "v1" in cache, but new thread loads "v2"
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).hasValue("v1");
        verifyValueInNewThread(underTest, "v2");
        underTest.unload();
    }

    @Test
    public void load_invalidates_cache_if_unload_has_not_been_called() {
        underTest = create(Collections.emptyMap());
        underTest.load();
        underTest.set("foo", "bar");
        // unload() is not called
        underTest.load();
        assertThat(underTest.get("foo")).isEmpty();
    }

    @Test
    public void keep_in_thread_cache_the_fact_that_a_property_is_not_in_db() {
        underTest = create(Collections.emptyMap());
        underTest.load();
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).isNotPresent();
        insertPropertyIntoDb(ThreadLocalSettingsTest.A_KEY, "bar");
        // do not execute new SQL request, cache contains the information of missing property
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).isNotPresent();
        underTest.unload();
    }

    @Test
    public void change_setting_loader() {
        underTest = new ThreadLocalSettings(new PropertyDefinitions(), new Properties());
        assertThat(underTest.getSettingLoader()).isNotNull();
        SettingLoader newLoader = Mockito.mock(SettingLoader.class);
        underTest.setSettingLoader(newLoader);
        assertThat(underTest.getSettingLoader()).isSameAs(newLoader);
    }

    @Test
    public void cache_db_calls_if_property_is_not_persisted() {
        underTest = create(Collections.emptyMap());
        underTest.load();
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).isNotPresent();
        assertThat(underTest.get(ThreadLocalSettingsTest.A_KEY)).isNotPresent();
        underTest.unload();
    }

    @Test
    public void getProperties_return_empty_if_DB_error_on_first_call_ever_out_of_thread_cache() {
        SettingLoader settingLoaderMock = Mockito.mock(SettingLoader.class);
        PersistenceException toBeThrown = new PersistenceException("Faking an error connecting to DB");
        Mockito.doThrow(toBeThrown).when(settingLoaderMock).loadAll();
        underTest = new ThreadLocalSettings(new PropertyDefinitions(), new Properties(), settingLoaderMock);
        assertThat(underTest.getProperties()).isEmpty();
    }

    @Test
    public void getProperties_returns_empty_if_DB_error_on_first_call_ever_in_thread_cache() {
        SettingLoader settingLoaderMock = Mockito.mock(SettingLoader.class);
        PersistenceException toBeThrown = new PersistenceException("Faking an error connecting to DB");
        Mockito.doThrow(toBeThrown).when(settingLoaderMock).loadAll();
        underTest = new ThreadLocalSettings(new PropertyDefinitions(), new Properties(), settingLoaderMock);
        underTest.load();
        assertThat(underTest.getProperties()).isEmpty();
    }

    @Test
    public void getProperties_return_properties_from_previous_thread_cache_if_DB_error_on_not_first_call() {
        String key = randomAlphanumeric(3);
        String value1 = randomAlphanumeric(4);
        String value2 = randomAlphanumeric(5);
        SettingLoader settingLoaderMock = Mockito.mock(SettingLoader.class);
        PersistenceException toBeThrown = new PersistenceException("Faking an error connecting to DB");
        Mockito.doAnswer(( invocationOnMock) -> ImmutableMap.of(key, value1)).doThrow(toBeThrown).doAnswer(( invocationOnMock) -> ImmutableMap.of(key, value2)).when(settingLoaderMock).loadAll();
        underTest = new ThreadLocalSettings(new PropertyDefinitions(), new Properties(), settingLoaderMock);
        underTest.load();
        assertThat(underTest.getProperties()).containsOnly(entry(key, value1));
        underTest.unload();
        underTest.load();
        assertThat(underTest.getProperties()).containsOnly(entry(key, value1));
        underTest.unload();
        underTest.load();
        assertThat(underTest.getProperties()).containsOnly(entry(key, value2));
        underTest.unload();
    }

    @Test
    public void get_returns_empty_if_DB_error_on_first_call_ever_out_of_thread_cache() {
        SettingLoader settingLoaderMock = Mockito.mock(SettingLoader.class);
        PersistenceException toBeThrown = new PersistenceException("Faking an error connecting to DB");
        String key = randomAlphanumeric(3);
        Mockito.doThrow(toBeThrown).when(settingLoaderMock).load(key);
        underTest = new ThreadLocalSettings(new PropertyDefinitions(), new Properties(), settingLoaderMock);
        assertThat(underTest.get(key)).isEmpty();
    }

    @Test
    public void get_returns_empty_if_DB_error_on_first_call_ever_in_thread_cache() {
        SettingLoader settingLoaderMock = Mockito.mock(SettingLoader.class);
        PersistenceException toBeThrown = new PersistenceException("Faking an error connecting to DB");
        String key = randomAlphanumeric(3);
        Mockito.doThrow(toBeThrown).when(settingLoaderMock).load(key);
        underTest = new ThreadLocalSettings(new PropertyDefinitions(), new Properties(), settingLoaderMock);
        underTest.load();
        assertThat(underTest.get(key)).isEmpty();
    }

    private class CacheCaptorThread extends Thread {
        private final CountDownLatch latch = new CountDownLatch(1);

        private ThreadLocalSettings settings;

        private String value;

        void verifyValue(ThreadLocalSettings settings, @Nullable
        String expectedValue) throws InterruptedException {
            this.settings = settings;
            this.start();
            this.latch.await(5, TimeUnit.SECONDS);
            assertThat(value).isEqualTo(expectedValue);
        }

        @Override
        public void run() {
            try {
                settings.load();
                value = settings.get(ThreadLocalSettingsTest.A_KEY).orElse(null);
                latch.countDown();
            } finally {
                settings.unload();
            }
        }
    }

    private static class MapSettingLoader implements SettingLoader {
        private final Map<String, String> map = new HashMap<>();

        public ThreadLocalSettingsTest.MapSettingLoader put(String key, String value) {
            map.put(key, value);
            return this;
        }

        public ThreadLocalSettingsTest.MapSettingLoader remove(String key) {
            map.remove(key);
            return this;
        }

        @Override
        public String load(String key) {
            return map.get(key);
        }

        @Override
        public Map<String, String> loadAll() {
            return Collections.unmodifiableMap(map);
        }
    }
}

