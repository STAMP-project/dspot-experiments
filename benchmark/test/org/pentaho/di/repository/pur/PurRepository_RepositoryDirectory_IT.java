/**
 * !
 * Copyright 2016 - 2017 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.repository.pur;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.trans.TransMeta;


/**
 * Created by nbaker on 1/14/16.
 */
public class PurRepository_RepositoryDirectory_IT extends PurRepositoryTestBase {
    private TransMeta transMeta;

    private RepositoryDirectoryInterface defaultSaveDirectory;

    public PurRepository_RepositoryDirectory_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void testGetRepositoryObjectsFirst() throws Exception {
        // Try it accessing getRepositoryObjects() first
        List<RepositoryElementMetaInterface> repositoryObjects = defaultSaveDirectory.getRepositoryObjects();
        Assert.assertEquals(1, repositoryObjects.size());
        Assert.assertEquals("Test", repositoryObjects.get(0).getName());
    }

    @Test
    public void testGetChildrenFirst() throws Exception {
        // Try it again this time calling getChildren() then getRepositoryObjects()
        defaultSaveDirectory.getChildren();
        List<RepositoryElementMetaInterface> repositoryObjects = defaultSaveDirectory.getRepositoryObjects();
        Assert.assertEquals(1, repositoryObjects.size());
        Assert.assertEquals("Test", repositoryObjects.get(0).getName());
    }

    @Test
    public void testGetChildrenThenGetSubDirectory() throws Exception {
        // Try it again this time calling getChildren() then getRepositoryObjects()
        defaultSaveDirectory.getChildren();
        defaultSaveDirectory.getSubdirectory(0);
        List<RepositoryDirectoryInterface> children = defaultSaveDirectory.getChildren();
        Assert.assertEquals(1, children.size());
        Assert.assertEquals("test dir", children.get(0).getName());
    }
}

