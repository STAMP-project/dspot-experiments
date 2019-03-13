/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.beanvalidation;


import java.math.BigDecimal;
import javax.persistence.EntityManager;
import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class BeanValidationTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testBeanValidationIntegrationOnFlush() {
        CupHolder ch = new CupHolder();
        ch.setRadius(new BigDecimal("12"));
        ch.setTitle("foo");
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        try {
            em.persist(ch);
            em.flush();
            Assert.fail("invalid object should not be persisted");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
        }
        Assert.assertTrue("A constraint violation exception should mark the transaction for rollback", em.getTransaction().getRollbackOnly());
        em.getTransaction().rollback();
        em.close();
    }

    @Test
    public void testBeanValidationIntegrationOnCommit() {
        CupHolder ch = new CupHolder();
        ch.setRadius(new BigDecimal("9"));
        ch.setTitle("foo");
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        em.persist(ch);
        em.flush();
        try {
            ch.setRadius(new BigDecimal("12"));
            em.getTransaction().commit();
            Assert.fail("invalid object should not be persisted");
        } catch (RollbackException e) {
            final Throwable cve = e.getCause();
            Assert.assertTrue((cve instanceof ConstraintViolationException));
            Assert.assertEquals(1, getConstraintViolations().size());
        }
        em.close();
    }

    @Test
    @RequiresDialect(H2Dialect.class)
    public void testTitleColumnHasExpectedLength() {
        EntityManager em = getOrCreateEntityManager();
        int len = ((Integer) (em.createNativeQuery("select CHARACTER_MAXIMUM_LENGTH from INFORMATION_SCHEMA.COLUMNS c where c.TABLE_NAME = 'CUPHOLDER' and c.COLUMN_NAME = 'TITLE'").getSingleResult()));
        Assert.assertEquals(64, len);
    }
}

