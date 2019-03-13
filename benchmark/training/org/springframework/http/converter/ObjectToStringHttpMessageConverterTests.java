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


import MediaType.APPLICATION_XML;
import MediaType.TEXT_HTML;
import MediaType.TEXT_PLAIN_VALUE;
import MediaType.TEXT_XML;
import StringHttpMessageConverter.DEFAULT_CHARSET;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;


/**
 * Test cases for {@link ObjectToStringHttpMessageConverter} class.
 *
 * @author <a href="mailto:dmitry.katsubo@gmail.com">Dmitry Katsubo</a>
 * @author Rossen Stoyanchev
 */
public class ObjectToStringHttpMessageConverterTests {
    private ObjectToStringHttpMessageConverter converter;

    private MockHttpServletResponse servletResponse;

    private ServletServerHttpResponse response;

    @Test
    public void canRead() {
        Assert.assertFalse(this.converter.canRead(Math.class, null));
        Assert.assertFalse(this.converter.canRead(Resource.class, null));
        Assert.assertTrue(this.converter.canRead(Locale.class, null));
        Assert.assertTrue(this.converter.canRead(BigInteger.class, null));
        Assert.assertFalse(this.converter.canRead(BigInteger.class, TEXT_HTML));
        Assert.assertFalse(this.converter.canRead(BigInteger.class, TEXT_XML));
        Assert.assertFalse(this.converter.canRead(BigInteger.class, APPLICATION_XML));
    }

    @Test
    public void canWrite() {
        Assert.assertFalse(this.converter.canWrite(Math.class, null));
        Assert.assertFalse(this.converter.canWrite(Resource.class, null));
        Assert.assertTrue(this.converter.canWrite(Locale.class, null));
        Assert.assertTrue(this.converter.canWrite(Double.class, null));
        Assert.assertFalse(this.converter.canWrite(BigInteger.class, TEXT_HTML));
        Assert.assertFalse(this.converter.canWrite(BigInteger.class, TEXT_XML));
        Assert.assertFalse(this.converter.canWrite(BigInteger.class, APPLICATION_XML));
        Assert.assertTrue(this.converter.canWrite(BigInteger.class, MediaType.valueOf("text/*")));
    }

    @Test
    public void defaultCharset() throws IOException {
        this.converter.write(Integer.valueOf(5), null, response);
        Assert.assertEquals("ISO-8859-1", servletResponse.getCharacterEncoding());
    }

    @Test
    public void defaultCharsetModified() throws IOException {
        ConversionService cs = new DefaultConversionService();
        ObjectToStringHttpMessageConverter converter = new ObjectToStringHttpMessageConverter(cs, StandardCharsets.UTF_16);
        converter.write(((byte) (31)), null, this.response);
        Assert.assertEquals("UTF-16", this.servletResponse.getCharacterEncoding());
    }

    @Test
    public void writeAcceptCharset() throws IOException {
        this.converter.write(new Date(), null, this.response);
        Assert.assertNotNull(this.servletResponse.getHeader("Accept-Charset"));
    }

    @Test
    public void writeAcceptCharsetTurnedOff() throws IOException {
        this.converter.setWriteAcceptCharset(false);
        this.converter.write(new Date(), null, this.response);
        Assert.assertNull(this.servletResponse.getHeader("Accept-Charset"));
    }

    @Test
    public void read() throws IOException {
        Short shortValue = Short.valueOf(((short) (781)));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType(TEXT_PLAIN_VALUE);
        request.setContent(shortValue.toString().getBytes(DEFAULT_CHARSET));
        Assert.assertEquals(shortValue, this.converter.read(Short.class, new ServletServerHttpRequest(request)));
        Float floatValue = Float.valueOf(123);
        request = new MockHttpServletRequest();
        request.setContentType(TEXT_PLAIN_VALUE);
        request.setCharacterEncoding("UTF-16");
        request.setContent(floatValue.toString().getBytes("UTF-16"));
        Assert.assertEquals(floatValue, this.converter.read(Float.class, new ServletServerHttpRequest(request)));
        Long longValue = Long.valueOf(55819182821331L);
        request = new MockHttpServletRequest();
        request.setContentType(TEXT_PLAIN_VALUE);
        request.setCharacterEncoding("UTF-8");
        request.setContent(longValue.toString().getBytes("UTF-8"));
        Assert.assertEquals(longValue, this.converter.read(Long.class, new ServletServerHttpRequest(request)));
    }

    @Test
    public void write() throws IOException {
        this.converter.write(((byte) (-8)), null, this.response);
        Assert.assertEquals("ISO-8859-1", this.servletResponse.getCharacterEncoding());
        Assert.assertTrue(this.servletResponse.getContentType().startsWith(TEXT_PLAIN_VALUE));
        Assert.assertEquals(2, this.servletResponse.getContentLength());
        Assert.assertArrayEquals(new byte[]{ '-', '8' }, this.servletResponse.getContentAsByteArray());
    }

    @Test
    public void writeUtf16() throws IOException {
        MediaType contentType = new MediaType("text", "plain", StandardCharsets.UTF_16);
        this.converter.write(Integer.valueOf(958), contentType, this.response);
        Assert.assertEquals("UTF-16", this.servletResponse.getCharacterEncoding());
        Assert.assertTrue(this.servletResponse.getContentType().startsWith(TEXT_PLAIN_VALUE));
        Assert.assertEquals(8, this.servletResponse.getContentLength());
        // First two bytes: byte order mark
        Assert.assertArrayEquals(new byte[]{ -2, -1, 0, '9', 0, '5', 0, '8' }, this.servletResponse.getContentAsByteArray());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConversionServiceRequired() {
        new ObjectToStringHttpMessageConverter(null);
    }
}

