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
package org.pentaho.di.ui.repository.repositoryexplorer.controllers;


import java.util.Collections;
import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.ui.core.database.dialog.DatabaseDialog;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIDatabaseConnection;
import org.pentaho.ui.xul.containers.XulTree;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class ConnectionsControllerTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private ConnectionsController controller;

    private DatabaseDialog databaseDialog;

    private DatabaseMeta databaseMeta;

    private Repository repository;

    private XulTree connectionsTable;

    @Test
    public void createConnection_NullName() throws Exception {
        testEditConnectionGetsWrongName(null);
    }

    @Test
    public void createConnection_EmptyName() throws Exception {
        testEditConnectionGetsWrongName("");
    }

    @Test
    public void createConnection_BlankName() throws Exception {
        testCreateConnectionGetsWrongName("  ");
    }

    @Test
    public void createConnection_NameExists() throws Exception {
        final String dbName = "name";
        Mockito.when(databaseDialog.open()).thenReturn(dbName);
        Mockito.when(databaseMeta.getDatabaseName()).thenReturn(dbName);
        Mockito.when(repository.getDatabaseID(dbName)).thenReturn(new StringObjectId("existing"));
        controller.createConnection();
        assertShowedAlreadyExistsMessage();
    }

    @Test
    public void createConnection_NewName() throws Exception {
        final String dbName = "name";
        Mockito.when(databaseDialog.open()).thenReturn(dbName);
        Mockito.when(databaseDialog.getDatabaseMeta()).thenReturn(new DatabaseMeta());
        Mockito.when(repository.getDatabaseID(dbName)).thenReturn(null);
        controller.createConnection();
        assertRepositorySavedDb();
    }

    @Test
    public void editConnection_NullName() throws Exception {
        testEditConnectionGetsWrongName(null);
    }

    @Test
    public void editConnection_EmptyName() throws Exception {
        testEditConnectionGetsWrongName("");
    }

    @Test
    public void editConnection_BlankName() throws Exception {
        testEditConnectionGetsWrongName("  ");
    }

    @Test
    public void editConnection_NameExists_Same() throws Exception {
        final String dbName = "name";
        List<UIDatabaseConnection> selectedConnection = createSelectedConnectionList(dbName);
        Mockito.when(connectionsTable.<UIDatabaseConnection>getSelectedItems()).thenReturn(selectedConnection);
        Mockito.when(repository.getDatabaseID(dbName)).thenReturn(new StringObjectId("existing"));
        Mockito.when(databaseDialog.open()).thenReturn(dbName);
        controller.editConnection();
        assertRepositorySavedDb();
    }

    @Test
    public void editConnection_NameDoesNotExist() throws Exception {
        final String dbName = "name";
        List<UIDatabaseConnection> selectedConnection = createSelectedConnectionList(dbName);
        Mockito.when(connectionsTable.<UIDatabaseConnection>getSelectedItems()).thenReturn(selectedConnection);
        Mockito.when(repository.getDatabaseID(dbName)).thenReturn(new StringObjectId("existing"));
        Mockito.when(databaseDialog.open()).thenReturn("non-existing-name");
        controller.editConnection();
        assertRepositorySavedDb();
    }

    @Test
    public void editConnection_NameExists_Different() throws Exception {
        final String dbName = "name";
        List<UIDatabaseConnection> selectedConnection = createSelectedConnectionList(dbName);
        Mockito.when(connectionsTable.<UIDatabaseConnection>getSelectedItems()).thenReturn(selectedConnection);
        final String anotherName = "anotherName";
        Mockito.when(repository.getDatabaseID(dbName)).thenReturn(new StringObjectId("existing"));
        Mockito.when(repository.getDatabaseID(anotherName)).thenReturn(new StringObjectId("another-existing"));
        Mockito.when(databaseDialog.open()).thenReturn(anotherName);
        controller.editConnection();
        assertShowedAlreadyExistsMessage();
    }

    @Test
    public void editConnection_NameExists_SameWithSpaces() throws Exception {
        final String dbName = " name";
        DatabaseMeta dbmeta = Mockito.spy(new DatabaseMeta());
        dbmeta.setName(dbName);
        List<UIDatabaseConnection> selectedConnection = Collections.singletonList(new UIDatabaseConnection(dbmeta, repository));
        Mockito.when(connectionsTable.<UIDatabaseConnection>getSelectedItems()).thenReturn(selectedConnection);
        Mockito.when(repository.getDatabaseID(dbName)).thenReturn(new StringObjectId("existing"));
        Mockito.when(databaseDialog.open()).thenReturn(dbName);
        controller.editConnection();
        Mockito.verify(dbmeta).setName(dbName.trim());
    }
}

