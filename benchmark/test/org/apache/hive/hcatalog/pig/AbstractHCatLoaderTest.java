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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.pig;


import ColumnProjectionUtils.READ_COLUMN_IDS_CONF_STR;
import DataType.BAG;
import DataType.BIGDECIMAL;
import DataType.BOOLEAN;
import DataType.BYTEARRAY;
import DataType.CHARARRAY;
import DataType.DATETIME;
import DataType.DOUBLE;
import DataType.FLOAT;
import DataType.INTEGER;
import DataType.LONG;
import DataType.MAP;
import DataType.TUPLE;
import HCatConstants.HCAT_DATA_CONVERT_BOOLEAN_TO_INTEGER;
import HCatFieldSchema.Type;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.common.type.Date;
import org.apache.hadoop.hive.common.type.Timestamp;
import org.apache.hadoop.hive.ql.IDriver;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hive.hcatalog.HcatTestUtils;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.data.Pair;
import org.apache.hive.hcatalog.mapreduce.HCatBaseTest;
import org.apache.pig.PigRunner;
import org.apache.pig.PigServer;
import org.apache.pig.ResourceStatistics;
import org.apache.pig.data.DataType;
import org.apache.pig.data.Tuple;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.apache.pig.impl.logicalLayer.schema.Schema.FieldSchema;
import org.apache.pig.tools.pigstats.OutputStats;
import org.apache.pig.tools.pigstats.PigStats;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractHCatLoaderTest extends HCatBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractHCatLoaderTest.class);

    private static final String BASIC_FILE_NAME = (TEST_DATA_DIR) + "/basic.input.data";

    private static final String COMPLEX_FILE_NAME = (TEST_DATA_DIR) + "/complex.input.data";

    private static final String DATE_FILE_NAME = (TEST_DATA_DIR) + "/datetimestamp.input.data";

    private static final String BASIC_TABLE = "junit_unparted_basic";

    private static final String COMPLEX_TABLE = "junit_unparted_complex";

    private static final String PARTITIONED_TABLE = "junit_parted_basic";

    private static final String SPECIFIC_SIZE_TABLE = "junit_specific_size";

    private static final String SPECIFIC_DATABASE = "junit_specific_db";

    private static final String SPECIFIC_SIZE_TABLE_2 = "junit_specific_size2";

    private static final String PARTITIONED_DATE_TABLE = "junit_parted_date";

    private Map<Integer, Pair<Integer, String>> basicInputData;

    protected String storageFormat;

    public AbstractHCatLoaderTest() {
        this.storageFormat = getStorageFormat();
    }

    @Test
    public void testSchemaLoadBasic() throws IOException {
        PigServer server = createPigServer(false);
        // test that schema was loaded correctly
        server.registerQuery((("X = load '" + (AbstractHCatLoaderTest.BASIC_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Schema dumpedXSchema = server.dumpSchema("X");
        List<FieldSchema> Xfields = dumpedXSchema.getFields();
        Assert.assertEquals(2, Xfields.size());
        Assert.assertTrue(Xfields.get(0).alias.equalsIgnoreCase("a"));
        Assert.assertTrue(((Xfields.get(0).type) == (DataType.INTEGER)));
        Assert.assertTrue(Xfields.get(1).alias.equalsIgnoreCase("b"));
        Assert.assertTrue(((Xfields.get(1).type) == (DataType.CHARARRAY)));
    }

    /**
     * Test that we properly translate data types in Hive/HCat table schema into Pig schema
     */
    @Test
    public void testSchemaLoadPrimitiveTypes() throws IOException {
        AbstractHCatLoaderTest.AllTypesTable.testSchemaLoadPrimitiveTypes();
    }

    /**
     * Test that value from Hive table are read properly in Pig
     */
    @Test
    public void testReadDataPrimitiveTypes() throws Exception {
        AbstractHCatLoaderTest.AllTypesTable.testReadDataPrimitiveTypes();
    }

    @Test
    public void testReadDataBasic() throws IOException {
        PigServer server = createPigServer(false);
        server.registerQuery((("X = load '" + (AbstractHCatLoaderTest.BASIC_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Iterator<Tuple> XIter = server.openIterator("X");
        int numTuplesRead = 0;
        while (XIter.hasNext()) {
            Tuple t = XIter.next();
            Assert.assertEquals(2, t.size());
            Assert.assertNotNull(t.get(0));
            Assert.assertNotNull(t.get(1));
            Assert.assertTrue(((t.get(0).getClass()) == (Integer.class)));
            Assert.assertTrue(((t.get(1).getClass()) == (String.class)));
            Assert.assertEquals(t.get(0), basicInputData.get(numTuplesRead).first);
            Assert.assertEquals(t.get(1), basicInputData.get(numTuplesRead).second);
            numTuplesRead++;
        } 
        Assert.assertEquals(basicInputData.size(), numTuplesRead);
    }

    @Test
    public void testSchemaLoadComplex() throws IOException {
        PigServer server = createPigServer(false);
        // test that schema was loaded correctly
        server.registerQuery((("K = load '" + (AbstractHCatLoaderTest.COMPLEX_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Schema dumpedKSchema = server.dumpSchema("K");
        List<FieldSchema> Kfields = dumpedKSchema.getFields();
        Assert.assertEquals(6, Kfields.size());
        Assert.assertEquals(CHARARRAY, Kfields.get(0).type);
        Assert.assertEquals("name", Kfields.get(0).alias.toLowerCase());
        Assert.assertEquals(INTEGER, Kfields.get(1).type);
        Assert.assertEquals("studentid", Kfields.get(1).alias.toLowerCase());
        Assert.assertEquals(TUPLE, Kfields.get(2).type);
        Assert.assertEquals("contact", Kfields.get(2).alias.toLowerCase());
        {
            Assert.assertNotNull(Kfields.get(2).schema);
            Assert.assertTrue(((Kfields.get(2).schema.getFields().size()) == 2));
            Assert.assertTrue(((Kfields.get(2).schema.getFields().get(0).type) == (DataType.CHARARRAY)));
            Assert.assertTrue(Kfields.get(2).schema.getFields().get(0).alias.equalsIgnoreCase("phno"));
            Assert.assertTrue(((Kfields.get(2).schema.getFields().get(1).type) == (DataType.CHARARRAY)));
            Assert.assertTrue(Kfields.get(2).schema.getFields().get(1).alias.equalsIgnoreCase("email"));
        }
        Assert.assertEquals(BAG, Kfields.get(3).type);
        Assert.assertEquals("currently_registered_courses", Kfields.get(3).alias.toLowerCase());
        {
            Assert.assertNotNull(Kfields.get(3).schema);
            Assert.assertEquals(1, Kfields.get(3).schema.getFields().size());
            Assert.assertEquals(TUPLE, Kfields.get(3).schema.getFields().get(0).type);
            Assert.assertNotNull(Kfields.get(3).schema.getFields().get(0).schema);
            Assert.assertEquals(1, Kfields.get(3).schema.getFields().get(0).schema.getFields().size());
            Assert.assertEquals(CHARARRAY, Kfields.get(3).schema.getFields().get(0).schema.getFields().get(0).type);
            // assertEquals("course",Kfields.get(3).schema.getFields().get(0).schema.getFields().get(0).alias.toLowerCase());
            // commented out, because the name becomes "innerfield" by default - we call it "course" in pig,
            // but in the metadata, it'd be anonymous, so this would be autogenerated, which is fine
        }
        Assert.assertEquals(MAP, Kfields.get(4).type);
        Assert.assertEquals("current_grades", Kfields.get(4).alias.toLowerCase());
        Assert.assertEquals(BAG, Kfields.get(5).type);
        Assert.assertEquals("phnos", Kfields.get(5).alias.toLowerCase());
        {
            Assert.assertNotNull(Kfields.get(5).schema);
            Assert.assertEquals(1, Kfields.get(5).schema.getFields().size());
            Assert.assertEquals(TUPLE, Kfields.get(5).schema.getFields().get(0).type);
            Assert.assertNotNull(Kfields.get(5).schema.getFields().get(0).schema);
            Assert.assertTrue(((Kfields.get(5).schema.getFields().get(0).schema.getFields().size()) == 2));
            Assert.assertEquals(CHARARRAY, Kfields.get(5).schema.getFields().get(0).schema.getFields().get(0).type);
            Assert.assertEquals("phno", Kfields.get(5).schema.getFields().get(0).schema.getFields().get(0).alias.toLowerCase());
            Assert.assertEquals(CHARARRAY, Kfields.get(5).schema.getFields().get(0).schema.getFields().get(1).type);
            Assert.assertEquals("type", Kfields.get(5).schema.getFields().get(0).schema.getFields().get(1).alias.toLowerCase());
        }
    }

    @Test
    public void testReadPartitionedBasic() throws Exception {
        PigServer server = createPigServer(false);
        driver.run(("select * from " + (AbstractHCatLoaderTest.PARTITIONED_TABLE)));
        ArrayList<String> valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertEquals(basicInputData.size(), valuesReadFromHiveDriver.size());
        server.registerQuery((("W = load '" + (AbstractHCatLoaderTest.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Schema dumpedWSchema = server.dumpSchema("W");
        List<FieldSchema> Wfields = dumpedWSchema.getFields();
        Assert.assertEquals(3, Wfields.size());
        Assert.assertTrue(Wfields.get(0).alias.equalsIgnoreCase("a"));
        Assert.assertTrue(((Wfields.get(0).type) == (DataType.INTEGER)));
        Assert.assertTrue(Wfields.get(1).alias.equalsIgnoreCase("b"));
        Assert.assertTrue(((Wfields.get(1).type) == (DataType.CHARARRAY)));
        Assert.assertTrue(Wfields.get(2).alias.equalsIgnoreCase("bkt"));
        Assert.assertTrue(((Wfields.get(2).type) == (DataType.CHARARRAY)));
        Iterator<Tuple> WIter = server.openIterator("W");
        Collection<Pair<Integer, String>> valuesRead = new ArrayList<Pair<Integer, String>>();
        while (WIter.hasNext()) {
            Tuple t = WIter.next();
            Assert.assertTrue(((t.size()) == 3));
            Assert.assertNotNull(t.get(0));
            Assert.assertNotNull(t.get(1));
            Assert.assertNotNull(t.get(2));
            Assert.assertTrue(((t.get(0).getClass()) == (Integer.class)));
            Assert.assertTrue(((t.get(1).getClass()) == (String.class)));
            Assert.assertTrue(((t.get(2).getClass()) == (String.class)));
            valuesRead.add(new Pair<Integer, String>(((Integer) (t.get(0))), ((String) (t.get(1)))));
            if (((Integer) (t.get(0))) < 2) {
                Assert.assertEquals("0", t.get(2));
            } else {
                Assert.assertEquals("1", t.get(2));
            }
        } 
        Assert.assertEquals(valuesReadFromHiveDriver.size(), valuesRead.size());
        server.registerQuery((("P1 = load '" + (AbstractHCatLoaderTest.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        server.registerQuery("P1filter = filter P1 by bkt == '0';");
        Iterator<Tuple> P1Iter = server.openIterator("P1filter");
        int count1 = 0;
        while (P1Iter.hasNext()) {
            Tuple t = P1Iter.next();
            Assert.assertEquals("0", t.get(2));
            Assert.assertEquals(1, t.get(0));
            count1++;
        } 
        Assert.assertEquals(3, count1);
        server.registerQuery((("P2 = load '" + (AbstractHCatLoaderTest.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        server.registerQuery("P2filter = filter P2 by bkt == '1';");
        Iterator<Tuple> P2Iter = server.openIterator("P2filter");
        int count2 = 0;
        while (P2Iter.hasNext()) {
            Tuple t = P2Iter.next();
            Assert.assertEquals("1", t.get(2));
            Assert.assertTrue((((Integer) (t.get(0))) > 1));
            count2++;
        } 
        Assert.assertEquals(6, count2);
    }

    @Test
    public void testReadMissingPartitionBasicNeg() throws Exception {
        PigServer server = createPigServer(false);
        File removedPartitionDir = new File(((((TEST_WAREHOUSE_DIR) + "/") + (AbstractHCatLoaderTest.PARTITIONED_TABLE)) + "/bkt=0"));
        if (!(AbstractHCatLoaderTest.removeDirectory(removedPartitionDir))) {
            System.out.println("Test did not run because its environment could not be set.");
            return;
        }
        driver.run(("select * from " + (AbstractHCatLoaderTest.PARTITIONED_TABLE)));
        ArrayList<String> valuesReadFromHiveDriver = new ArrayList<String>();
        driver.getResults(valuesReadFromHiveDriver);
        Assert.assertTrue(((valuesReadFromHiveDriver.size()) == 6));
        server.registerQuery((("W = load '" + (AbstractHCatLoaderTest.PARTITIONED_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        Schema dumpedWSchema = server.dumpSchema("W");
        List<FieldSchema> Wfields = dumpedWSchema.getFields();
        Assert.assertEquals(3, Wfields.size());
        Assert.assertTrue(Wfields.get(0).alias.equalsIgnoreCase("a"));
        Assert.assertTrue(((Wfields.get(0).type) == (DataType.INTEGER)));
        Assert.assertTrue(Wfields.get(1).alias.equalsIgnoreCase("b"));
        Assert.assertTrue(((Wfields.get(1).type) == (DataType.CHARARRAY)));
        Assert.assertTrue(Wfields.get(2).alias.equalsIgnoreCase("bkt"));
        Assert.assertTrue(((Wfields.get(2).type) == (DataType.CHARARRAY)));
        try {
            Iterator<Tuple> WIter = server.openIterator("W");
            Assert.fail("Should failed in retriving an invalid partition");
        } catch (IOException ioe) {
            // expected
        }
    }

    @Test
    public void testProjectionsBasic() throws IOException {
        PigServer server = createPigServer(false);
        // projections are handled by using generate, not "as" on the Load
        server.registerQuery((("Y1 = load '" + (AbstractHCatLoaderTest.BASIC_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        server.registerQuery("Y2 = foreach Y1 generate a;");
        server.registerQuery("Y3 = foreach Y1 generate b,a;");
        Schema dumpedY2Schema = server.dumpSchema("Y2");
        Schema dumpedY3Schema = server.dumpSchema("Y3");
        List<FieldSchema> Y2fields = dumpedY2Schema.getFields();
        List<FieldSchema> Y3fields = dumpedY3Schema.getFields();
        Assert.assertEquals(1, Y2fields.size());
        Assert.assertEquals("a", Y2fields.get(0).alias.toLowerCase());
        Assert.assertEquals(INTEGER, Y2fields.get(0).type);
        Assert.assertEquals(2, Y3fields.size());
        Assert.assertEquals("b", Y3fields.get(0).alias.toLowerCase());
        Assert.assertEquals(CHARARRAY, Y3fields.get(0).type);
        Assert.assertEquals("a", Y3fields.get(1).alias.toLowerCase());
        Assert.assertEquals(INTEGER, Y3fields.get(1).type);
        int numTuplesRead = 0;
        Iterator<Tuple> Y2Iter = server.openIterator("Y2");
        while (Y2Iter.hasNext()) {
            Tuple t = Y2Iter.next();
            Assert.assertEquals(t.size(), 1);
            Assert.assertNotNull(t.get(0));
            Assert.assertTrue(((t.get(0).getClass()) == (Integer.class)));
            Assert.assertEquals(t.get(0), basicInputData.get(numTuplesRead).first);
            numTuplesRead++;
        } 
        numTuplesRead = 0;
        Iterator<Tuple> Y3Iter = server.openIterator("Y3");
        while (Y3Iter.hasNext()) {
            Tuple t = Y3Iter.next();
            Assert.assertEquals(t.size(), 2);
            Assert.assertNotNull(t.get(0));
            Assert.assertTrue(((t.get(0).getClass()) == (String.class)));
            Assert.assertEquals(t.get(0), basicInputData.get(numTuplesRead).second);
            Assert.assertNotNull(t.get(1));
            Assert.assertTrue(((t.get(1).getClass()) == (Integer.class)));
            Assert.assertEquals(t.get(1), basicInputData.get(numTuplesRead).first);
            numTuplesRead++;
        } 
        Assert.assertEquals(basicInputData.size(), numTuplesRead);
    }

    @Test
    public void testColumnarStorePushdown() throws Exception {
        String PIGOUTPUT_DIR = (TEST_DATA_DIR) + "/colpushdownop";
        String PIG_FILE = "test.pig";
        String expectedCols = "0,1";
        PrintWriter w = new PrintWriter(new FileWriter(PIG_FILE));
        w.println((("A = load '" + (AbstractHCatLoaderTest.COMPLEX_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        w.println("B = foreach A generate name,studentid;");
        w.println("C = filter B by name is not null;");
        w.println((("store C into '" + PIGOUTPUT_DIR) + "' using PigStorage();"));
        w.close();
        try {
            String[] args = new String[]{ "-x", "local", PIG_FILE };
            PigStats stats = PigRunner.run(args, null);
            // Pig script was successful
            Assert.assertTrue(stats.isSuccessful());
            // Single MapReduce job is launched
            OutputStats outstats = stats.getOutputStats().get(0);
            Assert.assertTrue((outstats != null));
            Assert.assertEquals(expectedCols, outstats.getConf().get(READ_COLUMN_IDS_CONF_STR));
            // delete output file on exit
            FileSystem fs = FileSystem.get(outstats.getConf());
            if (fs.exists(new Path(PIGOUTPUT_DIR))) {
                fs.delete(new Path(PIGOUTPUT_DIR), true);
            }
        } finally {
            new File(PIG_FILE).delete();
        }
    }

    /**
     * Tests the failure case caused by HIVE-10752
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testColumnarStorePushdown2() throws Exception {
        PigServer server = createPigServer(false);
        server.registerQuery((("A = load '" + (AbstractHCatLoaderTest.COMPLEX_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        server.registerQuery((("B = load '" + (AbstractHCatLoaderTest.COMPLEX_TABLE)) + "' using org.apache.hive.hcatalog.pig.HCatLoader();"));
        server.registerQuery("C = join A by name, B by name;");
        server.registerQuery("D = foreach C generate B::studentid;");
        server.registerQuery("E = ORDER D by studentid asc;");
        Iterator<Tuple> iter = server.openIterator("E");
        Tuple t = iter.next();
        Assert.assertEquals(42, t.get(0));
        t = iter.next();
        Assert.assertEquals(1337, t.get(0));
    }

    @Test
    public void testGetInputBytes() throws Exception {
        File file = new File(((((TEST_WAREHOUSE_DIR) + "/") + (AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE)) + "/part-m-00000"));
        file.deleteOnExit();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.setLength((((2L * 1024) * 1024) * 1024));
        randomAccessFile.close();
        Job job = new Job();
        HCatLoader hCatLoader = new HCatLoader();
        hCatLoader.setUDFContextSignature("testGetInputBytes");
        hCatLoader.setLocation(AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE, job);
        ResourceStatistics statistics = hCatLoader.getStatistics(AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE, job);
        Assert.assertEquals(2048, ((long) (statistics.getmBytes())));
    }

    /**
     * Simulates Pig relying on HCatLoader to inform about input size of multiple tables.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetInputBytesMultipleTables() throws Exception {
        File file = new File(((((TEST_WAREHOUSE_DIR) + "/") + (AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE)) + "/part-m-00000"));
        file.deleteOnExit();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.setLength(987654321L);
        randomAccessFile.close();
        file = new File(((((((TEST_WAREHOUSE_DIR) + "/") + (AbstractHCatLoaderTest.SPECIFIC_DATABASE)) + ".db/") + (AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE_2)) + "/part-m-00000"));
        file.deleteOnExit();
        randomAccessFile = new RandomAccessFile(file, "rw");
        randomAccessFile.setLength(12345678L);
        randomAccessFile.close();
        Job job = new Job();
        HCatLoader hCatLoader = new HCatLoader();
        // Mocking that Pig would assign different signature for each POLoad operator
        hCatLoader.setUDFContextSignature(("testGetInputBytesMultipleTables" + (AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE)));
        hCatLoader.setLocation(AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE, job);
        HCatLoader hCatLoader2 = new HCatLoader();
        hCatLoader2.setUDFContextSignature(("testGetInputBytesMultipleTables" + (AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE_2)));
        hCatLoader2.setLocation((((AbstractHCatLoaderTest.SPECIFIC_DATABASE) + ".") + (AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE_2)), job);
        HCatLoader hCatLoader3 = new HCatLoader();
        hCatLoader3.setUDFContextSignature(("testGetInputBytesMultipleTables" + (AbstractHCatLoaderTest.PARTITIONED_TABLE)));
        hCatLoader3.setLocation(AbstractHCatLoaderTest.PARTITIONED_TABLE, job);
        long specificTableSize = -1;
        long specificTableSize2 = -1;
        long partitionedTableSize = -1;
        ResourceStatistics statistics = hCatLoader.getStatistics(AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE, job);
        specificTableSize = statistics.getSizeInBytes();
        Assert.assertEquals(987654321, specificTableSize);
        statistics = hCatLoader2.getStatistics(AbstractHCatLoaderTest.SPECIFIC_SIZE_TABLE_2, job);
        specificTableSize2 = statistics.getSizeInBytes();
        Assert.assertEquals(12345678, specificTableSize2);
        statistics = hCatLoader3.getStatistics(AbstractHCatLoaderTest.PARTITIONED_TABLE, job);
        partitionedTableSize = statistics.getSizeInBytes();
        // Partitioned table size here is dependent on underlying storage format, it's ~ 20<x<2000
        Assert.assertTrue(((20 < partitionedTableSize) && (partitionedTableSize < 2000)));
        // No-op here, just a reminder that Pig would do the calculation of the sum itself
        // e.g. when joining the 3 tables is requested
        Assert.assertTrue(((Math.abs((((specificTableSize + specificTableSize2) + partitionedTableSize) - ((987654321 + 12345678) + 1010)))) < 1010));
    }

    @Test
    public void testConvertBooleanToInt() throws Exception {
        String tbl = "test_convert_boolean_to_int";
        String inputFileName = (TEST_DATA_DIR) + "/testConvertBooleanToInt/data.txt";
        File inputDataDir = new File(inputFileName).getParentFile();
        inputDataDir.mkdir();
        String[] lines = new String[]{ "llama\ttrue", "alpaca\tfalse" };
        HcatTestUtils.createTestDataFile(inputFileName, lines);
        Assert.assertEquals(0, driver.run(("drop table if exists " + tbl)).getResponseCode());
        Assert.assertEquals(0, driver.run(((((("create external table " + tbl) + " (a string, b boolean) row format delimited fields terminated by \'\t\'") + " stored as textfile location 'file:///") + (inputDataDir.getPath().replaceAll("\\\\", "/"))) + "'")).getResponseCode());
        Properties properties = new Properties();
        properties.setProperty(HCAT_DATA_CONVERT_BOOLEAN_TO_INTEGER, "true");
        properties.put("stop.on.failure", Boolean.TRUE.toString());
        PigServer server = createPigServer(true, properties);
        server.registerQuery("data = load 'test_convert_boolean_to_int' using org.apache.hive.hcatalog.pig.HCatLoader();");
        Schema schema = server.dumpSchema("data");
        Assert.assertEquals(2, schema.getFields().size());
        Assert.assertEquals("a", schema.getField(0).alias);
        Assert.assertEquals(CHARARRAY, schema.getField(0).type);
        Assert.assertEquals("b", schema.getField(1).alias);
        if (PigHCatUtil.pigHasBooleanSupport()) {
            Assert.assertEquals(BOOLEAN, schema.getField(1).type);
        } else {
            Assert.assertEquals(INTEGER, schema.getField(1).type);
        }
        Iterator<Tuple> iterator = server.openIterator("data");
        Tuple t = iterator.next();
        Assert.assertEquals("llama", t.get(0));
        Assert.assertEquals(1, t.get(1));
        t = iterator.next();
        Assert.assertEquals("alpaca", t.get(0));
        Assert.assertEquals(0, t.get(1));
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * Test if we can read a date partitioned table
     */
    @Test
    public void testDatePartitionPushUp() throws Exception {
        PigServer server = createPigServer(false);
        server.registerQuery((((("X = load '" + (AbstractHCatLoaderTest.PARTITIONED_DATE_TABLE)) + "' using ") + (HCatLoader.class.getName())) + "();"));
        server.registerQuery("Y = filter X by dt == ToDate('2016-07-14','yyyy-MM-dd');");
        Iterator<Tuple> YIter = server.openIterator("Y");
        int numTuplesRead = 0;
        while (YIter.hasNext()) {
            Tuple t = YIter.next();
            Assert.assertEquals(t.size(), 2);
            numTuplesRead++;
        } 
        Assert.assertTrue(((("Expected " + 1) + "; found ") + numTuplesRead), (numTuplesRead == 1));
    }

    /**
     * basic tests that cover each scalar type
     * https://issues.apache.org/jira/browse/HIVE-5814
     */
    protected static final class AllTypesTable {
        private static final String ALL_TYPES_FILE_NAME = (TEST_DATA_DIR) + "/alltypes.input.data";

        private static final String ALL_PRIMITIVE_TYPES_TABLE = "junit_unparted_alltypes";

        private static final String ALL_TYPES_SCHEMA = "( c_boolean boolean, "// 0
         + (((((((((((("c_tinyint tinyint, "// 1
         + "c_smallint smallint, ")// 2
         + "c_int int, ") + // 3
        "c_bigint bigint, ")// 4
         + "c_float float, ") + // 5
        "c_double double, ")// 6
         + "c_decimal decimal(5,2), ")// 7
         + "c_string string, ")// 8
         + "c_char char(10), ")// 9
         + "c_varchar varchar(20), ")// 10
         + "c_binary binary, ")// 11
         + "c_date date, ") + // 12
        "c_timestamp timestamp)");// 13


        /**
         * raw data for #ALL_PRIMITIVE_TYPES_TABLE
         * All the values are within range of target data type (column)
         */
        private static final Object[][] primitiveRows = new Object[][]{ new Object[]{ Boolean.TRUE, Byte.MAX_VALUE, Short.MAX_VALUE, Integer.MAX_VALUE, Long.MAX_VALUE, Float.MAX_VALUE, Double.MAX_VALUE, 555.22, "Kyiv", "char(10)xx", "varchar(20)", "blah".getBytes(), Date.valueOf("2014-01-13"), Timestamp.valueOf("2014-01-13 19:26:25.0123") }, new Object[]{ Boolean.FALSE, Byte.MIN_VALUE, Short.MIN_VALUE, Integer.MIN_VALUE, Long.MIN_VALUE, Float.MIN_VALUE, Double.MIN_VALUE, -555.22, "Saint Petersburg", "char(xx)00", "varchar(yy)", "doh".getBytes(), Date.valueOf("2014-01-14"), Timestamp.valueOf("2014-01-14 19:26:25.0123") } };

        /**
         * Test that we properly translate data types in Hive/HCat table schema into Pig schema
         */
        static void testSchemaLoadPrimitiveTypes() throws IOException {
            PigServer server = createPigServer(false);
            server.registerQuery((((("X = load '" + (AbstractHCatLoaderTest.AllTypesTable.ALL_PRIMITIVE_TYPES_TABLE)) + "' using ") + (HCatLoader.class.getName())) + "();"));
            Schema dumpedXSchema = server.dumpSchema("X");
            List<FieldSchema> Xfields = dumpedXSchema.getFields();
            Assert.assertEquals(((("Expected " + (Type.numPrimitiveTypes())) + " fields, found ") + (Xfields.size())), Type.numPrimitiveTypes(), Xfields.size());
            AbstractHCatLoaderTest.checkProjection(Xfields.get(0), "c_boolean", BOOLEAN);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(1), "c_tinyint", INTEGER);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(2), "c_smallint", INTEGER);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(3), "c_int", INTEGER);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(4), "c_bigint", LONG);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(5), "c_float", FLOAT);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(6), "c_double", DOUBLE);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(7), "c_decimal", BIGDECIMAL);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(8), "c_string", CHARARRAY);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(9), "c_char", CHARARRAY);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(10), "c_varchar", CHARARRAY);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(11), "c_binary", BYTEARRAY);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(12), "c_date", DATETIME);
            AbstractHCatLoaderTest.checkProjection(Xfields.get(13), "c_timestamp", DATETIME);
        }

        /**
         * Test that value from Hive table are read properly in Pig
         */
        private static void testReadDataPrimitiveTypes() throws Exception {
            // testConvertBooleanToInt() sets HCatConstants.HCAT_DATA_CONVERT_BOOLEAN_TO_INTEGER=true, and
            // might be the last one to call HCatContext.INSTANCE.setConf(). Make sure setting is false.
            Properties properties = new Properties();
            properties.setProperty(HCAT_DATA_CONVERT_BOOLEAN_TO_INTEGER, "false");
            PigServer server = createPigServer(false, properties);
            server.registerQuery((((("X = load '" + (AbstractHCatLoaderTest.AllTypesTable.ALL_PRIMITIVE_TYPES_TABLE)) + "' using ") + (HCatLoader.class.getName())) + "();"));
            Iterator<Tuple> XIter = server.openIterator("X");
            int numTuplesRead = 0;
            while (XIter.hasNext()) {
                Tuple t = XIter.next();
                Assert.assertEquals(Type.numPrimitiveTypes(), t.size());
                int colPos = 0;
                for (Object referenceData : AbstractHCatLoaderTest.AllTypesTable.primitiveRows[numTuplesRead]) {
                    if (referenceData == null) {
                        Assert.assertTrue(((((("rowNum=" + numTuplesRead) + " colNum=") + colPos) + " Reference data is null; actual ") + (t.get(colPos))), ((t.get(colPos)) == null));
                    } else
                        if (referenceData instanceof Date) {
                            // Note that here we ignore nanos part of Hive Timestamp since nanos are dropped when
                            // reading Hive from Pig by design.
                            Assert.assertTrue((((((((((((("rowNum=" + numTuplesRead) + " colNum=") + colPos) + " Reference data=") + (toEpochMilli())) + " actual=") + (getMillis())) + "; types=(") + (referenceData.getClass())) + ",") + (t.get(colPos).getClass())) + ")"), ((toEpochMilli()) == (getMillis())));
                        } else
                            if (referenceData instanceof Timestamp) {
                                // Note that here we ignore nanos part of Hive Timestamp since nanos are dropped when
                                // reading Hive from Pig by design.
                                Assert.assertTrue((((((((((((("rowNum=" + numTuplesRead) + " colNum=") + colPos) + " Reference data=") + (toEpochMilli())) + " actual=") + (getMillis())) + "; types=(") + (referenceData.getClass())) + ",") + (t.get(colPos).getClass())) + ")"), ((toEpochMilli()) == (getMillis())));
                            } else {
                                // Doing String comps here as value objects in Hive in Pig are different so equals()
                                // doesn't work.
                                Assert.assertTrue((((((((((((("rowNum=" + numTuplesRead) + " colNum=") + colPos) + " Reference data=") + referenceData) + " actual=") + (t.get(colPos))) + "; types=(") + (referenceData.getClass())) + ",") + (t.get(colPos).getClass())) + ") "), referenceData.toString().equals(t.get(colPos).toString()));
                            }


                    colPos++;
                }
                numTuplesRead++;
            } 
            Assert.assertTrue(((("Expected " + (AbstractHCatLoaderTest.AllTypesTable.primitiveRows.length)) + "; found ") + numTuplesRead), (numTuplesRead == (AbstractHCatLoaderTest.AllTypesTable.primitiveRows.length)));
        }

        private static void setupAllTypesTable(IDriver driver) throws Exception {
            String[] primitiveData = new String[AbstractHCatLoaderTest.AllTypesTable.primitiveRows.length];
            for (int i = 0; i < (AbstractHCatLoaderTest.AllTypesTable.primitiveRows.length); i++) {
                Object[] rowData = AbstractHCatLoaderTest.AllTypesTable.primitiveRows[i];
                StringBuilder row = new StringBuilder();
                for (Object cell : rowData) {
                    row.append(((row.length()) == 0 ? "" : "\t")).append((cell == null ? null : cell));
                }
                primitiveData[i] = row.toString();
            }
            HcatTestUtils.createTestDataFile(AbstractHCatLoaderTest.AllTypesTable.ALL_TYPES_FILE_NAME, primitiveData);
            String cmd = ((("create table " + (AbstractHCatLoaderTest.AllTypesTable.ALL_PRIMITIVE_TYPES_TABLE)) + (AbstractHCatLoaderTest.AllTypesTable.ALL_TYPES_SCHEMA)) + "ROW FORMAT DELIMITED FIELDS TERMINATED BY \'\t\'") + " STORED AS TEXTFILE";
            AbstractHCatLoaderTest.executeStatementOnDriver(cmd, driver);
            cmd = (("load data local inpath '" + (HCatUtil.makePathASafeFileName(AbstractHCatLoaderTest.AllTypesTable.ALL_TYPES_FILE_NAME))) + "' into table ") + (AbstractHCatLoaderTest.AllTypesTable.ALL_PRIMITIVE_TYPES_TABLE);
            AbstractHCatLoaderTest.executeStatementOnDriver(cmd, driver);
        }
    }
}

