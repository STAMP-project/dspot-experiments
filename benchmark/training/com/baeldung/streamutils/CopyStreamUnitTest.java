package com.baeldung.streamutils;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.StreamUtils;


public class CopyStreamUnitTest {
    @Test
    public void whenCopyInputStreamToOutputStream_thenCorrect() throws IOException {
        String inputFileName = "src/test/resources/input.txt";
        String outputFileName = "src/test/resources/output.txt";
        File outputFile = new File(outputFileName);
        InputStream in = new FileInputStream(inputFileName);
        OutputStream out = new FileOutputStream(outputFileName);
        StreamUtils.copy(in, out);
        Assert.assertTrue(outputFile.exists());
        String inputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(inputFileName));
        String outputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(outputFileName));
        Assert.assertEquals(inputFileContent, outputFileContent);
    }

    @Test
    public void whenCopyRangeOfInputStreamToOutputStream_thenCorrect() throws IOException {
        String inputFileName = "src/test/resources/input.txt";
        String outputFileName = "src/test/resources/output.txt";
        File outputFile = new File(outputFileName);
        InputStream in = new FileInputStream(inputFileName);
        OutputStream out = new FileOutputStream(outputFileName);
        StreamUtils.copyRange(in, out, 1, 10);
        Assert.assertTrue(outputFile.exists());
        String inputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(inputFileName));
        String outputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(outputFileName));
        Assert.assertEquals(inputFileContent.substring(1, 11), outputFileContent);
    }

    @Test
    public void whenCopyStringToOutputStream_thenCorrect() throws IOException {
        String string = "Should be copied to OutputStream.";
        String outputFileName = "src/test/resources/output.txt";
        File outputFile = new File(outputFileName);
        OutputStream out = new FileOutputStream("src/test/resources/output.txt");
        StreamUtils.copy(string, StandardCharsets.UTF_8, out);
        Assert.assertTrue(outputFile.exists());
        String outputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(outputFileName));
        Assert.assertEquals(outputFileContent, string);
    }

    @Test
    public void whenCopyInputStreamToString_thenCorrect() throws IOException {
        String inputFileName = "src/test/resources/input.txt";
        InputStream is = new FileInputStream(inputFileName);
        String content = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        String inputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(inputFileName));
        Assert.assertEquals(inputFileContent, content);
    }

    @Test
    public void whenCopyByteArrayToOutputStream_thenCorrect() throws IOException {
        String outputFileName = "src/test/resources/output.txt";
        String string = "Should be copied to OutputStream.";
        byte[] byteArray = string.getBytes();
        OutputStream out = new FileOutputStream("src/test/resources/output.txt");
        StreamUtils.copy(byteArray, out);
        String outputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(outputFileName));
        Assert.assertEquals(outputFileContent, string);
    }

    @Test
    public void whenCopyInputStreamToByteArray_thenCorrect() throws IOException {
        String inputFileName = "src/test/resources/input.txt";
        InputStream in = new FileInputStream(inputFileName);
        byte[] out = StreamUtils.copyToByteArray(in);
        String content = new String(out);
        String inputFileContent = CopyStream.getStringFromInputStream(new FileInputStream(inputFileName));
        Assert.assertEquals(inputFileContent, content);
    }
}

