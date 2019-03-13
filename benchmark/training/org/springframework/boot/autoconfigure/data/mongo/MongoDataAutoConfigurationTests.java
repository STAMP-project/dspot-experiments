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
package org.springframework.boot.autoconfigure.data.mongo;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoClients;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.city.City;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mapping.model.CamelCaseAbbreviatingFieldNamingStrategy;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.test.util.ReflectionTestUtils;


/**
 * Tests for {@link MongoDataAutoConfiguration}.
 *
 * @author Josh Long
 * @author Oliver Gierke
 */
public class MongoDataAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class, MongoAutoConfiguration.class, MongoDataAutoConfiguration.class));

    @Test
    public void templateExists() {
        this.contextRunner.run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void gridFsTemplateExists() {
        this.contextRunner.withPropertyValues("spring.data.mongodb.gridFsDatabase:grid").run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void customConversions() {
        this.contextRunner.withUserConfiguration(MongoDataAutoConfigurationTests.CustomConversionsConfig.class).run(( context) -> {
            MongoTemplate template = context.getBean(.class);
            assertThat(template.getConverter().getConversionService().canConvert(.class, .class)).isTrue();
        });
    }

    @Test
    public void usesAutoConfigurationPackageToPickUpDocumentTypes() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        String cityPackage = City.class.getPackage().getName();
        AutoConfigurationPackages.register(context, cityPackage);
        context.register(MongoAutoConfiguration.class, MongoDataAutoConfiguration.class);
        try {
            context.refresh();
            MongoDataAutoConfigurationTests.assertDomainTypesDiscovered(context.getBean(MongoMappingContext.class), City.class);
        } finally {
            context.close();
        }
    }

    @Test
    public void defaultFieldNamingStrategy() {
        this.contextRunner.run(( context) -> {
            MongoMappingContext mappingContext = context.getBean(.class);
            FieldNamingStrategy fieldNamingStrategy = ((FieldNamingStrategy) (ReflectionTestUtils.getField(mappingContext, "fieldNamingStrategy")));
            assertThat(fieldNamingStrategy.getClass()).isEqualTo(.class);
        });
    }

    @Test
    public void customFieldNamingStrategy() {
        this.contextRunner.withPropertyValues(("spring.data.mongodb.field-naming-strategy:" + (CamelCaseAbbreviatingFieldNamingStrategy.class.getName()))).run(( context) -> {
            MongoMappingContext mappingContext = context.getBean(.class);
            FieldNamingStrategy fieldNamingStrategy = ((FieldNamingStrategy) (ReflectionTestUtils.getField(mappingContext, "fieldNamingStrategy")));
            assertThat(fieldNamingStrategy.getClass()).isEqualTo(.class);
        });
    }

    @Test
    public void interfaceFieldNamingStrategy() {
        this.contextRunner.withPropertyValues(("spring.data.mongodb.field-naming-strategy:" + (FieldNamingStrategy.class.getName()))).run(( context) -> assertThat(context).getFailure().isInstanceOf(.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void entityScanShouldSetInitialEntitySet() {
        this.contextRunner.withUserConfiguration(MongoDataAutoConfigurationTests.EntityScanConfig.class).run(( context) -> {
            MongoMappingContext mappingContext = context.getBean(.class);
            Set<Class<?>> initialEntitySet = ((Set<Class<?>>) (ReflectionTestUtils.getField(mappingContext, "initialEntitySet")));
            assertThat(initialEntitySet).containsOnly(.class, .class);
        });
    }

    @Test
    public void registersDefaultSimpleTypesWithMappingContext() {
        this.contextRunner.run(( context) -> {
            MongoMappingContext mappingContext = context.getBean(.class);
            BasicMongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(.class);
            MongoPersistentProperty dateProperty = entity.getPersistentProperty("date");
            assertThat(dateProperty.isEntity()).isFalse();
        });
    }

    @Test
    public void backsOffIfMongoClientBeanIsNotPresent() {
        ApplicationContextRunner runner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(MongoDataAutoConfiguration.class));
        runner.run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Test
    public void createsMongoDbFactoryForPreferredMongoClient() {
        this.contextRunner.run(( context) -> {
            MongoDbFactory dbFactory = context.getBean(.class);
            assertThat(dbFactory).isInstanceOf(.class);
        });
    }

    @Test
    public void createsMongoDbFactoryForFallbackMongoClient() {
        this.contextRunner.withUserConfiguration(MongoDataAutoConfigurationTests.FallbackMongoClientConfiguration.class).run(( context) -> {
            MongoDbFactory dbFactory = context.getBean(.class);
            assertThat(dbFactory).isInstanceOf(.class);
        });
    }

    @Configuration
    static class CustomConversionsConfig {
        @Bean
        public MongoCustomConversions customConversions() {
            return new MongoCustomConversions(Arrays.asList(new MongoDataAutoConfigurationTests.MyConverter()));
        }
    }

    @Configuration
    @EntityScan("org.springframework.boot.autoconfigure.data.mongo")
    static class EntityScanConfig {}

    @Configuration
    static class FallbackMongoClientConfiguration {
        @Bean
        MongoClient fallbackMongoClient() {
            return MongoClients.create();
        }
    }

    private static class MyConverter implements Converter<MongoClient, Boolean> {
        @Override
        public Boolean convert(MongoClient source) {
            return null;
        }
    }

    static class Sample {
        LocalDateTime date;
    }
}

