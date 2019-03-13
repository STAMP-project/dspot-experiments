/**
 * Copyright 2016 Naver Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.server.bo.codec.stat.strategy;


import org.junit.Test;


/**
 *
 *
 * @author HyunGil Jeong
 */
public class UnsignedLongEncodingStrategyTest extends EncodingStrategyTestBase<Long> {
    @Test
    public void test_small_values() {
        long minValue = 10;
        long maxValue = 100;
        testValues(minValue, maxValue);
    }

    @Test
    public void test_medium_values() {
        long minValue = 1000;
        long maxValue = 1000000;
        testValues(minValue, maxValue);
    }

    @Test
    public void test_large_values() {
        long minValue = 1000000;
        long maxValue = 1000000000;
        testValues(minValue, maxValue);
    }

    @Test
    public void test_huge_values() {
        long minValue = 1000000000;
        long maxValue = 100000000000000000L;
        testValues(minValue, maxValue);
    }
}

