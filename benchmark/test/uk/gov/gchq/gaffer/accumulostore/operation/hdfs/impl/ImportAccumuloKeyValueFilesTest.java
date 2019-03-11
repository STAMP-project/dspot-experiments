package uk.gov.gchq.gaffer.accumulostore.operation.hdfs.impl;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.accumulostore.operation.hdfs.operation.ImportAccumuloKeyValueFiles;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class ImportAccumuloKeyValueFilesTest extends OperationTest<ImportAccumuloKeyValueFiles> {
    private static final String INPUT_DIRECTORY = "/input";

    private static final String FAIL_DIRECTORY = "/fail";

    private static final String TEST_OPTION_KEY = "testOption";

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final ImportAccumuloKeyValueFiles op = new ImportAccumuloKeyValueFiles();
        op.setInputPath(ImportAccumuloKeyValueFilesTest.INPUT_DIRECTORY);
        op.setFailurePath(ImportAccumuloKeyValueFilesTest.FAIL_DIRECTORY);
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ImportAccumuloKeyValueFiles deserialisedOp = JSONSerialiser.deserialise(json, ImportAccumuloKeyValueFiles.class);
        // Then
        Assert.assertEquals(ImportAccumuloKeyValueFilesTest.INPUT_DIRECTORY, deserialisedOp.getInputPath());
        Assert.assertEquals(ImportAccumuloKeyValueFilesTest.FAIL_DIRECTORY, deserialisedOp.getFailurePath());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final ImportAccumuloKeyValueFiles importAccumuloKeyValueFiles = new ImportAccumuloKeyValueFiles.Builder().inputPath(ImportAccumuloKeyValueFilesTest.INPUT_DIRECTORY).failurePath(ImportAccumuloKeyValueFilesTest.FAIL_DIRECTORY).option(ImportAccumuloKeyValueFilesTest.TEST_OPTION_KEY, "true").build();
        // Then
        Assert.assertEquals(ImportAccumuloKeyValueFilesTest.INPUT_DIRECTORY, importAccumuloKeyValueFiles.getInputPath());
        Assert.assertEquals(ImportAccumuloKeyValueFilesTest.FAIL_DIRECTORY, importAccumuloKeyValueFiles.getFailurePath());
        Assert.assertEquals("true", importAccumuloKeyValueFiles.getOption(ImportAccumuloKeyValueFilesTest.TEST_OPTION_KEY));
    }
}

