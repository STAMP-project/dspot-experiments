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
package org.pentaho.di.trans.steps.exceloutput;


import java.util.Random;
import java.util.UUID;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class ExcelOutputMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<ExcelOutputMeta> testMetaClass = ExcelOutputMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class ExcelFieldLoadSaveValidator implements FieldLoadSaveValidator<ExcelField> {
        final Random rand = new Random();

        @Override
        public ExcelField getTestObject() {
            ExcelField rtn = new ExcelField();
            rtn.setFormat(UUID.randomUUID().toString());
            rtn.setName(UUID.randomUUID().toString());
            rtn.setType(rand.nextInt(7));
            return rtn;
        }

        @Override
        public boolean validateTestObject(ExcelField testObject, Object actual) {
            if (!(actual instanceof ExcelField)) {
                return false;
            }
            ExcelField actualInput = ((ExcelField) (actual));
            return testObject.toString().equals(actualInput.toString());
        }
    }
}

