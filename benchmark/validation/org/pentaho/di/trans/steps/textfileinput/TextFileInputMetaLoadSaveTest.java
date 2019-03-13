/**
 * ******************************************************************************
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


import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Andrey Khayrutdinov
 * @deprecated replaced by implementation in the ...steps.fileinput.text package
 */
@Deprecated
@RunWith(PowerMockRunner.class)
public class TextFileInputMetaLoadSaveTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private LoadSaveTester<TextFileInputMeta> tester;

    @Test
    public void testSerialization() throws Exception {
        tester.testSerialization();
    }

    private static class TextFileFilterValidator implements FieldLoadSaveValidator<TextFileFilter> {
        @Override
        public TextFileFilter getTestObject() {
            TextFileFilter fileFilter = new TextFileFilter();
            fileFilter.setFilterPosition(new Random().nextInt());
            fileFilter.setFilterString(UUID.randomUUID().toString());
            fileFilter.setFilterLastLine(new Random().nextBoolean());
            fileFilter.setFilterPositive(new Random().nextBoolean());
            return fileFilter;
        }

        @Override
        public boolean validateTestObject(TextFileFilter testObject, Object actual) {
            if (!(actual instanceof TextFileFilter)) {
                return false;
            }
            TextFileFilter another = ((TextFileFilter) (actual));
            return new EqualsBuilder().append(testObject.getFilterPosition(), another.getFilterPosition()).append(testObject.getFilterString(), another.getFilterString()).append(testObject.isFilterLastLine(), another.isFilterLastLine()).append(testObject.isFilterPositive(), another.isFilterPositive()).isEquals();
        }
    }
}

