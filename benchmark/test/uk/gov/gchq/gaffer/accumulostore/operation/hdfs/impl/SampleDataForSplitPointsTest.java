package uk.gov.gchq.gaffer.accumulostore.operation.hdfs.impl;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hdfs.operation.SampleDataForSplitPoints;
import uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.MapperGenerator;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class SampleDataForSplitPointsTest extends OperationTest<SampleDataForSplitPoints> {
    private static final String INPUT_DIRECTORY = "/input";

    private static final String TEST_OPTION_KEY = "testOption";

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final Map<String, String> inputMapperPairs = new HashMap<>();
        inputMapperPairs.put(SampleDataForSplitPointsTest.INPUT_DIRECTORY, MapperGenerator.class.getName());
        final String resultPath = "/result";
        final SampleDataForSplitPoints op = new SampleDataForSplitPoints();
        op.setInputMapperPairs(inputMapperPairs);
        op.setValidate(true);
        op.setProportionToSample(0.1F);
        op.setSplitsFilePath(resultPath);
        op.setNumMapTasks(5);
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final SampleDataForSplitPoints deserialisedOp = JSONSerialiser.deserialise(json, SampleDataForSplitPoints.class);
        // Then
        Assert.assertEquals(MapperGenerator.class.getName(), deserialisedOp.getInputMapperPairs().get(SampleDataForSplitPointsTest.INPUT_DIRECTORY));
        Assert.assertEquals(resultPath, deserialisedOp.getSplitsFilePath());
        Assert.assertTrue(deserialisedOp.isValidate());
        Assert.assertEquals(0.1F, deserialisedOp.getProportionToSample(), 1);
        Assert.assertEquals(new Integer(5), deserialisedOp.getNumMapTasks());
        Assert.assertEquals(new Integer(1), deserialisedOp.getNumReduceTasks());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final SampleDataForSplitPoints sampleDataForSplitPoints = new SampleDataForSplitPoints.Builder().addInputMapperPair(SampleDataForSplitPointsTest.INPUT_DIRECTORY, MapperGenerator.class.getName()).splitsFilePath("/test").proportionToSample(0.1F).mappers(5).validate(true).option(SampleDataForSplitPointsTest.TEST_OPTION_KEY, "true").build();
        Assert.assertEquals(MapperGenerator.class.getName(), sampleDataForSplitPoints.getInputMapperPairs().get(SampleDataForSplitPointsTest.INPUT_DIRECTORY));
        Assert.assertEquals("true", sampleDataForSplitPoints.getOption(SampleDataForSplitPointsTest.TEST_OPTION_KEY));
        Assert.assertEquals("/test", sampleDataForSplitPoints.getSplitsFilePath());
        Assert.assertTrue(sampleDataForSplitPoints.isValidate());
        Assert.assertEquals(0.1F, sampleDataForSplitPoints.getProportionToSample(), 1);
        Assert.assertEquals(new Integer(5), sampleDataForSplitPoints.getNumMapTasks());
    }

    @Test
    public void expectIllegalArgumentExceptionWhenTryingToSetReducers() {
        final SampleDataForSplitPoints op = getTestObject();
        try {
            op.setNumReduceTasks(10);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldShallowCloneOperationWithMinAndMaxMappers() {
        // Given
        final SampleDataForSplitPoints sampleDataForSplitPoints = new SampleDataForSplitPoints.Builder().addInputMapperPair(SampleDataForSplitPointsTest.INPUT_DIRECTORY, MapperGenerator.class.getName()).splitsFilePath("/test").proportionToSample(0.1F).maxMappers(10).minMappers(2).validate(true).option(SampleDataForSplitPointsTest.TEST_OPTION_KEY, "true").build();
        // When
        SampleDataForSplitPoints clone = sampleDataForSplitPoints.shallowClone();
        // Then
        Assert.assertEquals(MapperGenerator.class.getName(), clone.getInputMapperPairs().get(SampleDataForSplitPointsTest.INPUT_DIRECTORY));
        Assert.assertEquals("true", clone.getOption(SampleDataForSplitPointsTest.TEST_OPTION_KEY));
        Assert.assertEquals("/test", clone.getSplitsFilePath());
        Assert.assertTrue(clone.isValidate());
        Assert.assertEquals(0.1F, clone.getProportionToSample(), 1);
        Assert.assertEquals(new Integer(10), clone.getMaxMapTasks());
        Assert.assertEquals(new Integer(2), clone.getMinMapTasks());
    }
}

