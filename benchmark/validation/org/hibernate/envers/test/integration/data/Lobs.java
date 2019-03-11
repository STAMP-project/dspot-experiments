/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.data;


import DialectChecks.SupportsExpectedLobUsagePattern;
import java.util.Arrays;
import javax.persistence.EntityManager;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adam Warski (adam at warski dot org)
 * @author Chris Cranford
 */
@RequiresDialectFeature(SupportsExpectedLobUsagePattern.class)
public class Lobs extends BaseEnversJPAFunctionalTestCase {
    private Integer id1;

    private Integer id2;

    @Test
    @Priority(10)
    public void initData() {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        LobTestEntity lte = new LobTestEntity("abc", new byte[]{ 0, 1, 2 }, new char[]{ 'x', 'y', 'z' });
        em.persist(lte);
        id1 = lte.getId();
        em.getTransaction().commit();
        em.getTransaction().begin();
        lte = em.find(LobTestEntity.class, id1);
        lte.setStringLob("def");
        lte.setByteLob(new byte[]{ 3, 4, 5 });
        lte.setCharLob(new char[]{ 'h', 'i', 'j' });
        em.getTransaction().commit();
        // this creates a revision history for a Lob-capable entity but the change is on a non-audited
        // field and so it should only generate 1 revision, the initial persist.
        em.getTransaction().begin();
        LobTestEntity lte2 = new LobTestEntity("abc", new byte[]{ 0, 1, 2 }, new char[]{ 'x', 'y', 'z' });
        lte2.setData("Hi");
        em.persist(lte2);
        em.getTransaction().commit();
        id2 = lte2.getId();
        em.getTransaction().begin();
        lte2 = em.find(LobTestEntity.class, id2);
        lte2.setData("Hello World");
        em.getTransaction().commit();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1, 2), getAuditReader().getRevisions(LobTestEntity.class, id1));
    }

    @Test
    public void testHistoryOfId1() {
        LobTestEntity ver1 = new LobTestEntity(id1, "abc", new byte[]{ 0, 1, 2 }, new char[]{ 'x', 'y', 'z' });
        LobTestEntity ver2 = new LobTestEntity(id1, "def", new byte[]{ 3, 4, 5 }, new char[]{ 'h', 'i', 'j' });
        Assert.assertEquals(getAuditReader().find(LobTestEntity.class, id1, 1), ver1);
        Assert.assertEquals(getAuditReader().find(LobTestEntity.class, id1, 2), ver2);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10734")
    public void testRevisionsCountsForAuditedArraysWithNoChanges() {
        Assert.assertEquals(Arrays.asList(3), getAuditReader().getRevisions(LobTestEntity.class, id2));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10734")
    public void testHistoryOfId2() {
        LobTestEntity ver1 = new LobTestEntity(id2, "abc", new byte[]{ 0, 1, 2 }, new char[]{ 'x', 'y', 'z' });
        Assert.assertEquals(getAuditReader().find(LobTestEntity.class, id2, 3), ver1);
    }
}

