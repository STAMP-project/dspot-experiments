/**
 * Copyright 2015, The Querydsl Team (http://www.querydsl.com/team)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.querydsl.sql.mysql;


import com.querydsl.core.testutil.MySQL;
import com.querydsl.sql.H2Templates;
import com.querydsl.sql.QGeneratedKeysEntity;
import com.querydsl.sql.dml.SQLInsertClause;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MySQL.class)
public class GeneratedKeysMySQLTest {
    private Connection conn;

    private Statement stmt;

    @Test
    public void test() throws SQLException {
        stmt.execute("drop table if exists GENERATED_KEYS");
        stmt.execute(("create table GENERATED_KEYS(" + ("ID int AUTO_INCREMENT PRIMARY KEY, " + "NAME varchar(30))")));
        QGeneratedKeysEntity entity = new QGeneratedKeysEntity("entity");
        SQLInsertClause insertClause = new SQLInsertClause(conn, new H2Templates(), entity);
        ResultSet rs = insertClause.set(entity.name, "Hello").executeWithKeys();
        ResultSetMetaData md = rs.getMetaData();
        System.out.println(md.getColumnName(1));
        Assert.assertTrue(rs.next());
        Assert.assertEquals(1, rs.getInt(1));
        Assert.assertFalse(rs.next());
        insertClause = new SQLInsertClause(conn, new H2Templates(), entity);
        rs = insertClause.set(entity.name, "World").executeWithKeys();
        Assert.assertTrue(rs.next());
        Assert.assertEquals(2, rs.getInt(1));
        Assert.assertFalse(rs.next());
    }
}

