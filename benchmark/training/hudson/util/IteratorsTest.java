/**
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., Kohsuke Kawaguchi
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
package hudson.util;


import hudson.util.Iterators.CountingPredicate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hudson.test.Issue;


/**
 *
 *
 * @author Kohsuke Kawaguchi
 */
public class IteratorsTest {
    @Test
    public void reverseSequence() {
        List<Integer> lst = Iterators.reverseSequence(1, 4);
        Assert.assertEquals(3, ((int) (lst.get(0))));
        Assert.assertEquals(2, ((int) (lst.get(1))));
        Assert.assertEquals(1, ((int) (lst.get(2))));
        Assert.assertEquals(3, lst.size());
    }

    @Test
    public void sequence() {
        List<Integer> lst = Iterators.sequence(1, 4);
        Assert.assertEquals(1, ((int) (lst.get(0))));
        Assert.assertEquals(2, ((int) (lst.get(1))));
        Assert.assertEquals(3, ((int) (lst.get(2))));
        Assert.assertEquals(3, lst.size());
    }

    @Test
    public void wrap() {
        List<Integer> lst = Iterators.sequence(1, 4);
        Iterable<Integer> wrapped = Iterators.wrap(lst);
        Assert.assertFalse((wrapped instanceof List));
        Iterator<Integer> iter = wrapped.iterator();
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(1, ((int) (iter.next())));
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(2, ((int) (iter.next())));
        Assert.assertTrue(iter.hasNext());
        Assert.assertEquals(3, ((int) (iter.next())));
        Assert.assertFalse(iter.hasNext());
    }

    @Test
    public void limit() {
        Assert.assertEquals("[0]", com.google.common.collect.Iterators.toString(Iterators.limit(Arrays.asList(0, 1, 2, 3, 4).iterator(), IteratorsTest.EVEN)));
        Assert.assertEquals("[]", com.google.common.collect.Iterators.toString(Iterators.limit(Arrays.asList(1, 2, 4, 6).iterator(), IteratorsTest.EVEN)));
    }

    public static final CountingPredicate<Integer> EVEN = ( index, input) -> (input % 2) == 0;

    @Issue("JENKINS-51779")
    @Test
    public void skip() {
        List<Integer> lst = Iterators.sequence(1, 4);
        Iterator<Integer> it = lst.iterator();
        Iterators.skip(it, 0);
        Assert.assertEquals("[1, 2, 3]", com.google.common.collect.Iterators.toString(it));
        it = lst.iterator();
        Iterators.skip(it, 1);
        Assert.assertEquals("[2, 3]", com.google.common.collect.Iterators.toString(it));
        it = lst.iterator();
        Iterators.skip(it, 2);
        Assert.assertEquals("[3]", com.google.common.collect.Iterators.toString(it));
        it = lst.iterator();
        Iterators.skip(it, 3);
        Assert.assertEquals("[]", com.google.common.collect.Iterators.toString(it));
        it = lst.iterator();
        Iterators.skip(it, 4);
        Assert.assertEquals("[]", com.google.common.collect.Iterators.toString(it));
    }
}

