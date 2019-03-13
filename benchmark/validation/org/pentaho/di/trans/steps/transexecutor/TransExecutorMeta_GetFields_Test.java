/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.transexecutor;


import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMeta;


public class TransExecutorMeta_GetFields_Test {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private TransExecutorMeta meta;

    private StepMeta executionResult;

    private StepMeta resultFiles;

    private StepMeta outputRows;

    @Test
    public void getFieldsForExecutionResults() throws Exception {
        RowMetaInterface mock = invokeGetFieldsWith(executionResult);
        Mockito.verify(mock, Mockito.times(3)).addValueMeta(ArgumentMatchers.any(ValueMetaInterface.class));
    }

    @Test
    public void getFieldsForResultFiles() throws Exception {
        RowMetaInterface mock = invokeGetFieldsWith(resultFiles);
        Mockito.verify(mock).addValueMeta(ArgumentMatchers.any(ValueMetaInterface.class));
    }

    @Test
    public void getFieldsForInternalTransformationOutputRows() throws Exception {
        RowMetaInterface mock = invokeGetFieldsWith(outputRows);
        Mockito.verify(mock).addValueMeta(ArgumentMatchers.any(ValueMetaInterface.class));
    }
}

