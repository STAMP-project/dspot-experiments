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


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests for DatabaseLookup step
 *
 * @author Pavel Sakun
 * @see DatabaseLookup
 */
public class PDI5436Test {
    private StepMockHelper<DatabaseLookupMeta, DatabaseLookupData> smh;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testCacheAllTable() throws KettleException {
        DatabaseLookup stepSpy = Mockito.spy(new DatabaseLookup(smh.stepMeta, smh.stepDataInterface, 0, smh.transMeta, smh.trans));
        Database database = mockDatabase();
        Mockito.doReturn(database).when(stepSpy).getDatabase(ArgumentMatchers.any(DatabaseMeta.class));
        stepSpy.addRowSetToInputRowSets(mockInputRowSet());
        stepSpy.setInputRowMeta(mockInputRowMeta());
        RowSet outputRowSet = new QueueRowSet();
        stepSpy.addRowSetToOutputRowSets(outputRowSet);
        StepMetaInterface meta = mockStepMeta();
        StepDataInterface data = smh.initStepDataInterface;
        Assert.assertTrue("Step init failed", stepSpy.init(meta, data));
        Assert.assertTrue("Error processing row", stepSpy.processRow(meta, data));
        Assert.assertEquals("Cache lookup failed", "value", outputRowSet.getRow()[2]);
    }
}

