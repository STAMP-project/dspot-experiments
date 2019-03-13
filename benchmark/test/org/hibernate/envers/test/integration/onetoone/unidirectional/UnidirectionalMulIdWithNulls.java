/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.onetoone.unidirectional;


import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.ids.EmbId;
import org.hibernate.envers.test.entities.ids.EmbIdTestEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
public class UnidirectionalMulIdWithNulls extends BaseEnversJPAFunctionalTestCase {
    private EmbId ei;

    @Test
    @Priority(10)
    public void initData() {
        ei = new EmbId(1, 2);
        EntityManager em = getEntityManager();
        // Revision 1
        EmbIdTestEntity eite = new EmbIdTestEntity(ei, "data");
        UniRefIngMulIdEntity notNullRef = new UniRefIngMulIdEntity(1, "data 1", eite);
        UniRefIngMulIdEntity nullRef = new UniRefIngMulIdEntity(2, "data 2", null);
        em.getTransaction().begin();
        em.persist(eite);
        em.persist(notNullRef);
        em.persist(nullRef);
        em.getTransaction().commit();
    }

    @Test
    public void testNullReference() {
        UniRefIngMulIdEntity nullRef = getAuditReader().find(UniRefIngMulIdEntity.class, 2, 1);
        Assert.assertNull(nullRef.getReference());
    }

    @Test
    public void testNotNullReference() {
        EmbIdTestEntity eite = getAuditReader().find(EmbIdTestEntity.class, ei, 1);
        UniRefIngMulIdEntity notNullRef = getAuditReader().find(UniRefIngMulIdEntity.class, 1, 1);
        Assert.assertNotNull(notNullRef.getReference());
        Assert.assertEquals(notNullRef.getReference(), eite);
    }
}

