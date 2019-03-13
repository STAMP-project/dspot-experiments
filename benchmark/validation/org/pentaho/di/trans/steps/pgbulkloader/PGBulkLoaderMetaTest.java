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
package org.pentaho.di.trans.steps.pgbulkloader;


import java.util.List;
import java.util.Random;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.validator.FieldLoadSaveValidator;

import static PGBulkLoaderMeta.DATE_MASK_DATE;
import static PGBulkLoaderMeta.DATE_MASK_DATETIME;
import static PGBulkLoaderMeta.DATE_MASK_PASS_THROUGH;


/**
 * Created by gmoran on 2/25/14.
 */
public class PGBulkLoaderMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMeta stepMeta;

    private PGBulkLoader loader;

    private PGBulkLoaderData ld;

    private PGBulkLoaderMeta lm;

    LoadSaveTester loadSaveTester;

    Class<PGBulkLoaderMeta> testMetaClass = PGBulkLoaderMeta.class;

    @Test
    public void testSerialization() throws KettleException {
        loadSaveTester.testSerialization();
    }

    @Test
    public void testTopLevelMetadataEntries() {
        try {
            List<StepInjectionMetaEntry> entries = loader.getStepMeta().getStepMetaInterface().getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            String masterKeys = "SCHEMA TABLE LOADACTION STOPONERROR DELIMITER ENCLOSURE DBNAMEOVERRIDE MAPPINGS ";
            for (StepInjectionMetaEntry entry : entries) {
                String key = entry.getKey();
                Assert.assertTrue(masterKeys.contains(key));
                masterKeys = masterKeys.replace(key, "");
            }
            Assert.assertTrue(((masterKeys.trim().length()) == 0));
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testChildLevelMetadataEntries() {
        try {
            List<StepInjectionMetaEntry> entries = loader.getStepMeta().getStepMetaInterface().getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            String childKeys = "STREAMNAME FIELDNAME DATEMASK ";
            StepInjectionMetaEntry mappingEntry = null;
            for (StepInjectionMetaEntry entry : entries) {
                String key = entry.getKey();
                if (key.equals("MAPPINGS")) {
                    mappingEntry = entry;
                    break;
                }
            }
            Assert.assertNotNull(mappingEntry);
            List<StepInjectionMetaEntry> fieldAttributes = mappingEntry.getDetails().get(0).getDetails();
            for (StepInjectionMetaEntry attribute : fieldAttributes) {
                String key = attribute.getKey();
                Assert.assertTrue(childKeys.contains(key));
                childKeys = childKeys.replace(key, "");
            }
            Assert.assertTrue(((childKeys.trim().length()) == 0));
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInjection() {
        try {
            List<StepInjectionMetaEntry> entries = loader.getStepMeta().getStepMetaInterface().getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            for (StepInjectionMetaEntry entry : entries) {
                entry.setValueType(lm.findAttribute(entry.getKey()).getType());
                switch (entry.getValueType()) {
                    case ValueMetaInterface.TYPE_STRING :
                        entry.setValue("new_".concat(entry.getKey()));
                        break;
                    case ValueMetaInterface.TYPE_BOOLEAN :
                        entry.setValue(Boolean.TRUE);
                        break;
                    default :
                        break;
                }
                if (!(entry.getDetails().isEmpty())) {
                    List<StepInjectionMetaEntry> childEntries = entry.getDetails().get(0).getDetails();
                    for (StepInjectionMetaEntry childEntry : childEntries) {
                        childEntry.setValue("new_".concat(childEntry.getKey()));
                    }
                }
            }
            loader.getStepMeta().getStepMetaInterface().getStepMetaInjectionInterface().injectStepMetadataEntries(entries);
            Assert.assertEquals("Schema name not properly injected... ", "new_SCHEMA", lm.getSchemaName());
            Assert.assertEquals("Table name not properly injected... ", "new_TABLE", lm.getTableName());
            Assert.assertEquals("DB Name Override not properly injected... ", "new_DBNAMEOVERRIDE", lm.getDbNameOverride());
            Assert.assertEquals("Delimiter not properly injected... ", "new_DELIMITER", lm.getDelimiter());
            Assert.assertEquals("Enclosure not properly injected... ", "new_ENCLOSURE", lm.getEnclosure());
            Assert.assertEquals("Load action not properly injected... ", "new_LOADACTION", lm.getLoadAction());
            Assert.assertEquals("Stop on error not properly injected... ", Boolean.TRUE, lm.isStopOnError());
            Assert.assertEquals("Field name not properly injected... ", "new_FIELDNAME", lm.getFieldTable()[0]);
            Assert.assertEquals("Stream name not properly injected... ", "new_STREAMNAME", lm.getFieldStream()[0]);
            Assert.assertEquals("Date Mask not properly injected... ", "new_DATEMASK", lm.getDateMask()[0]);
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }

    public class DateMaskLoadSaveValidator implements FieldLoadSaveValidator<String> {
        Random r = new Random();

        private final String[] masks = new String[]{ DATE_MASK_PASS_THROUGH, DATE_MASK_DATE, DATE_MASK_DATETIME };

        @Override
        public String getTestObject() {
            int idx = r.nextInt(3);
            return masks[idx];
        }

        @Override
        public boolean validateTestObject(String test, Object actual) {
            return test.equals(actual);
        }
    }
}

