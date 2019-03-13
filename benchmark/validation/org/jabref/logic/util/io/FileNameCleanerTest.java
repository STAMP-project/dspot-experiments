package org.jabref.logic.util.io;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FileNameCleanerTest {
    @Test
    public void testCleanFileName() {
        Assertions.assertEquals("legalFilename.txt", FileNameCleaner.cleanFileName("legalFilename.txt"));
        Assertions.assertEquals("illegalFilename______.txt", FileNameCleaner.cleanFileName("illegalFilename/?*<>|.txt"));
    }

    @Test
    public void testCleanDirectoryName() {
        Assertions.assertEquals("legalFilename.txt", FileNameCleaner.cleanDirectoryName("legalFilename.txt"));
        Assertions.assertEquals("subdir/legalFilename.txt", FileNameCleaner.cleanDirectoryName("subdir/legalFilename.txt"));
        Assertions.assertEquals("illegalFilename/_____.txt", FileNameCleaner.cleanDirectoryName("illegalFilename/?*<>|.txt"));
    }

    @Test
    public void testCleanDirectoryNameForWindows() {
        Assertions.assertEquals("legalFilename.txt", FileNameCleaner.cleanDirectoryName("legalFilename.txt"));
        Assertions.assertEquals("subdir\\legalFilename.txt", FileNameCleaner.cleanDirectoryName("subdir\\legalFilename.txt"));
        Assertions.assertEquals("illegalFilename\\_____.txt", FileNameCleaner.cleanDirectoryName("illegalFilename\\?*<>|.txt"));
    }
}

