/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.ecid;


import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class EmbeddedCompositeIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testMerge() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Course uc = new UniversityCourse("mat2000", "Monash", "second year maths", 0);
        Course c = new Course("eng5000", "BHS", "grade 5 english");
        s.persist(uc);
        s.persist(c);
        t.commit();
        s.close();
        c.setDescription("Grade 5 English");
        uc.setDescription("Second year mathematics");
        s = openSession();
        t = s.beginTransaction();
        s.merge(c);
        s.merge(uc);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(c);
        s.delete(uc);
        t.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-799")
    public void testMerging() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Course course = new Course("EN-101", "BA", "preparatory english");
        s.persist(course);
        t.commit();
        s.close();
        String newDesc = "basic preparatory english";
        course.setDescription(newDesc);
        s = openSession();
        t = s.beginTransaction();
        Course c = ((Course) (s.merge(course)));
        Assert.assertEquals("description not merged", newDesc, c.getDescription());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Course cid = new Course("EN-101", "BA", null);
        course = ((Course) (s.get(Course.class, cid)));
        Assert.assertEquals("description not merged", newDesc, course.getDescription());
        s.delete(course);
        t.commit();
        s.close();
    }

    @Test
    public void testPolymorphism() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Course uc = new UniversityCourse("mat2000", "Monash", "second year maths", 0);
        Course c = new Course("eng5000", "BHS", "grade 5 english");
        s.persist(uc);
        s.persist(c);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Course ucid = new Course("mat2000", "Monash", null);
        Course cid = new Course("eng5000", "BHS", null);
        Course luc = ((Course) (s.load(Course.class, ucid)));
        Course lc = ((Course) (s.load(Course.class, cid)));
        Assert.assertFalse(Hibernate.isInitialized(luc));
        Assert.assertFalse(Hibernate.isInitialized(lc));
        Assert.assertEquals(UniversityCourse.class, Hibernate.getClass(luc));
        Assert.assertEquals(Course.class, Hibernate.getClass(lc));
        Assert.assertSame(getHibernateLazyInitializer().getImplementation(), cid);
        Assert.assertEquals(c.getCourseCode(), "eng5000");
        Assert.assertEquals(uc.getCourseCode(), "mat2000");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        ucid = new Course("mat2000", "Monash", null);
        cid = new Course("eng5000", "BHS", null);
        luc = ((Course) (s.get(Course.class, ucid)));
        lc = ((Course) (s.get(Course.class, cid)));
        Assert.assertTrue(Hibernate.isInitialized(luc));
        Assert.assertTrue(Hibernate.isInitialized(lc));
        Assert.assertEquals(UniversityCourse.class, Hibernate.getClass(luc));
        Assert.assertEquals(Course.class, Hibernate.getClass(lc));
        Assert.assertSame(lc, cid);
        Assert.assertEquals(c.getCourseCode(), "eng5000");
        Assert.assertEquals(uc.getCourseCode(), "mat2000");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        List list = s.createQuery("from Course order by courseCode").list();
        Assert.assertTrue(((list.get(0)) instanceof Course));
        Assert.assertTrue(((list.get(1)) instanceof UniversityCourse));
        c = ((Course) (list.get(0)));
        uc = ((UniversityCourse) (list.get(1)));
        Assert.assertEquals(c.getCourseCode(), "eng5000");
        Assert.assertEquals(uc.getCourseCode(), "mat2000");
        t.commit();
        s.close();
        c.setDescription("Grade 5 English");
        uc.setDescription("Second year mathematics");
        s = openSession();
        t = s.beginTransaction();
        s.saveOrUpdate(c);
        s.saveOrUpdate(uc);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(c);
        s.delete(uc);
        t.commit();
        s.close();
    }
}

