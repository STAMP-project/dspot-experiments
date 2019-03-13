/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.any;


import org.hibernate.LazyInitializationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


public class AnyTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testDefaultAnyAssociation() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        PropertySet set1 = new PropertySet("string");
        Property property = new StringProperty("name", "Alex");
        set1.setSomeProperty(property);
        set1.addGeneralProperty(property);
        s.save(set1);
        PropertySet set2 = new PropertySet("integer");
        property = new IntegerProperty("age", 33);
        set2.setSomeProperty(property);
        set2.addGeneralProperty(property);
        s.save(set2);
        s.flush();
        s.clear();
        Query q = s.createQuery("select s from PropertySet s where name = :name");
        q.setString("name", "string");
        PropertySet result = ((PropertySet) (q.uniqueResult()));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSomeProperty());
        Assert.assertTrue(((result.getSomeProperty()) instanceof StringProperty));
        Assert.assertEquals("Alex", result.getSomeProperty().asString());
        Assert.assertNotNull(result.getGeneralProperties());
        Assert.assertEquals(1, result.getGeneralProperties().size());
        Assert.assertEquals("Alex", result.getGeneralProperties().get(0).asString());
        q.setString("name", "integer");
        result = ((PropertySet) (q.uniqueResult()));
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSomeProperty());
        Assert.assertTrue(((result.getSomeProperty()) instanceof IntegerProperty));
        Assert.assertEquals("33", result.getSomeProperty().asString());
        Assert.assertNotNull(result.getGeneralProperties());
        Assert.assertEquals(1, result.getGeneralProperties().size());
        Assert.assertEquals("33", result.getGeneralProperties().get(0).asString());
        t.rollback();
        s.close();
    }

    @Test
    public void testManyToAnyWithMap() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        PropertyMap map = new PropertyMap("sample");
        map.getProperties().put("name", new StringProperty("name", "Alex"));
        map.getProperties().put("age", new IntegerProperty("age", 33));
        s.save(map);
        s.flush();
        s.clear();
        Query q = s.createQuery("SELECT map FROM PropertyMap map WHERE map.name = :name");
        q.setString("name", "sample");
        PropertyMap actualMap = ((PropertyMap) (q.uniqueResult()));
        Assert.assertNotNull(actualMap);
        Assert.assertNotNull(actualMap.getProperties());
        Property property = actualMap.getProperties().get("name");
        Assert.assertNotNull(property);
        Assert.assertTrue((property instanceof StringProperty));
        Assert.assertEquals("Alex", property.asString());
        property = actualMap.getProperties().get("age");
        Assert.assertNotNull(property);
        Assert.assertTrue((property instanceof IntegerProperty));
        Assert.assertEquals("33", property.asString());
        t.rollback();
        s.close();
    }

    @Test
    public void testMetaDataUseWithManyToAny() throws Exception {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        PropertyList list = new PropertyList("sample");
        StringProperty stringProperty = new StringProperty("name", "Alex");
        IntegerProperty integerProperty = new IntegerProperty("age", 33);
        LongProperty longProperty = new LongProperty("distance", 121L);
        CharProperty charProp = new CharProperty("Est", 'E');
        list.setSomeProperty(longProperty);
        list.addGeneralProperty(stringProperty);
        list.addGeneralProperty(integerProperty);
        list.addGeneralProperty(longProperty);
        list.addGeneralProperty(charProp);
        s.save(list);
        s.flush();
        s.clear();
        Query q = s.createQuery("SELECT list FROM PropertyList list WHERE list.name = :name");
        q.setString("name", "sample");
        PropertyList<Property> actualList = ((PropertyList<Property>) (q.uniqueResult()));
        Assert.assertNotNull(actualList);
        Assert.assertNotNull(actualList.getGeneralProperties());
        Assert.assertEquals(4, actualList.getGeneralProperties().size());
        Property property = actualList.getSomeProperty();
        Assert.assertNotNull(property);
        Assert.assertTrue((property instanceof LongProperty));
        Assert.assertEquals("121", property.asString());
        Assert.assertEquals("Alex", actualList.getGeneralProperties().get(0).asString());
        Assert.assertEquals("33", actualList.getGeneralProperties().get(1).asString());
        Assert.assertEquals("121", actualList.getGeneralProperties().get(2).asString());
        Assert.assertEquals("E", actualList.getGeneralProperties().get(3).asString());
        t.rollback();
        s.close();
    }

    @Test
    public void testFetchEager() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            PropertySet set = new PropertySet("string");
            Property property = new StringProperty("name", "Alex");
            set.setSomeProperty(property);
            s.save(set);
        });
        PropertySet result = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            return s.createQuery("select s from PropertySet s where name = :name", .class).setParameter("name", "string").getSingleResult();
        });
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSomeProperty());
        Assert.assertTrue(((result.getSomeProperty()) instanceof StringProperty));
        Assert.assertEquals("Alex", result.getSomeProperty().asString());
    }

    @Test
    public void testFetchLazy() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            LazyPropertySet set = new LazyPropertySet("string");
            Property property = new StringProperty("name", "Alex");
            set.setSomeProperty(property);
            s.save(set);
        });
        LazyPropertySet result = TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            return s.createQuery("select s from LazyPropertySet s where name = :name", .class).setParameter("name", "string").getSingleResult();
        });
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getSomeProperty());
        try {
            result.getSomeProperty().asString();
            Assert.fail("should not get the property string after session closed.");
        } catch (LazyInitializationException e) {
            // expected
        } catch (Exception e) {
            Assert.fail("should not throw exception other than LazyInitializationException.");
        }
    }
}

