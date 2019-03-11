package com.baeldung.java.nio2;


import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class FileIntegrationTest {
    private static final String TEMP_DIR = String.format("%s/temp%s", System.getProperty("user.home"), UUID.randomUUID().toString());

    // checking file or dir
    @Test
    public void givenExistentPath_whenConfirmsFileExists_thenCorrect() {
        Path p = Paths.get(FileIntegrationTest.TEMP_DIR);
        Assert.assertTrue(Files.exists(p));
    }

    @Test
    public void givenNonexistentPath_whenConfirmsFileNotExists_thenCorrect() {
        Path p = Paths.get(((FileIntegrationTest.TEMP_DIR) + "/inexistent_file.txt"));
        Assert.assertTrue(Files.notExists(p));
    }

    @Test
    public void givenDirPath_whenConfirmsNotRegularFile_thenCorrect() {
        Path p = Paths.get(FileIntegrationTest.TEMP_DIR);
        Assert.assertFalse(Files.isRegularFile(p));
    }

    @Test
    public void givenExistentDirPath_whenConfirmsReadable_thenCorrect() {
        Path p = Paths.get(FileIntegrationTest.TEMP_DIR);
        Assert.assertTrue(Files.isReadable(p));
    }

    @Test
    public void givenExistentDirPath_whenConfirmsWritable_thenCorrect() {
        Path p = Paths.get(System.getProperty("user.home"));
        Assert.assertTrue(Files.isWritable(p));
    }

    @Test
    public void givenExistentDirPath_whenConfirmsExecutable_thenCorrect() {
        Path p = Paths.get(System.getProperty("user.home"));
        Assert.assertTrue(Files.isExecutable(p));
    }

    @Test
    public void givenSameFilePaths_whenConfirmsIsSame_thenCorrect() throws IOException {
        Path p1 = Paths.get(FileIntegrationTest.TEMP_DIR);
        Path p2 = Paths.get(FileIntegrationTest.TEMP_DIR);
        Assert.assertTrue(Files.isSameFile(p1, p2));
    }

    // reading, writing and creating files
    // creating file
    @Test
    public void givenFilePath_whenCreatesNewFile_thenCorrect() throws IOException {
        String fileName = ("myfile_" + (UUID.randomUUID().toString())) + ".txt";
        Path p = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/") + fileName));
        Assert.assertFalse(Files.exists(p));
        Files.createFile(p);
        Assert.assertTrue(Files.exists(p));
    }

    @Test
    public void givenDirPath_whenCreatesNewDir_thenCorrect() throws IOException {
        String dirName = "myDir_" + (UUID.randomUUID().toString());
        Path p = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/") + dirName));
        Assert.assertFalse(Files.exists(p));
        Files.createDirectory(p);
        Assert.assertTrue(Files.exists(p));
        Assert.assertFalse(Files.isRegularFile(p));
        Assert.assertTrue(Files.isDirectory(p));
    }

    @Test(expected = NoSuchFileException.class)
    public void givenDirPath_whenFailsToCreateRecursively_thenCorrect() throws IOException {
        String dirName = ("myDir_" + (UUID.randomUUID().toString())) + "/subdir";
        Path p = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/") + dirName));
        Assert.assertFalse(Files.exists(p));
        Files.createDirectory(p);
    }

    @Test
    public void givenDirPath_whenCreatesRecursively_thenCorrect() throws IOException {
        Path dir = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/myDir_") + (UUID.randomUUID().toString())));
        Path subdir = dir.resolve("subdir");
        Assert.assertFalse(Files.exists(dir));
        Assert.assertFalse(Files.exists(subdir));
        Files.createDirectories(subdir);
        Assert.assertTrue(Files.exists(dir));
        Assert.assertTrue(Files.exists(subdir));
    }

    @Test
    public void givenFilePath_whenCreatesTempFile_thenCorrect() throws IOException {
        String prefix = "log_";
        String suffix = ".txt";
        Path p = Paths.get(((FileIntegrationTest.TEMP_DIR) + "/"));
        p = Files.createTempFile(p, prefix, suffix);
        // like log_8821081429012075286.txt
        Assert.assertTrue(Files.exists(p));
    }

    @Test
    public void givenPath_whenCreatesTempFileWithDefaults_thenCorrect() throws IOException {
        Path p = Paths.get(((FileIntegrationTest.TEMP_DIR) + "/"));
        p = Files.createTempFile(p, null, null);
        // like 8600179353689423985.tmp
        Assert.assertTrue(Files.exists(p));
    }

    @Test
    public void givenNoFilePath_whenCreatesTempFileInTempDir_thenCorrect() throws IOException {
        Path p = Files.createTempFile(null, null);
        // like C:\Users\new\AppData\Local\Temp\6100927974988978748.tmp
        Assert.assertTrue(Files.exists(p));
    }

    // delete file
    @Test
    public void givenPath_whenDeletes_thenCorrect() throws IOException {
        Path p = Paths.get(((FileIntegrationTest.TEMP_DIR) + "/fileToDelete.txt"));
        Assert.assertFalse(Files.exists(p));
        Files.createFile(p);
        Assert.assertTrue(Files.exists(p));
        Files.delete(p);
        Assert.assertFalse(Files.exists(p));
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void givenPath_whenFailsToDeleteNonEmptyDir_thenCorrect() throws IOException {
        Path dir = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/emptyDir") + (UUID.randomUUID().toString())));
        Files.createDirectory(dir);
        Assert.assertTrue(Files.exists(dir));
        Path file = dir.resolve("file.txt");
        Files.createFile(file);
        Files.delete(dir);
        Assert.assertTrue(Files.exists(dir));
    }

    @Test(expected = NoSuchFileException.class)
    public void givenInexistentFile_whenDeleteFails_thenCorrect() throws IOException {
        Path p = Paths.get(((FileIntegrationTest.TEMP_DIR) + "/inexistentFile.txt"));
        Assert.assertFalse(Files.exists(p));
        Files.delete(p);
    }

    @Test
    public void givenInexistentFile_whenDeleteIfExistsWorks_thenCorrect() throws IOException {
        Path p = Paths.get(((FileIntegrationTest.TEMP_DIR) + "/inexistentFile.txt"));
        Assert.assertFalse(Files.exists(p));
        Files.deleteIfExists(p);
    }

    // copy file
    @Test
    public void givenFilePath_whenCopiesToNewLocation_thenCorrect() throws IOException {
        Path dir1 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/firstdir_") + (UUID.randomUUID().toString())));
        Path dir2 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/otherdir_") + (UUID.randomUUID().toString())));
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);
        Path file1 = dir1.resolve("filetocopy.txt");
        Path file2 = dir2.resolve("filetocopy.txt");
        Files.createFile(file1);
        Assert.assertTrue(Files.exists(file1));
        Assert.assertFalse(Files.exists(file2));
        Files.copy(file1, file2);
        Assert.assertTrue(Files.exists(file2));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void givenPath_whenCopyFailsDueToExistingFile_thenCorrect() throws IOException {
        Path dir1 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/firstdir_") + (UUID.randomUUID().toString())));
        Path dir2 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/otherdir_") + (UUID.randomUUID().toString())));
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);
        Path file1 = dir1.resolve("filetocopy.txt");
        Path file2 = dir2.resolve("filetocopy.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        Assert.assertTrue(Files.exists(file1));
        Assert.assertTrue(Files.exists(file2));
        Files.copy(file1, file2);
        Files.copy(file1, file2, StandardCopyOption.REPLACE_EXISTING);
    }

    // moving files
    @Test
    public void givenFilePath_whenMovesToNewLocation_thenCorrect() throws IOException {
        Path dir1 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/firstdir_") + (UUID.randomUUID().toString())));
        Path dir2 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/otherdir_") + (UUID.randomUUID().toString())));
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);
        Path file1 = dir1.resolve("filetocopy.txt");
        Path file2 = dir2.resolve("filetocopy.txt");
        Files.createFile(file1);
        Assert.assertTrue(Files.exists(file1));
        Assert.assertFalse(Files.exists(file2));
        Files.move(file1, file2);
        Assert.assertTrue(Files.exists(file2));
        Assert.assertFalse(Files.exists(file1));
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void givenFilePath_whenMoveFailsDueToExistingFile_thenCorrect() throws IOException {
        Path dir1 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/firstdir_") + (UUID.randomUUID().toString())));
        Path dir2 = Paths.get((((FileIntegrationTest.TEMP_DIR) + "/otherdir_") + (UUID.randomUUID().toString())));
        Files.createDirectory(dir1);
        Files.createDirectory(dir2);
        Path file1 = dir1.resolve("filetocopy.txt");
        Path file2 = dir2.resolve("filetocopy.txt");
        Files.createFile(file1);
        Files.createFile(file2);
        Assert.assertTrue(Files.exists(file1));
        Assert.assertTrue(Files.exists(file2));
        Files.move(file1, file2);
        Files.move(file1, file2, StandardCopyOption.REPLACE_EXISTING);
        Assert.assertTrue(Files.exists(file2));
        Assert.assertFalse(Files.exists(file1));
    }
}

