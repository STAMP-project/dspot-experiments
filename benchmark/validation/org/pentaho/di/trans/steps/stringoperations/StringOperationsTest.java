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
package org.pentaho.di.trans.steps.stringoperations;


import junit.framework.Assert;
import org.junit.Test;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.trans.steps.mock.StepMockHelper;


/**
 * Tests for StringOperations step
 *
 * @author Pavel Sakun
 * @see StringOperations
 */
public class StringOperationsTest {
    private static StepMockHelper<StringOperationsMeta, StringOperationsData> smh;

    @Test
    public void testProcessBinaryInput() throws KettleException {
        StringOperations step = new StringOperations(StringOperationsTest.smh.stepMeta, StringOperationsTest.smh.stepDataInterface, 0, StringOperationsTest.smh.transMeta, StringOperationsTest.smh.trans);
        step.addRowSetToInputRowSets(mockInputRowSet());
        RowSet outputRowSet = new QueueRowSet();
        step.addRowSetToOutputRowSets(outputRowSet);
        StringOperationsMeta meta = mockStepMeta();
        StringOperationsData data = mockStepData();
        step.init(meta, data);
        boolean processResult;
        do {
            processResult = step.processRow(meta, data);
        } while (processResult );
        Assert.assertTrue(outputRowSet.isDone());
        Assert.assertTrue("Unexpected output", verifyOutput(new Object[][]{ new Object[]{ "Value" } }, outputRowSet));
    }
}

