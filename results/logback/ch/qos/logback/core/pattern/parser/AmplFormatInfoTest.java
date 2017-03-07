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


package ch.qos.logback.core.pattern.parser;


public class AmplFormatInfoTest {
    @org.junit.Test
    public void testEndingInDot() {
        try {
            ch.qos.logback.core.pattern.FormatInfo.valueOf("45.");
            org.junit.Assert.fail("45. is not a valid format info string");
        } catch (java.lang.IllegalArgumentException iae) {
            // OK
        }
    }

    @org.junit.Test
    public void testBasic() {
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("45");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(45);
            org.junit.Assert.assertEquals(witness, fi);
        }
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("4.5");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(4);
            witness.setMax(5);
            org.junit.Assert.assertEquals(witness, fi);
        }
    }

    @org.junit.Test
    public void testRightPad() {
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("-40");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(40);
            witness.setLeftPad(false);
            org.junit.Assert.assertEquals(witness, fi);
        }
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("-12.5");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(12);
            witness.setMax(5);
            witness.setLeftPad(false);
            org.junit.Assert.assertEquals(witness, fi);
        }
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("-14.-5");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(14);
            witness.setMax(5);
            witness.setLeftPad(false);
            witness.setLeftTruncate(false);
            org.junit.Assert.assertEquals(witness, fi);
        }
    }

    @org.junit.Test
    public void testMinOnly() {
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("49");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(49);
            org.junit.Assert.assertEquals(witness, fi);
        }
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf("-587");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMin(587);
            witness.setLeftPad(false);
            org.junit.Assert.assertEquals(witness, fi);
        }
    }

    @org.junit.Test
    public void testMaxOnly() {
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf(".49");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMax(49);
            org.junit.Assert.assertEquals(witness, fi);
        }
        {
            ch.qos.logback.core.pattern.FormatInfo fi = ch.qos.logback.core.pattern.FormatInfo.valueOf(".-5");
            ch.qos.logback.core.pattern.FormatInfo witness = new ch.qos.logback.core.pattern.FormatInfo();
            witness.setMax(5);
            witness.setLeftTruncate(false);
            org.junit.Assert.assertEquals(witness, fi);
        }
    }
}

