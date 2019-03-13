/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016 - 2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.synchronizeaftermerge;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class SynchronizeAfterMergeMetaInjectionTest extends BaseMetadataInjectionTest<SynchronizeAfterMergeMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("SHEMA_NAME", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getSchemaName();
            }
        });
        check("TABLE_NAME", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getTableName();
            }
        });
        check("TABLE_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getKeyLookup()[0];
            }
        });
        check("STREAM_FIELD1", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getKeyStream()[0];
            }
        });
        check("STREAM_FIELD2", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getKeyStream2()[0];
            }
        });
        check("COMPARATOR", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getKeyCondition()[0];
            }
        });
        check("UPDATE_TABLE_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getUpdateLookup()[0];
            }
        });
        check("STREAM_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getUpdateStream()[0];
            }
        });
        check("UPDATE", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.getUpdate()[0];
            }
        });
        check("COMMIT_SIZE", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getCommitSize();
            }
        });
        check("TABLE_NAME_IN_FIELD", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.istablenameInField();
            }
        });
        check("TABLE_NAME_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.gettablenameField();
            }
        });
        check("OPERATION_ORDER_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getOperationOrderField();
            }
        });
        check("USE_BATCH_UPDATE", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.useBatchUpdate();
            }
        });
        check("PERFORM_LOOKUP", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.isPerformLookup();
            }
        });
        check("ORDER_INSERT", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getOrderInsert();
            }
        });
        check("ORDER_UPDATE", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getOrderUpdate();
            }
        });
        check("ORDER_DELETE", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getOrderDelete();
            }
        });
        check("CONNECTION_NAME", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return "My Connection";
            }
        }, "My Connection");
    }

    @Test
    public void getXML() throws KettleException {
        skipProperties("CONNECTION_NAME", "TABLE_NAME", "STREAM_FIELD2", "PERFORM_LOOKUP", "COMPARATOR", "OPERATION_ORDER_FIELD", "ORDER_DELETE", "SHEMA_NAME", "TABLE_NAME_IN_FIELD", "ORDER_UPDATE", "ORDER_INSERT", "USE_BATCH_UPDATE", "STREAM_FIELD", "TABLE_FIELD", "COMMIT_SIZE", "TABLE_NAME_FIELD");
        meta.setDefault();
        check("STREAM_FIELD1", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getKeyStream()[0];
            }
        });
        check("UPDATE_TABLE_FIELD", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getUpdateLookup()[0];
            }
        });
        check("UPDATE", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.getUpdate()[0];
            }
        });
        meta.getXML();
        String[] actualKeyLookup = meta.getKeyLookup();
        Assert.assertNotNull(actualKeyLookup);
        Assert.assertEquals(1, actualKeyLookup.length);
        String[] actualKeyCondition = meta.getKeyCondition();
        Assert.assertNotNull(actualKeyCondition);
        Assert.assertEquals(1, actualKeyCondition.length);
        String[] actualKeyStream2 = meta.getKeyCondition();
        Assert.assertNotNull(actualKeyStream2);
        Assert.assertEquals(1, actualKeyStream2.length);
        String[] actualUpdateStream = meta.getUpdateStream();
        Assert.assertNotNull(actualUpdateStream);
        Assert.assertEquals(1, actualUpdateStream.length);
    }
}

