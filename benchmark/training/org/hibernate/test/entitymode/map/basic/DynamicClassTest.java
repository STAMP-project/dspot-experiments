/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.entitymode.map.basic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class DynamicClassTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testLazyDynamicClass() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Map cars = new HashMap();
        cars.put("description", "Cars");
        Map monaro = new HashMap();
        monaro.put("productLine", cars);
        monaro.put("name", "monaro");
        monaro.put("description", "Holden Monaro");
        Map hsv = new HashMap();
        hsv.put("productLine", cars);
        hsv.put("name", "hsv");
        hsv.put("description", "Holden Commodore HSV");
        List models = new ArrayList();
        cars.put("models", models);
        models.add(hsv);
        models.add(monaro);
        s.save("ProductLine", cars);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        cars = ((Map) (s.createQuery("from ProductLine pl order by pl.description").uniqueResult()));
        models = ((List) (cars.get("models")));
        Assert.assertFalse(Hibernate.isInitialized(models));
        Assert.assertEquals(models.size(), 2);
        Assert.assertTrue(Hibernate.isInitialized(models));
        s.clear();
        List list = s.createQuery("from Model m").list();
        for (Iterator i = list.iterator(); i.hasNext();) {
            Assert.assertFalse(Hibernate.isInitialized(((Map) (i.next())).get("productLine")));
        }
        Map model = ((Map) (list.get(0)));
        Assert.assertTrue(((List) (((Map) (model.get("productLine"))).get("models"))).contains(model));
        s.clear();
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        cars = ((Map) (s.createQuery("from ProductLine pl order by pl.description").uniqueResult()));
        s.delete(cars);
        t.commit();
        s.close();
    }
}

