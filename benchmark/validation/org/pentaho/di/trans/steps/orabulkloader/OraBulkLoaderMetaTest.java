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
package org.pentaho.di.trans.steps.orabulkloader;


import java.util.Random;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;


public class OraBulkLoaderMetaTest {
    Class<OraBulkLoaderMeta> testMetaClass = OraBulkLoaderMeta.class;

    LoadSaveTester loadSaveTester;

    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    public class DateMaskLoadSaveValidator implements FieldLoadSaveValidator<String> {
        Random r = new Random();

        @Override
        public String getTestObject() {
            boolean ltr = r.nextBoolean();
            String dm = (ltr) ? "DATE" : "DATETIME";
            return dm;
        }

        @Override
        public boolean validateTestObject(String test, Object actual) {
            return test.equals(actual);
        }
    }

    // PDI-16472
    @Test
    public void testGetXML() {
        OraBulkLoaderMeta oraBulkLoaderMeta = new OraBulkLoaderMeta();
        oraBulkLoaderMeta.setFieldTable(new String[]{ "fieldTable1", "fieldTable2" });
        oraBulkLoaderMeta.setFieldStream(new String[]{ "fieldStreamValue1" });
        oraBulkLoaderMeta.setDateMask(new String[]{  });
        oraBulkLoaderMeta.afterInjectionSynchronization();
        // run without exception
        oraBulkLoaderMeta.getXML();
        Assert.assertEquals(oraBulkLoaderMeta.getFieldStream().length, oraBulkLoaderMeta.getDateMask().length);
    }
}

