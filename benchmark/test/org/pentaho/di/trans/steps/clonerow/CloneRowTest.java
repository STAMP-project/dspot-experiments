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
package org.pentaho.di.trans.steps.clonerow;


import java.util.Collections;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 *
 *
 * @author Andrey Khayrutdinov
 */
public class CloneRowTest {
    private StepMockHelper<CloneRowMeta, CloneRowData> stepMockHelper;

    @Test(expected = KettleException.class)
    public void nullNrCloneField() throws Exception {
        CloneRow step = new CloneRow(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        step.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        RowMetaInterface inputRowMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(inputRowMeta.getInteger(ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyInt())).thenReturn(null);
        RowSet inputRowSet = stepMockHelper.getMockInputRowSet(new Integer[]{ null });
        Mockito.when(inputRowSet.getRowMeta()).thenReturn(inputRowMeta);
        step.setInputRowSets(Collections.singletonList(inputRowSet));
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isNrCloneInField()).thenReturn(true);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getNrCloneField()).thenReturn("field");
        step.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
    }
}

