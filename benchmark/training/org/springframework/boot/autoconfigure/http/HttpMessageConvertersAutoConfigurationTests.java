/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.autoconfigure.http;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import javax.json.bind.Jsonb;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration;
import org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.JsonbHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;


/**
 * Tests for {@link HttpMessageConvertersAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Oliver Gierke
 * @author David Liu
 * @author Andy Wilkinson
 * @author Sebastien Deleuze
 * @author Edd? Mel?ndez
 */
public class HttpMessageConvertersAutoConfigurationTests {
    private ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class));

    @Test
    public void jacksonNotAvailable() {
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void jacksonDefaultConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JacksonObjectMapperConfig.class).run(assertConverter(MappingJackson2HttpMessageConverter.class, "mappingJackson2HttpMessageConverter"));
    }

    @Test
    public void jacksonConverterWithBuilder() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JacksonObjectMapperBuilderConfig.class).run(assertConverter(MappingJackson2HttpMessageConverter.class, "mappingJackson2HttpMessageConverter"));
    }

    @Test
    public void jacksonXmlConverterWithBuilder() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JacksonObjectMapperBuilderConfig.class).run(assertConverter(MappingJackson2XmlHttpMessageConverter.class, "mappingJackson2XmlHttpMessageConverter"));
    }

    @Test
    public void jacksonCustomConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JacksonObjectMapperConfig.class, HttpMessageConvertersAutoConfigurationTests.JacksonConverterConfig.class).run(assertConverter(MappingJackson2HttpMessageConverter.class, "customJacksonMessageConverter"));
    }

    @Test
    public void gsonNotAvailable() {
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void gsonDefaultConverter() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(GsonAutoConfiguration.class)).run(assertConverter(GsonHttpMessageConverter.class, "gsonHttpMessageConverter"));
    }

    @Test
    public void gsonCustomConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.GsonConverterConfig.class).withConfiguration(AutoConfigurations.of(GsonAutoConfiguration.class)).run(assertConverter(GsonHttpMessageConverter.class, "customGsonMessageConverter"));
    }

    @Test
    public void gsonCanBePreferred() {
        allOptionsRunner().withPropertyValues("spring.http.converters.preferred-json-mapper:gson").run(( context) -> {
            assertConverterBeanExists(context, .class, "gsonHttpMessageConverter");
            assertConverterBeanRegisteredWithHttpMessageConverters(context, .class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void jsonbNotAvailable() {
        this.contextRunner.run(( context) -> {
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void jsonbDefaultConverter() {
        this.contextRunner.withConfiguration(AutoConfigurations.of(JsonbAutoConfiguration.class)).run(assertConverter(JsonbHttpMessageConverter.class, "jsonbHttpMessageConverter"));
    }

    @Test
    public void jsonbCustomConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JsonbConverterConfig.class).withConfiguration(AutoConfigurations.of(JsonbAutoConfiguration.class)).run(assertConverter(JsonbHttpMessageConverter.class, "customJsonbMessageConverter"));
    }

    @Test
    public void jsonbCanBePreferred() {
        allOptionsRunner().withPropertyValues("spring.http.converters.preferred-json-mapper:jsonb").run(( context) -> {
            assertConverterBeanExists(context, .class, "jsonbHttpMessageConverter");
            assertConverterBeanRegisteredWithHttpMessageConverters(context, .class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void stringDefaultConverter() {
        this.contextRunner.run(assertConverter(StringHttpMessageConverter.class, "stringHttpMessageConverter"));
    }

    @Test
    public void stringCustomConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.StringConverterConfig.class).run(assertConverter(StringHttpMessageConverter.class, "customStringMessageConverter"));
    }

    @Test
    public void typeConstrainedConverterDoesNotPreventAutoConfigurationOfJacksonConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JacksonObjectMapperBuilderConfig.class, HttpMessageConvertersAutoConfigurationTests.TypeConstrainedConverterConfiguration.class).run(( context) -> {
            BeanDefinition beanDefinition = ((GenericApplicationContext) (context.getSourceApplicationContext())).getBeanDefinition("mappingJackson2HttpMessageConverter");
            assertThat(beanDefinition.getFactoryBeanName()).isEqualTo(.class.getName());
        });
    }

    @Test
    public void typeConstrainedConverterFromSpringDataDoesNotPreventAutoConfigurationOfJacksonConverter() {
        this.contextRunner.withUserConfiguration(HttpMessageConvertersAutoConfigurationTests.JacksonObjectMapperBuilderConfig.class, RepositoryRestMvcConfiguration.class).run(( context) -> {
            BeanDefinition beanDefinition = ((GenericApplicationContext) (context.getSourceApplicationContext())).getBeanDefinition("mappingJackson2HttpMessageConverter");
            assertThat(beanDefinition.getFactoryBeanName()).isEqualTo(.class.getName());
        });
    }

    @Test
    public void jacksonIsPreferredByDefault() {
        allOptionsRunner().run(( context) -> {
            assertConverterBeanExists(context, .class, "mappingJackson2HttpMessageConverter");
            assertConverterBeanRegisteredWithHttpMessageConverters(context, .class);
            assertThat(context).doesNotHaveBean(.class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void gsonIsPreferredIfJacksonIsNotAvailable() {
        allOptionsRunner().withClassLoader(new FilteredClassLoader(ObjectMapper.class.getPackage().getName())).run(( context) -> {
            assertConverterBeanExists(context, .class, "gsonHttpMessageConverter");
            assertConverterBeanRegisteredWithHttpMessageConverters(context, .class);
            assertThat(context).doesNotHaveBean(.class);
        });
    }

    @Test
    public void jsonbIsPreferredIfJacksonAndGsonAreNotAvailable() {
        allOptionsRunner().withClassLoader(new FilteredClassLoader(ObjectMapper.class.getPackage().getName(), Gson.class.getPackage().getName())).run(assertConverter(JsonbHttpMessageConverter.class, "jsonbHttpMessageConverter"));
    }

    @Test
    public void whenServletWebApplicationHttpMessageConvertersIsConfigured() {
        new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class)).run(( context) -> assertThat(context).hasSingleBean(.class));
    }

    @Test
    public void whenReactiveWebApplicationHttpMessageConvertersIsNotConfigured() {
        new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class)).run(( context) -> assertThat(context).doesNotHaveBean(.class));
    }

    @Configuration
    protected static class JacksonObjectMapperConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Configuration
    protected static class JacksonObjectMapperBuilderConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        public Jackson2ObjectMapperBuilder builder() {
            return new Jackson2ObjectMapperBuilder();
        }
    }

    @Configuration
    protected static class JacksonConverterConfig {
        @Bean
        public MappingJackson2HttpMessageConverter customJacksonMessageConverter(ObjectMapper objectMapper) {
            MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
            converter.setObjectMapper(objectMapper);
            return converter;
        }
    }

    @Configuration
    protected static class GsonConverterConfig {
        @Bean
        public GsonHttpMessageConverter customGsonMessageConverter(Gson gson) {
            GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
            converter.setGson(gson);
            return converter;
        }
    }

    @Configuration
    protected static class JsonbConverterConfig {
        @Bean
        public JsonbHttpMessageConverter customJsonbMessageConverter(Jsonb jsonb) {
            JsonbHttpMessageConverter converter = new JsonbHttpMessageConverter();
            converter.setJsonb(jsonb);
            return converter;
        }
    }

    @Configuration
    protected static class StringConverterConfig {
        @Bean
        public StringHttpMessageConverter customStringMessageConverter() {
            return new StringHttpMessageConverter();
        }
    }

    @Configuration
    protected static class TypeConstrainedConverterConfiguration {
        @Bean
        public TypeConstrainedMappingJackson2HttpMessageConverter typeConstrainedConverter() {
            return new TypeConstrainedMappingJackson2HttpMessageConverter(RepresentationModel.class);
        }
    }
}

