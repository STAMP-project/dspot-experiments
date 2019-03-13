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
package org.springframework.boot.actuate.autoconfigure.metrics.jdbc;


import com.zaxxer.hikari.HikariDataSource;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.actuate.autoconfigure.metrics.test.MetricsRun;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;


/**
 * Tests for {@link DataSourcePoolMetricsAutoConfiguration}.
 *
 * @author Stephane Nicoll
 * @author Andy Wilkinson
 * @author Tommy Ludwig
 */
public class DataSourcePoolMetricsAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withPropertyValues("spring.datasource.generate-unique-name=true").with(MetricsRun.simple()).withConfiguration(AutoConfigurations.of(DataSourcePoolMetricsAutoConfiguration.class)).withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.BaseConfiguration.class);

    @Test
    public void autoConfiguredDataSourceIsInstrumented() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection().getMetaData();
            MeterRegistry registry = context.getBean(.class);
            registry.get("jdbc.connections.max").tags("name", "dataSource").meter();
        });
    }

    @Test
    public void dataSourceInstrumentationCanBeDisabled() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withPropertyValues("management.metrics.enable.jdbc=false").run(( context) -> {
            getConnection().getMetaData();
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("jdbc.connections.max").tags("name", "dataSource").meter()).isNull();
        });
    }

    @Test
    public void allDataSourcesCanBeInstrumented() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.TwoDataSourcesConfiguration.class).run(( context) -> {
            getConnection().getMetaData();
            getConnection().getMetaData();
            MeterRegistry registry = context.getBean(.class);
            registry.get("jdbc.connections.max").tags("name", "first").meter();
            registry.get("jdbc.connections.max").tags("name", "secondOne").meter();
        });
    }

    @Test
    public void autoConfiguredHikariDataSourceIsInstrumented() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            registry.get("hikaricp.connections").meter();
        });
    }

    @Test
    public void autoConfiguredHikariDataSourceIsInstrumentedWhenUsingDataSourceInitialization() {
        this.contextRunner.withPropertyValues("spring.datasource.schema:db/create-custom-schema.sql").withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            registry.get("hikaricp.connections").meter();
        });
    }

    @Test
    public void hikariCanBeInstrumentedAfterThePoolHasBeenSealed() {
        this.contextRunner.withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.HikariSealingConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            assertThat(context).hasNotFailed();
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("hikaricp.connections").meter()).isNotNull();
        });
    }

    @Test
    public void hikariDataSourceInstrumentationCanBeDisabled() {
        this.contextRunner.withPropertyValues("management.metrics.enable.hikaricp=false").withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.find("hikaricp.connections").meter()).isNull();
        });
    }

    @Test
    public void allHikariDataSourcesCanBeInstrumented() {
        this.contextRunner.withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.TwoHikariDataSourcesConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection();
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            registry.get("hikaricp.connections").tags("pool", "firstDataSource").meter();
            registry.get("hikaricp.connections").tags("pool", "secondOne").meter();
        });
    }

    @Test
    public void someHikariDataSourcesCanBeInstrumented() {
        this.contextRunner.withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.MixedDataSourcesConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection();
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.get("hikaricp.connections").meter().getId().getTags()).containsExactly(Tag.of("pool", "firstDataSource"));
        });
    }

    @Test
    public void hikariProxiedDataSourceCanBeInstrumented() {
        this.contextRunner.withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.ProxiedHikariDataSourcesConfiguration.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).run(( context) -> {
            getConnection();
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            registry.get("hikaricp.connections").tags("pool", "firstDataSource").meter();
            registry.get("hikaricp.connections").tags("pool", "secondOne").meter();
        });
    }

    @Test
    public void hikariDataSourceIsInstrumentedWithoutMetadataProvider() {
        this.contextRunner.withUserConfiguration(DataSourcePoolMetricsAutoConfigurationTests.OneHikariDataSourceConfiguration.class).run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            getConnection();
            MeterRegistry registry = context.getBean(.class);
            assertThat(registry.get("hikaricp.connections").meter().getId().getTags()).containsExactly(Tag.of("pool", "hikariDataSource"));
        });
    }

    @Configuration
    static class BaseConfiguration {
        @Bean
        public SimpleMeterRegistry simpleMeterRegistry() {
            return new SimpleMeterRegistry();
        }
    }

    @Configuration
    static class TwoDataSourcesConfiguration {
        @Bean
        public DataSource firstDataSource() {
            return createDataSource();
        }

        @Bean
        public DataSource secondOne() {
            return createDataSource();
        }

        private DataSource createDataSource() {
            String url = "jdbc:hsqldb:mem:test-" + (UUID.randomUUID());
            return org.springframework.boot.jdbc.DataSourceBuilder.create().url(url).build();
        }
    }

    @Configuration
    static class TwoHikariDataSourcesConfiguration {
        @Bean
        public DataSource firstDataSource() {
            return DataSourcePoolMetricsAutoConfigurationTests.createHikariDataSource("firstDataSource");
        }

        @Bean
        public DataSource secondOne() {
            return DataSourcePoolMetricsAutoConfigurationTests.createHikariDataSource("secondOne");
        }
    }

    @Configuration
    static class ProxiedHikariDataSourcesConfiguration {
        @Bean
        public DataSource proxiedDataSource() {
            return ((DataSource) (getProxy()));
        }

        @Bean
        public DataSource delegateDataSource() {
            return new org.springframework.jdbc.datasource.DelegatingDataSource(DataSourcePoolMetricsAutoConfigurationTests.createHikariDataSource("secondOne"));
        }
    }

    @Configuration
    static class OneHikariDataSourceConfiguration {
        @Bean
        public DataSource hikariDataSource() {
            return DataSourcePoolMetricsAutoConfigurationTests.createHikariDataSource("hikariDataSource");
        }
    }

    @Configuration
    static class MixedDataSourcesConfiguration {
        @Bean
        public DataSource firstDataSource() {
            return createHikariDataSource("firstDataSource");
        }

        @Bean
        public DataSource secondOne() {
            return createTomcatDataSource();
        }

        private HikariDataSource createHikariDataSource(String poolName) {
            String url = "jdbc:hsqldb:mem:test-" + (UUID.randomUUID());
            HikariDataSource hikariDataSource = org.springframework.boot.jdbc.DataSourceBuilder.create().url(url).type(HikariDataSource.class).build();
            hikariDataSource.setPoolName(poolName);
            return hikariDataSource;
        }

        private DataSource createTomcatDataSource() {
            String url = "jdbc:hsqldb:mem:test-" + (UUID.randomUUID());
            return org.springframework.boot.jdbc.DataSourceBuilder.create().url(url).type(DataSource.class).build();
        }
    }

    @Configuration
    static class HikariSealingConfiguration {
        @Bean
        public static DataSourcePoolMetricsAutoConfigurationTests.HikariSealingConfiguration.HikariSealer hikariSealer() {
            return new DataSourcePoolMetricsAutoConfigurationTests.HikariSealingConfiguration.HikariSealer();
        }

        static class HikariSealer implements BeanPostProcessor , PriorityOrdered {
            @Override
            public int getOrder() {
                return Ordered.HIGHEST_PRECEDENCE;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof HikariDataSource) {
                    try {
                        getConnection().close();
                    } catch (SQLException ex) {
                        throw new IllegalStateException(ex);
                    }
                }
                return bean;
            }
        }
    }
}

