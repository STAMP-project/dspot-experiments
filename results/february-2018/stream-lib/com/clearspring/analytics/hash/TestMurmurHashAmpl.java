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
 *
 *
 * @author epollan
 */
public class TestMurmurHashAmpl {
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload() {
        java.lang.String input = "hashthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
    }

    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload__6)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add8() {
        java.lang.String input = "hashthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__8 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload_add8__8)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload_add8__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload_add8__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload_add8__4)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString6() {
        java.lang.String input = "hshthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6__6)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2__6)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2__6)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString3() {
        java.lang.String input = "hash}this";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}this", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3__6)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}this", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3__6)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_add76() {
        java.lang.String input = "h*shthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4_add76__6 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7852005312626181413L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4_add76__6)));
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7852005312626181413L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4_add76__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h*shthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add9 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add9_literalMutationString121() {
        java.lang.String input = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__6 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__8 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString1 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString1_add45() {
        java.lang.String input = "";
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1_add45__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7207201254813729732L, ((long) (o_testHash64ByteArrayOverload_literalMutationString1_add45__4)));
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7207201254813729732L, ((long) (o_testHash64ByteArrayOverload_literalMutationString1_add45__4)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString3 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString3_add65() {
        java.lang.String input = "hash}this";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}this", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3_add65__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3_add65__4)));
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3_add65__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}this", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add9 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add9_literalMutationString124() {
        java.lang.String input = "hashths";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashths", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__6 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__8 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashths", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString6 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString6_add97() {
        java.lang.String input = "hshthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6_add97__10 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6_add97__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2_literalMutationString53() {
        java.lang.String input = "hello world";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString1 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString1_literalMutationString43() {
        java.lang.String input = "z";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString1 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString1_literalMutationString42() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2_add55() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2_add55__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2_add55__4)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add9 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add9_literalMutationString123 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add9_literalMutationString123_add1223() {
        java.lang.String input = "hasht{his";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hasht{his", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__6 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__8 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9_literalMutationString123_add1223__14 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add9__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hasht{his", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1800910510642166379L, ((long) (o_testHash64ByteArrayOverload_add9_literalMutationString123_add1223__14)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4_literalMutationString71 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString71_add733() {
        java.lang.String input = "hshthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4_literalMutationString71_add733__10 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4_literalMutationString71_add733__10)));
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4_literalMutationString71_add733__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString3 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString3_literalMutationString61 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString3_literalMutationString61_add630() {
        java.lang.String input = "hash}thBis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}thBis", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3_literalMutationString61_add630__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8351225490571405804L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3_literalMutationString61_add630__4)));
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}thBis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8351225490571405804L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3_literalMutationString61_add630__4)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4_add75 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_add75_literalMutationString766() {
        java.lang.String input = "h*shhis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h*shhis", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4_add75__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h*shhis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2_literalMutationString53 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2_literalMutationString53_add551() {
        java.lang.String input = "hello world";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2_literalMutationString53_add551__10 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-779442749388864765L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2_literalMutationString53_add551__10)));
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-779442749388864765L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2_literalMutationString53_add551__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add8 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add8_literalMutationString110 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add8_literalMutationString110_literalMutationString1070() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__8 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString1 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString1_literalMutationString42 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString1_literalMutationString42_add453() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1_literalMutationString42_add453__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString1_literalMutationString42_add453__4)));
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString1__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString1_literalMutationString42_add453__4)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2_literalMutationString53 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2_literalMutationString53_literalMutationString544() {
        java.lang.String input = "hello wPorld";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__6 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__8 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wPorld", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add8 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_add8_add118 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_add8_add118_add1170() {
        java.lang.String input = "hashthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8_add118__10 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__8 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8_add118_add1170__18 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload_add8_add118_add1170__18)));
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_add8__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload_add8_add118_add1170__18)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_add2029() {
        java.lang.String input = "hashthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2029__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2029__8 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload_add2029__8)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2029__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload_add2029__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload_add2029__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload_add2029__8)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2026() {
        java.lang.String input = "hasjhthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2026__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1883405224, ((int) (o_testHashByteArrayOverload_literalMutationString2026__6)));
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2026__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1883405224, ((int) (o_testHashByteArrayOverload_literalMutationString2026__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hasjhthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1883405224, ((int) (o_testHashByteArrayOverload_literalMutationString2026__6)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2023() {
        java.lang.String input = "hahthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hahthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_literalMutationString2023__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_literalMutationString2023__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hahthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2024 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2024_add2086() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2024_add2086__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(7931051, ((int) (o_testHashByteArrayOverload_literalMutationString2024_add2086__4)));
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2024__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2024__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(7931051, ((int) (o_testHashByteArrayOverload_literalMutationString2024_add2086__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_add2031 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_add2031_literalMutationString2156() {
        java.lang.String input = "has.hthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("has.hthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031__8 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("has.hthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2026 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2026_add2107() {
        java.lang.String input = "hasjhthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hasjhthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2026_add2107__6 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1883405224, ((int) (o_testHashByteArrayOverload_literalMutationString2026_add2107__6)));
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2026__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2026__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hasjhthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1883405224, ((int) (o_testHashByteArrayOverload_literalMutationString2026_add2107__6)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2023 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2023_add2078() {
        java.lang.String input = "hahthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023_add2078__10 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hahthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_literalMutationString2023_add2078__10)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2022 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2022_literalMutationString2062() {
        java.lang.String input = "@";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("@", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2022 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2022_add2066() {
        java.lang.String input = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022_add2066__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1285986640, ((int) (o_testHashByteArrayOverload_literalMutationString2022_add2066__4)));
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1285986640, ((int) (o_testHashByteArrayOverload_literalMutationString2022_add2066__4)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2023 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2023_add2076 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2023_add2076_add2592() {
        java.lang.String input = "hahthis";
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023_add2076__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023_add2076_add2592__8 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_literalMutationString2023_add2076_add2592__8)));
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1840755242, ((int) (o_testHashByteArrayOverload_literalMutationString2023_add2076_add2592__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hahthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_add2029 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_add2029_literalMutationString2134 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_add2029_literalMutationString2134_add3128() {
        java.lang.String input = "has&hthis";
        // AssertGenerator create local variable with return value of invocation
        byte[] o_testHashByteArrayOverload_add2029_literalMutationString2134_add3128__2 = // MethodCallAdder
        input.getBytes();
        // AssertGenerator add assertion
        byte[] array_1430400015 = new byte[]{104, 97, 115, 38, 104, 116, 104, 105, 115};
        	byte[] array_1971157063 = (byte[])o_testHashByteArrayOverload_add2029_literalMutationString2134_add3128__2;
        	for(int ii = 0; ii <array_1430400015.length; ii++) {
        		org.junit.Assert.assertEquals(array_1430400015[ii], array_1971157063[ii]);
        	};
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2029__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2029__8 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2029__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        byte[] array_1310421486 = new byte[]{104, 97, 115, 38, 104, 116, 104, 105, 115};
        	byte[] array_1787591090 = (byte[])o_testHashByteArrayOverload_add2029_literalMutationString2134_add3128__2;
        	for(int ii = 0; ii <array_1310421486.length; ii++) {
        		org.junit.Assert.assertEquals(array_1310421486[ii], array_1787591090[ii]);
        	};
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("has&hthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2022 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2022_literalMutationString2062 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2022_literalMutationString2062_add2463() {
        java.lang.String input = "@";
        // AssertGenerator create local variable with return value of invocation
        byte[] o_testHashByteArrayOverload_literalMutationString2022_literalMutationString2062_add2463__2 = // MethodCallAdder
        input.getBytes();
        // AssertGenerator add assertion
        byte[] array_1208308787 = new byte[]{64};
        	byte[] array_1970884701 = (byte[])o_testHashByteArrayOverload_literalMutationString2022_literalMutationString2062_add2463__2;
        	for(int ii = 0; ii <array_1208308787.length; ii++) {
        		org.junit.Assert.assertEquals(array_1208308787[ii], array_1970884701[ii]);
        	};
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2022__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("@", input);
        // AssertGenerator add assertion
        byte[] array_1256377864 = new byte[]{64};
        	byte[] array_1146581767 = (byte[])o_testHashByteArrayOverload_literalMutationString2022_literalMutationString2062_add2463__2;
        	for(int ii = 0; ii <array_1256377864.length; ii++) {
        		org.junit.Assert.assertEquals(array_1256377864[ii], array_1146581767[ii]);
        	};
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2024 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2024_literalMutationString2082 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2024_literalMutationString2082_add2651() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match M8urmurHash.hash64(String)";
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2024_literalMutationString2082_add2651__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2069627323, ((int) (o_testHashByteArrayOverload_literalMutationString2024_literalMutationString2082_add2651__4)));
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2024__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2024__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(2069627323, ((int) (o_testHashByteArrayOverload_literalMutationString2024_literalMutationString2082_add2651__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match M8urmurHash.hash64(String)", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_add2031 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_add2031_add2161 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_add2031_add2161_add3433() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031_add2161_add3433__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload_add2031_add2161_add3433__4)));
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031_add2161__10 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031__8 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_add2031__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload_add2031_add2161_add3433__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2023 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString2023_literalMutationString2070 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString2023_literalMutationString2070_add2530() {
        java.lang.String input = "hathis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hathis", input);
        byte[] inputBytes = input.getBytes();
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023_literalMutationString2070_add2530__4 = // MethodCallAdder
        com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1467508667, ((int) (o_testHashByteArrayOverload_literalMutationString2023_literalMutationString2070_add2530__4)));
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__6 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString2023__8 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(1467508667, ((int) (o_testHashByteArrayOverload_literalMutationString2023_literalMutationString2070_add2530__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hathis", input);
    }
}

