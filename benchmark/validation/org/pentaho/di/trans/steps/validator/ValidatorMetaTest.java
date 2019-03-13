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
package org.pentaho.di.trans.steps.validator;


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


public class ValidatorMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<ValidatorMeta> testMetaClass = ValidatorMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    // ValidationLoadSaveValidator
    public class ValidationLoadSaveValidator implements FieldLoadSaveValidator<Validation> {
        final Random rand = new Random();

        @Override
        public Validation getTestObject() {
            Validation rtn = new Validation();
            rtn.setName(UUID.randomUUID().toString());
            rtn.setFieldName(UUID.randomUUID().toString());
            rtn.setMaximumLength(UUID.randomUUID().toString());
            rtn.setMinimumLength(UUID.randomUUID().toString());
            rtn.setNullAllowed(rand.nextBoolean());
            rtn.setOnlyNullAllowed(rand.nextBoolean());
            rtn.setOnlyNumericAllowed(rand.nextBoolean());
            rtn.setDataType(rand.nextInt(9));
            rtn.setDataTypeVerified(rand.nextBoolean());
            rtn.setConversionMask(UUID.randomUUID().toString());
            rtn.setDecimalSymbol(UUID.randomUUID().toString());
            rtn.setGroupingSymbol(UUID.randomUUID().toString());
            rtn.setMinimumValue(UUID.randomUUID().toString());
            rtn.setMaximumValue(UUID.randomUUID().toString());
            rtn.setAllowedValues(new String[]{ UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString() });
            rtn.setSourcingValues(rand.nextBoolean());
            rtn.setSourcingStepName(UUID.randomUUID().toString());
            rtn.setSourcingStep(null);
            rtn.setSourcingField(UUID.randomUUID().toString());
            rtn.setStartString(UUID.randomUUID().toString());
            rtn.setStartStringNotAllowed(UUID.randomUUID().toString());
            rtn.setEndString(UUID.randomUUID().toString());
            rtn.setEndStringNotAllowed(UUID.randomUUID().toString());
            rtn.setRegularExpression(UUID.randomUUID().toString());
            rtn.setRegularExpressionNotAllowed(UUID.randomUUID().toString());
            rtn.setErrorCode(UUID.randomUUID().toString());
            rtn.setErrorDescription(UUID.randomUUID().toString());
            return rtn;
        }

        @Override
        public boolean validateTestObject(Validation testObject, Object actual) {
            if (!(actual instanceof Validation)) {
                return false;
            }
            Validation actualInput = ((Validation) (actual));
            return testObject.getXML().equals(actualInput.getXML());
        }
    }
}

