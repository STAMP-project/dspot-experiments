/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.emops;


import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class RefreshTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testRefreshNonManaged() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        Race race = new Race();
        em.persist(race);
        em.flush();
        em.clear();
        try {
            em.refresh(race);
            Assert.fail("Refresh should fail on a non managed entity");
        } catch (IllegalArgumentException e) {
            // success
        }
        em.getTransaction().rollback();
        em.close();
    }
}

