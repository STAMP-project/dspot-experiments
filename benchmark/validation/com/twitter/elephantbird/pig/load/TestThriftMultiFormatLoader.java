package com.twitter.elephantbird.pig.load;


import com.twitter.elephantbird.mapreduce.io.ThriftWritable;
import com.twitter.elephantbird.pig.util.ThriftToPig;
import com.twitter.elephantbird.thrift.test.TestPerson;
import java.io.File;
import java.util.Iterator;
import org.apache.hadoop.fs.FileUtil;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test {@link MultiFormatLoader} using a Thrift struct.
 */
public class TestThriftMultiFormatLoader {
    // create a directory with two lzo files, one in Base64Line format
    // and the other in Serialized blocks, and load them using
    // MultiFormatLoader
    private PigServer pigServer;

    private final String testDir = (System.getProperty("test.build.data")) + "/TestMultiFormatLoader";

    private final File inputDir = new File(testDir, "in");

    private final TestPerson[] records = new TestPerson[]{ makePerson(0), makePerson(1), makePerson(2) };

    @Test
    public void testMultiFormatLoader() throws Exception {
        // setUp might not have run because of missing lzo native libraries
        Assume.assumeTrue(((pigServer) != null));
        pigServer.registerQuery(String.format("A = load \'%s\' using %s(\'%s\');\n", inputDir.toURI().toString(), MultiFormatLoader.class.getName(), TestPerson.class.getName()));
        Iterator<Tuple> rows = pigServer.openIterator("A");
        // verify:
        for (int i = 0; i < 2; i++) {
            for (TestPerson person : records) {
                String expected = personToString(person);
                Assert.assertEquals(expected, rows.next().toString());
            }
        }
        FileUtil.fullyDelete(inputDir);
    }

    // thrift class related :
    private ThriftToPig<TestPerson> thriftToPig = ThriftToPig.newInstance(TestPerson.class);

    private ThriftWritable<TestPerson> thriftWritable = ThriftWritable.newInstance(TestPerson.class);
}

