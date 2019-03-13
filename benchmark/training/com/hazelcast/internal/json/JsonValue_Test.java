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


import Json.FALSE;
import Json.NULL;
import Json.TRUE;
import WriterConfig.MINIMAL;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category(QuickTest.class)
public class JsonValue_Test {
    @Test
    @SuppressWarnings("deprecation")
    public void testConstantsAreLiterals() {
        Assert.assertEquals(NULL, JsonValue.NULL);
        Assert.assertEquals(TRUE, JsonValue.TRUE);
        Assert.assertEquals(FALSE, JsonValue.FALSE);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void valueOf_int() {
        Assert.assertEquals(Json.value(23), JsonValue.valueOf(23));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void valueOf_long() {
        Assert.assertEquals(Json.value(23L), JsonValue.valueOf(23L));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void valueOf_float() {
        Assert.assertEquals(Json.value(23.5F), JsonValue.valueOf(23.5F));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void valueOf_double() {
        Assert.assertEquals(Json.value(23.5), JsonValue.valueOf(23.5));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void valueOf_boolean() {
        Assert.assertSame(Json.value(true), JsonValue.valueOf(true));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void valueOf_string() {
        Assert.assertEquals(Json.value("foo"), JsonValue.valueOf("foo"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_string() {
        Assert.assertEquals(new JsonArray(), JsonValue.readFrom("[]"));
        Assert.assertEquals(new JsonObject(), JsonValue.readFrom("{}"));
        Assert.assertEquals(Json.value(23), JsonValue.readFrom("23"));
        Assert.assertSame(NULL, JsonValue.readFrom("null"));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_reader() throws IOException {
        Assert.assertEquals(new JsonArray(), JsonValue.readFrom(new StringReader("[]")));
        Assert.assertEquals(new JsonObject(), JsonValue.readFrom(new StringReader("{}")));
        Assert.assertEquals(Json.value(23), JsonValue.readFrom(new StringReader("23")));
        Assert.assertSame(NULL, JsonValue.readFrom(new StringReader("null")));
    }

    @Test
    @SuppressWarnings("deprecation")
    public void readFrom_reader_doesNotCloseReader() throws IOException {
        Reader reader = Mockito.spy(new StringReader("{}"));
        JsonValue.readFrom(reader);
        Mockito.verify(reader, Mockito.never()).close();
    }

    @Test
    public void writeTo() throws IOException {
        JsonValue value = new JsonObject();
        Writer writer = new StringWriter();
        value.writeTo(writer);
        Assert.assertEquals("{}", writer.toString());
    }

    @Test
    public void writeTo_failsWithNullWriter() {
        final JsonValue value = new JsonObject();
        TestUtil.assertException(NullPointerException.class, "writer is null", new TestUtil.RunnableEx() {
            public void run() throws IOException {
                value.writeTo(null, MINIMAL);
            }
        });
    }

    @Test
    public void writeTo_failsWithNullConfig() {
        final JsonValue value = new JsonObject();
        TestUtil.assertException(NullPointerException.class, "config is null", new TestUtil.RunnableEx() {
            public void run() throws IOException {
                value.writeTo(new StringWriter(), null);
            }
        });
    }

    @Test
    public void toString_failsWithNullConfig() {
        final JsonValue value = new JsonObject();
        TestUtil.assertException(NullPointerException.class, "config is null", new TestUtil.RunnableEx() {
            public void run() throws IOException {
                value.toString(null);
            }
        });
    }

    @Test
    public void writeTo_doesNotCloseWriter() throws IOException {
        JsonValue value = new JsonObject();
        Writer writer = Mockito.spy(new StringWriter());
        value.writeTo(writer);
        Mockito.verify(writer, Mockito.never()).close();
    }

    @Test
    public void asObject_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not an object: null", new Runnable() {
            public void run() {
                NULL.asObject();
            }
        });
    }

    @Test
    public void asArray_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not an array: null", new Runnable() {
            public void run() {
                NULL.asArray();
            }
        });
    }

    @Test
    public void asString_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not a string: null", new Runnable() {
            public void run() {
                NULL.asString();
            }
        });
    }

    @Test
    public void asInt_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not a number: null", new Runnable() {
            public void run() {
                NULL.asInt();
            }
        });
    }

    @Test
    public void asLong_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not a number: null", new Runnable() {
            public void run() {
                NULL.asLong();
            }
        });
    }

    @Test
    public void asFloat_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not a number: null", new Runnable() {
            public void run() {
                NULL.asFloat();
            }
        });
    }

    @Test
    public void asDouble_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not a number: null", new Runnable() {
            public void run() {
                NULL.asDouble();
            }
        });
    }

    @Test
    public void asBoolean_failsOnIncompatibleType() {
        TestUtil.assertException(UnsupportedOperationException.class, "Not a boolean: null", new Runnable() {
            public void run() {
                NULL.asBoolean();
            }
        });
    }

    @Test
    public void isXxx_returnsFalseForIncompatibleType() {
        JsonValue jsonValue = new JsonValue() {
            @Override
            void write(JsonWriter writer) throws IOException {
            }
        };
        Assert.assertFalse(jsonValue.isArray());
        Assert.assertFalse(jsonValue.isObject());
        Assert.assertFalse(jsonValue.isString());
        Assert.assertFalse(jsonValue.isNumber());
        Assert.assertFalse(jsonValue.isBoolean());
        Assert.assertFalse(jsonValue.isNull());
        Assert.assertFalse(jsonValue.isTrue());
        Assert.assertFalse(jsonValue.isFalse());
    }
}

