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
package org.jboss.as.test.integration.weld.beanvalidation;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests for the integration of CDI and Bean Validation.
 *
 * @author Farah Juma
 */
@RunWith(Arquillian.class)
public class BeanValidationIntegrationTestCase {
    @Inject
    ValidatorFactory defaultValidatorFactory;

    @Test
    public void testInjectedValidatorFactoryIsCdiEnabled() {
        Assert.assertNotNull(defaultValidatorFactory);
        Set<ConstraintViolation<Reservation>> violations = defaultValidatorFactory.getValidator().validate(new Reservation(1, "Smith"));
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals("Not enough people for a reservation", violations.iterator().next().getMessage());
    }

    @Test
    public void testJndiBoundValidatorFactoryIsCdiEnabled() throws NamingException {
        ValidatorFactory validatorFactory = ((ValidatorFactory) (new InitialContext().lookup("java:comp/ValidatorFactory")));
        Assert.assertNotNull(validatorFactory);
        Set<ConstraintViolation<Reservation>> violations = validatorFactory.getValidator().validate(new Reservation(4, null));
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

