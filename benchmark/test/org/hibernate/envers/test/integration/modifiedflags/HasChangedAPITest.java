/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import java.util.List;
import javax.persistence.EntityManager;
import junit.framework.Assert;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.auditReader.AuditedTestEntity;
import org.hibernate.envers.test.integration.auditReader.NotAuditedTestEntity;
import org.junit.Test;


/**
 * A test which checks the correct behavior of AuditReader.isEntityClassAudited(Class entityClass).
 *
 * @author Hernan Chanfreau
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedAPITest extends AbstractModifiedFlagsEntityTest {
    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        AuditedTestEntity ent1 = new AuditedTestEntity(1, "str1");
        NotAuditedTestEntity ent2 = new NotAuditedTestEntity(1, "str1");
        em.persist(ent1);
        em.persist(ent2);
        em.getTransaction().commit();
        em.getTransaction().begin();
        ent1 = em.find(AuditedTestEntity.class, 1);
        ent2 = em.find(NotAuditedTestEntity.class, 1);
        ent1.setStr1("str2");
        ent2.setStr1("str2");
        em.getTransaction().commit();
    }

    @Test
    public void testHasChangedHasNotChangedCriteria() throws Exception {
        List list = getAuditReader().createQuery().forRevisionsOfEntity(AuditedTestEntity.class, true, true).add(AuditEntity.property("str1").hasChanged()).getResultList();
        Assert.assertEquals(2, list.size());
        Assert.assertEquals("str1", ((AuditedTestEntity) (list.get(0))).getStr1());
        Assert.assertEquals("str2", ((AuditedTestEntity) (list.get(1))).getStr1());
        list = getAuditReader().createQuery().forRevisionsOfEntity(AuditedTestEntity.class, true, true).add(AuditEntity.property("str1").hasNotChanged()).getResultList();
        Assert.assertTrue(list.isEmpty());
    }
}

