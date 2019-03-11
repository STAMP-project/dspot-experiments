/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ales Justin
 */
public class BigDecimalTypeTest extends BaseNonConfigCoreFunctionalTestCase {
    @Test
    public void testBigDecimalInSum() {
        final BigDecimal balance = new BigDecimal("1000000000.00000004");
        Session s = openSession();
        s.getTransaction().begin();
        try {
            BigDecimalTypeTest.Account account = new BigDecimalTypeTest.Account();
            account.id = 1L;
            account.balance = balance;
            s.save(account);
            s.getTransaction().commit();
        } catch (Exception e) {
            if (((s.getTransaction()) != null) && ((s.getTransaction().getStatus()) == (TransactionStatus.ACTIVE))) {
                s.getTransaction().rollback();
            }
            Assert.fail(e.getMessage());
        } finally {
            s.close();
        }
        s = openSession();
        s.getTransaction().begin();
        try {
            CriteriaBuilder b = s.getCriteriaBuilder();
            CriteriaQuery<BigDecimal> cq = b.createQuery(BigDecimal.class);
            Root<BigDecimalTypeTest.Account> account_ = cq.from(BigDecimalTypeTest.Account.class);
            cq.select(b.sum(account_.get("balance")));
            Query<BigDecimal> query = s.createQuery(cq);
            BigDecimal result = s.createQuery("SELECT SUM(a.balance) FROM Account a", BigDecimal.class).uniqueResult();
            Assert.assertEquals(0, balance.compareTo(result));
            result = query.uniqueResult();
            Assert.assertEquals(0, balance.compareTo(result));
            s.getTransaction().commit();
        } catch (Exception e) {
            if (((s.getTransaction()) != null) && ((s.getTransaction().getStatus()) == (TransactionStatus.ACTIVE))) {
                s.getTransaction().rollback();
            }
            Assert.fail(e.getMessage());
        } finally {
            s.close();
        }
    }

    @Entity(name = "Account")
    public static class Account {
        @Id
        private Long id;

        @Basic(optional = false)
        @Column(precision = 20, scale = 8)
        private BigDecimal balance;
    }
}

