/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.tuplizer;


import org.hibernate.Session;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class TuplizerTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEntityTuplizer() throws Exception {
        Cuisine cuisine = ProxyHelper.newCuisineProxy(null);
        cuisine.setName("Francaise");
        Country country = ProxyHelper.newCountryProxy(null);
        country.setName("France");
        cuisine.setCountry(country);
        Session s = openSession(new EntityNameInterceptor());
        s.getTransaction().begin();
        s.persist(cuisine);
        s.flush();
        s.clear();
        cuisine = ((Cuisine) (s.get(Cuisine.class, cuisine.getId())));
        Assert.assertNotNull(cuisine);
        Assert.assertEquals("Francaise", cuisine.getName());
        Assert.assertEquals("France", country.getName());
        s.getTransaction().rollback();
        s.close();
    }
}

