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
public class JsonWriter_Test {
    private StringWriter output;

    private JsonWriter writer;

    @Test
    public void writeLiteral() throws IOException {
        writer.writeLiteral("foo");
        Assert.assertEquals("foo", output.toString());
    }

    @Test
    public void writeNumber() throws IOException {
        writer.writeNumber("23");
        Assert.assertEquals("23", output.toString());
    }

    @Test
    public void writeString_empty() throws IOException {
        writer.writeString("");
        Assert.assertEquals("\"\"", output.toString());
    }

    @Test
    public void writeSting_escapesBackslashes() throws IOException {
        writer.writeString("foo\\bar");
        Assert.assertEquals("\"foo\\\\bar\"", output.toString());
    }

    @Test
    public void writeArrayParts() throws IOException {
        writer.writeArrayOpen();
        writer.writeArraySeparator();
        writer.writeArrayClose();
        Assert.assertEquals("[,]", output.toString());
    }

    @Test
    public void writeObjectParts() throws IOException {
        writer.writeObjectOpen();
        writer.writeMemberSeparator();
        writer.writeObjectSeparator();
        writer.writeObjectClose();
        Assert.assertEquals("{:,}", output.toString());
    }

    @Test
    public void writeMemberName_empty() throws IOException {
        writer.writeMemberName("");
        Assert.assertEquals("\"\"", output.toString());
    }

    @Test
    public void writeMemberName_escapesBackslashes() throws IOException {
        writer.writeMemberName("foo\\bar");
        Assert.assertEquals("\"foo\\\\bar\"", output.toString());
    }

    @Test
    public void escapesQuotes() throws IOException {
        writer.writeString("a\"b");
        Assert.assertEquals("\"a\\\"b\"", output.toString());
    }

    @Test
    public void escapesEscapedQuotes() throws IOException {
        writer.writeString("foo\\\"bar");
        Assert.assertEquals("\"foo\\\\\\\"bar\"", output.toString());
    }

    @Test
    public void escapesNewLine() throws IOException {
        writer.writeString("foo\nbar");
        Assert.assertEquals("\"foo\\nbar\"", output.toString());
    }

    @Test
    public void escapesWindowsNewLine() throws IOException {
        writer.writeString("foo\r\nbar");
        Assert.assertEquals("\"foo\\r\\nbar\"", output.toString());
    }

    @Test
    public void escapesTabs() throws IOException {
        writer.writeString("foo\tbar");
        Assert.assertEquals("\"foo\\tbar\"", output.toString());
    }

    @Test
    public void escapesSpecialCharacters() throws IOException {
        writer.writeString("foo\u2028bar\u2029");
        Assert.assertEquals("\"foo\\u2028bar\\u2029\"", output.toString());
    }

    @Test
    public void escapesZeroCharacter() throws IOException {
        writer.writeString(JsonWriter_Test.string('f', 'o', 'o', ((char) (0)), 'b', 'a', 'r'));
        Assert.assertEquals("\"foo\\u0000bar\"", output.toString());
    }

    @Test
    public void escapesEscapeCharacter() throws IOException {
        writer.writeString(JsonWriter_Test.string('f', 'o', 'o', ((char) (27)), 'b', 'a', 'r'));
        Assert.assertEquals("\"foo\\u001bbar\"", output.toString());
    }

    @Test
    public void escapesControlCharacters() throws IOException {
        writer.writeString(JsonWriter_Test.string(((char) (1)), ((char) (8)), ((char) (15)), ((char) (16)), ((char) (31))));
        Assert.assertEquals("\"\\u0001\\u0008\\u000f\\u0010\\u001f\"", output.toString());
    }

    @Test
    public void escapesFirstChar() throws IOException {
        writer.writeString(JsonWriter_Test.string('\\', 'x'));
        Assert.assertEquals("\"\\\\x\"", output.toString());
    }

    @Test
    public void escapesLastChar() throws IOException {
        writer.writeString(JsonWriter_Test.string('x', '\\'));
        Assert.assertEquals("\"x\\\\\"", output.toString());
    }
}

