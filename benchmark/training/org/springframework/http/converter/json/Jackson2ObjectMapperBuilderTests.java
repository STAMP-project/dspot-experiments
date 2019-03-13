/**
 * Copyright 2002-2018 the original author or authors.
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
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.std.ClassSerializer;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;
import com.fasterxml.jackson.dataformat.smile.SmileFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import kotlin.ranges.IntRange;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.FatalBeanException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.annotation.PropertyAccessor.GETTER;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.QUOTE_FIELD_NAMES;
import static com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER;
import static com.fasterxml.jackson.core.JsonParser.Feature.AUTO_CLOSE_SOURCE;
import static java.util.Collections.singletonMap;


/**
 * Test class for {@link Jackson2ObjectMapperBuilder}.
 *
 * @author Sebastien Deleuze
 * @author Edd? Mel?ndez
 */
@SuppressWarnings("deprecation")
public class Jackson2ObjectMapperBuilderTests {
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    @Test(expected = FatalBeanException.class)
    public void unknownFeature() {
        Jackson2ObjectMapperBuilder.json().featuresToEnable(Boolean.TRUE).build();
    }

    @Test
    public void defaultProperties() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        Assert.assertNotNull(objectMapper);
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertFalse(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assert.assertTrue(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertTrue(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
        Assert.assertTrue(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS));
        Assert.assertTrue(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_SETTERS));
        Assert.assertFalse(objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        Assert.assertTrue(objectMapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }

    @Test
    public void propertiesShortcut() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().autoDetectFields(false).defaultViewInclusion(true).failOnUnknownProperties(true).failOnEmptyBeans(false).autoDetectGettersSetters(false).indentOutput(true).build();
        Assert.assertNotNull(objectMapper);
        Assert.assertTrue(objectMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertTrue(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_IS_GETTERS));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_SETTERS));
        Assert.assertTrue(objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        Assert.assertFalse(objectMapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }

    @Test
    public void booleanSetters() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().featuresToEnable(MapperFeature.DEFAULT_VIEW_INCLUSION, DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, SerializationFeature.INDENT_OUTPUT).featuresToDisable(MapperFeature.AUTO_DETECT_FIELDS, MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_SETTERS, SerializationFeature.FAIL_ON_EMPTY_BEANS).build();
        Assert.assertNotNull(objectMapper);
        Assert.assertTrue(objectMapper.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertTrue(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
        Assert.assertFalse(objectMapper.isEnabled(MapperFeature.AUTO_DETECT_SETTERS));
        Assert.assertTrue(objectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        Assert.assertFalse(objectMapper.isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
    }

    @Test
    public void setNotNullSerializationInclusion() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        Assert.assertSame(ALWAYS, objectMapper.getSerializationConfig().getSerializationInclusion());
        objectMapper = Jackson2ObjectMapperBuilder.json().serializationInclusion(NON_NULL).build();
        Assert.assertSame(NON_NULL, objectMapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void setNotDefaultSerializationInclusion() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        Assert.assertSame(ALWAYS, objectMapper.getSerializationConfig().getSerializationInclusion());
        objectMapper = Jackson2ObjectMapperBuilder.json().serializationInclusion(NON_DEFAULT).build();
        Assert.assertSame(NON_DEFAULT, objectMapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void setNotEmptySerializationInclusion() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        Assert.assertSame(ALWAYS, objectMapper.getSerializationConfig().getSerializationInclusion());
        objectMapper = Jackson2ObjectMapperBuilder.json().serializationInclusion(NON_EMPTY).build();
        Assert.assertSame(NON_EMPTY, objectMapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void dateTimeFormatSetter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Jackson2ObjectMapperBuilderTests.DATE_FORMAT);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().dateFormat(dateFormat).build();
        Assert.assertEquals(dateFormat, objectMapper.getSerializationConfig().getDateFormat());
        Assert.assertEquals(dateFormat, objectMapper.getDeserializationConfig().getDateFormat());
    }

    @Test
    public void simpleDateFormatStringSetter() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Jackson2ObjectMapperBuilderTests.DATE_FORMAT);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().simpleDateFormat(Jackson2ObjectMapperBuilderTests.DATE_FORMAT).build();
        Assert.assertEquals(dateFormat, objectMapper.getSerializationConfig().getDateFormat());
        Assert.assertEquals(dateFormat, objectMapper.getDeserializationConfig().getDateFormat());
    }

    @Test
    public void localeSetter() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().locale(Locale.FRENCH).build();
        Assert.assertEquals(Locale.FRENCH, objectMapper.getSerializationConfig().getLocale());
        Assert.assertEquals(Locale.FRENCH, objectMapper.getDeserializationConfig().getLocale());
    }

    @Test
    public void timeZoneSetter() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().timeZone(timeZone).build();
        Assert.assertEquals(timeZone, objectMapper.getSerializationConfig().getTimeZone());
        Assert.assertEquals(timeZone, objectMapper.getDeserializationConfig().getTimeZone());
    }

    @Test
    public void timeZoneStringSetter() {
        String zoneId = "Europe/Paris";
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().timeZone(zoneId).build();
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        Assert.assertEquals(timeZone, objectMapper.getSerializationConfig().getTimeZone());
        Assert.assertEquals(timeZone, objectMapper.getDeserializationConfig().getTimeZone());
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongTimeZoneStringSetter() {
        String zoneId = "foo";
        Jackson2ObjectMapperBuilder.json().timeZone(zoneId).build();
    }

    @Test
    public void modules() {
        NumberSerializer serializer1 = new NumberSerializer(Integer.class);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Integer.class, serializer1);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modules(module).build();
        Serializers serializers = Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(objectMapper).serializers().iterator().next();
        Assert.assertSame(serializer1, serializers.findSerializer(null, SimpleType.construct(Integer.class), null));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void modulesToInstallByClass() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modulesToInstall(Jackson2ObjectMapperBuilderTests.CustomIntegerModule.class).build();
        Serializers serializers = Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(objectMapper).serializers().iterator().next();
        Assert.assertSame(Jackson2ObjectMapperBuilderTests.CustomIntegerSerializer.class, serializers.findSerializer(null, SimpleType.construct(Integer.class), null).getClass());
    }

    @Test
    public void modulesToInstallByInstance() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modulesToInstall(new Jackson2ObjectMapperBuilderTests.CustomIntegerModule()).build();
        Serializers serializers = Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(objectMapper).serializers().iterator().next();
        Assert.assertSame(Jackson2ObjectMapperBuilderTests.CustomIntegerSerializer.class, serializers.findSerializer(null, SimpleType.construct(Integer.class), null).getClass());
    }

    @Test
    public void wellKnownModules() throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        Long timestamp = 1322903730000L;
        DateTime dateTime = new DateTime(timestamp, DateTimeZone.UTC);
        Assert.assertEquals(timestamp.toString(), new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
        Path file = Paths.get("foo");
        Assert.assertTrue(new String(objectMapper.writeValueAsBytes(file), "UTF-8").endsWith("foo\""));
        Optional<String> optional = Optional.of("test");
        Assert.assertEquals("\"test\"", new String(objectMapper.writeValueAsBytes(optional), "UTF-8"));
        // Kotlin module
        IntRange range = new IntRange(1, 3);
        Assert.assertEquals("{\"start\":1,\"end\":3}", new String(objectMapper.writeValueAsBytes(range), "UTF-8"));
    }

    // SPR-12634
    @Test
    public void customizeWellKnownModulesWithModule() throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modulesToInstall(new Jackson2ObjectMapperBuilderTests.CustomIntegerModule()).build();
        DateTime dateTime = new DateTime(1322903730000L, DateTimeZone.UTC);
        Assert.assertEquals("1322903730000", new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
        Assert.assertThat(new String(objectMapper.writeValueAsBytes(new Integer(4)), "UTF-8"), Matchers.containsString("customid"));
    }

    // SPR-12634
    @Test
    @SuppressWarnings("unchecked")
    public void customizeWellKnownModulesWithModuleClass() throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modulesToInstall(Jackson2ObjectMapperBuilderTests.CustomIntegerModule.class).build();
        DateTime dateTime = new DateTime(1322903730000L, DateTimeZone.UTC);
        Assert.assertEquals("1322903730000", new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
        Assert.assertThat(new String(objectMapper.writeValueAsBytes(new Integer(4)), "UTF-8"), Matchers.containsString("customid"));
    }

    // SPR-12634
    @Test
    public void customizeWellKnownModulesWithSerializer() throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().serializerByType(Integer.class, new Jackson2ObjectMapperBuilderTests.CustomIntegerSerializer()).build();
        DateTime dateTime = new DateTime(1322903730000L, DateTimeZone.UTC);
        Assert.assertEquals("1322903730000", new String(objectMapper.writeValueAsBytes(dateTime), "UTF-8"));
        Assert.assertThat(new String(objectMapper.writeValueAsBytes(new Integer(4)), "UTF-8"), Matchers.containsString("customid"));
    }

    @Test
    public void propertyNamingStrategy() {
        PropertyNamingStrategy strategy = new PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy();
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().propertyNamingStrategy(strategy).build();
        Assert.assertSame(strategy, objectMapper.getSerializationConfig().getPropertyNamingStrategy());
        Assert.assertSame(strategy, objectMapper.getDeserializationConfig().getPropertyNamingStrategy());
    }

    @Test
    public void serializerByType() {
        JsonSerializer<Number> serializer = new NumberSerializer(Integer.class);
        ObjectMapper objectMapper = // Disable well-known modules detection
        Jackson2ObjectMapperBuilder.json().modules(new java.util.ArrayList()).serializerByType(Boolean.class, serializer).build();
        Assert.assertTrue(Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(objectMapper).hasSerializers());
        Serializers serializers = Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(objectMapper).serializers().iterator().next();
        Assert.assertSame(serializer, serializers.findSerializer(null, SimpleType.construct(Boolean.class), null));
    }

    @Test
    public void deserializerByType() throws JsonMappingException {
        JsonDeserializer<Date> deserializer = new DateDeserializers.DateDeserializer();
        ObjectMapper objectMapper = // Disable well-known modules detection
        Jackson2ObjectMapperBuilder.json().modules(new java.util.ArrayList()).deserializerByType(Date.class, deserializer).build();
        Assert.assertTrue(Jackson2ObjectMapperBuilderTests.getDeserializerFactoryConfig(objectMapper).hasDeserializers());
        Deserializers deserializers = Jackson2ObjectMapperBuilderTests.getDeserializerFactoryConfig(objectMapper).deserializers().iterator().next();
        Assert.assertSame(deserializer, deserializers.findBeanDeserializer(SimpleType.construct(Date.class), null, null));
    }

    @Test
    public void mixIn() {
        Class<?> target = String.class;
        Class<?> mixInSource = Object.class;
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modules().mixIn(target, mixInSource).build();
        Assert.assertEquals(1, objectMapper.mixInCount());
        Assert.assertSame(mixInSource, objectMapper.findMixInClassFor(target));
    }

    @Test
    public void mixIns() {
        Class<?> target = String.class;
        Class<?> mixInSource = Object.class;
        Map<Class<?>, Class<?>> mixIns = new HashMap<>();
        mixIns.put(target, mixInSource);
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().modules().mixIns(mixIns).build();
        Assert.assertEquals(1, objectMapper.mixInCount());
        Assert.assertSame(mixInSource, objectMapper.findMixInClassFor(target));
    }

    @Test
    public void filters() throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().filters(new SimpleFilterProvider().setFailOnUnknownId(false)).build();
        Jackson2ObjectMapperBuilderTests.JacksonFilteredBean bean = new Jackson2ObjectMapperBuilderTests.JacksonFilteredBean("value1", "value2");
        String output = objectMapper.writeValueAsString(bean);
        Assert.assertThat(output, Matchers.containsString("value1"));
        Assert.assertThat(output, Matchers.containsString("value2"));
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false).setDefaultFilter(SimpleBeanPropertyFilter.serializeAllExcept("property2"));
        objectMapper = Jackson2ObjectMapperBuilder.json().filters(provider).build();
        output = objectMapper.writeValueAsString(bean);
        Assert.assertThat(output, Matchers.containsString("value1"));
        Assert.assertThat(output, Matchers.not(Matchers.containsString("value2")));
    }

    @Test
    public void completeSetup() throws JsonMappingException {
        NopAnnotationIntrospector annotationIntrospector = NopAnnotationIntrospector.instance;
        Map<Class<?>, JsonDeserializer<?>> deserializerMap = new HashMap<>();
        JsonDeserializer<Date> deserializer = new DateDeserializers.DateDeserializer();
        deserializerMap.put(Date.class, deserializer);
        JsonSerializer<Class<?>> serializer1 = new ClassSerializer();
        JsonSerializer<Number> serializer2 = new NumberSerializer(Integer.class);
        Jackson2ObjectMapperBuilder builder = // Disable well-known modules detection
        Jackson2ObjectMapperBuilder.json().modules(new java.util.ArrayList()).serializers(serializer1).serializersByType(singletonMap(Boolean.class, serializer2)).deserializersByType(deserializerMap).annotationIntrospector(annotationIntrospector).featuresToEnable(SerializationFeature.FAIL_ON_EMPTY_BEANS, DeserializationFeature.UNWRAP_ROOT_VALUE, ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, WRITE_NUMBERS_AS_STRINGS).featuresToDisable(MapperFeature.AUTO_DETECT_GETTERS, MapperFeature.AUTO_DETECT_FIELDS, AUTO_CLOSE_SOURCE, QUOTE_FIELD_NAMES).serializationInclusion(NON_NULL);
        ObjectMapper mapper = new ObjectMapper();
        builder.configure(mapper);
        Assert.assertTrue(Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(mapper).hasSerializers());
        Assert.assertTrue(Jackson2ObjectMapperBuilderTests.getDeserializerFactoryConfig(mapper).hasDeserializers());
        Serializers serializers = Jackson2ObjectMapperBuilderTests.getSerializerFactoryConfig(mapper).serializers().iterator().next();
        Assert.assertSame(serializer1, serializers.findSerializer(null, SimpleType.construct(Class.class), null));
        Assert.assertSame(serializer2, serializers.findSerializer(null, SimpleType.construct(Boolean.class), null));
        Assert.assertNull(serializers.findSerializer(null, SimpleType.construct(Number.class), null));
        Deserializers deserializers = Jackson2ObjectMapperBuilderTests.getDeserializerFactoryConfig(mapper).deserializers().iterator().next();
        Assert.assertSame(deserializer, deserializers.findBeanDeserializer(SimpleType.construct(Date.class), null, null));
        Assert.assertSame(annotationIntrospector, mapper.getSerializationConfig().getAnnotationIntrospector());
        Assert.assertSame(annotationIntrospector, mapper.getDeserializationConfig().getAnnotationIntrospector());
        Assert.assertTrue(mapper.getSerializationConfig().isEnabled(SerializationFeature.FAIL_ON_EMPTY_BEANS));
        Assert.assertTrue(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.UNWRAP_ROOT_VALUE));
        Assert.assertTrue(mapper.getFactory().isEnabled(ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER));
        Assert.assertTrue(mapper.getFactory().isEnabled(WRITE_NUMBERS_AS_STRINGS));
        Assert.assertFalse(mapper.getSerializationConfig().isEnabled(MapperFeature.AUTO_DETECT_GETTERS));
        Assert.assertFalse(mapper.getDeserializationConfig().isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION));
        Assert.assertFalse(mapper.getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assert.assertFalse(mapper.getDeserializationConfig().isEnabled(MapperFeature.AUTO_DETECT_FIELDS));
        Assert.assertFalse(mapper.getFactory().isEnabled(AUTO_CLOSE_SOURCE));
        Assert.assertFalse(mapper.getFactory().isEnabled(QUOTE_FIELD_NAMES));
        Assert.assertSame(NON_NULL, mapper.getSerializationConfig().getSerializationInclusion());
    }

    @Test
    public void xmlMapper() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.xml().build();
        Assert.assertNotNull(objectMapper);
        Assert.assertEquals(XmlMapper.class, objectMapper.getClass());
    }

    @Test
    public void createXmlMapper() {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json().indentOutput(true);
        ObjectMapper jsonObjectMapper = builder.build();
        ObjectMapper xmlObjectMapper = builder.createXmlMapper(true).build();
        Assert.assertTrue(jsonObjectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        Assert.assertTrue(xmlObjectMapper.isEnabled(SerializationFeature.INDENT_OUTPUT));
        Assert.assertTrue(xmlObjectMapper.getClass().isAssignableFrom(XmlMapper.class));
    }

    // SPR-13975
    @Test
    public void defaultUseWrapper() throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.xml().defaultUseWrapper(false).build();
        Assert.assertNotNull(objectMapper);
        Assert.assertEquals(XmlMapper.class, objectMapper.getClass());
        Jackson2ObjectMapperBuilderTests.ListContainer<String> container = new Jackson2ObjectMapperBuilderTests.ListContainer<>(Arrays.asList("foo", "bar"));
        String output = objectMapper.writeValueAsString(container);
        Assert.assertThat(output, Matchers.containsString("<list>foo</list><list>bar</list></ListContainer>"));
    }

    // SPR-14435
    @Test
    public void smile() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.smile().build();
        Assert.assertNotNull(objectMapper);
        Assert.assertEquals(SmileFactory.class, objectMapper.getFactory().getClass());
    }

    // SPR-14435
    @Test
    public void cbor() {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.cbor().build();
        Assert.assertNotNull(objectMapper);
        Assert.assertEquals(CBORFactory.class, objectMapper.getFactory().getClass());
    }

    // SPR-14435
    @Test
    public void factory() {
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().factory(new SmileFactory()).build();
        Assert.assertNotNull(objectMapper);
        Assert.assertEquals(SmileFactory.class, objectMapper.getFactory().getClass());
    }

    @Test
    public void visibility() throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().visibility(GETTER, NONE).visibility(FIELD, ANY).build();
        String json = objectMapper.writeValueAsString(new Jackson2ObjectMapperBuilderTests.JacksonVisibilityBean());
        Assert.assertThat(json, Matchers.containsString("property1"));
        Assert.assertThat(json, Matchers.containsString("property2"));
        Assert.assertThat(json, Matchers.not(Matchers.containsString("property3")));
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
            serializers.addSerializer(Integer.class, new Jackson2ObjectMapperBuilderTests.CustomIntegerSerializer());
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
        public JacksonFilteredBean() {
        }

        public JacksonFilteredBean(String property1, String property2) {
            this.property1 = property1;
            this.property2 = property2;
        }

        private String property1;

        private String property2;

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

    public static class ListContainer<T> {
        private List<T> list;

        public ListContainer() {
        }

        public ListContainer(List<T> list) {
            this.list = list;
        }

        public List<T> getList() {
            return list;
        }

        public void setList(List<T> list) {
            this.list = list;
        }
    }

    public static class JacksonVisibilityBean {
        private String property1;

        public String property2;

        public String getProperty3() {
            return null;
        }
    }
}

