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
package org.pentaho.di.core.util;


import Const.VFS_USER_DIR_IS_ROOT;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleVariablesList;


/**
 * Created by Yury_Bakhmutski on 11/4/2015.
 */
public class KettleVariablesListTest {
    @Test
    public void testInit() throws Exception {
        KettleVariablesList variablesList = KettleVariablesList.getInstance();
        variablesList.init();
        // See PDI-14522
        boolean actual = Boolean.valueOf(variablesList.getDefaultValueMap().get(VFS_USER_DIR_IS_ROOT));
        Assert.assertEquals(false, actual);
        String vfsUserDirIsRootDefaultMessage = "Set this variable to true if VFS should treat the user directory" + " as the root directory when connecting via ftp. Defaults to false.";
        Assert.assertEquals(variablesList.getDescriptionMap().get(VFS_USER_DIR_IS_ROOT), vfsUserDirIsRootDefaultMessage);
    }

    @Test
    public void testInit_closeInputStream() throws Exception {
        KettleVariablesList.init();
        RandomAccessFile fos = null;
        try {
            File file = new File(Const.KETTLE_VARIABLES_FILE);
            if (file.exists()) {
                fos = new RandomAccessFile(file, "rw");
            }
        } catch (FileNotFoundException | SecurityException e) {
            Assert.fail("the file with properties should be unallocated");
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }
}

