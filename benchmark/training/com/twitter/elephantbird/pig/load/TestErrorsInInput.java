package com.twitter.elephantbird.pig.load;


import LzoRecordReader.BAD_RECORD_MIN_COUNT_CONF_KEY;
import LzoRecordReader.BAD_RECORD_THRESHOLD_CONF_KEY;
import Protobufs.NEWLINE_UTF8_BYTE;
import com.twitter.elephantbird.mapreduce.io.RawBlockWriter;
import com.twitter.elephantbird.mapreduce.io.ThriftConverter;
import com.twitter.elephantbird.pig.util.ThriftToPig;
import com.twitter.elephantbird.thrift.test.TestPerson;
import com.twitter.elephantbird.util.Codecs;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * 1. Test to ensure that empty records in B64Line and Block formats are
 * correctly skipped.
 * 2. Test error tolerance in input.
 */
public class TestErrorsInInput {
    // create a directory with two lzo files, one in Base64Line format
    // and the other in Serialized blocks, and load them using
    // MultiFormatLoader
    private static PigServer pigServer;

    private static Configuration conf;

    private final TestPerson[] records = new TestPerson[]{ makePerson(0), makePerson(1), makePerson(2) };

    @Test
    public void TestMultiFormatLoaderWithEmptyRecords() throws Exception {
        Assume.assumeTrue(((TestErrorsInInput.pigServer) != null));
        // initalize
        String testDir = (System.getProperty("test.build.data")) + "/TestEmptyRecords";
        final File inDir = new File(testDir, "in");
        inDir.mkdirs();
        // block writer
        RawBlockWriter blk_writer = new RawBlockWriter(createLzoOut(new File(inDir, "1-block.lzo"), TestErrorsInInput.conf));
        // b64 writer
        OutputStream b64_writer = createLzoOut(new File(inDir, "2-b64.lzo"), TestErrorsInInput.conf);
        Base64 base64 = Codecs.createStandardBase64();
        for (TestPerson rec : records) {
            // write a regular record and an empty record
            byte[] bytes = tConverter.toBytes(rec);
            blk_writer.write(bytes);
            blk_writer.write(new byte[0]);
            b64_writer.write(base64.encode(bytes));
            b64_writer.write(NEWLINE_UTF8_BYTE);
            b64_writer.write(NEWLINE_UTF8_BYTE);// empty line.

        }
        blk_writer.close();
        b64_writer.close();
        // end of initialization.
        TestErrorsInInput.pigServer.registerQuery(String.format("A = load \'%s\' using %s(\'%s\');\n", inDir.toURI().toString(), MultiFormatLoader.class.getName(), TestPerson.class.getName()));
        Iterator<Tuple> rows = TestErrorsInInput.pigServer.openIterator("A");
        // verify:
        for (int i = 0; i < 2; i++) {
            for (TestPerson person : records) {
                String expected = personToString(person);
                Assert.assertEquals(expected, rows.next().toString());
            }
        }
        FileUtil.fullyDelete(new File(testDir));
    }

    @Test
    public void TestErrorTolerance() throws Exception {
        // test configurable error tolerance in EB record reader.
        Assume.assumeTrue(((TestErrorsInInput.pigServer) != null));
        // initialize
        String testDir = (System.getProperty("test.build.data")) + "/TestErrorTolerance";
        final File inDir = new File(testDir, "in");
        inDir.mkdirs();
        // create input with 100 records with 10% of records with errors.
        RawBlockWriter blk_writer = new RawBlockWriter(createLzoOut(new File(inDir, "1-block.lzo"), TestErrorsInInput.conf));
        TestPerson person = records[((records.length) - 1)];
        String expectedStr = personToString(person);
        byte[] properRec = tConverter.toBytes(person);
        byte[] truncatedRec = Arrays.copyOfRange(properRec, 0, (((properRec.length) * 3) / 4));
        final int totalRecords = 100;
        final int pctErrors = 10;
        final int totalErrors = (totalRecords * pctErrors) / 100;
        final int goodRecords = totalRecords - totalErrors;
        int corruptIdx = new Random().nextInt(10);
        for (int i = 0; i < totalRecords; i++) {
            blk_writer.write(((i % 10) == corruptIdx ? truncatedRec : properRec));
        }
        blk_writer.close();
        String[] expectedRows = new String[goodRecords];
        for (int i = 0; i < goodRecords; i++) {
            expectedRows[i] = expectedStr;
        }
        // A = load 'in' using ThritPigLoader('TestPerson');
        String loadStmt = String.format("A = load \'%s\' using %s(\'%s\');\n", inDir.toURI().toString(), ThriftPigLoader.class.getName(), TestPerson.class.getName());
        // a simple load should fail.
        TestErrorsInInput.pigServer.registerQuery(loadStmt);
        try {
            verifyRows(expectedRows, TestErrorsInInput.pigServer.openIterator("A"));
            Assert.assertFalse("A Pig IOException was expected", true);
        } catch (IOException e) {
            // expected.
        }
        // loader should succeed with error rate set to 50%
        TestErrorsInInput.pigServer.getPigContext().getProperties().setProperty(BAD_RECORD_THRESHOLD_CONF_KEY, "0.5");
        TestErrorsInInput.pigServer.registerQuery(loadStmt);
        verifyRows(expectedRows, TestErrorsInInput.pigServer.openIterator("A"));
        // set low threshold and test min_error count works.
        TestErrorsInInput.pigServer.getPigContext().getProperties().setProperty(BAD_RECORD_THRESHOLD_CONF_KEY, "0.0001");
        TestErrorsInInput.pigServer.getPigContext().getProperties().setProperty(BAD_RECORD_MIN_COUNT_CONF_KEY, ("" + (totalErrors + 1)));
        verifyRows(expectedRows, TestErrorsInInput.pigServer.openIterator("A"));
    }

    // thrift class related :
    private ThriftToPig<TestPerson> thriftToPig = ThriftToPig.newInstance(TestPerson.class);

    private ThriftConverter<TestPerson> tConverter = ThriftConverter.newInstance(TestPerson.class);
}

