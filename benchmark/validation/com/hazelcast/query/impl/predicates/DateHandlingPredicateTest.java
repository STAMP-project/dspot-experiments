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
package com.hazelcast.query.impl.predicates;


import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicates;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DateHandlingPredicateTest extends HazelcastTestSupport {
    private static final long JUNE_2016_MILLIS = 1467110170001L;

    private static final DateHandlingPredicateTest.Customer CUSTOMER_0 = new DateHandlingPredicateTest.Customer(0);

    private static final DateHandlingPredicateTest.Customer CUSTOMER_1 = new DateHandlingPredicateTest.Customer(1);

    private IMap<Integer, DateHandlingPredicateTest.Customer> map;

    @Test
    public void dateValueInPredicate() throws Exception {
        // date vs. date
        MatcherAssert.assertThat(map.values(Predicates.equal("date", new Date(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
        // date vs. sqlDate
        MatcherAssert.assertThat(map.values(Predicates.equal("date", new java.sql.Date(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
        // date vs. sqlTimestamp
        MatcherAssert.assertThat(map.values(Predicates.equal("date", new Timestamp(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
    }

    @Test
    public void sqlDateValueInPredicate() throws Exception {
        // sqlDate vs. date
        MatcherAssert.assertThat(map.values(Predicates.equal("sqlDate", new Date(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
        // sqlDate vs. sqlDate
        MatcherAssert.assertThat(map.values(Predicates.equal("sqlDate", new java.sql.Date(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
        // sqlDate vs. sqlTimestamp
        MatcherAssert.assertThat(map.values(Predicates.equal("sqlDate", new Timestamp(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
    }

    @Test
    public void sqlTimestampValueInPredicate() throws Exception {
        // sqlTimestamp vs. date
        MatcherAssert.assertThat(map.values(Predicates.equal("sqlTimestamp", new Date(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
        // sqlTimestamp vs. sqlDate
        MatcherAssert.assertThat(map.values(Predicates.equal("sqlTimestamp", new java.sql.Date(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
        // sqlTimestamp vs. sqlTimestamp
        MatcherAssert.assertThat(map.values(Predicates.equal("sqlTimestamp", new Timestamp(DateHandlingPredicateTest.JUNE_2016_MILLIS))), CoreMatchers.allOf(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_0), CoreMatchers.not(Matchers.hasItem(DateHandlingPredicateTest.CUSTOMER_1))));
    }

    private static class Customer implements Serializable {
        private final int id;

        private final Date date;

        private final java.sql.Date sqlDate;

        private final Timestamp sqlTimestamp;

        Customer(int id) {
            this.id = id;
            this.date = new Date(((DateHandlingPredicateTest.JUNE_2016_MILLIS) + id));
            this.sqlDate = new java.sql.Date(((DateHandlingPredicateTest.JUNE_2016_MILLIS) + id));
            this.sqlTimestamp = new Timestamp(((DateHandlingPredicateTest.JUNE_2016_MILLIS) + id));
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o) {
                return true;
            }
            if ((o == null) || ((getClass()) != (o.getClass()))) {
                return false;
            }
            DateHandlingPredicateTest.Customer customer = ((DateHandlingPredicateTest.Customer) (o));
            return (id) == (customer.id);
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}

