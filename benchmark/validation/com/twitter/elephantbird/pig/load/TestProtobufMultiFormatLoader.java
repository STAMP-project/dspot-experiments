package com.twitter.elephantbird.pig.load;


import com.twitter.data.proto.tutorial.AddressBookProtos.Person;
import java.io.File;
import java.util.Iterator;
import org.apache.hadoop.fs.FileUtil;
import org.apache.pig.PigServer;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test {@link MultiFormatLoader} using a Protobuf.
 */
public class TestProtobufMultiFormatLoader {
    // create a directory with two lzo files, one in Base64Line format
    // and the other in Serialized blocks, and load them using
    // MultiFormatLoader
    private PigServer pigServer;

    private final String testDir = (System.getProperty("test.build.data")) + "/TestProtobufMultiFormatLoader";

    private final File inputDir = new File(testDir, "in");

    private final Person[] records = new Person[]{ TestProtobufMultiFormatLoader.makePerson(0), TestProtobufMultiFormatLoader.makePerson(1), TestProtobufMultiFormatLoader.makePerson(2) };

    @Test
    public void testMultiFormatLoader() throws Exception {
        // setUp might not have run because of missing lzo native libraries
        Assume.assumeTrue(((pigServer) != null));
        pigServer.registerQuery(String.format("A = load \'%s\' using %s(\'%s\');\n", inputDir.toURI().toString(), ProtobufPigLoader.class.getName(), Person.class.getName()));
        Iterator<Tuple> rows = pigServer.openIterator("A");
        // verify:
        for (int i = 0; i < 2; i++) {
            for (Person person : records) {
                String expected = TestProtobufMultiFormatLoader.personToString(person);
                Assert.assertEquals(expected, rows.next().toString());
            }
        }
        FileUtil.fullyDelete(inputDir);
    }
}

