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
package org.pentaho.di.ui.repository.repositoryexplorer.model;


import java.text.SimpleDateFormat;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryElementMetaInterface;


public class UIDatabaseConnectionTest {
    @Test
    public void testDefaults() {
        UIDatabaseConnection uiconn = new UIDatabaseConnection();
        Assert.assertNull(uiconn.getName());
        Assert.assertNull(uiconn.getType());
        Assert.assertNull(uiconn.getDisplayName());
        Assert.assertNull(uiconn.getDatabaseMeta());
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.when(dbMeta.getName()).thenReturn("TestDb");
        Mockito.when(dbMeta.getPluginId()).thenReturn("MYSQL");
        Mockito.when(dbMeta.getDisplayName()).thenReturn("TestDbDisplay");
        Repository repo = Mockito.mock(Repository.class);
        uiconn = new UIDatabaseConnection(dbMeta, repo);
        Assert.assertEquals("TestDb", uiconn.getName());
        Assert.assertEquals("MYSQL", uiconn.getType());
        Assert.assertEquals("TestDbDisplay", uiconn.getDisplayName());
        Assert.assertSame(dbMeta, uiconn.getDatabaseMeta());
    }

    @Test
    public void testModifiedDate() {
        final Long timestamp = 100000L;
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss z");
        UIDatabaseConnection uiconn = new UIDatabaseConnection();
        RepositoryElementMetaInterface repoMeta = Mockito.mock(RepositoryElementMetaInterface.class);
        Mockito.when(repoMeta.getModifiedDate()).thenReturn(new Date(timestamp));
        uiconn.setRepositoryElementMetaInterface(repoMeta);
        Assert.assertEquals(sdf.format(new Date(timestamp)), uiconn.getDateModified());
    }

    @Test
    public void testModifiedDateIsNull() {
        final Long timestamp = 100000L;
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm:ss z");
        UIDatabaseConnection uiconn = new UIDatabaseConnection();
        RepositoryElementMetaInterface repoMeta = Mockito.mock(RepositoryElementMetaInterface.class);
        Mockito.when(repoMeta.getModifiedDate()).thenReturn(null);
        uiconn.setRepositoryElementMetaInterface(repoMeta);
        Assert.assertEquals(null, uiconn.getDateModified());
    }
}

