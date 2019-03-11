package org.jabref.logic.util.io;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jabref.logic.layout.LayoutFormatterPreferences;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class FileUtilTest {
    private final Path nonExistingTestPath = Paths.get("nonExistingTestPath");

    private Path existingTestFile;

    private Path otherExistingTestFile;

    private LayoutFormatterPreferences layoutFormatterPreferences;

    private Path rootDir;

    @Test
    public void testIsBibFile() throws IOException {
        Path bibFile = Files.createFile(rootDir.resolve("test.bib"));
        Assertions.assertTrue(FileUtil.isBibFile(bibFile));
    }

    @Test
    public void testIsNotBibFile() throws IOException {
        Path bibFile = Files.createFile(rootDir.resolve("test.pdf"));
        Assertions.assertFalse(FileUtil.isBibFile(bibFile));
    }
}

