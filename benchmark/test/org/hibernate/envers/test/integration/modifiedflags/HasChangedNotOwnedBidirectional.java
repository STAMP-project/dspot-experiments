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
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.inheritance.joined.notownedrelation.Address;
import org.hibernate.envers.test.integration.inheritance.joined.notownedrelation.PersonalContact;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedNotOwnedBidirectional extends AbstractModifiedFlagsEntityTest {
    private Long pc_id;

    private Long a1_id;

    private Long a2_id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        pc_id = 1L;
        a1_id = 10L;
        a2_id = 100L;
        // Rev 1
        em.getTransaction().begin();
        PersonalContact pc = new PersonalContact(pc_id, "e", "f");
        Address a1 = new Address(a1_id, "a1");
        a1.setContact(pc);
        em.persist(pc);
        em.persist(a1);
        em.getTransaction().commit();
        // Rev 2
        em.getTransaction().begin();
        pc = em.find(PersonalContact.class, pc_id);
        Address a2 = new Address(a2_id, "a2");
        a2.setContact(pc);
        em.persist(a2);
        em.getTransaction().commit();
    }

    @Test
    public void testReferencedEntityHasChanged() throws Exception {
        List list = queryForPropertyHasChanged(PersonalContact.class, pc_id, "addresses");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(Address.class, a1_id, "contact");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(Address.class, a2_id, "contact");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(2), TestTools.extractRevisionNumbers(list));
    }
}

