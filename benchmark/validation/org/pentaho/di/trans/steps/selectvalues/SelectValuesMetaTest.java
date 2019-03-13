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
package org.pentaho.di.trans.steps.selectvalues;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.ArrayLoadSaveValidator;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.selectvalues.SelectValuesMeta.SelectField;

import static SelectValuesMeta.UNDEFINED;


public class SelectValuesMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private static final String FIRST_FIELD = "FIRST_FIELD";

    private static final String SECOND_FIELD = "SECOND_FIELD";

    private SelectValuesMeta selectValuesMeta;

    @Test
    public void loadSaveTest() throws KettleException {
        List<String> attributes = Arrays.asList("selectFields", "deleteName");
        SelectField selectField = new SelectField();
        selectField.setName("TEST_NAME");
        selectField.setRename("TEST_RENAME");
        selectField.setLength(2);
        selectField.setPrecision(2);
        Map<String, FieldLoadSaveValidator<?>> fieldLoadSaveValidatorTypeMap = new HashMap<String, FieldLoadSaveValidator<?>>();
        fieldLoadSaveValidatorTypeMap.put(SelectField[].class.getCanonicalName(), new ArrayLoadSaveValidator<SelectField>(new SelectValuesMetaTest.SelectFieldLoadSaveValidator(selectField), 2));
        LoadSaveTester<SelectValuesMeta> tester = new LoadSaveTester(SelectValuesMeta.class, attributes, new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, FieldLoadSaveValidator<?>>(), fieldLoadSaveValidatorTypeMap);
        tester.testSerialization();
    }

    @Test
    public void setSelectName() {
        selectValuesMeta.setSelectName(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD });
        Assert.assertArrayEquals(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD }, selectValuesMeta.getSelectName());
    }

    @Test
    public void setSelectName_getOtherFields() {
        selectValuesMeta.setSelectName(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD });
        Assert.assertArrayEquals(new String[]{ null, null }, selectValuesMeta.getSelectRename());
        Assert.assertArrayEquals(new int[]{ UNDEFINED, UNDEFINED }, selectValuesMeta.getSelectLength());
        Assert.assertArrayEquals(new int[]{ UNDEFINED, UNDEFINED }, selectValuesMeta.getSelectPrecision());
    }

    @Test
    public void setSelectName_smallerThanPrevious() {
        selectValuesMeta.setSelectName(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD });
        selectValuesMeta.setSelectName(new String[]{ SelectValuesMetaTest.FIRST_FIELD });
        Assert.assertArrayEquals(new String[]{ SelectValuesMetaTest.FIRST_FIELD }, selectValuesMeta.getSelectName());
    }

    @Test
    public void getSelectName() {
        Assert.assertArrayEquals(new String[0], selectValuesMeta.getSelectName());
    }

    @Test
    public void setSelectRename() {
        selectValuesMeta.setSelectRename(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD });
        Assert.assertArrayEquals(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD }, selectValuesMeta.getSelectRename());
    }

    @Test
    public void setSelectRename_getOtherFields() {
        selectValuesMeta.setSelectRename(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD });
        Assert.assertArrayEquals(new String[]{ null, null }, selectValuesMeta.getSelectName());
        Assert.assertArrayEquals(new int[]{ UNDEFINED, UNDEFINED }, selectValuesMeta.getSelectLength());
        Assert.assertArrayEquals(new int[]{ UNDEFINED, UNDEFINED }, selectValuesMeta.getSelectPrecision());
    }

    @Test
    public void setSelectRename_smallerThanPrevious() {
        selectValuesMeta.setSelectRename(new String[]{ SelectValuesMetaTest.FIRST_FIELD, SelectValuesMetaTest.SECOND_FIELD });
        selectValuesMeta.setSelectRename(new String[]{ SelectValuesMetaTest.FIRST_FIELD });
        Assert.assertArrayEquals(new String[]{ SelectValuesMetaTest.FIRST_FIELD, null }, selectValuesMeta.getSelectRename());
    }

    @Test
    public void getSelectRename() {
        Assert.assertArrayEquals(new String[0], selectValuesMeta.getSelectRename());
    }

    @Test
    public void setSelectLength() {
        selectValuesMeta.setSelectLength(new int[]{ 1, 2 });
        Assert.assertArrayEquals(new int[]{ 1, 2 }, selectValuesMeta.getSelectLength());
    }

    @Test
    public void setSelectLength_getOtherFields() {
        selectValuesMeta.setSelectLength(new int[]{ 1, 2 });
        Assert.assertArrayEquals(new String[]{ null, null }, selectValuesMeta.getSelectName());
        Assert.assertArrayEquals(new String[]{ null, null }, selectValuesMeta.getSelectRename());
        Assert.assertArrayEquals(new int[]{ UNDEFINED, UNDEFINED }, selectValuesMeta.getSelectPrecision());
    }

    @Test
    public void setSelectLength_smallerThanPrevious() {
        selectValuesMeta.setSelectLength(new int[]{ 1, 2 });
        selectValuesMeta.setSelectLength(new int[]{ 1 });
        Assert.assertArrayEquals(new int[]{ 1, UNDEFINED }, selectValuesMeta.getSelectLength());
    }

    @Test
    public void getSelectLength() {
        Assert.assertArrayEquals(new int[0], selectValuesMeta.getSelectLength());
    }

    @Test
    public void setSelectPrecision() {
        selectValuesMeta.setSelectPrecision(new int[]{ 1, 2 });
        Assert.assertArrayEquals(new int[]{ 1, 2 }, selectValuesMeta.getSelectPrecision());
    }

    @Test
    public void setSelectPrecision_getOtherFields() {
        selectValuesMeta.setSelectPrecision(new int[]{ 1, 2 });
        Assert.assertArrayEquals(new String[]{ null, null }, selectValuesMeta.getSelectName());
        Assert.assertArrayEquals(new String[]{ null, null }, selectValuesMeta.getSelectRename());
        Assert.assertArrayEquals(new int[]{ UNDEFINED, UNDEFINED }, selectValuesMeta.getSelectLength());
    }

    @Test
    public void setSelectPrecision_smallerThanPrevious() {
        selectValuesMeta.setSelectPrecision(new int[]{ 1, 2 });
        selectValuesMeta.setSelectPrecision(new int[]{ 1 });
        Assert.assertArrayEquals(new int[]{ 1, UNDEFINED }, selectValuesMeta.getSelectPrecision());
    }

    @Test
    public void setSelectFieldsNull() {
        selectValuesMeta.setSelectFields(null);
        Assert.assertNotNull(selectValuesMeta.getSelectFields());
        Assert.assertEquals(0, selectValuesMeta.getSelectFields().length);
    }

    @Test
    public void setDeleteNameNull() {
        selectValuesMeta.setDeleteName(null);
        Assert.assertNotNull(selectValuesMeta.getDeleteName());
        Assert.assertEquals(0, selectValuesMeta.getDeleteName().length);
    }

    @Test
    public void setMetaNull() {
        selectValuesMeta.setMeta(null);
        Assert.assertNotNull(selectValuesMeta.getMeta());
        Assert.assertEquals(0, selectValuesMeta.getMeta().length);
    }

    @Test
    public void getSelectPrecision() {
        Assert.assertArrayEquals(new int[0], selectValuesMeta.getSelectPrecision());
    }

    @Test
    public void testMetaDataFieldsRenameConflict() throws Exception {
        RowMetaInterface rowMeta = new RowMeta();
        rowMeta.addValueMeta(new ValueMetaString("A"));
        rowMeta.addValueMeta(new ValueMetaString("B"));
        SelectMetadataChange change = new SelectMetadataChange(selectValuesMeta);
        change.setName("A");
        change.setRename("B");
        selectValuesMeta.setMeta(new SelectMetadataChange[]{ change });
        selectValuesMeta.getMetadataFields(rowMeta, "select values", null);
        Assert.assertEquals("rename conflict", "B_1", rowMeta.getValueMeta(0).getName());
    }

    public static class SelectFieldLoadSaveValidator implements FieldLoadSaveValidator<SelectField> {
        private final SelectField defaultValue;

        public SelectFieldLoadSaveValidator(SelectField defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public SelectField getTestObject() {
            return defaultValue;
        }

        @Override
        public boolean validateTestObject(SelectField testObject, Object actual) {
            return EqualsBuilder.reflectionEquals(testObject, actual);
        }
    }
}

