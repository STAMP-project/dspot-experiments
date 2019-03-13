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
package org.sonar.application.es;


import Property.CLUSTER_ENABLED;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.application.logging.ListAppender;
import org.sonar.process.Props;
import org.sonar.process.System2;


public class EsSettingsTest {
    private static final boolean CLUSTER_ENABLED = true;

    private static final boolean CLUSTER_DISABLED = false;

    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ListAppender listAppender;

    @Test
    public void constructor_does_not_logs_warning_if_env_variable_ES_JVM_OPTIONS_is_not_set() {
        this.listAppender = ListAppender.attachMemoryAppenderToLoggerOf(EsSettings.class);
        Props props = minimalProps();
        System2 system2 = Mockito.mock(System2.class);
        new EsSettings(props, new EsInstallation(props), system2);
        assertThat(listAppender.getLogs()).isEmpty();
    }

    @Test
    public void constructor_does_not_logs_warning_if_env_variable_ES_JVM_OPTIONS_is_set_and_empty() {
        this.listAppender = ListAppender.attachMemoryAppenderToLoggerOf(EsSettings.class);
        Props props = minimalProps();
        System2 system2 = Mockito.mock(System2.class);
        Mockito.when(system2.getenv("ES_JVM_OPTIONS")).thenReturn("  ");
        new EsSettings(props, new EsInstallation(props), system2);
        assertThat(listAppender.getLogs()).isEmpty();
    }

    @Test
    public void constructor_logs_warning_if_env_variable_ES_JVM_OPTIONS_is_set_and_non_empty() {
        this.listAppender = ListAppender.attachMemoryAppenderToLoggerOf(EsSettings.class);
        Props props = minimalProps();
        System2 system2 = Mockito.mock(System2.class);
        Mockito.when(system2.getenv("ES_JVM_OPTIONS")).thenReturn(randomAlphanumeric(2));
        new EsSettings(props, new EsInstallation(props), system2);
        assertThat(listAppender.getLogs()).extracting(ILoggingEvent::getMessage).containsOnly(("ES_JVM_OPTIONS is defined but will be ignored. " + "Use sonar.search.javaOpts and/or sonar.search.javaAdditionalOpts in sonar.properties to specify jvm options for Elasticsearch"));
    }

    @Test
    public void test_default_settings_for_standalone_mode() throws Exception {
        File homeDir = temp.newFolder();
        Props props = new Props(new Properties());
        props.set(SEARCH_PORT.getKey(), "1234");
        props.set(SEARCH_HOST.getKey(), "127.0.0.1");
        props.set(PATH_HOME.getKey(), homeDir.getAbsolutePath());
        props.set(PATH_DATA.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_TEMP.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_LOGS.getKey(), temp.newFolder().getAbsolutePath());
        props.set(CLUSTER_NAME.getKey(), "sonarqube");
        EsSettings esSettings = new EsSettings(props, new EsInstallation(props), System2.INSTANCE);
        Map<String, String> generated = esSettings.build();
        assertThat(generated.get("transport.tcp.port")).isEqualTo("1234");
        assertThat(generated.get("transport.host")).isEqualTo("127.0.0.1");
        // no cluster, but cluster and node names are set though
        assertThat(generated.get("cluster.name")).isEqualTo("sonarqube");
        assertThat(generated.get("node.name")).isEqualTo("sonarqube");
        assertThat(generated.get("path.data")).isNotNull();
        assertThat(generated.get("path.logs")).isNotNull();
        assertThat(generated.get("path.home")).isNull();
        assertThat(generated.get("path.conf")).isNotNull();
        // http is disabled for security reasons
        assertThat(generated.get("http.enabled")).isEqualTo("false");
        assertThat(generated.get("discovery.zen.ping.unicast.hosts")).isNull();
        assertThat(generated.get("discovery.zen.minimum_master_nodes")).isEqualTo("1");
        assertThat(generated.get("discovery.initial_state_timeout")).isEqualTo("30s");
        assertThat(generated.get("action.auto_create_index")).isEqualTo("false");
    }

    @Test
    public void test_default_settings_for_cluster_mode() throws Exception {
        File homeDir = temp.newFolder();
        Props props = new Props(new Properties());
        props.set(SEARCH_PORT.getKey(), "1234");
        props.set(SEARCH_HOST.getKey(), "127.0.0.1");
        props.set(PATH_HOME.getKey(), homeDir.getAbsolutePath());
        props.set(PATH_DATA.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_TEMP.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_LOGS.getKey(), temp.newFolder().getAbsolutePath());
        props.set(CLUSTER_NAME.getKey(), "sonarqube-1");
        props.set(Property.CLUSTER_ENABLED.getKey(), "true");
        props.set(CLUSTER_NODE_NAME.getKey(), "node-1");
        EsSettings esSettings = new EsSettings(props, new EsInstallation(props), System2.INSTANCE);
        Map<String, String> generated = esSettings.build();
        assertThat(generated.get("cluster.name")).isEqualTo("sonarqube-1");
        assertThat(generated.get("node.name")).isEqualTo("node-1");
    }

    @Test
    public void test_node_name_default_for_cluster_mode() throws Exception {
        File homeDir = temp.newFolder();
        Props props = new Props(new Properties());
        props.set(CLUSTER_NAME.getKey(), "sonarqube");
        props.set(Property.CLUSTER_ENABLED.getKey(), "true");
        props.set(SEARCH_PORT.getKey(), "1234");
        props.set(SEARCH_HOST.getKey(), "127.0.0.1");
        props.set(PATH_HOME.getKey(), homeDir.getAbsolutePath());
        props.set(PATH_DATA.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_TEMP.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_LOGS.getKey(), temp.newFolder().getAbsolutePath());
        EsSettings esSettings = new EsSettings(props, new EsInstallation(props), System2.INSTANCE);
        Map<String, String> generated = esSettings.build();
        assertThat(generated.get("node.name")).startsWith("sonarqube-");
    }

    @Test
    public void test_node_name_default_for_standalone_mode() throws Exception {
        File homeDir = temp.newFolder();
        Props props = new Props(new Properties());
        props.set(CLUSTER_NAME.getKey(), "sonarqube");
        props.set(Property.CLUSTER_ENABLED.getKey(), "false");
        props.set(SEARCH_PORT.getKey(), "1234");
        props.set(SEARCH_HOST.getKey(), "127.0.0.1");
        props.set(PATH_HOME.getKey(), homeDir.getAbsolutePath());
        props.set(PATH_DATA.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_TEMP.getKey(), temp.newFolder().getAbsolutePath());
        props.set(PATH_LOGS.getKey(), temp.newFolder().getAbsolutePath());
        EsSettings esSettings = new EsSettings(props, new EsInstallation(props), System2.INSTANCE);
        Map<String, String> generated = esSettings.build();
        assertThat(generated.get("node.name")).isEqualTo("sonarqube");
    }

    @Test
    public void path_properties_are_values_from_EsFileSystem_argument() throws IOException {
        File foo = temp.newFolder();
        EsInstallation mockedEsInstallation = Mockito.mock(EsInstallation.class);
        File home = new File(foo, "home");
        Mockito.when(mockedEsInstallation.getHomeDirectory()).thenReturn(home);
        File conf = new File(foo, "conf");
        Mockito.when(mockedEsInstallation.getConfDirectory()).thenReturn(conf);
        File log = new File(foo, "log");
        Mockito.when(mockedEsInstallation.getLogDirectory()).thenReturn(log);
        File data = new File(foo, "data");
        Mockito.when(mockedEsInstallation.getDataDirectory()).thenReturn(data);
        EsSettings underTest = new EsSettings(minProps(new Random().nextBoolean()), mockedEsInstallation, System2.INSTANCE);
        Map<String, String> generated = underTest.build();
        assertThat(generated.get("path.data")).isEqualTo(data.getPath());
        assertThat(generated.get("path.logs")).isEqualTo(log.getPath());
        assertThat(generated.get("path.conf")).isEqualTo(conf.getPath());
    }

    @Test
    public void set_discovery_settings_if_cluster_is_enabled() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_ENABLED);
        props.set(CLUSTER_SEARCH_HOSTS.getKey(), "1.2.3.4:9000,1.2.3.5:8080");
        Map<String, String> settings = build();
        assertThat(settings.get("discovery.zen.ping.unicast.hosts")).isEqualTo("1.2.3.4:9000,1.2.3.5:8080");
        assertThat(settings.get("discovery.zen.minimum_master_nodes")).isEqualTo("2");
        assertThat(settings.get("discovery.initial_state_timeout")).isEqualTo("120s");
    }

    @Test
    public void incorrect_values_of_minimum_master_nodes() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_ENABLED);
        props.set(SEARCH_MINIMUM_MASTER_NODES.getKey(), "????");
        EsSettings underTest = new EsSettings(props, new EsInstallation(props), System2.INSTANCE);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Value of property sonar.search.minimumMasterNodes is not an integer:");
        underTest.build();
    }

    @Test
    public void cluster_is_enabled_with_defined_minimum_master_nodes() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_ENABLED);
        props.set(SEARCH_MINIMUM_MASTER_NODES.getKey(), "5");
        Map<String, String> settings = build();
        assertThat(settings.get("discovery.zen.minimum_master_nodes")).isEqualTo("5");
    }

    @Test
    public void cluster_is_enabled_with_defined_initialTimeout() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_ENABLED);
        props.set(SEARCH_INITIAL_STATE_TIMEOUT.getKey(), "10s");
        Map<String, String> settings = build();
        assertThat(settings.get("discovery.initial_state_timeout")).isEqualTo("10s");
    }

    @Test
    public void in_standalone_initialTimeout_is_not_overridable() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_DISABLED);
        props.set(SEARCH_INITIAL_STATE_TIMEOUT.getKey(), "10s");
        Map<String, String> settings = build();
        assertThat(settings.get("discovery.initial_state_timeout")).isEqualTo("30s");
    }

    @Test
    public void in_standalone_minimumMasterNodes_is_not_overridable() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_DISABLED);
        props.set(SEARCH_MINIMUM_MASTER_NODES.getKey(), "5");
        Map<String, String> settings = build();
        assertThat(settings.get("discovery.zen.minimum_master_nodes")).isEqualTo("1");
    }

    @Test
    public void enable_http_connector() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_DISABLED);
        props.set(SEARCH_HTTP_PORT.getKey(), "9010");
        Map<String, String> settings = build();
        assertThat(settings.get("http.port")).isEqualTo("9010");
        assertThat(settings.get("http.host")).isEqualTo("127.0.0.1");
        assertThat(settings.get("http.enabled")).isEqualTo("true");
    }

    @Test
    public void enable_http_connector_different_host() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_DISABLED);
        props.set(SEARCH_HTTP_PORT.getKey(), "9010");
        props.set(SEARCH_HOST.getKey(), "127.0.0.2");
        Map<String, String> settings = build();
        assertThat(settings.get("http.port")).isEqualTo("9010");
        assertThat(settings.get("http.host")).isEqualTo("127.0.0.2");
        assertThat(settings.get("http.enabled")).isEqualTo("true");
    }

    @Test
    public void enable_seccomp_filter_by_default() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_DISABLED);
        Map<String, String> settings = build();
        assertThat(settings.get("bootstrap.system_call_filter")).isNull();
    }

    @Test
    public void disable_seccomp_filter_if_configured_in_search_additional_props() throws Exception {
        Props props = minProps(EsSettingsTest.CLUSTER_DISABLED);
        props.set("sonar.search.javaAdditionalOpts", "-Xmx1G -Dbootstrap.system_call_filter=false -Dfoo=bar");
        Map<String, String> settings = build();
        assertThat(settings.get("bootstrap.system_call_filter")).isEqualTo("false");
    }
}

