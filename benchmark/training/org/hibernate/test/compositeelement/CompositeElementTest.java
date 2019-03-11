/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.compositeelement;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class CompositeElementTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testHandSQL() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Child c = new Child("Child One");
        Parent p = new Parent("Parent");
        p.getChildren().add(c);
        c.setParent(p);
        s.save(p);
        s.flush();
        p.getChildren().remove(c);
        c.setParent(null);
        s.flush();
        p.getChildren().add(c);
        c.setParent(p);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("select distinct p from Parent p join p.children c where c.name like 'Child%'").uniqueResult();
        s.clear();
        s.createQuery("select new Child(c.name) from Parent p left outer join p.children c where c.name like 'Child%'").uniqueResult();
        s.clear();
        // s.createQuery("select c from Parent p left outer join p.children c where c.name like 'Child%'").uniqueResult(); //we really need to be able to do this!
        s.clear();
        p = ((Parent) (s.createQuery("from Parent p left join fetch p.children").uniqueResult()));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(p);
        t.commit();
        s.close();
    }

    @Test
    public void testCustomColumnReadAndWrite() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Child c = new Child("Child One");
        c.setPosition(1);
        Parent p = new Parent("Parent");
        p.getChildren().add(c);
        c.setParent(p);
        s.save(p);
        s.flush();
        // Oracle returns BigDecimaal while other dialects return Integer;
        // casting to Number so it works on all dialects
        Number sqlValue = ((Number) (s.createSQLQuery("select child_position from ParentChild c where c.name='Child One'").uniqueResult()));
        Assert.assertEquals(0, sqlValue.intValue());
        Integer hqlValue = ((Integer) (s.createQuery("select c.position from Parent p join p.children c where p.name='Parent'").uniqueResult()));
        Assert.assertEquals(1, hqlValue.intValue());
        p = ((Parent) (s.createCriteria(Parent.class).add(Restrictions.eq("name", "Parent")).uniqueResult()));
        c = ((Child) (p.getChildren().iterator().next()));
        Assert.assertEquals(1, c.getPosition());
        p = ((Parent) (s.createQuery("from Parent p join p.children c where c.position = 1").uniqueResult()));
        c = ((Child) (p.getChildren().iterator().next()));
        Assert.assertEquals(1, c.getPosition());
        c.setPosition(2);
        s.flush();
        sqlValue = ((Number) (s.createSQLQuery("select child_position from ParentChild c where c.name='Child One'").uniqueResult()));
        Assert.assertEquals(1, sqlValue.intValue());
        s.delete(p);
        t.commit();
        s.close();
    }
}

