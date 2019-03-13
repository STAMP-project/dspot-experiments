/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.vfs;


import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.Test;


public class SftpFileObjectWithWindowsSupportTest {
    private static final String PATH = "C/Test";

    private static final String USERS = "Users";

    private static final String PERMISSION_READ = "(R)";

    private static final String PERMISSION_WRITE = "(W)";

    @Test
    public void isReadableLinuxCase() throws Exception {
        FileObject fileObjectReadable = SftpFileObjectWithWindowsSupportTest.getLinuxFileObject(true, false);
        FileObject fileObjectNotReadable = SftpFileObjectWithWindowsSupportTest.getLinuxFileObject(false, false);
        Assert.assertTrue(fileObjectReadable.isReadable());
        Assert.assertFalse(fileObjectNotReadable.isReadable());
    }

    @Test
    public void isReadableWindowsCase() throws Exception {
        FileObject fileObjectReadable = SftpFileObjectWithWindowsSupportTest.getWindowsFileObject(true, false);
        FileObject fileObjectNotReadable = SftpFileObjectWithWindowsSupportTest.getWindowsFileObject(false, false);
        Assert.assertTrue(fileObjectReadable.isReadable());
        Assert.assertFalse(fileObjectNotReadable.isReadable());
    }

    @Test
    public void isWritableLinuxCase() throws Exception {
        FileObject fileObjectWritable = SftpFileObjectWithWindowsSupportTest.getLinuxFileObject(true, true);
        FileObject fileObjectNotWritable = SftpFileObjectWithWindowsSupportTest.getLinuxFileObject(true, false);
        Assert.assertTrue(fileObjectWritable.isWriteable());
        Assert.assertFalse(fileObjectNotWritable.isWriteable());
    }

    @Test
    public void isWritableWindowsCase() throws Exception {
        FileObject fileObjectWritable = SftpFileObjectWithWindowsSupportTest.getWindowsFileObject(true, true);
        FileObject fileObjectNotWritable = SftpFileObjectWithWindowsSupportTest.getWindowsFileObject(true, false);
        Assert.assertTrue(fileObjectWritable.isWriteable());
        Assert.assertFalse(fileObjectNotWritable.isWriteable());
    }
}

