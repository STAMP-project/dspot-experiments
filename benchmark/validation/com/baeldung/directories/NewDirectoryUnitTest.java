package com.baeldung.directories;


import java.io.File;
import org.junit.Assert;
import org.junit.Test;


public class NewDirectoryUnitTest {
    private static final File TEMP_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

    @Test
    public void givenUnexistingDirectory_whenMkdir_thenTrue() {
        File newDirectory = new File(NewDirectoryUnitTest.TEMP_DIRECTORY, "new_directory");
        Assert.assertFalse(newDirectory.exists());
        boolean directoryCreated = newDirectory.mkdir();
        Assert.assertTrue(directoryCreated);
    }

    @Test
    public void givenExistingDirectory_whenMkdir_thenFalse() {
        File newDirectory = new File(NewDirectoryUnitTest.TEMP_DIRECTORY, "new_directory");
        newDirectory.mkdir();
        Assert.assertTrue(newDirectory.exists());
        boolean directoryCreated = newDirectory.mkdir();
        Assert.assertFalse(directoryCreated);
    }

    @Test
    public void givenUnexistingNestedDirectories_whenMkdir_thenFalse() {
        File newDirectory = new File(NewDirectoryUnitTest.TEMP_DIRECTORY, "new_directory");
        File nestedDirectory = new File(newDirectory, "nested_directory");
        Assert.assertFalse(newDirectory.exists());
        Assert.assertFalse(nestedDirectory.exists());
        boolean directoriesCreated = nestedDirectory.mkdir();
        Assert.assertFalse(directoriesCreated);
    }

    @Test
    public void givenUnexistingNestedDirectories_whenMkdirs_thenTrue() {
        File newDirectory = new File((((System.getProperty("java.io.tmpdir")) + (File.separator)) + "new_directory"));
        File nestedDirectory = new File(newDirectory, "nested_directory");
        Assert.assertFalse(newDirectory.exists());
        Assert.assertFalse(nestedDirectory.exists());
        boolean directoriesCreated = nestedDirectory.mkdirs();
        Assert.assertTrue(directoriesCreated);
    }

    @Test
    public void givenExistingParentDirectories_whenMkdirs_thenTrue() {
        File newDirectory = new File(NewDirectoryUnitTest.TEMP_DIRECTORY, "existing_directory");
        newDirectory.mkdir();
        File nestedDirectory = new File(newDirectory, "nested_directory");
        Assert.assertTrue(newDirectory.exists());
        Assert.assertFalse(nestedDirectory.exists());
        boolean directoriesCreated = nestedDirectory.mkdirs();
        Assert.assertTrue(directoriesCreated);
    }

    @Test
    public void givenExistingNestedDirectories_whenMkdirs_thenFalse() {
        File existingDirectory = new File(NewDirectoryUnitTest.TEMP_DIRECTORY, "existing_directory");
        File existingNestedDirectory = new File(existingDirectory, "existing_nested_directory");
        Assert.assertTrue(existingDirectory.exists());
        Assert.assertTrue(existingNestedDirectory.exists());
        boolean directoriesCreated = existingNestedDirectory.mkdirs();
        Assert.assertFalse(directoriesCreated);
    }
}

