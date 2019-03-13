/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.ui.repo.controller;


import RepositoryConnectController.DESCRIPTION;
import RepositoryConnectController.DISPLAY_NAME;
import RepositoryConnectController.IS_DEFAULT;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.widgets.Shell;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.repository.AbstractRepository;
import org.pentaho.di.repository.BaseRepositoryMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryCapabilities;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.Spoon;


/**
 * Created by bmorrise on 5/3/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class RepositoryConnectControllerTest {
    public static final String PLUGIN_NAME = "PLUGIN NAME";

    public static final String ID = "ID";

    public static final String PLUGIN_DESCRIPTION = "PLUGIN DESCRIPTION";

    public static final String DATABASE_NAME = "DATABASE NAME";

    public static final String REPOSITORY_NAME = "Repository Name";

    public static final String REPOSITORY_ID = "Repository ID";

    public static final String REPOSITORY_DESCRIPTION = "Repository Description";

    @Mock
    RepositoriesMeta repositoriesMeta;

    @Mock
    PluginRegistry pluginRegistry;

    @Mock
    RepositoryMeta repositoryMeta;

    @Mock
    PluginInterface pluginInterface;

    @Mock
    AbstractRepository repository;

    @Mock
    DatabaseMeta databaseMeta;

    @Mock
    PropsUI propsUI;

    @Mock
    Spoon spoon;

    private RepositoryConnectController controller;

    @Test
    public void testGetPlugins() throws Exception {
        String plugins = controller.getPlugins();
        Assert.assertEquals("[{\"name\":\"PLUGIN NAME\",\"description\":\"PLUGIN DESCRIPTION\",\"id\":\"ID\"}]", plugins);
    }

    @Test
    public void testGetSetParentShell() {
        Assert.assertNull(controller.getParentShell());
        Shell mockShell = Mockito.mock(Shell.class);
        controller.setParentShell(mockShell);
        Assert.assertNotNull(controller.getParentShell());
        Assert.assertEquals(mockShell, controller.getParentShell());
    }

    @Test
    public void testCreateRepository() throws Exception {
        String id = RepositoryConnectControllerTest.ID;
        Map<String, Object> items = new HashMap<>();
        Mockito.when(pluginRegistry.loadClass(RepositoryPluginType.class, id, RepositoryMeta.class)).thenReturn(repositoryMeta);
        Mockito.when(pluginRegistry.loadClass(RepositoryPluginType.class, repositoryMeta.getId(), Repository.class)).thenReturn(repository);
        Mockito.when(repository.test()).thenReturn(true);
        RepositoryMeta result = controller.createRepository(id, items);
        Assert.assertNotEquals(null, result);
        Mockito.when(repository.test()).thenReturn(false);
        result = controller.createRepository(id, items);
        Assert.assertEquals(null, result);
        Mockito.when(repository.test()).thenReturn(true);
        Mockito.doThrow(new KettleException("forced exception")).when(repositoriesMeta).writeData();
        result = controller.createRepository(id, items);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetRepositories() {
        Mockito.when(repositoriesMeta.nrRepositories()).thenReturn(1);
        Mockito.when(repositoriesMeta.getRepository(0)).thenReturn(repositoryMeta);
        JSONObject json = new JSONObject();
        json.put("displayName", RepositoryConnectControllerTest.REPOSITORY_NAME);
        json.put("isDefault", false);
        json.put("description", RepositoryConnectControllerTest.REPOSITORY_DESCRIPTION);
        json.put("id", RepositoryConnectControllerTest.REPOSITORY_ID);
        Mockito.when(repositoryMeta.toJSONObject()).thenReturn(json);
        String repositories = controller.getRepositories();
        Assert.assertEquals(("[{\"isDefault\":false,\"displayName\":\"Repository Name\",\"description\":\"Repository Description\"," + "\"id\":\"Repository ID\"}]"), repositories);
    }

    @Test
    public void testConnectToRepository() throws Exception {
        Mockito.when(pluginRegistry.loadClass(RepositoryPluginType.class, repositoryMeta.getId(), Repository.class)).thenReturn(repository);
        controller.setCurrentRepository(repositoryMeta);
        controller.connectToRepository();
        Mockito.verify(repository).init(repositoryMeta);
        Mockito.verify(repository).connect(null, null);
    }

    @Test
    public void testGetDatabases() throws Exception {
        Mockito.when(repositoriesMeta.nrDatabases()).thenReturn(1);
        Mockito.when(repositoriesMeta.getDatabase(0)).thenReturn(databaseMeta);
        Mockito.when(databaseMeta.getName()).thenReturn(RepositoryConnectControllerTest.DATABASE_NAME);
        String databases = controller.getDatabases();
        Assert.assertEquals("[{\"name\":\"DATABASE NAME\"}]", databases);
    }

    @Test
    public void testDeleteRepository() throws Exception {
        int index = 1;
        Mockito.when(repositoriesMeta.findRepository(RepositoryConnectControllerTest.REPOSITORY_NAME)).thenReturn(repositoryMeta);
        Mockito.when(repositoriesMeta.indexOfRepository(repositoryMeta)).thenReturn(index);
        Mockito.when(repositoriesMeta.getRepository(index)).thenReturn(repositoryMeta);
        boolean result = controller.deleteRepository(RepositoryConnectControllerTest.REPOSITORY_NAME);
        Assert.assertEquals(true, result);
        Mockito.verify(repositoriesMeta).removeRepository(index);
        Mockito.verify(repositoriesMeta).writeData();
    }

    @Test
    public void testSetDefaultRepository() {
        int index = 1;
        Mockito.when(repositoriesMeta.findRepository(RepositoryConnectControllerTest.REPOSITORY_NAME)).thenReturn(repositoryMeta);
        Mockito.when(repositoriesMeta.indexOfRepository(repositoryMeta)).thenReturn(index);
        boolean result = controller.setDefaultRepository(RepositoryConnectControllerTest.REPOSITORY_NAME);
        Assert.assertEquals(true, result);
    }

    @Test
    public void testAddDatabase() throws Exception {
        controller.addDatabase(databaseMeta);
        Mockito.verify(repositoriesMeta).addDatabase(databaseMeta);
        Mockito.verify(repositoriesMeta).writeData();
    }

    @Test
    public void testGetRepository() throws Exception {
        KettleFileRepositoryMeta kettleFileRepositoryMeta = new KettleFileRepositoryMeta();
        kettleFileRepositoryMeta.setId(RepositoryConnectControllerTest.REPOSITORY_ID);
        kettleFileRepositoryMeta.setDescription(RepositoryConnectControllerTest.REPOSITORY_DESCRIPTION);
        kettleFileRepositoryMeta.setName(RepositoryConnectControllerTest.REPOSITORY_NAME);
        Mockito.when(repositoriesMeta.findRepository(RepositoryConnectControllerTest.REPOSITORY_NAME)).thenReturn(kettleFileRepositoryMeta);
        String output = controller.getRepository(RepositoryConnectControllerTest.REPOSITORY_NAME);
        Assert.assertEquals(true, output.contains(RepositoryConnectControllerTest.REPOSITORY_ID));
        Assert.assertEquals(true, output.contains(RepositoryConnectControllerTest.REPOSITORY_DESCRIPTION));
        Assert.assertEquals(true, output.contains(RepositoryConnectControllerTest.REPOSITORY_NAME));
    }

    @Test
    public void testRepoSwitch() throws Exception {
        Mockito.when(pluginRegistry.loadClass(RepositoryPluginType.class, RepositoryConnectControllerTest.REPOSITORY_ID, Repository.class)).thenReturn(repository);
        KettleFileRepositoryMeta kettleFileRepositoryMeta = new KettleFileRepositoryMeta();
        kettleFileRepositoryMeta.setId(RepositoryConnectControllerTest.REPOSITORY_ID);
        kettleFileRepositoryMeta.setDescription(RepositoryConnectControllerTest.REPOSITORY_DESCRIPTION);
        kettleFileRepositoryMeta.setName(RepositoryConnectControllerTest.REPOSITORY_NAME);
        controller.connectToRepository(kettleFileRepositoryMeta);
        Mockito.verify(spoon).closeAllJobsAndTransformations(true);
        Mockito.when(spoon.getRepository()).thenReturn(repository);
        controller.connectToRepository(kettleFileRepositoryMeta);
        Mockito.verify(spoon).closeRepository();
    }

    @Test
    public void testOnlySetConnectedOnConnect() throws Exception {
        Mockito.when(pluginRegistry.loadClass(RepositoryPluginType.class, RepositoryConnectControllerTest.ID, Repository.class)).thenReturn(repository);
        Mockito.when(pluginRegistry.loadClass(RepositoryPluginType.class, RepositoryConnectControllerTest.ID, RepositoryMeta.class)).thenReturn(repositoryMeta);
        Mockito.when(repository.test()).thenReturn(true);
        Map<String, Object> items = new HashMap<>();
        RepositoryMeta result = controller.createRepository(RepositoryConnectControllerTest.ID, items);
        controller.setCurrentRepository(repositoryMeta);
        Assert.assertNotEquals(null, result);
        Assert.assertNull(controller.getConnectedRepository());
        controller.connectToRepository();
        Assert.assertNotNull(controller.getConnectedRepository());
    }

    @Test
    public void testEditConnectedRepository() throws Exception {
        RepositoryMeta before = new RepositoryConnectControllerTest.TestRepositoryMeta(RepositoryConnectControllerTest.ID, "name1", RepositoryConnectControllerTest.PLUGIN_DESCRIPTION, "same");
        Mockito.doReturn(repository).when(pluginRegistry).loadClass(RepositoryPluginType.class, RepositoryConnectControllerTest.ID, Repository.class);
        Mockito.when(repositoriesMeta.nrRepositories()).thenReturn(1);
        Mockito.when(repositoriesMeta.findRepository(ArgumentMatchers.anyString())).thenReturn(before);
        controller.setConnectedRepository(before);
        controller.setCurrentRepository(before);
        Map<String, Object> map = new HashMap<>();
        map.put(DISPLAY_NAME, "name2");
        map.put(IS_DEFAULT, true);
        map.put(DESCRIPTION, RepositoryConnectControllerTest.PLUGIN_DESCRIPTION);
        controller.updateRepository(RepositoryConnectControllerTest.ID, map);
        Assert.assertEquals("name2", getName());
    }

    @Test
    public void testIsDatabaseWithNameExist() throws Exception {
        final DatabaseMeta databaseMeta1 = new DatabaseMeta();
        databaseMeta1.setName("TestDB1");
        controller.addDatabase(databaseMeta1);
        final DatabaseMeta databaseMeta2 = new DatabaseMeta();
        databaseMeta2.setName("TestDB2");
        controller.addDatabase(databaseMeta2);
        Mockito.when(repositoriesMeta.nrDatabases()).thenReturn(2);
        Mockito.when(repositoriesMeta.getDatabase(0)).thenReturn(databaseMeta1);
        Mockito.when(repositoriesMeta.getDatabase(1)).thenReturn(databaseMeta2);
        // existing databases
        Assert.assertFalse(controller.isDatabaseWithNameExist(databaseMeta1, false));
        databaseMeta2.setName("TestDB1");
        Assert.assertTrue(controller.isDatabaseWithNameExist(databaseMeta2, false));
        // new databases
        final DatabaseMeta databaseMeta3 = new DatabaseMeta();
        databaseMeta3.setName("TestDB3");
        Assert.assertFalse(controller.isDatabaseWithNameExist(databaseMeta3, true));
        databaseMeta3.setName("TestDB1");
        Assert.assertTrue(controller.isDatabaseWithNameExist(databaseMeta3, true));
    }

    private static class TestRepositoryMeta extends BaseRepositoryMeta implements RepositoryMeta {
        private String innerStuff;

        public TestRepositoryMeta(String id, String name, String description, String innerStuff) {
            super(id, name, description, false);
            this.innerStuff = innerStuff;
        }

        @Override
        public RepositoryCapabilities getRepositoryCapabilities() {
            return null;
        }

        @Override
        public RepositoryMeta clone() {
            return new RepositoryConnectControllerTest.TestRepositoryMeta(getId(), getName(), getDescription(), innerStuff);
        }

        @SuppressWarnings("unchecked")
        @Override
        public JSONObject toJSONObject() {
            JSONObject obj = super.toJSONObject();
            obj.put("extra", innerStuff);
            return obj;
        }
    }
}

