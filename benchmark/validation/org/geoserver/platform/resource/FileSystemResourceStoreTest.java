/**
 * (c) 2017 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.platform.resource;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class FileSystemResourceStoreTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void renameSameFileName() throws IOException, InterruptedException {
        String sameName = "Filea";
        attemptRenameFile(sameName, sameName);
        Assert.assertEquals(sameName, folder.getRoot().list()[0]);
    }

    @Test
    public void renameFileNamesCaseDiffer() throws IOException, InterruptedException {
        String newName = "Filea";
        attemptRenameFile("FileA", newName);
        Assert.assertEquals(newName, folder.getRoot().list()[0]);
    }

    @Test
    public void renameFileNamesDiffer() throws IOException, InterruptedException {
        String newName = "FileB";
        attemptRenameFile("FileA", newName);
        Assert.assertEquals(newName, folder.getRoot().list()[0]);
    }

    @Test
    public void renameSameDirName() throws IOException, InterruptedException {
        String sameName = "Dira";
        attemptRenameDir(sameName, sameName);
        Assert.assertEquals(sameName, folder.getRoot().list()[0]);
    }

    @Test
    public void renameDirNamesCaseDiffer() throws IOException, InterruptedException {
        String newName = "Dira";
        attemptRenameDir("DirA", newName);
        Assert.assertEquals(newName, folder.getRoot().list()[0]);
    }

    @Test
    public void renameDirNamesDiffer() throws IOException, InterruptedException {
        String newName = "DirB";
        attemptRenameDir("DirA", newName);
        Assert.assertEquals(newName, folder.getRoot().list()[0]);
    }
}

