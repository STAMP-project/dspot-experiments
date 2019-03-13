/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.locking;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::locking-optimistic-exclude-attribute-mapping-example[]
public class OptimisticLockTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Phone phone = new org.hibernate.userguide.locking.Phone();
            phone.setId(1L);
            phone.setNumber("123-456-7890");
            entityManager.persist(phone);
            return phone;
        });
        // tag::locking-optimistic-exclude-attribute-example[]
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.locking.Phone phone = entityManager.find(.class, 1L);
            phone.setNumber("+123-456-7890");
            doInJPA(this::entityManagerFactory, ( _entityManager) -> {
                org.hibernate.userguide.locking.Phone _phone = _entityManager.find(.class, 1L);
                _phone.incrementCallCount();
                log.info("Bob changes the Phone call count");
            });
            log.info("Alice changes the Phone number");
        });
        // end::locking-optimistic-exclude-attribute-example[]
    }

    // tag::locking-optimistic-exclude-attribute-mapping-example[]
    @Entity(name = "Phone")
    public static class Phone {
        @Id
        private Long id;

        @Column(name = "`number`")
        private String number;

        @OptimisticLock(excluded = true)
        private long callCount;

        @Version
        private Long version;

        // Getters and setters are omitted for brevity
        // end::locking-optimistic-exclude-attribute-mapping-example[]
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public Long getVersion() {
            return version;
        }

        public long getCallCount() {
            return callCount;
        }

        // tag::locking-optimistic-exclude-attribute-mapping-example[]
        public void incrementCallCount() {
            (this.callCount)++;
        }
    }
}

