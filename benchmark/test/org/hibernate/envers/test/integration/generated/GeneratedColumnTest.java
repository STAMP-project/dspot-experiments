/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.generated;


import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-10841")
public class GeneratedColumnTest extends BaseEnversJPAFunctionalTestCase {
    private Integer entityId;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager entityManager = getOrCreateEntityManager();
        try {
            // Revision 1
            SimpleEntity se = new SimpleEntity();
            se.setData("data");
            entityManager.getTransaction().begin();
            entityManager.persist(se);
            entityManager.getTransaction().commit();
            entityManager.clear();
            entityId = se.getId();
            // Revision 2
            entityManager.getTransaction().begin();
            se = entityManager.find(SimpleEntity.class, se.getId());
            se.setData("data2");
            entityManager.merge(se);
            entityManager.getTransaction().commit();
            // Revision 3
            entityManager.getTransaction().begin();
            se = entityManager.find(SimpleEntity.class, se.getId());
            entityManager.remove(se);
            entityManager.getTransaction().commit();
        } finally {
            entityManager.close();
        }
    }

    @Test
    public void getRevisionCounts() {
        Assert.assertEquals(3, getAuditReader().getRevisions(SimpleEntity.class, entityId).size());
    }

    @Test
    public void testRevisionHistory() {
        // revision - insertion
        final SimpleEntity rev1 = getAuditReader().find(SimpleEntity.class, entityId, 1);
        Assert.assertEquals("data", rev1.getData());
        Assert.assertEquals(1, rev1.getCaseNumberInsert());
        // revision - update
        final SimpleEntity rev2 = getAuditReader().find(SimpleEntity.class, entityId, 2);
        Assert.assertEquals("data2", rev2.getData());
        Assert.assertEquals(1, rev2.getCaseNumberInsert());
        // revision - deletion
        final SimpleEntity rev3 = getAuditReader().find(SimpleEntity.class, entityId, 3);
        Assert.assertEquals("data2", rev2.getData());
        Assert.assertEquals(1, rev2.getCaseNumberInsert());
    }
}

