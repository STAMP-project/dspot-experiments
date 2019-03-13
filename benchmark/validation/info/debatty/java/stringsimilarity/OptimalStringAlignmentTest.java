/**
 * The MIT License
 *
 * Copyright 2016 Thibault Debatty.
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
package info.debatty.java.stringsimilarity;


import info.debatty.java.stringsimilarity.testutil.NullEmptyTests;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michail Bogdanos
 */
public class OptimalStringAlignmentTest {
    /**
     * Test of distance method, of class OptimalStringAlignment.
     */
    @Test
    public final void testDistance() {
        System.out.println("distance");
        OptimalStringAlignment instance = new OptimalStringAlignment();
        // equality
        Assert.assertEquals(0.0, instance.distance("ABDCEF", "ABDCEF"), 0.0);
        // single operation
        Assert.assertEquals(1.0, instance.distance("ABDCFE", "ABDCEF"), 0.0);
        Assert.assertEquals(1.0, instance.distance("BBDCEF", "ABDCEF"), 0.0);
        Assert.assertEquals(1.0, instance.distance("BDCEF", "ABDCEF"), 0.0);
        Assert.assertEquals(1.0, instance.distance("ABDCEF", "ADCEF"), 0.0);
        // other
        Assert.assertEquals(3.0, instance.distance("CA", "ABC"), 0.0);
        Assert.assertEquals(2.0, instance.distance("BAC", "CAB"), 0.0);
        Assert.assertEquals(4.0, instance.distance("abcde", "awxyz"), 0.0);
        Assert.assertEquals(5.0, instance.distance("abcde", "vwxyz"), 0.0);
        NullEmptyTests.testDistance(instance);
    }
}

