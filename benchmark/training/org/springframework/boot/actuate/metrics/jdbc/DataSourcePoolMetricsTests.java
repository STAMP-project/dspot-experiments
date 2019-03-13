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
package org.springframework.boot.actuate.metrics.jdbc;


import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Collection;
import java.util.Collections;
import javax.sql.DataSource;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link DataSourcePoolMetrics}.
 *
 * @author Jon Schneider
 * @author Andy Wilkinson
 */
public class DataSourcePoolMetricsTests {
    @Test
    public void dataSourceIsInstrumented() {
        new ApplicationContextRunner().withUserConfiguration(DataSourcePoolMetricsTests.DataSourceConfig.class, DataSourcePoolMetricsTests.MetricsApp.class).withConfiguration(AutoConfigurations.of(DataSourceAutoConfiguration.class)).withPropertyValues("spring.datasource.generate-unique-name=true", "metrics.use-global-registry=false").run(( context) -> {
            context.getBean(.class).getConnection().getMetaData();
            context.getBean(.class).get("jdbc.connections.max").meter();
        });
    }

    @Configuration
    static class MetricsApp {
        @Bean
        MeterRegistry registry() {
            return new SimpleMeterRegistry();
        }
    }

    @Configuration
    static class DataSourceConfig {
        DataSourceConfig(DataSource dataSource, Collection<DataSourcePoolMetadataProvider> metadataProviders, MeterRegistry registry) {
            new DataSourcePoolMetrics(dataSource, metadataProviders, "data.source", Collections.emptyList()).bindTo(registry);
        }
    }
}

