/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.types;


import java.util.Random;
import org.apache.flink.util.StringUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the serialization of StringValue.
 */
public class StringValueSerializationTest {
    private final Random rnd = new Random(2093486528937460234L);

    @Test
    public void testNonNullValues() {
        try {
            String[] testStrings = new String[]{ "a", "", "bcd", "jbmbmner8 jhk hj \n \t \u00fc\u00e4\u00df\u00df@\u00b5", "", "non-empty" };
            StringValueSerializationTest.testSerialization(testStrings);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testLongValues() {
        try {
            String[] testStrings = new String[]{ StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)) };
            StringValueSerializationTest.testSerialization(testStrings);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testMixedValues() {
        try {
            String[] testStrings = new String[]{ StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), "", StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), "", StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), "" };
            StringValueSerializationTest.testSerialization(testStrings);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }

    @Test
    public void testBinaryCopyOfLongStrings() {
        try {
            String[] testStrings = new String[]{ StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), "", StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), "", StringUtils.getRandomString(rnd, 10000, ((1024 * 1024) * 2)), "" };
            StringValueSerializationTest.testCopy(testStrings);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            Assert.fail(("Exception in test: " + (e.getMessage())));
        }
    }
}

