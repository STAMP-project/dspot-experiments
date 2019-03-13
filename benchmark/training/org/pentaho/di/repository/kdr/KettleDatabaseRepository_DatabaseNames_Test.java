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
package org.pentaho.di.repository.kdr;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.repository.kdr.delegates.KettleDatabaseRepositoryDatabaseDelegate;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class KettleDatabaseRepository_DatabaseNames_Test {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private KettleDatabaseRepository repository;

    private KettleDatabaseRepositoryDatabaseDelegate databaseDelegate;

    @Test
    public void getDatabaseId_ExactMatch() throws Exception {
        final String name = UUID.randomUUID().toString();
        final ObjectId expectedId = new StringObjectId("expected");
        Mockito.doReturn(expectedId).when(databaseDelegate).getDatabaseID(name);
        ObjectId id = repository.getDatabaseID(name);
        Assert.assertEquals(expectedId, id);
    }

    @Test
    public void getDatabaseId_InsensitiveMatch() throws Exception {
        final String name = "databaseWithCamelCase";
        final String lookupName = name.toLowerCase();
        Assert.assertNotSame(lookupName, name);
        final ObjectId expected = new StringObjectId("expected");
        Mockito.doReturn(expected).when(databaseDelegate).getDatabaseID(name);
        Mockito.doReturn(null).when(databaseDelegate).getDatabaseID(lookupName);
        DatabaseMeta db = new DatabaseMeta();
        db.setName(name);
        db.setObjectId(expected);
        List<DatabaseMeta> dbs = Collections.singletonList(db);
        Mockito.doReturn(dbs).when(repository).getDatabases();
        ObjectId id = repository.getDatabaseID(lookupName);
        Assert.assertEquals(expected, id);
    }

    @Test
    public void getDatabaseId_ReturnsExactMatch_PriorToCaseInsensitiveMatch() throws Exception {
        final String exact = "databaseExactMatch";
        final String similar = exact.toLowerCase();
        Assert.assertNotSame(similar, exact);
        final ObjectId exactId = new StringObjectId("exactId");
        Mockito.doReturn(exactId).when(databaseDelegate).getDatabaseID(exact);
        final ObjectId similarId = new StringObjectId("similarId");
        Mockito.doReturn(similarId).when(databaseDelegate).getDatabaseID(similar);
        DatabaseMeta db = new DatabaseMeta();
        db.setName(exact);
        DatabaseMeta another = new DatabaseMeta();
        db.setName(similar);
        List<DatabaseMeta> dbs = Arrays.asList(another, db);
        Mockito.doReturn(dbs).when(repository).getDatabases();
        ObjectId id = this.repository.getDatabaseID(exact);
        Assert.assertEquals(exactId, id);
    }
}

