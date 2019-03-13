/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.util;


import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 4.2
 */
public class Base64UtilsTests {
    @Test
    public void encode() throws UnsupportedEncodingException {
        byte[] bytes = new byte[]{ -79, 10, -115, -79, 100, -32, 117, 65, 5, -73, -87, -101, -25, 46, 63, -27 };
        Assert.assertArrayEquals(bytes, Base64Utils.decode(Base64Utils.encode(bytes)));
        bytes = "Hello World".getBytes("UTF-8");
        Assert.assertArrayEquals(bytes, Base64Utils.decode(Base64Utils.encode(bytes)));
        bytes = "Hello World\r\nSecond Line".getBytes("UTF-8");
        Assert.assertArrayEquals(bytes, Base64Utils.decode(Base64Utils.encode(bytes)));
        bytes = "Hello World\r\nSecond Line\r\n".getBytes("UTF-8");
        Assert.assertArrayEquals(bytes, Base64Utils.decode(Base64Utils.encode(bytes)));
        bytes = new byte[]{ ((byte) (251)), ((byte) (240)) };
        Assert.assertArrayEquals("+/A=".getBytes(), Base64Utils.encode(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decode(Base64Utils.encode(bytes)));
        Assert.assertArrayEquals("-_A=".getBytes(), Base64Utils.encodeUrlSafe(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeUrlSafe(Base64Utils.encodeUrlSafe(bytes)));
    }

    @Test
    public void encodeToStringWithJdk8VsJaxb() throws UnsupportedEncodingException {
        byte[] bytes = new byte[]{ -79, 10, -115, -79, 100, -32, 117, 65, 5, -73, -87, -101, -25, 46, 63, -27 };
        Assert.assertEquals(Base64Utils.encodeToString(bytes), DatatypeConverter.printBase64Binary(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeFromString(Base64Utils.encodeToString(bytes)));
        Assert.assertArrayEquals(bytes, DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(bytes)));
        bytes = "Hello World".getBytes("UTF-8");
        Assert.assertEquals(Base64Utils.encodeToString(bytes), DatatypeConverter.printBase64Binary(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeFromString(Base64Utils.encodeToString(bytes)));
        Assert.assertArrayEquals(bytes, DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(bytes)));
        bytes = "Hello World\r\nSecond Line".getBytes("UTF-8");
        Assert.assertEquals(Base64Utils.encodeToString(bytes), DatatypeConverter.printBase64Binary(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeFromString(Base64Utils.encodeToString(bytes)));
        Assert.assertArrayEquals(bytes, DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(bytes)));
        bytes = "Hello World\r\nSecond Line\r\n".getBytes("UTF-8");
        Assert.assertEquals(Base64Utils.encodeToString(bytes), DatatypeConverter.printBase64Binary(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeFromString(Base64Utils.encodeToString(bytes)));
        Assert.assertArrayEquals(bytes, DatatypeConverter.parseBase64Binary(DatatypeConverter.printBase64Binary(bytes)));
    }

    @Test
    public void encodeDecodeUrlSafe() {
        byte[] bytes = new byte[]{ ((byte) (251)), ((byte) (240)) };
        Assert.assertArrayEquals("-_A=".getBytes(), Base64Utils.encodeUrlSafe(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeUrlSafe(Base64Utils.encodeUrlSafe(bytes)));
        Assert.assertEquals("-_A=", Base64Utils.encodeToUrlSafeString(bytes));
        Assert.assertArrayEquals(bytes, Base64Utils.decodeFromUrlSafeString(Base64Utils.encodeToUrlSafeString(bytes)));
    }
}

