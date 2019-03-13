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
package org.pentaho.di.trans.steps.pentahoreporting;


import PentahoReportingOutputMeta.ProcessorType;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class PentahoReportingOutputMetaTest extends BaseMetadataInjectionTest<PentahoReportingOutputMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("INPUT_FILE_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getInputFileField();
            }
        });
        check("OUTPUT_FILE_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getOutputFileField();
            }
        });
        ValueMetaInterface valueMeta = new ValueMetaString("f");
        injector.setProperty(meta, "PARAMETER_NAME", setValue(valueMeta, "f1", "f2"), "f");
        injector.setProperty(meta, "FIELDNAME", setValue(valueMeta, "v1", "v2"), "f");
        Assert.assertEquals("v1", meta.getParameterFieldMap().get("f1"));
        Assert.assertEquals("v2", meta.getParameterFieldMap().get("f2"));
        skipPropertyTest("PARAMETER_NAME");
        skipPropertyTest("FIELDNAME");
        check("OUTPUT_PROCESSOR_TYPE", new BaseMetadataInjectionTest.EnumGetter() {
            public Enum<?> get() {
                return meta.getOutputProcessorType();
            }
        }, ProcessorType.class);
        check("CREATE_PARENT_FOLDER", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getCreateParentfolder();
            }
        });
    }
}

