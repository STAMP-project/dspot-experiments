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
package com.hazelcast.map.impl.operation;


import Comparison.GREATER;
import MapService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.MapInterceptor;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.query.IndexAwarePredicate;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * Verify that maps created on a member joining the cluster, do actually include index & interceptors
 * as expected through execution of PostJoinMapOperation on the joining member.
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PostJoinMapOperationTest extends HazelcastTestSupport {
    private static class Person implements Serializable {
        private final int age;

        private final String name;

        public Person(String name, int age) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if (!(o instanceof PostJoinMapOperationTest.Person)) {
                return false;
            }
            PostJoinMapOperationTest.Person person = ((PostJoinMapOperationTest.Person) (o));
            if ((age) != (person.age)) {
                return false;
            }
            return (name) != null ? name.equals(person.name) : (person.name) == null;
        }

        @Override
        public int hashCode() {
            int result = age;
            result = (31 * result) + ((name) != null ? name.hashCode() : 0);
            return result;
        }
    }

    private static final PostJoinMapOperationTest.Person RETURNED_FROM_INTERCEPTOR = new PostJoinMapOperationTest.Person("THE_PERSON", 100);

    public static class FixedReturnInterceptor implements MapInterceptor {
        @Override
        public Object interceptGet(Object value) {
            return PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR;
        }

        @Override
        public void afterGet(Object value) {
        }

        @Override
        public Object interceptPut(Object oldValue, Object newValue) {
            // allow put operations to proceed
            return null;
        }

        @Override
        public void afterPut(Object value) {
        }

        @Override
        public Object interceptRemove(Object removedValue) {
            return PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR;
        }

        @Override
        public void afterRemove(Object value) {
        }
    }

    // if it locates an index on Person.age, increments isIndexedInvoked
    public class AgePredicate implements IndexAwarePredicate {
        private final AtomicInteger isIndexedInvocationCounter;

        public AgePredicate(AtomicInteger atomicInteger) {
            this.isIndexedInvocationCounter = atomicInteger;
        }

        @Override
        public Set<QueryableEntry> filter(QueryContext queryContext) {
            Index ix = queryContext.getIndex("age");
            if (ix != null) {
                return ix.getRecords(GREATER, 50);
            } else {
                return null;
            }
        }

        @Override
        public boolean isIndexed(QueryContext queryContext) {
            Index ix = queryContext.getIndex("age");
            if (ix != null) {
                isIndexedInvocationCounter.incrementAndGet();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean apply(Map.Entry mapEntry) {
            if ((mapEntry.getValue()) instanceof PostJoinMapOperationTest.Person) {
                return (((PostJoinMapOperationTest.Person) (mapEntry.getValue())).getAge()) > 50;
            } else {
                return false;
            }
        }
    }

    @Test
    public void testPostJoinMapOperation_mapWithInterceptor() {
        TestHazelcastInstanceFactory hzFactory = createHazelcastInstanceFactory(2);
        // Given: A map with an interceptor on single-node Hazelcast
        HazelcastInstance hz1 = hzFactory.newHazelcastInstance();
        IMap<String, PostJoinMapOperationTest.Person> map = hz1.getMap("map");
        map.put("foo", new PostJoinMapOperationTest.Person("foo", 32));
        map.put("bar", new PostJoinMapOperationTest.Person("bar", 35));
        map.addInterceptor(new PostJoinMapOperationTest.FixedReturnInterceptor());
        Assert.assertEquals(PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR, map.get("foo"));
        // when: new member joins cluster
        HazelcastInstance hz2 = hzFactory.newHazelcastInstance();
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2);
        // then: values from map reference obtained from node 2 are returned by interceptor
        IMap<String, PostJoinMapOperationTest.Person> mapOnNode2 = hz2.getMap("map");
        Assert.assertEquals(PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR, mapOnNode2.get("whatever"));
        // put a value on node 2, then get it and verify it is the one returned by the interceptor
        String keyOwnedByNode2 = HazelcastTestSupport.generateKeyOwnedBy(hz2);
        map.put(keyOwnedByNode2, new PostJoinMapOperationTest.Person("not to be returned", 39));
        Assert.assertEquals(PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR, map.get(keyOwnedByNode2));
        hzFactory.terminateAll();
    }

    // This test is meant to verify that a query will be executed *with an index* on the joining node
    // See also QueryIndexMigrationTest, which tests that results are as expected.
    @Test
    public void testPostJoinMapOperation_mapWithIndex() throws InterruptedException {
        TestHazelcastInstanceFactory hzFactory = createHazelcastInstanceFactory(2);
        // given: a map with index on a single-node HazelcastInstance
        HazelcastInstance hz1 = hzFactory.newHazelcastInstance();
        IMap<String, PostJoinMapOperationTest.Person> map = hz1.getMap("map");
        map.put("foo", new PostJoinMapOperationTest.Person("foo", 32));
        map.put("bar", new PostJoinMapOperationTest.Person("bar", 70));
        map.addIndex("age", true);
        // when: new node joins and original node is terminated
        HazelcastInstance hz2 = hzFactory.newHazelcastInstance();
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2);
        hzFactory.terminate(hz1);
        HazelcastTestSupport.waitAllForSafeState(hz2);
        // then: once all migrations are committed, the query is executed *with* the index and
        // returns the expected results.
        final IMap<String, PostJoinMapOperationTest.Person> mapOnNode2 = hz2.getMap("map");
        final AtomicInteger invocationCounter = new AtomicInteger(0);
        // eventually index should be created after join
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Collection<PostJoinMapOperationTest.Person> personsWithAgePredicate = mapOnNode2.values(new PostJoinMapOperationTest.AgePredicate(invocationCounter));
                Assert.assertEquals("isIndexed should have located an index", 1, invocationCounter.get());
                Assert.assertEquals("index should return 1 match", 1, personsWithAgePredicate.size());
            }
        });
    }

    @Test
    public void testPostJoinMapOperation_whenMapHasNoData() {
        TestHazelcastInstanceFactory hzFactory = createHazelcastInstanceFactory(2);
        // given: a single node HazelcastInstance with a map configured with index and interceptor
        HazelcastInstance hz1 = hzFactory.newHazelcastInstance();
        IMap<String, PostJoinMapOperationTest.Person> map = hz1.getMap("map");
        map.addIndex("age", true);
        map.addInterceptor(new PostJoinMapOperationTest.FixedReturnInterceptor());
        Assert.assertEquals(PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR, map.get("foo"));
        // when: another member joins the cluster
        HazelcastInstance hz2 = hzFactory.newHazelcastInstance();
        HazelcastTestSupport.waitAllForSafeState(hz1, hz2);
        // then: index & interceptor exist on internal MapContainer on node that joined the cluster
        MapService mapService = HazelcastTestSupport.getNodeEngineImpl(hz2).getService(SERVICE_NAME);
        MapContainer mapContainerOnNode2 = mapService.getMapServiceContext().getMapContainer("map");
        Assert.assertEquals(1, mapContainerOnNode2.getIndexes().getIndexes().length);
        Assert.assertEquals(1, mapContainerOnNode2.getInterceptorRegistry().getInterceptors().size());
        Assert.assertEquals(PostJoinMapOperationTest.Person.class, mapContainerOnNode2.getInterceptorRegistry().getInterceptors().get(0).interceptGet("anything").getClass());
        Assert.assertEquals(PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR.getAge(), ((PostJoinMapOperationTest.Person) (mapContainerOnNode2.getInterceptorRegistry().getInterceptors().get(0).interceptGet("anything"))).getAge());
        // also verify via user API
        IMap<String, PostJoinMapOperationTest.Person> mapOnNode2 = hz2.getMap("map");
        Assert.assertEquals(PostJoinMapOperationTest.RETURNED_FROM_INTERCEPTOR, mapOnNode2.get("whatever"));
        hzFactory.terminateAll();
    }
}

