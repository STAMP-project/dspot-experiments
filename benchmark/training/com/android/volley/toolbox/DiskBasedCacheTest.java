/**
 * Copyright (C) 2013 The Android Open Source Project
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
package com.android.volley.toolbox;


import Cache.Entry;
import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache.CacheHeader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DiskBasedCacheTest {
    // Simple end-to-end serialize/deserialize test.
    @Test
    public void cacheHeaderSerialization() throws Exception {
        Cache.Entry e = new Cache.Entry();
        e.data = new byte[8];
        e.serverDate = 1234567L;
        e.lastModified = 13572468L;
        e.ttl = 9876543L;
        e.softTtl = 8765432L;
        e.etag = "etag";
        e.responseHeaders = new HashMap<String, String>();
        e.responseHeaders.put("fruit", "banana");
        CacheHeader first = new CacheHeader("my-magical-key", e);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        first.writeHeader(baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CacheHeader second = CacheHeader.readHeader(bais);
        Assert.assertEquals(first.key, second.key);
        Assert.assertEquals(first.serverDate, second.serverDate);
        Assert.assertEquals(first.lastModified, second.lastModified);
        Assert.assertEquals(first.ttl, second.ttl);
        Assert.assertEquals(first.softTtl, second.softTtl);
        Assert.assertEquals(first.etag, second.etag);
        Assert.assertEquals(first.responseHeaders, second.responseHeaders);
    }

    @Test
    public void serializeInt() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiskBasedCache.writeInt(baos, 0);
        DiskBasedCache.writeInt(baos, 19791214);
        DiskBasedCache.writeInt(baos, (-20050711));
        DiskBasedCache.writeInt(baos, Integer.MIN_VALUE);
        DiskBasedCache.writeInt(baos, Integer.MAX_VALUE);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Assert.assertEquals(DiskBasedCache.readInt(bais), 0);
        Assert.assertEquals(DiskBasedCache.readInt(bais), 19791214);
        Assert.assertEquals(DiskBasedCache.readInt(bais), (-20050711));
        Assert.assertEquals(DiskBasedCache.readInt(bais), Integer.MIN_VALUE);
        Assert.assertEquals(DiskBasedCache.readInt(bais), Integer.MAX_VALUE);
    }

    @Test
    public void serializeLong() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiskBasedCache.writeLong(baos, 0);
        DiskBasedCache.writeLong(baos, 31337);
        DiskBasedCache.writeLong(baos, (-4160));
        DiskBasedCache.writeLong(baos, 4295032832L);
        DiskBasedCache.writeLong(baos, (-4314824046L));
        DiskBasedCache.writeLong(baos, Long.MIN_VALUE);
        DiskBasedCache.writeLong(baos, Long.MAX_VALUE);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Assert.assertEquals(DiskBasedCache.readLong(bais), 0);
        Assert.assertEquals(DiskBasedCache.readLong(bais), 31337);
        Assert.assertEquals(DiskBasedCache.readLong(bais), (-4160));
        Assert.assertEquals(DiskBasedCache.readLong(bais), 4295032832L);
        Assert.assertEquals(DiskBasedCache.readLong(bais), (-4314824046L));
        Assert.assertEquals(DiskBasedCache.readLong(bais), Long.MIN_VALUE);
        Assert.assertEquals(DiskBasedCache.readLong(bais), Long.MAX_VALUE);
    }

    @Test
    public void serializeString() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DiskBasedCache.writeString(baos, "");
        DiskBasedCache.writeString(baos, "This is a string.");
        DiskBasedCache.writeString(baos, "?????");
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Assert.assertEquals(DiskBasedCache.readString(bais), "");
        Assert.assertEquals(DiskBasedCache.readString(bais), "This is a string.");
        Assert.assertEquals(DiskBasedCache.readString(bais), "?????");
    }

    @Test
    public void serializeMap() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Map<String, String> empty = new HashMap<String, String>();
        DiskBasedCache.writeStringStringMap(empty, baos);
        DiskBasedCache.writeStringStringMap(null, baos);
        Map<String, String> twoThings = new HashMap<String, String>();
        twoThings.put("first", "thing");
        twoThings.put("second", "item");
        DiskBasedCache.writeStringStringMap(twoThings, baos);
        Map<String, String> emptyKey = new HashMap<String, String>();
        emptyKey.put("", "value");
        DiskBasedCache.writeStringStringMap(emptyKey, baos);
        Map<String, String> emptyValue = new HashMap<String, String>();
        emptyValue.put("key", "");
        DiskBasedCache.writeStringStringMap(emptyValue, baos);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Assert.assertEquals(DiskBasedCache.readStringStringMap(bais), empty);
        Assert.assertEquals(DiskBasedCache.readStringStringMap(bais), empty);// null reads back empty

        Assert.assertEquals(DiskBasedCache.readStringStringMap(bais), twoThings);
        Assert.assertEquals(DiskBasedCache.readStringStringMap(bais), emptyKey);
        Assert.assertEquals(DiskBasedCache.readStringStringMap(bais), emptyValue);
    }

    @Test
    public void publicMethods() throws Exception {
        // Catch-all test to find API-breaking changes.
        Assert.assertNotNull(DiskBasedCache.class.getConstructor(File.class, int.class));
        Assert.assertNotNull(DiskBasedCache.class.getConstructor(File.class));
        Assert.assertNotNull(DiskBasedCache.class.getMethod("getFileForKey", String.class));
    }
}

