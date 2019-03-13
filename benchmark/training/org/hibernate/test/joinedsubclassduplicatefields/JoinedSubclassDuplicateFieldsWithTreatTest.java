/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.joinedsubclassduplicatefields;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author pholvs
 */
@TestForIssue(jiraKey = "HHH-11686")
public class JoinedSubclassDuplicateFieldsWithTreatTest extends BaseCoreFunctionalTestCase {
    @Test
    @FailureExpected(jiraKey = "HHH-11686")
    public void queryConstrainedSubclass() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.joinedsubclassduplicatefields.Deposit deposit1 = new org.hibernate.test.joinedsubclassduplicatefields.Deposit();
            deposit1.id = 1L;
            deposit1.interest = 10;
            org.hibernate.test.joinedsubclassduplicatefields.Loan loan1 = new org.hibernate.test.joinedsubclassduplicatefields.Loan();
            loan1.id = 2L;
            loan1.interest = 10;
            org.hibernate.test.joinedsubclassduplicatefields.Deposit deposit2 = new org.hibernate.test.joinedsubclassduplicatefields.Deposit();
            deposit2.id = 3L;
            deposit2.interest = 20;
            org.hibernate.test.joinedsubclassduplicatefields.Loan loan2 = new org.hibernate.test.joinedsubclassduplicatefields.Loan();
            loan2.id = 4L;
            loan2.interest = 30;
            session.persist(deposit1);
            session.persist(loan1);
            session.persist(deposit2);
            session.persist(loan2);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            List<org.hibernate.test.joinedsubclassduplicatefields.Account> accounts = session.createQuery(("select a " + ("from Account a " + "where treat(a as Loan).interest = 10"))).getResultList();
            assertEquals(1, accounts.size());
        });
    }

    @Entity(name = "Account")
    @Inheritance(strategy = InheritanceType.JOINED)
    public static class Account {
        @Id
        public Long id;
    }

    @Entity(name = "Deposit")
    public static class Deposit extends JoinedSubclassDuplicateFieldsWithTreatTest.Account {
        @Column
        public Integer interest;
    }

    @Entity(name = "Loan")
    public static class Loan extends JoinedSubclassDuplicateFieldsWithTreatTest.Account {
        @Column
        public Integer interest;

        @Column
        public Integer rate;
    }
}

