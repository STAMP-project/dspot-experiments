/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.synchronizeaftermerge;


import Const.INTERNAL_VARIABLE_CLUSTER_MASTER;
import Const.INTERNAL_VARIABLE_CLUSTER_SIZE;
import Const.INTERNAL_VARIABLE_SLAVE_SERVER_NUMBER;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.MySQLDatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;


public class SynchronizeAfterMergeTest {
    private static final String STEP_NAME = "Sync";

    @Test
    public void initWithCommitSizeVariable() {
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        Mockito.doReturn(SynchronizeAfterMergeTest.STEP_NAME).when(stepMeta).getName();
        Mockito.doReturn(1).when(stepMeta).getCopies();
        SynchronizeAfterMergeMeta smi = Mockito.mock(SynchronizeAfterMergeMeta.class);
        SynchronizeAfterMergeData sdi = Mockito.mock(SynchronizeAfterMergeData.class);
        DatabaseMeta dbMeta = Mockito.mock(DatabaseMeta.class);
        Mockito.doReturn(Mockito.mock(MySQLDatabaseMeta.class)).when(dbMeta).getDatabaseInterface();
        Mockito.doReturn(dbMeta).when(smi).getDatabaseMeta();
        Mockito.doReturn("${commit.size}").when(smi).getCommitSize();
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Mockito.doReturn("1").when(transMeta).getVariable(INTERNAL_VARIABLE_SLAVE_SERVER_NUMBER);
        Mockito.doReturn("2").when(transMeta).getVariable(INTERNAL_VARIABLE_CLUSTER_SIZE);
        Mockito.doReturn("Y").when(transMeta).getVariable(INTERNAL_VARIABLE_CLUSTER_MASTER);
        Mockito.doReturn(stepMeta).when(transMeta).findStep(SynchronizeAfterMergeTest.STEP_NAME);
        SynchronizeAfterMerge step = Mockito.mock(SynchronizeAfterMerge.class);
        Mockito.doCallRealMethod().when(step).setTransMeta(ArgumentMatchers.any(TransMeta.class));
        Mockito.doCallRealMethod().when(step).setStepMeta(ArgumentMatchers.any(StepMeta.class));
        Mockito.doCallRealMethod().when(step).init(ArgumentMatchers.any(StepMetaInterface.class), ArgumentMatchers.any(StepDataInterface.class));
        Mockito.doReturn(stepMeta).when(step).getStepMeta();
        Mockito.doReturn(transMeta).when(step).getTransMeta();
        Mockito.doReturn("120").when(step).environmentSubstitute("${commit.size}");
        step.setTransMeta(transMeta);
        step.setStepMeta(stepMeta);
        step.init(smi, sdi);
        Assert.assertEquals(120, sdi.commitSize);
    }
}

