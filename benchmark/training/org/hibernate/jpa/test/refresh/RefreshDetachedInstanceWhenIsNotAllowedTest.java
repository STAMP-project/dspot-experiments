/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.refresh;


import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class RefreshDetachedInstanceWhenIsNotAllowedTest extends BaseEntityManagerFunctionalTestCase {
    private TestEntity testEntity;

    @Test(expected = IllegalArgumentException.class)
    public void testUnwrappedSessionRefreshDetachedInstance() {
        final EntityManager entityManager = createEntityManager();
        final Session session = entityManager.unwrap(Session.class);
        session.refresh(testEntity);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRefreshDetachedInstance() {
        final EntityManager entityManager = createEntityManager();
        entityManager.refresh(testEntity);
    }
}

