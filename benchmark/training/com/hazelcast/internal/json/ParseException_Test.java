/**
 * *****************************************************************************
 * Copyright (c) 2013, 2016 EclipseSource.
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(QuickTest.class)
public class ParseException_Test {
    private Location location;

    @Test
    public void location() {
        ParseException exception = new ParseException("Foo", location);
        Assert.assertSame(location, exception.getLocation());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void position() {
        ParseException exception = new ParseException("Foo", location);
        Assert.assertEquals(location.offset, exception.getOffset());
        Assert.assertEquals(location.line, exception.getLine());
        Assert.assertEquals(location.column, exception.getColumn());
    }

    @Test
    public void message() {
        ParseException exception = new ParseException("Foo", location);
        Assert.assertEquals("Foo at 23:42", exception.getMessage());
    }
}

