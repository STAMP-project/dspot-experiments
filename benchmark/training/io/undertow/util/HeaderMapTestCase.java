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


import Headers.CONNECTION;
import Headers.CONTENT_DISPOSITION;
import Headers.CONTENT_MD5;
import Headers.DEFLATE;
import Headers.FROM;
import Headers.HOST;
import Headers.KEEP_ALIVE;
import Headers.NEGOTIATE;
import Headers.REFERER;
import Headers.RESPONSE_AUTH;
import Headers.TRANSFER_ENCODING;
import Headers.UPGRADE;
import Headers.USER_AGENT;
import io.undertow.testutils.category.UnitTest;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 *
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Category(UnitTest.class)
public final class HeaderMapTestCase {
    private static final List<HttpString> HTTP_STRING_LIST = Arrays.asList(CONNECTION, HOST, UPGRADE, CONTENT_MD5, KEEP_ALIVE, RESPONSE_AUTH, CONTENT_DISPOSITION, DEFLATE, NEGOTIATE, USER_AGENT, REFERER, TRANSFER_ENCODING, FROM);

    @Test
    public void testInitial() {
        final HeaderMap headerMap = new HeaderMap();
        Assert.assertEquals(0, headerMap.size());
        Assert.assertEquals((-1L), headerMap.fastIterate());
        Assert.assertFalse(headerMap.iterator().hasNext());
    }

    @Test
    public void testSimple() {
        final HeaderMap headerMap = new HeaderMap();
        headerMap.add(HOST, "yay.undertow.io");
        Assert.assertTrue(headerMap.contains(HOST));
        Assert.assertTrue(headerMap.contains("host"));
        Assert.assertEquals(1, headerMap.size());
        Assert.assertNotEquals((-1L), headerMap.fastIterate());
        Assert.assertEquals((-1L), headerMap.fiNext(headerMap.fastIterate()));
        Assert.assertEquals(HOST, headerMap.fiCurrent(headerMap.fastIterate()).getHeaderName());
        Assert.assertEquals("yay.undertow.io", headerMap.getFirst(HOST));
        Assert.assertEquals("yay.undertow.io", headerMap.getLast(HOST));
        Assert.assertEquals("yay.undertow.io", headerMap.get(HOST, 0));
        headerMap.remove("host");
        Assert.assertEquals(0, headerMap.size());
    }

    @Test
    public void testGrowing() {
        final HeaderMap headerMap = new HeaderMap();
        for (HttpString item : HeaderMapTestCase.HTTP_STRING_LIST) {
            for (int i = 0; i < (((item.hashCode()) & 7) + 1); i++)
                headerMap.add(item, "Test value");

        }
        for (HttpString item : HeaderMapTestCase.HTTP_STRING_LIST) {
            Assert.assertTrue(String.format("Missing %s (hash %08x)", item, Integer.valueOf(item.hashCode())), headerMap.contains(item));
            Assert.assertNotNull(headerMap.get(item));
            Assert.assertEquals((((item.hashCode()) & 7) + 1), headerMap.get(item).size());
            Assert.assertEquals("Test value", headerMap.getFirst(item));
            Assert.assertEquals("Test value", headerMap.getLast(item));
        }
        Assert.assertEquals(HeaderMapTestCase.HTTP_STRING_LIST.size(), headerMap.size());
        for (HttpString item : HeaderMapTestCase.HTTP_STRING_LIST) {
            Assert.assertTrue(headerMap.contains(item));
            Assert.assertNotNull(headerMap.remove(item));
            Assert.assertFalse(headerMap.contains(item));
        }
        Assert.assertEquals(0, headerMap.size());
    }

    @Test
    public void testCollision() {
        HeaderMap headerMap = new HeaderMap();
        headerMap.put(new HttpString("Link"), "a");
        headerMap.put(new HttpString("Rest"), "b");
        Assert.assertEquals("a", headerMap.getFirst(new HttpString("Link")));
        Assert.assertEquals("b", headerMap.getFirst(new HttpString("Rest")));
        Assert.assertEquals("a", headerMap.getFirst("Link"));
        Assert.assertEquals("b", headerMap.getFirst("Rest"));
    }
}

