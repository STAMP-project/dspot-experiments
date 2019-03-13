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
package org.pentaho.di.trans.steps.getrepositorynames;


import java.nio.file.Path;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.IUser;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryExtended;


public class GetRepositoryNamesTest {
    static Path baseDirName;

    static Repository repo;

    static RepositoryExtended repoExtended;

    @Test
    public void testGetRepoList_includeSubfolders() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", true, ".*", "", ObjectTypeSelection.All, 4);
    }

    @Test
    public void testGetRepoList_excludeSubfolders() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", false, ".*", "", ObjectTypeSelection.All, 0);
    }

    @Test
    public void testGetRepoList_transOnly() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", true, ".*", "", ObjectTypeSelection.Transformations, 2);
    }

    @Test
    public void testGetRepoList_jobsOnly() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", true, ".*", "", ObjectTypeSelection.Jobs, 2);
    }

    @Test
    public void testGetRepoList_nameMask() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", true, "Trans.*", "", ObjectTypeSelection.All, 2);
    }

    @Test
    public void testGetRepoList_withoutNameMask() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", true, "", "", ObjectTypeSelection.All, 4);
    }

    @Test
    public void testGetRepoList_excludeNameMask() throws KettleException {
        init(GetRepositoryNamesTest.repo, "/", true, ".*", "Trans1.*", ObjectTypeSelection.All, 3);
    }

    @Test
    public void testGetRepoList_includeSubfolders_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", true, ".*", "", ObjectTypeSelection.All, 4);
    }

    @Test
    public void testGetRepoList_excludeSubfolders_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", false, ".*", "", ObjectTypeSelection.All, 0);
    }

    @Test
    public void testGetRepoList_transOnly_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", true, ".*", "", ObjectTypeSelection.Transformations, 2);
    }

    @Test
    public void testGetRepoList_jobsOnly_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", true, ".*", "", ObjectTypeSelection.Jobs, 2);
    }

    @Test
    public void testGetRepoList_nameMask_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", true, "Trans.*", "", ObjectTypeSelection.All, 2);
    }

    @Test
    public void testGetRepoList_withoutNameMask_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", true, "", "", ObjectTypeSelection.All, 4);
    }

    @Test
    public void testGetRepoList_excludeNameMask_Extended() throws KettleException {
        init(GetRepositoryNamesTest.repoExtended, "/", true, ".*", "Trans1.*", ObjectTypeSelection.All, 3);
    }

    // PDI-16258
    @Test
    public void testShowHidden() throws KettleException {
        IUser user = Mockito.mock(IUser.class);
        Mockito.when(user.isAdmin()).thenReturn(true);
        Mockito.when(GetRepositoryNamesTest.repoExtended.getUserInfo()).thenReturn(user);
        init(GetRepositoryNamesTest.repoExtended, "/", false, ".*", "", ObjectTypeSelection.All, 0);
        Mockito.verify(GetRepositoryNamesTest.repoExtended, Mockito.never()).loadRepositoryDirectoryTree(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.eq(false), Mockito.anyBoolean(), ArgumentMatchers.anyBoolean());
        Mockito.when(user.isAdmin()).thenReturn(false);
        init(GetRepositoryNamesTest.repoExtended, "/", false, ".*", "", ObjectTypeSelection.All, 0);
        Mockito.verify(GetRepositoryNamesTest.repoExtended).loadRepositoryDirectoryTree(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.eq(false), Mockito.anyBoolean(), Mockito.anyBoolean());
    }
}

