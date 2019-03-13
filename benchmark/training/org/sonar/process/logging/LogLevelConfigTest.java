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
package org.sonar.process.logging;


import Level.DEBUG;
import Level.ERROR;
import Level.INFO;
import LogDomain.JMX;
import LogDomain.SQL;
import LogLevelConfig.Builder;
import ProcessId.ELASTICSEARCH;
import ProcessId.WEB_SERVER;
import java.util.Collections;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LogLevelConfigTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final String rootLoggerName = RandomStringUtils.randomAlphabetic(20);

    private Builder underTest = LogLevelConfig.newBuilder(rootLoggerName);

    @Test
    public void newBuilder_throws_NPE_if_rootLoggerName_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("rootLoggerName can't be null");
        LogLevelConfig.newBuilder(null);
    }

    @Test
    public void getLoggerName_returns_name_passed_to_builder() {
        String rootLoggerName = RandomStringUtils.randomAlphabetic(32);
        LogLevelConfig logLevelConfig = LogLevelConfig.newBuilder(rootLoggerName).build();
        assertThat(logLevelConfig.getRootLoggerName()).isEqualTo(rootLoggerName);
    }

    @Test
    public void build_can_create_empty_config_and_returned_maps_are_unmodifiable() {
        LogLevelConfig underTest = LogLevelConfig.newBuilder(rootLoggerName).build();
        LogLevelConfigTest.expectUnsupportedOperationException(() -> underTest.getConfiguredByProperties().put("1", Collections.emptyList()));
        LogLevelConfigTest.expectUnsupportedOperationException(() -> underTest.getConfiguredByHardcodedLevel().put("1", ERROR));
    }

    @Test
    public void builder_rootLevelFor_add_global_and_process_property_in_order_for_root_logger() {
        LogLevelConfig underTest = LogLevelConfig.newBuilder(rootLoggerName).rootLevelFor(ELASTICSEARCH).build();
        assertThat(underTest.getConfiguredByProperties()).hasSize(1);
        assertThat(underTest.getConfiguredByProperties().get(rootLoggerName)).containsExactly("sonar.log.level", "sonar.log.level.es");
        assertThat(underTest.getConfiguredByHardcodedLevel()).hasSize(0);
    }

    @Test
    public void builder_rootLevelFor_fails_with_ProcessId_if_loggerName_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("ProcessId can't be null");
        underTest.rootLevelFor(null);
    }

    @Test
    public void builder_rootLevelFor_fails_with_ISE_if_called_twice() {
        underTest.rootLevelFor(ELASTICSEARCH);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Configuration by property already registered for " + (rootLoggerName)));
        underTest.rootLevelFor(WEB_SERVER);
    }

    @Test
    public void builder_levelByDomain_fails_with_NPE_if_loggerName_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("loggerName can't be null");
        underTest.levelByDomain(null, WEB_SERVER, JMX);
    }

    @Test
    public void builder_levelByDomain_fails_with_IAE_if_loggerName_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("loggerName can't be empty");
        underTest.levelByDomain("", WEB_SERVER, JMX);
    }

    @Test
    public void builder_levelByDomain_fails_with_NPE_if_ProcessId_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("ProcessId can't be null");
        underTest.levelByDomain("bar", null, JMX);
    }

    @Test
    public void builder_levelByDomain_fails_with_NPE_if_LogDomain_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("LogDomain can't be null");
        underTest.levelByDomain("bar", WEB_SERVER, null);
    }

    @Test
    public void builder_levelByDomain_adds_global_process_and_domain_properties_in_order_for_specified_logger() {
        LogLevelConfig underTest = LogLevelConfig.newBuilder(rootLoggerName).levelByDomain("foo", WEB_SERVER, SQL).build();
        assertThat(underTest.getConfiguredByProperties()).hasSize(1);
        assertThat(underTest.getConfiguredByProperties().get("foo")).containsExactly("sonar.log.level", "sonar.log.level.web", "sonar.log.level.web.sql");
        assertThat(underTest.getConfiguredByHardcodedLevel()).hasSize(0);
    }

    @Test
    public void builder_levelByDomain_fails_with_ISE_if_loggerName_has_immutableLevel() {
        underTest.immutableLevel("bar", INFO);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration hardcoded level already registered for bar");
        underTest.levelByDomain("bar", WEB_SERVER, JMX);
    }

    @Test
    public void builder_immutableLevel_fails_with_NPE_if_logger_name_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("loggerName can't be null");
        underTest.immutableLevel(null, ERROR);
    }

    @Test
    public void builder_immutableLevel_fails_with_IAE_if_logger_name_is_empty() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("loggerName can't be empty");
        underTest.immutableLevel("", ERROR);
    }

    @Test
    public void builder_immutableLevel_fails_with_NPE_if_level_is_null() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("level can't be null");
        underTest.immutableLevel("foo", null);
    }

    @Test
    public void builder_immutableLevel_set_specified_level_for_specified_logger() {
        LogLevelConfig config = underTest.immutableLevel("bar", INFO).build();
        assertThat(config.getConfiguredByProperties()).isEmpty();
        assertThat(config.getConfiguredByHardcodedLevel()).hasSize(1);
        assertThat(config.getConfiguredByHardcodedLevel().get("bar")).isEqualTo(INFO);
    }

    @Test
    public void builder_fails_with_ISE_if_immutableLevel_called_twice_for_same_logger() {
        underTest.immutableLevel("foo", INFO);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration hardcoded level already registered for foo");
        underTest.immutableLevel("foo", DEBUG);
    }

    @Test
    public void builder_fails_with_ISE_if_logger_has_domain_config() {
        underTest.levelByDomain("pop", WEB_SERVER, JMX);
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Configuration by property already registered for pop");
        underTest.immutableLevel("pop", DEBUG);
    }
}

