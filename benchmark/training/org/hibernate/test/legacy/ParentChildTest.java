/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.legacy;


import DialectChecks.SupportsNoColumnInsert;
import LockMode.NONE;
import LockMode.READ;
import LockMode.UPGRADE;
import LockMode.UPGRADE_NOWAIT;
import LockMode.UPGRADE_SKIPLOCKED;
import ReplicationMode.IGNORE;
import ReplicationMode.OVERWRITE;
import StandardBasicTypes.INTEGER;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.IngresDialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.dialect.TeradataDialect;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.internal.SessionImpl;
import org.hibernate.jdbc.AbstractWork;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.junit.Assert;
import org.junit.Test;


@RequiresDialectFeature(SupportsNoColumnInsert.class)
public class ParentChildTest extends LegacyTestCase {
    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testReplicate() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Container baz = new Container();
        Contained f = new Contained();
        List list = new ArrayList();
        list.add(baz);
        f.setBag(list);
        List list2 = new ArrayList();
        list2.add(f);
        baz.setBag(list2);
        s.save(f);
        s.save(baz);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.replicate(baz, OVERWRITE);
        // HHH-2378
        SessionImpl x = ((SessionImpl) (s));
        EntityEntry entry = x.getPersistenceContext().getEntry(baz);
        Assert.assertNull(entry.getVersion());
        // ~~~~~~~
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.replicate(baz, IGNORE);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.delete(baz);
        s.delete(f);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testQueryOneToOne() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Serializable id = s.save(new Parent());
        Assert.assertTrue(((s.createQuery("from Parent p left join fetch p.child").list().size()) == 1));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Parent p = ((Parent) (s.createQuery("from Parent p left join fetch p.child").uniqueResult()));
        Assert.assertTrue(((p.getChild()) == null));
        s.createQuery("from Parent p join p.child c where c.x > 0").list();
        s.createQuery("from Child c join c.parent p where p.x > 0").list();
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        s.delete(s.get(Parent.class, id));
        t.commit();
        s.close();
    }

    @Test
    @FailureExpected(jiraKey = "unknown")
    public void testProxyReuse() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        FooProxy foo = new Foo();
        FooProxy foo2 = new Foo();
        Serializable id = s.save(foo);
        Serializable id2 = s.save(foo2);
        foo2.setInt(1234567);
        foo.setInt(1234);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        foo = ((FooProxy) (s.load(Foo.class, id)));
        foo2 = ((FooProxy) (s.load(Foo.class, id2)));
        Assert.assertFalse(Hibernate.isInitialized(foo));
        Hibernate.initialize(foo2);
        Hibernate.initialize(foo);
        Assert.assertTrue(((foo.getComponent().getImportantDates().length) == 4));
        Assert.assertTrue(((foo2.getComponent().getImportantDates().length) == 4));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        foo.setKey("xyzid");
        foo.setFloat(new Float(1.2F));
        foo2.setKey(((String) (id)));// intentionally id, not id2!

        foo2.setFloat(new Float(1.3F));
        foo2.getDependent().setKey(null);
        foo2.getComponent().getSubcomponent().getFee().setKey(null);
        Assert.assertFalse(foo2.getKey().equals(id));
        s.save(foo);
        s.update(foo2);
        Assert.assertEquals(foo2.getKey(), id);
        Assert.assertTrue(((foo2.getInt()) == 1234567));
        Assert.assertEquals(foo.getKey(), "xyzid");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        foo = ((FooProxy) (s.load(Foo.class, id)));
        Assert.assertTrue(((foo.getInt()) == 1234567));
        Assert.assertTrue(((foo.getComponent().getImportantDates().length) == 4));
        String feekey = foo.getDependent().getKey();
        String fookey = foo.getKey();
        s.delete(foo);
        s.delete(s.get(Foo.class, id2));
        s.delete(s.get(Foo.class, "xyzid"));
        // here is the issue (HHH-4092).  After the deletes above there are 2 Fees and a Glarch unexpectedly hanging around
        Assert.assertEquals(2, doDelete(s, "from java.lang.Object"));
        t.commit();
        s.close();
        // to account for new id rollback shit
        foo.setKey(fookey);
        foo.getDependent().setKey(feekey);
        foo.getComponent().setGlarch(null);
        foo.getComponent().setSubcomponent(null);
        s = openSession();
        t = s.beginTransaction();
        // foo.getComponent().setGlarch(null); //no id property!
        s.replicate(foo, OVERWRITE);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Foo refoo = ((Foo) (s.get(Foo.class, id)));
        Assert.assertEquals(feekey, refoo.getDependent().getKey());
        s.delete(refoo);
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = H2Dialect.class, comment = "Feature not supported: MVCC=TRUE && FOR UPDATE && JOIN")
    public void testComplexCriteria() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        baz.setDefaults();
        Map topGlarchez = new HashMap();
        baz.setTopGlarchez(topGlarchez);
        Glarch g1 = new Glarch();
        g1.setName("g1");
        s.save(g1);
        Glarch g2 = new Glarch();
        g2.setName("g2");
        s.save(g2);
        g1.setProxyArray(new GlarchProxy[]{ g2 });
        topGlarchez.put(new Character('1'), g1);
        topGlarchez.put(new Character('2'), g2);
        Foo foo1 = new Foo();
        Foo foo2 = new Foo();
        s.save(foo1);
        s.save(foo2);
        baz.getFooSet().add(foo1);
        baz.getFooSet().add(foo2);
        baz.setFooArray(new FooProxy[]{ foo1 });
        LockMode lockMode = (supportsLockingNullableSideOfJoin(getDialect())) ? LockMode.UPGRADE : LockMode.READ;
        Criteria crit = s.createCriteria(Baz.class);
        crit.createCriteria("topGlarchez").add(Restrictions.isNotNull("name")).createCriteria("proxyArray").add(Restrictions.eqProperty("name", "name")).add(Restrictions.eq("name", "g2")).add(Restrictions.gt("x", new Integer((-666))));
        crit.createCriteria("fooSet").add(Restrictions.isNull("null")).add(Restrictions.eq("string", "a string")).add(Restrictions.lt("integer", new Integer((-665))));
        // this is the bit causing the problems; creating the criteria on fooArray does not add it to FROM,
        // and so restriction below leads to an invalid reference.
        crit.createCriteria("fooArray").add(Restrictions.eq("string", "a string")).setLockMode(lockMode);
        List list = crit.list();
        Assert.assertTrue(((list.size()) == 2));
        s.createCriteria(Glarch.class).setLockMode(UPGRADE).list();
        s.createCriteria(Glarch.class).setLockMode(Criteria.ROOT_ALIAS, UPGRADE).list();
        g2.setName(null);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        list = s.createCriteria(Baz.class).add(Restrictions.isEmpty("fooSet")).list();
        Assert.assertEquals(list.size(), 0);
        list = s.createCriteria(Baz.class).add(Restrictions.isNotEmpty("fooSet")).list();
        Assert.assertEquals(new HashSet(list).size(), 1);
        list = s.createCriteria(Baz.class).add(Restrictions.sizeEq("fooSet", 2)).list();
        Assert.assertEquals(new HashSet(list).size(), 1);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        crit = s.createCriteria(Baz.class).setLockMode(lockMode);
        crit.createCriteria("topGlarchez").add(Restrictions.gt("x", new Integer((-666))));
        crit.createCriteria("fooSet").add(Restrictions.isNull("null"));
        list = crit.list();
        Assert.assertTrue(((list.size()) == 4));
        baz = ((Baz) (crit.uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(baz.getTopGlarchez()));// cos it is nonlazy

        Assert.assertTrue((!(Hibernate.isInitialized(baz.getFooSet()))));
        list = s.createCriteria(Baz.class).createCriteria("fooSet").createCriteria("foo").createCriteria("component.glarch").add(Restrictions.eq("name", "xxx")).list();
        Assert.assertTrue(((list.size()) == 0));
        list = s.createCriteria(Baz.class).createAlias("fooSet", "foo").createAlias("foo.foo", "foo2").setLockMode("foo2", lockMode).add(Restrictions.isNull("foo2.component.glarch")).createCriteria("foo2.component.glarch").add(Restrictions.eq("name", "xxx")).list();
        Assert.assertTrue(((list.size()) == 0));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        crit = s.createCriteria(Baz.class);
        crit.createCriteria("topGlarchez").add(Restrictions.isNotNull("name"));
        crit.createCriteria("fooSet").add(Restrictions.isNull("null"));
        list = crit.list();
        Assert.assertTrue(((list.size()) == 2));
        baz = ((Baz) (crit.uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(baz.getTopGlarchez()));// cos it is nonlazy

        Assert.assertTrue((!(Hibernate.isInitialized(baz.getFooSet()))));
        s.createCriteria(Child.class).setFetchMode("parent", FetchMode.JOIN).list();
        doDelete(s, "from Glarch g");
        s.delete(s.get(Foo.class, foo1.getKey()));
        s.delete(s.get(Foo.class, foo2.getKey()));
        s.delete(baz);
        t.commit();
        s.close();
    }

    @Test
    public void testArrayHQL() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        Foo foo1 = new Foo();
        s.save(foo1);
        baz.setFooArray(new FooProxy[]{ foo1 });
        s.flush();
        s.clear();
        baz = ((Baz) (s.createQuery("from Baz b left join fetch b.fooArray").uniqueResult()));
        Assert.assertEquals(1, baz.getFooArray().length);
        t.rollback();
        s.close();
    }

    @Test
    public void testArrayCriteria() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        Foo foo1 = new Foo();
        s.save(foo1);
        baz.setFooArray(new FooProxy[]{ foo1 });
        s.flush();
        s.clear();
        baz = ((Baz) (s.createCriteria(Baz.class).createCriteria("fooArray").uniqueResult()));
        Assert.assertEquals(1, baz.getFooArray().length);
        t.rollback();
        s.close();
    }

    @Test
    public void testLazyManyToOneHQL() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        Foo foo1 = new Foo();
        s.save(foo1);
        baz.setFoo(foo1);
        s.flush();
        s.clear();
        baz = ((Baz) (s.createQuery("from Baz b").uniqueResult()));
        Assert.assertFalse(Hibernate.isInitialized(baz.getFoo()));
        Assert.assertTrue(((baz.getFoo()) instanceof HibernateProxy));
        t.rollback();
        s.close();
    }

    @Test
    public void testLazyManyToOneCriteria() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        Foo foo1 = new Foo();
        s.save(foo1);
        baz.setFoo(foo1);
        s.flush();
        s.clear();
        baz = ((Baz) (s.createCriteria(Baz.class).uniqueResult()));
        Assert.assertTrue(Hibernate.isInitialized(baz.getFoo()));
        Assert.assertFalse(((baz.getFoo()) instanceof HibernateProxy));
        t.rollback();
        s.close();
    }

    @Test
    public void testLazyManyToOneGet() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        s.save(baz);
        Foo foo1 = new Foo();
        s.save(foo1);
        baz.setFoo(foo1);
        s.flush();
        s.clear();
        baz = ((Baz) (s.get(Baz.class, baz.getCode())));
        Assert.assertTrue(Hibernate.isInitialized(baz.getFoo()));
        Assert.assertFalse(((baz.getFoo()) instanceof HibernateProxy));
        t.rollback();
        s.close();
    }

    @Test
    public void testClassWhere() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        baz.setParts(new ArrayList());
        Part p1 = new Part();
        p1.setDescription("xyz");
        Part p2 = new Part();
        p2.setDescription("abc");
        baz.getParts().add(p1);
        baz.getParts().add(p2);
        s.save(baz);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertTrue(((s.createCriteria(Part.class).list().size()) == 1));// there is a where condition on Part mapping

        Assert.assertTrue(((s.createCriteria(Part.class).add(Restrictions.eq("id", p1.getId())).list().size()) == 1));
        Assert.assertTrue(((s.createQuery("from Part").list().size()) == 1));
        // only Part entities that satisfy the where condition on Part mapping should be included in the collection
        Assert.assertTrue(((s.createQuery("from Baz baz join baz.parts").list().size()) == 1));
        baz = ((Baz) (s.createCriteria(Baz.class).uniqueResult()));
        // only Part entities that satisfy the where condition on Part mapping should be included in the collection
        Assert.assertTrue(((s.createFilter(baz.getParts(), "").list().size()) == 1));
        // assertTrue( baz.getParts().size()==1 );
        s.delete(s.get(Part.class, p1.getId()));
        // p2.description does not satisfy the condition on Part mapping, so it's not possible to retrieve it
        // using Session#get; instead just delete using a native query
        s.createNativeQuery("delete from Part where id = :id").setParameter("id", p2.getId()).executeUpdate();
        s.delete(baz);
        t.commit();
        s.close();
    }

    @Test
    public void testClassWhereManyToMany() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Baz baz = new Baz();
        baz.setMoreParts(new ArrayList());
        Part p1 = new Part();
        p1.setDescription("xyz");
        Part p2 = new Part();
        p2.setDescription("abc");
        baz.getMoreParts().add(p1);
        baz.getMoreParts().add(p2);
        s.save(baz);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Assert.assertTrue(((s.createCriteria(Part.class).list().size()) == 1));// there is a where condition on Part mapping

        Assert.assertTrue(((s.createCriteria(Part.class).add(Restrictions.eq("id", p1.getId())).list().size()) == 1));
        Assert.assertTrue(((s.createQuery("from Part").list().size()) == 1));
        // only Part entities that satisfy the where condition on Part mapping should be included in the collection
        Assert.assertTrue(((s.createQuery("from Baz baz join baz.moreParts").list().size()) == 1));
        baz = ((Baz) (s.createCriteria(Baz.class).uniqueResult()));
        // only Part entities that satisfy the where condition on Part mapping should be included in the collection
        Assert.assertTrue(((s.createFilter(baz.getMoreParts(), "").list().size()) == 1));
        // assertTrue( baz.getParts().size()==1 );
        s.delete(s.get(Part.class, p1.getId()));
        // p2.description does not satisfy the condition on Part mapping, so it's not possible to retrieve it
        // using Session#get; instead just delete using a native query
        s.createNativeQuery("delete from baz_moreParts where baz = :baz AND part = :part").setParameter("baz", baz.getCode()).setParameter("part", p2.getId()).executeUpdate();
        s.createNativeQuery("delete from Part where id = :id").setParameter("id", p2.getId()).executeUpdate();
        s.delete(baz);
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testCollectionQuery() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Simple s1 = new Simple(Long.valueOf(1));
        s1.setName("s");
        s1.setCount(0);
        Simple s2 = new Simple(Long.valueOf(2));
        s2.setCount(2);
        Simple s3 = new Simple(Long.valueOf(3));
        s3.setCount(3);
        s.save(s1);
        s.save(s2);
        s.save(s3);
        Container c = new Container();
        Contained cd = new Contained();
        List bag = new ArrayList();
        bag.add(cd);
        c.setBag(bag);
        List l = new ArrayList();
        l.add(s1);
        l.add(s3);
        l.add(s2);
        c.setOneToMany(l);
        l = new ArrayList();
        l.add(s1);
        l.add(null);
        l.add(s2);
        c.setManyToMany(l);
        s.save(c);
        Container cx = new Container();
        s.save(cx);
        Simple sx = new Simple(Long.valueOf(5));
        sx.setCount(5);
        sx.setName("s");
        s.save(sx);
        Assert.assertTrue(((s.createQuery("select c from ContainerX c, Simple s where c.oneToMany[2] = s").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c, Simple s where c.manyToMany[2] = s").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c, Simple s where s = c.oneToMany[2]").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c, Simple s where s = c.manyToMany[2]").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c where c.oneToMany[0].name = 's'").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c where c.manyToMany[0].name = 's'").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c where 's' = c.oneToMany[2 - 2].name").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c where 's' = c.manyToMany[(3+1)/4-1].name").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c where c.oneToMany[ c.manyToMany[0].count ].name = 's'").list().size()) == 1));
        Assert.assertTrue(((s.createQuery("select c from ContainerX c where c.manyToMany[ c.oneToMany[0].count ].name = 's'").list().size()) == 1));
        if ((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof org.hibernate.dialect.TimesTenDialect))) {
            Assert.assertTrue(((s.createQuery("select c from ContainerX c where c.manyToMany[ maxindex(c.manyToMany) ].count = 2").list().size()) == 1));
        }
        Assert.assertTrue(s.contains(cd));
        if ((!((getDialect()) instanceof MySQLDialect)) && (!((getDialect()) instanceof HSQLDialect))) {
            s.createFilter(c.getBag(), "where 0 in elements(this.bag)").list();
            s.createFilter(c.getBag(), "where 0 in elements(this.lazyBag)").list();
        }
        s.createQuery("select count(comp.name) from ContainerX c join c.components comp").list();
        s.delete(cd);
        s.delete(c);
        s.delete(s1);
        s.delete(s2);
        s.delete(s3);
        s.delete(cx);
        s.delete(sx);
        t.commit();
        s.close();
    }

    @Test
    public void testParentChild() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent();
        Child c = new Child();
        c.setParent(p);
        p.setChild(c);
        s.save(p);
        s.save(c);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Child) (s.load(Child.class, new Long(c.getId()))));
        p = c.getParent();
        Assert.assertTrue("1-1 parent", (p != null));
        c.setCount(32);
        p.setCount(66);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Child) (s.load(Child.class, new Long(c.getId()))));
        p = c.getParent();
        Assert.assertTrue("1-1 update", ((p.getCount()) == 66));
        Assert.assertTrue("1-1 update", ((c.getCount()) == 32));
        Assert.assertTrue("1-1 query", ((s.createQuery("from Child c where c.parent.count=66").list().size()) == 1));
        Assert.assertTrue("1-1 query", ((((Object[]) (s.createQuery("from Parent p join p.child c where p.count=66").list().get(0))).length) == 2));
        s.createQuery("select c, c.parent from Child c order by c.parent.count").list();
        s.createQuery("select c, c.parent from Child c where c.parent.count=66 order by c.parent.count").list();
        s.createQuery("select c, c.parent, c.parent.count from Child c order by c.parent.count").iterate();
        List result = s.createQuery("FROM Parent AS p WHERE p.count = ?").setParameter(0, new Integer(66), INTEGER).list();
        Assert.assertEquals("1-1 query", 1, result.size());
        s.delete(c);
        s.delete(p);
        t.commit();
        s.close();
    }

    @Test
    public void testParentNullChild() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Parent p = new Parent();
        s.save(p);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Parent) (s.load(Parent.class, new Long(p.getId()))));
        Assert.assertTrue(((p.getChild()) == null));
        p.setCount(66);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        p = ((Parent) (s.load(Parent.class, new Long(p.getId()))));
        Assert.assertTrue("null 1-1 update", ((p.getCount()) == 66));
        Assert.assertTrue(((p.getChild()) == null));
        s.delete(p);
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testManyToMany() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Container c = new Container();
        c.setManyToMany(new ArrayList());
        c.setBag(new ArrayList());
        Simple s1 = new Simple(Long.valueOf(12));
        Simple s2 = new Simple(Long.valueOf((-1)));
        s1.setCount(123);
        s2.setCount(654);
        Contained c1 = new Contained();
        c1.setBag(new ArrayList());
        c1.getBag().add(c);
        c.getBag().add(c1);
        c.getManyToMany().add(s1);
        c.getManyToMany().add(s2);
        Serializable cid = s.save(c);
        s.save(s1);
        s.save(s2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.load(Container.class, cid)));
        Assert.assertTrue(((c.getBag().size()) == 1));
        Assert.assertTrue(((c.getManyToMany().size()) == 2));
        c1 = ((Contained) (c.getBag().iterator().next()));
        Assert.assertTrue(((c.getBag().size()) == 1));
        c.getBag().remove(c1);
        c1.getBag().remove(c);
        Assert.assertTrue(((c.getManyToMany().remove(0)) != null));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.load(Container.class, cid)));
        Assert.assertTrue(((c.getBag().size()) == 0));
        Assert.assertTrue(((c.getManyToMany().size()) == 1));
        c1 = ((Contained) (s.load(Contained.class, new Long(c1.getId()))));
        Assert.assertTrue(((c1.getBag().size()) == 0));
        Assert.assertEquals(1, doDelete(s, "from ContainerX c"));
        Assert.assertEquals(1, doDelete(s, "from Contained"));
        Assert.assertEquals(2, doDelete(s, "from Simple"));
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testContainer() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Container c = new Container();
        Simple x = new Simple(Long.valueOf(1));
        x.setCount(123);
        Simple y = new Simple(Long.valueOf(0));
        y.setCount(456);
        s.save(x);
        s.save(y);
        List o2m = new ArrayList();
        o2m.add(x);
        o2m.add(null);
        o2m.add(y);
        List m2m = new ArrayList();
        m2m.add(x);
        m2m.add(null);
        m2m.add(y);
        c.setOneToMany(o2m);
        c.setManyToMany(m2m);
        List comps = new ArrayList();
        Container.ContainerInnerClass ccic = new Container.ContainerInnerClass();
        ccic.setName("foo");
        ccic.setSimple(x);
        comps.add(ccic);
        comps.add(null);
        ccic = new Container.ContainerInnerClass();
        ccic.setName("bar");
        ccic.setSimple(y);
        comps.add(ccic);
        HashSet compos = new HashSet();
        compos.add(ccic);
        c.setComposites(compos);
        c.setComponents(comps);
        One one = new One();
        Many many = new Many();
        HashSet manies = new HashSet();
        manies.add(many);
        one.setManies(manies);
        many.setOne(one);
        ccic.setMany(many);
        ccic.setOne(one);
        s.save(one);
        s.save(many);
        s.save(c);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Long count = ((Long) (s.createQuery("select count(*) from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'").uniqueResult()));
        Assert.assertTrue(((count.intValue()) == 1));
        List res = s.createQuery("select c, s from ContainerX as c join c.components as ce join ce.simple as s where ce.name='foo'").list();
        Assert.assertTrue(((res.size()) == 1));
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.load(Container.class, new Long(c.getId()))));
        System.out.println(c.getOneToMany());
        System.out.println(c.getManyToMany());
        System.out.println(c.getComponents());
        System.out.println(c.getComposites());
        ccic = ((Container.ContainerInnerClass) (c.getComponents().get(2)));
        Assert.assertTrue(((ccic.getMany().getOne()) == (ccic.getOne())));
        Assert.assertTrue(((c.getComponents().size()) == 3));
        Assert.assertTrue(((c.getComposites().size()) == 1));
        Assert.assertTrue(((c.getOneToMany().size()) == 3));
        Assert.assertTrue(((c.getManyToMany().size()) == 3));
        Assert.assertTrue(((c.getOneToMany().get(0)) != null));
        Assert.assertTrue(((c.getOneToMany().get(2)) != null));
        for (int i = 0; i < 3; i++) {
            Assert.assertTrue(((c.getManyToMany().get(i)) == (c.getOneToMany().get(i))));
        }
        Object o1 = c.getOneToMany().get(0);
        Object o2 = c.getOneToMany().remove(2);
        c.getOneToMany().set(0, o2);
        c.getOneToMany().set(1, o1);
        o1 = c.getComponents().remove(2);
        c.getComponents().set(0, o1);
        c.getManyToMany().set(0, c.getManyToMany().get(2));
        Container.ContainerInnerClass ccic2 = new Container.ContainerInnerClass();
        ccic2.setName("foo");
        ccic2.setOne(one);
        ccic2.setMany(many);
        ccic2.setSimple(((Simple) (s.load(Simple.class, new Long(0)))));
        c.getComposites().add(ccic2);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.load(Container.class, new Long(c.getId()))));
        System.out.println(c.getOneToMany());
        System.out.println(c.getManyToMany());
        System.out.println(c.getComponents());
        System.out.println(c.getComposites());
        Assert.assertTrue(((c.getComponents().size()) == 1));// WAS: 2

        Assert.assertTrue(((c.getComposites().size()) == 2));
        Assert.assertTrue(((c.getOneToMany().size()) == 2));
        Assert.assertTrue(((c.getManyToMany().size()) == 3));
        Assert.assertTrue(((c.getOneToMany().get(0)) != null));
        Assert.assertTrue(((c.getOneToMany().get(1)) != null));
        ((Container.ContainerInnerClass) (c.getComponents().get(0))).setName("a different name");
        ((Container.ContainerInnerClass) (c.getComposites().iterator().next())).setName("once again");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.load(Container.class, new Long(c.getId()))));
        System.out.println(c.getOneToMany());
        System.out.println(c.getManyToMany());
        System.out.println(c.getComponents());
        System.out.println(c.getComposites());
        Assert.assertTrue(((c.getComponents().size()) == 1));// WAS: 2

        Assert.assertTrue(((c.getComposites().size()) == 2));
        Assert.assertTrue(((Container.ContainerInnerClass) (c.getComponents().get(0))).getName().equals("a different name"));
        Iterator iter = c.getComposites().iterator();
        boolean found = false;
        while (iter.hasNext()) {
            if (((Container.ContainerInnerClass) (iter.next())).getName().equals("once again"))
                found = true;

        } 
        Assert.assertTrue(found);
        c.getOneToMany().clear();
        c.getManyToMany().clear();
        c.getComposites().clear();
        c.getComponents().clear();
        doDelete(s, "from Simple");
        doDelete(s, "from Many");
        doDelete(s, "from One");
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.load(Container.class, new Long(c.getId()))));
        Assert.assertTrue(((c.getComponents().size()) == 0));
        Assert.assertTrue(((c.getComposites().size()) == 0));
        Assert.assertTrue(((c.getOneToMany().size()) == 0));
        Assert.assertTrue(((c.getManyToMany().size()) == 0));
        s.delete(c);
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testCascadeCompositeElements() throws Exception {
        Container c = new Container();
        List list = new ArrayList();
        c.setCascades(list);
        Container.ContainerInnerClass cic = new Container.ContainerInnerClass();
        cic.setMany(new Many());
        cic.setOne(new One());
        list.add(cic);
        Session s = openSession();
        s.beginTransaction();
        s.save(c);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        c = ((Container) (s.createQuery("from ContainerX c").iterate().next()));
        cic = ((Container.ContainerInnerClass) (c.getCascades().iterator().next()));
        Assert.assertTrue((((cic.getMany()) != null) && ((cic.getOne()) != null)));
        Assert.assertTrue(((c.getCascades().size()) == 1));
        s.delete(c);
        s.getTransaction().commit();
        s.close();
        c = new Container();
        s = openSession();
        s.beginTransaction();
        s.save(c);
        list = new ArrayList();
        c.setCascades(list);
        cic = new Container.ContainerInnerClass();
        cic.setMany(new Many());
        cic.setOne(new One());
        list.add(cic);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        c = ((Container) (s.createQuery("from ContainerX c").iterate().next()));
        cic = ((Container.ContainerInnerClass) (c.getCascades().iterator().next()));
        Assert.assertTrue((((cic.getMany()) != null) && ((cic.getOne()) != null)));
        Assert.assertTrue(((c.getCascades().size()) == 1));
        s.delete(c);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = " HANA doesn't support tables consisting of only a single auto-generated column")
    public void testBag() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Container c = new Container();
        Contained c1 = new Contained();
        Contained c2 = new Contained();
        c.setBag(new ArrayList());
        c.getBag().add(c1);
        c.getBag().add(c2);
        c1.getBag().add(c);
        c2.getBag().add(c);
        s.save(c);
        c.getBag().add(c2);
        c2.getBag().add(c);
        c.getLazyBag().add(c1);
        c1.getLazyBag().add(c);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.createQuery("from ContainerX c").list().get(0)));
        c.getLazyBag().size();
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.createQuery("from ContainerX c").list().get(0)));
        Contained c3 = new Contained();
        // c.getBag().add(c3);
        // c3.getBag().add(c);
        c.getLazyBag().add(c3);
        c3.getLazyBag().add(c);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.createQuery("from ContainerX c").list().get(0)));
        Contained c4 = new Contained();
        c.getLazyBag().add(c4);
        c4.getLazyBag().add(c);
        Assert.assertTrue(((c.getLazyBag().size()) == 3));// forces initialization

        // s.save(c4);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        c = ((Container) (s.createQuery("from ContainerX c").list().get(0)));
        Iterator i = c.getBag().iterator();
        int j = 0;
        while (i.hasNext()) {
            Assert.assertTrue(((i.next()) != null));
            j++;
        } 
        Assert.assertTrue((j == 3));
        Assert.assertTrue(((c.getLazyBag().size()) == 3));
        s.delete(c);
        c.getBag().remove(c2);
        Iterator iter = c.getBag().iterator();
        j = 0;
        while (iter.hasNext()) {
            j++;
            s.delete(iter.next());
        } 
        Assert.assertTrue((j == 2));
        s.delete(s.load(Contained.class, new Long(c4.getId())));
        s.delete(s.load(Contained.class, new Long(c3.getId())));
        t.commit();
        s.close();
    }

    @Test
    public void testCircularCascade() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Circular c = new Circular();
        c.setClazz(Circular.class);
        c.setOther(new Circular());
        c.getOther().setOther(new Circular());
        c.getOther().getOther().setOther(c);
        c.setAnyEntity(c.getOther());
        String id = ((String) (s.save(c)));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        c = ((Circular) (s.load(Circular.class, id)));
        c.getOther().getOther().setClazz(Foo.class);
        tx.commit();
        s.close();
        c.getOther().setClazz(Qux.class);
        s = openSession();
        tx = s.beginTransaction();
        s.saveOrUpdate(c);
        tx.commit();
        s.close();
        c.getOther().getOther().setClazz(Bar.class);
        s = openSession();
        tx = s.beginTransaction();
        s.saveOrUpdate(c);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        c = ((Circular) (s.load(Circular.class, id)));
        Assert.assertTrue(((c.getOther().getOther().getClazz()) == (Bar.class)));
        Assert.assertTrue(((c.getOther().getClazz()) == (Qux.class)));
        Assert.assertTrue(((c.getOther().getOther().getOther()) == c));
        Assert.assertTrue(((c.getAnyEntity()) == (c.getOther())));
        Assert.assertEquals(3, doDelete(s, "from Universe"));
        tx.commit();
        s.close();
    }

    @Test
    public void testDeleteEmpty() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Assert.assertEquals(0, doDelete(s, "from Simple"));
        Assert.assertEquals(0, doDelete(s, "from Universe"));
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLocking() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Simple s1 = new Simple(Long.valueOf(1));
        s1.setCount(1);
        Simple s2 = new Simple(Long.valueOf(2));
        s2.setCount(2);
        Simple s3 = new Simple(Long.valueOf(3));
        s3.setCount(3);
        Simple s4 = new Simple(Long.valueOf(4));
        s4.setCount(4);
        Simple s5 = new Simple(Long.valueOf(5));
        s5.setCount(5);
        s.save(s1);
        s.save(s2);
        s.save(s3);
        s.save(s4);
        s.save(s5);
        Assert.assertTrue(((s.getCurrentLockMode(s1)) == (LockMode.WRITE)));
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        s1 = ((Simple) (s.load(Simple.class, new Long(1), NONE)));
        Assert.assertTrue((((s.getCurrentLockMode(s1)) == (LockMode.READ)) || ((s.getCurrentLockMode(s1)) == (LockMode.NONE))));// depends if cache is enabled

        s2 = ((Simple) (s.load(Simple.class, new Long(2), READ)));
        Assert.assertTrue(((s.getCurrentLockMode(s2)) == (LockMode.READ)));
        s3 = ((Simple) (s.load(Simple.class, new Long(3), UPGRADE)));
        Assert.assertTrue(((s.getCurrentLockMode(s3)) == (LockMode.UPGRADE)));
        s4 = ((Simple) (s.byId(Simple.class).with(new org.hibernate.LockOptions(LockMode.UPGRADE_NOWAIT)).load(4L)));
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.UPGRADE_NOWAIT)));
        s5 = ((Simple) (s.byId(Simple.class).with(new org.hibernate.LockOptions(LockMode.UPGRADE_SKIPLOCKED)).load(5L)));
        Assert.assertTrue(((s.getCurrentLockMode(s5)) == (LockMode.UPGRADE_SKIPLOCKED)));
        s1 = ((Simple) (s.load(Simple.class, new Long(1), UPGRADE)));// upgrade

        Assert.assertTrue(((s.getCurrentLockMode(s1)) == (LockMode.UPGRADE)));
        s2 = ((Simple) (s.load(Simple.class, new Long(2), NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s2)) == (LockMode.READ)));
        s3 = ((Simple) (s.load(Simple.class, new Long(3), READ)));
        Assert.assertTrue(((s.getCurrentLockMode(s3)) == (LockMode.UPGRADE)));
        s4 = ((Simple) (s.load(Simple.class, new Long(4), UPGRADE)));
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.UPGRADE_NOWAIT)));
        s5 = ((Simple) (s.load(Simple.class, new Long(5), UPGRADE)));
        Assert.assertTrue(((s.getCurrentLockMode(s5)) == (LockMode.UPGRADE_SKIPLOCKED)));
        s.lock(s2, UPGRADE);// upgrade

        Assert.assertTrue(((s.getCurrentLockMode(s2)) == (LockMode.UPGRADE)));
        s.lock(s3, UPGRADE);
        Assert.assertTrue(((s.getCurrentLockMode(s3)) == (LockMode.UPGRADE)));
        s.lock(s1, UPGRADE_NOWAIT);
        s.lock(s4, NONE);
        s.lock(s5, UPGRADE_SKIPLOCKED);
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.UPGRADE_NOWAIT)));
        Assert.assertTrue(((s.getCurrentLockMode(s5)) == (LockMode.UPGRADE_SKIPLOCKED)));
        tx.commit();
        tx = s.beginTransaction();
        Assert.assertTrue(((s.getCurrentLockMode(s3)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s1)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s2)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s5)) == (LockMode.NONE)));
        s.lock(s1, READ);// upgrade

        Assert.assertTrue(((s.getCurrentLockMode(s1)) == (LockMode.READ)));
        s.lock(s2, UPGRADE);// upgrade

        Assert.assertTrue(((s.getCurrentLockMode(s2)) == (LockMode.UPGRADE)));
        s.lock(s3, UPGRADE_NOWAIT);// upgrade

        Assert.assertTrue(((s.getCurrentLockMode(s3)) == (LockMode.UPGRADE_NOWAIT)));
        s.lock(s4, NONE);
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.NONE)));
        s4.setName("s4");
        s.flush();
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.WRITE)));
        tx.commit();
        tx = s.beginTransaction();
        Assert.assertTrue(((s.getCurrentLockMode(s3)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s1)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s2)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s4)) == (LockMode.NONE)));
        Assert.assertTrue(((s.getCurrentLockMode(s5)) == (LockMode.NONE)));
        s.delete(s1);
        s.delete(s2);
        s.delete(s3);
        s.delete(s4);
        s.delete(s5);
        tx.commit();
        s.close();
    }

    @Test
    public void testObjectType() throws Exception {
        Session s = openSession();
        s.beginTransaction();
        Parent g = new Parent();
        Foo foo = new Foo();
        g.setAny(foo);
        s.save(g);
        s.save(foo);
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        g = ((Parent) (s.load(Parent.class, new Long(g.getId()))));
        Assert.assertTrue((((g.getAny()) != null) && ((g.getAny()) instanceof FooProxy)));
        s.delete(g.getAny());
        s.delete(g);
        s.getTransaction().commit();
        s.close();
    }

    @Test
    public void testLoadAfterNonExists() throws SQLException, HibernateException {
        Session session = openSession();
        if (((getDialect()) instanceof MySQLDialect) || ((getDialect()) instanceof IngresDialect)) {
            session.doWork(new AbstractWork() {
                @Override
                public void execute(Connection connection) throws SQLException {
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                }
            });
        }
        session.getTransaction().begin();
        // First, prime the fixture session to think the entity does not exist
        try {
            session.load(Simple.class, new Long((-1)));
            Assert.fail();
        } catch (ObjectNotFoundException onfe) {
            if ((getDialect()) instanceof TeradataDialect) {
                session.getTransaction().rollback();
                session.getTransaction().begin();
            }
            // this is correct
        }
        // Next, lets create that entity "under the covers"
        Session anotherSession = openSession();
        anotherSession.beginTransaction();
        Simple myNewSimple = new Simple(Long.valueOf((-1)));
        myNewSimple.setName("My under the radar Simple entity");
        myNewSimple.setAddress("SessionCacheTest.testLoadAfterNonExists");
        myNewSimple.setCount(1);
        myNewSimple.setDate(new Date());
        myNewSimple.setPay(Float.valueOf(100000000));
        anotherSession.save(myNewSimple);
        anotherSession.getTransaction().commit();
        anotherSession.close();
        // Now, lets make sure the original session can see the created row...
        session.clear();
        try {
            Simple dummy = ((Simple) (session.get(Simple.class, Long.valueOf((-1)))));
            Assert.assertNotNull("Unable to locate entity Simple with id = -1", dummy);
            session.delete(dummy);
        } catch (ObjectNotFoundException onfe) {
            Assert.fail("Unable to locate entity Simple with id = -1");
        }
        session.getTransaction().commit();
        session.close();
    }
}

