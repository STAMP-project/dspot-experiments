package com.baeldung.crunch;


import org.apache.crunch.PCollection;
import org.apache.crunch.Pipeline;
import org.apache.crunch.Source;
import org.apache.crunch.impl.mem.MemPipeline;
import org.apache.crunch.io.From;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;


public class MemPipelineUnitTest {
    private static final String INPUT_FILE_PATH = "src/test/resources/crunch/input.txt";

    @Test
    public void givenPipeLineAndSource_whenSourceRead_thenExpectedNumberOfRecordsRead() {
        Pipeline pipeline = MemPipeline.getInstance();
        Source<String> source = From.textFile(MemPipelineUnitTest.INPUT_FILE_PATH);
        PCollection<String> lines = pipeline.read(source);
        Assertions.assertEquals(21, lines.asCollection().getValue().size());
    }

    @Test
    public void givenPipeLine_whenTextFileRead_thenExpectedNumberOfRecordsRead() {
        Pipeline pipeline = MemPipeline.getInstance();
        PCollection<String> lines = pipeline.readTextFile(MemPipelineUnitTest.INPUT_FILE_PATH);
        Assertions.assertEquals(21, lines.asCollection().getValue().size());
    }
}

