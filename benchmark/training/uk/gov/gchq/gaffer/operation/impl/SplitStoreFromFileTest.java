package uk.gov.gchq.gaffer.operation.impl;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class SplitStoreFromFileTest extends OperationTest<SplitStoreFromFile> {
    private static final String INPUT_DIRECTORY = "/input";

    private static final String TEST_OPTION_KEY = "testOption";

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final SplitStoreFromFile op = new SplitStoreFromFile();
        op.setInputPath(SplitStoreFromFileTest.INPUT_DIRECTORY);
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final SplitStoreFromFile deserialisedOp = JSONSerialiser.deserialise(json, SplitStoreFromFile.class);
        // Then
        Assert.assertEquals(SplitStoreFromFileTest.INPUT_DIRECTORY, deserialisedOp.getInputPath());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final SplitStoreFromFile splitTable = new SplitStoreFromFile.Builder().inputPath(SplitStoreFromFileTest.INPUT_DIRECTORY).option(SplitStoreFromFileTest.TEST_OPTION_KEY, "true").build();
        Assert.assertEquals(SplitStoreFromFileTest.INPUT_DIRECTORY, splitTable.getInputPath());
        Assert.assertEquals("true", splitTable.getOption(SplitStoreFromFileTest.TEST_OPTION_KEY));
    }
}

