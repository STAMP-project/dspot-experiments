/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.inheritance.mixed;


import java.util.Arrays;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.integration.inheritance.mixed.entities.Activity;
import org.hibernate.envers.test.integration.inheritance.mixed.entities.ActivityId;
import org.hibernate.envers.test.integration.inheritance.mixed.entities.CheckInActivity;
import org.hibernate.envers.test.integration.inheritance.mixed.entities.NormalActivity;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Skowronek (mskowr at o2 pl)
 */
public class MixedInheritanceStrategiesEntityTest extends BaseEnversJPAFunctionalTestCase {
    private ActivityId id2;

    private ActivityId id1;

    private ActivityId id3;

    @Test
    @Priority(10)
    public void initData() {
        NormalActivity normalActivity = new NormalActivity();
        id1 = new ActivityId(1, 2);
        normalActivity.setId(id1);
        normalActivity.setSequenceNumber(1);
        // Revision 1
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(normalActivity);
        getEntityManager().getTransaction().commit();
        // Revision 2
        getEntityManager().getTransaction().begin();
        normalActivity = getEntityManager().find(NormalActivity.class, id1);
        CheckInActivity checkInActivity = new CheckInActivity();
        id2 = new ActivityId(2, 3);
        checkInActivity.setId(id2);
        checkInActivity.setSequenceNumber(0);
        checkInActivity.setDurationInMinutes(30);
        checkInActivity.setRelatedActivity(normalActivity);
        getEntityManager().persist(checkInActivity);
        getEntityManager().getTransaction().commit();
        // Revision 3
        normalActivity = new NormalActivity();
        id3 = new ActivityId(3, 4);
        normalActivity.setId(id3);
        normalActivity.setSequenceNumber(2);
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(normalActivity);
        getEntityManager().getTransaction().commit();
        // Revision 4
        getEntityManager().getTransaction().begin();
        normalActivity = getEntityManager().find(NormalActivity.class, id3);
        checkInActivity = getEntityManager().find(CheckInActivity.class, id2);
        checkInActivity.setRelatedActivity(normalActivity);
        getEntityManager().merge(checkInActivity);
        getEntityManager().getTransaction().commit();
    }

    @Test
    public void testRevisionsCounts() {
        Assert.assertEquals(Arrays.asList(1), getAuditReader().getRevisions(NormalActivity.class, id1));
        Assert.assertEquals(Arrays.asList(3), getAuditReader().getRevisions(NormalActivity.class, id3));
        Assert.assertEquals(Arrays.asList(2, 4), getAuditReader().getRevisions(CheckInActivity.class, id2));
    }

    @Test
    public void testCurrentStateOfCheckInActivity() {
        final CheckInActivity checkInActivity = getEntityManager().find(CheckInActivity.class, id2);
        final NormalActivity normalActivity = getEntityManager().find(NormalActivity.class, id3);
        Assert.assertEquals(id2, checkInActivity.getId());
        Assert.assertEquals(0, checkInActivity.getSequenceNumber().intValue());
        Assert.assertEquals(30, checkInActivity.getDurationInMinutes().intValue());
        final Activity relatedActivity = checkInActivity.getRelatedActivity();
        Assert.assertEquals(normalActivity.getId(), relatedActivity.getId());
        Assert.assertEquals(normalActivity.getSequenceNumber(), relatedActivity.getSequenceNumber());
    }

    @Test
    public void testCheckCurrentStateOfNormalActivities() throws Exception {
        final NormalActivity normalActivity1 = getEntityManager().find(NormalActivity.class, id1);
        final NormalActivity normalActivity2 = getEntityManager().find(NormalActivity.class, id3);
        Assert.assertEquals(id1, normalActivity1.getId());
        Assert.assertEquals(1, normalActivity1.getSequenceNumber().intValue());
        Assert.assertEquals(id3, normalActivity2.getId());
        Assert.assertEquals(2, normalActivity2.getSequenceNumber().intValue());
    }

    @Test
    public void doTestFirstRevisionOfCheckInActivity() throws Exception {
        CheckInActivity checkInActivity = getAuditReader().find(CheckInActivity.class, id2, 2);
        NormalActivity normalActivity = getAuditReader().find(NormalActivity.class, id1, 2);
        Assert.assertEquals(id2, checkInActivity.getId());
        Assert.assertEquals(0, checkInActivity.getSequenceNumber().intValue());
        Assert.assertEquals(30, checkInActivity.getDurationInMinutes().intValue());
        Activity relatedActivity = checkInActivity.getRelatedActivity();
        Assert.assertEquals(normalActivity.getId(), relatedActivity.getId());
        Assert.assertEquals(normalActivity.getSequenceNumber(), relatedActivity.getSequenceNumber());
    }

    @Test
    public void doTestSecondRevisionOfCheckInActivity() throws Exception {
        CheckInActivity checkInActivity = getAuditReader().find(CheckInActivity.class, id2, 4);
        NormalActivity normalActivity = getAuditReader().find(NormalActivity.class, id3, 4);
        Assert.assertEquals(id2, checkInActivity.getId());
        Assert.assertEquals(0, checkInActivity.getSequenceNumber().intValue());
        Assert.assertEquals(30, checkInActivity.getDurationInMinutes().intValue());
        Activity relatedActivity = checkInActivity.getRelatedActivity();
        Assert.assertEquals(normalActivity.getId(), relatedActivity.getId());
        Assert.assertEquals(normalActivity.getSequenceNumber(), relatedActivity.getSequenceNumber());
    }
}

