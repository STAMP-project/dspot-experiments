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
package org.pentaho.di.trans.steps.normaliser;


import NormaliserMeta.NormaliserField;
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


public class NormaliserMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<NormaliserMeta> testMetaClass = NormaliserMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    // NormaliserFieldLoadSaveValidator
    public class NormaliserFieldLoadSaveValidator implements FieldLoadSaveValidator<NormaliserMeta.NormaliserField> {
        final Random rand = new Random();

        @Override
        public NormaliserField getTestObject() {
            NormaliserMeta.NormaliserField rtn = new NormaliserMeta.NormaliserField();
            rtn.setName(UUID.randomUUID().toString());
            rtn.setNorm(UUID.randomUUID().toString());
            rtn.setValue(UUID.randomUUID().toString());
            return rtn;
        }

        @Override
        public boolean validateTestObject(NormaliserMeta.NormaliserField testObject, Object actual) {
            if (!(actual instanceof NormaliserMeta.NormaliserField)) {
                return false;
            }
            NormaliserMeta.NormaliserField another = ((NormaliserMeta.NormaliserField) (actual));
            return new EqualsBuilder().append(testObject.getName(), another.getName()).append(testObject.getNorm(), another.getNorm()).append(testObject.getValue(), another.getValue()).isEquals();
        }
    }
}

