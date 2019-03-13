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
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapListenerTest extends HazelcastTestSupport {
    private static final int AGE_THRESHOLD = 50;

    @Test
    public void testListener_eventCountsCorrect() throws Exception {
        // GIVEN
        HazelcastInstance hz = createHazelcastInstance();
        // GIVEN MAP & LISTENER
        IMap<String, MapListenerTest.Person> map = hz.getMap("map");
        MapListenerTest.AllListener listener = new MapListenerTest.AllListener();
        map.addEntryListener(listener, new SqlPredicate(("age > " + (MapListenerTest.AGE_THRESHOLD))), true);
        // GIVEN MAP EVENTS
        generateMapEvents(map, listener);
        // THEN
        HazelcastTestSupport.assertAtomicEventually("wrong entries count", listener.entries.get(), listener.entriesObserved, 60);
        HazelcastTestSupport.assertAtomicEventually("wrong exits count", listener.exits.get(), listener.exitsObserved, 60);
    }

    @Test
    public void testListener_hazelcastAwareHandled() throws Exception {
        // GIVEN
        HazelcastInstance[] instances = createHazelcastInstanceFactory(2).newInstances();
        // GIVEN MAP & LISTENER
        IMap<String, MapListenerTest.Person> map = instances[0].getMap("map");
        MapListenerTest.MyEntryListener listener = new MapListenerTest.MyEntryListener();
        map.addEntryListener(listener, new SqlPredicate(("age > " + (MapListenerTest.AGE_THRESHOLD))), true);
        // GIVEN MAP EVENTS
        generateMapEvents(map, null);
        // THEN
        Assert.assertEquals("HazelcastInstance not injected properly to listener", 0, listener.nulls.get());
    }

    static class MyEntryListener implements HazelcastInstanceAware , EntryAddedListener<String, MapListenerTest.Person> , EntryRemovedListener<String, MapListenerTest.Person> , EntryUpdatedListener<String, MapListenerTest.Person> {
        private HazelcastInstance hazelcastInstance;

        public AtomicInteger nulls = new AtomicInteger(0);

        @Override
        public void entryAdded(EntryEvent<String, MapListenerTest.Person> event) {
            trackNullInstance();
        }

        @Override
        public void entryUpdated(EntryEvent<String, MapListenerTest.Person> event) {
            trackNullInstance();
        }

        @Override
        public void entryRemoved(EntryEvent<String, MapListenerTest.Person> event) {
            trackNullInstance();
        }

        private void trackNullInstance() {
            if ((hazelcastInstance) == null) {
                nulls.incrementAndGet();
            }
        }

        @Override
        public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
            this.hazelcastInstance = hazelcastInstance;
        }
    }

    class AllListener implements EntryAddedListener<String, MapListenerTest.Person> , EntryRemovedListener<String, MapListenerTest.Person> , EntryUpdatedListener<String, MapListenerTest.Person> {
        final AtomicInteger entries;

        final AtomicInteger exits;

        final AtomicInteger entriesObserved;

        final AtomicInteger exitsObserved;

        public AllListener() {
            entries = new AtomicInteger();
            exits = new AtomicInteger();
            entriesObserved = new AtomicInteger();
            exitsObserved = new AtomicInteger();
        }

        @Override
        public void entryAdded(EntryEvent<String, MapListenerTest.Person> event) {
            entriesObserved.incrementAndGet();
        }

        @Override
        public void entryRemoved(EntryEvent<String, MapListenerTest.Person> event) {
            exitsObserved.incrementAndGet();
        }

        @Override
        public void entryUpdated(EntryEvent<String, MapListenerTest.Person> event) {
            if (((event.getOldValue().getAge()) > (MapListenerTest.AGE_THRESHOLD)) && ((event.getValue().getAge()) <= (MapListenerTest.AGE_THRESHOLD))) {
                exitsObserved.incrementAndGet();
            } else
                if (((event.getOldValue().getAge()) <= (MapListenerTest.AGE_THRESHOLD)) && ((event.getValue().getAge()) > (MapListenerTest.AGE_THRESHOLD))) {
                    entriesObserved.incrementAndGet();
                }

        }
    }

    static class Person implements Serializable {
        private int age;

        private String name;

        public Person(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public Person(MapListenerTest.Person p) {
            this.name = p.name;
            this.age = p.age;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return (("Person{" + "age=") + (age)) + '}';
        }
    }

    static class MapRandomizer implements Runnable {
        private static final int ACTION_ADD = 0;

        private static final int ACTION_UPDATE_AGE = 1;

        private static final int ACTION_REMOVE = 2;

        final Random random = new Random();

        final IMap<String, MapListenerTest.Person> map;

        final MapListenerTest.AllListener listener;

        volatile boolean running;

        MapRandomizer(IMap<String, MapListenerTest.Person> map, MapListenerTest.AllListener listener) {
            this.map = map;
            this.running = true;
            this.listener = listener;
        }

        private void act() {
            int action = ((random.nextInt(10)) < 6) ? MapListenerTest.MapRandomizer.ACTION_ADD : (random.nextInt(10)) < 8 ? MapListenerTest.MapRandomizer.ACTION_UPDATE_AGE : MapListenerTest.MapRandomizer.ACTION_REMOVE;
            switch (action) {
                case MapListenerTest.MapRandomizer.ACTION_ADD :
                    addPerson();
                    break;
                case MapListenerTest.MapRandomizer.ACTION_UPDATE_AGE :
                    updatePersonAge();
                    break;
                case MapListenerTest.MapRandomizer.ACTION_REMOVE :
                    removePerson();
                    break;
                default :
                    throw new UnsupportedOperationException(("Unsupported action: " + action));
            }
        }

        private void addPerson() {
            MapListenerTest.Person p = new MapListenerTest.Person(random.nextInt(100), UUID.randomUUID().toString());
            map.put(p.getName(), p);
            if ((listener) != null) {
                if ((p.getAge()) > (MapListenerTest.AGE_THRESHOLD)) {
                    listener.entries.incrementAndGet();
                }
            }
        }

        private void updatePersonAge() {
            if ((map.size()) > 0) {
                Collection<String> allKeys = map.keySet();
                String key = allKeys.toArray(new String[0])[random.nextInt(map.size())];
                MapListenerTest.Person p = map.get(key);
                int oldAge = p.getAge();
                p.setAge(((p.getAge()) + ((random.nextInt(10)) * ((int) (Math.pow((-1), random.nextInt(2)))))));
                if ((listener) != null) {
                    if ((oldAge > (MapListenerTest.AGE_THRESHOLD)) && ((p.getAge()) <= (MapListenerTest.AGE_THRESHOLD))) {
                        listener.exits.incrementAndGet();
                    } else
                        if ((oldAge <= (MapListenerTest.AGE_THRESHOLD)) && ((p.getAge()) > (MapListenerTest.AGE_THRESHOLD))) {
                            listener.entries.incrementAndGet();
                        }

                }
                map.put(key, p);
            }
        }

        private void removePerson() {
            if ((map.size()) > 0) {
                Collection<String> allKeys = map.keySet();
                String key = allKeys.toArray(new String[0])[random.nextInt(map.size())];
                if ((listener) != null) {
                    if ((map.get(key).getAge()) > (MapListenerTest.AGE_THRESHOLD)) {
                        listener.exits.incrementAndGet();
                    }
                }
                map.remove(key);
            }
        }

        @Override
        public void run() {
            while (running) {
                act();
            } 
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }
}

