/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.repository.repositoryexplorer.model;


import RepositoryObjectType.TRANSFORMATION;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryElementMetaInterface;


public class UIRepositoryDirectoryTest {
    @Test
    public void testGetRepositoryObjectsFilled() throws Exception {
        RepositoryDirectory root = new RepositoryDirectory();
        root.setObjectId(new LongObjectId(0L));
        RepositoryDirectory dir = new RepositoryDirectory();
        dir.setObjectId(new LongObjectId(1L));
        root.addSubdirectory(dir);
        RepositoryElementMetaInterface meta = Mockito.mock(RepositoryElementMetaInterface.class);
        Mockito.when(meta.getObjectType()).thenReturn(TRANSFORMATION);
        root.setRepositoryObjects(Collections.emptyList());
        dir.setRepositoryObjects(Collections.singletonList(meta));
        Repository repo = Mockito.mock(Repository.class);
        String dirTest = "dirTest";
        dir.setName(dirTest);
        String[] dirNames = new String[]{ dirTest };
        Mockito.when(repo.getDirectoryNames(ArgumentMatchers.any())).thenReturn(dirNames);
        UIRepositoryDirectory uiDir = new UIRepositoryDirectory(root, null, repo);
        UIRepositoryObjects objects = uiDir.getRepositoryObjects();
        Assert.assertNotNull(objects);
        uiDir = new UIRepositoryDirectory(dir, uiDir, repo);
        objects = uiDir.getRepositoryObjects();
        Assert.assertEquals(1, objects.size());
    }

    @Test
    public void testGetRepositoryObjectsNulled() throws Exception {
        RepositoryDirectory root = new RepositoryDirectory();
        root.setObjectId(new LongObjectId(0L));
        RepositoryDirectory dir = new RepositoryDirectory();
        dir.setObjectId(new LongObjectId(1L));
        root.addSubdirectory(dir);
        RepositoryElementMetaInterface meta = Mockito.mock(RepositoryElementMetaInterface.class);
        Mockito.when(meta.getObjectType()).thenReturn(TRANSFORMATION);
        Repository repo = Mockito.mock(Repository.class);
        Mockito.when(repo.getJobAndTransformationObjects(dir.getObjectId(), false)).thenReturn(Collections.singletonList(meta));
        Mockito.when(repo.getJobAndTransformationObjects(root.getObjectId(), false)).thenReturn(Collections.emptyList());
        String dirTest = "dirTest";
        dir.setName(dirTest);
        String[] dirNames = new String[]{ dirTest };
        Mockito.when(repo.getDirectoryNames(ArgumentMatchers.any())).thenReturn(dirNames);
        UIRepositoryDirectory uiDir = new UIRepositoryDirectory(root, null, repo);
        UIRepositoryObjects objects = uiDir.getRepositoryObjects();
        Assert.assertNotNull(objects);
        uiDir = new UIRepositoryDirectory(dir, uiDir, repo);
        objects = uiDir.getRepositoryObjects();
        Assert.assertEquals(1, objects.size());
    }

    @Test
    public void testRefresh() throws Exception {
        RepositoryDirectory rd = Mockito.mock(RepositoryDirectory.class);
        Mockito.when(rd.getObjectId()).thenReturn(new LongObjectId(0L));
        UIRepositoryDirectory uiDir = new UIRepositoryDirectory(rd, null, null);
        uiDir.populateChildren();
        uiDir.getRepositoryObjects();
        uiDir.clear();
        Mockito.verify(rd).getChildren();
        Mockito.verify(rd).getRepositoryObjects();
    }
}

