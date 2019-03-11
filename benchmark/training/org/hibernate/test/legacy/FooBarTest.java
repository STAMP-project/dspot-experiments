/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.legacy;


import DialectChecks.SupportsCircularCascadeDeleteCheck;
import DialectChecks.SupportsEmptyInListCheck;
import DialectChecks.SupportsNoColumnInsert;
import FetchMode.JOIN;
import FetchMode.SELECT;
import FlushMode.MANUAL;
import LockMode.READ;
import LockMode.UPGRADE;
import LockMode.WRITE;
import StandardBasicTypes.CHARACTER;
import StandardBasicTypes.DATE;
import StandardBasicTypes.LONG;
import StandardBasicTypes.STRING;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.QueryException;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.DerbyDialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.InterbaseDialect;
import org.hibernate.dialect.MckoiDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.Oracle12cDialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PointbaseDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.dialect.SAPDBDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.dialect.SybaseDialect;
import org.hibernate.dialect.TimesTenDialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.SerializationHelper;
import org.hibernate.internal.util.collections.JoinedIterator;
import org.hibernate.jdbc.AbstractWork;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.env.ConnectionProviderBuilder;
import org.junit.Assert;
import org.junit.Test;

import static java.util.Locale.ROOT;


@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class FooBarTest extends LegacyTestCase {
    @Test
    public void testSaveOrUpdateCopyAny() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Bar bar = new Bar();
        One one = new One();
        bar.setObject(one);
        s.save(bar);
        GlarchProxy g = bar.getComponent().getGlarch();
        bar.getComponent().setGlarch(null);
        s.delete(g);
        s.flush();
        Assert.assertTrue(s.contains(one));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Bar bar2 = ((Bar) (s.merge(bar)));
        s.flush();
        s.delete(bar2);
        s.flush();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRefreshProxy() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Glarch g = new Glarch();
        Serializable gid = s.save(g);
        s.flush();
        s.clear();
        GlarchProxy gp = ((GlarchProxy) (s.load(Glarch.class, gid)));
        gp.getName();// force init

        s.refresh(gp);
        s.delete(gp);
        s.flush();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(value = SupportsCircularCascadeDeleteCheck.class, comment = "db/dialect does not support circular cascade delete constraints")
    public void testOnCascadeDelete() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.subs = new ArrayList();
        Baz sub = new Baz();
        sub.superBaz = baz;
        baz.subs.add(sub);
        s.save(baz);
        s.flush();
        Assert.assertTrue(((s.createQuery("from Baz").list().size()) == 2));
        s.getTransaction().commit();
        s.beginTransaction();
        s.delete(baz);
        s.getTransaction().commit();
        s.beginTransaction();
        Assert.assertTrue(((s.createQuery("from Baz").list().size()) == 0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveFromIdbag() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setByteBag(new ArrayList());
        byte[] bytes = new byte[]{ 12, 13 };
        baz.getByteBag().add(new byte[]{ 10, 45 });
        baz.getByteBag().add(bytes);
        baz.getByteBag().add(new byte[]{ 1, 11 });
        baz.getByteBag().add(new byte[]{ 12 });
        s.save(baz);
        s.flush();
        baz.getByteBag().remove(bytes);
        s.flush();
        baz.getByteBag().add(bytes);
        s.flush();
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLoad() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Qux q = new Qux();
        s.save(q);
        BarProxy b = new Bar();
        s.save(b);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        q = ((Qux) (s.load(Qux.class, q.getKey())));
        b = ((BarProxy) (s.load(Foo.class, b.getKey())));
        b.getKey();
        Assert.assertFalse(Hibernate.isInitialized(b));
        b.getBarString();
        Assert.assertTrue(Hibernate.isInitialized(b));
        BarProxy b2 = ((BarProxy) (s.load(Bar.class, b.getKey())));
        Qux q2 = ((Qux) (s.load(Qux.class, q.getKey())));
        Assert.assertTrue("loaded same object", (q == q2));
        Assert.assertTrue("loaded same object", (b == b2));
        Assert.assertTrue(((Math.round(b.getFormula())) == ((b.getInt()) / 2)));
        s.delete(q2);
        s.delete(b2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testJoin() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        foo.setJoinedProp("foo");
        s.save(foo);
        s.flush();
        foo.setJoinedProp("bar");
        s.flush();
        String fid = foo.getKey();
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Foo foo2 = new Foo();
        foo2.setJoinedProp("foo");
        s.save(foo2);
        s.createQuery("select foo.id from Foo foo where foo.joinedProp = 'foo'").list();
        Assert.assertNull(s.get(Foo.class, fid));
        s.delete(foo2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testDereferenceLazyCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setFooSet(new HashSet());
        Foo foo = new Foo();
        baz.getFooSet().add(foo);
        s.save(foo);
        s.save(baz);
        foo.setBytes("foobar".getBytes());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = ((Foo) (s.get(Foo.class, foo.getKey())));
        Assert.assertTrue(Hibernate.isInitialized(foo.getBytes()));
        Assert.assertTrue(((foo.getBytes().length) == 6));
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getFooSet().size()) == 1));
        s.getTransaction().commit();
        s.close();
        sessionFactory().getCache().evictCollectionRegion("org.hibernate.test.legacy.Baz.fooSet");
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertFalse(Hibernate.isInitialized(baz.getFooSet()));
        baz.setFooSet(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = ((Foo) (s.get(Foo.class, foo.getKey())));
        Assert.assertTrue(((foo.getBytes().length) == 6));
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertFalse(Hibernate.isInitialized(baz.getFooSet()));
        Assert.assertTrue(((baz.getFooSet().size()) == 0));
        s.delete(baz);
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testMoveLazyCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        Baz baz2 = new Baz();
        baz.setFooSet(new HashSet());
        Foo foo = new Foo();
        baz.getFooSet().add(foo);
        s.save(foo);
        s.save(baz);
        s.save(baz2);
        foo.setBytes("foobar".getBytes());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = ((Foo) (s.get(Foo.class, foo.getKey())));
        Assert.assertTrue(Hibernate.isInitialized(foo.getBytes()));
        Assert.assertTrue(((foo.getBytes().length) == 6));
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getFooSet().size()) == 1));
        s.getTransaction().commit();
        s.close();
        sessionFactory().getCache().evictCollectionRegion("org.hibernate.test.legacy.Baz.fooSet");
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertFalse(Hibernate.isInitialized(baz.getFooSet()));
        baz2 = ((Baz) (s.get(Baz.class, baz2.getCode())));
        baz2.setFooSet(baz.getFooSet());
        baz.setFooSet(null);
        Assert.assertFalse(Hibernate.isInitialized(baz2.getFooSet()));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = ((Foo) (s.get(Foo.class, foo.getKey())));
        Assert.assertTrue(((foo.getBytes().length) == 6));
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        baz2 = ((Baz) (s.get(Baz.class, baz2.getCode())));
        Assert.assertFalse(Hibernate.isInitialized(baz.getFooSet()));
        Assert.assertTrue(((baz.getFooSet().size()) == 0));
        Assert.assertTrue(Hibernate.isInitialized(baz2.getFooSet()));// fooSet has batching enabled

        Assert.assertTrue(((baz2.getFooSet().size()) == 1));
        s.delete(baz);
        s.delete(baz2);
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testCriteriaCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz bb = ((Baz) (s.createCriteria(Baz.class).uniqueResult()));
        Assert.assertTrue((bb == null));
        Baz baz = new Baz();
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Baz b = ((Baz) (s.createCriteria(Baz.class).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(b.getTopGlarchez()));
        Assert.assertTrue(((b.getTopGlarchez().size()) == 0));
        s.delete(b);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testQuery() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        Foo foo2 = new Foo();
        s.save(foo2);
        foo.setFoo(foo2);
        List list = s.createQuery("from Foo foo inner join fetch foo.foo").list();
        Foo foof = ((Foo) (list.get(0)));
        Assert.assertTrue(Hibernate.isInitialized(foof.getFoo()));
        s.createQuery("from Baz baz left outer join fetch baz.fooToGlarch").list();
        list = s.createQuery("select foo, bar from Foo foo left outer join foo.foo bar where foo = ?").setParameter(0, foo, s.getTypeHelper().entity(Foo.class)).list();
        Object[] row1 = ((Object[]) (list.get(0)));
        Assert.assertTrue((((row1[0]) == foo) && ((row1[1]) == foo2)));
        s.createQuery("select foo.foo.foo.string from Foo foo where foo.foo = 'bar'").list();
        s.createQuery("select foo.foo.foo.foo.string from Foo foo where foo.foo = 'bar'").list();
        s.createQuery("select foo from Foo foo where foo.foo.foo = 'bar'").list();
        s.createQuery("select foo.foo.foo.foo.string from Foo foo where foo.foo.foo = 'bar'").list();
        s.createQuery("select foo.foo.foo.string from Foo foo where foo.foo.foo.foo.string = 'bar'").list();
        if (!((getDialect()) instanceof HSQLDialect))
            s.createQuery("select foo.string from Foo foo where foo.foo.foo.foo = foo.foo.foo").list();

        s.createQuery("select foo.string from Foo foo where foo.foo.foo = 'bar' and foo.foo.foo.foo = 'baz'").list();
        s.createQuery("select foo.string from Foo foo where foo.foo.foo.foo.string = 'a' and foo.foo.string = 'b'").list();
        s.createQuery("from Bar bar, foo in elements(bar.baz.fooArray)").list();
        // s.find("from Baz as baz where baz.topComponents[baz].name = 'bazzz'");
        if (((getDialect()) instanceof DB2Dialect) && (!((getDialect()) instanceof DerbyDialect))) {
            s.createQuery("from Foo foo where lower( foo.foo.string ) = 'foo'").list();
            s.createQuery("from Foo foo where lower( (foo.foo.string || 'foo') || 'bar' ) = 'foo'").list();
            s.createQuery("from Foo foo where repeat( (foo.foo.string || 'foo') || 'bar', 2 ) = 'foo'").list();
            s.createQuery("from Bar foo where foo.foo.integer is not null and repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'").list();
            s.createQuery("from Bar foo where foo.foo.integer is not null or repeat( (foo.foo.string || 'foo') || 'bar', (5+5)/2 ) = 'foo'").list();
        }
        if ((getDialect()) instanceof SybaseDialect) {
            s.createQuery("select baz from Baz as baz join baz.fooArray foo group by baz order by sum(foo.float)").iterate();
        }
        s.createQuery("from Foo as foo where foo.component.glarch.name is not null").list();
        s.createQuery("from Foo as foo left outer join foo.component.glarch as glarch where glarch.name = 'foo'").list();
        list = s.createQuery("from Foo").list();
        Assert.assertTrue((((list.size()) == 2) && ((list.get(0)) instanceof FooProxy)));
        list = s.createQuery("from Foo foo left outer join foo.foo").list();
        Assert.assertTrue((((list.size()) == 2) && ((((Object[]) (list.get(0)))[0]) instanceof FooProxy)));
        s.createQuery("from Bar, Bar").list();
        s.createQuery("from Foo, Bar").list();
        s.createQuery("from Baz baz left join baz.fooToGlarch, Bar bar join bar.foo").list();
        s.createQuery("from Baz baz left join baz.fooToGlarch join baz.fooSet").list();
        s.createQuery("from Baz baz left join baz.fooToGlarch join fetch baz.fooSet foo left join fetch foo.foo").list();
        list = s.createQuery("from Foo foo where foo.string='osama bin laden' and foo.boolean = true order by foo.string asc, foo.component.count desc").list();
        Assert.assertTrue("empty query", ((list.size()) == 0));
        Iterator iter = s.createQuery("from Foo foo where foo.string='osama bin laden' order by foo.string asc, foo.component.count desc").iterate();
        Assert.assertTrue("empty iterator", (!(iter.hasNext())));
        list = s.createQuery("select foo.foo from Foo foo").list();
        Assert.assertTrue("query", ((list.size()) == 1));
        Assert.assertTrue("returned object", ((list.get(0)) == (foo.getFoo())));
        foo.getFoo().setFoo(foo);
        foo.setString("fizard");
        // The following test is disabled for databases with no subselects...also for Interbase (not sure why).
        if ((((((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect))) && (!((getDialect()) instanceof MckoiDialect))) && (!((getDialect()) instanceof SAPDBDialect))) && (!((getDialect()) instanceof PointbaseDialect))) && (!((getDialect()) instanceof DerbyDialect))) {
            // && !db.equals("weblogic") {
            if (!((getDialect()) instanceof InterbaseDialect)) {
                list = s.createQuery("from Foo foo where ? = some elements(foo.component.importantDates)").setParameter(0, foo.getTimestamp(), DATE).list();
                Assert.assertTrue("component query", ((list.size()) == 2));
            }
            if (!((getDialect()) instanceof TimesTenDialect)) {
                list = s.createQuery("from Foo foo where size(foo.component.importantDates) = 3").list();// WAS: 4

                Assert.assertTrue("component query", ((list.size()) == 2));
                list = s.createQuery("from Foo foo where 0 = size(foo.component.importantDates)").list();
                Assert.assertTrue("component query", ((list.size()) == 0));
            }
            list = s.createQuery("from Foo foo where exists elements(foo.component.importantDates)").list();
            Assert.assertTrue("component query", ((list.size()) == 2));
            s.createQuery("from Foo foo where not exists (from Bar bar where bar.id = foo.id)").list();
            s.createQuery("select foo.foo from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)").list();
            s.createQuery("select foo.foo from Foo foo where foo = some(from Foo x where (x.long > foo.foo.long))").list();
            if (!((getDialect()) instanceof TimesTenDialect)) {
                s.createQuery("select foo.foo from Foo foo where foo.long = some( select max(x.long) from Foo x where (x.long > foo.foo.long) group by x.foo )").list();
            }
            s.createQuery("from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long) and foo.foo.string='baz'").list();
            s.createQuery("from Foo foo where foo.foo.string='baz' and foo = some(select x from Foo x where x.long > foo.foo.long)").list();
            s.createQuery("from Foo foo where foo = some(select x from Foo x where x.long > foo.foo.long)").list();
            s.createQuery("select foo.string, foo.date, foo.foo.string, foo.id from Foo foo, Baz baz where foo in elements(baz.fooArray) and foo.string like 'foo'").iterate();
        }
        list = s.createQuery("from Foo foo where foo.component.count is null order by foo.component.count").list();
        Assert.assertTrue("component query", ((list.size()) == 0));
        list = s.createQuery("from Foo foo where foo.component.name='foo'").list();
        Assert.assertTrue("component query", ((list.size()) == 2));
        list = s.createQuery("select distinct foo.component.name, foo.component.name from Foo foo where foo.component.name='foo'").list();
        Assert.assertTrue("component query", ((list.size()) == 1));
        list = s.createQuery("select distinct foo.component.name, foo.id from Foo foo where foo.component.name='foo'").list();
        Assert.assertTrue("component query", ((list.size()) == 2));
        list = s.createQuery("select foo.foo from Foo foo").list();
        Assert.assertTrue("query", ((list.size()) == 2));
        list = s.createQuery("from Foo foo where foo.id=?").setParameter(0, foo.getKey(), STRING).list();
        Assert.assertTrue("id query", ((list.size()) == 1));
        list = s.createQuery("from Foo foo where foo.key=?").setParameter(0, foo.getKey(), STRING).list();
        Assert.assertTrue("named id query", ((list.size()) == 1));
        Assert.assertTrue("id query", ((list.get(0)) == foo));
        list = s.createQuery("select foo.foo from Foo foo where foo.string='fizard'").list();
        Assert.assertTrue("query", ((list.size()) == 1));
        Assert.assertTrue("returned object", ((list.get(0)) == (foo.getFoo())));
        list = s.createQuery("from Foo foo where foo.component.subcomponent.name='bar'").list();
        Assert.assertTrue("components of components", ((list.size()) == 2));
        list = s.createQuery("select foo.foo from Foo foo where foo.foo.id=?").setParameter(0, foo.getFoo().getKey(), STRING).list();
        Assert.assertTrue("by id query", ((list.size()) == 1));
        Assert.assertTrue("by id returned object", ((list.get(0)) == (foo.getFoo())));
        s.createQuery("from Foo foo where foo.foo = ?").setParameter(0, foo.getFoo(), s.getTypeHelper().entity(Foo.class)).list();
        Assert.assertTrue((!(s.createQuery("from Bar bar where bar.string='a string' or bar.string='a string'").iterate().hasNext())));
        iter = s.createQuery("select foo.component.name, elements(foo.component.importantDates) from Foo foo where foo.foo.id=?").setParameter(0, foo.getFoo().getKey(), STRING).iterate();
        int i = 0;
        while (iter.hasNext()) {
            i++;
            Object[] row = ((Object[]) (iter.next()));
            Assert.assertTrue((((row[0]) instanceof String) && (((row[1]) == null) || ((row[1]) instanceof Date))));
        } 
        Assert.assertTrue((i == 3));// WAS: 4

        iter = s.createQuery("select max( elements(foo.component.importantDates) ) from Foo foo group by foo.id").iterate();
        Assert.assertTrue(((iter.next()) instanceof Date));
        list = s.createQuery((("select foo.foo.foo.foo from Foo foo, Foo foo2 where" + (" foo = foo2.foo and not not ( not foo.string='fizard' )" + " and foo2.string between 'a' and (foo.foo.string)")) + (((((getDialect()) instanceof HSQLDialect) || ((getDialect()) instanceof InterbaseDialect)) || ((getDialect()) instanceof TimesTenDialect)) || ((getDialect()) instanceof org.hibernate.dialect.TeradataDialect) ? " and ( foo2.string in ( 'fiz', 'blah') or 1=1 )" : " and ( foo2.string in ( 'fiz', 'blah', foo.foo.string, foo.string, foo2.string ) )"))).list();
        Assert.assertTrue("complex query", ((list.size()) == 1));
        Assert.assertTrue("returned object", ((list.get(0)) == foo));
        foo.setString("from BoogieDown  -tinsel town  =!@#$^&*())");
        list = s.createQuery("from Foo foo where foo.string='from BoogieDown  -tinsel town  =!@#$^&*())'").list();
        Assert.assertTrue("single quotes", ((list.size()) == 1));
        list = s.createQuery("from Foo foo where not foo.string='foo''bar'").list();
        Assert.assertTrue("single quotes", ((list.size()) == 2));
        list = s.createQuery("from Foo foo where foo.component.glarch.next is null").list();
        Assert.assertTrue("query association in component", ((list.size()) == 2));
        Bar bar = new Bar();
        Baz baz = new Baz();
        baz.setDefaults();
        bar.setBaz(baz);
        baz.setManyToAny(new ArrayList());
        baz.getManyToAny().add(bar);
        baz.getManyToAny().add(foo);
        s.save(bar);
        s.save(baz);
        list = s.createQuery(" from Bar bar where bar.baz.count=667 and bar.baz.count!=123 and not bar.baz.name='1-E-1'").list();
        Assert.assertTrue("query many-to-one", ((list.size()) == 1));
        list = s.createQuery(" from Bar i where i.baz.name='Bazza'").list();
        Assert.assertTrue("query many-to-one", ((list.size()) == 1));
        Iterator rs = s.createQuery("select count(distinct foo.foo) from Foo foo").iterate();
        Assert.assertTrue("count", ((((Long) (rs.next())).longValue()) == 2));
        Assert.assertTrue((!(rs.hasNext())));
        rs = s.createQuery("select count(foo.foo.boolean) from Foo foo").iterate();
        Assert.assertTrue("count", ((((Long) (rs.next())).longValue()) == 2));
        Assert.assertTrue((!(rs.hasNext())));
        rs = s.createQuery("select count(*), foo.int from Foo foo group by foo.int").iterate();
        Assert.assertTrue("count(*) group by", ((Object[]) (rs.next()))[0].equals(new Long(3)));
        Assert.assertTrue((!(rs.hasNext())));
        rs = s.createQuery("select sum(foo.foo.int) from Foo foo").iterate();
        Assert.assertTrue("sum", ((((Long) (rs.next())).longValue()) == 4));
        Assert.assertTrue((!(rs.hasNext())));
        rs = s.createQuery("select count(foo) from Foo foo where foo.id=?").setParameter(0, foo.getKey(), STRING).iterate();
        Assert.assertTrue("id query count", ((((Long) (rs.next())).longValue()) == 1));
        Assert.assertTrue((!(rs.hasNext())));
        s.createQuery("from Foo foo where foo.boolean = ?").setParameter(0, new Boolean(true), StandardBasicTypes.BOOLEAN).list();
        s.createQuery("select new Foo(fo.x) from Fo fo").list();
        s.createQuery("select new Foo(fo.integer) from Foo fo").list();
        list = // .setComment("projection test")
        s.createQuery("select new Foo(fo.x) from Foo fo").setCacheable(true).list();
        Assert.assertTrue(((list.size()) == 3));
        list = // .setComment("projection test 2")
        s.createQuery("select new Foo(fo.x) from Foo fo").setCacheable(true).list();
        Assert.assertTrue(((list.size()) == 3));
        rs = s.createQuery("select new Foo(fo.x) from Foo fo").iterate();
        Assert.assertTrue("projection iterate (results)", rs.hasNext());
        Assert.assertTrue("projection iterate (return check)", Foo.class.isAssignableFrom(rs.next().getClass()));
        ScrollableResults sr = s.createQuery("select new Foo(fo.x) from Foo fo").scroll();
        Assert.assertTrue("projection scroll (results)", sr.next());
        Assert.assertTrue("projection scroll (return check)", Foo.class.isAssignableFrom(sr.get(0).getClass()));
        list = s.createQuery("select foo.long, foo.component.name, foo, foo.foo from Foo foo").list();
        rs = list.iterator();
        int count = 0;
        while (rs.hasNext()) {
            count++;
            Object[] row = ((Object[]) (rs.next()));
            Assert.assertTrue(((row[0]) instanceof Long));
            Assert.assertTrue(((row[1]) instanceof String));
            Assert.assertTrue(((row[2]) instanceof Foo));
            Assert.assertTrue(((row[3]) instanceof Foo));
        } 
        Assert.assertTrue((count != 0));
        list = s.createQuery("select avg(foo.float), max(foo.component.name), count(distinct foo.id) from Foo foo").list();
        rs = list.iterator();
        count = 0;
        while (rs.hasNext()) {
            count++;
            Object[] row = ((Object[]) (rs.next()));
            Assert.assertTrue(((row[0]) instanceof Double));
            Assert.assertTrue(((row[1]) instanceof String));
            Assert.assertTrue(((row[2]) instanceof Long));
        } 
        Assert.assertTrue((count != 0));
        list = s.createQuery("select foo.long, foo.component, foo, foo.foo from Foo foo").list();
        rs = list.iterator();
        count = 0;
        while (rs.hasNext()) {
            count++;
            Object[] row = ((Object[]) (rs.next()));
            Assert.assertTrue(((row[0]) instanceof Long));
            Assert.assertTrue(((row[1]) instanceof FooComponent));
            Assert.assertTrue(((row[2]) instanceof Foo));
            Assert.assertTrue(((row[3]) instanceof Foo));
        } 
        Assert.assertTrue((count != 0));
        s.save(new Holder("ice T"));
        s.save(new Holder("ice cube"));
        Assert.assertTrue(((s.createQuery("from java.lang.Object as o").list().size()) == 15));
        Assert.assertTrue(((s.createQuery("from Named").list().size()) == 7));
        Assert.assertTrue(((s.createQuery("from Named n where n.name is not null").list().size()) == 4));
        iter = s.createQuery("from Named n").iterate();
        while (iter.hasNext()) {
            Assert.assertTrue(((iter.next()) instanceof Named));
        } 
        s.save(new Holder("bar"));
        iter = s.createQuery("from Named n0, Named n1 where n0.name = n1.name").iterate();
        int cnt = 0;
        while (iter.hasNext()) {
            Object[] row = ((Object[]) (iter.next()));
            if ((row[0]) != (row[1]))
                cnt++;

        } 
        if (!((getDialect()) instanceof HSQLDialect)) {
            Assert.assertTrue((cnt == 2));
            Assert.assertTrue(((s.createQuery("from Named n0, Named n1 where n0.name = n1.name").list().size()) == 7));
        }
        Query qu = s.createQuery("from Named n where n.name = :name");
        qu.getReturnTypes();
        qu.getNamedParameters();
        iter = s.createQuery("from java.lang.Object").iterate();
        int c = 0;
        while (iter.hasNext()) {
            iter.next();
            c++;
        } 
        Assert.assertTrue((c == 16));
        s.createQuery("select baz.code, min(baz.count) from Baz baz group by baz.code").iterate();
        iter = s.createQuery("selecT baz from Baz baz where baz.stringDateMap['foo'] is not null or baz.stringDateMap['bar'] = ?").setParameter(0, new Date(), DATE).iterate();
        Assert.assertFalse(iter.hasNext());
        list = s.createQuery("select baz from Baz baz where baz.stringDateMap['now'] is not null").list();
        Assert.assertTrue(((list.size()) == 1));
        list = s.createQuery("select baz from Baz baz where baz.stringDateMap['now'] is not null and baz.stringDateMap['big bang'] < baz.stringDateMap['now']").list();
        Assert.assertTrue(((list.size()) == 1));
        list = s.createQuery("select index(date) from Baz baz join baz.stringDateMap date").list();
        System.out.println(list);
        Assert.assertTrue(((list.size()) == 2));
        s.createQuery("from Foo foo where foo.integer not between 1 and 5 and foo.string not in ('cde', 'abc') and foo.string is not null and foo.integer<=3").list();
        s.createQuery("from Baz baz inner join baz.collectionComponent.nested.foos foo where foo.string is null").list();
        if ((((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof MckoiDialect))) && (!((getDialect()) instanceof SAPDBDialect))) && (!((getDialect()) instanceof PointbaseDialect))) {
            s.createQuery("from Baz baz inner join baz.fooSet where '1' in (from baz.fooSet foo where foo.string is not null)").list();
            s.createQuery("from Baz baz where 'a' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)").list();
            s.createQuery("from Baz baz where 'b' in elements(baz.collectionComponent.nested.foos) and 1.0 in elements(baz.collectionComponent.nested.floats)").list();
        }
        s.createQuery("from Foo foo join foo.foo where foo.foo in ('1','2','3')").list();
        if (!((getDialect()) instanceof HSQLDialect))
            s.createQuery("from Foo foo left join foo.foo where foo.foo in ('1','2','3')").list();

        s.createQuery("select foo.foo from Foo foo where foo.foo in ('1','2','3')").list();
        s.createQuery("select foo.foo.string from Foo foo where foo.foo in ('1','2','3')").list();
        s.createQuery("select foo.foo.string from Foo foo where foo.foo.string in ('1','2','3')").list();
        s.createQuery("select foo.foo.long from Foo foo where foo.foo.string in ('1','2','3')").list();
        s.createQuery("select count(*) from Foo foo where foo.foo.string in ('1','2','3') or foo.foo.long in (1,2,3)").list();
        s.createQuery("select count(*) from Foo foo where foo.foo.string in ('1','2','3') group by foo.foo.long").list();
        s.createQuery("from Foo foo1 left join foo1.foo foo2 left join foo2.foo where foo1.string is not null").list();
        s.createQuery("from Foo foo1 left join foo1.foo.foo where foo1.string is not null").list();
        s.createQuery("from Foo foo1 left join foo1.foo foo2 left join foo1.foo.foo foo3 where foo1.string is not null").list();
        s.createQuery("select foo.formula from Foo foo where foo.formula > 0").list();
        int len = s.createQuery("from Foo as foo join foo.foo as foo2 where foo2.id >'a' or foo2.id <'a'").list().size();
        Assert.assertTrue((len == 2));
        for (Object entity : s.createQuery("from Holder").list()) {
            s.delete(entity);
        }
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        baz = ((Baz) (s.createQuery("from Baz baz left outer join fetch baz.manyToAny").uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(baz.getManyToAny()));
        Assert.assertTrue(((baz.getManyToAny().size()) == 2));
        BarProxy barp = ((BarProxy) (baz.getManyToAny().get(0)));
        s.createQuery("from Baz baz join baz.manyToAny").list();
        Assert.assertTrue(((s.createQuery("select baz from Baz baz join baz.manyToAny a where index(a) = 0").list().size()) == 1));
        FooProxy foop = ((FooProxy) (s.get(Foo.class, foo.getKey())));
        Assert.assertTrue((foop == (baz.getManyToAny().get(1))));
        barp.setBaz(baz);
        Assert.assertTrue(((s.createQuery("select bar from Bar bar where bar.baz.stringDateMap['now'] is not null").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select bar from Bar bar join bar.baz b where b.stringDateMap['big bang'] < b.stringDateMap['now'] and b.stringDateMap['now'] is not null").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select bar from Bar bar where bar.baz.stringDateMap['big bang'] < bar.baz.stringDateMap['now'] and bar.baz.stringDateMap['now'] is not null").list().size()) == 1));
        list = s.createQuery("select foo.string, foo.component, foo.id from Bar foo").list();
        Assert.assertTrue(((FooComponent) (((Object[]) (list.get(0)))[1])).getName().equals("foo"));
        list = s.createQuery("select elements(baz.components) from Baz baz").list();
        Assert.assertTrue(((list.size()) == 2));
        list = s.createQuery("select bc.name from Baz baz join baz.components bc").list();
        Assert.assertTrue(((list.size()) == 2));
        // list = s.find("select bc from Baz baz join baz.components bc");
        s.createQuery("from Foo foo where foo.integer < 10 order by foo.string").setMaxResults(12).list();
        s.delete(barp);
        s.delete(baz);
        s.delete(foop.getFoo());
        s.delete(foop);
        txn.commit();
        s.close();
    }

    @Test
    public void testCascadeDeleteDetached() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        List list = new ArrayList();
        list.add(new Fee());
        baz.setFees(list);
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        s.getTransaction().commit();
        s.close();
        Assert.assertFalse(Hibernate.isInitialized(baz.getFees()));
        s = openSession();
        s.beginTransaction();
        s.delete(baz);
        s.flush();
        Assert.assertFalse(s.createQuery("from Fee").iterate().hasNext());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = new Baz();
        list = new ArrayList();
        list.add(new Fee());
        list.add(new Fee());
        baz.setFees(list);
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Hibernate.initialize(baz.getFees());
        s.getTransaction().commit();
        s.close();
        Assert.assertTrue(((baz.getFees().size()) == 2));
        s = openSession();
        s.beginTransaction();
        s.delete(baz);
        s.flush();
        Assert.assertFalse(s.createQuery("from Fee").iterate().hasNext());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testForeignKeys() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        Foo foo = new Foo();
        List bag = new ArrayList();
        bag.add(foo);
        baz.setIdFooBag(bag);
        baz.setFoo(foo);
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNonlazyCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (// .setComment("criteria test")
        s.createCriteria(Baz.class).setFetchMode("stringDateMap", JOIN).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(baz.getFooToGlarch()));
        Assert.assertTrue(Hibernate.isInitialized(baz.getFooComponentToFoo()));
        Assert.assertTrue((!(Hibernate.isInitialized(baz.getStringSet()))));
        Assert.assertTrue(Hibernate.isInitialized(baz.getStringDateMap()));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReuseDeletedCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        s.flush();
        s.delete(baz);
        Baz baz2 = new Baz();
        baz2.setStringArray(new String[]{ "x-y-z" });
        s.save(baz2);
        s.getTransaction().commit();
        s.close();
        baz2.setStringSet(baz.getStringSet());
        baz2.setStringArray(baz.getStringArray());
        baz2.setFooArray(baz.getFooArray());
        s = openSession();
        s.beginTransaction();
        s.update(baz2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        Assert.assertTrue(((baz2.getStringArray().length) == 3));
        Assert.assertTrue(((baz2.getStringSet().size()) == 3));
        s.delete(baz2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testPropertyRef() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Holder h = new Holder();
        h.setName("foo");
        Holder h2 = new Holder();
        h2.setName("bar");
        h.setOtherHolder(h2);
        Serializable hid = s.save(h);
        Qux q = new Qux();
        q.setHolder(h2);
        Serializable qid = s.save(q);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        h = ((Holder) (s.load(Holder.class, hid)));
        Assert.assertEquals(h.getName(), "foo");
        Assert.assertEquals(h.getOtherHolder().getName(), "bar");
        Object[] res = ((Object[]) (s.createQuery("from Holder h join h.otherHolder oh where h.otherHolder.name = 'bar'").list().get(0)));
        Assert.assertTrue(((res[0]) == h));
        q = ((Qux) (s.get(Qux.class, qid)));
        Assert.assertTrue(((q.getHolder()) == (h.getOtherHolder())));
        s.delete(h);
        s.delete(q);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testQueryCollectionOfValues() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        Glarch g = new Glarch();
        Serializable gid = s.save(g);
        if (((((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect)))/* && !(dialect instanceof MckoiDialect) */
         && (!((getDialect()) instanceof SAPDBDialect))) && (!((getDialect()) instanceof PointbaseDialect))) && (!((getDialect()) instanceof TimesTenDialect))) {
            s.createFilter(baz.getFooArray(), "where size(this.bytes) > 0").list();
            s.createFilter(baz.getFooArray(), "where 0 in elements(this.bytes)").list();
        }
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.createQuery("from Baz baz join baz.fooSet foo join foo.foo.foo foo2 where foo2.string = 'foo'").list();
        s.createQuery("from Baz baz join baz.fooArray foo join foo.foo.foo foo2 where foo2.string = 'foo'").list();
        s.createQuery("from Baz baz join baz.stringDateMap date where index(date) = 'foo'").list();
        s.createQuery("from Baz baz join baz.topGlarchez g where index(g) = 'A'").list();
        s.createQuery("select index(g) from Baz baz join baz.topGlarchez g").list();
        Assert.assertTrue(((s.createQuery("from Baz baz left join baz.stringSet").list().size()) == 3));
        baz = ((Baz) (s.createQuery("from Baz baz join baz.stringSet str where str='foo'").list().get(0)));
        Assert.assertTrue((!(Hibernate.isInitialized(baz.getStringSet()))));
        baz = ((Baz) (s.createQuery("from Baz baz left join fetch baz.stringSet").list().get(0)));
        Assert.assertTrue(Hibernate.isInitialized(baz.getStringSet()));
        Assert.assertTrue(((s.createQuery("from Baz baz join baz.stringSet string where string='foo'").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("from Baz baz inner join baz.components comp where comp.name='foo'").list().size()) == 1));
        // List bss = s.find("select baz, ss from Baz baz inner join baz.stringSet ss");
        s.createQuery("from Glarch g inner join g.fooComponents comp where comp.fee is not null").list();
        s.createQuery("from Glarch g inner join g.fooComponents comp join comp.fee fee where fee.count > 0").list();
        s.createQuery("from Glarch g inner join g.fooComponents comp where comp.fee.count is not null").list();
        s.delete(baz);
        s.delete(s.get(Glarch.class, gid));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testBatchLoad() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        SortedSet stringSet = new TreeSet();
        stringSet.add("foo");
        stringSet.add("bar");
        Set fooSet = new HashSet();
        for (int i = 0; i < 3; i++) {
            Foo foo = new Foo();
            s.save(foo);
            fooSet.add(foo);
        }
        baz.setFooSet(fooSet);
        baz.setStringSet(stringSet);
        s.save(baz);
        Baz baz2 = new Baz();
        fooSet = new HashSet();
        for (int i = 0; i < 2; i++) {
            Foo foo = new Foo();
            s.save(foo);
            fooSet.add(foo);
        }
        baz2.setFooSet(fooSet);
        s.save(baz2);
        Baz baz3 = new Baz();
        stringSet = new TreeSet();
        stringSet.add("foo");
        stringSet.add("baz");
        baz3.setStringSet(stringSet);
        s.save(baz3);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz3 = ((Baz) (s.load(Baz.class, baz3.getCode())));
        Assert.assertFalse((((Hibernate.isInitialized(baz.getFooSet())) || (Hibernate.isInitialized(baz2.getFooSet()))) || (Hibernate.isInitialized(baz3.getFooSet()))));
        Assert.assertFalse((((Hibernate.isInitialized(baz.getStringSet())) || (Hibernate.isInitialized(baz2.getStringSet()))) || (Hibernate.isInitialized(baz3.getStringSet()))));
        Assert.assertTrue(((baz.getFooSet().size()) == 3));
        Assert.assertTrue((((Hibernate.isInitialized(baz.getFooSet())) && (Hibernate.isInitialized(baz2.getFooSet()))) && (Hibernate.isInitialized(baz3.getFooSet()))));
        Assert.assertTrue(((baz2.getFooSet().size()) == 2));
        Assert.assertTrue(baz3.getStringSet().contains("baz"));
        Assert.assertTrue((((Hibernate.isInitialized(baz.getStringSet())) && (Hibernate.isInitialized(baz2.getStringSet()))) && (Hibernate.isInitialized(baz3.getStringSet()))));
        Assert.assertTrue((((baz.getStringSet().size()) == 2) && ((baz2.getStringSet().size()) == 0)));
        s.delete(baz);
        s.delete(baz2);
        s.delete(baz3);
        Iterator iter = new JoinedIterator(new Iterator[]{ baz.getFooSet().iterator(), baz2.getFooSet().iterator() });
        while (iter.hasNext())
            s.delete(iter.next());

        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFetchInitializedCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        Collection fooBag = new ArrayList();
        fooBag.add(new Foo());
        fooBag.add(new Foo());
        baz.setFooBag(fooBag);
        s.save(baz);
        s.flush();
        fooBag = baz.getFooBag();
        s.createQuery("from Baz baz left join fetch baz.fooBag").list();
        Assert.assertTrue((fooBag == (baz.getFooBag())));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Object bag = baz.getFooBag();
        Assert.assertFalse(Hibernate.isInitialized(bag));
        s.createQuery("from Baz baz left join fetch baz.fooBag").list();
        Assert.assertTrue((bag == (baz.getFooBag())));
        Assert.assertTrue(((baz.getFooBag().size()) == 2));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLateCollectionAdd() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        List l = new ArrayList();
        baz.setStringList(l);
        l.add("foo");
        Serializable id = s.save(baz);
        l.add("bar");
        s.flush();
        l.add("baz");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, id)));
        Assert.assertTrue((((baz.getStringList().size()) == 3) && (baz.getStringList().contains("bar"))));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testUpdate() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        s.getTransaction().commit();
        s.close();
        foo = ((Foo) (SerializationHelper.deserialize(SerializationHelper.serialize(foo))));
        s = openSession();
        s.beginTransaction();
        FooProxy foo2 = ((FooProxy) (s.load(Foo.class, foo.getKey())));
        foo2.setString("dirty");
        foo2.setBoolean(new Boolean(false));
        foo2.setBytes(new byte[]{ 1, 2, 3 });
        foo2.setDate(null);
        foo2.setShort(new Short("69"));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo2.setString("dirty again");
        s.update(foo2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo2.setString("dirty again 2");
        s.update(foo2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Foo foo3 = new Foo();
        s.load(foo3, foo.getKey());
        // There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
        Assert.assertTrue("update", foo2.equalsFoo(foo3));
        s.delete(foo3);
        doDelete(s, "from Glarch");
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testListRemove() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz b = new Baz();
        List stringList = new ArrayList();
        List feeList = new ArrayList();
        b.setFees(feeList);
        b.setStringList(stringList);
        feeList.add(new Fee());
        feeList.add(new Fee());
        feeList.add(new Fee());
        feeList.add(new Fee());
        stringList.add("foo");
        stringList.add("bar");
        stringList.add("baz");
        stringList.add("glarch");
        s.save(b);
        s.flush();
        stringList.remove(1);
        feeList.remove(1);
        s.flush();
        s.evict(b);
        s.refresh(b);
        Assert.assertTrue(((b.getFees().size()) == 3));
        stringList = b.getStringList();
        Assert.assertTrue(((((stringList.size()) == 3) && ("baz".equals(stringList.get(1)))) && ("foo".equals(stringList.get(0)))));
        s.delete(b);
        doDelete(s, "from Fee");
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFetchInitializedCollectionDupe() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        Collection fooBag = new ArrayList();
        fooBag.add(new Foo());
        fooBag.add(new Foo());
        baz.setFooBag(fooBag);
        s.save(baz);
        s.flush();
        fooBag = baz.getFooBag();
        s.createQuery("from Baz baz left join fetch baz.fooBag").list();
        Assert.assertTrue(Hibernate.isInitialized(fooBag));
        Assert.assertTrue((fooBag == (baz.getFooBag())));
        Assert.assertTrue(((baz.getFooBag().size()) == 2));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Object bag = baz.getFooBag();
        Assert.assertFalse(Hibernate.isInitialized(bag));
        s.createQuery("from Baz baz left join fetch baz.fooBag").list();
        Assert.assertTrue(Hibernate.isInitialized(bag));
        Assert.assertTrue((bag == (baz.getFooBag())));
        Assert.assertTrue(((baz.getFooBag().size()) == 2));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSortables() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz b = new Baz();
        b.setName("name");
        SortedSet ss = new TreeSet();
        ss.add(new Sortable("foo"));
        ss.add(new Sortable("bar"));
        ss.add(new Sortable("baz"));
        b.setSortablez(ss);
        s.save(b);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Criteria cr = s.createCriteria(Baz.class);
        cr.setFetchMode("topGlarchez", SELECT);
        List result = cr.addOrder(Order.asc("name")).list();
        Assert.assertTrue(((result.size()) == 1));
        b = ((Baz) (result.get(0)));
        Assert.assertTrue(((b.getSortablez().size()) == 3));
        Assert.assertEquals(((Sortable) (b.getSortablez().iterator().next())).getName(), "bar");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        result = s.createQuery("from Baz baz left join fetch baz.sortablez order by baz.name asc").list();
        b = ((Baz) (result.get(0)));
        Assert.assertTrue(((b.getSortablez().size()) == 3));
        Assert.assertEquals(((Sortable) (b.getSortablez().iterator().next())).getName(), "bar");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        result = s.createQuery("from Baz baz order by baz.name asc").list();
        b = ((Baz) (result.get(0)));
        Assert.assertTrue(((b.getSortablez().size()) == 3));
        Assert.assertEquals(((Sortable) (b.getSortablez().iterator().next())).getName(), "bar");
        s.delete(b);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFetchList() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        Foo foo = new Foo();
        s.save(foo);
        Foo foo2 = new Foo();
        s.save(foo2);
        s.flush();
        List list = new ArrayList();
        for (int i = 0; i < 5; i++) {
            Fee fee = new Fee();
            list.add(fee);
        }
        baz.setFees(list);
        list = s.createQuery("from Foo foo, Baz baz left join fetch baz.fees").list();
        Assert.assertTrue(Hibernate.isInitialized(((Baz) (((Object[]) (list.get(0)))[1])).getFees()));
        s.delete(foo);
        s.delete(foo2);
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testBagOneToMany() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        List list = new ArrayList();
        baz.setBazez(list);
        list.add(new Baz());
        s.save(baz);
        s.flush();
        list.add(new Baz());
        s.flush();
        list.add(0, new Baz());
        s.flush();
        s.delete(list.remove(1));
        s.flush();
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = H2Dialect.class, comment = "Feature not supported: MVCC=TRUE && FOR UPDATE && JOIN")
    public void testQueryLockMode() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Bar bar = new Bar();
        s.save(bar);
        s.flush();
        bar.setString("changed");
        Baz baz = new Baz();
        baz.setFoo(bar);
        s.save(baz);
        Query q = s.createQuery("from Foo foo, Bar bar");
        if (supportsLockingNullableSideOfJoin(getDialect())) {
            q.setLockMode("bar", UPGRADE);
        }
        Object[] result = ((Object[]) (q.uniqueResult()));
        Object b = result[0];
        Assert.assertTrue((((s.getCurrentLockMode(b)) == (LockMode.WRITE)) && ((s.getCurrentLockMode(result[1])) == (LockMode.WRITE))));
        tx.commit();
        tx = s.beginTransaction();
        Assert.assertTrue(((s.getCurrentLockMode(b)) == (LockMode.NONE)));
        s.createQuery("from Foo foo").list();
        Assert.assertTrue(((s.getCurrentLockMode(b)) == (LockMode.NONE)));
        q = s.createQuery("from Foo foo");
        q.setLockMode("foo", READ);
        q.list();
        Assert.assertTrue(((s.getCurrentLockMode(b)) == (LockMode.READ)));
        s.evict(baz);
        tx.commit();
        tx = s.beginTransaction();
        Assert.assertTrue(((s.getCurrentLockMode(b)) == (LockMode.NONE)));
        s.delete(s.load(Baz.class, baz.getCode()));
        Assert.assertTrue(((s.getCurrentLockMode(b)) == (LockMode.NONE)));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        q = s.createQuery("from Foo foo, Bar bar, Bar bar2");
        if (supportsLockingNullableSideOfJoin(getDialect())) {
            q.setLockMode("bar", UPGRADE);
        }
        q.setLockMode("bar2", READ);
        result = ((Object[]) (q.list().get(0)));
        if (supportsLockingNullableSideOfJoin(getDialect())) {
            Assert.assertTrue((((s.getCurrentLockMode(result[0])) == (LockMode.UPGRADE)) && ((s.getCurrentLockMode(result[1])) == (LockMode.UPGRADE))));
        }
        s.delete(result[0]);
        tx.commit();
        s.close();
    }

    @Test
    public void testManyToManyBag() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        Serializable id = s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, id)));
        baz.getFooBag().add(new Foo());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, id)));
        Assert.assertTrue((!(Hibernate.isInitialized(baz.getFooBag()))));
        Assert.assertTrue(((baz.getFooBag().size()) == 1));
        if (!((getDialect()) instanceof HSQLDialect))
            Assert.assertTrue(Hibernate.isInitialized(baz.getFooBag().iterator().next()));

        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testIdBag() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        List l = new ArrayList();
        List l2 = new ArrayList();
        baz.setIdFooBag(l);
        baz.setByteBag(l2);
        l.add(new Foo());
        l.add(new Bar());
        byte[] bytes = "ffo".getBytes();
        l2.add(bytes);
        l2.add("foo".getBytes());
        s.flush();
        l.add(new Foo());
        l.add(new Bar());
        l2.add("bar".getBytes());
        s.flush();
        s.delete(l.remove(3));
        bytes[1] = 'o';
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getIdFooBag().size()) == 3));
        Assert.assertTrue(((baz.getByteBag().size()) == 3));
        bytes = "foobar".getBytes();
        Iterator iter = baz.getIdFooBag().iterator();
        while (iter.hasNext())
            s.delete(iter.next());

        baz.setIdFooBag(null);
        baz.getByteBag().add(bytes);
        baz.getByteBag().add(bytes);
        Assert.assertTrue(((baz.getByteBag().size()) == 5));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getIdFooBag().size()) == 0));
        Assert.assertTrue(((baz.getByteBag().size()) == 5));
        baz.getIdFooBag().add(new Foo());
        iter = baz.getByteBag().iterator();
        iter.next();
        iter.remove();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getIdFooBag().size()) == 1));
        Assert.assertTrue(((baz.getByteBag().size()) == 4));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testForceOuterJoin() throws Exception {
        if (isOuterJoinFetchingDisabled()) {
            return;
        }
        Session s = openSession();
        s.beginTransaction();
        Glarch g = new Glarch();
        FooComponent fc = new FooComponent();
        fc.setGlarch(g);
        FooProxy f = new Foo();
        FooProxy f2 = new Foo();
        f.setComponent(fc);
        f.setFoo(f2);
        s.save(f2);
        Serializable id = s.save(f);
        Serializable gid = s.getIdentifier(f.getComponent().getGlarch());
        s.getTransaction().commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Foo.class);
        s = openSession();
        s.beginTransaction();
        f = ((FooProxy) (s.load(Foo.class, id)));
        Assert.assertFalse(Hibernate.isInitialized(f));
        Assert.assertTrue(Hibernate.isInitialized(f.getComponent().getGlarch()));// outer-join="true"

        Assert.assertFalse(Hibernate.isInitialized(f.getFoo()));// outer-join="auto"

        Assert.assertEquals(s.getIdentifier(f.getComponent().getGlarch()), gid);
        s.delete(f);
        s.delete(f.getFoo());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testEmptyCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Serializable id = s.save(new Baz());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Baz baz = ((Baz) (s.load(Baz.class, id)));
        Set foos = baz.getFooSet();
        Assert.assertTrue(((foos.size()) == 0));
        Foo foo = new Foo();
        foos.add(foo);
        s.save(foo);
        s.flush();
        s.delete(foo);
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testOneToOneGenerator() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        X x = new X();
        Y y = new Y();
        x.setY(y);
        y.setTheX(x);
        x.getXxs().add(new X.XX(x));
        x.getXxs().add(new X.XX(x));
        Serializable id = s.save(y);
        Assert.assertEquals(id, s.save(x));
        s.flush();
        Assert.assertTrue(((s.contains(y)) && (s.contains(x))));
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(new Long(x.getId()), y.getId());
        s = openSession();
        s.beginTransaction();
        x = new X();
        y = new Y();
        x.setY(y);
        y.setTheX(x);
        x.getXxs().add(new X.XX(x));
        s.save(y);
        s.flush();
        Assert.assertTrue(((s.contains(y)) && (s.contains(x))));
        s.getTransaction().commit();
        s.close();
        Assert.assertEquals(new Long(x.getId()), y.getId());
        s = openSession();
        s.beginTransaction();
        x = new X();
        y = new Y();
        x.setY(y);
        y.setTheX(x);
        x.getXxs().add(new X.XX(x));
        x.getXxs().add(new X.XX(x));
        id = s.save(x);
        Assert.assertEquals(id, y.getId());
        Assert.assertEquals(id, new Long(x.getId()));
        s.flush();
        Assert.assertTrue(((s.contains(y)) && (s.contains(x))));
        doDelete(s, "from X x");
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLimit() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        for (int i = 0; i < 10; i++)
            s.save(new Foo());

        Iterator iter = s.createQuery("from Foo foo").setMaxResults(4).setFirstResult(2).iterate();
        int count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        } 
        Assert.assertEquals(4, count);
        iter = s.createQuery("select foo from Foo foo").setMaxResults(2).setFirstResult(2).list().iterator();
        count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        } 
        Assert.assertTrue((count == 2));
        iter = s.createQuery("select foo from Foo foo").setMaxResults(3).list().iterator();
        count = 0;
        while (iter.hasNext()) {
            iter.next();
            count++;
        } 
        Assert.assertTrue((count == 3));
        Assert.assertEquals(10, doDelete(s, "from Foo foo"));
        txn.commit();
        s.close();
    }

    @Test
    public void testCustom() throws Exception {
        GlarchProxy g = new Glarch();
        Multiplicity m = new Multiplicity();
        m.count = 12;
        m.glarch = g;
        g.setMultiple(m);
        Session s = openSession();
        s.beginTransaction();
        Serializable gid = s.save(g);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // g = (Glarch) s.createQuery( "from Glarch g where g.multiple.count=12" ).list().get(0);
        s.createQuery("from Glarch g where g.multiple.count=12").list().get(0);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((Glarch) (s.createQuery("from Glarch g where g.multiple.glarch=g and g.multiple.count=12").list().get(0)));
        Assert.assertTrue(((g.getMultiple()) != null));
        Assert.assertEquals(g.getMultiple().count, 12);
        Assert.assertSame(g.getMultiple().glarch, g);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue(((g.getMultiple()) != null));
        Assert.assertEquals(g.getMultiple().count, 12);
        Assert.assertSame(g.getMultiple().glarch, g);
        s.delete(g);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSaveAddDelete() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        Set bars = new HashSet();
        baz.setCascadingBars(bars);
        s.save(baz);
        s.flush();
        baz.getCascadingBars().add(new Bar());
        s.delete(baz);
        s.flush();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testNamedParams() throws Exception {
        Bar bar = new Bar();
        Bar bar2 = new Bar();
        bar.setName("Bar");
        bar2.setName("Bar Two");
        bar.setX(10);
        bar2.setX(1000);
        Baz baz = new Baz();
        baz.setCascadingBars(new HashSet());
        baz.getCascadingBars().add(bar);
        bar.setBaz(baz);
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        s.save(baz);
        s.save(bar2);
        List list = s.createQuery("from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like 'Bar %'").list();
        Object row = list.iterator().next();
        Assert.assertTrue(((row instanceof Object[]) && ((((Object[]) (row)).length) == 3)));
        Query q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like 'Bar%'");
        list = q.list();
        if (!((getDialect()) instanceof SAPDBDialect))
            Assert.assertTrue(((list.size()) == 2));

        q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where ( bar.name in (:nameList) or bar.name in (:nameList) ) and bar.string = :stringVal");
        HashSet nameList = new HashSet();
        nameList.add("bar");
        nameList.add("Bar");
        nameList.add("Bar Two");
        q.setParameterList("nameList", nameList);
        q.setParameter("stringVal", "a string");
        list = q.list();
        if (!((getDialect()) instanceof SAPDBDialect))
            Assert.assertTrue(((list.size()) == 2));

        try {
            q.setParameterList("nameList", ((Collection) (null)));
            Assert.fail("Should throw a QueryException when passing a null!");
        } catch (IllegalArgumentException qe) {
            // should happen
        }
        q = s.createQuery("select bar, b from Bar bar inner join bar.baz baz inner join baz.cascadingBars b where bar.name like 'Bar%'");
        Object result = q.uniqueResult();
        Assert.assertTrue((result != null));
        q = s.createQuery("select bar, b from Bar bar left join bar.baz baz left join baz.cascadingBars b where bar.name like :name and b.name like :name");
        q.setString("name", "Bar%");
        list = q.list();
        Assert.assertTrue(((list.size()) == 1));
        // This test added for issue HB-297 - there is an named parameter in the Order By clause
        q = s.createQuery("select bar from Bar bar order by ((bar.x - :valueX)*(bar.x - :valueX))");
        q.setInteger("valueX", ((bar.getX()) + 1));
        list = q.list();
        Assert.assertTrue(((((Bar) (list.get(0))).getX()) == (bar.getX())));
        q.setInteger("valueX", ((bar2.getX()) + 1));
        list = q.list();
        Assert.assertTrue(((((Bar) (list.get(0))).getX()) == (bar2.getX())));
        s.delete(baz);
        s.delete(bar2);
        txn.commit();
        s.close();
    }

    @Test
    @RequiresDialectFeature(value = SupportsEmptyInListCheck.class, comment = "Dialect does not support SQL empty in list [x in ()]")
    public void testEmptyInListQuery() {
        Session s = openSession();
        s.beginTransaction();
        Query q = s.createQuery("select bar from Bar as bar where bar.name in (:nameList)");
        q.setParameterList("nameList", Collections.EMPTY_LIST);
        Assert.assertEquals(0, q.list().size());
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testParameterCheck() throws HibernateException {
        Session s = openSession();
        try {
            Query q = s.createQuery("select bar from Bar as bar where bar.x > :myX");
            q.list();
            Assert.fail("Should throw QueryException for missing myX");
        } catch (QueryException iae) {
            // should happen
        } finally {
            s.close();
        }
        s = openSession();
        try {
            Query q = s.createQuery("select bar from Bar as bar where bar.x > ?");
            q.list();
            Assert.fail("Should throw QueryException for missing ?");
        } catch (QueryException iae) {
            // should happen
        } finally {
            s.close();
        }
        s = openSession();
        try {
            Query q = s.createQuery("select bar from Bar as bar where bar.x > ? or bar.short = 1 or bar.string = 'ff ? bb'");
            q.setInteger(0, 1);
            q.list();
        } catch (QueryException iae) {
            Assert.fail("Should not throw QueryException for missing ?");
        } finally {
            s.close();
        }
        s = openSession();
        try {
            Query q = s.createQuery("select bar from Bar as bar where bar.string = ' ? ' or bar.string = '?'");
            q.list();
        } catch (QueryException iae) {
            Assert.fail("Should not throw QueryException for ? in quotes");
        } finally {
            s.close();
        }
        s = openSession();
        try {
            Query q = s.createQuery("select bar from Bar as bar where bar.string = ? or bar.string = ? or bar.string = ?");
            q.setParameter(0, "bull");
            q.setParameter(2, "shit");
            q.list();
            Assert.fail("should throw exception telling me i have not set parameter 1");
        } catch (QueryException iae) {
            // should happen!
        } finally {
            s.close();
        }
    }

    @Test
    public void testDyna() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        GlarchProxy g = new Glarch();
        g.setName("G");
        Serializable id = s.save(g);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, id)));
        Assert.assertTrue(g.getName().equals("G"));
        Assert.assertTrue(((g.getDynaBean().get("foo").equals("foo")) && (g.getDynaBean().get("bar").equals(new Integer(66)))));
        Assert.assertTrue((!(g instanceof Glarch)));
        g.getDynaBean().put("foo", "bar");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, id)));
        Assert.assertTrue(((g.getDynaBean().get("foo").equals("bar")) && (g.getDynaBean().get("bar").equals(new Integer(66)))));
        g.setDynaBean(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, id)));
        Assert.assertTrue(((g.getDynaBean()) == null));
        s.delete(g);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFindByCriteria() throws Exception {
        if ((getDialect()) instanceof DB2Dialect) {
            return;
        }
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Foo f = new Foo();
        s.save(f);
        s.flush();
        List list = s.createCriteria(Foo.class).add(Restrictions.eq("integer", f.getInteger())).add(Restrictions.eqProperty("integer", "integer")).add(Restrictions.like("string", f.getString().toUpperCase(ROOT)).ignoreCase()).add(Restrictions.in("boolean", f.getBoolean(), f.getBoolean())).setFetchMode("foo", JOIN).setFetchMode("baz", SELECT).setFetchMode("abstracts", JOIN).list();
        Assert.assertTrue((((list.size()) == 1) && ((list.get(0)) == f)));
        list = s.createCriteria(Foo.class).add(Restrictions.disjunction().add(Restrictions.eq("integer", f.getInteger())).add(Restrictions.like("string", f.getString())).add(Restrictions.eq("boolean", f.getBoolean()))).add(Restrictions.isNotNull("boolean")).list();
        Assert.assertTrue((((list.size()) == 1) && ((list.get(0)) == f)));
        Foo example = new Foo();
        example.setString("a STRing");
        list = s.createCriteria(Foo.class).add(org.hibernate.criterion.Example.create(example).excludeZeroes().ignoreCase().excludeProperty("bool").excludeProperty("char").excludeProperty("yesno")).list();
        Assert.assertTrue(("Example API without like did not work correctly, size was " + (list.size())), (((list.size()) == 1) && ((list.get(0)) == f)));
        example.setString("rin");
        list = s.createCriteria(Foo.class).add(org.hibernate.criterion.Example.create(example).excludeZeroes().enableLike(MatchMode.ANYWHERE).excludeProperty("bool").excludeProperty("char").excludeProperty("yesno")).list();
        Assert.assertTrue(("Example API without like did not work correctly, size was " + (list.size())), (((list.size()) == 1) && ((list.get(0)) == f)));
        list = s.createCriteria(Foo.class).add(Restrictions.or(Restrictions.and(Restrictions.eq("integer", f.getInteger()), Restrictions.like("string", f.getString())), Restrictions.eq("boolean", f.getBoolean()))).list();
        Assert.assertTrue((((list.size()) == 1) && ((list.get(0)) == f)));
        list = s.createCriteria(Foo.class).setMaxResults(5).addOrder(Order.asc("date")).list();
        Assert.assertTrue((((list.size()) == 1) && ((list.get(0)) == f)));
        list = s.createCriteria(Foo.class).setFirstResult(1).addOrder(Order.asc("date")).addOrder(Order.desc("string")).list();
        Assert.assertTrue(((list.size()) == 0));
        list = s.createCriteria(Foo.class).setFetchMode("component.importantDates", JOIN).list();
        Assert.assertTrue(((list.size()) == 3));
        list = s.createCriteria(Foo.class).setFetchMode("component.importantDates", JOIN).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
        Assert.assertTrue(((list.size()) == 1));
        f.setFoo(new Foo());
        s.save(f.getFoo());
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        list = s.createCriteria(Foo.class).add(Restrictions.eq("integer", f.getInteger())).add(Restrictions.like("string", f.getString())).add(Restrictions.in("boolean", f.getBoolean(), f.getBoolean())).add(Restrictions.isNotNull("foo")).setFetchMode("foo", JOIN).setFetchMode("baz", SELECT).setFetchMode("component.glarch", SELECT).setFetchMode("foo.baz", SELECT).setFetchMode("foo.component.glarch", SELECT).list();
        f = ((Foo) (list.get(0)));
        Assert.assertTrue(Hibernate.isInitialized(f.getFoo()));
        Assert.assertTrue((!(Hibernate.isInitialized(f.getComponent().getGlarch()))));
        s.save(new Bar());
        list = s.createCriteria(Bar.class).list();
        Assert.assertTrue(((list.size()) == 1));
        Assert.assertTrue(((s.createCriteria(Foo.class).list().size()) == 3));
        s.delete(list.get(0));
        s.delete(f.getFoo());
        s.delete(f);
        txn.commit();
        s.close();
    }

    @Test
    public void testAfterDelete() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        s.flush();
        s.delete(foo);
        s.save(foo);
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testCollectionWhere() throws Exception {
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        Baz baz = new Baz();
        Foo[] arr = new Foo[10];
        arr[0] = foo1;
        arr[9] = foo2;
        Session s = openSession();
        s.beginTransaction();
        s.save(foo1);
        s.save(foo2);
        baz.setFooArray(arr);
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        final Session s2 = openSession();
        s2.beginTransaction();
        baz = ((Baz) (s2.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getFooArray().length) == 1));
        Assert.assertTrue(((s2.createQuery("from Baz baz join baz.fooArray foo").list().size()) == 1));
        Assert.assertTrue(((s2.createQuery("from Foo foo").list().size()) == 2));
        Assert.assertTrue(((s2.createFilter(baz.getFooArray(), "").list().size()) == 1));
        // assertTrue( s.delete("from java.lang.Object o")==9 );
        doDelete(s2, "from Foo foo");
        final String bazid = baz.getCode();
        s2.delete(baz);
        int rows = s2.doReturningWork(new org.hibernate.jdbc.AbstractReturningWork<Integer>() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                Statement st = connection.createStatement();
                return st.executeUpdate((("delete from FOO_ARRAY where id_='" + bazid) + "' and i>=8"));
            }
        });
        Assert.assertTrue((rows == 1));
        s2.getTransaction().commit();
        s2.close();
    }

    @Test
    public void testComponentParent() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        BarProxy bar = new Bar();
        bar.setBarComponent(new FooComponent());
        Baz baz = new Baz();
        baz.setComponents(new FooComponent[]{ new FooComponent(), new FooComponent() });
        s.save(bar);
        s.save(baz);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        bar = ((BarProxy) (s.load(Bar.class, bar.getKey())));
        s.load(baz, baz.getCode());
        Assert.assertTrue(((bar.getBarComponent().getParent()) == bar));
        Assert.assertTrue((((baz.getComponents()[0].getBaz()) == baz) && ((baz.getComponents()[1].getBaz()) == baz)));
        s.delete(baz);
        s.delete(bar);
        t.commit();
        s.close();
    }

    @Test
    public void testCollectionCache() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.load(Baz.class, baz.getCode());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testCascadeSave() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        List list = new ArrayList();
        list.add(new Fee());
        list.add(new Fee());
        baz.setFees(list);
        s.save(baz);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getFees().size()) == 2));
        s.delete(baz);
        Assert.assertTrue((!(s.createQuery("from Fee fee").iterate().hasNext())));
        t.commit();
        s.close();
    }

    @Test
    public void testCollectionsInSelect() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Foo[] foos = new Foo[]{ null, new Foo() };
        s.save(foos[1]);
        Baz baz = new Baz();
        baz.setDefaults();
        baz.setFooArray(foos);
        s.save(baz);
        Baz baz2 = new Baz();
        baz2.setDefaults();
        s.save(baz2);
        Bar bar = new Bar();
        bar.setBaz(baz);
        s.save(bar);
        List list = s.createQuery("select new Result(foo.string, foo.long, foo.integer) from Foo foo").list();
        Assert.assertTrue(((((list.size()) == 2) && ((list.get(0)) instanceof Result)) && ((list.get(1)) instanceof Result)));
        /* list = s.find("select new Result( baz.name, foo.long, count(elements(baz.fooArray)) ) from Baz baz join baz.fooArray foo group by baz.name, foo.long");
        assertTrue( list.size()==1 && ( list.get(0) instanceof Result ) );
        Result r = ((Result) list.get(0) );
        assertEquals( r.getName(), baz.getName() );
        assertEquals( r.getCount(), 1 );
        assertEquals( r.getAmount(), foos[1].getLong().longValue() );
         */
        list = s.createQuery("select new Result( baz.name, max(foo.long), count(foo) ) from Baz baz join baz.fooArray foo group by baz.name").list();
        Assert.assertTrue((((list.size()) == 1) && ((list.get(0)) instanceof Result)));
        Result r = ((Result) (list.get(0)));
        Assert.assertEquals(r.getName(), baz.getName());
        Assert.assertEquals(r.getCount(), 1);
        Assert.assertTrue(((r.getAmount()) > 696969696969696000L));
        // s.find("select max( elements(bar.baz.fooArray) ) from Bar as bar");
        // The following test is disabled for databases with no subselects...also for Interbase (not sure why).
        if ((((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect)))/* && !(dialect instanceof MckoiDialect) */
         && (!((getDialect()) instanceof SAPDBDialect))) && (!((getDialect()) instanceof PointbaseDialect))) {
            s.createQuery("select count(*) from Baz as baz where 1 in indices(baz.fooArray)").list();
            s.createQuery("select count(*) from Bar as bar where 'abc' in elements(bar.baz.fooArray)").list();
            s.createQuery("select count(*) from Bar as bar where 1 in indices(bar.baz.fooArray)").list();
            if (((((((!((getDialect()) instanceof DB2Dialect)) && (!((getDialect()) instanceof Oracle8iDialect))) && (!(SybaseDialect.class.isAssignableFrom(getDialect().getClass())))) && (!(SQLServerDialect.class.isAssignableFrom(getDialect().getClass())))) && (!((getDialect()) instanceof PostgreSQLDialect))) && (!((getDialect()) instanceof PostgreSQL81Dialect))) && (!((getDialect()) instanceof AbstractHANADialect))) {
                // SybaseAnywhereDialect supports implicit conversions from strings to ints
                s.createQuery("select count(*) from Bar as bar, bar.component.glarch.proxyArray as g where g.id in indices(bar.baz.fooArray)").list();
                s.createQuery("select max( elements(bar.baz.fooArray) ) from Bar as bar, bar.component.glarch.proxyArray as g where g.id in indices(bar.baz.fooArray)").list();
            }
            s.createQuery("select count(*) from Bar as bar where '1' in (from bar.component.glarch.proxyArray g where g.name='foo')").list();
            s.createQuery("select count(*) from Bar as bar where '1' in (from bar.component.glarch.proxyArray g where g.name='foo')").list();
            s.createQuery("select count(*) from Bar as bar left outer join bar.component.glarch.proxyArray as pg where '1' in (from bar.component.glarch.proxyArray)").list();
        }
        list = s.createQuery("from Baz baz left join baz.fooToGlarch join fetch baz.fooArray foo left join fetch foo.foo").list();
        Assert.assertTrue((((list.size()) == 1) && ((((Object[]) (list.get(0))).length) == 2)));
        s.createQuery("select baz.name from Bar bar inner join bar.baz baz inner join baz.fooSet foo where baz.name = bar.string").list();
        s.createQuery("SELECT baz.name FROM Bar AS bar INNER JOIN bar.baz AS baz INNER JOIN baz.fooSet AS foo WHERE baz.name = bar.string").list();
        if (!((getDialect()) instanceof HSQLDialect))
            s.createQuery("select baz.name from Bar bar join bar.baz baz left outer join baz.fooSet foo where baz.name = bar.string").list();

        s.createQuery("select baz.name from Bar bar join bar.baz baz join baz.fooSet foo where baz.name = bar.string").list();
        s.createQuery("SELECT baz.name FROM Bar AS bar JOIN bar.baz AS baz JOIN baz.fooSet AS foo WHERE baz.name = bar.string").list();
        if (!((getDialect()) instanceof HSQLDialect)) {
            s.createQuery("select baz.name from Bar bar left join bar.baz baz left join baz.fooSet foo where baz.name = bar.string").list();
            s.createQuery("select foo.string from Bar bar left join bar.baz.fooSet foo where bar.string = foo.string").list();
        }
        s.createQuery("select baz.name from Bar bar left join bar.baz baz left join baz.fooArray foo where baz.name = bar.string").list();
        s.createQuery("select foo.string from Bar bar left join bar.baz.fooArray foo where bar.string = foo.string").list();
        s.createQuery("select bar.string, foo.string from Bar bar inner join bar.baz as baz inner join baz.fooSet as foo where baz.name = 'name'").list();
        s.createQuery("select foo from Bar bar inner join bar.baz as baz inner join baz.fooSet as foo").list();
        s.createQuery("select foo from Bar bar inner join bar.baz.fooSet as foo").list();
        s.createQuery("select bar.string, foo.string from Bar bar join bar.baz as baz join baz.fooSet as foo where baz.name = 'name'").list();
        s.createQuery("select foo from Bar bar join bar.baz as baz join baz.fooSet as foo").list();
        s.createQuery("select foo from Bar bar join bar.baz.fooSet as foo").list();
        Assert.assertTrue(((s.createQuery("from Bar bar join bar.baz.fooArray foo").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("from Bar bar join bar.baz.fooSet foo").list().size()) == 0));
        Assert.assertTrue(((s.createQuery("from Bar bar join bar.baz.fooArray foo").list().size()) == 1));
        s.delete(bar);
        if ((((getDialect()) instanceof DB2Dialect) || ((getDialect()) instanceof PostgreSQLDialect)) || ((getDialect()) instanceof PostgreSQL81Dialect)) {
            s.createQuery("select one from One one join one.manies many group by one order by count(many)").iterate();
            s.createQuery("select one from One one join one.manies many group by one having count(many) < 5").iterate();
        }
        s.createQuery("from One one join one.manies many where one.id = 1 and many.id = 1").list();
        s.createQuery("select one.id, elements(one.manies) from One one").iterate();
        s.createQuery("select max( elements(one.manies) ) from One one").iterate();
        s.createQuery("select one, elements(one.manies) from One one").list();
        Iterator iter = s.createQuery("select elements(baz.fooArray) from Baz baz where baz.id=?").setParameter(0, baz.getCode(), STRING).iterate();
        Assert.assertTrue((((iter.next()) == (foos[1])) && (!(iter.hasNext()))));
        list = s.createQuery("select elements(baz.fooArray) from Baz baz where baz.id=?").setParameter(0, baz.getCode(), STRING).list();
        Assert.assertEquals(1, list.size());
        iter = s.createQuery("select indices(baz.fooArray) from Baz baz where baz.id=?").setParameter(0, baz.getCode(), STRING).iterate();
        Assert.assertTrue(((iter.next().equals(new Integer(1))) && (!(iter.hasNext()))));
        iter = s.createQuery("select size(baz.stringSet) from Baz baz where baz.id=?").setParameter(0, baz.getCode(), STRING).iterate();
        Assert.assertEquals(new Integer(3), iter.next());
        s.createQuery("from Foo foo where foo.component.glarch.id is not null").list();
        iter = s.createQuery("select baz, size(baz.stringSet), count( distinct elements(baz.stringSet) ), max( elements(baz.stringSet) ) from Baz baz group by baz").iterate();
        while (iter.hasNext()) {
            Object[] arr = ((Object[]) (iter.next()));
            log.info((((((((arr[0]) + " ") + (arr[1])) + " ") + (arr[2])) + " ") + (arr[3])));
        } 
        s.delete(baz);
        s.delete(baz2);
        s.delete(foos[1]);
        t.commit();
        s.close();
    }

    @Test
    public void testNewFlushing() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        s.flush();
        baz.getStringArray()[0] = "a new value";
        Iterator iter = s.createQuery("from Baz baz").iterate();// no flush

        Assert.assertTrue(((iter.next()) == baz));
        iter = s.createQuery("select elements(baz.stringArray) from Baz baz").iterate();
        boolean found = false;
        while (iter.hasNext()) {
            if (iter.next().equals("a new value"))
                found = true;

        } 
        Assert.assertTrue(found);
        baz.setStringArray(null);
        s.createQuery("from Baz baz").iterate();// no flush

        iter = s.createQuery("select elements(baz.stringArray) from Baz baz").iterate();
        Assert.assertTrue((!(iter.hasNext())));
        baz.getStringList().add("1E1");
        iter = s.createQuery("from Foo foo").iterate();// no flush

        Assert.assertTrue((!(iter.hasNext())));
        iter = s.createQuery("select elements(baz.stringList) from Baz baz").iterate();
        found = false;
        while (iter.hasNext()) {
            if (iter.next().equals("1E1"))
                found = true;

        } 
        Assert.assertTrue(found);
        baz.getStringList().remove("1E1");
        iter = s.createQuery("select elements(baz.stringArray) from Baz baz").iterate();// no flush

        iter = s.createQuery("select elements(baz.stringList) from Baz baz").iterate();
        found = false;
        while (iter.hasNext()) {
            if (iter.next().equals("1E1"))
                found = true;

        } 
        Assert.assertTrue((!found));
        List newList = new ArrayList();
        newList.add("value");
        baz.setStringList(newList);
        iter = s.createQuery("from Foo foo").iterate();// no flush

        baz.setStringList(null);
        iter = s.createQuery("select elements(baz.stringList) from Baz baz").iterate();
        Assert.assertTrue((!(iter.hasNext())));
        baz.setStringList(newList);
        iter = s.createQuery("from Foo foo").iterate();// no flush

        iter = s.createQuery("select elements(baz.stringList) from Baz baz").iterate();
        Assert.assertTrue(iter.hasNext());
        s.delete(baz);
        txn.commit();
        s.close();
    }

    @Test
    public void testPersistCollections() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Assert.assertEquals(0L, s.createQuery("select count(*) from Bar").iterate().next());
        Assert.assertEquals(0L, s.createQuery("select count(*) from Bar b").iterate().next());
        Assert.assertFalse(s.createQuery("from Glarch g").iterate().hasNext());
        Baz baz = new Baz();
        s.save(baz);
        baz.setDefaults();
        baz.setStringArray(new String[]{ "stuff" });
        Set bars = new HashSet();
        bars.add(new Bar());
        baz.setCascadingBars(bars);
        HashMap sgm = new HashMap();
        sgm.put("a", new Glarch());
        sgm.put("b", new Glarch());
        baz.setStringGlarchMap(sgm);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        Assert.assertEquals(1L, ((Long) (s.createQuery("select count(*) from Bar").iterate().next())).longValue());
        baz = ((Baz) (((Object[]) (s.createQuery("select baz, baz from Baz baz").list().get(0)))[1]));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        // System.out.println( s.print(baz) );
        Foo foo = new Foo();
        s.save(foo);
        Foo foo2 = new Foo();
        s.save(foo2);
        baz.setFooArray(new Foo[]{ foo, foo, null, foo2 });
        baz.getFooSet().add(foo);
        baz.getCustoms().add(new String[]{ "new", "custom" });
        baz.setStringArray(null);
        baz.getStringList().set(0, "new value");
        baz.setStringSet(new TreeSet());
        Time time = new Time(12345);
        baz.getTimeArray()[2] = time;
        // System.out.println(time);
        Assert.assertTrue(((baz.getStringGlarchMap().size()) == 1));
        // The following test is disabled databases with no subselects
        if (((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect))) && (!((getDialect()) instanceof PointbaseDialect))) {
            List list = s.createQuery("select foo from Foo foo, Baz baz where foo in elements(baz.fooArray) and 3 = some elements(baz.intArray) and 4 > all indices(baz.intArray)").list();
            Assert.assertTrue("collection.elements find", ((list.size()) == 2));
        }
        // SAPDB doesn't like distinct with binary type
        // Oracle12cDialect stores binary types as blobs and do no support distinct on blobs
        if ((!((getDialect()) instanceof SAPDBDialect)) && (!((getDialect()) instanceof Oracle12cDialect))) {
            List list = s.createQuery("select distinct foo from Baz baz join baz.fooArray foo").list();
            Assert.assertTrue("collection.elements find", ((list.size()) == 2));
        }
        List list = s.createQuery("select foo from Baz baz join baz.fooSet foo").list();
        Assert.assertTrue("association.elements find", ((list.size()) == 1));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        Assert.assertEquals(1, ((Long) (s.createQuery("select count(*) from Bar").iterate().next())).longValue());
        baz = ((Baz) (s.createQuery("select baz from Baz baz order by baz").list().get(0)));
        Assert.assertTrue("collection of custom types - added element", (((baz.getCustoms().size()) == 4) && ((baz.getCustoms().get(0)) != null)));
        Assert.assertTrue("component of component in collection", ((baz.getComponents()[1].getSubcomponent()) != null));
        Assert.assertTrue(((baz.getComponents()[1].getBaz()) == baz));
        Assert.assertTrue("set of objects", ((FooProxy) (baz.getFooSet().iterator().next())).getKey().equals(foo.getKey()));
        Assert.assertTrue("collection removed", ((baz.getStringArray().length) == 0));
        Assert.assertTrue("changed element", baz.getStringList().get(0).equals("new value"));
        Assert.assertTrue("replaced set", ((baz.getStringSet().size()) == 0));
        Assert.assertTrue("array element change", ((baz.getTimeArray()[2]) != null));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        // System.out.println( s.print(baz) );
        baz.getStringSet().add("two");
        baz.getStringSet().add("one");
        baz.getBag().add("three");
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        baz = ((Baz) (s.createQuery("select baz from Baz baz order by baz").list().get(0)));
        Assert.assertTrue(((baz.getStringSet().size()) == 2));
        Assert.assertTrue(baz.getStringSet().first().equals("one"));
        Assert.assertTrue(baz.getStringSet().last().equals("two"));
        Assert.assertTrue(((baz.getBag().size()) == 5));
        baz.getStringSet().remove("two");
        baz.getBag().remove("duplicate");
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        Assert.assertEquals(1, ((Long) (s.createQuery("select count(*) from Bar").iterate().next())).longValue());
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        Bar bar = new Bar();
        Bar bar2 = new Bar();
        s.save(bar);
        s.save(bar2);
        baz.setTopFoos(new HashSet());
        baz.getTopFoos().add(bar);
        baz.getTopFoos().add(bar2);
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        baz.setTopGlarchez(new TreeMap());
        GlarchProxy g = new Glarch();
        s.save(g);
        baz.getTopGlarchez().put('G', g);
        HashMap map = new HashMap();
        map.put(bar, g);
        map.put(bar2, g);
        baz.setFooToGlarch(map);
        map = new HashMap();
        map.put(new FooComponent("name", 123, null, null), bar);
        map.put(new FooComponent("nameName", 12, null, null), bar);
        baz.setFooComponentToFoo(map);
        map = new HashMap();
        map.put(bar, g);
        baz.setGlarchToFoo(map);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        baz = ((Baz) (s.createQuery("select baz from Baz baz order by baz").list().get(0)));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        Session s2 = openSession();
        Transaction txn2 = s2.beginTransaction();
        Assert.assertEquals(3, ((Long) (s2.createQuery("select count(*) from Bar").iterate().next())).longValue());
        Baz baz2 = ((Baz) (s2.createQuery("select baz from Baz baz order by baz").list().get(0)));
        Object o = baz2.getFooComponentToFoo().get(new FooComponent("name", 123, null, null));
        Assert.assertTrue(((o == (baz2.getFooComponentToFoo().get(new FooComponent("nameName", 12, null, null)))) && (o != null)));
        txn2.commit();
        s2.close();
        Assert.assertTrue(Hibernate.isInitialized(baz.getFooToGlarch()));
        Assert.assertTrue(((baz.getTopFoos().size()) == 2));
        Assert.assertTrue(((baz.getTopGlarchez().size()) == 1));
        Assert.assertTrue(((baz.getTopFoos().iterator().next()) != null));
        Assert.assertTrue(((baz.getStringSet().size()) == 1));
        Assert.assertTrue(((baz.getBag().size()) == 4));
        Assert.assertTrue(((baz.getFooToGlarch().size()) == 2));
        Assert.assertTrue(((baz.getFooComponentToFoo().size()) == 2));
        Assert.assertTrue(((baz.getGlarchToFoo().size()) == 1));
        Iterator iter = baz.getFooToGlarch().keySet().iterator();
        for (int i = 0; i < 2; i++)
            Assert.assertTrue(((iter.next()) instanceof BarProxy));

        FooComponent fooComp = ((FooComponent) (baz.getFooComponentToFoo().keySet().iterator().next()));
        Assert.assertTrue((((((fooComp.getCount()) == 123) && (fooComp.getName().equals("name"))) || (((fooComp.getCount()) == 12) && (fooComp.getName().equals("nameName")))) && ((baz.getFooComponentToFoo().get(fooComp)) instanceof BarProxy)));
        Glarch g2 = new Glarch();
        s.save(g2);
        g = ((GlarchProxy) (baz.getTopGlarchez().get('G')));
        baz.getTopGlarchez().put('H', g);
        baz.getTopGlarchez().put('G', g2);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getTopGlarchez().size()) == 2));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        Assert.assertEquals(3, ((Long) (s.createQuery("select count(*) from Bar").iterate().next())).longValue());
        baz = ((Baz) (s.createQuery("select baz from Baz baz order by baz").list().get(0)));
        Assert.assertTrue(((baz.getTopGlarchez().size()) == 2));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 1));
        txn.commit();
        final Session s3 = ((Session) (SerializationHelper.deserialize(SerializationHelper.serialize(s))));
        s.close();
        txn2 = s3.beginTransaction();
        baz = ((Baz) (s3.load(Baz.class, baz.getCode())));
        Assert.assertEquals(3, ((Long) (s3.createQuery("select count(*) from Bar").iterate().next())).longValue());
        s3.delete(baz);
        s3.delete(baz.getTopGlarchez().get('G'));
        s3.delete(baz.getTopGlarchez().get('H'));
        int rows = s3.doReturningWork(new org.hibernate.jdbc.AbstractReturningWork<Integer>() {
            @Override
            public Integer execute(Connection connection) throws SQLException {
                final String sql = ((("update " + (getDialect().openQuote())) + "glarchez") + (getDialect().closeQuote())) + " set baz_map_id=null where baz_map_index='a'";
                Statement st = connection.createStatement();
                return st.executeUpdate(sql);
            }
        });
        Assert.assertTrue((rows == 1));
        Assert.assertEquals(2, doDelete(s3, "from Bar bar"));
        FooProxy[] arr = baz.getFooArray();
        Assert.assertTrue("new array of objects", (((arr.length) == 4) && (arr[1].getKey().equals(foo.getKey()))));
        for (int i = 1; i < (arr.length); i++) {
            if ((arr[i]) != null)
                s3.delete(arr[i]);

        }
        s3.load(Qux.class, new Long(666));// nonexistent

        Assert.assertEquals(1, doDelete(s3, "from Glarch g"));
        txn2.commit();
        s3.disconnect();
        Session s4 = ((Session) (SerializationHelper.deserialize(SerializationHelper.serialize(s3))));
        s3.close();
        // s3.reconnect();
        Assert.assertTrue(((s4.load(Qux.class, new Long(666))) != null));// nonexistent

        // s3.disconnect();
        s4.close();
    }

    @Test
    public void testSaveFlush() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Fee fee = new Fee();
        s.save(fee);
        fee.setFi("blah");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        fee = ((Fee) (s.load(Fee.class, fee.getKey())));
        Assert.assertTrue("blah".equals(fee.getFi()));
        s.delete(fee);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testCreateUpdate() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        foo.setString("dirty");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Foo foo2 = new Foo();
        s.load(foo2, foo.getKey());
        // There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
        Assert.assertTrue("create-update", foo.equalsFoo(foo2));
        // System.out.println( s.print(foo2) );
        s.delete(foo2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = new Foo();
        s.save(foo);
        foo.setString("dirty");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.load(foo2, foo.getKey());
        // There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
        Assert.assertTrue("create-update", foo.equalsFoo(foo2));
        // System.out.println( s.print(foo2) );
        s.delete(foo2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testUpdateCollections() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Holder baz = new Holder();
        baz.setName("123");
        Foo f1 = new Foo();
        Foo f2 = new Foo();
        Foo f3 = new Foo();
        One o = new One();
        baz.setOnes(new ArrayList());
        baz.getOnes().add(o);
        Foo[] foos = new Foo[]{ f1, null, f2 };
        baz.setFooArray(foos);
        baz.setFoos(new HashSet());
        baz.getFoos().add(f1);
        s.save(f1);
        s.save(f2);
        s.save(f3);
        s.save(o);
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        baz.getOnes().set(0, null);
        baz.getOnes().add(o);
        baz.getFoos().add(f2);
        foos[0] = f3;
        foos[1] = f1;
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Holder h = ((Holder) (s.load(Holder.class, baz.getId())));
        Assert.assertTrue(((h.getOnes().get(0)) == null));
        Assert.assertTrue(((h.getOnes().get(1)) != null));
        Assert.assertTrue(((h.getFooArray()[0]) != null));
        Assert.assertTrue(((h.getFooArray()[1]) != null));
        Assert.assertTrue(((h.getFooArray()[2]) != null));
        Assert.assertTrue(((h.getFoos().size()) == 2));
        s.getTransaction().commit();
        s.close();
        baz.getFoos().remove(f1);
        baz.getFoos().remove(f2);
        baz.getFooArray()[0] = null;
        baz.getFooArray()[0] = null;
        baz.getFooArray()[0] = null;
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(baz);
        doDelete(s, "from Foo");
        baz.getOnes().remove(o);
        doDelete(s, "from One");
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testCreate() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Foo foo2 = new Foo();
        s.load(foo2, foo.getKey());
        // There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
        Assert.assertTrue("create", foo.equalsFoo(foo2));
        s.delete(foo2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void loadFoo() {
        Session s = openSession();
        s.beginTransaction();
        FooProxy foo = new Foo();
        s.save(foo);
        s.getTransaction().commit();
        s.close();
        final String id = ((Foo) (foo)).key;
        s = openSession();
        s.beginTransaction();
        foo = ((FooProxy) (s.load(Foo.class, id)));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testCallback() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Qux q = new Qux("0");
        s.save(q);
        q.setChild(new Qux("1"));
        s.save(q.getChild());
        Qux q2 = new Qux("2");
        q2.setChild(q.getChild());
        Qux q3 = new Qux("3");
        q.getChild().setChild(q3);
        s.save(q3);
        Qux q4 = new Qux("4");
        q4.setChild(q3);
        s.save(q4);
        s.save(q2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List l = s.createQuery("from Qux").list();
        Assert.assertTrue("", ((l.size()) == 5));
        s.delete(l.get(0));
        s.delete(l.get(1));
        s.delete(l.get(2));
        s.delete(l.get(3));
        s.delete(l.get(4));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testPolymorphism() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Bar bar = new Bar();
        s.save(bar);
        bar.setBarString("bar bar");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        FooProxy foo = ((FooProxy) (s.load(Foo.class, bar.getKey())));
        Assert.assertTrue("polymorphic", (foo instanceof BarProxy));
        Assert.assertTrue("subclass property", ((BarProxy) (foo)).getBarString().equals(bar.getBarString()));
        // System.out.println( s.print(foo) );
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRemoveContains() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        s.flush();
        Assert.assertTrue(s.contains(baz));
        s.evict(baz);
        Assert.assertFalse(s.contains(baz));
        Baz baz2 = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertFalse((baz == baz2));
        s.delete(baz2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testCollectionOfSelf() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Bar bar = new Bar();
        s.save(bar);
        bar.setAbstracts(new HashSet());
        bar.getAbstracts().add(bar);
        Bar bar2 = new Bar();
        bar.getAbstracts().add(bar2);
        bar.setFoo(bar);
        s.save(bar2);
        s.getTransaction().commit();
        s.close();
        bar.setAbstracts(null);
        s = openSession();
        s.beginTransaction();
        s.load(bar, bar.getKey());
        bar2 = s.load(Bar.class, bar2.getKey());
        Assert.assertTrue("collection contains self", (((bar.getAbstracts().size()) == 2) && (bar.getAbstracts().contains(bar))));
        Assert.assertTrue("association to self", ((bar.getFoo()) == bar));
        // for MySQL :(
        bar.getAbstracts().clear();
        bar.setFoo(null);
        s.flush();
        s.delete(bar);
        s.delete(bar2);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testFind() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Bar bar = new Bar();
        s.save(bar);
        bar.setBarString("bar bar");
        bar.setString("xxx");
        Foo foo = new Foo();
        s.save(foo);
        foo.setString("foo bar");
        s.save(new Foo());
        s.save(new Bar());
        List list1 = s.createQuery("select foo from Foo foo where foo.string='foo bar'").list();
        Assert.assertTrue("find size", ((list1.size()) == 1));
        Assert.assertTrue("find ==", ((list1.get(0)) == foo));
        List list2 = s.createQuery("from Foo foo order by foo.string, foo.date").list();
        Assert.assertTrue("find size", ((list2.size()) == 4));
        list1 = s.createQuery("from Foo foo where foo.class='B'").list();
        Assert.assertTrue("class special property", ((list1.size()) == 2));
        list1 = s.createQuery("from Foo foo where foo.class=Bar").list();
        Assert.assertTrue("class special property", ((list1.size()) == 2));
        list1 = s.createQuery("from Foo foo where foo.class=Bar").list();
        list2 = s.createQuery("select bar from Bar bar, Foo foo where bar.string = foo.string and not bar=foo").list();
        Assert.assertTrue("class special property", ((list1.size()) == 2));
        Assert.assertTrue("select from a subclass", ((list2.size()) == 1));
        Trivial t = new Trivial();
        s.save(t);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        list1 = s.createQuery("from Foo foo where foo.string='foo bar'").list();
        Assert.assertTrue("find size", ((list1.size()) == 1));
        // There is an interbase bug that causes null integers to return as 0, also numeric precision is <= 15
        Assert.assertTrue("find equals", ((Foo) (list1.get(0))).equalsFoo(foo));
        list2 = s.createQuery("select foo from Foo foo").list();
        Assert.assertTrue("find size", ((list2.size()) == 5));
        List list3 = s.createQuery("from Bar bar where bar.barString='bar bar'").list();
        Assert.assertTrue("find size", ((list3.size()) == 1));
        Assert.assertTrue("find same instance", ((list2.contains(list1.get(0))) && (list2.contains(list2.get(0)))));
        Assert.assertTrue(((s.createQuery("from Trivial").list().size()) == 1));
        doDelete(s, "from Trivial");
        list2 = s.createQuery("from Foo foo where foo.date = ?").setParameter(0, new java.sql.Date(123), DATE).list();
        Assert.assertTrue("find by date", ((list2.size()) == 4));
        Iterator iter = list2.iterator();
        while (iter.hasNext()) {
            s.delete(iter.next());
        } 
        list2 = s.createQuery("from Foo foo").list();
        Assert.assertTrue("find deleted", ((list2.size()) == 0));
        txn.commit();
        s.close();
    }

    @Test
    public void testDeleteRecursive() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo x = new Foo();
        Foo y = new Foo();
        x.setFoo(y);
        y.setFoo(x);
        s.save(x);
        s.save(y);
        s.flush();
        s.delete(y);
        s.delete(x);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testReachability() throws Exception {
        // first for unkeyed collections
        Session s = openSession();
        s.beginTransaction();
        Baz baz1 = new Baz();
        s.save(baz1);
        Baz baz2 = new Baz();
        s.save(baz2);
        baz1.setIntArray(new int[]{ 1, 2, 3, 4 });
        baz1.setFooSet(new HashSet());
        Foo foo = new Foo();
        s.save(foo);
        baz1.getFooSet().add(foo);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz1 = ((Baz) (s.load(Baz.class, baz1.getCode())));
        baz2.setFooSet(baz1.getFooSet());
        baz1.setFooSet(null);
        baz2.setIntArray(baz1.getIntArray());
        baz1.setIntArray(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz1 = ((Baz) (s.load(Baz.class, baz1.getCode())));
        Assert.assertTrue("unkeyed reachability", ((baz2.getIntArray().length) == 4));
        Assert.assertTrue("unkeyed reachability", ((baz2.getFooSet().size()) == 1));
        Assert.assertTrue("unkeyed reachability", ((baz1.getIntArray().length) == 0));
        Assert.assertTrue("unkeyed reachability", ((baz1.getFooSet().size()) == 0));
        // System.out.println( s.print(baz1) + s.print(baz2) );
        FooProxy fp = ((FooProxy) (baz2.getFooSet().iterator().next()));
        s.delete(fp);
        s.delete(baz1);
        s.delete(baz2);
        s.getTransaction().commit();
        s.close();
        // now for collections of collections
        s = openSession();
        s.beginTransaction();
        baz1 = new Baz();
        s.save(baz1);
        baz2 = new Baz();
        s.save(baz2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz1 = ((Baz) (s.load(Baz.class, baz1.getCode())));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz1 = ((Baz) (s.load(Baz.class, baz1.getCode())));
        s.delete(baz1);
        s.delete(baz2);
        s.getTransaction().commit();
        s.close();
        // now for keyed collections
        s = openSession();
        s.beginTransaction();
        baz1 = new Baz();
        s.save(baz1);
        baz2 = new Baz();
        s.save(baz2);
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        s.save(foo1);
        s.save(foo2);
        baz1.setFooArray(new Foo[]{ foo1, null, foo2 });
        baz1.setStringDateMap(new TreeMap());
        baz1.getStringDateMap().put("today", new Date(System.currentTimeMillis()));
        baz1.getStringDateMap().put("tomorrow", new Date(((System.currentTimeMillis()) + 86400000)));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz1 = ((Baz) (s.load(Baz.class, baz1.getCode())));
        baz2.setFooArray(baz1.getFooArray());
        baz1.setFooArray(null);
        baz2.setStringDateMap(baz1.getStringDateMap());
        baz1.setStringDateMap(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz2 = ((Baz) (s.load(Baz.class, baz2.getCode())));
        baz1 = ((Baz) (s.load(Baz.class, baz1.getCode())));
        Assert.assertTrue("reachability", ((baz2.getStringDateMap().size()) == 2));
        Assert.assertTrue("reachability", ((baz2.getFooArray().length) == 3));
        Assert.assertTrue("reachability", ((baz1.getStringDateMap().size()) == 0));
        Assert.assertTrue("reachability", ((baz1.getFooArray().length) == 0));
        Assert.assertTrue("null element", ((baz2.getFooArray()[1]) == null));
        Assert.assertTrue("non-null element", ((baz2.getStringDateMap().get("today")) != null));
        Assert.assertTrue("non-null element", ((baz2.getStringDateMap().get("tomorrow")) != null));
        Assert.assertTrue("null element", ((baz2.getStringDateMap().get("foo")) == null));
        s.delete(baz2.getFooArray()[0]);
        s.delete(baz2.getFooArray()[2]);
        s.delete(baz1);
        s.delete(baz2);
        s.flush();
        Assert.assertTrue(((s.createQuery("from java.lang.Object").list().size()) == 0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testPersistentLifecycle() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Qux q = new Qux();
        s.save(q);
        q.setStuff("foo bar baz qux");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        q = ((Qux) (s.load(Qux.class, q.getKey())));
        Assert.assertTrue("lifecycle create", q.getCreated());
        Assert.assertTrue("lifecycle load", q.getLoaded());
        Assert.assertTrue("lifecycle subobject", ((q.getFoo()) != null));
        s.delete(q);
        Assert.assertTrue("lifecycle delete", q.getDeleted());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Assert.assertTrue("subdeletion", ((s.createQuery("from Foo foo").list().size()) == 0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testIterators() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        for (int i = 0; i < 10; i++) {
            Qux q = new Qux();
            Object qid = s.save(q);
            Assert.assertTrue("not null", (qid != null));
        }
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Iterator iter = s.createQuery("from Qux q where q.stuff is null").iterate();
        int count = 0;
        while (iter.hasNext()) {
            Qux q = ((Qux) (iter.next()));
            q.setStuff("foo");
            if ((count == 0) || (count == 5))
                iter.remove();

            count++;
        } 
        Assert.assertTrue("iterate", (count == 10));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Assert.assertEquals(8, doDelete(s, "from Qux q where q.stuff=?", "foo", STRING));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        iter = s.createQuery("from Qux q").iterate();
        Assert.assertTrue("empty iterator", (!(iter.hasNext())));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testVersioning() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        GlarchProxy g = new Glarch();
        s.save(g);
        GlarchProxy g2 = new Glarch();
        s.save(g2);
        Serializable gid = s.getIdentifier(g);
        Serializable g2id = s.getIdentifier(g2);
        g.setName("glarch");
        txn.commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Glarch.class);
        s = openSession();
        txn = s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        s.lock(g, UPGRADE);
        g2 = ((GlarchProxy) (s.load(Glarch.class, g2id)));
        Assert.assertTrue("version", ((g.getVersion()) == 1));
        Assert.assertTrue("version", ((g.getDerivedVersion()) == 1));
        Assert.assertTrue("version", ((g2.getVersion()) == 0));
        g.setName("foo");
        Assert.assertTrue("find by version", ((s.createQuery("from Glarch g where g.version=2").list().size()) == 1));
        g.setName("bar");
        txn.commit();
        s.close();
        sessionFactory().getCache().evictEntityRegion(Glarch.class);
        s = openSession();
        txn = s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        g2 = ((GlarchProxy) (s.load(Glarch.class, g2id)));
        Assert.assertTrue("version", ((g.getVersion()) == 3));
        Assert.assertTrue("version", ((g.getDerivedVersion()) == 3));
        Assert.assertTrue("version", ((g2.getVersion()) == 0));
        g.setNext(null);
        g2.setNext(g);
        s.delete(g2);
        s.delete(g);
        txn.commit();
        s.close();
    }

    @Test
    public void testVersionedCollections() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        GlarchProxy g = new Glarch();
        s.save(g);
        g.setProxyArray(new GlarchProxy[]{ g });
        String gid = ((String) (s.getIdentifier(g)));
        ArrayList list = new ArrayList();
        list.add("foo");
        g.setStrings(list);
        HashSet set = new HashSet();
        set.add(g);
        g.setProxySet(set);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue(((g.getStrings().size()) == 1));
        Assert.assertTrue(((g.getProxyArray().length) == 1));
        Assert.assertTrue(((g.getProxySet().size()) == 1));
        Assert.assertTrue("versioned collection before", ((g.getVersion()) == 1));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue(g.getStrings().get(0).equals("foo"));
        Assert.assertTrue(((g.getProxyArray()[0]) == g));
        Assert.assertTrue(((g.getProxySet().iterator().next()) == g));
        Assert.assertTrue("versioned collection before", ((g.getVersion()) == 1));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue("versioned collection before", ((g.getVersion()) == 1));
        g.getStrings().add("bar");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue("versioned collection after", ((g.getVersion()) == 2));
        Assert.assertTrue("versioned collection after", ((g.getStrings().size()) == 2));
        g.setProxyArray(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue("versioned collection after", ((g.getVersion()) == 3));
        Assert.assertTrue("versioned collection after", ((g.getProxyArray().length) == 0));
        g.setFooComponents(new ArrayList());
        g.setProxyArray(null);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue("versioned collection after", ((g.getVersion()) == 4));
        s.delete(g);
        s.flush();
        Assert.assertTrue(((s.createQuery("from java.lang.Object").list().size()) == 0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testRecursiveLoad() throws Exception {
        // Non polymorphic class (there is an implementation optimization
        // being tested here)
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        GlarchProxy last = new Glarch();
        s.save(last);
        last.setOrder(((short) (0)));
        for (int i = 0; i < 5; i++) {
            GlarchProxy next = new Glarch();
            s.save(next);
            last.setNext(next);
            last = next;
            last.setOrder(((short) (i + 1)));
        }
        Iterator iter = s.createQuery("from Glarch g").iterate();
        while (iter.hasNext()) {
            iter.next();
        } 
        List list = s.createQuery("from Glarch g").list();
        Assert.assertTrue("recursive find", ((list.size()) == 6));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        list = s.createQuery("from Glarch g").list();
        Assert.assertTrue("recursive iter", ((list.size()) == 6));
        list = s.createQuery("from Glarch g where g.next is not null").list();
        Assert.assertTrue("recursive iter", ((list.size()) == 5));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        iter = s.createQuery("from Glarch g order by g.order asc").iterate();
        while (iter.hasNext()) {
            GlarchProxy g = ((GlarchProxy) (iter.next()));
            Assert.assertTrue("not null", (g != null));
            iter.remove();
        } 
        txn.commit();
        s.close();
        // Same thing but using polymorphic class (no optimisation possible):
        s = openSession();
        txn = s.beginTransaction();
        FooProxy flast = new Bar();
        s.save(flast);
        flast.setString("foo0");
        for (int i = 0; i < 5; i++) {
            FooProxy foo = new Bar();
            s.save(foo);
            flast.setFoo(foo);
            flast = flast.getFoo();
            flast.setString(("foo" + (i + 1)));
        }
        iter = s.createQuery("from Foo foo").iterate();
        while (iter.hasNext()) {
            iter.next();
        } 
        list = s.createQuery("from Foo foo").list();
        Assert.assertTrue("recursive find", ((list.size()) == 6));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        list = s.createQuery("from Foo foo").list();
        Assert.assertTrue("recursive iter", ((list.size()) == 6));
        iter = list.iterator();
        while (iter.hasNext()) {
            Assert.assertTrue("polymorphic recursive load", ((iter.next()) instanceof BarProxy));
        } 
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        iter = s.createQuery("from Foo foo order by foo.string asc").iterate();
        while (iter.hasNext()) {
            BarProxy bar = ((BarProxy) (iter.next()));
            Assert.assertTrue("not null", (bar != null));
            iter.remove();
        } 
        txn.commit();
        s.close();
    }

    @Test
    public void testScrollableIterator() throws Exception {
        // skip if not one of these named dialects
        // 9i/10g too because of inheritence...
        boolean match = ((((getDialect()) instanceof DB2Dialect) || ((getDialect()) instanceof SybaseDialect)) || ((getDialect()) instanceof HSQLDialect)) || ((getDialect()) instanceof Oracle8iDialect);
        if (!match) {
            return;
        }
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        s.save(new Foo());
        s.save(new Foo());
        s.save(new Foo());
        s.save(new Bar());
        Query query = s.createQuery("select f, f.integer from Foo f");
        Assert.assertTrue(((query.getReturnTypes().length) == 2));
        ScrollableResults iter = query.scroll();
        Assert.assertTrue(iter.next());
        Assert.assertTrue(iter.scroll(1));
        FooProxy f2 = ((FooProxy) (iter.get()[0]));
        Assert.assertTrue((f2 != null));
        Assert.assertTrue(iter.scroll((-1)));
        Object f1 = iter.get(0);
        iter.next();
        Assert.assertTrue(((f1 != null) && ((iter.get(0)) == f2)));
        iter.getInteger(1);
        Assert.assertTrue((!(iter.scroll(100))));
        Assert.assertTrue(iter.first());
        Assert.assertTrue(iter.scroll(3));
        Object f4 = iter.get(0);
        Assert.assertTrue((f4 != null));
        Assert.assertTrue((!(iter.next())));
        Assert.assertTrue(iter.first());
        Assert.assertTrue(((iter.get(0)) == f1));
        Assert.assertTrue(iter.last());
        Assert.assertTrue(((iter.get(0)) == f4));
        Assert.assertTrue(iter.previous());
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        query = s.createQuery("select f, f.integer from Foo f");
        Assert.assertTrue(((query.getReturnTypes().length) == 2));
        iter = query.scroll();
        Assert.assertTrue(iter.next());
        Assert.assertTrue(iter.scroll(1));
        f2 = ((FooProxy) (iter.get()[0]));
        Assert.assertTrue((f2 != null));
        Assert.assertTrue((((f2.getString()) != null) && ((f2.getComponent().getImportantDates().length) > 0)));
        Assert.assertTrue(iter.scroll((-1)));
        f1 = iter.get(0);
        iter.next();
        Assert.assertTrue(((f1 != null) && ((iter.get(0)) == f2)));
        iter.getInteger(1);
        Assert.assertTrue((!(iter.scroll(100))));
        Assert.assertTrue(iter.first());
        Assert.assertTrue(iter.scroll(3));
        f4 = iter.get(0);
        Assert.assertTrue((f4 != null));
        Assert.assertTrue((!(iter.next())));
        Assert.assertTrue(iter.first());
        Assert.assertTrue(((iter.get(0)) == f1));
        Assert.assertTrue(iter.last());
        Assert.assertTrue(((iter.get(0)) == f4));
        Assert.assertTrue(iter.previous());
        int i = 0;
        for (Object entity : s.createQuery("from Foo").list()) {
            i++;
            s.delete(entity);
        }
        Assert.assertEquals(4, i);
        s.flush();
        Assert.assertTrue(((s.createQuery("from java.lang.Object").list().size()) == 0));
        txn.commit();
        s.close();
    }

    @Test
    public void testMultiColumnQueries() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        Foo foo1 = new Foo();
        s.save(foo1);
        foo.setFoo(foo1);
        List l = s.createQuery("select parent, child from Foo parent, Foo child where parent.foo = child").list();
        Assert.assertTrue("multi-column find", ((l.size()) == 1));
        Iterator rs = null;
        Object[] row = null;
        // Derby does not support multiple DISTINCT aggregates
        if (!((getDialect()) instanceof DerbyDialect)) {
            rs = s.createQuery("select count(distinct child.id), count(distinct parent.id) from Foo parent, Foo child where parent.foo = child").iterate();
            row = ((Object[]) (rs.next()));
            Assert.assertTrue("multi-column count", ((((Long) (row[0])).intValue()) == 1));
            Assert.assertTrue("multi-column count", ((((Long) (row[1])).intValue()) == 1));
            Assert.assertTrue((!(rs.hasNext())));
        }
        rs = s.createQuery("select child.id, parent.id, child.long from Foo parent, Foo child where parent.foo = child").iterate();
        row = ((Object[]) (rs.next()));
        Assert.assertTrue("multi-column id", row[0].equals(foo.getFoo().getKey()));
        Assert.assertTrue("multi-column id", row[1].equals(foo.getKey()));
        Assert.assertTrue("multi-column property", row[2].equals(foo.getFoo().getLong()));
        Assert.assertTrue((!(rs.hasNext())));
        rs = s.createQuery("select child.id, parent.id, child.long, child, parent.foo from Foo parent, Foo child where parent.foo = child").iterate();
        row = ((Object[]) (rs.next()));
        Assert.assertTrue((((((foo.getFoo().getKey().equals(row[0])) && (foo.getKey().equals(row[1]))) && (foo.getFoo().getLong().equals(row[2]))) && ((row[3]) == (foo.getFoo()))) && ((row[3]) == (row[4]))));
        Assert.assertTrue((!(rs.hasNext())));
        row = ((Object[]) (l.get(0)));
        Assert.assertTrue("multi-column find", (((row[0]) == foo) && ((row[1]) == (foo.getFoo()))));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        Iterator iter = s.createQuery("select parent, child from Foo parent, Foo child where parent.foo = child and parent.string='a string'").iterate();
        int deletions = 0;
        while (iter.hasNext()) {
            Object[] pnc = ((Object[]) (iter.next()));
            s.delete(pnc[0]);
            s.delete(pnc[1]);
            deletions++;
        } 
        Assert.assertTrue("multi-column iterate", (deletions == 1));
        txn.commit();
        s.close();
    }

    @Test
    public void testDeleteTransient() throws Exception {
        Fee fee = new Fee();
        Fee fee2 = new Fee();
        fee2.setAnotherFee(fee);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.save(fee);
        s.save(fee2);
        s.flush();
        fee.setCount(123);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        s.delete(fee);
        s.delete(fee2);
        // foo.setAnotherFee(null);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Assert.assertTrue(((s.createQuery("from Fee fee").list().size()) == 0));
        tx.commit();
        s.close();
    }

    @Test
    public void testDeleteUpdatedTransient() throws Exception {
        Fee fee = new Fee();
        Fee fee2 = new Fee();
        fee2.setAnotherFee(fee);
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.save(fee);
        s.save(fee2);
        s.flush();
        fee.setCount(123);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        s.update(fee);
        // fee2.setAnotherFee(null);
        s.update(fee2);
        s.delete(fee);
        s.delete(fee2);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Assert.assertTrue(((s.createQuery("from Fee fee").list().size()) == 0));
        tx.commit();
        s.close();
    }

    @Test
    public void testUpdateOrder() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Fee fee1 = new Fee();
        s.save(fee1);
        Fee fee2 = new Fee();
        fee1.setFee(fee2);
        fee2.setFee(fee1);
        fee2.setFees(new HashSet());
        Fee fee3 = new Fee();
        fee3.setFee(fee1);
        fee3.setAnotherFee(fee2);
        fee2.setAnotherFee(fee3);
        s.save(fee3);
        s.save(fee2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        fee1.setCount(10);
        fee2.setCount(20);
        fee3.setCount(30);
        s.update(fee1);
        s.update(fee2);
        s.update(fee3);
        s.flush();
        s.delete(fee1);
        s.delete(fee2);
        s.delete(fee3);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Assert.assertTrue(((s.createQuery("from Fee fee").list().size()) == 0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testUpdateFromTransient() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Fee fee1 = new Fee();
        s.save(fee1);
        Fee fee2 = new Fee();
        fee1.setFee(fee2);
        fee2.setFee(fee1);
        fee2.setFees(new HashSet());
        Fee fee3 = new Fee();
        fee3.setFee(fee1);
        fee3.setAnotherFee(fee2);
        fee2.setAnotherFee(fee3);
        s.save(fee3);
        s.save(fee2);
        s.getTransaction().commit();
        s.close();
        fee1.setFi("changed");
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(fee1);
        s.getTransaction().commit();
        s.close();
        Qux q = new Qux("quxxy");
        q.setTheKey(0);
        fee1.setQux(q);
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(fee1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        fee1 = ((Fee) (s.load(Fee.class, fee1.getKey())));
        Assert.assertTrue("updated from transient", fee1.getFi().equals("changed"));
        Assert.assertTrue("unsaved value", ((fee1.getQux()) != null));
        s.delete(fee1.getQux());
        fee1.setQux(null);
        s.getTransaction().commit();
        s.close();
        fee2.setFi("CHANGED");
        fee2.getFees().add("an element");
        fee1.setFi("changed again");
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(fee2);
        s.update(fee1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Fee fee = new Fee();
        s.load(fee, fee2.getKey());
        fee1 = ((Fee) (s.load(Fee.class, fee1.getKey())));
        Assert.assertTrue("updated from transient", fee1.getFi().equals("changed again"));
        Assert.assertTrue("updated from transient", fee.getFi().equals("CHANGED"));
        Assert.assertTrue("updated collection", fee.getFees().contains("an element"));
        s.getTransaction().commit();
        s.close();
        fee.getFees().clear();
        fee.getFees().add("new element");
        fee1.setFee(null);
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(fee);
        s.saveOrUpdate(fee1);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.load(fee, fee.getKey());
        Assert.assertTrue("update", ((fee.getAnotherFee()) != null));
        Assert.assertTrue("update", ((fee.getFee()) != null));
        Assert.assertTrue("update", ((fee.getAnotherFee().getFee()) == (fee.getFee())));
        Assert.assertTrue("updated collection", fee.getFees().contains("new element"));
        Assert.assertTrue("updated collection", (!(fee.getFees().contains("an element"))));
        s.getTransaction().commit();
        s.close();
        fee.setQux(new Qux("quxy"));
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(fee);
        s.getTransaction().commit();
        s.close();
        fee.getQux().setStuff("xxx");
        s = openSession();
        s.beginTransaction();
        s.saveOrUpdate(fee);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.load(fee, fee.getKey());
        Assert.assertTrue("cascade update", ((fee.getQux()) != null));
        Assert.assertTrue("cascade update", fee.getQux().getStuff().equals("xxx"));
        Assert.assertTrue("update", ((fee.getAnotherFee()) != null));
        Assert.assertTrue("update", ((fee.getFee()) != null));
        Assert.assertTrue("update", ((fee.getAnotherFee().getFee()) == (fee.getFee())));
        fee.getAnotherFee().setAnotherFee(null);
        s.delete(fee);
        doDelete(s, "from Fee fee");
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Assert.assertTrue(((s.createQuery("from Fee fee").list().size()) == 0));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testArraysOfTimes() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        baz.setDefaults();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz.getTimeArray()[2] = new Date(123);
        baz.getTimeArray()[3] = new Time(1234);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testComponents() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Foo foo = new Foo();
        // foo.setComponent( new FooComponent("foo", 69, null, new FooComponent("bar", 96, null, null) ) );
        s.save(foo);
        foo.getComponent().setName("IFA");
        txn.commit();
        s.close();
        foo.setComponent(null);
        s = openSession();
        txn = s.beginTransaction();
        s.load(foo, foo.getKey());
        Assert.assertTrue("save components", ((foo.getComponent().getName().equals("IFA")) && (foo.getComponent().getSubcomponent().getName().equals("bar"))));
        Assert.assertTrue("cascade save via component", ((foo.getComponent().getGlarch()) != null));
        foo.getComponent().getSubcomponent().setName("baz");
        txn.commit();
        s.close();
        foo.setComponent(null);
        s = openSession();
        txn = s.beginTransaction();
        s.load(foo, foo.getKey());
        Assert.assertTrue("update components", ((foo.getComponent().getName().equals("IFA")) && (foo.getComponent().getSubcomponent().getName().equals("baz"))));
        s.delete(foo);
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        foo = new Foo();
        s.save(foo);
        foo.setCustom(new String[]{ "one", "two" });
        Assert.assertTrue(((s.createQuery("from Foo foo where foo.custom.s1 = 'one'").list().get(0)) == foo));
        s.delete(foo);
        txn.commit();
        s.close();
    }

    @Test
    public void testNoForeignKeyViolations() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Glarch g1 = new Glarch();
        Glarch g2 = new Glarch();
        g1.setNext(g2);
        g2.setNext(g1);
        s.save(g1);
        s.save(g2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List l = s.createQuery("from Glarch g where g.next is not null").list();
        s.delete(l.get(0));
        s.delete(l.get(1));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLazyCollections() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Qux q = new Qux();
        s.save(q);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        q = ((Qux) (s.load(Qux.class, q.getKey())));
        s.getTransaction().commit();
        s.close();
        System.out.println("Two exceptions are supposed to occur:");
        boolean ok = false;
        try {
            q.getMoreFums().isEmpty();
        } catch (LazyInitializationException e) {
            ok = true;
        }
        Assert.assertTrue("lazy collection with one-to-many", ok);
        ok = false;
        try {
            q.getFums().isEmpty();
        } catch (LazyInitializationException e) {
            ok = true;
        }
        Assert.assertTrue("lazy collection with many-to-many", ok);
        s = openSession();
        s.beginTransaction();
        q = ((Qux) (s.load(Qux.class, q.getKey())));
        s.delete(q);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-7603")
    public void testLazyCollectionsTouchedDuringPreCommit() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Qux q = new Qux();
        s.save(q);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        q = ((Qux) (s.load(Qux.class, q.getKey())));
        s.getTransaction().commit();
        // clear the session
        s.clear();
        // now reload the proxy and delete it
        s.beginTransaction();
        final Qux qToDelete = ((Qux) (s.load(Qux.class, q.getKey())));
        // register a pre commit process that will touch the collection and delete the entity
        getActionQueue().registerProcess(new BeforeTransactionCompletionProcess() {
            @Override
            public void doBeforeTransactionCompletion(SessionImplementor session) {
                qToDelete.getFums().size();
            }
        });
        s.delete(qToDelete);
        boolean ok = false;
        try {
            s.getTransaction().commit();
        } catch (LazyInitializationException e) {
            ok = true;
            s.getTransaction().rollback();
        } catch (TransactionException te) {
            if ((te.getCause()) instanceof LazyInitializationException) {
                ok = true;
            }
            s.getTransaction().rollback();
        } finally {
            s.close();
        }
        Assert.assertTrue("lazy collection should have blown in the before trans completion", ok);
        s = openSession();
        s.beginTransaction();
        q = ((Qux) (s.load(Qux.class, q.getKey())));
        s.delete(q);
        s.getTransaction().commit();
        s.close();
    }

    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA currently requires specifying table name by 'FOR UPDATE of t1.c1' if there are more than one tables/views/subqueries in the FROM clause")
    @SkipForDialect(value = H2Dialect.class, comment = "Feature not supported: MVCC=TRUE && FOR UPDATE && JOIN")
    @Test
    public void testNewSessionLifecycle() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Serializable fid = null;
        try {
            Foo f = new Foo();
            s.save(f);
            fid = s.getIdentifier(f);
            s.getTransaction().commit();
        } catch (Exception e) {
            s.getTransaction().rollback();
            throw e;
        } finally {
            s.close();
        }
        s = openSession();
        s.beginTransaction();
        try {
            Foo f = new Foo();
            s.delete(f);
            s.getTransaction().commit();
        } catch (Exception e) {
            s.getTransaction().rollback();
            throw e;
        } finally {
            s.close();
        }
        s = openSession();
        s.beginTransaction();
        try {
            Foo f = ((Foo) (s.load(Foo.class, fid, UPGRADE)));
            s.delete(f);
            s.flush();
            s.getTransaction().commit();
        } catch (Exception e) {
            s.getTransaction().rollback();
            throw e;
        } finally {
            s.close();
        }
    }

    @Test
    public void testOrderBy() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        List list = s.createQuery("select foo from Foo foo, Fee fee where foo.dependent = fee order by foo.string desc, foo.component.count asc, fee.id").list();
        Assert.assertTrue("order by", ((list.size()) == 1));
        Foo foo2 = new Foo();
        s.save(foo2);
        foo.setFoo(foo2);
        list = s.createQuery("select foo.foo, foo.dependent from Foo foo order by foo.foo.string desc, foo.component.count asc, foo.dependent.id").list();
        Assert.assertTrue("order by", ((list.size()) == 1));
        list = s.createQuery("select foo from Foo foo order by foo.dependent.id, foo.dependent.fi").list();
        Assert.assertTrue("order by", ((list.size()) == 2));
        s.delete(foo);
        s.delete(foo2);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        Many manyB = new Many();
        s.save(manyB);
        One oneB = new One();
        s.save(oneB);
        oneB.setValue("b");
        manyB.setOne(oneB);
        Many manyA = new Many();
        s.save(manyA);
        One oneA = new One();
        s.save(oneA);
        oneA.setValue("a");
        manyA.setOne(oneA);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List results = s.createQuery((("SELECT one FROM " + (One.class.getName())) + " one ORDER BY one.value ASC")).list();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("'a' isn't first element", "a", ((One) (results.get(0))).getValue());
        Assert.assertEquals("'b' isn't second element", "b", ((One) (results.get(1))).getValue());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        results = s.createQuery((("SELECT many.one FROM " + (Many.class.getName())) + " many ORDER BY many.one.value ASC, many.one.id")).list();
        Assert.assertEquals(2, results.size());
        Assert.assertEquals(2, results.size());
        Assert.assertEquals("'a' isn't first element", "a", ((One) (results.get(0))).getValue());
        Assert.assertEquals("'b' isn't second element", "b", ((One) (results.get(1))).getValue());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        oneA = ((One) (s.load(One.class, oneA.getKey())));
        manyA = ((Many) (s.load(Many.class, manyA.getKey())));
        oneB = ((One) (s.load(One.class, oneB.getKey())));
        manyB = ((Many) (s.load(Many.class, manyB.getKey())));
        s.delete(manyA);
        s.delete(oneA);
        s.delete(manyB);
        s.delete(oneB);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testManyToOne() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        One one = new One();
        s.save(one);
        one.setValue("yada");
        Many many = new Many();
        many.setOne(one);
        s.save(many);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        one = ((One) (s.load(One.class, one.getKey())));
        one.getManies().size();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        many = ((Many) (s.load(Many.class, many.getKey())));
        Assert.assertTrue("many-to-one assoc", ((many.getOne()) != null));
        s.delete(many.getOne());
        s.delete(many);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSaveDelete() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo f = new Foo();
        s.save(f);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(s.load(Foo.class, f.getKey()));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testProxyArray() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        GlarchProxy g = new Glarch();
        Glarch g1 = new Glarch();
        Glarch g2 = new Glarch();
        g.setProxyArray(new GlarchProxy[]{ g1, g2 });
        Glarch g3 = new Glarch();
        s.save(g3);
        g2.setProxyArray(new GlarchProxy[]{ null, g3, g });
        Set set = new HashSet();
        set.add(g1);
        set.add(g2);
        g.setProxySet(set);
        s.save(g);
        s.save(g1);
        s.save(g2);
        Serializable id = s.getIdentifier(g);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, id)));
        Assert.assertTrue("array of proxies", ((g.getProxyArray().length) == 2));
        Assert.assertTrue("array of proxies", ((g.getProxyArray()[0]) != null));
        Assert.assertTrue("deferred load test", ((g.getProxyArray()[1].getProxyArray()[0]) == null));
        Assert.assertTrue("deferred load test", ((g.getProxyArray()[1].getProxyArray()[2]) == g));
        Assert.assertTrue("set of proxies", ((g.getProxySet().size()) == 2));
        Iterator iter = s.createQuery("from Glarch g").iterate();
        while (iter.hasNext()) {
            iter.next();
            iter.remove();
        } 
        s.getTransaction().commit();
        s.disconnect();
        SerializationHelper.deserialize(SerializationHelper.serialize(s));
        s.close();
    }

    @Test
    public void testCache() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Immutable im = new Immutable();
        s.save(im);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.load(im, im.getId());
        s.getTransaction().commit();
        s.close();
        final Session s2 = openSession();
        s2.beginTransaction();
        s2.load(im, im.getId());
        Assert.assertEquals("cached object identity", im, s2.createQuery("from Immutable im where im = ?").setParameter(0, im, s2.getTypeHelper().entity(Immutable.class)).uniqueResult());
        s2.doWork(new AbstractWork() {
            @Override
            public void execute(Connection connection) throws SQLException {
                Statement st = connection.createStatement();
                st.executeUpdate("delete from immut");
            }
        });
        s2.getTransaction().commit();
        s2.close();
    }

    @Test
    public void testFindLoad() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        FooProxy foo = new Foo();
        s.save(foo);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = ((FooProxy) (s.createQuery("from Foo foo").list().get(0)));
        FooProxy foo2 = ((FooProxy) (s.load(Foo.class, foo.getKey())));
        Assert.assertTrue("find returns same object as load", (foo == foo2));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo2 = ((FooProxy) (s.load(Foo.class, foo.getKey())));
        foo = ((FooProxy) (s.createQuery("from Foo foo").list().get(0)));
        Assert.assertTrue("find returns same object as load", (foo == foo2));
        doDelete(s, "from Foo foo");
        s.getTransaction().commit();
        s.close();
    }

    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA currently requires specifying table name by 'FOR UPDATE of t1.c1' if there are more than one tables/views/subqueries in the FROM clause")
    @SkipForDialect(value = H2Dialect.class, comment = "Feature not supported: MVCC=TRUE && FOR UPDATE && JOIN")
    @Test
    public void testRefresh() throws Exception {
        final Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        s.save(foo);
        s.flush();
        s.doWork(new AbstractWork() {
            @Override
            public void execute(Connection connection) throws SQLException {
                final String sql = ((("update " + (getDialect().openQuote())) + "foos") + (getDialect().closeQuote())) + " set long_ = -3";
                Statement st = connection.createStatement();
                st.executeUpdate(sql);
            }
        });
        s.refresh(foo);
        Assert.assertEquals(Long.valueOf((-3L)), foo.getLong());
        // NOTE : this test used to test for LockMode.READ here, but that actually highlights a bug
        // `foo` has just been inserted and then updated in this same Session - its lock mode
        // therefore ought to be WRITE.  See https://hibernate.atlassian.net/browse/HHH-12257
        Assert.assertEquals(WRITE, s.getCurrentLockMode(foo));
        s.refresh(foo, UPGRADE);
        if (getDialect().supportsOuterJoinForUpdate()) {
            Assert.assertEquals(UPGRADE, s.getCurrentLockMode(foo));
        }
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testAutoFlush() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        FooProxy foo = new Foo();
        s.save(foo);
        Assert.assertTrue("autoflush create", ((s.createQuery("from Foo foo").list().size()) == 1));
        foo.setChar('X');
        Assert.assertTrue("autoflush update", ((s.createQuery("from Foo foo where foo.char='X'").list().size()) == 1));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        foo = ((FooProxy) (s.load(Foo.class, foo.getKey())));
        // s.update( new Foo(), foo.getKey() );
        // assertTrue( s.find("from Foo foo where not foo.char='X'").size()==1, "autoflush update" );
        if (((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect))) && (!((getDialect()) instanceof PointbaseDialect))) {
            foo.setBytes("osama".getBytes());
            Assert.assertTrue("autoflush collection update", ((s.createQuery("from Foo foo where 111 in elements(foo.bytes)").list().size()) == 1));
            foo.getBytes()[0] = 69;
            Assert.assertTrue("autoflush collection update", ((s.createQuery("from Foo foo where 69 in elements(foo.bytes)").list().size()) == 1));
        }
        s.delete(foo);
        Assert.assertTrue("autoflush delete", ((s.createQuery("from Foo foo").list().size()) == 0));
        txn.commit();
        s.close();
    }

    @Test
    public void testVeto() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Vetoer v = new Vetoer();
        s.save(v);
        s.save(v);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.update(v);
        s.update(v);
        s.delete(v);
        s.delete(v);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testSerializableType() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Vetoer v = new Vetoer();
        v.setStrings(new String[]{ "foo", "bar", "baz" });
        s.save(v);
        Serializable id = s.save(v);
        v.getStrings()[1] = "osama";
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        v = ((Vetoer) (s.load(Vetoer.class, id)));
        Assert.assertTrue("serializable type", v.getStrings()[1].equals("osama"));
        s.delete(v);
        s.delete(v);
        s.flush();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testAutoFlushCollections() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        baz.getStringArray()[0] = "bark";
        Iterator i = s.createQuery("select elements(baz.stringArray) from Baz baz").iterate();
        boolean found = false;
        while (i.hasNext()) {
            if ("bark".equals(i.next()))
                found = true;

        } 
        Assert.assertTrue(found);
        baz.setStringArray(null);
        i = s.createQuery("select distinct elements(baz.stringArray) from Baz baz").iterate();
        Assert.assertTrue((!(i.hasNext())));
        baz.setStringArray(new String[]{ "foo", "bar" });
        i = s.createQuery("select elements(baz.stringArray) from Baz baz").iterate();
        Assert.assertTrue(i.hasNext());
        Foo foo = new Foo();
        s.save(foo);
        s.flush();
        baz.setFooArray(new Foo[]{ foo });
        i = s.createQuery("select foo from Baz baz join baz.fooArray foo").iterate();
        found = false;
        while (i.hasNext()) {
            if (foo == (i.next()))
                found = true;

        } 
        Assert.assertTrue(found);
        baz.getFooArray()[0] = null;
        i = s.createQuery("select foo from Baz baz join baz.fooArray foo").iterate();
        Assert.assertTrue((!(i.hasNext())));
        baz.getFooArray()[0] = foo;
        i = s.createQuery("select elements(baz.fooArray) from Baz baz").iterate();
        Assert.assertTrue(i.hasNext());
        if (((((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect))) && (!((getDialect()) instanceof InterbaseDialect))) && (!((getDialect()) instanceof PointbaseDialect))) && (!((getDialect()) instanceof SAPDBDialect))) {
            baz.getFooArray()[0] = null;
            i = s.createQuery("from Baz baz where ? in elements(baz.fooArray)").setParameter(0, foo, s.getTypeHelper().entity(Foo.class)).iterate();
            Assert.assertTrue((!(i.hasNext())));
            baz.getFooArray()[0] = foo;
            i = s.createQuery("select foo from Foo foo where foo in (select elt from Baz baz join baz.fooArray elt)").iterate();
            Assert.assertTrue(i.hasNext());
        }
        s.delete(foo);
        s.delete(baz);
        tx.commit();
        s.close();
    }

    @Test
    @RequiresDialect(value = H2Dialect.class, comment = "this is more like a unit test")
    public void testUserProvidedConnection() throws Exception {
        ConnectionProvider dcp = ConnectionProviderBuilder.buildConnectionProvider();
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        s.createQuery("from Fo").list();
        tx.commit();
        Connection c = s.disconnect();
        Assert.assertTrue((c != null));
        s.reconnect(c);
        tx = s.beginTransaction();
        s.createQuery("from Fo").list();
        tx.commit();
        c.close();
    }

    @Test
    public void testCachedCollection() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Baz baz = new Baz();
        baz.setDefaults();
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        ((FooComponent) (baz.getTopComponents().get(0))).setCount(99);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((((FooComponent) (baz.getTopComponents().get(0))).getCount()) == 99));
        s.delete(baz);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testComplicatedQuery() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        Foo foo = new Foo();
        Serializable id = s.save(foo);
        Assert.assertTrue((id != null));
        Qux q = new Qux("q");
        foo.getDependent().setQux(q);
        s.save(q);
        q.getFoo().setString("foo2");
        // s.flush();
        // s.connection().commit();
        Assert.assertTrue(s.createQuery("from Foo foo where foo.dependent.qux.foo.string = 'foo2'").iterate().hasNext());
        s.delete(foo);
        txn.commit();
        s.close();
    }

    @Test
    public void testLoadAfterDelete() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Foo foo = new Foo();
        Serializable id = s.save(foo);
        s.flush();
        s.delete(foo);
        boolean err = false;
        try {
            s.load(Foo.class, id);
        } catch (ObjectNotFoundException ode) {
            err = true;
        }
        Assert.assertTrue(err);
        s.flush();
        err = false;
        try {
            ((FooProxy) (s.load(Foo.class, id))).getBool();
        } catch (ObjectNotFoundException onfe) {
            err = true;
        }
        Assert.assertTrue(err);
        id = FumTest.fumKey("abc");// yuck!!

        Fo fo = Fo.newFo(((FumCompositeID) (id)));
        s.save(fo);
        s.flush();
        s.delete(fo);
        err = false;
        try {
            s.load(Fo.class, id);
        } catch (ObjectNotFoundException ode) {
            err = true;
        }
        Assert.assertTrue(err);
        s.flush();
        err = false;
        try {
            s.load(Fo.class, id);
        } catch (ObjectNotFoundException onfe) {
            err = true;
        }
        Assert.assertTrue(err);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testObjectType() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        GlarchProxy g = new Glarch();
        Foo foo = new Foo();
        g.setAny(foo);
        Serializable gid = s.save(g);
        s.save(foo);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((GlarchProxy) (s.load(Glarch.class, gid)));
        Assert.assertTrue((((g.getAny()) != null) && ((g.getAny()) instanceof FooProxy)));
        s.delete(g.getAny());
        s.delete(g);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testAny() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        One one = new One();
        BarProxy foo = new Bar();
        foo.setObject(one);
        Serializable fid = s.save(foo);
        Serializable oid = one.getKey();
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        List results = s.createQuery("from Bar bar where bar.object.id = ? and bar.object.class = ?").setParameter(0, oid, LONG).setParameter(1, new Character('O'), CHARACTER).list();
        Assert.assertEquals(1, results.size());
        results = s.createQuery("select one from One one, Bar bar where bar.object.id = one.id and bar.object.class = 'O'").list();
        Assert.assertEquals(1, results.size());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        foo = ((BarProxy) (s.load(Foo.class, fid)));
        Assert.assertTrue(((((foo.getObject()) != null) && ((foo.getObject()) instanceof One)) && (s.getIdentifier(foo.getObject()).equals(oid))));
        // s.delete( foo.getObject() );
        s.delete(foo);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testEmbeddedCompositeID() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Location l = new Location();
        l.setCountryCode("AU");
        l.setDescription("foo bar");
        l.setLocale(Locale.getDefault());
        l.setStreetName("Brunswick Rd");
        l.setStreetNumber(300);
        l.setCity("Melbourne");
        s.save(l);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.setFlushMode(MANUAL);
        l = ((Location) (s.createQuery("from Location l where l.countryCode = 'AU' and l.description='foo bar'").list().get(0)));
        Assert.assertTrue(l.getCountryCode().equals("AU"));
        Assert.assertTrue(l.getCity().equals("Melbourne"));
        Assert.assertTrue(l.getLocale().equals(Locale.getDefault()));
        Assert.assertTrue(((s.createCriteria(Location.class).add(Restrictions.eq("streetNumber", new Integer(300))).list().size()) == 1));
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        l.setDescription("sick're");
        s.update(l);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        l = new Location();
        l.setCountryCode("AU");
        l.setDescription("foo bar");
        l.setLocale(Locale.ENGLISH);
        l.setStreetName("Brunswick Rd");
        l.setStreetNumber(300);
        l.setCity("Melbourne");
        Assert.assertTrue((l == (s.load(Location.class, l))));
        Assert.assertTrue(l.getLocale().equals(Locale.getDefault()));
        s.delete(l);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testAutosaveChildren() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        Set bars = new HashSet();
        baz.setCascadingBars(bars);
        s.save(baz);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        baz.getCascadingBars().add(new Bar());
        baz.getCascadingBars().add(new Bar());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getCascadingBars().size()) == 2));
        Assert.assertTrue(((baz.getCascadingBars().iterator().next()) != null));
        baz.getCascadingBars().clear();// test all-delete-orphan;

        s.flush();
        Assert.assertTrue(((s.createQuery("from Bar bar").list().size()) == 0));
        s.delete(baz);
        t.commit();
        s.close();
    }

    @Test
    public void testOrphanDelete() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        Set bars = new HashSet();
        baz.setCascadingBars(bars);
        bars.add(new Bar());
        bars.add(new Bar());
        bars.add(new Bar());
        bars.add(new Bar());
        s.save(baz);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        bars = baz.getCascadingBars();
        Assert.assertEquals(4, bars.size());
        bars.remove(bars.iterator().next());
        Assert.assertEquals(3, s.createQuery("From Bar bar").list().size());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.load(Baz.class, baz.getCode())));
        bars = baz.getCascadingBars();
        Assert.assertEquals(3, bars.size());
        bars.remove(bars.iterator().next());
        s.delete(baz);
        bars.remove(bars.iterator().next());
        Assert.assertEquals(0, s.createQuery("From Bar bar").list().size());
        t.commit();
        s.close();
    }

    @Test
    public void testTransientOrphanDelete() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        Set bars = new HashSet();
        baz.setCascadingBars(bars);
        bars.add(new Bar());
        bars.add(new Bar());
        bars.add(new Bar());
        List foos = new ArrayList();
        foos.add(new Foo());
        foos.add(new Foo());
        baz.setFooBag(foos);
        s.save(baz);
        Iterator i = new JoinedIterator(new Iterator[]{ foos.iterator(), bars.iterator() });
        while (i.hasNext()) {
            FooComponent cmp = ((Foo) (i.next())).getComponent();
            s.delete(cmp.getGlarch());
            cmp.setGlarch(null);
        } 
        t.commit();
        s.close();
        bars.remove(bars.iterator().next());
        foos.remove(1);
        s = openSession();
        t = s.beginTransaction();
        s.update(baz);
        Assert.assertEquals(2, s.createQuery("From Bar bar").list().size());
        Assert.assertEquals(3, s.createQuery("From Foo foo").list().size());
        t.commit();
        s.close();
        foos.remove(0);
        s = openSession();
        t = s.beginTransaction();
        s.update(baz);
        bars.remove(bars.iterator().next());
        Assert.assertEquals(1, s.createQuery("From Foo foo").list().size());
        s.delete(baz);
        // s.flush();
        Assert.assertEquals(0, s.createQuery("From Foo foo").list().size());
        t.commit();
        s.close();
    }

    @Test
    public void testPSCache() throws Exception {
        Session s = openSession();
        Transaction txn = s.beginTransaction();
        for (int i = 0; i < 10; i++)
            s.save(new Foo());

        Query q = s.createQuery("from Foo");
        q.setMaxResults(2);
        q.setFirstResult(5);
        Assert.assertTrue(((q.list().size()) == 2));
        q = s.createQuery("from Foo");
        Assert.assertTrue(((q.list().size()) == 10));
        Assert.assertTrue(((q.list().size()) == 10));
        q.setMaxResults(3);
        q.setFirstResult(3);
        Assert.assertTrue(((q.list().size()) == 3));
        q = s.createQuery("from Foo");
        Assert.assertTrue(((q.list().size()) == 10));
        txn.commit();
        s.close();
        s = openSession();
        txn = s.beginTransaction();
        q = s.createQuery("from Foo");
        Assert.assertTrue(((q.list().size()) == 10));
        q.setMaxResults(5);
        Assert.assertTrue(((q.list().size()) == 5));
        doDelete(s, "from Foo");
        txn.commit();
        s.close();
    }

    @Test
    public void testForCertain() throws Exception {
        Glarch g = new Glarch();
        Glarch g2 = new Glarch();
        List set = new ArrayList();
        set.add("foo");
        g2.setStrings(set);
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Serializable gid = s.save(g);
        Serializable g2id = s.save(g2);
        t.commit();
        Assert.assertTrue(((g.getVersion()) == 0));
        Assert.assertTrue(((g2.getVersion()) == 0));
        s.close();
        s = openSession();
        t = s.beginTransaction();
        g = ((Glarch) (s.get(Glarch.class, gid)));
        g2 = ((Glarch) (s.get(Glarch.class, g2id)));
        Assert.assertTrue(((g2.getStrings().size()) == 1));
        s.delete(g);
        s.delete(g2);
        t.commit();
        s.close();
    }

    @Test
    public void testBagMultipleElements() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        baz.setBag(new ArrayList());
        baz.setByteBag(new ArrayList());
        s.save(baz);
        baz.getBag().add("foo");
        baz.getBag().add("bar");
        baz.getByteBag().add("foo".getBytes());
        baz.getByteBag().add("bar".getBytes());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        // put in cache
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getBag().size()) == 2));
        Assert.assertTrue(((baz.getByteBag().size()) == 2));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getBag().size()) == 2));
        Assert.assertTrue(((baz.getByteBag().size()) == 2));
        baz.getBag().remove("bar");
        baz.getBag().add("foo");
        baz.getByteBag().add("bar".getBytes());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertTrue(((baz.getBag().size()) == 2));
        Assert.assertTrue(((baz.getByteBag().size()) == 3));
        s.delete(baz);
        t.commit();
        s.close();
    }

    @Test
    public void testWierdSession() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Serializable id = s.save(new Foo());
        t.commit();
        s.close();
        s = openSession();
        s.setFlushMode(MANUAL);
        t = s.beginTransaction();
        Foo foo = ((Foo) (s.get(Foo.class, id)));
        t.commit();
        t = s.beginTransaction();
        s.flush();
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        foo = ((Foo) (s.get(Foo.class, id)));
        s.delete(foo);
        t.commit();
        s.close();
    }
}

