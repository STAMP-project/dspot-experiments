package com.baeldung.file;


import com.baeldung.files.ListFiles;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;


public class ListFilesUnitTest {
    private ListFiles listFiles = new ListFiles();

    private String DIRECTORY = "src/test/resources/listFilesUnitTestFolder";

    private static final int DEPTH = 1;

    private Set<String> EXPECTED_FILE_LIST = new HashSet<String>() {
        {
            add("test.xml");
            add("employee.json");
            add("students.json");
            add("country.txt");
        }
    };

    @Test
    public void givenDir_whenUsingJAVAIO_thenListAllFiles() throws IOException {
        Assert.assertEquals(EXPECTED_FILE_LIST, listFiles.listFilesUsingJavaIO(DIRECTORY));
    }

    @Test
    public void givenDir_whenWalkingTree_thenListAllFiles() throws IOException {
        Assert.assertEquals(EXPECTED_FILE_LIST, listFiles.listFilesUsingFileWalk(DIRECTORY, ListFilesUnitTest.DEPTH));
    }

    @Test
    public void givenDir_whenWalkingTreeWithVisitor_thenListAllFiles() throws IOException {
        Assert.assertEquals(EXPECTED_FILE_LIST, listFiles.listFilesUsingFileWalkAndVisitor(DIRECTORY));
    }

    @Test
    public void givenDir_whenUsingDirectoryStream_thenListAllFiles() throws IOException {
        Assert.assertEquals(EXPECTED_FILE_LIST, listFiles.listFilesUsingDirectoryStream(DIRECTORY));
    }
}

