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
package org.pentaho.di.trans.steps.abort;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class AbortTest {
    private StepMockHelper<AbortMeta, StepDataInterface> stepMockHelper;

    @Test
    public void testAbortDoesntAbortWithoutInputRow() throws KettleException {
        Abort abort = new Abort(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        abort.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        abort.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet());
        Assert.assertFalse(abort.isStopped());
        abort.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
        Mockito.verify(stepMockHelper.trans, Mockito.never()).stopAll();
        Assert.assertFalse(abort.isStopped());
    }

    @Test
    public void testAbortAbortsWithInputRow() throws KettleException {
        Abort abort = new Abort(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        abort.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        abort.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet(new Object[]{  }));
        Assert.assertFalse(abort.isStopped());
        abort.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
        Mockito.verify(stepMockHelper.trans, Mockito.times(1)).stopAll();
        Assert.assertTrue(abort.isStopped());
    }

    @Test
    public void testSafeStop() throws KettleException {
        Abort abort = new Abort(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isSafeStop()).thenReturn(true);
        abort.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        abort.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet(new Object[]{  }));
        abort.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
        Mockito.verify(stepMockHelper.trans).safeStop();
    }

    @Test
    public void testAbortWithError() throws KettleException {
        Abort abort = new Abort(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isSafeStop()).thenReturn(false);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isAbortWithError()).thenReturn(true);
        abort.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        abort.addRowSetToInputRowSets(stepMockHelper.getMockInputRowSet(new Object[]{  }));
        abort.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
        Assert.assertEquals(1L, abort.getErrors());
        Mockito.verify(stepMockHelper.trans).stopAll();
    }
}

