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
package org.springframework.boot.autoconfigure.http.codec;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.codec.CodecCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.support.DefaultClientCodecConfigurer;
import org.springframework.util.ReflectionUtils;


/**
 * Tests for {@link CodecsAutoConfiguration}.
 *
 * @author Madhura Bhave
 * @author Andy Wilkinson
 */
public class CodecsAutoConfigurationTests {
    private WebApplicationContextRunner contextRunner = new WebApplicationContextRunner().withConfiguration(AutoConfigurations.of(CodecsAutoConfiguration.class));

    @Test
    public void autoConfigShouldProvideALoggingRequestDetailsCustomizer() {
        this.contextRunner.run(( context) -> {
            CodecCustomizer customizer = context.getBean(.class);
            CodecConfigurer configurer = new DefaultClientCodecConfigurer();
            customizer.customize(configurer);
            assertThat(configurer.defaultCodecs()).hasFieldOrPropertyWithValue("enableLoggingRequestDetails", false);
        });
    }

    @Test
    public void loggingRequestDetailsCustomizerShouldUseHttpProperties() {
        this.contextRunner.withPropertyValues("spring.http.log-request-details=true").run(( context) -> {
            CodecCustomizer customizer = context.getBean(.class);
            CodecConfigurer configurer = new DefaultClientCodecConfigurer();
            customizer.customize(configurer);
            assertThat(configurer.defaultCodecs()).hasFieldOrPropertyWithValue("enableLoggingRequestDetails", true);
        });
    }

    @Test
    public void loggingRequestDetailsBeanShouldHaveOrderZero() {
        this.contextRunner.run(( context) -> {
            Method customizerMethod = ReflectionUtils.findMethod(.class, "loggingCodecCustomizer", .class);
            Integer order = new org.springframework.boot.autoconfigure.http.codec.TestAnnotationAwareOrderComparator().findOrder(customizerMethod);
            assertThat(order).isEqualTo(0);
        });
    }

    @Test
    public void jacksonCodecCustomizerBacksOffWhenThereIsNoObjectMapper() {
        this.contextRunner.run(( context) -> assertThat(context).doesNotHaveBean("jacksonCodecCustomizer"));
    }

    @Test
    public void jacksonCodecCustomizerIsAutoConfiguredWhenObjectMapperIsPresent() {
        this.contextRunner.withUserConfiguration(CodecsAutoConfigurationTests.ObjectMapperConfiguration.class).run(( context) -> assertThat(context).hasBean("jacksonCodecCustomizer"));
    }

    @Test
    public void userProvidedCustomizerCanOverrideJacksonCodecCustomizer() {
        this.contextRunner.withUserConfiguration(CodecsAutoConfigurationTests.ObjectMapperConfiguration.class, CodecsAutoConfigurationTests.CodecCustomizerConfiguration.class).run(( context) -> {
            List<CodecCustomizer> codecCustomizers = context.getBean(.class).codecCustomizers;
            assertThat(codecCustomizers).hasSize(3);
            assertThat(codecCustomizers.get(2)).isInstanceOf(.class);
        });
    }

    static class TestAnnotationAwareOrderComparator extends AnnotationAwareOrderComparator {
        @Override
        public Integer findOrder(Object obj) {
            return super.findOrder(obj);
        }
    }

    @Configuration
    static class ObjectMapperConfiguration {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Configuration
    static class CodecCustomizerConfiguration {
        @Bean
        CodecCustomizer codecCustomizer() {
            return new CodecsAutoConfigurationTests.TestCodecCustomizer();
        }

        @Bean
        CodecsAutoConfigurationTests.CodecCustomizers codecCustomizers(java.util.List<CodecCustomizer> customizers) {
            return new CodecsAutoConfigurationTests.CodecCustomizers(customizers);
        }
    }

    private static final class TestCodecCustomizer implements CodecCustomizer {
        @Override
        public void customize(CodecConfigurer configurer) {
        }
    }

    private static final class CodecCustomizers {
        private final java.util.List<CodecCustomizer> codecCustomizers;

        private CodecCustomizers(java.util.List<CodecCustomizer> codecCustomizers) {
            this.codecCustomizers = codecCustomizers;
        }
    }
}

