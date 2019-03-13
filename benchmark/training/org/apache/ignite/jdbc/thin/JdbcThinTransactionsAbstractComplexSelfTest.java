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
package org.apache.ignite.jdbc.thin;


import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteException;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test to check various transactional scenarios.
 */
public abstract class JdbcThinTransactionsAbstractComplexSelfTest extends JdbcThinAbstractSelfTest {
    /**
     * Client node index.
     */
    static final int CLI_IDX = 1;

    /**
     * Closure to perform ordinary delete after repeatable read.
     */
    private final IgniteInClosure<Connection> afterReadDel = new IgniteInClosure<Connection>() {
        @Override
        public void apply(Connection conn) {
            execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
        }
    };

    /**
     * Closure to perform fast delete after repeatable read.
     */
    private final IgniteInClosure<Connection> afterReadFastDel = new IgniteInClosure<Connection>() {
        @Override
        public void apply(Connection conn) {
            execute(conn, "DELETE FROM \"Person\".Person where id = 1");
        }
    };

    /**
     * Closure to perform ordinary update after repeatable read.
     */
    private final IgniteInClosure<Connection> afterReadUpdate = new IgniteInClosure<Connection>() {
        @Override
        public void apply(Connection conn) {
            execute(conn, "UPDATE \"Person\".Person set firstname = \'Joe\' where firstname = \'John\'");
        }
    };

    /**
     * Closure to perform ordinary delete and rollback after repeatable read.
     */
    private final IgniteInClosure<Connection> afterReadDelAndRollback = new IgniteInClosure<Connection>() {
        @Override
        public void apply(Connection conn) {
            execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
            rollback(conn);
        }
    };

    /**
     * Closure to perform fast delete after repeatable read.
     */
    private final IgniteInClosure<Connection> afterReadFastDelAndRollback = new IgniteInClosure<Connection>() {
        @Override
        public void apply(Connection conn) {
            execute(conn, "DELETE FROM \"Person\".Person where id = 1");
            rollback(conn);
        }
    };

    /**
     * Closure to perform ordinary update and rollback after repeatable read.
     */
    private final IgniteInClosure<Connection> afterReadUpdateAndRollback = new IgniteInClosure<Connection>() {
        @Override
        public void apply(Connection conn) {
            execute(conn, "UPDATE \"Person\".Person set firstname = \'Joe\' where firstname = \'John\'");
            rollback(conn);
        }
    };

    /**
     *
     */
    @Test
    public void testSingleDmlStatement() throws SQLException {
        insertPerson(6, "John", "Doe", 2, 2);
        assertEquals(Collections.singletonList(JdbcThinTransactionsAbstractComplexSelfTest.l(6, "John", "Doe", 2, 2)), execute("SELECT * FROM \"Person\".Person where id = 6"));
    }

    /**
     *
     */
    @Test
    public void testMultipleDmlStatements() throws SQLException {
        executeInTransaction(new JdbcThinTransactionsAbstractComplexSelfTest.TransactionClosure() {
            @Override
            public void apply(Connection conn) {
                insertPerson(conn, 6, "John", "Doe", 2, 2);
                // https://issues.apache.org/jira/browse/IGNITE-6938 - we can only see results of
                // UPDATE of what we have not inserted ourselves.
                execute(conn, "UPDATE \"Person\".person SET lastname = \'Jameson\' where lastname = \'Jules\'");
                execute(conn, "DELETE FROM \"Person\".person where id = 5");
            }
        });
        assertEquals(JdbcThinTransactionsAbstractComplexSelfTest.l(JdbcThinTransactionsAbstractComplexSelfTest.l(3, "Sam", "Jameson", 2, 2), JdbcThinTransactionsAbstractComplexSelfTest.l(6, "John", "Doe", 2, 2)), execute("SELECT * FROM \"Person\".Person where id = 3 or id >= 5 order by id"));
    }

    /**
     *
     */
    @Test
    public void testBatchDmlStatements() throws SQLException {
        doBatchedInsert();
        assertEquals(JdbcThinTransactionsAbstractComplexSelfTest.l(JdbcThinTransactionsAbstractComplexSelfTest.l(6, "John", "Doe", 2, 2), JdbcThinTransactionsAbstractComplexSelfTest.l(7, "Mary", "Lee", 1, 3)), execute("SELECT * FROM \"Person\".Person where id > 5 order by id"));
    }

    /**
     *
     */
    @Test
    public void testBatchDmlStatementsIntermediateFailure() throws SQLException {
        insertPerson(6, "John", "Doe", 2, 2);
        IgniteException e = ((IgniteException) (GridTestUtils.assertThrows(null, new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                doBatchedInsert();
                return null;
            }
        }, IgniteException.class, ("Duplicate key during INSERT [key=KeyCacheObjectImpl " + "[part=6, val=6, hasValBytes=true]]"))));
        assertTrue(((e.getCause()) instanceof BatchUpdateException));
        assertEquals(IgniteQueryErrorCode.DUPLICATE_KEY, ((BatchUpdateException) (e.getCause())).getErrorCode());
        assertTrue(e.getCause().getMessage().contains(("Duplicate key during INSERT [key=KeyCacheObjectImpl " + "[part=6, val=6, hasValBytes=true]]")));
        // First we insert id 7, then 6. Still, 7 is not in the cache as long as the whole batch has failed inside tx.
        assertEquals(Collections.emptyList(), execute("SELECT * FROM \"Person\".Person where id > 6 order by id"));
    }

    /**
     *
     */
    @Test
    public void testInsertAndQueryMultipleCaches() throws SQLException {
        executeInTransaction(new JdbcThinTransactionsAbstractComplexSelfTest.TransactionClosure() {
            @Override
            public void apply(Connection conn) {
                insertCity(conn, 5, "St Petersburg", 6000);
                insertCompany(conn, 6, "VK", 5);
                insertPerson(conn, 6, "Peter", "Sergeev", 5, 6);
            }
        });
        try (Connection c = connect("distributedJoins=true")) {
            assertEquals(JdbcThinTransactionsAbstractComplexSelfTest.l(JdbcThinTransactionsAbstractComplexSelfTest.l(5, "St Petersburg", 6000, 6, 5, "VK", 6, "Peter", "Sergeev", 5, 6)), execute(c, ("SELECT * FROM City left join Company on City.id = Company.\"cityid\" " + "left join \"Person\".Person p on City.id = p.cityid WHERE p.id = 6 or company.id = 6")));
        }
    }

    /**
     *
     */
    @Test
    public void testColocatedJoinSelectAndInsertInTransaction() throws SQLException {
        // We'd like to put some Google into cities with over 1K population which don't have it yet
        executeInTransaction(new JdbcThinTransactionsAbstractComplexSelfTest.TransactionClosure() {
            @Override
            public void apply(Connection conn) {
                List<Integer> ids = JdbcThinTransactionsAbstractComplexSelfTest.flat(execute(conn, ("SELECT distinct City.id from City left join Company c on " + "City.id = c.\"cityid\" where population >= 1000 and c.name <> \'Google\' order by City.id")));
                assertEqualsCollections(JdbcThinTransactionsAbstractComplexSelfTest.l(1, 2), ids);
                int i = 5;
                for (int l : ids)
                    insertCompany(conn, (++i), "Google", l);

            }
        });
        assertEqualsCollections(JdbcThinTransactionsAbstractComplexSelfTest.l("Los Angeles", "Seattle", "New York"), JdbcThinTransactionsAbstractComplexSelfTest.flat(execute(("SELECT City.name from City " + "left join Company c on city.id = c.\"cityid\" WHERE c.name = \'Google\' order by City.id"))));
    }

    /**
     *
     */
    @Test
    public void testDistributedJoinSelectAndInsertInTransaction() throws SQLException {
        try (Connection c = connect("distributedJoins=true")) {
            // We'd like to put some Google into cities with over 1K population which don't have it yet
            executeInTransaction(c, new JdbcThinTransactionsAbstractComplexSelfTest.TransactionClosure() {
                @Override
                public void apply(Connection conn) {
                    List<?> res = JdbcThinTransactionsAbstractComplexSelfTest.flat(execute(conn, ("SELECT p.id,p.name,c.id from Company c left join Product p on " + ("c.id = p.companyid left join City on city.id = c.\"cityid\" WHERE c.name <> \'Microsoft\' " + "and population < 1000"))));
                    assertEqualsCollections(JdbcThinTransactionsAbstractComplexSelfTest.l(3, "Mac", 5), res);
                    insertProduct(conn, 4, ((String) (res.get(1))), 1);
                }
            });
        }
        try (Connection c = connect("distributedJoins=true")) {
            assertEqualsCollections(JdbcThinTransactionsAbstractComplexSelfTest.l("Windows", "Mac"), JdbcThinTransactionsAbstractComplexSelfTest.flat(execute(c, ("SELECT p.name from Company c left join " + "Product p on c.id = p.companyid WHERE c.name = 'Microsoft' order by p.id"))));
        }
    }

    /**
     *
     */
    @Test
    public void testInsertFromExpression() throws SQLException {
        executeInTransaction(new JdbcThinTransactionsAbstractComplexSelfTest.TransactionClosure() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "insert into city (id, name, population) values (? + 1, ?, ?)", 8, "Moscow", 15000);
            }
        });
    }

    /**
     *
     */
    @Test
    public void testAutoRollback() throws SQLException {
        try (Connection c = connect()) {
            begin(c);
            insertPerson(c, 6, "John", "Doe", 2, 2);
        }
        // Connection has not hung on close and update has not been applied.
        assertTrue(personCache().query(new SqlFieldsQuery("SELECT * FROM \"Person\".Person WHERE id = 6")).getAll().isEmpty());
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadWithConcurrentDelete() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
            }
        }, null);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadWithConcurrentFastDelete() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where id = 1");
            }
        }, null);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadWithConcurrentCacheRemove() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                personCache().remove(1);
            }
        }, null);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndDeleteWithConcurrentDelete() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
            }
        }, afterReadDel);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndDeleteWithConcurrentFastDelete() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where id = 1");
            }
        }, afterReadDel);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndDeleteWithConcurrentCacheRemove() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                personCache().remove(1);
            }
        }, afterReadDel);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndFastDeleteWithConcurrentDelete() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
            }
        }, afterReadFastDel);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndFastDeleteWithConcurrentFastDelete() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where id = 1");
            }
        }, afterReadFastDel);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndFastDeleteWithConcurrentCacheRemove() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                personCache().remove(1);
            }
        }, afterReadFastDel);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndDeleteWithConcurrentDeleteAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
            }
        }, afterReadDelAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndDeleteWithConcurrentFastDeleteAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where id = 1");
            }
        }, afterReadDelAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndDeleteWithConcurrentCacheRemoveAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                personCache().remove(1);
            }
        }, afterReadDelAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndFastDeleteWithConcurrentDeleteAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where firstname = \'John\'");
            }
        }, afterReadFastDelAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndFastDeleteWithConcurrentFastDeleteAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "DELETE FROM \"Person\".Person where id = 1");
            }
        }, afterReadFastDelAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndFastDeleteWithConcurrentCacheRemoveAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                personCache().remove(1);
            }
        }, afterReadFastDelAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadWithConcurrentUpdate() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "UPDATE \"Person\".Person SET lastname = \'Fix\' where firstname = \'John\'");
            }
        }, null);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadWithConcurrentCacheReplace() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                JdbcThinTransactionsAbstractComplexSelfTest.Person p = new JdbcThinTransactionsAbstractComplexSelfTest.Person();
                p.id = 1;
                p.firstName = "Luke";
                p.lastName = "Maxwell";
                personCache().replace(1, p);
            }
        }, null);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndUpdateWithConcurrentUpdate() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "UPDATE \"Person\".Person SET lastname = \'Fix\' where firstname = \'John\'");
            }
        }, afterReadUpdate);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndUpdateWithConcurrentCacheReplace() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                JdbcThinTransactionsAbstractComplexSelfTest.Person p = new JdbcThinTransactionsAbstractComplexSelfTest.Person();
                p.id = 1;
                p.firstName = "Luke";
                p.lastName = "Maxwell";
                personCache().replace(1, p);
            }
        }, afterReadUpdate);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndUpdateWithConcurrentUpdateAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                execute(conn, "UPDATE \"Person\".Person SET lastname = \'Fix\' where firstname = \'John\'");
            }
        }, afterReadUpdateAndRollback);
    }

    /**
     *
     */
    @Test
    public void testRepeatableReadAndUpdateWithConcurrentCacheReplaceAndRollback() throws Exception {
        doTestRepeatableRead(new IgniteInClosure<Connection>() {
            @Override
            public void apply(Connection conn) {
                JdbcThinTransactionsAbstractComplexSelfTest.Person p = new JdbcThinTransactionsAbstractComplexSelfTest.Person();
                p.id = 1;
                p.firstName = "Luke";
                p.lastName = "Maxwell";
                personCache().replace(1, p);
            }
        }, afterReadUpdateAndRollback);
    }

    /**
     * Person class.
     */
    private static final class Person {
        /**
         *
         */
        @QuerySqlField
        public int id;

        /**
         *
         */
        @QuerySqlField
        public String firstName;

        /**
         *
         */
        @QuerySqlField
        public String lastName;
    }

    /**
     * Closure to be executed in scope of a transaction.
     */
    // No-op.
    private abstract static class TransactionClosure implements IgniteInClosure<Connection> {}
}

