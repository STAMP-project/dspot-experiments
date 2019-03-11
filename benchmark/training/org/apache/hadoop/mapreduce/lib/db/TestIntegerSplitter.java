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
package org.apache.hadoop.mapreduce.lib.db;


import java.sql.SQLException;
import java.util.List;
import org.junit.Test;


public class TestIntegerSplitter {
    @Test
    public void testEvenSplits() throws SQLException {
        List<Long> splits = new IntegerSplitter().split(10, 0, 100);
        long[] expected = new long[]{ 0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 };
        assertLongArrayEquals(expected, toLongArray(splits));
    }

    @Test
    public void testOddSplits() throws SQLException {
        List<Long> splits = new IntegerSplitter().split(10, 0, 95);
        long[] expected = new long[]{ 0, 9, 18, 27, 36, 45, 54, 63, 72, 81, 90, 95 };
        assertLongArrayEquals(expected, toLongArray(splits));
    }

    @Test
    public void testSingletonSplit() throws SQLException {
        List<Long> splits = new IntegerSplitter().split(1, 5, 5);
        long[] expected = new long[]{ 5, 5 };
        assertLongArrayEquals(expected, toLongArray(splits));
    }

    @Test
    public void testSingletonSplit2() throws SQLException {
        // Same test, but overly-high numSplits
        List<Long> splits = new IntegerSplitter().split(5, 5, 5);
        long[] expected = new long[]{ 5, 5 };
        assertLongArrayEquals(expected, toLongArray(splits));
    }

    @Test
    public void testTooManySplits() throws SQLException {
        List<Long> splits = new IntegerSplitter().split(5, 3, 5);
        long[] expected = new long[]{ 3, 4, 5 };
        assertLongArrayEquals(expected, toLongArray(splits));
    }
}

