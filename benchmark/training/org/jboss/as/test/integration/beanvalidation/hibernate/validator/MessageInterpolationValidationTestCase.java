package org.jboss.as.test.integration.beanvalidation.hibernate.validator;


import java.util.Locale;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests that message interpolation works correctly for Hibernate Validator.
 *
 * @author Madhumita Sadhukhan
 */
@RunWith(Arquillian.class)
public class MessageInterpolationValidationTestCase {
    @Test
    public void testCustomMessageInterpolation() {
        HibernateValidatorConfiguration configuration = Validation.byProvider(HibernateValidator.class).configure();
        Assert.assertNotNull(configuration);
        final MessageInterpolator messageInterpolator = new MessageInterpolationValidationTestCase.CustomMessageInterpolator();
        configuration.messageInterpolator(messageInterpolator);
        ValidatorFactory factory = configuration.buildValidatorFactory();
        Validator validator = factory.getValidator();
        // create employee
        Employee emp = new Employee();
        emp.setEmail("MADHUMITA");
        Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(emp);
        Assert.assertEquals("Wrong number of constraints", constraintViolations.size(), 1);
        Assert.assertEquals(MessageInterpolationValidationTestCase.CustomMessageInterpolator.MESSAGE, constraintViolations.iterator().next().getMessage());
    }

    private static class CustomMessageInterpolator implements MessageInterpolator {
        public static final String MESSAGE = "Message created by custom interpolator";

        @Override
        public String interpolate(String messageTemplate, Context context) {
            return MessageInterpolationValidationTestCase.CustomMessageInterpolator.MESSAGE;
        }

        @Override
        public String interpolate(String messageTemplate, Context context, Locale locale) {
            return MessageInterpolationValidationTestCase.CustomMessageInterpolator.MESSAGE;
        }
    }
}

