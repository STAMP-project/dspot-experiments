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
package org.pentaho.di.trans.steps.orabulkloader;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class OraBulkLoaderMetaInjectionTest extends BaseMetadataInjectionTest<OraBulkLoaderMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("SCHEMA_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getSchemaName();
            }
        });
        check("TABLE_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getTableName();
            }
        });
        check("SQLLDR_PATH", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getSqlldr();
            }
        });
        check("CONTROL_FILE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getControlFile();
            }
        });
        check("DATA_FILE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getDataFile();
            }
        });
        check("LOG_FILE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getLogFile();
            }
        });
        check("BAD_FILE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getBadFile();
            }
        });
        check("DISCARD_FILE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getDiscardFile();
            }
        });
        check("FIELD_TABLE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldTable()[0];
            }
        });
        check("FIELD_STREAM", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFieldStream()[0];
            }
        });
        check("FIELD_DATEMASK", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getDateMask()[0];
            }
        });
        check("COMMIT_SIZE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getCommitSize();
            }
        });
        check("BIND_SIZE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getBindSize();
            }
        });
        check("READ_SIZE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getReadSize();
            }
        });
        check("MAX_ERRORS", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getMaxErrors();
            }
        });
        check("LOAD_METHOD", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getLoadMethod();
            }
        });
        check("LOAD_ACTION", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getLoadAction();
            }
        });
        check("ENCODING", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getEncoding();
            }
        });
        check("ORACLE_CHARSET_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getCharacterSetName();
            }
        });
        check("DIRECT_PATH", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isDirectPath();
            }
        });
        check("ERASE_FILES", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isEraseFiles();
            }
        });
        check("DB_NAME_OVERRIDE", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getDbNameOverride();
            }
        });
        check("FAIL_ON_WARNING", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isFailOnWarning();
            }
        });
        check("FAIL_ON_ERROR", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isFailOnError();
            }
        });
        check("PARALLEL", new BaseMetadataInjectionTest.BooleanGetter() {
            public boolean get() {
                return meta.isParallel();
            }
        });
        check("RECORD_TERMINATOR", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getAltRecordTerm();
            }
        });
        check("CONNECTION_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return "My Connection";
            }
        }, "My Connection");
    }
}

