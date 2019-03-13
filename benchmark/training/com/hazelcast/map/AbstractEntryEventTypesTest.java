/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastTestSupport;
import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Test;
import org.junit.runners.Parameterized;


/**
 * Common superclass to test types of events published with different filtering strategies (default & query cache natural event
 * types)
 */
public abstract class AbstractEntryEventTypesTest extends HazelcastTestSupport {
    @Parameterized.Parameter
    public boolean includeValue;

    @SuppressWarnings("unchecked")
    final Predicate<Integer, AbstractEntryEventTypesTest.Person> predicate = new SqlPredicate("age > 50");

    final AtomicInteger eventCounter = new AtomicInteger();

    HazelcastInstance instance;

    IMap<Integer, AbstractEntryEventTypesTest.Person> map;

    public static class CountEntryAddedListener implements EntryAddedListener<Integer, AbstractEntryEventTypesTest.Person> {
        private final AtomicInteger counter;

        CountEntryAddedListener(AtomicInteger counter) {
            this.counter = counter;
        }

        @Override
        public void entryAdded(EntryEvent<Integer, AbstractEntryEventTypesTest.Person> event) {
            counter.incrementAndGet();
        }
    }

    public static class CountEntryRemovedListener implements EntryRemovedListener<Integer, AbstractEntryEventTypesTest.Person> {
        private final AtomicInteger counter;

        CountEntryRemovedListener(AtomicInteger counter) {
            this.counter = counter;
        }

        @Override
        public void entryRemoved(EntryEvent<Integer, AbstractEntryEventTypesTest.Person> event) {
            counter.incrementAndGet();
        }
    }

    public static class CountEntryUpdatedListener implements EntryUpdatedListener<Integer, AbstractEntryEventTypesTest.Person> {
        private final AtomicInteger counter;

        CountEntryUpdatedListener(AtomicInteger counter) {
            this.counter = counter;
        }

        @Override
        public void entryUpdated(EntryEvent<Integer, AbstractEntryEventTypesTest.Person> event) {
            counter.incrementAndGet();
        }
    }

    public static class Person implements Serializable {
        String name;

        int age;

        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    public void entryAddedEvent_whenNoPredicateConfigured() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryAddedListener(eventCounter), includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 40));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryAddedEvent_whenValueMatchesPredicate() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryAddedListener(eventCounter), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 75));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryAddedEvent_whenValueOutsidePredicate() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryAddedListener(eventCounter), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 35));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 0);
    }

    @Test
    public void entryRemovedEvent_whenNoPredicateConfigured() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryRemovedListener(eventCounter), includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 40));
        map.remove(1);
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryRemovedEvent_whenValueMatchesPredicate() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryRemovedListener(eventCounter), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 55));
        map.remove(1);
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryRemovedEvent_whenValueOutsidePredicate() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryRemovedListener(eventCounter), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 35));
        map.remove(1);
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 0);
    }

    @Test
    public void entryUpdatedEvent_whenNoPredicateConfigured() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryUpdatedListener(eventCounter), includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 30));
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 60));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryUpdatedEvent_whenOldValueOutside_newValueMatchesPredicate() {
        map.addEntryListener(mapListenerFor_entryUpdatedEvent_whenOldValueOutside_newValueMatchesPredicate(), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 30));
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 60));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryUpdatedEvent_whenOldValueOutside_newValueOutsidePredicate() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryUpdatedListener(eventCounter), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 30));
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 20));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 0);
    }

    @Test
    public void entryUpdatedEvent_whenOldValueMatches_newValueMatchesPredicate() {
        map.addEntryListener(new AbstractEntryEventTypesTest.CountEntryUpdatedListener(eventCounter), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 59));
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 60));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, 1);
    }

    @Test
    public void entryUpdatedEvent_whenOldValueMatches_newValueOutsidePredicate() {
        map.addEntryListener(mapListenerFor_entryUpdatedEvent_whenOldValueMatches_newValueOutsidePredicate(), predicate, includeValue);
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 59));
        map.put(1, new AbstractEntryEventTypesTest.Person("a", 30));
        HazelcastTestSupport.assertEqualsEventually(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return eventCounter.get();
            }
        }, expectedCountFor_entryUpdatedEvent_whenOldValueMatches_newValueOutsidePredicate());
    }
}

