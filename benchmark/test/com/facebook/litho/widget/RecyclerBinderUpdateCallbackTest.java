/**
 * Copyright 2014-present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.litho.widget;


import ComponentsReporter.LogLevel;
import ComponentsReporter.LogLevel.ERROR;
import ComponentsReporter.Reporter;
import RecyclerBinderUpdateCallback.ComponentRenderer;
import RecyclerBinderUpdateCallback.Operation;
import RecyclerBinderUpdateCallback.Operation.DELETE;
import RecyclerBinderUpdateCallback.Operation.INSERT;
import RecyclerBinderUpdateCallback.Operation.UPDATE;
import RecyclerBinderUpdateCallback.OperationExecutor;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link RecyclerBinderUpdateCallback}
 */
@RunWith(ComponentsTestRunner.class)
public class RecyclerBinderUpdateCallbackTest {
    private static final int OLD_DATA_SIZE = 12;

    private static final int NEW_DATA_SIZE = 12;

    private static final String OBJECT_KEY = "objectKey";

    private List<Object> mOldData;

    private List<Object> mNewData;

    private ComponentContext mComponentContext;

    private ComponentRenderer mComponentRenderer;

    private OperationExecutor mOperationExecutor;

    private Reporter mReporter;

    @Test
    public void testApplyChangeset() {
        RecyclerBinderUpdateCallback callback = new RecyclerBinderUpdateCallback(null, mOldData, mComponentRenderer, mOperationExecutor, 0);
        callback.onInserted(0, RecyclerBinderUpdateCallbackTest.OLD_DATA_SIZE);
        callback.applyChangeset(mComponentContext);
        Mockito.verify(mReporter, Mockito.never()).emitMessage(ArgumentMatchers.any(LogLevel.class), ArgumentMatchers.anyString());
        final List<RecyclerBinderUpdateCallback.Operation> operations = callback.getOperations();
        assertThat(operations.size()).isEqualTo(1);
        final RecyclerBinderUpdateCallback.Operation firstOperation = operations.get(0);
        assertThat(firstOperation.getType()).isEqualTo(INSERT);
        assertThat(firstOperation.getIndex()).isEqualTo(0);
        assertThat(firstOperation.getComponentContainers().size()).isEqualTo(RecyclerBinderUpdateCallbackTest.OLD_DATA_SIZE);
        assertThat(firstOperation.getDataContainers().size()).isEqualTo(RecyclerBinderUpdateCallbackTest.OLD_DATA_SIZE);
        assertThat(firstOperation.getDataContainers().get(0).getPrevious()).isNull();
        for (int i = 0, size = firstOperation.getDataContainers().size(); i < size; i++) {
            assertThat(firstOperation.getDataContainers().get(i).getNext()).isEqualTo(mOldData.get(i));
        }
    }

    @Test
    public void testApplyChangesetWithMultiOperations() {
        List<Object> oldData = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            oldData.add(("o" + i));
        }
        List<Object> newData = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            newData.add(("n" + i));
        }
        RecyclerBinderUpdateCallback callback = new RecyclerBinderUpdateCallback(null, oldData, mComponentRenderer, mOperationExecutor, 0);
        callback.onInserted(0, 12);
        callback.applyChangeset(mComponentContext);
        Mockito.verify(mReporter, Mockito.never()).emitMessage(ArgumentMatchers.any(LogLevel.class), ArgumentMatchers.anyString());
        final RecyclerBinderUpdateCallback callback2 = new RecyclerBinderUpdateCallback(oldData, newData, mComponentRenderer, mOperationExecutor, 0);
        callback2.onInserted(0, 5);
        callback2.onChanged(6, 6, null);
        callback2.onInserted(17, 3);
        callback2.applyChangeset(mComponentContext);
        final List<RecyclerBinderUpdateCallback.Operation> operations = callback2.getOperations();
        assertThat(operations.size()).isEqualTo(3);
        final RecyclerBinderUpdateCallback.Operation firstOperation = operations.get(0);
        assertThat(firstOperation.getType()).isEqualTo(INSERT);
        assertThat(firstOperation.getIndex()).isEqualTo(0);
        assertThat(firstOperation.getComponentContainers().size()).isEqualTo(5);
        for (int i = 0, size = firstOperation.getDataContainers().size(); i < size; i++) {
            assertThat(firstOperation.getDataContainers().get(i).getNext()).isEqualTo(newData.get(i));
        }
        final RecyclerBinderUpdateCallback.Operation secondOperation = operations.get(1);
        assertThat(secondOperation.getType()).isEqualTo(UPDATE);
        assertThat(secondOperation.getIndex()).isEqualTo(6);
        assertThat(secondOperation.getComponentContainers().size()).isEqualTo(6);
        for (int i = 0, size = secondOperation.getDataContainers().size(); i < size; i++) {
            assertThat(secondOperation.getDataContainers().get(i).getNext()).isEqualTo(newData.get((i + 6)));
            assertThat(secondOperation.getDataContainers().get(i).getPrevious()).isEqualTo(oldData.get((i + 1)));
        }
    }

    @Test
    public void testApplyChangesetWithInValidOperations() {
        final RecyclerBinderUpdateCallback callback1 = new RecyclerBinderUpdateCallback(null, mOldData, mComponentRenderer, mOperationExecutor, 0);
        callback1.onInserted(0, RecyclerBinderUpdateCallbackTest.OLD_DATA_SIZE);
        callback1.applyChangeset(mComponentContext);
        Mockito.verify(mReporter, Mockito.never()).emitMessage(ArgumentMatchers.any(LogLevel.class), ArgumentMatchers.anyString());
        final RecyclerBinderUpdateCallback.Operation operation = ((RecyclerBinderUpdateCallback.Operation) (callback1.getOperations().get(0)));
        RecyclerBinderUpdateCallbackTest.assertOperationComponentContainer(operation, mOldData);
        assertThat(operation.getDataContainers().size()).isEqualTo(RecyclerBinderUpdateCallbackTest.OLD_DATA_SIZE);
        for (int i = 0, size = operation.getDataContainers().size(); i < size; i++) {
            assertThat(operation.getDataContainers().get(i).getPrevious()).isNull();
            assertThat(operation.getDataContainers().get(i).getNext()).isEqualTo(mOldData.get(i));
        }
        final RecyclerBinderUpdateCallback callback2 = new RecyclerBinderUpdateCallback(mOldData, mNewData, mComponentRenderer, mOperationExecutor, 0);
        // Apply invalid operations
        callback2.onChanged(7, 5, null);
        callback2.onChanged(5, 1, null);
        callback2.onMoved(4, 5);
        callback2.onChanged(5, 1, null);
        callback2.onMoved(6, 4);
        callback2.onChanged(2, 3, null);
        callback2.onMoved(1, 10);
        callback2.onChanged(10, 1, null);
        callback2.onRemoved(0, 1);
        callback2.applyChangeset(mComponentContext);
        Mockito.verify(mReporter).emitMessage(ArgumentMatchers.eq(ERROR), ArgumentMatchers.anyString());
        final List<RecyclerBinderUpdateCallback.Operation> operations = callback2.getOperations();
        assertThat(operations.size()).isEqualTo(2);
        final RecyclerBinderUpdateCallback.Operation firstOperation = operations.get(0);
        assertThat(firstOperation.getType()).isEqualTo(DELETE);
        assertThat(firstOperation.getIndex()).isEqualTo(0);
        assertThat(firstOperation.getToIndex()).isEqualTo(RecyclerBinderUpdateCallbackTest.OLD_DATA_SIZE);
        for (int i = 0, size = firstOperation.getDataContainers().size(); i < size; i++) {
            assertThat(firstOperation.getDataContainers().get(i).getPrevious()).isEqualTo(mOldData.get(i));
        }
        final RecyclerBinderUpdateCallback.Operation secondOperation = operations.get(1);
        assertThat(secondOperation.getType()).isEqualTo(INSERT);
        assertThat(secondOperation.getIndex()).isEqualTo(0);
        assertThat(secondOperation.getComponentContainers().size()).isEqualTo(RecyclerBinderUpdateCallbackTest.NEW_DATA_SIZE);
        RecyclerBinderUpdateCallbackTest.assertOperationComponentContainer(secondOperation, mNewData);
        assertThat(secondOperation.getDataContainers().size()).isEqualTo(RecyclerBinderUpdateCallbackTest.NEW_DATA_SIZE);
        for (int i = 0, size = secondOperation.getDataContainers().size(); i < size; i++) {
            assertThat(secondOperation.getDataContainers().get(i).getNext()).isEqualTo(mNewData.get(i));
        }
    }

    private static class TestObjectRenderer implements RecyclerBinderUpdateCallback.ComponentRenderer<Object> {
        private final ComponentContext mComponentContext;

        TestObjectRenderer(ComponentContext componentContext) {
            mComponentContext = componentContext;
        }

        @Override
        public RenderInfo render(Object o, int idx) {
            return ComponentRenderInfo.create().customAttribute(RecyclerBinderUpdateCallbackTest.OBJECT_KEY, o).component(EmptyComponent.create(mComponentContext)).build();
        }
    }
}

