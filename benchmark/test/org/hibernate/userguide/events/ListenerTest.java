/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.events;


import AvailableSettings.JACC_CONTEXT_ID;
import AvailableSettings.JACC_ENABLED;
import EventType.PRE_DELETE;
import EventType.PRE_INSERT;
import EventType.PRE_LOAD;
import EventType.PRE_UPDATE;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import org.hibernate.HibernateException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.DuplicationStrategy;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.integrator.spi.ServiceContributingIntegrator;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.secure.internal.DisabledJaccServiceImpl;
import org.hibernate.secure.internal.JaccPreDeleteEventListener;
import org.hibernate.secure.internal.JaccPreInsertEventListener;
import org.hibernate.secure.internal.JaccPreLoadEventListener;
import org.hibernate.secure.internal.JaccPreUpdateEventListener;
import org.hibernate.secure.internal.JaccSecurityListener;
import org.hibernate.secure.internal.StandardJaccServiceImpl;
import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.IntegrationException;
import org.hibernate.secure.spi.JaccPermissionDeclarations;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.jboss.logging.Logger;
import org.junit.Test;

import static Action.KEEP_ORIGINAL;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::events-declarative-security-jacc-example[]
public class ListenerTest extends BaseEntityManagerFunctionalTestCase {
    @Test(expected = SecurityException.class)
    public void testLoadListener() {
        Serializable customerId = 1L;
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // tag::events-interceptors-load-listener-example[]
            EntityManagerFactory entityManagerFactory = entityManagerFactory();
            SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(.class);
            sessionFactory.getServiceRegistry().getService(.class).prependListeners(EventType.LOAD, new org.hibernate.userguide.events.SecuredLoadEntityListener());
            org.hibernate.userguide.events.Customer customer = entityManager.find(.class, customerId);
            // end::events-interceptors-load-listener-example[]
        });
    }

    @Test
    public void testJPACallback() {
        Long personId = 1L;
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.events.Person person = new org.hibernate.userguide.events.Person();
            person.id = personId;
            person.name = "John Doe";
            person.dateOfBirth = Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 0, 0, 0));
            entityManager.persist(person);
        });
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.events.Person person = entityManager.find(.class, personId);
            assertTrue((person.age > 0));
        });
    }

    @Entity(name = "Customer")
    public static class Customer {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        public Customer() {
        }

        public Customer(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    // tag::events-jpa-callbacks-example[]
    @Entity
    @EntityListeners(ListenerTest.LastUpdateListener.class)
    public static class Person {
        @Id
        private Long id;

        private String name;

        private Date dateOfBirth;

        @Transient
        private long age;

        private Date lastUpdate;

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        /**
         * Set the transient property at load time based on a calculation.
         * Note that a native Hibernate formula mapping is better for this purpose.
         */
        @PostLoad
        public void calculateAge() {
            age = ChronoUnit.YEARS.between(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateOfBirth.getTime()), ZoneOffset.UTC), LocalDateTime.now());
        }
    }

    public static class LastUpdateListener {
        @PreUpdate
        @PrePersist
        public void setLastUpdate(ListenerTest.Person p) {
            p.setLastUpdate(new Date());
        }
    }

    // end::events-jpa-callbacks-example[]
    // tag::events-interceptors-example[]
    public static class SecuredLoadEntityListener implements LoadEventListener {
        // this is the single method defined by the LoadEventListener interface
        public void onLoad(LoadEvent event, LoadEventListener.LoadType loadType) throws HibernateException {
            if (!(ListenerTest.Principal.isAuthorized(event.getEntityClassName(), event.getEntityId()))) {
                throw new SecurityException("Unauthorized access");
            }
        }
    }

    // end::events-Principal-example[]
    public static class Principal {
        public static boolean isAuthorized(String clazz, Serializable id) {
            return false;
        }
    }

    // tag::events-declarative-security-jacc-example[]
    public static class JaccIntegrator implements ServiceContributingIntegrator {
        private static final Logger log = Logger.getLogger(ListenerTest.JaccIntegrator.class);

        private static final DuplicationStrategy DUPLICATION_STRATEGY = new DuplicationStrategy() {
            @Override
            public boolean areMatch(Object listener, Object original) {
                return (listener.getClass().equals(original.getClass())) && (JaccSecurityListener.class.isInstance(original));
            }

            @Override
            public Action getAction() {
                return KEEP_ORIGINAL;
            }
        };

        @Override
        public void prepareServices(StandardServiceRegistryBuilder serviceRegistryBuilder) {
            boolean isSecurityEnabled = serviceRegistryBuilder.getSettings().containsKey(JACC_ENABLED);
            final JaccService jaccService = (isSecurityEnabled) ? new StandardJaccServiceImpl() : new DisabledJaccServiceImpl();
            serviceRegistryBuilder.addService(JaccService.class, jaccService);
        }

        @Override
        public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
            // pass no permissions here, because atm actually injecting the
            // permissions into the JaccService is handled on SessionFactoryImpl via
            // the org.hibernate.boot.cfgxml.spi.CfgXmlAccessService
            doIntegration(serviceRegistry.getService(ConfigurationService.class).getSettings(), null, serviceRegistry);
        }

        private void doIntegration(Map properties, JaccPermissionDeclarations permissionDeclarations, SessionFactoryServiceRegistry serviceRegistry) {
            boolean isSecurityEnabled = properties.containsKey(JACC_ENABLED);
            if (!isSecurityEnabled) {
                ListenerTest.JaccIntegrator.log.debug("Skipping JACC integration as it was not enabled");
                return;
            }
            final String contextId = ((String) (properties.get(JACC_CONTEXT_ID)));
            if (contextId == null) {
                throw new IntegrationException("JACC context id must be specified");
            }
            final JaccService jaccService = serviceRegistry.getService(JaccService.class);
            if (jaccService == null) {
                throw new IntegrationException("JaccService was not set up");
            }
            if (permissionDeclarations != null) {
                for (GrantedPermission declaration : permissionDeclarations.getPermissionDeclarations()) {
                    jaccService.addPermission(declaration);
                }
            }
            final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
            eventListenerRegistry.addDuplicationStrategy(ListenerTest.JaccIntegrator.DUPLICATION_STRATEGY);
            eventListenerRegistry.prependListeners(PRE_DELETE, new JaccPreDeleteEventListener());
            eventListenerRegistry.prependListeners(PRE_INSERT, new JaccPreInsertEventListener());
            eventListenerRegistry.prependListeners(PRE_UPDATE, new JaccPreUpdateEventListener());
            eventListenerRegistry.prependListeners(PRE_LOAD, new JaccPreLoadEventListener());
        }

        @Override
        public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
            // nothing to do
        }
    }
}

