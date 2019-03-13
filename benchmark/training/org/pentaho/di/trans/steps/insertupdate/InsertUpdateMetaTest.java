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
package org.pentaho.di.trans.steps.insertupdate;


import java.sql.Connection;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class InsertUpdateMetaTest {
    LoadSaveTester loadSaveTester;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMeta stepMeta;

    private InsertUpdate upd;

    private InsertUpdateData ud;

    private InsertUpdateMeta umi;

    private StepMockHelper<InsertUpdateMeta, InsertUpdateData> mockHelper;

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
    public void testProvidesModeler() throws Exception {
        InsertUpdateMeta insertUpdateMeta = new InsertUpdateMeta();
        insertUpdateMeta.setUpdateLookup(new String[]{ "f1", "f2", "f3" });
        insertUpdateMeta.setUpdateStream(new String[]{ "s4", "s5", "s6" });
        InsertUpdateData tableOutputData = new InsertUpdateData();
        tableOutputData.insertRowMeta = Mockito.mock(RowMeta.class);
        Assert.assertEquals(tableOutputData.insertRowMeta, insertUpdateMeta.getRowMeta(tableOutputData));
        Assert.assertEquals(3, insertUpdateMeta.getDatabaseFields().size());
        Assert.assertEquals("f1", insertUpdateMeta.getDatabaseFields().get(0));
        Assert.assertEquals("f2", insertUpdateMeta.getDatabaseFields().get(1));
        Assert.assertEquals("f3", insertUpdateMeta.getDatabaseFields().get(2));
        Assert.assertEquals(3, insertUpdateMeta.getStreamFields().size());
        Assert.assertEquals("s4", insertUpdateMeta.getStreamFields().get(0));
        Assert.assertEquals("s5", insertUpdateMeta.getStreamFields().get(1));
        Assert.assertEquals("s6", insertUpdateMeta.getStreamFields().get(2));
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
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testErrorProcessRow() throws KettleException {
        Mockito.when(mockHelper.logChannelInterfaceFactory.create(Mockito.any(), Mockito.any(LoggingObjectInterface.class))).thenReturn(mockHelper.logChannelInterface);
        Mockito.when(mockHelper.stepMeta.getStepMetaInterface()).thenReturn(new InsertUpdateMeta());
        InsertUpdate insertUpdateStep = new InsertUpdate(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        insertUpdateStep = Mockito.spy(insertUpdateStep);
        Mockito.doReturn(new Object[]{  }).when(insertUpdateStep).getRow();
        insertUpdateStep.first = false;
        mockHelper.processRowsStepDataInterface.lookupParameterRowMeta = Mockito.mock(RowMetaInterface.class);
        mockHelper.processRowsStepDataInterface.keynrs = new int[]{  };
        mockHelper.processRowsStepDataInterface.db = Mockito.mock(Database.class);
        mockHelper.processRowsStepDataInterface.valuenrs = new int[]{  };
        Mockito.doThrow(new KettleStepException("Test exception")).when(insertUpdateStep).putRow(Mockito.any(), Mockito.any());
        boolean result = insertUpdateStep.processRow(mockHelper.processRowsStepMetaInterface, mockHelper.processRowsStepDataInterface);
        Assert.assertFalse(result);
    }

    // PDI-16349
    @Test
    public void keyStream2ProcessRow() throws KettleException {
        InsertUpdate insertUpdateStep = new InsertUpdate(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        insertUpdateStep.setInputRowMeta(Mockito.mock(RowMetaInterface.class));
        insertUpdateStep = Mockito.spy(insertUpdateStep);
        InsertUpdateMeta insertUpdateMeta = new InsertUpdateMeta();
        insertUpdateMeta.setKeyStream(new String[]{ "test_field" });
        insertUpdateMeta.setKeyCondition(new String[]{ "test_condition" });
        insertUpdateMeta.setKeyStream2(new String[]{  });
        insertUpdateMeta.setUpdateLookup(new String[]{  });
        insertUpdateMeta.setKeyLookup(new String[]{  });
        insertUpdateMeta.setUpdateBypassed(true);
        insertUpdateMeta.setDatabaseMeta(Mockito.mock(DatabaseMeta.class));
        Database database = Mockito.mock(Database.class);
        mockHelper.processRowsStepDataInterface.db = database;
        Mockito.doReturn(Mockito.mock(Connection.class)).when(database).getConnection();
        Mockito.doNothing().when(insertUpdateStep).lookupValues(Mockito.any(), Mockito.any());
        Mockito.doNothing().when(insertUpdateStep).putRow(Mockito.any(), Mockito.any());
        Mockito.doReturn(new Object[]{  }).when(insertUpdateStep).getRow();
        insertUpdateStep.first = true;
        insertUpdateMeta.afterInjectionSynchronization();
        // run without a exception
        insertUpdateStep.processRow(insertUpdateMeta, mockHelper.processRowsStepDataInterface);
        Assert.assertEquals(insertUpdateMeta.getKeyStream().length, insertUpdateMeta.getKeyStream2().length);
    }
}

