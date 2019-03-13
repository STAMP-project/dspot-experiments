/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.file;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class BaseFileOutputMetaTest {
    @Mock
    BaseFileOutputMeta meta;

    @Test
    public void testGetFiles() {
        String[] filePaths;
        filePaths = meta.getFiles("foo", "txt", false);
        Assert.assertNotNull(filePaths);
        Assert.assertEquals(1, filePaths.length);
        Assert.assertEquals("foo.txt", filePaths[0]);
        filePaths = meta.getFiles("foo", "txt", true);
        Assert.assertNotNull(filePaths);
        Assert.assertEquals(1, filePaths.length);
        Assert.assertEquals("foo.txt", filePaths[0]);
        Mockito.when(meta.isStepNrInFilename()).thenReturn(true);
        filePaths = meta.getFiles("foo", "txt", false);
        Assert.assertNotNull(filePaths);
        Assert.assertEquals(1, filePaths.length);
        Assert.assertEquals("foo_<step>.txt", filePaths[0]);
        filePaths = meta.getFiles("foo", "txt", true);
        Assert.assertNotNull(filePaths);
        Assert.assertEquals(4, filePaths.length);
        Assert.assertEquals("foo_0.txt", filePaths[0]);
        Assert.assertEquals("foo_1.txt", filePaths[1]);
        Assert.assertEquals("foo_2.txt", filePaths[2]);
        Assert.assertEquals("...", filePaths[3]);
        Mockito.when(meta.isPartNrInFilename()).thenReturn(true);
        filePaths = meta.getFiles("foo", "txt", false);
        Assert.assertNotNull(filePaths);
        Assert.assertEquals(1, filePaths.length);
        Assert.assertEquals("foo_<step>_<partition>.txt", filePaths[0]);
        Mockito.when(meta.getSplitEvery()).thenReturn(1);
        filePaths = meta.getFiles("foo", "txt", false);
        Assert.assertNotNull(filePaths);
        Assert.assertEquals(1, filePaths.length);
        Assert.assertEquals("foo_<step>_<partition>_<split>.txt", filePaths[0]);
    }
}

