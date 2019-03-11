package com.baeldung.stream;


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class OutputStreamExamplesUnitTest {
    StringBuilder filePath = new StringBuilder();

    @Test
    public void givenOutputStream_whenWriteSingleByteCalled_thenOutputCreated() throws IOException {
        final File file = new File(filePath.toString());
        OutputStreamExamples examples = new OutputStreamExamples();
        examples.fileOutputStreamByteSingle(filePath.toString(), "Hello World!");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void givenOutputStream_whenWriteByteSequenceCalled_thenOutputCreated() throws IOException {
        final File file = new File(filePath.toString());
        OutputStreamExamples examples = new OutputStreamExamples();
        examples.fileOutputStreamByteSequence(filePath.toString(), "Hello World!");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void givenOutputStream_whenWriteByteSubSequenceCalled_thenOutputCreated() throws IOException {
        final File file = new File(filePath.toString());
        OutputStreamExamples examples = new OutputStreamExamples();
        examples.fileOutputStreamByteSubSequence(filePath.toString(), "Hello World!");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void givenBufferedOutputStream_whenCalled_thenOutputCreated() throws IOException {
        final File file = new File(filePath.toString());
        OutputStreamExamples examples = new OutputStreamExamples();
        examples.bufferedOutputStream(filePath.toString(), "Hello", "World!");
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void givenOutputStreamWriter_whenCalled_thenOutputCreated() throws IOException {
        final File file = new File(filePath.toString());
        OutputStreamExamples examples = new OutputStreamExamples();
        examples.outputStreamWriter(filePath.toString(), "Hello World!");
        Assert.assertTrue(file.exists());
        file.delete();
    }
}

