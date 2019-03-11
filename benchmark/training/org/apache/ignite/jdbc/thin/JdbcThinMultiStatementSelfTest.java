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


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 * Tests for ddl queries that contain multiply sql statements, separated by ";".
 */
public class JdbcThinMultiStatementSelfTest extends GridCommonAbstractTest {
    /**
     * Assert that script containing both h2 and non h2 (native) sql statements is handled correctly.
     */
    @Test
    public void testMixedCommands() throws Exception {
        execute(("CREATE TABLE public.transactions (pk INT, id INT, k VARCHAR, v VARCHAR, PRIMARY KEY (pk, id)); " + ((("CREATE INDEX transactions_id_k_v ON public.transactions (id, k, v) INLINE_SIZE 150; " + "INSERT INTO public.transactions VALUES (1,2,'some', 'word') ; ") + "CREATE INDEX transactions_k_v_id ON public.transactions (k, v, id) INLINE_SIZE 150; ") + "CREATE INDEX transactions_pk_id ON public.transactions (pk, id) INLINE_SIZE 20;")));
    }

    /**
     * Sanity test for scripts, containing empty statements are handled correctly.
     */
    @Test
    public void testEmptyStatements() throws Exception {
        execute(";; ;;;;");
        execute(" ;; ;;;; ");
        execute(("CREATE TABLE ONE (id INT PRIMARY KEY, VAL VARCHAR);;" + ("CREATE INDEX T_IDX ON ONE(val)" + ";;UPDATE ONE SET VAL = 'SOME';;;  ")));
        execute(("DROP INDEX T_IDX ;;  ;;" + "UPDATE ONE SET VAL = 'SOME'"));
    }

    /**
     * Check multi-statement containing both h2 and native parser statements (having "?" args) works well.
     */
    @Test
    public void testMultiStatementTxWithParams() throws Exception {
        int leoAge = 28;
        String nickolas = "Nickolas";
        int gabAge = 84;
        String gabName = "Gab";
        int delYounger = 19;
        String complexQuery = "INSERT INTO TEST_TX VALUES (5, ?, 'Leo'); "// 1
         + (((((";;;;" + "BEGIN ; ") + "UPDATE TEST_TX  SET name = ? WHERE name = 'Nick' ;")// 2
         + "INSERT INTO TEST_TX VALUES (6, ?, ?); ")// 3, 4
         + "DELETE FROM TEST_TX WHERE age < ?; ")// 5
         + "COMMIT;");
        try (Connection c = GridTestUtils.connect(grid(0), null)) {
            try (PreparedStatement p = c.prepareStatement(complexQuery)) {
                p.setInt(1, leoAge);
                p.setString(2, nickolas);
                p.setInt(3, gabAge);
                p.setString(4, gabName);
                p.setInt(5, delYounger);
                assertFalse("Expected, that first result is an update count.", p.execute());
                assertTrue("Expected update count of the INSERT.", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of an empty statement.", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of an empty statement.", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of an empty statement.", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of an empty statement.", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of the BEGIN", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of the UPDATE", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of the INSERT", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of the DELETE", ((p.getUpdateCount()) != (-1)));
                assertTrue("Expected update count of the COMMIT", ((p.getUpdateCount()) != (-1)));
                assertFalse("There should have been no results.", p.getMoreResults());
                assertFalse("There should have been no update results.", ((p.getUpdateCount()) != (-1)));
            }
            try (PreparedStatement sel = c.prepareStatement("SELECT * FROM TEST_TX ORDER BY ID;")) {
                try (ResultSet pers = sel.executeQuery()) {
                    assertTrue(pers.next());
                    assertEquals(43, JdbcThinMultiStatementSelfTest.age(pers));
                    assertEquals("Valery", JdbcThinMultiStatementSelfTest.name(pers));
                    assertTrue(pers.next());
                    assertEquals(25, JdbcThinMultiStatementSelfTest.age(pers));
                    assertEquals("Michel", JdbcThinMultiStatementSelfTest.name(pers));
                    assertTrue(pers.next());
                    assertEquals(19, JdbcThinMultiStatementSelfTest.age(pers));
                    assertEquals("Nickolas", JdbcThinMultiStatementSelfTest.name(pers));
                    assertTrue(pers.next());
                    assertEquals(28, JdbcThinMultiStatementSelfTest.age(pers));
                    assertEquals("Leo", JdbcThinMultiStatementSelfTest.name(pers));
                    assertTrue(pers.next());
                    assertEquals(84, JdbcThinMultiStatementSelfTest.age(pers));
                    assertEquals("Gab", JdbcThinMultiStatementSelfTest.name(pers));
                    assertFalse(pers.next());
                }
            }
        }
    }
}

