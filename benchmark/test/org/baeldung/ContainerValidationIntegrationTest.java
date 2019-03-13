package org.baeldung;


import java.util.Collections;
import java.util.OptionalInt;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.Assert;
import org.junit.Test;


public class ContainerValidationIntegrationTest {
    private Validator validator;

    @Test
    public void whenEmptyAddress_thenValidationFails() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setAddresses(Collections.singletonList(" "));
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals("Address must not be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void whenInvalidEmail_thenValidationFails() {
        CustomerMap map = new CustomerMap();
        map.setCustomers(Collections.singletonMap("john", new Customer()));
        Set<ConstraintViolation<CustomerMap>> violations = validator.validate(map);
        Assert.assertEquals(1, violations.size());
        Assert.assertEquals("Must be a valid email", violations.iterator().next().getMessage());
    }

    @Test
    public void whenAgeTooLow_thenValidationFails() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setAge(15);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        Assert.assertEquals(1, violations.size());
    }

    @Test
    public void whenAgeNull_thenValidationSucceeds() {
        Customer customer = new Customer();
        customer.setName("John");
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        Assert.assertEquals(0, violations.size());
    }

    @Test
    public void whenNumberOrdersValid_thenValidationSucceeds() {
        Customer customer = new Customer();
        customer.setName("John");
        customer.setNumberOfOrders(OptionalInt.of(1));
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        Assert.assertEquals(0, violations.size());
    }
}

