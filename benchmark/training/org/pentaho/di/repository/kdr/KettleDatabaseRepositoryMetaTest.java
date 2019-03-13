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
package org.pentaho.di.repository.kdr;


import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.repository.RepositoriesMeta;


/**
 * Created by bmorrise on 4/26/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class KettleDatabaseRepositoryMetaTest {
    public static final String JSON_OUTPUT = "{\"isDefault\":true,\"displayName\":\"Name\",\"description\":\"Description\"," + "\"databaseConnection\":\"Database Connection\",\"id\":\"KettleDatabaseRepository\"}";

    @Mock
    RepositoriesMeta repositoriesMeta;

    @Mock
    DatabaseMeta databaseMeta;

    public static final String NAME = "Name";

    public static final String DESCRIPTION = "Description";

    public static final String DATABASE_CONNECTION = "Database Connection";

    KettleDatabaseRepositoryMeta kettleDatabaseRepositoryMeta;

    @Test
    public void testPopulate() throws Exception {
        kettleDatabaseRepositoryMeta.setConnection(databaseMeta);
        Mockito.when(databaseMeta.getName()).thenReturn(KettleDatabaseRepositoryMetaTest.DATABASE_CONNECTION);
        Mockito.when(repositoriesMeta.searchDatabase(KettleDatabaseRepositoryMetaTest.DATABASE_CONNECTION)).thenReturn(databaseMeta);
        Map<String, Object> properties = new HashMap<>();
        properties.put("displayName", KettleDatabaseRepositoryMetaTest.NAME);
        properties.put("description", KettleDatabaseRepositoryMetaTest.DESCRIPTION);
        properties.put("databaseConnection", KettleDatabaseRepositoryMetaTest.DATABASE_CONNECTION);
        properties.put("isDefault", true);
        kettleDatabaseRepositoryMeta.populate(properties, repositoriesMeta);
        Assert.assertEquals(KettleDatabaseRepositoryMetaTest.NAME, kettleDatabaseRepositoryMeta.getName());
        Assert.assertEquals(KettleDatabaseRepositoryMetaTest.DESCRIPTION, kettleDatabaseRepositoryMeta.getDescription());
        Assert.assertEquals(KettleDatabaseRepositoryMetaTest.DATABASE_CONNECTION, kettleDatabaseRepositoryMeta.getConnection().getName());
        Assert.assertEquals(true, kettleDatabaseRepositoryMeta.isDefault());
    }

    @Test
    public void testToJSONString() {
        Mockito.when(databaseMeta.getName()).thenReturn(KettleDatabaseRepositoryMetaTest.DATABASE_CONNECTION);
        kettleDatabaseRepositoryMeta.setName(KettleDatabaseRepositoryMetaTest.NAME);
        kettleDatabaseRepositoryMeta.setDescription(KettleDatabaseRepositoryMetaTest.DESCRIPTION);
        kettleDatabaseRepositoryMeta.setConnection(databaseMeta);
        kettleDatabaseRepositoryMeta.setDefault(true);
        JSONObject json = kettleDatabaseRepositoryMeta.toJSONObject();
        Assert.assertEquals(KettleDatabaseRepositoryMetaTest.JSON_OUTPUT, json.toString());
    }
}

