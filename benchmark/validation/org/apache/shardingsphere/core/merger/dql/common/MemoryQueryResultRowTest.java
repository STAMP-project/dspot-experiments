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
package org.apache.shardingsphere.core.merger.dql.common;


import java.sql.ResultSet;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MemoryQueryResultRowTest {
    @Mock
    private ResultSet resultSet;

    private MemoryQueryResultRow memoryResultSetRow;

    @Test
    public void assertGetCell() {
        Assert.assertThat(memoryResultSetRow.getCell(1).toString(), CoreMatchers.is("value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertGetCellWithNegativeColumnIndex() {
        memoryResultSetRow.getCell((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertGetCellWithColumnIndexOutOfRange() {
        memoryResultSetRow.getCell(2);
    }

    @Test
    public void assertSetCell() {
        memoryResultSetRow.setCell(1, "new");
        Assert.assertThat(memoryResultSetRow.getCell(1).toString(), CoreMatchers.is("new"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertSetCellWithNegativeColumnIndex() {
        memoryResultSetRow.setCell((-1), "new");
    }

    @Test(expected = IllegalArgumentException.class)
    public void assertSetCellWithColumnIndexOutOfRange() {
        memoryResultSetRow.setCell(2, "new");
    }
}

