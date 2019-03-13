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
package org.pentaho.di.trans.steps.regexeval;


import java.util.regex.Pattern;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class RegexEvalUnitTest {
    private StepMockHelper<RegexEvalMeta, RegexEvalData> stepMockHelper;

    @Test
    public void testOutputIsMuchBiggerThanInputDoesntThrowArrayIndexOutOfBounds() throws KettleException {
        RegexEval regexEval = new RegexEval(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.isAllowCaptureGroupsFlagSet()).thenReturn(true);
        String[] outFields = new String[]{ "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k" };
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getFieldName()).thenReturn(outFields);
        Mockito.when(stepMockHelper.processRowsStepMetaInterface.getMatcher()).thenReturn("\\.+");
        stepMockHelper.processRowsStepDataInterface.pattern = Pattern.compile("(a)(b)(c)(d)(e)(f)(g)(h)(i)(j)(k)");
        Object[] inputRow = new Object[]{  };
        RowSet inputRowSet = stepMockHelper.getMockInputRowSet(inputRow);
        RowMetaInterface mockInputRowMeta = Mockito.mock(RowMetaInterface.class);
        RowMetaInterface mockOutputRoMeta = Mockito.mock(RowMetaInterface.class);
        Mockito.when(mockOutputRoMeta.size()).thenReturn(outFields.length);
        Mockito.when(mockInputRowMeta.size()).thenReturn(0);
        Mockito.when(inputRowSet.getRowMeta()).thenReturn(mockInputRowMeta);
        Mockito.when(mockInputRowMeta.clone()).thenReturn(mockOutputRoMeta);
        Mockito.when(mockInputRowMeta.isNull(ArgumentMatchers.any(Object[].class), ArgumentMatchers.anyInt())).thenReturn(true);
        regexEval.addRowSetToInputRowSets(inputRowSet);
        regexEval.init(stepMockHelper.initStepMetaInterface, stepMockHelper.initStepDataInterface);
        regexEval.processRow(stepMockHelper.processRowsStepMetaInterface, stepMockHelper.processRowsStepDataInterface);
    }
}

