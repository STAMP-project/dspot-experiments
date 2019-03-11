/**
 * Copyright 2015 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.datastore;


import com.google.cloud.Timestamp;
import org.junit.Assert;
import org.junit.Test;


public class ProjectionEntityTest {
    private static final Key KEY = Key.newBuilder("ds1", "k1", "n1").build();

    private static final StringValue STRING_INDEX_VALUE = StringValue.newBuilder("foo").setMeaning(18).build();

    private static final BlobValue BLOB_VALUE = BlobValue.of(Blob.copyFrom(new byte[]{ 1 }));

    private static final TimestampValue TIMESTAMP_VALUE = TimestampValue.of(Timestamp.now());

    private static final LongValue LONG_INDEX_VALUE = LongValue.newBuilder((((ProjectionEntityTest.TIMESTAMP_VALUE.get().getSeconds()) * 1000000) + ((ProjectionEntityTest.TIMESTAMP_VALUE.get().getNanos()) / 1000))).setMeaning(18).build();

    private static final ProjectionEntity ENTITY1 = new ProjectionEntity.Builder().setKey(ProjectionEntityTest.KEY).set("a", "b").build();

    private static final ProjectionEntity ENTITY2 = new ProjectionEntity.Builder().set("a", ProjectionEntityTest.STRING_INDEX_VALUE).set("b", ProjectionEntityTest.BLOB_VALUE).set("c", ProjectionEntityTest.TIMESTAMP_VALUE).set("d", ProjectionEntityTest.LONG_INDEX_VALUE).build();

    @Test
    public void testHasKey() throws Exception {
        Assert.assertTrue(ProjectionEntityTest.ENTITY1.hasKey());
        Assert.assertFalse(ProjectionEntityTest.ENTITY2.hasKey());
    }

    @Test
    public void testKey() throws Exception {
        Assert.assertEquals(ProjectionEntityTest.KEY, ProjectionEntityTest.ENTITY1.getKey());
        Assert.assertNull(ProjectionEntityTest.ENTITY2.getKey());
    }

    @Test
    public void testGetBlob() throws Exception {
        Assert.assertArrayEquals(ProjectionEntityTest.STRING_INDEX_VALUE.get().getBytes(), ProjectionEntityTest.ENTITY2.getBlob("a").toByteArray());
        Assert.assertEquals(ProjectionEntityTest.BLOB_VALUE.get(), ProjectionEntityTest.ENTITY2.getBlob("b"));
    }

    @Test
    public void testGetTimestamp() throws Exception {
        Assert.assertEquals(ProjectionEntityTest.TIMESTAMP_VALUE.get(), ProjectionEntityTest.ENTITY2.getTimestamp("c"));
        Assert.assertEquals(ProjectionEntityTest.TIMESTAMP_VALUE.get(), ProjectionEntityTest.ENTITY2.getTimestamp("d"));
    }
}

