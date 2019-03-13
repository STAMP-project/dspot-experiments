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


import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;


public class TagsTest {
    private static final Tags TAGS = Tags.of("tag1", "tag2");

    @Test
    public void testToBuilder() {
        Tags tags = TagsTest.TAGS.toBuilder().setValues("tag1").build();
        Assert.assertEquals(ImmutableList.of("tag1"), tags.getValues());
        compareTags(TagsTest.TAGS, tags.toBuilder().setValues("tag1", "tag2").build());
    }

    @Test
    public void testBuilder() {
        Tags tags = Tags.newBuilder().setValues(ImmutableList.of("tag1", "tag2")).build();
        Assert.assertEquals(ImmutableList.of("tag1", "tag2"), tags.getValues());
        Assert.assertNull(tags.getFingerprint());
        tags = Tags.newBuilder().add("tag1").add("tag2").build();
        Assert.assertEquals(ImmutableList.of("tag1", "tag2"), tags.getValues());
        Assert.assertNull(tags.getFingerprint());
        tags = Tags.newBuilder().add("tag1").add("tag2").setFingerprint("fingerprint").build();
        Assert.assertEquals(ImmutableList.of("tag1", "tag2"), tags.getValues());
        Assert.assertEquals("fingerprint", tags.getFingerprint());
    }

    @Test
    public void testOf() {
        compareTags(TagsTest.TAGS, Tags.of("tag1", "tag2"));
        compareTags(TagsTest.TAGS, Tags.of(ImmutableList.of("tag1", "tag2")));
    }

    @Test
    public void testToAndFromPb() {
        compareTags(TagsTest.TAGS, Tags.fromPb(TagsTest.TAGS.toPb()));
        Tags tags = Tags.newBuilder().add("tag1").add("tag2").setFingerprint("fingerprint").build();
        compareTags(tags, Tags.fromPb(tags.toPb()));
    }
}

