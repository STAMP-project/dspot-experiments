/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.linq4j.test;


import CorrelateJoinType.ANTI;
import CorrelateJoinType.INNER;
import CorrelateJoinType.LEFT;
import CorrelateJoinType.SEMI;
import org.apache.calcite.linq4j.function.Function2;
import org.junit.Test;


/**
 * Tests {@link org.apache.calcite.linq4j.ExtendedEnumerable#correlateJoin}
 */
public class CorrelateJoinTest {
    static final Function2<Integer, Integer, Integer[]> SELECT_BOTH = ( v0, v1) -> new Integer[]{ v0, v1 };

    @Test
    public void testInner() {
        testJoin(INNER, new Integer[][]{ new Integer[]{ 2, 20 }, new Integer[]{ 3, -30 }, new Integer[]{ 3, -60 }, new Integer[]{ 20, 200 }, new Integer[]{ 30, -300 }, new Integer[]{ 30, -600 } });
    }

    @Test
    public void testLeft() {
        testJoin(LEFT, new Integer[][]{ new Integer[]{ 1, null }, new Integer[]{ 2, 20 }, new Integer[]{ 3, -30 }, new Integer[]{ 3, -60 }, new Integer[]{ 10, null }, new Integer[]{ 20, 200 }, new Integer[]{ 30, -300 }, new Integer[]{ 30, -600 } });
    }

    @Test
    public void testSemi() {
        testJoin(SEMI, new Integer[][]{ new Integer[]{ 2, null }, new Integer[]{ 3, null }, new Integer[]{ 20, null }, new Integer[]{ 30, null } });
    }

    @Test
    public void testAnti() {
        testJoin(ANTI, new Integer[][]{ new Integer[]{ 1, null }, new Integer[]{ 10, null } });
    }
}

/**
 * End CorrelateJoinTest.java
 */
