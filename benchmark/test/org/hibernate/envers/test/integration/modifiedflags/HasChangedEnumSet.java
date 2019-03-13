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
import org.hibernate.envers.test.entities.collection.EnumSetEntity;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;

import static org.hibernate.envers.test.entities.collection.EnumSetEntity.E1.X;
import static org.hibernate.envers.test.entities.collection.EnumSetEntity.E1.Y;
import static org.hibernate.envers.test.entities.collection.EnumSetEntity.E2.A;
import static org.hibernate.envers.test.entities.collection.EnumSetEntity.E2.B;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedEnumSet extends AbstractModifiedFlagsEntityTest {
    private Integer sse1_id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        EnumSetEntity sse1 = new EnumSetEntity();
        // Revision 1 (sse1: initialy 1 element)
        em.getTransaction().begin();
        sse1.getEnums1().add(X);
        sse1.getEnums2().add(A);
        em.persist(sse1);
        em.getTransaction().commit();
        // Revision 2 (sse1: adding 1 element/removing a non-existing element)
        em.getTransaction().begin();
        sse1 = em.find(EnumSetEntity.class, sse1.getId());
        sse1.getEnums1().add(Y);
        sse1.getEnums2().remove(B);
        em.getTransaction().commit();
        // Revision 3 (sse1: removing 1 element/adding an exisiting element)
        em.getTransaction().begin();
        sse1 = em.find(EnumSetEntity.class, sse1.getId());
        sse1.getEnums1().remove(X);
        sse1.getEnums2().add(A);
        em.getTransaction().commit();
        // 
        sse1_id = sse1.getId();
    }

    @Test
    public void testHasChanged() throws Exception {
        List list = queryForPropertyHasChanged(EnumSetEntity.class, sse1_id, "enums1");
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2, 3), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasChanged(EnumSetEntity.class, sse1_id, "enums2");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(EnumSetEntity.class, sse1_id, "enums1");
        Assert.assertEquals(0, list.size());
        list = queryForPropertyHasNotChanged(EnumSetEntity.class, sse1_id, "enums2");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(2, 3), TestTools.extractRevisionNumbers(list));
    }
}

