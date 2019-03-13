package com.baeldung.file;


import com.baeldung.util.StreamUtils;
import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static java.nio.file.Files.write;


public class FilesManualTest {
    public static final String fileName = "src/main/resources/countries.properties";

    @Test
    public void whenAppendToFileUsingGuava_thenCorrect() throws IOException {
        File file = new File(FilesManualTest.fileName);
        CharSink chs = Files.asCharSink(file, Charsets.UTF_8, FileWriteMode.APPEND);
        chs.write("Spain\r\n");
        assertThat(StreamUtils.getStringFromInputStream(new FileInputStream(FilesManualTest.fileName))).isEqualTo(("UK\r\n" + (("US\r\n" + "Germany\r\n") + "Spain\r\n")));
    }

    @Test
    public void whenAppendToFileUsingFiles_thenCorrect() throws IOException {
        write(Paths.get(FilesManualTest.fileName), "Spain\r\n".getBytes(), StandardOpenOption.APPEND);
        assertThat(StreamUtils.getStringFromInputStream(new FileInputStream(FilesManualTest.fileName))).isEqualTo(("UK\r\n" + (("US\r\n" + "Germany\r\n") + "Spain\r\n")));
    }

    @Test
    public void whenAppendToFileUsingFileUtils_thenCorrect() throws IOException {
        File file = new File(FilesManualTest.fileName);
        FileUtils.writeStringToFile(file, "Spain\r\n", StandardCharsets.UTF_8, true);
        assertThat(StreamUtils.getStringFromInputStream(new FileInputStream(FilesManualTest.fileName))).isEqualTo(("UK\r\n" + (("US\r\n" + "Germany\r\n") + "Spain\r\n")));
    }

    @Test
    public void whenAppendToFileUsingFileOutputStream_thenCorrect() throws Exception {
        FileOutputStream fos = new FileOutputStream(FilesManualTest.fileName, true);
        fos.write("Spain\r\n".getBytes());
        fos.close();
        assertThat(StreamUtils.getStringFromInputStream(new FileInputStream(FilesManualTest.fileName))).isEqualTo(("UK\r\n" + (("US\r\n" + "Germany\r\n") + "Spain\r\n")));
    }

    @Test
    public void whenAppendToFileUsingFileWriter_thenCorrect() throws IOException {
        FileWriter fw = new FileWriter(FilesManualTest.fileName, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write("Spain");
        bw.newLine();
        bw.close();
        assertThat(StreamUtils.getStringFromInputStream(new FileInputStream(FilesManualTest.fileName))).isEqualTo(("UK\r\n" + (("US\r\n" + "Germany\r\n") + "Spain\r\n")));
    }
}

