/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.harmony.luni.tests.java.util;


import java.util.UUID;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;


public class UUIDTest extends TestCase {
    /**
     *
     *
     * @see UUID#UUID(long, long)
     */
    public void test_ConstructorJJ() {
        UUID uuid = new UUID(-568210367123287600L, -6384696206158828554L);
        TestCase.assertEquals(2, uuid.variant());
        TestCase.assertEquals(1, uuid.version());
        TestCase.assertEquals(130742845922168750L, uuid.timestamp());
        TestCase.assertEquals(130742845922168750L, uuid.timestamp());
        TestCase.assertEquals(10085, uuid.clockSequence());
        TestCase.assertEquals(690568981494L, uuid.node());
    }

    /**
     *
     *
     * @see UUID#getLeastSignificantBits()
     */
    public void test_getLeastSignificantBits() {
        UUID uuid = new UUID(0, 0);
        TestCase.assertEquals(0, uuid.getLeastSignificantBits());
        uuid = new UUID(0, Long.MIN_VALUE);
        TestCase.assertEquals(Long.MIN_VALUE, uuid.getLeastSignificantBits());
        uuid = new UUID(0, Long.MAX_VALUE);
        TestCase.assertEquals(Long.MAX_VALUE, uuid.getLeastSignificantBits());
    }

    /**
     *
     *
     * @see UUID#getMostSignificantBits()
     */
    public void test_getMostSignificantBits() {
        UUID uuid = new UUID(0, 0);
        TestCase.assertEquals(0, uuid.getMostSignificantBits());
        uuid = new UUID(Long.MIN_VALUE, 0);
        TestCase.assertEquals(Long.MIN_VALUE, uuid.getMostSignificantBits());
        uuid = new UUID(Long.MAX_VALUE, 0);
        TestCase.assertEquals(Long.MAX_VALUE, uuid.getMostSignificantBits());
    }

    /**
     *
     *
     * @see UUID#version()
     */
    public void test_version() {
        UUID uuid = new UUID(0, 0);
        TestCase.assertEquals(0, uuid.version());
        uuid = new UUID(4096L, 0);
        TestCase.assertEquals(1, uuid.version());
        uuid = new UUID(8192L, 0);
        TestCase.assertEquals(2, uuid.version());
        uuid = new UUID(12288L, 0);
        TestCase.assertEquals(3, uuid.version());
        uuid = new UUID(16384L, 0);
        TestCase.assertEquals(4, uuid.version());
        uuid = new UUID(20480L, 0);
        TestCase.assertEquals(5, uuid.version());
    }

    /**
     *
     *
     * @see UUID#variant()
     */
    public void test_variant() {
        UUID uuid = new UUID(0, 0L);
        TestCase.assertEquals(0, uuid.variant());
        uuid = new UUID(0, 8070450532247928832L);
        TestCase.assertEquals(0, uuid.variant());
        uuid = new UUID(0, 3458764513820540928L);
        TestCase.assertEquals(0, uuid.variant());
        uuid = new UUID(0, 1152921504606846976L);
        TestCase.assertEquals(0, uuid.variant());
        uuid = new UUID(0, -9223372036854775808L);
        TestCase.assertEquals(2, uuid.variant());
        uuid = new UUID(0, -5764607523034234880L);
        TestCase.assertEquals(2, uuid.variant());
        uuid = new UUID(0, -6917529027641081856L);
        TestCase.assertEquals(2, uuid.variant());
        uuid = new UUID(0, -8070450532247928832L);
        TestCase.assertEquals(2, uuid.variant());
        uuid = new UUID(0, -4611686018427387904L);
        TestCase.assertEquals(6, uuid.variant());
        uuid = new UUID(0, -3458764513820540928L);
        TestCase.assertEquals(6, uuid.variant());
        uuid = new UUID(0, -2305843009213693952L);
        TestCase.assertEquals(7, uuid.variant());
        uuid = new UUID(0, -1152921504606846976L);
        TestCase.assertEquals(7, uuid.variant());
    }

    /**
     *
     *
     * @see UUID#timestamp()
     */
    public void test_timestamp() {
        UUID uuid = new UUID(4096L, -9223372036854775808L);
        TestCase.assertEquals(0, uuid.timestamp());
        uuid = new UUID(8608480567158444851L, -9223372036854775808L);
        TestCase.assertEquals(230621831490926455L, uuid.timestamp());
        uuid = new UUID(0L, -9223372036854775808L);
        try {
            uuid.timestamp();
            TestCase.fail("No UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
        uuid = new UUID(8192L, -9223372036854775808L);
        try {
            uuid.timestamp();
            TestCase.fail("No UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     *
     *
     * @see UUID#clockSequence()
     */
    public void test_clockSequence() {
        UUID uuid = new UUID(4096L, -9223372036854775808L);
        TestCase.assertEquals(0, uuid.clockSequence());
        uuid = new UUID(4096L, -8070732007224639488L);
        TestCase.assertEquals(4095, uuid.clockSequence());
        uuid = new UUID(4096L, -4611967493404098560L);
        TestCase.assertEquals(16383, uuid.clockSequence());
        uuid = new UUID(0L, -9223372036854775808L);
        try {
            uuid.clockSequence();
            TestCase.fail("No UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
        uuid = new UUID(8192L, -9223372036854775808L);
        try {
            uuid.clockSequence();
            TestCase.fail("No UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     *
     *
     * @see UUID#node()
     */
    public void test_node() {
        UUID uuid = new UUID(4096L, -9223372036854775808L);
        TestCase.assertEquals(0, uuid.node());
        uuid = new UUID(4096L, -9223090561878065153L);
        TestCase.assertEquals(281474976710655L, uuid.node());
        uuid = new UUID(0L, -9223372036854775808L);
        try {
            uuid.node();
            TestCase.fail("No UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
        uuid = new UUID(8192L, -9223372036854775808L);
        try {
            uuid.node();
            TestCase.fail("No UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
        }
    }

    /**
     *
     *
     * @see UUID#compareTo(UUID)
     */
    public void test_compareTo() {
        UUID uuid1 = new UUID(0, 0);
        TestCase.assertEquals(0, uuid1.compareTo(uuid1));
        UUID uuid2 = new UUID(1, 0);
        TestCase.assertEquals((-1), uuid1.compareTo(uuid2));
        TestCase.assertEquals(1, uuid2.compareTo(uuid1));
        uuid2 = new UUID(0, 1);
        TestCase.assertEquals((-1), uuid1.compareTo(uuid2));
        TestCase.assertEquals(1, uuid2.compareTo(uuid1));
    }

    /**
     *
     *
     * @see UUID#hashCode()
     */
    public void test_hashCode() {
        UUID uuid = new UUID(0, 0);
        TestCase.assertEquals(0, uuid.hashCode());
        uuid = new UUID(123, 123);
        UUID uuidClone = new UUID(123, 123);
        TestCase.assertEquals(uuid.hashCode(), uuidClone.hashCode());
    }

    /**
     *
     *
     * @see UUID#equals(Object)
     */
    public void test_equalsObject() {
        UUID uuid1 = new UUID(0, 0);
        TestCase.assertEquals(uuid1, uuid1);
        TestCase.assertFalse(uuid1.equals(null));
        TestCase.assertFalse(uuid1.equals("NOT A UUID"));
        UUID uuid2 = new UUID(0, 0);
        TestCase.assertEquals(uuid1, uuid2);
        TestCase.assertEquals(uuid2, uuid1);
        uuid1 = new UUID(-568210367123287600L, -6384696206158828554L);
        uuid2 = new UUID(-568210367123287600L, -6384696206158828554L);
        TestCase.assertEquals(uuid1, uuid2);
        TestCase.assertEquals(uuid2, uuid1);
        uuid2 = new UUID(-568210367123287600L, -6384696206158828553L);
        TestCase.assertFalse(uuid1.equals(uuid2));
        TestCase.assertFalse(uuid2.equals(uuid1));
    }

    /**
     *
     *
     * @see UUID#toString()
     */
    public void test_toString() {
        UUID uuid = new UUID(-568210367123287600L, -6384696206158828554L);
        String actual = uuid.toString();
        TestCase.assertEquals("f81d4fae-7dec-11d0-a765-00a0c91e6bf6", actual);
        uuid = new UUID(4096L, -9223372036854775808L);
        actual = uuid.toString();
        TestCase.assertEquals("00000000-0000-1000-8000-000000000000", actual);
    }

    /**
     *
     *
     * @unknown serialization/deserialization.
     */
    public void testSerializationSelf() throws Exception {
        SerializationTest.verifySelf(new UUID(-568210367123287600L, -6384696206158828554L));
    }

    /**
     *
     *
     * @unknown serialization/deserialization compatibility with RI.
     */
    public void testSerializationCompatibility() throws Exception {
        SerializationTest.verifyGolden(this, new UUID(-568210367123287600L, -6384696206158828554L));
    }

    /**
     *
     *
     * @see UUID#randomUUID()
     */
    public void test_randomUUID() {
        UUID uuid = UUID.randomUUID();
        TestCase.assertEquals(2, uuid.variant());
        TestCase.assertEquals(4, uuid.version());
    }

    /**
     *
     *
     * @see UUID#nameUUIDFromBytes(byte[])
     */
    public void test_nameUUIDFromBytes() throws Exception {
        byte[] name = new byte[]{ ((byte) (107)), ((byte) (167)), ((byte) (184)), ((byte) (17)), ((byte) (157)), ((byte) (173)), ((byte) (17)), ((byte) (209)), ((byte) (128)), ((byte) (180)), ((byte) (0)), ((byte) (192)), ((byte) (79)), ((byte) (212)), ((byte) (48)), ((byte) (200)) };
        UUID uuid = UUID.nameUUIDFromBytes(name);
        TestCase.assertEquals(2, uuid.variant());
        TestCase.assertEquals(3, uuid.version());
        TestCase.assertEquals(-5767591888853461179L, uuid.getLeastSignificantBits());
        TestCase.assertEquals(1499058437454118826L, uuid.getMostSignificantBits());
        uuid = UUID.nameUUIDFromBytes(new byte[0]);
        TestCase.assertEquals(2, uuid.variant());
        TestCase.assertEquals(3, uuid.version());
        TestCase.assertEquals(-6232971331865394562L, uuid.getLeastSignificantBits());
        TestCase.assertEquals(-3162216497309273596L, uuid.getMostSignificantBits());
        try {
            UUID.nameUUIDFromBytes(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
    }

    /**
     *
     *
     * @see UUID#fromString(String)
     */
    public void test_fromString() {
        UUID actual = UUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6");
        UUID expected = new UUID(-568210367123287600L, -6384696206158828554L);
        TestCase.assertEquals(expected, actual);
        TestCase.assertEquals(2, actual.variant());
        TestCase.assertEquals(1, actual.version());
        TestCase.assertEquals(130742845922168750L, actual.timestamp());
        TestCase.assertEquals(10085, actual.clockSequence());
        TestCase.assertEquals(690568981494L, actual.node());
        actual = UUID.fromString("00000000-0000-1000-8000-000000000000");
        expected = new UUID(4096L, -9223372036854775808L);
        TestCase.assertEquals(expected, actual);
        TestCase.assertEquals(2, actual.variant());
        TestCase.assertEquals(1, actual.version());
        TestCase.assertEquals(0L, actual.timestamp());
        TestCase.assertEquals(0, actual.clockSequence());
        TestCase.assertEquals(0L, actual.node());
        try {
            UUID.fromString(null);
            TestCase.fail("No NPE");
        } catch (NullPointerException e) {
        }
        try {
            UUID.fromString("");
            TestCase.fail("No IAE");
        } catch (IllegalArgumentException e) {
        }
        try {
            UUID.fromString("f81d4fae_7dec-11d0-a765-00a0c91e6bf6");
            TestCase.fail("No IAE");
        } catch (IllegalArgumentException e) {
        }
        try {
            UUID.fromString("f81d4fae-7dec_11d0-a765-00a0c91e6bf6");
            TestCase.fail("No IAE");
        } catch (IllegalArgumentException e) {
        }
        try {
            UUID.fromString("f81d4fae-7dec-11d0_a765-00a0c91e6bf6");
            TestCase.fail("No IAE");
        } catch (IllegalArgumentException e) {
        }
        try {
            UUID.fromString("f81d4fae-7dec-11d0-a765_00a0c91e6bf6");
            TestCase.fail("No IAE");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     *
     *
     * @unknown java.util.UUID#fromString(String)
     */
    public void test_fromString_LString_Exception() {
        UUID uuid = UUID.fromString("0-0-0-0-0");
        try {
            uuid = UUID.fromString("0-0-0-0-");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("-0-0-0-0-0");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("-0-0-0-0");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("-0-0-0-");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("0--0-0-0");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("0-0-0-0-");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("-1-0-0-0-0");
            TestCase.fail("should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
        uuid = UUID.fromString("123456789-0-0-0-0");
        TestCase.assertEquals(2541551402828693504L, uuid.getMostSignificantBits());
        TestCase.assertEquals(0L, uuid.getLeastSignificantBits());
        uuid = UUID.fromString("111123456789-0-0-0-0");
        TestCase.assertEquals(2541551402828693504L, uuid.getMostSignificantBits());
        TestCase.assertEquals(0L, uuid.getLeastSignificantBits());
        uuid = UUID.fromString("7fffffffffffffff-0-0-0-0");
        TestCase.assertEquals(-4294967296L, uuid.getMostSignificantBits());
        TestCase.assertEquals(0L, uuid.getLeastSignificantBits());
        try {
            uuid = UUID.fromString("8000000000000000-0-0-0-0");
            TestCase.fail("should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // expected
        }
        uuid = UUID.fromString("7fffffffffffffff-7fffffffffffffff-7fffffffffffffff-0-0");
        TestCase.assertEquals(-1L, uuid.getMostSignificantBits());
        TestCase.assertEquals(0L, uuid.getLeastSignificantBits());
        uuid = UUID.fromString("0-0-0-7fffffffffffffff-7fffffffffffffff");
        TestCase.assertEquals(0L, uuid.getMostSignificantBits());
        TestCase.assertEquals(-1L, uuid.getLeastSignificantBits());
        try {
            uuid = UUID.fromString("0-0-0-8000000000000000-0");
            TestCase.fail("should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // expected
        }
        try {
            uuid = UUID.fromString("0-0-0-0-8000000000000000");
            TestCase.fail("should throw NumberFormatException");
        } catch (NumberFormatException e) {
            // expected
        }
    }
}

