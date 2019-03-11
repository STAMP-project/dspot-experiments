package com.tinkerpop.blueprints.impls.tg;


import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;


/**
 *
 *
 * @author Victor Su
 */
public class TinkerMetadataWriterTest extends TestCase {
    public void testNormal() throws Exception {
        TinkerGraph g = TinkerGraphFactory.createTinkerGraph();
        createManualIndices(g);
        createKeyIndices(g);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        TinkerMetadataWriter.save(g, bos);
        byte[] expected = streamToByteArray(TinkerMetadataWriterTest.class.getResourceAsStream("example-tinkergraph-metadata.dat"));
        byte[] actual = bos.toByteArray();
        TestCase.assertEquals(expected.length, actual.length);
        for (int ix = 0; ix < (actual.length); ix++) {
            TestCase.assertEquals(expected[ix], actual[ix]);
        }
    }
}

