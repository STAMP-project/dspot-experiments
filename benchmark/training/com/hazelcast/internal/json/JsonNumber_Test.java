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
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(QuickTest.class)
public class JsonNumber_Test {
    private StringWriter output;

    private JsonWriter writer;

    @Test
    public void constructor_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "string is null", new Runnable() {
            public void run() {
                new JsonNumber(null);
            }
        });
    }

    @Test
    public void write() throws IOException {
        new JsonNumber("23").write(writer);
        Assert.assertEquals("23", output.toString());
    }

    @Test
    public void toString_returnsInputString() {
        Assert.assertEquals("foo", new JsonNumber("foo").toString());
    }

    @Test
    public void isNumber() {
        Assert.assertTrue(new JsonNumber("23").isNumber());
    }

    @Test
    public void asInt() {
        Assert.assertEquals(23, new JsonNumber("23").asInt());
    }

    @Test(expected = NumberFormatException.class)
    public void asInt_failsWithExceedingValues() {
        new JsonNumber("10000000000").asInt();
    }

    @Test(expected = NumberFormatException.class)
    public void asInt_failsWithExponent() {
        new JsonNumber("1e5").asInt();
    }

    @Test(expected = NumberFormatException.class)
    public void asInt_failsWithFractional() {
        new JsonNumber("23.5").asInt();
    }

    @Test
    public void asLong() {
        Assert.assertEquals(23L, new JsonNumber("23").asLong());
    }

    @Test(expected = NumberFormatException.class)
    public void asLong_failsWithExceedingValues() {
        new JsonNumber("10000000000000000000").asLong();
    }

    @Test(expected = NumberFormatException.class)
    public void asLong_failsWithExponent() {
        new JsonNumber("1e5").asLong();
    }

    @Test(expected = NumberFormatException.class)
    public void asLong_failsWithFractional() {
        new JsonNumber("23.5").asLong();
    }

    @Test
    public void asFloat() {
        Assert.assertEquals(23.05F, new JsonNumber("23.05").asFloat(), 0);
    }

    @Test
    public void asFloat_returnsInfinityForExceedingValues() {
        Assert.assertEquals(Float.POSITIVE_INFINITY, new JsonNumber("1e50").asFloat(), 0);
        Assert.assertEquals(Float.NEGATIVE_INFINITY, new JsonNumber("-1e50").asFloat(), 0);
    }

    @Test
    public void asDouble() {
        double result = new JsonNumber("23.05").asDouble();
        Assert.assertEquals(23.05, result, 0);
    }

    @Test
    public void asDouble_returnsInfinityForExceedingValues() {
        Assert.assertEquals(Double.POSITIVE_INFINITY, new JsonNumber("1e500").asDouble(), 0);
        Assert.assertEquals(Double.NEGATIVE_INFINITY, new JsonNumber("-1e500").asDouble(), 0);
    }

    @Test
    public void equals_trueForSameInstance() {
        JsonNumber number = new JsonNumber("23");
        Assert.assertTrue(number.equals(number));
    }

    @Test
    public void equals_trueForEqualNumberStrings() {
        Assert.assertTrue(new JsonNumber("23").equals(new JsonNumber("23")));
    }

    @Test
    public void equals_falseForDifferentNumberStrings() {
        Assert.assertFalse(new JsonNumber("23").equals(new JsonNumber("42")));
        Assert.assertFalse(new JsonNumber("1e+5").equals(new JsonNumber("1e5")));
    }

    @Test
    public void equals_falseForNull() {
        Assert.assertFalse(new JsonNumber("23").equals(null));
    }

    @Test
    public void equals_falseForSubclass() {
        Assert.assertFalse(new JsonNumber("23").equals(new JsonNumber("23") {}));
    }

    @Test
    public void hashCode_equalsForEqualStrings() {
        Assert.assertTrue(((new JsonNumber("23").hashCode()) == (new JsonNumber("23").hashCode())));
    }

    @Test
    public void hashCode_differsForDifferentStrings() {
        Assert.assertFalse(((new JsonNumber("23").hashCode()) == (new JsonNumber("42").hashCode())));
    }

    @Test
    public void canBeSerializedAndDeserialized() throws Exception {
        JsonNumber number = new JsonNumber("3.14");
        Assert.assertEquals(number, TestUtil.serializeAndDeserialize(number));
    }
}

