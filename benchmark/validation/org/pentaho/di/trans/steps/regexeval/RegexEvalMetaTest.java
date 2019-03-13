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
package org.pentaho.di.trans.steps.regexeval;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


public class RegexEvalMetaTest implements InitializerInterface<StepMetaInterface> {
    RowMetaInterface mockInputRowMeta;

    VariableSpace mockVariableSpace;

    LoadSaveTester loadSaveTester;

    Class<RegexEvalMeta> testMetaClass = RegexEvalMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testGetFieldsReplacesResultFieldIfItExists() throws KettleStepException {
        RegexEvalMeta regexEvalMeta = new RegexEvalMeta();
        String name = "TEST_NAME";
        String resultField = "result";
        regexEvalMeta.setResultFieldName(resultField);
        Mockito.when(mockInputRowMeta.indexOfValue(resultField)).thenReturn(0);
        ValueMetaInterface mockValueMeta = Mockito.mock(ValueMetaInterface.class);
        String mockName = "MOCK_NAME";
        Mockito.when(mockValueMeta.getName()).thenReturn(mockName);
        Mockito.when(mockInputRowMeta.getValueMeta(0)).thenReturn(mockValueMeta);
        regexEvalMeta.setReplacefields(true);
        regexEvalMeta.getFields(mockInputRowMeta, name, null, null, mockVariableSpace, null, null);
        ArgumentCaptor<ValueMetaInterface> captor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(mockInputRowMeta).setValueMeta(ArgumentMatchers.eq(0), captor.capture());
        Assert.assertEquals(mockName, captor.getValue().getName());
    }

    @Test
    public void testGetFieldsAddsResultFieldIfDoesntExist() throws KettleStepException {
        RegexEvalMeta regexEvalMeta = new RegexEvalMeta();
        String name = "TEST_NAME";
        String resultField = "result";
        regexEvalMeta.setResultFieldName(resultField);
        Mockito.when(mockInputRowMeta.indexOfValue(resultField)).thenReturn((-1));
        ValueMetaInterface mockValueMeta = Mockito.mock(ValueMetaInterface.class);
        String mockName = "MOCK_NAME";
        Mockito.when(mockVariableSpace.environmentSubstitute(resultField)).thenReturn(mockName);
        Mockito.when(mockInputRowMeta.getValueMeta(0)).thenReturn(mockValueMeta);
        regexEvalMeta.setReplacefields(true);
        regexEvalMeta.getFields(mockInputRowMeta, name, null, null, mockVariableSpace, null, null);
        ArgumentCaptor<ValueMetaInterface> captor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(mockInputRowMeta).addValueMeta(captor.capture());
        Assert.assertEquals(mockName, captor.getValue().getName());
    }

    @Test
    public void testGetFieldsReplacesFieldIfItExists() throws KettleStepException {
        RegexEvalMeta regexEvalMeta = new RegexEvalMeta();
        String name = "TEST_NAME";
        regexEvalMeta.allocate(1);
        String fieldName = "fieldname";
        // CHECKSTYLE:Indentation:OFF
        regexEvalMeta.getFieldName()[0] = fieldName;
        Mockito.when(mockInputRowMeta.indexOfValue(fieldName)).thenReturn(0);
        ValueMetaInterface mockValueMeta = Mockito.mock(ValueMetaInterface.class);
        String mockName = "MOCK_NAME";
        Mockito.when(mockValueMeta.getName()).thenReturn(mockName);
        Mockito.when(mockInputRowMeta.getValueMeta(0)).thenReturn(mockValueMeta);
        regexEvalMeta.setReplacefields(true);
        regexEvalMeta.setAllowCaptureGroupsFlag(true);
        regexEvalMeta.getFields(mockInputRowMeta, name, null, null, mockVariableSpace, null, null);
        ArgumentCaptor<ValueMetaInterface> captor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(mockInputRowMeta).setValueMeta(ArgumentMatchers.eq(0), captor.capture());
        Assert.assertEquals(mockName, captor.getValue().getName());
    }

    @Test
    public void testGetFieldsAddsFieldIfDoesntExist() throws KettleStepException {
        RegexEvalMeta regexEvalMeta = new RegexEvalMeta();
        String name = "TEST_NAME";
        regexEvalMeta.allocate(1);
        String fieldName = "fieldname";
        regexEvalMeta.getFieldName()[0] = fieldName;
        Mockito.when(mockInputRowMeta.indexOfValue(fieldName)).thenReturn((-1));
        ValueMetaInterface mockValueMeta = Mockito.mock(ValueMetaInterface.class);
        String mockName = "MOCK_NAME";
        Mockito.when(mockVariableSpace.environmentSubstitute(fieldName)).thenReturn(mockName);
        Mockito.when(mockInputRowMeta.getValueMeta(0)).thenReturn(mockValueMeta);
        regexEvalMeta.setReplacefields(true);
        regexEvalMeta.setAllowCaptureGroupsFlag(true);
        regexEvalMeta.getFields(mockInputRowMeta, name, null, null, mockVariableSpace, null, null);
        ArgumentCaptor<ValueMetaInterface> captor = ArgumentCaptor.forClass(ValueMetaInterface.class);
        Mockito.verify(mockInputRowMeta).addValueMeta(captor.capture());
        Assert.assertEquals(fieldName, captor.getValue().getName());
    }

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }
}

