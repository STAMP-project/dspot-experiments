/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id: SQLLoaderTest.java 11383 2007-04-02 15:34:02Z steve.ebersole@jboss.com $
 */
package org.hibernate.test.legacy;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.TimesTenDialect;
import org.hibernate.testing.DialectCheck;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


public class SQLLoaderTest extends LegacyTestCase {
    static int nextInt = 1;

    static long nextLong = 1;

    @Test
    public void testTS() throws Exception {
        Session session = openSession();
        Transaction txn = session.beginTransaction();
        Simple sim = new Simple(Long.valueOf(1));
        sim.setDate(new Date());
        session.save(sim);
        Query q = session.createSQLQuery("select {sim.*} from SimpleEntity {sim} where {sim}.date_ = ?").addEntity("sim", Simple.class);
        q.setTimestamp(0, sim.getDate());
        Assert.assertTrue(((q.list().size()) == 1));
        session.delete(sim);
        txn.commit();
        session.close();
    }

    @Test
    public void testFindBySQLStar() throws SQLException, HibernateException {
        Session session = openSession();
        session.beginTransaction();
        for (Object entity : session.createQuery("from Assignable").list()) {
            session.delete(entity);
        }
        for (Object entity : session.createQuery("from Category").list()) {
            session.delete(entity);
        }
        for (Object entity : session.createQuery("from Simple").list()) {
            session.delete(entity);
        }
        for (Object entity : session.createQuery("from A").list()) {
            session.delete(entity);
        }
        Category s = new Category();
        s.setName(String.valueOf(((SQLLoaderTest.nextLong)++)));
        session.save(s);
        Simple simple = new Simple(Long.valueOf(((SQLLoaderTest.nextLong)++)));
        simple.init();
        session.save(simple);
        A a = new A();
        session.save(a);
        B b = new B();
        session.save(b);
        session.flush();
        session.createSQLQuery("select {category.*} from category {category}").addEntity("category", Category.class).list();
        session.createSQLQuery("select {simple.*} from SimpleEntity {simple}").addEntity("simple", Simple.class).list();
        session.createSQLQuery("select {a.*} from TA {a}").addEntity("a", A.class).list();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testFindBySQLProperties() throws SQLException, HibernateException {
        Session session = openSession();
        session.beginTransaction();
        for (Object entity : session.createQuery("from Category").list()) {
            session.delete(entity);
        }
        Category s = new Category();
        s.setName(String.valueOf(((SQLLoaderTest.nextLong)++)));
        session.save(s);
        s = new Category();
        s.setName("WannaBeFound");
        session.flush();
        Query query = session.createSQLQuery("select {category.*} from category {category} where {category}.name = :name").addEntity("category", Category.class);
        query.setProperties(s);
        // query.setParameter("name", s.getName());
        query.list();
        query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in (:names)").addEntity("category", Category.class);
        String[] str = new String[]{ "WannaBeFound", "NotThere" };
        query.setParameterList("names", str);
        query.uniqueResult();
        query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in :names").addEntity("category", Category.class);
        query.setParameterList("names", str);
        query.uniqueResult();
        query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in (:names)").addEntity("category", Category.class);
        str = new String[]{ "WannaBeFound" };
        query.setParameterList("names", str);
        query.uniqueResult();
        query = session.createSQLQuery("select {category.*} from category {category} where {category}.name in :names").addEntity("category", Category.class);
        query.setParameterList("names", str);
        query.uniqueResult();
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testFindBySQLAssociatedObjects() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        for (Object entity : s.createQuery("from Assignable").list()) {
            s.delete(entity);
        }
        for (Object entity : s.createQuery("from Category").list()) {
            s.delete(entity);
        }
        Category c = new Category();
        c.setName("NAME");
        Assignable assn = new Assignable();
        assn.setId("i.d.");
        List l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List list = s.createSQLQuery("select {category.*} from category {category}").addEntity("category", Category.class).list();
        list.get(0);
        s.getTransaction().commit();
        s.close();
        if ((getDialect()) instanceof MySQLDialect) {
            return;
        }
        s = openSession();
        s.beginTransaction();
        Query query = s.getNamedQuery("namedsql");
        Assert.assertNotNull(query);
        list = query.list();
        Assert.assertNotNull(list);
        Object[] values = ((Object[]) (list.get(0)));
        Assert.assertNotNull(values[0]);
        Assert.assertNotNull(values[1]);
        Assert.assertTrue(("wrong type: " + (values[0].getClass())), ((values[0]) instanceof Category));
        Assert.assertTrue(("wrong type: " + (values[1].getClass())), ((values[1]) instanceof Assignable));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect(MySQLDialect.class)
    public void testPropertyResultSQL() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        for (Object entity : s.createQuery("from Assignable").list()) {
            s.delete(entity);
        }
        for (Object entity : s.createQuery("from Category").list()) {
            s.delete(entity);
        }
        Category c = new Category();
        c.setName("NAME");
        Assignable assn = new Assignable();
        assn.setId("i.d.");
        List l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Query query = s.getNamedQuery("nonaliasedsql");
        Assert.assertNotNull(query);
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertTrue(((list.get(0)) instanceof Category));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFindBySQLMultipleObject() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        for (Object entity : s.createQuery("from Assignable").list()) {
            s.delete(entity);
        }
        for (Object entity : s.createQuery("from Category").list()) {
            s.delete(entity);
        }
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Category c = new Category();
        c.setName("NAME");
        Assignable assn = new Assignable();
        assn.setId("i.d.");
        List l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.flush();
        c = new Category();
        c.setName("NAME2");
        assn = new Assignable();
        assn.setId("i.d.2");
        l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.flush();
        assn = new Assignable();
        assn.setId("i.d.3");
        s.save(assn);
        s.getTransaction().commit();
        s.close();
        if ((getDialect()) instanceof MySQLDialect) {
            return;
        }
        s = openSession();
        s.beginTransaction();
        String sql = "select {category.*}, {assignable.*} from category {category}, \"assign-able\" {assignable}";
        List list = s.createSQLQuery(sql).addEntity("category", Category.class).addEntity("assignable", Assignable.class).list();
        Assert.assertTrue(((list.size()) == 6));// crossproduct of 2 categories x 3 assignables

        Assert.assertTrue(((list.get(0)) instanceof Object[]));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFindBySQLParameters() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        for (Object entity : s.createQuery("from Assignable").list()) {
            s.delete(entity);
        }
        for (Object entity : s.createQuery("from Category").list()) {
            s.delete(entity);
        }
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Category c = new Category();
        c.setName("Good");
        Assignable assn = new Assignable();
        assn.setId("i.d.");
        List l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.flush();
        c = new Category();
        c.setName("Best");
        assn = new Assignable();
        assn.setId("i.d.2");
        l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.flush();
        c = new Category();
        c.setName("Better");
        assn = new Assignable();
        assn.setId("i.d.7");
        l = new ArrayList();
        l.add(c);
        assn.setCategories(l);
        c.setAssignable(assn);
        s.save(assn);
        s.flush();
        assn = new Assignable();
        assn.setId("i.d.3");
        s.save(assn);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Query basicParam = s.createSQLQuery("select {category.*} from category {category} where {category}.name = 'Best'").addEntity("category", Category.class);
        List list = basicParam.list();
        Assert.assertEquals(1, list.size());
        Query unnamedParam = s.createSQLQuery("select {category.*} from category {category} where {category}.name = ? or {category}.name = ?").addEntity("category", Category.class);
        unnamedParam.setString(0, "Good");
        unnamedParam.setString(1, "Best");
        list = unnamedParam.list();
        Assert.assertEquals(2, list.size());
        Query namedParam = s.createSQLQuery("select {category.*} from category {category} where ({category}.name=:firstCat or {category}.name=:secondCat)").addEntity("category", Category.class);
        namedParam.setString("firstCat", "Better");
        namedParam.setString("secondCat", "Best");
        list = namedParam.list();
        Assert.assertEquals(2, list.size());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect({ HSQLDialect.class, PostgreSQL81Dialect.class, PostgreSQLDialect.class })
    public void testEscapedJDBC() throws SQLException, HibernateException {
        Session session = openSession();
        session.beginTransaction();
        for (Object entity : session.createQuery("from A").list()) {
            session.delete(entity);
        }
        A savedA = new A();
        savedA.setName("Max");
        session.save(savedA);
        B savedB = new B();
        session.save(savedB);
        session.flush();
        int count = session.createQuery("from A").list().size();
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        Query query;
        if ((getDialect()) instanceof TimesTenDialect) {
            // TimesTen does not permit general expressions (like UPPER) in the second part of a LIKE expression,
            // so we execute a similar test
            query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, count_ as {a.count}, name as {a.name} from TA where {fn ucase(name)} like 'MAX'").addEntity("a", A.class);
        } else {
            query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, count_ as {a.count}, name as {a.name} from TA where {fn ucase(name)} like {fn ucase('max')}").addEntity("a", A.class);
        }
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertEquals(1, list.size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testDoubleAliasing() throws SQLException, HibernateException {
        Session session = openSession();
        session.beginTransaction();
        for (Object entity : session.createQuery("from A").list()) {
            session.delete(entity);
        }
        A savedA = new A();
        savedA.setName("Max");
        session.save(savedA);
        B savedB = new B();
        session.save(savedB);
        session.flush();
        int count = session.createQuery("from A").list().size();
        session.getTransaction().commit();
        session.close();
        session = openSession();
        session.beginTransaction();
        String sql = "select a.identifier_column as {a1.id}, " + (((((((("    a.clazz_discriminata as {a1.class}, " + "    a.count_ as {a1.count}, ") + "    a.name as {a1.name}, ") + "    b.identifier_column as {a2.id}, ") + "    b.clazz_discriminata as {a2.class}, ") + "    b.count_ as {a2.count}, ") + "    b.name as {a2.name} ") + "from TA a, TA b ") + "where a.identifier_column = b.identifier_column");
        Query query = session.createSQLQuery(sql).addEntity("a1", A.class).addEntity("a2", A.class);
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testEmbeddedCompositeProperties() throws SQLException, HibernateException {
        Session session = openSession();
        session.beginTransaction();
        Single s = new Single();
        s.setId("my id");
        s.setString("string 1");
        session.save(s);
        session.getTransaction().commit();
        session = openSession();
        session.beginTransaction();
        SQLQuery query = session.createSQLQuery("select {sing.*} from Single {sing}").addEntity("sing", Single.class);
        List list = query.list();
        Assert.assertTrue(((list.size()) == 1));
        session.clear();
        query = session.createSQLQuery("select {sing.*} from Single {sing} where sing.id = ?").addEntity("sing", Single.class);
        query.setString(0, "my id");
        list = query.list();
        Assert.assertTrue(((list.size()) == 1));
        session.clear();
        query = session.createSQLQuery("select s.id as {sing.id}, s.string_ as {sing.string}, s.prop as {sing.prop} from Single s where s.id = ?").addEntity("sing", Single.class);
        query.setString(0, "my id");
        list = query.list();
        Assert.assertTrue(((list.size()) == 1));
        session.clear();
        query = session.createSQLQuery("select s.id as {sing.id}, s.string_ as {sing.string}, s.prop as {sing.prop} from Single s where s.id = ?").addEntity("sing", Single.class);
        query.setString(0, "my id");
        list = query.list();
        Assert.assertTrue(((list.size()) == 1));
        session.getTransaction().commit();
        session.close();
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testReturnPropertyComponentRename() throws SQLException, HibernateException {
        // failure expected because this was a regression introduced previously which needs to get tracked down.
        Componentizable componentizable = setupComponentData();
        Session session = openSession();
        session.beginTransaction();
        Query namedQuery = session.getNamedQuery("queryComponentWithOtherColumn");
        List list = namedQuery.list();
        Assert.assertEquals(1, list.size());
        Assert.assertEquals("flakky comp", ((Componentizable) (list.get(0))).getComponent().getName());
        session.clear();
        session.delete(componentizable);
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testComponentStar() throws SQLException, HibernateException {
        componentTest("select {comp.*} from Componentizable comp");
    }

    @Test
    public void testComponentNoStar() throws SQLException, HibernateException {
        componentTest("select comp.id as {comp.id}, comp.nickName as {comp.nickName}, comp.name as {comp.component.name}, comp.subName as {comp.component.subComponent.subName}, comp.subName1 as {comp.component.subComponent.subName1} from Componentizable comp");
    }

    @Test
    @SkipForDialect(MySQLDialect.class)
    public void testFindSimpleBySQL() throws Exception {
        Session session = openSession();
        session.beginTransaction();
        Category s = new Category();
        s.setName(String.valueOf(((SQLLoaderTest.nextLong)++)));
        session.save(s);
        session.flush();
        Query query = session.createSQLQuery("select s.category_key_col as {category.id}, s.name as {category.name}, s.\"assign-able-id\" as {category.assignable} from {category} s").addEntity("category", Category.class);
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertTrue(((list.size()) > 0));
        Assert.assertTrue(((list.get(0)) instanceof Category));
        session.getTransaction().commit();
        session.close();
        // How do we handle objects with composite id's ? (such as Single)
    }

    @Test
    public void testFindBySQLSimpleByDiffSessions() throws Exception {
        Session session = openSession();
        session.beginTransaction();
        Category s = new Category();
        s.setName(String.valueOf(((SQLLoaderTest.nextLong)++)));
        session.save(s);
        session.getTransaction().commit();
        session.close();
        if ((getDialect()) instanceof MySQLDialect) {
            return;
        }
        session = openSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("select s.category_key_col as {category.id}, s.name as {category.name}, s.\"assign-able-id\" as {category.assignable} from {category} s").addEntity("category", Category.class);
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertTrue(((list.size()) > 0));
        Assert.assertTrue(((list.get(0)) instanceof Category));
        // How do we handle objects that does not have id property (such as Simple ?)
        // How do we handle objects with composite id's ? (such as Single)
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testFindBySQLDiscriminatedSameSession() throws Exception {
        Session session = openSession();
        session.beginTransaction();
        for (Object entity : session.createQuery("from A").list()) {
            session.delete(entity);
        }
        A savedA = new A();
        session.save(savedA);
        B savedB = new B();
        session.save(savedB);
        session.flush();
        Query query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, name as {a.name}, count_ as {a.count} from TA {a}").addEntity("a", A.class);
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertEquals(2, list.size());
        A a1 = ((A) (list.get(0)));
        A a2 = ((A) (list.get(1)));
        Assert.assertTrue(((a2 instanceof B) || (a1 instanceof B)));
        Assert.assertFalse(((a1 instanceof B) && (a2 instanceof B)));
        if (a1 instanceof B) {
            Assert.assertSame(a1, savedB);
            Assert.assertSame(a2, savedA);
        } else {
            Assert.assertSame(a2, savedB);
            Assert.assertSame(a1, savedA);
        }
        session.clear();
        List list2 = session.getNamedQuery("propertyResultDiscriminator").list();
        Assert.assertEquals(2, list2.size());
        session.getTransaction().commit();
        session.close();
    }

    @Test
    public void testFindBySQLDiscriminatedDiffSession() throws Exception {
        Session session = openSession();
        session.beginTransaction();
        for (Object entity : session.createQuery("from A").list()) {
            session.delete(entity);
        }
        A savedA = new A();
        session.save(savedA);
        B savedB = new B();
        session.save(savedB);
        session.getTransaction().commit();
        int count = session.createQuery("from A").list().size();
        session.close();
        session = openSession();
        session.beginTransaction();
        Query query = session.createSQLQuery("select identifier_column as {a.id}, clazz_discriminata as {a.class}, count_ as {a.count}, name as {a.name} from TA").addEntity("a", A.class);
        List list = query.list();
        Assert.assertNotNull(list);
        Assert.assertEquals(count, list.size());
        session.getTransaction().commit();
        session.close();
    }

    public static class DoubleQuoteDialect implements DialectCheck {
        @Override
        public boolean isMatch(Dialect dialect) {
            return ('"' == (dialect.openQuote())) && ('"' == (dialect.closeQuote()));
        }
    }

    // because the XML mapping defines the loader for CompositeIdId using a column name that needs to be quoted
    @Test
    @TestForIssue(jiraKey = "HHH-21")
    @RequiresDialectFeature(SQLLoaderTest.DoubleQuoteDialect.class)
    public void testCompositeIdId() throws SQLException, HibernateException {
        Session s = openSession();
        s.beginTransaction();
        CompositeIdId id = new CompositeIdId();
        id.setName("Max");
        id.setUser("c64");
        id.setId("games");
        s.save(id);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // having a composite id with one property named id works since the map used by sqlloader to map names to properties handles it.
        // NOTE : SYSTEM is an ANSI SQL defined keyword, so it gets quoted; so it needs to get quoted here too
        String sql = String.format(("select %1$s as {c.user}, " + (((("  id as {c.id}, name as {c.name}, " + "  foo as {c.composite.foo}, ") + "  bar as {c.composite.bar} ") + "from CompositeIdId ") + "where %1$s=? and id=?")), (((getDialect().openQuote()) + "user") + (getDialect().closeQuote())));
        SQLQuery query = s.createSQLQuery(sql).addEntity("c", CompositeIdId.class);
        query.setString(0, "c64");
        query.setString(1, "games");
        CompositeIdId id2 = ((CompositeIdId) (query.uniqueResult()));
        check(id, id2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        CompositeIdId useForGet = new CompositeIdId();
        useForGet.setUser("c64");
        useForGet.setId("games");
        // this doesn't work since the verification does not take column span into respect!
        CompositeIdId getted = ((CompositeIdId) (s.get(CompositeIdId.class, useForGet)));
        check(id, getted);
        s.getTransaction().commit();
        s.close();
    }
}

