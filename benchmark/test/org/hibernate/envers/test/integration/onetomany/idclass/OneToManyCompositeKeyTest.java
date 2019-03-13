/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.onetomany.idclass;


import java.util.Arrays;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-7625")
public class OneToManyCompositeKeyTest extends BaseEnversJPAFunctionalTestCase {
    private ManyToManyCompositeKey.ManyToManyId owning1Id = null;

    private ManyToManyCompositeKey.ManyToManyId owning2Id = null;

    private Long oneToManyId;

    private Long manyToOne1Id;

    private Long manyToOne2Id;

    @Test
    @Priority(10)
    public void initData() {
        // Revision 1
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            OneToManyOwned oneToManyOwned = new OneToManyOwned("data", null);
            ManyToOneOwned manyToOneOwned1 = new ManyToOneOwned("data1");
            ManyToOneOwned manyToOneOwned2 = new ManyToOneOwned("data2");
            ManyToManyCompositeKey owning1 = new ManyToManyCompositeKey(oneToManyOwned, manyToOneOwned1);
            ManyToManyCompositeKey owning2 = new ManyToManyCompositeKey(oneToManyOwned, manyToOneOwned2);
            entityManager.persist(oneToManyOwned);
            entityManager.persist(manyToOneOwned1);
            entityManager.persist(manyToOneOwned2);
            entityManager.persist(owning1);
            entityManager.persist(owning2);
            owning1Id = owning1.getId();
            owning2Id = owning2.getId();
            oneToManyId = oneToManyOwned.getId();
            manyToOne1Id = manyToOneOwned1.getId();
            manyToOne2Id = manyToOneOwned2.getId();
        });
        // Revision 2
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            ManyToManyCompositeKey owning1 = entityManager.find(.class, owning1Id);
            entityManager.remove(owning1);
        });
        // Revision 3
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            ManyToManyCompositeKey owning2 = entityManager.find(.class, owning2Id);
            entityManager.remove(owning2);
        });
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(ManyToManyCompositeKey.class, owning1Id));
        Assert.assertEquals(Arrays.asList(1, 3), getAuditReader().getRevisions(ManyToManyCompositeKey.class, owning2Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(OneToManyOwned.class, oneToManyId));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(ManyToOneOwned.class, manyToOne1Id));
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(ManyToOneOwned.class, manyToOne2Id));
    }

    @Test
    public void testOneToManyHistory() {
        final OneToManyOwned rev1 = getAuditReader().find(OneToManyOwned.class, oneToManyId, 1);
        Assert.assertEquals("data", rev1.getData());
        Assert.assertEquals(2, rev1.getManyToManyCompositeKeys().size());
    }

    @Test
    public void testManyToOne1History() {
        final ManyToOneOwned rev1 = getAuditReader().find(ManyToOneOwned.class, manyToOne1Id, 1);
        Assert.assertEquals("data1", rev1.getData());
    }

    @Test
    public void testManyToOne2History() {
        final ManyToOneOwned rev1 = getAuditReader().find(ManyToOneOwned.class, manyToOne2Id, 1);
        Assert.assertEquals("data2", rev1.getData());
    }

    @Test
    public void testOwning1History() {
        // objects
        final OneToManyOwned oneToMany = new OneToManyOwned(1L, "data", null);
        final ManyToOneOwned manyToOne = new ManyToOneOwned(2L, "data1");
        // insert revision
        final ManyToManyCompositeKey rev1 = getAuditReader().find(ManyToManyCompositeKey.class, owning1Id, 1);
        Assert.assertEquals(rev1.getOneToMany(), oneToMany);
        Assert.assertEquals(rev1.getManyToOne(), manyToOne);
        // removal revision - find returns null for deleted
        Assert.assertNull(getAuditReader().find(ManyToManyCompositeKey.class, owning1Id, 2));
        // fetch revision 2 using 'select deletions' api and verify.
        final ManyToManyCompositeKey rev2 = ((ManyToManyCompositeKey) (getAuditReader().createQuery().forRevisionsOfEntity(ManyToManyCompositeKey.class, true, true).add(AuditEntity.id().eq(owning1Id)).add(AuditEntity.revisionNumber().eq(2)).getSingleResult()));
        Assert.assertEquals(rev2.getOneToMany(), oneToMany);
        Assert.assertEquals(rev2.getManyToOne(), manyToOne);
    }

    @Test
    public void testOwning2History() {
        // objects
        final OneToManyOwned oneToMany = new OneToManyOwned(1L, "data", null);
        final ManyToOneOwned manyToOne = new ManyToOneOwned(3L, "data2");
        // insert revision
        final ManyToManyCompositeKey rev1 = getAuditReader().find(ManyToManyCompositeKey.class, owning2Id, 1);
        Assert.assertEquals(rev1.getOneToMany(), oneToMany);
        Assert.assertEquals(rev1.getManyToOne(), manyToOne);
        // removal revision - find returns null for deleted
        Assert.assertNull(getAuditReader().find(ManyToManyCompositeKey.class, owning2Id, 3));
        // fetch revision 3 using 'select deletions' api and verify.
        final ManyToManyCompositeKey rev2 = ((ManyToManyCompositeKey) (getAuditReader().createQuery().forRevisionsOfEntity(ManyToManyCompositeKey.class, true, true).add(AuditEntity.id().eq(owning2Id)).add(AuditEntity.revisionNumber().eq(3)).getSingleResult()));
        Assert.assertEquals(rev2.getOneToMany(), oneToMany);
        Assert.assertEquals(rev2.getManyToOne(), manyToOne);
    }
}

