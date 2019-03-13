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


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.SneakyThrows;
import org.apache.shardingsphere.core.merger.QueryResult;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class DistinctQueryResultTest {
    private DistinctQueryResult distinctQueryResult;

    @Test
    public void assertDivide() {
        List<DistinctQueryResult> actual = distinctQueryResult.divide();
        Assert.assertThat(actual.size(), CoreMatchers.is(2));
        Assert.assertThat(actual.iterator().next().getColumnCount(), CoreMatchers.is(((Object) (1))));
    }

    @Test
    public void assertNext() {
        Assert.assertTrue(distinctQueryResult.next());
        Assert.assertTrue(distinctQueryResult.next());
        Assert.assertFalse(distinctQueryResult.next());
    }

    @Test
    public void assertGetValueByColumnIndex() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getValue(1, Object.class), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetValueByColumnLabel() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getValue("order_id", Object.class), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetCalendarValueByColumnIndex() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getCalendarValue(1, Object.class, Calendar.getInstance()), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetCalendarValueByColumnLabel() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getCalendarValue("order_id", Object.class, Calendar.getInstance()), CoreMatchers.is(((Object) (10))));
    }

    @Test
    @SneakyThrows
    public void assertGetInputStreamByColumnIndex() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getInputStream(1, "Unicode").read(), CoreMatchers.is(getInputStream(10).read()));
    }

    @Test
    @SneakyThrows
    public void assertGetInputStreamByColumnLabel() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getInputStream("order_id", "Unicode").read(), CoreMatchers.is(getInputStream(10).read()));
    }

    @Test
    public void assertWasNull() {
        Assert.assertTrue(distinctQueryResult.wasNull());
    }

    @Test
    public void assertGetColumnCount() {
        Assert.assertThat(distinctQueryResult.getColumnCount(), CoreMatchers.is(1));
    }

    @Test
    @SneakyThrows
    public void assertGetColumnLabel() {
        Assert.assertThat(distinctQueryResult.getColumnLabel(1), CoreMatchers.is("order_id"));
    }

    @Test(expected = SQLException.class)
    @SneakyThrows
    public void assertGetColumnLabelWithException() {
        Assert.assertThat(distinctQueryResult.getColumnLabel(2), CoreMatchers.is("order_id"));
    }

    @Test
    public void assertGetColumnIndex() {
        Assert.assertThat(distinctQueryResult.getColumnIndex("order_id"), CoreMatchers.is(1));
    }

    @Test
    public void assertGetColumnLabelAndIndexMap() {
        Multimap<String, Integer> expected = HashMultimap.create();
        expected.put("order_id", 1);
        Assert.assertThat(distinctQueryResult.getColumnLabelAndIndexMap(), CoreMatchers.is(expected));
    }

    @Test
    public void assertGetResultData() {
        Assert.assertThat(distinctQueryResult.getResultData().next().getColumnValue(1), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertGetCurrentRow() {
        distinctQueryResult.next();
        Assert.assertThat(distinctQueryResult.getCurrentRow().getColumnValue(1), CoreMatchers.is(((Object) (10))));
    }

    @Test(expected = SQLException.class)
    @SneakyThrows
    public void assertGetColumnLabelAndIndexMapWithException() {
        QueryResult queryResult = Mockito.mock(QueryResult.class);
        Mockito.when(queryResult.next()).thenReturn(true).thenReturn(false);
        Mockito.when(queryResult.getColumnCount()).thenThrow(SQLException.class);
        Mockito.when(queryResult.getColumnLabel(1)).thenReturn("order_id");
        Mockito.when(queryResult.getValue(1, Object.class)).thenReturn(10);
        Collection<QueryResult> queryResults = new LinkedList<>();
        queryResults.add(queryResult);
        List<String> distinctColumnLabels = Collections.singletonList("order_id");
        distinctQueryResult = new DistinctQueryResult(queryResults, distinctColumnLabels);
    }

    @Test(expected = SQLException.class)
    @SneakyThrows
    public void assertGetResultDataWithException() {
        QueryResult queryResult = Mockito.mock(QueryResult.class);
        Mockito.when(queryResult.next()).thenThrow(SQLException.class);
        Mockito.when(queryResult.getColumnCount()).thenReturn(1);
        Mockito.when(queryResult.getColumnLabel(1)).thenReturn("order_id");
        Mockito.when(queryResult.getValue(1, Object.class)).thenReturn(10);
        Collection<QueryResult> queryResults = new LinkedList<>();
        queryResults.add(queryResult);
        List<String> distinctColumnLabels = Collections.singletonList("order_id");
        distinctQueryResult = new DistinctQueryResult(queryResults, distinctColumnLabels);
    }
}

