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
package org.apache.calcite.adapter.elasticsearch;


import org.junit.ClassRule;
import org.junit.Test;


/**
 * Test of different boolean expressions (some more complex than others).
 */
public class BooleanLogicTest {
    @ClassRule
    public static final EmbeddedElasticsearchPolicy NODE = EmbeddedElasticsearchPolicy.create();

    private static final String NAME = "docs";

    @Test
    public void expressions() {
        assertSingle("select * from view");
        assertSingle("select * from view where a = 'a'");
        assertEmpty("select * from view where a <> 'a'");
        assertSingle("select * from view where  'a' = a");
        assertEmpty("select * from view where a = 'b'");
        assertEmpty("select * from view where 'b' = a");
        assertSingle("select * from view where a in ('a', 'b')");
        assertSingle("select * from view where a in ('a', 'c') and b = 'b'");
        assertSingle("select * from view where (a = 'ZZ' or a = 'a')  and b = 'b'");
        assertSingle("select * from view where b = 'b' and a in ('a', 'c')");
        assertSingle("select * from view where num = 42 and a in ('a', 'c')");
        assertEmpty("select * from view where a in ('a', 'c') and b = 'c'");
        assertSingle("select * from view where a in ('a', 'c') and b = 'b' and num = 42");
        assertSingle("select * from view where a in ('a', 'c') and b = 'b' and num >= 42");
        assertEmpty("select * from view where a in ('a', 'c') and b = 'b' and num <> 42");
        assertEmpty("select * from view where a in ('a', 'c') and b = 'b' and num > 42");
        assertSingle("select * from view where num = 42");
        assertSingle("select * from view where 42 = num");
        assertEmpty("select * from view where num > 42");
        assertEmpty("select * from view where 42 > num");
        assertEmpty("select * from view where num > 42 and num > 42");
        assertEmpty("select * from view where num > 42 and num < 42");
        assertEmpty("select * from view where num > 42 and num < 42 and num <> 42");
        assertEmpty("select * from view where num > 42 and num < 42 and num = 42");
        assertEmpty("select * from view where num > 42 or num < 42 and num = 42");
        assertSingle("select * from view where num > 42 and num < 42 or num = 42");
        assertSingle("select * from view where num > 42 or num < 42 or num = 42");
        assertSingle("select * from view where num >= 42 and num <= 42 and num = 42");
        assertEmpty("select * from view where num >= 42 and num <= 42 and num <> 42");
        assertEmpty("select * from view where num < 42");
        assertEmpty("select * from view where num <> 42");
        assertSingle("select * from view where num >= 42");
        assertSingle("select * from view where num <= 42");
        assertSingle("select * from view where num < 43");
        assertSingle("select * from view where num < 50");
        assertSingle("select * from view where num > 41");
        assertSingle("select * from view where num > 0");
        assertSingle("select * from view where (a = 'a' and b = 'b') or (num = 42 and c = 'c')");
        assertSingle("select * from view where c = 'c' and (a in ('a', 'b') or num in (41, 42))");
        assertSingle("select * from view where (a = 'a' or b = 'b') or (num = 42 and c = 'c')");
        assertSingle(("select * from view where a = 'a' and (b = '0' or (b = 'b' and " + "(c = '0' or (c = 'c' and num = 42))))"));
    }

    /**
     * Tests negations ({@code NOT} operator).
     */
    @Test
    public void notExpression() {
        assertEmpty("select * from view where not a = 'a'");
        assertSingle("select * from view where not not a = 'a'");
        assertEmpty("select * from view where not not not a = 'a'");
        assertSingle("select * from view where not a <> 'a'");
        assertSingle("select * from view where not not not a <> 'a'");
        assertEmpty("select * from view where not 'a' = a");
        assertSingle("select * from view where not 'a' <> a");
        assertSingle("select * from view where not a = 'b'");
        assertSingle("select * from view where not 'b' = a");
        assertEmpty("select * from view where not a in ('a')");
        assertEmpty("select * from view where a not in ('a')");
        assertSingle("select * from view where not a not in ('a')");
        assertEmpty("select * from view where not a not in ('b')");
        assertEmpty("select * from view where not not a not in ('a')");
        assertSingle("select * from view where not not a not in ('b')");
        assertEmpty("select * from view where not a in ('a', 'b')");
        assertEmpty("select * from view where a not in ('a', 'b')");
        assertEmpty("select * from view where not a not in ('z')");
        assertEmpty("select * from view where not a not in ('z')");
        assertSingle("select * from view where not a in ('z')");
        assertSingle("select * from view where not (not num = 42 or not a in ('a', 'c'))");
        assertEmpty("select * from view where not num > 0");
        assertEmpty("select * from view where num = 42 and a not in ('a', 'c')");
        assertSingle("select * from view where not (num > 42 or num < 42 and num = 42)");
    }
}

/**
 * End BooleanLogicTest.java
 */
