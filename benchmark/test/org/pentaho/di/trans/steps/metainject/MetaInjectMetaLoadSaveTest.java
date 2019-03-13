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
package org.pentaho.di.trans.steps.metainject;


import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class MetaInjectMetaLoadSaveTest {
    LoadSaveTester loadSaveTester;

    Class<MetaInjectMeta> testMetaClass = MetaInjectMeta.class;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class MetaInjectOutputFieldLoadSaveValidator implements FieldLoadSaveValidator<MetaInjectOutputField> {
        final Random rand = new Random();

        @Override
        public MetaInjectOutputField getTestObject() {
            MetaInjectOutputField rtn = new MetaInjectOutputField();
            rtn.setName(UUID.randomUUID().toString());
            rtn.setLength(rand.nextInt(100));
            rtn.setPrecision(rand.nextInt(9));
            rtn.setType(rand.nextInt(7));
            return rtn;
        }

        @Override
        public boolean validateTestObject(MetaInjectOutputField testObject, Object actual) {
            if (!(actual instanceof MetaInjectOutputField)) {
                return false;
            }
            MetaInjectOutputField another = ((MetaInjectOutputField) (actual));
            return new EqualsBuilder().append(testObject.getLength(), another.getLength()).append(testObject.getPrecision(), another.getPrecision()).append(testObject.getName(), another.getName()).append(testObject.getType(), another.getType()).isEquals();
        }
    }

    // MetaInjectMappingLoadSaveValidator
    public class MetaInjectMappingLoadSaveValidator implements FieldLoadSaveValidator<MetaInjectMapping> {
        final Random rand = new Random();

        @Override
        public MetaInjectMapping getTestObject() {
            MetaInjectMapping rtn = new MetaInjectMapping();
            rtn.setSourceField(UUID.randomUUID().toString());
            rtn.setSourceStep(UUID.randomUUID().toString());
            rtn.setTargetField(UUID.randomUUID().toString());
            rtn.setTargetStep(UUID.randomUUID().toString());
            return rtn;
        }

        @Override
        public boolean validateTestObject(MetaInjectMapping testObject, Object actual) {
            if (!(actual instanceof MetaInjectMapping)) {
                return false;
            }
            MetaInjectMapping another = ((MetaInjectMapping) (actual));
            return new EqualsBuilder().append(testObject.getSourceField(), another.getSourceField()).append(testObject.getSourceStep(), another.getSourceStep()).append(testObject.getTargetField(), another.getTargetField()).append(testObject.getTargetStep(), another.getTargetStep()).isEquals();
        }
    }

    // TargetStepAttributeLoadSaveValidator
    public class TargetStepAttributeLoadSaveValidator implements FieldLoadSaveValidator<TargetStepAttribute> {
        final Random rand = new Random();

        @Override
        public TargetStepAttribute getTestObject() {
            return new TargetStepAttribute(UUID.randomUUID().toString(), UUID.randomUUID().toString(), rand.nextBoolean());
        }

        @Override
        public boolean validateTestObject(TargetStepAttribute testObject, Object actual) {
            if (!(actual instanceof TargetStepAttribute)) {
                return false;
            }
            TargetStepAttribute another = ((TargetStepAttribute) (actual));
            return new EqualsBuilder().append(testObject.getStepname(), another.getStepname()).append(testObject.getAttributeKey(), another.getAttributeKey()).append(testObject.isDetail(), another.isDetail()).isEquals();
        }
    }

    // SourceStepFieldLoadSaveValidator
    public class SourceStepFieldLoadSaveValidator implements FieldLoadSaveValidator<SourceStepField> {
        final Random rand = new Random();

        @Override
        public SourceStepField getTestObject() {
            return new SourceStepField(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        }

        @Override
        public boolean validateTestObject(SourceStepField testObject, Object actual) {
            if (!(actual instanceof SourceStepField)) {
                return false;
            }
            SourceStepField another = ((SourceStepField) (actual));
            return new EqualsBuilder().append(testObject.getStepname(), another.getStepname()).append(testObject.getField(), another.getField()).isEquals();
        }
    }
}

