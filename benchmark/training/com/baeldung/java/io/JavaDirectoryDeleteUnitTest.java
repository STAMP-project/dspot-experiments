package com.baeldung.java.io;


import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.FileSystemUtils;


public class JavaDirectoryDeleteUnitTest {
    private static Path TEMP_DIRECTORY;

    private static final String DIRECTORY_NAME = "toBeDeleted";

    private static final List<String> ALL_LINES = Arrays.asList("This is line 1", "This is line 2", "This is line 3", "This is line 4", "This is line 5", "This is line 6");

    @Test
    public void givenDirectory_whenDeletedWithRecursion_thenIsGone() throws IOException {
        Path pathToBeDeleted = JavaDirectoryDeleteUnitTest.TEMP_DIRECTORY.resolve(JavaDirectoryDeleteUnitTest.DIRECTORY_NAME);
        boolean result = deleteDirectory(pathToBeDeleted.toFile());
        Assert.assertTrue("Could not delete directory", result);
        Assert.assertFalse("Directory still exists", Files.exists(pathToBeDeleted));
    }

    @Test
    public void givenDirectory_whenDeletedWithCommonsIOFileUtils_thenIsGone() throws IOException {
        Path pathToBeDeleted = JavaDirectoryDeleteUnitTest.TEMP_DIRECTORY.resolve(JavaDirectoryDeleteUnitTest.DIRECTORY_NAME);
        FileUtils.deleteDirectory(pathToBeDeleted.toFile());
        Assert.assertFalse("Directory still exists", Files.exists(pathToBeDeleted));
    }

    @Test
    public void givenDirectory_whenDeletedWithSpringFileSystemUtils_thenIsGone() throws IOException {
        Path pathToBeDeleted = JavaDirectoryDeleteUnitTest.TEMP_DIRECTORY.resolve(JavaDirectoryDeleteUnitTest.DIRECTORY_NAME);
        boolean result = FileSystemUtils.deleteRecursively(pathToBeDeleted.toFile());
        Assert.assertTrue("Could not delete directory", result);
        Assert.assertFalse("Directory still exists", Files.exists(pathToBeDeleted));
    }

    @Test
    public void givenDirectory_whenDeletedWithFilesWalk_thenIsGone() throws IOException {
        Path pathToBeDeleted = JavaDirectoryDeleteUnitTest.TEMP_DIRECTORY.resolve(JavaDirectoryDeleteUnitTest.DIRECTORY_NAME);
        Files.walk(pathToBeDeleted).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        Assert.assertFalse("Directory still exists", Files.exists(pathToBeDeleted));
    }

    @Test
    public void givenDirectory_whenDeletedWithNIO2WalkFileTree_thenIsGone() throws IOException {
        Path pathToBeDeleted = JavaDirectoryDeleteUnitTest.TEMP_DIRECTORY.resolve(JavaDirectoryDeleteUnitTest.DIRECTORY_NAME);
        Files.walkFileTree(pathToBeDeleted, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
        });
        Assert.assertFalse("Directory still exists", Files.exists(pathToBeDeleted));
    }
}

