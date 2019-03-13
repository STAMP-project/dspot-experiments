/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.cid;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * test some composite id functionalities
 *
 * @author Emmanuel Bernard
 */
public class CompositeIdTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testOneToOneInCompositePk() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        B b = new B();
        C c = new C();
        s.persist(b);
        s.persist(c);
        A a = new A();
        a.setAId(new AId());
        a.getAId().setB(b);
        a.getAId().setC(c);
        s.persist(a);
        s.flush();
        s.clear();
        a = ((A) (s.get(A.class, a.getAId())));
        Assert.assertEquals(b.getId(), a.getAId().getB().getId());
        tx.rollback();
        s.close();
    }

    /**
     * This feature is not supported by the EJB3
     * this is an hibernate extension
     */
    @Test
    public void testManyToOneInCompositePk() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        ParentPk ppk = new ParentPk();
        ppk.setFirstName("Emmanuel");
        ppk.setLastName("Bernard");
        Parent p = new Parent();
        p.id = ppk;
        s.persist(p);
        ChildPk cpk = new ChildPk();
        cpk.parent = p;
        cpk.nthChild = 1;
        Child c = new Child();
        c.id = cpk;
        s.persist(c);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Query q = s.createQuery("select c from Child c where c.id.nthChild = :nth");
        q.setInteger("nth", 1);
        List results = q.list();
        Assert.assertEquals(1, results.size());
        c = ((Child) (results.get(0)));
        Assert.assertNotNull(c);
        Assert.assertNotNull(c.id.parent);
        // FIXME mke it work in unambigious cases
        // assertNotNull(c.id.parent.id);
        // assertEquals(p.id.getFirstName(), c.id.parent.id.getFirstName());
        s.delete(c);
        s.delete(c.id.parent);
        tx.commit();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10476")
    public void testManyToOneInCompositePkInPC() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        ParentPk ppk = new ParentPk();
        ppk.setFirstName("Emmanuel");
        ppk.setLastName("Bernard");
        Parent p = new Parent();
        p.id = ppk;
        s.persist(p);
        ChildPk cpk = new ChildPk();
        cpk.parent = p;
        cpk.nthChild = 1;
        Child c = new Child();
        c.id = cpk;
        s.persist(c);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        p = ((Parent) (s.get(Parent.class, ppk)));
        // p.id should be ppk.
        Assert.assertSame(ppk, p.id);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        c = ((Child) (s.get(Child.class, cpk)));
        // c.id should be cpk
        Assert.assertSame(cpk, c.id);
        // only Child should be in PC (c.id.parent should not be in PC)
        SessionImplementor sessionImplementor = ((SessionImplementor) (s));
        Assert.assertTrue(sessionImplementor.getPersistenceContext().isEntryFor(c));
        Assert.assertFalse(sessionImplementor.getPersistenceContext().isEntryFor(c.id.parent));
        tx.commit();
        s.close();
    }

    /**
     * This feature is not supported by the EJB3
     * this is an hibernate extension
     */
    @Test
    public void testManyToOneInCompositePkAndSubclass() throws Exception {
        Session s;
        Transaction tx;
        s = openSession();
        tx = s.beginTransaction();
        ParentPk ppk = new ParentPk();
        ppk.setFirstName("Emmanuel");
        ppk.setLastName("Bernard");
        Parent p = new Parent();
        p.id = ppk;
        s.persist(p);
        ChildPk cpk = new ChildPk();
        cpk.parent = p;
        cpk.nthChild = 1;
        LittleGenius c = new LittleGenius();
        c.particularSkill = "Human Annotation parser";
        c.id = cpk;
        s.persist(c);
        tx.commit();
        s.close();
        s = openSession();
        tx = s.beginTransaction();
        Query q = s.createQuery("select c from Child c where c.id.nthChild = :nth");
        q.setInteger("nth", 1);
        List results = q.list();
        Assert.assertEquals(1, results.size());
        c = ((LittleGenius) (results.get(0)));
        Assert.assertNotNull(c);
        Assert.assertNotNull(c.id.parent);
        // FIXME mke it work in unambigious cases
        // assertNotNull(c.id.parent.id);
        // assertEquals(p.id.getFirstName(), c.id.parent.id.getFirstName());
        s.delete(c);
        s.delete(c.id.parent);
        tx.commit();
        s.close();
    }

    @Test
    public void testManyToOneInCompositeId() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Channel channel = new Channel();
        s.persist(channel);
        Presenter pres = new Presenter();
        pres.name = "Casimir";
        s.persist(pres);
        TvMagazinPk pk = new TvMagazinPk();
        TvMagazin mag = new TvMagazin();
        mag.time = new Date();
        mag.id = pk;
        pk.channel = channel;
        pk.presenter = pres;
        s.persist(mag);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        mag = ((TvMagazin) (s.createQuery("from TvMagazin mag").uniqueResult()));
        Assert.assertNotNull(mag.id);
        Assert.assertNotNull(mag.id.channel);
        Assert.assertEquals(channel.id, mag.id.channel.id);
        Assert.assertNotNull(mag.id.presenter);
        Assert.assertEquals(pres.name, mag.id.presenter.name);
        s.delete(mag);
        s.delete(mag.id.channel);
        s.delete(mag.id.presenter);
        tx.commit();
        s.close();
    }

    @Test
    public void testManyToOneInCompositeIdClass() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Order order = new Order();
        s.persist(order);
        Product product = new Product();
        product.name = "small car";
        s.persist(product);
        OrderLine orderLine = new OrderLine();
        orderLine.order = order;
        orderLine.product = product;
        s.persist(orderLine);
        s.flush();
        s.clear();
        orderLine = ((OrderLine) (s.createQuery("select ol from OrderLine ol").uniqueResult()));
        Assert.assertNotNull(orderLine.order);
        Assert.assertEquals(order.id, orderLine.order.id);
        Assert.assertNotNull(orderLine.product);
        Assert.assertEquals(product.name, orderLine.product.name);
        tx.rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10476")
    public void testManyToOneInCompositeIdClassInPC() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Order order = new Order();
        s.persist(order);
        Product product = new Product();
        product.name = "small car";
        s.persist(product);
        OrderLine orderLine = new OrderLine();
        orderLine.order = order;
        orderLine.product = product;
        s.persist(orderLine);
        s.flush();
        s.clear();
        s.clear();
        OrderLinePk orderLinePK = new OrderLinePk();
        orderLinePK.order = orderLine.order;
        orderLinePK.product = orderLine.product;
        orderLine = ((OrderLine) (s.get(OrderLine.class, orderLinePK)));
        Assert.assertTrue(((orderLine.order) != (orderLinePK.order)));
        Assert.assertTrue(((orderLine.product) != (orderLinePK.product)));
        SessionImplementor sessionImplementor = ((SessionImplementor) (s));
        Assert.assertTrue(sessionImplementor.getPersistenceContext().isEntryFor(orderLine));
        Assert.assertTrue(sessionImplementor.getPersistenceContext().isEntryFor(orderLine.order));
        Assert.assertTrue(sessionImplementor.getPersistenceContext().isEntryFor(orderLine.product));
        Assert.assertFalse(sessionImplementor.getPersistenceContext().isEntryFor(orderLinePK.order));
        Assert.assertFalse(sessionImplementor.getPersistenceContext().isEntryFor(orderLinePK.product));
        tx.rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10476")
    public void testGetWithUpdatedDetachedEntityInCompositeID() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Channel channel = new Channel();
        Presenter presenter = new Presenter();
        presenter.name = "Jane";
        TvMagazin tvMagazin = new TvMagazin();
        tvMagazin.id = new TvMagazinPk();
        tvMagazin.id.channel = channel;
        tvMagazin.id.presenter = presenter;
        s.persist(channel);
        s.persist(presenter);
        s.persist(tvMagazin);
        s.flush();
        s.clear();
        // update channel
        channel.name = "chnl";
        TvMagazinPk pkNew = new TvMagazinPk();
        // set pkNew.channel to the unmerged copy.
        pkNew.channel = channel;
        pkNew.presenter = presenter;
        // the following fails because there is already a managed channel
        tvMagazin = s.get(TvMagazin.class, pkNew);
        channel = s.get(Channel.class, channel.id);
        Assert.assertNull(channel.name);
        s.flush();
        s.clear();
        // make sure that channel.name is still null
        channel = s.get(Channel.class, channel.id);
        Assert.assertNull(channel.name);
        tx.rollback();
        s.close();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10476")
    public void testGetWithDetachedEntityInCompositeIDWithManagedCopy() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Channel channel = new Channel();
        Presenter presenter = new Presenter();
        presenter.name = "Jane";
        TvMagazin tvMagazin = new TvMagazin();
        tvMagazin.id = new TvMagazinPk();
        tvMagazin.id.channel = channel;
        tvMagazin.id.presenter = presenter;
        s.persist(channel);
        s.persist(presenter);
        s.persist(tvMagazin);
        s.flush();
        s.clear();
        // merge channel to put channel back in PersistenceContext
        s.merge(channel);
        TvMagazinPk pkNew = new TvMagazinPk();
        // set pkNew.channel to the unmerged copy.
        pkNew.channel = channel;
        pkNew.presenter = presenter;
        // the following fails because there is already a managed channel
        tvMagazin = s.get(TvMagazin.class, pkNew);
        tx.rollback();
        s.close();
    }

    @Test
    public void testSecondaryTableWithCompositeId() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Channel channel = new Channel();
        s.persist(channel);
        Presenter pres = new Presenter();
        pres.name = "Tim Russet";
        s.persist(pres);
        TvMagazinPk pk = new TvMagazinPk();
        TvProgram program = new TvProgram();
        program.time = new Date();
        program.id = pk;
        program.text = "Award Winning Programming";
        pk.channel = channel;
        pk.presenter = pres;
        s.persist(program);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        program = ((TvProgram) (s.createQuery("from TvProgram pr").uniqueResult()));
        Assert.assertNotNull(program.id);
        Assert.assertNotNull(program.id.channel);
        Assert.assertEquals(channel.id, program.id.channel.id);
        Assert.assertNotNull(program.id.presenter);
        Assert.assertNotNull(program.text);
        Assert.assertEquals(pres.name, program.id.presenter.name);
        s.delete(program);
        s.delete(program.id.channel);
        s.delete(program.id.presenter);
        tx.commit();
        s.close();
    }

    @Test
    public void testSecondaryTableWithIdClass() throws Exception {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Channel channel = new Channel();
        s.persist(channel);
        Presenter pres = new Presenter();
        pres.name = "Bob";
        s.persist(pres);
        TvProgramIdClass program = new TvProgramIdClass();
        program.time = new Date();
        program.channel = channel;
        program.presenter = pres;
        program.text = "Jump the shark programming";
        s.persist(program);
        tx.commit();
        s.clear();
        tx = s.beginTransaction();
        program = ((TvProgramIdClass) (s.createQuery("from TvProgramIdClass pr").uniqueResult()));
        Assert.assertNotNull(program.channel);
        Assert.assertEquals(channel.id, program.channel.id);
        Assert.assertNotNull(program.presenter);
        Assert.assertNotNull(program.text);
        Assert.assertEquals(pres.name, program.presenter.name);
        s.delete(program);
        s.delete(program.channel);
        s.delete(program.presenter);
        tx.commit();
        s.close();
    }

    @Test
    public void testQueryInAndComposite() {
        Session s = openSession();
        Transaction transaction = s.beginTransaction();
        createData(s);
        s.flush();
        List ids = new ArrayList<SomeEntityId>(2);
        ids.add(new SomeEntityId(1, 12));
        ids.add(new SomeEntityId(10, 23));
        Criteria criteria = s.createCriteria(SomeEntity.class);
        Disjunction disjunction = Restrictions.disjunction();
        disjunction.add(Restrictions.in("id", ids));
        criteria.add(disjunction);
        List list = criteria.list();
        Assert.assertEquals(2, list.size());
        transaction.rollback();
        s.close();
    }

    @Test
    public void testQueryInAndCompositeWithHQL() {
        Session s = openSession();
        Transaction transaction = s.beginTransaction();
        createData(s);
        s.flush();
        List ids = new ArrayList<SomeEntityId>(2);
        ids.add(new SomeEntityId(1, 12));
        ids.add(new SomeEntityId(10, 23));
        ids.add(new SomeEntityId(10, 22));
        Query query = s.createQuery("from SomeEntity e where e.id in :idList");
        query.setParameterList("idList", ids);
        List list = query.list();
        Assert.assertEquals(3, list.size());
        transaction.rollback();
        s.close();
    }
}

