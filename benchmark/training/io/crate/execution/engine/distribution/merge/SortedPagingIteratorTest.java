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


import com.google.common.collect.Ordering;
import io.crate.data.ArrayBucket;
import io.crate.data.Bucket;
import io.crate.data.Row;
import io.crate.execution.engine.sort.OrderingByPosition;
import io.crate.test.integration.CrateUnitTest;
import io.crate.testing.TestingHelpers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Test;


public class SortedPagingIteratorTest extends CrateUnitTest {
    public static final Ordering<Row> ORDERING = OrderingByPosition.rowOrdering(new int[]{ 0 }, new boolean[]{ false }, new Boolean[]{ null });

    @Test
    public void testTwoBucketsAndTwoPagesAreSortedCorrectly() throws Exception {
        SortedPagingIterator<Void, Row> pagingIterator = new SortedPagingIterator(SortedPagingIteratorTest.ORDERING, randomBoolean());
        pagingIterator.merge(numberedBuckets(Arrays.<Bucket>asList(new ArrayBucket(new Object[][]{ new Object[]{ "a" }, new Object[]{ "b" }, new Object[]{ "c" } }), new ArrayBucket(new Object[][]{ new Object[]{ "x" }, new Object[]{ "y" } }))));
        List<Object[]> rows = new ArrayList<>();
        consumeRows(pagingIterator, rows);
        assertThat(rows.size(), Is.is(3));
        assertThat(TestingHelpers.printRows(rows), Is.is("a\nb\nc\n"));
        pagingIterator.merge(numberedBuckets(Arrays.<Bucket>asList(new ArrayBucket(new Object[][]{ new Object[]{ "d" }, new Object[]{ "e" } }), new ArrayBucket(new Object[][]{ new Object[]{ "y" }, new Object[]{ "z" } }))));
        consumeRows(pagingIterator, rows);
        assertThat(rows.size(), Is.is(5));
        assertThat(TestingHelpers.printRows(rows), Is.is("a\nb\nc\nd\ne\n"));
        pagingIterator.finish();
        consumeRows(pagingIterator, rows);
        assertThat(rows.size(), Is.is(9));
        assertThat(TestingHelpers.printRows(rows), Is.is("a\nb\nc\nd\ne\nx\ny\ny\nz\n"));
    }

    @Test
    public void testReplayReplaysCorrectly() throws Exception {
        SortedPagingIterator<Void, Row> pagingIterator = new SortedPagingIterator(SortedPagingIteratorTest.ORDERING, true);
        pagingIterator.merge(numberedBuckets(Arrays.<Bucket>asList(new ArrayBucket(new Object[][]{ new Object[]{ "a" }, new Object[]{ "b" }, new Object[]{ "c" } }), new ArrayBucket(new Object[][]{ new Object[]{ "x" }, new Object[]{ "y" } }), new ArrayBucket(new Object[][]{ new Object[]{ "m" }, new Object[]{ "n" }, new Object[]{ "o" } }))));
        List<Object> rows = new ArrayList<>();
        consumeSingleColumnRows(pagingIterator, rows);
        pagingIterator.merge(numberedBuckets(Arrays.<Bucket>asList(new ArrayBucket(new Object[][]{ new Object[]{ "d" }, new Object[]{ "e" }, new Object[]{ "f" } }), new ArrayBucket(new Object[][]{ new Object[]{ "z" } }))));
        pagingIterator.finish();
        consumeSingleColumnRows(pagingIterator, rows);
        assertThat(rows.toString(), Is.is("[a, b, c, d, e, f, m, n, o, x, y, z]"));
        List<Object> replayedRows = new ArrayList<>();
        consumeSingleColumnRows(pagingIterator.repeat().iterator(), replayedRows);
        assertThat(rows, Is.is(replayedRows));
    }
}

