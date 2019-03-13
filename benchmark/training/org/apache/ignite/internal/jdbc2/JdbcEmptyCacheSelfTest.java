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


import java.sql.ResultSet;
import java.sql.Statement;
import org.junit.Test;


/**
 * Tests for empty cache.
 */
public class JdbcEmptyCacheSelfTest extends GridCommonAbstractTest {
    /**
     * Cache name.
     */
    private static final String CACHE_NAME = "cache";

    /**
     * JDBC URL.
     */
    private static final String BASE_URL = (((CFG_URL_PREFIX) + "cache=") + (JdbcEmptyCacheSelfTest.CACHE_NAME)) + "@modules/clients/src/test/config/jdbc-config.xml";

    /**
     * Statement.
     */
    private Statement stmt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectNumber() throws Exception {
        ResultSet rs = stmt.executeQuery("select 1");
        int cnt = 0;
        while (rs.next()) {
            assert (rs.getInt(1)) == 1;
            assert "1".equals(rs.getString(1));
            cnt++;
        } 
        assert cnt == 1;
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSelectString() throws Exception {
        ResultSet rs = stmt.executeQuery("select 'str'");
        int cnt = 0;
        while (rs.next()) {
            assertEquals("str", rs.getString(1));
            cnt++;
        } 
        assert cnt == 1;
    }
}

