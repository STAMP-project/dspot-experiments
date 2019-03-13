/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cut;


import DialectChecks.DoesNotSupportRowValueConstructorSyntax;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gavin King
 */
public class CompositeUserTypeTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testCompositeUserType() {
        Session s = openSession();
        org.hibernate.Transaction t = s.beginTransaction();
        Transaction tran = new Transaction();
        tran.setDescription("a small transaction");
        tran.setValue(new MonetoryAmount(new BigDecimal(1.5), Currency.getInstance("USD")));
        s.persist(tran);
        List result = s.createQuery("from Transaction tran where tran.value.amount > 1.0 and tran.value.currency = 'USD'").list();
        Assert.assertEquals(result.size(), 1);
        tran.getValue().setCurrency(Currency.getInstance("AUD"));
        result = s.createQuery("from Transaction tran where tran.value.amount > 1.0 and tran.value.currency = 'AUD'").list();
        Assert.assertEquals(result.size(), 1);
        if (!((getDialect()) instanceof HSQLDialect)) {
            result = s.createQuery("from Transaction txn where txn.value = (1.5, 'AUD')").list();
            Assert.assertEquals(result.size(), 1);
            result = s.createQuery("from Transaction where value = (1.5, 'AUD')").list();
            Assert.assertEquals(result.size(), 1);
            result = s.createQuery("from Transaction where value != (1.4, 'AUD')").list();
            Assert.assertEquals(result.size(), 1);
        }
        s.delete(tran);
        t.commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = { SybaseASE15Dialect.class }, jiraKey = "HHH-6788")
    @SkipForDialect(value = { DB2Dialect.class }, jiraKey = "HHH-6867")
    public void testCustomColumnReadAndWrite() {
        Session s = openSession();
        org.hibernate.Transaction t = s.beginTransaction();
        final BigDecimal AMOUNT = new BigDecimal(7.3E7);
        final BigDecimal AMOUNT_MILLIONS = AMOUNT.divide(new BigDecimal(1000000.0));
        MutualFund f = new MutualFund();
        f.setHoldings(new MonetoryAmount(AMOUNT, Currency.getInstance("USD")));
        s.persist(f);
        s.flush();
        // Test value conversion during insert
        BigDecimal amountViaSql = ((BigDecimal) (s.createSQLQuery("select amount_millions from MutualFund").uniqueResult()));
        Assert.assertEquals(AMOUNT_MILLIONS.doubleValue(), amountViaSql.doubleValue(), 0.01);
        // Test projection
        BigDecimal amountViaHql = ((BigDecimal) (s.createQuery("select f.holdings.amount from MutualFund f").uniqueResult()));
        Assert.assertEquals(AMOUNT.doubleValue(), amountViaHql.doubleValue(), 0.01);
        // Test restriction and entity load via criteria
        BigDecimal one = new BigDecimal(1);
        f = ((MutualFund) (s.createCriteria(MutualFund.class).add(Restrictions.between("holdings.amount", AMOUNT.subtract(one), AMOUNT.add(one))).uniqueResult()));
        Assert.assertEquals(AMOUNT.doubleValue(), f.getHoldings().getAmount().doubleValue(), 0.01);
        // Test predicate and entity load via HQL
        f = ((MutualFund) (s.createQuery("from MutualFund f where f.holdings.amount between ?1 and ?2").setBigDecimal(1, AMOUNT.subtract(one)).setBigDecimal(2, AMOUNT.add(one)).uniqueResult()));
        Assert.assertEquals(AMOUNT.doubleValue(), f.getHoldings().getAmount().doubleValue(), 0.01);
        s.delete(f);
        t.commit();
        s.close();
    }

    /**
     * Tests the {@code <>} operator on composite types.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-5946")
    public void testNotEqualOperator() {
        final Session s = openSession();
        s.getTransaction().begin();
        final Transaction t1 = new Transaction();
        t1.setDescription("foo");
        t1.setValue(new MonetoryAmount(new BigDecimal(178), Currency.getInstance("EUR")));
        t1.setTimestamp(new CompositeDateTime(2014, 8, 23, 14, 23, 0));
        s.persist(t1);
        final Transaction t2 = new Transaction();
        t2.setDescription("bar");
        t2.setValue(new MonetoryAmount(new BigDecimal(1000000), Currency.getInstance("USD")));
        t1.setTimestamp(new CompositeDateTime(2014, 8, 22, 14, 23, 0));
        s.persist(t2);
        final Transaction t3 = new Transaction();
        t3.setDescription("bar");
        t3.setValue(new MonetoryAmount(new BigDecimal(1000000), Currency.getInstance("EUR")));
        t3.setTimestamp(new CompositeDateTime(2014, 8, 22, 14, 23, 1));
        s.persist(t3);
        final Query q1 = s.createQuery("from Transaction where value <> :amount");
        q1.setParameter("amount", new MonetoryAmount(new BigDecimal(178), Currency.getInstance("EUR")));
        Assert.assertEquals(2, q1.list().size());
        final Query q2 = s.createQuery("from Transaction where value <> :amount and description = :str");
        q2.setParameter("amount", new MonetoryAmount(new BigDecimal(1000000), Currency.getInstance("USD")));
        q2.setParameter("str", "bar");
        Assert.assertEquals(1, q2.list().size());
        final Query q3 = s.createQuery("from Transaction where timestamp <> :timestamp");
        q3.setParameter("timestamp", new CompositeDateTime(2014, 8, 23, 14, 23, 0));
        Assert.assertEquals(2, q3.list().size());
        s.delete(t3);
        s.delete(t2);
        s.delete(t1);
        s.getTransaction().commit();
        s.close();
    }

    /**
     * Tests the {@code <} operator on composite types. As long as we don't support it, we need to throw an exception
     * rather than create a random query.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-5946")
    @RequiresDialectFeature(DoesNotSupportRowValueConstructorSyntax.class)
    public void testLessThanOperator() {
        final Session s = openSession();
        try {
            final Query q = s.createQuery("from Transaction where value < :amount");
            q.setParameter("amount", new MonetoryAmount(BigDecimal.ZERO, Currency.getInstance("EUR")));
            q.list();
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QuerySyntaxException.class, e.getCause());
            // expected
        } finally {
            s.close();
        }
    }

    /**
     * Tests the {@code <=} operator on composite types. As long as we don't support it, we need to throw an exception
     * rather than create a random query.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-5946")
    @RequiresDialectFeature(DoesNotSupportRowValueConstructorSyntax.class)
    public void testLessOrEqualOperator() {
        final Session s = openSession();
        try {
            final Query q = s.createQuery("from Transaction where value <= :amount");
            q.setParameter("amount", new MonetoryAmount(BigDecimal.ZERO, Currency.getInstance("USD")));
            q.list();
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QuerySyntaxException.class, e.getCause());
            // expected
        } finally {
            s.close();
        }
    }

    /**
     * Tests the {@code >} operator on composite types. As long as we don't support it, we need to throw an exception
     * rather than create a random query.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-5946")
    @RequiresDialectFeature(DoesNotSupportRowValueConstructorSyntax.class)
    public void testGreaterThanOperator() {
        final Session s = openSession();
        try {
            final Query q = s.createQuery("from Transaction where value > :amount");
            q.setParameter("amount", new MonetoryAmount(BigDecimal.ZERO, Currency.getInstance("EUR")));
            q.list();
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QuerySyntaxException.class, e.getCause());
            // expected
        } finally {
            s.close();
        }
    }

    /**
     * Tests the {@code >=} operator on composite types. As long as we don't support it, we need to throw an exception
     * rather than create a random query.
     */
    @Test
    @TestForIssue(jiraKey = "HHH-5946")
    @RequiresDialectFeature(DoesNotSupportRowValueConstructorSyntax.class)
    public void testGreaterOrEqualOperator() {
        final Session s = openSession();
        try {
            final Query q = s.createQuery("from Transaction where value >= :amount");
            q.setParameter("amount", new MonetoryAmount(BigDecimal.ZERO, Currency.getInstance("USD")));
            q.list();
        } catch (IllegalArgumentException e) {
            ExtraAssertions.assertTyping(QuerySyntaxException.class, e.getCause());
            // expected
        } finally {
            s.close();
        }
    }
}

