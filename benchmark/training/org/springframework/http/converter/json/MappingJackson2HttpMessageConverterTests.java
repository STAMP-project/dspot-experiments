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


import MediaType.TEXT_EVENT_STREAM;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;


/**
 * Jackson 2.x converter tests.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 */
public class MappingJackson2HttpMessageConverterTests {
    protected static final String NEWLINE_SYSTEM_PROPERTY = System.getProperty("line.separator");

    private final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

    @Test
    public void canRead() {
        Assert.assertTrue(converter.canRead(MappingJackson2HttpMessageConverterTests.MyBean.class, new MediaType("application", "json")));
        Assert.assertTrue(converter.canRead(Map.class, new MediaType("application", "json")));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue(converter.canWrite(MappingJackson2HttpMessageConverterTests.MyBean.class, new MediaType("application", "json")));
        Assert.assertTrue(converter.canWrite(Map.class, new MediaType("application", "json")));
    }

    // SPR-7905
    @Test
    public void canReadAndWriteMicroformats() {
        Assert.assertTrue(converter.canRead(MappingJackson2HttpMessageConverterTests.MyBean.class, new MediaType("application", "vnd.test-micro-type+json")));
        Assert.assertTrue(converter.canWrite(MappingJackson2HttpMessageConverterTests.MyBean.class, new MediaType("application", "vnd.test-micro-type+json")));
    }

    @Test
    public void readTyped() throws IOException {
        String body = "{" + ((((("\"bytes\":\"AQI=\"," + "\"array\":[\"Foo\",\"Bar\"],") + "\"number\":42,") + "\"string\":\"Foo\",") + "\"bool\":true,") + "\"fraction\":42.0}");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        MappingJackson2HttpMessageConverterTests.MyBean result = ((MappingJackson2HttpMessageConverterTests.MyBean) (converter.read(MappingJackson2HttpMessageConverterTests.MyBean.class, inputMessage)));
        Assert.assertEquals("Foo", result.getString());
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(42.0F, result.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, result.getArray());
        Assert.assertTrue(result.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, result.getBytes());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readUntyped() throws IOException {
        String body = "{" + ((((("\"bytes\":\"AQI=\"," + "\"array\":[\"Foo\",\"Bar\"],") + "\"number\":42,") + "\"string\":\"Foo\",") + "\"bool\":true,") + "\"fraction\":42.0}");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        HashMap<String, Object> result = ((HashMap<String, Object>) (converter.read(HashMap.class, inputMessage)));
        Assert.assertEquals("Foo", result.get("string"));
        Assert.assertEquals(42, result.get("number"));
        Assert.assertEquals(42.0, ((Double) (result.get("fraction"))), 0.0);
        List<String> array = new ArrayList<>();
        array.add("Foo");
        array.add("Bar");
        Assert.assertEquals(array, result.get("array"));
        Assert.assertEquals(Boolean.TRUE, result.get("bool"));
        Assert.assertEquals("AQI=", result.get("bytes"));
    }

    @Test
    public void write() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.MyBean body = new MappingJackson2HttpMessageConverterTests.MyBean();
        body.setString("Foo");
        body.setNumber(42);
        body.setFraction(42.0F);
        body.setArray(new String[]{ "Foo", "Bar" });
        body.setBool(true);
        body.setBytes(new byte[]{ 1, 2 });
        converter.write(body, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertTrue(result.contains("\"string\":\"Foo\""));
        Assert.assertTrue(result.contains("\"number\":42"));
        Assert.assertTrue(result.contains("fraction\":42.0"));
        Assert.assertTrue(result.contains("\"array\":[\"Foo\",\"Bar\"]"));
        Assert.assertTrue(result.contains("\"bool\":true"));
        Assert.assertTrue(result.contains("\"bytes\":\"AQI=\""));
        Assert.assertEquals("Invalid content-type", new MediaType("application", "json", StandardCharsets.UTF_8), outputMessage.getHeaders().getContentType());
    }

    @Test
    public void writeWithBaseType() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.MyBean body = new MappingJackson2HttpMessageConverterTests.MyBean();
        body.setString("Foo");
        body.setNumber(42);
        body.setFraction(42.0F);
        body.setArray(new String[]{ "Foo", "Bar" });
        body.setBool(true);
        body.setBytes(new byte[]{ 1, 2 });
        converter.write(body, MappingJackson2HttpMessageConverterTests.MyBase.class, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertTrue(result.contains("\"string\":\"Foo\""));
        Assert.assertTrue(result.contains("\"number\":42"));
        Assert.assertTrue(result.contains("fraction\":42.0"));
        Assert.assertTrue(result.contains("\"array\":[\"Foo\",\"Bar\"]"));
        Assert.assertTrue(result.contains("\"bool\":true"));
        Assert.assertTrue(result.contains("\"bytes\":\"AQI=\""));
        Assert.assertEquals("Invalid content-type", new MediaType("application", "json", StandardCharsets.UTF_8), outputMessage.getHeaders().getContentType());
    }

    @Test
    public void writeUTF16() throws IOException {
        MediaType contentType = new MediaType("application", "json", StandardCharsets.UTF_16BE);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        String body = "H\u00e9llo W\u00f6rld";
        converter.write(body, contentType, outputMessage);
        Assert.assertEquals("Invalid result", (("\"" + body) + "\""), outputMessage.getBodyAsString(StandardCharsets.UTF_16BE));
        Assert.assertEquals("Invalid content-type", contentType, outputMessage.getHeaders().getContentType());
    }

    @Test(expected = HttpMessageNotReadableException.class)
    public void readInvalidJson() throws IOException {
        String body = "FooBar";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        converter.read(MappingJackson2HttpMessageConverterTests.MyBean.class, inputMessage);
    }

    @Test
    public void readValidJsonWithUnknownProperty() throws IOException {
        String body = "{\"string\":\"string\",\"unknownProperty\":\"value\"}";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        converter.read(MappingJackson2HttpMessageConverterTests.MyBean.class, inputMessage);
        // Assert no HttpMessageNotReadableException is thrown
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readAndWriteGenerics() throws Exception {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter() {
            @Override
            protected JavaType getJavaType(Type type, @Nullable
            Class<?> contextClass) {
                if ((type instanceof Class) && (List.class.isAssignableFrom(((Class<?>) (type))))) {
                    return new ObjectMapper().getTypeFactory().constructCollectionType(ArrayList.class, MappingJackson2HttpMessageConverterTests.MyBean.class);
                } else {
                    return super.getJavaType(type, contextClass);
                }
            }
        };
        String body = "[{" + ((((("\"bytes\":\"AQI=\"," + "\"array\":[\"Foo\",\"Bar\"],") + "\"number\":42,") + "\"string\":\"Foo\",") + "\"bool\":true,") + "\"fraction\":42.0}]");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        List<MappingJackson2HttpMessageConverterTests.MyBean> results = ((List<MappingJackson2HttpMessageConverterTests.MyBean>) (converter.read(List.class, inputMessage)));
        Assert.assertEquals(1, results.size());
        MappingJackson2HttpMessageConverterTests.MyBean result = results.get(0);
        Assert.assertEquals("Foo", result.getString());
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(42.0F, result.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, result.getArray());
        Assert.assertTrue(result.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, result.getBytes());
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(results, new MediaType("application", "json"), outputMessage);
        JSONAssert.assertEquals(body, outputMessage.getBodyAsString(StandardCharsets.UTF_8), true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readAndWriteParameterizedType() throws Exception {
        ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyBean>> beansList = new ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyBean>>() {};
        String body = "[{" + ((((("\"bytes\":\"AQI=\"," + "\"array\":[\"Foo\",\"Bar\"],") + "\"number\":42,") + "\"string\":\"Foo\",") + "\"bool\":true,") + "\"fraction\":42.0}]");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MappingJackson2HttpMessageConverterTests.MyBean> results = ((List<MappingJackson2HttpMessageConverterTests.MyBean>) (converter.read(beansList.getType(), null, inputMessage)));
        Assert.assertEquals(1, results.size());
        MappingJackson2HttpMessageConverterTests.MyBean result = results.get(0);
        Assert.assertEquals("Foo", result.getString());
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(42.0F, result.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, result.getArray());
        Assert.assertTrue(result.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, result.getBytes());
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(results, beansList.getType(), new MediaType("application", "json"), outputMessage);
        JSONAssert.assertEquals(body, outputMessage.getBodyAsString(StandardCharsets.UTF_8), true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void writeParameterizedBaseType() throws Exception {
        ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyBean>> beansList = new ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyBean>>() {};
        ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyBase>> baseList = new ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyBase>>() {};
        String body = "[{" + ((((("\"bytes\":\"AQI=\"," + "\"array\":[\"Foo\",\"Bar\"],") + "\"number\":42,") + "\"string\":\"Foo\",") + "\"bool\":true,") + "\"fraction\":42.0}]");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MappingJackson2HttpMessageConverterTests.MyBean> results = ((List<MappingJackson2HttpMessageConverterTests.MyBean>) (converter.read(beansList.getType(), null, inputMessage)));
        Assert.assertEquals(1, results.size());
        MappingJackson2HttpMessageConverterTests.MyBean result = results.get(0);
        Assert.assertEquals("Foo", result.getString());
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(42.0F, result.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, result.getArray());
        Assert.assertTrue(result.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, result.getBytes());
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(results, baseList.getType(), new MediaType("application", "json"), outputMessage);
        JSONAssert.assertEquals(body, outputMessage.getBodyAsString(StandardCharsets.UTF_8), true);
    }

    @Test
    public void prettyPrint() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.PrettyPrintBean bean = new MappingJackson2HttpMessageConverterTests.PrettyPrintBean();
        bean.setName("Jason");
        this.converter.setPrettyPrint(true);
        this.converter.writeInternal(bean, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertEquals((((("{" + (MappingJackson2HttpMessageConverterTests.NEWLINE_SYSTEM_PROPERTY)) + "  \"name\" : \"Jason\"") + (MappingJackson2HttpMessageConverterTests.NEWLINE_SYSTEM_PROPERTY)) + "}"), result);
    }

    @Test
    public void prettyPrintWithSse() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        outputMessage.getHeaders().setContentType(TEXT_EVENT_STREAM);
        MappingJackson2HttpMessageConverterTests.PrettyPrintBean bean = new MappingJackson2HttpMessageConverterTests.PrettyPrintBean();
        bean.setName("Jason");
        this.converter.setPrettyPrint(true);
        this.converter.writeInternal(bean, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertEquals("{\ndata:  \"name\" : \"Jason\"\ndata:}", result);
    }

    @Test
    public void prefixJson() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.setPrefixJson(true);
        this.converter.writeInternal("foo", null, outputMessage);
        Assert.assertEquals(")]}\', \"foo\"", outputMessage.getBodyAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void prefixJsonCustom() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.setJsonPrefix(")))");
        this.converter.writeInternal("foo", null, outputMessage);
        Assert.assertEquals(")))\"foo\"", outputMessage.getBodyAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void fieldLevelJsonView() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.JacksonViewBean bean = new MappingJackson2HttpMessageConverterTests.JacksonViewBean();
        bean.setWithView1("with");
        bean.setWithView2("with");
        bean.setWithoutView("without");
        MappingJacksonValue jacksonValue = new MappingJacksonValue(bean);
        jacksonValue.setSerializationView(MappingJackson2HttpMessageConverterTests.MyJacksonView1.class);
        this.converter.writeInternal(jacksonValue, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertThat(result, CoreMatchers.containsString("\"withView1\":\"with\""));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("\"withView2\":\"with\"")));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("\"withoutView\":\"without\"")));
    }

    @Test
    public void classLevelJsonView() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.JacksonViewBean bean = new MappingJackson2HttpMessageConverterTests.JacksonViewBean();
        bean.setWithView1("with");
        bean.setWithView2("with");
        bean.setWithoutView("without");
        MappingJacksonValue jacksonValue = new MappingJacksonValue(bean);
        jacksonValue.setSerializationView(MappingJackson2HttpMessageConverterTests.MyJacksonView3.class);
        this.converter.writeInternal(jacksonValue, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("\"withView1\":\"with\"")));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("\"withView2\":\"with\"")));
        Assert.assertThat(result, CoreMatchers.containsString("\"withoutView\":\"without\""));
    }

    @Test
    public void filters() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.JacksonFilteredBean bean = new MappingJackson2HttpMessageConverterTests.JacksonFilteredBean();
        bean.setProperty1("value");
        bean.setProperty2("value");
        MappingJacksonValue jacksonValue = new MappingJacksonValue(bean);
        FilterProvider filters = new SimpleFilterProvider().addFilter("myJacksonFilter", SimpleBeanPropertyFilter.serializeAllExcept("property2"));
        jacksonValue.setFilters(filters);
        this.converter.writeInternal(jacksonValue, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertThat(result, CoreMatchers.containsString("\"property1\":\"value\""));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("\"property2\":\"value\"")));
    }

    // SPR-13318
    @Test
    public void writeSubType() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2HttpMessageConverterTests.MyBean bean = new MappingJackson2HttpMessageConverterTests.MyBean();
        bean.setString("Foo");
        bean.setNumber(42);
        this.converter.writeInternal(bean, MappingJackson2HttpMessageConverterTests.MyInterface.class, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertTrue(result.contains("\"string\":\"Foo\""));
        Assert.assertTrue(result.contains("\"number\":42"));
    }

    // SPR-13318
    @Test
    public void writeSubTypeList() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        List<MappingJackson2HttpMessageConverterTests.MyBean> beans = new ArrayList<>();
        MappingJackson2HttpMessageConverterTests.MyBean foo = new MappingJackson2HttpMessageConverterTests.MyBean();
        foo.setString("Foo");
        foo.setNumber(42);
        beans.add(foo);
        MappingJackson2HttpMessageConverterTests.MyBean bar = new MappingJackson2HttpMessageConverterTests.MyBean();
        bar.setString("Bar");
        bar.setNumber(123);
        beans.add(bar);
        ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyInterface>> typeReference = new ParameterizedTypeReference<List<MappingJackson2HttpMessageConverterTests.MyInterface>>() {};
        this.converter.writeInternal(beans, typeReference.getType(), outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertTrue(result.contains("\"string\":\"Foo\""));
        Assert.assertTrue(result.contains("\"number\":42"));
        Assert.assertTrue(result.contains("\"string\":\"Bar\""));
        Assert.assertTrue(result.contains("\"number\":123"));
    }

    @Test
    public void readWithNoDefaultConstructor() throws Exception {
        String body = "{\"property1\":\"foo\",\"property2\":\"bar\"}";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        try {
            converter.read(MappingJackson2HttpMessageConverterTests.BeanWithNoDefaultConstructor.class, inputMessage);
        } catch (HttpMessageConversionException ex) {
            Assert.assertTrue(ex.getMessage(), ex.getMessage().startsWith("Type definition error:"));
            return;
        }
        Assert.fail();
    }

    interface MyInterface {
        String getString();

        void setString(String string);
    }

    public static class MyBase implements MappingJackson2HttpMessageConverterTests.MyInterface {
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    public static class MyBean extends MappingJackson2HttpMessageConverterTests.MyBase {
        private int number;

        private float fraction;

        private String[] array;

        private boolean bool;

        private byte[] bytes;

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public float getFraction() {
            return fraction;
        }

        public void setFraction(float fraction) {
            this.fraction = fraction;
        }

        public String[] getArray() {
            return array;
        }

        public void setArray(String[] array) {
            this.array = array;
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }
    }

    public static class PrettyPrintBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private interface MyJacksonView1 {}

    private interface MyJacksonView2 {}

    private interface MyJacksonView3 {}

    @SuppressWarnings("unused")
    @JsonView(MappingJackson2HttpMessageConverterTests.MyJacksonView3.class)
    private static class JacksonViewBean {
        @JsonView(MappingJackson2HttpMessageConverterTests.MyJacksonView1.class)
        private String withView1;

        @JsonView(MappingJackson2HttpMessageConverterTests.MyJacksonView2.class)
        private String withView2;

        private String withoutView;

        public String getWithView1() {
            return withView1;
        }

        public void setWithView1(String withView1) {
            this.withView1 = withView1;
        }

        public String getWithView2() {
            return withView2;
        }

        public void setWithView2(String withView2) {
            this.withView2 = withView2;
        }

        public String getWithoutView() {
            return withoutView;
        }

        public void setWithoutView(String withoutView) {
            this.withoutView = withoutView;
        }
    }

    @JsonFilter("myJacksonFilter")
    @SuppressWarnings("unused")
    private static class JacksonFilteredBean {
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

    private static class BeanWithNoDefaultConstructor {
        private final String property1;

        private final String property2;

        public BeanWithNoDefaultConstructor(String property1, String property2) {
            this.property1 = property1;
            this.property2 = property2;
        }

        public String getProperty1() {
            return property1;
        }

        public String getProperty2() {
            return property2;
        }
    }
}

