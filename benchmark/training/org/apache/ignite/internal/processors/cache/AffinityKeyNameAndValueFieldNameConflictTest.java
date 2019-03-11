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
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import javax.cache.CacheException;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * IGNITE-7793 SQL does not work if value has sql field which name equals to affinity keyProducer name
 */
public class AffinityKeyNameAndValueFieldNameConflictTest extends GridCommonAbstractTest {
    /**
     *
     */
    private static final String PERSON_CACHE = "person";

    /**
     *
     */
    private Class<?> keyCls;

    /**
     *
     */
    private BiFunction<Integer, String, ?> keyProducer;

    /**
     *
     */
    private boolean qryEntityCfg;

    /**
     *
     */
    private boolean keyFieldSpecified;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryEntityConfig() throws Exception {
        qryEntityCfg = true;
        keyCls = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1.class;
        keyProducer = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1::new;
        checkQuery();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testQueryEntityConfigKeySpecified() throws Exception {
        qryEntityCfg = true;
        keyFieldSpecified = true;
        keyCls = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1.class;
        keyProducer = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1::new;
        checkQuery();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAnnotationConfig() throws Exception {
        keyCls = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1.class;
        keyProducer = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1::new;
        checkQuery();
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testAnnotationConfigCollision() throws Exception {
        keyCls = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey2.class;
        keyProducer = AffinityKeyNameAndValueFieldNameConflictTest.PersonKey2::new;
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                checkQuery();
                return null;
            }
        }, CacheException.class, "Property with name 'name' already exists.");
    }

    /**
     *
     */
    public static class PersonKey1 {
        /**
         *
         */
        @QuerySqlField
        private int id;

        /**
         *
         */
        @AffinityKeyMapped
        private String name;

        /**
         *
         *
         * @param id
         * 		Key.
         * @param name
         * 		Affinity keyProducer.
         */
        public PersonKey1(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1 other = ((AffinityKeyNameAndValueFieldNameConflictTest.PersonKey1) (o));
            return (id) == (other.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     *
     */
    public static class PersonKey2 {
        /**
         *
         */
        @QuerySqlField
        private int id;

        /**
         *
         */
        @QuerySqlField
        @AffinityKeyMapped
        private String name;

        /**
         *
         *
         * @param id
         * 		Key.
         * @param name
         * 		Affinity keyProducer.
         */
        public PersonKey2(int id, String name) {
            this.id = id;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            AffinityKeyNameAndValueFieldNameConflictTest.PersonKey2 other = ((AffinityKeyNameAndValueFieldNameConflictTest.PersonKey2) (o));
            return (id) == (other.id);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public int hashCode() {
            return id;
        }
    }

    /**
     *
     */
    private static class Person implements Serializable {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         *
         * @param name
         * 		name.
         */
        public Person(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public String toString() {
            return S.toString(AffinityKeyNameAndValueFieldNameConflictTest.Person.class, this);
        }
    }
}

