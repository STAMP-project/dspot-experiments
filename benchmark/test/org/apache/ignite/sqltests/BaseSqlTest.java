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
package org.apache.ignite.sqltests;


import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.FieldsQueryCursor;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.processors.cache.index.AbstractIndexingCommonTest;
import org.apache.ignite.internal.processors.query.IgniteSQLException;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.lang.IgnitePredicate;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test base for test for sql features.
 */
public class BaseSqlTest extends AbstractIndexingCommonTest {
    /**
     * Number of all employees.
     */
    public static final long EMP_CNT = 1000L;

    /**
     * Number of all departments.
     */
    public static final long DEP_CNT = 50L;

    /**
     * Number of all addresses.
     */
    public static final long ADDR_CNT = 500L;

    /**
     * Number of employees that aren't associated with any department.
     */
    public static final long FREE_EMP_CNT = 50;

    /**
     * Number of departments that don't have employees and addresses.
     */
    public static final long FREE_DEP_CNT = 5;

    /**
     * Number of adderesses that are not associated with any departments.
     */
    public static final long FREE_ADDR_CNT = 30;

    /**
     * Number of possible age values (width of ages values range).
     */
    public static final int AGES_CNT = 50;

    /**
     * Name of client node.
     */
    public static final String CLIENT_NODE_NAME = "clientNode";

    /**
     * Name of the Employee table cache.
     */
    public static final String EMP_CACHE_NAME = "SQL_PUBLIC_EMPLOYEE";

    /**
     * Name of the Department table cache.
     */
    public static final String DEP_CACHE_NAME = "SQL_PUBLIC_DEPARTMENT";

    /**
     * Name of the Address table cache.
     */
    public static final String ADDR_CACHE_NAME = "SQL_PUBLIC_ADDRESS";

    /**
     * Client node instance.
     */
    protected static IgniteEx client;

    /**
     * Node name of second server.
     */
    public final String SRV2_NAME = "server2";

    /**
     * Node name of first server.
     */
    public final String SRV1_NAME = "server1";

    public static final String[] ALL_EMP_FIELDS = new String[]{ "ID", "DEPID", "DEPIDNOIDX", "FIRSTNAME", "LASTNAME", "AGE", "SALARY" };

    /**
     * Flag that forces to do explain query in log before performing actual query.
     */
    public static boolean explain = false;

    /**
     * Department table name.
     */
    protected String DEP_TAB = "Department";

    /**
     * Random for generator.
     */
    private Random rnd = new Random();

    /**
     * Result of sql query. Contains metadata and all values in memory.
     */
    static class Result {
        /**
         * Names of columns.
         */
        private List<String> colNames;

        /**
         * Table
         */
        private List<List<?>> vals;

        /**
         *
         */
        public Result(List<String> colNames, List<List<?>> vals) {
            this.colNames = colNames;
            this.vals = vals;
        }

        /**
         *
         *
         * @return metadata - name of columns.
         */
        public List<String> columnNames() {
            return colNames;
        }

        /**
         *
         *
         * @return table, the actual data.
         */
        public List<List<?>> values() {
            return vals;
        }

        /**
         * Creates result from cursor.
         *
         * @param cursor
         * 		cursor to use to read column names and data.
         * @return Result that contains data and metadata, fetched from cursor.
         */
        public static BaseSqlTest.Result fromCursor(FieldsQueryCursor<List<?>> cursor) {
            List<String> cols = BaseSqlTest.readColNames(cursor);
            List<List<?>> vals = cursor.getAll();
            return new BaseSqlTest.Result(cols, vals);
        }
    }

    /**
     * Check basic SELECT * query.
     */
    @Test
    public void testBasicSelect() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result emps = executeFrom("SELECT * FROM Employee", node);
            assertContainsEq("SELECT * returned unexpected column names.", emps.columnNames(), Arrays.asList(BaseSqlTest.ALL_EMP_FIELDS));
            List<List<Object>> expEmps = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), null, emps.columnNames().toArray(new String[0]));
            assertContainsEq(emps.values(), expEmps);
        });
    }

    /**
     * Check SELECT query with projection (fields).
     */
    @Test
    public void testSelectFields() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result res = executeFrom("SELECT firstName, id, age FROM Employee;", node);
            String[] fields = new String[]{ "FIRSTNAME", "ID", "AGE" };
            assertEquals("Returned column names are incorrect.", res.columnNames(), Arrays.asList(fields));
            List<List<Object>> expected = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), null, fields);
            assertContainsEq(res.values(), expected);
        });
    }

    /**
     * Check basic BETWEEN operator usage.
     */
    @Test
    public void testSelectBetween() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result emps = executeFrom("SELECT * FROM Employee e WHERE e.id BETWEEN 101 and 200", node);
            assertEquals("Fetched number of employees is incorrect", 100, emps.values().size());
            String[] fields = emps.columnNames().toArray(new String[0]);
            assertContainsEq("SELECT * returned unexpected column names.", emps.columnNames(), Arrays.asList(BaseSqlTest.ALL_EMP_FIELDS));
            IgnitePredicate<Map<String, Object>> between = ( row) -> {
                long id = ((Long) (row.get("ID")));
                return (101 <= id) && (id <= 200);
            };
            List<List<Object>> expected = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), between, fields);
            assertContainsEq(emps.values(), expected);
        });
    }

    /**
     * Check BETWEEN operator filters out all the result (empty result set is expected).
     */
    @Test
    public void testEmptyBetween() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result emps = executeFrom("SELECT * FROM Employee e WHERE e.id BETWEEN 200 AND 101", node);
            assertTrue(("SQL should have returned empty result set, but it have returned: " + emps), emps.values().isEmpty());
        });
    }

    /**
     * Check SELECT IN with fixed values.
     */
    @Test
    public void testSelectInStatic() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result actual = executeFrom("SELECT age FROM Employee WHERE id IN (1, 256, 42)", node);
            List<List<Object>> expected = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), ( row) -> {
                Object id = row.get("ID");
                return ((F.eq(id, 1L)) || (F.eq(id, 256L))) || (F.eq(id, 42L));
            }, "AGE");
            assertContainsEq(actual.values(), expected);
        });
    }

    /**
     * Check SELECT IN with simple subquery values.
     */
    @Test
    public void testSelectInSubquery() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result actual = executeFrom("SELECT lastName FROM Employee WHERE id in (SELECT id FROM Employee WHERE age < 30)", node);
            List<List<Object>> expected = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), ( row) -> ((Integer) (row.get("AGE"))) < 30, "lastName");
            assertContainsEq(actual.values(), expected);
        });
    }

    /**
     * Check ORDER BY operator with varchar field.
     */
    @Test
    public void testBasicOrderByLastName() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result result = executeFrom("SELECT * FROM Employee e ORDER BY e.lastName", node);
            List<List<Object>> exp = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), null, result.columnNames().toArray(new String[0]));
            assertContainsEq(result.values(), exp);
            int lastNameIdx = result.columnNames().indexOf("LASTNAME");
            Comparator<List<?>> asc = Comparator.comparing((List<?> row) -> ((String) (row.get(lastNameIdx))));
            assertSortedBy(result.values(), asc);
        });
    }

    /**
     * Check DISTINCT operator selecting not unique field.
     */
    @Test
    public void testBasicDistinct() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result ages = executeFrom("SELECT DISTINCT age FROM Employee", node);
            Set<Object> expected = BaseSqlTest.distinct(BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), null, "age"));
            assertContainsEq("Values in cache differ from values returned from sql.", ages.values(), expected);
        });
    }

    /**
     * Check simple WHERE operator.
     */
    @Test
    public void testDistinctWithWhere() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result ages = executeFrom("SELECT DISTINCT age FROM Employee WHERE id < 100", node);
            Set<Object> expAges = BaseSqlTest.distinct(BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), ( row) -> ((Long) (row.get("ID"))) < 100, "age"));
            assertContainsEq(ages.values(), expAges);
        });
    }

    /**
     * Check greater operator in where clause with both indexed and non-indexed field.
     */
    @Test
    public void testWhereGreater() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result idxActual = executeFrom("SELECT firstName FROM Employee WHERE age > 30", node);
            BaseSqlTest.Result noidxActual = executeFrom("SELECT firstName FROM Employee WHERE salary > 75", node);
            IgniteCache<Object, Object> cache = node.cache(BaseSqlTest.EMP_CACHE_NAME);
            List<List<Object>> idxExp = BaseSqlTest.select(cache, ( row) -> ((Integer) (row.get("AGE"))) > 30, "firstName");
            List<List<Object>> noidxExp = BaseSqlTest.select(cache, ( row) -> ((Integer) (row.get("SALARY"))) > 75, "firstName");
            assertContainsEq(idxActual.values(), idxExp);
            assertContainsEq(noidxActual.values(), noidxExp);
        });
    }

    /**
     * Check less operator in where clause with both indexed and non-indexed field.
     */
    @Test
    public void testWhereLess() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result idxActual = executeFrom("SELECT firstName FROM Employee WHERE age < 30", node);
            BaseSqlTest.Result noidxActual = executeFrom("SELECT firstName FROM Employee WHERE salary < 75", node);
            IgniteCache<Object, Object> cache = node.cache(BaseSqlTest.EMP_CACHE_NAME);
            List<List<Object>> idxExp = BaseSqlTest.select(cache, ( row) -> ((Integer) (row.get("AGE"))) < 30, "firstName");
            List<List<Object>> noidxExp = BaseSqlTest.select(cache, ( row) -> ((Integer) (row.get("SALARY"))) < 75, "firstName");
            assertContainsEq(idxActual.values(), idxExp);
            assertContainsEq(noidxActual.values(), noidxExp);
        });
    }

    /**
     * Check equals operator in where clause with both indexed and non-indexed field.
     */
    @Test
    public void testWhereEq() {
        testAllNodes(( node) -> {
            BaseSqlTest.Result idxActual = executeFrom("SELECT firstName FROM Employee WHERE age = 30", node);
            BaseSqlTest.Result noidxActual = executeFrom("SELECT firstName FROM Employee WHERE salary = 75", node);
            IgniteCache<Object, Object> cache = node.cache(BaseSqlTest.EMP_CACHE_NAME);
            List<List<Object>> idxExp = BaseSqlTest.select(cache, ( row) -> ((Integer) (row.get("AGE"))) == 30, "firstName");
            List<List<Object>> noidxExp = BaseSqlTest.select(cache, ( row) -> ((Integer) (row.get("SALARY"))) == 75, "firstName");
            assertContainsEq(idxActual.values(), idxExp);
            assertContainsEq(noidxActual.values(), noidxExp);
        });
    }

    /**
     * Check GROUP BY operator with indexed field.
     */
    @Test
    public void testGroupByIndexedField() {
        testAllNodes(( node) -> {
            // Need to filter out only part of records (each one is a count of employees
            // of particular age) in HAVING clause.
            final int avgAge = ((int) ((BaseSqlTest.EMP_CNT) / (BaseSqlTest.AGES_CNT)));
            BaseSqlTest.Result result = executeFrom(("SELECT age, COUNT(*) FROM Employee GROUP BY age HAVING COUNT(*) > " + avgAge), node);
            List<List<Object>> all = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), null, "age");
            Map<Integer, Long> cntGroups = new HashMap<>();
            for (List<Object> entry : all) {
                Integer age = ((Integer) (entry.get(0)));
                long cnt = cntGroups.getOrDefault(age, 0L);
                cntGroups.put(age, (cnt + 1L));
            }
            List<List<Object>> expected = cntGroups.entrySet().stream().filter(( ent) -> (ent.getValue()) > avgAge).map(( ent) -> Arrays.<Object>asList(ent.getKey(), ent.getValue())).collect(Collectors.toList());
            assertContainsEq(result.values(), expected);
        });
    }

    /**
     * Check GROUP BY operator with indexed field.
     */
    @Test
    public void testGroupByNonIndexedField() {
        testAllNodes(( node) -> {
            // Need to filter out only part of records (each one is a count of employees
            // associated with particular department id) in HAVING clause.
            final int avgDep = ((int) (((BaseSqlTest.EMP_CNT) - (BaseSqlTest.FREE_EMP_CNT)) / ((BaseSqlTest.DEP_CNT) - (BaseSqlTest.FREE_DEP_CNT))));
            BaseSqlTest.Result result = executeFrom((("SELECT depId, COUNT(*) " + (("FROM Employee " + "GROUP BY depIdNoidx ") + "HAVING COUNT(*) > ")) + avgDep), node);
            List<List<Object>> all = BaseSqlTest.select(node.cache(BaseSqlTest.EMP_CACHE_NAME), null, "depId");
            Map<Long, Long> cntGroups = new HashMap<>();
            for (List<Object> entry : all) {
                Long depId = ((Long) (entry.get(0)));
                long cnt = cntGroups.getOrDefault(depId, 0L);
                cntGroups.put(depId, (cnt + 1L));
            }
            List<List<Object>> expected = cntGroups.entrySet().stream().filter(( ent) -> (ent.getValue()) > avgDep).map(( ent) -> Arrays.<Object>asList(ent.getKey(), ent.getValue())).collect(Collectors.toList());
            assertContainsEq(result.values(), expected);
        });
    }

    /**
     * Check INNER JOIN with collocated data.
     */
    @Test
    public void testInnerJoinEmployeeDepartment() {
        checkInnerJoinEmployeeDepartment(DEP_TAB);
    }

    /**
     * Check LEFT JOIN with collocated data.
     */
    @Test
    public void testLeftJoin() {
        checkLeftJoinEmployeeDepartment(DEP_TAB);
    }

    /**
     * Check RIGHT JOIN with collocated data.
     */
    @Test
    public void testRightJoin() {
        checkRightJoinEmployeeDepartment(DEP_TAB);
    }

    /**
     * Check that FULL OUTER JOIN (which is currently unsupported) causes valid error message.
     */
    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testFullOuterJoinIsNotSupported() {
        testAllNodes(( node) -> {
            String fullOuterJoinQry = "SELECT e.id as EmpId, e.firstName as EmpName, d.id as DepId, d.name as DepName " + ("FROM Employee e FULL OUTER JOIN Department d " + "ON e.depId = d.id");
            GridTestUtils.assertThrows(log, () -> executeFrom(fullOuterJoinQry, node), IgniteSQLException.class, "Failed to parse query.");
            String fullOuterJoinSubquery = ("SELECT EmpId from (" + fullOuterJoinQry) + ")";
            GridTestUtils.assertThrows(log, () -> executeFrom(fullOuterJoinSubquery, node), IgniteSQLException.class, "Failed to parse query.");
        });
    }

    /**
     * Check that distributed FULL OUTER JOIN (which is currently unsupported) causes valid error message.
     */
    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void testFullOuterDistributedJoinIsNotSupported() {
        testAllNodes(( node) -> {
            String qry = "SELECT d.id, d.name, a.address " + ("FROM Department d FULL OUTER JOIN Address a " + "ON d.idNoidx = a.depIdNoidx");
            GridTestUtils.assertThrows(log, () -> executeFrom(distributedJoinQry(false, qry), node), IgniteSQLException.class, "Failed to parse query.");
        });
    }
}

