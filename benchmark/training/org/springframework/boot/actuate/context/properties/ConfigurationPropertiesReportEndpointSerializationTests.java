/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.actuate.context.properties;


import com.zaxxer.hikari.HikariDataSource;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import org.junit.Test;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ApplicationConfigurationProperties;
import org.springframework.boot.actuate.context.properties.ConfigurationPropertiesReportEndpoint.ConfigurationPropertiesBeanDescriptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * Tests for {@link ConfigurationPropertiesReportEndpoint} serialization.
 *
 * @author Dave Syer
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 */
public class ConfigurationPropertiesReportEndpointSerializationTests {
    @Test
    public void testNaming() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.FooConfig.class).withPropertyValues("foo.name:foo");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo).isNotNull();
            assertThat(foo.getPrefix()).isEqualTo("foo");
            Map<String, Object> map = foo.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).hasSize(2);
            assertThat(map.get("name")).isEqualTo("foo");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNestedNaming() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.FooConfig.class).withPropertyValues("foo.bar.name:foo");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo).isNotNull();
            Map<String, Object> map = foo.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).hasSize(2);
            assertThat(((Map<String, Object>) (map.get("bar"))).get("name")).isEqualTo("foo");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSelfReferentialProperty() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.SelfReferentialConfig.class).withPropertyValues("foo.name:foo");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo.getPrefix()).isEqualTo("foo");
            Map<String, Object> map = foo.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).containsOnlyKeys("bar", "name");
            assertThat(map).containsEntry("name", "foo");
            Map<String, Object> bar = ((Map<String, Object>) (map.get("bar")));
            assertThat(bar).containsOnlyKeys("name");
            assertThat(bar).containsEntry("name", "123456");
        });
    }

    @Test
    public void testCycle() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.CycleConfig.class);
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor cycle = applicationProperties.getContexts().get(context.getId()).getBeans().get("cycle");
            assertThat(cycle.getPrefix()).isEqualTo("cycle");
            Map<String, Object> map = cycle.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).containsOnlyKeys("error");
            assertThat(map).containsEntry("error", "Cannot serialize 'cycle'");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMap() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.MapConfig.class).withPropertyValues("foo.map.name:foo");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor fooProperties = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(fooProperties).isNotNull();
            assertThat(fooProperties.getPrefix()).isEqualTo("foo");
            Map<String, Object> map = fooProperties.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).hasSize(3);
            assertThat(((Map<String, Object>) (map.get("map"))).get("name")).isEqualTo("foo");
        });
    }

    @Test
    public void testEmptyMapIsNotAdded() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.MapConfig.class);
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo).isNotNull();
            assertThat(foo.getPrefix()).isEqualTo("foo");
            Map<String, Object> map = foo.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).hasSize(2);
            assertThat(map).doesNotContainKey("map");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testList() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.ListConfig.class).withPropertyValues("foo.list[0]:foo");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo).isNotNull();
            assertThat(foo.getPrefix()).isEqualTo("foo");
            Map<String, Object> map = foo.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).hasSize(3);
            assertThat(((List<String>) (map.get("list"))).get(0)).isEqualTo("foo");
        });
    }

    @Test
    public void testInetAddress() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.AddressedConfig.class).withPropertyValues("foo.address:192.168.1.10");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo).isNotNull();
            assertThat(foo.getPrefix()).isEqualTo("foo");
            Map<String, Object> map = foo.getProperties();
            assertThat(map).isNotNull();
            assertThat(map).hasSize(3);
            assertThat(map.get("address")).isEqualTo("192.168.1.10");
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInitializedMapAndList() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.InitializedMapAndListPropertiesConfig.class).withPropertyValues("foo.map.entryOne:true", "foo.list[0]:abc");
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor foo = applicationProperties.getContexts().get(context.getId()).getBeans().get("foo");
            assertThat(foo.getPrefix()).isEqualTo("foo");
            Map<String, Object> propertiesMap = foo.getProperties();
            assertThat(propertiesMap).containsOnlyKeys("bar", "name", "map", "list");
            Map<String, Object> map = ((Map<String, Object>) (propertiesMap.get("map")));
            assertThat(map).containsOnly(entry("entryOne", true));
            List<String> list = ((List<String>) (propertiesMap.get("list")));
            assertThat(list).containsExactly("abc");
        });
    }

    @Test
    public void hikariDataSourceConfigurationPropertiesBeanCanBeSerialized() {
        ApplicationContextRunner contextRunner = new ApplicationContextRunner().withUserConfiguration(ConfigurationPropertiesReportEndpointSerializationTests.HikariDataSourceConfig.class);
        contextRunner.run(( context) -> {
            ConfigurationPropertiesReportEndpoint endpoint = context.getBean(.class);
            ApplicationConfigurationProperties applicationProperties = endpoint.configurationProperties();
            ConfigurationPropertiesBeanDescriptor hikariDataSource = applicationProperties.getContexts().get(context.getId()).getBeans().get("hikariDataSource");
            Map<String, Object> nestedProperties = hikariDataSource.getProperties();
            assertThat(nestedProperties).doesNotContainKey("error");
        });
    }

    @Configuration
    @EnableConfigurationProperties
    public static class Base {
        @Bean
        public ConfigurationPropertiesReportEndpoint endpoint() {
            return new ConfigurationPropertiesReportEndpoint();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class FooConfig {
        @Bean
        @ConfigurationProperties(prefix = "foo")
        public ConfigurationPropertiesReportEndpointSerializationTests.Foo foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.Foo();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class SelfReferentialConfig {
        @Bean
        @ConfigurationProperties(prefix = "foo")
        public ConfigurationPropertiesReportEndpointSerializationTests.SelfReferential foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.SelfReferential();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class MetadataCycleConfig {
        @Bean
        @ConfigurationProperties(prefix = "bar")
        public ConfigurationPropertiesReportEndpointSerializationTests.SelfReferential foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.SelfReferential();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class MapConfig {
        @Bean
        @ConfigurationProperties(prefix = "foo")
        public ConfigurationPropertiesReportEndpointSerializationTests.MapHolder foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.MapHolder();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class ListConfig {
        @Bean
        @ConfigurationProperties(prefix = "foo")
        public ConfigurationPropertiesReportEndpointSerializationTests.ListHolder foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.ListHolder();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class MetadataMapConfig {
        @Bean
        @ConfigurationProperties(prefix = "spam")
        public ConfigurationPropertiesReportEndpointSerializationTests.MapHolder foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.MapHolder();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class AddressedConfig {
        @Bean
        @ConfigurationProperties(prefix = "foo")
        public ConfigurationPropertiesReportEndpointSerializationTests.Addressed foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.Addressed();
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    public static class InitializedMapAndListPropertiesConfig {
        @Bean
        @ConfigurationProperties(prefix = "foo")
        public ConfigurationPropertiesReportEndpointSerializationTests.InitializedMapAndListProperties foo() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.InitializedMapAndListProperties();
        }
    }

    public static class Foo {
        private String name = "654321";

        private ConfigurationPropertiesReportEndpointSerializationTests.Foo.Bar bar = new ConfigurationPropertiesReportEndpointSerializationTests.Foo.Bar();

        public ConfigurationPropertiesReportEndpointSerializationTests.Foo.Bar getBar() {
            return this.bar;
        }

        public void setBar(ConfigurationPropertiesReportEndpointSerializationTests.Foo.Bar bar) {
            this.bar = bar;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // No setter so it doesn't appear in the report
        public String getSummary() {
            return "Name: " + (this.name);
        }

        public static class Bar {
            private String name = "123456";

            public String getName() {
                return this.name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }

    public static class SelfReferential extends ConfigurationPropertiesReportEndpointSerializationTests.Foo {
        private ConfigurationPropertiesReportEndpointSerializationTests.Foo self;

        public SelfReferential() {
            this.self = this;
        }

        public ConfigurationPropertiesReportEndpointSerializationTests.Foo getSelf() {
            return this.self;
        }

        public void setSelf(ConfigurationPropertiesReportEndpointSerializationTests.Foo self) {
            this.self = self;
        }
    }

    public static class MapHolder extends ConfigurationPropertiesReportEndpointSerializationTests.Foo {
        private java.util.Map<String, Object> map;

        public java.util.Map<String, Object> getMap() {
            return this.map;
        }

        public void setMap(java.util.Map<String, Object> map) {
            this.map = map;
        }
    }

    public static class ListHolder extends ConfigurationPropertiesReportEndpointSerializationTests.Foo {
        private java.util.List<String> list;

        public java.util.List<String> getList() {
            return this.list;
        }

        public void setList(java.util.List<String> list) {
            this.list = list;
        }
    }

    public static class Addressed extends ConfigurationPropertiesReportEndpointSerializationTests.Foo {
        private InetAddress address;

        public InetAddress getAddress() {
            return this.address;
        }

        public void setAddress(InetAddress address) {
            this.address = address;
        }
    }

    public static class InitializedMapAndListProperties extends ConfigurationPropertiesReportEndpointSerializationTests.Foo {
        private java.util.Map<String, Boolean> map = new HashMap<>();

        private java.util.List<String> list = new ArrayList<>();

        public java.util.Map<String, Boolean> getMap() {
            return this.map;
        }

        public java.util.List<String> getList() {
            return this.list;
        }
    }

    static class Cycle {
        private final ConfigurationPropertiesReportEndpointSerializationTests.Cycle.Alpha alpha = new ConfigurationPropertiesReportEndpointSerializationTests.Cycle.Alpha(this);

        public ConfigurationPropertiesReportEndpointSerializationTests.Cycle.Alpha getAlpha() {
            return this.alpha;
        }

        static class Alpha {
            private final ConfigurationPropertiesReportEndpointSerializationTests.Cycle cycle;

            Alpha(ConfigurationPropertiesReportEndpointSerializationTests.Cycle cycle) {
                this.cycle = cycle;
            }

            public ConfigurationPropertiesReportEndpointSerializationTests.Cycle getCycle() {
                return this.cycle;
            }
        }
    }

    @Configuration
    @Import(ConfigurationPropertiesReportEndpointSerializationTests.Base.class)
    static class CycleConfig {
        // gh-11037
        @Bean
        @ConfigurationProperties(prefix = "cycle")
        public ConfigurationPropertiesReportEndpointSerializationTests.Cycle cycle() {
            return new ConfigurationPropertiesReportEndpointSerializationTests.Cycle();
        }
    }

    @Configuration
    @EnableConfigurationProperties
    static class HikariDataSourceConfig {
        @Bean
        public ConfigurationPropertiesReportEndpoint endpoint() {
            return new ConfigurationPropertiesReportEndpoint();
        }

        @Bean
        @ConfigurationProperties(prefix = "test.datasource")
        public HikariDataSource hikariDataSource() {
            return new HikariDataSource();
        }
    }
}

