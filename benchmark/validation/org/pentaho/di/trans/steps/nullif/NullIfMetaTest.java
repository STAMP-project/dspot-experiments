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
package org.pentaho.di.trans.steps.nullif;


import Const.KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.pentaho.di.trans.steps.nullif.NullIfMeta.Field;


public class NullIfMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    LoadSaveTester loadSaveTester;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void setFieldValueTest() {
        Field field = new Field();
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "N");
        field.setFieldValue("theValue");
        Assert.assertEquals("theValue", field.getFieldValue());
    }

    @Test
    public void setFieldValueNullTest() {
        Field field = new Field();
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "N");
        field.setFieldValue(null);
        Assert.assertEquals(null, field.getFieldValue());
    }

    @Test
    public void setFieldValueNullWithEmptyStringsDiffersFromNullTest() {
        Field field = new Field();
        System.setProperty(KETTLE_EMPTY_STRING_DIFFERS_FROM_NULL, "Y");
        field.setFieldValue(null);
        Assert.assertEquals("", field.getFieldValue());
    }

    public static class NullIfFieldLoadSaveValidator implements FieldLoadSaveValidator<Field> {
        private final Field defaultValue;

        public NullIfFieldLoadSaveValidator(Field defaultValue) {
            this.defaultValue = defaultValue;
        }

        @Override
        public Field getTestObject() {
            return defaultValue;
        }

        @Override
        public boolean validateTestObject(Field testObject, Object actual) {
            return EqualsBuilder.reflectionEquals(testObject, actual);
        }
    }
}

