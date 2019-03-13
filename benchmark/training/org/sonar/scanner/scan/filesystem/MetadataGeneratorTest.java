/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.scan.filesystem;


import InputFile.Status.ADDED;
import InputFile.Type.MAIN;
import MetadataGenerator.UTF_32BE;
import MetadataGenerator.UTF_32LE;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.fs.internal.FileMetadata;
import org.sonar.api.utils.PathUtils;


public class MetadataGeneratorTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Mock
    private StatusDetection statusDetection;

    @Mock
    private DefaultModuleFileSystem fs;

    private FileMetadata metadata;

    private MetadataGenerator generator;

    @Test
    public void should_detect_charset_from_BOM() {
        Path basedir = Paths.get("src/test/resources/org/sonar/scanner/scan/filesystem/");
        assertThat(createInputFileWithMetadata(basedir.resolve("without_BOM.txt")).charset()).isEqualTo(StandardCharsets.US_ASCII);
        assertThat(createInputFileWithMetadata(basedir.resolve("UTF-8.txt")).charset()).isEqualTo(StandardCharsets.UTF_8);
        assertThat(createInputFileWithMetadata(basedir.resolve("UTF-16BE.txt")).charset()).isEqualTo(StandardCharsets.UTF_16BE);
        assertThat(createInputFileWithMetadata(basedir.resolve("UTF-16LE.txt")).charset()).isEqualTo(StandardCharsets.UTF_16LE);
        assertThat(createInputFileWithMetadata(basedir.resolve("UTF-32BE.txt")).charset()).isEqualTo(UTF_32BE);
        assertThat(createInputFileWithMetadata(basedir.resolve("UTF-32LE.txt")).charset()).isEqualTo(UTF_32LE);
    }

    @Test
    public void start_with_bom() throws Exception {
        Path tempFile = temp.newFile().toPath();
        FileUtils.write(tempFile.toFile(), "\ufefffoo\nbar\r\nbaz", StandardCharsets.UTF_8, true);
        DefaultInputFile inputFile = createInputFileWithMetadata(tempFile);
        assertThat(inputFile.lines()).isEqualTo(3);
        assertThat(inputFile.nonBlankLines()).isEqualTo(3);
        assertThat(inputFile.hash()).isEqualTo(DigestUtils.md5Hex("foo\nbar\nbaz"));
        assertThat(inputFile.originalLineStartOffsets()).containsOnly(0, 4, 9);
        assertThat(inputFile.originalLineEndOffsets()).containsOnly(3, 7, 12);
    }

    @Test
    public void use_default_charset_if_detection_fails() throws IOException {
        Path tempFile = temp.newFile().toPath();
        byte invalidWindows1252 = ((byte) (129));
        byte[] b = new byte[]{ ((byte) (223)), ((byte) (255)), ((byte) (255)), invalidWindows1252 };
        FileUtils.writeByteArrayToFile(tempFile.toFile(), b);
        DefaultInputFile inputFile = createInputFileWithMetadata(tempFile);
        assertThat(inputFile.charset()).isEqualTo(StandardCharsets.US_ASCII);
    }

    @Test
    public void non_existing_file_should_throw_exception() {
        try {
            createInputFileWithMetadata(Paths.get(""), "non_existing");
            Assert.fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).endsWith(("Unable to read file " + (Paths.get("").resolve("non_existing").toAbsolutePath())));
            assertThat(e.getCause()).isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    public void complete_input_file() throws Exception {
        // file system
        Path baseDir = temp.newFolder().toPath();
        Path srcFile = baseDir.resolve("src/main/java/foo/Bar.java");
        FileUtils.touch(srcFile.toFile());
        FileUtils.write(srcFile.toFile(), "single line");
        // status
        DefaultInputFile inputFile = createInputFileWithMetadata(baseDir, "src/main/java/foo/Bar.java");
        Mockito.when(statusDetection.status("foo", inputFile, "6c1d64c0b3555892fe7273e954f6fb5a")).thenReturn(ADDED);
        assertThat(inputFile.type()).isEqualTo(MAIN);
        assertThat(inputFile.file()).isEqualTo(srcFile.toFile());
        assertThat(inputFile.absolutePath()).isEqualTo(PathUtils.sanitize(srcFile.toAbsolutePath().toString()));
        assertThat(inputFile.key()).isEqualTo("struts:src/main/java/foo/Bar.java");
        assertThat(inputFile.relativePath()).isEqualTo("src/main/java/foo/Bar.java");
        assertThat(inputFile.lines()).isEqualTo(1);
    }
}

