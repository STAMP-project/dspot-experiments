package com.baeldung.java.nio2.attributes;


import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicAttribsIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(BasicAttribsIntegrationTest.class);

    private static final String HOME = System.getProperty("user.home");

    private static BasicFileAttributes basicAttribs;

    @Test
    public void givenFileTimes_whenComparesThem_ThenCorrect() {
        FileTime created = BasicAttribsIntegrationTest.basicAttribs.creationTime();
        FileTime modified = BasicAttribsIntegrationTest.basicAttribs.lastModifiedTime();
        FileTime accessed = BasicAttribsIntegrationTest.basicAttribs.lastAccessTime();
        BasicAttribsIntegrationTest.LOG.debug(("Created: " + created));
        BasicAttribsIntegrationTest.LOG.debug(("Modified: " + modified));
        BasicAttribsIntegrationTest.LOG.debug(("Accessed: " + accessed));
    }

    @Test
    public void givenPath_whenGetsFileSize_thenCorrect() {
        long size = BasicAttribsIntegrationTest.basicAttribs.size();
        Assert.assertTrue((size > 0));
    }

    @Test
    public void givenPath_whenChecksIfDirectory_thenCorrect() {
        boolean isDir = BasicAttribsIntegrationTest.basicAttribs.isDirectory();
        Assert.assertTrue(isDir);
    }

    @Test
    public void givenPath_whenChecksIfFile_thenCorrect() {
        boolean isFile = BasicAttribsIntegrationTest.basicAttribs.isRegularFile();
        Assert.assertFalse(isFile);
    }

    @Test
    public void givenPath_whenChecksIfSymLink_thenCorrect() {
        boolean isSymLink = BasicAttribsIntegrationTest.basicAttribs.isSymbolicLink();
        Assert.assertFalse(isSymLink);
    }

    @Test
    public void givenPath_whenChecksIfOther_thenCorrect() {
        boolean isOther = BasicAttribsIntegrationTest.basicAttribs.isOther();
        Assert.assertFalse(isOther);
    }
}

