/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.jpa.test.factory.puUtil;


import java.io.Serializable;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class GetIdentifierTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void getIdentifierTest() throws Exception {
        EntityManager entityManager = getOrCreateEntityManager();
        entityManager.getTransaction().begin();
        // This gives a NullPointerException right now. Look at HHH-10623 when this issue is fixed
        Serializable nestedLegacyEntityId = ((Serializable) (entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(createExisitingNestedLegacyEntity())));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Test
    public void getIdentifierOfNonEntityTest() {
        try {
            entityManagerFactory().getPersistenceUnitUtil().getIdentifier(this);
            Assert.fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Test
    public void getIdentifierOfNullTest() {
        try {
            entityManagerFactory().getPersistenceUnitUtil().getIdentifier(null);
            Assert.fail("should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }
}

