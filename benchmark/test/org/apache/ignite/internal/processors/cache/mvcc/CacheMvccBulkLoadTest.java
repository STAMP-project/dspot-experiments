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


import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.junit.Test;


/**
 *
 */
public class CacheMvccBulkLoadTest extends CacheMvccAbstractTest {
    /**
     *
     */
    private IgniteCache<Object, Object> sqlNexus;

    /**
     *
     */
    private Statement stmt;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCopyStoresData() throws Exception {
        String csvFilePath = new File(getClass().getResource("mvcc_person.csv").toURI()).getAbsolutePath();
        stmt.executeUpdate((("copy from '" + csvFilePath) + "' into person (id, name) format csv"));
        List<List<?>> rows = sqlNexus.query(CacheMvccBulkLoadTest.q("select * from person")).getAll();
        List<List<? extends Serializable>> exp = Arrays.asList(Arrays.asList(1, "John"), Arrays.asList(2, "Jack"));
        assertEquals(exp, rows);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCopyDoesNotOverwrite() throws Exception {
        sqlNexus.query(CacheMvccBulkLoadTest.q("insert into person values(1, 'Old')"));
        String csvFilePath = new File(getClass().getResource("mvcc_person.csv").toURI()).getAbsolutePath();
        stmt.executeUpdate((("copy from '" + csvFilePath) + "' into person (id, name) format csv"));
        List<List<?>> rows = sqlNexus.query(CacheMvccBulkLoadTest.q("select * from person")).getAll();
        List<List<? extends Serializable>> exp = Arrays.asList(Arrays.asList(1, "Old"), Arrays.asList(2, "Jack"));
        assertEquals(exp, rows);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCopyLeavesPartialResultsInCaseOfFailure() throws Exception {
        String csvFilePath = new File(getClass().getResource("mvcc_person_broken.csv").toURI()).getAbsolutePath();
        try {
            stmt.executeUpdate((("copy from '" + csvFilePath) + "' into person (id, name) format csv"));
            fail();
        } catch (SQLException ignored) {
            // assert exception is thrown
        }
        List<List<?>> rows = sqlNexus.query(CacheMvccBulkLoadTest.q("select * from person")).getAll();
        List<List<? extends Serializable>> exp = Collections.singletonList(Arrays.asList(1, "John"));
        assertEquals(exp, rows);
    }
}

