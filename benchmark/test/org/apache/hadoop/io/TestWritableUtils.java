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
package org.apache.hadoop.io;


import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestWritableUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestWritableUtils.class);

    @Test
    public void testVInt() throws Exception {
        testValue(12, 1);
        testValue(127, 1);
        testValue((-112), 1);
        testValue((-113), 2);
        testValue((-128), 2);
        testValue(128, 2);
        testValue((-129), 2);
        testValue(255, 2);
        testValue((-256), 2);
        testValue(256, 3);
        testValue((-257), 3);
        testValue(65535, 3);
        testValue((-65536), 3);
        testValue(65536, 4);
        testValue((-65537), 4);
        testReadInRange(123, 122, 123, true);
        testReadInRange(123, 0, 100, false);
        testReadInRange(0, 0, 100, true);
        testReadInRange((-1), 0, 100, false);
        testReadInRange(1099511627776L, 0, Integer.MAX_VALUE, false);
    }
}

