/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.util;


import java.nio.ByteBuffer;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for the StringUtils class.
 */
public class StringUtilsTest {
    /**
     * Tests that {@link StringUtils#fromByteBuffer(ByteBuffer)} correctly
     * base64 encodes the contents in a ByteBuffer and returns the correct
     * result.
     */
    @Test
    public void testFromByteBuffer() {
        String expectedData = "hello world";
        String expectedEncodedData = "aGVsbG8gd29ybGQ=";
        ByteBuffer byteBuffer = ByteBuffer.wrap(expectedData.getBytes());
        String encodedData = StringUtils.fromByteBuffer(byteBuffer);
        Assert.assertEquals(expectedEncodedData, encodedData);
    }

    /**
     * Tests that we can correctly convert Bytes to strings.
     */
    @Test
    public void testFromByte() {
        Assert.assertEquals("123", StringUtils.fromByte(new Byte("123")));
        Assert.assertEquals("-99", StringUtils.fromByte(new Byte("-99")));
    }

    @Test
    public void testUTF8Charset() {
        Assert.assertEquals(StringUtils.UTF8.displayName(), "UTF-8");
    }

    /**
     *
     *
     * @see https://github.com/aws/aws-sdk-java/pull/517
     */
    @Test(timeout = 10 * 1000)
    public void replace_ReplacementStringContainsMatchString_DoesNotCauseInfiniteLoop() {
        Assert.assertEquals("aabc", StringUtils.replace("abc", "a", "aa"));
    }

    @Test
    public void replace_EmptyReplacementString_RemovesAllOccurencesOfMatchString() {
        Assert.assertEquals("bbb", StringUtils.replace("ababab", "a", ""));
    }

    @Test
    public void replace_MatchNotFound_ReturnsOriginalString() {
        Assert.assertEquals("abc", StringUtils.replace("abc", "d", "e"));
    }

    @Test
    public void lowerCase_NonEmptyString() {
        String input = "x-amz-InvocAtion-typE";
        String expected = "x-amz-invocation-type";
        Assert.assertEquals(expected, StringUtils.lowerCase(input));
    }

    @Test
    public void lowerCase_NullString() {
        Assert.assertNull(StringUtils.lowerCase(null));
    }

    @Test
    public void lowerCase_EmptyString() {
        Assert.assertThat(StringUtils.lowerCase(""), Matchers.isEmptyString());
    }

    @Test
    public void upperCase_NonEmptyString() {
        String input = "dHkdjj139_)(e";
        String expected = "DHKDJJ139_)(E";
        Assert.assertEquals(expected, StringUtils.upperCase(input));
    }

    @Test
    public void upperCase_NullString() {
        Assert.assertNull(StringUtils.upperCase(null));
    }

    @Test
    public void upperCase_EmptyString() {
        Assert.assertThat(StringUtils.upperCase(""), Matchers.isEmptyString());
    }

    @Test
    public void testCompare() {
        Assert.assertTrue(((StringUtils.compare("truck", "Car")) > 0));
        Assert.assertTrue(((StringUtils.compare("", "dd")) < 0));
        Assert.assertTrue(((StringUtils.compare("dd", "")) > 0));
        Assert.assertEquals(0, StringUtils.compare("", ""));
        Assert.assertTrue(((StringUtils.compare(" ", "")) > 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompare_String1Null() {
        String str1 = null;
        String str2 = "test";
        int result = StringUtils.compare(str1, str2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompare_String2Null() {
        String str1 = "test";
        String str2 = null;
        int result = StringUtils.compare(str1, str2);
    }

    @Test
    public void testAppendAndCompact() {
        String[] pieces = new String[]{ " ", "\t", "\n", "\u000b", "\r", "\f", "word", "foo", "bar", "baq" };
        int ITERATIONS = 10000;
        Random rng = new Random();
        for (int i = 0; i < ITERATIONS; i++) {
            int parts = rng.nextInt(10);
            String s = "";
            for (int j = 0; j < parts; j++) {
                s = s + (pieces[rng.nextInt(pieces.length)]);
            }
            StringBuilder sb = new StringBuilder();
            StringUtils.appendCompactedString(sb, s);
            String compacted = s.replaceAll("\\s+", " ");
            Assert.assertEquals((('[' + compacted) + ']'), sb.toString(), compacted);
        }
    }

    @Test
    public void begins_with_ignore_case() {
        Assert.assertTrue(StringUtils.beginsWithIgnoreCase("foobar", "FoO"));
    }

    @Test
    public void begins_with_ignore_case_returns_false_when_seq_doesnot_match() {
        Assert.assertFalse(StringUtils.beginsWithIgnoreCase("foobar", "baz"));
    }

    @Test
    public void hasValue() {
        Assert.assertTrue(StringUtils.hasValue("something"));
        Assert.assertFalse(StringUtils.hasValue(null));
        Assert.assertFalse(StringUtils.hasValue(""));
    }
}

