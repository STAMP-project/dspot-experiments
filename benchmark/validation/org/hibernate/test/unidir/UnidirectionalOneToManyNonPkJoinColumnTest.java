/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.unidir;


import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class UnidirectionalOneToManyNonPkJoinColumnTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12064")
    public void test() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // Save the entity on the One side
            org.hibernate.test.unidir.Customer customer = new org.hibernate.test.unidir.Customer();
            customer.idCode = "ABC";
            customer.translationId = 1L;
            entityManager.persist(customer);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            // Attempt to load the entity saved in the previous session
            entityManager.find(.class, "ABC");
        });
    }

    @Entity(name = "Customer")
    @Table(name = "tbl_customer")
    public static class Customer implements Serializable {
        @Id
        public String idCode;

        public Long translationId;

        @Fetch(FetchMode.JOIN)
        @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
        @JoinColumn(name = "translationId", referencedColumnName = "translationId")
        public List<UnidirectionalOneToManyNonPkJoinColumnTest.Order> translations;
    }

    @Entity(name = "Order")
    @Table(name = "tbl_order")
    public static class Order {
        @Id
        public long id;

        public long translationId;
    }
}

