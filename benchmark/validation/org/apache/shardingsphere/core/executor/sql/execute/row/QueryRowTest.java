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
package org.apache.shardingsphere.core.executor.sql.execute.row;


import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class QueryRowTest {
    private QueryRow queryRow;

    @Test
    public void assertGetColumnValue() {
        Assert.assertThat(queryRow.getColumnValue(1), CoreMatchers.is(((Object) (10))));
    }

    @Test
    public void assertEqual() {
        QueryRow queryRow1 = new QueryRow(Collections.singletonList(((Object) (10))));
        Assert.assertTrue(queryRow1.equals(queryRow));
    }

    @Test
    public void assertEqualPartly() {
        QueryRow queryRow1 = new QueryRow(Collections.singletonList(((Object) (10))), Collections.singletonList(1));
        QueryRow queryRow2 = new QueryRow(Collections.singletonList(((Object) (8))), Collections.singletonList(1));
        Assert.assertTrue(queryRow.equals(queryRow1));
        Assert.assertFalse(queryRow.equals(queryRow2));
    }

    @Test
    public void assertHashCode() {
        Assert.assertEquals(41, queryRow.hashCode());
    }

    @Test
    public void assertGetRowData() {
        Assert.assertThat(queryRow.getRowData(), CoreMatchers.is(Collections.singletonList(((Object) (10)))));
    }

    @Test
    public void assertGetDistinctColumnIndexes() {
        Assert.assertThat(queryRow.getDistinctColumnIndexes(), CoreMatchers.is(Collections.singletonList(1)));
    }
}

