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
public class GetReferenceTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testWrongIdType() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        try {
            em.getReference(Competitor.class, "30");
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        } catch (Exception e) {
            Assert.fail(("Wrong exception: " + e));
        }
        try {
            em.getReference(Mail.class, 1);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // success
        } catch (Exception e) {
            Assert.fail(("Wrong exception: " + e));
        }
        em.close();
    }
}

