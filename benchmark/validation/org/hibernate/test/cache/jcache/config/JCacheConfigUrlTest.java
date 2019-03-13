/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cache.jcache.config;


import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;


public class JCacheConfigUrlTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void test() {
        Product product = new Product();
        product.setName("Acme");
        product.setPriceCents(100L);
        doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(product);
        });
    }
}

