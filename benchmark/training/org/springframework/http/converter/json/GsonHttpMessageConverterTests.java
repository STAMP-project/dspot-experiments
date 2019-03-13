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


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;


/**
 * Gson 2.x converter tests.
 *
 * @author Roy Clarkson
 * @author Juergen Hoeller
 */
public class GsonHttpMessageConverterTests {
    private final GsonHttpMessageConverter converter = new GsonHttpMessageConverter();

    @Test
    public void canRead() {
        Assert.assertTrue(this.converter.canRead(GsonHttpMessageConverterTests.MyBean.class, new MediaType("application", "json")));
        Assert.assertTrue(this.converter.canRead(Map.class, new MediaType("application", "json")));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue(this.converter.canWrite(GsonHttpMessageConverterTests.MyBean.class, new MediaType("application", "json")));
        Assert.assertTrue(this.converter.canWrite(Map.class, new MediaType("application", "json")));
    }

    @Test
    public void canReadAndWriteMicroformats() {
        Assert.assertTrue(this.converter.canRead(GsonHttpMessageConverterTests.MyBean.class, new MediaType("application", "vnd.test-micro-type+json")));
        Assert.assertTrue(this.converter.canWrite(GsonHttpMessageConverterTests.MyBean.class, new MediaType("application", "vnd.test-micro-type+json")));
    }

    @Test
    public void readTyped() throws IOException {
        String body = "{\"bytes\":[1,2],\"array\":[\"Foo\",\"Bar\"]," + "\"number\":42,\"string\":\"Foo\",\"bool\":true,\"fraction\":42.0}";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        GsonHttpMessageConverterTests.MyBean result = ((GsonHttpMessageConverterTests.MyBean) (this.converter.read(GsonHttpMessageConverterTests.MyBean.class, inputMessage)));
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
        String body = "{\"bytes\":[1,2],\"array\":[\"Foo\",\"Bar\"]," + "\"number\":42,\"string\":\"Foo\",\"bool\":true,\"fraction\":42.0}";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        HashMap<String, Object> result = ((HashMap<String, Object>) (this.converter.read(HashMap.class, inputMessage)));
        Assert.assertEquals("Foo", result.get("string"));
        Number n = ((Number) (result.get("number")));
        Assert.assertEquals(42, n.longValue());
        n = ((Number) (result.get("fraction")));
        Assert.assertEquals(42.0, n.doubleValue(), 0.0);
        List<String> array = new ArrayList<>();
        array.add("Foo");
        array.add("Bar");
        Assert.assertEquals(array, result.get("array"));
        Assert.assertEquals(Boolean.TRUE, result.get("bool"));
        byte[] bytes = new byte[2];
        List<Number> resultBytes = ((ArrayList<Number>) (result.get("bytes")));
        for (int i = 0; i < 2; i++) {
            bytes[i] = resultBytes.get(i).byteValue();
        }
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, bytes);
    }

    @Test
    public void write() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        GsonHttpMessageConverterTests.MyBean body = new GsonHttpMessageConverterTests.MyBean();
        body.setString("Foo");
        body.setNumber(42);
        body.setFraction(42.0F);
        body.setArray(new String[]{ "Foo", "Bar" });
        body.setBool(true);
        body.setBytes(new byte[]{ 1, 2 });
        this.converter.write(body, null, outputMessage);
        Charset utf8 = StandardCharsets.UTF_8;
        String result = outputMessage.getBodyAsString(utf8);
        Assert.assertTrue(result.contains("\"string\":\"Foo\""));
        Assert.assertTrue(result.contains("\"number\":42"));
        Assert.assertTrue(result.contains("fraction\":42.0"));
        Assert.assertTrue(result.contains("\"array\":[\"Foo\",\"Bar\"]"));
        Assert.assertTrue(result.contains("\"bool\":true"));
        Assert.assertTrue(result.contains("\"bytes\":[1,2]"));
        Assert.assertEquals("Invalid content-type", new MediaType("application", "json", utf8), outputMessage.getHeaders().getContentType());
    }

    @Test
    public void writeWithBaseType() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        GsonHttpMessageConverterTests.MyBean body = new GsonHttpMessageConverterTests.MyBean();
        body.setString("Foo");
        body.setNumber(42);
        body.setFraction(42.0F);
        body.setArray(new String[]{ "Foo", "Bar" });
        body.setBool(true);
        body.setBytes(new byte[]{ 1, 2 });
        this.converter.write(body, GsonHttpMessageConverterTests.MyBase.class, null, outputMessage);
        Charset utf8 = StandardCharsets.UTF_8;
        String result = outputMessage.getBodyAsString(utf8);
        Assert.assertTrue(result.contains("\"string\":\"Foo\""));
        Assert.assertTrue(result.contains("\"number\":42"));
        Assert.assertTrue(result.contains("fraction\":42.0"));
        Assert.assertTrue(result.contains("\"array\":[\"Foo\",\"Bar\"]"));
        Assert.assertTrue(result.contains("\"bool\":true"));
        Assert.assertTrue(result.contains("\"bytes\":[1,2]"));
        Assert.assertEquals("Invalid content-type", new MediaType("application", "json", utf8), outputMessage.getHeaders().getContentType());
    }

    @Test
    public void writeUTF16() throws IOException {
        MediaType contentType = new MediaType("application", "json", StandardCharsets.UTF_16BE);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        String body = "H\u00e9llo W\u00f6rld";
        this.converter.write(body, contentType, outputMessage);
        Assert.assertEquals("Invalid result", (("\"" + body) + "\""), outputMessage.getBodyAsString(StandardCharsets.UTF_16BE));
        Assert.assertEquals("Invalid content-type", contentType, outputMessage.getHeaders().getContentType());
    }

    @Test(expected = HttpMessageNotReadableException.class)
    public void readInvalidJson() throws IOException {
        String body = "FooBar";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        this.converter.read(GsonHttpMessageConverterTests.MyBean.class, inputMessage);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readAndWriteGenerics() throws Exception {
        Field beansList = GsonHttpMessageConverterTests.ListHolder.class.getField("listField");
        String body = "[{\"bytes\":[1,2],\"array\":[\"Foo\",\"Bar\"]," + "\"number\":42,\"string\":\"Foo\",\"bool\":true,\"fraction\":42.0}]";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes(StandardCharsets.UTF_8));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        Type genericType = beansList.getGenericType();
        List<GsonHttpMessageConverterTests.MyBean> results = ((List<GsonHttpMessageConverterTests.MyBean>) (converter.read(genericType, GsonHttpMessageConverterTests.MyBeanListHolder.class, inputMessage)));
        Assert.assertEquals(1, results.size());
        GsonHttpMessageConverterTests.MyBean result = results.get(0);
        Assert.assertEquals("Foo", result.getString());
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(42.0F, result.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, result.getArray());
        Assert.assertTrue(result.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, result.getBytes());
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        converter.write(results, genericType, new MediaType("application", "json"), outputMessage);
        JSONAssert.assertEquals(body, outputMessage.getBodyAsString(StandardCharsets.UTF_8), true);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void readAndWriteParameterizedType() throws Exception {
        ParameterizedTypeReference<List<GsonHttpMessageConverterTests.MyBean>> beansList = new ParameterizedTypeReference<List<GsonHttpMessageConverterTests.MyBean>>() {};
        String body = "[{\"bytes\":[1,2],\"array\":[\"Foo\",\"Bar\"]," + "\"number\":42,\"string\":\"Foo\",\"bool\":true,\"fraction\":42.0}]";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes(StandardCharsets.UTF_8));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        List<GsonHttpMessageConverterTests.MyBean> results = ((List<GsonHttpMessageConverterTests.MyBean>) (converter.read(beansList.getType(), null, inputMessage)));
        Assert.assertEquals(1, results.size());
        GsonHttpMessageConverterTests.MyBean result = results.get(0);
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
        ParameterizedTypeReference<List<GsonHttpMessageConverterTests.MyBean>> beansList = new ParameterizedTypeReference<List<GsonHttpMessageConverterTests.MyBean>>() {};
        ParameterizedTypeReference<List<GsonHttpMessageConverterTests.MyBase>> baseList = new ParameterizedTypeReference<List<GsonHttpMessageConverterTests.MyBase>>() {};
        String body = "[{\"bytes\":[1,2],\"array\":[\"Foo\",\"Bar\"]," + "\"number\":42,\"string\":\"Foo\",\"bool\":true,\"fraction\":42.0}]";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes(StandardCharsets.UTF_8));
        inputMessage.getHeaders().setContentType(new MediaType("application", "json"));
        List<GsonHttpMessageConverterTests.MyBean> results = ((List<GsonHttpMessageConverterTests.MyBean>) (converter.read(beansList.getType(), null, inputMessage)));
        Assert.assertEquals(1, results.size());
        GsonHttpMessageConverterTests.MyBean result = results.get(0);
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
    public void prefixJson() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.setPrefixJson(true);
        this.converter.writeInternal("foo", null, outputMessage);
        Assert.assertEquals(")]}\', \"foo\"", outputMessage.getBodyAsString(StandardCharsets.UTF_8));
    }

    @Test
    public void prefixJsonCustom() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.setJsonPrefix(")))");
        this.converter.writeInternal("foo", null, outputMessage);
        Assert.assertEquals(")))\"foo\"", outputMessage.getBodyAsString(StandardCharsets.UTF_8));
    }

    public static class MyBase {
        private String string;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }

    public static class MyBean extends GsonHttpMessageConverterTests.MyBase {
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

    public static class ListHolder<E> {
        public List<E> listField;
    }

    public static class MyBeanListHolder extends GsonHttpMessageConverterTests.ListHolder<GsonHttpMessageConverterTests.MyBean> {}
}

