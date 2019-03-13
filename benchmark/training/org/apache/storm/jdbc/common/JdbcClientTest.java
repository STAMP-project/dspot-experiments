/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.  The ASF licenses this file to you under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package org.apache.storm.jdbc.common;


import com.google.common.collect.Lists;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runners.model.MultipleFailureException;


public class JdbcClientTest {
    private static final String tableName = "user_details";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private JdbcClient client;

    @Test
    public void testInsertAndSelect() {
        List<Column> row1 = createRow(1, "bob");
        List<Column> row2 = createRow(2, "alice");
        List<List<Column>> rows = Lists.newArrayList(row1, row2);
        client.insert(JdbcClientTest.tableName, rows);
        List<List<Column>> selectedRows = client.select("select * from user_details where id = ?", Lists.newArrayList(new Column("id", 1, Types.INTEGER)));
        List<List<Column>> expectedRows = Lists.newArrayList();
        expectedRows.add(row1);
        Assert.assertEquals(expectedRows, selectedRows);
        List<Column> row3 = createRow(3, "frank");
        List<List<Column>> moreRows = new ArrayList<List<Column>>();
        moreRows.add(row3);
        client.executeInsertQuery("insert into user_details values(?,?,?)", moreRows);
        selectedRows = client.select("select * from user_details where id = ?", Lists.newArrayList(new Column("id", 3, Types.INTEGER)));
        expectedRows = Lists.newArrayList();
        expectedRows.add(row3);
        Assert.assertEquals(expectedRows, selectedRows);
        selectedRows = client.select("select * from user_details order by id", Lists.<Column>newArrayList());
        rows.add(row3);
        Assert.assertEquals(rows, selectedRows);
    }

    @Test
    public void testInsertConnectionError() {
        ConnectionProvider connectionProvider = new ThrowingConnectionProvider(null);
        this.client = new JdbcClient(connectionProvider, 60);
        List<Column> row = createRow(1, "frank");
        List<List<Column>> rows = new ArrayList<List<Column>>();
        rows.add(row);
        String query = "insert into user_details values(?,?,?)";
        thrown.expect(MultipleFailureException.class);
        client.executeInsertQuery(query, rows);
    }
}

