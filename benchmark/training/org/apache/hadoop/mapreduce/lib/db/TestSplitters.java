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


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test Splitters. Splitters should build parts of sql sentences for split result.
 */
public class TestSplitters {
    private Configuration configuration;

    @Test(timeout = 2000)
    public void testBooleanSplitter() throws Exception {
        BooleanSplitter splitter = new BooleanSplitter();
        ResultSet result = Mockito.mock(ResultSet.class);
        Mockito.when(result.getString(1)).thenReturn("result1");
        List<InputSplit> splits = splitter.split(configuration, result, "column");
        assertSplits(new String[]{ "column = FALSE column = FALSE", "column IS NULL column IS NULL" }, splits);
        Mockito.when(result.getString(1)).thenReturn("result1");
        Mockito.when(result.getString(2)).thenReturn("result2");
        Mockito.when(result.getBoolean(1)).thenReturn(true);
        Mockito.when(result.getBoolean(2)).thenReturn(false);
        splits = splitter.split(configuration, result, "column");
        Assert.assertEquals(0, splits.size());
        Mockito.when(result.getString(1)).thenReturn("result1");
        Mockito.when(result.getString(2)).thenReturn("result2");
        Mockito.when(result.getBoolean(1)).thenReturn(false);
        Mockito.when(result.getBoolean(2)).thenReturn(true);
        splits = splitter.split(configuration, result, "column");
        assertSplits(new String[]{ "column = FALSE column = FALSE", ".*column = TRUE" }, splits);
    }

    @Test(timeout = 2000)
    public void testFloatSplitter() throws Exception {
        FloatSplitter splitter = new FloatSplitter();
        ResultSet results = Mockito.mock(ResultSet.class);
        List<InputSplit> splits = splitter.split(configuration, results, "column");
        assertSplits(new String[]{ ".*column IS NULL" }, splits);
        Mockito.when(results.getString(1)).thenReturn("result1");
        Mockito.when(results.getString(2)).thenReturn("result2");
        Mockito.when(results.getDouble(1)).thenReturn(5.0);
        Mockito.when(results.getDouble(2)).thenReturn(7.0);
        splits = splitter.split(configuration, results, "column1");
        assertSplits(new String[]{ "column1 >= 5.0 column1 < 6.0", "column1 >= 6.0 column1 <= 7.0" }, splits);
    }

    @Test(timeout = 2000)
    public void testBigDecimalSplitter() throws Exception {
        BigDecimalSplitter splitter = new BigDecimalSplitter();
        ResultSet result = Mockito.mock(ResultSet.class);
        List<InputSplit> splits = splitter.split(configuration, result, "column");
        assertSplits(new String[]{ ".*column IS NULL" }, splits);
        Mockito.when(result.getString(1)).thenReturn("result1");
        Mockito.when(result.getString(2)).thenReturn("result2");
        Mockito.when(result.getBigDecimal(1)).thenReturn(new BigDecimal(10));
        Mockito.when(result.getBigDecimal(2)).thenReturn(new BigDecimal(12));
        splits = splitter.split(configuration, result, "column1");
        assertSplits(new String[]{ "column1 >= 10 column1 < 11", "column1 >= 11 column1 <= 12" }, splits);
    }

    @Test(timeout = 2000)
    public void testIntegerSplitter() throws Exception {
        IntegerSplitter splitter = new IntegerSplitter();
        ResultSet result = Mockito.mock(ResultSet.class);
        List<InputSplit> splits = splitter.split(configuration, result, "column");
        assertSplits(new String[]{ ".*column IS NULL" }, splits);
        Mockito.when(result.getString(1)).thenReturn("result1");
        Mockito.when(result.getString(2)).thenReturn("result2");
        Mockito.when(result.getLong(1)).thenReturn(8L);
        Mockito.when(result.getLong(2)).thenReturn(19L);
        splits = splitter.split(configuration, result, "column1");
        assertSplits(new String[]{ "column1 >= 8 column1 < 13", "column1 >= 13 column1 < 18", "column1 >= 18 column1 <= 19" }, splits);
    }

    @Test(timeout = 2000)
    public void testTextSplitter() throws Exception {
        TextSplitter splitter = new TextSplitter();
        ResultSet result = Mockito.mock(ResultSet.class);
        List<InputSplit> splits = splitter.split(configuration, result, "column");
        assertSplits(new String[]{ "column IS NULL column IS NULL" }, splits);
        Mockito.when(result.getString(1)).thenReturn("result1");
        Mockito.when(result.getString(2)).thenReturn("result2");
        splits = splitter.split(configuration, result, "column1");
        assertSplits(new String[]{ "column1 >= 'result1' column1 < 'result1.'", "column1 >= 'result1' column1 <= 'result2'" }, splits);
    }
}

