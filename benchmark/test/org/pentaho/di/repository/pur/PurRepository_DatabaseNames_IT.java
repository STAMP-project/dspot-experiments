/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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


import RepositoryObjectType.DATABASE;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.repository.RepositoryFilenameUtils;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class PurRepository_DatabaseNames_IT extends PurRepositoryTestBase {
    public PurRepository_DatabaseNames_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void saveDatabaseModifiedDate() throws Exception {
        Long testStart = System.currentTimeMillis();
        final String name = UUID.randomUUID().toString();
        DatabaseMeta db = saveDatabase(name);
        RepositoryObject info = purRepository.getObjectInformation(db.getObjectId(), DATABASE);
        Assert.assertNotNull(info.getModifiedDate());
        Assert.assertTrue((testStart <= (info.getModifiedDate().getTime())));
    }

    @Test
    public void getDatabaseId_ExactMatch() throws Exception {
        final String name = UUID.randomUUID().toString();
        DatabaseMeta db = saveDatabase(name);
        ObjectId id = purRepository.getDatabaseID(name);
        Assert.assertEquals(db.getObjectId(), id);
    }

    @Test
    public void getDatabaseId_InsensitiveMatch() throws Exception {
        final String name = "databaseWithCamelCase";
        final String lookupName = name.toLowerCase();
        Assert.assertNotSame(lookupName, name);
        DatabaseMeta db = saveDatabase(name);
        ObjectId id = purRepository.getDatabaseID(lookupName);
        Assert.assertEquals(db.getObjectId(), id);
    }

    @Test
    public void getDatabaseId_ReturnsExactMatch_PriorToCaseInsensitiveMatch() throws Exception {
        final String exact = "databaseExactMatch";
        final String similar = exact.toLowerCase();
        Assert.assertNotSame(similar, exact);
        DatabaseMeta db = saveDatabase(exact);
        // simulate legacy repository - store a DB with a name different only in case
        // it became illegal to store such DB via API, thus create the file accessing UnifiedRepository directly
        DatabaseMeta another = new DatabaseMeta();
        another.setName(similar);
        final String filename = PurRepository.checkAndSanitize(((RepositoryFilenameUtils.escape(similar, unifiedRepository.getReservedChars())) + (DATABASE.getExtension())));
        RepositoryFile file = new RepositoryFile.Builder(filename).title(similar).build();
        file = unifiedRepository.createFile(purRepository.getDatabaseMetaParentFolderId(), file, new org.pentaho.platform.api.repository2.unified.data.node.NodeRepositoryFileData(new DatabaseDelegate(purRepository).elementToDataNode(another)), null);
        Assert.assertNotNull(file.getId());
        Assert.assertNotSame(file.getId().toString(), db.getObjectId().toString());
        ObjectId id = purRepository.getDatabaseID(exact);
        Assert.assertEquals(db.getObjectId(), id);
    }
}

