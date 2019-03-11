/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.executor.sql.execute.result;


import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import lombok.SneakyThrows;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class AggregationDistinctQueryResultTest {
    private AggregationDistinctQueryResult aggregationDistinctQueryResult;

    @Test
    public void assertDivide() {
        List<DistinctQueryResult> actual = aggregationDistinctQueryResult.divide();
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        Assert.assertThat(actual.iterator().next().getColumnCount(), CoreMatchers.is(((Object) (5))));
    }

    @Test
    public void assertGetValueByColumnIndex() {
        aggregationDistinctQueryResult.next();
        Assert.assertThat(aggregationDistinctQueryResult.getValue(1, Object.class), CoreMatchers.is(((Object) (10))));
        Assert.assertThat(aggregationDistinctQueryResult.getValue(2, Object.class), CoreMatchers.is(((Object) (1))));
        Assert.assertThat(aggregationDistinctQueryResult.getValue(3, Object.class), CoreMatchers.is(((Object) (10))));
        Assert.assertThat(aggregationDistinctQueryResult.getValue(4, Object.class), CoreMatchers.is(((Object) (1))));
        Assert.assertThat(aggregationDistinctQueryResult.getValue(5, Object.class), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetValueByColumnLabel() {
        aggregationDistinctQueryResult.next();
        Assert.assertThat(aggregationDistinctQueryResult.getValue("order_id", Object.class), CoreMatchers.is(((Object) (10))));
        Assert.assertThat(aggregationDistinctQueryResult.getValue("a", Object.class), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetCalendarValueByColumnIndex() {
        aggregationDistinctQueryResult.next();
        Assert.assertThat(aggregationDistinctQueryResult.getCalendarValue(1, Object.class, Calendar.getInstance()), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetCalendarValueByColumnLabel() {
        aggregationDistinctQueryResult.next();
        Assert.assertThat(aggregationDistinctQueryResult.getCalendarValue("order_id", Object.class, Calendar.getInstance()), CoreMatchers.is(((Object) (10))));
    }

    @Test
    @SneakyThrows
    public void assertGetInputStreamByColumnIndex() {
        aggregationDistinctQueryResult.next();
        Assert.assertThat(aggregationDistinctQueryResult.getInputStream(1, "Unicode").read(), CoreMatchers.is(getInputStream(10).read()));
    }

    @Test
    @SneakyThrows
    public void assertGetInputStreamByColumnLabel() {
        aggregationDistinctQueryResult.next();
        Assert.assertThat(aggregationDistinctQueryResult.getInputStream("order_id", "Unicode").read(), CoreMatchers.is(getInputStream(10).read()));
    }

    @Test
    public void assertWasNull() {
        Assert.assertTrue(aggregationDistinctQueryResult.wasNull());
    }

    @Test
    public void assertGetColumnCount() {
        Assert.assertThat(aggregationDistinctQueryResult.getColumnCount(), CoreMatchers.is(5));
    }

    @Test
    @SneakyThrows
    public void assertGetColumnLabel() {
        Assert.assertThat(aggregationDistinctQueryResult.getColumnLabel(3), CoreMatchers.is("a"));
        Assert.assertThat(aggregationDistinctQueryResult.getColumnLabel(1), CoreMatchers.is("order_id"));
    }

    @Test(expected = SQLException.class)
    @SneakyThrows
    public void assertGetColumnLabelWithException() {
        Assert.assertThat(aggregationDistinctQueryResult.getColumnLabel(6), CoreMatchers.is("order_id"));
    }

    @Test
    public void assertGetColumnIndex() {
        Assert.assertThat(aggregationDistinctQueryResult.getColumnIndex("c"), CoreMatchers.is(2));
    }
}

