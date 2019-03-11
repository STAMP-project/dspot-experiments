/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cdi.extended;


import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RunWith(Arquillian.class)
@Ignore("WildFly has not released a version supporting JPA 2.2 and CDI 2.0")
public class ConversationalPersistenceContextExtendedTest {
    @EJB
    private ConversationalEventManager eventManager;

    @PersistenceContext
    private EntityManager em;

    @Test
    @SuppressWarnings("unchecked")
    public void testIt() throws Exception {
        Event event = eventManager.saveEvent("Untold");
        Assert.assertEquals(0, ((Number) (em.createNativeQuery("select count(*) from Event").getSingleResult())).intValue());
        eventManager.endConversation();
        Assert.assertEquals(1, ((Number) (em.createNativeQuery("select count(*) from Event").getSingleResult())).intValue());
    }
}

