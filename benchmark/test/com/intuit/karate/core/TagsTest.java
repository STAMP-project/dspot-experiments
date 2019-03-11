/**
 * The MIT License
 *
 * Copyright 2018 Intuit Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.intuit.karate.core;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pthomas3
 */
public class TagsTest {
    @Test
    public void testCucumberOptionsTagsConversion() {
        Assert.assertEquals("anyOf('@foo')", Tags.fromKarateOptionsTags("@foo"));
        Assert.assertEquals("anyOf('@foo','@bar')", Tags.fromKarateOptionsTags("@foo,@bar"));
        Assert.assertEquals("anyOf('@foo') && anyOf('@bar')", Tags.fromKarateOptionsTags("@foo", "@bar"));
        Assert.assertEquals("anyOf('@foo') && not('@bar')", Tags.fromKarateOptionsTags("@foo", "~@bar"));
        // detect new syntax and use as-is
        Assert.assertEquals("anyOf('@foo')", Tags.fromKarateOptionsTags("anyOf('@foo')"));
    }

    @Test
    public void testTagSelectors() {
        Assert.assertTrue(eval(null));
        Assert.assertTrue(eval(null, "@foo", "@bar"));
        Assert.assertTrue(eval("anyOf('@foo')", "@foo", "@bar"));
        Assert.assertTrue(eval("not('@ignore')"));
        Assert.assertTrue(eval("not('@ignore')", "@foo", "@bar"));
        Assert.assertTrue(eval("anyOf('@foo', '@bar')", "@foo", "@bar"));
        Assert.assertTrue(eval("anyOf('@foo', '@baz')", "@foo", "@bar"));
        Assert.assertTrue(eval("allOf('@foo')", "@foo", "@bar"));
        Assert.assertTrue(eval("allOf('@foo', '@bar')", "@foo", "@bar"));
        Assert.assertTrue(eval("allOf('@foo', '@bar') && not('@ignore')", "@foo", "@bar"));
        Assert.assertTrue(eval("anyOf('@foo') && !anyOf('@ignore')", "@foo", "@bar"));
        Assert.assertFalse(eval("!anyOf('@ignore')", "@ignore"));
        Assert.assertFalse(eval("not('@ignore')", "@ignore"));
        Assert.assertFalse(eval("not('@ignore', '@foo')", "@ignore"));
        Assert.assertFalse(eval("!anyOf('@ignore')", "@foo", "@bar", "@ignore"));
        Assert.assertFalse(eval("anyOf('@foo') && !anyOf('@ignore')", "@foo", "@bar", "@ignore"));
        Assert.assertFalse(eval("anyOf('@foo')", "@bar", "@ignore"));
        Assert.assertFalse(eval("allOf('@foo', '@baz')", "@foo", "@bar"));
        Assert.assertFalse(eval("anyOf('@foo') && anyOf('@baz')", "@foo", "@bar"));
        Assert.assertFalse(eval("!anyOf('@foo')", "@foo", "@bar"));
        Assert.assertFalse(eval("allOf('@foo', '@bar') && not('@ignore')", "@foo", "@bar", "@ignore"));
    }

    @Test
    public void testTagValueSelectors() {
        Assert.assertFalse(eval("valuesFor('@id').isPresent"));
        Assert.assertFalse(eval("valuesFor('@id').isPresent", "@foo"));
        Assert.assertFalse(eval("valuesFor('@id').isPresent", "@id"));
        Assert.assertFalse(eval("valuesFor('@id').isPresent", "@foo", "@id"));
        Assert.assertFalse(eval("valuesFor('@id').isPresent", "@id="));
        Assert.assertTrue(eval("valuesFor('@id').isPresent", "@id=1"));
        Assert.assertTrue(eval("valuesFor('@id').isOnly(1)", "@id=1"));
        Assert.assertTrue(eval("valuesFor('@id').isAnyOf(1)", "@id=1"));
        Assert.assertTrue(eval("valuesFor('@id').isAllOf(1)", "@id=1"));
        Assert.assertTrue(eval("valuesFor('@id').isAllOf(1)", "@id=1,2"));
        Assert.assertFalse(eval("valuesFor('@id').isAnyOf(2)", "@id=1"));
        Assert.assertTrue(eval("valuesFor('@id').isAnyOf(1)", "@id=1,2"));
        Assert.assertTrue(eval("valuesFor('@id').isAnyOf(2)", "@id=1,2"));
        Assert.assertTrue(eval("valuesFor('@id').isAllOf(1, 2)", "@id=1,2"));
        Assert.assertTrue(eval("valuesFor('@id').isOnly(1, 2)", "@id=1,2"));
        Assert.assertFalse(eval("valuesFor('@id').isOnly(1, 3)", "@id=1,2"));
        Assert.assertTrue(eval("valuesFor('@id').isAnyOf(1, 2)", "@id=1,2"));
        Assert.assertTrue(eval("valuesFor('@id').isAnyOf(1, 3)", "@id=1,2"));
        Assert.assertTrue(eval("valuesFor('@id').isEach(function(s){return s.startsWith('1')})", "@id=100,1000"));
        Assert.assertTrue(eval("valuesFor('@id').isEach(function(s){return /^1.*/.test(s)})", "@id=100,1000"));
    }
}

