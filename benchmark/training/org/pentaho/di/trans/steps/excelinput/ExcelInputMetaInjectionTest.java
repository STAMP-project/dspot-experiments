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
package org.pentaho.di.trans.steps.excelinput;


import ValueMetaBase.trimTypeCode;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class ExcelInputMetaInjectionTest extends BaseMetadataInjectionTest<ExcelInputMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getField()[0].getName();
            }
        });
        check("LENGTH", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getField()[0].getLength();
            }
        });
        check("PRECISION", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getField()[0].getPrecision();
            }
        });
        int[] trimInts = new int[trimTypeCode.length];
        for (int i = 0; i < (trimInts.length); i++) {
            trimInts[i] = i;
        }
        checkStringToInt("TRIM_TYPE", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getField()[0].getTrimType();
            }
        }, trimTypeCode, trimInts);
        check("FORMAT", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getField()[0].getFormat();
            }
        });
        check("CURRENCY", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getField()[0].getCurrencySymbol();
            }
        });
        check("DECIMAL", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getField()[0].getDecimalSymbol();
            }
        });
        check("GROUP", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getField()[0].getGroupSymbol();
            }
        });
        check("REPEAT", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.getField()[0].isRepeated();
            }
        });
        // TODO check field type plugins
        skipPropertyTest("TYPE");
        check("SHEET_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getSheetName()[0];
            }
        });
        check("SHEET_START_ROW", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getStartRow()[0];
            }
        });
        check("SHEET_START_COL", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getStartColumn()[0];
            }
        });
        check("FILENAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFileName()[0];
            }
        });
        check("FILEMASK", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFileMask()[0];
            }
        });
        check("EXCLUDE_FILEMASK", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getExludeFileMask()[0];
            }
        });
        check("FILE_REQUIRED", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFileRequired()[0];
            }
        });
        check("INCLUDE_SUBFOLDERS", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getIncludeSubFolders()[0];
            }
        });
        check("SPREADSHEET_TYPE", new BaseMetadataInjectionTest.EnumGetter() {
            public SpreadSheetType get() {
                return meta.getSpreadSheetType();
            }
        }, SpreadSheetType.class);
    }
}

