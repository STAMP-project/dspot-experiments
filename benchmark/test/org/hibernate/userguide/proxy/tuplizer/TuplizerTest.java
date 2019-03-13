/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.proxy.tuplizer;


import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class TuplizerTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testEntityTuplizer() throws Exception {
        // tag::entity-tuplizer-dynamic-proxy-example[]
        Cuisine _cuisine = doInHibernateSessionBuilder(() -> sessionFactory().withOptions().interceptor(new EntityNameInterceptor()), ( session) -> {
            Cuisine cuisine = ProxyHelper.newProxy(.class, null);
            cuisine.setName("Fran?aise");
            Country country = ProxyHelper.newProxy(.class, null);
            country.setName("France");
            cuisine.setCountry(country);
            session.persist(cuisine);
            return cuisine;
        });
        doInHibernateSessionBuilder(() -> sessionFactory().withOptions().interceptor(new EntityNameInterceptor()), ( session) -> {
            Cuisine cuisine = session.get(.class, _cuisine.getId());
            assertEquals("Fran?aise", cuisine.getName());
            assertEquals("France", cuisine.getCountry().getName());
        });
        // end::entity-tuplizer-dynamic-proxy-example[]
    }
}

