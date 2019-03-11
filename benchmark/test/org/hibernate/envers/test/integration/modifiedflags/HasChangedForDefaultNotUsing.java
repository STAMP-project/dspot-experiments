/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.modifiedflags;


import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import junit.framework.Assert;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.envers.test.entities.components.Component1;
import org.hibernate.envers.test.entities.components.Component2;
import org.hibernate.envers.test.integration.modifiedflags.entities.PartialModifiedFlagsEntity;
import org.hibernate.envers.test.integration.modifiedflags.entities.WithModifiedFlagReferencingEntity;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Test;


/**
 *
 *
 * @author Michal Skowronek (mskowr at o2 dot pl)
 */
public class HasChangedForDefaultNotUsing extends AbstractModifiedFlagsEntityTest {
    private static final int entityId = 1;

    private static final int refEntityId = 1;

    @Test
    @Priority(10)
    public void initData() {
        PartialModifiedFlagsEntity entity = new PartialModifiedFlagsEntity(HasChangedForDefaultNotUsing.entityId);
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        // Revision 2
        em.getTransaction().begin();
        entity.setData("data1");
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 3
        em.getTransaction().begin();
        entity.setComp1(new Component1("str1", "str2"));
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 4
        em.getTransaction().begin();
        entity.setComp2(new Component2("str1", "str2"));
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 5
        em.getTransaction().begin();
        WithModifiedFlagReferencingEntity withModifiedFlagReferencingEntity = new WithModifiedFlagReferencingEntity(HasChangedForDefaultNotUsing.refEntityId, "first");
        withModifiedFlagReferencingEntity.setReference(entity);
        em.persist(withModifiedFlagReferencingEntity);
        em.getTransaction().commit();
        // Revision 6
        em.getTransaction().begin();
        withModifiedFlagReferencingEntity = em.find(WithModifiedFlagReferencingEntity.class, HasChangedForDefaultNotUsing.refEntityId);
        withModifiedFlagReferencingEntity.setReference(null);
        withModifiedFlagReferencingEntity.setSecondReference(entity);
        em.merge(withModifiedFlagReferencingEntity);
        em.getTransaction().commit();
        // Revision 7
        em.getTransaction().begin();
        entity.getStringSet().add("firstElement");
        entity.getStringSet().add("secondElement");
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 8
        em.getTransaction().begin();
        entity.getStringSet().remove("secondElement");
        entity.getStringMap().put("someKey", "someValue");
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 9 - main entity doesn't change
        em.getTransaction().begin();
        StrTestEntity strTestEntity = new StrTestEntity("first");
        em.persist(strTestEntity);
        em.getTransaction().commit();
        // Revision 10
        em.getTransaction().begin();
        entity.getEntitiesSet().add(strTestEntity);
        entity = em.merge(entity);
        em.getTransaction().commit();
        // Revision 11
        em.getTransaction().begin();
        entity.getEntitiesSet().remove(strTestEntity);
        entity.getEntitiesMap().put("someKey", strTestEntity);
        em.merge(entity);
        em.getTransaction().commit();
        // Revision 12 - main entity doesn't change
        em.getTransaction().begin();
        strTestEntity.setStr("second");
        em.merge(strTestEntity);
        em.getTransaction().commit();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(((Number) (1)), 2, 3, 4, 5, 6, 7, 8, 10, 11), getAuditReader().getRevisions(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId));
    }

    @Test
    public void testHasChangedData() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "data");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(2), TestTools.extractRevisionNumbers(list));
    }

    @Test
    public void testHasChangedComp1() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "comp1");
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(TestTools.makeList(3), TestTools.extractRevisionNumbers(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasChangedComp2() throws Exception {
        queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "comp2");
    }

    @Test
    public void testHasChangedReferencing() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "referencing");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(5, 6), TestTools.extractRevisionNumbers(list));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHasChangedReferencing2() throws Exception {
        queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "referencing2");
    }

    @Test
    public void testHasChangedStringSet() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "stringSet");
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(TestTools.makeList(1, 7, 8), TestTools.extractRevisionNumbers(list));
    }

    @Test
    public void testHasChangedStringMap() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "stringMap");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 8), TestTools.extractRevisionNumbers(list));
    }

    @Test
    public void testHasChangedStringSetAndMap() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "stringSet", "stringMap");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 8), TestTools.extractRevisionNumbers(list));
    }

    @Test
    public void testHasChangedEntitiesSet() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "entitiesSet");
        Assert.assertEquals(3, list.size());
        Assert.assertEquals(TestTools.makeList(1, 10, 11), TestTools.extractRevisionNumbers(list));
    }

    @Test
    public void testHasChangedEntitiesMap() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "entitiesMap");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 11), TestTools.extractRevisionNumbers(list));
    }

    @Test
    public void testHasChangedEntitiesSetAndMap() throws Exception {
        List list = queryForPropertyHasChanged(PartialModifiedFlagsEntity.class, HasChangedForDefaultNotUsing.entityId, "entitiesSet", "entitiesMap");
        Assert.assertEquals(2, list.size());
        Assert.assertEquals(TestTools.makeList(1, 11), TestTools.extractRevisionNumbers(list));
    }
}

