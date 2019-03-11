/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.envers;


import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.envers.Audited;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
// end::envers-revisionlog-RevisionListener-example[]
public class CustomRevisionEntityTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void test() {
        // tag::envers-revisionlog-RevisionEntity-persist-example[]
        CustomRevisionEntityTest.CurrentUser.INSTANCE.logIn("Vlad Mihalcea");
        doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.userguide.envers.Customer customer = new org.hibernate.userguide.envers.Customer();
            customer.setId(1L);
            customer.setFirstName("John");
            customer.setLastName("Doe");
            entityManager.persist(customer);
        });
        CustomRevisionEntityTest.CurrentUser.INSTANCE.logOut();
        // end::envers-revisionlog-RevisionEntity-persist-example[]
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

    // tag::envers-revisionlog-CurrentUser-example[]
    public static class CurrentUser {
        public static final CustomRevisionEntityTest.CurrentUser INSTANCE = new CustomRevisionEntityTest.CurrentUser();

        private static final ThreadLocal<String> storage = new ThreadLocal<>();

        public void logIn(String user) {
            CustomRevisionEntityTest.CurrentUser.storage.set(user);
        }

        public void logOut() {
            CustomRevisionEntityTest.CurrentUser.storage.remove();
        }

        public String get() {
            return CustomRevisionEntityTest.CurrentUser.storage.get();
        }
    }

    // end::envers-revisionlog-CurrentUser-example[]
    // tag::envers-revisionlog-RevisionEntity-example[]
    @Entity(name = "CustomRevisionEntity")
    @Table(name = "CUSTOM_REV_INFO")
    @RevisionEntity(CustomRevisionEntityTest.CustomRevisionEntityListener.class)
    public static class CustomRevisionEntity extends DefaultRevisionEntity {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    // end::envers-revisionlog-RevisionEntity-example[]
    // tag::envers-revisionlog-RevisionListener-example[]
    public static class CustomRevisionEntityListener implements RevisionListener {
        public void newRevision(Object revisionEntity) {
            CustomRevisionEntityTest.CustomRevisionEntity customRevisionEntity = ((CustomRevisionEntityTest.CustomRevisionEntity) (revisionEntity));
            customRevisionEntity.setUsername(CustomRevisionEntityTest.CurrentUser.INSTANCE.get());
        }
    }
}

