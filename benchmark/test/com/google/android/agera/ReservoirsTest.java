/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.agera;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.android.agera.test.matchers.HasPrivateConstructor;
import com.google.android.agera.test.matchers.ReservoirGives;
import com.google.android.agera.test.matchers.UpdatableUpdated;
import com.google.android.agera.test.mocks.MockUpdatable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(manifest = NONE)
@RunWith(RobolectricTestRunner.class)
public final class ReservoirsTest {
    private static final String STRING_A = "STRING_A";

    private static final String STRING_B = "STRING_B";

    private static final Integer INTEGER_1 = 1;

    private static final Integer INTEGER_2 = 2;

    private ReservoirsTest.MockQueue mockQueue;

    private Reservoir<String> stringReservoir;

    private Reservoir<Integer> integerReservoir;

    private Reservoir<Object> customQueueReservoir;

    private MockUpdatable updatable;

    private MockUpdatable anotherUpdatable;

    @Test
    public void shouldGiveAbsentValueFromEmptyReservoir() throws Exception {
        MatcherAssert.assertThat(integerReservoir, ReservoirGives.givesAbsentValueOf(Integer.class));
    }

    @Test
    public void shouldQueueValues() throws Exception {
        stringReservoir.accept(ReservoirsTest.STRING_A);
        stringReservoir.accept(ReservoirsTest.STRING_A);
        stringReservoir.accept(ReservoirsTest.STRING_B);
        stringReservoir.accept(ReservoirsTest.STRING_B);
        MatcherAssert.assertThat(stringReservoir, ReservoirGives.givesPresentValue(ReservoirsTest.STRING_A));
        MatcherAssert.assertThat(stringReservoir, ReservoirGives.givesPresentValue(ReservoirsTest.STRING_A));
        MatcherAssert.assertThat(stringReservoir, ReservoirGives.givesPresentValue(ReservoirsTest.STRING_B));
        MatcherAssert.assertThat(stringReservoir, ReservoirGives.givesPresentValue(ReservoirsTest.STRING_B));
        MatcherAssert.assertThat(stringReservoir, ReservoirGives.givesAbsentValueOf(String.class));
    }

    @Test
    public void shouldNotGetUpdateFromEmptyReservoir() throws Exception {
        updatable.addToObservable(stringReservoir);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldGetUpdateOnFirstValue() throws Exception {
        updatable.addToObservable(integerReservoir);
        give(integerReservoir, ReservoirsTest.INTEGER_1);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldGetUpdateOnFirstAcceptedValue() throws Exception {
        mockQueue.reject(ReservoirsTest.STRING_A);
        updatable.addToObservable(customQueueReservoir);
        give(customQueueReservoir, ReservoirsTest.STRING_A);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
        give(customQueueReservoir, ReservoirsTest.STRING_B);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldGetUpdateFromFirstUpdatableOnRegisteringToNonEmptyReservoir() throws Exception {
        give(stringReservoir, ReservoirsTest.STRING_A);
        updatable.addToObservable(stringReservoir);
        anotherUpdatable.addToObservable(stringReservoir);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
        MatcherAssert.assertThat(anotherUpdatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldNotGetUpdateOnSecondValueWithoutDequeuingFirst() throws Exception {
        updatable.addToObservable(stringReservoir);
        give(stringReservoir, ReservoirsTest.STRING_A);
        updatable.resetUpdated();
        give(stringReservoir, ReservoirsTest.STRING_B);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldGetUpdateOnDequeuingNonLastValue() throws Exception {
        updatable.addToObservable(integerReservoir);
        give(integerReservoir, ReservoirsTest.INTEGER_1);
        give(integerReservoir, ReservoirsTest.INTEGER_2);
        updatable.resetUpdated();
        retrieveFrom(integerReservoir);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasUpdated());
    }

    @Test
    public void shouldNotGetUpdateOnDequeuingLastValue() throws Exception {
        updatable.addToObservable(integerReservoir);
        give(integerReservoir, ReservoirsTest.INTEGER_1);
        updatable.resetUpdated();
        retrieveFrom(integerReservoir);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldUseCustomQueue() throws Exception {
        mockQueue.reject(ReservoirsTest.INTEGER_1).prioritize(ReservoirsTest.STRING_A);
        give(customQueueReservoir, ReservoirsTest.INTEGER_2);
        give(customQueueReservoir, ReservoirsTest.STRING_B);
        give(customQueueReservoir, ReservoirsTest.INTEGER_1);
        give(customQueueReservoir, ReservoirsTest.STRING_A);
        MatcherAssert.assertThat(customQueueReservoir, ReservoirGives.givesPresentValue(((Object) (ReservoirsTest.STRING_A))));
        MatcherAssert.assertThat(customQueueReservoir, ReservoirGives.givesPresentValue(((Object) (ReservoirsTest.INTEGER_2))));
        MatcherAssert.assertThat(customQueueReservoir, ReservoirGives.givesPresentValue(((Object) (ReservoirsTest.STRING_B))));
        MatcherAssert.assertThat(customQueueReservoir, ReservoirGives.givesAbsentValueOf(Object.class));
    }

    @Test
    public void shouldNotGetUpdateWhenCustomQueuePrioritizesAnotherValue() throws Exception {
        mockQueue.prioritize(ReservoirsTest.INTEGER_2);
        updatable.addToObservable(customQueueReservoir);
        give(customQueueReservoir, ReservoirsTest.INTEGER_1);
        updatable.resetUpdated();
        give(customQueueReservoir, ReservoirsTest.INTEGER_2);
        MatcherAssert.assertThat(updatable, UpdatableUpdated.wasNotUpdated());
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Reservoirs.class, HasPrivateConstructor.hasPrivateConstructor());
    }

    private static final class MockQueue implements Queue<Object> {
        private final Set<Object> toReject = new HashSet<>();

        private final Set<Object> toPrioritize = new HashSet<>();

        private final ArrayDeque<Object> store = new ArrayDeque<>();

        public ReservoirsTest.MockQueue reject(@NonNull
        final Object o) {
            toReject.add(o);
            return this;
        }

        public ReservoirsTest.MockQueue prioritize(@NonNull
        final Object o) {
            toPrioritize.add(o);
            return this;
        }

        @Override
        public boolean isEmpty() {
            return store.isEmpty();
        }

        @Override
        public boolean offer(@NonNull
        final Object o) {
            if (toReject.contains(o)) {
                return false;
            }
            if (toPrioritize.contains(o)) {
                store.offerFirst(o);
            } else {
                store.offerLast(o);
            }
            return true;
        }

        @Nullable
        @Override
        public Object poll() {
            return store.pollFirst();
        }

        @Override
        public boolean add(@NonNull
        final Object o) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public boolean addAll(@NonNull
        final Collection<?> collection) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public void clear() {
            throw new AssertionError("Unexpected");
        }

        @Override
        public boolean contains(@NonNull
        final Object object) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public boolean containsAll(@NonNull
        final Collection<?> ignore) {
            throw new AssertionError("Unexpected");
        }

        @NonNull
        @Override
        public Iterator<Object> iterator() {
            throw new AssertionError("Unexpected");
        }

        @Override
        public boolean remove(Object object) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public boolean removeAll(@NonNull
        final Collection<?> ignore) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public boolean retainAll(@NonNull
        final Collection<?> ignore) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public int size() {
            throw new AssertionError("Unexpected");
        }

        @NonNull
        @Override
        public Object[] toArray() {
            throw new AssertionError("Unexpected");
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull
        final T[] ignore) {
            throw new AssertionError("Unexpected");
        }

        @Override
        public Object remove() {
            throw new AssertionError("Unexpected");
        }

        @Override
        public Object element() {
            throw new AssertionError("Unexpected");
        }

        @Override
        public Object peek() {
            throw new AssertionError("Unexpected");
        }
    }
}

