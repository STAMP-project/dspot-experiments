/**
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.objectmapping;


import Type.MISSING;
import Type.VALUE;
import io.helidon.common.CollectionsHelper;
import io.helidon.config.Config;
import io.helidon.config.ConfigMappingException;
import io.helidon.config.ConfigSources;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Tests {@link io.helidon.config.ConfigMappers} with focus on generic config mapping support, aka deserialization.
 */
public class GenericConfigMapperTest {
    private static final boolean DEBUG = false;

    @Test
    public void testLoadWholeBean() {
        Config config = Config.builder().sources(ConfigSources.create(// names
        // uid
        // greeting
        // pageSize
        // basicRange
        // logging
        // security
        GenericConfigMapperTest.prepareConfigApp(false, true, true, true, true, true, true).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        GenericConfigMapperTest.print(config);
        MatcherAssert.assertThat(config.get("app.uid").type(), Matchers.is(MISSING));
        MatcherAssert.assertThat(config.get("app.security.providers.0.uid").type(), Matchers.is(MISSING));
        MatcherAssert.assertThat(config.get("app.security.providers.1.uid").type(), Matchers.is(MISSING));
        GenericConfigMapperTest.AppConfig appConfig = config.get("app").as(GenericConfigMapperTest.AppConfig.class).get();
        MatcherAssert.assertThat(appConfig.getUid(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(appConfig.getGreeting(), Matchers.is("Hello"));
        MatcherAssert.assertThat(appConfig.isGreetingSetterCalled(), Matchers.is(true));
        MatcherAssert.assertThat(appConfig.getPageSize(), Matchers.is(20));
        MatcherAssert.assertThat(appConfig.getBasicRange(), Matchers.contains((-10), 10));
        MatcherAssert.assertThat(appConfig.getLogging().getLevel(""), Matchers.is(Optional.of("WARN")));
        MatcherAssert.assertThat(appConfig.getLogging().getLevel("io.helidon.config"), Matchers.is(Optional.of("CONFIG")));
        MatcherAssert.assertThat(appConfig.getLogging().getLevel("my.app"), Matchers.is(Optional.of("FINER")));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(0).getUid(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(0).getName(), Matchers.is("Provider1"));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(0).getClazz(), Matchers.equalTo(GenericConfigMapperTest.TestProvider1.class));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(1).getUid(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(1).getName(), Matchers.is("Provider2"));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(1).getClazz(), Matchers.equalTo(GenericConfigMapperTest.TestProvider2.class));
        MatcherAssert.assertThat(appConfig.getNames().entrySet(), Matchers.hasSize(3));
        MatcherAssert.assertThat(appConfig.getNames(), Matchers.hasEntry("app.names.pavel", "machr"));
        MatcherAssert.assertThat(appConfig.getNames(), Matchers.hasEntry("app.names.ondrej", "hipsta"));
        MatcherAssert.assertThat(appConfig.getNames(), Matchers.hasEntry("app.names.marek", "lebigboss"));
    }

    @Test
    public void testTransient() {
        Config config = Config.builder().sources(ConfigSources.create(// names
        // UID
        // greeting
        // pageSize
        // basicRange
        // logging
        // security
        GenericConfigMapperTest.prepareConfigApp(true, true, true, true, true, true, true).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        GenericConfigMapperTest.print(config);
        MatcherAssert.assertThat(config.get("app.uid").type(), Matchers.is(VALUE));
        MatcherAssert.assertThat(config.get("app.security.providers.0.uid").type(), Matchers.is(VALUE));
        MatcherAssert.assertThat(config.get("app.security.providers.1.uid").type(), Matchers.is(VALUE));
        GenericConfigMapperTest.AppConfig appConfig = config.get("app").as(GenericConfigMapperTest.AppConfig.class).get();
        MatcherAssert.assertThat(appConfig.getUid(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(0).getUid(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(appConfig.getSecurity().getProviders().get(1).getUid(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void testNotSetValues() {
        Config config = Config.builder().sources(ConfigSources.create(// NAMES
        // uid
        // greeting
        // pageSize
        // basicRange
        // LOGGING
        // SECURITY
        GenericConfigMapperTest.prepareConfigApp(false, true, true, true, false, false, false).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        GenericConfigMapperTest.print(config);
        GenericConfigMapperTest.AppConfig appConfig = config.get("app").as(GenericConfigMapperTest.AppConfig.class).get();
        MatcherAssert.assertThat(appConfig.getLogging().getLevel(""), Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(appConfig.getLogging().getLevel("io.helidon.config"), Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(appConfig.getLogging().getLevel("my.app"), Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(appConfig.getSecurity(), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(appConfig.getNames().entrySet(), Matchers.hasSize(0));
    }

    @Test
    public void testWithDefault() {
        Config config = Config.builder().sources(ConfigSources.create(// names
        // uid
        // greeting
        // PAGESIZE
        // basicRange
        // logging
        // security
        GenericConfigMapperTest.prepareConfigApp(false, true, false, true, true, true, true).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        GenericConfigMapperTest.print(config);
        GenericConfigMapperTest.AppConfig appConfig = config.get("app").as(GenericConfigMapperTest.AppConfig.class).get();
        MatcherAssert.assertThat(appConfig.getPageSize(), Matchers.is(10));
    }

    @Test
    public void testWithDefaultSupplier() {
        Config config = Config.builder().sources(ConfigSources.create(// names
        // uid
        // greeting
        // pageSize
        // BASICRANGE
        // logging
        // security
        GenericConfigMapperTest.prepareConfigApp(false, true, true, false, true, true, true).build())).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        GenericConfigMapperTest.print(config);
        GenericConfigMapperTest.AppConfig appConfig = config.get("app").as(GenericConfigMapperTest.AppConfig.class).get();
        MatcherAssert.assertThat(appConfig.getBasicRange(), Matchers.contains((-5), 5));
    }

    @Test
    public void testWithDefaultWrongFormat() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("numberWithDefaultSupplier", "42"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        Assertions.assertThrows(ConfigMappingException.class, () -> {
            config.as(GenericConfigMapperTest.WrongDefaultBean.class).get();
        });
    }

    @Test
    public void testWithDefaultSupplierWrongReturnType() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("numberWithDefault", "23"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        Assertions.assertThrows(ConfigMappingException.class, () -> {
            config.as(GenericConfigMapperTest.WrongDefaultBean.class).get();
        });
    }

    @Test
    public void testWrongDefaultsNotUsed() {
        Config config = Config.builder(ConfigSources.create(CollectionsHelper.mapOf("numberWithDefault", "23", "numberWithDefaultSupplier", "42"))).disableEnvironmentVariablesSource().disableSystemPropertiesSource().build();
        GenericConfigMapperTest.WrongDefaultBean wrongDefaultBean = config.as(GenericConfigMapperTest.WrongDefaultBean.class).get();
        MatcherAssert.assertThat(wrongDefaultBean.numberWithDefault, Matchers.is(23));
        MatcherAssert.assertThat(wrongDefaultBean.numberWithDefaultSupplier, Matchers.is(42));
    }

    /**
     * Represents JavaBean that can be initialized by generic mapping support.
     * <p>
     * It accepts following config structure:
     * <pre>
     * app {
     *     greeting= Hello
     *     page_size= 20
     *     basic-range= [ -10, 10 ]
     *     logging {
     *         level= WARN
     *         io.helidon.config.level= CONFIG
     *         my.app.level= FINER
     *     }
     *     security {
     *         providers: [
     *             {
     *                 name= Provider1
     *                 class= io.helidon.config.GenericConfigMapperTest$TestProvider1
     *             },
     *             {
     *                 name= Provider2
     *                 class= io.helidon.config.GenericConfigMapperTest$TestProvider2
     *             }
     *         ]
     *     names {
     *         pavel= machr
     *         ondrej= hipsta
     *         marek= lebigboss
     *     }
     * }
     * </pre>
     * Following JavaBean fields are marked as {@link Transient}, i.e. will not be loaded from configuration:
     * <pre>
     * app.uid
     * app.security.providers.*.uid
     * </pre>
     */
    public static class AppConfig {
        public Long uid;

        public String greeting;

        private Boolean greetingSetterCalled;

        // ignored, used on setter
        @Value(key = "page-size", withDefault = "42")
        private Integer pageSize;

        private List<Integer> basicRange;

        private GenericConfigMapperTest.AppConfig.LoggingConfig logging = new GenericConfigMapperTest.AppConfig.LoggingConfig(Config.empty());

        // ignored, used on setter
        @Value(withDefaultSupplier = GenericConfigMapperTest.AppConfig.DefaultSecurityConfigSupplier.class)
        public GenericConfigMapperTest.AppConfig.SecurityConfig security;

        public Map<String, String> names = CollectionsHelper.mapOf();

        public AppConfig() {
            greetingSetterCalled = false;
            names = CollectionsHelper.mapOf();
        }

        public Long getUid() {
            return uid;
        }

        @Transient
        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getGreeting() {
            return greeting;
        }

        public void setGreeting(String greeting) {
            this.greeting = greeting;
            greetingSetterCalled = true;
        }

        public boolean isGreetingSetterCalled() {
            return greetingSetterCalled;
        }

        public int getPageSize() {
            return pageSize;
        }

        @Value(withDefault = "10")
        public void setPage_size(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<Integer> getBasicRange() {
            return basicRange;
        }

        @Value(key = "basic-range", withDefault = "ignored value", withDefaultSupplier = GenericConfigMapperTest.AppConfig.DefaultBasicRangeSupplier.class)
        public void setBasicRange(List<Integer> basicRange) {
            this.basicRange = basicRange;
        }

        public void setLogging(GenericConfigMapperTest.AppConfig.LoggingConfig loggingConfig) {
            this.logging = loggingConfig;
        }

        @Value
        public void setSecurity(GenericConfigMapperTest.AppConfig.SecurityConfig securityConfig) {
            this.security = securityConfig;
        }

        public Boolean getGreetingSetterCalled() {
            return greetingSetterCalled;
        }

        public GenericConfigMapperTest.AppConfig.LoggingConfig getLogging() {
            return logging;
        }

        public GenericConfigMapperTest.AppConfig.SecurityConfig getSecurity() {
            return security;
        }

        public Map<String, String> getNames() {
            return names;
        }

        /**
         * To test implicit {@link LoggingConfig#from(Config)} mapping support for custom objects.
         */
        public static class LoggingConfig {
            private Config config;

            private LoggingConfig(Config config) {
                this.config = config;
            }

            public static GenericConfigMapperTest.AppConfig.LoggingConfig from(Config config) {
                return new GenericConfigMapperTest.AppConfig.LoggingConfig(config);
            }

            public Optional<String> getLevel(String name) {
                return config.get(name).get("level").asString().asOptional();
            }
        }

        /**
         * Contains list of other objects.
         */
        public static class SecurityConfig {
            public List<GenericConfigMapperTest.AppConfig.ProviderConfig> providers;

            public SecurityConfig() {
                providers = CollectionsHelper.listOf();
            }

            public List<GenericConfigMapperTest.AppConfig.ProviderConfig> getProviders() {
                return providers;
            }

            public void setProviders(List<GenericConfigMapperTest.AppConfig.ProviderConfig> providers) {
                this.providers = providers;
            }
        }

        public static class ProviderConfig {
            @Transient
            public Long uid;

            public String name;

            @Value(key = "class")
            public Class<?> clazz;

            public Long getUid() {
                return uid;
            }

            public String getName() {
                return name;
            }

            public Class<?> getClazz() {
                return clazz;
            }
        }

        public static class DefaultBasicRangeSupplier implements Supplier<List<Integer>> {
            @Override
            public List<Integer> get() {
                return CollectionsHelper.listOf((-5), 5);
            }
        }

        public static class DefaultSecurityConfigSupplier implements Supplier<GenericConfigMapperTest.AppConfig.SecurityConfig> {
            @Override
            public GenericConfigMapperTest.AppConfig.SecurityConfig get() {
                return new GenericConfigMapperTest.AppConfig.SecurityConfig();
            }
        }
    }

    /**
     *
     *
     * @see AppConfig.ProviderConfig
     */
    public static class TestProvider1 {}

    /**
     *
     *
     * @see AppConfig.ProviderConfig
     */
    public static class TestProvider2 {}

    public static class WrongDefaultBean {
        @Value(withDefault = "wrong value")
        public int numberWithDefault;

        @Value(withDefaultSupplier = GenericConfigMapperTest.UuidStringSupplier.class)
        public int numberWithDefaultSupplier;
    }

    public static class UuidStringSupplier implements Supplier<String> {
        @Override
        public String get() {
            return UUID.randomUUID().toString();
        }
    }
}

