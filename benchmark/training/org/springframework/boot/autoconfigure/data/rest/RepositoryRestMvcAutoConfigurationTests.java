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
package org.springframework.boot.autoconfigure.data.rest;


import RepositoryDetectionStrategies.ALL;
import RepositoryDetectionStrategies.VISIBILITY;
import java.net.URI;
import org.junit.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.TestAutoConfigurationPackage;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.city.City;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.BaseUri;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * Tests for {@link RepositoryRestMvcAutoConfiguration}.
 *
 * @author Rob Winch
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 */
public class RepositoryRestMvcAutoConfigurationTests {
    private AnnotationConfigWebApplicationContext context;

    @Test
    public void testDefaultRepositoryConfiguration() {
        load(RepositoryRestMvcAutoConfigurationTests.TestConfiguration.class);
        assertThat(this.context.getBean(RepositoryRestMvcConfiguration.class)).isNotNull();
    }

    @Test
    public void testWithCustomBasePath() {
        load(RepositoryRestMvcAutoConfigurationTests.TestConfiguration.class, "spring.data.rest.base-path:foo");
        assertThat(this.context.getBean(RepositoryRestMvcConfiguration.class)).isNotNull();
        RepositoryRestConfiguration bean = this.context.getBean(RepositoryRestConfiguration.class);
        URI expectedUri = URI.create("/foo");
        assertThat(bean.getBaseUri()).as("Custom basePath not set").isEqualTo(expectedUri);
        BaseUri baseUri = this.context.getBean(BaseUri.class);
        assertThat(expectedUri).as("Custom basePath has not been applied to BaseUri bean").isEqualTo(baseUri.getUri());
    }

    @Test
    public void testWithCustomSettings() {
        load(RepositoryRestMvcAutoConfigurationTests.TestConfiguration.class, "spring.data.rest.default-page-size:42", "spring.data.rest.max-page-size:78", "spring.data.rest.page-param-name:_page", "spring.data.rest.limit-param-name:_limit", "spring.data.rest.sort-param-name:_sort", "spring.data.rest.detection-strategy=visibility", "spring.data.rest.default-media-type:application/my-json", "spring.data.rest.return-body-on-create:false", "spring.data.rest.return-body-on-update:false", "spring.data.rest.enable-enum-translation:true");
        assertThat(this.context.getBean(RepositoryRestMvcConfiguration.class)).isNotNull();
        RepositoryRestConfiguration bean = this.context.getBean(RepositoryRestConfiguration.class);
        assertThat(bean.getDefaultPageSize()).isEqualTo(42);
        assertThat(bean.getMaxPageSize()).isEqualTo(78);
        assertThat(bean.getPageParamName()).isEqualTo("_page");
        assertThat(bean.getLimitParamName()).isEqualTo("_limit");
        assertThat(bean.getSortParamName()).isEqualTo("_sort");
        assertThat(bean.getRepositoryDetectionStrategy()).isEqualTo(VISIBILITY);
        assertThat(bean.getDefaultMediaType()).isEqualTo(MediaType.parseMediaType("application/my-json"));
        assertThat(bean.returnBodyOnCreate(null)).isFalse();
        assertThat(bean.returnBodyOnUpdate(null)).isFalse();
        assertThat(bean.isEnableEnumTranslation()).isTrue();
    }

    @Test
    public void testWithCustomConfigurer() {
        load(RepositoryRestMvcAutoConfigurationTests.TestConfigurationWithConfigurer.class, "spring.data.rest.detection-strategy=visibility", "spring.data.rest.default-media-type:application/my-json");
        assertThat(this.context.getBean(RepositoryRestMvcConfiguration.class)).isNotNull();
        RepositoryRestConfiguration bean = this.context.getBean(RepositoryRestConfiguration.class);
        assertThat(bean.getRepositoryDetectionStrategy()).isEqualTo(ALL);
        assertThat(bean.getDefaultMediaType()).isEqualTo(MediaType.parseMediaType("application/my-custom-json"));
        assertThat(bean.getMaxPageSize()).isEqualTo(78);
    }

    @Test
    public void backOffWithCustomConfiguration() {
        load(RepositoryRestMvcAutoConfigurationTests.TestConfigurationWithRestMvcConfig.class, "spring.data.rest.base-path:foo");
        assertThat(this.context.getBean(RepositoryRestMvcConfiguration.class)).isNotNull();
        RepositoryRestConfiguration bean = this.context.getBean(RepositoryRestConfiguration.class);
        assertThat(bean.getBaseUri()).isEqualTo(URI.create(""));
    }

    @Configuration
    @Import(EmbeddedDataSourceConfiguration.class)
    @ImportAutoConfiguration({ HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, PropertyPlaceholderAutoConfiguration.class, RepositoryRestMvcAutoConfiguration.class, JacksonAutoConfiguration.class })
    protected static class BaseConfiguration {}

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    @EnableWebMvc
    protected static class TestConfiguration {}

    @Import({ RepositoryRestMvcAutoConfigurationTests.TestConfiguration.class, RepositoryRestMvcAutoConfigurationTests.TestRepositoryRestConfigurer.class })
    protected static class TestConfigurationWithConfigurer {}

    @Import({ RepositoryRestMvcAutoConfigurationTests.TestConfiguration.class, RepositoryRestMvcConfiguration.class })
    protected static class TestConfigurationWithRestMvcConfig {}

    @Configuration
    @TestAutoConfigurationPackage(City.class)
    @EnableWebMvc
    static class TestConfigurationWithObjectMapperBuilder {
        @Bean
        public Jackson2ObjectMapperBuilder objectMapperBuilder() {
            Jackson2ObjectMapperBuilder objectMapperBuilder = new Jackson2ObjectMapperBuilder();
            objectMapperBuilder.simpleDateFormat("yyyy-MM");
            return objectMapperBuilder;
        }
    }

    static class TestRepositoryRestConfigurer implements RepositoryRestConfigurer {
        @Override
        public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
            config.setRepositoryDetectionStrategy(ALL);
            config.setDefaultMediaType(MediaType.parseMediaType("application/my-custom-json"));
            config.setMaxPageSize(78);
        }
    }
}

