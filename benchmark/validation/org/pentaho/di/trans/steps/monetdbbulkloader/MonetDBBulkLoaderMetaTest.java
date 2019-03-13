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
package org.pentaho.di.trans.steps.monetdbbulkloader;


import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.step.StepInjectionMetaEntry;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.pentaho.di.trans.steps.loadsave.initializer.InitializerInterface;


/**
 * Created by gmoran on 2/25/14.
 */
public class MonetDBBulkLoaderMetaTest implements InitializerInterface<StepMetaInterface> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private StepMeta stepMeta;

    private MonetDBBulkLoader loader;

    private MonetDBBulkLoaderData ld;

    private MonetDBBulkLoaderMeta lm;

    LoadSaveTester loadSaveTester;

    Class<MonetDBBulkLoaderMeta> testMetaClass = MonetDBBulkLoaderMeta.class;

    @Test
    public void testTopLevelMetadataEntries() {
        try {
            List<StepInjectionMetaEntry> entries = loader.getStepMeta().getStepMetaInterface().getStepMetaInjectionInterface().getStepInjectionMetadataEntries();
            String masterKeys = "SCHEMA TABLE LOGFILE FIELD_SEPARATOR FIELD_ENCLOSURE NULL_REPRESENTATION ENCODING TRUNCATE " + "FULLY_QUOTE_SQL BUFFER_SIZE MAPPINGS ";
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
            String childKeys = "STREAMNAME FIELDNAME FIELD_FORMAT_OK ";
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

    // Note - the cloneTest() was removed since it's being covered by the load/save tester now.
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
                        switch (childEntry.getValueType()) {
                            case ValueMetaInterface.TYPE_STRING :
                                childEntry.setValue("new_".concat(childEntry.getKey()));
                                break;
                            case ValueMetaInterface.TYPE_BOOLEAN :
                                childEntry.setValue(Boolean.TRUE);
                                break;
                            default :
                                break;
                        }
                    }
                }
            }
            loader.getStepMeta().getStepMetaInterface().getStepMetaInjectionInterface().injectStepMetadataEntries(entries);
            Assert.assertEquals("Schema name not properly injected... ", "new_SCHEMA", lm.getSchemaName());
            Assert.assertEquals("Table name not properly injected... ", "new_TABLE", lm.getTableName());
            Assert.assertEquals("Logfile not properly injected... ", "new_LOGFILE", lm.getLogFile());
            Assert.assertEquals("Field separator not properly injected... ", "new_FIELD_SEPARATOR", lm.getFieldSeparator());
            Assert.assertEquals("Field enclosure not properly injected... ", "new_FIELD_ENCLOSURE", lm.getFieldEnclosure());
            Assert.assertEquals("Null representation not properly injected... ", "new_NULL_REPRESENTATION", lm.getNULLrepresentation());
            Assert.assertEquals("Encoding path not properly injected... ", "new_ENCODING", lm.getEncoding());
            Assert.assertEquals("Buffer size not properly injected... ", "new_BUFFER_SIZE", lm.getBufferSize());
            Assert.assertEquals("Truncate not properly injected... ", Boolean.TRUE, lm.isTruncate());
            Assert.assertEquals("Fully Quote SQL not properly injected... ", Boolean.TRUE, lm.isFullyQuoteSQL());
            Assert.assertEquals("Field name not properly injected... ", "new_FIELDNAME", lm.getFieldTable()[0]);
            Assert.assertEquals("Stream name not properly injected... ", "new_STREAMNAME", lm.getFieldStream()[0]);
            Assert.assertEquals("Field Format not properly injected... ", Boolean.TRUE, lm.getFieldFormatOk()[0]);
        } catch (KettleException e) {
            Assert.fail(e.getMessage());
        }
    }
}

