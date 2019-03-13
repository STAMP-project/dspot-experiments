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
package org.sonar.batch.bootstrapper;


import LoggingConfiguration.FORMAT_DEFAULT;
import LoggingConfiguration.FORMAT_MAVEN;
import LoggingConfiguration.LEVEL_ROOT_DEFAULT;
import LoggingConfiguration.LEVEL_ROOT_VERBOSE;
import LoggingConfiguration.PROPERTY_FORMAT;
import LoggingConfiguration.PROPERTY_ROOT_LOGGER_LEVEL;
import com.google.common.collect.Maps;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;


public class LoggingConfigurationTest {
    @Test
    public void testSetVerbose() {
        assertThat(new LoggingConfiguration(null).setVerbose(true).getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_VERBOSE);
        assertThat(new LoggingConfiguration(null).setVerbose(false).getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_DEFAULT);
        assertThat(new LoggingConfiguration(null).setRootLevel("ERROR").getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("ERROR");
    }

    @Test
    public void testSetVerboseAnalysis() {
        Map<String, String> props = Maps.newHashMap();
        LoggingConfiguration conf = new LoggingConfiguration(null).setProperties(props);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_DEFAULT);
        props.put("sonar.verbose", "true");
        conf.setProperties(props);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_VERBOSE);
    }

    @Test
    public void shouldNotBeVerboseByDefault() {
        assertThat(new LoggingConfiguration(null).getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_DEFAULT);
    }

    @Test
    public void test_log_listener_setter() {
        LogOutput listener = Mockito.mock(LogOutput.class);
        assertThat(new LoggingConfiguration(null).setLogOutput(listener).getLogOutput()).isEqualTo(listener);
    }

    @Test
    public void test_deprecated_log_properties() {
        Map<String, String> properties = Maps.newHashMap();
        assertThat(new LoggingConfiguration(null).setProperties(properties).getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_DEFAULT);
        properties.put("sonar.verbose", "true");
        LoggingConfiguration conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_VERBOSE);
        properties.put("sonar.verbose", "false");
        conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo(LEVEL_ROOT_DEFAULT);
        properties.put("sonar.verbose", "false");
        properties.put("sonar.log.profilingLevel", "FULL");
        conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("DEBUG");
        properties.put("sonar.verbose", "false");
        properties.put("sonar.log.profilingLevel", "BASIC");
        conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("DEBUG");
    }

    @Test
    public void test_log_level_property() {
        Map<String, String> properties = Maps.newHashMap();
        LoggingConfiguration conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("INFO");
        properties.put("sonar.log.level", "INFO");
        conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("INFO");
        properties.put("sonar.log.level", "DEBUG");
        conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("DEBUG");
        properties.put("sonar.log.level", "TRACE");
        conf = new LoggingConfiguration(null).setProperties(properties);
        assertThat(conf.getSubstitutionVariable(PROPERTY_ROOT_LOGGER_LEVEL)).isEqualTo("DEBUG");
    }

    @Test
    public void testDefaultFormat() {
        assertThat(new LoggingConfiguration(null).getSubstitutionVariable(PROPERTY_FORMAT)).isEqualTo(FORMAT_DEFAULT);
    }

    @Test
    public void testMavenFormat() {
        assertThat(new LoggingConfiguration(new EnvironmentInformation("maven", "1.0")).getSubstitutionVariable(PROPERTY_FORMAT)).isEqualTo(FORMAT_MAVEN);
    }

    @Test
    public void testSetFormat() {
        assertThat(new LoggingConfiguration(null).setFormat("%d %level").getSubstitutionVariable(PROPERTY_FORMAT)).isEqualTo("%d %level");
    }

    @Test
    public void shouldNotSetBlankFormat() {
        assertThat(new LoggingConfiguration(null).setFormat(null).getSubstitutionVariable(PROPERTY_FORMAT)).isEqualTo(FORMAT_DEFAULT);
        assertThat(new LoggingConfiguration(null).setFormat("").getSubstitutionVariable(PROPERTY_FORMAT)).isEqualTo(FORMAT_DEFAULT);
        assertThat(new LoggingConfiguration(null).setFormat("   ").getSubstitutionVariable(PROPERTY_FORMAT)).isEqualTo(FORMAT_DEFAULT);
    }
}

