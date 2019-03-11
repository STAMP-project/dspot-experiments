/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.manytomany;


import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.envers.test.entities.StrTestEntityComparator;
import org.hibernate.envers.test.entities.manytomany.SortedSetEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Skowronek (mskowr at o2 pl)
 */
public class CustomComparatorEntityTest extends BaseEnversJPAFunctionalTestCase {
    private Integer id1;

    private Integer id2;

    private Integer id3;

    private Integer id4;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        SortedSetEntity entity1 = new SortedSetEntity(1, "sortedEntity1");
        // Revision 1
        em.getTransaction().begin();
        em.persist(entity1);
        em.getTransaction().commit();
        // Revision 2
        em.getTransaction().begin();
        entity1 = em.find(SortedSetEntity.class, 1);
        final StrTestEntity strTestEntity1 = new StrTestEntity("abc");
        em.persist(strTestEntity1);
        id1 = strTestEntity1.getId();
        entity1.getSortedSet().add(strTestEntity1);
        entity1.getSortedMap().put(strTestEntity1, "abc");
        em.getTransaction().commit();
        // Revision 3
        em.getTransaction().begin();
        entity1 = em.find(SortedSetEntity.class, 1);
        final StrTestEntity strTestEntity2 = new StrTestEntity("aaa");
        em.persist(strTestEntity2);
        id2 = strTestEntity2.getId();
        entity1.getSortedSet().add(strTestEntity2);
        entity1.getSortedMap().put(strTestEntity2, "aaa");
        em.getTransaction().commit();
        // Revision 4
        em.getTransaction().begin();
        entity1 = em.find(SortedSetEntity.class, 1);
        final StrTestEntity strTestEntity3 = new StrTestEntity("aba");
        em.persist(strTestEntity3);
        id3 = strTestEntity3.getId();
        entity1.getSortedSet().add(strTestEntity3);
        entity1.getSortedMap().put(strTestEntity3, "aba");
        em.getTransaction().commit();
        // Revision 5
        em.getTransaction().begin();
        entity1 = em.find(SortedSetEntity.class, 1);
        final StrTestEntity strTestEntity4 = new StrTestEntity("aac");
        em.persist(strTestEntity4);
        id4 = strTestEntity4.getId();
        entity1.getSortedSet().add(strTestEntity4);
        entity1.getSortedMap().put(strTestEntity4, "aac");
        em.getTransaction().commit();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), getAuditReader().getRevisions(SortedSetEntity.class, 1));
        Assert.assertEquals(Arrays.asList(2), getAuditReader().getRevisions(StrTestEntity.class, id1));
        Assert.assertEquals(Arrays.asList(3), getAuditReader().getRevisions(StrTestEntity.class, id2));
        Assert.assertEquals(Arrays.asList(4), getAuditReader().getRevisions(StrTestEntity.class, id3));
        Assert.assertEquals(Arrays.asList(5), getAuditReader().getRevisions(StrTestEntity.class, id4));
    }

    @Test
    public void testCurrentStateOfEntity1() {
        final SortedSetEntity entity1 = getEntityManager().find(SortedSetEntity.class, 1);
        Assert.assertEquals("sortedEntity1", entity1.getData());
        Assert.assertEquals(Integer.valueOf(1), entity1.getId());
        final SortedSet<StrTestEntity> sortedSet = entity1.getSortedSet();
        Assert.assertEquals(StrTestEntityComparator.class, sortedSet.comparator().getClass());
        Assert.assertEquals(4, sortedSet.size());
        final Iterator<StrTestEntity> iterator = sortedSet.iterator();
        checkStrTestEntity(iterator.next(), id2, "aaa");
        checkStrTestEntity(iterator.next(), id4, "aac");
        checkStrTestEntity(iterator.next(), id3, "aba");
        checkStrTestEntity(iterator.next(), id1, "abc");
        final SortedMap<StrTestEntity, String> sortedMap = entity1.getSortedMap();
        Assert.assertEquals(StrTestEntityComparator.class, sortedMap.comparator().getClass());
        Assert.assertEquals(4, sortedMap.size());
        Iterator<Map.Entry<StrTestEntity, String>> mapIterator = sortedMap.entrySet().iterator();
        checkStrTestEntity(mapIterator.next().getKey(), id2, "aaa");
        checkStrTestEntity(mapIterator.next().getKey(), id4, "aac");
        checkStrTestEntity(mapIterator.next().getKey(), id3, "aba");
        checkStrTestEntity(mapIterator.next().getKey(), id1, "abc");
        mapIterator = sortedMap.entrySet().iterator();
        Assert.assertEquals(mapIterator.next().getValue(), "aaa");
        Assert.assertEquals(mapIterator.next().getValue(), "aac");
        Assert.assertEquals(mapIterator.next().getValue(), "aba");
        Assert.assertEquals(mapIterator.next().getValue(), "abc");
    }

    @Test
    public void testHistoryOfEntity1() throws Exception {
        SortedSetEntity entity1 = getAuditReader().find(SortedSetEntity.class, 1, 1);
        Assert.assertEquals("sortedEntity1", entity1.getData());
        Assert.assertEquals(Integer.valueOf(1), entity1.getId());
        SortedSet<StrTestEntity> sortedSet = entity1.getSortedSet();
        Assert.assertEquals(StrTestEntityComparator.class, sortedSet.comparator().getClass());
        Assert.assertEquals(0, sortedSet.size());
        SortedMap<StrTestEntity, String> sortedMap = entity1.getSortedMap();
        Assert.assertEquals(StrTestEntityComparator.class, sortedMap.comparator().getClass());
        Assert.assertEquals(0, sortedMap.size());
        entity1 = getAuditReader().find(SortedSetEntity.class, 1, 2);
        Assert.assertEquals("sortedEntity1", entity1.getData());
        Assert.assertEquals(Integer.valueOf(1), entity1.getId());
        sortedSet = entity1.getSortedSet();
        Assert.assertEquals(StrTestEntityComparator.class, sortedSet.comparator().getClass());
        Assert.assertEquals(1, sortedSet.size());
        Iterator<StrTestEntity> iterator = sortedSet.iterator();
        checkStrTestEntity(iterator.next(), id1, "abc");
        sortedMap = entity1.getSortedMap();
        Assert.assertEquals(StrTestEntityComparator.class, sortedMap.comparator().getClass());
        Assert.assertEquals(1, sortedMap.size());
        Iterator<Map.Entry<StrTestEntity, String>> mapIterator = sortedMap.entrySet().iterator();
        checkStrTestEntity(mapIterator.next().getKey(), id1, "abc");
        mapIterator = sortedMap.entrySet().iterator();
        Assert.assertEquals(mapIterator.next().getValue(), "abc");
        entity1 = getAuditReader().find(SortedSetEntity.class, 1, 3);
        Assert.assertEquals("sortedEntity1", entity1.getData());
        Assert.assertEquals(Integer.valueOf(1), entity1.getId());
        sortedSet = entity1.getSortedSet();
        Assert.assertEquals(StrTestEntityComparator.class, sortedSet.comparator().getClass());
        Assert.assertEquals(2, sortedSet.size());
        iterator = sortedSet.iterator();
        checkStrTestEntity(iterator.next(), id2, "aaa");
        checkStrTestEntity(iterator.next(), id1, "abc");
        sortedMap = entity1.getSortedMap();
        Assert.assertEquals(StrTestEntityComparator.class, sortedMap.comparator().getClass());
        Assert.assertEquals(2, sortedMap.size());
        mapIterator = sortedMap.entrySet().iterator();
        checkStrTestEntity(mapIterator.next().getKey(), id2, "aaa");
        checkStrTestEntity(mapIterator.next().getKey(), id1, "abc");
        mapIterator = sortedMap.entrySet().iterator();
        Assert.assertEquals(mapIterator.next().getValue(), "aaa");
        Assert.assertEquals(mapIterator.next().getValue(), "abc");
        entity1 = getAuditReader().find(SortedSetEntity.class, 1, 4);
        Assert.assertEquals("sortedEntity1", entity1.getData());
        Assert.assertEquals(Integer.valueOf(1), entity1.getId());
        sortedSet = entity1.getSortedSet();
        Assert.assertEquals(StrTestEntityComparator.class, sortedSet.comparator().getClass());
        Assert.assertEquals(3, sortedSet.size());
        iterator = sortedSet.iterator();
        checkStrTestEntity(iterator.next(), id2, "aaa");
        checkStrTestEntity(iterator.next(), id3, "aba");
        checkStrTestEntity(iterator.next(), id1, "abc");
        sortedMap = entity1.getSortedMap();
        Assert.assertEquals(StrTestEntityComparator.class, sortedMap.comparator().getClass());
        Assert.assertEquals(3, sortedMap.size());
        mapIterator = sortedMap.entrySet().iterator();
        checkStrTestEntity(mapIterator.next().getKey(), id2, "aaa");
        checkStrTestEntity(mapIterator.next().getKey(), id3, "aba");
        checkStrTestEntity(mapIterator.next().getKey(), id1, "abc");
        mapIterator = sortedMap.entrySet().iterator();
        Assert.assertEquals(mapIterator.next().getValue(), "aaa");
        Assert.assertEquals(mapIterator.next().getValue(), "aba");
        Assert.assertEquals(mapIterator.next().getValue(), "abc");
        entity1 = getAuditReader().find(SortedSetEntity.class, 1, 5);
        Assert.assertEquals("sortedEntity1", entity1.getData());
        Assert.assertEquals(Integer.valueOf(1), entity1.getId());
        sortedSet = entity1.getSortedSet();
        Assert.assertEquals(StrTestEntityComparator.class, sortedSet.comparator().getClass());
        Assert.assertEquals(4, sortedSet.size());
        iterator = sortedSet.iterator();
        checkStrTestEntity(iterator.next(), id2, "aaa");
        checkStrTestEntity(iterator.next(), id4, "aac");
        checkStrTestEntity(iterator.next(), id3, "aba");
        checkStrTestEntity(iterator.next(), id1, "abc");
        sortedMap = entity1.getSortedMap();
        Assert.assertEquals(StrTestEntityComparator.class, sortedMap.comparator().getClass());
        Assert.assertEquals(4, sortedMap.size());
        mapIterator = sortedMap.entrySet().iterator();
        checkStrTestEntity(mapIterator.next().getKey(), id2, "aaa");
        checkStrTestEntity(mapIterator.next().getKey(), id4, "aac");
        checkStrTestEntity(mapIterator.next().getKey(), id3, "aba");
        checkStrTestEntity(mapIterator.next().getKey(), id1, "abc");
        mapIterator = sortedMap.entrySet().iterator();
        Assert.assertEquals(mapIterator.next().getValue(), "aaa");
        Assert.assertEquals(mapIterator.next().getValue(), "aac");
        Assert.assertEquals(mapIterator.next().getValue(), "aba");
        Assert.assertEquals(mapIterator.next().getValue(), "abc");
    }
}

