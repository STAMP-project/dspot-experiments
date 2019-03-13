/**
 * Copyright (C) 2014 Square, Inc.
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
package okhttp3;


import CacheControl.Builder;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public final class CacheControlTest {
    @Test
    public void emptyBuilderIsEmpty() throws Exception {
        CacheControl cacheControl = new CacheControl.Builder().build();
        Assert.assertEquals("", cacheControl.toString());
        Assert.assertFalse(cacheControl.noCache());
        Assert.assertFalse(cacheControl.noStore());
        Assert.assertEquals((-1), cacheControl.maxAgeSeconds());
        Assert.assertEquals((-1), cacheControl.sMaxAgeSeconds());
        Assert.assertFalse(cacheControl.isPrivate());
        Assert.assertFalse(cacheControl.isPublic());
        Assert.assertFalse(cacheControl.mustRevalidate());
        Assert.assertEquals((-1), cacheControl.maxStaleSeconds());
        Assert.assertEquals((-1), cacheControl.minFreshSeconds());
        Assert.assertFalse(cacheControl.onlyIfCached());
        Assert.assertFalse(cacheControl.mustRevalidate());
    }

    @Test
    public void completeBuilder() throws Exception {
        CacheControl cacheControl = new CacheControl.Builder().noCache().noStore().maxAge(1, TimeUnit.SECONDS).maxStale(2, TimeUnit.SECONDS).minFresh(3, TimeUnit.SECONDS).onlyIfCached().noTransform().immutable().build();
        Assert.assertEquals(("no-cache, no-store, max-age=1, max-stale=2, min-fresh=3, only-if-cached, " + "no-transform, immutable"), cacheControl.toString());
        Assert.assertTrue(cacheControl.noCache());
        Assert.assertTrue(cacheControl.noStore());
        Assert.assertEquals(1, cacheControl.maxAgeSeconds());
        Assert.assertEquals(2, cacheControl.maxStaleSeconds());
        Assert.assertEquals(3, cacheControl.minFreshSeconds());
        Assert.assertTrue(cacheControl.onlyIfCached());
        Assert.assertTrue(cacheControl.noTransform());
        Assert.assertTrue(cacheControl.immutable());
        // These members are accessible to response headers only.
        Assert.assertEquals((-1), cacheControl.sMaxAgeSeconds());
        Assert.assertFalse(cacheControl.isPrivate());
        Assert.assertFalse(cacheControl.isPublic());
        Assert.assertFalse(cacheControl.mustRevalidate());
    }

    @Test
    public void parseEmpty() throws Exception {
        CacheControl cacheControl = CacheControl.parse(new Headers.Builder().set("Cache-Control", "").build());
        Assert.assertEquals("", cacheControl.toString());
        Assert.assertFalse(cacheControl.noCache());
        Assert.assertFalse(cacheControl.noStore());
        Assert.assertEquals((-1), cacheControl.maxAgeSeconds());
        Assert.assertEquals((-1), cacheControl.sMaxAgeSeconds());
        Assert.assertFalse(cacheControl.isPublic());
        Assert.assertFalse(cacheControl.mustRevalidate());
        Assert.assertEquals((-1), cacheControl.maxStaleSeconds());
        Assert.assertEquals((-1), cacheControl.minFreshSeconds());
        Assert.assertFalse(cacheControl.onlyIfCached());
        Assert.assertFalse(cacheControl.mustRevalidate());
    }

    @Test
    public void parse() throws Exception {
        String header = "no-cache, no-store, max-age=1, s-maxage=2, private, public, must-revalidate, " + "max-stale=3, min-fresh=4, only-if-cached, no-transform";
        CacheControl cacheControl = CacheControl.parse(new Headers.Builder().set("Cache-Control", header).build());
        Assert.assertTrue(cacheControl.noCache());
        Assert.assertTrue(cacheControl.noStore());
        Assert.assertEquals(1, cacheControl.maxAgeSeconds());
        Assert.assertEquals(2, cacheControl.sMaxAgeSeconds());
        Assert.assertTrue(cacheControl.isPrivate());
        Assert.assertTrue(cacheControl.isPublic());
        Assert.assertTrue(cacheControl.mustRevalidate());
        Assert.assertEquals(3, cacheControl.maxStaleSeconds());
        Assert.assertEquals(4, cacheControl.minFreshSeconds());
        Assert.assertTrue(cacheControl.onlyIfCached());
        Assert.assertTrue(cacheControl.noTransform());
        Assert.assertEquals(header, cacheControl.toString());
    }

    @Test
    public void parseIgnoreCacheControlExtensions() throws Exception {
        // Example from http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.6
        String header = "private, community=\"UCI\"";
        CacheControl cacheControl = CacheControl.parse(new Headers.Builder().set("Cache-Control", header).build());
        Assert.assertFalse(cacheControl.noCache());
        Assert.assertFalse(cacheControl.noStore());
        Assert.assertEquals((-1), cacheControl.maxAgeSeconds());
        Assert.assertEquals((-1), cacheControl.sMaxAgeSeconds());
        Assert.assertTrue(cacheControl.isPrivate());
        Assert.assertFalse(cacheControl.isPublic());
        Assert.assertFalse(cacheControl.mustRevalidate());
        Assert.assertEquals((-1), cacheControl.maxStaleSeconds());
        Assert.assertEquals((-1), cacheControl.minFreshSeconds());
        Assert.assertFalse(cacheControl.onlyIfCached());
        Assert.assertFalse(cacheControl.noTransform());
        Assert.assertFalse(cacheControl.immutable());
        Assert.assertEquals(header, cacheControl.toString());
    }

    @Test
    public void parseCacheControlAndPragmaAreCombined() {
        Headers headers = Headers.of("Cache-Control", "max-age=12", "Pragma", "must-revalidate", "Pragma", "public");
        CacheControl cacheControl = CacheControl.parse(headers);
        Assert.assertEquals("max-age=12, public, must-revalidate", cacheControl.toString());
    }

    // Testing instance equality.
    @SuppressWarnings("RedundantStringConstructorCall")
    @Test
    public void parseCacheControlHeaderValueIsRetained() {
        String value = new String("max-age=12");
        Headers headers = Headers.of("Cache-Control", value);
        CacheControl cacheControl = CacheControl.parse(headers);
        Assert.assertSame(value, cacheControl.toString());
    }

    @Test
    public void parseCacheControlHeaderValueInvalidatedByPragma() {
        Headers headers = Headers.of("Cache-Control", "max-age=12", "Pragma", "must-revalidate");
        CacheControl cacheControl = CacheControl.parse(headers);
        Assert.assertNull(cacheControl.headerValue);
    }

    @Test
    public void parseCacheControlHeaderValueInvalidatedByTwoValues() {
        Headers headers = Headers.of("Cache-Control", "max-age=12", "Cache-Control", "must-revalidate");
        CacheControl cacheControl = CacheControl.parse(headers);
        Assert.assertNull(cacheControl.headerValue);
    }

    @Test
    public void parsePragmaHeaderValueIsNotRetained() {
        Headers headers = Headers.of("Pragma", "must-revalidate");
        CacheControl cacheControl = CacheControl.parse(headers);
        Assert.assertNull(cacheControl.headerValue);
    }

    @Test
    public void computedHeaderValueIsCached() {
        CacheControl cacheControl = new CacheControl.Builder().maxAge(2, TimeUnit.DAYS).build();
        Assert.assertNull(cacheControl.headerValue);
        Assert.assertEquals("max-age=172800", cacheControl.toString());
        Assert.assertEquals("max-age=172800", cacheControl.headerValue);
        cacheControl.headerValue = "Hi";
        Assert.assertEquals("Hi", cacheControl.toString());
    }

    @Test
    public void timeDurationTruncatedToMaxValue() throws Exception {
        CacheControl cacheControl = // Longer than Integer.MAX_VALUE seconds.
        new CacheControl.Builder().maxAge((365 * 100), TimeUnit.DAYS).build();
        Assert.assertEquals(Integer.MAX_VALUE, cacheControl.maxAgeSeconds());
    }

    @Test
    public void secondsMustBeNonNegative() throws Exception {
        CacheControl.Builder builder = new CacheControl.Builder();
        try {
            builder.maxAge((-1), TimeUnit.SECONDS);
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void timePrecisionIsTruncatedToSeconds() throws Exception {
        CacheControl cacheControl = new CacheControl.Builder().maxAge(4999, TimeUnit.MILLISECONDS).build();
        Assert.assertEquals(4, cacheControl.maxAgeSeconds());
    }
}

