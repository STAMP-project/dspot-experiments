/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.cdi;


import Action.CREATE_DROP;
import AvailableSettings.CDI_BEAN_MANAGER;
import AvailableSettings.DELAY_CDI_ACCESS;
import AvailableSettings.HBM2DDL_AUTO;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;
import javax.inject.Inject;
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
import org.hibernate.testing.transaction.TransactionUtil2;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class BasicCdiTest {
    private static int count;

    @Test
    @SuppressWarnings("unchecked")
    public void testIt() {
        final SeContainerInitializer cdiInitializer = SeContainerInitializer.newInstance().disableDiscovery().addBeanClasses(BasicCdiTest.Monitor.class, BasicCdiTest.EventQueue.class, BasicCdiTest.Event.class);
        BasicCdiTest.count = 0;
        try (final SeContainer cdiContainer = cdiInitializer.initialize()) {
            BootstrapServiceRegistry bsr = new BootstrapServiceRegistryBuilder().build();
            final StandardServiceRegistry ssr = new StandardServiceRegistryBuilder(bsr).applySetting(CDI_BEAN_MANAGER, cdiContainer.getBeanManager()).applySetting(DELAY_CDI_ACCESS, "true").applySetting(HBM2DDL_AUTO, CREATE_DROP).build();
            final SessionFactoryImplementor sessionFactory;
            try {
                sessionFactory = ((SessionFactoryImplementor) (addAnnotatedClass(BasicCdiTest.MyEntity.class).buildMetadata().getSessionFactoryBuilder().build()));
            } catch (Exception e) {
                StandardServiceRegistryBuilder.destroy(ssr);
                throw e;
            }
            try {
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> session.persist(new org.hibernate.jpa.test.cdi.MyEntity(1)));
                Assert.assertEquals(1, BasicCdiTest.count);
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                    org.hibernate.jpa.test.cdi.MyEntity it = session.find(.class, 1);
                    assertNotNull(it);
                });
            } finally {
                TransactionUtil2.inTransaction(sessionFactory, ( session) -> {
                    session.createQuery("delete MyEntity").executeUpdate();
                });
                sessionFactory.close();
            }
        }
    }

    @Entity(name = "MyEntity")
    @EntityListeners(BasicCdiTest.Monitor.class)
    @Table(name = "my_entity")
    public static class MyEntity {
        private Integer id;

        private String name;

        public MyEntity() {
        }

        public MyEntity(Integer id) {
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

    public static class EventQueue {
        private List<BasicCdiTest.Event> events;

        public void addEvent(BasicCdiTest.Event anEvent) {
            if ((events) == null) {
                events = new ArrayList<>();
            }
            events.add(anEvent);
            (BasicCdiTest.count)++;
        }
    }

    public static class Event {
        private final String who;

        private final String what;

        private final String when;

        public Event(String who, String what, String when) {
            this.who = who;
            this.what = what;
            this.when = when;
        }

        public String getWho() {
            return who;
        }

        public String getWhat() {
            return what;
        }

        public String getWhen() {
            return when;
        }
    }

    public static class Monitor {
        private final BasicCdiTest.EventQueue eventQueue;

        @Inject
        public Monitor(BasicCdiTest.EventQueue eventQueue) {
            this.eventQueue = eventQueue;
        }

        @PrePersist
        public void onCreate(Object entity) {
            eventQueue.addEvent(new BasicCdiTest.Event(entity.toString(), "created", now()));
        }

        private String now() {
            return new SimpleDateFormat().format(new Date());
        }
    }
}

