/**
 * Protocol Buffers - Google's data interchange format
 */
/**
 * Copyright 2008 Google Inc.  All rights reserved.
 */
/**
 * https://developers.google.com/protocol-buffers/
 */
/**
 *
 */
/**
 * Redistribution and use in source and binary forms, with or without
 */
/**
 * modification, are permitted provided that the following conditions are
 */
/**
 * met:
 */
/**
 *
 */
/**
 * * Redistributions of source code must retain the above copyright
 */
/**
 * notice, this list of conditions and the following disclaimer.
 */
/**
 * * Redistributions in binary form must reproduce the above
 */
/**
 * copyright notice, this list of conditions and the following disclaimer
 */
/**
 * in the documentation and/or other materials provided with the
 */
/**
 * distribution.
 */
/**
 * * Neither the name of Google Inc. nor the names of its
 */
/**
 * contributors may be used to endorse or promote products derived from
 */
/**
 * this software without specific prior written permission.
 */
/**
 *
 */
/**
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 */
/**
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 */
/**
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 */
/**
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 */
/**
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 */
/**
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 */
/**
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 */
/**
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 */
/**
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 */
/**
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.google.protobuf;


import junit.framework.TestCase;


/**
 * Tests cases for {@link ByteString#isValidUtf8()}. This includes three
 * brute force tests that actually test every permutation of one byte, two byte,
 * and three byte sequences to ensure that the method produces the right result
 * for every possible byte encoding where "right" means it's consistent with
 * java's UTF-8 string encoding/decoding such that the method returns true for
 * any sequence that will round trip when converted to a String and then back to
 * bytes and will return false for any sequence that will not round trip.
 * See also {@link IsValidUtf8FourByteTest}. It also includes some
 * other more targeted tests.
 *
 * @author jonp@google.com (Jon Perlow)
 * @author martinrb@google.com (Martin Buchholz)
 */
public class IsValidUtf8Test extends TestCase {
    /**
     * Tests that round tripping of all two byte permutations work.
     */
    public void testIsValidUtf8_1Byte() {
        IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.LITERAL_FACTORY, 1, IsValidUtf8TestUtil.EXPECTED_ONE_BYTE_ROUNDTRIPPABLE_COUNT);
        IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.HEAP_NIO_FACTORY, 1, IsValidUtf8TestUtil.EXPECTED_ONE_BYTE_ROUNDTRIPPABLE_COUNT);
        IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.DIRECT_NIO_FACTORY, 1, IsValidUtf8TestUtil.EXPECTED_ONE_BYTE_ROUNDTRIPPABLE_COUNT);
    }

    /**
     * Tests that round tripping of all two byte permutations work.
     */
    public void testIsValidUtf8_2Bytes() {
        IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.LITERAL_FACTORY, 2, IsValidUtf8TestUtil.EXPECTED_TWO_BYTE_ROUNDTRIPPABLE_COUNT);
        IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.HEAP_NIO_FACTORY, 2, IsValidUtf8TestUtil.EXPECTED_TWO_BYTE_ROUNDTRIPPABLE_COUNT);
        IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.DIRECT_NIO_FACTORY, 2, IsValidUtf8TestUtil.EXPECTED_TWO_BYTE_ROUNDTRIPPABLE_COUNT);
    }

    /**
     * Tests that round tripping of all three byte permutations work.
     */
    public void testIsValidUtf8_3Bytes() {
        // Travis' OOM killer doesn't like this test
        if ((System.getenv("TRAVIS")) == null) {
            IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.LITERAL_FACTORY, 3, IsValidUtf8TestUtil.EXPECTED_THREE_BYTE_ROUNDTRIPPABLE_COUNT);
            IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.HEAP_NIO_FACTORY, 3, IsValidUtf8TestUtil.EXPECTED_THREE_BYTE_ROUNDTRIPPABLE_COUNT);
            IsValidUtf8TestUtil.testBytes(IsValidUtf8TestUtil.DIRECT_NIO_FACTORY, 3, IsValidUtf8TestUtil.EXPECTED_THREE_BYTE_ROUNDTRIPPABLE_COUNT);
        }
    }

    /**
     * Tests that round tripping of a sample of four byte permutations work.
     * All permutations are prohibitively expensive to test for automated runs;
     * {@link IsValidUtf8FourByteTest} is used for full coverage. This method
     * tests specific four-byte cases.
     */
    public void testIsValidUtf8_4BytesSamples() {
        // Valid 4 byte.
        assertValidUtf8(240, 164, 173, 162);
        // Bad trailing bytes
        assertInvalidUtf8(240, 164, 173, 127);
        assertInvalidUtf8(240, 164, 173, 192);
        // Special cases for byte2
        assertInvalidUtf8(240, 143, 173, 162);
        assertInvalidUtf8(244, 144, 173, 162);
    }

    /**
     * Tests some hard-coded test cases.
     */
    public void testSomeSequences() {
        // Empty
        TestCase.assertTrue(IsValidUtf8Test.asBytes("").isValidUtf8());
        // One-byte characters, including control characters
        TestCase.assertTrue(IsValidUtf8Test.asBytes("\u0000abc\u007f").isValidUtf8());
        // Two-byte characters
        TestCase.assertTrue(IsValidUtf8Test.asBytes("\u00a2\u00a2").isValidUtf8());
        // Three-byte characters
        TestCase.assertTrue(IsValidUtf8Test.asBytes("\u020ac\u020ac").isValidUtf8());
        // Four-byte characters
        TestCase.assertTrue(IsValidUtf8Test.asBytes("\u024b62\u024b62").isValidUtf8());
        // Mixed string
        TestCase.assertTrue(IsValidUtf8Test.asBytes("a\u020ac\u00a2b\\u024B62u020acc\u00a2de\u024b62").isValidUtf8());
        // Not a valid string
        assertInvalidUtf8((-1), 0, (-1), 0);
    }

    public void testShardsHaveExpectedRoundTrippables() {
        // A sanity check.
        int actual = 0;
        for (IsValidUtf8TestUtil.Shard shard : IsValidUtf8TestUtil.FOUR_BYTE_SHARDS) {
            actual += shard.expected;
        }
        TestCase.assertEquals(IsValidUtf8TestUtil.EXPECTED_FOUR_BYTE_ROUNDTRIPPABLE_COUNT, actual);
    }
}

