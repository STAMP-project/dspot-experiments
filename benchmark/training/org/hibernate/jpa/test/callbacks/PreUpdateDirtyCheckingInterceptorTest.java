/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.callbacks;


import java.io.Serializable;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import org.hibernate.EmptyInterceptor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.hibernate.type.Type;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-12718")
public class PreUpdateDirtyCheckingInterceptorTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testPreUpdateModifications() {
        PreUpdateDirtyCheckingInterceptorTest.Person person = new PreUpdateDirtyCheckingInterceptorTest.Person();
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.persist(person);
        });
        TransactionUtil.doInHibernateSessionBuilder(this::sessionWithInterceptor, ( session) -> {
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
    }

    public static class OnFlushDirtyInterceptor extends EmptyInterceptor {
        private static PreUpdateDirtyCheckingInterceptorTest.OnFlushDirtyInterceptor INSTANCE = new PreUpdateDirtyCheckingInterceptorTest.OnFlushDirtyInterceptor();

        @Override
        public int[] findDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
            int[] result = new int[propertyNames.length];
            int span = 0;
            for (int i = 0; i < (previousState.length); i++) {
                if (!(Objects.deepEquals(previousState[i], currentState[i]))) {
                    result[(span++)] = i;
                }
            }
            return result;
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

