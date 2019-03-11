/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.jpa.test;


import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class NamedQueryTransactionFailureTest extends BaseEntityManagerFunctionalTestCase {
    private TransactionCoordinator transactionCoordinator;

    @Test
    @TestForIssue(jiraKey = "HHH-11997")
    public void testNamedQueryWithTransactionSynchStatus() {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                try {
                    Mockito.reset(transactionCoordinator);
                    doThrow(.class).when(transactionCoordinator).pulse();
                    entityManager.createNamedQuery("NamedQuery");
                } catch ( e) {
                    assertEquals(.class, e.getClass());
                    assertEquals(.class, e.getCause().getClass());
                }
            });
        } catch (Exception ignore) {
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11997")
    public void testNamedQueryWithMarkForRollbackOnlyFailure() {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                try {
                    Mockito.reset(transactionCoordinator);
                    doNothing().doThrow(.class).when(transactionCoordinator).pulse();
                    entityManager.createNamedQuery("NamedQuery");
                } catch ( e) {
                    assertEquals(.class, e.getClass());
                    assertEquals(.class, e.getCause().getClass());
                }
            });
        } catch (Exception ignore) {
        }
    }
}

