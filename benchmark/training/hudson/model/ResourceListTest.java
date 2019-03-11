/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi, Stephen Connolly
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
package hudson.model;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Stephen Connolly
 */
public class ResourceListTest {
    private Resource a1;

    private Resource a2;

    private Resource a3;

    private Resource a4;

    private Resource a;

    private Resource b1;

    private Resource b2;

    private Resource b3;

    private Resource b4;

    private Resource b;

    private Resource c1;

    private Resource c2;

    private Resource c3;

    private Resource c4;

    private Resource c;

    private Resource d;

    private Resource e;

    private Resource f;

    private int fWriteCount;

    private Random entropy;

    private ResourceList x;

    private ResourceList y;

    private ResourceList z;

    @Test
    public void emptyLists() {
        z.r(a);
        ResourceList w = new ResourceList();
        w.w(a);
        Assert.assertFalse("Empty vs Empty", x.isCollidingWith(y));
        Assert.assertFalse("Empty vs Empty", y.isCollidingWith(x));
        Assert.assertFalse("Empty vs Read", x.isCollidingWith(z));
        Assert.assertFalse("Read vs Empty", z.isCollidingWith(x));
        Assert.assertFalse("Empty vs Write", x.isCollidingWith(w));
        Assert.assertFalse("Write vs Empty", w.isCollidingWith(x));
    }

    @Test
    public void simpleR() {
        x.r(a);
        y.r(b);
        z.r(a);
        Assert.assertFalse("Read-Read", x.isCollidingWith(y));
        Assert.assertFalse("Read-Read", y.isCollidingWith(x));
        Assert.assertFalse("Read-Read", x.isCollidingWith(z));
        Assert.assertFalse("Read-Read", z.isCollidingWith(x));
        Assert.assertFalse("Read-Read", z.isCollidingWith(y));
        Assert.assertFalse("Read-Read", y.isCollidingWith(z));
    }

    @Test
    public void simpleRW() {
        x.r(a);
        y.r(b);
        z.w(a);
        Assert.assertFalse("Read-Read different resources", x.isCollidingWith(y));
        Assert.assertFalse("Read-Read different resources", y.isCollidingWith(x));
        Assert.assertTrue("Read-Write same resource", x.isCollidingWith(z));
        Assert.assertTrue("Read-Write same resource", z.isCollidingWith(x));
        Assert.assertFalse("Read-Write different resources", z.isCollidingWith(y));
        Assert.assertFalse("Read-Write different resources", y.isCollidingWith(z));
    }

    @Test
    public void simpleW() {
        x.w(a);
        y.w(b);
        z.w(a);
        Assert.assertFalse(x.isCollidingWith(y));
        Assert.assertFalse(y.isCollidingWith(x));
        Assert.assertTrue(x.isCollidingWith(z));
        Assert.assertTrue(z.isCollidingWith(x));
        Assert.assertFalse(z.isCollidingWith(y));
        Assert.assertFalse(y.isCollidingWith(z));
        ResourceList w = ResourceList.union(x, y);
        Assert.assertTrue(w.isCollidingWith(z));
        Assert.assertTrue(z.isCollidingWith(w));
        ResourceList v = new ResourceList();
        v.w(a1);
        Assert.assertTrue(w.isCollidingWith(v));
        Assert.assertTrue(z.isCollidingWith(w));
    }

    @Test
    public void parentChildR() {
        x.r(a1);
        x.r(a2);
        y.r(a3);
        y.r(a4);
        z.r(a);
        Assert.assertFalse("Reads should never conflict", x.isCollidingWith(y));
        Assert.assertFalse("Reads should never conflict", y.isCollidingWith(x));
        Assert.assertFalse("Reads should never conflict", x.isCollidingWith(z));
        Assert.assertFalse("Reads should never conflict", z.isCollidingWith(x));
        Assert.assertFalse("Reads should never conflict", z.isCollidingWith(y));
        Assert.assertFalse("Reads should never conflict", y.isCollidingWith(z));
    }

    @Test
    public void parentChildW() {
        x.w(a1);
        x.w(a2);
        y.w(a3);
        y.w(a4);
        z.w(a);
        Assert.assertFalse("Sibling resources should not conflict", x.isCollidingWith(y));
        Assert.assertFalse("Sibling resources should not conflict", y.isCollidingWith(x));
        Assert.assertTrue("Taking parent resource assumes all children are taken too", x.isCollidingWith(z));
        Assert.assertTrue("Taking parent resource assumes all children are taken too", z.isCollidingWith(x));
        Assert.assertTrue("Taking parent resource assumes all children are taken too", z.isCollidingWith(y));
        Assert.assertTrue("Taking parent resource assumes all children are taken too", y.isCollidingWith(z));
    }

    @Test
    public void parentChildR3() {
        x.r(c1);
        x.r(c2);
        y.r(c3);
        y.r(c4);
        z.r(c);
        Assert.assertFalse("Reads should never conflict", x.isCollidingWith(y));
        Assert.assertFalse("Reads should never conflict", y.isCollidingWith(x));
        Assert.assertFalse("Reads should never conflict", x.isCollidingWith(z));
        Assert.assertFalse("Reads should never conflict", z.isCollidingWith(x));
        Assert.assertFalse("Reads should never conflict", z.isCollidingWith(y));
        Assert.assertFalse("Reads should never conflict", y.isCollidingWith(z));
    }

    @Test
    public void parentChildW3() {
        x.w(c1);
        x.w(c2);
        y.w(c3);
        y.w(c4);
        z.w(c);
        Assert.assertFalse("Sibling resources should not conflict", x.isCollidingWith(y));
        Assert.assertFalse("Sibling resources should not conflict", y.isCollidingWith(x));
        Assert.assertFalse("Using less than the limit of child resources should not be a problem", x.isCollidingWith(z));
        Assert.assertFalse("Using less than the limit of child resources should not be a problem", z.isCollidingWith(x));
        Assert.assertFalse("Using less than the limit of child resources should not be a problem", z.isCollidingWith(y));
        Assert.assertFalse("Using less than the limit of child resources should not be a problem", y.isCollidingWith(z));
        ResourceList w = ResourceList.union(x, y);
        Assert.assertFalse("Using less than the limit of child resources should not be a problem", w.isCollidingWith(z));
        Assert.assertFalse("Using less than the limit of child resources should not be a problem", z.isCollidingWith(w));
        Assert.assertFalse("Total count = 2, limit is 3", w.isCollidingWith(x));
        Assert.assertFalse("Total count = 2, limit is 3", x.isCollidingWith(w));
        ResourceList v = ResourceList.union(x, x);// write count is two

        Assert.assertFalse("Total count = 3, limit is 3", v.isCollidingWith(x));
        Assert.assertFalse("Total count = 3, limit is 3", x.isCollidingWith(v));
        v = ResourceList.union(v, x);// write count is three

        Assert.assertTrue("Total count = 4, limit is 3", v.isCollidingWith(x));
        Assert.assertTrue("Total count = 4, limit is 3", x.isCollidingWith(v));
    }

    @Test
    public void multiWrite1() {
        y.w(e);
        Assert.assertFalse(x.isCollidingWith(y));
        Assert.assertFalse(y.isCollidingWith(x));
        for (int i = 0; i < (fWriteCount); i++) {
            x.w(e);
            Assert.assertTrue((("Total = W" + (i + 1)) + ", Limit = W1"), x.isCollidingWith(y));
            Assert.assertTrue((("Total = W" + (i + 1)) + ", Limit = W1"), y.isCollidingWith(x));
        }
        int j = (entropy.nextInt(50)) + 3;
        for (int i = 1; i < j; i++) {
            Assert.assertTrue((("Total = W" + (i + (fWriteCount))) + ", Limit = W1"), x.isCollidingWith(y));
            Assert.assertTrue((("Total = W" + (i + (fWriteCount))) + ", Limit = W1"), y.isCollidingWith(x));
            x.w(e);
        }
    }

    @Test
    public void multiWriteN() {
        y.w(f);
        for (int i = 0; i < (f.numConcurrentWrite); i++) {
            Assert.assertFalse(((("Total = W" + i) + ", Limit = W") + (f.numConcurrentWrite)), x.isCollidingWith(y));
            Assert.assertFalse(((("Total = W" + i) + ", Limit = W") + (f.numConcurrentWrite)), y.isCollidingWith(x));
            x.w(f);
        }
        int j = (entropy.nextInt(50)) + 3;
        for (int i = 1; i < j; i++) {
            Assert.assertTrue(((("Total = W" + ((fWriteCount) + i)) + ", Limit = W") + (fWriteCount)), x.isCollidingWith(y));
            Assert.assertTrue(((("Total = W" + ((fWriteCount) + i)) + ", Limit = W") + (fWriteCount)), y.isCollidingWith(x));
            x.w(f);
        }
    }

    @Test
    public void multiRead1() {
        y.r(e);
        for (int i = 0; i < (fWriteCount); i++) {
            Assert.assertFalse((("Total = R" + (i + 1)) + ", Limit = W1"), x.isCollidingWith(y));
            Assert.assertFalse((("Total = R" + (i + 1)) + ", Limit = W1"), y.isCollidingWith(x));
            x.r(e);
        }
        int j = (entropy.nextInt(50)) + 3;
        for (int i = 1; i < j; i++) {
            Assert.assertFalse((("Total = R" + (i + (fWriteCount))) + ", Limit = W1"), x.isCollidingWith(y));
            Assert.assertFalse((("Total = R" + (i + (fWriteCount))) + ", Limit = W1"), y.isCollidingWith(x));
            x.r(e);
        }
    }

    @Test
    public void multiReadN() {
        y.r(f);
        for (int i = 0; i < (fWriteCount); i++) {
            Assert.assertFalse(((("Total = R" + (i + 1)) + ", Limit = W") + (fWriteCount)), x.isCollidingWith(y));
            Assert.assertFalse(((("Total = R" + (i + 1)) + ", Limit = W") + (fWriteCount)), y.isCollidingWith(x));
            x.r(f);
        }
        int j = (entropy.nextInt(50)) + 3;
        for (int i = 1; i < j; i++) {
            Assert.assertFalse(((("Total = R" + ((fWriteCount) + i)) + ", Limit = W") + (fWriteCount)), x.isCollidingWith(y));
            Assert.assertFalse(((("Total = R" + ((fWriteCount) + i)) + ", Limit = W") + (fWriteCount)), y.isCollidingWith(x));
            x.r(f);
        }
    }
}

