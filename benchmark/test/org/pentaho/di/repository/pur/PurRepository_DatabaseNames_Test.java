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
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoryTestLazySupport;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class PurRepository_DatabaseNames_Test extends RepositoryTestLazySupport {
    private static final String EXISTING_DB = "existing";

    private static final String EXISTING_DB_PATH = PurRepository_DatabaseNames_Test.getPathForDb(PurRepository_DatabaseNames_Test.EXISTING_DB);

    public PurRepository_DatabaseNames_Test(Boolean lazyRepo) {
        super(lazyRepo);
    }

    private PurRepository purRepository;

    private IUnifiedRepository unifiedRepository;

    @Test
    public void getDatabaseId_ExactMatch() throws Exception {
        ObjectId databaseID = purRepository.getDatabaseID(PurRepository_DatabaseNames_Test.EXISTING_DB);
        Assert.assertEquals(PurRepository_DatabaseNames_Test.EXISTING_DB, databaseID.getId());
    }

    @Test
    public void getDatabaseId_InsensitiveMatch() throws Exception {
        final String lookupName = PurRepository_DatabaseNames_Test.EXISTING_DB.toUpperCase();
        Assert.assertNotSame(lookupName, PurRepository_DatabaseNames_Test.EXISTING_DB);
        List<RepositoryFile> files = Arrays.asList(PurRepository_DatabaseNames_Test.file("a"), PurRepository_DatabaseNames_Test.file(PurRepository_DatabaseNames_Test.EXISTING_DB), PurRepository_DatabaseNames_Test.file("b"));
        purRepository = Mockito.spy(purRepository);
        Mockito.doReturn(files).when(purRepository).getAllFilesOfType(ArgumentMatchers.any(ObjectId.class), ArgumentMatchers.eq(DATABASE), ArgumentMatchers.anyBoolean());
        ObjectId databaseID = purRepository.getDatabaseID(lookupName);
        Assert.assertEquals(PurRepository_DatabaseNames_Test.EXISTING_DB, databaseID.getId());
    }

    @Test(expected = KettleException.class)
    public void getDatabaseId_FailsOnRepositoryException() throws Exception {
        Mockito.when(unifiedRepository.getFile(PurRepository_DatabaseNames_Test.getPathForDb("non-existing"))).thenThrow(new RuntimeException());
        purRepository.getDatabaseID("non-existing");
    }
}

