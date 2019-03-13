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
package org.pentaho.di.concurrency;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.RowSet;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.RowListener;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepPartitioningMeta;


public class BaseStepConcurrencyTest {
    private static final String STEP_META = "StepMeta";

    private BaseStep baseStep;

    /**
     * Row listeners collection modifiers are exposed out of BaseStep class,
     * whereas the collection traversal is happening on every row being processed.
     *
     * We should be sure that modification of the collection will not throw a concurrent modification exception.
     */
    @Test
    public void testRowListeners() throws Exception {
        int modifiersAmount = 100;
        int traversersAmount = 100;
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Mockito.when(stepMeta.getName()).thenReturn(BaseStepConcurrencyTest.STEP_META);
        Mockito.when(transMeta.findStep(BaseStepConcurrencyTest.STEP_META)).thenReturn(stepMeta);
        Mockito.when(stepMeta.getTargetStepPartitioningMeta()).thenReturn(Mockito.mock(StepPartitioningMeta.class));
        baseStep = new BaseStep(stepMeta, null, 0, transMeta, Mockito.mock(Trans.class));
        AtomicBoolean condition = new AtomicBoolean(true);
        List<BaseStepConcurrencyTest.RowListenersModifier> rowListenersModifiers = new ArrayList<>();
        for (int i = 0; i < modifiersAmount; i++) {
            rowListenersModifiers.add(new BaseStepConcurrencyTest.RowListenersModifier(condition));
        }
        List<BaseStepConcurrencyTest.RowListenersTraverser> rowListenersTraversers = new ArrayList<>();
        for (int i = 0; i < traversersAmount; i++) {
            rowListenersTraversers.add(new BaseStepConcurrencyTest.RowListenersTraverser(condition));
        }
        ConcurrencyTestRunner<?, ?> runner = new ConcurrencyTestRunner<Object, Object>(rowListenersModifiers, rowListenersTraversers, condition);
        runner.runConcurrentTest();
        runner.checkNoExceptionRaised();
    }

    /**
     * Row sets collection modifiers are exposed out of BaseStep class,
     * whereas the collection traversal is happening on every row being processed.
     *
     * We should be sure that modification of the collection will not throw a concurrent modification exception.
     */
    @Test
    public void testInputOutputRowSets() throws Exception {
        int modifiersAmount = 100;
        int traversersAmount = 100;
        StepMeta stepMeta = Mockito.mock(StepMeta.class);
        TransMeta transMeta = Mockito.mock(TransMeta.class);
        Mockito.when(stepMeta.getName()).thenReturn(BaseStepConcurrencyTest.STEP_META);
        Mockito.when(transMeta.findStep(BaseStepConcurrencyTest.STEP_META)).thenReturn(stepMeta);
        Mockito.when(stepMeta.getTargetStepPartitioningMeta()).thenReturn(Mockito.mock(StepPartitioningMeta.class));
        baseStep = new BaseStep(stepMeta, null, 0, transMeta, Mockito.mock(Trans.class));
        AtomicBoolean condition = new AtomicBoolean(true);
        List<BaseStepConcurrencyTest.RowSetsModifier> rowSetsModifiers = new ArrayList<>();
        for (int i = 0; i < modifiersAmount; i++) {
            rowSetsModifiers.add(new BaseStepConcurrencyTest.RowSetsModifier(condition));
        }
        List<BaseStepConcurrencyTest.RowSetsTraverser> rowSetsTraversers = new ArrayList<>();
        for (int i = 0; i < traversersAmount; i++) {
            rowSetsTraversers.add(new BaseStepConcurrencyTest.RowSetsTraverser(condition));
        }
        ConcurrencyTestRunner<?, ?> runner = new ConcurrencyTestRunner<Object, Object>(rowSetsModifiers, rowSetsTraversers, condition);
        runner.runConcurrentTest();
        runner.checkNoExceptionRaised();
    }

    private class RowSetsModifier extends StopOnErrorCallable<BaseStep> {
        RowSetsModifier(AtomicBoolean condition) {
            super(condition);
        }

        @Override
        BaseStep doCall() {
            baseStep.addRowSetToInputRowSets(Mockito.mock(RowSet.class));
            baseStep.addRowSetToOutputRowSets(Mockito.mock(RowSet.class));
            return null;
        }
    }

    private class RowSetsTraverser extends StopOnErrorCallable<BaseStep> {
        RowSetsTraverser(AtomicBoolean condition) {
            super(condition);
        }

        @Override
        BaseStep doCall() {
            for (RowSet rowSet : baseStep.getInputRowSets()) {
                rowSet.setRowMeta(Mockito.mock(RowMetaInterface.class));
            }
            for (RowSet rowSet : baseStep.getOutputRowSets()) {
                rowSet.setRowMeta(Mockito.mock(RowMetaInterface.class));
            }
            return null;
        }
    }

    private class RowListenersModifier extends StopOnErrorCallable<BaseStep> {
        RowListenersModifier(AtomicBoolean condition) {
            super(condition);
        }

        @Override
        BaseStep doCall() {
            baseStep.addRowListener(Mockito.mock(RowListener.class));
            return null;
        }
    }

    private class RowListenersTraverser extends StopOnErrorCallable<BaseStep> {
        RowListenersTraverser(AtomicBoolean condition) {
            super(condition);
        }

        @Override
        BaseStep doCall() throws Exception {
            for (RowListener rowListener : baseStep.getRowListeners()) {
                rowListener.rowWrittenEvent(Mockito.mock(RowMetaInterface.class), new Object[]{  });
            }
            return null;
        }
    }
}

