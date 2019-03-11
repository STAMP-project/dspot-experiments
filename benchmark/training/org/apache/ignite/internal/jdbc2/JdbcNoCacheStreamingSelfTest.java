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
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Data streaming test for thick driver and no explicit caches.
 */
public class JdbcNoCacheStreamingSelfTest extends GridCommonAbstractTest {
    /**
     * JDBC URL.
     */
    private static final String BASE_URL = (IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=default@modules/clients/src/test/config/jdbc-config.xml";

    /**
     * Connection.
     */
    protected Connection conn;

    /**
     *
     */
    protected transient IgniteLogger log;

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testStreamedInsert() throws Exception {
        for (int i = 10; i <= 100; i += 10)
            ignite(0).cache(DEFAULT_CACHE_NAME).put(i, (i * 100));

        try (Connection conn = createConnection(false)) {
            try (PreparedStatement stmt = conn.prepareStatement("insert into Integer(_key, _val) values (?, ?)")) {
                for (int i = 1; i <= 100; i++) {
                    stmt.setInt(1, i);
                    stmt.setInt(2, i);
                    stmt.executeUpdate();
                }
            }
        }
        U.sleep(500);
        // Now let's check it's all there.
        for (int i = 1; i <= 100; i++) {
            if ((i % 10) != 0)
                assertEquals(i, grid(0).cache(DEFAULT_CACHE_NAME).get(i));
            else// All that divides by 10 evenly should point to numbers 100 times greater - see above

                assertEquals((i * 100), grid(0).cache(DEFAULT_CACHE_NAME).get(i));

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
            ignite(0).cache(DEFAULT_CACHE_NAME).put(i, (i * 100));

        try (Connection conn = createConnection(true)) {
            try (PreparedStatement stmt = conn.prepareStatement("insert into Integer(_key, _val) values (?, ?)")) {
                for (int i = 1; i <= 100; i++) {
                    stmt.setInt(1, i);
                    stmt.setInt(2, i);
                    stmt.executeUpdate();
                }
            }
        }
        U.sleep(500);
        // Now let's check it's all there.
        // i should point to i at all times as we've turned overwrites on above.
        for (int i = 1; i <= 100; i++)
            assertEquals(i, grid(0).cache(DEFAULT_CACHE_NAME).get(i));

    }
}

