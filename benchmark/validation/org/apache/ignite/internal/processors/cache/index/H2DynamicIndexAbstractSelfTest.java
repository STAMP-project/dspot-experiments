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
package org.apache.ignite.internal.processors.cache.index;


import IgniteQueryErrorCode.INDEX_ALREADY_EXISTS;
import IgniteQueryErrorCode.INDEX_NOT_FOUND;
import java.util.Collections;
import java.util.List;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.internal.util.typedef.F;
import org.junit.Test;


/**
 * Test that checks indexes handling on H2 side.
 */
public abstract class H2DynamicIndexAbstractSelfTest extends AbstractSchemaSelfTest {
    /**
     * Client node index.
     */
    private static final int CLIENT = 2;

    /**
     * Test that after index creation index is used by queries.
     */
    @Test
    public void testCreateIndex() throws Exception {
        IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        assertSize(3);
        cache.query(new SqlFieldsQuery((((((("CREATE INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"") + (AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED)) + "\" ASC)"))).getAll();
        // Test that local queries on all nodes use new index.
        for (int i = 0; i < 4; i++) {
            if (ignite(i).configuration().isClientMode())
                continue;

            List<List<?>> locRes = ignite(i).cache("cache").query(new SqlFieldsQuery(("explain select \"id\" from " + "\"cache\".\"ValueClass\" where \"field1\" = \'A\'")).setLocal(true)).getAll();
            assertEquals(F.asList(Collections.singletonList(("SELECT\n" + ((("    \"id\"\n" + "FROM \"cache\".\"ValueClass\"\n") + "    /* \"cache\".\"idx_1\": \"field1\" = \'A\' */\n") + "WHERE \"field1\" = \'A\'")))), locRes);
        }
        assertSize(3);
        cache.remove(new AbstractSchemaSelfTest.KeyClass(2));
        assertSize(2);
        cache.put(new AbstractSchemaSelfTest.KeyClass(4), new AbstractSchemaSelfTest.ValueClass("someVal"));
        assertSize(3);
    }

    /**
     * Test that creating an index with duplicate name yields an error.
     */
    @Test
    public void testCreateIndexWithDuplicateName() {
        final IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        cache.query(new SqlFieldsQuery((((((("CREATE INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"") + (AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED)) + "\" ASC)")));
        AbstractSchemaSelfTest.assertSqlException(new AbstractSchemaSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                cache.query(new SqlFieldsQuery((((("CREATE INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"id\" ASC)")));
            }
        }, INDEX_ALREADY_EXISTS);
    }

    /**
     * Test that creating an index with duplicate name does not yield an error with {@code IF NOT EXISTS}.
     */
    @Test
    public void testCreateIndexIfNotExists() {
        final IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        cache.query(new SqlFieldsQuery((((((("CREATE INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"") + (AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED)) + "\" ASC)")));
        cache.query(new SqlFieldsQuery((((("CREATE INDEX IF NOT EXISTS \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"id\" ASC)")));
    }

    /**
     * Test that after index drop there are no attempts to use it, and data state remains intact.
     */
    @Test
    public void testDropIndex() {
        IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        assertSize(3);
        cache.query(new SqlFieldsQuery((((((("CREATE INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"") + (AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED)) + "\" ASC)")));
        assertSize(3);
        cache.query(new SqlFieldsQuery((("DROP INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\"")));
        // Test that no local queries on all nodes use new index.
        for (int i = 0; i < 4; i++) {
            if (ignite(i).configuration().isClientMode())
                continue;

            List<List<?>> locRes = ignite(i).cache("cache").query(new SqlFieldsQuery(("explain select \"id\" from " + "\"cache\".\"ValueClass\" where \"field1\" = \'A\'")).setLocal(true)).getAll();
            assertEquals(F.asList(Collections.singletonList(("SELECT\n" + ((("    \"id\"\n" + "FROM \"cache\".\"ValueClass\"\n") + "    /* \"cache\".\"ValueClass\".__SCAN_ */\n") + "WHERE \"field1\" = \'A\'")))), locRes);
        }
        assertSize(3);
    }

    /**
     * Test that dropping a non-existent index yields an error.
     */
    @Test
    public void testDropMissingIndex() {
        final IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        AbstractSchemaSelfTest.assertSqlException(new AbstractSchemaSelfTest.RunnableX() {
            @Override
            public void run() throws Exception {
                cache.query(new SqlFieldsQuery((("DROP INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\"")));
            }
        }, INDEX_NOT_FOUND);
    }

    /**
     * Test that dropping a non-existent index does not yield an error with {@code IF EXISTS}.
     */
    @Test
    public void testDropMissingIndexIfExists() {
        final IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        cache.query(new SqlFieldsQuery((("DROP INDEX IF EXISTS \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\"")));
    }

    /**
     * Test that changes in cache affect index, and vice versa.
     */
    @Test
    public void testIndexState() {
        IgniteCache<AbstractSchemaSelfTest.KeyClass, AbstractSchemaSelfTest.ValueClass> cache = cache();
        assertColumnValues("val1", "val2", "val3");
        cache.query(new SqlFieldsQuery((((((("CREATE INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\" ON \"") + (AbstractSchemaSelfTest.TBL_NAME_ESCAPED)) + "\"(\"") + (AbstractSchemaSelfTest.FIELD_NAME_1_ESCAPED)) + "\" ASC)")));
        assertColumnValues("val1", "val2", "val3");
        cache.remove(new AbstractSchemaSelfTest.KeyClass(2));
        assertColumnValues("val1", "val3");
        cache.put(new AbstractSchemaSelfTest.KeyClass(0), new AbstractSchemaSelfTest.ValueClass("someVal"));
        assertColumnValues("someVal", "val1", "val3");
        cache.query(new SqlFieldsQuery((("DROP INDEX \"" + (AbstractSchemaSelfTest.IDX_NAME_1_ESCAPED)) + "\"")));
        assertColumnValues("someVal", "val1", "val3");
    }
}

