/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.reventity.removal;


import DialectChecks.SupportsCascadeDeleteCheck;
import java.util.ArrayList;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.envers.test.entities.manytomany.ListOwnedEntity;
import org.hibernate.envers.test.entities.manytomany.ListOwningEntity;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Lukasz Antoniak (lukasz dot antoniak at gmail dot com)
 */
@TestForIssue(jiraKey = "HHH-7807")
@RequiresDialectFeature(SupportsCascadeDeleteCheck.class)
public abstract class AbstractRevisionEntityRemovalTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        // Revision 1 - simple entity
        em.getTransaction().begin();
        em.persist(new StrTestEntity("data"));
        em.getTransaction().commit();
        // Revision 2 - many-to-many relation
        em.getTransaction().begin();
        ListOwnedEntity owned = new ListOwnedEntity(1, "data");
        ListOwningEntity owning = new ListOwningEntity(1, "data");
        owned.setReferencing(new ArrayList<ListOwningEntity>());
        owning.setReferences(new ArrayList<ListOwnedEntity>());
        owned.getReferencing().add(owning);
        owning.getReferences().add(owned);
        em.persist(owned);
        em.persist(owning);
        em.getTransaction().commit();
        em.getTransaction().begin();
        Assert.assertEquals(1, countRecords(em, "STR_TEST_AUD"));
        Assert.assertEquals(1, countRecords(em, "ListOwned_AUD"));
        Assert.assertEquals(1, countRecords(em, "ListOwning_AUD"));
        Assert.assertEquals(1, countRecords(em, "ListOwning_ListOwned_AUD"));
        em.getTransaction().commit();
        em.close();
    }

    @Test
    @Priority(9)
    public void testRemoveExistingRevisions() {
        EntityManager em = getEntityManager();
        removeRevision(em, 1);
        removeRevision(em, 2);
        em.close();
    }

    @Test
    @Priority(8)
    public void testEmptyAuditTables() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Assert.assertEquals(0, countRecords(em, "STR_TEST_AUD"));
        Assert.assertEquals(0, countRecords(em, "ListOwned_AUD"));
        Assert.assertEquals(0, countRecords(em, "ListOwning_AUD"));
        Assert.assertEquals(0, countRecords(em, "ListOwning_ListOwned_AUD"));
        em.getTransaction().commit();
        em.close();
    }
}

