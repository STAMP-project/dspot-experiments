/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.onetomany.detached;


import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.onetomany.detached.IndexedListJoinColumnBidirectionalRefEdEntity;
import org.hibernate.envers.test.entities.onetomany.detached.IndexedListJoinColumnBidirectionalRefIngEntity;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for a "fake" bidirectional mapping where one side uses @OneToMany+@JoinColumn (and thus owns the relatin),
 * and the other uses a @ManyToOne(insertable=false, updatable=false).
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class IndexedJoinColumnBidirectionalList extends BaseEnversJPAFunctionalTestCase {
    private Integer ed1_id;

    private Integer ed2_id;

    private Integer ed3_id;

    private Integer ing1_id;

    private Integer ing2_id;

    @Test
    @Priority(10)
    public void createData() {
        EntityManager em = getEntityManager();
        IndexedListJoinColumnBidirectionalRefEdEntity ed1 = new IndexedListJoinColumnBidirectionalRefEdEntity("ed1", null);
        IndexedListJoinColumnBidirectionalRefEdEntity ed2 = new IndexedListJoinColumnBidirectionalRefEdEntity("ed2", null);
        IndexedListJoinColumnBidirectionalRefEdEntity ed3 = new IndexedListJoinColumnBidirectionalRefEdEntity("ed3", null);
        IndexedListJoinColumnBidirectionalRefIngEntity ing1 = new IndexedListJoinColumnBidirectionalRefIngEntity("coll1", ed1, ed2, ed3);
        IndexedListJoinColumnBidirectionalRefIngEntity ing2 = new IndexedListJoinColumnBidirectionalRefIngEntity("coll1");
        // Revision 1 (ing1: ed1, ed2, ed3)
        em.getTransaction().begin();
        em.persist(ed1);
        em.persist(ed2);
        em.persist(ed3);
        em.persist(ing1);
        em.persist(ing2);
        em.getTransaction().commit();
        // Revision 2 (ing1: ed1, ed3, ing2: ed2)
        em.getTransaction().begin();
        ing1 = em.find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1.getId());
        ing2 = em.find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2.getId());
        ed2 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2.getId());
        ing1.getReferences().remove(ed2);
        ing2.getReferences().add(ed2);
        em.getTransaction().commit();
        em.clear();
        // Revision 3 (ing1: ed3, ed1, ing2: ed2)
        em.getTransaction().begin();
        ing1 = em.find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1.getId());
        ing2 = em.find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2.getId());
        ed1 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1.getId());
        ed2 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2.getId());
        ed3 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3.getId());
        ing1.getReferences().remove(ed3);
        ing1.getReferences().add(0, ed3);
        em.getTransaction().commit();
        em.clear();
        // Revision 4 (ing1: ed2, ed3, ed1)
        em.getTransaction().begin();
        ing1 = em.find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1.getId());
        ing2 = em.find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2.getId());
        ed1 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1.getId());
        ed2 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2.getId());
        ed3 = em.find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3.getId());
        ing2.getReferences().remove(ed2);
        ing1.getReferences().add(0, ed2);
        em.getTransaction().commit();
        em.clear();
        // 
        ing1_id = ing1.getId();
        ing2_id = ing2.getId();
        ed1_id = ed1.getId();
        ed2_id = ed2.getId();
        ed3_id = ed3.getId();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id));
        Assert.assertEquals(Arrays.asList(1, 2, 4), getAuditReader().getRevisions(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2_id));
        Assert.assertEquals(Arrays.asList(1, 3, 4), getAuditReader().getRevisions(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1_id));
        Assert.assertEquals(Arrays.asList(1, 2, 4), getAuditReader().getRevisions(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id));
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4), getAuditReader().getRevisions(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3_id));
    }

    @Test
    public void testHistoryOfIng1() {
        IndexedListJoinColumnBidirectionalRefEdEntity ed1 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1_id);
        IndexedListJoinColumnBidirectionalRefEdEntity ed2 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id);
        IndexedListJoinColumnBidirectionalRefEdEntity ed3 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3_id);
        IndexedListJoinColumnBidirectionalRefIngEntity rev1 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id, 1);
        IndexedListJoinColumnBidirectionalRefIngEntity rev2 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id, 2);
        IndexedListJoinColumnBidirectionalRefIngEntity rev3 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id, 3);
        IndexedListJoinColumnBidirectionalRefIngEntity rev4 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id, 4);
        Assert.assertEquals(rev1.getReferences().size(), 3);
        Assert.assertEquals(rev1.getReferences().get(0), ed1);
        Assert.assertEquals(rev1.getReferences().get(1), ed2);
        Assert.assertEquals(rev1.getReferences().get(2), ed3);
        Assert.assertEquals(rev2.getReferences().size(), 2);
        Assert.assertEquals(rev2.getReferences().get(0), ed1);
        Assert.assertEquals(rev2.getReferences().get(1), ed3);
        Assert.assertEquals(rev3.getReferences().size(), 2);
        Assert.assertEquals(rev3.getReferences().get(0), ed3);
        Assert.assertEquals(rev3.getReferences().get(1), ed1);
        Assert.assertEquals(rev4.getReferences().size(), 3);
        Assert.assertEquals(rev4.getReferences().get(0), ed2);
        Assert.assertEquals(rev4.getReferences().get(1), ed3);
        Assert.assertEquals(rev4.getReferences().get(2), ed1);
    }

    @Test
    public void testHistoryOfIng2() {
        IndexedListJoinColumnBidirectionalRefEdEntity ed2 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id);
        IndexedListJoinColumnBidirectionalRefIngEntity rev1 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2_id, 1);
        IndexedListJoinColumnBidirectionalRefIngEntity rev2 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2_id, 2);
        IndexedListJoinColumnBidirectionalRefIngEntity rev3 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2_id, 3);
        IndexedListJoinColumnBidirectionalRefIngEntity rev4 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2_id, 4);
        Assert.assertEquals(rev1.getReferences().size(), 0);
        Assert.assertEquals(rev2.getReferences().size(), 1);
        Assert.assertEquals(rev2.getReferences().get(0), ed2);
        Assert.assertEquals(rev3.getReferences().size(), 1);
        Assert.assertEquals(rev3.getReferences().get(0), ed2);
        Assert.assertEquals(rev4.getReferences().size(), 0);
    }

    @Test
    public void testHistoryOfEd1() {
        IndexedListJoinColumnBidirectionalRefIngEntity ing1 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id);
        IndexedListJoinColumnBidirectionalRefEdEntity rev1 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1_id, 1);
        IndexedListJoinColumnBidirectionalRefEdEntity rev2 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1_id, 2);
        IndexedListJoinColumnBidirectionalRefEdEntity rev3 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1_id, 3);
        IndexedListJoinColumnBidirectionalRefEdEntity rev4 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed1_id, 4);
        Assert.assertTrue(rev1.getOwner().equals(ing1));
        Assert.assertTrue(rev2.getOwner().equals(ing1));
        Assert.assertTrue(rev3.getOwner().equals(ing1));
        Assert.assertTrue(rev4.getOwner().equals(ing1));
        Assert.assertEquals(rev1.getPosition(), Integer.valueOf(0));
        Assert.assertEquals(rev2.getPosition(), Integer.valueOf(0));
        Assert.assertEquals(rev3.getPosition(), Integer.valueOf(1));
        Assert.assertEquals(rev4.getPosition(), Integer.valueOf(2));
    }

    @Test
    public void testHistoryOfEd2() {
        IndexedListJoinColumnBidirectionalRefIngEntity ing1 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id);
        IndexedListJoinColumnBidirectionalRefIngEntity ing2 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing2_id);
        IndexedListJoinColumnBidirectionalRefEdEntity rev1 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id, 1);
        IndexedListJoinColumnBidirectionalRefEdEntity rev2 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id, 2);
        IndexedListJoinColumnBidirectionalRefEdEntity rev3 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id, 3);
        IndexedListJoinColumnBidirectionalRefEdEntity rev4 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed2_id, 4);
        Assert.assertTrue(rev1.getOwner().equals(ing1));
        Assert.assertTrue(rev2.getOwner().equals(ing2));
        Assert.assertTrue(rev3.getOwner().equals(ing2));
        Assert.assertTrue(rev4.getOwner().equals(ing1));
        Assert.assertEquals(rev1.getPosition(), Integer.valueOf(1));
        Assert.assertEquals(rev2.getPosition(), Integer.valueOf(0));
        Assert.assertEquals(rev3.getPosition(), Integer.valueOf(0));
        Assert.assertEquals(rev4.getPosition(), Integer.valueOf(0));
    }

    @Test
    public void testHistoryOfEd3() {
        IndexedListJoinColumnBidirectionalRefIngEntity ing1 = getEntityManager().find(IndexedListJoinColumnBidirectionalRefIngEntity.class, ing1_id);
        IndexedListJoinColumnBidirectionalRefEdEntity rev1 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3_id, 1);
        IndexedListJoinColumnBidirectionalRefEdEntity rev2 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3_id, 2);
        IndexedListJoinColumnBidirectionalRefEdEntity rev3 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3_id, 3);
        IndexedListJoinColumnBidirectionalRefEdEntity rev4 = getAuditReader().find(IndexedListJoinColumnBidirectionalRefEdEntity.class, ed3_id, 4);
        Assert.assertTrue(rev1.getOwner().equals(ing1));
        Assert.assertTrue(rev2.getOwner().equals(ing1));
        Assert.assertTrue(rev3.getOwner().equals(ing1));
        Assert.assertTrue(rev4.getOwner().equals(ing1));
        Assert.assertEquals(rev1.getPosition(), Integer.valueOf(2));
        Assert.assertEquals(rev2.getPosition(), Integer.valueOf(1));
        Assert.assertEquals(rev3.getPosition(), Integer.valueOf(0));
        Assert.assertEquals(rev4.getPosition(), Integer.valueOf(1));
    }
}

