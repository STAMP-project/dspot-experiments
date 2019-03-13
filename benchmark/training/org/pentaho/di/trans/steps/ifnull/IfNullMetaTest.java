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
package org.pentaho.di.trans.steps.ifnull;


import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.ifnull.IfNullMeta.Fields;
import org.pentaho.di.trans.steps.ifnull.IfNullMeta.ValueTypes;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class IfNullMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    @Test
    public void testLoadSave() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testSetDefault() throws Exception {
        IfNullMeta inm = new IfNullMeta();
        inm.setDefault();
        Assert.assertTrue((((inm.getValueTypes()) != null) && ((inm.getValueTypes().length) == 0)));
        Assert.assertTrue((((inm.getFields()) != null) && ((inm.getFields().length) == 0)));
        Assert.assertFalse(inm.isSelectFields());
        Assert.assertFalse(inm.isSelectValuesType());
    }

    public static class FieldsLoadSaveValidator implements FieldLoadSaveValidator<Fields> {
        private final Fields defaultValue;

        public FieldsLoadSaveValidator(Fields defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Fields getTestObject() {
            return defaultValue;
        }

        @Override
        public boolean validateTestObject(Fields testObject, Object actual) {
            return EqualsBuilder.reflectionEquals(testObject, actual);
        }
    }

    public static class ValueTypesLoadSaveValidator implements FieldLoadSaveValidator<ValueTypes> {
        private final ValueTypes defaultValue;

        public ValueTypesLoadSaveValidator(ValueTypes defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public ValueTypes getTestObject() {
            return defaultValue;
        }

        @Override
        public boolean validateTestObject(ValueTypes testObject, Object actual) {
            return EqualsBuilder.reflectionEquals(testObject, actual);
        }
    }
}

