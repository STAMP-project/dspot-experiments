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
}

