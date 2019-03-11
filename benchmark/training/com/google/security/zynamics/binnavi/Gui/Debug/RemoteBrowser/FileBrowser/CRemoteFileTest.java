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


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class CRemoteFileTest {
    @Test
    public void test1Simple() throws IOException {
        final CRemoteFile remoteFile = new CRemoteFile("C", true);
        try {
            remoteFile.canExecute();
            Assert.fail();
        } catch (final IllegalStateException e) {
        }
        try {
            remoteFile.canRead();
            Assert.fail();
        } catch (final IllegalStateException e) {
        }
        try {
            remoteFile.listFiles();
            Assert.fail();
        } catch (final IllegalStateException e) {
        }
        Assert.assertFalse(remoteFile.canWrite());
        final File foo = new File("foo");
        Assert.assertFalse(remoteFile.renameTo(foo));
        Assert.assertTrue(remoteFile.exists());
        Assert.assertEquals("C", remoteFile.getAbsolutePath());
        final File second = remoteFile.getAbsoluteFile();
        Assert.assertEquals(second.getAbsolutePath(), remoteFile.getAbsolutePath());
        Assert.assertTrue(second.isDirectory());
        final File third = remoteFile.getCanonicalFile();
        Assert.assertEquals(third.getCanonicalPath(), remoteFile.getCanonicalPath());
        Assert.assertTrue(third.isDirectory());
        Assert.assertEquals(null, remoteFile.getParentFile());
        Assert.assertEquals(0, remoteFile.lastModified());
        Assert.assertEquals(0, remoteFile.length());
    }
}

