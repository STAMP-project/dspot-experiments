package uk.gov.gchq.gaffer.operation.impl;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class SplitStoreTest extends OperationTest<SplitStore> {
    private static final String INPUT_DIRECTORY = "/input";

    private static final String TEST_OPTION_KEY = "testOption";

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final SplitStore op = new SplitStore();
        op.setInputPath(SplitStoreTest.INPUT_DIRECTORY);
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final SplitStore deserialisedOp = JSONSerialiser.deserialise(json, SplitStore.class);
        // Then
        Assert.assertEquals(SplitStoreTest.INPUT_DIRECTORY, deserialisedOp.getInputPath());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final SplitStore splitTable = new SplitStore.Builder().inputPath(SplitStoreTest.INPUT_DIRECTORY).option(SplitStoreTest.TEST_OPTION_KEY, "true").build();
        Assert.assertEquals(SplitStoreTest.INPUT_DIRECTORY, splitTable.getInputPath());
        Assert.assertEquals("true", splitTable.getOption(SplitStoreTest.TEST_OPTION_KEY));
    }
}

