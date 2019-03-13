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


import java.util.Arrays;
import java.util.Locale;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import org.apache.calcite.test.CalciteAssert;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * Tests usage of scrolling API like correct results and resource cleanup
 * (delete scroll after scan).
 */
public class ScrollingTest {
    @ClassRule
    public static final EmbeddedElasticsearchPolicy NODE = EmbeddedElasticsearchPolicy.create();

    private static final String NAME = "scroll";

    private static final int SIZE = 10;

    @Test
    public void scrolling() throws Exception {
        final String[] expected = IntStream.range(0, ScrollingTest.SIZE).mapToObj(( i) -> "V=" + i).toArray(String[]::new);
        final String query = String.format(Locale.ROOT, ("select _MAP['value'] as v from " + "\"elastic\".\"%s\""), ScrollingTest.NAME);
        for (int fetchSize : Arrays.asList(1, 2, 3, ((ScrollingTest.SIZE) / 2), ((ScrollingTest.SIZE) - 1), ScrollingTest.SIZE, ((ScrollingTest.SIZE) + 1), (2 * (ScrollingTest.SIZE)))) {
            CalciteAssert.that().with(newConnectionFactory(fetchSize)).query(query).returnsUnordered(expected);
            assertNoActiveScrolls();
        }
    }
}

/**
 * End ScrollingTest.java
 */
