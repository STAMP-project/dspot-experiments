/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabaseContent;
import com.google.security.zynamics.binnavi.disassembly.CProjectFactory;
import com.google.security.zynamics.binnavi.disassembly.Modules.CModuleFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class DatabaseTest {
    @Test
    public void testConnect() throws CouldntConnectException, CouldntInitializeDatabaseException, CouldntLoadDataException, CouldntLoadDriverException, InvalidDatabaseException, InvalidDatabaseFormatException, InvalidDatabaseVersionException {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        mockDatabase.getContent().m_modules.add(CModuleFactory.get());
        mockDatabase.getContent().m_projects.add(CProjectFactory.get());
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.connect();
        Assert.assertTrue(database.isConnected());
        database.load();
        listener.m_allowClosing = false;
        database.close();
        listener.m_allowClosing = true;
        database.close();
        Assert.assertEquals("openedDatabase;loadedDatabase;closingDatabase;closingDatabase;closedDatabase;", listener.events);
        Assert.assertEquals("Database 'Mock Database' [Unloaded]", database.toString());
        database.dispose();
    }

    @Test
    public void testCreateProject() throws CouldntDeleteException, CouldntSaveDataException {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        final Project newProject = database.createProject("Hannes");
        mockDatabase.getContent().addProject("Fork");
        Assert.assertEquals("addedProject;addedProject;", listener.events);
        Assert.assertEquals(2, database.getProjects().size());
        database.deleteProject(newProject);
        Assert.assertEquals("addedProject;addedProject;deletedProject;", listener.events);
        Assert.assertEquals(1, database.getProjects().size());
        database.removeListener(listener);
    }

    @Test
    public void testDeleteModule() throws CouldntConnectException, CouldntDeleteException, CouldntInitializeDatabaseException, CouldntLoadDataException, CouldntLoadDriverException, InvalidDatabaseException, InvalidDatabaseFormatException, InvalidDatabaseVersionException {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        mockDatabase.getContent().m_modules.add(CModuleFactory.get());
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.connect();
        database.load();
        database.deleteModule(database.getModules().get(0));
        Assert.assertEquals("openedDatabase;loadedDatabase;deletedModule;", listener.events);
        Assert.assertTrue(database.getModules().isEmpty());
    }

    @Test
    public void testGetDebuggerTemplateManager() {
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        final DebuggerTemplateManager manager1 = database.getDebuggerTemplateManager();
        final DebuggerTemplateManager manager2 = database.getDebuggerTemplateManager();
        Assert.assertNotNull(manager1);
        Assert.assertEquals(manager1, manager2);
    }

    @Test
    public void testNodeTagManager() {
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        final TagManager manager1 = database.getNodeTagManager();
        final TagManager manager2 = database.getNodeTagManager();
        Assert.assertNotNull(manager1);
        Assert.assertEquals(manager1, manager2);
    }

    @Test
    public void testSetAutoConnect() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setAutoConnect(false);
        Assert.assertEquals("changedAutoConnect;", listener.events);
        Assert.assertFalse(database.isAutoConnect());
        Assert.assertFalse(mockDatabase.getConfiguration().isAutoConnect());
        mockDatabase.getConfiguration().setAutoConnect(true);
        Assert.assertEquals("changedAutoConnect;changedAutoConnect;", listener.events);
        Assert.assertTrue(database.isAutoConnect());
        Assert.assertTrue(mockDatabase.getConfiguration().isAutoConnect());
        database.removeListener(listener);
    }

    @Test
    public void testSetDescription() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setDescription("Fark 1");
        Assert.assertEquals("changedDescription;", listener.events);
        Assert.assertEquals("Fark 1", database.getDescription());
        Assert.assertEquals("Fark 1", mockDatabase.getConfiguration().getDescription());
        mockDatabase.getConfiguration().setDescription("Fark 2");
        Assert.assertEquals("changedDescription;changedDescription;", listener.events);
        Assert.assertEquals("Fark 2", database.getDescription());
        Assert.assertEquals("Fark 2", mockDatabase.getConfiguration().getDescription());
        database.removeListener(listener);
    }

    @Test
    public void testSetDriver() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setDriver("Fark 1");
        Assert.assertEquals("changedDriver;", listener.events);
        Assert.assertEquals("Fark 1", database.getDriver());
        Assert.assertEquals("Fark 1", mockDatabase.getConfiguration().getDriver());
        mockDatabase.getConfiguration().setDriver("Fark 2");
        Assert.assertEquals("changedDriver;changedDriver;", listener.events);
        Assert.assertEquals("Fark 2", database.getDriver());
        Assert.assertEquals("Fark 2", mockDatabase.getConfiguration().getDriver());
        database.removeListener(listener);
    }

    @Test
    public void testSetHost() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setHost("Fark 1");
        Assert.assertEquals("changedHost;", listener.events);
        Assert.assertEquals("Fark 1", database.getHost());
        Assert.assertEquals("Fark 1", mockDatabase.getConfiguration().getHost());
        mockDatabase.getConfiguration().setHost("Fark 2");
        Assert.assertEquals("changedHost;changedHost;", listener.events);
        Assert.assertEquals("Fark 2", database.getHost());
        Assert.assertEquals("Fark 2", mockDatabase.getConfiguration().getHost());
        database.removeListener(listener);
    }

    @Test
    public void testSetName() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setName("Fark 1");
        Assert.assertEquals("changedName;", listener.events);
        Assert.assertEquals("Fark 1", database.getName());
        Assert.assertEquals("Fark 1", mockDatabase.getConfiguration().getName());
        mockDatabase.getConfiguration().setName("Fark 2");
        Assert.assertEquals("changedName;changedName;", listener.events);
        Assert.assertEquals("Fark 2", database.getName());
        Assert.assertEquals("Fark 2", mockDatabase.getConfiguration().getName());
        database.removeListener(listener);
    }

    @Test
    public void testSetPassword() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setPassword("Fark 1");
        Assert.assertEquals("changedPassword;", listener.events);
        Assert.assertEquals("Fark 1", database.getPassword());
        Assert.assertEquals("Fark 1", mockDatabase.getConfiguration().getPassword());
        mockDatabase.getConfiguration().setPassword("Fark 2");
        Assert.assertEquals("changedPassword;changedPassword;", listener.events);
        Assert.assertEquals("Fark 2", database.getPassword());
        Assert.assertEquals("Fark 2", mockDatabase.getConfiguration().getPassword());
        database.removeListener(listener);
    }

    @Test
    public void testSetSavePassword() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setSavePassword(false);
        Assert.assertEquals("changedSavePassword;", listener.events);
        Assert.assertFalse(database.isSavePassword());
        Assert.assertFalse(mockDatabase.getConfiguration().isSavePassword());
        mockDatabase.getConfiguration().setSavePassword(true);
        Assert.assertEquals("changedSavePassword;changedSavePassword;", listener.events);
        Assert.assertTrue(database.isSavePassword());
        Assert.assertTrue(mockDatabase.getConfiguration().isSavePassword());
        database.removeListener(listener);
    }

    @Test
    public void testSetUser() {
        final MockDatabaseListener listener = new MockDatabaseListener();
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        database.addListener(listener);
        database.setUser("Fark 1");
        Assert.assertEquals("changedUser;", listener.events);
        Assert.assertEquals("Fark 1", database.getUser());
        Assert.assertEquals("Fark 1", mockDatabase.getConfiguration().getUser());
        mockDatabase.getConfiguration().setUser("Fark 2");
        Assert.assertEquals("changedUser;changedUser;", listener.events);
        Assert.assertEquals("Fark 2", database.getUser());
        Assert.assertEquals("Fark 2", mockDatabase.getConfiguration().getUser());
        database.removeListener(listener);
    }

    @Test
    public void testViewTagManager() {
        final MockDatabase mockDatabase = new MockDatabase();
        final Database database = new Database(mockDatabase);
        final TagManager manager1 = database.getViewTagManager();
        final TagManager manager2 = database.getViewTagManager();
        Assert.assertNotNull(manager1);
        Assert.assertEquals(manager1, manager2);
    }
}

