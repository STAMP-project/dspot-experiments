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
package org.pentaho.di.trans.steps.stringoperations;


import ValueMetaInterface.STORAGE_TYPE_BINARY_STRING;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


/**
 * User: Dzmitry Stsiapanau Date: 2/3/14 Time: 5:41 PM
 */
public class StringOperationsMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<StringOperationsMeta> testMetaClass = StringOperationsMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testGetFields() throws Exception {
        StringOperationsMeta meta = new StringOperationsMeta();
        meta.allocate(1);
        meta.setFieldInStream(new String[]{ "field1" });
        RowMetaInterface rowMetaInterface = new RowMeta();
        ValueMetaInterface valueMeta = new ValueMetaString("field1");
        valueMeta.setStorageMetadata(new ValueMetaString("field1"));
        valueMeta.setStorageType(STORAGE_TYPE_BINARY_STRING);
        rowMetaInterface.addValueMeta(valueMeta);
        VariableSpace space = Mockito.mock(VariableSpace.class);
        meta.getFields(rowMetaInterface, "STRING_OPERATIONS", null, null, space, null, null);
        RowMetaInterface expectedRowMeta = new RowMeta();
        expectedRowMeta.addValueMeta(new ValueMetaString("field1"));
        Assert.assertEquals(expectedRowMeta.toString(), rowMetaInterface.toString());
    }
}

