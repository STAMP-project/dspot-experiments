/**
 * Licensed to Crate.IO GmbH ("Crate") under one or more contributor
 * license agreements.  See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.  Crate licenses
 * this file to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial agreement.
 */
package io.crate.execution.engine.distribution.merge;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import io.crate.breaker.RamAccountingContext;
import io.crate.breaker.RowAccountingWithEstimators;
import io.crate.data.Row;
import io.crate.data.RowN;
import io.crate.expression.symbol.Literal;
import io.crate.planner.PositionalOrderBy;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.Arrays;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.elasticsearch.common.breaker.CircuitBreakingException;
import org.elasticsearch.common.breaker.MemoryCircuitBreaker;
import org.elasticsearch.common.breaker.NoopCircuitBreaker;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.hamcrest.Matchers;
import org.junit.Test;


public class RamAccountingPageIteratorTest extends CrateUnitTest {
    private static NoopCircuitBreaker NOOP_CIRCUIT_BREAKER = new NoopCircuitBreaker("dummy");

    private static RowN[] TEST_ROWS = new RowN[]{ new RowN(new String[]{ "a", "b", "c" }), new RowN(new String[]{ "d", "e", "f" }) };

    private long originalBufferSize;

    @Test
    public void testNoRamAccountingWrappingAppliedForNullOrderByAndNonRepeat() {
        PagingIterator<Integer, Row> pagingIterator1 = PagingIterator.create(2, false, null, () -> null);
        assertThat(pagingIterator1, Matchers.instanceOf(PassThroughPagingIterator.class));
    }

    @Test
    public void testRamAccountingWrappingAppliedForRepeatableIterator() {
        PagingIterator<Integer, Row> repeatableIterator = PagingIterator.create(2, true, null, () -> null);
        assertThat(repeatableIterator, Matchers.instanceOf(RamAccountingPageIterator.class));
        assertThat(((RamAccountingPageIterator) (repeatableIterator)).delegatePagingIterator, Matchers.instanceOf(PassThroughPagingIterator.class));
    }

    @Test
    public void testRamAccountingWrappingAppliedForOrderedIterators() {
        PositionalOrderBy orderBy = PositionalOrderBy.of(new io.crate.analyze.OrderBy(Collections.singletonList(Literal.of(1)), new boolean[]{ false }, new Boolean[]{ false }), Collections.singletonList(Literal.of(1)));
        PagingIterator<Integer, Row> repeatingSortedPagingIterator = PagingIterator.create(2, true, orderBy, () -> null);
        assertThat(repeatingSortedPagingIterator, Matchers.instanceOf(RamAccountingPageIterator.class));
        assertThat(((RamAccountingPageIterator) (repeatingSortedPagingIterator)).delegatePagingIterator, Matchers.instanceOf(SortedPagingIterator.class));
        PagingIterator<Integer, Row> nonRepeatingSortedPagingIterator = PagingIterator.create(2, false, orderBy, () -> null);
        assertThat(nonRepeatingSortedPagingIterator, Matchers.instanceOf(RamAccountingPageIterator.class));
        assertThat(((RamAccountingPageIterator) (nonRepeatingSortedPagingIterator)).delegatePagingIterator, Matchers.instanceOf(SortedPagingIterator.class));
    }

    @Test
    public void testNoCircuitBreaking() {
        PagingIterator<Integer, Row> pagingIterator = PagingIterator.create(2, true, null, () -> new RowAccountingWithEstimators(ImmutableList.of(DataTypes.STRING, DataTypes.STRING, DataTypes.STRING), new RamAccountingContext("test", RamAccountingPageIteratorTest.NOOP_CIRCUIT_BREAKER)));
        assertThat(pagingIterator, Matchers.instanceOf(RamAccountingPageIterator.class));
        assertThat(((RamAccountingPageIterator) (pagingIterator)).delegatePagingIterator, Matchers.instanceOf(PassThroughPagingIterator.class));
        pagingIterator.merge(Arrays.asList(new KeyIterable(0, Collections.singletonList(RamAccountingPageIteratorTest.TEST_ROWS[0])), new KeyIterable(1, Collections.singletonList(RamAccountingPageIteratorTest.TEST_ROWS[1]))));
        pagingIterator.finish();
        Row[] rows = Iterators.toArray(pagingIterator, Row.class);
        assertThat(rows[0], TestingHelpers.isRow("a", "b", "c"));
        assertThat(rows[1], TestingHelpers.isRow("d", "e", "f"));
    }

    @Test
    public void testCircuitBreaking() throws Exception {
        PagingIterator<Integer, Row> pagingIterator = PagingIterator.create(2, true, null, () -> new RowAccountingWithEstimators(ImmutableList.of(DataTypes.STRING, DataTypes.STRING, DataTypes.STRING), new RamAccountingContext("test", new MemoryCircuitBreaker(new ByteSizeValue(197, ByteSizeUnit.BYTES), 1, LogManager.getLogger(.class)))));
        expectedException.expect(CircuitBreakingException.class);
        expectedException.expectMessage("Data too large, data for field [test] would be [198/198b], which is larger than the limit of [197/197b]");
        pagingIterator.merge(Arrays.asList(new KeyIterable(0, Collections.singletonList(RamAccountingPageIteratorTest.TEST_ROWS[0])), new KeyIterable(1, Collections.singletonList(RamAccountingPageIteratorTest.TEST_ROWS[1]))));
    }
}

