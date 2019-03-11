/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.basic;


import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Tomasz Dziurko (tdziurko at gmail dot com)
 */
public class TransactionRollbackBehaviour extends BaseEnversJPAFunctionalTestCase {
    @Test
    public void testAuditRecordsRollbackWithAutoClear() {
        testAuditRecordsRollbackBehavior(false, true);
    }

    @Test
    public void testAuditRecordsRollbackWithNoAutoClear() {
        testAuditRecordsRollbackBehavior(false, false);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8189")
    public void testFlushedAuditRecordsRollback() {
        // default auto-clear behavior
        testAuditRecordsRollbackBehavior(true, null);
    }
}

