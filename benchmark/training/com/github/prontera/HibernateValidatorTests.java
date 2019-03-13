package com.github.prontera;


import com.github.prontera.util.HibernateValidators;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.Assert;
import org.junit.Test;


public class HibernateValidatorTests {
    @Test
    public void manufacturerIsNull() {
        Car car = new Car(null, "DD-AB-123", 4);
        Set<ConstraintViolation<Car>> constraintViolations = HibernateValidators.validate(car);
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals("may not be null", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void licensePlateTooShort() {
        Car car = new Car("Morris", "D", 4);
        Set<ConstraintViolation<Car>> constraintViolations = HibernateValidators.validate(car);
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals("size must be between 2 and 14", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void seatCountTooLow() {
        Car car = new Car("Morris", "DD-AB-123", 1);
        Set<ConstraintViolation<Car>> constraintViolations = HibernateValidators.validate(car);
        Assert.assertEquals(1, constraintViolations.size());
        Assert.assertEquals("must be greater than or equal to 2", constraintViolations.iterator().next().getMessage());
    }

    @Test
    public void carIsValid() {
        Car car = new Car("Morris", "DD-AB-123", 2);
        Set<ConstraintViolation<Car>> constraintViolations = HibernateValidators.validate(car);
        Assert.assertEquals(0, constraintViolations.size());
    }
}

