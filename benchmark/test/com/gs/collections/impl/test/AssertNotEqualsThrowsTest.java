/**
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.collections.impl.test;


import org.junit.Assert;
import org.junit.Test;


public class AssertNotEqualsThrowsTest {
    private static final Object OBJECT = new Object();

    private static final String STRING = "1";

    private static final double DOUBLE = 1.0;

    private static final double DOUBLE_DELTA = 0.5;

    private static final float FLOAT = 1.0F;

    private static final float FLOAT_DELTA = 0.5F;

    private static final long LONG = 1L;

    private static final boolean BOOLEAN = true;

    private static final byte BYTE = 1;

    private static final char CHAR = '1';

    private static final short SHORT = 1;

    private static final int INT = 1;

    @Test
    public void objectObject() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.OBJECT, AssertNotEqualsThrowsTest.OBJECT);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringObjectObject() {
        try {
            Verify.assertNotEquals("items", AssertNotEqualsThrowsTest.OBJECT, AssertNotEqualsThrowsTest.OBJECT);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringString() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.STRING, AssertNotEqualsThrowsTest.STRING);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertNotEquals(null, null);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringStringString() {
        try {
            Verify.assertNotEquals("strings", AssertNotEqualsThrowsTest.STRING, AssertNotEqualsThrowsTest.STRING);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void doubleDoubleDouble() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.DOUBLE, AssertNotEqualsThrowsTest.DOUBLE, AssertNotEqualsThrowsTest.DOUBLE_DELTA);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertNotEquals(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, AssertNotEqualsThrowsTest.DOUBLE_DELTA);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringDoubleDoubleDouble() {
        try {
            Verify.assertNotEquals("doubles", AssertNotEqualsThrowsTest.DOUBLE, AssertNotEqualsThrowsTest.DOUBLE, AssertNotEqualsThrowsTest.DOUBLE_DELTA);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void floatFloatFloat() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.FLOAT, AssertNotEqualsThrowsTest.FLOAT, AssertNotEqualsThrowsTest.FLOAT_DELTA);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
        try {
            Verify.assertNotEquals(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, AssertNotEqualsThrowsTest.FLOAT_DELTA);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringFloatFloatFloat() {
        try {
            Verify.assertNotEquals("floats", AssertNotEqualsThrowsTest.FLOAT, AssertNotEqualsThrowsTest.FLOAT, AssertNotEqualsThrowsTest.FLOAT_DELTA);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void longLong() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.LONG, AssertNotEqualsThrowsTest.LONG);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringLongLong() {
        try {
            Verify.assertNotEquals("longs", AssertNotEqualsThrowsTest.LONG, AssertNotEqualsThrowsTest.LONG);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void booleanBoolean() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.BOOLEAN, AssertNotEqualsThrowsTest.BOOLEAN);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringBooleanBoolean() {
        try {
            Verify.assertNotEquals("booleans", AssertNotEqualsThrowsTest.BOOLEAN, AssertNotEqualsThrowsTest.BOOLEAN);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void byteByte() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.BYTE, AssertNotEqualsThrowsTest.BYTE);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringByteByte() {
        try {
            Verify.assertNotEquals("bytes", AssertNotEqualsThrowsTest.BYTE, AssertNotEqualsThrowsTest.BYTE);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void charChar() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.CHAR, AssertNotEqualsThrowsTest.CHAR);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringCharChar() {
        try {
            Verify.assertNotEquals("chars", AssertNotEqualsThrowsTest.CHAR, AssertNotEqualsThrowsTest.CHAR);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void shortShort() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.SHORT, AssertNotEqualsThrowsTest.SHORT);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringShortShort() {
        try {
            Verify.assertNotEquals("shorts", AssertNotEqualsThrowsTest.SHORT, AssertNotEqualsThrowsTest.SHORT);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void intInt() {
        try {
            Verify.assertNotEquals(AssertNotEqualsThrowsTest.INT, AssertNotEqualsThrowsTest.INT);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }

    @Test
    public void stringIntInt() {
        try {
            Verify.assertNotEquals("ints", AssertNotEqualsThrowsTest.INT, AssertNotEqualsThrowsTest.INT);
            Assert.fail("AssertionError expected");
        } catch (AssertionError e) {
            Verify.assertContains(AssertNotEqualsThrowsTest.class.getName(), e.getStackTrace()[0].toString());
        }
    }
}

