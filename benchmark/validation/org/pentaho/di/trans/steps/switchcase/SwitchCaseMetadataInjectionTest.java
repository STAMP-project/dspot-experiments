/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016 - 2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.trans.steps.switchcase;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class SwitchCaseMetadataInjectionTest extends BaseMetadataInjectionTest<SwitchCaseMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("FIELD_NAME", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getFieldname();
            }
        });
        String[] typeNames = ValueMetaBase.getAllTypes();
        checkStringToInt("VALUE_TYPE", new BaseMetadataInjectionTest.IntGetter() {
            public int get() {
                return meta.getCaseValueType();
            }
        }, typeNames, BaseMetadataInjectionTest.getTypeCodes(typeNames));
        check("VALUE_DECIMAL", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getCaseValueDecimal();
            }
        });
        check("VALUE_GROUP", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getCaseValueGroup();
            }
        });
        check("VALUE_FORMAT", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getCaseValueFormat();
            }
        });
        check("CONTAINS", new BaseMetadataInjectionTest.BooleanGetter() {
            @Override
            public boolean get() {
                return meta.isContains();
            }
        });
        check("DEFAULT_TARGET_STEP_NAME", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getDefaultTargetStepname();
            }
        });
        check("SWITCH_CASE_TARGET.CASE_VALUE", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getCaseTargets().get(0).caseValue;
            }
        });
        check("SWITCH_CASE_TARGET.CASE_TARGET_STEP_NAME", new BaseMetadataInjectionTest.StringGetter() {
            @Override
            public String get() {
                return meta.getCaseTargets().get(0).caseTargetStepname;
            }
        });
    }
}

