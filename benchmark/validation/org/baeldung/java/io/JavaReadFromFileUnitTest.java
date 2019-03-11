package org.baeldung.java.io;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StreamTokenizer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JavaReadFromFileUnitTest {
    private static final Logger LOG = LoggerFactory.getLogger(JavaReadFromFileUnitTest.class);

    @Test
    public void whenReadWithBufferedReader_thenCorrect() throws IOException {
        final String expected_value = "Hello world";
        final BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/test_read.in"));
        final String currentLine = reader.readLine();
        reader.close();
        Assert.assertEquals(expected_value, currentLine);
    }

    @Test
    public void whenReadWithScanner_thenCorrect() throws IOException {
        final Scanner scanner = new Scanner(new File("src/test/resources/test_read1.in"));
        scanner.useDelimiter(" ");
        Assert.assertTrue(scanner.hasNext());
        Assert.assertEquals("Hello", scanner.next());
        Assert.assertEquals("world", scanner.next());
        Assert.assertEquals(1, scanner.nextInt());
        scanner.close();
    }

    @Test
    public void whenReadWithScannerTwoDelimiters_thenCorrect() throws IOException {
        final Scanner scanner = new Scanner(new File("src/test/resources/test_read2.in"));
        scanner.useDelimiter(",| ");
        Assert.assertTrue(scanner.hasNextInt());
        Assert.assertEquals(2, scanner.nextInt());
        Assert.assertEquals(3, scanner.nextInt());
        Assert.assertEquals(4, scanner.nextInt());
        scanner.close();
    }

    @Test
    public void whenReadWithStreamTokenizer_thenCorrectTokens() throws IOException {
        final FileReader reader = new FileReader("src/test/resources/test_read3.in");
        final StreamTokenizer tokenizer = new StreamTokenizer(reader);
        tokenizer.nextToken();
        Assert.assertEquals(StreamTokenizer.TT_WORD, tokenizer.ttype);
        Assert.assertEquals("Hello", tokenizer.sval);
        tokenizer.nextToken();
        Assert.assertEquals(StreamTokenizer.TT_NUMBER, tokenizer.ttype);
        Assert.assertEquals(1, tokenizer.nval, 1.0E-7);
        tokenizer.nextToken();
        Assert.assertEquals(StreamTokenizer.TT_EOF, tokenizer.ttype);
        reader.close();
    }

    @Test
    public void whenReadWithDataInputStream_thenCorrect() throws IOException {
        final String expected_value = "Hello";
        String result;
        final DataInputStream reader = new DataInputStream(new FileInputStream("src/test/resources/test_read4.in"));
        result = reader.readUTF();
        reader.close();
        Assert.assertEquals(expected_value, result);
    }

    @Test
    public void whenReadFileContentsIntoString_thenCorrect() throws IOException {
        final String expected_value = "Hello world \n Test line \n";
        final BufferedReader reader = new BufferedReader(new FileReader("src/test/resources/test_read8.in"));
        final StringBuilder builder = new StringBuilder();
        String currentLine = reader.readLine();
        while (currentLine != null) {
            builder.append(currentLine);
            builder.append("\n");
            currentLine = reader.readLine();
        } 
        reader.close();
        Assert.assertEquals(expected_value, builder.toString());
    }

    @Test
    public void whenReadWithFileChannel_thenCorrect() throws IOException {
        final String expected_value = "Hello world";
        final RandomAccessFile reader = new RandomAccessFile("src/test/resources/test_read.in", "r");
        final FileChannel channel = reader.getChannel();
        int bufferSize = 1024;
        if (bufferSize > (channel.size())) {
            bufferSize = ((int) (channel.size()));
        }
        final ByteBuffer buff = ByteBuffer.allocate(bufferSize);
        channel.read(buff);
        buff.flip();
        Assert.assertEquals(expected_value, new String(buff.array()));
        channel.close();
        reader.close();
    }

    @Test
    public void whenReadSmallFileJava7_thenCorrect() throws IOException {
        final String expected_value = "Hello world";
        final Path path = Paths.get("src/test/resources/test_read.in");
        final String read = Files.readAllLines(path, Charset.defaultCharset()).get(0);
        Assert.assertEquals(expected_value, read);
    }

    @Test
    public void whenReadLargeFileJava7_thenCorrect() throws IOException {
        final String expected_value = "Hello world";
        final Path path = Paths.get("src/test/resources/test_read.in");
        final BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
        final String line = reader.readLine();
        Assert.assertEquals(expected_value, line);
    }
}

