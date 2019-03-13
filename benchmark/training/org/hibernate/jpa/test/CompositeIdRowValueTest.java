package org.hibernate.jpa.test;


import DialectChecks.SupportsRowValueConstructorSyntaxCheck;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hamcrest.core.Is;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-9029")
@RequiresDialectFeature(SupportsRowValueConstructorSyntaxCheck.class)
public class CompositeIdRowValueTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testTupleAfterSubQuery() {
        EntityManager em = getOrCreateEntityManager();
        Query q = em.createQuery(("SELECT e FROM EntityWithCompositeId e " + ("WHERE EXISTS (SELECT 1 FROM EntityWithCompositeId) " + "AND e.id = :id")));
        q.setParameter("id", new CompositeId(1, 2));
        Assert.assertThat(q.getResultList().size(), Is.is(0));
    }
}

