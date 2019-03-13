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
package org.apache.nifi.processors.standard.util;


import JdbcCommon.AvroConversionOptions;
import Schema.Type.INT;
import Schema.Type.LONG;
import Schema.Type.NULL;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.util.Utf8;
import org.apache.commons.io.input.ReaderInputStream;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestJdbcCommon {
    static final String createTable = "create table restaurants(id integer, name varchar(20), city varchar(50))";

    static final String dropTable = "drop table restaurants";

    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    /**
     * Setting up Connection is expensive operation.
     * So let's do this only once and reuse Connection in each test.
     */
    protected static Connection con;

    @Test
    public void testCreateSchema() throws ClassNotFoundException, SQLException {
        final Statement st = TestJdbcCommon.con.createStatement();
        st.executeUpdate("insert into restaurants values (1, 'Irifunes', 'San Mateo')");
        st.executeUpdate("insert into restaurants values (2, 'Estradas', 'Daly City')");
        st.executeUpdate("insert into restaurants values (3, 'Prime Rib House', 'San Francisco')");
        final ResultSet resultSet = st.executeQuery("select * from restaurants");
        final Schema schema = JdbcCommon.createSchema(resultSet);
        Assert.assertNotNull(schema);
        // records name, should be result set first column table name
        // Notice! sql select may join data from different tables, other columns
        // may have different table names
        Assert.assertEquals("RESTAURANTS", schema.getName());
        Assert.assertNotNull(schema.getField("ID"));
        Assert.assertNotNull(schema.getField("NAME"));
        Assert.assertNotNull(schema.getField("CITY"));
        st.close();
        // con.close();
    }

    @Test
    public void testCreateSchemaNoColumns() throws ClassNotFoundException, SQLException {
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        final ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(0);
        Mockito.when(resultSetMetaData.getTableName(1)).thenThrow(SQLException.class);
        final Schema schema = JdbcCommon.createSchema(resultSet);
        Assert.assertNotNull(schema);
        // records name, should be result set first column table name
        // Notice! sql select may join data from different tables, other columns
        // may have different table names
        Assert.assertEquals("NiFi_ExecuteSQL_Record", schema.getName());
        Assert.assertNull(schema.getField("ID"));
    }

    @Test
    public void testCreateSchemaNoTableName() throws ClassNotFoundException, SQLException {
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        final ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(1);
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("");
        Mockito.when(resultSetMetaData.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("ID");
        final Schema schema = JdbcCommon.createSchema(resultSet);
        Assert.assertNotNull(schema);
        // records name, should be result set first column table name
        Assert.assertEquals("NiFi_ExecuteSQL_Record", schema.getName());
    }

    @Test
    public void testCreateSchemaOnlyColumnLabel() throws ClassNotFoundException, SQLException {
        final ResultSet resultSet = Mockito.mock(ResultSet.class);
        final ResultSetMetaData resultSetMetaData = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(2);
        Mockito.when(resultSetMetaData.getTableName(1)).thenReturn("TEST");
        Mockito.when(resultSetMetaData.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(resultSetMetaData.getColumnName(1)).thenReturn("");
        Mockito.when(resultSetMetaData.getColumnLabel(1)).thenReturn("ID");
        Mockito.when(resultSetMetaData.getColumnType(2)).thenReturn(Types.VARCHAR);
        Mockito.when(resultSetMetaData.getColumnName(2)).thenReturn("VCHARC");
        Mockito.when(resultSetMetaData.getColumnLabel(2)).thenReturn("NOT_VCHARC");
        final Schema schema = JdbcCommon.createSchema(resultSet);
        Assert.assertNotNull(schema);
        Assert.assertNotNull(schema.getField("ID"));
        Assert.assertNotNull(schema.getField("NOT_VCHARC"));
        // records name, should be result set first column table name
        Assert.assertEquals("TEST", schema.getName());
    }

    @Test
    public void testConvertToBytes() throws IOException, ClassNotFoundException, SQLException {
        final Statement st = TestJdbcCommon.con.createStatement();
        st.executeUpdate("insert into restaurants values (1, 'Irifunes', 'San Mateo')");
        st.executeUpdate("insert into restaurants values (2, 'Estradas', 'Daly City')");
        st.executeUpdate("insert into restaurants values (3, 'Prime Rib House', 'San Francisco')");
        final ResultSet resultSet = st.executeQuery("select R.*, ROW_NUMBER() OVER () as rownr from restaurants R");
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        JdbcCommon.convertToAvroStream(resultSet, outStream, false);
        final byte[] serializedBytes = outStream.toByteArray();
        Assert.assertNotNull(serializedBytes);
        System.out.println(("Avro serialized result size in bytes: " + (serializedBytes.length)));
        st.close();
        // Deserialize bytes to records
        final InputStream instream = new ByteArrayInputStream(serializedBytes);
        final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
        try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
            GenericRecord record = null;
            while (dataFileReader.hasNext()) {
                // Reuse record object by passing it to next(). This saves us from
                // allocating and garbage collecting many objects for files with
                // many items.
                record = dataFileReader.next(record);
                System.out.println(record);
            } 
        }
    }

    @Test
    public void testCreateSchemaTypes() throws IllegalAccessException, IllegalArgumentException, SQLException {
        final Set<Integer> fieldsToIgnore = new HashSet<>();
        fieldsToIgnore.add(Types.NULL);
        fieldsToIgnore.add(Types.OTHER);
        final Field[] fieldTypes = Types.class.getFields();
        for (final Field field : fieldTypes) {
            final Object fieldObject = field.get(null);
            final int type = ((int) (fieldObject));
            if (fieldsToIgnore.contains(Types.NULL)) {
                continue;
            }
            final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
            Mockito.when(metadata.getColumnCount()).thenReturn(1);
            Mockito.when(metadata.getColumnType(1)).thenReturn(type);
            Mockito.when(metadata.getColumnName(1)).thenReturn(field.getName());
            Mockito.when(metadata.getTableName(1)).thenReturn("table");
            final ResultSet rs = Mockito.mock(ResultSet.class);
            Mockito.when(rs.getMetaData()).thenReturn(metadata);
            try {
                JdbcCommon.createSchema(rs);
            } catch (IllegalArgumentException | SQLException sqle) {
                sqle.printStackTrace();
                Assert.fail(("Failed when using type " + (field.getName())));
            }
        }
    }

    @Test
    public void testSignedIntShouldBeInt() throws IllegalAccessException, IllegalArgumentException, SQLException {
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(metadata.isSigned(1)).thenReturn(true);
        Mockito.when(metadata.getColumnName(1)).thenReturn("Col1");
        Mockito.when(metadata.getTableName(1)).thenReturn("Table1");
        final ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(rs.getMetaData()).thenReturn(metadata);
        Schema schema = JdbcCommon.createSchema(rs);
        Assert.assertNotNull(schema);
        Schema.Field field = schema.getField("Col1");
        Schema fieldSchema = field.schema();
        Assert.assertEquals(2, fieldSchema.getTypes().size());
        boolean foundIntSchema = false;
        boolean foundNullSchema = false;
        for (Schema type : fieldSchema.getTypes()) {
            if (type.getType().equals(INT)) {
                foundIntSchema = true;
            } else
                if (type.getType().equals(NULL)) {
                    foundNullSchema = true;
                }

        }
        Assert.assertTrue(foundIntSchema);
        Assert.assertTrue(foundNullSchema);
    }

    @Test
    public void testUnsignedIntShouldBeLong() throws IllegalAccessException, IllegalArgumentException, SQLException {
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(metadata.getPrecision(1)).thenReturn(10);
        Mockito.when(metadata.isSigned(1)).thenReturn(false);
        Mockito.when(metadata.getColumnName(1)).thenReturn("Col1");
        Mockito.when(metadata.getTableName(1)).thenReturn("Table1");
        final ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(rs.getMetaData()).thenReturn(metadata);
        Schema schema = JdbcCommon.createSchema(rs);
        Assert.assertNotNull(schema);
        Schema.Field field = schema.getField("Col1");
        Schema fieldSchema = field.schema();
        Assert.assertEquals(2, fieldSchema.getTypes().size());
        boolean foundLongSchema = false;
        boolean foundNullSchema = false;
        for (Schema type : fieldSchema.getTypes()) {
            if (type.getType().equals(LONG)) {
                foundLongSchema = true;
            } else
                if (type.getType().equals(NULL)) {
                    foundNullSchema = true;
                }

        }
        Assert.assertTrue(foundLongSchema);
        Assert.assertTrue(foundNullSchema);
    }

    @Test
    public void testMediumUnsignedIntShouldBeInt() throws IllegalAccessException, IllegalArgumentException, SQLException {
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(metadata.getPrecision(1)).thenReturn(8);
        Mockito.when(metadata.isSigned(1)).thenReturn(false);
        Mockito.when(metadata.getColumnName(1)).thenReturn("Col1");
        Mockito.when(metadata.getTableName(1)).thenReturn("Table1");
        final ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(rs.getMetaData()).thenReturn(metadata);
        Schema schema = JdbcCommon.createSchema(rs);
        Assert.assertNotNull(schema);
        Schema.Field field = schema.getField("Col1");
        Schema fieldSchema = field.schema();
        Assert.assertEquals(2, fieldSchema.getTypes().size());
        boolean foundIntSchema = false;
        boolean foundNullSchema = false;
        for (Schema type : fieldSchema.getTypes()) {
            if (type.getType().equals(INT)) {
                foundIntSchema = true;
            } else
                if (type.getType().equals(NULL)) {
                    foundNullSchema = true;
                }

        }
        Assert.assertTrue(foundIntSchema);
        Assert.assertTrue(foundNullSchema);
    }

    @Test
    public void testInt9ShouldBeLong() throws IllegalAccessException, IllegalArgumentException, SQLException {
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(metadata.getPrecision(1)).thenReturn(9);
        Mockito.when(metadata.isSigned(1)).thenReturn(false);
        Mockito.when(metadata.getColumnName(1)).thenReturn("Col1");
        Mockito.when(metadata.getTableName(1)).thenReturn("Table1");
        final ResultSet rs = Mockito.mock(ResultSet.class);
        Mockito.when(rs.getMetaData()).thenReturn(metadata);
        Schema schema = JdbcCommon.createSchema(rs);
        Assert.assertNotNull(schema);
        Schema.Field field = schema.getField("Col1");
        Schema fieldSchema = field.schema();
        Assert.assertEquals(2, fieldSchema.getTypes().size());
        boolean foundLongSchema = false;
        boolean foundNullSchema = false;
        for (Schema type : fieldSchema.getTypes()) {
            if (type.getType().equals(LONG)) {
                foundLongSchema = true;
            } else
                if (type.getType().equals(NULL)) {
                    foundNullSchema = true;
                }

        }
        Assert.assertTrue(foundLongSchema);
        Assert.assertTrue(foundNullSchema);
    }

    @Test
    public void testConvertToAvroStreamForBigDecimal() throws IOException, SQLException {
        final BigDecimal bigDecimal = new BigDecimal(12345.0);
        // If db returns a precision, it should be used.
        testConvertToAvroStreamForBigDecimal(bigDecimal, 38, 10, 38, 0);
    }

    @Test
    public void testConvertToAvroStreamForBigDecimalWithUndefinedPrecision() throws IOException, SQLException {
        final int expectedScale = 3;
        final int dbPrecision = 0;
        final BigDecimal bigDecimal = new BigDecimal(new BigInteger("12345"), expectedScale, new MathContext(dbPrecision));
        // If db doesn't return a precision, default precision should be used.
        testConvertToAvroStreamForBigDecimal(bigDecimal, dbPrecision, 24, 24, expectedScale);
    }

    @Test
    public void testClob() throws Exception {
        try (final Statement stmt = TestJdbcCommon.con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE clobtest (id INT, text CLOB(64 K))");
            stmt.execute("INSERT INTO clobtest VALUES (41, NULL)");
            PreparedStatement ps = TestJdbcCommon.con.prepareStatement("INSERT INTO clobtest VALUES (?, ?)");
            ps.setInt(1, 42);
            final char[] buffer = new char[4002];
            IntStream.range(0, 4002).forEach(( i) -> buffer[i] = String.valueOf((i % 10)).charAt(0));
            // Put a zero-byte in to test the buffer building logic
            buffer[1] = 0;
            ReaderInputStream isr = new ReaderInputStream(new CharArrayReader(buffer), Charset.defaultCharset());
            // - set the value of the input parameter to the input stream
            ps.setAsciiStream(2, isr, 4002);
            ps.execute();
            isr.close();
            final ResultSet resultSet = stmt.executeQuery("select * from clobtest");
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            JdbcCommon.convertToAvroStream(resultSet, outStream, false);
            final byte[] serializedBytes = outStream.toByteArray();
            Assert.assertNotNull(serializedBytes);
            // Deserialize bytes to records
            final InputStream instream = new ByteArrayInputStream(serializedBytes);
            final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
            try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
                GenericRecord record = null;
                while (dataFileReader.hasNext()) {
                    // Reuse record object by passing it to next(). This saves us from
                    // allocating and garbage collecting many objects for files with
                    // many items.
                    record = dataFileReader.next(record);
                    Integer id = ((Integer) (record.get("ID")));
                    Object o = record.get("TEXT");
                    if (id == 41) {
                        Assert.assertNull(o);
                    } else {
                        Assert.assertNotNull(o);
                        final String text = o.toString();
                        Assert.assertEquals(4002, text.length());
                        // Third character should be '2'
                        Assert.assertEquals('2', text.charAt(2));
                    }
                } 
            }
        }
    }

    @Test
    public void testBlob() throws Exception {
        try (final Statement stmt = TestJdbcCommon.con.createStatement()) {
            stmt.executeUpdate("CREATE TABLE blobtest (id INT, b BLOB(64 K))");
            stmt.execute("INSERT INTO blobtest VALUES (41, NULL)");
            PreparedStatement ps = TestJdbcCommon.con.prepareStatement("INSERT INTO blobtest VALUES (?, ?)");
            ps.setInt(1, 42);
            final byte[] buffer = new byte[4002];
            IntStream.range(0, 4002).forEach(( i) -> buffer[i] = ((byte) ((i % 10) + 65)));
            // Put a zero-byte in to test the buffer building logic
            buffer[1] = 0;
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            // - set the value of the input parameter to the input stream
            ps.setBlob(2, bais, 4002);
            ps.execute();
            bais.close();
            final ResultSet resultSet = stmt.executeQuery("select * from blobtest");
            final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            JdbcCommon.convertToAvroStream(resultSet, outStream, false);
            final byte[] serializedBytes = outStream.toByteArray();
            Assert.assertNotNull(serializedBytes);
            // Deserialize bytes to records
            final InputStream instream = new ByteArrayInputStream(serializedBytes);
            final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
            try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
                GenericRecord record = null;
                while (dataFileReader.hasNext()) {
                    // Reuse record object by passing it to next(). This saves us from
                    // allocating and garbage collecting many objects for files with
                    // many items.
                    record = dataFileReader.next(record);
                    Integer id = ((Integer) (record.get("ID")));
                    Object o = record.get("B");
                    if (id == 41) {
                        Assert.assertNull(o);
                    } else {
                        Assert.assertNotNull(o);
                        Assert.assertTrue((o instanceof ByteBuffer));
                        final byte[] blob = ((ByteBuffer) (o)).array();
                        Assert.assertEquals(4002, blob.length);
                        // Third byte should be 67 ('C')
                        Assert.assertEquals('C', blob[2]);
                    }
                } 
            }
        }
    }

    @Test
    public void testConvertToAvroStreamForShort() throws IOException, SQLException {
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.TINYINT);
        Mockito.when(metadata.getColumnName(1)).thenReturn("t_int");
        Mockito.when(metadata.getTableName(1)).thenReturn("table");
        final ResultSet rs = JdbcCommonTestUtils.resultSetReturningMetadata(metadata);
        final short s = 25;
        Mockito.when(rs.getObject(Mockito.anyInt())).thenReturn(s);
        final InputStream instream = JdbcCommonTestUtils.convertResultSetToAvroInputStream(rs);
        final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
        try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
            GenericRecord record = null;
            while (dataFileReader.hasNext()) {
                record = dataFileReader.next(record);
                Assert.assertEquals(Short.toString(s), record.get("t_int").toString());
            } 
        }
    }

    @Test
    public void testConvertToAvroStreamForUnsignedIntegerWithPrecision1ReturnedAsLong_NIFI5612() throws IOException, SQLException {
        final String mockColumnName = "t_int";
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(metadata.isSigned(1)).thenReturn(false);
        Mockito.when(metadata.getPrecision(1)).thenReturn(1);
        Mockito.when(metadata.getColumnName(1)).thenReturn(mockColumnName);
        Mockito.when(metadata.getTableName(1)).thenReturn("table");
        final ResultSet rs = JdbcCommonTestUtils.resultSetReturningMetadata(metadata);
        final Long ret = 0L;
        Mockito.when(rs.getObject(Mockito.anyInt())).thenReturn(ret);
        final InputStream instream = JdbcCommonTestUtils.convertResultSetToAvroInputStream(rs);
        final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
        try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
            GenericRecord record = null;
            while (dataFileReader.hasNext()) {
                record = dataFileReader.next(record);
                Assert.assertEquals(Long.toString(ret), record.get(mockColumnName).toString());
            } 
        }
    }

    @Test
    public void testConvertToAvroStreamForUnsignedIntegerWithPrecision10() throws IOException, SQLException {
        final String mockColumnName = "t_int";
        final ResultSetMetaData metadata = Mockito.mock(ResultSetMetaData.class);
        Mockito.when(metadata.getColumnCount()).thenReturn(1);
        Mockito.when(metadata.getColumnType(1)).thenReturn(Types.INTEGER);
        Mockito.when(metadata.isSigned(1)).thenReturn(false);
        Mockito.when(metadata.getPrecision(1)).thenReturn(10);
        Mockito.when(metadata.getColumnName(1)).thenReturn(mockColumnName);
        Mockito.when(metadata.getTableName(1)).thenReturn("table");
        final ResultSet rs = JdbcCommonTestUtils.resultSetReturningMetadata(metadata);
        final Long ret = 0L;
        Mockito.when(rs.getObject(Mockito.anyInt())).thenReturn(ret);
        final InputStream instream = JdbcCommonTestUtils.convertResultSetToAvroInputStream(rs);
        final DatumReader<GenericRecord> datumReader = new org.apache.avro.generic.GenericDatumReader();
        try (final DataFileStream<GenericRecord> dataFileReader = new DataFileStream(instream, datumReader)) {
            GenericRecord record = null;
            while (dataFileReader.hasNext()) {
                record = dataFileReader.next(record);
                Assert.assertEquals(Long.toString(ret), record.get(mockColumnName).toString());
            } 
        }
    }

    @Test
    public void testConvertToAvroStreamForDateTimeAsString() throws IOException, SQLException, ParseException {
        final JdbcCommon.AvroConversionOptions options = AvroConversionOptions.builder().convertNames(true).useLogicalTypes(false).build();
        testConvertToAvroStreamForDateTime(options, ( record, date) -> assertEquals(new Utf8(date.toString()), record.get("date")), ( record, time) -> assertEquals(new Utf8(time.toString()), record.get("time")), ( record, timestamp) -> assertEquals(new Utf8(timestamp.toString()), record.get("timestamp")));
    }

    @Test
    public void testConvertToAvroStreamForDateTimeAsLogicalType() throws IOException, SQLException, ParseException {
        final JdbcCommon.AvroConversionOptions options = AvroConversionOptions.builder().convertNames(true).useLogicalTypes(true).build();
        testConvertToAvroStreamForDateTime(options, ( record, date) -> {
            final int daysSinceEpoch = ((int) (record.get("date")));
            final long millisSinceEpoch = TimeUnit.MILLISECONDS.convert(daysSinceEpoch, TimeUnit.DAYS);
            assertEquals(date, new Date(millisSinceEpoch));
        }, ( record, time) -> assertEquals(time, new Time(((int) (record.get("time"))))), ( record, timestamp) -> assertEquals(timestamp, new Timestamp(((long) (record.get("timestamp"))))));
    }

    // many test use Derby as database, so ensure driver is available
    @Test
    public void testDriverLoad() throws ClassNotFoundException {
        final Class<?> clazz = Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        Assert.assertNotNull(clazz);
    }
}

