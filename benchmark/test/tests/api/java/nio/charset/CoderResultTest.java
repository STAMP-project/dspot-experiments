/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tests.api.java.nio.charset;


import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.charset.CoderResult;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import junit.framework.TestCase;


/**
 * Test class java.nio.charset.CoderResult.
 */
public class CoderResultTest extends TestCase {
    /* Test the constant OVERFLOW and UNDERFLOW. */
    public void testConstants() throws Exception {
        TestCase.assertNotSame(CoderResult.OVERFLOW, CoderResult.UNDERFLOW);
        TestCase.assertNotNull(CoderResult.OVERFLOW);
        TestCase.assertFalse(CoderResult.OVERFLOW.isError());
        TestCase.assertFalse(CoderResult.OVERFLOW.isMalformed());
        TestCase.assertFalse(CoderResult.OVERFLOW.isUnderflow());
        TestCase.assertFalse(CoderResult.OVERFLOW.isUnmappable());
        TestCase.assertTrue(CoderResult.OVERFLOW.isOverflow());
        TestCase.assertTrue(((CoderResult.OVERFLOW.toString().indexOf("OVERFLOW")) != (-1)));
        try {
            CoderResult.OVERFLOW.throwException();
            TestCase.fail("Should throw BufferOverflowException");
        } catch (BufferOverflowException ex) {
            // expected
        }
        try {
            CoderResult.OVERFLOW.length();
            TestCase.fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        TestCase.assertNotNull(CoderResult.UNDERFLOW);
        TestCase.assertFalse(CoderResult.UNDERFLOW.isError());
        TestCase.assertFalse(CoderResult.UNDERFLOW.isMalformed());
        TestCase.assertTrue(CoderResult.UNDERFLOW.isUnderflow());
        TestCase.assertFalse(CoderResult.UNDERFLOW.isUnmappable());
        TestCase.assertFalse(CoderResult.UNDERFLOW.isOverflow());
        TestCase.assertTrue(((CoderResult.UNDERFLOW.toString().indexOf("UNDERFLOW")) != (-1)));
        try {
            CoderResult.UNDERFLOW.throwException();
            TestCase.fail("Should throw BufferOverflowException");
        } catch (BufferUnderflowException ex) {
            // expected
        }
        try {
            CoderResult.UNDERFLOW.length();
            TestCase.fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
    }

    /**
     * Test method isError().
     */
    public void testIsError() {
        TestCase.assertFalse(CoderResult.UNDERFLOW.isError());
        TestCase.assertFalse(CoderResult.OVERFLOW.isError());
        TestCase.assertTrue(CoderResult.malformedForLength(1).isError());
        TestCase.assertTrue(CoderResult.unmappableForLength(1).isError());
    }

    /**
     * Test method isMalformed().
     */
    public void testIsMalformed() {
        TestCase.assertFalse(CoderResult.UNDERFLOW.isMalformed());
        TestCase.assertFalse(CoderResult.OVERFLOW.isMalformed());
        TestCase.assertTrue(CoderResult.malformedForLength(1).isMalformed());
        TestCase.assertFalse(CoderResult.unmappableForLength(1).isMalformed());
    }

    /**
     * Test method isMalformed().
     */
    public void testIsUnmappable() {
        TestCase.assertFalse(CoderResult.UNDERFLOW.isUnmappable());
        TestCase.assertFalse(CoderResult.OVERFLOW.isUnmappable());
        TestCase.assertFalse(CoderResult.malformedForLength(1).isUnmappable());
        TestCase.assertTrue(CoderResult.unmappableForLength(1).isUnmappable());
    }

    /**
     * Test method isOverflow().
     */
    public void testIsOverflow() {
        TestCase.assertFalse(CoderResult.UNDERFLOW.isOverflow());
        TestCase.assertTrue(CoderResult.OVERFLOW.isOverflow());
        TestCase.assertFalse(CoderResult.malformedForLength(1).isOverflow());
        TestCase.assertFalse(CoderResult.unmappableForLength(1).isOverflow());
    }

    /**
     * Test method isUnderflow().
     */
    public void testIsUnderflow() {
        TestCase.assertTrue(CoderResult.UNDERFLOW.isUnderflow());
        TestCase.assertFalse(CoderResult.OVERFLOW.isUnderflow());
        TestCase.assertFalse(CoderResult.malformedForLength(1).isUnderflow());
        TestCase.assertFalse(CoderResult.unmappableForLength(1).isUnderflow());
    }

    /**
     * Test method length().
     */
    public void testLength() {
        try {
            CoderResult.UNDERFLOW.length();
            TestCase.fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        try {
            CoderResult.OVERFLOW.length();
            TestCase.fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // expected
        }
        TestCase.assertEquals(CoderResult.malformedForLength(1).length(), 1);
        TestCase.assertEquals(CoderResult.unmappableForLength(1).length(), 1);
    }

    /**
     * Test method malformedForLength(int).
     */
    public void testMalformedForLength() {
        TestCase.assertNotNull(CoderResult.malformedForLength(Integer.MAX_VALUE));
        TestCase.assertNotNull(CoderResult.malformedForLength(1));
        TestCase.assertSame(CoderResult.malformedForLength(1), CoderResult.malformedForLength(1));
        TestCase.assertNotSame(CoderResult.malformedForLength(1), CoderResult.unmappableForLength(1));
        TestCase.assertNotSame(CoderResult.malformedForLength(2), CoderResult.malformedForLength(1));
        try {
            CoderResult.malformedForLength((-1));
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            CoderResult.malformedForLength(0);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Test method unmappableForLength(int).
     */
    public void testUnmappableForLength() {
        TestCase.assertNotNull(CoderResult.unmappableForLength(Integer.MAX_VALUE));
        TestCase.assertNotNull(CoderResult.unmappableForLength(1));
        TestCase.assertSame(CoderResult.unmappableForLength(1), CoderResult.unmappableForLength(1));
        TestCase.assertNotSame(CoderResult.unmappableForLength(2), CoderResult.unmappableForLength(1));
        try {
            CoderResult.unmappableForLength((-1));
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            CoderResult.unmappableForLength(0);
            TestCase.fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Test method throwException().
     */
    public void testThrowException() throws Exception {
        try {
            CoderResult.OVERFLOW.throwException();
            TestCase.fail("Should throw BufferOverflowException");
        } catch (BufferOverflowException ex) {
            // expected
        }
        try {
            CoderResult.UNDERFLOW.throwException();
            TestCase.fail("Should throw BufferOverflowException");
        } catch (BufferUnderflowException ex) {
            // expected
        }
        try {
            CoderResult.malformedForLength(1).throwException();
            TestCase.fail("Should throw MalformedInputException");
        } catch (MalformedInputException ex) {
            TestCase.assertEquals(ex.getInputLength(), 1);
        }
        try {
            CoderResult.unmappableForLength(1).throwException();
            TestCase.fail("Should throw UnmappableCharacterException");
        } catch (UnmappableCharacterException ex) {
            TestCase.assertEquals(ex.getInputLength(), 1);
        }
    }

    /**
     * Test method toString().
     */
    public void testToString() throws Exception {
        TestCase.assertTrue(((CoderResult.OVERFLOW.toString().indexOf("OVERFLOW")) != (-1)));
        TestCase.assertTrue(((CoderResult.UNDERFLOW.toString().indexOf("UNDERFLOW")) != (-1)));
        TestCase.assertTrue(((CoderResult.malformedForLength(666).toString().indexOf("666")) != (-1)));
        TestCase.assertTrue(((CoderResult.unmappableForLength(666).toString().indexOf("666")) != (-1)));
    }
}

