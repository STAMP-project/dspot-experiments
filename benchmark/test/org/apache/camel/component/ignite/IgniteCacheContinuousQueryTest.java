/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.ignite;


import EventType.CREATED;
import IgniteConstants.IGNITE_CACHE_EVENT_TYPE;
import IgniteConstants.IGNITE_CACHE_KEY;
import IgniteConstants.IGNITE_CACHE_NAME;
import com.google.common.collect.Iterators;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import org.apache.camel.Exchange;
import org.apache.ignite.IgniteCache;
import org.junit.Test;


public class IgniteCacheContinuousQueryTest extends AbstractIgniteTest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Test
    public void testContinuousQueryDoNotFireExistingEntries() throws Exception {
        context.getRouteController().startRoute("continuousQuery");
        getMockEndpoint("mock:test1").expectedMessageCount(100);
        Map<Integer, IgniteCacheContinuousQueryTest.Person> persons = createPersons(1, 100);
        IgniteCache<Integer, IgniteCacheContinuousQueryTest.Person> cache = ignite().getOrCreateCache("testcontinuous1");
        cache.putAll(persons);
        assertMockEndpointsSatisfied();
        for (Exchange exchange : getMockEndpoint("mock:test1").getExchanges()) {
            assert_().that(exchange.getIn().getHeader(IGNITE_CACHE_NAME)).isEqualTo("testcontinuous1");
            assert_().that(exchange.getIn().getHeader(IGNITE_CACHE_EVENT_TYPE)).isEqualTo(CREATED);
            assert_().that(exchange.getIn().getHeader(IGNITE_CACHE_KEY)).isIn(persons.keySet());
            assert_().that(exchange.getIn().getBody()).isIn(persons.values());
        }
    }

    @Test
    public void testContinuousQueryFireExistingEntriesWithQuery() throws Exception {
        getMockEndpoint("mock:test2").expectedMessageCount(50);
        Map<Integer, IgniteCacheContinuousQueryTest.Person> persons = createPersons(1, 100);
        IgniteCache<Integer, IgniteCacheContinuousQueryTest.Person> cache = ignite().getOrCreateCache("testcontinuous1");
        cache.putAll(persons);
        context.getRouteController().startRoute("continuousQuery.fireExistingEntries");
        assertMockEndpointsSatisfied();
        resetMocks();
        getMockEndpoint("mock:test2").expectedMessageCount(100);
        persons = createPersons(101, 100);
        cache.putAll(persons);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testContinuousQueryFireExistingEntriesWithQueryAndRemoteFilter() throws Exception {
        getMockEndpoint("mock:test3").expectedMessageCount(50);
        Map<Integer, IgniteCacheContinuousQueryTest.Person> persons = createPersons(1, 100);
        IgniteCache<Integer, IgniteCacheContinuousQueryTest.Person> cache = ignite().getOrCreateCache("testcontinuous1");
        cache.putAll(persons);
        context.getRouteController().startRoute("remoteFilter");
        assertMockEndpointsSatisfied();
        resetMocks();
        getMockEndpoint("mock:test3").expectedMessageCount(50);
        persons = createPersons(101, 100);
        cache.putAll(persons);
        assertMockEndpointsSatisfied();
    }

    @Test
    public void testContinuousQueryGroupedUpdates() throws Exception {
        // One hundred Iterables of 1 item each.
        getMockEndpoint("mock:test4").expectedMessageCount(100);
        context.getRouteController().startRoute("groupedUpdate");
        Map<Integer, IgniteCacheContinuousQueryTest.Person> persons = createPersons(1, 100);
        IgniteCache<Integer, IgniteCacheContinuousQueryTest.Person> cache = ignite().getOrCreateCache("testcontinuous1");
        cache.putAll(persons);
        assertMockEndpointsSatisfied();
        for (Exchange exchange : getMockEndpoint("mock:test4").getExchanges()) {
            assert_().that(exchange.getIn().getHeader(IGNITE_CACHE_NAME)).isEqualTo("testcontinuous1");
            assert_().that(exchange.getIn().getBody()).isInstanceOf(Iterable.class);
            assert_().that(Iterators.size(exchange.getIn().getBody(Iterable.class).iterator())).isEqualTo(1);
        }
    }

    public static class Person implements Serializable {
        private static final long serialVersionUID = -6582521698437964648L;

        private Integer id;

        private String name;

        private String surname;

        public static IgniteCacheContinuousQueryTest.Person create(Integer id, String name, String surname) {
            IgniteCacheContinuousQueryTest.Person p = new IgniteCacheContinuousQueryTest.Person();
            p.setId(id);
            p.setName(name);
            p.setSurname(surname);
            return p;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = (prime * result) + ((id) == null ? 0 : id.hashCode());
            result = (prime * result) + ((name) == null ? 0 : name.hashCode());
            result = (prime * result) + ((surname) == null ? 0 : surname.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ((obj == null) || (!(obj instanceof IgniteCacheContinuousQueryTest.Person))) {
                return false;
            }
            if ((this) == obj) {
                return true;
            }
            IgniteCacheContinuousQueryTest.Person other = ((IgniteCacheContinuousQueryTest.Person) (obj));
            return ((Objects.equals(this.id, other.id)) && (Objects.equals(this.name, other.name))) && (Objects.equals(this.surname, other.surname));
        }
    }
}

