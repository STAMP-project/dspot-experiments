/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.flush;


import RevisionType.ADD;
import RevisionType.MOD;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-8243")
public class CommitFlush extends AbstractFlushTest {
    private Integer id = null;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        // Revision 1
        em.getTransaction().begin();
        StrTestEntity entity = new StrTestEntity("x");
        em.persist(entity);
        em.getTransaction().commit();
        id = entity.getId();
        // Revision 2
        em.getTransaction().begin();
        entity = em.find(StrTestEntity.class, entity.getId());
        entity.setStr("y");
        entity = em.merge(entity);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(StrTestEntity.class, id));
    }

    @Test
    public void testHistoryOfId() {
        StrTestEntity ver1 = new StrTestEntity("x", id);
        StrTestEntity ver2 = new StrTestEntity("y", id);
        Assert.assertEquals(ver1, getAuditReader().find(StrTestEntity.class, id, 1));
        Assert.assertEquals(ver2, getAuditReader().find(StrTestEntity.class, id, 2));
    }

    @Test
    public void testCurrent() {
        Assert.assertEquals(new StrTestEntity("y", id), getEntityManager().find(StrTestEntity.class, id));
    }

    @Test
    public void testRevisionTypes() {
        List<Object[]> results = getAuditReader().createQuery().forRevisionsOfEntity(StrTestEntity.class, false, true).add(AuditEntity.id().eq(id)).getResultList();
        Assert.assertEquals(results.get(0)[2], ADD);
        Assert.assertEquals(results.get(1)[2], MOD);
    }
}

