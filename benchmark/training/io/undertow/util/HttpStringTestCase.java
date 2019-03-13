/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.util;


import Headers.ACCEPT;
import Headers.ACCEPT_CHARSET;
import Headers.CONTENT_TYPE;
import Headers.COOKIE;
import io.undertow.testutils.category.UnitTest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static Headers.ACCEPT_CHARSET_STRING;
import static Headers.ACCEPT_STRING;
import static Headers.CONTENT_TYPE_STRING;
import static Headers.COOKIE_STRING;


/**
 *
 *
 * @author Matej Lazar
 */
@Category(UnitTest.class)
public class HttpStringTestCase {
    @Test
    public void testOrderShorterFirst() {
        HttpString a = new HttpString("a");
        HttpString aa = new HttpString("aa");
        Assert.assertEquals((-1), a.compareTo(aa));
    }

    /**
     * test HttpString.compareTo part: bytes.length - other.bytes.length
     */
    @Test
    public void testCompareShorterFirst() {
        HttpString accept = new HttpString(ACCEPT_STRING);
        Assert.assertEquals(accept.compareTo(ACCEPT_CHARSET), ACCEPT.compareTo(ACCEPT_CHARSET));
        HttpString acceptCharset = new HttpString(ACCEPT_CHARSET_STRING);
        Assert.assertEquals(acceptCharset.compareTo(ACCEPT), ACCEPT_CHARSET.compareTo(ACCEPT));
    }

    /**
     * test HttpString.compareTo part: res = signum(higher(bytes[i]) - higher(other.bytes[i]));
     */
    @Test
    public void testCompare() {
        HttpString contentType = new HttpString(CONTENT_TYPE_STRING);
        Assert.assertEquals(contentType.compareTo(COOKIE), CONTENT_TYPE.compareTo(COOKIE));
        HttpString cookie = new HttpString(COOKIE_STRING);
        Assert.assertEquals(cookie.compareTo(CONTENT_TYPE), COOKIE.compareTo(CONTENT_TYPE));
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream so = new ObjectOutputStream(out);
        HttpString testString = new HttpString("test");
        so.writeObject(testString);
        so.close();
        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(out.toByteArray()));
        Object res = in.readObject();
        Assert.assertEquals(testString, res);
    }
}

