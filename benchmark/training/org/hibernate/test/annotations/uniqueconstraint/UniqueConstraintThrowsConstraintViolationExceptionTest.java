/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.uniqueconstraint;


import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11236")
public class UniqueConstraintThrowsConstraintViolationExceptionTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUniqueConstraintWithEmptyColumnName() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.test.annotations.uniqueconstraint.Customer customer1 = new org.hibernate.test.annotations.uniqueconstraint.Customer();
            customer1.customerId = "123";
            session.persist(customer1);
        });
        try {
            TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
                org.hibernate.test.annotations.uniqueconstraint.Customer customer1 = new org.hibernate.test.annotations.uniqueconstraint.Customer();
                customer1.customerId = "123";
                session.persist(customer1);
            });
            Assert.fail("Should throw");
        } catch (PersistenceException e) {
            Assert.assertEquals(ConstraintViolationException.class, e.getCause().getClass());
        }
    }

    @Entity
    @Table(name = "CUSTOMER")
    public static class Customer {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "CUSTOMER_ACCOUNT_NUMBER")
        public Long customerAccountNumber;

        @Basic
        @Column(name = "CUSTOMER_ID", unique = true)
        public String customerId;

        @Basic
        @Column(name = "BILLING_ADDRESS")
        public String billingAddress;

        public Customer() {
        }
    }
}

