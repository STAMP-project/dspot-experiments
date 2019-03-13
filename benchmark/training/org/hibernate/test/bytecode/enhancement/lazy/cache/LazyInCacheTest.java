/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy.cache;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Luis Barreiro
 */
@RunWith(BytecodeEnhancerRunner.class)
public class LazyInCacheTest extends BaseCoreFunctionalTestCase {
    private Long orderId;

    @Test
    public void test() {
        TransactionUtil.doInJPA(this::sessionFactory, ( em) -> {
            org.hibernate.test.bytecode.enhancement.lazy.cache.Order order = em.find(.class, orderId);
            Assert.assertEquals(1, order.products.size());
        });
    }

    // --- //
    @Entity
    @Table(name = "ORDER_TABLE")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private static class Order {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        @OneToMany
        List<LazyInCacheTest.Product> products = new ArrayList<>();

        @OneToMany
        List<LazyInCacheTest.Tag> tags = new ArrayList<>();

        @Basic(fetch = FetchType.LAZY)
        @Type(type = "org.hibernate.type.BinaryType")
        byte[] data;
    }

    @Entity
    @Table(name = "PRODUCT")
    private static class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        String name;
    }

    @Entity
    @Table(name = "TAG")
    private static class Tag {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        Long id;

        String name;
    }
}

