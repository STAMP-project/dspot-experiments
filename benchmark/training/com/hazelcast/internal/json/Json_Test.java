/**
 * *****************************************************************************
 * Copyright (c) 2015 EclipseSource.
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


import Json.FALSE;
import Json.NULL;
import Json.TRUE;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(QuickTest.class)
public class Json_Test {
    @Test
    public void literalConstants() {
        Assert.assertTrue(NULL.isNull());
        Assert.assertTrue(TRUE.isTrue());
        Assert.assertTrue(FALSE.isFalse());
    }

    @Test
    public void value_int() {
        Assert.assertEquals("0", Json.value(0).toString());
        Assert.assertEquals("23", Json.value(23).toString());
        Assert.assertEquals("-1", Json.value((-1)).toString());
        Assert.assertEquals("2147483647", Json.value(Integer.MAX_VALUE).toString());
        Assert.assertEquals("-2147483648", Json.value(Integer.MIN_VALUE).toString());
    }

    @Test
    public void value_long() {
        Assert.assertEquals("0", Json.value(0L).toString());
        Assert.assertEquals("9223372036854775807", Json.value(Long.MAX_VALUE).toString());
        Assert.assertEquals("-9223372036854775808", Json.value(Long.MIN_VALUE).toString());
    }

    @Test
    public void value_float() {
        Assert.assertEquals("23.5", Json.value(23.5F).toString());
        Assert.assertEquals("-3.1416", Json.value((-3.1416F)).toString());
        Assert.assertEquals("1.23E-6", Json.value(1.23E-6F).toString());
        Assert.assertEquals("-1.23E7", Json.value((-1.23E7F)).toString());
    }

    @Test
    public void value_float_cutsOffPointZero() {
        Assert.assertEquals("0", Json.value(0.0F).toString());
        Assert.assertEquals("-1", Json.value((-1.0F)).toString());
        Assert.assertEquals("10", Json.value(10.0F).toString());
    }

    @Test
    public void value_float_failsWithInfinity() {
        String message = "Infinite and NaN values not permitted in JSON";
        TestUtil.assertException(IllegalArgumentException.class, message, new Runnable() {
            public void run() {
                Json.value(Float.POSITIVE_INFINITY);
            }
        });
    }

    @Test
    public void value_float_failsWithNaN() {
        String message = "Infinite and NaN values not permitted in JSON";
        TestUtil.assertException(IllegalArgumentException.class, message, new Runnable() {
            public void run() {
                Json.value(Float.NaN);
            }
        });
    }

    @Test
    public void value_double() {
        Assert.assertEquals("23.5", Json.value(23.5).toString());
        Assert.assertEquals("3.1416", Json.value(3.1416).toString());
        Assert.assertEquals("1.23E-6", Json.value(1.23E-6).toString());
        Assert.assertEquals("1.7976931348623157E308", Json.value(1.7976931348623157E308).toString());
    }

    @Test
    public void value_double_cutsOffPointZero() {
        Assert.assertEquals("0", Json.value(0.0).toString());
        Assert.assertEquals("-1", Json.value((-1.0)).toString());
        Assert.assertEquals("10", Json.value(10.0).toString());
    }

    @Test
    public void value_double_failsWithInfinity() {
        String message = "Infinite and NaN values not permitted in JSON";
        TestUtil.assertException(IllegalArgumentException.class, message, new Runnable() {
            public void run() {
                Json.value(Double.POSITIVE_INFINITY);
            }
        });
    }

    @Test
    public void value_double_failsWithNaN() {
        String message = "Infinite and NaN values not permitted in JSON";
        TestUtil.assertException(IllegalArgumentException.class, message, new Runnable() {
            public void run() {
                Json.value(Double.NaN);
            }
        });
    }

    @Test
    public void value_boolean() {
        Assert.assertSame(TRUE, Json.value(true));
        Assert.assertSame(FALSE, Json.value(false));
    }

    @Test
    public void value_string() {
        Assert.assertEquals("", Json.value("").asString());
        Assert.assertEquals("Hello", Json.value("Hello").asString());
        Assert.assertEquals("\"Hello\"", Json.value("\"Hello\"").asString());
    }

    @Test
    public void value_string_toleratesNull() {
        Assert.assertSame(NULL, Json.value(null));
    }

    @Test
    public void array() {
        Assert.assertEquals(new JsonArray(), Json.array());
    }

    @Test
    public void array_int() {
        Assert.assertEquals(new JsonArray().add(23), Json.array(new int[]{ 23 }));
        Assert.assertEquals(new JsonArray().add(23).add(42), Json.array(new int[]{ 23, 42 }));
    }

    @Test
    public void array_int_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "values is null", new Runnable() {
            public void run() {
                Json.array(((int[]) (null)));
            }
        });
    }

    @Test
    public void array_long() {
        Assert.assertEquals(new JsonArray().add(23L), Json.array(new long[]{ 23L }));
        Assert.assertEquals(new JsonArray().add(23L).add(42L), Json.array(new long[]{ 23L, 42L }));
    }

    @Test
    public void array_long_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "values is null", new Runnable() {
            public void run() {
                Json.array(((long[]) (null)));
            }
        });
    }

    @Test
    public void array_float() {
        Assert.assertEquals(new JsonArray().add(3.14F), Json.array(new float[]{ 3.14F }));
        Assert.assertEquals(new JsonArray().add(3.14F).add(1.41F), Json.array(new float[]{ 3.14F, 1.41F }));
    }

    @Test
    public void array_float_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "values is null", new Runnable() {
            public void run() {
                Json.array(((float[]) (null)));
            }
        });
    }

    @Test
    public void array_double() {
        Assert.assertEquals(new JsonArray().add(3.14), Json.array(3.14));
        Assert.assertEquals(new JsonArray().add(3.14).add(1.41), Json.array(3.14, 1.41));
    }

    @Test
    public void array_double_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "values is null", new Runnable() {
            public void run() {
                Json.array(((double[]) (null)));
            }
        });
    }

    @Test
    public void array_boolean() {
        Assert.assertEquals(new JsonArray().add(true), Json.array(true));
        Assert.assertEquals(new JsonArray().add(true).add(false), Json.array(true, false));
    }

    @Test
    public void array_boolean_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "values is null", new Runnable() {
            public void run() {
                Json.array(((boolean[]) (null)));
            }
        });
    }

    @Test
    public void array_string() {
        Assert.assertEquals(new JsonArray().add("foo"), Json.array("foo"));
        Assert.assertEquals(new JsonArray().add("foo").add("bar"), Json.array("foo", "bar"));
    }

    @Test
    public void array_string_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "values is null", new Runnable() {
            public void run() {
                Json.array(((String[]) (null)));
            }
        });
    }

    @Test
    public void object() {
        Assert.assertEquals(new JsonObject(), Json.object());
    }

    @Test
    public void parse_string() {
        Assert.assertEquals(Json.value(23), Json.parse("23"));
    }

    @Test
    public void parse_string_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "string is null", new Runnable() {
            public void run() {
                Json.parse(((String) (null)));
            }
        });
    }

    @Test
    public void parse_reader() throws IOException {
        Reader reader = new StringReader("23");
        Assert.assertEquals(Json.value(23), Json.parse(reader));
    }

    @Test
    public void parse_reader_failsWithNull() {
        TestUtil.assertException(NullPointerException.class, "reader is null", new TestUtil.RunnableEx() {
            public void run() throws IOException {
                Json.parse(((Reader) (null)));
            }
        });
    }
}

