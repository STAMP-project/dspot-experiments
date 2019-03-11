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
package ch.qos.logback.classic.pattern;


import org.junit.Assert;
import org.junit.Test;


public class TargetLengthBasedClassNameAbbreviatorTest {
    @Test
    public void testShortName() {
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(100);
            String name = "hello";
            Assert.assertEquals(name, abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(100);
            String name = "hello.world";
            Assert.assertEquals(name, abbreviator.abbreviate(name));
        }
    }

    @Test
    public void testNoDot() {
        TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
        String name = "hello";
        Assert.assertEquals(name, abbreviator.abbreviate(name));
    }

    @Test
    public void testOneDot() {
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "hello.world";
            Assert.assertEquals("h.world", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "h.world";
            Assert.assertEquals("h.world", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = ".world";
            Assert.assertEquals(".world", abbreviator.abbreviate(name));
        }
    }

    @Test
    public void testTwoDot() {
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "com.logback.Foobar";
            Assert.assertEquals("c.l.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "c.logback.Foobar";
            Assert.assertEquals("c.l.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "c..Foobar";
            Assert.assertEquals("c..Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "..Foobar";
            Assert.assertEquals("..Foobar", abbreviator.abbreviate(name));
        }
    }

    @Test
    public void test3Dot() {
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "com.logback.xyz.Foobar";
            Assert.assertEquals("c.l.x.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(13);
            String name = "com.logback.xyz.Foobar";
            Assert.assertEquals("c.l.x.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(14);
            String name = "com.logback.xyz.Foobar";
            Assert.assertEquals("c.l.xyz.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(15);
            String name = "com.logback.alligator.Foobar";
            Assert.assertEquals("c.l.a.Foobar", abbreviator.abbreviate(name));
        }
    }

    @Test
    public void testXDot() {
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(21);
            String name = "com.logback.wombat.alligator.Foobar";
            Assert.assertEquals("c.l.w.a.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(22);
            String name = "com.logback.wombat.alligator.Foobar";
            Assert.assertEquals("c.l.w.alligator.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(1);
            String name = "com.logback.wombat.alligator.tomato.Foobar";
            Assert.assertEquals("c.l.w.a.t.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(21);
            String name = "com.logback.wombat.alligator.tomato.Foobar";
            Assert.assertEquals("c.l.w.a.tomato.Foobar", abbreviator.abbreviate(name));
        }
        {
            TargetLengthBasedClassNameAbbreviator abbreviator = new TargetLengthBasedClassNameAbbreviator(29);
            String name = "com.logback.wombat.alligator.tomato.Foobar";
            Assert.assertEquals("c.l.w.alligator.tomato.Foobar", abbreviator.abbreviate(name));
        }
    }
}

