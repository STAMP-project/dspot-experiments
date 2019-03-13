/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.http.converter.json;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.ClassSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_FIELD_NAMES;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;
import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;


/**
 * Test cases for {@link Jackson2ObjectMapperFactoryBean}.
 *
 * @author Dmitry Katsubo
 * @author Brian Clozel
 * @author Sebastien Deleuze
 * @author Sam Brannen
 */
@SuppressWarnings("deprecation")
public class Jackson2ObjectMapperFactoryBeanTests {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat(Jackson2ObjectMapperFactoryBeanTests.DATE_FORMAT);

    private final Jackson2ObjectMapperFactoryBean factory = new Jackson2ObjectMapperFactoryBean();

    @Test(expected = FatalBeanException.class)
    public void unknownFeature() {
        this.factory.setFeaturesToEnable(Boolean.TRUE);
        this.factory.afterPropertiesSet();
    }

    @Test
    public void booleanSetters() {
        this.factory.setAutoDetectFields(false);
        this.factory.setAutoDetectGettersSetters(false);
        this.factory.setDefaultViewInclusion(false);
        this.factory.setFailOnEmptyBeans(false);
        this.factory.setIndentOutput(true);
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        Assert.assertFalse(objectMapper.getSerializationConfig().isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertFalse(objectMapper.getSerializationConfig().isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.AUTO_DETECT_SETTERS));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertFalse(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        Assert.assertTrue(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.INDENT_OUTPUT));
        Assert.assertSame(ALWAYS, objectMapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void defaultSerializationInclusion() {
        this.factory.afterPropertiesSet();
        Assert.assertSame(ALWAYS, this.factory.getObject().getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void nonNullSerializationInclusion() {
        this.factory.setSerializationInclusion(NON_NULL);
        this.factory.afterPropertiesSet();
        Assert.assertSame(NON_NULL, this.factory.getObject().getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void nonDefaultSerializationInclusion() {
        this.factory.setSerializationInclusion(NON_DEFAULT);
        this.factory.afterPropertiesSet();
        Assert.assertSame(NON_DEFAULT, this.factory.getObject().getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void nonEmptySerializationInclusion() {
        this.factory.setSerializationInclusion(NON_EMPTY);
        this.factory.afterPropertiesSet();
        Assert.assertSame(NON_EMPTY, this.factory.getObject().getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void setDateFormat() {
        this.factory.setDateFormat(this.dateFormat);
        this.factory.afterPropertiesSet();
        Assert.assertEquals(this.dateFormat, this.factory.getObject().getSerializationConfig().getDateFormat());
        Assert.assertEquals(this.dateFormat, this.factory.getObject().getDeserializationConfig().getDateFormat());
    }

    @Test
    public void setSimpleDateFormat() {
        this.factory.setSimpleDateFormat(Jackson2ObjectMapperFactoryBeanTests.DATE_FORMAT);
        this.factory.afterPropertiesSet();
        Assert.assertEquals(this.dateFormat, this.factory.getObject().getSerializationConfig().getDateFormat());
        Assert.assertEquals(this.dateFormat, this.factory.getObject().getDeserializationConfig().getDateFormat());
    }

    @Test
    public void setLocale() {
        this.factory.setLocale(Locale.FRENCH);
        this.factory.afterPropertiesSet();
        Assert.assertEquals(Locale.FRENCH, this.factory.getObject().getSerializationConfig().getLocale());
        Assert.assertEquals(Locale.FRENCH, this.factory.getObject().getDeserializationConfig().getLocale());
    }

    @Test
    public void setTimeZone() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
        this.factory.setTimeZone(timeZone);
        this.factory.afterPropertiesSet();
        Assert.assertEquals(timeZone, this.factory.getObject().getSerializationConfig().getTimeZone());
        Assert.assertEquals(timeZone, this.factory.getObject().getDeserializationConfig().getTimeZone());
    }

    @Test
    public void setTimeZoneWithInvalidZoneId() {
        this.factory.setTimeZone(TimeZone.getTimeZone("bogusZoneId"));
        this.factory.afterPropertiesSet();
        TimeZone timeZone = TimeZone.getTimeZone("GMT");
        Assert.assertEquals(timeZone, this.factory.getObject().getSerializationConfig().getTimeZone());
        Assert.assertEquals(timeZone, this.factory.getObject().getDeserializationConfig().getTimeZone());
    }

    @Test
    public void setModules() {
        NumberSerializer serializer = new NumberSerializer(Integer.class);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Integer.class, serializer);
        this.factory.setModules(Arrays.asList(new Module[]{ module }));
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        Serializers serializers = Jackson2ObjectMapperFactoryBeanTests.getSerializerFactoryConfig(objectMapper).serializers().iterator().next();
        Assert.assertSame(serializer, serializers.findSerializer(null, SimpleType.construct(Integer.class), null));
    }

    @Test
    public void defaultModules() throws JsonProcessingException, UnsupportedEncodingException {
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        Long timestamp = 1322903730000L;
        DateTime dateTime = new DateTime(timestamp, DateTimeZone.UTC);
        Assert.assertEquals(timestamp.toString(), new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
    }

    // SPR-12634
    @Test
    @SuppressWarnings("unchecked")
    public void customizeDefaultModulesWithModuleClass() throws JsonProcessingException, UnsupportedEncodingException {
        this.factory.setModulesToInstall(Jackson2ObjectMapperFactoryBeanTests.CustomIntegerModule.class);
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        DateTime dateTime = new DateTime(1322903730000L, DateTimeZone.UTC);
        Assert.assertEquals("1322903730000", new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
        Assert.assertThat(new String(objectMapper.writeValueAsBytes(new Integer(4)), "UTF-8"), containsString("customid"));
    }

    // SPR-12634
    @Test
    public void customizeDefaultModulesWithSerializer() throws JsonProcessingException, UnsupportedEncodingException {
        Map<Class<?>, JsonSerializer<?>> serializers = new HashMap<>();
        serializers.put(Integer.class, new Jackson2ObjectMapperFactoryBeanTests.CustomIntegerSerializer());
        this.factory.setSerializersByType(serializers);
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        DateTime dateTime = new DateTime(1322903730000L, DateTimeZone.UTC);
        Assert.assertEquals("1322903730000", new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
        Assert.assertThat(new String(objectMapper.writeValueAsBytes(new Integer(4)), "UTF-8"), containsString("customid"));
    }

    @Test
    public void simpleSetup() {
        this.factory.afterPropertiesSet();
        Assert.assertNotNull(this.factory.getObject());
        Assert.assertTrue(this.factory.isSingleton());
        Assert.assertEquals(ObjectMapper.class, this.factory.getObjectType());
    }

    @Test
    public void undefinedObjectType() {
        Assert.assertNull(this.factory.getObjectType());
    }

    @Test
    public void propertyNamingStrategy() {
        PropertyNamingStrategy strategy = new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy();
        this.factory.setPropertyNamingStrategy(strategy);
        this.factory.afterPropertiesSet();
        Assert.assertSame(strategy, this.factory.getObject().getSerializationConfig().getPropertyNamingStrategy());
        Assert.assertSame(strategy, this.factory.getObject().getDeserializationConfig().getPropertyNamingStrategy());
    }

    @Test
    public void setMixIns() {
        Class<?> target = String.class;
        Class<?> mixinSource = Object.class;
        Map<Class<?>, Class<?>> mixIns = new HashMap<>();
        mixIns.put(target, mixinSource);
        this.factory.setModules(Collections.emptyList());
        this.factory.setMixIns(mixIns);
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        Assert.assertEquals(1, objectMapper.mixInCount());
        Assert.assertSame(mixinSource, objectMapper.findMixInClassFor(target));
    }

    @Test
    public void setFilters() throws JsonProcessingException {
        this.factory.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        this.factory.afterPropertiesSet();
        ObjectMapper objectMapper = this.factory.getObject();
        Jackson2ObjectMapperFactoryBeanTests.JacksonFilteredBean bean = new Jackson2ObjectMapperFactoryBeanTests.JacksonFilteredBean("value1", "value2");
        String output = objectMapper.writeValueAsString(bean);
        Assert.assertThat(output, containsString("value1"));
        Assert.assertThat(output, containsString("value2"));
    }

    @Test
    public void completeSetup() {
        NopAnnotationIntrospector annotationIntrospector = NopAnnotationIntrospector.instance;
        ObjectMapper objectMapper = new ObjectMapper();
        this.factory.setObjectMapper(objectMapper);
        Assert.assertTrue(this.factory.isSingleton());
        Assert.assertEquals(ObjectMapper.class, this.factory.getObjectType());
        Map<Class<?>, JsonDeserializer<?>> deserializers = new HashMap<>();
        deserializers.put(Date.class, new DateDeserializers.DateDeserializer());
        JsonSerializer<Class<?>> serializer1 = new ClassSerializer();
        JsonSerializer<Number> serializer2 = new NumberSerializer(Integer.class);
        // Disable well-known modules detection
        this.factory.setModules(new ArrayList());
        this.factory.setSerializers(serializer1);
        this.factory.setSerializersByType(Collections.singletonMap(Boolean.class, serializer2));
        this.factory.setDeserializersByType(deserializers);
        this.factory.setAnnotationIntrospector(annotationIntrospector);
        this.factory.setFeaturesToEnable(SerializationFeature.FAIL_ON_EMPTY_BEANS, DeserializationFeature.UNWRAP_ROOT_VALUE, ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, WRITE_NUMBERS_AS_STRINGS);
        this.factory.setFeaturesToDisable(MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_FIELDS, AUTO_CLOSE_SOURCE, QUOTE_FIELD_NAMES);
        Assert.assertFalse(Jackson2ObjectMapperFactoryBeanTests.getSerializerFactoryConfig(objectMapper).hasSerializers());
        Assert.assertFalse(Jackson2ObjectMapperFactoryBeanTests.getDeserializerFactoryConfig(objectMapper).hasDeserializers());
        this.factory.setSerializationInclusion(NON_NULL);
        this.factory.afterPropertiesSet();
        Assert.assertSame(objectMapper, this.factory.getObject());
        Assert.assertTrue(Jackson2ObjectMapperFactoryBeanTests.getSerializerFactoryConfig(objectMapper).hasSerializers());
        Assert.assertTrue(Jackson2ObjectMapperFactoryBeanTests.getDeserializerFactoryConfig(objectMapper).hasDeserializers());
        Serializers serializers = Jackson2ObjectMapperFactoryBeanTests.getSerializerFactoryConfig(objectMapper).serializers().iterator().next();
        Assert.assertSame(serializer1, serializers.findSerializer(null, SimpleType.construct(Class.class), null));
        Assert.assertSame(serializer2, serializers.findSerializer(null, SimpleType.construct(Boolean.class), null));
        Assert.assertNull(serializers.findSerializer(null, SimpleType.construct(Number.class), null));
        Assert.assertSame(annotationIntrospector, objectMapper.getSerializationConfig().getAnnotationIntrospector());
        Assert.assertSame(annotationIntrospector, objectMapper.getDeserializationConfig().getAnnotationIntrospector());
        Assert.assertTrue(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        Assert.assertTrue(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE));
        Assert.assertTrue(objectMapper.getFactory().isEnabled(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER));
        Assert.assertTrue(objectMapper.getFactory().isEnabled(WRITE_NUMBERS_AS_STRINGS));
        Assert.assertFalse(objectMapper.getSerializationConfig().isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assert.assertFalse(objectMapper.getDeserializationConfig().isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertFalse(objectMapper.getFactory().isEnabled(AUTO_CLOSE_SOURCE));
        Assert.assertFalse(objectMapper.getFactory().isEnabled(QUOTE_FIELD_NAMES));
        Assert.assertSame(NON_NULL, objectMapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void setObjectMapper() {
        this.factory.setObjectMapper(new XmlMapper());
        this.factory.afterPropertiesSet();
        Assert.assertNotNull(this.factory.getObject());
        Assert.assertTrue(this.factory.isSingleton());
        Assert.assertEquals(XmlMapper.class, this.factory.getObjectType());
    }

    @Test
    public void setCreateXmlMapper() {
        this.factory.setCreateXmlMapper(true);
        this.factory.afterPropertiesSet();
        Assert.assertNotNull(this.factory.getObject());
        Assert.assertTrue(this.factory.isSingleton());
        Assert.assertEquals(XmlMapper.class, this.factory.getObjectType());
    }

    // SPR-14435
    @Test
    public void setFactory() {
        this.factory.setFactory(new SmileFactory());
        this.factory.afterPropertiesSet();
        Assert.assertNotNull(this.factory.getObject());
        Assert.assertTrue(this.factory.isSingleton());
        Assert.assertEquals(SmileFactory.class, this.factory.getObject().getFactory().getClass());
    }

    public static class CustomIntegerModule extends Module {
        @Override
        public String getModuleName() {
            return this.getClass().getSimpleName();
        }

        @Override
        public Version version() {
            return Version.unknownVersion();
        }

        @Override
        public void setupModule(Module.SetupContext context) {
            SimpleSerializers serializers = new SimpleSerializers();
            serializers.addSerializer(Integer.class, new Jackson2ObjectMapperFactoryBeanTests.CustomIntegerSerializer());
            context.addSerializers(serializers);
        }
    }

    public static class CustomIntegerSerializer extends JsonSerializer<Integer> {
        @Override
        public void serialize(Integer value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("customid", value);
            gen.writeEndObject();
        }
    }

    @JsonFilter("myJacksonFilter")
    public static class JacksonFilteredBean {
        private String property1;

        private String property2;

        public JacksonFilteredBean(String property1, String property2) {
            this.property1 = property1;
            this.property2 = property2;
        }

        public String getProperty1() {
            return property1;
        }

        public void setProperty1(String property1) {
            this.property1 = property1;
        }

        public String getProperty2() {
            return property2;
        }

        public void setProperty2(String property2) {
            this.property2 = property2;
        }
    }
}

