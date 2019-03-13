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
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "HHH-12718")
@RunWith(BytecodeEnhancerRunner.class)
public class PreUpdateBytecodeEnhancementTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPreUpdateModifications() {
        PreUpdateBytecodeEnhancementTest.Person person = new PreUpdateBytecodeEnhancementTest.Person();
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            entityManager.persist(person);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.callbacks.Person p = entityManager.find(.class, person.id);
            assertNotNull(p);
            assertNotNull(p.createdAt);
            assertNull(p.lastUpdatedAt);
            p.setName("Changed Name");
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.callbacks.Person p = entityManager.find(.class, person.id);
            assertNotNull(p.lastUpdatedAt);
        });
    }

    @Entity(name = "Person")
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

