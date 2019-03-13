package com.twitter.elephantbird.pig.load;


import com.google.protobuf.Message;
import com.twitter.data.proto.tutorial.AddressBookProtos.Person;
import com.twitter.data.proto.tutorial.AddressBookProtos.PersonWithoutEmail;
import com.twitter.elephantbird.mapreduce.io.ProtobufWritable;
import com.twitter.elephantbird.pig.piggybank.ProtobufBytesToTuple;
import com.twitter.elephantbird.pig.store.RCFileProtobufPigStorage;
import com.twitter.elephantbird.util.Codecs;
import com.twitter.elephantbird.util.CoreTestUtil;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.apache.commons.codec.binary.Base64;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.pig.PigServer;
import org.apache.pig.data.DataByteArray;
import org.apache.pig.data.Tuple;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test RCFile loader and storage with Protobufs.
 */
public class TestRCFileProtobufStorage {
    private PigServer pigServer;

    private final String testDir = CoreTestUtil.getTestDataDir(TestRCFileProtobufStorage.class);

    private final File inputDir = new File(testDir, "in");

    private final File rcfile_in = new File(testDir, "rcfile_in");

    private final Person[] records = new Person[]{ TestRCFileProtobufStorage.makePerson(0), TestRCFileProtobufStorage.makePerson(1), TestRCFileProtobufStorage.makePerson(2), TestRCFileProtobufStorage.makePersonWithDefaults(3, true), TestRCFileProtobufStorage.makePersonWithDefaults(4, false), TestRCFileProtobufStorage.makePersonWithDefaults(4, true) };

    private static final Base64 base64 = Codecs.createStandardBase64();

    public static class B64ToTuple extends ProtobufBytesToTuple<Message> {
        public B64ToTuple(String className) {
            super(className);
        }

        @Override
        public Tuple exec(Tuple input) throws IOException {
            byte[] bytes = get();
            input.set(0, new DataByteArray(TestRCFileProtobufStorage.base64.decode(bytes)));
            return super.exec(input);
        }
    }

    @Test
    public void testRCFileStorage() throws Exception {
        /* create a directory with three rcfiles :
         - one created with normal Person objects using RCFileProtobufPigStorage.
         - one created with Person objects where the optional fields are not set.
         - other with PersonWithoutEmail (for testing unknown fields)
           using the same objects as the first one.

        Then load both files using RCFileProtobufPigLoader
         */
        // write to rcFile using RCFileProtobufStorage
        for (String line : String.format(("DEFINE b64ToTuple %s(\'%s\');\n" + ((("A = load \'%s\' as (line);\n" + "A = foreach A generate b64ToTuple(line) as t;\n") + "A = foreach A generate FLATTEN(t);\n") + "STORE A into \'%s\' using %s(\'%s\');\n")), TestRCFileProtobufStorage.B64ToTuple.class.getName(), Person.class.getName(), inputDir.toURI().toString(), rcfile_in.toURI().toString(), RCFileProtobufPigStorage.class.getName(), Person.class.getName()).split("\n")) {
            pigServer.registerQuery((line + "\n"));
        }
        // create an rcfile with Person objects directly with out converting to a
        // tuple so that optional fields that are not set are null in RCFile
        ProtobufWritable<Person> personWritable = ProtobufWritable.newInstance(Person.class);
        RecordWriter<Writable, Writable> protoWriter = TestRCFileProtobufStorage.createProtoWriter(Person.class, new File(rcfile_in, "persons_with_unset_fields.rc"));
        for (Person person : records) {
            personWritable.set(person);
            protoWriter.write(null, personWritable);
        }
        protoWriter.close(null);
        // create an rcFile with PersonWithoutEmail to test unknown fields
        ProtobufWritable<PersonWithoutEmail> pweWritable = ProtobufWritable.newInstance(PersonWithoutEmail.class);
        protoWriter = TestRCFileProtobufStorage.createProtoWriter(PersonWithoutEmail.class, new File(rcfile_in, "persons_with_unknows.rc"));
        for (Person person : records) {
            pweWritable.set(PersonWithoutEmail.newBuilder().mergeFrom(person.toByteArray()).build());
            protoWriter.write(null, pweWritable);
        }
        protoWriter.close(null);
        // load all the files
        pigServer.registerQuery(String.format("A = load \'%s\' using %s(\'%s\');\n", rcfile_in.toURI().toString(), RCFileProtobufPigLoader.class.getName(), Person.class.getName()));
        // verify the result:
        Iterator<Tuple> rows = pigServer.openIterator("A");
        for (int i = 0; i < 3; i++) {
            for (Person person : records) {
                String expected = TestRCFileProtobufStorage.personToString(person);
                Assert.assertEquals(expected, rows.next().toString());
            }
        }
        // clean up on successful run
        FileUtil.fullyDelete(new File(testDir));
    }
}

