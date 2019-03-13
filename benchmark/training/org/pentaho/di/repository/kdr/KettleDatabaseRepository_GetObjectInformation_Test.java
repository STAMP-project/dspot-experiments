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


import KettleDatabaseRepositoryBase.FIELD_DATABASE_NAME;
import KettleDatabaseRepositoryBase.FIELD_JOB_NAME;
import KettleDatabaseRepositoryBase.FIELD_TRANSFORMATION_NAME;
import RepositoryObjectType.DATABASE;
import RepositoryObjectType.JOB;
import RepositoryObjectType.TRANSFORMATION;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObject;
import org.pentaho.di.repository.StringObjectId;
import org.pentaho.di.repository.kdr.delegates.KettleDatabaseRepositoryDatabaseDelegate;
import org.pentaho.di.repository.kdr.delegates.KettleDatabaseRepositoryJobDelegate;
import org.pentaho.di.repository.kdr.delegates.KettleDatabaseRepositoryTransDelegate;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class KettleDatabaseRepository_GetObjectInformation_Test {
    private static final String ABSENT_ID = "non-existing object";

    private static final String EXISTING_ID = "existing object";

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private KettleDatabaseRepository repository;

    private RepositoryDirectoryInterface directoryInterface;

    @Test
    public void getObjectInformation_AbsentJob_IsDeletedFlagSet() throws Exception {
        KettleDatabaseRepositoryJobDelegate jobDelegate = Mockito.spy(new KettleDatabaseRepositoryJobDelegate(repository));
        RowMeta meta = KettleDatabaseRepository_GetObjectInformation_Test.createMetaForJob();
        Mockito.doReturn(new org.pentaho.di.core.RowMetaAndData(meta, new Object[meta.size()])).when(jobDelegate).getJob(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.ABSENT_ID));
        assertIsDeletedSet_ForAbsentObject(null, jobDelegate, JOB);
    }

    @Test
    public void getObjectInformation_AbsentTrans_IsDeletedFlagSet() throws Exception {
        KettleDatabaseRepositoryTransDelegate transDelegate = Mockito.spy(new KettleDatabaseRepositoryTransDelegate(repository));
        RowMeta meta = KettleDatabaseRepository_GetObjectInformation_Test.createMetaForTrans();
        Mockito.doReturn(new org.pentaho.di.core.RowMetaAndData(meta, new Object[meta.size()])).when(transDelegate).getTransformation(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.ABSENT_ID));
        assertIsDeletedSet_ForAbsentObject(transDelegate, null, TRANSFORMATION);
    }

    @Test
    public void getObjectInformation_ExistingJob_IsDeletedFlagNotSet() throws Exception {
        KettleDatabaseRepositoryJobDelegate jobDelegate = Mockito.spy(new KettleDatabaseRepositoryJobDelegate(repository));
        RowMeta meta = KettleDatabaseRepository_GetObjectInformation_Test.createMetaForJob();
        Object[] values = new Object[meta.size()];
        values[Arrays.asList(meta.getFieldNames()).indexOf(FIELD_JOB_NAME)] = KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID;
        Mockito.doReturn(new org.pentaho.di.core.RowMetaAndData(meta, values)).when(jobDelegate).getJob(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID));
        assertIsDeletedNotSet_ForExistingObject(null, jobDelegate, JOB);
    }

    @Test
    public void getObjectInformation_ExistingTrans_IsDeletedFlagNotSet() throws Exception {
        KettleDatabaseRepositoryTransDelegate transDelegate = Mockito.spy(new KettleDatabaseRepositoryTransDelegate(repository));
        RowMeta meta = KettleDatabaseRepository_GetObjectInformation_Test.createMetaForJob();
        Object[] values = new Object[meta.size()];
        values[Arrays.asList(meta.getFieldNames()).indexOf(FIELD_TRANSFORMATION_NAME)] = KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID;
        Mockito.doReturn(new org.pentaho.di.core.RowMetaAndData(meta, values)).when(transDelegate).getTransformation(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID));
        assertIsDeletedNotSet_ForExistingObject(transDelegate, null, TRANSFORMATION);
    }

    @Test
    public void getObjectInformation_GetDatabaseInformation() throws Exception {
        KettleDatabaseRepositoryDatabaseDelegate databaseDelegate = Mockito.spy(new KettleDatabaseRepositoryDatabaseDelegate(repository));
        repository.databaseDelegate = databaseDelegate;
        RowMeta meta = KettleDatabaseRepository_GetObjectInformation_Test.createMetaForDatabase();
        Object[] values = new Object[meta.size()];
        values[Arrays.asList(meta.getFieldNames()).indexOf(FIELD_DATABASE_NAME)] = KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID;
        Mockito.doReturn(new org.pentaho.di.core.RowMetaAndData(meta, values)).when(databaseDelegate).getDatabase(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID));
        RepositoryObject actual = repository.getObjectInformation(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID), DATABASE);
        Assert.assertEquals(new StringObjectId(KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID), actual.getObjectId());
        Assert.assertEquals(KettleDatabaseRepository_GetObjectInformation_Test.EXISTING_ID, actual.getName());
        Assert.assertEquals(DATABASE, actual.getObjectType());
    }
}

