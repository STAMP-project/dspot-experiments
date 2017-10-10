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
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf17() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_0 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf17__11 = // StatementAdderMethod cloned existing statement
vc_0.hash(inputBytes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf17__11, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf29() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_8 = -164298280;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -164298280);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_4 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf29__13 = // StatementAdderMethod cloned existing statement
vc_4.hash(inputBytes, vc_8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf29__13, 1968502063);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf91() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_33 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_31);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf91__13 = // StatementAdderMethod cloned existing statement
vc_31.hash64(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf91__13, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test
    public void testHash64ByteArrayOverload_literalMutation5() {
        java.lang.String input = "hshthis";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "hshthis");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf92() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_31);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf92__11 = // StatementAdderMethod cloned existing statement
vc_31.hash64(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf92__11, -8896273065425798843L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf58() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_16 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_15 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf58__13 = // StatementAdderMethod cloned existing statement
vc_15.hash(vc_16);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf58__13, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf31() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_8 = -164298280;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_8, -164298280);
        // StatementAdderOnAssert create random local variable
        byte[] vc_7 = new byte []{71,2};
        // AssertGenerator add assertion
        byte[] array_25589055 = new byte[]{71, 2};
	byte[] array_178865378 = (byte[])vc_7;
	for(int ii = 0; ii <array_25589055.length; ii++) {
		junit.framework.Assert.assertEquals(array_25589055[ii], array_178865378[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_4 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf31__15 = // StatementAdderMethod cloned existing statement
vc_4.hash(vc_7, vc_8);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf31__15, -1548656874);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf54() {
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
        junit.framework.Assert.assertNull(vc_14);
        // StatementAdderMethod cloned existing statement
        vc_14.hash(vc_17);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf61() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_18 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_18);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf61__11 = // StatementAdderMethod cloned existing statement
vc_18.hashLong(hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf61__11, 1003210458);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf52_literalMutation2228() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("hello world", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_16 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_16);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_14 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_14);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_14);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf52__13 = // StatementAdderMethod cloned existing statement
vc_14.hash(vc_16);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf52__13, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf39_cf2149_failAssert14() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create random local variable
            int vc_8 = -164298280;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_8, -164298280);
            // StatementAdderOnAssert create random local variable
            byte[] vc_7 = new byte []{71,2};
            // AssertGenerator add assertion
            byte[] array_1701640805 = new byte[]{71, 2};
	byte[] array_877917364 = (byte[])vc_7;
	for(int ii = 0; ii <array_1701640805.length; ii++) {
		junit.framework.Assert.assertEquals(array_1701640805[ii], array_877917364[ii]);
	};
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_5 = new com.clearspring.analytics.hash.MurmurHash();
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf39__15 = // StatementAdderMethod cloned existing statement
vc_5.hash(vc_7, vc_8);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf39__15, -1548656874);
            // StatementAdderOnAssert create random local variable
            int vc_480 = 1148236839;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_477 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_477.hash64(inputBytes, vc_480);
            // MethodAssertGenerator build local variable
            Object o_29_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf39_cf2149 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf62_cf3134() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_20 = 2841935647616897715L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_18 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_18);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_18);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf62__13 = // StatementAdderMethod cloned existing statement
vc_18.hashLong(vc_20);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf62__13, -1981890912);
        // StatementAdderOnAssert create literal from method
        int int_vc_56 = 1000;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_56, 1000);
        // StatementAdderOnAssert create random local variable
        byte[] vc_707 = new byte []{84,16,61};
        // AssertGenerator add assertion
        byte[] array_1840549706 = new byte[]{84, 16, 61};
	byte[] array_550870440 = (byte[])vc_707;
	for(int ii = 0; ii <array_1840549706.length; ii++) {
		junit.framework.Assert.assertEquals(array_1840549706[ii], array_550870440[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_704 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_704);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf62_cf3134__27 = // StatementAdderMethod cloned existing statement
vc_704.hash(vc_707, int_vc_56);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf62_cf3134__27, 664493202);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf66_cf3542() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_20 = 2841935647616897715L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_19 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf66__13 = // StatementAdderMethod cloned existing statement
vc_19.hashLong(vc_20);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf66__13, -1981890912);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_803 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_803);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_801 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_801);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf66_cf3542__23 = // StatementAdderMethod cloned existing statement
vc_801.hash64(vc_803);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf66_cf3542__23, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf61_cf3051_failAssert5() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_18 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_18);
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf61__11 = // StatementAdderMethod cloned existing statement
vc_18.hashLong(hashOfString);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf61__11, 1003210458);
            // StatementAdderOnAssert create literal from method
            int int_vc_54 = 1000;
            // StatementAdderMethod cloned existing statement
            vc_18.hash64(inputBytes, int_vc_54);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf61_cf3051 should have thrown ArrayIndexOutOfBoundsException");
        } catch (java.lang.ArrayIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf53_cf2381_failAssert15() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_14 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_14);
            // AssertGenerator replace invocation
            int o_testHash64ByteArrayOverload_cf53__11 = // StatementAdderMethod cloned existing statement
vc_14.hash(bytesAsObject);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf53__11, -1974946086);
            // StatementAdderOnAssert create null value
            byte[] vc_527 = (byte[])null;
            // StatementAdderMethod cloned existing statement
            vc_14.hash(vc_527);
            // MethodAssertGenerator build local variable
            Object o_21_0 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
            org.junit.Assert.fail("testHash64ByteArrayOverload_cf53_cf2381 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_literalMutation1_cf164() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_54 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_literalMutation1_cf164__13 = // StatementAdderMethod cloned existing statement
vc_54.hashLong(hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_literalMutation1_cf164__13, -1912552220);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf66_cf3490_literalMutation12185() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_20 = 2841935647616897715L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_20, 2841935647616897715L);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_19 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf66__13 = // StatementAdderMethod cloned existing statement
vc_19.hashLong(vc_20);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf66__13, -1981890912);
        // StatementAdderOnAssert create random local variable
        long vc_790 = 7814543360698037336L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_790, 7814543360698037336L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_790, 7814543360698037336L);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf66_cf3490__21 = // StatementAdderMethod cloned existing statement
vc_19.hashLong(vc_790);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf66_cf3490__21, 1599885151);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf97_cf4119_cf13004() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_33 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_32 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf97__13 = // StatementAdderMethod cloned existing statement
vc_32.hash64(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf97__13, 0L);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf97_cf4119__19 = // StatementAdderMethod cloned existing statement
vc_32.hashLong(hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf97_cf4119__19, 1003210458);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2639 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2639);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf97_cf4119_cf13004__27 = // StatementAdderMethod cloned existing statement
vc_2639.hash(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf97_cf4119_cf13004__27, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_literalMutation1_literalMutation112_cf6432() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_1433 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1433);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_1431 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_1431);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_literalMutation1_literalMutation112_cf6432__17 = // StatementAdderMethod cloned existing statement
vc_1431.hash64(vc_1433);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_literalMutation1_literalMutation112_cf6432__17, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHasL.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf58_literalMutation2664_cf5333() {
        java.lang.String input = "MumurHash.hash64(byte[]) did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MumurHash.hash64(byte[]) did not match MurmurHash.hash64(String)");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MumurHash.hash64(byte[]) did not match MurmurHash.hash64(String)");
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_16 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_16);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_16);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_16);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_15 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf58__13 = // StatementAdderMethod cloned existing statement
vc_15.hash(vc_16);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf58__13, 0);
        // StatementAdderOnAssert create random local variable
        int vc_1215 = -457218696;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_1215, -457218696);
        // StatementAdderOnAssert create random local variable
        byte[] vc_1214 = new byte []{108,76};
        // AssertGenerator add assertion
        byte[] array_1313459303 = new byte[]{108, 76};
	byte[] array_1831750329 = (byte[])vc_1214;
	for(int ii = 0; ii <array_1313459303.length; ii++) {
		junit.framework.Assert.assertEquals(array_1313459303[ii], array_1831750329[ii]);
	};
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_1212 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf58_literalMutation2664_cf5333__29 = // StatementAdderMethod cloned existing statement
vc_1212.hash64(vc_1214, vc_1215);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf58_literalMutation2664_cf5333__29, 2727565480428223225L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf91_cf3624_cf13300() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_33 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_33);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_31 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_31);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_31);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_31);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf91__13 = // StatementAdderMethod cloned existing statement
vc_31.hash64(vc_33);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf91__13, 0L);
        // StatementAdderOnAssert create literal from method
        int int_vc_70 = 1000;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_70, 1000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_70, 1000);
        // StatementAdderOnAssert create random local variable
        byte[] vc_812 = new byte []{43,126,66};
        // AssertGenerator add assertion
        byte[] array_971402958 = new byte[]{43, 126, 66};
	byte[] array_899686322 = (byte[])vc_812;
	for(int ii = 0; ii <array_971402958.length; ii++) {
		junit.framework.Assert.assertEquals(array_971402958[ii], array_899686322[ii]);
	};
        // AssertGenerator add assertion
        byte[] array_1312229139 = new byte[]{43, 126, 66};
	byte[] array_744691770 = (byte[])vc_812;
	for(int ii = 0; ii <array_1312229139.length; ii++) {
		junit.framework.Assert.assertEquals(array_1312229139[ii], array_744691770[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_809 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_809);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_809);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf91_cf3624__27 = // StatementAdderMethod cloned existing statement
vc_809.hash(vc_812, int_vc_70);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf91_cf3624__27, 421118521);
        // StatementAdderOnAssert create literal from method
        int int_vc_291 = 1000;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_291, 1000);
        // StatementAdderOnAssert create random local variable
        byte[] vc_2702 = new byte []{41,6,36};
        // AssertGenerator add assertion
        byte[] array_137663992 = new byte[]{41, 6, 36};
	byte[] array_1279070265 = (byte[])vc_2702;
	for(int ii = 0; ii <array_137663992.length; ii++) {
		junit.framework.Assert.assertEquals(array_137663992[ii], array_1279070265[ii]);
	};
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2700 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf91_cf3624_cf13300__47 = // StatementAdderMethod cloned existing statement
vc_2700.hash(vc_2702, int_vc_291);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf91_cf3624_cf13300__47, 1877156422);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHash64ByteArrayOverload_cf17_cf646_cf12586() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        org.junit.Assert.assertEquals("MurmurHash.hash64(byte[]) did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_0 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_0);
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf17__11 = // StatementAdderMethod cloned existing statement
vc_0.hash(inputBytes);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf17__11, -1974946086);
        // StatementAdderOnAssert create literal from method
        int int_vc_0 = 1000;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_0, 1000);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_0, 1000);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_215 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHash64ByteArrayOverload_cf17_cf646__21 = // StatementAdderMethod cloned existing statement
vc_215.hash(inputBytes, int_vc_0);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf17_cf646__21, 235740474);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2553 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2553);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2551 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2551);
        // AssertGenerator replace invocation
        long o_testHash64ByteArrayOverload_cf17_cf646_cf12586__33 = // StatementAdderMethod cloned existing statement
vc_2551.hash64(vc_2553);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHash64ByteArrayOverload_cf17_cf646_cf12586__33, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14255() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_2908 = new byte []{102,60};
        // AssertGenerator add assertion
        byte[] array_323511476 = new byte[]{102, 60};
	byte[] array_1964458494 = (byte[])vc_2908;
	for(int ii = 0; ii <array_323511476.length; ii++) {
		junit.framework.Assert.assertEquals(array_323511476[ii], array_1964458494[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2905 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2905);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14255__13 = // StatementAdderMethod cloned existing statement
vc_2905.hash(vc_2908);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14255__13, -1629179905);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14338() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        java.lang.Object vc_2922 = new java.lang.Object();
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2919 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2919);
        // StatementAdderMethod cloned existing statement
        vc_2919.hash(vc_2922);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14336() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2921 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2921);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2919 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2919);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14336__13 = // StatementAdderMethod cloned existing statement
vc_2919.hash(vc_2921);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14336__13, 0);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14424() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2936 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2936);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14424__11 = // StatementAdderMethod cloned existing statement
vc_2936.hash64(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14424__11, -8896273065425798843L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14429() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2938 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2938);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2937 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14429__13 = // StatementAdderMethod cloned existing statement
vc_2937.hash64(vc_2938);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14429__13, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14273() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_2912 = new byte []{};
        // AssertGenerator add assertion
        byte[] array_1918015090 = new byte[]{};
	byte[] array_1509611857 = (byte[])vc_2912;
	for(int ii = 0; ii <array_1918015090.length; ii++) {
		junit.framework.Assert.assertEquals(array_1918015090[ii], array_1509611857[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2909 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2909);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14273__13 = // StatementAdderMethod cloned existing statement
vc_2909.hash(vc_2912, hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14273__13, -1515877182);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14350() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_2925 = -8971699194478646591L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2924 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14350__13 = // StatementAdderMethod cloned existing statement
vc_2924.hashLong(vc_2925);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14350__13, 101462275);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14261() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2906 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14261__11 = // StatementAdderMethod cloned existing statement
vc_2906.hash(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14261__11, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14360() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_2929 = new byte []{49};
        // AssertGenerator add assertion
        byte[] array_655104318 = new byte[]{49};
	byte[] array_774792082 = (byte[])vc_2929;
	for(int ii = 0; ii <array_655104318.length; ii++) {
		junit.framework.Assert.assertEquals(array_655104318[ii], array_774792082[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2926 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2926);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14360__13 = // StatementAdderMethod cloned existing statement
vc_2926.hash64(vc_2929, hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14360__13, -3645701925788887455L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14346_cf18757() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_2925 = -8971699194478646591L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2923 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2923);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2923);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14346__13 = // StatementAdderMethod cloned existing statement
vc_2923.hashLong(vc_2925);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14346__13, 101462275);
        // StatementAdderOnAssert create random local variable
        long vc_3590 = -5156624653448614731L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3590, -5156624653448614731L);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_3588 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3588);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14346_cf18757__25 = // StatementAdderMethod cloned existing statement
vc_3588.hashLong(vc_3590);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14346_cf18757__25, 552403238);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14275_literalMutation16655() {
        java.lang.String input = "MurmurHash.hash(byte[]) did not match Murmu5Hash.hash(String)";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "MurmurHash.hash(byte[]) did not match Murmu5Hash.hash(String)");
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_2913 = 1672112320;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2913, 1672112320);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2913, 1672112320);
        // StatementAdderOnAssert create random local variable
        byte[] vc_2912 = new byte []{};
        // AssertGenerator add assertion
        byte[] array_1740543168 = new byte[]{};
	byte[] array_1358466667 = (byte[])vc_2912;
	for(int ii = 0; ii <array_1740543168.length; ii++) {
		junit.framework.Assert.assertEquals(array_1740543168[ii], array_1358466667[ii]);
	};
        // AssertGenerator add assertion
        byte[] array_1701405831 = new byte[]{};
	byte[] array_1895104220 = (byte[])vc_2912;
	for(int ii = 0; ii <array_1701405831.length; ii++) {
		junit.framework.Assert.assertEquals(array_1701405831[ii], array_1895104220[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2909 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2909);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2909);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14275__15 = // StatementAdderMethod cloned existing statement
vc_2909.hash(vc_2912, vc_2913);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14275__15, 2074595009);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14342_cf18263() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2921 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2921);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2921);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2920 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14342__13 = // StatementAdderMethod cloned existing statement
vc_2920.hash(vc_2921);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14342__13, 0);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_3515 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14342_cf18263__21 = // StatementAdderMethod cloned existing statement
vc_3515.hash(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14342_cf18263__21, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14423_cf21125() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2938 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2938);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2938);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2936 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2936);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2936);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14423__13 = // StatementAdderMethod cloned existing statement
vc_2936.hash64(vc_2938);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14423__13, 0L);
        // StatementAdderOnAssert create random local variable
        int vc_3928 = 666371152;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3928, 666371152);
        // StatementAdderOnAssert create random local variable
        byte[] vc_3927 = new byte []{120,0,31,7};
        // AssertGenerator add assertion
        byte[] array_722436477 = new byte[]{120, 0, 31, 7};
	byte[] array_56904561 = (byte[])vc_3927;
	for(int ii = 0; ii <array_722436477.length; ii++) {
		junit.framework.Assert.assertEquals(array_722436477[ii], array_56904561[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_3924 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3924);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14423_cf21125__27 = // StatementAdderMethod cloned existing statement
vc_3924.hash(vc_3927, vc_3928);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14423_cf21125__27, -1996750677);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14350_cf19057() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_2925 = -8971699194478646591L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2924 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14350__13 = // StatementAdderMethod cloned existing statement
vc_2924.hashLong(vc_2925);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14350__13, 101462275);
        // StatementAdderOnAssert create random local variable
        int vc_3630 = -1866313340;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3630, -1866313340);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_3627 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14350_cf19057__23 = // StatementAdderMethod cloned existing statement
vc_3627.hash64(inputBytes, vc_3630);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14350_cf19057__23, 267575790394012467L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14375_cf20174() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        byte[] vc_2928 = (byte[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2928);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2928);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2927 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14375__13 = // StatementAdderMethod cloned existing statement
vc_2927.hash64(vc_2928, hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14375__13, -3645701925788887455L);
        // StatementAdderOnAssert create random local variable
        byte[] vc_3787 = new byte []{98,83,79};
        // AssertGenerator add assertion
        byte[] array_1120527526 = new byte[]{98, 83, 79};
	byte[] array_1973146398 = (byte[])vc_3787;
	for(int ii = 0; ii <array_1120527526.length; ii++) {
		junit.framework.Assert.assertEquals(array_1120527526[ii], array_1973146398[ii]);
	};
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_3785 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14375_cf20174__23 = // StatementAdderMethod cloned existing statement
vc_3785.hash(vc_3787, hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14375_cf20174__23, 1587576802);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14337_literalMutation17658() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not:match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2919 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2919);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2919);
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14337__11 = // StatementAdderMethod cloned existing statement
vc_2919.hash(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14337__11, -1974946086);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14360_cf19686() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        byte[] vc_2929 = new byte []{49};
        // AssertGenerator add assertion
        byte[] array_70813907 = new byte[]{49};
	byte[] array_587751095 = (byte[])vc_2929;
	for(int ii = 0; ii <array_70813907.length; ii++) {
		junit.framework.Assert.assertEquals(array_70813907[ii], array_587751095[ii]);
	};
        // AssertGenerator add assertion
        byte[] array_655104318 = new byte[]{49};
	byte[] array_774792082 = (byte[])vc_2929;
	for(int ii = 0; ii <array_655104318.length; ii++) {
		junit.framework.Assert.assertEquals(array_655104318[ii], array_774792082[ii]);
	};
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2926 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2926);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2926);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14360__13 = // StatementAdderMethod cloned existing statement
vc_2926.hash64(vc_2929, hashOfString);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14360__13, -3645701925788887455L);
        // StatementAdderOnAssert create literal from method
        int int_vc_385 = 1000;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(int_vc_385, 1000);
        // StatementAdderOnAssert create random local variable
        byte[] vc_3717 = new byte []{71,2};
        // AssertGenerator add assertion
        byte[] array_1301293166 = new byte[]{71, 2};
	byte[] array_2070630392 = (byte[])vc_3717;
	for(int ii = 0; ii <array_1301293166.length; ii++) {
		junit.framework.Assert.assertEquals(array_1301293166[ii], array_2070630392[ii]);
	};
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_3715 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14360_cf19686__27 = // StatementAdderMethod cloned existing statement
vc_3715.hash(vc_3717, int_vc_385);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14360_cf19686__27, 1928548861);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14423_cf21318() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2938 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2938);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2938);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_2936 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2936);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2936);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14423__13 = // StatementAdderMethod cloned existing statement
vc_2936.hash64(vc_2938);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14423__13, 0L);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_3951 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3951);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14423_cf21318__23 = // StatementAdderMethod cloned existing statement
vc_3951.hash64(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14423_cf21318__23, -8896273065425798843L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14350_cf19115_cf22554() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        long vc_2925 = -8971699194478646591L;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_2925, -8971699194478646591L);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2924 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14350__13 = // StatementAdderMethod cloned existing statement
vc_2924.hashLong(vc_2925);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14350__13, 101462275);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_3636 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3636);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3636);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14350_cf19115__21 = // StatementAdderMethod cloned existing statement
vc_3636.hash64(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14350_cf19115__21, -8896273065425798843L);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_4127 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_cf14350_cf19115_cf22554__31 = // StatementAdderMethod cloned existing statement
vc_4127.hash64(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14350_cf19115_cf22554__31, -8896273065425798843L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14423_cf21125_cf24631_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String input = "hashthis";
            byte[] inputBytes = input.getBytes();
            int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
            // MethodAssertGenerator build local variable
            Object o_6_0 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
            java.lang.Object bytesAsObject = inputBytes;
            // StatementAdderOnAssert create null value
            java.lang.Object vc_2938 = (java.lang.Object)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2938);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2938);
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_2936 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2936);
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_2936);
            // AssertGenerator replace invocation
            long o_testHashByteArrayOverload_cf14423__13 = // StatementAdderMethod cloned existing statement
vc_2936.hash64(vc_2938);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14423__13, 0L);
            // StatementAdderOnAssert create random local variable
            int vc_3928 = 666371152;
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(vc_3928, 666371152);
            // StatementAdderOnAssert create random local variable
            byte[] vc_3927 = new byte []{120,0,31,7};
            // AssertGenerator add assertion
            byte[] array_722436477 = new byte[]{120, 0, 31, 7};
	byte[] array_56904561 = (byte[])vc_3927;
	for(int ii = 0; ii <array_722436477.length; ii++) {
		junit.framework.Assert.assertEquals(array_722436477[ii], array_56904561[ii]);
	};
            // StatementAdderOnAssert create null value
            com.clearspring.analytics.hash.MurmurHash vc_3924 = (com.clearspring.analytics.hash.MurmurHash)null;
            // AssertGenerator add assertion
            junit.framework.Assert.assertNull(vc_3924);
            // AssertGenerator replace invocation
            int o_testHashByteArrayOverload_cf14423_cf21125__27 = // StatementAdderMethod cloned existing statement
vc_3924.hash(vc_3927, vc_3928);
            // AssertGenerator add assertion
            junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14423_cf21125__27, -1996750677);
            // StatementAdderOnAssert create literal from method
            int int_vc_467 = 1000;
            // StatementAdderOnAssert create null value
            byte[] vc_4416 = (byte[])null;
            // StatementAdderOnAssert create random local variable
            com.clearspring.analytics.hash.MurmurHash vc_4415 = new com.clearspring.analytics.hash.MurmurHash();
            // StatementAdderMethod cloned existing statement
            vc_4415.hash(vc_4416, int_vc_467);
            // MethodAssertGenerator build local variable
            Object o_49_0 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
            org.junit.Assert.fail("testHashByteArrayOverload_cf14423_cf21125_cf24631 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_cf14342_cf18263_cf24083() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create null value
        java.lang.Object vc_2921 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2921);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2921);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_2921);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_2920 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14342__13 = // StatementAdderMethod cloned existing statement
vc_2920.hash(vc_2921);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14342__13, 0);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_3515 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14342_cf18263__21 = // StatementAdderMethod cloned existing statement
vc_3515.hash(bytesAsObject);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14342_cf18263__21, -1974946086);
        // StatementAdderOnAssert create random local variable
        byte[] vc_4343 = new byte []{29,100};
        // AssertGenerator add assertion
        byte[] array_284653492 = new byte[]{29, 100};
	byte[] array_2009204648 = (byte[])vc_4343;
	for(int ii = 0; ii <array_284653492.length; ii++) {
		junit.framework.Assert.assertEquals(array_284653492[ii], array_2009204648[ii]);
	};
        // AssertGenerator replace invocation
        int o_testHashByteArrayOverload_cf14342_cf18263_cf24083__29 = // StatementAdderMethod cloned existing statement
vc_2920.hash(vc_4343);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_cf14342_cf18263_cf24083__29, 392741285);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 1000)
    public void testHashByteArrayOverload_literalMutation14241_cf15328_cf23815() {
        java.lang.String input = "Po+,..k:";
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "Po+,..k:");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "Po+,..k:");
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(input, "Po+,..k:");
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        org.junit.Assert.assertEquals("MurmurHash.hash(byte[]) did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(inputBytes));
        java.lang.Object bytesAsObject = inputBytes;
        // StatementAdderOnAssert create random local variable
        int vc_3105 = -2031777819;
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3105, -2031777819);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(vc_3105, -2031777819);
        // StatementAdderOnAssert create null value
        byte[] vc_3103 = (byte[])null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3103);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3103);
        // StatementAdderOnAssert create null value
        com.clearspring.analytics.hash.MurmurHash vc_3101 = (com.clearspring.analytics.hash.MurmurHash)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3101);
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_3101);
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_literalMutation14241_cf15328__17 = // StatementAdderMethod cloned existing statement
vc_3101.hash64(vc_3103, vc_3105);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_literalMutation14241_cf15328__17, 8201275205705844318L);
        // StatementAdderOnAssert create null value
        java.lang.Object vc_4303 = (java.lang.Object)null;
        // AssertGenerator add assertion
        junit.framework.Assert.assertNull(vc_4303);
        // StatementAdderOnAssert create random local variable
        com.clearspring.analytics.hash.MurmurHash vc_4302 = new com.clearspring.analytics.hash.MurmurHash();
        // AssertGenerator replace invocation
        long o_testHashByteArrayOverload_literalMutation14241_cf15328_cf23815__33 = // StatementAdderMethod cloned existing statement
vc_4302.hash64(vc_4303);
        // AssertGenerator add assertion
        junit.framework.Assert.assertEquals(o_testHashByteArrayOverload_literalMutation14241_cf15328_cf23815__33, 0L);
        org.junit.Assert.assertEquals("MurmurHash.hash(Object) given a byte[] did not match MurmurHash.hash(String)", hashOfString, com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject));
    }
}

