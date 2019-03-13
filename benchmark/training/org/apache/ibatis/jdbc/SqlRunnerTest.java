/**
 * Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.jdbc;


import Null.LONGVARCHAR;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.BaseDataTest;
import org.junit.Assert;
import org.junit.Test;

import static SqlRunner.NO_GENERATED_KEY;


public class SqlRunnerTest extends BaseDataTest {
    @Test
    public void shouldSelectOne() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DDL);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DATA);
        Connection connection = ds.getConnection();
        SqlRunner exec = new SqlRunner(connection);
        Map<String, Object> row = exec.selectOne("SELECT * FROM PRODUCT WHERE PRODUCTID = ?", "FI-SW-01");
        connection.close();
        Assert.assertEquals("FI-SW-01", row.get("PRODUCTID"));
    }

    @Test
    public void shouldSelectList() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DDL);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DATA);
        Connection connection = ds.getConnection();
        SqlRunner exec = new SqlRunner(connection);
        List<Map<String, Object>> rows = exec.selectAll("SELECT * FROM PRODUCT");
        connection.close();
        Assert.assertEquals(16, rows.size());
    }

    @Test
    public void shouldInsert() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.BLOG_PROPERTIES);
        BaseDataTest.runScript(ds, BaseDataTest.BLOG_DDL);
        Connection connection = ds.getConnection();
        SqlRunner exec = new SqlRunner(connection);
        exec.setUseGeneratedKeySupport(true);
        int id = exec.insert("INSERT INTO author (username, password, email, bio) VALUES (?,?,?,?)", "someone", "******", "someone@apache.org", LONGVARCHAR);
        Map<String, Object> row = exec.selectOne("SELECT * FROM author WHERE username = ?", "someone");
        connection.rollback();
        connection.close();
        Assert.assertTrue(((NO_GENERATED_KEY) != id));
        Assert.assertEquals("someone", row.get("USERNAME"));
    }

    @Test
    public void shouldUpdateCategory() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DDL);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DATA);
        Connection connection = ds.getConnection();
        SqlRunner exec = new SqlRunner(connection);
        int count = exec.update("update product set category = ? where productid = ?", "DOGS", "FI-SW-01");
        Map<String, Object> row = exec.selectOne("SELECT * FROM PRODUCT WHERE PRODUCTID = ?", "FI-SW-01");
        connection.close();
        Assert.assertEquals("DOGS", row.get("CATEGORY"));
        Assert.assertEquals(1, count);
    }

    @Test
    public void shouldDeleteOne() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DDL);
        BaseDataTest.runScript(ds, BaseDataTest.JPETSTORE_DATA);
        Connection connection = ds.getConnection();
        SqlRunner exec = new SqlRunner(connection);
        int count = exec.delete("delete from item");
        List<Map<String, Object>> rows = exec.selectAll("SELECT * FROM ITEM");
        connection.close();
        Assert.assertEquals(28, count);
        Assert.assertEquals(0, rows.size());
    }

    @Test
    public void shouldDemonstrateDDLThroughRunMethod() throws Exception {
        DataSource ds = BaseDataTest.createUnpooledDataSource(BaseDataTest.JPETSTORE_PROPERTIES);
        Connection connection = ds.getConnection();
        SqlRunner exec = new SqlRunner(connection);
        exec.run("CREATE TABLE BLAH(ID INTEGER)");
        exec.run("insert into BLAH values (1)");
        List<Map<String, Object>> rows = exec.selectAll("SELECT * FROM BLAH");
        exec.run("DROP TABLE BLAH");
        connection.close();
        Assert.assertEquals(1, rows.size());
    }
}

