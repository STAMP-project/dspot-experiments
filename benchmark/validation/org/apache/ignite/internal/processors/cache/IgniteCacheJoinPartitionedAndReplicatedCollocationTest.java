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
package org.apache.ignite.internal.processors.cache;


import java.io.Serializable;
import org.apache.ignite.Ignite;
import org.apache.ignite.internal.processors.query.h2.sql.AbstractH2CompareQueryTest;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheJoinPartitionedAndReplicatedCollocationTest extends AbstractH2CompareQueryTest {
    /**
     *
     */
    private static final String PERSON_CACHE = "person";

    /**
     *
     */
    private static final String ACCOUNT_CACHE = "acc";

    /**
     *
     */
    private boolean client;

    /**
     *
     */
    private boolean h2DataInserted;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin() throws Exception {
        Ignite client = grid(AbstractH2CompareQueryTest.SRVS);
        client.createCache(personCache());
        checkJoin(0);
        h2DataInserted = true;
        checkJoin(1);
        checkJoin(2);
    }

    /**
     *
     */
    private static class Account implements Serializable {
        /**
         *
         */
        int personId;

        /**
         *
         */
        String name;

        /**
         *
         *
         * @param personId
         * 		Person ID.
         * @param name
         * 		Name.
         */
        public Account(int personId, String name) {
            this.personId = personId;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheJoinPartitionedAndReplicatedCollocationTest.Account.class, this);
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        String name;

        /**
         *
         *
         * @param name
         * 		Name.
         */
        public Person(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheJoinPartitionedAndReplicatedCollocationTest.Person.class, this);
        }
    }
}

