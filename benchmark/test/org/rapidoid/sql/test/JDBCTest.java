/**
 * -
 * #%L
 * rapidoid-sql
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.rapidoid.sql.test;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.jdbc.C3P0Factory;
import org.rapidoid.jdbc.JDBC;
import org.rapidoid.jdbc.JdbcClient;


@Authors("Nikolche Mihajlovski")
@Since("5.x")
public class JDBCTest extends SQLTestCommons {
    @Test
    public void testWithMySQL() {
        JDBC.mysql("non-existing.non-existing--host", 12345, "testdb").usePool(false);
        // only testing if the driver is properly loaded and trying to connect
        try {
            JDBC.execute("create table abc (id int)");
        } catch (Exception e) {
            eq(e.getCause().getClass().getSimpleName(), "CommunicationsException");
        }
        isFalse(JDBC.api().usePool());
    }

    @Test
    public void testWithH2() {
        JDBC.h2("test");
        insertAndCheckData(JDBC.api());
    }

    @Test
    public void testWithH2AndC3P0() {
        JdbcClient jdbc = JDBC.h2("test");
        jdbc.dataSource(C3P0Factory.createDataSourceFor(jdbc));
        insertAndCheckData(JDBC.api());
    }

    @Test
    public void testWithHSQLDB() {
        JDBC.hsql("test");
        insertAndCheckData(JDBC.api());
        isTrue(JDBC.api().usePool());
    }

    @Test
    public void testWithHSQLDBAndC3P0() {
        JdbcClient jdbc = JDBC.hsql("test");
        jdbc.dataSource(C3P0Factory.createDataSourceFor(jdbc));
        insertAndCheckData(JDBC.api());
    }

    @Test
    public void testMultiAPI() {
        JdbcClient client1 = JDBC.api("a").hsql("test");
        JdbcClient client2 = JDBC.api("b").h2("test");
        insertAndCheckData(client1);
        insertAndCheckData(client2);
    }

    @Test
    public void testDefaultAPI() {
        JdbcClient jdbc = JDBC.api();
        JdbcClient jdbc2 = JDBC.api("default");
        isTrue((jdbc == jdbc2));
    }
}

