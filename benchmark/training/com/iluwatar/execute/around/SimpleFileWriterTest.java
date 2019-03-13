/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.execute.around;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Predicate;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.migrationsupport.rules.EnableRuleMigrationSupport;
import org.junit.rules.TemporaryFolder;


/**
 * Date: 12/12/15 - 3:21 PM
 *
 * @author Jeroen Meulemeester
 */
@EnableRuleMigrationSupport
public class SimpleFileWriterTest {
    @Rule
    public final TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testWriterNotNull() throws Exception {
        final File temporaryFile = this.testFolder.newFile();
        new SimpleFileWriter(temporaryFile.getPath(), Assertions::assertNotNull);
    }

    @Test
    public void testCreatesNonExistentFile() throws Exception {
        final File nonExistingFile = new File(this.testFolder.getRoot(), "non-existing-file");
        Assertions.assertFalse(nonExistingFile.exists());
        new SimpleFileWriter(nonExistingFile.getPath(), Assertions::assertNotNull);
        Assertions.assertTrue(nonExistingFile.exists());
    }

    @Test
    public void testContentsAreWrittenToFile() throws Exception {
        final String testMessage = "Test message";
        final File temporaryFile = this.testFolder.newFile();
        Assertions.assertTrue(temporaryFile.exists());
        new SimpleFileWriter(temporaryFile.getPath(), ( writer) -> writer.write(testMessage));
        Assertions.assertTrue(Files.lines(temporaryFile.toPath()).allMatch(testMessage::equals));
    }

    @Test
    public void testRipplesIoExceptionOccurredWhileWriting() {
        String message = "Some error";
        Assertions.assertThrows(IOException.class, () -> {
            final File temporaryFile = this.testFolder.newFile();
            new SimpleFileWriter(temporaryFile.getPath(), ( writer) -> {
                throw new IOException(message);
            });
        }, message);
    }
}

