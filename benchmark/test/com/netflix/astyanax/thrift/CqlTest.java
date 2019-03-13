package com.netflix.astyanax.thrift;


import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.OperationResult;
import com.netflix.astyanax.cql.CqlSchema;
import com.netflix.astyanax.cql.CqlStatementResult;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.model.Row;
import com.netflix.astyanax.model.Rows;
import com.netflix.astyanax.serializers.IntegerSerializer;
import com.netflix.astyanax.serializers.MapSerializer;
import com.netflix.astyanax.serializers.StringSerializer;
import com.netflix.astyanax.serializers.UUIDSerializer;
import com.netflix.astyanax.shaded.org.apache.cassandra.db.marshal.UTF8Type;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import junit.framework.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CqlTest {
    private static Logger Log = LoggerFactory.getLogger(CqlTest.class);

    private static Keyspace keyspace;

    private static AstyanaxContext<Keyspace> keyspaceContext;

    private static String TEST_CLUSTER_NAME = "cass_sandbox";

    private static String TEST_KEYSPACE_NAME = "CqlTest";

    private static final String SEEDS = "localhost:9160";

    private static final long CASSANDRA_WAIT_TIME = 1000;

    static ColumnFamily<Integer, String> CQL3_CF = ColumnFamily.newColumnFamily("Cql3CF", IntegerSerializer.get(), StringSerializer.get());

    static ColumnFamily<String, String> User_CF = ColumnFamily.newColumnFamily("UserCF", StringSerializer.get(), StringSerializer.get());

    static ColumnFamily<UUID, String> UUID_CF = ColumnFamily.newColumnFamily("uuidtest", UUIDSerializer.get(), StringSerializer.get());

    @Test
    public void testCompoundKey() throws Exception {
        OperationResult<CqlStatementResult> result;
        result = CqlTest.keyspace.prepareCqlStatement().withCql("INSERT INTO employees (empID, deptID, first_name, last_name) VALUES (111, 222, 'eran', 'landau');").execute();
        result = CqlTest.keyspace.prepareCqlStatement().withCql("INSERT INTO employees (empID, deptID, first_name, last_name) VALUES (111, 233, 'netta', 'landau');").execute();
        result = CqlTest.keyspace.prepareCqlStatement().withCql("SELECT * FROM employees WHERE empId=111;").execute();
        Assert.assertTrue((!(result.getResult().getRows(CqlTest.CQL3_CF).isEmpty())));
        for (Row<Integer, String> row : result.getResult().getRows(CqlTest.CQL3_CF)) {
            CqlTest.Log.info(("CQL Key: " + (row.getKey())));
            ColumnList<String> columns = row.getColumns();
            CqlTest.Log.info(("   empid      : " + (columns.getIntegerValue("empid", null))));
            CqlTest.Log.info(("   deptid     : " + (columns.getIntegerValue("deptid", null))));
            CqlTest.Log.info(("   first_name : " + (columns.getStringValue("first_name", null))));
            CqlTest.Log.info(("   last_name  : " + (columns.getStringValue("last_name", null))));
        }
    }

    @Test
    public void testKeyspaceCql() throws Exception {
        CqlTest.keyspace.prepareQuery(CqlTest.CQL3_CF).withCql("INSERT INTO employees (empID, deptID, first_name, last_name) VALUES (999, 233, 'arielle', 'landau');").execute();
        CqlStatementResult result = CqlTest.keyspace.prepareCqlStatement().withCql("SELECT * FROM employees WHERE empID=999;").execute().getResult();
        CqlSchema schema = result.getSchema();
        Rows<Integer, String> rows = result.getRows(CqlTest.CQL3_CF);
        Assert.assertEquals(1, rows.size());
        // Assert.assertTrue(999 == rows.getRowByIndex(0).getKey());
    }

    @Test
    public void testCollections() throws Exception {
        OperationResult<CqlStatementResult> result;
        result = CqlTest.keyspace.prepareCqlStatement().withCql("INSERT INTO users (id, given, surname, favs) VALUES ('jsmith', 'John', 'Smith', { 'fruit' : 'apple', 'band' : 'Beatles' })").execute();
        Rows<String, String> rows = CqlTest.keyspace.prepareCqlStatement().withCql("SELECT * FROM users;").execute().getResult().getRows(CqlTest.User_CF);
        MapSerializer<String, String> mapSerializer = new MapSerializer<String, String>(UTF8Type.instance, UTF8Type.instance);
        for (Row<String, String> row : rows) {
            CqlTest.Log.info(row.getKey());
            for (Column<String> column : row.getColumns()) {
                CqlTest.Log.info(("  " + (column.getName())));
            }
            Column<String> favs = row.getColumns().getColumnByName("favs");
            Map<String, String> map = favs.getValue(mapSerializer);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                CqlTest.Log.info((((" fav: " + (entry.getKey())) + " = ") + (entry.getValue())));
            }
        }
    }

    @Test
    public void testUUIDPart() throws Exception {
        CqlStatementResult result;
        CqlTest.keyspace.prepareCqlStatement().withCql("CREATE TABLE uuidtest (id UUID PRIMARY KEY, given text, surname text);").execute();
        CqlTest.keyspace.prepareCqlStatement().withCql("INSERT INTO uuidtest (id, given, surname) VALUES (00000000-0000-0000-0000-000000000000, 'x', 'arielle');").execute();
        result = CqlTest.keyspace.prepareCqlStatement().withCql("SELECT given,surname FROM uuidtest ;").execute().getResult();
        Rows<UUID, String> rows = result.getRows(CqlTest.UUID_CF);
        Iterator<Row<UUID, String>> iter = rows.iterator();
        while (iter.hasNext()) {
            Row<UUID, String> row = iter.next();
            ColumnList<String> cols = row.getColumns();
            Iterator<Column<String>> colIter = cols.iterator();
            while (colIter.hasNext()) {
                Column<String> col = colIter.next();
                String name = col.getName();
                CqlTest.Log.info("*************************************");
                if (name.equals("given")) {
                    String val = col.getValue(StringSerializer.get());
                    CqlTest.Log.info(((("columnname=  " + name) + "  columnvalue= ") + val));
                    Assert.assertEquals("x", val);
                }
                if (name.equals("surname")) {
                    String val = col.getValue(StringSerializer.get());
                    CqlTest.Log.info(((("columnname=  " + name) + "  columnvalue= ") + val));
                    Assert.assertEquals("arielle", val);
                }
            } 
            CqlTest.Log.info("*************************************");
        } 
        Assert.assertEquals(1, rows.size());
    }

    @Test
    public void testUUID() throws Exception {
        CqlTest.keyspace.prepareCqlStatement().withCql("CREATE TABLE uuidtest1 (id UUID PRIMARY KEY, given text, surname text);").execute();
        CqlTest.keyspace.prepareCqlStatement().withCql("INSERT INTO uuidtest1 (id, given, surname) VALUES (00000000-0000-0000-0000-000000000000, 'x', 'arielle');").execute();
        CqlStatementResult result = CqlTest.keyspace.prepareCqlStatement().withCql("SELECT * FROM uuidtest1 ;").execute().getResult();
        Rows<UUID, String> rows = result.getRows(CqlTest.UUID_CF);
        Iterator<Row<UUID, String>> iter = rows.iterator();
        while (iter.hasNext()) {
            Row<UUID, String> row = iter.next();
            ColumnList<String> cols = row.getColumns();
            Iterator<Column<String>> colIter = cols.iterator();
            while (colIter.hasNext()) {
                Column<String> col = colIter.next();
                String name = col.getName();
                CqlTest.Log.info("*************************************");
                if (name.equals("id")) {
                    UUID val = col.getValue(UUIDSerializer.get());
                    CqlTest.Log.info(((("columnname=  " + name) + "  columnvalue= ") + val));
                    Assert.assertEquals("00000000-0000-0000-0000-000000000000", val.toString());
                }
                if (name.equals("given")) {
                    String val = col.getValue(StringSerializer.get());
                    CqlTest.Log.info(((("columnname=  " + name) + "  columnvalue= ") + (val.toString())));
                    Assert.assertEquals("x", val);
                }
                if (name.equals("surname")) {
                    String val = col.getValue(StringSerializer.get());
                    CqlTest.Log.info(((("columnname=  " + name) + "  columnvalue= ") + val));
                    Assert.assertEquals("arielle", val);
                }
            } 
            CqlTest.Log.info("*************************************");
        } 
        Assert.assertEquals(1, rows.size());
    }
}

