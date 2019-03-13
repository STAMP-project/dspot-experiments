/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import java.math.BigDecimal;
import javax.validation.ConstraintViolationException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 */
public class BeanValidationAutoTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testListeners() {
        CupHolder ch = new CupHolder();
        ch.setRadius(new BigDecimal("12"));
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.persist(ch);
            s.flush();
            Assert.fail("invalid object should not be persisted");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
        }
        tx.rollback();
        s.close();
    }
}

