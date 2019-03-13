/**
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse;


import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


public class InstallationIdTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testGetGeneratesInstallationIdAndFile() throws Exception {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        String installationIdString = installationId.get();
        Assert.assertNotNull(installationIdString);
        Assert.assertEquals(installationIdString, ParseFileUtils.readFileToString(installationIdFile, "UTF-8"));
    }

    @Test
    public void testGetReadsInstallationIdFromFile() throws Exception {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        ParseFileUtils.writeStringToFile(installationIdFile, "test_installation_id", "UTF-8");
        Assert.assertEquals("test_installation_id", installationId.get());
    }

    @Test
    public void testSetWritesInstallationIdToFile() throws Exception {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        installationId.set("test_installation_id");
        Assert.assertEquals("test_installation_id", ParseFileUtils.readFileToString(installationIdFile, "UTF-8"));
    }

    @Test
    public void testSetThenGet() {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        installationId.set("test_installation_id");
        Assert.assertEquals("test_installation_id", installationId.get());
    }

    @Test
    public void testInstallationIdIsCachedInMemory() {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        String installationIdString = installationId.get();
        ParseFileUtils.deleteQuietly(installationIdFile);
        Assert.assertEquals(installationIdString, installationId.get());
    }

    @Test
    public void testInstallationIdIsRandom() {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        String installationIdString = new InstallationId(installationIdFile).get();
        ParseFileUtils.deleteQuietly(installationIdFile);
        Assert.assertFalse(installationIdString.equals(new InstallationId(installationIdFile).get()));
    }

    @Test
    public void testSetSameDoesNotWriteToDisk() {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        String installationIdString = installationId.get();
        ParseFileUtils.deleteQuietly(installationIdFile);
        installationId.set(installationIdString);
        Assert.assertFalse(installationIdFile.exists());
    }

    @Test
    public void testSetNullDoesNotPersist() {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        String installationIdString = installationId.get();
        installationId.set(null);
        Assert.assertEquals(installationIdString, installationId.get());
    }

    @Test
    public void testSetEmptyStringDoesNotPersist() {
        File installationIdFile = new File(temporaryFolder.getRoot(), "installationId");
        InstallationId installationId = new InstallationId(installationIdFile);
        String installationIdString = installationId.get();
        installationId.set("");
        Assert.assertEquals(installationIdString, installationId.get());
    }
}

