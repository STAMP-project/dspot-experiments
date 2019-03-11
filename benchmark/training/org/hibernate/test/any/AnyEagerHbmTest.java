/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.any;


import org.hibernate.test.annotations.any.PropertySet;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


public class AnyEagerHbmTest extends BaseCoreFunctionalTestCase {
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
        Assert.assertTrue(((result.getSomeProperty()) instanceof org.hibernate.test.annotations.any.StringProperty));
        Assert.assertEquals("Alex", result.getSomeProperty().asString());
    }
}

