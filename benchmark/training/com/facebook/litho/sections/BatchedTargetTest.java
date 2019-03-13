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
package com.facebook.litho.sections;


import ComponentsConfiguration.IS_INTERNAL_BUILD;
import com.facebook.litho.sections.SectionTree.Target;
import com.facebook.litho.sections.logger.SectionsDebugLogger;
import com.facebook.litho.testing.testrunner.ComponentsTestRunner;
import com.facebook.litho.widget.ChangeSetCompleteCallback;
import com.facebook.litho.widget.ComponentRenderInfo;
import com.facebook.litho.widget.RenderInfo;
import java.util.List;
import org.hamcrest.core.Is;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;


/**
 * Tests {@link BatchedTarget}
 */
@RunWith(ComponentsTestRunner.class)
public class BatchedTargetTest {
    private Target mMockTarget;

    private SectionsDebugLogger mMockSectionsDebugLogger;

    private BatchedTarget mTarget;

    @Captor
    private ArgumentCaptor<List<RenderInfo>> mListCaptor;

    @Test
    public void testConsolidateSequentialInserts() throws Exception {
        Change[] ops = new Change[]{ Change.insert(0, ComponentRenderInfo.createEmpty()), Change.insert(1, ComponentRenderInfo.createEmpty()), Change.insert(2, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).insertRange(ArgumentMatchers.eq(0), ArgumentMatchers.eq(3), mListCaptor.capture());
        assertThat(ops[0].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(0));
        assertThat(ops[1].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(1));
        assertThat(ops[2].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(2));
    }

    @Test
    public void testOnlyConsolidateInsertsIfSequentialIndexes() throws Exception {
        Change[] ops = new Change[]{ Change.insert(0, ComponentRenderInfo.createEmpty()), Change.insert(1, ComponentRenderInfo.createEmpty()), Change.insert(20, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).insertRange(ArgumentMatchers.eq(0), ArgumentMatchers.eq(2), mListCaptor.capture());
        assertThat(ops[0].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(0));
        assertThat(ops[1].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(1));
        Mockito.verify(mMockTarget).insert(20, ops[2].getRenderInfo());
    }

    @Test
    public void testDoNotConsolidateInsertsIfNotIncreasingSequentialIndexes() throws Exception {
        Change[] ops = new Change[]{ Change.insert(10, ComponentRenderInfo.createEmpty()), Change.insert(9, ComponentRenderInfo.createEmpty()), Change.insert(20, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).insert(10, ops[0].getRenderInfo());
        Mockito.verify(mMockTarget).insert(9, ops[1].getRenderInfo());
        Mockito.verify(mMockTarget).insert(20, ops[2].getRenderInfo());
    }

    @Test
    public void testDuplicateIndexInserts() throws Exception {
        Change[] ops = new Change[]{ Change.insert(0, ComponentRenderInfo.createEmpty()), Change.insert(1, ComponentRenderInfo.createEmpty()), Change.insert(1, ComponentRenderInfo.createEmpty()), Change.insert(20, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).insertRange(ArgumentMatchers.eq(0), ArgumentMatchers.eq(2), mListCaptor.capture());
        assertThat(ops[0].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(0));
        assertThat(ops[1].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(1));
        Mockito.verify(mMockTarget).insert(1, ops[2].getRenderInfo());
        Mockito.verify(mMockTarget).insert(20, ops[3].getRenderInfo());
    }

    @Test
    public void testConsolidateSequentialDeletes() throws Exception {
        Change[] ops = new Change[]{ Change.remove(1), Change.remove(1), Change.remove(1) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).deleteRange(1, 3);
    }

    @Test
    public void testDoNotConsolidateDeletesIfSequentialIncreasingIndexes() throws Exception {
        Change[] ops = new Change[]{ Change.remove(2), Change.remove(1), Change.remove(20), Change.remove(21) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).deleteRange(1, 2);
        Mockito.verify(mMockTarget).delete(20);
        Mockito.verify(mMockTarget).delete(21);
    }

    @Test
    public void testConsolidateSequentialUpdates() throws Exception {
        Change[] ops = new Change[]{ Change.update(2, ComponentRenderInfo.createEmpty()), Change.update(1, ComponentRenderInfo.createEmpty()), Change.update(20, ComponentRenderInfo.createEmpty()), Change.update(21, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).updateRange(ArgumentMatchers.eq(1), ArgumentMatchers.eq(2), mListCaptor.capture());
        assertThat(ops[1].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(0));
        assertThat(ops[0].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(1));
        Mockito.verify(mMockTarget).updateRange(ArgumentMatchers.eq(20), ArgumentMatchers.eq(2), mListCaptor.capture());
        assertThat(ops[2].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(0));
        assertThat(ops[3].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(1));
    }

    @Test
    public void testOnlyConsolidateUpdatesIfSequentialIndexes() throws Exception {
        Change[] ops = new Change[]{ Change.update(2, ComponentRenderInfo.createEmpty()), Change.update(12, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).update(2, ops[0].getRenderInfo());
        Mockito.verify(mMockTarget).update(12, ops[1].getRenderInfo());
    }

    @Test
    public void testDuplicateSequentialUpdatesUseLastComponentInfo() throws Exception {
        Change[] ops = new Change[]{ Change.update(99, ComponentRenderInfo.createEmpty()), Change.update(100, ComponentRenderInfo.createEmpty()), Change.update(101, ComponentRenderInfo.createEmpty()), Change.update(99, ComponentRenderInfo.createEmpty()) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).updateRange(ArgumentMatchers.eq(99), ArgumentMatchers.eq(3), mListCaptor.capture());
        assertThat(ops[3].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(0));
        assertThat(ops[1].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(1));
        assertThat(ops[2].getRenderInfo()).isEqualTo(mListCaptor.getValue().get(2));
    }

    @Test
    public void testInsertRangeConsolidatesFirst() throws Exception {
        Change[] ops = new Change[]{ Change.insert(99, ComponentRenderInfo.createEmpty()), Change.insert(100, ComponentRenderInfo.createEmpty()), Change.insert(101, ComponentRenderInfo.createEmpty()), Change.insertRange(102, 20, dummyComponentInfos(20)) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).insertRange(ArgumentMatchers.eq(99), ArgumentMatchers.eq(3), ArgumentMatchers.anyListOf(RenderInfo.class));
        Mockito.verify(mMockTarget).insertRange(ArgumentMatchers.eq(102), ArgumentMatchers.eq(20), ArgumentMatchers.anyListOf(RenderInfo.class));
    }

    @Test
    public void testUpdateRangeConsolidatesFirst() throws Exception {
        Change[] ops = new Change[]{ Change.update(99, ComponentRenderInfo.createEmpty()), Change.update(100, ComponentRenderInfo.createEmpty()), Change.update(101, ComponentRenderInfo.createEmpty()), Change.updateRange(102, 20, dummyComponentInfos(20)) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).updateRange(ArgumentMatchers.eq(99), ArgumentMatchers.eq(3), ArgumentMatchers.anyListOf(RenderInfo.class));
        Mockito.verify(mMockTarget).updateRange(ArgumentMatchers.eq(102), ArgumentMatchers.eq(20), ArgumentMatchers.anyListOf(RenderInfo.class));
    }

    @Test
    public void testDeleteRangeConsolidatesFirst() throws Exception {
        Change[] ops = new Change[]{ Change.remove(99), Change.remove(99), Change.remove(99), Change.removeRange(102, 20) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).deleteRange(ArgumentMatchers.eq(99), ArgumentMatchers.eq(3));
        Mockito.verify(mMockTarget).deleteRange(ArgumentMatchers.eq(102), ArgumentMatchers.eq(20));
    }

    @Test
    public void testConsolidateDifferentTypes() throws Exception {
        Change[] ops = new Change[]{ Change.remove(2), Change.remove(1), Change.update(99, ComponentRenderInfo.createEmpty()), Change.update(100, ComponentRenderInfo.createEmpty()), Change.insert(0, ComponentRenderInfo.createEmpty()), Change.insert(1, ComponentRenderInfo.createEmpty()), Change.update(101, ComponentRenderInfo.createEmpty()), Change.update(99, ComponentRenderInfo.createEmpty()), Change.move(14, 55) };
        executeOperations(ops);
        Mockito.verify(mMockTarget).deleteRange(1, 2);
        Mockito.verify(mMockTarget).updateRange(ArgumentMatchers.eq(99), ArgumentMatchers.eq(2), ArgumentMatchers.anyListOf(RenderInfo.class));
        Mockito.verify(mMockTarget).insertRange(ArgumentMatchers.eq(0), ArgumentMatchers.eq(2), ArgumentMatchers.anyListOf(RenderInfo.class));
        Mockito.verify(mMockTarget).update(101, ops[6].getRenderInfo());
        Mockito.verify(mMockTarget).update(99, ops[7].getRenderInfo());
        Mockito.verify(mMockTarget).move(14, 55);
    }

    @Test
    public void testLoggerDelete() throws Exception {
        Assume.assumeThat("Logging is only available in debug mode.", IS_INTERNAL_BUILD, Is.is(true));
        Change[] ops = new Change[]{ Change.insert(0, ComponentRenderInfo.createEmpty()), Change.remove(0) };
        executeOperations(ops);
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 0, ops[0].getRenderInfo(), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logDelete("", 0, Thread.currentThread().getName());
    }

    @Test
    public void testLoggerDifferentTypes() throws Exception {
        Assume.assumeThat("Logging is only available in debug mode.", IS_INTERNAL_BUILD, Is.is(true));
        Change[] ops = new Change[]{ Change.insert(0, ComponentRenderInfo.createEmpty()), Change.insertRange(1, 3, dummyComponentInfos(3)), Change.update(3, ComponentRenderInfo.createEmpty()), Change.updateRange(2, 2, dummyComponentInfos(2)), Change.insertRange(4, 2, dummyComponentInfos(2)), Change.insert(6, ComponentRenderInfo.createEmpty()), Change.remove(5), Change.move(2, 3) };
        executeOperations(ops);
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 0, ops[0].getRenderInfo(), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 1, ops[1].getRenderInfos().get(0), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 2, ops[1].getRenderInfos().get(1), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 3, ops[1].getRenderInfos().get(2), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logUpdate("", 3, ops[2].getRenderInfo(), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logUpdate("", 2, ops[3].getRenderInfos().get(0), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logUpdate("", 3, ops[3].getRenderInfos().get(1), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 4, ops[4].getRenderInfos().get(0), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 5, ops[4].getRenderInfos().get(1), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logInsert("", 6, ops[5].getRenderInfo(), Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logDelete("", 5, Thread.currentThread().getName());
        Mockito.verify(mMockSectionsDebugLogger).logMove("", 2, 3, Thread.currentThread().getName());
    }

    @Test
    public void testNotifyChangeSetCompleteForwarded() {
        final ChangeSetCompleteCallback changeSetCompleteCallback = Mockito.mock(ChangeSetCompleteCallback.class);
        mTarget.notifyChangeSetComplete(true, changeSetCompleteCallback);
        Mockito.verify(mMockTarget).notifyChangeSetComplete(true, changeSetCompleteCallback);
    }
}

