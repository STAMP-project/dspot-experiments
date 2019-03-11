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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;
import org.apache.ignite.IgniteJdbcDriver;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Test JDBC with several local caches.
 */
public class JdbcLocalCachesSelfTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "cache";

    /**
     * JDBC URL.
     */
    private static final String BASE_URL = (((IgniteJdbcDriver.CFG_URL_PREFIX) + "cache=") + (JdbcLocalCachesSelfTest.CACHE_NAME)) + "@modules/clients/src/test/config/jdbc-config.xml";

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCache1() throws Exception {
        Properties cfg = new Properties();
        cfg.setProperty(IgniteJdbcDriver.PROP_NODE_ID, grid(0).localNode().id().toString());
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(JdbcLocalCachesSelfTest.BASE_URL, cfg);
            ResultSet rs = conn.createStatement().executeQuery("select _val from Integer order by _val");
            int cnt = 0;
            while (rs.next())
                assertEquals((++cnt), rs.getInt(1));

            assertEquals(2, cnt);
        } finally {
            if (conn != null)
                conn.close();

        }
    }

    /**
     * Verifies that <code>select count(*)</code> behaves correctly in
     * {@link org.apache.ignite.cache.CacheMode#LOCAL} mode.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCountAll() throws Exception {
        Properties cfg = new Properties();
        cfg.setProperty(IgniteJdbcDriver.PROP_NODE_ID, grid(0).localNode().id().toString());
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(JdbcLocalCachesSelfTest.BASE_URL, cfg);
            ResultSet rs = conn.createStatement().executeQuery("select count(*) from Integer");
            assertTrue(rs.next());
            assertEquals(2L, rs.getLong(1));
        } finally {
            if (conn != null)
                conn.close();

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCache2() throws Exception {
        Properties cfg = new Properties();
        cfg.setProperty(IgniteJdbcDriver.PROP_NODE_ID, grid(1).localNode().id().toString());
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(JdbcLocalCachesSelfTest.BASE_URL, cfg);
            ResultSet rs = conn.createStatement().executeQuery("select _val from Integer order by _val");
            int cnt = 0;
            while (rs.next())
                assertEquals(((++cnt) + 2), rs.getInt(1));

            assertEquals(2, cnt);
        } finally {
            if (conn != null)
                conn.close();

        }
    }
}

