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
package org.apache.hive.jdbc;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.Date;
import org.apache.hadoop.hive.common.type.Timestamp;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.llap.FieldDesc;
import org.apache.hadoop.hive.llap.Row;
import org.apache.hadoop.hive.llap.Schema;
import org.apache.hive.jdbc.miniHS2.MiniHS2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Specialize this base class for different serde's/formats
 * {@link #beforeTest(boolean) beforeTest} should be called
 * by sub-classes in a {@link org.junit.BeforeClass} initializer
 */
public abstract class BaseJdbcWithMiniLlap {
    private static MiniHS2 miniHS2 = null;

    private static String dataFileDir;

    private static Path kvDataFilePath;

    private static Path dataTypesFilePath;

    private static HiveConf conf = null;

    private static Connection hs2Conn = null;

    @Test(timeout = 60000)
    public void testLlapInputFormatEndToEnd() throws Exception {
        createTestTable("testtab1");
        int rowCount;
        BaseJdbcWithMiniLlap.RowCollector rowCollector = new BaseJdbcWithMiniLlap.RowCollector();
        String query = "select * from testtab1 where under_col = 0";
        rowCount = processQuery(query, 1, rowCollector);
        Assert.assertEquals(3, rowCount);
        Assert.assertArrayEquals(new String[]{ "0", "val_0" }, rowCollector.rows.get(0));
        Assert.assertArrayEquals(new String[]{ "0", "val_0" }, rowCollector.rows.get(1));
        Assert.assertArrayEquals(new String[]{ "0", "val_0" }, rowCollector.rows.get(2));
        // Try empty rows query
        rowCollector.rows.clear();
        query = "select * from testtab1 where true = false";
        rowCount = processQuery(query, 1, rowCollector);
        Assert.assertEquals(0, rowCount);
    }

    @Test(timeout = 60000)
    public void testNonAsciiStrings() throws Exception {
        createTestTable("testtab_nonascii");
        BaseJdbcWithMiniLlap.RowCollector rowCollector = new BaseJdbcWithMiniLlap.RowCollector();
        String nonAscii = "? c?t? du gar?on";
        String query = ("select value, '" + nonAscii) + "' from testtab_nonascii where under_col=0";
        int rowCount = processQuery(query, 1, rowCollector);
        Assert.assertEquals(3, rowCount);
        Assert.assertArrayEquals(new String[]{ "val_0", nonAscii }, rowCollector.rows.get(0));
        Assert.assertArrayEquals(new String[]{ "val_0", nonAscii }, rowCollector.rows.get(1));
        Assert.assertArrayEquals(new String[]{ "val_0", nonAscii }, rowCollector.rows.get(2));
    }

    @Test(timeout = 60000)
    public void testEscapedStrings() throws Exception {
        createTestTable("testtab1");
        BaseJdbcWithMiniLlap.RowCollector rowCollector = new BaseJdbcWithMiniLlap.RowCollector();
        String expectedVal1 = "\'a\',\"b\",\\c\\";
        String expectedVal2 = "multi\nline";
        String query = "select value, \'\\\'a\\\',\"b\",\\\\c\\\\\', \'multi\\nline\' from testtab1 where under_col=0";
        int rowCount = processQuery(query, 1, rowCollector);
        Assert.assertEquals(3, rowCount);
        Assert.assertArrayEquals(new String[]{ "val_0", expectedVal1, expectedVal2 }, rowCollector.rows.get(0));
        Assert.assertArrayEquals(new String[]{ "val_0", expectedVal1, expectedVal2 }, rowCollector.rows.get(1));
        Assert.assertArrayEquals(new String[]{ "val_0", expectedVal1, expectedVal2 }, rowCollector.rows.get(2));
    }

    @Test(timeout = 60000)
    public void testDataTypes() throws Exception {
        createDataTypesTable("datatypes");
        BaseJdbcWithMiniLlap.RowCollector2 rowCollector = new BaseJdbcWithMiniLlap.RowCollector2();
        String query = "select * from datatypes";
        int rowCount = processQuery(query, 1, rowCollector);
        Assert.assertEquals(3, rowCount);
        // Verify schema
        String[][] colNameTypes = new String[][]{ new String[]{ "datatypes.c1", "int" }, new String[]{ "datatypes.c2", "boolean" }, new String[]{ "datatypes.c3", "double" }, new String[]{ "datatypes.c4", "string" }, new String[]{ "datatypes.c5", "array<int>" }, new String[]{ "datatypes.c6", "map<int,string>" }, new String[]{ "datatypes.c7", "map<string,string>" }, new String[]{ "datatypes.c8", "struct<r:string,s:int,t:double>" }, new String[]{ "datatypes.c9", "tinyint" }, new String[]{ "datatypes.c10", "smallint" }, new String[]{ "datatypes.c11", "float" }, new String[]{ "datatypes.c12", "bigint" }, new String[]{ "datatypes.c13", "array<array<string>>" }, new String[]{ "datatypes.c14", "map<int,map<int,int>>" }, new String[]{ "datatypes.c15", "struct<r:int,s:struct<a:int,b:string>>" }, new String[]{ "datatypes.c16", "array<struct<m:map<string,string>,n:int>>" }, new String[]{ "datatypes.c17", "timestamp" }, new String[]{ "datatypes.c18", "decimal(16,7)" }, new String[]{ "datatypes.c19", "binary" }, new String[]{ "datatypes.c20", "date" }, new String[]{ "datatypes.c21", "varchar(20)" }, new String[]{ "datatypes.c22", "char(15)" }, new String[]{ "datatypes.c23", "binary" } };
        FieldDesc fieldDesc;
        Assert.assertEquals(23, rowCollector.numColumns);
        for (int idx = 0; idx < (rowCollector.numColumns); ++idx) {
            fieldDesc = rowCollector.schema.getColumns().get(idx);
            Assert.assertEquals(("ColName idx=" + idx), colNameTypes[idx][0], fieldDesc.getName());
            Assert.assertEquals(("ColType idx=" + idx), colNameTypes[idx][1], fieldDesc.getTypeInfo().getTypeName());
        }
        // First row is all nulls
        Object[] rowValues = rowCollector.rows.get(0);
        for (int idx = 0; idx < (rowCollector.numColumns); ++idx) {
            Assert.assertEquals(("idx=" + idx), null, rowValues[idx]);
        }
        // Second Row
        rowValues = rowCollector.rows.get(1);
        Assert.assertEquals(Integer.valueOf((-1)), rowValues[0]);
        Assert.assertEquals(Boolean.FALSE, rowValues[1]);
        Assert.assertEquals(Double.valueOf((-1.1)), rowValues[2]);
        Assert.assertEquals("", rowValues[3]);
        List<?> c5Value = ((List<?>) (rowValues[4]));
        Assert.assertEquals(0, c5Value.size());
        Map<?, ?> c6Value = ((Map<?, ?>) (rowValues[5]));
        Assert.assertEquals(0, c6Value.size());
        Map<?, ?> c7Value = ((Map<?, ?>) (rowValues[6]));
        Assert.assertEquals(0, c7Value.size());
        List<?> c8Value = ((List<?>) (rowValues[7]));
        Assert.assertEquals(null, c8Value.get(0));
        Assert.assertEquals(null, c8Value.get(1));
        Assert.assertEquals(null, c8Value.get(2));
        Assert.assertEquals(Byte.valueOf(((byte) (-1))), rowValues[8]);
        Assert.assertEquals(Short.valueOf(((short) (-1))), rowValues[9]);
        Assert.assertEquals(Float.valueOf((-1.0F)), rowValues[10]);
        Assert.assertEquals(Long.valueOf((-1L)), rowValues[11]);
        List<?> c13Value = ((List<?>) (rowValues[12]));
        Assert.assertEquals(0, c13Value.size());
        Map<?, ?> c14Value = ((Map<?, ?>) (rowValues[13]));
        Assert.assertEquals(0, c14Value.size());
        List<?> c15Value = ((List<?>) (rowValues[14]));
        Assert.assertEquals(null, c15Value.get(0));
        Assert.assertEquals(null, c15Value.get(1));
        List<?> c16Value = ((List<?>) (rowValues[15]));
        Assert.assertEquals(0, c16Value.size());
        Assert.assertEquals(null, rowValues[16]);
        Assert.assertEquals(null, rowValues[17]);
        Assert.assertEquals(null, rowValues[18]);
        Assert.assertEquals(null, rowValues[19]);
        Assert.assertEquals(null, rowValues[20]);
        Assert.assertEquals(null, rowValues[21]);
        Assert.assertEquals(null, rowValues[22]);
        // Third row
        rowValues = rowCollector.rows.get(2);
        Assert.assertEquals(Integer.valueOf(1), rowValues[0]);
        Assert.assertEquals(Boolean.TRUE, rowValues[1]);
        Assert.assertEquals(Double.valueOf(1.1), rowValues[2]);
        Assert.assertEquals("1", rowValues[3]);
        c5Value = ((List<?>) (rowValues[4]));
        Assert.assertEquals(2, c5Value.size());
        Assert.assertEquals(Integer.valueOf(1), c5Value.get(0));
        Assert.assertEquals(Integer.valueOf(2), c5Value.get(1));
        c6Value = ((Map<?, ?>) (rowValues[5]));
        Assert.assertEquals(2, c6Value.size());
        Assert.assertEquals("x", c6Value.get(Integer.valueOf(1)));
        Assert.assertEquals("y", c6Value.get(Integer.valueOf(2)));
        c7Value = ((Map<?, ?>) (rowValues[6]));
        Assert.assertEquals(1, c7Value.size());
        Assert.assertEquals("v", c7Value.get("k"));
        c8Value = ((List<?>) (rowValues[7]));
        Assert.assertEquals("a", c8Value.get(0));
        Assert.assertEquals(Integer.valueOf(9), c8Value.get(1));
        Assert.assertEquals(Double.valueOf(2.2), c8Value.get(2));
        Assert.assertEquals(Byte.valueOf(((byte) (1))), rowValues[8]);
        Assert.assertEquals(Short.valueOf(((short) (1))), rowValues[9]);
        Assert.assertEquals(Float.valueOf(1.0F), rowValues[10]);
        Assert.assertEquals(Long.valueOf(1L), rowValues[11]);
        c13Value = ((List<?>) (rowValues[12]));
        Assert.assertEquals(2, c13Value.size());
        List<?> listVal = ((List<?>) (c13Value.get(0)));
        Assert.assertEquals("a", listVal.get(0));
        Assert.assertEquals("b", listVal.get(1));
        listVal = ((List<?>) (c13Value.get(1)));
        Assert.assertEquals("c", listVal.get(0));
        Assert.assertEquals("d", listVal.get(1));
        c14Value = ((Map<?, ?>) (rowValues[13]));
        Assert.assertEquals(2, c14Value.size());
        Map<?, ?> mapVal = ((Map<?, ?>) (c14Value.get(Integer.valueOf(1))));
        Assert.assertEquals(2, mapVal.size());
        Assert.assertEquals(Integer.valueOf(12), mapVal.get(Integer.valueOf(11)));
        Assert.assertEquals(Integer.valueOf(14), mapVal.get(Integer.valueOf(13)));
        mapVal = ((Map<?, ?>) (c14Value.get(Integer.valueOf(2))));
        Assert.assertEquals(1, mapVal.size());
        Assert.assertEquals(Integer.valueOf(22), mapVal.get(Integer.valueOf(21)));
        c15Value = ((List<?>) (rowValues[14]));
        Assert.assertEquals(Integer.valueOf(1), c15Value.get(0));
        listVal = ((List<?>) (c15Value.get(1)));
        Assert.assertEquals(2, listVal.size());
        Assert.assertEquals(Integer.valueOf(2), listVal.get(0));
        Assert.assertEquals("x", listVal.get(1));
        c16Value = ((List<?>) (rowValues[15]));
        Assert.assertEquals(2, c16Value.size());
        listVal = ((List<?>) (c16Value.get(0)));
        Assert.assertEquals(2, listVal.size());
        mapVal = ((Map<?, ?>) (listVal.get(0)));
        Assert.assertEquals(0, mapVal.size());
        Assert.assertEquals(Integer.valueOf(1), listVal.get(1));
        listVal = ((List<?>) (c16Value.get(1)));
        mapVal = ((Map<?, ?>) (listVal.get(0)));
        Assert.assertEquals(2, mapVal.size());
        Assert.assertEquals("b", mapVal.get("a"));
        Assert.assertEquals("d", mapVal.get("c"));
        Assert.assertEquals(Integer.valueOf(2), listVal.get(1));
        Assert.assertEquals(Timestamp.valueOf("2012-04-22 09:00:00.123456789"), rowValues[16]);
        Assert.assertEquals(new BigDecimal("123456789.123456"), rowValues[17]);
        Assert.assertArrayEquals("abcd".getBytes("UTF-8"), ((byte[]) (rowValues[18])));
        Assert.assertEquals(Date.valueOf("2013-01-01"), rowValues[19]);
        Assert.assertEquals("abc123", rowValues[20]);
        Assert.assertEquals("abc123         ", rowValues[21]);
        Assert.assertArrayEquals("X'01FF'".getBytes("UTF-8"), ((byte[]) (rowValues[22])));
    }

    @Test(timeout = 60000)
    public void testComplexQuery() throws Exception {
        createTestTable("testtab1");
        BaseJdbcWithMiniLlap.RowCollector rowCollector = new BaseJdbcWithMiniLlap.RowCollector();
        String query = "select value, count(*) from testtab1 where under_col=0 group by value";
        int rowCount = processQuery(query, 1, rowCollector);
        Assert.assertEquals(1, rowCount);
        Assert.assertArrayEquals(new String[]{ "val_0", "3" }, rowCollector.rows.get(0));
    }

    private interface RowProcessor {
        void process(Row row);
    }

    protected static class RowCollector implements BaseJdbcWithMiniLlap.RowProcessor {
        ArrayList<String[]> rows = new ArrayList<String[]>();

        Schema schema = null;

        int numColumns = 0;

        public void process(Row row) {
            if ((schema) == null) {
                schema = row.getSchema();
                numColumns = schema.getColumns().size();
            }
            String[] arr = new String[numColumns];
            for (int idx = 0; idx < (numColumns); ++idx) {
                Object val = row.getValue(idx);
                arr[idx] = (val == null) ? null : val.toString();
            }
            rows.add(arr);
        }
    }

    // Save the actual values from each row as opposed to the String representation.
    protected static class RowCollector2 implements BaseJdbcWithMiniLlap.RowProcessor {
        ArrayList<Object[]> rows = new ArrayList<Object[]>();

        Schema schema = null;

        int numColumns = 0;

        public void process(Row row) {
            if ((schema) == null) {
                schema = row.getSchema();
                numColumns = schema.getColumns().size();
            }
            Object[] arr = new Object[numColumns];
            for (int idx = 0; idx < (numColumns); ++idx) {
                arr[idx] = row.getValue(idx);
            }
            rows.add(arr);
        }
    }

    /**
     * Test CLI kill command of a query that is running.
     * We spawn 2 threads - one running the query and
     * the other attempting to cancel.
     * We're using a dummy udf to simulate a query,
     * that runs for a sufficiently long time.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testKillQuery() throws Exception {
        String tableName = "testtab1";
        createTestTable(tableName);
        Connection con = BaseJdbcWithMiniLlap.hs2Conn;
        Connection con2 = BaseJdbcWithMiniLlap.getConnection(BaseJdbcWithMiniLlap.miniHS2.getJdbcURL(), System.getProperty("user.name"), "bar");
        String udfName = TestJdbcWithMiniHS2.SleepMsUDF.class.getName();
        Statement stmt1 = con.createStatement();
        Statement stmt2 = con2.createStatement();
        stmt1.execute((("create temporary function sleepMsUDF as '" + udfName) + "'"));
        stmt1.close();
        final Statement stmt = con.createStatement();
        BaseJdbcWithMiniLlap.ExceptionHolder tExecuteHolder = new BaseJdbcWithMiniLlap.ExceptionHolder();
        BaseJdbcWithMiniLlap.ExceptionHolder tKillHolder = new BaseJdbcWithMiniLlap.ExceptionHolder();
        // Thread executing the query
        Thread tExecute = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Executing query: ");
                    // The test table has 500 rows, so total query time should be ~ 500*500ms
                    stmt.executeQuery(((((("select sleepMsUDF(t1.under_col, 100), t1.under_col, t2.under_col " + "from ") + tableName) + " t1 join ") + tableName) + " t2 on t1.under_col = t2.under_col"));
                    Assert.fail("Expecting SQLException");
                } catch (SQLException e) {
                    tExecuteHolder.throwable = e;
                }
            }
        });
        // Thread killing the query
        Thread tKill = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    String queryId = getQueryId();
                    System.out.println(("Killing query: " + queryId));
                    stmt2.execute((("kill query '" + queryId) + "'"));
                    stmt2.close();
                } catch (Exception e) {
                    tKillHolder.throwable = e;
                }
            }
        });
        tExecute.start();
        tKill.start();
        tExecute.join();
        tKill.join();
        stmt.close();
        con2.close();
        Assert.assertNotNull("tExecute", tExecuteHolder.throwable);
        Assert.assertNull("tCancel", tKillHolder.throwable);
    }

    private static class ExceptionHolder {
        Throwable throwable;
    }
}

