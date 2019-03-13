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
package org.springframework.http.converter;


import MediaType.ALL;
import MediaType.APPLICATION_FORM_URLENCODED;
import MediaType.TEXT_XML;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.MockHttpInputMessage;
import org.springframework.http.MockHttpOutputMessage;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.util.MultiValueMap;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 */
public class FormHttpMessageConverterTests {
    private final FormHttpMessageConverter converter = new AllEncompassingFormHttpMessageConverter();

    @Test
    public void canRead() {
        Assert.assertTrue(this.converter.canRead(MultiValueMap.class, new MediaType("application", "x-www-form-urlencoded")));
        Assert.assertFalse(this.converter.canRead(MultiValueMap.class, new MediaType("multipart", "form-data")));
    }

    @Test
    public void canWrite() {
        Assert.assertTrue(this.converter.canWrite(MultiValueMap.class, new MediaType("application", "x-www-form-urlencoded")));
        Assert.assertTrue(this.converter.canWrite(MultiValueMap.class, new MediaType("multipart", "form-data")));
        Assert.assertTrue(this.converter.canWrite(MultiValueMap.class, new MediaType("multipart", "form-data", StandardCharsets.UTF_8)));
        Assert.assertTrue(this.converter.canWrite(MultiValueMap.class, ALL));
    }

    @Test
    public void readForm() throws Exception {
        String body = "name+1=value+1&name+2=value+2%2B1&name+2=value+2%2B2&name+3";
        MockHttpInputMessage inputMessage = new MockHttpInputMessage(body.getBytes(StandardCharsets.ISO_8859_1));
        inputMessage.getHeaders().setContentType(new MediaType("application", "x-www-form-urlencoded", StandardCharsets.ISO_8859_1));
        MultiValueMap<String, String> result = this.converter.read(null, inputMessage);
        Assert.assertEquals("Invalid result", 3, result.size());
        Assert.assertEquals("Invalid result", "value 1", result.getFirst("name 1"));
        List<String> values = result.get("name 2");
        Assert.assertEquals("Invalid result", 2, values.size());
        Assert.assertEquals("Invalid result", "value 2+1", values.get(0));
        Assert.assertEquals("Invalid result", "value 2+2", values.get(1));
        Assert.assertNull("Invalid result", result.getFirst("name 3"));
    }

    @Test
    public void writeForm() throws IOException {
        MultiValueMap<String, String> body = new org.springframework.util.LinkedMultiValueMap();
        body.set("name 1", "value 1");
        body.add("name 2", "value 2+1");
        body.add("name 2", "value 2+2");
        body.add("name 3", null);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.write(body, APPLICATION_FORM_URLENCODED, outputMessage);
        Assert.assertEquals("Invalid result", "name+1=value+1&name+2=value+2%2B1&name+2=value+2%2B2&name+3", outputMessage.getBodyAsString(StandardCharsets.UTF_8));
        Assert.assertEquals("Invalid content-type", "application/x-www-form-urlencoded;charset=UTF-8", outputMessage.getHeaders().getContentType().toString());
        Assert.assertEquals("Invalid content-length", outputMessage.getBodyAsBytes().length, outputMessage.getHeaders().getContentLength());
    }

    @Test
    public void writeMultipart() throws Exception {
        MultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap();
        parts.add("name 1", "value 1");
        parts.add("name 2", "value 2+1");
        parts.add("name 2", "value 2+2");
        parts.add("name 3", null);
        Resource logo = new ClassPathResource("/org/springframework/http/converter/logo.jpg");
        parts.add("logo", logo);
        // SPR-12108
        Resource utf8 = new ClassPathResource("/org/springframework/http/converter/logo.jpg") {
            @Override
            public String getFilename() {
                return "Hall\u00f6le.jpg";
            }
        };
        parts.add("utf8", utf8);
        Source xml = new StreamSource(new StringReader("<root><child/></root>"));
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(TEXT_XML);
        HttpEntity<Source> entity = new HttpEntity(xml, entityHeaders);
        parts.add("xml", entity);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.write(parts, new MediaType("multipart", "form-data", StandardCharsets.UTF_8), outputMessage);
        final MediaType contentType = outputMessage.getHeaders().getContentType();
        // SPR-17030
        Assert.assertThat(contentType.getParameters().keySet(), Matchers.contains("charset", "boundary"));
        // see if Commons FileUpload can read what we wrote
        FileItemFactory fileItemFactory = new DiskFileItemFactory();
        FileUpload fileUpload = new FileUpload(fileItemFactory);
        RequestContext requestContext = new FormHttpMessageConverterTests.MockHttpOutputMessageRequestContext(outputMessage);
        List<FileItem> items = fileUpload.parseRequest(requestContext);
        Assert.assertEquals(6, items.size());
        FileItem item = items.get(0);
        Assert.assertTrue(item.isFormField());
        Assert.assertEquals("name 1", item.getFieldName());
        Assert.assertEquals("value 1", item.getString());
        item = items.get(1);
        Assert.assertTrue(item.isFormField());
        Assert.assertEquals("name 2", item.getFieldName());
        Assert.assertEquals("value 2+1", item.getString());
        item = items.get(2);
        Assert.assertTrue(item.isFormField());
        Assert.assertEquals("name 2", item.getFieldName());
        Assert.assertEquals("value 2+2", item.getString());
        item = items.get(3);
        Assert.assertFalse(item.isFormField());
        Assert.assertEquals("logo", item.getFieldName());
        Assert.assertEquals("logo.jpg", item.getName());
        Assert.assertEquals("image/jpeg", item.getContentType());
        Assert.assertEquals(logo.getFile().length(), item.getSize());
        item = items.get(4);
        Assert.assertFalse(item.isFormField());
        Assert.assertEquals("utf8", item.getFieldName());
        Assert.assertEquals("Hall\u00f6le.jpg", item.getName());
        Assert.assertEquals("image/jpeg", item.getContentType());
        Assert.assertEquals(logo.getFile().length(), item.getSize());
        item = items.get(5);
        Assert.assertEquals("xml", item.getFieldName());
        Assert.assertEquals("text/xml", item.getContentType());
        Mockito.verify(outputMessage.getBody(), Mockito.never()).close();
    }

    // SPR-13309
    @Test
    public void writeMultipartOrder() throws Exception {
        FormHttpMessageConverterTests.MyBean myBean = new FormHttpMessageConverterTests.MyBean();
        myBean.setString("foo");
        MultiValueMap<String, Object> parts = new org.springframework.util.LinkedMultiValueMap();
        parts.add("part1", myBean);
        org.springframework.http.HttpHeaders entityHeaders = new org.springframework.http.HttpHeaders();
        entityHeaders.setContentType(TEXT_XML);
        HttpEntity<FormHttpMessageConverterTests.MyBean> entity = new HttpEntity(myBean, entityHeaders);
        parts.add("part2", entity);
        MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
        this.converter.setMultipartCharset(StandardCharsets.UTF_8);
        this.converter.write(parts, new MediaType("multipart", "form-data", StandardCharsets.UTF_8), outputMessage);
        final MediaType contentType = outputMessage.getHeaders().getContentType();
        Assert.assertNotNull("No boundary found", contentType.getParameter("boundary"));
        // see if Commons FileUpload can read what we wrote
        FileItemFactory fileItemFactory = new DiskFileItemFactory();
        FileUpload fileUpload = new FileUpload(fileItemFactory);
        RequestContext requestContext = new FormHttpMessageConverterTests.MockHttpOutputMessageRequestContext(outputMessage);
        List<FileItem> items = fileUpload.parseRequest(requestContext);
        Assert.assertEquals(2, items.size());
        FileItem item = items.get(0);
        Assert.assertTrue(item.isFormField());
        Assert.assertEquals("part1", item.getFieldName());
        Assert.assertEquals("{\"string\":\"foo\"}", item.getString());
        item = items.get(1);
        Assert.assertTrue(item.isFormField());
        Assert.assertEquals("part2", item.getFieldName());
        // With developer builds we get: <MyBean><string>foo</string></MyBean>
        // But on CI server we get: <MyBean xmlns=""><string>foo</string></MyBean>
        // So... we make a compromise:
        Assert.assertThat(item.getString(), CoreMatchers.allOf(CoreMatchers.startsWith("<MyBean"), CoreMatchers.endsWith("><string>foo</string></MyBean>")));
    }

    private static class MockHttpOutputMessageRequestContext implements RequestContext {
        private final MockHttpOutputMessage outputMessage;

        private MockHttpOutputMessageRequestContext(MockHttpOutputMessage outputMessage) {
            this.outputMessage = outputMessage;
        }

        @Override
        public String getCharacterEncoding() {
            MediaType type = this.outputMessage.getHeaders().getContentType();
            return (type != null) && ((type.getCharset()) != null) ? type.getCharset().name() : null;
        }

        @Override
        public String getContentType() {
            MediaType type = this.outputMessage.getHeaders().getContentType();
            return type != null ? type.toString() : null;
        }

        @Override
        @Deprecated
        public int getContentLength() {
            return this.outputMessage.getBodyAsBytes().length;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(this.outputMessage.getBodyAsBytes());
        }
    }

    public static class MyBean {
        private String string;

        public String getString() {
            return this.string;
        }

        public void setString(String string) {
            this.string = string;
        }
    }
}

