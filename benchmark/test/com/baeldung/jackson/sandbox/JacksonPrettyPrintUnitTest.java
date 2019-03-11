package com.baeldung.jackson.sandbox;


import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.File;
import java.io.IOException;
import org.junit.Test;


public class JacksonPrettyPrintUnitTest {
    @Test
    public final void whenDeserializing_thenCorrect() throws JsonParseException, JsonMappingException, IOException {
        // final String fileName = "src/main/resources/example1.json";
        final String fileName = "src/main/resources/example1.json";
        new File(fileName);
        JacksonPrettyPrintUnitTest.printJsonFromFile(fileName);
    }
}

