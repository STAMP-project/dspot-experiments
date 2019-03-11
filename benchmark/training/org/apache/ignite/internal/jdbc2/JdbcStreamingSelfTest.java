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


import java.sql.Connection;
import java.sql.PreparedStatement;
import org.apache.ignite.IgniteJdbcDriver;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.jdbc.thin.JdbcThinAbstractSelfTest;
import org.junit.Test;


/**
 * Data streaming test.
 */
public class JdbcStreamingSelfTest extends JdbcThinAbstractSelfTest {
    /**
     * JDBC URL.
     */
    private static final String BASE_URL = (IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=default@modules/clients/src/test/config/jdbc-config.xml";

    /**
     * Streaming URL.
     */
    private static final String STREAMING_URL = (IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=person@modules/clients/src/test/config/jdbc-config.xml";

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testStreamedInsert() throws Exception {
        for (int i = 10; i <= 100; i += 10)
            put(i, nameForId((i * 100)));

        try (Connection conn = createStreamedConnection(false)) {
            try (PreparedStatement stmt = conn.prepareStatement(("insert into PUBLIC.Person(\"id\", \"name\") " + "values (?, ?)"))) {
                for (int i = 1; i <= 100; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, nameForId(i));
                    stmt.executeUpdate();
                }
            }
        }
        U.sleep(500);
        // Now let's check it's all there.
        for (int i = 1; i <= 100; i++) {
            if ((i % 10) != 0)
                assertEquals(nameForId(i), nameForIdInCache(i));
            else// All that divides by 10 evenly should point to numbers 100 times greater - see above

                assertEquals(nameForId((i * 100)), nameForIdInCache(i));

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testStreamedInsertWithoutColumnsList() throws Exception {
        for (int i = 10; i <= 100; i += 10)
            put(i, nameForId((i * 100)));

        try (Connection conn = createStreamedConnection(false)) {
            try (PreparedStatement stmt = conn.prepareStatement(("insert into PUBLIC.Person(\"id\", \"name\") " + "values (?, ?)"))) {
                for (int i = 1; i <= 100; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, nameForId(i));
                    stmt.executeUpdate();
                }
            }
        }
        U.sleep(500);
        // Now let's check it's all there.
        for (int i = 1; i <= 100; i++) {
            if ((i % 10) != 0)
                assertEquals(nameForId(i), nameForIdInCache(i));
            else// All that divides by 10 evenly should point to numbers 100 times greater - see above

                assertEquals(nameForId((i * 100)), nameForIdInCache(i));

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testStreamedInsertWithOverwritesAllowed() throws Exception {
        for (int i = 10; i <= 100; i += 10)
            put(i, nameForId((i * 100)));

        try (Connection conn = createStreamedConnection(true)) {
            try (PreparedStatement stmt = conn.prepareStatement(("insert into PUBLIC.Person(\"id\", \"name\") " + "values (?, ?)"))) {
                for (int i = 1; i <= 100; i++) {
                    stmt.setInt(1, i);
                    stmt.setString(2, nameForId(i));
                    stmt.executeUpdate();
                }
            }
        }
        U.sleep(500);
        // Now let's check it's all there.
        // i should point to i at all times as we've turned overwrites on above.
        for (int i = 1; i <= 100; i++)
            assertEquals(nameForId(i), nameForIdInCache(i));

    }

    /**
     *
     */
    @Test
    public void testOnlyInsertsAllowed() {
        assertStatementForbidden("CREATE TABLE PUBLIC.X (x int primary key, y int)");
        assertStatementForbidden("CREATE INDEX idx_1 ON Person(name)");
        assertStatementForbidden("SELECT * from Person");
        assertStatementForbidden(("insert into PUBLIC.Person(\"id\", \"name\") " + "(select \"id\" + 1, CONCAT(\"name\", \'1\') from Person)"));
        assertStatementForbidden("DELETE from Person");
        assertStatementForbidden("UPDATE Person SET \"name\" = \'name0\'");
        assertStatementForbidden("alter table Person add column y int");
    }
}

