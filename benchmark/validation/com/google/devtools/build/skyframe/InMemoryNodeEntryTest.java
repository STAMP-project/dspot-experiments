/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.skyframe;


import DependencyState.ALREADY_EVALUATING;
import DependencyState.DONE;
import DependencyState.NEEDS_SCHEDULING;
import DirtyState.NEEDS_FORCED_REBUILDING;
import DirtyType.CHANGE;
import DirtyType.DIRTY;
import DirtyType.FORCE_REBUILD;
import NodeEntry.DirtyState.CHECK_DEPENDENCIES;
import NodeEntry.DirtyState.NEEDS_REBUILDING;
import NodeEntry.DirtyState.REBUILDING;
import NodeEntry.DirtyState.VERIFIED_CLEAN;
import Order.STABLE_ORDER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.collect.nestedset.NestedSet;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import com.google.devtools.build.lib.events.ExtendedEventHandler.Postable;
import com.google.devtools.build.lib.util.GroupedList;
import com.google.devtools.build.lib.util.GroupedList.GroupedListHelper;
import com.google.devtools.build.skyframe.InMemoryNodeEntry.ChangedValueAtSameVersionException;
import com.google.devtools.build.skyframe.SkyFunctionException.ReifiedSkyFunctionException;
import com.google.devtools.build.skyframe.SkyFunctionException.Transience;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link InMemoryNodeEntry}.
 */
@RunWith(JUnit4.class)
public class InMemoryNodeEntryTest {
    private static final NestedSet<TaggedEvents> NO_EVENTS = NestedSetBuilder.<TaggedEvents>emptySet(STABLE_ORDER);

    private static final NestedSet<Postable> NO_POSTS = NestedSetBuilder.<Postable>emptySet(STABLE_ORDER);

    @Test
    public void createEntry() {
        InMemoryNodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        assertThat(entry.isDone()).isFalse();
        assertThat(entry.isReady()).isTrue();
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.getTemporaryDirectDeps()).isEmpty();
    }

    private static final IntVersion ZERO_VERSION = IntVersion.of(0L);

    private static final IntVersion ONE_VERSION = IntVersion.of(1L);

    @Test
    public void signalEntry() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep1 = InMemoryNodeEntryTest.key("dep1");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep1);
        assertThat(entry.isReady()).isFalse();
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep1)).isTrue();
        assertThat(entry.isReady()).isTrue();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep1);
        SkyKey dep2 = InMemoryNodeEntryTest.key("dep2");
        SkyKey dep3 = InMemoryNodeEntryTest.key("dep3");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep2);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep3);
        assertThat(entry.isReady()).isFalse();
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep2)).isFalse();
        assertThat(entry.isReady()).isFalse();
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep3)).isTrue();
        assertThat(entry.isReady()).isTrue();
        assertThat(/* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L)).isEmpty();
        assertThat(entry.isDone()).isTrue();
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ZERO_VERSION);
        assertThat(entry.getDirectDeps()).containsExactly(dep1, dep2, dep3);
    }

    @Test
    public void signalExternalDep() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        entry.addExternalDep();
        assertThat(entry.isReady()).isFalse();
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null)).isTrue();
        assertThat(entry.isReady()).isTrue();
        entry.addExternalDep();
        assertThat(entry.isReady()).isFalse();
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null)).isTrue();
        assertThat(entry.isReady()).isTrue();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly();
    }

    @Test
    public void reverseDeps() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        SkyKey mother = InMemoryNodeEntryTest.key("mother");
        SkyKey father = InMemoryNodeEntryTest.key("father");
        assertThat(entry.addReverseDepAndCheckIfDone(mother)).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.addReverseDepAndCheckIfDone(null)).isEqualTo(ALREADY_EVALUATING);
        assertThat(entry.addReverseDepAndCheckIfDone(father)).isEqualTo(ALREADY_EVALUATING);
        entry.markRebuilding();
        assertThat(/* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L)).containsExactly(mother, father);
        assertThat(entry.getReverseDepsForDoneEntry()).containsExactly(mother, father);
        assertThat(entry.isDone()).isTrue();
        entry.removeReverseDep(mother);
        assertThat(entry.getReverseDepsForDoneEntry()).doesNotContain(mother);
    }

    @Test
    public void errorValue() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        ReifiedSkyFunctionException exception = new ReifiedSkyFunctionException(new GenericFunctionException(new SomeErrorException("oops"), Transience.PERSISTENT), InMemoryNodeEntryTest.key("cause"));
        ErrorInfo errorInfo = ErrorInfo.fromException(exception, false);
        assertThat(/* value= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, null, errorInfo, 0L)).isEmpty();
        assertThat(entry.isDone()).isTrue();
        assertThat(entry.getValue()).isNull();
        assertThat(entry.getErrorInfo()).isEqualTo(errorInfo);
    }

    @Test
    public void errorAndValue() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        ReifiedSkyFunctionException exception = new ReifiedSkyFunctionException(new GenericFunctionException(new SomeErrorException("oops"), Transience.PERSISTENT), InMemoryNodeEntryTest.key("cause"));
        ErrorInfo errorInfo = ErrorInfo.fromException(exception, false);
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, errorInfo, 0L);
        assertThat(entry.isDone()).isTrue();
        assertThat(entry.getErrorInfo()).isEqualTo(errorInfo);
    }

    @Test
    public void crashOnNullErrorAndValue() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        try {
            /* value= */
            /* errorInfo= */
            /* graphVersion= */
            InMemoryNodeEntryTest.setValue(entry, null, null, 0L);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void crashOnTooManySignals() {
        InMemoryNodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        try {
            entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void crashOnSetValueWhenDone() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDone()).isTrue();
        try {
            /* errorInfo= */
            /* graphVersion= */
            InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 1L);
            Assert.fail();
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void crashOnChangedValueAtSameVersion() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);
        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(1), null, 0L);
        entry.markDirty(CHANGE);
        entry.addReverseDepAndCheckIfDone(null);
        entry.markRebuilding();
        try {
            /* errorInfo= */
            /* graphVersion= */
            InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(2), null, 0L);
            Assert.fail();
        } catch (ChangedValueAtSameVersionException e) {
            // Expected.
        }
    }

    @Test
    public void dirtyLifecycle() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(DIRTY);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isFalse();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
        assertThat(entry.getTemporaryDirectDeps()).isEmpty();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        entry.addReverseDepAndCheckIfDone(parent);
        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, dep);
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep);
        assertThat(entry.isReady()).isTrue();
        entry.markRebuilding();
        assertThat(/* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 1L)).containsExactly(parent);
    }

    @Test
    public void changedLifecycle() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(CHANGE);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        entry.addReverseDepAndCheckIfDone(parent);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        assertThat(entry.isReady()).isTrue();
        assertThat(entry.getTemporaryDirectDeps()).isEmpty();
        entry.markRebuilding();
        assertThat(/* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 1L)).containsExactly(parent);
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ONE_VERSION);
    }

    @Test
    public void forceRebuildLifecycle() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(FORCE_REBUILD);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        entry.addReverseDepAndCheckIfDone(parent);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_FORCED_REBUILDING);
        assertThat(entry.isReady()).isTrue();
        assertThat(entry.getTemporaryDirectDeps()).isEmpty();
        // A force-rebuilt node tolerates evaluating to different values within the same version.
        entry.forceRebuild();
        assertThat(/* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L)).containsExactly(parent);
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ZERO_VERSION);
    }

    @Test
    public void markDirtyThenChanged() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(DIRTY);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isFalse();
        assertThat(entry.isDone()).isFalse();
        entry.markDirty(CHANGE);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
    }

    @Test
    public void markChangedThenDirty() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(CHANGE);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.isDone()).isFalse();
        entry.markDirty(DIRTY);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
    }

    @Test
    public void crashOnTwiceMarkedChanged() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(CHANGE);
        try {
            entry.markDirty(CHANGE);
            Assert.fail("Cannot mark entry changed twice");
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void crashOnTwiceMarkedDirty() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        entry.markDirty(DIRTY);
        try {
            entry.markDirty(DIRTY);
            Assert.fail("Cannot mark entry dirty twice");
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void allowTwiceMarkedForceRebuild() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(FORCE_REBUILD);
        entry.markDirty(FORCE_REBUILD);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isTrue();
        assertThat(entry.isDone()).isFalse();
    }

    @Test
    public void crashOnAddReverseDepTwice() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        assertThat(entry.addReverseDepAndCheckIfDone(parent)).isEqualTo(NEEDS_SCHEDULING);
        try {
            entry.addReverseDepAndCheckIfDone(parent);
            entry.markRebuilding();
            assertThat(/* errorInfo= */
            /* graphVersion= */
            InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L)).containsExactly(parent);
            Assert.fail("Cannot add same dep twice");
        } catch (IllegalStateException e) {
            assertThat(e).hasMessageThat().contains("Duplicate reverse deps");
        }
    }

    @Test
    public void crashOnAddReverseDepTwiceAfterDone() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        assertThat(entry.addReverseDepAndCheckIfDone(parent)).isEqualTo(DONE);
        try {
            entry.addReverseDepAndCheckIfDone(parent);
            // We only check for duplicates when we request all the reverse deps.
            entry.getReverseDepsForDoneEntry();
            Assert.fail("Cannot add same dep twice");
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void crashOnAddReverseDepBeforeAfterDone() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        assertThat(entry.addReverseDepAndCheckIfDone(parent)).isEqualTo(NEEDS_SCHEDULING);
        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        try {
            entry.addReverseDepAndCheckIfDone(parent);
            // We only check for duplicates when we request all the reverse deps.
            entry.getReverseDepsForDoneEntry();
            Assert.fail("Cannot add same dep twice");
        } catch (IllegalStateException e) {
            // Expected.
        }
    }

    @Test
    public void pruneBeforeBuild() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(DIRTY);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isFalse();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        entry.addReverseDepAndCheckIfDone(parent);
        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null);
        assertThat(entry.getDirtyState()).isEqualTo(VERIFIED_CLEAN);
        assertThat(entry.markClean()).containsExactly(parent);
        assertThat(entry.isDone()).isTrue();
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ZERO_VERSION);
    }

    private static class IntegerValue implements SkyValue {
        private final int value;

        IntegerValue(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object that) {
            return (that instanceof InMemoryNodeEntryTest.IntegerValue) && ((((InMemoryNodeEntryTest.IntegerValue) (that)).value) == (value));
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    @Test
    public void pruneAfterBuild() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        entry.markDirty(DIRTY);
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, null);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep);
        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 1L);
        assertThat(entry.isDone()).isTrue();
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ZERO_VERSION);
    }

    @Test
    public void noPruneWhenDetailsChange() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(DIRTY);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isFalse();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
        SkyKey parent = InMemoryNodeEntryTest.key("parent");
        entry.addReverseDepAndCheckIfDone(parent);
        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, null);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep);
        ReifiedSkyFunctionException exception = new ReifiedSkyFunctionException(new GenericFunctionException(new SomeErrorException("oops"), Transience.PERSISTENT), InMemoryNodeEntryTest.key("cause"));
        entry.markRebuilding();
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), ErrorInfo.fromException(exception, false), 1L);
        assertThat(entry.isDone()).isTrue();
        assertWithMessage("Version increments when setValue changes").that(entry.getVersion()).isEqualTo(IntVersion.of(1));
    }

    @Test
    public void pruneWhenDepGroupReordered() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        SkyKey dep1InGroup = InMemoryNodeEntryTest.key("dep1InGroup");
        SkyKey dep2InGroup = InMemoryNodeEntryTest.key("dep2InGroup");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        InMemoryNodeEntryTest.addTemporaryDirectDeps(entry, dep1InGroup, dep2InGroup);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep1InGroup);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep2InGroup);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        assertThat(entry.isDirty()).isFalse();
        assertThat(entry.isDone()).isTrue();
        entry.markDirty(DIRTY);
        assertThat(entry.isDirty()).isTrue();
        assertThat(entry.isChanged()).isFalse();
        assertThat(entry.isDone()).isFalse();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        assertThat(entry.isReady()).isTrue();
        entry.addReverseDepAndCheckIfDone(null);
        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, null);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep);
        entry.markRebuilding();
        InMemoryNodeEntryTest.addTemporaryDirectDeps(entry, dep2InGroup, dep1InGroup);
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, dep2InGroup)).isFalse();
        assertThat(entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, dep1InGroup)).isTrue();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 1L);
        assertThat(entry.isDone()).isTrue();
        assertWithMessage("Version does not change when dep group reordered").that(entry.getVersion()).isEqualTo(IntVersion.of(0));
    }

    @Test
    public void errorInfoCannotBePruned() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        ReifiedSkyFunctionException exception = new ReifiedSkyFunctionException(new GenericFunctionException(new SomeErrorException("oops"), Transience.PERSISTENT), InMemoryNodeEntryTest.key("cause"));
        ErrorInfo errorInfo = ErrorInfo.fromException(exception, false);
        /* value= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, null, errorInfo, 0L);
        entry.markDirty(DIRTY);
        entry.addReverseDepAndCheckIfDone(null);// Restart evaluation.

        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, null);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep);
        entry.markRebuilding();
        /* value= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, null, errorInfo, 1L);
        assertThat(entry.isDone()).isTrue();
        // ErrorInfo is treated as a NotComparableSkyValue, so it is not pruned.
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ONE_VERSION);
    }

    @Test
    public void ineligibleForPruning() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry() {
            @Override
            public boolean isEligibleForChangePruningOnUnchangedValue() {
                return false;
            }
        };
        entry.addReverseDepAndCheckIfDone(null);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        entry.markDirty(CHANGE);
        entry.addReverseDepAndCheckIfDone(null);
        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 1L);
        assertThat(entry.getVersion()).isEqualTo(InMemoryNodeEntryTest.ONE_VERSION);
    }

    @Test
    public void getDependencyGroup() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        SkyKey dep2 = InMemoryNodeEntryTest.key("dep2");
        SkyKey dep3 = InMemoryNodeEntryTest.key("dep3");
        InMemoryNodeEntryTest.addTemporaryDirectDeps(entry, dep, dep2);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep3);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep2);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep3);
        /* value= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        entry.markDirty(DIRTY);
        entry.addReverseDepAndCheckIfDone(null);// Restart evaluation.

        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep, dep2);
        InMemoryNodeEntryTest.addTemporaryDirectDeps(entry, dep, dep2);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null);
        /* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null);
        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep3);
    }

    @Test
    public void maintainDependencyGroupAfterRemoval() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        SkyKey dep2 = InMemoryNodeEntryTest.key("dep2");
        SkyKey dep3 = InMemoryNodeEntryTest.key("dep3");
        SkyKey dep4 = InMemoryNodeEntryTest.key("dep4");
        SkyKey dep5 = InMemoryNodeEntryTest.key("dep5");
        InMemoryNodeEntryTest.addTemporaryDirectDeps(entry, dep, dep2, dep3);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep4);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep5);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep4);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        // Oops! Evaluation terminated with an error, but we're going to set this entry's value anyway.
        entry.removeUnfinishedDeps(ImmutableSet.of(dep2, dep3, dep5));
        ReifiedSkyFunctionException exception = new ReifiedSkyFunctionException(new GenericFunctionException(new SomeErrorException("oops"), Transience.PERSISTENT), InMemoryNodeEntryTest.key("key"));
        InMemoryNodeEntryTest.setValue(entry, null, ErrorInfo.fromException(exception, false), 0L);
        entry.markDirty(DIRTY);
        entry.addReverseDepAndCheckIfDone(null);// Restart evaluation.

        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep4);
    }

    @Test
    public void pruneWhenDepsChange() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        SkyKey dep = InMemoryNodeEntryTest.key("dep");
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        entry.markDirty(DIRTY);
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        assertThat(entry.getNextDirtyDirectDeps()).containsExactly(dep);
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
        assertThat(/* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, null)).isTrue();
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasTemporaryDirectDepsThat().containsExactly(dep);
        entry.markRebuilding();
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, InMemoryNodeEntryTest.key("dep2"));
        assertThat(/* childForDebugging= */
        entry.signalDep(InMemoryNodeEntryTest.ONE_VERSION, null)).isTrue();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 1L);
        assertThat(entry.isDone()).isTrue();
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).hasVersionThat().isEqualTo(InMemoryNodeEntryTest.ZERO_VERSION);
    }

    @Test
    public void checkDepsOneByOne() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(null);// Start evaluation.

        entry.markRebuilding();
        List<SkyKey> deps = new ArrayList<>();
        for (int ii = 0; ii < 10; ii++) {
            SkyKey dep = InMemoryNodeEntryTest.key(Integer.toString(ii));
            deps.add(dep);
            InMemoryNodeEntryTest.addTemporaryDirectDep(entry, dep);
            entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
        }
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new InMemoryNodeEntryTest.IntegerValue(5), null, 0L);
        entry.markDirty(DIRTY);
        entry.addReverseDepAndCheckIfDone(null);// Start new evaluation.

        assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
        for (int ii = 0; ii < 10; ii++) {
            assertThat(entry.getNextDirtyDirectDeps()).containsExactly(deps.get(ii));
            InMemoryNodeEntryTest.addTemporaryDirectDep(entry, deps.get(ii));
            assertThat(/* childForDebugging= */
            entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, null)).isTrue();
            if (ii < 9) {
                assertThat(entry.getDirtyState()).isEqualTo(CHECK_DEPENDENCIES);
            } else {
                assertThat(entry.getDirtyState()).isEqualTo(VERIFIED_CLEAN);
            }
        }
    }

    @Test
    public void signalOnlyNewParents() throws InterruptedException {
        NodeEntry entry = new InMemoryNodeEntry();
        entry.addReverseDepAndCheckIfDone(InMemoryNodeEntryTest.key("parent"));
        entry.markRebuilding();
        /* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 0L);
        entry.markDirty(CHANGE);
        SkyKey newParent = InMemoryNodeEntryTest.key("new parent");
        entry.addReverseDepAndCheckIfDone(newParent);
        assertThat(entry.getDirtyState()).isEqualTo(NEEDS_REBUILDING);
        entry.markRebuilding();
        assertThat(entry.getDirtyState()).isEqualTo(REBUILDING);
        assertThat(/* errorInfo= */
        /* graphVersion= */
        InMemoryNodeEntryTest.setValue(entry, new SkyValue() {}, null, 1L)).containsExactly(newParent);
    }

    @Test
    public void testClone() throws InterruptedException {
        InMemoryNodeEntry entry = new InMemoryNodeEntry();
        IntVersion version = IntVersion.of(0);
        InMemoryNodeEntryTest.IntegerValue originalValue = new InMemoryNodeEntryTest.IntegerValue(42);
        SkyKey originalChild = InMemoryNodeEntryTest.key("child");
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        entry.markRebuilding();
        InMemoryNodeEntryTest.addTemporaryDirectDep(entry, originalChild);
        entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, originalChild);
        entry.setValue(originalValue, version, null);
        entry.addReverseDepAndCheckIfDone(InMemoryNodeEntryTest.key("parent1"));
        InMemoryNodeEntry clone1 = entry.cloneNodeEntry();
        entry.addReverseDepAndCheckIfDone(InMemoryNodeEntryTest.key("parent2"));
        InMemoryNodeEntry clone2 = entry.cloneNodeEntry();
        entry.removeReverseDep(InMemoryNodeEntryTest.key("parent1"));
        entry.removeReverseDep(InMemoryNodeEntryTest.key("parent2"));
        InMemoryNodeEntryTest.IntegerValue updatedValue = new InMemoryNodeEntryTest.IntegerValue(52);
        clone2.markDirty(CHANGE);
        clone2.addReverseDepAndCheckIfDone(null);
        SkyKey newChild = InMemoryNodeEntryTest.key("newchild");
        InMemoryNodeEntryTest.addTemporaryDirectDep(clone2, newChild);
        clone2.signalDep(InMemoryNodeEntryTest.ONE_VERSION, newChild);
        clone2.markRebuilding();
        clone2.setValue(updatedValue, version.next(), null);
        assertThat(entry.getVersion()).isEqualTo(version);
        assertThat(clone1.getVersion()).isEqualTo(version);
        assertThat(clone2.getVersion()).isEqualTo(version.next());
        assertThat(entry.getValue()).isEqualTo(originalValue);
        assertThat(clone1.getValue()).isEqualTo(originalValue);
        assertThat(clone2.getValue()).isEqualTo(updatedValue);
        assertThat(entry.getDirectDeps()).containsExactly(originalChild);
        assertThat(clone1.getDirectDeps()).containsExactly(originalChild);
        assertThat(clone2.getDirectDeps()).containsExactly(newChild);
        assertThat(entry.getReverseDepsForDoneEntry()).isEmpty();
        assertThat(clone1.getReverseDepsForDoneEntry()).containsExactly(InMemoryNodeEntryTest.key("parent1"));
        assertThat(clone2.getReverseDepsForDoneEntry()).containsExactly(InMemoryNodeEntryTest.key("parent1"), InMemoryNodeEntryTest.key("parent2"));
    }

    @Test
    public void getGroupedDirectDeps() throws InterruptedException {
        InMemoryNodeEntry entry = new InMemoryNodeEntry();
        ImmutableList<ImmutableSet<SkyKey>> groupedDirectDeps = ImmutableList.of(ImmutableSet.of(InMemoryNodeEntryTest.key("1A")), ImmutableSet.of(InMemoryNodeEntryTest.key("2A"), InMemoryNodeEntryTest.key("2B")), ImmutableSet.of(InMemoryNodeEntryTest.key("3A"), InMemoryNodeEntryTest.key("3B"), InMemoryNodeEntryTest.key("3C")), ImmutableSet.of(InMemoryNodeEntryTest.key("4A"), InMemoryNodeEntryTest.key("4B"), InMemoryNodeEntryTest.key("4C"), InMemoryNodeEntryTest.key("4D")));
        NodeEntrySubjectFactory.assertThatNodeEntry(entry).addReverseDepAndCheckIfDone(null).isEqualTo(NEEDS_SCHEDULING);
        entry.markRebuilding();
        for (Set<SkyKey> depGroup : groupedDirectDeps) {
            GroupedListHelper<SkyKey> helper = new GroupedListHelper();
            helper.startGroup();
            for (SkyKey item : depGroup) {
                helper.add(item);
            }
            helper.endGroup();
            entry.addTemporaryDirectDeps(helper);
            for (SkyKey dep : depGroup) {
                entry.signalDep(InMemoryNodeEntryTest.ZERO_VERSION, dep);
            }
        }
        entry.setValue(new InMemoryNodeEntryTest.IntegerValue(42), IntVersion.of(42L), null);
        int i = 0;
        GroupedList<SkyKey> entryGroupedDirectDeps = entry.getGroupedDirectDeps();
        assertThat(Iterables.size(entryGroupedDirectDeps)).isEqualTo(groupedDirectDeps.size());
        for (Iterable<SkyKey> depGroup : entryGroupedDirectDeps) {
            assertThat(depGroup).containsExactlyElementsIn(groupedDirectDeps.get((i++)));
        }
    }
}

