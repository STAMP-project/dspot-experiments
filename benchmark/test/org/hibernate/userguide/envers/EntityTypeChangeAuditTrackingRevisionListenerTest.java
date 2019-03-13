/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.envers;


import AvailableSettings.HBM2DDL_AUTO;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.Audited;
import org.hibernate.envers.EntityTrackingRevisionListener;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;
import org.hibernate.envers.RevisionType;
import org.hibernate.jpa.AvailableSettings.LOADED_CLASSES;
import org.hibernate.jpa.boot.spi.Bootstrap;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::envers-tracking-modified-entities-revchanges-EntityType-example[]
public class EntityTypeChangeAuditTrackingRevisionListenerTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testLifecycle() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.envers.Customer customer = new org.hibernate.userguide.envers.Customer();
            customer.setId(1L);
            customer.setFirstName("John");
            customer.setLastName("Doe");
            entityManager.persist(customer);
        });
        EntityManagerFactory entityManagerFactory = null;
        try {
            Map settings = buildSettings();
            settings.put(LOADED_CLASSES, Arrays.asList(EntityTypeChangeAuditTrackingRevisionListenerTest.ApplicationCustomer.class, EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionEntity.class, EntityTypeChangeAuditTrackingRevisionListenerTest.EntityType.class));
            settings.put(HBM2DDL_AUTO, "update");
            entityManagerFactory = Bootstrap.getEntityManagerFactoryBuilder(new TestingPersistenceUnitDescriptorImpl(getClass().getSimpleName()), settings).build().unwrap(SessionFactoryImplementor.class);
            final EntityManagerFactory emf = entityManagerFactory;
            doInJPA(() -> emf, ( entityManager) -> {
                org.hibernate.userguide.envers.ApplicationCustomer customer = new org.hibernate.userguide.envers.ApplicationCustomer();
                customer.setId(2L);
                customer.setFirstName("John");
                customer.setLastName("Doe Jr.");
                entityManager.persist(customer);
            });
            doInJPA(() -> emf, ( entityManager) -> {
                // tag::envers-tracking-modified-entities-revchanges-query-example[]
                AuditReader auditReader = AuditReaderFactory.get(entityManager);
                List<Number> revisions = auditReader.getRevisions(.class, 1L);
                org.hibernate.userguide.envers.CustomTrackingRevisionEntity revEntity = auditReader.findRevision(.class, revisions.get(0));
                Set<org.hibernate.userguide.envers.EntityType> modifiedEntityTypes = revEntity.getModifiedEntityTypes();
                assertEquals(1, modifiedEntityTypes.size());
                org.hibernate.userguide.envers.EntityType entityType = modifiedEntityTypes.iterator().next();
                assertEquals(.class.getName(), entityType.getEntityClassName());
                // end::envers-tracking-modified-entities-revchanges-query-example[]
            });
        } finally {
            if (entityManagerFactory != null) {
                entityManagerFactory.close();
            }
        }
    }

    @Audited
    @Entity(name = "Customer")
    public static class Customer {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_on")
        @CreationTimestamp
        private Date createdOn;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }
    }

    @Audited
    @Entity(name = "Customer")
    public static class ApplicationCustomer {
        @Id
        private Long id;

        private String firstName;

        private String lastName;

        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "created_on")
        @CreationTimestamp
        private Date createdOn;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }
    }

    // tag::envers-tracking-modified-entities-revchanges-EntityTrackingRevisionListener-example[]
    public static class CustomTrackingRevisionListener implements EntityTrackingRevisionListener {
        @Override
        public void entityChanged(Class entityClass, String entityName, Serializable entityId, RevisionType revisionType, Object revisionEntity) {
            String type = entityClass.getName();
            ((EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionEntity) (revisionEntity)).addModifiedEntityType(type);
        }

        @Override
        public void newRevision(Object revisionEntity) {
        }
    }

    // end::envers-tracking-modified-entities-revchanges-EntityTrackingRevisionListener-example[]
    // tag::envers-tracking-modified-entities-revchanges-RevisionEntity-example[]
    @Entity(name = "CustomTrackingRevisionEntity")
    @Table(name = "TRACKING_REV_INFO")
    @RevisionEntity(EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionListener.class)
    public static class CustomTrackingRevisionEntity {
        @Id
        @GeneratedValue
        @RevisionNumber
        private int customId;

        @RevisionTimestamp
        private long customTimestamp;

        @OneToMany(mappedBy = "revision", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
        private java.util.Set<EntityTypeChangeAuditTrackingRevisionListenerTest.EntityType> modifiedEntityTypes = new HashSet<>();

        public java.util.Set<EntityTypeChangeAuditTrackingRevisionListenerTest.EntityType> getModifiedEntityTypes() {
            return modifiedEntityTypes;
        }

        public void addModifiedEntityType(String entityClassName) {
            modifiedEntityTypes.add(new EntityTypeChangeAuditTrackingRevisionListenerTest.EntityType(this, entityClassName));
        }
    }

    // end::envers-tracking-modified-entities-revchanges-RevisionEntity-example[]
    // tag::envers-tracking-modified-entities-revchanges-EntityType-example[]
    // tag::envers-tracking-modified-entities-revchanges-EntityType-example[]
    @Entity(name = "EntityType")
    public static class EntityType {
        @Id
        @GeneratedValue
        private Integer id;

        @ManyToOne
        private EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionEntity revision;

        private String entityClassName;

        private EntityType() {
        }

        public EntityType(EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionEntity revision, String entityClassName) {
            this.revision = revision;
            this.entityClassName = entityClassName;
        }

        // Getters and setters are omitted for brevity
        // end::envers-tracking-modified-entities-revchanges-EntityType-example[]
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionEntity getRevision() {
            return revision;
        }

        public void setRevision(EntityTypeChangeAuditTrackingRevisionListenerTest.CustomTrackingRevisionEntity revision) {
            this.revision = revision;
        }

        public String getEntityClassName() {
            return entityClassName;
        }

        public void setEntityClassName(String entityClassName) {
            this.entityClassName = entityClassName;
        }
    }
}

