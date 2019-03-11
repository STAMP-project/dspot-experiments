/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.fetchprofiles.join;


import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.UnknownProfileException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Various tests related to join-style fetch profiles.
 *
 * @author Steve Ebersole
 */
public class JoinFetchProfileTest extends BaseCoreFunctionalTestCase {
    @SuppressWarnings({ "UnusedDeclaration" })
    private static interface TestData {
        public Long getStudentId();

        public Long getDepartmentId();

        public Long getCourseId();

        public Long getSectionId();

        public Long getEnrollmentId();
    }

    private interface TestCode {
        public void perform(JoinFetchProfileTest.TestData data);
    }

    @Test
    public void testNormalLoading() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                CourseOffering section = ((CourseOffering) (session.get(CourseOffering.class, data.getSectionId())));
                Assert.assertEquals(1, sessionFactory().getStatistics().getEntityLoadCount());
                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertFalse(Hibernate.isInitialized(section.getCourse()));
                Assert.assertFalse(Hibernate.isInitialized(section.getEnrollments()));
                Assert.assertFalse(Hibernate.isInitialized(section.getCourse().getCode().getDepartment()));
                Assert.assertTrue(Hibernate.isInitialized(section.getCourse()));
                Assert.assertEquals(1, sessionFactory().getStatistics().getEntityFetchCount());
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testNormalCriteria() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                CourseOffering section = ((CourseOffering) (session.createCriteria(CourseOffering.class).uniqueResult()));
                Assert.assertEquals(1, sessionFactory().getStatistics().getEntityLoadCount());
                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertFalse(Hibernate.isInitialized(section.getCourse()));
                Assert.assertFalse(Hibernate.isInitialized(section.getEnrollments()));
                Assert.assertFalse(Hibernate.isInitialized(section.getCourse().getCode().getDepartment()));
                Assert.assertTrue(Hibernate.isInitialized(section.getCourse()));
                Assert.assertEquals(1, sessionFactory().getStatistics().getEntityFetchCount());
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testBasicFetchProfileOperation() {
        Assert.assertTrue("fetch profile not parsed properly", sessionFactory().containsFetchProfileDefinition("enrollment.details"));
        Assert.assertTrue("fetch profile not parsed properly", sessionFactory().containsFetchProfileDefinition("offering.details"));
        Assert.assertTrue("fetch profile not parsed properly", sessionFactory().containsFetchProfileDefinition("course.details"));
        Session s = openSession();
        SessionImplementor si = ((SessionImplementor) (s));
        s.enableFetchProfile("enrollment.details");
        Assert.assertTrue(si.getLoadQueryInfluencers().hasEnabledFetchProfiles());
        s.disableFetchProfile("enrollment.details");
        Assert.assertFalse(si.getLoadQueryInfluencers().hasEnabledFetchProfiles());
        try {
            s.enableFetchProfile("never-gonna-get-it");
            Assert.fail("expecting failure on undefined fetch-profile");
        } catch (UnknownProfileException expected) {
        }
        s.close();
    }

    @Test
    public void testLoadManyToOneFetchProfile() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                session.enableFetchProfile("enrollment.details");
                Enrollment enrollment = ((Enrollment) (session.get(Enrollment.class, data.getEnrollmentId())));
                Assert.assertEquals(3, sessionFactory().getStatistics().getEntityLoadCount());// enrollment + (section + student)

                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertTrue(Hibernate.isInitialized(enrollment.getOffering()));
                Assert.assertTrue(Hibernate.isInitialized(enrollment.getStudent()));
                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testCriteriaManyToOneFetchProfile() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                session.enableFetchProfile("enrollment.details");
                Enrollment enrollment = ((Enrollment) (session.createCriteria(Enrollment.class).uniqueResult()));
                Assert.assertEquals(3, sessionFactory().getStatistics().getEntityLoadCount());// enrollment + (section + student)

                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertTrue(Hibernate.isInitialized(enrollment.getOffering()));
                Assert.assertTrue(Hibernate.isInitialized(enrollment.getStudent()));
                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testLoadOneToManyFetchProfile() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                session.enableFetchProfile("offering.details");
                CourseOffering section = ((CourseOffering) (session.get(CourseOffering.class, data.getSectionId())));
                Assert.assertEquals(3, sessionFactory().getStatistics().getEntityLoadCount());// section + (enrollments + course)

                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertTrue(Hibernate.isInitialized(section.getEnrollments()));
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testLoadDeepFetchProfile() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                // enable both enrollment and offering detail profiles;
                // then loading the section/offering should fetch the enrollment
                // which in turn should fetch student (+ offering).
                session.enableFetchProfile("offering.details");
                session.enableFetchProfile("enrollment.details");
                CourseOffering section = ((CourseOffering) (session.get(CourseOffering.class, data.getSectionId())));
                Assert.assertEquals(4, sessionFactory().getStatistics().getEntityLoadCount());// section + (course + enrollments + (student))

                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertTrue(Hibernate.isInitialized(section.getEnrollments()));
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testLoadComponentDerefFetchProfile() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                session.enableFetchProfile("course.details");
                Course course = ((Course) (session.get(Course.class, data.getCourseId())));
                Assert.assertEquals(2, sessionFactory().getStatistics().getEntityLoadCount());// course + department

                Assert.assertEquals(0, sessionFactory().getStatistics().getEntityFetchCount());
                Assert.assertTrue(Hibernate.isInitialized(course.getCode().getDepartment()));
                session.getTransaction().commit();
                session.close();
            }
        });
    }

    @Test
    public void testHQL() {
        performWithStandardData(new JoinFetchProfileTest.TestCode() {
            public void perform(JoinFetchProfileTest.TestData data) {
                Session session = openSession();
                session.beginTransaction();
                session.enableFetchProfile("offering.details");
                session.enableFetchProfile("enrollment.details");
                List sections = session.createQuery("from CourseOffering").list();
                int sectionCount = sections.size();
                Assert.assertEquals("unexpected CourseOffering count", 1, sectionCount);
                Assert.assertEquals(4, sessionFactory().getStatistics().getEntityLoadCount());
                Assert.assertEquals(2, sessionFactory().getStatistics().getEntityFetchCount());
                session.getTransaction().commit();
                session.close();
            }
        });
    }
}

