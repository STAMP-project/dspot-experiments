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
package org.pentaho.di.repository.filerep;


import java.util.UUID;
import org.apache.commons.vfs2.FileObject;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.ObjectId;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class KettleFileRepository_DatabaseNames_Test extends KettleFileRepositoryTestBase {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void getDatabaseId_ExactMatch() throws Exception {
        final String name = UUID.randomUUID().toString();
        DatabaseMeta db = saveDatabase(name);
        ObjectId id = repository.getDatabaseID(name);
        Assert.assertEquals(db.getObjectId(), id);
    }

    @Test
    public void getDatabaseId_InsensitiveMatch() throws Exception {
        final String name = "databaseWithCamelCase";
        final String lookupName = name.toLowerCase();
        Assert.assertNotSame(lookupName, name);
        DatabaseMeta db = saveDatabase(name);
        ObjectId id = repository.getDatabaseID(lookupName);
        Assert.assertEquals(db.getObjectId(), id);
    }

    @Test
    public void getDatabaseId_ReturnsExactMatch_PriorToCaseInsensitiveMatch() throws Exception {
        final String exact = "databaseExactMatch";
        final String similar = exact.toLowerCase();
        Assert.assertNotSame(similar, exact);
        DatabaseMeta db = saveDatabase(exact);
        // simulate legacy repository - store a DB with a name different only in case
        DatabaseMeta another = new DatabaseMeta();
        another.setName(similar);
        FileObject fileObject = repository.getFileObject(another);
        Assert.assertFalse(fileObject.exists());
        // just create it - enough for this case
        fileObject.createFile();
        Assert.assertTrue(fileObject.exists());
        ObjectId id = this.repository.getDatabaseID(exact);
        Assert.assertEquals(db.getObjectId(), id);
    }
}

