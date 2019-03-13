/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2019 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.blockingstep;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class BlockingStep_PDI_11344_Test {
    private StepMockHelper<BlockingStepMeta, BlockingStepData> mockHelper;

    @Test
    public void outputRowMetaIsCreateOnce() throws Exception {
        BlockingStep step = new BlockingStep(mockHelper.stepMeta, mockHelper.stepDataInterface, 0, mockHelper.transMeta, mockHelper.trans);
        step = Mockito.spy(step);
        BlockingStepData data = new BlockingStepData();
        step.init(mockHelper.processRowsStepMetaInterface, data);
        step.setInputRowMeta(BlockingStep_PDI_11344_Test.createRowMetaInterface());
        Mockito.doReturn(new Object[0]).when(step).getRow();
        step.processRow(mockHelper.processRowsStepMetaInterface, data);
        RowMetaInterface outputRowMeta = data.outputRowMeta;
        Assert.assertNotNull(outputRowMeta);
        Mockito.doReturn(new Object[0]).when(step).getRow();
        step.processRow(mockHelper.processRowsStepMetaInterface, data);
        Assert.assertTrue(((data.outputRowMeta) == outputRowMeta));
    }
}

