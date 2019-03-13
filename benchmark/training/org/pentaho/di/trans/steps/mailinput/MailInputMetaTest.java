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
/**
 * Tests for MailInputMeta class
 *
 * @author Marc Batchelor - removed useless test case, added load/save tests
 * @see MailInputMeta
 */
package org.pentaho.di.trans.steps.mailinput;


import MailInputField.ColumnDesc.length;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class MailInputMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<MailInputMeta> testMetaClass = MailInputMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class MailInputFieldLoadSaveValidator implements FieldLoadSaveValidator<MailInputField> {
        final Random rand = new Random();

        @Override
        public MailInputField getTestObject() {
            MailInputField rtn = new MailInputField();
            rtn.setName(UUID.randomUUID().toString());
            rtn.setColumn(rand.nextInt(length));
            return rtn;
        }

        @Override
        public boolean validateTestObject(MailInputField testObject, Object actual) {
            if (!(actual instanceof MailInputField)) {
                return false;
            }
            MailInputField another = ((MailInputField) (actual));
            return new EqualsBuilder().append(testObject.getName(), another.getName()).append(testObject.getColumn(), another.getColumn()).isEquals();
        }
    }

    public class StringIntLoadSaveValidator implements FieldLoadSaveValidator<String> {
        final Random rand = new Random();

        int intBound;

        public StringIntLoadSaveValidator() {
            intBound = 0;
        }

        public StringIntLoadSaveValidator(int bounds) {
            if (bounds <= 0) {
                throw new IllegalArgumentException("Bad boundary for StringIntLoadSaveValidator");
            }
            this.intBound = bounds;
        }

        @Override
        public String getTestObject() {
            int someInt = 0;
            if ((intBound) > 0) {
                someInt = rand.nextInt(intBound);
            } else {
                someInt = rand.nextInt();
            }
            return Integer.toString(someInt);
        }

        @Override
        public boolean validateTestObject(String testObject, Object actual) {
            return actual.equals(testObject);
        }
    }
}

