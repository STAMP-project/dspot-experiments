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
    public void testHash64ByteArrayOverload_cf17() {
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
        byte[] array_237950422 = new byte[]{58, 96};
	byte[] array_770429092 = (byte[])vc_7;
	for(int ii = 0; ii <array_237950422.length; ii++) {
		org.junit.Assert.assertEquals(array_237950422[ii], array_770429092[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_4 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_4);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf17__15 = // StatementAdderMethod cloned existing statement
vc_4.hash(vc_7, vc_8);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf17__15, -1529859249);
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
    public void testHash64ByteArrayOverload_cf42() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_18 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf42__11 = // StatementAdderMethod cloned existing statement
vc_18.hashLong(hashOfString);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf42__11, 1003210458);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf33() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_16 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_14 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_14);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf33__13 = // StatementAdderMethod cloned existing statement
vc_14.hash(vc_16);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf33__13, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf66() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_33 = (java.lang.Object)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_33);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_31);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf66__13 = // StatementAdderMethod cloned existing statement
vc_31.hash64(vc_33);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHash64ByteArrayOverload_cf66__13, 0L);
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
    public void testHash64ByteArrayOverload_cf8() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_3 = new byte []{106};
        // AssertGenerator add assertion
        byte[] array_138879699 = new byte[]{106};
	byte[] array_625173884 = (byte[])vc_3;
	for(int ii = 0; ii <array_138879699.length; ii++) {
		org.junit.Assert.assertEquals(array_138879699[ii], array_625173884[ii]);
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
    public void testHash64ByteArrayOverload_cf49_failAssert5_literalMutation2687() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "R]r3_{}V";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "R]r3_{}V");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, 1413690671907941003L);
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
    public void testHash64ByteArrayOverload_cf43_cf1823_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = 56140174;
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            long vc_20 = 1861202967246610446L;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_20;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_18 = (com.clearspring.analytics.hash.MurmurHash)null;
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_18;
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf43__13 = // StatementAdderMethod cloned existing statement
vc_18.hashLong(vc_20);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_testHash64ByteArrayOverload_cf43__13;
            // StatementAdderOnAssert create literal from method
            int int_vc_47 = 10000;
            // StatementAdderOnAssert create random local variable
            byte[] vc_619 = new byte []{108,60,25,97};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_617 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_617.hash64(vc_619, int_vc_47);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf43_cf1823 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
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
    public void testHash64ByteArrayOverload_cf23_cf940_failAssert16() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = -1529859249;
            // MethodAssertGenerator build local variable
            Object o_11_1 = 1231906488;
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_8 = 1231906488;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_8;
            // StatementAdderOnAssert create random local variable
            byte[] vc_7 = new byte []{58,96};
            // AssertGenerator add assertion
            byte[] array_1318541046 = new byte[]{58, 96};
	byte[] array_1059744590 = (byte[])vc_7;
	for(int ii = 0; ii <array_1318541046.length; ii++) {
		org.junit.Assert.assertEquals(array_1318541046[ii], array_1059744590[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_5 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf23__15 = // StatementAdderMethod cloned existing statement
vc_5.hash(vc_7, vc_8);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testHash64ByteArrayOverload_cf23__15;
            // StatementAdderOnAssert create null value
            byte[] vc_352 = (byte[])null;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_351 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_351.hash(vc_352);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf23_cf940 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
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
            byte[] array_1197633672 = new byte[]{124, 24, 78, 76};
	byte[] array_905817341 = (byte[])vc_24;
	for(int ii = 0; ii <array_1197633672.length; ii++) {
		org.junit.Assert.assertEquals(array_1197633672[ii], array_905817341[ii]);
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
    public void testHash64ByteArrayOverload_cf68_cf2371_failAssert17() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_34 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_31;
            // StatementAdderMethod cloned existing statement
            vc_31.hash64(vc_34);
            // StatementAdderOnAssert create random local variable
            int vc_795 = 2058659172;
            // StatementAdderOnAssert create null value
            byte[] vc_793 = (byte[])null;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_791 = (com.clearspring.analytics.hash.MurmurHash)null;
            // StatementAdderMethod cloned existing statement
            vc_791.hash64(vc_793, vc_795);
            // MethodAssertGenerator build local variable
            Object o_25_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf68_cf2371 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf23_cf940_failAssert16_literalMutation4358() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_21_1 = -1529859249;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_21_1, -1529859249);
            // MethodAssertGenerator build local variable
            Object o_11_1 = 1231906488;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_1, 1231906488);
            java.lang.String input = "I(`VZPh?";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "I(`VZPh?");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -7219545021020219917L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_8 = 1231906488;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_8, 1231906488);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_8;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 1231906488);
            // StatementAdderOnAssert create random local variable
            byte[] vc_7 = new byte []{58,96};
            // AssertGenerator add assertion
            byte[] array_1296942621 = new byte[]{58, 96};
	byte[] array_487520139 = (byte[])vc_7;
	for(int ii = 0; ii <array_1296942621.length; ii++) {
		org.junit.Assert.assertEquals(array_1296942621[ii], array_487520139[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_1318541046 = new byte[]{58, 96};
	byte[] array_1059744590 = (byte[])vc_7;
	for(int ii = 0; ii <array_1318541046.length; ii++) {
		org.junit.Assert.assertEquals(array_1318541046[ii], array_1059744590[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_5 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf23__15 = // StatementAdderMethod cloned existing statement
vc_5.hash(vc_7, vc_8);
            // MethodAssertGenerator build local variable
            Object o_21_0 = o_testHash64ByteArrayOverload_cf23__15;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_21_0, -1529859249);
            // StatementAdderOnAssert create null value
            byte[] vc_352 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_352);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_351 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_351.hash(vc_352);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf23_cf940 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_cf49_failAssert5_literalMutation2686_cf2771_failAssert4() {
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
                // StatementAdderOnAssert create random local variable
                com.clearspring.analytics.hash.MurmurHash vc_880 = new com.clearspring.analytics.hash.MurmurHash();
                // StatementAdderMethod cloned existing statement
                vc_880.hash(vc_881, int_vc_82);
                // MethodAssertGenerator build local variable
                Object o_30_0 = vc_21;
                // StatementAdderMethod cloned existing statement
                vc_21.hash64(inputBytes, vc_25);
                // MethodAssertGenerator build local variable
                Object o_15_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
                org.junit.Assert.fail("testHash64ByteArrayOverload_cf49 should have thrown ArrayIndexOutOfBoundsException");
            } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
            }
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf49_failAssert5_literalMutation2686_cf2771 should have thrown NullPointerException");
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
            byte[] array_1599968176 = new byte[]{106};
	byte[] array_1123884576 = (byte[])vc_3;
	for(int ii = 0; ii <array_1599968176.length; ii++) {
		org.junit.Assert.assertEquals(array_1599968176[ii], array_1123884576[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_138879699 = new byte[]{106};
	byte[] array_625173884 = (byte[])vc_3;
	for(int ii = 0; ii <array_138879699.length; ii++) {
		org.junit.Assert.assertEquals(array_138879699[ii], array_625173884[ii]);
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
            byte[] array_1181827562 = new byte[]{24, 4};
	byte[] array_537317941 = (byte[])vc_234;
	for(int ii = 0; ii <array_1181827562.length; ii++) {
		org.junit.Assert.assertEquals(array_1181827562[ii], array_537317941[ii]);
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
    public void testHash64ByteArrayOverload_cf46_cf1878_failAssert12_literalMutation4312() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_13_1 = 1003210458;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_1, 1003210458);
            java.lang.String input = "h1ashthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "h1ashthis");
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -6350129592754054543L);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_19 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf46__11 = // StatementAdderMethod cloned existing statement
vc_19.hashLong(hashOfString);
            // MethodAssertGenerator build local variable
            Object o_13_0 = o_testHash64ByteArrayOverload_cf46__11;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_13_0, 1356348271);
            // StatementAdderOnAssert create literal from method
            int int_vc_49 = 10000;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_49, 10000);
            // StatementAdderOnAssert create null value
            byte[] vc_636 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_636);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_635 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_635.hash(vc_636, int_vc_49);
            // MethodAssertGenerator build local variable
            Object o_23_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf46_cf1878 should have thrown NullPointerException");
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
    public void testHashByteArrayOverload_cf4438() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_1233 = -1415310480;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_1233, -1415310480);
        // StatementAdderOnAssert create random local variable
        byte[] vc_1232 = new byte []{66};
        // AssertGenerator add assertion
        byte[] array_1870731410 = new byte[]{66};
	byte[] array_16922229 = (byte[])vc_1232;
	for(int ii = 0; ii <array_1870731410.length; ii++) {
		org.junit.Assert.assertEquals(array_1870731410[ii], array_16922229[ii]);
	};
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_1230 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4438__15 = // StatementAdderMethod cloned existing statement
vc_1230.hash(vc_1232, vc_1233);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4438__15, 2085985349);
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
    public void testHashByteArrayOverload_cf4470() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_1250 = -821384933;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(vc_1250, -821384933);
        // StatementAdderOnAssert create null value
        byte[] vc_1248 = (byte[])null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1248);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1246 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_1246);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf4470__15 = // StatementAdderMethod cloned existing statement
vc_1246.hash64(vc_1248, vc_1250);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4470__15, -1799135965680762558L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4464() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_1240 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf4464__11 = // StatementAdderMethod cloned existing statement
vc_1240.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(o_testHashByteArrayOverload_cf4464__11, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutation4407_cf4519_failAssert27() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hqashthis";
            // MethodAssertGenerator build local variable
            Object o_2_0 = input;
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_8_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            byte[] vc_1262 = (byte[])null;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1260 = (com.clearspring.analytics.hash.MurmurHash)null;
            // StatementAdderMethod cloned existing statement
            vc_1260.hash(vc_1262);
            // MethodAssertGenerator build local variable
            Object o_17_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_literalMutation4407_cf4519 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4469_cf7323_failAssert28() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            byte[] vc_1248 = (byte[])null;
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1248;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1246 = (com.clearspring.analytics.hash.MurmurHash)null;
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_1246;
            // AssertGenerator replace invocation
            long o_testHashByteArrayOverload_cf4469__13 = // StatementAdderMethod cloned existing statement
vc_1246.hash64(vc_1248, hashOfString);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_testHashByteArrayOverload_cf4469__13;
            // StatementAdderOnAssert create literal from method
            int int_vc_197 = 10000;
            // StatementAdderOnAssert create null value
            byte[] vc_1983 = (byte[])null;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1982 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_1982.hash64(vc_1983, int_vc_197);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4469_cf7323 should have thrown NullPointerException");
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
    public void testHashByteArrayOverload_cf4473_cf7845_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            byte[] vc_1249 = new byte []{24,73,98};
            // AssertGenerator add assertion
            byte[] array_1306196102 = new byte[]{24, 73, 98};
	byte[] array_1500795835 = (byte[])vc_1249;
	for(int ii = 0; ii <array_1306196102.length; ii++) {
		org.junit.Assert.assertEquals(array_1306196102[ii], array_1500795835[ii]);
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
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4420_cf5569_failAssert16_literalMutation10477() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_17_1 = -1285986640;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_1, -1285986640);
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
            // StatementAdderOnAssert create random local variable
            byte[] vc_1228 = new byte []{};
            // AssertGenerator add assertion
            byte[] array_1569356331 = new byte[]{};
	byte[] array_2092033968 = (byte[])vc_1228;
	for(int ii = 0; ii <array_1569356331.length; ii++) {
		org.junit.Assert.assertEquals(array_1569356331[ii], array_2092033968[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_1282735726 = new byte[]{};
	byte[] array_1894458049 = (byte[])vc_1228;
	for(int ii = 0; ii <array_1282735726.length; ii++) {
		org.junit.Assert.assertEquals(array_1282735726[ii], array_1894458049[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1226 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf4420__13 = // StatementAdderMethod cloned existing statement
vc_1226.hash(vc_1228);
            // MethodAssertGenerator build local variable
            Object o_17_0 = o_testHashByteArrayOverload_cf4420__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_17_0, -1285986640);
            // StatementAdderOnAssert create random local variable
            int vc_1565 = 436791398;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1565, 436791398);
            // StatementAdderOnAssert create null value
            byte[] vc_1563 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1563);
            // StatementAdderMethod cloned existing statement
            vc_1226.hash64(vc_1563, vc_1565);
            // MethodAssertGenerator build local variable
            Object o_25_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4420_cf5569 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4459_cf6587_failAssert9_add10427() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1974946086);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1242 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1239 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1239);
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_1239;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_13_0);
            // StatementAdderMethod cloned existing statement
            vc_1239.hash(vc_1242);
            // StatementAdderOnAssert create literal from method
            int int_vc_174 = 10000;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_174, 10000);
            // StatementAdderOnAssert create null value
            byte[] vc_1808 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1808);
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_1807 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            // MethodCallAdder
            vc_1807.hash64(vc_1808, int_vc_174);
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
            byte[] array_612116640 = new byte[]{66};
	byte[] array_1646949888 = (byte[])vc_1232;
	for(int ii = 0; ii <array_612116640.length; ii++) {
		org.junit.Assert.assertEquals(array_612116640[ii], array_1646949888[ii]);
	};
            // AssertGenerator add assertion
            byte[] array_2045667518 = new byte[]{66};
	byte[] array_1446190906 = (byte[])vc_1232;
	for(int ii = 0; ii <array_2045667518.length; ii++) {
		org.junit.Assert.assertEquals(array_2045667518[ii], array_1446190906[ii]);
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
    public void testHashByteArrayOverload_cf4459_cf6502_failAssert14_literalMutation10455() {
        // AssertGenerator generate try/catch block with fail statement
        try {
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
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1242 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1239 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1239);
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_1239;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_13_0);
            // StatementAdderMethod cloned existing statement
            vc_1239.hash(vc_1242);
            // StatementAdderOnAssert create null value
            byte[] vc_1791 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1791);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1789 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1789);
            // StatementAdderMethod cloned existing statement
            vc_1789.hash(vc_1791, hashOfString);
            // MethodAssertGenerator build local variable
            Object o_23_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4459_cf6502 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4459_cf6587_failAssert9_literalMutation10428() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "has2hthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "has2hthis");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, 2141473202);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            java.lang.Object vc_1242 = new java.lang.Object();
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1239 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1239);
            // MethodAssertGenerator build local variable
            Object o_13_0 = vc_1239;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_13_0);
            // StatementAdderMethod cloned existing statement
            vc_1239.hash(vc_1242);
            // StatementAdderOnAssert create literal from method
            int int_vc_174 = 10000;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(int_vc_174, 10000);
            // StatementAdderOnAssert create null value
            byte[] vc_1808 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1808);
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

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_cf4466_cf6953_failAssert2_literalMutation10371() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_19_1 = -1907575683;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_1, -1907575683);
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1974946086);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            long vc_1245 = 9223372036854775807L;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(vc_1245, 9223372036854775807L);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1245;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_11_0, 9223372036854775807L);
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
            org.junit.Assert.assertEquals(o_19_0, 310834921);
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
    public void testHashByteArrayOverload_cf4469_cf7226_failAssert11_literalMutation10441() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "h,shthis";
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(input, "h,shthis");
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_6_0, -1681773766);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            byte[] vc_1248 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1248);
            // MethodAssertGenerator build local variable
            Object o_11_0 = vc_1248;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_11_0);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1246 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1246);
            // MethodAssertGenerator build local variable
            Object o_15_0 = vc_1246;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(o_15_0);
            // AssertGenerator replace invocation
            long o_testHashByteArrayOverload_cf4469__13 = // StatementAdderMethod cloned existing statement
vc_1246.hash64(vc_1248, hashOfString);
            // MethodAssertGenerator build local variable
            Object o_19_0 = o_testHashByteArrayOverload_cf4469__13;
            // AssertGenerator add assertion
            org.junit.Assert.assertEquals(o_19_0, 4815520876666023129L);
            // StatementAdderOnAssert create null value
            byte[] vc_1962 = (byte[])null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1962);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_1960 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            org.junit.Assert.assertNull(vc_1960);
            // StatementAdderMethod cloned existing statement
            vc_1960.hash(vc_1962);
            // MethodAssertGenerator build local variable
            Object o_27_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf4469_cf7226 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }
}

