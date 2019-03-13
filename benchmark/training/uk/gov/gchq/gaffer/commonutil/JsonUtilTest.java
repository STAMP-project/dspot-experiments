/**
 * Copyright 2017-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.commonutil;


import org.junit.Assert;
import org.junit.Test;


public class JsonUtilTest {
    @Test
    public void shouldReturnTrueWhenJsonObjectsAreEqualButInADifferentOrder() {
        // Given
        final String json1 = "{\"a\": 1, \"b\": 2}";
        final String json2 = "{\"b\": 2, \"a\": 1}";
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2.getBytes());
        // Then
        Assert.assertTrue(resultStr);
        Assert.assertTrue(resultBytes);
        JsonAssert.assertEquals(json1, json2);
        JsonAssert.assertEquals(json1.getBytes(), json2.getBytes());
    }

    @Test
    public void shouldReturnFalseWhenJsonObjectsAreDifferentSizes() {
        // Given
        final String json1 = "{\"a\": 1, \"b\": 2}";
        final String json2 = "{\"a\": 1, \"b\": 2, \"c\": 3}";
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2.getBytes());
        // Then
        Assert.assertFalse(resultStr);
        Assert.assertFalse(resultBytes);
        try {
            JsonAssert.assertEquals(json1, json2);
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
        try {
            JsonAssert.assertEquals(json1.getBytes(), json2.getBytes());
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnFalseWhenJsonObjectsAreNotEqual() {
        // Given
        final String json1 = "{\"a\": 1, \"b\": 2}";
        final String json2 = "{\"a\": 1, \"b\": 3}";
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2.getBytes());
        // Then
        Assert.assertFalse(resultStr);
        Assert.assertFalse(resultBytes);
        try {
            JsonAssert.assertEquals(json1, json2);
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
        try {
            JsonAssert.assertEquals(json1.getBytes(), json2.getBytes());
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnTrueWhenJsonArraysAreEqual() {
        // Given
        final String json1 = "[1,2,3]";
        final String json2 = "[1,2,3]";
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2.getBytes());
        // Then
        Assert.assertTrue(resultStr);
        Assert.assertTrue(resultBytes);
        JsonAssert.assertEquals(json1, json2);
        JsonAssert.assertEquals(json1.getBytes(), json2.getBytes());
    }

    @Test
    public void shouldReturnFalseWhenJsonArraysAreNotEqual() {
        // Given
        final String json1 = "[1,2,3]";
        final String json2 = "[1,2,4]";
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2.getBytes());
        // Then
        Assert.assertFalse(resultStr);
        Assert.assertFalse(resultBytes);
        try {
            JsonAssert.assertEquals(json1, json2);
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
        try {
            JsonAssert.assertEquals(json1.getBytes(), json2.getBytes());
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnFalseWhenJsonArraysAreDifferentSizes() {
        // Given
        final String json1 = "[1,2,3]";
        final String json2 = "[1,2,3,4]";
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2.getBytes());
        // Then
        Assert.assertFalse(resultStr);
        Assert.assertFalse(resultBytes);
        try {
            JsonAssert.assertEquals(json1, json2);
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
        try {
            JsonAssert.assertEquals(json1.getBytes(), json2.getBytes());
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldReturnFalseWhenOneJsonObjectIsNull() {
        // Given
        final String json1 = "{\"a\": 1, \"b\": 2}";
        final String json2 = null;
        // Required as .getBytes() of a null string throws a NullPointer
        final byte[] json2ToBytes = null;
        // When
        final boolean resultStr = JsonUtil.equals(json1, json2);
        final boolean resultBytes = JsonUtil.equals(json1.getBytes(), json2ToBytes);
        // Then
        Assert.assertFalse(resultStr);
        Assert.assertFalse(resultBytes);
        try {
            JsonAssert.assertEquals(json1, json2);
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
        try {
            JsonAssert.assertEquals(json1.getBytes(), json2ToBytes);
            Assert.fail("Exception expected");
        } catch (final AssertionError e) {
            Assert.assertNotNull(e.getMessage());
        }
    }
}

