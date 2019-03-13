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
package org.pentaho.di.trans.steps.textfileinput;


import TextFileInputMetaInjection.Entry;
import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepInjectionUtil;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


/**
 *
 *
 * @deprecated replaced by implementation in the ...steps.fileinput.text package
 */
public class TextFileInputMetaInjectionTest {
    @Test
    public void extractingAll() throws Exception {
        TextFileInputMetaInjection injection = new TextFileInputMetaInjection(new TextFileInputMeta());
        List<StepInjectionMetaEntry> metadata = injection.getStepInjectionMetadataEntries();
        List<StepInjectionMetaEntry> extracted = injection.extractStepMetadataEntries();
        Assert.assertEquals(metadata.size(), extracted.size());
        for (StepInjectionMetaEntry metaEntry : metadata) {
            Assert.assertNotNull(metaEntry.getKey(), StepInjectionUtil.findEntry(extracted, metaEntry.getKey()));
        }
    }

    @Test
    public void topEntriesAreInjected() throws Exception {
        TextFileInputMetaInjection[] topEntries = Entry.getTopEntries();
        List<StepInjectionMetaEntry> injectionValues = TextFileInputMetaInjectionTest.createInjectionValues(topEntries);
        TextFileInputMetaInjection injection = new TextFileInputMetaInjection(new TextFileInputMeta());
        injection.injectStepMetadataEntries(injectionValues);
        TextFileInputMetaInjectionTest.assertInjected(injection.extractStepMetadataEntries(), injectionValues);
    }

    private static interface Generator<T> {
        T generateValue();
    }

    private static class YesNoGenerator implements TextFileInputMetaInjectionTest.Generator<String> {
        private final Random random = new Random();

        @Override
        public String generateValue() {
            if (random.nextBoolean()) {
                return "Y";
            } else {
                return "N";
            }
        }
    }

    private static class ValidatorAdapter<T> implements TextFileInputMetaInjectionTest.Generator<String> {
        private final FieldLoadSaveValidator<T> validator;

        public ValidatorAdapter(FieldLoadSaveValidator<T> validator) {
            this.validator = validator;
        }

        @Override
        public String generateValue() {
            return validator.getTestObject().toString();
        }
    }

    private static class Constant<T> implements TextFileInputMetaInjectionTest.Generator<T> {
        private final T value;

        public Constant(T value) {
            this.value = value;
        }

        @Override
        public T generateValue() {
            return value;
        }
    }
}

