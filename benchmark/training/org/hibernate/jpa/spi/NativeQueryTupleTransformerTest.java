package org.hibernate.jpa.spi;


import javax.persistence.Tuple;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Maksym Symonov
 */
public class NativeQueryTupleTransformerTest {
    private final NativeQueryTupleTransformer nativeQueryTupleTransformer = new NativeQueryTupleTransformer();

    @Test
    public void nullValueIsExtractedFromTuple() {
        final Tuple tuple = ((Tuple) (nativeQueryTupleTransformer.transformTuple(new Object[]{ 1L, null }, new String[]{ "id", "value" })));
        Assert.assertEquals(1L, tuple.get("id"));
        Assert.assertNull(tuple.get("value"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void missingAliasCausesExceptionWhenIsExtractedFromTuple() {
        final Tuple tuple = ((Tuple) (nativeQueryTupleTransformer.transformTuple(new Object[]{ 1L, null }, new String[]{ "id", "value" })));
        tuple.get("unknownAlias");
    }
}

