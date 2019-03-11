/**
 * Copyright 2016 Google LLC
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
package com.google.cloud.compute.deprecated;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class MetadataTest {
    private static final Metadata METADATA = Metadata.newBuilder().add("key1", "value1").add("key2", "value2").build();

    @Test
    public void testToBuilder() {
        Metadata metadata = MetadataTest.METADATA.toBuilder().setFingerprint("newFingerprint").build();
        Assert.assertEquals("newFingerprint", metadata.getFingerprint());
        compareMetadata(MetadataTest.METADATA, metadata.toBuilder().setFingerprint(null).build());
    }

    @Test
    public void testBuilder() {
        Assert.assertEquals(ImmutableMap.of("key1", "value1", "key2", "value2"), MetadataTest.METADATA.getValues());
        Assert.assertNull(MetadataTest.METADATA.getFingerprint());
        Metadata metadata = Metadata.newBuilder().setValues(ImmutableMap.of("key1", "value1", "key2", "value2")).build();
        Assert.assertEquals(ImmutableMap.of("key1", "value1", "key2", "value2"), metadata.getValues());
        Assert.assertNull(metadata.getFingerprint());
        metadata = Metadata.newBuilder().setValues(ImmutableMap.of("key1", "value1", "key2", "value2")).setFingerprint("fingerprint").build();
        Assert.assertEquals(ImmutableMap.of("key1", "value1", "key2", "value2"), metadata.getValues());
        Assert.assertEquals("fingerprint", metadata.getFingerprint());
    }

    @Test
    public void testOf() {
        Map<String, String> map = ImmutableMap.of("key1", "value1", "key2", "value2");
        compareMetadata(MetadataTest.METADATA, Metadata.of(map));
    }

    @Test
    public void testToAndFromPb() {
        compareMetadata(MetadataTest.METADATA, Metadata.fromPb(MetadataTest.METADATA.toPb()));
        Metadata metadata = Metadata.newBuilder().setValues(ImmutableMap.of("key1", "value1", "key2", "value2")).setFingerprint("fingerprint").build();
        compareMetadata(metadata, Metadata.fromPb(metadata.toPb()));
    }
}

