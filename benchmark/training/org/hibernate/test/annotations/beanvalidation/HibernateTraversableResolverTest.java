/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.beanvalidation;


import java.math.BigDecimal;
import javax.validation.ConstraintViolation;
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
public class HibernateTraversableResolverTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testNonLazyAssocFieldWithConstraintsFailureExpected() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Screen screen = new Screen();
        screen.setPowerSupply(null);
        try {
            s.persist(screen);
            s.flush();
            Assert.fail("@NotNull on a non lazy association is not evaluated");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
        }
        tx.rollback();
        s.close();
    }

    @Test
    public void testEmbedded() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Screen screen = new Screen();
        PowerSupply ps = new PowerSupply();
        screen.setPowerSupply(ps);
        Button button = new Button();
        button.setName(null);
        button.setSize(3);
        screen.setStopButton(button);
        try {
            s.persist(screen);
            s.flush();
            Assert.fail("@NotNull on embedded property is not evaluated");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
            ConstraintViolation<?> cv = e.getConstraintViolations().iterator().next();
            Assert.assertEquals(Screen.class, cv.getRootBeanClass());
            // toString works since hibernate validator's Path implementation works accordingly. Should do a Path comparison though
            Assert.assertEquals("stopButton.name", cv.getPropertyPath().toString());
        }
        tx.rollback();
        s.close();
    }

    @Test
    public void testToOneAssocNotValidated() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Screen screen = new Screen();
        PowerSupply ps = new PowerSupply();
        ps.setPosition("1");
        ps.setPower(new BigDecimal(350));
        screen.setPowerSupply(ps);
        try {
            s.persist(screen);
            s.flush();
            Assert.fail("Associated objects should not be validated");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
            final ConstraintViolation constraintViolation = e.getConstraintViolations().iterator().next();
            Assert.assertEquals(PowerSupply.class, constraintViolation.getRootBeanClass());
        }
        tx.rollback();
        s.close();
    }

    @Test
    public void testCollectionAssocNotValidated() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Screen screen = new Screen();
        screen.setStopButton(new Button());
        screen.getStopButton().setName("STOOOOOP");
        PowerSupply ps = new PowerSupply();
        screen.setPowerSupply(ps);
        Color c = new Color();
        c.setName("Blue");
        s.persist(c);
        c.setName(null);
        screen.getDisplayColors().add(c);
        try {
            s.persist(screen);
            s.flush();
            Assert.fail("Associated objects should not be validated");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
            final ConstraintViolation constraintViolation = e.getConstraintViolations().iterator().next();
            Assert.assertEquals(Color.class, constraintViolation.getRootBeanClass());
        }
        tx.rollback();
        s.close();
    }

    @Test
    public void testEmbeddedCollection() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Screen screen = new Screen();
        PowerSupply ps = new PowerSupply();
        screen.setPowerSupply(ps);
        DisplayConnector conn = new DisplayConnector();
        conn.setNumber(0);
        screen.getConnectors().add(conn);
        try {
            s.persist(screen);
            s.flush();
            Assert.fail("Collection of embedded objects should be validated");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
            final ConstraintViolation constraintViolation = e.getConstraintViolations().iterator().next();
            Assert.assertEquals(Screen.class, constraintViolation.getRootBeanClass());
            // toString works since hibernate validator's Path implementation works accordingly. Should do a Path comparison though
            Assert.assertEquals("connectors[].number", constraintViolation.getPropertyPath().toString());
        }
        tx.rollback();
        s.close();
    }

    @Test
    public void testAssocInEmbeddedNotValidated() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        Screen screen = new Screen();
        screen.setStopButton(new Button());
        screen.getStopButton().setName("STOOOOOP");
        PowerSupply ps = new PowerSupply();
        screen.setPowerSupply(ps);
        DisplayConnector conn = new DisplayConnector();
        conn.setNumber(1);
        screen.getConnectors().add(conn);
        final Display display = new Display();
        display.setBrand("dell");
        conn.setDisplay(display);
        s.persist(display);
        s.flush();
        try {
            display.setBrand(null);
            s.persist(screen);
            s.flush();
            Assert.fail("Collection of embedded objects should be validated");
        } catch (ConstraintViolationException e) {
            Assert.assertEquals(1, e.getConstraintViolations().size());
            final ConstraintViolation constraintViolation = e.getConstraintViolations().iterator().next();
            Assert.assertEquals(Display.class, constraintViolation.getRootBeanClass());
        }
        tx.rollback();
        s.close();
    }
}

