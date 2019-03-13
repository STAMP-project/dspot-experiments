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
package org.pentaho.di.trans.steps.prioritizestreams;


import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.QueueRowSet;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.SingleRowRowSet;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.mock.StepMockHelper;

import static junit.framework.Assert.assertTrue;


public class PrioritizeStreamsTest {
    private static StepMockHelper<PrioritizeStreamsMeta, StepDataInterface> stepMockHelper;

    /**
     * [PDI-9088] Prioritize streams step causing null pointer exception downstream
     *
     * @throws KettleException
     * 		
     */
    @Test
    public void testProcessRow() throws KettleException {
        PrioritizeStreamsMeta meta = new PrioritizeStreamsMeta();
        meta.setStepName(new String[]{ "high", "medium", "low" });
        PrioritizeStreamsData data = new PrioritizeStreamsData();
        PrioritizeStreamsTest.PrioritizeStreamsInner step = new PrioritizeStreamsTest.PrioritizeStreamsInner(PrioritizeStreamsTest.stepMockHelper);
        try {
            step.processRow(meta, data);
        } catch (NullPointerException e) {
            Assert.fail(("NullPointerException detecded, seems that RowMetaInterface was not set for RowSet you are attempting" + "to read from."));
        }
        assertTrue("First waiting for row set is 'high'", data.currentRowSet.getClass().equals(SingleRowRowSet.class));
    }

    private class PrioritizeStreamsInner extends PrioritizeStreams {
        public PrioritizeStreamsInner(StepMockHelper<PrioritizeStreamsMeta, StepDataInterface> stepMockHelper) {
            super(stepMockHelper.stepMeta, stepMockHelper.stepDataInterface, 0, stepMockHelper.transMeta, stepMockHelper.trans);
        }

        @Override
        public RowSet findInputRowSet(String sourceStep) throws KettleStepException {
            if (sourceStep.equals("high")) {
                return new SingleRowRowSet();
            }
            return new QueueRowSet();
        }

        @Override
        protected void checkInputLayoutValid(RowMetaInterface referenceRowMeta, RowMetaInterface compareRowMeta) {
            // always true.
        }

        @Override
        public Object[] getRowFrom(RowSet rowSet) throws KettleStepException {
            rowSet.setRowMeta(new RowMeta());
            return new Object[]{  };
        }

        @Override
        public void putRow(RowMetaInterface rmi, Object[] input) {
            if (rmi == null) {
                throw new NullPointerException();
            }
        }
    }
}

