/**
 * Contributions to SpotBugs
 * Copyright (C) 2018, Brian Riehman
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs.classfile.impl;


import java.io.File;
import org.junit.Assume;
import org.junit.Test;


public class ClassFactoryTest {
    @Test
    public void ignoreNonExistentFile() throws Exception {
        File file = ClassFactoryTest.tempFile();
        file.delete();
        FilesystemCodeBaseLocator locator = buildLocator(file);
        assertHasNoCodeBase(ClassFactory.createFilesystemCodeBase(locator));
    }

    @Test
    public void ignoreUnreadableFile() throws Exception {
        File file = ClassFactoryTest.createZipFile();
        file.deleteOnExit();
        file.setReadable(false);
        Assume.assumeFalse("File cannot be marked as unreadable, skipping test.", file.canRead());
        FilesystemCodeBaseLocator locator = buildLocator(file);
        assertHasNoCodeBase(ClassFactory.createFilesystemCodeBase(locator));
    }

    @Test
    public void ignoreUnknownNonClassFileFormat() throws Exception {
        FilesystemCodeBaseLocator locator = buildLocator(ClassFactoryTest.tempFile());
        assertHasNoCodeBase(ClassFactory.createFilesystemCodeBase(locator));
    }

    @Test
    public void acceptZipFile() throws Exception {
        File zipFile = ClassFactoryTest.createZipFile();
        zipFile.deleteOnExit();
        FilesystemCodeBaseLocator locator = buildLocator(zipFile);
        assertHasCodeBase(ClassFactory.createFilesystemCodeBase(locator));
    }
}

