/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.job.entries.copyfiles;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.steps.named.cluster.NamedClusterEmbedManager;


public class JobEntryCopyFilesTest {
    private JobEntryCopyFiles entry;

    private NamedClusterEmbedManager mockNamedClusterEmbedManager;

    private final String EMPTY = "";

    @Test
    public void fileNotCopied() throws Exception {
        entry.source_filefolder = new String[]{ EMPTY };
        entry.destination_filefolder = new String[]{ EMPTY };
        entry.wildcard = new String[]{ EMPTY };
        entry.execute(new Result(), 0);
        Mockito.verify(entry, Mockito.never()).processFileFolder(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class), ArgumentMatchers.any(Result.class));
    }

    @Test
    public void fileCopied() throws Exception {
        String srcPath = "path/to/file";
        String destPath = "path/to/dir";
        entry.source_filefolder = new String[]{ srcPath };
        entry.destination_filefolder = new String[]{ destPath };
        entry.wildcard = new String[]{ EMPTY };
        Result result = entry.execute(new Result(), 0);
        Mockito.verify(entry).processFileFolder(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class), ArgumentMatchers.any(Result.class));
        Mockito.verify(entry, Mockito.atLeast(1)).preprocessfilefilder(ArgumentMatchers.any(String[].class));
        Assert.assertFalse(result.getResult());
        Assert.assertEquals(1, result.getNrErrors());
        Mockito.verify(mockNamedClusterEmbedManager).passEmbeddedMetastoreKey(ArgumentMatchers.anyObject(), ArgumentMatchers.anyString());
    }

    @Test
    public void filesCopied() throws Exception {
        String[] srcPath = new String[]{ "path1", "path2", "path3" };
        String[] destPath = new String[]{ "dest1", "dest2", "dest3" };
        entry.source_filefolder = srcPath;
        entry.destination_filefolder = destPath;
        entry.wildcard = new String[]{ EMPTY, EMPTY, EMPTY };
        Result result = entry.execute(new Result(), 0);
        Mockito.verify(entry, Mockito.times(srcPath.length)).processFileFolder(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Job.class), ArgumentMatchers.any(Result.class));
        Assert.assertFalse(result.getResult());
        Assert.assertEquals(3, result.getNrErrors());
    }

    @Test
    public void saveLoad() throws Exception {
        String[] srcPath = new String[]{ "EMPTY_SOURCE_URL-0-" };
        String[] destPath = new String[]{ "EMPTY_DEST_URL-0-" };
        entry.source_filefolder = srcPath;
        entry.destination_filefolder = destPath;
        entry.wildcard = new String[]{ EMPTY };
        String xml = ("<entry>" + (entry.getXML())) + "</entry>";
        Assert.assertTrue(xml.contains(srcPath[0]));
        Assert.assertTrue(xml.contains(destPath[0]));
        JobEntryCopyFiles loadedentry = new JobEntryCopyFiles();
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        loadedentry.loadXML(XMLHandler.getSubNode(XMLHandler.loadXMLFile(is, null, false, false), "entry"), new ArrayList<org.pentaho.di.core.database.DatabaseMeta>(), null, null, null);
        Assert.assertTrue(loadedentry.destination_filefolder[0].equals(destPath[0]));
        Assert.assertTrue(loadedentry.source_filefolder[0].equals(srcPath[0]));
        Mockito.verify(mockNamedClusterEmbedManager, Mockito.times(2)).registerUrl(ArgumentMatchers.anyString());
    }
}

