package uk.gov.gchq.gaffer.operation.impl;


import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class SplitStoreFromIterableTest extends OperationTest<SplitStoreFromIterable> {
    private static final String TEST_OPTION_KEY = "testOption";

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final SplitStoreFromIterable<String> op = new SplitStoreFromIterable.Builder<String>().input("1", "2", "3").option(SplitStoreFromIterableTest.TEST_OPTION_KEY, "false").build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final SplitStoreFromIterable deserialisedOp = JSONSerialiser.deserialise(json, SplitStoreFromIterable.class);
        // Then
        Assert.assertEquals(Arrays.asList("1", "2", "3"), deserialisedOp.getInput());
        Assert.assertEquals("false", deserialisedOp.getOptions().get(SplitStoreFromIterableTest.TEST_OPTION_KEY));
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final SplitStoreFromIterable<String> op = new SplitStoreFromIterable.Builder<String>().input("1", "2", "3").option(SplitStoreFromIterableTest.TEST_OPTION_KEY, "false").build();
        Assert.assertEquals(Arrays.asList("1", "2", "3"), op.getInput());
        Assert.assertEquals("false", op.getOptions().get(SplitStoreFromIterableTest.TEST_OPTION_KEY));
    }
}

