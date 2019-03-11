/**
 * The MIT License
 * Copyright ? 2010 JmxTrans team
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
package com.googlecode.jmxtrans.model.naming;


import org.junit.Assert;
import org.junit.Test;


public class StringUtilsTest {
    @Test
    public void testCleanupStr() {
        Assert.assertEquals("addfber1241qdw!??$", StringUtils.cleanupStr("addfber1241qdw!??$"));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd.abcd"));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd.abcd."));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr(".abcd_abcd"));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd/abcd"));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd/abcd/"));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr("/abcd_abcd"));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd'_abcd'"));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr("/abcd\"_abcd\""));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr("/ab cd_abcd"));
        Assert.assertEquals(null, StringUtils.cleanupStr(null));
        Assert.assertEquals("", StringUtils.cleanupStr(""));
        Assert.assertEquals("abcd", StringUtils.cleanupStr("\"abcd\".\"\""));
        Assert.assertEquals("abcd", StringUtils.cleanupStr("\"abcd\"_\"\""));
    }

    @Test
    public void testCleanupStrDottedKeysKept() {
        Assert.assertEquals("addfber1241qdw!??$", StringUtils.cleanupStr("addfber1241qdw!??$", true));
        Assert.assertEquals("abcd.abcd", StringUtils.cleanupStr("abcd.abcd", true));
        Assert.assertEquals("abcd.abcd", StringUtils.cleanupStr("abcd.abcd.", true));
        Assert.assertEquals(".abcd_abcd", StringUtils.cleanupStr(".abcd_abcd", true));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd/abcd", true));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd/abcd/", true));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr("/abcd_abcd", true));
        Assert.assertEquals("abcd_abcd", StringUtils.cleanupStr("abcd'_abcd'", true));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr("/abcd\"_abcd\"", true));
        Assert.assertEquals("_abcd_abcd", StringUtils.cleanupStr("/ab cd_abcd", true));
        Assert.assertEquals("abcd", StringUtils.cleanupStr("\"abcd\".\"\""));
        Assert.assertEquals("abcd", StringUtils.cleanupStr("\"abcd\"_\"\""));
    }
}

