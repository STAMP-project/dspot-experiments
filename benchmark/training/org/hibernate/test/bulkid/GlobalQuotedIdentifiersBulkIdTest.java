package org.hibernate.test.bulkid;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12561")
public class GlobalQuotedIdentifiersBulkIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testBulkUpdate() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            int updateCount = entityManager.createQuery(("UPDATE Person u " + ("SET u.employedOn = :date " + "WHERE u.id IN :userIds"))).setParameter("date", Timestamp.valueOf("2018-06-03 00:00:00")).setParameter("userIds", Arrays.asList(1L, 2L, 3L)).executeUpdate();
            assertEquals(3, updateCount);
        });
    }

    @Entity(name = "Person")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Person {
        @Id
        @GeneratedValue
        private Long id;

        private String name;

        private boolean employed;

        @Temporal(TemporalType.TIMESTAMP)
        private Date employedOn;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Date getEmployedOn() {
            return employedOn;
        }

        public void setEmployedOn(Date employedOn) {
            this.employedOn = employedOn;
        }

        public boolean isEmployed() {
            return employed;
        }

        public void setEmployed(boolean employed) {
            this.employed = employed;
        }
    }

    @Entity(name = "Doctor")
    public static class Doctor extends GlobalQuotedIdentifiersBulkIdTest.Person {}

    @Entity(name = "Engineer")
    public static class Engineer extends GlobalQuotedIdentifiersBulkIdTest.Person {
        private boolean fellow;

        public boolean isFellow() {
            return fellow;
        }

        public void setFellow(boolean fellow) {
            this.fellow = fellow;
        }
    }
}

