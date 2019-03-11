/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
/**
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linecorp.armeria.common;


import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.testing.NullPointerTester;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;

import static com.google.common.collect.ImmutableMultimap.of;


/**
 * Tests for {@link MediaType}.
 *
 * @author Gregory Kick
 */
public class MediaTypeTest {
    // Forked from Guava at abd0906f6ed288671b75aa6f828a6ba904fe4477 (24.0)
    // reflection
    @Test
    public void testParse_useConstants() throws Exception {
        for (MediaType constant : MediaTypeTest.getConstants()) {
            Assert.assertSame(constant, MediaType.parse(constant.toString()));
        }
    }

    // reflection
    @Test
    public void testCreate_useConstants() throws Exception {
        for (MediaType constant : MediaTypeTest.getConstants()) {
            Assert.assertSame(constant, MediaType.create(constant.type(), constant.subtype()).withParameters(constant.parameters()));
        }
    }

    // reflection
    @Test
    public void testConstants_charset() throws Exception {
        for (Field field : MediaTypeTest.getConstantFields()) {
            Optional<Charset> charset = charset();
            if (field.getName().endsWith("_UTF_8")) {
                assertThat(charset).hasValue(Charsets.UTF_8);
            } else {
                assertThat(charset).isEmpty();
            }
        }
    }

    // reflection
    @Test
    public void testConstants_areUnique() {
        assertThat(MediaTypeTest.getConstants()).doesNotHaveDuplicates();
    }

    @Test
    public void testCreate_invalidType() {
        try {
            MediaType.create("te><t", "plaintext");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCreate_invalidSubtype() {
        try {
            MediaType.create("text", "pl@intext");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCreate_wildcardTypeDeclaredSubtype() {
        try {
            MediaType.create("*", "text");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testCreateApplicationType() {
        MediaType newType = MediaType.createApplicationType("yams");
        Assert.assertEquals("application", newType.type());
        Assert.assertEquals("yams", newType.subtype());
    }

    @Test
    public void testCreateAudioType() {
        MediaType newType = MediaType.createAudioType("yams");
        Assert.assertEquals("audio", newType.type());
        Assert.assertEquals("yams", newType.subtype());
    }

    @Test
    public void testCreateImageType() {
        MediaType newType = MediaType.createImageType("yams");
        Assert.assertEquals("image", newType.type());
        Assert.assertEquals("yams", newType.subtype());
    }

    @Test
    public void testCreateTextType() {
        MediaType newType = MediaType.createTextType("yams");
        Assert.assertEquals("text", newType.type());
        Assert.assertEquals("yams", newType.subtype());
    }

    @Test
    public void testCreateVideoType() {
        MediaType newType = MediaType.createVideoType("yams");
        Assert.assertEquals("video", newType.type());
        Assert.assertEquals("yams", newType.subtype());
    }

    @Test
    public void testGetType() {
        Assert.assertEquals("text", MediaType.parse("text/plain").type());
        Assert.assertEquals("application", MediaType.parse("application/atom+xml; charset=utf-8").type());
    }

    @Test
    public void testGetSubtype() {
        Assert.assertEquals("plain", MediaType.parse("text/plain").subtype());
        Assert.assertEquals("atom+xml", MediaType.parse("application/atom+xml; charset=utf-8").subtype());
    }

    private static final Map<String, Collection<String>> PARAMETERS = ImmutableListMultimap.of("a", "1", "a", "2", "b", "3").asMap();

    @Test
    public void testGetParameters() {
        Assert.assertEquals(ImmutableMap.of(), MediaType.parse("text/plain").parameters());
        Assert.assertEquals(ImmutableMap.of("charset", ImmutableList.of("utf-8")), MediaType.parse("application/atom+xml; charset=utf-8").parameters());
        Assert.assertEquals(MediaTypeTest.PARAMETERS, MediaType.parse("application/atom+xml; a=1; a=2; b=3").parameters());
    }

    @Test
    public void testWithoutParameters() {
        Assert.assertSame(MediaType.parse("image/gif"), MediaType.parse("image/gif").withoutParameters());
        Assert.assertEquals(MediaType.parse("image/gif"), MediaType.parse("image/gif; foo=bar").withoutParameters());
    }

    @Test
    public void testWithParameters() {
        Assert.assertEquals(MediaType.parse("text/plain; a=1; a=2; b=3"), MediaType.parse("text/plain").withParameters(MediaTypeTest.PARAMETERS));
        Assert.assertEquals(MediaType.parse("text/plain; a=1; a=2; b=3"), MediaType.parse("text/plain; a=1; a=2; b=3").withParameters(MediaTypeTest.PARAMETERS));
    }

    @Test
    public void testWithParameters_invalidAttribute() {
        MediaType mediaType = MediaType.parse("text/plain");
        ImmutableListMultimap<String, String> parameters = ImmutableListMultimap.of("a", "1", "@", "2", "b", "3");
        try {
            mediaType.withParameters(parameters.asMap());
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWithParameter() {
        Assert.assertEquals(MediaType.parse("text/plain; a=1"), MediaType.parse("text/plain").withParameter("a", "1"));
        Assert.assertEquals(MediaType.parse("text/plain; a=1"), MediaType.parse("text/plain; a=1; a=2").withParameter("a", "1"));
        Assert.assertEquals(MediaType.parse("text/plain; a=3"), MediaType.parse("text/plain; a=1; a=2").withParameter("a", "3"));
        Assert.assertEquals(MediaType.parse("text/plain; a=1; a=2; b=3"), MediaType.parse("text/plain; a=1; a=2").withParameter("b", "3"));
    }

    @Test
    public void testWithParameter_invalidAttribute() {
        MediaType mediaType = MediaType.parse("text/plain");
        try {
            mediaType.withParameter("@", "2");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWithParametersIterable() {
        Assert.assertEquals(MediaType.parse("text/plain"), MediaType.parse("text/plain; a=1; a=2").withParameters("a", ImmutableSet.of()));
        Assert.assertEquals(MediaType.parse("text/plain; a=1"), MediaType.parse("text/plain").withParameters("a", ImmutableSet.of("1")));
        Assert.assertEquals(MediaType.parse("text/plain; a=1"), MediaType.parse("text/plain; a=1; a=2").withParameters("a", ImmutableSet.of("1")));
        Assert.assertEquals(MediaType.parse("text/plain; a=1; a=3"), MediaType.parse("text/plain; a=1; a=2").withParameters("a", ImmutableSet.of("1", "3")));
        Assert.assertEquals(MediaType.parse("text/plain; a=1; a=2; b=3; b=4"), MediaType.parse("text/plain; a=1; a=2").withParameters("b", ImmutableSet.of("3", "4")));
    }

    @Test
    public void testWithParametersIterable_invalidAttribute() {
        MediaType mediaType = MediaType.parse("text/plain");
        try {
            mediaType.withParameters("@", ImmutableSet.of("2"));
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testWithParametersIterable_nullValue() {
        MediaType mediaType = MediaType.parse("text/plain");
        try {
            mediaType.withParameters("a", Collections.singletonList(null));
            Assert.fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void testWithCharset() {
        Assert.assertEquals(MediaType.parse("text/plain; charset=utf-8"), MediaType.parse("text/plain").withCharset(Charsets.UTF_8));
        Assert.assertEquals(MediaType.parse("text/plain; charset=utf-8"), MediaType.parse("text/plain; charset=utf-16").withCharset(Charsets.UTF_8));
    }

    @Test
    public void testHasWildcard() {
        Assert.assertFalse(MediaType.PLAIN_TEXT_UTF_8.hasWildcard());
        Assert.assertFalse(MediaType.JPEG.hasWildcard());
        Assert.assertTrue(MediaType.ANY_TYPE.hasWildcard());
        Assert.assertTrue(MediaType.ANY_APPLICATION_TYPE.hasWildcard());
        Assert.assertTrue(MediaType.ANY_AUDIO_TYPE.hasWildcard());
        Assert.assertTrue(MediaType.ANY_IMAGE_TYPE.hasWildcard());
        Assert.assertTrue(MediaType.ANY_TEXT_TYPE.hasWildcard());
        Assert.assertTrue(MediaType.ANY_VIDEO_TYPE.hasWildcard());
    }

    @Test
    public void testIs() {
        Assert.assertTrue(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.ANY_TYPE));
        Assert.assertTrue(MediaType.JPEG.is(MediaType.ANY_TYPE));
        Assert.assertTrue(MediaType.ANY_TEXT_TYPE.is(MediaType.ANY_TYPE));
        Assert.assertTrue(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.ANY_TEXT_TYPE));
        Assert.assertTrue(MediaType.PLAIN_TEXT_UTF_8.withoutParameters().is(MediaType.ANY_TEXT_TYPE));
        Assert.assertFalse(MediaType.JPEG.is(MediaType.ANY_TEXT_TYPE));
        Assert.assertTrue(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.PLAIN_TEXT_UTF_8));
        Assert.assertTrue(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.PLAIN_TEXT_UTF_8.withoutParameters()));
        Assert.assertFalse(MediaType.PLAIN_TEXT_UTF_8.withoutParameters().is(MediaType.PLAIN_TEXT_UTF_8));
        Assert.assertFalse(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.HTML_UTF_8));
        Assert.assertFalse(MediaType.PLAIN_TEXT_UTF_8.withParameter("charset", "UTF-16").is(MediaType.PLAIN_TEXT_UTF_8));
        Assert.assertFalse(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.PLAIN_TEXT_UTF_8.withParameter("charset", "UTF-16")));
    }

    @Test
    public void testBelongsTo() {
        // For quality factor, "belongsTo" has a different behavior to "is".
        assertThat(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.ANY_TYPE.withParameter("q", "0.9"))).isFalse();
        assertThat(MediaType.PLAIN_TEXT_UTF_8.belongsTo(MediaType.ANY_TYPE.withParameter("q", "0.9"))).isTrue();
        // For the other parameters, "belongsTo" has the same behavior as "is".
        assertThat(MediaType.PLAIN_TEXT_UTF_8.is(MediaType.ANY_TYPE.withParameter("charset", "UTF-16"))).isFalse();
        assertThat(MediaType.PLAIN_TEXT_UTF_8.belongsTo(MediaType.ANY_TYPE.withParameter("charset", "UTF-16"))).isFalse();
    }

    @Test
    public void testParse_empty() {
        try {
            MediaType.parse("");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testParse_badInput() {
        try {
            MediaType.parse("/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("te<t/plain");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/pl@in");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain;");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; ");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=@");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=\"@");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1;");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; ");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; b");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=1; b=");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            MediaType.parse("text/plain; a=\u2025");
            Assert.fail();
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testGetCharset() {
        assertThat(MediaType.parse("text/plain").charset()).isEmpty();
        assertThat(MediaType.parse("text/plain; charset=utf-8").charset()).hasValue(Charsets.UTF_8);
    }

    // Non-UTF-8 Charset
    @Test
    public void testGetCharset_utf16() {
        assertThat(MediaType.parse("text/plain; charset=utf-16").charset()).hasValue(Charsets.UTF_16);
    }

    @Test
    public void testGetCharset_tooMany() {
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8; charset=utf-16");
        try {
            mediaType.charset();
            Assert.fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    public void testGetCharset_illegalCharset() {
        MediaType mediaType = MediaType.parse("text/plain; charset=\"!@#$%^&*()\"");
        try {
            mediaType.charset();
            Assert.fail();
        } catch (IllegalCharsetNameException expected) {
        }
    }

    @Test
    public void testGetCharset_unsupportedCharset() {
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-wtf");
        try {
            mediaType.charset();
            Assert.fail();
        } catch (UnsupportedCharsetException expected) {
        }
    }

    @Test
    public void testEquals() {
        new com.google.common.testing.EqualsTester().addEqualityGroup(MediaType.create("text", "plain"), MediaType.create("TEXT", "PLAIN"), MediaType.parse("text/plain"), MediaType.parse("TEXT/PLAIN"), MediaType.create("text", "plain").withParameter("a", "1").withoutParameters()).addEqualityGroup(MediaType.create("text", "plain").withCharset(Charsets.UTF_8), MediaType.create("text", "plain").withParameter("CHARSET", "UTF-8"), MediaType.create("text", "plain").withParameters(of("charset", "utf-8").asMap()), MediaType.parse("text/plain;charset=utf-8"), MediaType.parse("text/plain; charset=utf-8"), MediaType.parse("text/plain;  charset=utf-8"), MediaType.parse("text/plain; \tcharset=utf-8"), MediaType.parse("text/plain; \r\n\tcharset=utf-8"), MediaType.parse("text/plain; CHARSET=utf-8"), MediaType.parse("text/plain; charset=\"utf-8\""), MediaType.parse("text/plain; charset=\"\\u\\tf-\\8\""), MediaType.parse("text/plain; charset=UTF-8"), MediaType.parse("text/plain ; charset=utf-8")).addEqualityGroup(MediaType.parse("text/plain; charset=utf-8; charset=utf-8")).addEqualityGroup(MediaType.create("text", "plain").withParameter("a", "value"), MediaType.create("text", "plain").withParameter("A", "value")).addEqualityGroup(MediaType.create("text", "plain").withParameter("a", "VALUE"), MediaType.create("text", "plain").withParameter("A", "VALUE")).addEqualityGroup(MediaType.create("text", "plain").withParameters(ImmutableListMultimap.of("a", "1", "a", "2").asMap()), MediaType.create("text", "plain").withParameters(ImmutableListMultimap.of("a", "2", "a", "1").asMap())).addEqualityGroup(MediaType.create("text", "csv")).addEqualityGroup(MediaType.create("application", "atom+xml")).testEquals();
    }

    // Non-UTF-8 Charset
    @Test
    public void testEquals_nonUtf8Charsets() {
        new com.google.common.testing.EqualsTester().addEqualityGroup(MediaType.create("text", "plain")).addEqualityGroup(MediaType.create("text", "plain").withCharset(Charsets.UTF_8)).addEqualityGroup(MediaType.create("text", "plain").withCharset(Charsets.UTF_16)).testEquals();
    }

    // com.google.common.testing.NullPointerTester
    @Test
    public void testNullPointer() {
        NullPointerTester tester = new NullPointerTester();
        tester.testAllPublicConstructors(MediaType.class);
        tester.testAllPublicStaticMethods(MediaType.class);
        tester.testAllPublicInstanceMethods(MediaType.parse("text/plain"));
    }

    @Test
    public void testToString() {
        Assert.assertEquals("text/plain", MediaType.create("text", "plain").toString());
        Assert.assertEquals("text/plain; something=\"cr@zy\"; something-else=\"crazy with spaces\"", MediaType.create("text", "plain").withParameter("something", "cr@zy").withParameter("something-else", "crazy with spaces").toString());
    }
}

