/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.embeddables;


import java.math.BigDecimal;
import javax.persistence.PersistenceException;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Pheby
 */
@RequiresDialect(H2Dialect.class)
public class EmbeddableIntegratorTest extends BaseUnitTestCase {
    /**
     * Throws a mapping exception because DollarValue is not mapped
     */
    @Test
    public void testWithoutIntegrator() {
        SessionFactory sf = new Configuration().addAnnotatedClass(Investor.class).setProperty("hibernate.hbm2ddl.auto", "create-drop").buildSessionFactory();
        try {
            Session sess = sf.openSession();
            try {
                sess.getTransaction().begin();
                Investor myInv = getInvestor();
                myInv.setId(1L);
                sess.save(myInv);
                sess.flush();
                Assert.fail("A JDBCException expected");
                sess.clear();
                Investor inv = ((Investor) (sess.get(Investor.class, 1L)));
                Assert.assertEquals(new BigDecimal("100"), inv.getInvestments().get(0).getAmount().getAmount());
            } catch (PersistenceException e) {
                ExtraAssertions.assertTyping(JDBCException.class, e.getCause());
                sess.getTransaction().rollback();
            }
            sess.close();
        } finally {
            sf.close();
        }
    }

    @Test
    public void testWithTypeContributor() {
        SessionFactory sf = new Configuration().addAnnotatedClass(Investor.class).registerTypeContributor(new InvestorTypeContributor()).setProperty("hibernate.hbm2ddl.auto", "create-drop").buildSessionFactory();
        Session sess = sf.openSession();
        try {
            sess.getTransaction().begin();
            Investor myInv = getInvestor();
            myInv.setId(2L);
            sess.save(myInv);
            sess.flush();
            sess.clear();
            Investor inv = ((Investor) (sess.get(Investor.class, 2L)));
            Assert.assertEquals(new BigDecimal("100"), inv.getInvestments().get(0).getAmount().getAmount());
        } catch (Exception e) {
            sess.getTransaction().rollback();
            throw e;
        } finally {
            sess.close();
            sf.close();
        }
    }
}

