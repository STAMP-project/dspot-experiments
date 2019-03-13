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
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class DoubleFlushModMod extends AbstractFlushTest {
    private Integer id;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        StrTestEntity fe = new StrTestEntity("x");
        em.persist(fe);
        em.flush();
        em.getTransaction().commit();
        // Revision 2
        em.getTransaction().begin();
        fe = em.find(StrTestEntity.class, fe.getId());
        fe.setStr("y");
        em.flush();
        fe.setStr("z");
        em.flush();
        em.getTransaction().commit();
        // 
        id = fe.getId();
    }

    @Test
    public void testRevisionsCounts() {
        assert Arrays.asList(1, 2).equals(getAuditReader().getRevisions(StrTestEntity.class, id));
    }

    @Test
    public void testHistoryOfId() {
        StrTestEntity ver1 = new StrTestEntity("x", id);
        StrTestEntity ver2 = new StrTestEntity("z", id);
        assert getAuditReader().find(StrTestEntity.class, id, 1).equals(ver1);
        assert getAuditReader().find(StrTestEntity.class, id, 2).equals(ver2);
    }

    @Test
    public void testRevisionTypes() {
        @SuppressWarnings({ "unchecked" })
        List<Object[]> results = getAuditReader().createQuery().forRevisionsOfEntity(StrTestEntity.class, false, true).add(AuditEntity.id().eq(id)).getResultList();
        Assert.assertEquals(results.get(0)[2], ADD);
        Assert.assertEquals(results.get(1)[2], MOD);
    }
}

