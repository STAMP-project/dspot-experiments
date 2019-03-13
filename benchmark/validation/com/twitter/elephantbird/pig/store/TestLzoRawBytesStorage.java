package com.twitter.elephantbird.pig.store;


import com.twitter.elephantbird.mapreduce.io.ThriftConverter;
import com.twitter.elephantbird.pig.load.LzoRawBytesLoader;
import com.twitter.elephantbird.thrift.test.Name;
import com.twitter.elephantbird.thrift.test.Person;
import com.twitter.elephantbird.util.ThriftUtils;
import java.io.File;
import java.util.concurrent.Callable;
import org.apache.hadoop.conf.Configuration;
import org.apache.pig.PigServer;
import org.junit.Test;


/**
 * Unit tests for {@link LzoRawBytesLoader} and {@link LzoRawBytesStorage}.
 *
 * @author Andy Schlaikjer
 */
public class TestLzoRawBytesStorage {
    private final Person message = new Person(new Name("A", "B"), 1, "a@b.com", null);

    private final ThriftConverter<Person> converter = new ThriftConverter<Person>(ThriftUtils.<Person>getTypeRef(Person.class));

    private PigServer pigServer;

    private File tempPath;

    private String tempFilename;

    Configuration conf = new Configuration();

    @Test
    public void testLzoRawBytesLoader() throws Exception {
        runTest(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                pigServer.registerQuery(String.format("B = LOAD 'file:%s-thrift' USING %s() AS (thrift: bytearray);", tempFilename, LzoRawBytesLoader.class.getName()));
                validate(pigServer.openIterator("B"));
                return null;
            }
        });
    }

    @Test
    public void testLzoRawBytesStorage() throws Exception {
        runTest(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                pigServer.registerQuery(String.format("B = LOAD 'file:%s-thrift' USING %s() AS (thrift: bytearray);", tempFilename, LzoRawBytesLoader.class.getName()));
                pigServer.registerQuery(String.format("STORE B INTO 'file:%s-bytes' USING %s();", tempFilename, LzoRawBytesStorage.class.getName()));
                pigServer.executeBatch();
                pigServer.registerQuery(String.format("B2 = LOAD 'file:%s-bytes' USING %s() AS (thrift: bytearray);", tempFilename, LzoRawBytesLoader.class.getName()));
                validate(pigServer.openIterator("B2"));
                return null;
            }
        });
    }
}

