/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.jta;


import TestingJtaPlatformImpl.INSTANCE;
import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.IntTestEntity;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.jta.TestingJtaPlatformImpl;
import org.junit.Assert;
import org.junit.Test;


/**
 * Simple test that checks that Envers can still perform its beforeTransactionCompletion
 * callbacks successfully even if the Hibernate Session/EntityManager has been closed
 * prior to the JTA transaction commit.
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-11232")
public class JtaSessionClosedBeforeCommitTest extends BaseEnversJPAFunctionalTestCase {
    private Integer entityId;

    @Test
    @Priority(10)
    public void initData() throws Exception {
        INSTANCE.getTransactionManager().begin();
        EntityManager entityManager = getEntityManager();
        try {
            IntTestEntity ite = new IntTestEntity(10);
            entityManager.persist(ite);
            entityId = ite.getId();
            // simulates spring JtaTransactionManager.triggerBeforeCompletion()
            // this closes the entity manager prior to the JTA transaction.
            entityManager.close();
        } finally {
            TestingJtaPlatformImpl.tryCommit();
        }
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(IntTestEntity.class, entityId));
    }

    @Test
    public void testRevisionHistory() {
        Assert.assertEquals(new IntTestEntity(10, entityId), getAuditReader().find(IntTestEntity.class, entityId, 1));
    }
}

