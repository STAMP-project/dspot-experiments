package com.baeldung.csv;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WriteCsvFileExampleUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(WriteCsvFileExampleUnitTest.class);

    private WriteCsvFileExample csvExample;

    @Test
    public void givenCommaContainingData_whenEscapeSpecialCharacters_stringReturnedInQuotes() {
        String data = "three,two,one";
        String escapedData = csvExample.escapeSpecialCharacters(data);
        String expectedData = "\"three,two,one\"";
        Assert.assertEquals(expectedData, escapedData);
    }

    @Test
    public void givenQuoteContainingData_whenEscapeSpecialCharacters_stringReturnedFormatted() {
        String data = "She said \"Hello\"";
        String escapedData = csvExample.escapeSpecialCharacters(data);
        String expectedData = "\"She said \"\"Hello\"\"\"";
        Assert.assertEquals(expectedData, escapedData);
    }

    @Test
    public void givenNewlineContainingData_whenEscapeSpecialCharacters_stringReturnedInQuotes() {
        String dataNewline = "This contains\na newline";
        String dataCarriageReturn = "This contains\r\na newline and carriage return";
        String escapedDataNl = csvExample.escapeSpecialCharacters(dataNewline);
        String escapedDataCr = csvExample.escapeSpecialCharacters(dataCarriageReturn);
        String expectedData = "This contains a newline";
        Assert.assertEquals(expectedData, escapedDataNl);
        String expectedDataCr = "This contains a newline and carriage return";
        Assert.assertEquals(expectedDataCr, escapedDataCr);
    }

    @Test
    public void givenNonSpecialData_whenEscapeSpecialCharacters_stringReturnedUnchanged() {
        String data = "This is nothing special";
        String returnedData = csvExample.escapeSpecialCharacters(data);
        Assert.assertEquals(data, returnedData);
    }

    @Test
    public void givenDataArray_whenConvertToCSV_thenOutputCreated() throws IOException {
        List<String[]> dataLines = new ArrayList<String[]>();
        dataLines.add(new String[]{ "John", "Doe", "38", "Comment Data\nAnother line of comment data" });
        dataLines.add(new String[]{ "Jane", "Doe, Jr.", "19", "She said \"I\'m being quoted\"" });
        File csvOutputFile = File.createTempFile("exampleOutput", ".csv");
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            dataLines.stream().map(csvExample::convertToCSV).forEach(pw::println);
        } catch (FileNotFoundException e) {
            WriteCsvFileExampleUnitTest.LOG.error(("IOException " + (e.getMessage())));
        }
        Assert.assertTrue(csvOutputFile.exists());
        csvOutputFile.deleteOnExit();
    }
}

