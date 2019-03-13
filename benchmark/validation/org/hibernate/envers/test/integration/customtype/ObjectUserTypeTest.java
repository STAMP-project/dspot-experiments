/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.customtype;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7870")
@RequiresDialect(Oracle8iDialect.class)
public class ObjectUserTypeTest extends BaseEnversJPAFunctionalTestCase {
    private int id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        // Revision 1 - add
        em.getTransaction().begin();
        ObjectUserTypeEntity entity = new ObjectUserTypeEntity("builtInType1", "stringUserType1");
        em.persist(entity);
        em.getTransaction().commit();
        id = entity.getId();
        // Revision 2 - modify
        em.getTransaction().begin();
        entity = em.find(ObjectUserTypeEntity.class, entity.getId());
        entity.setUserType(2);
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 3 - remove
        em.getTransaction().begin();
        entity = em.find(ObjectUserTypeEntity.class, entity.getId());
        em.remove(entity);
        em.getTransaction().commit();
        em.close();
    }

    @Test
    public void testRevisionCount() {
        Assert.assertEquals(Arrays.asList(1, 2, 3), getAuditReader().getRevisions(ObjectUserTypeEntity.class, id));
    }

    @Test
    public void testHistory() {
        ObjectUserTypeEntity ver1 = new ObjectUserTypeEntity(id, "builtInType1", "stringUserType1");
        ObjectUserTypeEntity ver2 = new ObjectUserTypeEntity(id, "builtInType1", 2);
        Assert.assertEquals(ver1, getAuditReader().find(ObjectUserTypeEntity.class, id, 1));
        Assert.assertEquals(ver2, getAuditReader().find(ObjectUserTypeEntity.class, id, 2));
        Assert.assertEquals(ver2, getAuditReader().createQuery().forRevisionsOfEntity(ObjectUserTypeEntity.class, true, true).getResultList().get(2));// Checking delete state.

    }
}

