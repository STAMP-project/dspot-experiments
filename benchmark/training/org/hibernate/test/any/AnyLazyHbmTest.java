/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.any;


import org.hibernate.LazyInitializationException;
import org.hibernate.test.annotations.any.LazyPropertySet;
import org.hibernate.test.annotations.any.Property;
import org.hibernate.test.annotations.any.StringProperty;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


public class AnyLazyHbmTest extends BaseCoreFunctionalTestCase {
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

