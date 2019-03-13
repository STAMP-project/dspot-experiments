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
package org.springframework.http.converter.xml;


import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJacksonValue;


/**
 * Jackson 2.x XML converter tests.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class MappingJackson2XmlHttpMessageConverterTests {
    private final MappingJackson2XmlHttpMessageConverter converter = new MappingJackson2XmlHttpMessageConverter();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void canRead() {
        Assert.assertTrue(converter.canRead(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, new MediaType("application", "xml")));
        Assert.assertTrue(converter.canRead(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, new MediaType("text", "xml")));
        Assert.assertTrue(converter.canRead(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, new MediaType("application", "soap+xml")));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue(converter.canWrite(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, new MediaType("application", "xml")));
        Assert.assertTrue(converter.canWrite(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, new MediaType("text", "xml")));
        Assert.assertTrue(converter.canWrite(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, new MediaType("application", "soap+xml")));
    }

    @Test
    public void read() throws IOException {
        String body = "<MyBean>" + (((((("<string>Foo</string>" + "<number>42</number>") + "<fraction>42.0</fraction>") + "<array><array>Foo</array>") + "<array>Bar</array></array>") + "<bool>true</bool>") + "<bytes>AQI=</bytes></MyBean>");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "xml"));
        MappingJackson2XmlHttpMessageConverterTests.MyBean result = ((MappingJackson2XmlHttpMessageConverterTests.MyBean) (converter.read(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, inputMessage)));
        Assert.assertEquals("Foo", result.getString());
        Assert.assertEquals(42, result.getNumber());
        Assert.assertEquals(42.0F, result.getFraction(), 0.0F);
        Assert.assertArrayEquals(new String[]{ "Foo", "Bar" }, result.getArray());
        Assert.assertTrue(result.isBool());
        Assert.assertArrayEquals(new byte[]{ 1, 2 }, result.getBytes());
    }

    @Test
    public void write() throws IOException {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2XmlHttpMessageConverterTests.MyBean body = new MappingJackson2XmlHttpMessageConverterTests.MyBean();
        body.setString("Foo");
        body.setNumber(42);
        body.setFraction(42.0F);
        body.setArray(new String[]{ "Foo", "Bar" });
        body.setBool(true);
        body.setBytes(new byte[]{ 1, 2 });
        converter.write(body, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertTrue(result.contains("<string>Foo</string>"));
        Assert.assertTrue(result.contains("<number>42</number>"));
        Assert.assertTrue(result.contains("<fraction>42.0</fraction>"));
        Assert.assertTrue(result.contains("<array><array>Foo</array><array>Bar</array></array>"));
        Assert.assertTrue(result.contains("<bool>true</bool>"));
        Assert.assertTrue(result.contains("<bytes>AQI=</bytes>"));
        Assert.assertEquals("Invalid content-type", new MediaType("application", "xml", StandardCharsets.UTF_8), outputMessage.getHeaders().getContentType());
    }

    @Test
    public void readInvalidXml() throws IOException {
        String body = "FooBar";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "xml"));
        this.thrown.expect(HttpMessageNotReadableException.class);
        converter.read(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, inputMessage);
    }

    @Test
    public void readValidXmlWithUnknownProperty() throws IOException {
        String body = "<MyBean><string>string</string><unknownProperty>value</unknownProperty></MyBean>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "xml"));
        converter.read(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, inputMessage);
        // Assert no HttpMessageNotReadableException is thrown
    }

    @Test
    public void jsonView() throws Exception {
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        MappingJackson2XmlHttpMessageConverterTests.JacksonViewBean bean = new MappingJackson2XmlHttpMessageConverterTests.JacksonViewBean();
        bean.setWithView1("with");
        bean.setWithView2("with");
        bean.setWithoutView("without");
        MappingJacksonValue jacksonValue = new MappingJacksonValue(bean);
        jacksonValue.setSerializationView(MappingJackson2XmlHttpMessageConverterTests.MyJacksonView1.class);
        this.converter.write(jacksonValue, null, outputMessage);
        String result = outputMessage.getBodyAsString(StandardCharsets.UTF_8);
        Assert.assertThat(result, CoreMatchers.containsString("<withView1>with</withView1>"));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("<withView2>with</withView2>")));
        Assert.assertThat(result, CoreMatchers.not(CoreMatchers.containsString("<withoutView>without</withoutView>")));
    }

    @Test
    public void customXmlMapper() {
        new MappingJackson2XmlHttpMessageConverter(new MappingJackson2XmlHttpMessageConverterTests.MyXmlMapper());
        // Assert no exception is thrown
    }

    @Test
    public void readWithExternalReference() throws IOException {
        String body = (("<!DOCTYPE MyBean SYSTEM \"http://192.168.28.42/1.jsp\" [" + ("  <!ELEMENT root ANY >\n" + "  <!ENTITY ext SYSTEM \"")) + (new ClassPathResource("external.txt", getClass()).getURI())) + "\" >]><MyBean><string>&ext;</string></MyBean>";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "xml"));
        this.thrown.expect(HttpMessageNotReadableException.class);
        this.converter.read(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, inputMessage);
    }

    @Test
    public void readWithXmlBomb() throws IOException {
        // https://en.wikipedia.org/wiki/Billion_laughs
        // https://msdn.microsoft.com/en-us/magazine/ee335713.aspx
        String body = "<?xml version=\"1.0\"?>\n" + ((((((((((((("<!DOCTYPE lolz [\n" + " <!ENTITY lol \"lol\">\n") + " <!ELEMENT lolz (#PCDATA)>\n") + " <!ENTITY lol1 \"&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;\">\n") + " <!ENTITY lol2 \"&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;&lol1;\">\n") + " <!ENTITY lol3 \"&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;\">\n") + " <!ENTITY lol4 \"&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;\">\n") + " <!ENTITY lol5 \"&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;\">\n") + " <!ENTITY lol6 \"&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;\">\n") + " <!ENTITY lol7 \"&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;\">\n") + " <!ENTITY lol8 \"&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;\">\n") + " <!ENTITY lol9 \"&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;\">\n") + "]>\n") + "<MyBean>&lol9;</MyBean>");
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes("UTF-8"));
        inputMessage.getHeaders().setContentType(new MediaType("application", "xml"));
        this.thrown.expect(HttpMessageNotReadableException.class);
        this.converter.read(MappingJackson2XmlHttpMessageConverterTests.MyBean.class, inputMessage);
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

    private interface MyJacksonView1 {}

    private interface MyJacksonView2 {}

    @SuppressWarnings("unused")
    private static class JacksonViewBean {
        @JsonView(MappingJackson2XmlHttpMessageConverterTests.MyJacksonView1.class)
        private String withView1;

        @JsonView(MappingJackson2XmlHttpMessageConverterTests.MyJacksonView2.class)
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

    @SuppressWarnings("serial")
    private static class MyXmlMapper extends XmlMapper {}
}

