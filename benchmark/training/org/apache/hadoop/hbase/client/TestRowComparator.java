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
package org.apache.hadoop.hbase.client;


import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, SmallTests.class })
public class TestRowComparator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRowComparator.class);

    private static final List<byte[]> DEFAULT_ROWS = IntStream.range(1, 9).mapToObj(String::valueOf).map(Bytes::toBytes).collect(Collectors.toList());

    @Test
    public void testPut() {
        TestRowComparator.test(( row) -> new Put(row));
    }

    @Test
    public void testDelete() {
        TestRowComparator.test(( row) -> new Delete(row));
    }

    @Test
    public void testAppend() {
        TestRowComparator.test(( row) -> new Append(row));
    }

    @Test
    public void testIncrement() {
        TestRowComparator.test(( row) -> new Increment(row));
    }

    @Test
    public void testGet() {
        TestRowComparator.test(( row) -> new Get(row));
    }
}

