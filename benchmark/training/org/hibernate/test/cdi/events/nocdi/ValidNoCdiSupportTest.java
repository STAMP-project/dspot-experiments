/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.events.nocdi;


import Action.CREATE_DROP;
import AvailableSettings.HBM2DDL_AUTO;
import java.util.concurrent.atomic.AtomicInteger;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests valid use of ManagedBeanRegistry when CDI is not available -
 * meaning injection is not requested.
 *
 * @author Steve Ebersole
 */
public class ValidNoCdiSupportTest extends BaseUnitTestCase {
    @Test
    public void testIt() {
        ValidNoCdiSupportTest.AnotherListener.reset();
        BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
        final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder(bsr).applySetting(HBM2DDL_AUTO, CREATE_DROP).build();
        final SessionFactoryImplementor sessionFactory;
        try {
            sessionFactory = ((SessionFactoryImplementor) (addAnnotatedClass(ValidNoCdiSupportTest.AnotherEntity.class).buildMetadata().getSessionFactoryBuilder().build()));
        } catch (Exception e) {
            StandardServiceRegistryBuilder.destroy(ssr);
            throw e;
        }
        // The CDI bean should have been built immediately...
        Assert.assertTrue(ValidNoCdiSupportTest.AnotherListener.wasInstantiated());
        Assert.assertEquals(0, ValidNoCdiSupportTest.AnotherListener.currentCount());
        try {
            TransactionUtil2.inTransaction(sessionFactory, ( session) -> session.persist(new org.hibernate.test.cdi.events.nocdi.AnotherEntity(1)));
            Assert.assertEquals(1, ValidNoCdiSupportTest.AnotherListener.currentCount());
            TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                org.hibernate.test.cdi.events.nocdi.AnotherEntity it = session.find(.class, 1);
                assertNotNull(it);
            });
        } finally {
            TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                session.createQuery("delete AnotherEntity").executeUpdate();
            });
            sessionFactory.close();
        }
    }

    @Entity(name = "AnotherEntity")
    @Table(name = "another_entity")
    @EntityListeners(ValidNoCdiSupportTest.AnotherListener.class)
    public static class AnotherEntity {
        private Integer id;

        private String name;

        public AnotherEntity() {
        }

        public AnotherEntity(Integer id) {
            this.id = id;
        }

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class AnotherListener {
        private static final AtomicInteger count = new AtomicInteger(0);

        private static boolean instantiated;

        public AnotherListener() {
            ValidNoCdiSupportTest.AnotherListener.instantiated = true;
        }

        public static void reset() {
            ValidNoCdiSupportTest.AnotherListener.count.set(0);
            ValidNoCdiSupportTest.AnotherListener.instantiated = false;
        }

        public static boolean wasInstantiated() {
            return ValidNoCdiSupportTest.AnotherListener.instantiated;
        }

        public static int currentCount() {
            return ValidNoCdiSupportTest.AnotherListener.count.get();
        }

        @PrePersist
        public void onCreate(Object entity) {
            ValidNoCdiSupportTest.AnotherListener.count.getAndIncrement();
        }
    }
}

