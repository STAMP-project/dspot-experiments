/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.reventity;


import java.util.Arrays;
import java.util.Date;
import javax.persistence.EntityManager;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.exception.RevisionDoesNotExistException;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.entities.StrTestEntity;
import org.hibernate.envers.test.entities.reventity.CustomRevEntityColumnMapping;
import org.junit.Test;


/**
 * Test which checks if auditing when the revision number in the revision entity has a @Column annotation with
 * a columnDefinition specified works.
 *
 * @author Adam Warski (adam at warski dot org)
 */
public class CustomColumnMapping extends BaseEnversJPAFunctionalTestCase {
    private Integer id;

    private long timestamp1;

    private long timestamp2;

    private long timestamp3;

    @Test
    @Priority(10)
    public void initData() throws InterruptedException {
        timestamp1 = System.currentTimeMillis();
        Thread.sleep(100);
        // Revision 1
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        StrTestEntity te = new StrTestEntity("x");
        em.persist(te);
        id = te.getId();
        em.getTransaction().commit();
        timestamp2 = System.currentTimeMillis();
        Thread.sleep(100);
        // Revision 2
        em.getTransaction().begin();
        te = em.find(StrTestEntity.class, id);
        te.setStr("y");
        em.getTransaction().commit();
        timestamp3 = System.currentTimeMillis();
    }

    @Test(expected = RevisionDoesNotExistException.class)
    public void testTimestamps1() {
        getAuditReader().getRevisionNumberForDate(new Date(timestamp1));
    }

    @Test
    public void testTimestamps() {
        assert (getAuditReader().getRevisionNumberForDate(new Date(timestamp2)).intValue()) == 1;
        assert (getAuditReader().getRevisionNumberForDate(new Date(timestamp3)).intValue()) == 2;
    }

    @Test
    public void testDatesForRevisions() {
        AuditReader vr = getAuditReader();
        assert (vr.getRevisionNumberForDate(vr.getRevisionDate(1L)).intValue()) == 1;
        assert (vr.getRevisionNumberForDate(vr.getRevisionDate(2L)).intValue()) == 2;
    }

    @Test
    public void testRevisionsForDates() {
        AuditReader vr = getAuditReader();
        assert (vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp2))).getTime()) <= (timestamp2);
        assert (vr.getRevisionDate(((vr.getRevisionNumberForDate(new Date(timestamp2)).longValue()) + 1L)).getTime()) > (timestamp2);
        assert (vr.getRevisionDate(vr.getRevisionNumberForDate(new Date(timestamp3))).getTime()) <= (timestamp3);
    }

    @Test
    public void testFindRevision() {
        AuditReader vr = getAuditReader();
        long rev1Timestamp = vr.findRevision(CustomRevEntityColumnMapping.class, 1L).getCustomTimestamp();
        assert rev1Timestamp > (timestamp1);
        assert rev1Timestamp <= (timestamp2);
        long rev2Timestamp = vr.findRevision(CustomRevEntityColumnMapping.class, 2L).getCustomTimestamp();
        assert rev2Timestamp > (timestamp2);
        assert rev2Timestamp <= (timestamp3);
    }

    @Test
    public void testRevisionsCounts() {
        assert Arrays.asList(1L, 2L).equals(getAuditReader().getRevisions(StrTestEntity.class, id));
    }

    @Test
    public void testHistoryOfId1() {
        StrTestEntity ver1 = new StrTestEntity("x", id);
        StrTestEntity ver2 = new StrTestEntity("y", id);
        assert getAuditReader().find(StrTestEntity.class, id, 1L).equals(ver1);
        assert getAuditReader().find(StrTestEntity.class, id, 2L).equals(ver2);
    }
}

