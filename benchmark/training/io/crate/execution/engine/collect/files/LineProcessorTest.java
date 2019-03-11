package io.crate.execution.engine.collect.files;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class LineProcessorTest {
    private LineProcessor subjectUnderTest;

    private URI uri;

    private BufferedReader bufferedReader;

    @Test
    public void readFirstLine_givenFileExtensionIsCsv_AndDefaultJSONFileFormat_thenReadsLine() throws IOException, URISyntaxException {
        uri = new URI("file.csv");
        Reader reader = new StringReader("some/string");
        bufferedReader = new BufferedReader(reader);
        subjectUnderTest.readFirstLine(uri, JSON, bufferedReader);
        Assert.assertThat(bufferedReader.readLine(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void readFirstLine_givenFileFormatIsCsv_thenReadsLine() throws IOException, URISyntaxException {
        uri = new URI("file.any");
        Reader reader = new StringReader("some/string");
        bufferedReader = new BufferedReader(reader);
        subjectUnderTest.readFirstLine(uri, CSV, bufferedReader);
        Assert.assertThat(bufferedReader.readLine(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void readFirstLine_givenFileExtensionIsJson__AndDefaultJSONFileFormat_thenDoesNotReadLine() throws IOException, URISyntaxException {
        uri = new URI("file.json");
        Reader reader = new StringReader("some/string");
        bufferedReader = new BufferedReader(reader);
        subjectUnderTest.readFirstLine(uri, JSON, bufferedReader);
        Assert.assertThat(bufferedReader.readLine(), Matchers.is("some/string"));
    }

    @Test
    public void readFirstLine_givenFileFormatIsJson_thenDoesNotReadLine() throws IOException, URISyntaxException {
        uri = new URI("file.any");
        Reader reader = new StringReader("some/string");
        bufferedReader = new BufferedReader(reader);
        subjectUnderTest.readFirstLine(uri, JSON, bufferedReader);
        Assert.assertThat(bufferedReader.readLine(), Matchers.is("some/string"));
    }
}

