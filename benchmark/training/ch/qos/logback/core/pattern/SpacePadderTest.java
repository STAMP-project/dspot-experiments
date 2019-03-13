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
package ch.qos.logback.core.pattern;


import org.junit.Assert;
import org.junit.Test;


public class SpacePadderTest {
    @Test
    public void smoke() {
        {
            StringBuilder buf = new StringBuilder();
            String s = "a";
            SpacePadder.leftPad(buf, s, 4);
            Assert.assertEquals("   a", buf.toString());
        }
        {
            StringBuilder buf = new StringBuilder();
            String s = "a";
            SpacePadder.rightPad(buf, s, 4);
            Assert.assertEquals("a   ", buf.toString());
        }
    }

    @Test
    public void nullString() {
        String s = null;
        {
            StringBuilder buf = new StringBuilder();
            SpacePadder.leftPad(buf, s, 2);
            Assert.assertEquals("  ", buf.toString());
        }
        {
            StringBuilder buf = new StringBuilder();
            SpacePadder.rightPad(buf, s, 2);
            Assert.assertEquals("  ", buf.toString());
        }
    }

    @Test
    public void longString() {
        {
            StringBuilder buf = new StringBuilder();
            String s = "abc";
            SpacePadder.leftPad(buf, s, 2);
            Assert.assertEquals(s, buf.toString());
        }
        {
            StringBuilder buf = new StringBuilder();
            String s = "abc";
            SpacePadder.rightPad(buf, s, 2);
            Assert.assertEquals(s, buf.toString());
        }
    }

    @Test
    public void lengthyPad() {
        {
            StringBuilder buf = new StringBuilder();
            String s = "abc";
            SpacePadder.leftPad(buf, s, 33);
            Assert.assertEquals("                              abc", buf.toString());
        }
        {
            StringBuilder buf = new StringBuilder();
            String s = "abc";
            SpacePadder.rightPad(buf, s, 33);
            Assert.assertEquals("abc                              ", buf.toString());
        }
    }
}

