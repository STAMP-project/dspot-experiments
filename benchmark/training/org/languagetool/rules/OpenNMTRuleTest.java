/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2017 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules;


import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class OpenNMTRuleTest {
    @Test
    public void testGetFirstDiffPosition() throws IOException {
        OpenNMTRule r = new OpenNMTRule();
        testFirst(r, "", "", (-1));
        testFirst(r, "a", "", 0);
        testFirst(r, "a", "a", (-1));
        testFirst(r, "ab", "ab", (-1));
        testFirst(r, "ab", "ba", 0);
        testFirst(r, "xa", "xb", 1);
        testFirst(r, "xax", "xbx", 1);
        testFirst(r, "xxxa", "xxxb", 3);
        testFirst(r, "xxxa", "xxx", 3);
        testFirst(r, "xxx", "xxxb", 3);
        testFirst(r, "xxxyyy", "xxxbyyy", 3);
        testFirst(r, "This were a example.", "This were an example.", 11);
    }

    @Test
    public void testGetLastDiffPosition() throws IOException {
        OpenNMTRule r = new OpenNMTRule();
        testLast(r, "", "", (-1));
        testLast(r, "a", "", 1);
        testLast(r, "a", "a", (-1));
        testLast(r, "ba", "a", 1);
        testLast(r, "a", "ba", 0);
        testLast(r, "xba", "bba", 1);
        testLast(r, "bba", "xba", 1);
        testLast(r, "aa", "aa", (-1));
        testLast(r, "bbb", "bbb", (-1));
        testLast(r, "bb", "b", 1);
        testLast(r, "bbb", "b", 2);
        // testLast(r, "bbb", "bb", 3);
        testLast(r, "b", "bb", 0);
        testLast(r, "b", "bbb", 0);
        // testLast(r, "bb", "bbb", 2);
        testLast(r, "This were a example.", "This were an example.", 11);
    }

    @Test
    public void testGetLeftWordBoundary() throws IOException {
        OpenNMTRule r = new OpenNMTRule();
        Assert.assertThat(r.getLeftWordBoundary("foo", 0), CoreMatchers.is(0));
        Assert.assertThat(r.getLeftWordBoundary("foo", 2), CoreMatchers.is(0));
        Assert.assertThat(r.getLeftWordBoundary("foo bar", 0), CoreMatchers.is(0));
        Assert.assertThat(r.getLeftWordBoundary("foo. bar", 2), CoreMatchers.is(0));
        Assert.assertThat(r.getLeftWordBoundary("foo. bar", 5), CoreMatchers.is(5));
        Assert.assertThat(r.getLeftWordBoundary("foo bar", 4), CoreMatchers.is(4));
        Assert.assertThat(r.getLeftWordBoundary("foo bar", 6), CoreMatchers.is(4));
        Assert.assertThat(r.getLeftWordBoundary(".f??.", 3), CoreMatchers.is(1));
    }

    @Test
    public void testGetRightWordBoundary() throws IOException {
        OpenNMTRule r = new OpenNMTRule();
        Assert.assertThat(r.getRightWordBoundary("foo", 0), CoreMatchers.is(3));
        Assert.assertThat(r.getRightWordBoundary("foo", 3), CoreMatchers.is(3));
        Assert.assertThat(r.getRightWordBoundary("foo bar", 0), CoreMatchers.is(3));
        Assert.assertThat(r.getRightWordBoundary("foo.", 0), CoreMatchers.is(3));
        Assert.assertThat(r.getRightWordBoundary("f??.", 0), CoreMatchers.is(3));
        Assert.assertThat(r.getRightWordBoundary("foo bar", 4), CoreMatchers.is(7));
    }
}

