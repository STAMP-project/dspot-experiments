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
package org.pentaho.di.trans.steps.filterrows;


import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.Condition;
import org.pentaho.di.core.injection.BaseMetadataInjectionTest;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class FilterRowsMetaInjectionTest extends BaseMetadataInjectionTest<FilterRowsMeta> {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void test() throws Exception {
        check("SEND_FALSE_STEP", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getFalseStepname();
            }
        });
        check("SEND_TRUE_STEP", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getTrueStepname();
            }
        });
        check("CONDITION", new BaseMetadataInjectionTest.StringGetter() {
            public String get() {
                return meta.getConditionXML();
            }
        }, new Condition().getXML());
    }
}

