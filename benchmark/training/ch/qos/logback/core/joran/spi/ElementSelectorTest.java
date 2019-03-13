/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.core.joran.spi;


import org.junit.Assert;
import org.junit.Test;


/**
 * Test pattern manipulation code.
 *
 * @author Ceki Gulcu
 */
public class ElementSelectorTest {
    @Test
    public void test1() {
        ElementSelector p = new ElementSelector("a");
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("a", p.peekLast());
        Assert.assertEquals("a", p.get(0));
    }

    @Test
    public void testSuffix() {
        ElementSelector p = new ElementSelector("a/");
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("a", p.peekLast());
        Assert.assertEquals("a", p.get(0));
    }

    @Test
    public void test2() {
        ElementSelector p = new ElementSelector("a/b");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("b", p.peekLast());
        Assert.assertEquals("a", p.get(0));
        Assert.assertEquals("b", p.get(1));
    }

    @Test
    public void test3() {
        ElementSelector p = new ElementSelector("a123/b1234/cvvsdf");
        Assert.assertEquals(3, p.size());
        Assert.assertEquals("a123", p.get(0));
        Assert.assertEquals("b1234", p.get(1));
        Assert.assertEquals("cvvsdf", p.get(2));
    }

    @Test
    public void test4() {
        ElementSelector p = new ElementSelector("/a123/b1234/cvvsdf");
        Assert.assertEquals(3, p.size());
        Assert.assertEquals("a123", p.get(0));
        Assert.assertEquals("b1234", p.get(1));
        Assert.assertEquals("cvvsdf", p.get(2));
    }

    @Test
    public void test5() {
        ElementSelector p = new ElementSelector("//a");
        Assert.assertEquals(1, p.size());
        Assert.assertEquals("a", p.get(0));
    }

    @Test
    public void test6() {
        ElementSelector p = new ElementSelector("//a//b");
        Assert.assertEquals(2, p.size());
        Assert.assertEquals("a", p.get(0));
        Assert.assertEquals("b", p.get(1));
    }

    // test tail matching
    @Test
    public void testTailMatch() {
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("*");
            Assert.assertEquals(0, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a");
            ElementSelector ruleElementSelector = new ElementSelector("*/a");
            Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/A");
            ElementSelector ruleElementSelector = new ElementSelector("*/a");
            Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a");
            ElementSelector ruleElementSelector = new ElementSelector("*/A");
            Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("*/b");
            Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/B");
            ElementSelector ruleElementSelector = new ElementSelector("*/b");
            Assert.assertEquals(1, ruleElementSelector.getTailMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/b/c");
            ElementSelector ruleElementSelector = new ElementSelector("*/b/c");
            Assert.assertEquals(2, ruleElementSelector.getTailMatchLength(p));
        }
    }

    // test prefix matching
    @Test
    public void testPrefixMatch() {
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("/x/*");
            Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a");
            ElementSelector ruleElementSelector = new ElementSelector("/x/*");
            Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("/a/*");
            Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("/A/*");
            Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/A/b");
            ElementSelector ruleElementSelector = new ElementSelector("/a/*");
            Assert.assertEquals(1, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("/a/b/*");
            Assert.assertEquals(2, ruleElementSelector.getPrefixMatchLength(p));
        }
        {
            ElementPath p = new ElementPath("/a/b");
            ElementSelector ruleElementSelector = new ElementSelector("/*");
            Assert.assertEquals(0, ruleElementSelector.getPrefixMatchLength(p));
        }
    }
}

