/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http;


import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.EIGHT;
import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.FIVE;
import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.ONE;
import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.SIX_QUOTED;
import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.THREE;
import static io.netty.handler.codec.http.HttpHeadersTestUtils.HeaderValue.TWO;


public class CombinedHttpHeadersTest {
    private static final CharSequence HEADER_NAME = "testHeader";

    @Test
    public void addCharSequencesCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void addCharSequencesCsvWithExistingHeader() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, FIVE.subset(4));
        CombinedHttpHeadersTest.assertCsvValues(headers, FIVE);
    }

    @Test
    public void addCombinedHeadersWhenEmpty() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        final CombinedHttpHeaders otherHeaders = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "a");
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "b");
        headers.add(otherHeaders);
        Assert.assertEquals("a,b", headers.get(CombinedHttpHeadersTest.HEADER_NAME).toString());
    }

    @Test
    public void addCombinedHeadersWhenNotEmpty() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, "a");
        final CombinedHttpHeaders otherHeaders = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "b");
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "c");
        headers.add(otherHeaders);
        Assert.assertEquals("a,b,c", headers.get(CombinedHttpHeadersTest.HEADER_NAME).toString());
    }

    @Test
    public void dontCombineSetCookieHeaders() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(HttpHeaderNames.SET_COOKIE, "a");
        final CombinedHttpHeaders otherHeaders = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        otherHeaders.add(HttpHeaderNames.SET_COOKIE, "b");
        otherHeaders.add(HttpHeaderNames.SET_COOKIE, "c");
        headers.add(otherHeaders);
        Assert.assertThat(headers.getAll(HttpHeaderNames.SET_COOKIE), Matchers.hasSize(3));
    }

    @Test
    public void dontCombineSetCookieHeadersRegardlessOfCase() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add("Set-Cookie", "a");
        final CombinedHttpHeaders otherHeaders = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        otherHeaders.add("set-cookie", "b");
        otherHeaders.add("SET-COOKIE", "c");
        headers.add(otherHeaders);
        Assert.assertThat(headers.getAll(HttpHeaderNames.SET_COOKIE), Matchers.hasSize(3));
    }

    @Test
    public void setCombinedHeadersWhenNotEmpty() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, "a");
        final CombinedHttpHeaders otherHeaders = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "b");
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "c");
        headers.set(otherHeaders);
        Assert.assertEquals("b,c", headers.get(CombinedHttpHeadersTest.HEADER_NAME).toString());
    }

    @Test
    public void addUncombinedHeaders() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, "a");
        final DefaultHttpHeaders otherHeaders = new DefaultHttpHeaders();
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "b");
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "c");
        headers.add(otherHeaders);
        Assert.assertEquals("a,b,c", headers.get(CombinedHttpHeadersTest.HEADER_NAME).toString());
    }

    @Test
    public void setUncombinedHeaders() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, "a");
        final DefaultHttpHeaders otherHeaders = new DefaultHttpHeaders();
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "b");
        otherHeaders.add(CombinedHttpHeadersTest.HEADER_NAME, "c");
        headers.set(otherHeaders);
        Assert.assertEquals("b,c", headers.get(CombinedHttpHeadersTest.HEADER_NAME).toString());
    }

    @Test
    public void addCharSequencesCsvWithValueContainingComma() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, SIX_QUOTED.subset(4));
        Assert.assertTrue(contentEquals(SIX_QUOTED.subsetAsCsvString(4), headers.get(CombinedHttpHeadersTest.HEADER_NAME)));
        Assert.assertEquals(SIX_QUOTED.subset(4), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
    }

    @Test
    public void addCharSequencesCsvWithValueContainingCommas() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, EIGHT.subset(6));
        Assert.assertTrue(contentEquals(EIGHT.subsetAsCsvString(6), headers.get(CombinedHttpHeadersTest.HEADER_NAME)));
        Assert.assertEquals(EIGHT.subset(6), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
    }

    @Test(expected = NullPointerException.class)
    public void addCharSequencesCsvNullValue() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        final String value = null;
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, value);
    }

    @Test
    public void addCharSequencesCsvMultipleTimes() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        for (int i = 0; i < 5; ++i) {
            headers.add(CombinedHttpHeadersTest.HEADER_NAME, "value");
        }
        Assert.assertTrue(contentEquals("value,value,value,value,value", headers.get(CombinedHttpHeadersTest.HEADER_NAME)));
    }

    @Test
    public void addCharSequenceCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        CombinedHttpHeadersTest.addValues(headers, ONE, TWO, THREE);
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void addCharSequenceCsvSingleValue() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        CombinedHttpHeadersTest.addValues(headers, ONE);
        CombinedHttpHeadersTest.assertCsvValue(headers, ONE);
    }

    @Test
    public void addIterableCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void addIterableCsvWithExistingHeader() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, FIVE.subset(4));
        CombinedHttpHeadersTest.assertCsvValues(headers, FIVE);
    }

    @Test
    public void addIterableCsvSingleValue() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, ONE.asList());
        CombinedHttpHeadersTest.assertCsvValue(headers, ONE);
    }

    @Test
    public void addIterableCsvEmpty() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, Collections.<CharSequence>emptyList());
        Assert.assertEquals(Arrays.asList(""), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
    }

    @Test
    public void addObjectCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        CombinedHttpHeadersTest.addObjectValues(headers, ONE, TWO, THREE);
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void addObjectsCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void addObjectsIterableCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void addObjectsCsvWithExistingHeader() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, FIVE.subset(4));
        CombinedHttpHeadersTest.assertCsvValues(headers, FIVE);
    }

    @Test
    public void setCharSequenceCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void setIterableCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void setObjectObjectsCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void setObjectIterableCsv() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, THREE.asList());
        CombinedHttpHeadersTest.assertCsvValues(headers, THREE);
    }

    @Test
    public void testGetAll() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, Arrays.asList("a", "b", "c"));
        Assert.assertEquals(Arrays.asList("a", "b", "c"), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, Arrays.asList("a,", "b,", "c,"));
        Assert.assertEquals(Arrays.asList("a,", "b,", "c,"), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, Arrays.asList("a\"", "b\"", "c\""));
        Assert.assertEquals(Arrays.asList("a\"", "b\"", "c\""), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, Arrays.asList("\"a\"", "\"b\"", "\"c\""));
        Assert.assertEquals(Arrays.asList("a", "b", "c"), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, "a,b,c");
        Assert.assertEquals(Arrays.asList("a,b,c"), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, "\"a,b,c\"");
        Assert.assertEquals(Arrays.asList("a,b,c"), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
    }

    @Test
    public void getAllDontCombineSetCookie() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(HttpHeaderNames.SET_COOKIE, "a");
        headers.add(HttpHeaderNames.SET_COOKIE, "b");
        Assert.assertThat(headers.getAll(HttpHeaderNames.SET_COOKIE), Matchers.hasSize(2));
        Assert.assertEquals(Arrays.asList("a", "b"), headers.getAll(HttpHeaderNames.SET_COOKIE));
    }

    @Test
    public void owsTrimming() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, Arrays.asList("\ta", "   ", "  b ", "\t \t"));
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, " c, d \t");
        Assert.assertEquals(Arrays.asList("a", "", "b", "", "c, d"), headers.getAll(CombinedHttpHeadersTest.HEADER_NAME));
        Assert.assertEquals("a,,b,,\"c, d\"", headers.get(CombinedHttpHeadersTest.HEADER_NAME));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "a", true));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, " a ", true));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "a", true));
        Assert.assertFalse(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "a,b", true));
        Assert.assertFalse(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, " c, d ", true));
        Assert.assertFalse(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "c, d", true));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, " c ", true));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "d", true));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "\t", true));
        Assert.assertTrue(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "", true));
        Assert.assertFalse(headers.containsValue(CombinedHttpHeadersTest.HEADER_NAME, "e", true));
        HttpHeaders copiedHeaders = CombinedHttpHeadersTest.newCombinedHttpHeaders().add(headers);
        Assert.assertEquals(Arrays.asList("a", "", "b", "", "c, d"), copiedHeaders.getAll(CombinedHttpHeadersTest.HEADER_NAME));
    }

    @Test
    public void valueIterator() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.set(CombinedHttpHeadersTest.HEADER_NAME, Arrays.asList("\ta", "   ", "  b ", "\t \t"));
        headers.add(CombinedHttpHeadersTest.HEADER_NAME, " c, d \t");
        Assert.assertFalse(headers.valueStringIterator("foo").hasNext());
        CombinedHttpHeadersTest.assertValueIterator(headers.valueStringIterator(CombinedHttpHeadersTest.HEADER_NAME));
        Assert.assertFalse(headers.valueCharSequenceIterator("foo").hasNext());
        CombinedHttpHeadersTest.assertValueIterator(headers.valueCharSequenceIterator(CombinedHttpHeadersTest.HEADER_NAME));
    }

    @Test
    public void nonCombinableHeaderIterator() {
        final CombinedHttpHeaders headers = CombinedHttpHeadersTest.newCombinedHttpHeaders();
        headers.add(HttpHeaderNames.SET_COOKIE, "c");
        headers.add(HttpHeaderNames.SET_COOKIE, "b");
        headers.add(HttpHeaderNames.SET_COOKIE, "a");
        final Iterator<String> strItr = headers.valueStringIterator(HttpHeaderNames.SET_COOKIE);
        Assert.assertTrue(strItr.hasNext());
        Assert.assertEquals("a", strItr.next());
        Assert.assertTrue(strItr.hasNext());
        Assert.assertEquals("b", strItr.next());
        Assert.assertTrue(strItr.hasNext());
        Assert.assertEquals("c", strItr.next());
    }
}

