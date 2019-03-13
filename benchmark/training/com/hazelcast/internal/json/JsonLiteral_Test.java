/**
 * *****************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ****************************************************************************
 */
package com.hazelcast.internal.json;


import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(QuickTest.class)
public class JsonLiteral_Test {
    @Test
    public void isNull() {
        Assert.assertTrue(NULL.isNull());
        Assert.assertFalse(TRUE.isNull());
        Assert.assertFalse(FALSE.isNull());
    }

    @Test
    public void isTrue() {
        Assert.assertTrue(TRUE.isTrue());
        Assert.assertFalse(NULL.isTrue());
        Assert.assertFalse(FALSE.isTrue());
    }

    @Test
    public void isFalse() {
        Assert.assertTrue(FALSE.isFalse());
        Assert.assertFalse(NULL.isFalse());
        Assert.assertFalse(TRUE.isFalse());
    }

    @Test
    public void isBoolean() {
        Assert.assertTrue(TRUE.isBoolean());
        Assert.assertTrue(FALSE.isBoolean());
        Assert.assertFalse(NULL.isBoolean());
    }

    @Test
    public void NULL_write() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        NULL.write(writer);
        Mockito.verify(writer).writeLiteral("null");
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void TRUE_write() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        TRUE.write(writer);
        Mockito.verify(writer).writeLiteral("true");
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void FALSE_write() throws IOException {
        JsonWriter writer = Mockito.mock(JsonWriter.class);
        FALSE.write(writer);
        Mockito.verify(writer).writeLiteral("false");
        Mockito.verifyNoMoreInteractions(writer);
    }

    @Test
    public void NULL_toString() {
        Assert.assertEquals("null", NULL.toString());
    }

    @Test
    public void TRUE_toString() {
        Assert.assertEquals("true", TRUE.toString());
    }

    @Test
    public void FALSE_toString() {
        Assert.assertEquals("false", FALSE.toString());
    }

    @Test
    public void NULL_equals() {
        Assert.assertTrue(NULL.equals(NULL));
        Assert.assertFalse(NULL.equals(null));
        Assert.assertFalse(NULL.equals(TRUE));
        Assert.assertFalse(NULL.equals(FALSE));
        Assert.assertFalse(NULL.equals(Json.Json.value("null")));
    }

    @Test
    public void TRUE_equals() {
        Assert.assertTrue(TRUE.equals(TRUE));
        Assert.assertFalse(TRUE.equals(null));
        Assert.assertFalse(TRUE.equals(FALSE));
        Assert.assertFalse(TRUE.equals(Boolean.TRUE));
        Assert.assertFalse(NULL.equals(Json.Json.value("true")));
    }

    @Test
    public void FALSE_equals() {
        Assert.assertTrue(FALSE.equals(FALSE));
        Assert.assertFalse(FALSE.equals(null));
        Assert.assertFalse(FALSE.equals(TRUE));
        Assert.assertFalse(FALSE.equals(Boolean.FALSE));
        Assert.assertFalse(NULL.equals(Json.Json.value("false")));
    }

    @Test
    public void NULL_isSerializable() throws Exception {
        Assert.assertEquals(NULL, TestUtil.serializeAndDeserialize(NULL));
        Assert.assertTrue(TestUtil.serializeAndDeserialize(NULL).isNull());
    }

    @Test
    public void TRUE_isSerializable() throws Exception {
        Assert.assertEquals(TRUE, TestUtil.serializeAndDeserialize(TRUE));
        Assert.assertTrue(TestUtil.serializeAndDeserialize(TRUE).isBoolean());
        Assert.assertTrue(TestUtil.serializeAndDeserialize(TRUE).isTrue());
    }

    @Test
    public void FALSE_isSerializable() throws Exception {
        Assert.assertEquals(FALSE, TestUtil.serializeAndDeserialize(FALSE));
        Assert.assertTrue(TestUtil.serializeAndDeserialize(FALSE).isBoolean());
        Assert.assertTrue(TestUtil.serializeAndDeserialize(FALSE).isFalse());
    }

    @Test
    public void sameAfterDeserialization() throws Exception {
        JsonArray array = new JsonArray().add(NULL).add(NULL);
        JsonArray deserialized = TestUtil.serializeAndDeserialize(array);
        Assert.assertNotSame(NULL, deserialized.get(0));
        Assert.assertSame(deserialized.get(0), deserialized.get(1));
    }
}

