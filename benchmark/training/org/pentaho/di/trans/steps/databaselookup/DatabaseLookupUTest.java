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
package org.pentaho.di.trans.steps.databaselookup;


import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.MySQLDatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBinary;
import org.pentaho.di.core.row.value.ValueMetaInteger;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.databaselookup.readallcache.ReadAllCache;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.metastore.api.IMetaStore;

import static DatabaseLookupMeta.CONDITION_EQ;
import static DatabaseLookupMeta.CONDITION_GE;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class DatabaseLookupUTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String BINARY_FIELD = "aBinaryFieldInDb";

    private static final String ID_FIELD = "id";

    private StepMockHelper<DatabaseLookupMeta, DatabaseLookupData> mockHelper;

    @Test
    public void mySqlVariantDbIsLazyConverted() throws Exception {
        DatabaseLookupMeta meta = createDatabaseMeta();
        DatabaseLookupData data = createDatabaseData();
        Database db = createVirtualDb(meta.getDatabaseMeta());
        DatabaseLookup lookup = spyLookup(mockHelper, db, meta.getDatabaseMeta());
        lookup.init(meta, data);
        lookup.processRow(meta, data);
        Mockito.verify(db).getLookup(ArgumentMatchers.any(PreparedStatement.class), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(false));
    }

    @Test
    public void testEqualsAndIsNullAreCached() throws Exception {
        Mockito.when(mockHelper.logChannelInterfaceFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(LoggingObjectInterface.class))).thenReturn(mockHelper.logChannelInterface);
        DatabaseLookup look = new DatabaseLookupUTest.MockDatabaseLookup(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        DatabaseLookupData lookData = new DatabaseLookupData();
        lookData.cache = DefaultCache.newCache(lookData, 0);
        lookData.lookupMeta = new RowMeta();
        MySQLDatabaseMeta mysql = new MySQLDatabaseMeta();
        mysql.setName("MySQL");
        DatabaseMeta dbMeta = new DatabaseMeta();
        dbMeta.setDatabaseInterface(mysql);
        DatabaseLookupMeta meta = new DatabaseLookupMeta();
        meta.setDatabaseMeta(dbMeta);
        meta.setTablename("VirtualTable");
        meta.setTableKeyField(new String[]{ "ID1", "ID2" });
        meta.setKeyCondition(new String[]{ "=", "IS NULL" });
        meta.setReturnValueNewName(new String[]{ "val1", "val2" });
        meta.setReturnValueField(new String[]{ DatabaseLookupUTest.BINARY_FIELD, DatabaseLookupUTest.BINARY_FIELD });
        meta.setReturnValueDefaultType(new int[]{ ValueMetaInterface.TYPE_BINARY, ValueMetaInterface.TYPE_BINARY });
        meta.setStreamKeyField1(new String[0]);
        meta.setStreamKeyField2(new String[0]);
        meta.setReturnValueDefault(new String[]{ "", "" });
        meta = Mockito.spy(meta);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                RowMetaInterface row = ((RowMetaInterface) (invocation.getArguments()[0]));
                ValueMetaInterface v = new ValueMetaBinary(DatabaseLookupUTest.BINARY_FIELD);
                row.addValueMeta(v);
                return null;
            }
        }).when(meta).getFields(ArgumentMatchers.any(RowMetaInterface.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(RowMetaInterface[].class), ArgumentMatchers.any(StepMeta.class), ArgumentMatchers.any(VariableSpace.class), ArgumentMatchers.any(Repository.class), ArgumentMatchers.any(IMetaStore.class));
        look.init(meta, lookData);
        Assert.assertTrue(lookData.allEquals);// Test for fix on PDI-15202

    }

    @Test
    public void getRowInCacheTest() throws KettleException {
        Mockito.when(mockHelper.logChannelInterfaceFactory.create(ArgumentMatchers.any(), ArgumentMatchers.any(LoggingObjectInterface.class))).thenReturn(mockHelper.logChannelInterface);
        DatabaseLookup look = new DatabaseLookup(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        DatabaseLookupData lookData = new DatabaseLookupData();
        lookData.cache = DefaultCache.newCache(lookData, 0);
        lookData.lookupMeta = new RowMeta();
        look.init(new DatabaseLookupMeta(), lookData);
        ValueMetaInterface valueMeta = new ValueMetaInteger("fieldTest");
        RowMeta lookupMeta = new RowMeta();
        lookupMeta.setValueMetaList(Collections.singletonList(valueMeta));
        Object[] kgsRow1 = new Object[1];
        kgsRow1[0] = 1L;
        Object[] kgsRow2 = new Object[1];
        kgsRow2[0] = 2L;
        Object[] add1 = new Object[1];
        add1[0] = 10L;
        Object[] add2 = new Object[1];
        add2[0] = 20L;
        lookData.cache.storeRowInCache(mockHelper.processRowsStepMetaInterface, lookupMeta, kgsRow1, add1);
        lookData.cache.storeRowInCache(mockHelper.processRowsStepMetaInterface, lookupMeta, kgsRow2, add2);
        Object[] rowToCache = new Object[1];
        rowToCache[0] = 0L;
        lookData.conditions = new int[1];
        lookData.conditions[0] = CONDITION_GE;
        Object[] dataFromCache = lookData.cache.getRowFromCache(lookupMeta, rowToCache);
        Assert.assertArrayEquals(dataFromCache, add1);
    }

    @Test
    public void createsReadOnlyCache_WhenReadAll_AndNotAllEquals() throws Exception {
        DatabaseLookupData data = getCreatedData(false);
        Assert.assertThat(data.cache, CoreMatchers.is(CoreMatchers.instanceOf(ReadAllCache.class)));
    }

    @Test
    public void createsReadDefaultCache_WhenReadAll_AndAllEquals() throws Exception {
        DatabaseLookupData data = getCreatedData(true);
        Assert.assertThat(data.cache, CoreMatchers.is(CoreMatchers.instanceOf(DefaultCache.class)));
    }

    @Test
    public void createsReadDefaultCache_AndUsesOnlyNeededFieldsFromMeta() throws Exception {
        Database db = Mockito.mock(Database.class);
        Mockito.when(db.getRows(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt())).thenReturn(Arrays.asList(new Object[]{ 1L }, new Object[]{ 2L }));
        RowMeta returnRowMeta = new RowMeta();
        returnRowMeta.addValueMeta(new ValueMetaInteger());
        returnRowMeta.addValueMeta(new ValueMetaInteger());
        Mockito.when(db.getReturnRowMeta()).thenReturn(returnRowMeta);
        DatabaseLookupMeta meta = createTestMeta();
        DatabaseLookupData data = new DatabaseLookupData();
        DatabaseLookup step = createSpiedStep(db, mockHelper, meta);
        step.init(meta, data);
        data.db = db;
        data.keytypes = new int[]{ ValueMetaInterface.TYPE_INTEGER };
        data.allEquals = true;
        data.conditions = new int[]{ CONDITION_EQ };
        step.processRow(meta, data);
        data.lookupMeta = new RowMeta();
        data.lookupMeta.addValueMeta(new ValueMetaInteger());
        Assert.assertNotNull(data.cache.getRowFromCache(data.lookupMeta, new Object[]{ 1L }));
        Assert.assertNotNull(data.cache.getRowFromCache(data.lookupMeta, new Object[]{ 2L }));
    }

    public class MockDatabaseLookup extends DatabaseLookup {
        public MockDatabaseLookup(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans) {
            super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
        }

        @Override
        Database getDatabase(DatabaseMeta meta) {
            try {
                return createVirtualDb(meta);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}

