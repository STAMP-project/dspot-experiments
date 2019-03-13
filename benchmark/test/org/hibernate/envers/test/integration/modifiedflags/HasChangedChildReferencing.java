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
import org.hibernate.envers.test.integration.inheritance.joined.childrelation.ChildIngEntity;
import org.hibernate.envers.test.integration.inheritance.joined.childrelation.ReferencedEntity;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedChildReferencing extends AbstractModifiedFlagsEntityTest {
    private Integer re_id1;

    private Integer re_id2;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        re_id1 = 1;
        re_id2 = 10;
        Integer c_id = 100;
        // Rev 1
        em.getTransaction().begin();
        ReferencedEntity re1 = new ReferencedEntity(re_id1);
        em.persist(re1);
        ReferencedEntity re2 = new ReferencedEntity(re_id2);
        em.persist(re2);
        em.getTransaction().commit();
        // Rev 2
        em.getTransaction().begin();
        re1 = em.find(ReferencedEntity.class, re_id1);
        ChildIngEntity cie = new ChildIngEntity(c_id, "y", 1L);
        cie.setReferenced(re1);
        em.persist(cie);
        c_id = cie.getId();
        em.getTransaction().commit();
        // Rev 3
        em.getTransaction().begin();
        re2 = em.find(ReferencedEntity.class, re_id2);
        cie = em.find(ChildIngEntity.class, c_id);
        cie.setReferenced(re2);
        em.getTransaction().commit();
    }

    @Test
    public void testReferencedEntityHasChanged() throws Exception {
        List list = queryForPropertyHasChanged(ReferencedEntity.class, re_id1, "referencing");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(2, 3), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(ReferencedEntity.class, re_id1, "referencing");
        Assert.assertEquals(1, list.size());// initially referencing collection is null

        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(ReferencedEntity.class, re_id2, "referencing");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(3), TestTools.extractRevisionNumbers(list));
    }
}

