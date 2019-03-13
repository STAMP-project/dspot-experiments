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
package org.pentaho.di.trans.steps.normaliser;


import java.util.List;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.TransTestFactory;


public class NormaliserTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testNormaliserProcessRowsWikiData() throws Exception {
        // We should have 1 row as input to the normaliser and 3 rows as output to the normaliser with the data
        // shown on the Wiki page: http://wiki.pentaho.com/display/EAI/Row+Normaliser
        // 
        // 
        // Data input looks like this:
        // 
        // DATE     PR1_NR  PR_SL PR2_NR  PR2_SL  PR3_NR  PR3_SL
        // 2003010  5       100   10      250     4       150
        // 
        // Data output looks like this:
        // 
        // DATE     Type      Product Sales Product Number
        // 2003010  Product1  100           5
        // 2003010  Product2  250           10
        // 2003010  Product3  150           4
        // 
        final String stepName = "Row Normaliser";
        NormaliserMeta stepMeta = new NormaliserMeta();
        stepMeta.setDefault();
        stepMeta.setNormaliserFields(getTestNormaliserFieldsWiki());
        stepMeta.setTypeField("Type");
        TransMeta transMeta = TransTestFactory.generateTestTransformation(null, stepMeta, stepName);
        List<RowMetaAndData> inputList = getWikiInputRowMetaAndData();
        List<RowMetaAndData> outputList = TransTestFactory.executeTestTransformation(transMeta, TransTestFactory.INJECTOR_STEPNAME, stepName, TransTestFactory.DUMMY_STEPNAME, inputList);
        List<RowMetaAndData> expectedOutput = this.getExpectedWikiOutputRowMetaAndData();
        checkResults(expectedOutput, outputList);
    }
}

