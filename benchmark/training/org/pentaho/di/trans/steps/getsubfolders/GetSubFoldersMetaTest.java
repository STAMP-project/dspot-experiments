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
package org.pentaho.di.trans.steps.getsubfolders;


import GetSubFoldersMeta.RequiredFoldersCode.length;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.StringLoadSaveValidator;

import static GetSubFoldersMeta.RequiredFoldersCode;


public class GetSubFoldersMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void getFieldsTest() throws KettleStepException {
        GetSubFoldersMeta stepMeta = new GetSubFoldersMeta();
        String stepName = UUID.randomUUID().toString();
        RowMeta rowMeta = new RowMeta();
        stepMeta.getFields(rowMeta, stepName, null, null, new Variables(), null, null);
        Assert.assertFalse(stepMeta.includeRowNumber());
        Assert.assertEquals(10, rowMeta.size());
        Assert.assertEquals("folderName", rowMeta.getValueMeta(0).getName());
        Assert.assertEquals("short_folderName", rowMeta.getValueMeta(1).getName());
        Assert.assertEquals("path", rowMeta.getValueMeta(2).getName());
        Assert.assertEquals("ishidden", rowMeta.getValueMeta(3).getName());
        Assert.assertEquals("isreadable", rowMeta.getValueMeta(4).getName());
        Assert.assertEquals("iswriteable", rowMeta.getValueMeta(5).getName());
        Assert.assertEquals("lastmodifiedtime", rowMeta.getValueMeta(6).getName());
        Assert.assertEquals("uri", rowMeta.getValueMeta(7).getName());
        Assert.assertEquals("rooturi", rowMeta.getValueMeta(8).getName());
        Assert.assertEquals("childrens", rowMeta.getValueMeta(9).getName());
        stepMeta.setIncludeRowNumber(true);
        rowMeta = new RowMeta();
        stepMeta.getFields(rowMeta, stepName, null, null, new Variables(), null, null);
        Assert.assertTrue(stepMeta.includeRowNumber());
        Assert.assertEquals(11, rowMeta.size());
        Assert.assertEquals("folderName", rowMeta.getValueMeta(0).getName());
        Assert.assertEquals("short_folderName", rowMeta.getValueMeta(1).getName());
        Assert.assertEquals("path", rowMeta.getValueMeta(2).getName());
        Assert.assertEquals("ishidden", rowMeta.getValueMeta(3).getName());
        Assert.assertEquals("isreadable", rowMeta.getValueMeta(4).getName());
        Assert.assertEquals("iswriteable", rowMeta.getValueMeta(5).getName());
        Assert.assertEquals("lastmodifiedtime", rowMeta.getValueMeta(6).getName());
        Assert.assertEquals("uri", rowMeta.getValueMeta(7).getName());
        Assert.assertEquals("rooturi", rowMeta.getValueMeta(8).getName());
        Assert.assertEquals("childrens", rowMeta.getValueMeta(9).getName());
        Assert.assertEquals(null, rowMeta.getValueMeta(10).getName());
        stepMeta.setRowNumberField("MyRowNumber");
        rowMeta = new RowMeta();
        stepMeta.getFields(rowMeta, stepName, null, null, new Variables(), null, null);
        Assert.assertEquals("MyRowNumber", stepMeta.getRowNumberField());
        Assert.assertEquals(11, rowMeta.size());
        Assert.assertEquals("MyRowNumber", rowMeta.getValueMeta(10).getName());
    }

    @Test
    public void loadSaveTest() throws KettleException {
        List<String> attributes = Arrays.asList("rownum", "foldername_dynamic", "rownum_field", "foldername_field", "limit", "name", "file_required");
        Map<String, String> getterMap = new HashMap<String, String>();
        getterMap.put("rownum", "includeRowNumber");
        getterMap.put("foldername_dynamic", "isFoldernameDynamic");
        getterMap.put("foldername_field", "getDynamicFoldernameField");
        getterMap.put("rownum_field", "getRowNumberField");
        getterMap.put("limit", "getRowLimit");
        getterMap.put("name", "getFolderName");
        getterMap.put("file_required", "getFolderRequired");
        Map<String, String> setterMap = new HashMap<String, String>();
        setterMap.put("rownum", "setIncludeRowNumber");
        setterMap.put("foldername_dynamic", "setFolderField");
        setterMap.put("foldername_field", "setDynamicFoldernameField");
        setterMap.put("rownum_field", "setRowNumberField");
        setterMap.put("limit", "setRowLimit");
        setterMap.put("name", "setFolderName");
        setterMap.put("file_required", "setFolderRequired");
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorAttributeMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldLoadSaveValidatorAttributeMap.put("file_required", new ArrayLoadSaveValidator<String>(new GetSubFoldersMetaTest.FileRequiredFieldLoadSaveValidator(), 50));
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorTypeMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldLoadSaveValidatorTypeMap.put(String[].class.getCanonicalName(), new ArrayLoadSaveValidator<String>(new StringLoadSaveValidator(), 50));
        LoadSaveTester tester = new LoadSaveTester(GetSubFoldersMeta.class, attributes, getterMap, setterMap, fieldLoadSaveValidatorAttributeMap, fieldLoadSaveValidatorTypeMap);
        tester.testSerialization();
    }

    public class FileRequiredFieldLoadSaveValidator implements FieldLoadSaveValidator<String> {
        @Override
        public String getTestObject() {
            return RequiredFoldersCode[new Random().nextInt(length)];
        }

        @Override
        public boolean validateTestObject(String testObject, Object actual) {
            return testObject.equals(actual);
        }
    }
}

