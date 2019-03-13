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
package org.pentaho.di.trans.steps.fieldsplitter;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class FieldSplitterMetaInjectionTest extends BaseMetadataInjectionTest<FieldSplitterMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("FIELD_TO_SPLIT", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getSplitField();
            }
        });
        check("DELIMITER", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getDelimiter();
            }
        });
        check("ENCLOSURE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getEnclosure();
            }
        });
        check("NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldName()[0];
            }
        });
        check("ID", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldID()[0];
            }
        });
        check("REMOVE_ID", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getFieldRemoveID()[0];
            }
        });
        check("FORMAT", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldFormat()[0];
            }
        });
        check("GROUPING", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldGroup()[0];
            }
        });
        check("DECIMAL", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldDecimal()[0];
            }
        });
        check("CURRENCY", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldCurrency()[0];
            }
        });
        check("LENGTH", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getFieldLength()[0];
            }
        });
        check("PRECISION", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getFieldPrecision()[0];
            }
        });
        check("NULL_IF", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldNullIf()[0];
            }
        });
        check("DEFAULT", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldIfNull()[0];
            }
        });
        ValueMetaInterface mftt = new ValueMetaString("f");
        injector.setProperty(meta, "TRIM_TYPE", setValue(mftt, "none"), "f");
        Assert.assertEquals(0, meta.getFieldTrimType()[0]);
        injector.setProperty(meta, "TRIM_TYPE", setValue(mftt, "left"), "f");
        Assert.assertEquals(1, meta.getFieldTrimType()[0]);
        injector.setProperty(meta, "TRIM_TYPE", setValue(mftt, "right"), "f");
        Assert.assertEquals(2, meta.getFieldTrimType()[0]);
        injector.setProperty(meta, "TRIM_TYPE", setValue(mftt, "both"), "f");
        Assert.assertEquals(3, meta.getFieldTrimType()[0]);
        skipPropertyTest("TRIM_TYPE");
        // TODO check field type plugins
        skipPropertyTest("DATA_TYPE");
    }
}

