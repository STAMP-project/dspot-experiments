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
package org.pentaho.di.trans.steps.rowgenerator;


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.RowAdapter;


public class RowGeneratorUnitTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private RowGenerator rowGenerator;

    @Test
    public void testReadRowLimitAsTransformationVar() throws KettleException {
        long rowLimit = ((RowGeneratorData) (rowGenerator.getStepDataInterface())).rowLimit;
        Assert.assertEquals(rowLimit, 1440);
    }

    @Test
    public void doesNotWriteRowOnTimeWhenStopped() throws InterruptedException, KettleException {
        TransMeta transMeta = new TransMeta(getClass().getResource("safe-stop.ktr").getPath());
        Trans trans = new Trans(transMeta);
        trans.prepareExecution(new String[]{  });
        trans.getSteps().get(1).step.addRowListener(new RowAdapter() {
            @Override
            public void rowWrittenEvent(RowMetaInterface rowMeta, Object[] row) throws KettleStepException {
                trans.safeStop();
            }
        });
        trans.startThreads();
        trans.waitUntilFinished();
        Assert.assertEquals(1, trans.getSteps().get(0).step.getLinesWritten());
        Assert.assertEquals(1, trans.getSteps().get(1).step.getLinesRead());
    }
}

