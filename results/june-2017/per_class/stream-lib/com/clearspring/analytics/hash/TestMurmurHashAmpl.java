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


package com.clearspring.analytics.hash;


/**
 * @author epollan
 */
public class TestMurmurHashAmpl {
    @org.junit.Test
    public void testHashByteArrayOverload() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    @org.junit.Test
    public void testHash64ByteArrayOverload() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf39() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_16 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_15 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf39__13 = // StatementAdderMethod cloned existing statement
vc_15.hash(vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf39__13, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test
    public void testHash64ByteArrayOverload_literalMutation5() {
        java.lang.String input = "hshthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(input, "hshthis");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf35() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_17 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_14 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // StatementAdderMethod cloned existing statement
        vc_14.hash(vc_17);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf46() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_19 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf46__11 = // StatementAdderMethod cloned existing statement
vc_19.hashLong(hashOfString);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf46__11, 1003210458);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf68() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_34 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_31);
        // StatementAdderMethod cloned existing statement
        vc_31.hash64(vc_34);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf43() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_20 = 1861202967246610446L;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_20, 1861202967246610446L);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_18 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf43__13 = // StatementAdderMethod cloned existing statement
vc_18.hashLong(vc_20);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf43__13, 56140174);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf34() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_14 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf34__11 = // StatementAdderMethod cloned existing statement
vc_14.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf34__11, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf23() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_8 = 1231906488;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_8, 1231906488);
        // StatementAdderOnAssert create random local variable
        byte[] vc_7 = new byte []{58,96};
        // AssertGenerator add assertion
        byte[] array_665537158 = new byte[]{58, 96};
	byte[] array_1095027691 = (byte[])vc_7;
	for(int ii = 0; ii <array_665537158.length; ii++) {
		org.junit.Assert.assertEquals(array_665537158[ii], array_1095027691[ii]);
	};
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_5 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf23__15 = // StatementAdderMethod cloned existing statement
vc_5.hash(vc_7, vc_8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf23__15, -1529859249);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf8() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_3 = new byte []{106};
        // AssertGenerator add assertion
        byte[] array_834046276 = new byte[]{106};
	byte[] array_1655146783 = (byte[])vc_3;
	for(int ii = 0; ii <array_834046276.length; ii++) {
		org.junit.Assert.assertEquals(array_834046276[ii], array_1655146783[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_0 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf8__13 = // StatementAdderMethod cloned existing statement
vc_0.hash(vc_3);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf8__13, -473270964);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf72() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_33 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_33);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_32 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf72__13 = // StatementAdderMethod cloned existing statement
vc_32.hash64(vc_33);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf72__13, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test
    public void testHash64ByteArrayOverload_literalMutation2() {
        java.lang.String input = "hash}this";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(input, "hash}this");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf55_failAssert8_literalMutation2714() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashth^is";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "hashth^is");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1080279897600799724L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_25 = 1640902160;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_25, 1640902160);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_22 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_22.hash64(inputBytes, vc_25);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf55 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf49_failAssert5_literalMutation2686() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -7065322190441338459L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_25 = 1640902160;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_25, 1640902160);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_21 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_21);
            // StatementAdderMethod cloned existing statement
            vc_21.hash64(inputBytes, vc_25);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf49 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf56_failAssert9_literalMutation2728() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hshthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "hshthis");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, 6901914564845240700L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_25 = 1640902160;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_25, 1640902160);
            // StatementAdderOnAssert create random local variable
            byte[] vc_24 = new byte []{124,24,78,76};
            // AssertGenerator add assertion
            byte[] array_1295280108 = new byte[]{124, 24, 78, 76};
	byte[] array_1090438284 = (byte[])vc_24;
	for(int ii = 0; ii <array_1295280108.length; ii++) {
		org.junit.Assert.assertEquals(array_1295280108[ii], array_1090438284[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_22 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_22.hash64(vc_24, vc_25);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf56 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf15_failAssert2_literalMutation2659() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -8896273065425798843L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_8 = 1231906489;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_8, 1231906489);
            // StatementAdderOnAssert create null value
            byte[] vc_6 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_6);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_4 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_4);
            // StatementAdderMethod cloned existing statement
            vc_4.hash(vc_6, vc_8);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf15 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf68_cf2332_failAssert0_literalMutation4238() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashtahis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "hashtahis");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, 2463909794579015713L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_34 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_31);
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_31;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_13_0);
            // StatementAdderMethod cloned existing statement
            vc_31.hash64(vc_34);
            // StatementAdderOnAssert create random local variable
            int vc_778 = 1726547684;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_778, 1726547684);
            // StatementAdderOnAssert create null value
            byte[] vc_776 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_776);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_775 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_775.hash(vc_776, vc_778);
            // MethodAssertGenerator build local variable
            Object o_25_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf68_cf2332 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf8_cf527_failAssert3_literalMutation4249() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = -473270964;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_1, -473270964);
            java.lang.String input = "ma)Zp`f_";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "ma)Zp`f_");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -2145221132657896081L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            byte[] vc_3 = new byte []{106};
            // AssertGenerator add assertion
            byte[] array_384801557 = new byte[]{106};
	byte[] array_212307035 = (byte[])vc_3;
	for(int ii = 0; ii <array_384801557.length; ii++) {
		org.junit.Assert.assertEquals(array_384801557[ii], array_212307035[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_834046276 = new byte[]{106};
	byte[] array_1655146783 = (byte[])vc_3;
	for(int ii = 0; ii <array_834046276.length; ii++) {
		org.junit.Assert.assertEquals(array_834046276[ii], array_1655146783[ii]);
	};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_0 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_0);
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_0;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_15_0);
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf8__13 = // StatementAdderMethod cloned existing statement
vc_0.hash(vc_3);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_testHash64ByteArrayOverload_cf8__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, -473270964);
            // StatementAdderOnAssert create random local variable
            int vc_235 = 1425321297;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_235, 1425321297);
            // StatementAdderOnAssert create random local variable
            byte[] vc_234 = new byte []{24,4};
            // AssertGenerator add assertion
            byte[] array_553349935 = new byte[]{24, 4};
	byte[] array_1132328144 = (byte[])vc_234;
	for(int ii = 0; ii <array_553349935.length; ii++) {
		org.junit.Assert.assertEquals(array_553349935[ii], array_1132328144[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_232 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_232.hash64(vc_234, vc_235);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf8_cf527 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutation5_cf391_failAssert9_add4278() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hshthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "hshthis");
            // MethodAssertGenerator build local variable
            Object o_2_0 = input;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_2_0, "hshthis");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_8_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_8_0, 6901914564845240700L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_183 = 487418983;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_183, 487418983);
            // StatementAdderOnAssert create null value
            byte[] vc_181 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_181);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_180 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_180.hash(vc_181, vc_183);
            // StatementAdderMethod cloned existing statement
            vc_180.hash(vc_181, vc_183);
            // MethodAssertGenerator build local variable
            Object o_19_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_literalMutation5_cf391 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf66_cf2098_failAssert13_literalMutation4324() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -8896273065425798843L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            java.lang.Object vc_33 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_33);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_33;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_11_0);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_31);
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_31;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_15_0);
            // AssertGenerator replace invocation
            long o_testHash64ByteArrayOverload_cf66__13 = // StatementAdderMethod cloned existing statement
vc_31.hash64(vc_33);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_testHash64ByteArrayOverload_cf66__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, 0L);
            // StatementAdderOnAssert create literal from method
            int int_vc_58 = 10001;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_58, 10001);
            // StatementAdderOnAssert create null value
            byte[] vc_706 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_706);
            // StatementAdderMethod cloned existing statement
            vc_31.hash(vc_706, int_vc_58);
            // MethodAssertGenerator build local variable
            Object o_27_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf66_cf2098 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf49_failAssert5_literalMutation2686_cf2762_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_18_1 = 1640902160;
                java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
                // MethodAssertGenerator build local variable
                Object o_4_0 = input;
                byte[] inputBytes = input.getBytes();
                long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
                // MethodAssertGenerator build local variable
                Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
                // MethodAssertGenerator build local variable
                Object o_13_0 = o_6_0;
                java.lang.Object bytesAsObject = inputBytes;
                // StatementAdderOnAssert create random local variable
                int vc_25 = 1640902160;
                // MethodAssertGenerator build local variable
                Object o_18_0 = vc_25;
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.hash.MurmurHash vc_21 = (com.clearspring.analytics.hash.MurmurHash)null;
                // StatementAdderOnAssert create literal from method
                int int_vc_82 = 1640902160;
                // StatementAdderOnAssert create null value
                byte[] vc_881 = (byte[])null;
                // StatementAdderMethod cloned existing statement
                vc_21.hash(vc_881, int_vc_82);
                // MethodAssertGenerator build local variable
                Object o_28_0 = vc_21;
                // StatementAdderMethod cloned existing statement
                vc_21.hash64(inputBytes, vc_25);
                // MethodAssertGenerator build local variable
                Object o_15_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
                org.junit.Assert.fail("testHash64ByteArrayOverload_cf49 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf49_failAssert5_literalMutation2686_cf2762 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4457() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1241 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1241);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1239 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1239);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4457__13 = // StatementAdderMethod cloned existing statement
vc_1239.hash(vc_1241);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4457__13, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4414() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_1228 = new byte []{};
        // AssertGenerator add assertion
        byte[] array_1716170 = new byte[]{};
	byte[] array_2060640024 = (byte[])vc_1228;
	for(int ii = 0; ii <array_1716170.length; ii++) {
		org.junit.Assert.assertEquals(array_1716170[ii], array_2060640024[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1225 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1225);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4414__13 = // StatementAdderMethod cloned existing statement
vc_1225.hash(vc_1228);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4414__13, -1285986640);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4425() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_1232 = new byte []{66};
        // AssertGenerator add assertion
        byte[] array_1389986866 = new byte[]{66};
	byte[] array_877831976 = (byte[])vc_1232;
	for(int ii = 0; ii <array_1389986866.length; ii++) {
		org.junit.Assert.assertEquals(array_1389986866[ii], array_877831976[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1229 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1229);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4425__13 = // StatementAdderMethod cloned existing statement
vc_1229.hash(vc_1232, hashOfString);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4425__13, -1218651226);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4466() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_1245 = 7463338671154831092L;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_1245, 7463338671154831092L);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1243 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1243);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4466__13 = // StatementAdderMethod cloned existing statement
vc_1243.hashLong(vc_1245);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4466__13, -1907575683);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4505() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1258 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1258);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1256 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1256);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf4505__13 = // StatementAdderMethod cloned existing statement
vc_1256.hash64(vc_1258);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4505__13, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test
    public void testHashByteArrayOverload_literalMutation4408() {
        java.lang.String input = "hashths";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(input, "hashths");
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4506() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1256 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1256);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf4506__11 = // StatementAdderMethod cloned existing statement
vc_1256.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4506__11, -8896273065425798843L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4459() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1242 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1239 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1239);
        // StatementAdderMethod cloned existing statement
        vc_1239.hash(vc_1242);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4419() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_1226 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4419__11 = // StatementAdderMethod cloned existing statement
vc_1226.hash(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4419__11, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4507() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_1259 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1256 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1256);
        // StatementAdderMethod cloned existing statement
        vc_1256.hash64(vc_1259);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4474() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_1250 = -821384933;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_1250, -821384933);
        // StatementAdderOnAssert create random local variable
        byte[] vc_1249 = new byte []{24,73,98};
        // AssertGenerator add assertion
        byte[] array_1704759680 = new byte[]{24, 73, 98};
	byte[] array_1227908883 = (byte[])vc_1249;
	for(int ii = 0; ii <array_1704759680.length; ii++) {
		org.junit.Assert.assertEquals(array_1704759680[ii], array_1227908883[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1246 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1246);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf4474__15 = // StatementAdderMethod cloned existing statement
vc_1246.hash64(vc_1249, vc_1250);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4474__15, -1799135965680762558L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4426_cf5782_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 2085985349;
            // MethodAssertGenerator build local variable
            Object o_11_1 = -1415310480;
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_1233 = -1415310480;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1233;
            // StatementAdderOnAssert create random local variable
            byte[] vc_1232 = new byte []{66};
            // AssertGenerator add assertion
            byte[] array_927105474 = new byte[]{66};
	byte[] array_772714935 = (byte[])vc_1232;
	for(int ii = 0; ii <array_927105474.length; ii++) {
		org.junit.Assert.assertEquals(array_927105474[ii], array_772714935[ii]);
	};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1229 = (com.clearspring.analytics.hash.MurmurHash)null;
            // MethodAssertGenerator build local variable
            Object o_19_0 = vc_1229;
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf4426__15 = // StatementAdderMethod cloned existing statement
vc_1229.hash(vc_1232, vc_1233);
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testHashByteArrayOverload_cf4426__15;
            // StatementAdderOnAssert create null value
            byte[] vc_1616 = (byte[])null;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1614 = (com.clearspring.analytics.hash.MurmurHash)null;
            // StatementAdderMethod cloned existing statement
            vc_1614.hash(vc_1616, vc_1233);
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4426_cf5782 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4422_failAssert3_literalMutation9745() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hasthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "hasthis");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -343642372);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_1233 = -1415310480;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1233, -1415310480);
            // StatementAdderOnAssert create null value
            byte[] vc_1231 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1231);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1229 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1229);
            // StatementAdderMethod cloned existing statement
            vc_1229.hash(vc_1231, vc_1233);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4422 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4433_failAssert4_literalMutation9755() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "S$>+z,&]";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "S$>+z,&]");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1326417055);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            byte[] vc_1231 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1231);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1230 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_1230.hash(vc_1231, hashOfString);
            // MethodAssertGenerator build local variable
            Object o_15_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4433 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4459_cf6587_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1242 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1239 = (com.clearspring.analytics.hash.MurmurHash)null;
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_1239;
            // StatementAdderMethod cloned existing statement
            vc_1239.hash(vc_1242);
            // StatementAdderOnAssert create literal from method
            int int_vc_174 = 10000;
            // StatementAdderOnAssert create null value
            byte[] vc_1808 = (byte[])null;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1807 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_1807.hash64(vc_1808, int_vc_174);
            // MethodAssertGenerator build local variable
            Object o_25_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4459_cf6587 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4466_cf6953_failAssert2_literalMutation10368() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = -1907575683;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_1, -1907575683);
            java.lang.String input = "hshthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "hshthis");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -233990250);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            long vc_1245 = 7463338671154831092L;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1245, 7463338671154831092L);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1245;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 7463338671154831092L);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1243 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1243);
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_1243;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_15_0);
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf4466__13 = // StatementAdderMethod cloned existing statement
vc_1243.hashLong(vc_1245);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_testHashByteArrayOverload_cf4466__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, -1907575683);
            // StatementAdderOnAssert create literal from method
            int int_vc_184 = 10000;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_184, 10000);
            // StatementAdderOnAssert create null value
            byte[] vc_1896 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1896);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1895 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_1895.hash(vc_1896, int_vc_184);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4466_cf6953 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4464_cf6785_failAssert21_literalMutation10510() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = -1974946086;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_1, -1974946086);
            java.lang.String input = "L2X`XhvN";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "L2X`XhvN");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1609813809);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1240 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf4464__11 = // StatementAdderMethod cloned existing statement
vc_1240.hash(bytesAsObject);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_testHashByteArrayOverload_cf4464__11;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, -1609813809);
            // StatementAdderOnAssert create null value
            byte[] vc_1857 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1857);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1856 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_1856.hash(vc_1857);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4464_cf6785 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4426_cf5782_failAssert1_add10353() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_23_1 = 2085985349;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_1, 2085985349);
            // MethodAssertGenerator build local variable
            Object o_11_1 = -1415310480;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_1, -1415310480);
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1974946086);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_1233 = -1415310480;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1233, -1415310480);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1233;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, -1415310480);
            // StatementAdderOnAssert create random local variable
            byte[] vc_1232 = new byte []{66};
            // AssertGenerator add assertion
            byte[] array_231644021 = new byte[]{66};
	byte[] array_1936148013 = (byte[])vc_1232;
	for(int ii = 0; ii <array_231644021.length; ii++) {
		org.junit.Assert.assertEquals(array_231644021[ii], array_1936148013[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_927105474 = new byte[]{66};
	byte[] array_772714935 = (byte[])vc_1232;
	for(int ii = 0; ii <array_927105474.length; ii++) {
		org.junit.Assert.assertEquals(array_927105474[ii], array_772714935[ii]);
	};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1229 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1229);
            // MethodAssertGenerator build local variable
            Object o_19_0 = vc_1229;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_19_0);
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf4426__15 = // StatementAdderMethod cloned existing statement
vc_1229.hash(vc_1232, vc_1233);
            // MethodAssertGenerator build local variable
            Object o_23_0 = o_testHashByteArrayOverload_cf4426__15;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_23_0, 2085985349);
            // StatementAdderOnAssert create null value
            byte[] vc_1616 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1616);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1614 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1614);
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_1614.hash(vc_1616, vc_1233);
            // StatementAdderMethod cloned existing statement
            vc_1614.hash(vc_1616, vc_1233);
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4426_cf5782 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4473_cf7845_failAssert6_literalMutation10400_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.String input = "hasphthis";
                byte[] inputBytes = input.getBytes();
                int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
                // MethodAssertGenerator build local variable
                Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
                java.lang.Object bytesAsObject = inputBytes;
                // StatementAdderOnAssert create random local variable
                byte[] vc_1249 = new byte []{24,73,98};
                // AssertGenerator add assertion
                byte[] array_1005303541 = new byte[]{24, 73, 98};
	byte[] array_2085056533 = (byte[])vc_1249;
	for(int ii = 0; ii <array_1005303541.length; ii++) {
		org.junit.Assert.assertEquals(array_1005303541[ii], array_2085056533[ii]);
	};
                // StatementAdderOnAssert create null value
                com.clearspring.analytics.hash.MurmurHash vc_1246 = (com.clearspring.analytics.hash.MurmurHash)null;
                // MethodAssertGenerator build local variable
                Object o_15_0 = vc_1246;
                // AssertGenerator replace invocation
                long o_testHashByteArrayOverload_cf4473__13 = // StatementAdderMethod cloned existing statement
vc_1246.hash64(vc_1249, hashOfString);
                // MethodAssertGenerator build local variable
                Object o_19_0 = o_testHashByteArrayOverload_cf4473__13;
                // StatementAdderOnAssert create null value
                byte[] vc_2106 = (byte[])null;
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.hash.MurmurHash vc_2105 = new com.clearspring.analytics.hash.MurmurHash();
                // StatementAdderMethod cloned existing statement
                vc_2105.hash(vc_2106, hashOfString);
                // MethodAssertGenerator build local variable
                Object o_27_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
                org.junit.Assert.fail("testHashByteArrayOverload_cf4473_cf7845 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testHashByteArrayOverload_cf4473_cf7845_failAssert6_literalMutation10400 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4482_cf8350_failAssert15_literalMutation10473() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_11_1 = -821384933;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_1, -821384933);
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1974946086);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_1250 = -821384933;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1250, -821384933);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1250;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, -821384933);
            // StatementAdderOnAssert create null value
            byte[] vc_1248 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1248);
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_1248;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_15_0);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1247 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            long o_testHashByteArrayOverload_cf4482__15 = // StatementAdderMethod cloned existing statement
vc_1247.hash64(vc_1248, vc_1250);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testHashByteArrayOverload_cf4482__15;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_21_0, -1799135965680762558L);
            // StatementAdderOnAssert create random local variable
            int vc_2230 = 2147483647;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_2230, 2147483647);
            // StatementAdderOnAssert create random local variable
            byte[] vc_2229 = new byte []{14,18};
            // AssertGenerator add assertion
            byte[] array_1040658003 = new byte[]{14, 18};
	byte[] array_951215557 = (byte[])vc_2229;
	for(int ii = 0; ii <array_1040658003.length; ii++) {
		org.junit.Assert.assertEquals(array_1040658003[ii], array_951215557[ii]);
	};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_2226 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_2226);
            // StatementAdderMethod cloned existing statement
            vc_2226.hash64(vc_2229, vc_2230);
            // MethodAssertGenerator build local variable
            Object o_31_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4482_cf8350 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4463_cf6646_failAssert22_literalMutation10513() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = 0;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_1, 0);
            java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, 7931051);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            java.lang.Object vc_1241 = (java.lang.Object)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1241);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1241;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_11_0);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1240 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf4463__13 = // StatementAdderMethod cloned existing statement
vc_1240.hash(vc_1241);
            // MethodAssertGenerator build local variable
            Object o_17_0 = o_testHashByteArrayOverload_cf4463__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, 0);
            // StatementAdderOnAssert create literal from method
            int int_vc_176 = 10000;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_176, 10000);
            // StatementAdderOnAssert create null value
            byte[] vc_1826 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1826);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1824 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1824);
            // StatementAdderMethod cloned existing statement
            vc_1824.hash(vc_1826, int_vc_176);
            // MethodAssertGenerator build local variable
            Object o_27_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4463_cf6646 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

