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
package org.pentaho.di.trans.steps.update;


import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.SQLStatement;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;
import org.pentaho.metastore.api.IMetaStore;


public class UpdateMetaTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMeta stepMeta;

    private Update upd;

    private UpdateData ud;

    private UpdateMeta umi;

    LoadSaveTester loadSaveTester;

    Class<UpdateMeta> testMetaClass = UpdateMeta.class;

    private StepMockHelper<UpdateMeta, UpdateData> mockHelper;

    public static final String databaseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((((((((("<connection>" + "<name>lookup</name>") + "<server>127.0.0.1</server>") + "<type>H2</type>") + "<access>Native</access>") + "<database>mem:db</database>") + "<port></port>") + "<username>sa</username>") + "<password></password>") + "</connection>");

    @Test
    public void testCommitCountFixed() {
        umi.setCommitSize("100");
        Assert.assertTrue(((umi.getCommitSize(upd)) == 100));
    }

    @Test
    public void testCommitCountVar() {
        umi.setCommitSize("${max.sz}");
        Assert.assertTrue(((umi.getCommitSize(upd)) == 10));
    }

    @Test
    public void testCommitCountMissedVar() {
        umi.setCommitSize("missed-var");
        try {
            umi.getCommitSize(upd);
            Assert.fail();
        } catch (Exception ex) {
        }
    }

    @Test
    public void testUseDefaultSchemaName() throws Exception {
        String schemaName = "";
        String tableName = "tableName";
        String schemaTable = "default.tableName";
        DatabaseMeta databaseMeta = Mockito.spy(new DatabaseMeta(UpdateMetaTest.databaseXML));
        Mockito.doReturn("someValue").when(databaseMeta).getFieldDefinition(ArgumentMatchers.any(ValueMetaInterface.class), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyBoolean());
        Mockito.doReturn(schemaTable).when(databaseMeta).getQuotedSchemaTableCombination(schemaName, tableName);
        ValueMetaInterface valueMeta = Mockito.mock(ValueMetaInterface.class);
        Mockito.when(valueMeta.clone()).thenReturn(Mockito.mock(ValueMetaInterface.class));
        RowMetaInterface rowMetaInterface = Mockito.mock(RowMetaInterface.class);
        Mockito.when(rowMetaInterface.size()).thenReturn(1);
        Mockito.when(rowMetaInterface.searchValueMeta(ArgumentMatchers.anyString())).thenReturn(valueMeta);
        UpdateMeta updateMeta = new UpdateMeta();
        updateMeta.setDatabaseMeta(databaseMeta);
        updateMeta.setTableName(tableName);
        updateMeta.setSchemaName(schemaName);
        updateMeta.setKeyLookup(new String[]{ "KeyLookup1", "KeyLookup2" });
        updateMeta.setKeyStream(new String[]{ "KeyStream1", "KeyStream2" });
        updateMeta.setUpdateLookup(new String[]{ "updateLookup1", "updateLookup2" });
        updateMeta.setUpdateStream(new String[]{ "UpdateStream1", "UpdateStream2" });
        SQLStatement sqlStatement = updateMeta.getSQLStatements(new TransMeta(), Mockito.mock(StepMeta.class), rowMetaInterface, Mockito.mock(Repository.class), Mockito.mock(IMetaStore.class));
        String sql = sqlStatement.getSQL();
        Assert.assertTrue(((StringUtils.countMatches(sql, schemaTable)) == 2));
    }

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testPDI16559() throws Exception {
        UpdateMeta update = new UpdateMeta();
        update.setKeyStream(new String[]{ "field1", "field2", "field3", "field4", "field5" });
        update.setKeyLookup(new String[]{ "lkup1", "lkup2" });
        update.setKeyCondition(new String[]{ "cond1", "cond2", "cond3" });
        update.setKeyStream2(new String[]{ "str21", "str22", "str23", "str24" });
        update.setUpdateLookup(new String[]{ "updlkup1", "updlkup2", "updlkup3", "updlkup4" });
        update.setUpdateStream(new String[]{ "updlkup1", "updlkup2" });
        try {
            String badXml = update.getXML();
            Assert.fail("Before calling afterInjectionSynchronization, should have thrown an ArrayIndexOOB");
        } catch (Exception expected) {
            // Do Nothing
        }
        update.afterInjectionSynchronization();
        // run without a exception
        String ktrXml = update.getXML();
        int targetSz = update.getKeyStream().length;
        Assert.assertEquals(targetSz, update.getKeyLookup().length);
        Assert.assertEquals(targetSz, update.getKeyCondition().length);
        Assert.assertEquals(targetSz, update.getKeyStream2().length);
        targetSz = update.getUpdateLookup().length;
        Assert.assertEquals(targetSz, update.getUpdateStream().length);
    }

    @Test
    public void testReadRepAllocatesSizeProperly() throws Exception {
        Repository rep = Mockito.mock(Repository.class);
        ObjectId objectId = new ObjectId() {
            @Override
            public String getId() {
                return "testId";
            }
        };
        Mockito.when(rep.countNrStepAttributes(objectId, "key_name")).thenReturn(2);
        Mockito.when(rep.countNrStepAttributes(objectId, "key_field")).thenReturn(2);
        Mockito.when(rep.countNrStepAttributes(objectId, "key_condition")).thenReturn(0);
        Mockito.when(rep.countNrStepAttributes(objectId, "key_name2")).thenReturn(0);
        Mockito.when(rep.countNrStepAttributes(objectId, "value_name")).thenReturn(3);
        Mockito.when(rep.countNrStepAttributes(objectId, "value_rename")).thenReturn(2);
        UpdateMeta updateMeta = Mockito.spy(UpdateMeta.class);
        updateMeta.readRep(rep, null, objectId, null);
        Mockito.verify(rep).countNrStepAttributes(objectId, "key_name");
        Mockito.verify(rep).countNrStepAttributes(objectId, "key_field");
        Mockito.verify(rep).countNrStepAttributes(objectId, "key_condition");
        Mockito.verify(rep).countNrStepAttributes(objectId, "key_name2");
        Mockito.verify(rep).countNrStepAttributes(objectId, "value_name");
        Mockito.verify(rep).countNrStepAttributes(objectId, "value_rename");
        Mockito.verify(updateMeta).allocate(2, 3);
    }
}

