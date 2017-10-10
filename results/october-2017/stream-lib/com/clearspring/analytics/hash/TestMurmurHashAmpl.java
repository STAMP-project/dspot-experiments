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
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload() {
        java.lang.String input = "hashthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_0 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload__7)));
        java.lang.Object bytesAsObject = inputBytes;
        long long_1 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-8896273065425798843L, ((long) (o_testHash64ByteArrayOverload__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
    }

    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload() {
        java.lang.String input = "hashthis";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_2 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_3 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-1974946086, ((int) (o_testHashByteArrayOverload__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString6() {
        java.lang.String input = "hshthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_14 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6__7)));
        java.lang.Object bytesAsObject = inputBytes;
        long long_15 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(6901914564845240700L, ((long) (o_testHash64ByteArrayOverload_literalMutationString6__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_6 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2__7)));
        java.lang.Object bytesAsObject = inputBytes;
        long long_7 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7065322190441338459L, ((long) (o_testHash64ByteArrayOverload_literalMutationString2__7)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString3() {
        java.lang.String input = "hash}this";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}this", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_8 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        long long_9 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-4410106181081413790L, ((long) (o_testHash64ByteArrayOverload_literalMutationString3__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}this", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4() {
        java.lang.String input = "h*shthis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7852005312626181413L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7)));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7852005312626181413L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h*shthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-7852005312626181413L, ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString42() {
        java.lang.String input = "h*shhis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_87 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_88 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10));
        java.lang.String String_89 = input;
        java.lang.Long Long_90 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h*shhis", String_89);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h*shhis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString45() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_99 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_100 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10));
        java.lang.String String_101 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_101);
        java.lang.Long Long_102 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_101);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString3 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString3_literalMutationString35() {
        java.lang.String input = "hash}t&is";
        java.lang.String String_59 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}t&is", String_59);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_8 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        long long_9 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_60 = ((long) (o_testHash64ByteArrayOverload_literalMutationString3__10));
        java.lang.Long Long_61 = ((long) (o_testHash64ByteArrayOverload_literalMutationString3__7));
        java.lang.String String_62 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}t&is", String_62);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}t&is", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hash}t&is", String_59);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2_literalMutationString32() {
        java.lang.String input = "hello world";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_6 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_47 = ((long) (o_testHash64ByteArrayOverload_literalMutationString2__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_7 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_48 = ((long) (o_testHash64ByteArrayOverload_literalMutationString2__10));
        java.lang.String String_49 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_49);
        java.lang.Long Long_50 = ((long) (o_testHash64ByteArrayOverload_literalMutationString2__7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_49);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString3 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString3_literalMutationString39() {
        java.lang.String input = "hah}this";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hah}this", input);
        java.lang.String String_75 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hah}this", String_75);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_8 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        long long_9 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString3__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_76 = ((long) (o_testHash64ByteArrayOverload_literalMutationString3__10));
        java.lang.Long Long_77 = ((long) (o_testHash64ByteArrayOverload_literalMutationString3__7));
        java.lang.String String_78 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hah}this", String_78);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hah}this", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hah}this", String_75);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString6 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString6_literalMutationString57() {
        java.lang.String input = "hshtis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshtis", input);
        java.lang.String String_158 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshtis", String_158);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_14 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_159 = ((long) (o_testHash64ByteArrayOverload_literalMutationString6__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_15 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_160 = ((long) (o_testHash64ByteArrayOverload_literalMutationString6__10));
        java.lang.Long Long_161 = ((long) (o_testHash64ByteArrayOverload_literalMutationString6__7));
        java.lang.String String_162 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshtis", String_162);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshtis", String_158);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hshtis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4_literalMutationString41 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString41_literalMutationString243() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        java.lang.String String_504 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_504);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_83 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_84 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10));
        java.lang.String String_85 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_85);
        java.lang.String String_505 = String_85;
        java.lang.Long Long_86 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.String String_506 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_506);
        java.lang.String String_507 = String_85;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_507);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_85);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_506);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_504);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", String_505);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4_literalMutationString40 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString40_literalMutationString238() {
        java.lang.String input = "hrshhis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_79 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_80 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10));
        java.lang.String String_81 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", String_81);
        java.lang.Long Long_82 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.String String_490 = String_81;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", String_490);
        java.lang.String String_491 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", String_491);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", String_490);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshhis", String_81);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4_literalMutationString40 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString40_literalMutationString239() {
        java.lang.String input = "hrshtghis";
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_79 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_80 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10));
        java.lang.String String_81 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshtghis", String_81);
        java.lang.Long Long_82 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.String String_492 = String_81;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshtghis", String_492);
        java.lang.String String_493 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshtghis", String_493);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshtghis", String_492);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshtghis", String_81);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hrshtghis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString2_literalMutationString32 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString2_literalMutationString32_literalMutationString195() {
        java.lang.String input = "hello wor(ld";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", input);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_6 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_47 = ((long) (o_testHash64ByteArrayOverload_literalMutationString2__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_7 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString2__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_48 = ((long) (o_testHash64ByteArrayOverload_literalMutationString2__10));
        java.lang.String String_49 = input;
        java.lang.String String_325 = String_49;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", String_325);
        java.lang.Long Long_50 = ((long) (o_testHash64ByteArrayOverload_literalMutationString2__7));
        java.lang.String String_326 = String_49;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", String_326);
        java.lang.String String_327 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", String_327);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", String_325);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", String_326);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello wor(ld", String_49);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString4_literalMutationString45 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString4_literalMutationString45_literalMutationString265() {
        java.lang.String input = "hello world";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
        java.lang.String String_568 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_568);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_10 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_99 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_11 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString4__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_100 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__10));
        java.lang.String String_101 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_101);
        java.lang.String String_569 = String_101;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_569);
        java.lang.Long Long_102 = ((long) (o_testHash64ByteArrayOverload_literalMutationString4__7));
        java.lang.String String_570 = String_101;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_570);
        java.lang.String String_571 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_571);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_570);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_101);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_568);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_569);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString6 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHash64ByteArrayOverload_literalMutationString6_literalMutationString55 */
    @org.junit.Test(timeout = 10000)
    public void testHash64ByteArrayOverload_literalMutationString6_literalMutationString55_literalMutationString321() {
        java.lang.String input = "hhthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", input);
        java.lang.String String_857 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_857);
        java.lang.String String_148 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_148);
        java.lang.String String_858 = String_148;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_858);
        byte[] inputBytes = input.getBytes();
        long hashOfString = com.clearspring.analytics.hash.MurmurHash.hash64(input);
        long long_14 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__7 = com.clearspring.analytics.hash.MurmurHash.hash64(inputBytes);
        java.lang.Long Long_149 = ((long) (o_testHash64ByteArrayOverload_literalMutationString6__7));
        java.lang.Object bytesAsObject = inputBytes;
        long long_15 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        long o_testHash64ByteArrayOverload_literalMutationString6__10 = com.clearspring.analytics.hash.MurmurHash.hash64(bytesAsObject);
        java.lang.Long Long_150 = ((long) (o_testHash64ByteArrayOverload_literalMutationString6__10));
        java.lang.Long Long_151 = ((long) (o_testHash64ByteArrayOverload_literalMutationString6__7));
        java.lang.String String_152 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_152);
        java.lang.String String_859 = String_152;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_859);
        java.lang.String String_860 = String_148;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_860);
        java.lang.String String_861 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_861);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_858);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_859);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_152);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_148);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_860);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hhthis", String_857);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString866() {
        java.lang.String input = "MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_918 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(7931051, ((int) (o_testHashByteArrayOverload_literalMutationString866__7)));
        java.lang.Object bytesAsObject = inputBytes;
        int int_919 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(7931051, ((int) (o_testHashByteArrayOverload_literalMutationString866__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("MurmurHash.hash64(Object) given a byte[] did not match MurmurHash.hash64(String)", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(7931051, ((int) (o_testHashByteArrayOverload_literalMutationString866__7)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString867() {
        java.lang.String input = "hashhis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashhis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_920 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString867__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_921 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString867__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-590652159, ((int) (o_testHashByteArrayOverload_literalMutationString867__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashhis", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(-590652159, ((int) (o_testHashByteArrayOverload_literalMutationString867__7)));
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString868() {
        java.lang.String input = "hashAthis";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashAthis", input);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_922 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_923 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(426960432, ((int) (o_testHashByteArrayOverload_literalMutationString868__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(426960432, ((int) (o_testHashByteArrayOverload_literalMutationString868__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hashAthis", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString866 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString866_literalMutationString895() {
        java.lang.String input = "hello world";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
        java.lang.String String_955 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_955);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_918 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Integer Integer_956 = ((int) (o_testHashByteArrayOverload_literalMutationString866__7));
        java.lang.Object bytesAsObject = inputBytes;
        int int_919 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_957 = ((int) (o_testHashByteArrayOverload_literalMutationString866__10));
        java.lang.String String_958 = input;
        java.lang.Integer Integer_959 = ((int) (o_testHashByteArrayOverload_literalMutationString866__7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_958);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("hello world", String_955);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString865 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString865_literalMutationString891() {
        java.lang.String input = "?";
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_916 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString865__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Integer Integer_936 = ((int) (o_testHashByteArrayOverload_literalMutationString865__7));
        java.lang.Object bytesAsObject = inputBytes;
        int int_917 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString865__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_937 = ((int) (o_testHashByteArrayOverload_literalMutationString865__10));
        java.lang.Integer Integer_938 = ((int) (o_testHashByteArrayOverload_literalMutationString865__7));
        java.lang.String String_939 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("?", String_939);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("?", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString866 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString866_literalMutationString893() {
        java.lang.String input = "ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!", input);
        java.lang.String String_945 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!", String_945);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_918 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Integer Integer_946 = ((int) (o_testHashByteArrayOverload_literalMutationString866__7));
        java.lang.Object bytesAsObject = inputBytes;
        int int_919 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_947 = ((int) (o_testHashByteArrayOverload_literalMutationString866__10));
        java.lang.String String_948 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!", String_948);
        java.lang.Integer Integer_949 = ((int) (o_testHashByteArrayOverload_literalMutationString866__7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!", String_945);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ySysP>6W.t0C-?9AC*$S oY.>c^U!$Cz2lvLY3Pe#L360:}[gYFUICnc)SU7EvLBHp9HIW?9U-1%h+1!", String_948);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString866 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString866_literalMutationString892() {
        java.lang.String input = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        java.lang.String String_940 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_940);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_918 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Integer Integer_941 = ((int) (o_testHashByteArrayOverload_literalMutationString866__7));
        java.lang.Object bytesAsObject = inputBytes;
        int int_919 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString866__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_942 = ((int) (o_testHashByteArrayOverload_literalMutationString866__10));
        java.lang.String String_943 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_943);
        java.lang.Integer Integer_944 = ((int) (o_testHashByteArrayOverload_literalMutationString866__7));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_940);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_943);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868_literalMutationString905 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString868_literalMutationString905_literalMutationString1107() {
        java.lang.String input = "z";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", input);
        java.lang.String String_1470 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1470);
        java.lang.String String_998 = input;
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_922 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_923 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_999 = ((int) (o_testHashByteArrayOverload_literalMutationString868__10));
        java.lang.Integer Integer_1000 = ((int) (o_testHashByteArrayOverload_literalMutationString868__7));
        java.lang.String String_1001 = input;
        java.lang.String String_1471 = String_1001;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1471);
        java.lang.String String_1472 = String_998;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1472);
        java.lang.String String_1473 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1473);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1470);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1472);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_998);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1001);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("z", String_1471);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868_literalMutationString908 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString868_literalMutationString908_literalMutationString1121() {
        java.lang.String input = "h{ashAt;his";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", input);
        java.lang.String String_1527 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1527);
        java.lang.String String_1010 = input;
        java.lang.String String_1528 = String_1010;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1528);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_922 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_923 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_1011 = ((int) (o_testHashByteArrayOverload_literalMutationString868__10));
        java.lang.Integer Integer_1012 = ((int) (o_testHashByteArrayOverload_literalMutationString868__7));
        java.lang.String String_1013 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1013);
        java.lang.String String_1529 = String_1013;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1529);
        java.lang.String String_1530 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1530);
        java.lang.String String_1531 = String_1010;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1531);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1013);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1010);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1529);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1528);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1530);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("h{ashAt;his", String_1527);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868_literalMutationString904 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString868_literalMutationString904_literalMutationString1100() {
        java.lang.String input = "";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        java.lang.String String_1437 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1437);
        java.lang.String String_994 = input;
        java.lang.String String_1438 = String_994;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1438);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_922 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_923 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_995 = ((int) (o_testHashByteArrayOverload_literalMutationString868__10));
        java.lang.Integer Integer_996 = ((int) (o_testHashByteArrayOverload_literalMutationString868__7));
        java.lang.String String_997 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_997);
        java.lang.String String_1439 = String_997;
        java.lang.String String_1440 = String_994;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1440);
        java.lang.String String_1441 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1441);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1437);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1438);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_997);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_994);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1440);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("", String_1439);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868_literalMutationString904 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString868_literalMutationString904_literalMutationString1101() {
        java.lang.String input = "ha!hAths";
        java.lang.String String_1442 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1442);
        java.lang.String String_994 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_994);
        java.lang.String String_1443 = String_994;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1443);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_922 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_923 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_995 = ((int) (o_testHashByteArrayOverload_literalMutationString868__10));
        java.lang.Integer Integer_996 = ((int) (o_testHashByteArrayOverload_literalMutationString868__7));
        java.lang.String String_997 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_997);
        java.lang.String String_1444 = String_997;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1444);
        java.lang.String String_1445 = String_994;
        java.lang.String String_1446 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1446);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1442);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1444);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_997);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1445);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_1443);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", String_994);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha!hAths", input);
    }

    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868 */
    /* amplification of com.clearspring.analytics.hash.TestMurmurHash#testHashByteArrayOverload_literalMutationString868_literalMutationString904 */
    @org.junit.Test(timeout = 10000)
    public void testHashByteArrayOverload_literalMutationString868_literalMutationString904_literalMutationString1104() {
        java.lang.String input = "ha1shAths";
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", input);
        java.lang.String String_1457 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1457);
        java.lang.String String_994 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_994);
        java.lang.String String_1458 = String_994;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1458);
        byte[] inputBytes = input.getBytes();
        int hashOfString = com.clearspring.analytics.hash.MurmurHash.hash(input);
        int int_922 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__7 = com.clearspring.analytics.hash.MurmurHash.hash(inputBytes);
        java.lang.Object bytesAsObject = inputBytes;
        int int_923 = hashOfString;
        // AssertGenerator create local variable with return value of invocation
        int o_testHashByteArrayOverload_literalMutationString868__10 = com.clearspring.analytics.hash.MurmurHash.hash(bytesAsObject);
        java.lang.Integer Integer_995 = ((int) (o_testHashByteArrayOverload_literalMutationString868__10));
        java.lang.Integer Integer_996 = ((int) (o_testHashByteArrayOverload_literalMutationString868__7));
        java.lang.String String_997 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_997);
        java.lang.String String_1459 = String_997;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1459);
        java.lang.String String_1460 = String_994;
        java.lang.String String_1461 = input;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1461);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_997);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1458);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", input);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1457);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_994);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1460);
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals("ha1shAths", String_1459);
    }
}

