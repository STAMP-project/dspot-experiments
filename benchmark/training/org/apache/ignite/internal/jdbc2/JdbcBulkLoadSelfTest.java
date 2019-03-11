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
package org.apache.ignite.internal.jdbc2;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteJdbcDriver;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * COPY command test for the regular JDBC driver.
 */
public class JdbcBulkLoadSelfTest extends GridCommonAbstractTest {
    /**
     * JDBC URL.
     */
    private static final String BASE_URL = (IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=default@modules/clients/src/test/config/jdbc-config.xml";

    /**
     * Connection.
     */
    protected Connection conn;

    /**
     * The logger.
     */
    protected transient IgniteLogger log;

    /**
     * This is more a placeholder for implementation of IGNITE-7553.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testBulkLoadThrows() throws Exception {
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                conn = createConnection();
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(("copy from 'dummy.csv' into Person" + " (_key, id, firstName, lastName) format csv"));
                    return null;
                }
            }
        }, SQLException.class, "COPY command is currently supported only in thin JDBC driver.");
    }

    /**
     * A test class for creating a query entity.
     */
    private static class Person implements Serializable {
        /**
         * ID.
         */
        @QuerySqlField
        private final int id;

        /**
         * First name.
         */
        @QuerySqlField(index = false)
        private final String firstName;

        /**
         * Last name.
         */
        @QuerySqlField(index = false)
        private final String lastName;

        /**
         * Age.
         */
        @QuerySqlField
        private final int age;

        /**
         *
         *
         * @param id
         * 		ID.
         * @param firstName
         * 		First name
         * @param lastName
         * 		Last name
         * @param age
         * 		Age.
         */
        private Person(int id, String firstName, String lastName, int age) {
            assert !(F.isEmpty(firstName));
            assert !(F.isEmpty(lastName));
            assert age > 0;
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.age = age;
        }
    }
}

