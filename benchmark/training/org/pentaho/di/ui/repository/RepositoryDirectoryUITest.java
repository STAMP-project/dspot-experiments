/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.repository;


import RepositoryObjectType.TRANSFORMATION;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryElementMetaInterface;


public class RepositoryDirectoryUITest {
    @Test
    public void testLoadRepositoryObjectsFilled() throws Exception {
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
        Assert.assertTrue(RepositoryDirectoryUI.loadRepositoryObjects(root, true, true, repo).isEmpty());
        Assert.assertEquals(1, RepositoryDirectoryUI.loadRepositoryObjects(dir, true, true, repo).size());
        Mockito.verifyZeroInteractions(repo);
    }

    @Test
    public void testLoadRepositoryObjectsNulled() throws Exception {
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
        Assert.assertTrue(RepositoryDirectoryUI.loadRepositoryObjects(root, true, true, repo).isEmpty());
        Assert.assertEquals(1, RepositoryDirectoryUI.loadRepositoryObjects(dir, true, true, repo).size());
    }
}

