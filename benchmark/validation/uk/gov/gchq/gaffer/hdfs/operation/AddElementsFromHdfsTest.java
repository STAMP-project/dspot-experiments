/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.hdfs.operation;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.mapreduce.Partitioner;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.TextJobInitialiser;
import uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.MapperGenerator;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class AddElementsFromHdfsTest extends OperationTest<AddElementsFromHdfs> {
    private static final String ADD_ELEMENTS_FROM_HDFS_JSON = String.format(("{%n" + ((("  \"class\" : \"uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs\",%n" + "  \"inputMapperPairs\" : {%n    \"TestInput\" : \"uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.MapperGenerator\"%n  } ,%n") + "  \"outputPath\" : \"TestOutput\"%n") + "}")));

    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final Map<String, String> inputMapperPairs = new HashMap<>();
        inputMapperPairs.put("inputPath", MapperGenerator.class.getName());
        final AddElementsFromHdfs addElements = new AddElementsFromHdfs.Builder().inputMapperPairs(inputMapperPairs).outputPath("outputPath").failurePath("failurePath").jobInitialiser(new TextJobInitialiser()).partitioner(Partitioner.class).mappers(5).reducers(10).splitsFilePath("/path/to/splits/file").useProvidedSplits(false).build();
        // When
        String json = new String(JSONSerialiser.serialise(addElements, true));
        // Then
        JsonAssert.assertEquals(String.format(("{%n" + ((((((((((("  \"class\" : \"uk.gov.gchq.gaffer.hdfs.operation.AddElementsFromHdfs\",%n" + "  \"failurePath\" : \"failurePath\",%n") + "  \"inputMapperPairs\" : { \"inputPath\" :\"uk.gov.gchq.gaffer.hdfs.operation.mapper.generator.MapperGenerator\"},%n") + "  \"outputPath\" : \"outputPath\",%n") + "  \"jobInitialiser\" : {%n") + "    \"class\" : \"uk.gov.gchq.gaffer.hdfs.operation.handler.job.initialiser.TextJobInitialiser\"%n") + "  },%n") + "  \"numMapTasks\" : 5,%n") + "  \"numReduceTasks\" : 10,%n") + "  \"splitsFilePath\" : \"/path/to/splits/file\",%n") + "  \"partitioner\" : \"org.apache.hadoop.mapreduce.Partitioner\"%n") + "}"))), json);
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        final Map<String, String> inputMapperPairs = new HashMap<>();
        inputMapperPairs.put("inputPath", MapperGenerator.class.getName());
        final AddElementsFromHdfs addElements = new AddElementsFromHdfs.Builder().inputMapperPairs(inputMapperPairs).outputPath("output").failurePath("fail").mappers(10).reducers(20).validate(true).option("testOption", "true").build();
        Assert.assertEquals("true", addElements.getOption("testOption"));
        Assert.assertTrue(addElements.isValidate());
        Assert.assertEquals("fail", addElements.getFailurePath());
        Assert.assertEquals(new Integer(10), addElements.getNumMapTasks());
        Assert.assertEquals(new Integer(20), addElements.getNumReduceTasks());
        Assert.assertEquals("output", addElements.getOutputPath());
        Assert.assertEquals(MapperGenerator.class.getName(), addElements.getInputMapperPairs().get("inputPath"));
    }

    @Test
    public void shouldSerialisePopulatedAddElementsFromHdfsOperation() throws IOException {
        // Given
        final Map<String, String> inputMapperPairs = new HashMap<>();
        inputMapperPairs.put("TestInput", MapperGenerator.class.getName());
        final AddElementsFromHdfs addElementsFromHdfs = getTestObject();
        addElementsFromHdfs.setInputMapperPairs(inputMapperPairs);
        addElementsFromHdfs.setOutputPath("TestOutput");
        // When
        final String json = new String(JSONSerialiser.serialise(addElementsFromHdfs, true));
        // Then
        JsonAssert.assertEquals(AddElementsFromHdfsTest.ADD_ELEMENTS_FROM_HDFS_JSON, json);
    }

    @Test
    public void shouldDeserialiseAddElementsOperation() throws IOException {
        // When
        final AddElementsFromHdfs addElementsFromHdfs = JSONSerialiser.deserialise(AddElementsFromHdfsTest.ADD_ELEMENTS_FROM_HDFS_JSON.getBytes(), AddElementsFromHdfs.class);
        // Then
        final Map<String, String> inputMapperPairs = new HashMap<>();
        inputMapperPairs.put("TestInput", MapperGenerator.class.getName());
        Assert.assertEquals(inputMapperPairs, addElementsFromHdfs.getInputMapperPairs());
        Assert.assertEquals("TestOutput", addElementsFromHdfs.getOutputPath());
    }
}

