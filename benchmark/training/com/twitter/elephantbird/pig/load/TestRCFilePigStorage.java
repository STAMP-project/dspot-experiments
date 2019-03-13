package com.twitter.elephantbird.pig.load;


import com.twitter.elephantbird.pig.store.RCFilePigStorage;
import com.twitter.elephantbird.util.CoreTestUtil;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.FileUtil;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to make sure PigStorage and RCFilePigStorage return the same tuples.
 */
public class TestRCFilePigStorage {
    private PigServer pigServer;

    private final String testDir = CoreTestUtil.getTestDataDir(TestRCFilePigStorage.class);

    private final File pigDir = new File(testDir, "pig_in");

    private final File rcfileDir = new File(testDir, "rcfile_in");

    private final int numRecords = 5;

    private final String schema = "name : chararray, " + (("age: int, " + "phone:(number: chararray, type: chararray),") + "occupation: chararray");

    @Test
    public void testRCFilePigStorage() throws IOException {
        // make sure both PigStorage & RCFilePigStorage read the same data
        for (String line : String.format(("A = load \'%s\' as (%s);\n" + ((("B = load \'%s\' using %s() as (%s);\n" + "-- projection \n") + "C = foreach A generate name, phone.number;\n") + "D = foreach B generate name, phone.number;\n")), pigDir.toURI().toString(), schema, rcfileDir.toURI().toString(), RCFilePigStorage.class.getName(), schema).split("\n")) {
            pigServer.registerQuery((line + "\n"));
        }
        Iterator<Tuple> rowsA = pigServer.openIterator("A");
        Iterator<Tuple> rowsB = pigServer.openIterator("B");
        // compare.
        for (int i = 0; i < (numRecords); i++) {
            Assert.assertEquals(rowsA.next().toString(), rowsB.next().toString());
        }
        Iterator<Tuple> rowsC = pigServer.openIterator("C");
        Iterator<Tuple> rowsD = pigServer.openIterator("D");
        for (int i = 0; i < (numRecords); i++) {
            Assert.assertEquals(rowsC.next().toString(), rowsD.next().toString());
        }
        FileUtil.fullyDelete(new File(testDir));
    }
}

