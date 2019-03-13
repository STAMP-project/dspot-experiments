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
package org.pentaho.di.trans.steps.constant;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


public class ConstantTest {
    private StepMockHelper<ConstantMeta, ConstantData> mockHelper;

    private ConstantMeta constantMeta = Mockito.mock(ConstantMeta.class);

    private ConstantData constantData = Mockito.mock(ConstantData.class);

    private RowMetaAndData rowMetaAndData = Mockito.mock(RowMetaAndData.class);

    private Constant constantSpy;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testProcessRow_success() throws Exception {
        Mockito.doReturn(new Object[1]).when(constantSpy).getRow();
        Mockito.doReturn(new RowMeta()).when(constantSpy).getInputRowMeta();
        Mockito.doReturn(new Object[1]).when(rowMetaAndData).getData();
        boolean success = constantSpy.processRow(constantMeta, constantData);
        Assert.assertTrue(success);
    }

    @Test
    public void testProcessRow_fail() throws Exception {
        Mockito.doReturn(null).when(constantSpy).getRow();
        Mockito.doReturn(null).when(constantSpy).getInputRowMeta();
        boolean success = constantSpy.processRow(constantMeta, constantData);
        Assert.assertFalse(success);
    }
}

