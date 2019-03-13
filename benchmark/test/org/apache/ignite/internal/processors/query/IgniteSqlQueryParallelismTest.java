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
package org.apache.ignite.internal.processors.query;


import java.io.Serializable;
import java.util.concurrent.Callable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * A test against setting different values of query parallelism in cache configurations of the same cache.
 */
@SuppressWarnings("unchecked")
public class IgniteSqlQueryParallelismTest extends AbstractIndexingCommonTest {
    /**
     *
     */
    private boolean isClient = false;

    /**
     *
     */
    private int qryParallelism = 4;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIndexSegmentationOnClient() throws Exception {
        IgniteCache<Object, Object> c1 = ignite(0).cache("org");
        IgniteCache<Object, Object> c2 = ignite(0).cache("pers");
        c1.put(1, new IgniteSqlQueryParallelismTest.Organization("o1"));
        c1.put(2, new IgniteSqlQueryParallelismTest.Organization("o2"));
        c2.put(1, new IgniteSqlQueryParallelismTest.Person2(1, "o1"));
        c2.put(2, new IgniteSqlQueryParallelismTest.Person2(2, "o2"));
        c2.put(3, new IgniteSqlQueryParallelismTest.Person2(3, "o3"));
        String select0 = "select o.name n1, p.name n2 from \"pers\".Person2 p join \"org\".Organization o on p.name = o.name";
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                isClient = true;
                qryParallelism = 2;
                Ignite client = startGrid(4);
                return null;
            }
        }, IgniteCheckedException.class, "Query parallelism mismatch");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testIndexSegmentation() throws Exception {
        IgniteCache<Object, Object> c1 = ignite(0).cache("org");
        IgniteCache<Object, Object> c2 = ignite(0).cache("pers");
        c1.put(1, new IgniteSqlQueryParallelismTest.Organization("o1"));
        c1.put(2, new IgniteSqlQueryParallelismTest.Organization("o2"));
        c2.put(1, new IgniteSqlQueryParallelismTest.Person2(1, "o1"));
        c2.put(2, new IgniteSqlQueryParallelismTest.Person2(2, "o2"));
        c2.put(3, new IgniteSqlQueryParallelismTest.Person2(3, "o3"));
        String select0 = "select o.name n1, p.name n2 from \"pers\".Person2 p join \"org\".Organization o on p.name = o.name";
        GridTestUtils.assertThrows(log, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                qryParallelism = 2;
                Ignite client = startGrid(4);
                return null;
            }
        }, IgniteCheckedException.class, "Query parallelism mismatch");
    }

    /**
     *
     */
    private static class Person2 implements Serializable {
        /**
         *
         */
        @QuerySqlField(index = true)
        int orgId;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        public Person2() {
            // No-op.
        }

        /**
         *
         *
         * @param orgId
         * 		Organization ID.
         * @param name
         * 		Name.
         */
        public Person2(int orgId, String name) {
            this.orgId = orgId;
            this.name = name;
        }
    }

    /**
     *
     */
    private static class Organization implements Serializable {
        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        public Organization() {
            // No-op.
        }

        /**
         *
         *
         * @param name
         * 		Organization name.
         */
        public Organization(String name) {
            this.name = name;
        }
    }
}

