/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.security.zynamics.binnavi.Gui.Debug.RemoteBrowser.FileBrowser;


import com.google.security.zynamics.binnavi.debug.models.remotebrowser.RemoteFileSystem;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xml.sax.SAXException;


@RunWith(JUnit4.class)
public class CRemoteFileSystemViewTest {
    @Test
    public void test1Simple() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        @SuppressWarnings("unused")
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
    }

    @Test
    public void test2CreateNewDirectory() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File file = new File("Directory");
        Assert.assertEquals(null, remoteFileSystemView.createNewFolder(file));
    }

    @Test
    public void test3getDefaultDirectory() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File file = new CRemoteFile("Directory", true);
        Assert.assertEquals(file, remoteFileSystemView.getDefaultDirectory());
    }

    @Test
    public void test4getFiles() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File dir = new CRemoteFile("Directory", true);
        final File[] fileArray = remoteFileSystemView.getFiles(dir, false);
        final File file = new CRemoteFile("Directory/File", false);
        Assert.assertEquals(file, fileArray[0]);
    }

    @Test
    public void test5getHomeDirectory() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File file = new CRemoteFile("Directory", true);
        Assert.assertEquals(file, remoteFileSystemView.getHomeDirectory());
    }

    @Test
    public void test6getParentDirectory() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File dir = new CRemoteFile("Directory", true);
        final File file = new CRemoteFile("Directory/File", false);
        Assert.assertEquals(dir, remoteFileSystemView.getParentDirectory(file));
    }

    @Test
    public void test7getRoots() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File[] fileArray = remoteFileSystemView.getRoots();
        final File root = new CRemoteFile("C/", false);
        Assert.assertEquals(root, fileArray[0]);
    }

    @Test
    public void test8getSystemDisplayName() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        final File file = new CRemoteFile("File", false);
        Assert.assertEquals("File", remoteFileSystemView.getSystemDisplayName(file));
        final File root = new CRemoteFile("C/", false);
        Assert.assertEquals("C/", remoteFileSystemView.getSystemDisplayName(root));
    }

    @Test
    public void test9setFileSystem() throws IOException, ParserConfigurationException, SAXException {
        final RemoteFileSystem fileSystem = RemoteFileSystem.parse("<foo><Drives><Drive name='C' /></Drives><Directories><Directory name='Directory' /></Directories><Directory name='Directory' /><Files><File name='File' /></Files></foo>".getBytes());
        final CRemoteFileSystemView remoteFileSystemView = new CRemoteFileSystemView(fileSystem);
        remoteFileSystemView.setFileSystem(fileSystem);
    }
}

