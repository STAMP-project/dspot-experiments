/**
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat Inc., and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.jpa.beanvalidation.cdi;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.naming.InitialContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the integration of JPA, CDI, and Bean Validation.
 *
 * @author Farah Juma
 */
@RunWith(Arquillian.class)
public class BeanValidationCdiIntegrationTestCase {
    private static final String ARCHIVE_NAME = "BeanValidationCdiIntegrationTestCase";

    @ArquillianResource
    private InitialContext iniCtx;

    @Test
    public void testSuccessfulBeanValidation() throws Exception {
        SFSB sfsb = lookup("SFSB", SFSB.class);
        sfsb.createReservation(6, "Smith");
    }

    @Test
    public void testFailingBeanValidation() throws Exception {
        SFSB sfsb = lookup("SFSB", SFSB.class);
        try {
            sfsb.createReservation(1, null);
            Assert.fail("Should have thrown validation error for invalid values in Reservation entity");
        } catch (Throwable throwable) {
            ConstraintViolationException constraintViolationException = null;
            // Find the ConstraintViolationException
            while ((throwable != null) && (!(throwable instanceof ConstraintViolationException))) {
                throwable = throwable.getCause();
            } 
            constraintViolationException = ((ConstraintViolationException) (throwable));
            Set<ConstraintViolation<?>> violations = constraintViolationException.getConstraintViolations();
            List<String> actualViolations = new ArrayList<String>();
            for (ConstraintViolation<?> violation : violations) {
                actualViolations.add(violation.getMessage());
            }
            List<String> expectedViolations = new ArrayList<String>();
            expectedViolations.add("may not be null");
            expectedViolations.add("Not enough people for a reservation");
            Collections.sort(actualViolations);
            Collections.sort(expectedViolations);
            Assert.assertEquals(expectedViolations, actualViolations);
        }
    }
}

