/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.manytomany.unidirectional;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.UnversionedStrTestEntity;
import org.hibernate.envers.test.entities.manytomany.unidirectional.M2MTargetNotAuditedEntity;
import org.hibernate.envers.test.tools.TestTools;
import org.junit.Assert;
import org.junit.Test;


/**
 * A test for auditing a many-to-many relation where the target entity is not audited.
 *
 * @author Adam Warski
 */
public class M2MRelationNotAuditedTarget extends BaseEnversJPAFunctionalTestCase {
    private Integer tnae1_id;

    private Integer tnae2_id;

    private Integer uste1_id;

    private Integer uste2_id;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        UnversionedStrTestEntity uste1 = new UnversionedStrTestEntity("str1");
        UnversionedStrTestEntity uste2 = new UnversionedStrTestEntity("str2");
        // No revision
        em.getTransaction().begin();
        em.persist(uste1);
        em.persist(uste2);
        em.getTransaction().commit();
        // Revision 1
        em.getTransaction().begin();
        uste1 = em.find(UnversionedStrTestEntity.class, uste1.getId());
        uste2 = em.find(UnversionedStrTestEntity.class, uste2.getId());
        M2MTargetNotAuditedEntity tnae1 = new M2MTargetNotAuditedEntity(1, "tnae1", new ArrayList<UnversionedStrTestEntity>());
        M2MTargetNotAuditedEntity tnae2 = new M2MTargetNotAuditedEntity(2, "tnae2", new ArrayList<UnversionedStrTestEntity>());
        tnae2.getReferences().add(uste1);
        tnae2.getReferences().add(uste2);
        em.persist(tnae1);
        em.persist(tnae2);
        em.getTransaction().commit();
        // Revision 2
        em.getTransaction().begin();
        tnae1 = em.find(M2MTargetNotAuditedEntity.class, tnae1.getId());
        tnae2 = em.find(M2MTargetNotAuditedEntity.class, tnae2.getId());
        tnae1.getReferences().add(uste1);
        tnae2.getReferences().remove(uste1);
        em.getTransaction().commit();
        // Revision 3
        em.getTransaction().begin();
        tnae1 = em.find(M2MTargetNotAuditedEntity.class, tnae1.getId());
        tnae2 = em.find(M2MTargetNotAuditedEntity.class, tnae2.getId());
        // field not changed!!!
        tnae1.getReferences().add(uste1);
        tnae2.getReferences().remove(uste2);
        em.getTransaction().commit();
        // Revision 4
        em.getTransaction().begin();
        tnae1 = em.find(M2MTargetNotAuditedEntity.class, tnae1.getId());
        tnae2 = em.find(M2MTargetNotAuditedEntity.class, tnae2.getId());
        tnae1.getReferences().add(uste2);
        tnae2.getReferences().add(uste1);
        em.getTransaction().commit();
        // 
        tnae1_id = tnae1.getId();
        tnae2_id = tnae2.getId();
        uste1_id = uste1.getId();
        uste2_id = uste2.getId();
    }

    @Test
    public void testRevisionsCounts() {
        List<Number> revisions = getAuditReader().getRevisions(M2MTargetNotAuditedEntity.class, tnae1_id);
        assert Arrays.asList(1, 2, 4).equals(revisions);
        revisions = getAuditReader().getRevisions(M2MTargetNotAuditedEntity.class, tnae2_id);
        assert Arrays.asList(1, 2, 3, 4).equals(revisions);
    }

    @Test
    public void testHistoryOfTnae1_id() {
        UnversionedStrTestEntity uste1 = getEntityManager().find(UnversionedStrTestEntity.class, uste1_id);
        UnversionedStrTestEntity uste2 = getEntityManager().find(UnversionedStrTestEntity.class, uste2_id);
        M2MTargetNotAuditedEntity rev1 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae1_id, 1);
        M2MTargetNotAuditedEntity rev2 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae1_id, 2);
        M2MTargetNotAuditedEntity rev3 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae1_id, 3);
        M2MTargetNotAuditedEntity rev4 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae1_id, 4);
        Assert.assertTrue(TestTools.checkCollection(rev1.getReferences()));
        Assert.assertTrue(TestTools.checkCollection(rev2.getReferences(), uste1));
        Assert.assertTrue(TestTools.checkCollection(rev3.getReferences(), uste1));
        Assert.assertTrue(TestTools.checkCollection(rev4.getReferences(), uste1, uste2));
    }

    @Test
    public void testHistoryOfTnae2_id() {
        UnversionedStrTestEntity uste1 = getEntityManager().find(UnversionedStrTestEntity.class, uste1_id);
        UnversionedStrTestEntity uste2 = getEntityManager().find(UnversionedStrTestEntity.class, uste2_id);
        M2MTargetNotAuditedEntity rev1 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae2_id, 1);
        M2MTargetNotAuditedEntity rev2 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae2_id, 2);
        M2MTargetNotAuditedEntity rev3 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae2_id, 3);
        M2MTargetNotAuditedEntity rev4 = getAuditReader().find(M2MTargetNotAuditedEntity.class, tnae2_id, 4);
        Assert.assertTrue(TestTools.checkCollection(rev1.getReferences(), uste1, uste2));
        Assert.assertTrue(TestTools.checkCollection(rev2.getReferences(), uste2));
        Assert.assertTrue(TestTools.checkCollection(rev3.getReferences()));
        Assert.assertTrue(TestTools.checkCollection(rev4.getReferences(), uste1));
    }
}

