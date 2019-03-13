package com.twitter.elephantbird.pig.piggybank;


import com.google.common.collect.ImmutableMap;
import com.twitter.elephantbird.pig.util.PigTestUtil;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.apache.pig.impl.logicalLayer.schema.Schema;
import org.junit.Assert;
import org.junit.Test;


public class TestJsonStringToMap {
    private static final TupleFactory tupleFactory_ = TupleFactory.getInstance();

    private final JsonStringToMap udf_ = new JsonStringToMap();

    @Test
    public void testSchema() {
        Schema schema = udf_.outputSchema(null);
        Assert.assertNotNull(schema);
        Assert.assertEquals("{json: map[chararray]}", schema.toString());
    }

    @Test
    public final void testStandard() throws IOException, ExecException {
        Tuple input = TestJsonStringToMap.tupleFactory_.newTuple(Arrays.asList("{\"name\": \"value\", \"number\": 2}"));
        Map<String, String> result = udf_.exec(input);
        Assert.assertTrue("It should return a Map", (result instanceof Map<?, ?>));
        Assert.assertEquals("value", result.get("name"));
        Assert.assertEquals("It is expected to return numbers as strings", "2", result.get("number"));
    }

    @Test
    public final void testNestedJson() throws IOException, ExecException {
        Tuple input = TestJsonStringToMap.tupleFactory_.newTuple(Arrays.asList("{\"name\": \"value\", \"nestedJson\": {\"json\": \"ihazit\"}}"));
        Map<String, String> result = udf_.exec(input);
        Assert.assertTrue("Nested Json should just return as a String", ((result.get("nestedJson")) instanceof String));
    }

    @Test
    public final void testInThePig() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        String tempFilename = tempFile.getAbsolutePath();
        PrintWriter pw = new PrintWriter(tempFile);
        pw.println("1\t{\"name\": \"bob\", \"number\": 2}");
        pw.close();
        PigServer pig = PigTestUtil.makePigServer();
        try {
            pig.registerQuery(String.format("DEFINE JsonStringToMap %s();", JsonStringToMap.class.getName()));
            pig.registerQuery(String.format("x = LOAD '%s' AS (id: int, value: chararray);", tempFilename));
            pig.registerQuery(String.format("x = FOREACH x GENERATE id, JsonStringToMap(value);", tempFilename));
            Schema schema = pig.dumpSchema("x");
            Assert.assertNotNull(schema);
            Assert.assertEquals("{id: int,json: map[chararray]}", schema.toString());
            Iterator<Tuple> x = pig.openIterator("x");
            Assert.assertNotNull(x);
            Assert.assertTrue(x.hasNext());
            Tuple t = x.next();
            Assert.assertNotNull(t);
            Assert.assertEquals(2, t.size());
            Map<?, ?> actual = ((Map<?, ?>) (t.get(1)));
            Assert.assertNotNull(actual);
            Map<String, String> expected = ImmutableMap.<String, String>of("name", "bob", "number", "2");
            Assert.assertEquals(expected.size(), actual.size());
            for (Map.Entry<String, String> e : expected.entrySet()) {
                Assert.assertEquals(e.getValue(), actual.get(e.getKey()));
            }
        } finally {
            pig.shutdown();
        }
    }
}

