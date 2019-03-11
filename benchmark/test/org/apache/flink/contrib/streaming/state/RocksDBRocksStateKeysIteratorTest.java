/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.contrib.streaming.state;


import IntSerializer.INSTANCE;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the RocksIteratorWrapper.
 */
public class RocksDBRocksStateKeysIteratorTest {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testIterator() throws Exception {
        // test for keyGroupPrefixBytes == 1 && ambiguousKeyPossible == false
        testIteratorHelper(INSTANCE, StringSerializer.INSTANCE, 128, ( i) -> i);
        // test for keyGroupPrefixBytes == 1 && ambiguousKeyPossible == true
        testIteratorHelper(StringSerializer.INSTANCE, StringSerializer.INSTANCE, 128, ( i) -> String.valueOf(i));
        // test for keyGroupPrefixBytes == 2 && ambiguousKeyPossible == false
        testIteratorHelper(INSTANCE, StringSerializer.INSTANCE, 256, ( i) -> i);
        // test for keyGroupPrefixBytes == 2 && ambiguousKeyPossible == true
        testIteratorHelper(StringSerializer.INSTANCE, StringSerializer.INSTANCE, 256, ( i) -> String.valueOf(i));
    }
}

