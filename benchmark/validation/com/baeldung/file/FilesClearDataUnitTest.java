package com.baeldung.file;


import com.baeldung.util.StreamUtils;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static com.google.common.io.Files.write;


public class FilesClearDataUnitTest {
    public static final String FILE_PATH = "src/test/resources/fileexample.txt";

    @Test
    public void givenExistingFile_whenDeleteContentUsingPrintWritter_thenEmptyFile() throws IOException {
        PrintWriter writer = new PrintWriter(FilesClearDataUnitTest.FILE_PATH);
        writer.print("");
        writer.close();
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingPrintWritterWithougObject_thenEmptyFile() throws IOException {
        new PrintWriter(FilesClearDataUnitTest.FILE_PATH).close();
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingFileWriter_thenEmptyFile() throws IOException {
        new FileWriter(FilesClearDataUnitTest.FILE_PATH, false).close();
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingFileOutputStream_thenEmptyFile() throws IOException {
        new FileOutputStream(FilesClearDataUnitTest.FILE_PATH).close();
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingFileUtils_thenEmptyFile() throws IOException {
        FileUtils.write(new File(FilesClearDataUnitTest.FILE_PATH), "", Charset.defaultCharset());
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingNIOFiles_thenEmptyFile() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(FilesClearDataUnitTest.FILE_PATH));
        writer.write("");
        writer.flush();
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingNIOFileChannel_thenEmptyFile() throws IOException {
        FileChannel.open(Paths.get(FilesClearDataUnitTest.FILE_PATH), StandardOpenOption.WRITE).truncate(0).close();
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }

    @Test
    public void givenExistingFile_whenDeleteContentUsingGuava_thenEmptyFile() throws IOException {
        File file = new File(FilesClearDataUnitTest.FILE_PATH);
        byte[] empty = new byte[0];
        write(empty, file);
        Assertions.assertEquals(0, StreamUtils.getStringFromInputStream(new FileInputStream(FilesClearDataUnitTest.FILE_PATH)).length());
    }
}

