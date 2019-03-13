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
public class UnsignedShortEncodingStrategyTest extends EncodingStrategyTestBase<Short> {
    @Test
    public void test_small_values() {
        short minValue = 10;
        short maxValue = 100;
        testValues(minValue, maxValue);
    }

    @Test
    public void test_medium_values() {
        short minValue = 100;
        short maxValue = 1000;
        testValues(minValue, maxValue);
    }

    @Test
    public void test_large_values() {
        short minValue = 1000;
        short maxValue = 10000;
        testValues(minValue, maxValue);
    }
}

