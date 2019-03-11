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
import org.hibernate.envers.test.integration.inheritance.joined.ChildEntity;
import org.hibernate.envers.test.integration.inheritance.joined.ParentEntity;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedChildAuditing extends AbstractModifiedFlagsEntityTest {
    private Integer id1;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        id1 = 1;
        // Rev 1
        em.getTransaction().begin();
        ChildEntity ce = new ChildEntity(id1, "x", 1L);
        em.persist(ce);
        em.getTransaction().commit();
        // Rev 2
        em.getTransaction().begin();
        ce = em.find(ChildEntity.class, id1);
        ce.setData("y");
        ce.setNumVal(2L);
        em.getTransaction().commit();
    }

    @Test
    public void testChildHasChanged() throws Exception {
        List list = queryForPropertyHasChanged(ChildEntity.class, id1, "data");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(ChildEntity.class, id1, "numVal");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(ChildEntity.class, id1, "data");
        Assert.assertEquals(0, list.size());
        list = queryForPropertyHasNotChanged(ChildEntity.class, id1, "numVal");
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testParentHasChanged() throws Exception {
        List list = queryForPropertyHasChanged(ParentEntity.class, id1, "data");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(ParentEntity.class, id1, "data");
        Assert.assertEquals(0, list.size());
    }
}

