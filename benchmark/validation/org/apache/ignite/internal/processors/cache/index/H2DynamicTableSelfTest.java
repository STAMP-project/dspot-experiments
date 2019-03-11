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


import CacheAtomicityMode.ATOMIC;
import CacheMode.PARTITIONED;
import CacheMode.REPLICATED;
import CacheWriteSynchronizationMode.FULL_ASYNC;
import CacheWriteSynchronizationMode.FULL_SYNC;
import CacheWriteSynchronizationMode.PRIMARY_SYNC;
import QueryUtils.DFLT_SCHEMA;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.QueryEntity;
import org.apache.ignite.cache.QueryIndex;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.IgniteInternalCache;
import org.apache.ignite.internal.processors.query.IgniteSQLException;
import org.apache.ignite.internal.processors.query.QueryTypeDescriptorImpl;
import org.apache.ignite.internal.processors.query.QueryUtils;
import org.apache.ignite.internal.processors.query.h2.H2TableDescriptor;
import org.apache.ignite.internal.processors.query.h2.IgniteH2Indexing;
import org.apache.ignite.internal.processors.query.schema.SchemaOperationException;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Tests for CREATE/DROP TABLE.
 */
@SuppressWarnings("ThrowableNotThrown")
public class H2DynamicTableSelfTest extends AbstractSchemaSelfTest {
    /**
     * Client node index.
     */
    private static final int CLIENT = 2;

    /**
     *
     */
    private static final String INDEXED_CACHE_NAME = (AbstractSchemaSelfTest.CACHE_NAME) + "_idx";

    /**
     *
     */
    private static final String INDEXED_CACHE_NAME_2 = (H2DynamicTableSelfTest.INDEXED_CACHE_NAME) + "_2";

    /**
     * Data region name.
     */
    public static final String DATA_REGION_NAME = "my_data_region";

    /**
     * Bad data region name.
     */
    public static final String DATA_REGION_NAME_BAD = "my_data_region_bad";

    /**
     * Cache with backups.
     */
    private static final String CACHE_NAME_BACKUPS = (AbstractSchemaSelfTest.CACHE_NAME) + "_backups";

    /**
     * Name of the cache that has query parallelism = 7 in it configuration.
     */
    private static final String CACHE_NAME_PARALLELISM_7 = (AbstractSchemaSelfTest.CACHE_NAME) + "_parallelism";

    /**
     * Number of backups for backup test.
     */
    private static final int DFLT_BACKUPS = 2;

    /**
     * Test that {@code CREATE TABLE} actually creates new cache, H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTable() throws Exception {
        doTestCreateTable(AbstractSchemaSelfTest.CACHE_NAME, null, null, null);
    }

    /**
     * Test that {@code CREATE TABLE} actually creates new cache, H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableWithCacheGroup() throws Exception {
        doTestCreateTable(AbstractSchemaSelfTest.CACHE_NAME, "MyGroup", null, null);
    }

    /**
     * Test that {@code CREATE TABLE} actually creates new cache, H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableWithCacheGroupAndLegacyParamName() throws Exception {
        doTestCreateTable(AbstractSchemaSelfTest.CACHE_NAME, "MyGroup", null, null, true);
    }

    /**
     * Test that {@code CREATE TABLE} actually creates new cache from template,
     * H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableWithWriteSyncMode() throws Exception {
        doTestCreateTable(((AbstractSchemaSelfTest.CACHE_NAME) + "_async"), null, null, FULL_ASYNC);
    }

    /**
     * Test that {@code CREATE TABLE} with reserved template cache name actually creates new {@code REPLICATED} cache,
     * H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableReplicated() throws Exception {
        doTestCreateTable("REPLICATED", null, REPLICATED, FULL_SYNC);
    }

    /**
     * Test that {@code CREATE TABLE} with reserved template cache name actually creates new {@code PARTITIONED} cache,
     * H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTablePartitioned() throws Exception {
        doTestCreateTable("PARTITIONED", null, PARTITIONED, FULL_SYNC);
    }

    /**
     * Test that {@code CREATE TABLE} with reserved template cache name actually creates new {@code REPLICATED} cache,
     * H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableReplicatedCaseInsensitive() throws Exception {
        doTestCreateTable("replicated", null, REPLICATED, FULL_SYNC);
    }

    /**
     * Test that {@code CREATE TABLE} with reserved template cache name actually creates new {@code PARTITIONED} cache,
     * H2 table and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTablePartitionedCaseInsensitive() throws Exception {
        doTestCreateTable("partitioned", null, PARTITIONED, FULL_SYNC);
    }

    /**
     * Test that {@code CREATE TABLE} with reserved template cache name actually creates new {@code PARTITIONED} cache,
     * H2 table and type descriptor on all nodes, when no cache template name is given.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableNoTemplate() throws Exception {
        doTestCreateTable(null, null, PARTITIONED, FULL_SYNC);
    }

    /**
     * Test behavior depending on table name case sensitivity.
     */
    @Test
    public void testTableNameCaseSensitivity() {
        doTestTableNameCaseSensitivity("Person", false);
        doTestTableNameCaseSensitivity("Person", true);
    }

    /**
     * Test that {@code CREATE TABLE} with given write sync mode actually creates new cache as needed.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testFullSyncWriteMode() throws Exception {
        doTestCreateTable(null, null, null, FULL_SYNC, "write_synchronization_mode=full_sync");
    }

    /**
     * Test that {@code CREATE TABLE} with given write sync mode actually creates new cache as needed.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testPrimarySyncWriteMode() throws Exception {
        doTestCreateTable(null, null, null, PRIMARY_SYNC, "write_synchronization_mode=primary_sync");
    }

    /**
     * Test that {@code CREATE TABLE} with given write sync mode actually creates new cache as needed.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testFullAsyncWriteMode() throws Exception {
        doTestCreateTable(null, null, null, FULL_ASYNC, "write_synchronization_mode=full_async");
    }

    /**
     * Test behavior only in case of cache name override.
     */
    @Test
    public void testCustomCacheName() {
        doTestCustomNames("cname", null, null);
    }

    /**
     * Test behavior only in case of key type name override.
     */
    @Test
    public void testCustomKeyTypeName() {
        doTestCustomNames(null, "keytype", null);
    }

    /**
     * Test behavior only in case of value type name override.
     */
    @Test
    public void testCustomValueTypeName() {
        doTestCustomNames(null, null, "valtype");
    }

    /**
     * Test behavior only in case of cache and key type name override.
     */
    @Test
    public void testCustomCacheAndKeyTypeName() {
        doTestCustomNames("cname", "keytype", null);
    }

    /**
     * Test behavior only in case of cache and value type name override.
     */
    @Test
    public void testCustomCacheAndValueTypeName() {
        doTestCustomNames("cname", null, "valtype");
    }

    /**
     * Test behavior only in case of key and value type name override.
     */
    @Test
    public void testCustomKeyAndValueTypeName() {
        doTestCustomNames(null, "keytype", "valtype");
    }

    /**
     * Test behavior only in case of cache, key, and value type name override.
     */
    @Test
    public void testCustomCacheAndKeyAndValueTypeName() {
        doTestCustomNames("cname", "keytype", "valtype");
    }

    /**
     * Test that attempting to create a cache with a pre-existing name yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDuplicateCustomCacheName() throws Exception {
        client().getOrCreateCache("new");
        try {
            GridTestUtils.assertThrows(null, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    doTestCustomNames("new", null, null);
                    return null;
                }
            }, IgniteSQLException.class, "Table already exists: NameTest");
        } finally {
            client().destroyCache("new");
        }
    }

    /**
     * Test that {@code CREATE TABLE} with given write sync mode actually creates new cache as needed.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testPlainKey() throws Exception {
        doTestCreateTable(null, null, null, FULL_SYNC);
    }

    /**
     * Test backups propagation.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testBackups() throws Exception {
        String cacheName = "BackupTestCache";
        execute((((("CREATE TABLE \"BackupTest\" (id BIGINT PRIMARY KEY, name VARCHAR) WITH \"template=" + (H2DynamicTableSelfTest.CACHE_NAME_BACKUPS)) + ", cache_name=") + cacheName) + "\""));
        CacheConfiguration ccfg = grid(0).cache(cacheName).getConfiguration(CacheConfiguration.class);
        assertEquals(H2DynamicTableSelfTest.DFLT_BACKUPS, ccfg.getBackups());
        execute("DROP TABLE PUBLIC.\"BackupTest\"");
        execute((((("CREATE TABLE \"BackupTest\" (id BIGINT PRIMARY KEY, name VARCHAR) WITH \"template=" + (H2DynamicTableSelfTest.CACHE_NAME_BACKUPS)) + ", cache_name=") + cacheName) + ", backups=1\""));
        ccfg = grid(0).cache(cacheName).getConfiguration(CacheConfiguration.class);
        assertEquals(1, ccfg.getBackups());
    }

    /**
     * Test parallelism WITH create table command parameter.
     */
    @Test
    public void testQueryParallelism() {
        execute("CREATE TABLE QP_DEFAULT (id INT PRIMARY KEY, val INT)");
        assertQueryParallelism("QP_DEFAULT", 1);
        execute("CREATE TABLE QP_DEFAULT_EXPLICIT (id INT PRIMARY KEY, val INT) WITH \"parallelism = 1 \"");
        assertQueryParallelism("QP_DEFAULT_EXPLICIT", 1);
        execute("CREATE TABLE QP_CUSTOM (id INT PRIMARY KEY, val INT) WITH \"parallelism = 42 \"");
        assertQueryParallelism("QP_CUSTOM", 42);
        execute(((("CREATE TABLE QP_DEFAULT_FROM_TEMPLATE (id INT PRIMARY KEY, val INT) " + "WITH \"template = ") + (H2DynamicTableSelfTest.CACHE_NAME_PARALLELISM_7)) + " \""));
        assertQueryParallelism("QP_DEFAULT_FROM_TEMPLATE", 7);
        execute(((("CREATE TABLE QP_OVERWRITE_TEMPLATE (id INT PRIMARY KEY, val INT) " + "WITH \"parallelism = 42, template = ") + (H2DynamicTableSelfTest.CACHE_NAME_PARALLELISM_7)) + " \""));
        assertQueryParallelism("QP_OVERWRITE_TEMPLATE", 42);
    }

    /**
     * Test that attempting to specify negative number of backups yields exception.
     */
    @Test
    public void testNegativeBackups() {
        assertCreateTableWithParamsThrows("bAckUPs = -5  ", "\"BACKUPS\" cannot be negative: -5");
    }

    /**
     * Negative test that is trying to set incorrect parallelism value: empty, negative, zero or non-integer.
     */
    @Test
    public void testQueryParallelismNegative() {
        assertCreateTableWithParamsThrows("parallelism = 0", "\"PARALLELISM\" must be positive: 0");
        assertCreateTableWithParamsThrows("parallelism = -5", "\"PARALLELISM\" must be positive: -5");
        assertCreateTableWithParamsThrows("parallelism = 3.14", "Parameter value must be an integer [name=PARALLELISM, value=3.14]");
        assertCreateTableWithParamsThrows("parallelism =", "Parameter value cannot be empty: PARALLELISM");
        assertCreateTableWithParamsThrows("parallelism = Five please", "Parameter value must be an integer [name=PARALLELISM, value=Five please]");
    }

    /**
     * Test that attempting to omit mandatory value of BACKUPS parameter yields an error.
     */
    @Test
    public void testEmptyBackups() {
        assertCreateTableWithParamsThrows(" bAckUPs =  ", "Parameter value cannot be empty: BACKUPS");
    }

    /**
     * Test that attempting to omit mandatory value of ATOMICITY parameter yields an error.
     */
    @Test
    public void testEmptyAtomicity() {
        assertCreateTableWithParamsThrows("AtomicitY=  ", "Parameter value cannot be empty: ATOMICITY");
    }

    /**
     * Test that providing an invalid value of ATOMICITY parameter yields an error.
     */
    @Test
    public void testInvalidAtomicity() {
        assertCreateTableWithParamsThrows("atomicity=InvalidValue", "Invalid value of \"ATOMICITY\" parameter (should be either TRANSACTIONAL or ATOMIC): InvalidValue");
    }

    /**
     * Test that attempting to omit mandatory value of CACHEGROUP parameter yields an error.
     */
    @Test
    public void testEmptyCacheGroup() {
        assertCreateTableWithParamsThrows("cache_group=", "Parameter value cannot be empty: CACHE_GROUP");
    }

    /**
     * Test that attempting to omit mandatory value of WRITE_SYNCHRONIZATION_MODE parameter yields an error.
     */
    @Test
    public void testEmptyWriteSyncMode() {
        assertCreateTableWithParamsThrows("write_synchronization_mode=", "Parameter value cannot be empty: WRITE_SYNCHRONIZATION_MODE");
    }

    /**
     * Test that attempting to provide invalid value of WRITE_SYNCHRONIZATION_MODE parameter yields an error.
     */
    @Test
    public void testInvalidWriteSyncMode() {
        assertCreateTableWithParamsThrows("write_synchronization_mode=invalid", ("Invalid value of \"WRITE_SYNCHRONIZATION_MODE\" parameter " + "(should be FULL_SYNC, FULL_ASYNC, or PRIMARY_SYNC): invalid"));
    }

    /**
     * Test that attempting to {@code CREATE TABLE} that already exists does not yield an error if the statement
     *     contains {@code IF NOT EXISTS} clause.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableIfNotExists() throws Exception {
        execute(("CREATE TABLE \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        execute(("CREATE TABLE IF NOT EXISTS \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
    }

    /**
     * Test that attempting to {@code CREATE TABLE} that already exists yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateExistingTable() throws Exception {
        execute(("CREATE TABLE \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute(("CREATE TABLE \"Person\" (\"id\" int, \"city\" varchar" + (", \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
                return null;
            }
        }, IgniteSQLException.class, "Table already exists: Person");
    }

    /**
     * Test that attempting to use a non-existing column name for the primary key when {@code CREATE TABLE}
     * yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCreateTableWithWrongColumnNameAsKey() throws Exception {
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute(("CREATE TABLE \"Person\" (\"id\" int, \"city\" varchar" + (", \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"c_ity\")) WITH " + "\"template=cache\"")));
                return null;
            }
        }, IgniteSQLException.class, "PRIMARY KEY column is not defined: c_ity");
    }

    /**
     * Test that {@code DROP TABLE} executed at client node actually removes specified cache and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDropTableFromClient() throws Exception {
        AbstractSchemaSelfTest.execute(grid(0), ("CREATE TABLE IF NOT EXISTS \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        AbstractSchemaSelfTest.execute(client(), "DROP TABLE \"Person\"");
        for (int i = 0; i < 4; i++) {
            IgniteEx node = grid(i);
            assertNull(node.cache("Person"));
            QueryTypeDescriptorImpl desc = AbstractSchemaSelfTest.type(node, "Person", "Person");
            assertNull(desc);
        }
    }

    /**
     * Test that {@code DROP TABLE} actually removes specified cache and type descriptor on all nodes.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDropTable() throws Exception {
        execute(("CREATE TABLE IF NOT EXISTS \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        execute("DROP TABLE \"Person\"");
        for (int i = 0; i < 4; i++) {
            IgniteEx node = grid(i);
            assertNull(node.cache("Person"));
            QueryTypeDescriptorImpl desc = AbstractSchemaSelfTest.type(node, "Person", "Person");
            assertNull(desc);
        }
    }

    /**
     * Test that attempting to execute {@code DROP TABLE} via API of cache being dropped yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testCacheSelfDrop() throws Exception {
        execute(("CREATE TABLE IF NOT EXISTS \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                client().cache(QueryUtils.createTableCacheName(DFLT_SCHEMA, "Person")).query(new SqlFieldsQuery("DROP TABLE \"Person\"")).getAll();
                return null;
            }
        }, IgniteSQLException.class, ("DROP TABLE cannot be called from the same cache that holds the table " + "being dropped"));
        execute("DROP TABLE \"Person\"");
    }

    /**
     * Test that attempting to {@code DROP TABLE} that does not exist does not yield an error if the statement contains
     *     {@code IF EXISTS} clause.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDropMissingTableIfExists() throws Exception {
        execute("DROP TABLE IF EXISTS \"City\"");
    }

    /**
     * Test that attempting to {@code DROP TABLE} that does not exist yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDropMissingTable() throws Exception {
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute("DROP TABLE \"City\"");
                return null;
            }
        }, IgniteSQLException.class, "Table doesn't exist: City");
    }

    /**
     * Check that {@code DROP TABLE} for caches not created with {@code CREATE TABLE} yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDropNonDynamicTable() throws Exception {
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute("DROP TABLE PUBLIC.\"Integer\"");
                return null;
            }
        }, IgniteSQLException.class, "Only cache created with CREATE TABLE may be removed with DROP TABLE [cacheName=cache_idx_2]");
    }

    /**
     * Test that attempting to destroy via cache API a cache created via SQL finishes successfully.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testDestroyDynamicSqlCache() throws Exception {
        execute(("CREATE TABLE \"Person\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "\"template=cache\"")));
        client().destroyCache(H2DynamicTableSelfTest.cacheName("Person"));
    }

    /**
     * Test that attempting to start a node that has a cache with the name already present in the grid and whose
     * SQL flag does not match that of cache with the same name that is already started, yields an error.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testSqlFlagCompatibilityCheck() throws Exception {
        execute(("CREATE TABLE \"Person\" (\"id\" int, \"city\" varchar, \"name\" varchar, \"surname\" varchar, " + "\"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH \"template=cache\""));
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                String cacheName = H2DynamicTableSelfTest.cacheName("Person");
                Ignition.start(clientConfiguration(5).setCacheConfiguration(new CacheConfiguration(cacheName)));
                return null;
            }
        }, IgniteException.class, ("Cache configuration mismatch (local cache was created via Ignite API, while " + "remote cache was created via CREATE TABLE): SQL_PUBLIC_Person"));
    }

    /**
     * Tests index name conflict check in discovery thread.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testIndexNameConflictCheckDiscovery() throws Exception {
        AbstractSchemaSelfTest.execute(grid(0), "CREATE TABLE \"Person\" (id int primary key, name varchar)");
        AbstractSchemaSelfTest.execute(grid(0), "CREATE INDEX \"idx\" ON \"Person\" (\"name\")");
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                QueryEntity e = new QueryEntity();
                e.setTableName("City");
                e.setKeyFields(Collections.singleton("name"));
                e.setFields(new LinkedHashMap(Collections.singletonMap("name", String.class.getName())));
                e.setIndexes(Collections.singleton(new QueryIndex("name").setName("idx")));
                e.setKeyType("CityKey");
                e.setValueType("City");
                AbstractSchemaSelfTest.queryProcessor(client()).dynamicTableCreate("PUBLIC", e, PARTITIONED.name(), null, null, null, null, ATOMIC, null, 10, false, false, null);
                return null;
            }
        }, SchemaOperationException.class, "Index already exists: idx");
    }

    /**
     * Tests table name conflict check in {@link CommandProcessor}.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testTableNameConflictCheckSql() throws Exception {
        AbstractSchemaSelfTest.execute(grid(0), "CREATE TABLE \"Person\" (id int primary key, name varchar)");
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                AbstractSchemaSelfTest.execute(client(), "CREATE TABLE \"Person\" (id int primary key, name varchar)");
                return null;
            }
        }, IgniteSQLException.class, "Table already exists: Person");
    }

    /**
     *
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testAffinityKey() throws Exception {
        execute(("CREATE TABLE \"City\" (\"name\" varchar primary key, \"code\" int) WITH wrap_key,wrap_value," + "\"affinity_key=\'name\'\""));
        assertAffinityCacheConfiguration("City", "name");
        execute("INSERT INTO \"City\" (\"name\", \"code\") values (\'A\', 1), (\'B\', 2), (\'C\', 3)");
        List<String> cityNames = Arrays.asList("A", "B", "C");
        List<Integer> cityCodes = Arrays.asList(1, 2, 3);
        // We need unique name for this table to avoid conflicts with existing binary metadata.
        execute(("CREATE TABLE \"Person2\" (\"id\" int, \"city\" varchar," + (" \"name\" varchar, \"surname\" varchar, \"age\" int, PRIMARY KEY (\"id\", \"city\")) WITH " + "wrap_key,wrap_value,\"template=cache,affinity_key=\'city\'\"")));
        assertAffinityCacheConfiguration("Person2", "city");
        Random r = new Random();
        Map<Integer, Integer> personId2cityCode = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            int cityIdx = r.nextInt(3);
            String cityName = cityNames.get(cityIdx);
            int cityCode = cityCodes.get(cityIdx);
            personId2cityCode.put(i, cityCode);
            AbstractSchemaSelfTest.queryProcessor(client()).querySqlFields(new SqlFieldsQuery(("insert into \"Person2\"(\"id\", " + "\"city\") values (?, ?)")).setArgs(i, cityName), true).getAll();
        }
        List<List<?>> res = AbstractSchemaSelfTest.queryProcessor(client()).querySqlFields(new SqlFieldsQuery(("select \"id\", " + ("c.\"code\" from \"Person2\" p left join \"City\" c on p.\"city\" = c.\"name\" where c.\"name\" " + "is not null"))), true).getAll();
        assertEquals(100, res.size());
        for (int i = 0; i < 100; i++) {
            assertNotNull(res.get(i).get(0));
            assertNotNull(res.get(i).get(1));
            int id = ((Integer) (res.get(i).get(0)));
            int code = ((Integer) (res.get(i).get(1)));
            assertEquals(((int) (personId2cityCode.get(id))), code);
        }
    }

    /**
     * Test data region.
     *
     * @throws Exception
     * 		If failed.
     */
    @SuppressWarnings({ "ThrowableNotThrown", "unchecked" })
    @Test
    public void testDataRegion() throws Exception {
        // Empty region name.
        GridTestUtils.assertThrows(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                execute("CREATE TABLE TEST_DATA_REGION (name varchar primary key, code int) WITH \"data_region=\"");
                return null;
            }
        }, IgniteSQLException.class, "Parameter value cannot be empty: DATA_REGION");
        // Valid region name.
        execute((("CREATE TABLE TEST_DATA_REGION (name varchar primary key, code int) WITH \"data_region=" + (H2DynamicTableSelfTest.DATA_REGION_NAME)) + "\""));
        CacheConfiguration ccfg = client().cache("SQL_PUBLIC_TEST_DATA_REGION").getConfiguration(CacheConfiguration.class);
        assertEquals(H2DynamicTableSelfTest.DATA_REGION_NAME, ccfg.getDataRegionName());
    }

    /**
     * Test various cases of affinity key column specification.
     */
    @Test
    public void testAffinityKeyCaseSensitivity() {
        execute(("CREATE TABLE \"A\" (\"name\" varchar primary key, \"code\" int) WITH wrap_key,wrap_value," + "\"affinity_key=\'name\'\""));
        assertAffinityCacheConfiguration("A", "name");
        execute(("CREATE TABLE \"B\" (name varchar primary key, \"code\" int) WITH wrap_key,wrap_value," + "\"affinity_key=name\""));
        assertAffinityCacheConfiguration("B", "NAME");
        execute(("CREATE TABLE \"C\" (name varchar primary key, \"code\" int) WITH wrap_key,wrap_value," + "\"affinity_key=NamE\""));
        assertAffinityCacheConfiguration("C", "NAME");
        execute(("CREATE TABLE \"D\" (\"name\" varchar primary key, \"code\" int) WITH wrap_key,wrap_value," + "\"affinity_key=NAME\""));
        assertAffinityCacheConfiguration("D", "name");
        // Error arises because user has specified case sensitive affinity column name
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute(("CREATE TABLE \"E\" (name varchar primary key, \"code\" int) WITH wrap_key,wrap_value," + "\"affinity_key=\'Name\'\""));
                return null;
            }
        }, IgniteSQLException.class, "Affinity key column with given name not found: Name");
        // Error arises because user declares case insensitive affinity column name while having two 'name'
        // columns whose names are equal in ignore case.
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute(("CREATE TABLE \"E\" (\"name\" varchar, \"Name\" int, val int, primary key(\"name\", " + "\"Name\")) WITH \"affinity_key=name\""));
                return null;
            }
        }, IgniteSQLException.class, "Ambiguous affinity column name, use single quotes for case sensitivity: name");
        execute(("CREATE TABLE \"E\" (\"name\" varchar, \"Name\" int, val int, primary key(\"name\", " + "\"Name\")) WITH wrap_key,wrap_value,\"affinityKey=\'Name\'\""));
        assertAffinityCacheConfiguration("E", "Name");
        execute("drop table a");
        execute("drop table b");
        execute("drop table c");
        execute("drop table d");
        execute("drop table e");
    }

    /**
     * Tests that attempting to specify an affinity key that actually is a value column yields an error.
     */
    @Test
    public void testAffinityKeyNotKeyColumn() {
        // Error arises because user has specified case sensitive affinity column name
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute("CREATE TABLE \"E\" (name varchar primary key, \"code\" int) WITH \"affinity_key=code\"");
                return null;
            }
        }, IgniteSQLException.class, "Affinity key column must be one of key columns: code");
    }

    /**
     * Tests that attempting to specify an affinity key that actually is a value column yields an error.
     */
    @Test
    public void testAffinityKeyNotFound() {
        // Error arises because user has specified case sensitive affinity column name
        GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                execute("CREATE TABLE \"E\" (name varchar primary key, \"code\" int) WITH \"affinity_key=missing\"");
                return null;
            }
        }, IgniteSQLException.class, "Affinity key column with given name not found: missing");
    }

    /**
     * Tests behavior on sequential create and drop of a table and its index.
     */
    @Test
    public void testTableAndIndexRecreate() {
        execute("drop table if exists \"PUBLIC\".t");
        // First let's check behavior without index name set
        execute("create table \"PUBLIC\".t (a int primary key, b varchar(30))");
        fillRecreatedTable();
        execute("create index on \"PUBLIC\".t (b desc)");
        execute("drop table \"PUBLIC\".t");
        assertNull(client().cache(H2DynamicTableSelfTest.cacheName("t")));
        execute("create table \"PUBLIC\".t (a int primary key, b varchar(30))");
        fillRecreatedTable();
        execute("create index on \"PUBLIC\".t (b desc)");
        execute("drop table \"PUBLIC\".t");
        assertNull(client().cache("t"));
        // And now let's do the same for the named index
        execute("create table \"PUBLIC\".t (a int primary key, b varchar(30))");
        fillRecreatedTable();
        execute("create index namedIdx on \"PUBLIC\".t (b desc)");
        execute("drop table \"PUBLIC\".t");
        assertNull(client().cache("t"));
        execute("create table \"PUBLIC\".t (a int primary key, b varchar(30))");
        fillRecreatedTable();
        execute("create index namedIdx on \"PUBLIC\".t (b desc)");
        execute("drop table \"PUBLIC\".t");
    }

    /**
     *
     *
     * @throws Exception
     * 		If test failed.
     */
    @Test
    public void testQueryLocalWithRecreate() throws Exception {
        execute(("CREATE TABLE A(id int primary key, name varchar, surname varchar) WITH \"cache_name=cache," + "template=replicated\""));
        // In order for local queries to work, let's use non client node.
        IgniteInternalCache cache = grid(0).cachex("cache");
        assertNotNull(cache);
        executeLocal(cache.context(), "INSERT INTO A(id, name, surname) values (1, 'X', 'Y')");
        assertEqualsCollections(Collections.singletonList(Arrays.asList(1, "X", "Y")), executeLocal(cache.context(), "SELECT id, name, surname FROM A"));
        execute("DROP TABLE A");
        execute("CREATE TABLE A(id int primary key, name varchar, surname varchar) WITH \"cache_name=cache\"");
        cache = grid(0).cachex("cache");
        assertNotNull(cache);
        try {
            executeLocal(cache.context(), "INSERT INTO A(id, name, surname) values (1, 'X', 'Y')");
        } finally {
            execute("DROP TABLE A");
        }
    }

    /**
     * Test that it's impossible to create tables with same name regardless of key/value wrapping settings.
     */
    @Test
    public void testWrappedAndUnwrappedKeyTablesInteroperability() {
        {
            execute("create table a (id int primary key, x varchar)");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_key", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_value", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_key,wrap_value", "Table already exists: A");
            execute("drop table a");
        }
        {
            execute("create table a (id int primary key, x varchar) with wrap_key");
            assertDdlCommandThrows("create table a (id int primary key, x varchar)", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_value", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_key,wrap_value", "Table already exists: A");
            execute("drop table a");
        }
        {
            execute("create table a (id int primary key, x varchar) with wrap_value");
            assertDdlCommandThrows("create table a (id int primary key, x varchar)", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_key", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_key,wrap_value", "Table already exists: A");
            execute("drop table a");
        }
        {
            execute("create table a (id int primary key, x varchar) with wrap_key,wrap_value");
            assertDdlCommandThrows("create table a (id int primary key, x varchar)", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_value", "Table already exists: A");
            assertDdlCommandThrows("create table a (id int primary key, x varchar) with wrap_key", "Table already exists: A");
            execute("drop table a");
        }
    }

    /**
     * Test that it's possible to create tables with matching key and/or value primitive types.
     */
    @Test
    public void testDynamicTablesInteroperability() {
        execute("create table a (id int primary key, x varchar) with \"wrap_value=false\"");
        execute("create table b (id long primary key, y varchar) with \"wrap_value=false\"");
        execute("create table c (id int primary key, z long) with \"wrap_value=false\"");
        execute("create table d (id int primary key, w varchar) with \"wrap_value=false\"");
        execute("drop table a");
        execute("drop table b");
        execute("drop table c");
        execute("drop table d");
    }

    /**
     * Test that when key or value has more than one column, wrap=false is forbidden.
     */
    @Test
    public void testWrappingAlwaysOnWithComplexObjects() {
        assertDdlCommandThrows("create table a (id int, x varchar, c long, primary key(id, c)) with \"wrap_key=false\"", "WRAP_KEY cannot be false when composite primary key exists.");
        assertDdlCommandThrows("create table a (id int, x varchar, c long, primary key(id)) with \"wrap_value=false\"", "WRAP_VALUE cannot be false when multiple non-primary key columns exist.");
    }

    /**
     * Test behavior when neither key nor value should be wrapped.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testNoWrap() throws SQLException {
        doTestKeyValueWrap(false, false, false);
    }

    /**
     * Test behavior when only key is wrapped.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testKeyWrap() throws SQLException {
        doTestKeyValueWrap(true, false, false);
    }

    /**
     * Test behavior when only value is wrapped.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testValueWrap() throws SQLException {
        doTestKeyValueWrap(false, true, false);
    }

    /**
     * Test behavior when both key and value is wrapped.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testKeyAndValueWrap() throws SQLException {
        doTestKeyValueWrap(true, true, false);
    }

    /**
     * Test behavior when neither key nor value should be wrapped.
     * Key and value are UUID.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testUuidNoWrap() throws SQLException {
        doTestKeyValueWrap(false, false, true);
    }

    /**
     * Test behavior when only key is wrapped.
     * Key and value are UUID.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testUuidKeyWrap() throws SQLException {
        doTestKeyValueWrap(true, false, true);
    }

    /**
     * Test behavior when only value is wrapped.
     * Key and value are UUID.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testUuidValueWrap() throws SQLException {
        doTestKeyValueWrap(false, true, true);
    }

    /**
     * Test behavior when both key and value is wrapped.
     * Key and value are UUID.
     *
     * @throws SQLException
     * 		if failed.
     */
    @Test
    public void testUuidKeyAndValueWrap() throws SQLException {
        doTestKeyValueWrap(true, true, true);
    }

    /**
     * Test that tables method only returns tables belonging to given cache.
     *
     * @throws Exception
     * 		if failed.
     */
    @Test
    public void testGetTablesForCache() throws Exception {
        try {
            execute("create table t1(id int primary key, name varchar)");
            execute("create table t2(id int primary key, name varchar)");
            IgniteH2Indexing h2Idx = ((IgniteH2Indexing) (grid(0).context().query().getIndexing()));
            String cacheName = H2DynamicTableSelfTest.cacheName("T1");
            Collection<H2TableDescriptor> col = h2Idx.schemaManager().tablesForCache(cacheName);
            assertNotNull(col);
            H2TableDescriptor[] tables = col.toArray(new H2TableDescriptor[col.size()]);
            assertEquals(1, tables.length);
            assertEquals(tables[0].table().getName(), "T1");
        } finally {
            execute("drop table t1 if exists");
            execute("drop table t2 if exists");
        }
    }
}

