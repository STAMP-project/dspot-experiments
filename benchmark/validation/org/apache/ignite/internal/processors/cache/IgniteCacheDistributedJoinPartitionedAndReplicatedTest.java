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
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class IgniteCacheDistributedJoinPartitionedAndReplicatedTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String PERSON_CACHE = "person";

    /**
     *
     */
    private static final String ORG_CACHE = "org";

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
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin1() throws Exception {
        join(true, CacheMode.REPLICATED, CacheMode.PARTITIONED, CacheMode.PARTITIONED);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJoin3() throws Exception {
        join(true, CacheMode.PARTITIONED, CacheMode.PARTITIONED, CacheMode.REPLICATED);
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
        int orgId;

        /**
         *
         */
        String name;

        /**
         *
         *
         * @param personId
         * 		Person ID.
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        public Account(int personId, int orgId, String name) {
            this.personId = personId;
            this.orgId = orgId;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinPartitionedAndReplicatedTest.Account.class, this);
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        int orgId;

        /**
         *
         */
        String name;

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        public Person(int orgId, String name) {
            this.orgId = orgId;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinPartitionedAndReplicatedTest.Person.class, this);
        }
    }

    /**
     *
     */
    private static class Organization implements Serializable {
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
        public Organization(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(IgniteCacheDistributedJoinPartitionedAndReplicatedTest.Organization.class, this);
        }
    }
}

