/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.batchfetch;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.loader.BatchFetchStyle;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class BatchFetchTest extends BaseCoreFunctionalTestCase {
    @SuppressWarnings({ "unchecked" })
    @Test
    public void testBatchFetch() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        ProductLine cars = new ProductLine();
        cars.setDescription("Cars");
        Model monaro = new Model(cars);
        monaro.setName("monaro");
        monaro.setDescription("Holden Monaro");
        Model hsv = new Model(cars);
        hsv.setName("hsv");
        hsv.setDescription("Holden Commodore HSV");
        s.save(cars);
        ProductLine oss = new ProductLine();
        oss.setDescription("OSS");
        Model jboss = new Model(oss);
        jboss.setName("JBoss");
        jboss.setDescription("JBoss Application Server");
        Model hibernate = new Model(oss);
        hibernate.setName("Hibernate");
        hibernate.setDescription("Hibernate");
        Model cache = new Model(oss);
        cache.setName("JBossCache");
        cache.setDescription("JBoss TreeCache");
        s.save(oss);
        t.commit();
        s.close();
        s.getSessionFactory().getCache().evictEntityRegion(Model.class);
        s.getSessionFactory().getCache().evictEntityRegion(ProductLine.class);
        s = openSession();
        t = s.beginTransaction();
        List list = s.createQuery("from ProductLine pl order by pl.description").list();
        cars = ((ProductLine) (list.get(0)));
        oss = ((ProductLine) (list.get(1)));
        Assert.assertFalse(Hibernate.isInitialized(cars.getModels()));
        Assert.assertFalse(Hibernate.isInitialized(oss.getModels()));
        Assert.assertEquals(cars.getModels().size(), 2);// fetch both collections

        Assert.assertTrue(Hibernate.isInitialized(cars.getModels()));
        Assert.assertTrue(Hibernate.isInitialized(oss.getModels()));
        s.clear();
        list = s.createQuery("from Model m").list();
        hibernate = ((Model) (s.get(Model.class, hibernate.getId())));
        hibernate.getProductLine().getId();
        for (Object aList : list) {
            Assert.assertFalse(Hibernate.isInitialized(((Model) (aList)).getProductLine()));
        }
        Assert.assertEquals(hibernate.getProductLine().getDescription(), "OSS");// fetch both productlines

        s.clear();
        Iterator iter = s.createQuery("from Model").iterate();
        list = new ArrayList();
        while (iter.hasNext()) {
            list.add(iter.next());
        } 
        Model m = ((Model) (list.get(0)));
        m.getDescription();// fetch a batch of 4

        s.clear();
        list = s.createQuery("from ProductLine").list();
        ProductLine pl = ((ProductLine) (list.get(0)));
        ProductLine pl2 = ((ProductLine) (list.get(1)));
        s.evict(pl2);
        pl.getModels().size();// fetch just one collection! (how can we write an assertion for that??)

        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        list = s.createQuery("from ProductLine pl order by pl.description").list();
        cars = ((ProductLine) (list.get(0)));
        oss = ((ProductLine) (list.get(1)));
        Assert.assertEquals(cars.getModels().size(), 2);
        Assert.assertEquals(oss.getModels().size(), 3);
        s.delete(cars);
        s.delete(oss);
        t.commit();
        s.close();
    }

    @Test
    @SuppressWarnings({ "unchecked" })
    public void testBatchFetch2() {
        Session s = openSession();
        s.beginTransaction();
        int size = 32 + 14;
        for (int i = 0; i < size; i++) {
            s.save(new BatchLoadableEntity(i));
        }
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        // load them all as proxies
        for (int i = 0; i < size; i++) {
            BatchLoadableEntity entity = ((BatchLoadableEntity) (s.load(BatchLoadableEntity.class, i)));
            Assert.assertFalse(Hibernate.isInitialized(entity));
        }
        sessionFactory().getStatistics().clear();
        // now start initializing them...
        for (int i = 0; i < size; i++) {
            BatchLoadableEntity entity = ((BatchLoadableEntity) (s.load(BatchLoadableEntity.class, i)));
            Hibernate.initialize(entity);
            Assert.assertTrue(Hibernate.isInitialized(entity));
        }
        // so at this point, all entities are initialized.  see how many fetches were performed.
        final int expectedFetchCount;
        if ((sessionFactory().getSettings().getBatchFetchStyle()) == (BatchFetchStyle.LEGACY)) {
            expectedFetchCount = 3;// (32 + 10 + 4)

        } else
            if ((sessionFactory().getSettings().getBatchFetchStyle()) == (BatchFetchStyle.DYNAMIC)) {
                expectedFetchCount = 2;// (32 + 14) : because we limited batch-size to 32

            } else {
                // PADDED
                expectedFetchCount = 2;// (32 + 16*) with the 16 being padded

            }

        Assert.assertEquals(expectedFetchCount, sessionFactory().getStatistics().getEntityStatistics(BatchLoadableEntity.class.getName()).getFetchCount());
        s.getTransaction().commit();
        s.close();
        s = openSession();
        s.beginTransaction();
        s.createQuery("delete BatchLoadableEntity").executeUpdate();
        s.getTransaction().commit();
        s.close();
    }
}

