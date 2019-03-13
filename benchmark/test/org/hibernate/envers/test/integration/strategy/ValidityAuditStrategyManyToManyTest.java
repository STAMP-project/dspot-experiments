/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.strategy;


import java.util.Arrays;
import java.util.HashSet;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.manytomany.SetOwnedEntity;
import org.hibernate.envers.test.entities.manytomany.SetOwningEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the ValidityAuditStrategy on many-to-many Sets.
 * It was first introduced because of a bug when adding and removing the same element
 * from the set multiple times between database persists.
 * Created on: 24.05.11
 *
 * @author Oliver Lorenz
 * @since 3.6.5
 */
public class ValidityAuditStrategyManyToManyTest extends BaseEnversJPAFunctionalTestCase {
    private Integer ing_id;

    private Integer ed_id;

    @Test
    @Priority(10)
    public void initData() {
        final EntityManager em = getEntityManager();
        final SetOwningEntity setOwningEntity = new SetOwningEntity(1, "parent");
        final SetOwnedEntity setOwnedEntity = new SetOwnedEntity(2, "child");
        // Revision 1: Initial persist
        em.getTransaction().begin();
        em.persist(setOwningEntity);
        em.persist(setOwnedEntity);
        em.getTransaction().commit();
        em.clear();
        ing_id = setOwningEntity.getId();
        ed_id = setOwnedEntity.getId();
    }

    @Test
    @Priority(5)
    public void testMultipleAddAndRemove() {
        final EntityManager em = getEntityManager();
        // Revision 2: add child for first time
        em.getTransaction().begin();
        SetOwningEntity owningEntity = getEntityManager().find(SetOwningEntity.class, ing_id);
        SetOwnedEntity ownedEntity = getEntityManager().find(SetOwnedEntity.class, ed_id);
        owningEntity.setReferences(new HashSet<SetOwnedEntity>());
        owningEntity.getReferences().add(ownedEntity);
        em.getTransaction().commit();
        em.clear();
        // Revision 3: remove child
        em.getTransaction().begin();
        owningEntity = getEntityManager().find(SetOwningEntity.class, ing_id);
        ownedEntity = getEntityManager().find(SetOwnedEntity.class, ed_id);
        owningEntity.getReferences().remove(ownedEntity);
        em.getTransaction().commit();
        em.clear();
        // Revision 4: add child again
        em.getTransaction().begin();
        owningEntity = getEntityManager().find(SetOwningEntity.class, ing_id);
        ownedEntity = getEntityManager().find(SetOwnedEntity.class, ed_id);
        owningEntity.getReferences().add(ownedEntity);
        em.getTransaction().commit();
        em.clear();
        // Revision 5: remove child again
        em.getTransaction().begin();
        owningEntity = getEntityManager().find(SetOwningEntity.class, ing_id);
        ownedEntity = getEntityManager().find(SetOwnedEntity.class, ed_id);
        owningEntity.getReferences().remove(ownedEntity);
        em.getTransaction().commit();
        em.clear();
        // now the set owning entity list should be empty again
        owningEntity = getEntityManager().find(SetOwningEntity.class, ing_id);
        Assert.assertEquals(owningEntity.getReferences().size(), 0);
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(getAuditReader().getRevisions(SetOwningEntity.class, ing_id), Arrays.asList(1, 2, 3, 4, 5));
        Assert.assertEquals(getAuditReader().getRevisions(SetOwnedEntity.class, ed_id), Arrays.asList(1, 2, 3, 4, 5));
    }

    @Test
    public void testHistoryOfIng1() {
        SetOwningEntity ver_empty = createOwningEntity();
        SetOwningEntity ver_child = createOwningEntity(new SetOwnedEntity(ed_id, "child"));
        Assert.assertEquals(getAuditReader().find(SetOwningEntity.class, ing_id, 1), ver_empty);
        Assert.assertEquals(getAuditReader().find(SetOwningEntity.class, ing_id, 2), ver_child);
        Assert.assertEquals(getAuditReader().find(SetOwningEntity.class, ing_id, 3), ver_empty);
        Assert.assertEquals(getAuditReader().find(SetOwningEntity.class, ing_id, 4), ver_child);
        Assert.assertEquals(getAuditReader().find(SetOwningEntity.class, ing_id, 5), ver_empty);
    }

    @Test
    public void testHistoryOfEd1() {
        SetOwnedEntity ver_empty = createOwnedEntity();
        SetOwnedEntity ver_child = createOwnedEntity(new SetOwningEntity(ing_id, "parent"));
        Assert.assertEquals(getAuditReader().find(SetOwnedEntity.class, ed_id, 1), ver_empty);
        Assert.assertEquals(getAuditReader().find(SetOwnedEntity.class, ed_id, 2), ver_child);
        Assert.assertEquals(getAuditReader().find(SetOwnedEntity.class, ed_id, 3), ver_empty);
        Assert.assertEquals(getAuditReader().find(SetOwnedEntity.class, ed_id, 4), ver_child);
        Assert.assertEquals(getAuditReader().find(SetOwnedEntity.class, ed_id, 5), ver_empty);
    }
}

