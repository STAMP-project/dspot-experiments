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


import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Test for PDI-3499
 *
 * @author Pavel Sakun
 * @see InsertUpdate
 */
public class PDI3499Test {
    StepMockHelper<InsertUpdateMeta, InsertUpdateData> smh;

    @Test
    public void testDateLazyConversion() throws KettleException {
        Database db = Mockito.mock(Database.class);
        RowMeta returnRowMeta = new RowMeta();
        Mockito.doReturn(new Object[]{ new Timestamp(System.currentTimeMillis()) }).when(db).getLookup(ArgumentMatchers.any(PreparedStatement.class));
        returnRowMeta.addValueMeta(new ValueMetaDate("TimeStamp"));
        Mockito.doReturn(returnRowMeta).when(db).getReturnRowMeta();
        ValueMetaString storageMetadata = new ValueMetaString("Date");
        storageMetadata.setConversionMask("yyyy-MM-dd");
        ValueMetaDate valueMeta = new ValueMetaDate("Date");
        valueMeta.setStorageType(STORAGE_TYPE_BINARY_STRING);
        valueMeta.setStorageMetadata(storageMetadata);
        RowMeta inputRowMeta = new RowMeta();
        inputRowMeta.addValueMeta(valueMeta);
        InsertUpdateMeta stepMeta = smh.processRowsStepMetaInterface;
        Mockito.doReturn(new Boolean[]{ true }).when(stepMeta).getUpdate();
        InsertUpdateData stepData = smh.processRowsStepDataInterface;
        stepData.lookupParameterRowMeta = inputRowMeta;
        stepData.db = db;
        stepData.keynrs = stepData.valuenrs = new int[]{ 0 };
        stepData.keynrs2 = new int[]{ -1 };
        stepData.updateParameterRowMeta = Mockito.when(Mockito.mock(RowMeta.class).size()).thenReturn(2).getMock();
        InsertUpdate step = new InsertUpdate(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans);
        step.setInputRowMeta(inputRowMeta);
        step.addRowSetToInputRowSets(smh.getMockInputRowSet(new Object[]{ "2013-12-20".getBytes() }));
        step.init(smh.initStepMetaInterface, smh.initStepDataInterface);
        step.first = false;
        step.processRow(stepMeta, stepData);
    }
}

