/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.collection.EnumMapEntity;
import org.hibernate.envers.test.entities.collection.EnumMapType;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;

import static org.hibernate.envers.test.entities.collection.EnumMapEntity.EnumType.TYPE_A;
import static org.hibernate.envers.test.entities.collection.EnumMapEntity.EnumType.TYPE_B;
import static org.hibernate.envers.test.entities.collection.EnumMapEntity.EnumType.TYPE_C;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-6374")
public class EnumMapTest extends BaseEnversJPAFunctionalTestCase {
    private Integer entityId;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getOrCreateEntityManager();
        try {
            // revision 1
            EnumMapEntity entity = new EnumMapEntity();
            entity.getTypes().put(TYPE_A, new EnumMapType("A"));
            entity.getTypes().put(TYPE_B, new EnumMapType("B"));
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            // revision 2
            em.getTransaction().begin();
            entity = em.find(EnumMapEntity.class, entity.getId());
            entity.getTypes().remove(TYPE_A);
            entity.getTypes().put(TYPE_C, new EnumMapType("C"));
            em.getTransaction().commit();
            entityId = entity.getId();
        } finally {
            em.close();
        }
    }

    @Test
    public void testRevisionsCount() {
        Assert.assertEquals(2, getAuditReader().getRevisions(EnumMapEntity.class, entityId).size());
    }

    @Test
    public void testAuditEnumMapCollection() {
        EnumMapEntity rev1 = getAuditReader().find(EnumMapEntity.class, entityId, 1);
        Assert.assertTrue(rev1.getTypes().keySet().containsAll(Arrays.asList(TYPE_A, TYPE_B)));
        EnumMapEntity rev2 = getAuditReader().find(EnumMapEntity.class, entityId, 2);
        Assert.assertTrue(rev2.getTypes().keySet().containsAll(Arrays.asList(TYPE_B, TYPE_C)));
    }
}

