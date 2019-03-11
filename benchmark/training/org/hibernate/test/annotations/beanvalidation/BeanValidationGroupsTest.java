/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
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
public class BeanValidationGroupsTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testListeners() {
        CupHolder ch = new CupHolder();
        ch.setRadius(new BigDecimal("12"));
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        try {
            s.persist(ch);
            s.flush();
        } catch (ConstraintViolationException e) {
            Assert.fail("invalid object should not be validated");
        }
        try {
            ch.setRadius(null);
            s.flush();
        } catch (ConstraintViolationException e) {
            Assert.fail("invalid object should not be validated");
        }
        try {
            s.delete(ch);
            s.flush();
            Assert.fail("invalid object should not be persisted");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
            // TODO - seems this explicit case is necessary with JDK 5 (at least on Mac). With Java 6 there is no problem
            Annotation annotation = e.getConstraintViolations().iterator().next().getConstraintDescriptor().getAnnotation();
            Assert.assertEquals(NotNull.class, annotation.annotationType());
        }
        tx.rollback();
        s.close();
    }
}

