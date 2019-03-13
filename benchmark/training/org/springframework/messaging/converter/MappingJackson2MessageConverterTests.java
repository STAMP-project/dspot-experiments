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
package org.springframework.messaging.converter;


import MessageHeaders.CONTENT_TYPE;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeType;


/**
 * Test fixture for {@link MappingJackson2MessageConverter}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class MappingJackson2MessageConverterTests {
    @Test
    public void defaultConstructor() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        Assert.assertThat(converter.getSupportedMimeTypes(), contains(new MimeType("application", "json", StandardCharsets.UTF_8)));
        Assert.assertFalse(converter.getObjectMapper().getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    // SPR-12724
    @Test
    public void mimetypeParametrizedConstructor() {
        MimeType mimetype = new MimeType("application", "xml", StandardCharsets.UTF_8);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter(mimetype);
        Assert.assertThat(converter.getSupportedMimeTypes(), contains(mimetype));
        Assert.assertFalse(converter.getObjectMapper().getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    // SPR-12724
    @Test
    public void mimetypesParametrizedConstructor() {
        MimeType jsonMimetype = new MimeType("application", "json", StandardCharsets.UTF_8);
        MimeType xmlMimetype = new MimeType("application", "xml", StandardCharsets.UTF_8);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter(jsonMimetype, xmlMimetype);
        Assert.assertThat(converter.getSupportedMimeTypes(), contains(jsonMimetype, xmlMimetype));
        Assert.assertFalse(converter.getObjectMapper().getDeserializationConfig().isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    }

    @Test
    public void fromMessage() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        String payload = "{" + ((((("\"bytes\":\"AQI=\"," + "\"array\":[\"Foo\",\"Bar\"],") + "\"number\":42,") + "\"string\":\"Foo\",") + "\"bool\":true,") + "\"fraction\":42.0}");
        Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
        MappingJackson2MessageConverterTests.MyBean actual = ((MappingJackson2MessageConverterTests.MyBean) (converter.fromMessage(message, MappingJackson2MessageConverterTests.MyBean.class)));
        Assert.assertEquals("Foo", actual.getString());
        Assert.assertEquals(42, actual.getNumber());
        Assert.assertEquals(42.0F, actual.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, actual.getArray());
        Assert.assertTrue(actual.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, actual.getBytes());
    }

    @Test
    public void fromMessageUntyped() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        String payload = "{\"bytes\":\"AQI=\",\"array\":[\"Foo\",\"Bar\"]," + "\"number\":42,\"string\":\"Foo\",\"bool\":true,\"fraction\":42.0}";
        Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
        @SuppressWarnings("unchecked")
        HashMap<String, Object> actual = ((HashMap<String, Object>) (converter.fromMessage(message, HashMap.class)));
        Assert.assertEquals("Foo", actual.get("string"));
        Assert.assertEquals(42, actual.get("number"));
        Assert.assertEquals(42.0, ((Double) (actual.get("fraction"))), 0.0);
        Assert.assertEquals(Arrays.asList("Foo", "Bar"), actual.get("array"));
        Assert.assertEquals(Boolean.TRUE, actual.get("bool"));
        Assert.assertEquals("AQI=", actual.get("bytes"));
    }

    @Test(expected = MessageConversionException.class)
    public void fromMessageInvalidJson() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        String payload = "FooBar";
        Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
        converter.fromMessage(message, MappingJackson2MessageConverterTests.MyBean.class);
    }

    @Test
    public void fromMessageValidJsonWithUnknownProperty() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        String payload = "{\"string\":\"string\",\"unknownProperty\":\"value\"}";
        Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
        MappingJackson2MessageConverterTests.MyBean myBean = ((MappingJackson2MessageConverterTests.MyBean) (converter.fromMessage(message, MappingJackson2MessageConverterTests.MyBean.class)));
        Assert.assertEquals("string", myBean.getString());
    }

    // SPR-16252
    @Test
    public void fromMessageToList() throws Exception {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        String payload = "[1, 2, 3, 4, 5, 6, 7, 8, 9]";
        Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
        Method method = getClass().getDeclaredMethod("handleList", List.class);
        MethodParameter param = new MethodParameter(method, 0);
        Object actual = converter.fromMessage(message, List.class, param);
        Assert.assertNotNull(actual);
        Assert.assertEquals(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L), actual);
    }

    // SPR-16486
    @Test
    public void fromMessageToMessageWithPojo() throws Exception {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        String payload = "{\"string\":\"foo\"}";
        Message<?> message = MessageBuilder.withPayload(payload.getBytes(StandardCharsets.UTF_8)).build();
        Method method = getClass().getDeclaredMethod("handleMessage", Message.class);
        MethodParameter param = new MethodParameter(method, 0);
        Object actual = converter.fromMessage(message, MappingJackson2MessageConverterTests.MyBean.class, param);
        Assert.assertTrue((actual instanceof MappingJackson2MessageConverterTests.MyBean));
        Assert.assertEquals("foo", ((MappingJackson2MessageConverterTests.MyBean) (actual)).getString());
    }

    @Test
    public void toMessage() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        MappingJackson2MessageConverterTests.MyBean payload = new MappingJackson2MessageConverterTests.MyBean();
        payload.setString("Foo");
        payload.setNumber(42);
        payload.setFraction(42.0F);
        payload.setArray(new String[]{ "Foo", "Bar" });
        payload.setBool(true);
        payload.setBytes(new byte[]{ 1, 2 });
        Message<?> message = converter.toMessage(payload, null);
        String actual = new String(((byte[]) (message.getPayload())), StandardCharsets.UTF_8);
        Assert.assertTrue(actual.contains("\"string\":\"Foo\""));
        Assert.assertTrue(actual.contains("\"number\":42"));
        Assert.assertTrue(actual.contains("fraction\":42.0"));
        Assert.assertTrue(actual.contains("\"array\":[\"Foo\",\"Bar\"]"));
        Assert.assertTrue(actual.contains("\"bool\":true"));
        Assert.assertTrue(actual.contains("\"bytes\":\"AQI=\""));
        Assert.assertEquals("Invalid content-type", new MimeType("application", "json", StandardCharsets.UTF_8), message.getHeaders().get(CONTENT_TYPE, MimeType.class));
    }

    @Test
    public void toMessageUtf16() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        MimeType contentType = new MimeType("application", "json", StandardCharsets.UTF_16BE);
        Map<String, Object> map = new HashMap<>();
        map.put(CONTENT_TYPE, contentType);
        MessageHeaders headers = new MessageHeaders(map);
        String payload = "H\u00e9llo W\u00f6rld";
        Message<?> message = converter.toMessage(payload, headers);
        Assert.assertEquals((("\"" + payload) + "\""), new String(((byte[]) (message.getPayload())), StandardCharsets.UTF_16BE));
        Assert.assertEquals(contentType, message.getHeaders().get(CONTENT_TYPE));
    }

    @Test
    public void toMessageUtf16String() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setSerializedPayloadClass(String.class);
        MimeType contentType = new MimeType("application", "json", StandardCharsets.UTF_16BE);
        Map<String, Object> map = new HashMap<>();
        map.put(CONTENT_TYPE, contentType);
        MessageHeaders headers = new MessageHeaders(map);
        String payload = "H\u00e9llo W\u00f6rld";
        Message<?> message = converter.toMessage(payload, headers);
        Assert.assertEquals((("\"" + payload) + "\""), message.getPayload());
        Assert.assertEquals(contentType, message.getHeaders().get(CONTENT_TYPE));
    }

    @Test
    public void toMessageJsonView() throws Exception {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        Map<String, Object> map = new HashMap<>();
        Method method = getClass().getDeclaredMethod("jsonViewResponse");
        MethodParameter returnType = new MethodParameter(method, (-1));
        Message<?> message = converter.toMessage(jsonViewResponse(), new MessageHeaders(map), returnType);
        String actual = new String(((byte[]) (message.getPayload())), StandardCharsets.UTF_8);
        Assert.assertThat(actual, containsString("\"withView1\":\"with\""));
        Assert.assertThat(actual, containsString("\"withView2\":\"with\""));
        Assert.assertThat(actual, not(containsString("\"withoutView\":\"with\"")));
        method = getClass().getDeclaredMethod("jsonViewPayload", MappingJackson2MessageConverterTests.JacksonViewBean.class);
        MethodParameter param = new MethodParameter(method, 0);
        MappingJackson2MessageConverterTests.JacksonViewBean back = ((MappingJackson2MessageConverterTests.JacksonViewBean) (converter.fromMessage(message, MappingJackson2MessageConverterTests.JacksonViewBean.class, param)));
        Assert.assertNull(back.getWithView1());
        Assert.assertEquals("with", back.getWithView2());
        Assert.assertNull(back.getWithoutView());
    }

    public static class MyBean {
        private String string;

        private int number;

        private float fraction;

        private String[] array;

        private boolean bool;

        private byte[] bytes;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public boolean isBool() {
            return bool;
        }

        public void setBool(boolean bool) {
            this.bool = bool;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

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
    }

    public interface MyJacksonView1 {}

    public interface MyJacksonView2 {}

    public static class JacksonViewBean {
        @JsonView(MappingJackson2MessageConverterTests.MyJacksonView1.class)
        private String withView1;

        @JsonView({ MappingJackson2MessageConverterTests.MyJacksonView1.class, MappingJackson2MessageConverterTests.MyJacksonView2.class })
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
}

