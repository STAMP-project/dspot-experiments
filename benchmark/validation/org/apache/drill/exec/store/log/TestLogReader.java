/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.store.log;


import MinorType.INT;
import MinorType.VARCHAR;
import org.apache.drill.exec.record.BatchSchema;
import org.apache.drill.exec.record.metadata.SchemaBuilder;
import org.apache.drill.exec.rpc.RpcException;
import org.apache.drill.test.BaseDirTestWatcher;
import org.apache.drill.test.ClusterTest;
import org.apache.drill.test.rowSet.RowSet;
import org.apache.drill.test.rowSet.RowSetUtilities;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class TestLogReader extends ClusterTest {
    public static final String DATE_ONLY_PATTERN = "(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d) .*";

    @ClassRule
    public static final BaseDirTestWatcher dirTestWatcher = new BaseDirTestWatcher();

    @Test
    public void testWildcard() throws RpcException {
        String sql = "SELECT * FROM cp.`regex/simple.log1`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("year", INT).addNullable("month", INT).addNullable("day", INT).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow(2017, 12, 17).addRow(2017, 12, 18).addRow(2017, 12, 19).build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testExplicit() throws RpcException {
        String sql = "SELECT `day`, `month` FROM cp.`regex/simple.log1`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("day", INT).addNullable("month", INT).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow(17, 12).addRow(18, 12).addRow(19, 12).build();
        // results.print();
        // expected.print();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testMissing() throws RpcException {
        String sql = "SELECT `day`, `missing`, `month` FROM cp.`regex/simple.log1`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("day", INT).addNullable("missing", VARCHAR).addNullable("month", INT).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow(17, null, 12).addRow(18, null, 12).addRow(19, null, 12).build();
        // results.print();
        // expected.print();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testRaw() throws RpcException {
        String sql = "SELECT `_raw` FROM cp.`regex/simple.log1`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("_raw", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("2017-12-17 10:52:41,820 [main] INFO  o.a.d.e.e.f.FunctionImplementationRegistry - Function registry loaded.  459 functions loaded in 1396 ms.").addRow("2017-12-18 10:52:37,652 [main] INFO  o.a.drill.common.config.DrillConfig - Configuration and plugin file(s) identified in 115ms.").addRow("2017-12-19 11:12:27,278 [main] ERROR o.apache.drill.exec.server.Drillbit - Failure during initial startup of Drillbit.").build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testDate() throws RpcException {
        String sql = "SELECT TYPEOF(`entry_date`) AS entry_date FROM cp.`regex/simple.log2` LIMIT 1";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().add("entry_date", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("TIMESTAMP").build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testCount() throws RpcException {
        String sql = "SELECT COUNT(*) FROM cp.`regex/simple.log1`";
        long result = ClusterTest.client.queryBuilder().sql(sql).singletonLong();
        Assert.assertEquals(3, result);
    }

    @Test
    public void testFull() throws RpcException {
        String sql = "SELECT * FROM cp.`regex/simple.log1`";
        ClusterTest.client.queryBuilder().sql(sql).printCsv();
    }

    // This section tests log queries without a defined schema
    @Test
    public void testStarQueryNoSchema() throws RpcException {
        String sql = "SELECT * FROM cp.`regex/mysql.sqllog`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("field_0", VARCHAR).addNullable("field_1", VARCHAR).addNullable("field_2", VARCHAR).addNullable("field_3", VARCHAR).addNullable("field_4", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("070823", "21:00:32", "1", "Connect", "root@localhost on test1").addRow("070823", "21:00:48", "1", "Query", "show tables").addRow("070823", "21:00:56", "1", "Query", "select * from category").addRow("070917", "16:29:01", "21", "Query", "select * from location").addRow("070917", "16:29:12", "21", "Query", "select * from location where id = 1 LIMIT 1").build();
        // results.print();
        // expected.print();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testAllFieldsQueryNoSchema() throws RpcException {
        String sql = "SELECT field_0, field_1, field_2, field_3, field_4 FROM cp.`regex/mysql.sqllog`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("field_0", VARCHAR).addNullable("field_1", VARCHAR).addNullable("field_2", VARCHAR).addNullable("field_3", VARCHAR).addNullable("field_4", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("070823", "21:00:32", "1", "Connect", "root@localhost on test1").addRow("070823", "21:00:48", "1", "Query", "show tables").addRow("070823", "21:00:56", "1", "Query", "select * from category").addRow("070917", "16:29:01", "21", "Query", "select * from location").addRow("070917", "16:29:12", "21", "Query", "select * from location where id = 1 LIMIT 1").build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testSomeFieldsQueryNoSchema() throws RpcException {
        String sql = "SELECT field_0, field_4 FROM cp.`regex/mysql.sqllog`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("field_0", VARCHAR).addNullable("field_4", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("070823", "root@localhost on test1").addRow("070823", "show tables").addRow("070823", "select * from category").addRow("070917", "select * from location").addRow("070917", "select * from location where id = 1 LIMIT 1").build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testRawNoSchema() throws RpcException {
        String sql = "SELECT _raw FROM cp.`regex/mysql.sqllog`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("_raw", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("070823 21:00:32       1 Connect     root@localhost on test1").addRow("070823 21:00:48       1 Query       show tables").addRow("070823 21:00:56       1 Query       select * from category").addRow("070917 16:29:01      21 Query       select * from location").addRow("070917 16:29:12      21 Query       select * from location where id = 1 LIMIT 1").build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testUMNoSchema() throws RpcException {
        String sql = "SELECT _unmatched_rows FROM cp.`regex/mysql.sqllog`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("_unmatched_rows", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("dfadkfjaldkjafsdfjlksdjflksjdlkfjsldkfjslkjl").build();
        RowSetUtilities.verify(expected, results);
    }

    @Test
    public void testRawUMNoSchema() throws RpcException {
        String sql = "SELECT _raw, _unmatched_rows FROM cp.`regex/mysql.sqllog`";
        RowSet results = ClusterTest.client.queryBuilder().sql(sql).rowSet();
        BatchSchema expectedSchema = new SchemaBuilder().addNullable("_raw", VARCHAR).addNullable("_unmatched_rows", VARCHAR).build();
        RowSet expected = ClusterTest.client.rowSetBuilder(expectedSchema).addRow("070823 21:00:32       1 Connect     root@localhost on test1", null).addRow("070823 21:00:48       1 Query       show tables", null).addRow("070823 21:00:56       1 Query       select * from category", null).addRow("070917 16:29:01      21 Query       select * from location", null).addRow("070917 16:29:12      21 Query       select * from location where id = 1 LIMIT 1", null).addRow(null, "dfadkfjaldkjafsdfjlksdjflksjdlkfjsldkfjslkjl").build();
        RowSetUtilities.verify(expected, results);
    }
}

