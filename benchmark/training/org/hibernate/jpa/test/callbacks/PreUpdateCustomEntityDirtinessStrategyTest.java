/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.callbacks;


import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.Session;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12718")
public class PreUpdateCustomEntityDirtinessStrategyTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testPreUpdateModifications() {
        PreUpdateCustomEntityDirtinessStrategyTest.Person person = new PreUpdateCustomEntityDirtinessStrategyTest.Person();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.jpa.test.callbacks.Person p = session.find(.class, person.id);
            assertNotNull(p);
            assertNotNull(p.createdAt);
            assertNull(p.lastUpdatedAt);
            p.setName("Changed Name");
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.jpa.test.callbacks.Person p = session.find(.class, person.id);
            assertNotNull(p.lastUpdatedAt);
        });
        Assert.assertTrue(PreUpdateCustomEntityDirtinessStrategyTest.DefaultCustomEntityDirtinessStrategy.INSTANCE.isPersonNameChanged());
        Assert.assertTrue(PreUpdateCustomEntityDirtinessStrategyTest.DefaultCustomEntityDirtinessStrategy.INSTANCE.isPersonLastUpdatedAtChanged());
    }

    public static class DefaultCustomEntityDirtinessStrategy implements CustomEntityDirtinessStrategy {
        private static final PreUpdateCustomEntityDirtinessStrategyTest.DefaultCustomEntityDirtinessStrategy INSTANCE = new PreUpdateCustomEntityDirtinessStrategyTest.DefaultCustomEntityDirtinessStrategy();

        private boolean personNameChanged = false;

        private boolean personLastUpdatedAtChanged = false;

        @Override
        public boolean canDirtyCheck(Object entity, EntityPersister persister, Session session) {
            return true;
        }

        @Override
        public boolean isDirty(Object entity, EntityPersister persister, Session session) {
            PreUpdateCustomEntityDirtinessStrategyTest.Person person = ((PreUpdateCustomEntityDirtinessStrategyTest.Person) (entity));
            if (!(personNameChanged)) {
                personNameChanged = (person.getName()) != null;
                return personNameChanged;
            }
            if (!(personLastUpdatedAtChanged)) {
                personLastUpdatedAtChanged = (person.getLastUpdatedAt()) != null;
                return personLastUpdatedAtChanged;
            }
            return false;
        }

        @Override
        public void resetDirty(Object entity, EntityPersister persister, Session session) {
        }

        @Override
        public void findDirty(Object entity, EntityPersister persister, Session session, DirtyCheckContext dirtyCheckContext) {
        }

        public boolean isPersonNameChanged() {
            return personNameChanged;
        }

        public boolean isPersonLastUpdatedAtChanged() {
            return personLastUpdatedAtChanged;
        }
    }

    @Entity(name = "Person")
    @DynamicUpdate
    private static class Person {
        @Id
        @GeneratedValue
        private int id;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        private Instant createdAt;

        public Instant getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Instant createdAt) {
            this.createdAt = createdAt;
        }

        private Instant lastUpdatedAt;

        public Instant getLastUpdatedAt() {
            return lastUpdatedAt;
        }

        public void setLastUpdatedAt(Instant lastUpdatedAt) {
            this.lastUpdatedAt = lastUpdatedAt;
        }

        @ElementCollection
        private List<String> tags;

        @Lob
        @Basic(fetch = FetchType.LAZY)
        private ByteBuffer image;

        @PrePersist
        void beforeCreate() {
            this.setCreatedAt(Instant.now());
        }

        @PreUpdate
        void beforeUpdate() {
            this.setLastUpdatedAt(Instant.now());
        }
    }
}

