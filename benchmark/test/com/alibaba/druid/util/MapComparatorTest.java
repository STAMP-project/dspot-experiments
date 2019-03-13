/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.util;


import junit.framework.TestCase;


public class MapComparatorTest extends TestCase {
    private String orderByKey = "orderby";

    public void test_comparator_date() throws Exception {
        test_comparator_date_0(true);
        test_comparator_date_0(false);
    }

    public void test_comparator_String() throws Exception {
        test_comparator_string_0(true);
        test_comparator_string_0(false);
    }

    public void test_comparator_number() throws Exception {
        test_comparator_number_0(true);
        test_comparator_number_0(false);
    }

    public void test_comparator_array() throws Exception {
        test_comparator_array_0(true);
        test_comparator_array_0(false);
    }
}

