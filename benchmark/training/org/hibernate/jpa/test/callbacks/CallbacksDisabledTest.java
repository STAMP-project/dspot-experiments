/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.callbacks;


import java.util.Date;
import javax.persistence.EntityManager;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.jpa.test.Cat;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sanne Grinovero
 */
@SuppressWarnings("unchecked")
public class CallbacksDisabledTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testCallbacksAreDisabled() throws Exception {
        EntityManager em = getOrCreateEntityManager();
        Cat c = new Cat();
        c.setName("Kitty");
        c.setDateOfBirth(new Date(90, 11, 15));
        em.getTransaction().begin();
        em.persist(c);
        em.getTransaction().commit();
        em.clear();
        em.getTransaction().begin();
        c = em.find(Cat.class, c.getId());
        Assert.assertTrue(((c.getAge()) == 0));// With listeners enabled this would be false. Proven by org.hibernate.jpa.test.callbacks.CallbacksTest.testCallbackMethod

        em.getTransaction().commit();
        em.close();
    }
}

