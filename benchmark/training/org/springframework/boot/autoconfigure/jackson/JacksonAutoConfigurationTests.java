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
package org.springframework.boot.autoconfigure.jackson;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.jackson.JsonObjectSerializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import static com.fasterxml.jackson.annotation.JsonCreator.Mode.DEFAULT;
import static com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING;


/**
 * Tests for {@link JacksonAutoConfiguration}.
 *
 * @author Dave Syer
 * @author Oliver Gierke
 * @author Andy Wilkinson
 * @author Marcel Overdijk
 * @author Sebastien Deleuze
 * @author Johannes Edmeier
 * @author Grzegorz Poznachowski
 */
public class JacksonAutoConfigurationTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class));

    @Test
    public void registersJodaModuleAutomatically() {
        this.contextRunner.run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class);
            assertThat(objectMapper.canSerialize(.class)).isTrue();
        });
    }

    @Test
    public void doubleModuleRegistration() {
        this.contextRunner.withUserConfiguration(JacksonAutoConfigurationTests.DoubleModulesConfig.class).withConfiguration(AutoConfigurations.of(HttpMessageConvertersAutoConfiguration.class)).run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.writeValueAsString(new org.springframework.boot.autoconfigure.jackson.Foo())).isEqualTo("{\"foo\":\"bar\"}");
        });
    }

    @Test
    public void noCustomDateFormat() {
        this.contextRunner.run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.getDateFormat()).isInstanceOf(.class);
        });
    }

    @Test
    public void customDateFormat() {
        this.contextRunner.withPropertyValues("spring.jackson.date-format:yyyyMMddHHmmss").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            DateFormat dateFormat = mapper.getDateFormat();
            assertThat(dateFormat).isInstanceOf(.class);
            assertThat(((SimpleDateFormat) (dateFormat)).toPattern()).isEqualTo("yyyyMMddHHmmss");
        });
    }

    @Test
    public void customJodaDateTimeFormat() throws Exception {
        this.contextRunner.withPropertyValues("spring.jackson.date-format:yyyyMMddHHmmss", "spring.jackson.joda-date-time-format:yyyy-MM-dd HH:mm:ss").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            DateTime dateTime = new DateTime(1988, 6, 25, 20, 30, DateTimeZone.UTC);
            assertThat(mapper.writeValueAsString(dateTime)).isEqualTo("\"1988-06-25 20:30:00\"");
            Date date = dateTime.toDate();
            assertThat(mapper.writeValueAsString(date)).isEqualTo("\"19880625203000\"");
        });
    }

    @Test
    public void customDateFormatClass() {
        this.contextRunner.withPropertyValues("spring.jackson.date-format:org.springframework.boot.autoconfigure.jackson.JacksonAutoConfigurationTests.MyDateFormat").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.getDateFormat()).isInstanceOf(.class);
        });
    }

    @Test
    public void noCustomPropertyNamingStrategy() {
        this.contextRunner.run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.getPropertyNamingStrategy()).isNull();
        });
    }

    @Test
    public void customPropertyNamingStrategyField() {
        this.contextRunner.withPropertyValues("spring.jackson.property-naming-strategy:SNAKE_CASE").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.getPropertyNamingStrategy()).isInstanceOf(.class);
        });
    }

    @Test
    public void customPropertyNamingStrategyClass() {
        this.contextRunner.withPropertyValues("spring.jackson.property-naming-strategy:com.fasterxml.jackson.databind.PropertyNamingStrategy.SnakeCaseStrategy").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.getPropertyNamingStrategy()).isInstanceOf(.class);
        });
    }

    @Test
    public void enableSerializationFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.serialization.indent_output:true").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(SerializationFeature.INDENT_OUTPUT.enabledByDefault()).isFalse();
            assertThat(mapper.getSerializationConfig().hasSerializationFeatures(SerializationFeature.INDENT_OUTPUT.getMask())).isTrue();
        });
    }

    @Test
    public void disableSerializationFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.serialization.write_dates_as_timestamps:false").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS.enabledByDefault()).isTrue();
            assertThat(mapper.getSerializationConfig().hasSerializationFeatures(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS.getMask())).isFalse();
        });
    }

    @Test
    public void enableDeserializationFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.deserialization.use_big_decimal_for_floats:true").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS.enabledByDefault()).isFalse();
            assertThat(mapper.getDeserializationConfig().hasDeserializationFeatures(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS.getMask())).isTrue();
        });
    }

    @Test
    public void disableDeserializationFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.deserialization.fail-on-unknown-properties:false").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.enabledByDefault()).isTrue();
            assertThat(mapper.getDeserializationConfig().hasDeserializationFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.getMask())).isFalse();
        });
    }

    @Test
    public void enableMapperFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.mapper.require_setters_for_getters:true").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS.enabledByDefault()).isFalse();
            assertThat(mapper.getSerializationConfig().hasMapperFeatures(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS.getMask())).isTrue();
            assertThat(mapper.getDeserializationConfig().hasMapperFeatures(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS.getMask())).isTrue();
        });
    }

    @Test
    public void disableMapperFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.mapper.use_annotations:false").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(MapperFeature.USE_ANNOTATIONS.enabledByDefault()).isTrue();
            assertThat(mapper.getDeserializationConfig().hasMapperFeatures(MapperFeature.USE_ANNOTATIONS.getMask())).isFalse();
            assertThat(mapper.getSerializationConfig().hasMapperFeatures(MapperFeature.USE_ANNOTATIONS.getMask())).isFalse();
        });
    }

    @Test
    public void enableParserFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.parser.allow_single_quotes:true").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(JsonParser.Feature.ALLOW_SINGLE_QUOTES.enabledByDefault()).isFalse();
            assertThat(mapper.getFactory().isEnabled(JsonParser.Feature.ALLOW_SINGLE_QUOTES)).isTrue();
        });
    }

    @Test
    public void disableParserFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.parser.auto_close_source:false").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(JsonParser.Feature.AUTO_CLOSE_SOURCE.enabledByDefault()).isTrue();
            assertThat(mapper.getFactory().isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)).isFalse();
        });
    }

    @Test
    public void enableGeneratorFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.generator.write_numbers_as_strings:true").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS.enabledByDefault()).isFalse();
            assertThat(mapper.getFactory().isEnabled(JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS)).isTrue();
        });
    }

    @Test
    public void disableGeneratorFeature() {
        this.contextRunner.withPropertyValues("spring.jackson.generator.auto_close_target:false").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(JsonGenerator.Feature.AUTO_CLOSE_TARGET.enabledByDefault()).isTrue();
            assertThat(mapper.getFactory().isEnabled(JsonGenerator.Feature.AUTO_CLOSE_TARGET)).isFalse();
        });
    }

    @Test
    public void defaultObjectMapperBuilder() {
        this.contextRunner.run(( context) -> {
            Jackson2ObjectMapperBuilder builder = context.getBean(.class);
            ObjectMapper mapper = builder.build();
            assertThat(MapperFeature.DEFAULT_VIEW_INCLUSION.enabledByDefault()).isTrue();
            assertThat(mapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)).isFalse();
            assertThat(MapperFeature.DEFAULT_VIEW_INCLUSION.enabledByDefault()).isTrue();
            assertThat(mapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)).isFalse();
            assertThat(mapper.getSerializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION)).isFalse();
            assertThat(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.enabledByDefault()).isTrue();
            assertThat(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isFalse();
        });
    }

    @Test
    public void moduleBeansAndWellKnownModulesAreRegisteredWithTheObjectMapperBuilder() {
        this.contextRunner.withUserConfiguration(JacksonAutoConfigurationTests.ModuleConfig.class).run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class).build();
            assertThat(context.getBean(.class).getOwners()).contains(((ObjectCodec) (objectMapper)));
            assertThat(objectMapper.canSerialize(.class)).isTrue();
            assertThat(objectMapper.canSerialize(.class)).isTrue();
        });
    }

    @Test
    public void defaultSerializationInclusion() {
        this.contextRunner.run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class).build();
            assertThat(objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion()).isEqualTo(JsonInclude.Include.USE_DEFAULTS);
        });
    }

    @Test
    public void customSerializationInclusion() {
        this.contextRunner.withPropertyValues("spring.jackson.default-property-inclusion:non_null").run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class).build();
            assertThat(objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion()).isEqualTo(JsonInclude.Include.NON_NULL);
        });
    }

    @Test
    public void customTimeZoneFormattingADateTime() {
        this.contextRunner.withPropertyValues("spring.jackson.time-zone:America/Los_Angeles", "spring.jackson.date-format:zzzz", "spring.jackson.locale:en").run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class).build();
            DateTime dateTime = new DateTime(1436966242231L, DateTimeZone.UTC);
            assertThat(objectMapper.writeValueAsString(dateTime)).isEqualTo("\"Pacific Daylight Time\"");
        });
    }

    @Test
    public void customTimeZoneFormattingADate() throws JsonProcessingException {
        this.contextRunner.withPropertyValues("spring.jackson.time-zone:GMT+10", "spring.jackson.date-format:z").run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class).build();
            Date date = new Date(1436966242231L);
            assertThat(objectMapper.writeValueAsString(date)).isEqualTo("\"GMT+10:00\"");
        });
    }

    @Test
    public void customLocaleWithJodaTime() throws JsonProcessingException {
        this.contextRunner.withPropertyValues("spring.jackson.locale:de_DE", "spring.jackson.date-format:zzzz", "spring.jackson.serialization.write-dates-with-zone-id:true").run(( context) -> {
            ObjectMapper objectMapper = context.getBean(.class);
            DateTime jodaTime = new DateTime(1478424650000L, DateTimeZone.forID("Europe/Rome"));
            assertThat(objectMapper.writeValueAsString(jodaTime)).startsWith("\"Mitteleurop\u00e4ische ");
        });
    }

    @Test
    public void additionalJacksonBuilderCustomization() {
        this.contextRunner.withUserConfiguration(JacksonAutoConfigurationTests.ObjectMapperBuilderCustomConfig.class).run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            assertThat(mapper.getDateFormat()).isInstanceOf(.class);
        });
    }

    @Test
    public void parameterNamesModuleIsAutoConfigured() {
        assertParameterNamesModuleCreatorBinding(DEFAULT, JacksonAutoConfiguration.class);
    }

    @Test
    public void customParameterNamesModuleCanBeConfigured() {
        assertParameterNamesModuleCreatorBinding(DELEGATING, JacksonAutoConfigurationTests.ParameterNamesModuleConfig.class, JacksonAutoConfiguration.class);
    }

    @Test
    public void writeDatesAsTimestampsDefault() {
        this.contextRunner.run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            DateTime dateTime = new DateTime(1988, 6, 25, 20, 30, DateTimeZone.UTC);
            String expected = FormatConfig.DEFAULT_DATETIME_PRINTER.rawFormatter().withZone(DateTimeZone.UTC).print(dateTime);
            assertThat(mapper.writeValueAsString(dateTime)).isEqualTo((("\"" + expected) + "\""));
        });
    }

    @Test
    public void writeWithVisibility() {
        this.contextRunner.withPropertyValues("spring.jackson.visibility.getter:none", "spring.jackson.visibility.field:any").run(( context) -> {
            ObjectMapper mapper = context.getBean(.class);
            String json = mapper.writeValueAsString(new org.springframework.boot.autoconfigure.jackson.VisibilityBean());
            assertThat(json).contains("property1");
            assertThat(json).contains("property2");
            assertThat(json).doesNotContain("property3");
        });
    }

    public static class MyDateFormat extends SimpleDateFormat {
        public MyDateFormat() {
            super("yyyy-MM-dd HH:mm:ss");
        }
    }

    @Configuration
    protected static class MockObjectMapperConfig {
        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            return Mockito.mock(ObjectMapper.class);
        }
    }

    @Configuration
    @Import(JacksonAutoConfigurationTests.BazSerializer.class)
    protected static class ModuleConfig {
        @Bean
        public JacksonAutoConfigurationTests.CustomModule jacksonModule() {
            return new JacksonAutoConfigurationTests.CustomModule();
        }
    }

    @Configuration
    protected static class DoubleModulesConfig {
        @Bean
        public Module jacksonModule() {
            SimpleModule module = new SimpleModule();
            module.addSerializer(JacksonAutoConfigurationTests.Foo.class, new JsonSerializer<JacksonAutoConfigurationTests.Foo>() {
                @Override
                public void serialize(JacksonAutoConfigurationTests.Foo value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                    jgen.writeStartObject();
                    jgen.writeStringField("foo", "bar");
                    jgen.writeEndObject();
                }
            });
            return module;
        }

        @Bean
        @Primary
        public ObjectMapper objectMapper() {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(jacksonModule());
            return mapper;
        }
    }

    @Configuration
    protected static class ParameterNamesModuleConfig {
        @Bean
        public ParameterNamesModule parameterNamesModule() {
            return new ParameterNamesModule(DELEGATING);
        }
    }

    @Configuration
    protected static class ObjectMapperBuilderCustomConfig {
        @Bean
        public Jackson2ObjectMapperBuilderCustomizer customDateFormat() {
            return ( builder) -> builder.dateFormat(new org.springframework.boot.autoconfigure.jackson.MyDateFormat());
        }
    }

    protected static final class Foo {
        private String name;

        private Foo() {
        }

        static JacksonAutoConfigurationTests.Foo create() {
            return new JacksonAutoConfigurationTests.Foo();
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    protected static class Bar {
        private String propertyName;

        public String getPropertyName() {
            return this.propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }
    }

    @JsonComponent
    private static class BazSerializer extends JsonObjectSerializer<JacksonAutoConfigurationTests.Baz> {
        @Override
        protected void serializeObject(JacksonAutoConfigurationTests.Baz value, JsonGenerator jgen, SerializerProvider provider) {
        }
    }

    private static class Baz {}

    private static class CustomModule extends SimpleModule {
        private Set<ObjectCodec> owners = new HashSet<>();

        @Override
        public void setupModule(Module.SetupContext context) {
            this.owners.add(context.getOwner());
        }

        Set<ObjectCodec> getOwners() {
            return this.owners;
        }
    }

    @SuppressWarnings("unused")
    private static class VisibilityBean {
        private String property1;

        public String property2;

        public String getProperty3() {
            return null;
        }
    }
}

