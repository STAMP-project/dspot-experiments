/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.planner;


import io.crate.analyze.OrderBy;
import io.crate.expression.symbol.Symbol;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


public class PositionalOrderByTest {
    @Test
    public void testNewOutputMapping() throws Exception {
        List<Symbol> oldOutputs = Arrays.asList(PositionalOrderByTest.ref("a"), PositionalOrderByTest.ref("b"), PositionalOrderByTest.ref("c"), PositionalOrderByTest.ref("d"));
        List<Symbol> newOutputs = Arrays.asList(PositionalOrderByTest.ref("b"), PositionalOrderByTest.ref("c"), PositionalOrderByTest.ref("d"), PositionalOrderByTest.ref("a"));
        OrderBy orderBy = new OrderBy(Arrays.asList(PositionalOrderByTest.ref("a"), PositionalOrderByTest.ref("c"), PositionalOrderByTest.ref("b")), new boolean[]{ true, false, true }, new Boolean[]{ true, null, null });
        PositionalOrderBy positionalOrderBy = PositionalOrderBy.of(orderBy, oldOutputs);
        PositionalOrderBy newOrderBy = positionalOrderBy.tryMapToNewOutputs(oldOutputs, newOutputs);
        PositionalOrderBy expected = PositionalOrderBy.of(orderBy, newOutputs);
        MatcherAssert.assertThat(newOrderBy.indices(), Matchers.is(expected.indices()));
        MatcherAssert.assertThat(newOrderBy.reverseFlags(), Matchers.is(expected.reverseFlags()));
        MatcherAssert.assertThat(newOrderBy.nullsFirst(), Matchers.is(expected.nullsFirst()));
    }
}

