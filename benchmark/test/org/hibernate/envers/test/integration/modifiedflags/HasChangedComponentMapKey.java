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
import org.hibernate.envers.test.entities.components.Component1;
import org.hibernate.envers.test.entities.components.Component2;
import org.hibernate.envers.test.entities.components.ComponentTestEntity;
import org.hibernate.envers.test.integration.collection.mapkey.ComponentMapKeyEntity;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedComponentMapKey extends AbstractModifiedFlagsEntityTest {
    private Integer cmke_id;

    private Integer cte1_id;

    private Integer cte2_id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        ComponentMapKeyEntity imke = new ComponentMapKeyEntity();
        // Revision 1 (intialy 1 mapping)
        em.getTransaction().begin();
        ComponentTestEntity cte1 = new ComponentTestEntity(new Component1("x1", "y2"), new Component2("a1", "b2"));
        ComponentTestEntity cte2 = new ComponentTestEntity(new Component1("x1", "y2"), new Component2("a1", "b2"));
        em.persist(cte1);
        em.persist(cte2);
        imke.getIdmap().put(cte1.getComp1(), cte1);
        em.persist(imke);
        em.getTransaction().commit();
        // Revision 2 (sse1: adding 1 mapping)
        em.getTransaction().begin();
        cte2 = em.find(ComponentTestEntity.class, cte2.getId());
        imke = em.find(ComponentMapKeyEntity.class, imke.getId());
        imke.getIdmap().put(cte2.getComp1(), cte2);
        em.getTransaction().commit();
        // 
        cmke_id = imke.getId();
        cte1_id = cte1.getId();
        cte2_id = cte2.getId();
    }

    @Test
    public void testHasChangedMapEntity() throws Exception {
        List list = queryForPropertyHasChanged(ComponentMapKeyEntity.class, cmke_id, "idmap");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 2), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(ComponentMapKeyEntity.class, cmke_id, "idmap");
        Assert.assertEquals(0, list.size());
    }

    @Test
    public void testHasChangedComponentEntity() throws Exception {
        List list = queryForPropertyHasChanged(ComponentTestEntity.class, cte1_id, "comp1");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(ComponentTestEntity.class, cte1_id, "comp1");
        Assert.assertEquals(0, list.size());
        list = queryForPropertyHasChanged(ComponentTestEntity.class, cte2_id, "comp1");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(1), TestTools.extractRevisionNumbers(list));
        list = queryForPropertyHasNotChanged(ComponentTestEntity.class, cte2_id, "comp1");
        Assert.assertEquals(0, list.size());
    }
}

