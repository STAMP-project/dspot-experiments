/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
/**
 * $Id$
 */
package org.hibernate.test.annotations;


import Configuration.ARTEFACT_PROCESSING_ORDER;
import Environment.HBM2DDL_AUTO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class ConfigurationTest {
    @Test
    public void testDeclarativeMix() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("org/hibernate/test/annotations/hibernate.cfg.xml");
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        SessionFactory sf = cfg.buildSessionFactory();
        Assert.assertNotNull(sf);
        Session s = sf.openSession();
        Transaction tx = s.beginTransaction();
        Query q = s.createQuery("from Boat");
        Assert.assertEquals(0, q.list().size());
        q = s.createQuery("from Plane");
        Assert.assertEquals(0, q.list().size());
        tx.commit();
        s.close();
        sf.close();
    }

    @Test
    public void testIgnoringHbm() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("org/hibernate/test/annotations/hibernate.cfg.xml");
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        cfg.setProperty(ARTEFACT_PROCESSING_ORDER, "class");
        try (SessionFactoryImplementor sf = ((SessionFactoryImplementor) (cfg.buildSessionFactory()))) {
            Assert.assertNotNull(sf);
            TransactionUtil2.inTransaction(sf, ( session) -> {
                try {
                    session.createQuery("from Boat").list();
                    fail("Boat should not be mapped");
                } catch ( expected) {
                    assertTyping(.class, expected.getCause());
                    // expected outcome
                    // see org.hibernate.test.jpa.compliance.tck2_2.QueryApiTest#testInvalidQueryMarksTxnForRollback
                    // for testing of how this invalid query String case is handled in terms of transactions
                }
            });
            TransactionUtil2.inTransaction(sf, ( session) -> {
                assertEquals(0, session.createQuery("from Plane").list().size());
            });
        }
    }

    @Test
    public void testPrecedenceHbm() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("org/hibernate/test/annotations/hibernate.cfg.xml");
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        cfg.addAnnotatedClass(Boat.class);
        SessionFactory sf = cfg.buildSessionFactory();
        Assert.assertNotNull(sf);
        Session s = sf.openSession();
        s.getTransaction().begin();
        Boat boat = new Boat();
        boat.setSize(12);
        boat.setWeight(34);
        s.persist(boat);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        boat = ((Boat) (s.get(Boat.class, boat.getId())));
        Assert.assertTrue("Annotation has precedence", (34 != (boat.getWeight())));
        s.delete(boat);
        // s.getTransaction().commit();
        tx.commit();
        s.close();
        sf.close();
    }

    @Test
    public void testPrecedenceAnnotation() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("org/hibernate/test/annotations/hibernate.cfg.xml");
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        cfg.setProperty(ARTEFACT_PROCESSING_ORDER, "class, hbm");
        cfg.addAnnotatedClass(Boat.class);
        SessionFactory sf = cfg.buildSessionFactory();
        Assert.assertNotNull(sf);
        Session s = sf.openSession();
        s.getTransaction().begin();
        Boat boat = new Boat();
        boat.setSize(12);
        boat.setWeight(34);
        s.persist(boat);
        s.getTransaction().commit();
        s.clear();
        Transaction tx = s.beginTransaction();
        boat = ((Boat) (s.get(Boat.class, boat.getId())));
        Assert.assertTrue("Annotation has precedence", (34 == (boat.getWeight())));
        s.delete(boat);
        tx.commit();
        s.close();
        sf.close();
    }

    @Test
    public void testHbmWithSubclassExtends() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("org/hibernate/test/annotations/hibernate.cfg.xml");
        cfg.addClass(Ferry.class);
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        SessionFactory sf = cfg.buildSessionFactory();
        Assert.assertNotNull(sf);
        Session s = sf.openSession();
        Transaction tx = s.beginTransaction();
        Query q = s.createQuery("from Ferry");
        Assert.assertEquals(0, q.list().size());
        q = s.createQuery("from Plane");
        Assert.assertEquals(0, q.list().size());
        tx.commit();
        s.close();
        sf.close();
    }

    @Test
    public void testAnnReferencesHbm() throws Exception {
        Configuration cfg = new Configuration();
        cfg.configure("org/hibernate/test/annotations/hibernate.cfg.xml");
        cfg.addAnnotatedClass(Port.class);
        cfg.setProperty(HBM2DDL_AUTO, "create-drop");
        SessionFactory sf = cfg.buildSessionFactory();
        Assert.assertNotNull(sf);
        Session s = sf.openSession();
        Transaction tx = s.beginTransaction();
        Query q = s.createQuery("from Boat");
        Assert.assertEquals(0, q.list().size());
        q = s.createQuery("from Port");
        Assert.assertEquals(0, q.list().size());
        tx.commit();
        s.close();
        sf.close();
    }
}

