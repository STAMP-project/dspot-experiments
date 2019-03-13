/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.components.dynamic;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.hibernate.Session;
import org.hibernate.envers.exception.AuditException;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.BaseEnversFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Zuchowski (author at zuchos dot com)
More advanced tests for dynamic component.
 */
@TestForIssue(jiraKey = "HHH-8049")
public class AuditedDynamicComponentsAdvancedCasesTest extends BaseEnversFunctionalTestCase {
    public static final String PROP_BOOLEAN = "propBoolean";

    public static final String PROP_INT = "propInt";

    public static final String PROP_FLOAT = "propFloat";

    public static final String PROP_MANY_TO_ONE = "propManyToOne";

    public static final String PROP_ONE_TO_ONE = "propOneToOne";

    public static final String INTERNAL_COMPONENT = "internalComponent";

    public static final String INTERNAL_LIST = "internalList";

    public static final String INTERNAL_MAP = "internalMap";

    public static final String INTERNAL_MAP_WITH_MANY_TO_MANY = "internalMapWithEntities";

    public static final String INTERNAL_SET = "internalSet";

    public static final String INTERNAL_SET_OF_COMPONENTS = "internalSetOfComponents";

    public static final String AGE_USER_TYPE = "ageUserType";

    public static final String INTERNAL_LIST_OF_USER_TYPES = "internalListOfUserTypes";

    // smoke test to make sure that hibernate & envers are working with the entity&mappings
    @Test
    @Priority(10)
    public void shouldInitData() {
        // given
        ManyToOneEntity manyToOne = getManyToOneEntity();
        OneToOneEntity oneToOne = getOneToOneEntity();
        ManyToManyEntity manyToManyEntity = getManyToManyEntity();
        AdvancedEntity advancedEntity = getAdvancedEntity(manyToOne, oneToOne, manyToManyEntity);
        // rev 1
        Session session = openSession();
        session.getTransaction().begin();
        session.save(manyToOne);
        session.save(oneToOne);
        session.save(manyToManyEntity);
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 2
        session.getTransaction().begin();
        InternalComponent internalComponent = ((InternalComponent) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_COMPONENT)));
        internalComponent.setProperty("new value");
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 3
        session.getTransaction().begin();
        List<String> internalList = ((List) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_LIST)));
        internalList.add("four");
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 4
        session.getTransaction().begin();
        Map<String, String> map = ((Map) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP)));
        map.put("three", "3");
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 5
        session.getTransaction().begin();
        Map<String, ManyToManyEntity> mapWithManyToMany = ((Map) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP_WITH_MANY_TO_MANY)));
        ManyToManyEntity manyToManyEntity2 = new ManyToManyEntity(2L, "new value");
        mapWithManyToMany.put("entity2", manyToManyEntity2);
        session.save(manyToManyEntity2);
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 6
        session.getTransaction().begin();
        mapWithManyToMany = ((Map) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP_WITH_MANY_TO_MANY)));
        mapWithManyToMany.clear();
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 7
        session.getTransaction().begin();
        Set<InternalComponent> internalComponentSet = ((Set) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_SET_OF_COMPONENTS)));
        internalComponentSet.add(new InternalComponent("drei"));
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 8
        session.getTransaction().begin();
        advancedEntity.getDynamicConfiguration().put(AuditedDynamicComponentsAdvancedCasesTest.AGE_USER_TYPE, new Age(19));
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev 9
        session.getTransaction().begin();
        List<Age> ages = ((List<Age>) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_LIST_OF_USER_TYPES)));
        ages.add(new Age(4));
        session.save(advancedEntity);
        session.getTransaction().commit();
        // rev this, should not create revision
        session.getTransaction().begin();
        session.getTransaction().commit();
        // sanity check. Loaded entity should be equal to one that we created.
        AdvancedEntity advancedEntityActual = ((AdvancedEntity) (session.load(AdvancedEntity.class, 1L)));
        Assert.assertEquals(advancedEntity, advancedEntityActual);
    }

    @Test
    public void shouldMakeRevisions() {
        Session session = openSession();
        session.getTransaction().begin();
        // given & when shouldInitData
        ManyToOneEntity manyToOne = getManyToOneEntity();
        OneToOneEntity oneToOne = getOneToOneEntity();
        ManyToManyEntity manyToManyEntity = getManyToManyEntity();
        AdvancedEntity advancedEntity = getAdvancedEntity(manyToOne, oneToOne, manyToManyEntity);
        // then v1
        AdvancedEntity ver1 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 1);
        Assert.assertEquals(advancedEntity, ver1);
        // then v2
        InternalComponent internalComponent = ((InternalComponent) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_COMPONENT)));
        internalComponent.setProperty("new value");
        AdvancedEntity ver2 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 2);
        Assert.assertEquals(advancedEntity, ver2);
        // then v3
        List internalList = ((List) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_LIST)));
        internalList.add("four");
        AdvancedEntity ver3 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 3);
        Assert.assertEquals(advancedEntity, ver3);
        // then v4
        Map<String, String> map = ((Map) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP)));
        map.put("three", "3");
        AdvancedEntity ver4 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 4);
        Assert.assertEquals(advancedEntity, ver4);
        // then v5
        Map<String, ManyToManyEntity> mapWithManyToMany = ((Map) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP_WITH_MANY_TO_MANY)));
        ManyToManyEntity manyToManyEntity2 = new ManyToManyEntity(2L, "new value");
        mapWithManyToMany.put("entity2", manyToManyEntity2);
        AdvancedEntity ver5 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 5);
        Assert.assertEquals(advancedEntity, ver5);
        // then v6
        mapWithManyToMany = ((Map) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP_WITH_MANY_TO_MANY)));
        mapWithManyToMany.clear();
        AdvancedEntity ver6 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 6);
        Assert.assertEquals(advancedEntity, ver6);
        // then v7
        Set<InternalComponent> internalComponentSet = ((Set) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_SET_OF_COMPONENTS)));
        internalComponentSet.add(new InternalComponent("drei"));
        AdvancedEntity ver7 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 7);
        Assert.assertEquals(advancedEntity, ver7);
        // then v8
        advancedEntity.getDynamicConfiguration().put(AuditedDynamicComponentsAdvancedCasesTest.AGE_USER_TYPE, new Age(19));
        AdvancedEntity ver8 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 8);
        Assert.assertEquals(advancedEntity, ver8);
        // then v9
        List<Age> ages = ((List<Age>) (advancedEntity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_LIST_OF_USER_TYPES)));
        ages.add(new Age(4));
        AdvancedEntity ver9 = getAuditReader().find(AdvancedEntity.class, advancedEntity.getId(), 9);
        Assert.assertEquals(advancedEntity, ver9);
        session.getTransaction().commit();
    }

    @Test
    public void testOfQueryOnDynamicComponent() {
        // given (and result of shouldInitData()
        AdvancedEntity entity = getAdvancedEntity(getManyToOneEntity(), getOneToOneEntity(), getManyToManyEntity());
        // when
        ManyToOneEntity manyToOneEntity = ((ManyToOneEntity) (entity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.PROP_MANY_TO_ONE)));
        List resultList = getAuditReader().createQuery().forEntitiesAtRevision(AdvancedEntity.class, 1).add(AuditEntity.relatedId(("dynamicConfiguration_" + (AuditedDynamicComponentsAdvancedCasesTest.PROP_MANY_TO_ONE))).eq(manyToOneEntity.getId())).getResultList();
        // then
        Assert.assertEquals(entity, resultList.get(0));
        // when
        InternalComponent internalComponent = ((InternalComponent) (entity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_COMPONENT)));
        resultList = getAuditReader().createQuery().forEntitiesAtRevision(AdvancedEntity.class, 1).add(AuditEntity.property((("dynamicConfiguration_" + (AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_COMPONENT)) + "_property")).eq(internalComponent.getProperty())).getResultList();
        // then
        Assert.assertEquals(entity, resultList.get(0));
        // when
        try {
            OneToOneEntity oneToOneEntity = ((OneToOneEntity) (entity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.PROP_ONE_TO_ONE)));
            getAuditReader().createQuery().forEntitiesAtRevision(AdvancedEntity.class, 1).add(AuditEntity.property(("dynamicConfiguration_" + (AuditedDynamicComponentsAdvancedCasesTest.PROP_ONE_TO_ONE))).eq(oneToOneEntity)).getResultList();
            // then
            Assert.fail();
        } catch (Exception e) {
            if (getSession().getTransaction().isActive()) {
                getSession().getTransaction().rollback();
            }
            assertTyping(IllegalArgumentException.class, e);
        }
        try {
            getAuditReader().createQuery().forEntitiesAtRevision(AdvancedEntity.class, 1).add(AuditEntity.property(("dynamicConfiguration_" + (AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP_WITH_MANY_TO_MANY))).eq(entity.getDynamicConfiguration().get(AuditedDynamicComponentsAdvancedCasesTest.INTERNAL_MAP_WITH_MANY_TO_MANY))).getResultList();
            Assert.fail();
        } catch (Exception e) {
            if (getSession().getTransaction().isActive()) {
                getSession().getTransaction().rollback();
            }
            assertTyping(AuditException.class, e);
            Assert.assertEquals("This type of relation (org.hibernate.envers.test.integration.components.dynamic.AdvancedEntity.dynamicConfiguration_internalMapWithEntities) isn't supported and can't be used in queries.", e.getMessage());
        }
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), getAuditReader().getRevisions(AdvancedEntity.class, 1L));
    }
}

