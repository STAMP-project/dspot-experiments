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
package org.apache.ignite.internal.processors.cache.mvcc;


import java.util.Arrays;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.transactions.TransactionDuplicateKeyException;
import org.junit.Test;


/**
 *
 */
public class CacheMvccDmlSimpleTest extends CacheMvccAbstractTest {
    /**
     *
     */
    private IgniteCache<?, ?> cache;

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testInsert() throws Exception {
        int cnt = update("insert into Integer(_key, _val) values(1, 1),(2, 2)");
        assertEquals(2, cnt);
        assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
        try {
            update("insert into Integer(_key, _val) values(3, 3),(1, 1)");
        } catch (CacheException e) {
            assertTrue(((e.getCause()) instanceof TransactionDuplicateKeyException));
            assertTrue(e.getMessage().startsWith("Duplicate key during INSERT ["));
        }
        assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testMerge() throws Exception {
        {
            int cnt = update("merge into Integer(_key, _val) values(1, 1),(2, 2)");
            assertEquals(2, cnt);
            assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("merge into Integer(_key, _val) values(3, 3),(1, 1)");
            assertEquals(2, cnt);
            assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2), Arrays.asList(3, 3)), query("select * from Integer"));
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testUpdate() throws Exception {
        {
            int cnt = update("update Integer set _val = 42 where _key = 42");
            assertEquals(0, cnt);
            assertTrue(query("select * from Integer").isEmpty());
        }
        update("insert into Integer(_key, _val) values(1, 1),(2, 2)");
        {
            int cnt = update("update Integer set _val = 42 where _key = 42");
            assertEquals(0, cnt);
            assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("update Integer set _val = 42 where _key >= 42");
            assertEquals(0, cnt);
            assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("update Integer set _val = 11 where _key = 1");
            assertEquals(1, cnt);
            assertEquals(asSet(Arrays.asList(1, 11), Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("update Integer set _val = 12 where _key <= 2");
            assertEquals(asSet(Arrays.asList(1, 12), Arrays.asList(2, 12)), query("select * from Integer"));
            assertEquals(2, cnt);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDelete() throws Exception {
        {
            int cnt = update("delete from Integer where _key = 42");
            assertEquals(0, cnt);
        }
        update("insert into Integer(_key, _val) values(1, 1),(2, 2)");
        {
            int cnt = update("delete from Integer where _key = 42");
            assertEquals(0, cnt);
            assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("delete from Integer where _key >= 42");
            assertEquals(0, cnt);
            assertEquals(asSet(Arrays.asList(1, 1), Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("delete from Integer where _key = 1");
            assertEquals(1, cnt);
            assertEquals(asSet(Arrays.asList(2, 2)), query("select * from Integer"));
        }
        {
            int cnt = update("delete from Integer where _key <= 2");
            assertTrue(query("select * from Integer").isEmpty());
            assertEquals(1, cnt);
        }
    }
}

