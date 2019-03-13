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
package org.pentaho.di.trans.steps.denormaliser;


import DenormaliserTargetField.typeAggrDesc.length;
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


public class DenormalizerMetaTest implements InitializerInterface<StepMetaInterface> {
    LoadSaveTester loadSaveTester;

    Class<DenormaliserMeta> testMetaClass = DenormaliserMeta.class;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class DenormaliserTargetFieldLoadSaveValidator implements FieldLoadSaveValidator<DenormaliserTargetField> {
        final Random rand = new Random();

        @Override
        public DenormaliserTargetField getTestObject() {
            DenormaliserTargetField rtn = new DenormaliserTargetField();
            rtn.setFieldName(UUID.randomUUID().toString());
            rtn.setKeyValue(UUID.randomUUID().toString());
            rtn.setTargetCurrencySymbol(UUID.randomUUID().toString());
            rtn.setTargetGroupingSymbol(UUID.randomUUID().toString());
            rtn.setTargetName(UUID.randomUUID().toString());
            rtn.setTargetType(rand.nextInt(7));
            rtn.setTargetPrecision(rand.nextInt(9));
            rtn.setTargetNullString(UUID.randomUUID().toString());
            rtn.setTargetLength(rand.nextInt(50));
            rtn.setTargetDecimalSymbol(UUID.randomUUID().toString());
            rtn.setTargetAggregationType(rand.nextInt(length));
            return rtn;
        }

        @Override
        public boolean validateTestObject(DenormaliserTargetField testObject, Object actual) {
            if (!(actual instanceof DenormaliserTargetField)) {
                return false;
            }
            DenormaliserTargetField another = ((DenormaliserTargetField) (actual));
            return new EqualsBuilder().append(testObject.getFieldName(), another.getFieldName()).append(testObject.getKeyValue(), another.getKeyValue()).append(testObject.getTargetName(), another.getTargetName()).append(testObject.getTargetType(), another.getTargetType()).append(testObject.getTargetLength(), another.getTargetLength()).append(testObject.getTargetPrecision(), another.getTargetPrecision()).append(testObject.getTargetCurrencySymbol(), another.getTargetCurrencySymbol()).append(testObject.getTargetDecimalSymbol(), another.getTargetDecimalSymbol()).append(testObject.getTargetGroupingSymbol(), another.getTargetGroupingSymbol()).append(testObject.getTargetNullString(), another.getTargetNullString()).append(testObject.getTargetFormat(), another.getTargetFormat()).append(testObject.getTargetAggregationType(), another.getTargetAggregationType()).isEquals();
        }
    }
}

