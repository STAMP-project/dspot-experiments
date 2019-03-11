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
package org.apache.harmony.nio.tests.java.nio;


import java.nio.Buffer;
import java.nio.InvalidMarkException;
import junit.framework.TestCase;


/**
 * Tests a java.nio.Buffer instance.
 */
public class AbstractBufferTest extends TestCase {
    protected Buffer baseBuf;

    public void testCapacity() {
        TestCase.assertTrue((((0 <= (baseBuf.position())) && ((baseBuf.position()) <= (baseBuf.limit()))) && ((baseBuf.limit()) <= (baseBuf.capacity()))));
    }

    public void testClear() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        Buffer ret = baseBuf.clear();
        TestCase.assertSame(ret, baseBuf);
        TestCase.assertEquals(baseBuf.position(), 0);
        TestCase.assertEquals(baseBuf.limit(), baseBuf.capacity());
        try {
            baseBuf.reset();
            TestCase.fail("Should throw Exception");// $NON-NLS-1$S

        } catch (InvalidMarkException e) {
            // expected
        }
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    public void testFlip() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        Buffer ret = baseBuf.flip();
        TestCase.assertSame(ret, baseBuf);
        TestCase.assertEquals(baseBuf.position(), 0);
        TestCase.assertEquals(baseBuf.limit(), oldPosition);
        try {
            baseBuf.reset();
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (InvalidMarkException e) {
            // expected
        }
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    public void testHasRemaining() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        TestCase.assertEquals(baseBuf.hasRemaining(), ((baseBuf.position()) < (baseBuf.limit())));
        baseBuf.position(baseBuf.limit());
        TestCase.assertFalse(baseBuf.hasRemaining());
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    public void testIsReadOnly() {
        baseBuf.isReadOnly();
    }

    /* Class under test for int limit() */
    public void testLimit() {
        TestCase.assertTrue((((0 <= (baseBuf.position())) && ((baseBuf.position()) <= (baseBuf.limit()))) && ((baseBuf.limit()) <= (baseBuf.capacity()))));
    }

    /* Class under test for Buffer limit(int) */
    public void testLimitint() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        Buffer ret = baseBuf.limit(baseBuf.limit());
        TestCase.assertSame(ret, baseBuf);
        baseBuf.mark();
        baseBuf.limit(baseBuf.capacity());
        TestCase.assertEquals(baseBuf.limit(), baseBuf.capacity());
        // position should not change
        TestCase.assertEquals(baseBuf.position(), oldPosition);
        // mark should be valid
        baseBuf.reset();
        if ((baseBuf.capacity()) > 0) {
            baseBuf.limit(baseBuf.capacity());
            baseBuf.position(baseBuf.capacity());
            baseBuf.mark();
            baseBuf.limit(((baseBuf.capacity()) - 1));
            // position should be the new limit
            TestCase.assertEquals(baseBuf.position(), baseBuf.limit());
            // mark should be invalid
            try {
                baseBuf.reset();
                TestCase.fail("Should throw Exception");// $NON-NLS-1$

            } catch (InvalidMarkException e) {
                // expected
            }
        }
        try {
            baseBuf.limit((-1));
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            baseBuf.limit(((baseBuf.capacity()) + 1));
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // expected
        }
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    public void testMark() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        Buffer ret = baseBuf.mark();
        TestCase.assertSame(ret, baseBuf);
        baseBuf.mark();
        baseBuf.position(baseBuf.limit());
        baseBuf.reset();
        TestCase.assertEquals(baseBuf.position(), oldPosition);
        baseBuf.mark();
        baseBuf.position(baseBuf.limit());
        baseBuf.reset();
        TestCase.assertEquals(baseBuf.position(), oldPosition);
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    /* Class under test for int position() */
    public void testPosition() {
        TestCase.assertTrue((((0 <= (baseBuf.position())) && ((baseBuf.position()) <= (baseBuf.limit()))) && ((baseBuf.limit()) <= (baseBuf.capacity()))));
    }

    /* Class under test for Buffer position(int) */
    public void testPositionint() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        try {
            baseBuf.position((-1));
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            baseBuf.position(((baseBuf.limit()) + 1));
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (IllegalArgumentException e) {
            // expected
        }
        baseBuf.mark();
        baseBuf.position(baseBuf.position());
        baseBuf.reset();
        TestCase.assertEquals(baseBuf.position(), oldPosition);
        baseBuf.position(0);
        TestCase.assertEquals(baseBuf.position(), 0);
        baseBuf.position(baseBuf.limit());
        TestCase.assertEquals(baseBuf.position(), baseBuf.limit());
        if ((baseBuf.capacity()) > 0) {
            baseBuf.limit(baseBuf.capacity());
            baseBuf.position(baseBuf.limit());
            baseBuf.mark();
            baseBuf.position(((baseBuf.limit()) - 1));
            TestCase.assertEquals(baseBuf.position(), ((baseBuf.limit()) - 1));
            // mark should be invalid
            try {
                baseBuf.reset();
                TestCase.fail("Should throw Exception");// $NON-NLS-1$

            } catch (InvalidMarkException e) {
                // expected
            }
        }
        Buffer ret = baseBuf.position(0);
        TestCase.assertSame(ret, baseBuf);
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    public void testRemaining() {
        TestCase.assertEquals(baseBuf.remaining(), ((baseBuf.limit()) - (baseBuf.position())));
    }

    public void testReset() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        baseBuf.mark();
        baseBuf.position(baseBuf.limit());
        baseBuf.reset();
        TestCase.assertEquals(baseBuf.position(), oldPosition);
        baseBuf.mark();
        baseBuf.position(baseBuf.limit());
        baseBuf.reset();
        TestCase.assertEquals(baseBuf.position(), oldPosition);
        Buffer ret = baseBuf.reset();
        TestCase.assertSame(ret, baseBuf);
        baseBuf.clear();
        try {
            baseBuf.reset();
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (InvalidMarkException e) {
            // expected
        }
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }

    public void testRewind() {
        // save state
        int oldPosition = baseBuf.position();
        int oldLimit = baseBuf.limit();
        Buffer ret = baseBuf.rewind();
        TestCase.assertEquals(baseBuf.position(), 0);
        TestCase.assertSame(ret, baseBuf);
        try {
            baseBuf.reset();
            TestCase.fail("Should throw Exception");// $NON-NLS-1$

        } catch (InvalidMarkException e) {
            // expected
        }
        // restore state
        baseBuf.limit(oldLimit);
        baseBuf.position(oldPosition);
    }
}

