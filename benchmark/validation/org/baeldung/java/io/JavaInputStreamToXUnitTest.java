package org.baeldung.java.io;


import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.io.Files.write;


@SuppressWarnings("unused")
public class JavaInputStreamToXUnitTest {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int DEFAULT_SIZE = 1500000;

    // tests - InputStream to String
    @Test
    public final void givenUsingJava5_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        final StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != (-1)) {
                textBuilder.append(((char) (c)));
            } 
        }
        Assert.assertEquals(textBuilder.toString(), originalString);
    }

    @Test
    public final void givenUsingJava7_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());// exampleString.getBytes(StandardCharsets.UTF_8);

        // When
        String text;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
        Assert.assertThat(text, Matchers.equalTo(originalString));
    }

    @Test
    public final void givenUsingGuava_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        final ByteSource byteSource = new ByteSource() {
            @Override
            public final InputStream openStream() throws IOException {
                return inputStream;
            }
        };
        final String text = byteSource.asCharSource(Charsets.UTF_8).read();
        Assert.assertThat(text, Matchers.equalTo(originalString));
    }

    @Test
    public final void givenUsingGuavaAndJava7_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        // When
        String text;
        try (final Reader reader = new InputStreamReader(inputStream)) {
            text = CharStreams.toString(reader);
        }
        Assert.assertThat(text, Matchers.equalTo(originalString));
    }

    @Test
    public final void givenUsingCommonsIo_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        // When
        final String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());
        Assert.assertThat(text, Matchers.equalTo(originalString));
    }

    @Test
    public final void givenUsingCommonsIoWithCopy_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        // When
        final StringWriter writer = new StringWriter();
        final String encoding = StandardCharsets.UTF_8.name();
        IOUtils.copy(inputStream, writer, encoding);
        Assert.assertThat(writer.toString(), Matchers.equalTo(originalString));
    }

    @Test
    public final void givenUsingTempFile_whenConvertingAnInputStreamToAString_thenCorrect() throws IOException {
        final String originalString = RandomStringUtils.randomAlphabetic(JavaInputStreamToXUnitTest.DEFAULT_SIZE);
        final InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        // When
        Path tempFile = Files.createTempDirectory("").resolve(((UUID.randomUUID().toString()) + ".tmp"));
        Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
        String result = new String(Files.readAllBytes(tempFile));
        Assert.assertThat(result, Matchers.equalTo(originalString));
    }

    // tests - InputStream to byte[]
    @Test
    public final void givenUsingPlainJavaOnFixedSizeStream_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
        final InputStream initialStream = new ByteArrayInputStream(new byte[]{ 0, 1, 2 });
        final byte[] targetArray = new byte[initialStream.available()];
        initialStream.read(targetArray);
    }

    @Test
    public final void givenUsingPlainJavaOnUnknownSizeStream_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
        final InputStream is = new ByteArrayInputStream(new byte[]{ 0, 1, 2 });
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        final byte[] data = new byte[1024];
        while ((nRead = is.read(data, 0, data.length)) != (-1)) {
            buffer.write(data, 0, nRead);
        } 
        buffer.flush();
        final byte[] byteArray = buffer.toByteArray();
    }

    @Test
    public final void givenUsingGuava_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
        final InputStream initialStream = ByteSource.wrap(new byte[]{ 0, 1, 2 }).openStream();
        final byte[] targetArray = ByteStreams.toByteArray(initialStream);
    }

    @Test
    public final void givenUsingCommonsIO_whenConvertingAnInputStreamToAByteArray_thenCorrect() throws IOException {
        final InputStream initialStream = new ByteArrayInputStream(new byte[]{ 0, 1, 2 });
        final byte[] targetArray = IOUtils.toByteArray(initialStream);
    }

    // tests - InputStream to File
    @Test
    public final void whenConvertingToFile_thenCorrect() throws IOException {
        final InputStream initialStream = new FileInputStream(new File("src/test/resources/sample.txt"));
        final byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        final File targetFile = new File("src/test/resources/targetFile.tmp");
        final OutputStream outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        IOUtils.closeQuietly(initialStream);
        IOUtils.closeQuietly(outStream);
    }

    @Test
    public final void whenConvertingInProgressToFile_thenCorrect() throws IOException {
        final InputStream initialStream = new FileInputStream(new File("src/test/resources/sample.txt"));
        final File targetFile = new File("src/test/resources/targetFile.tmp");
        final OutputStream outStream = new FileOutputStream(targetFile);
        final byte[] buffer = new byte[8 * 1024];
        int bytesRead;
        while ((bytesRead = initialStream.read(buffer)) != (-1)) {
            outStream.write(buffer, 0, bytesRead);
        } 
        IOUtils.closeQuietly(initialStream);
        IOUtils.closeQuietly(outStream);
    }

    @Test
    public final void whenConvertingAnInProgressInputStreamToFile_thenCorrect2() throws IOException {
        final InputStream initialStream = new FileInputStream(new File("src/test/resources/sample.txt"));
        final File targetFile = new File("src/test/resources/targetFile.tmp");
        Files.copy(initialStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(initialStream);
    }

    @Test
    public final void whenConvertingInputStreamToFile_thenCorrect3() throws IOException {
        final InputStream initialStream = new FileInputStream(new File("src/test/resources/sample.txt"));
        final byte[] buffer = new byte[initialStream.available()];
        initialStream.read(buffer);
        final File targetFile = new File("src/test/resources/targetFile.tmp");
        write(buffer, targetFile);
        IOUtils.closeQuietly(initialStream);
    }

    @Test
    public final void whenConvertingInputStreamToFile_thenCorrect4() throws IOException {
        final InputStream initialStream = FileUtils.openInputStream(new File("src/test/resources/sample.txt"));
        final File targetFile = new File("src/test/resources/targetFile.tmp");
        FileUtils.copyInputStreamToFile(initialStream, targetFile);
    }

    @Test
    public final void givenUsingPlainJava_whenConvertingAnInputStreamToString_thenCorrect() throws IOException {
        String originalString = RandomStringUtils.randomAlphabetic(8);
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != (-1)) {
            buffer.write(data, 0, nRead);
        } 
        buffer.flush();
        byte[] byteArray = buffer.toByteArray();
        String text = new String(byteArray, StandardCharsets.UTF_8);
        Assert.assertThat(text, Matchers.equalTo(originalString));
    }
}

