/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.logging;


import LogLevelUpdateOccurs.ALWAYS;
import LogLevelUpdateOccurs.NEVER;
import LogLevelUpdateOccurs.ONLY_WHEN_USING_DEFAULT_CONFIG;
import LogLevelUpdateScope.ALL_LOGGERS;
import LogLevelUpdateScope.GEODE_AND_APPLICATION_LOGGERS;
import LogLevelUpdateScope.GEODE_AND_SECURITY_LOGGERS;
import LogLevelUpdateScope.GEODE_LOGGERS;
import org.apache.geode.test.junit.categories.LoggingTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for {@link Configuration}.
 */
@Category(LoggingTest.class)
public class ConfigurationTest {
    private ProviderAgent providerAgent;

    private LogConfig logConfig;

    private LogConfigSupplier logConfigSupplier;

    private Configuration configuration;

    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void logConfigSupplierIsNullByDefault() {
        assertThat(configuration.getLogConfigSupplier()).isNull();
    }

    @Test
    public void initializeSetsLogConfigSupplier() {
        configuration.initialize(logConfigSupplier);
        assertThat(configuration.getLogConfigSupplier()).isSameAs(logConfigSupplier);
    }

    @Test
    public void initializeAddsSelfAsListenerToLogConfigSupplier() {
        configuration.initialize(logConfigSupplier);
        Mockito.verify(logConfigSupplier).addLogConfigListener(ArgumentMatchers.eq(configuration));
    }

    @Test
    public void initializeReplacesLogConfigSupplier() {
        LogConfigSupplier logConfigSupplier1 = mockLogConfigSupplier();
        LogConfigSupplier logConfigSupplier2 = mockLogConfigSupplier();
        LogConfigSupplier logConfigSupplier3 = mockLogConfigSupplier();
        configuration.initialize(logConfigSupplier1);
        configuration.initialize(logConfigSupplier2);
        configuration.initialize(logConfigSupplier3);
        assertThat(configuration.getLogConfigSupplier()).isSameAs(logConfigSupplier3);
    }

    @Test
    public void initializeAddsSelfAsListenerOnlyOnceToEachLogConfigSupplier() {
        LogConfigSupplier logConfigSupplier1 = mockLogConfigSupplier();
        LogConfigSupplier logConfigSupplier2 = mockLogConfigSupplier();
        LogConfigSupplier logConfigSupplier3 = mockLogConfigSupplier();
        configuration.initialize(logConfigSupplier1);
        configuration.initialize(logConfigSupplier2);
        configuration.initialize(logConfigSupplier3);
        Mockito.verify(logConfigSupplier1).addLogConfigListener(ArgumentMatchers.eq(configuration));
        Mockito.verify(logConfigSupplier2).addLogConfigListener(ArgumentMatchers.eq(configuration));
        Mockito.verify(logConfigSupplier3).addLogConfigListener(ArgumentMatchers.eq(configuration));
    }

    @Test
    public void initializeWithNullThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> configuration.initialize(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void initializeConfiguresProviderAgent() {
        configuration.initialize(logConfigSupplier);
        Mockito.verify(providerAgent).configure(ArgumentMatchers.eq(logConfig), ArgumentMatchers.isA(LogLevelUpdateOccurs.class), ArgumentMatchers.isA(LogLevelUpdateScope.class));
    }

    @Test
    public void configChangedWithoutLogConfigSupplierThrowsIllegalStateException() {
        assertThatThrownBy(() -> configuration.configChanged()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void configChangedReconfiguresProviderAgent() {
        configuration.initialize(logConfigSupplier);
        configuration.configChanged();
        Mockito.verify(providerAgent, Mockito.times(2)).configure(ArgumentMatchers.eq(logConfig), ArgumentMatchers.isA(LogLevelUpdateOccurs.class), ArgumentMatchers.isA(LogLevelUpdateScope.class));
    }

    @Test
    public void shutdownRemovesCurrentLogConfigSupplier() {
        configuration.initialize(logConfigSupplier);
        configuration.shutdown();
        assertThat(configuration.getLogConfigSupplier()).isNull();
    }

    @Test
    public void shutdownRemovesSelfAsListenerFromCurrentLogConfigSupplier() {
        configuration.initialize(logConfigSupplier);
        configuration.shutdown();
        Mockito.verify(logConfigSupplier).removeLogConfigListener(ArgumentMatchers.eq(configuration));
    }

    @Test
    public void shutdownDoesNothingWithoutCurrentLogConfigSupplier() {
        configuration.shutdown();
        Mockito.verifyNoMoreInteractions(logConfigSupplier);
    }

    @Test
    public void getLogLevelUpdateOccurs_defaultsTo_ONLY_WHEN_USING_DEFAULT_CONFIG() {
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(ONLY_WHEN_USING_DEFAULT_CONFIG);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesSystemPropertySetTo_ONLY_WHEN_USING_DEFAULT_CONFIG() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, ONLY_WHEN_USING_DEFAULT_CONFIG.name());
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(ONLY_WHEN_USING_DEFAULT_CONFIG);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesSystemPropertySetTo_ALWAYS() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, ALWAYS.name());
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(ALWAYS);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesSystemPropertySetTo_NEVER() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, NEVER.name());
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(NEVER);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesSystemPropertySetTo_only_when_using_default_config() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, ONLY_WHEN_USING_DEFAULT_CONFIG.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(ONLY_WHEN_USING_DEFAULT_CONFIG);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesSystemPropertySetTo_always() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, ALWAYS.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(ALWAYS);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesSystemPropertySetTo_never() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, NEVER.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(NEVER);
    }

    @Test
    public void getLogLevelUpdateOccurs_usesDefaultWhenSystemPropertySetTo_gibberish() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_OCCURS_PROPERTY, "gibberish");
        assertThat(Configuration.getLogLevelUpdateOccurs()).isEqualTo(ONLY_WHEN_USING_DEFAULT_CONFIG);
    }

    @Test
    public void getLogLevelUpdateScope_defaultsTo_GEODE_LOGGERS() {
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_GEODE_LOGGERS() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, GEODE_LOGGERS.name());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_GEODE_AND_SECURITY_LOGGERS() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, GEODE_AND_SECURITY_LOGGERS.name());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_AND_SECURITY_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_GEODE_AND_APPLICATION_LOGGERS() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, GEODE_AND_APPLICATION_LOGGERS.name());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_AND_APPLICATION_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_ALL_LOGGERS() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, ALL_LOGGERS.name());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(ALL_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_geode_loggers() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, GEODE_LOGGERS.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_geode_and_security_loggers() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, GEODE_AND_SECURITY_LOGGERS.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_AND_SECURITY_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_geode_and_application_loggers() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, GEODE_AND_APPLICATION_LOGGERS.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_AND_APPLICATION_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesSystemPropertySetTo_all_loggers() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, ALL_LOGGERS.name().toLowerCase());
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(ALL_LOGGERS);
    }

    @Test
    public void getLogLevelUpdateScope_usesDefaultWhenSystemPropertySetTo_gibberish() {
        System.setProperty(Configuration.LOG_LEVEL_UPDATE_SCOPE_PROPERTY, "gibberish");
        assertThat(Configuration.getLogLevelUpdateScope()).isEqualTo(GEODE_LOGGERS);
    }
}

