/**
 * The MIT License
 *
 * Copyright (c) 2012, CloudBees, Inc.
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
package jenkins.model.lazy;


import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class SortedListTest {
    SortedList<String> l = new SortedList<String>(new ArrayList<String>(Arrays.asList("B", "D", "F")));

    @Test
    public void testCeil() {
        Assert.assertEquals(0, l.ceil("A"));
        Assert.assertEquals(0, l.ceil("B"));
        Assert.assertEquals(1, l.ceil("C"));
        Assert.assertEquals(1, l.ceil("D"));
        Assert.assertEquals(2, l.ceil("E"));
        Assert.assertEquals(2, l.ceil("F"));
        Assert.assertEquals(3, l.ceil("G"));
    }

    @Test
    public void testFloor() {
        Assert.assertEquals((-1), l.floor("A"));
        Assert.assertEquals(0, l.floor("B"));
        Assert.assertEquals(0, l.floor("C"));
        Assert.assertEquals(1, l.floor("D"));
        Assert.assertEquals(1, l.floor("E"));
        Assert.assertEquals(2, l.floor("F"));
        Assert.assertEquals(2, l.floor("G"));
    }

    @Test
    public void testLower() {
        Assert.assertEquals((-1), l.lower("A"));
        Assert.assertEquals((-1), l.lower("B"));
        Assert.assertEquals(0, l.lower("C"));
        Assert.assertEquals(0, l.lower("D"));
        Assert.assertEquals(1, l.lower("E"));
        Assert.assertEquals(1, l.lower("F"));
        Assert.assertEquals(2, l.lower("G"));
    }

    @Test
    public void testHigher() {
        Assert.assertEquals(0, l.higher("A"));
        Assert.assertEquals(1, l.higher("B"));
        Assert.assertEquals(1, l.higher("C"));
        Assert.assertEquals(2, l.higher("D"));
        Assert.assertEquals(2, l.higher("E"));
        Assert.assertEquals(3, l.higher("F"));
        Assert.assertEquals(3, l.higher("G"));
    }

    @Test
    public void testRange() {
        Assert.assertTrue(l.isInRange(0));
        Assert.assertTrue(l.isInRange(1));
        Assert.assertTrue(l.isInRange(2));
        Assert.assertFalse(l.isInRange((-1)));
        Assert.assertFalse(l.isInRange(3));
    }

    @Test
    public void remove() {
        l.remove("nosuchthing");
        Assert.assertEquals(3, l.size());
        l.remove("B");
        Assert.assertEquals(2, l.size());
        Assert.assertEquals("D", l.get(0));
        Assert.assertEquals("F", l.get(1));
    }

    @Test
    public void testClone() {
        final int originalSize = l.size();
        SortedList<String> l2 = new SortedList<String>(l);
        Assert.assertEquals(originalSize, l2.size());
        Assert.assertEquals(originalSize, l.size());
        for (int i = 0; i < originalSize; i++) {
            Assert.assertEquals(l.get(i), l2.get(i));
        }
        l.remove(0);
        Assert.assertEquals((originalSize - 1), l.size());
        Assert.assertEquals(originalSize, l2.size());
        l2.remove(1);
        l2.remove(1);
        Assert.assertEquals((originalSize - 1), l.size());
        Assert.assertEquals((originalSize - 2), l2.size());
    }
}

