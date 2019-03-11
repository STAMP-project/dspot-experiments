/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.dialect;


import org.hibernate.dialect.pagination.Informix10LimitHandler;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


public class InformixLimitHandlerTestCase extends BaseNonConfigCoreFunctionalTestCase {
    private Informix10LimitHandler informixLimitHandler;

    private final String TEST_SQL = "SELECT field FROM table";

    @Test
    @TestForIssue(jiraKey = "HHH-11509")
    public void testCorrectLimit() {
        assertLimitHandlerEquals("SELECT FIRST 10 field FROM table", 0, 10);
        assertLimitHandlerEquals("SELECT SKIP 3 FIRST 5 field FROM table", 3, 5);
        assertLimitHandlerEquals("SELECT SKIP 10 FIRST 5 field FROM table", 10, 5);
        assertLimitHandlerEquals("SELECT SKIP 55 FIRST 12 field FROM table", 55, 12);
    }
}

